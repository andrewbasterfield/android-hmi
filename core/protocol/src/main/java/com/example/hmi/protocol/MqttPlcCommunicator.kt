package com.example.hmi.protocol

import android.util.Log
import com.hivemq.client.mqtt.datatypes.MqttQos
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import com.example.hmi.protocol.utils.JsonPathUtils
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import com.hivemq.client.mqtt.mqtt3.exceptions.Mqtt3DisconnectException
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class MqttPlcCommunicator @Inject constructor() : PlcCommunicator {

    companion object {
        private const val TAG = "MqttPlcCommunicator"
        private const val MAX_RECONNECT_ATTEMPTS = 5
        private const val STABLE_CONNECTION_MS = 5000L
        private const val SUBSCRIPTION_GRACE_PERIOD_MS = 5000L
    }

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    private var reconnectAttempts = 0
    private var lastConnectedTime = 0L
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private var client: Mqtt3AsyncClient? = null
    private var currentProfile: PlcConnectionProfile? = null

    private val _attributeUpdates = MutableSharedFlow<Triple<String, String, String>>(replay = 16, extraBufferCapacity = 64)
    override val attributeUpdates: Flow<Triple<String, String, String>> = _attributeUpdates.asSharedFlow()

    // Cache for shared subscriptions (Reference Counting via shareIn)
    private val rawTopicFlows = mutableMapOf<String, Flow<String>>()
    private val attributeFlows = mutableMapOf<String, Flow<String>>()
    private val communicatorScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private fun getFullTopic(tagAddress: String): String {
        val prefix = currentProfile?.mqttSettings?.rootTopicPrefix ?: ""
        return if (tagAddress.startsWith("/") || prefix.isEmpty()) {
            tagAddress.removePrefix("/")
        } else {
            prefix.removeSuffix("/") + "/" + tagAddress.removePrefix("/")
        }
    }

    override suspend fun connect(profile: PlcConnectionProfile): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val settings = profile.mqttSettings ?: MqttSettings()
        currentProfile = profile
        
        // Reset state for new connection
        synchronized(rawTopicFlows) { rawTopicFlows.clear() }
        synchronized(attributeFlows) { attributeFlows.clear() }
        
        val clientBuilder = Mqtt3Client.builder()
            .identifier(settings.clientId)
            .serverHost(profile.ipAddress)
            .serverPort(profile.port)
            .transportConfig()
                .socketConnectTimeout(5, TimeUnit.SECONDS)
                .applyTransportConfig()
            .addConnectedListener { _ ->
                lastConnectedTime = System.currentTimeMillis()
                Log.d(TAG, "Connected")
                _connectionState.value = ConnectionState.CONNECTED
                publishOnlineStatus()
            }
            .addDisconnectedListener { context ->
                // Check if disconnection was unexpected (BUG-014 fix)
                // In HiveMQ MQTT 3, Mqtt3DisconnectException represents an intentional disconnect.
                val cause = context.cause
                val hasError = cause !is Mqtt3DisconnectException
                if (hasError) {
                    // Only reset attempts if we had a stable connection (5+ seconds)
                    val connectionDuration = System.currentTimeMillis() - lastConnectedTime
                    if (connectionDuration >= STABLE_CONNECTION_MS) {
                        Log.d(TAG, "Connection was stable for ${connectionDuration}ms, resetting attempts")
                        reconnectAttempts = 0
                    }

                    reconnectAttempts++
                    Log.w(TAG, "Disconnected unexpectedly (attempt $reconnectAttempts/$MAX_RECONNECT_ATTEMPTS): $cause")

                    if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
                        Log.e(TAG, "Max reconnection attempts reached, giving up")
                        _connectionState.value = ConnectionState.ERROR
                        // Stop automatic reconnection by disconnecting
                        client?.disconnect()
                    } else {
                        _connectionState.value = ConnectionState.RECONNECTING
                    }
                } else {
                    // Intentional disconnection (no cause)
                    Log.d(TAG, "Disconnected intentionally")
                    reconnectAttempts = 0
                    _connectionState.value = ConnectionState.DISCONNECTED
                }
            }
            .automaticReconnect()
                .initialDelay(1, java.util.concurrent.TimeUnit.SECONDS)
                .maxDelay(10, java.util.concurrent.TimeUnit.SECONDS)
                .applyAutomaticReconnect()
            
        if (settings.username != null && settings.password != null) {
            clientBuilder.simpleAuth()
                .username(settings.username)
                .password(settings.password.toByteArray())
                .applySimpleAuth()
        }
        
        // FR-010: Last Will and Testament
        clientBuilder.willPublish()
            .topic(getFullTopic("status"))
            .payload("offline".toByteArray())
            .qos(MqttQos.AT_LEAST_ONCE)
            .retain(true)
            .applyWillPublish()

        val mqttClient = clientBuilder.buildAsync()
        client = mqttClient

        _connectionState.value = ConnectionState.CONNECTING
        Log.d(TAG, "Connecting to MQTT broker at ${profile.ipAddress}:${profile.port}")

        mqttClient.connect()
            .whenComplete { _: Mqtt3ConnAck?, throwable: Throwable? ->
                if (throwable == null) {
                    Log.i(TAG, "Connected to MQTT broker at ${profile.ipAddress}:${profile.port}")
                    _connectionState.value = ConnectionState.CONNECTED
                    continuation.resume(Result.success(Unit))
                } else {
                    Log.e(TAG, "Failed to connect to MQTT broker: ${throwable.message}", throwable)
                    _connectionState.value = ConnectionState.ERROR
                    continuation.resume(Result.failure(throwable))
                }
            }
            
        continuation.invokeOnCancellation {
            mqttClient.disconnect()
        }
    }

    override suspend fun disconnect() {
        Log.d(TAG, "Disconnecting from MQTT broker")
        val mqttClient = client
        val settings = currentProfile?.mqttSettings

        // Publish offline status before disconnecting (LWT only fires on unexpected disconnect)
        if (mqttClient != null && settings != null) {
            try {
                suspendCancellableCoroutine<Unit> { continuation ->
                    mqttClient.publishWith()
                        .topic(getFullTopic("status"))
                        .payload("offline".toByteArray())
                        .qos(MqttQos.AT_LEAST_ONCE)
                        .retain(true)
                        .send()
                        .whenComplete { _, throwable ->
                            if (throwable != null) {
                                Log.w(TAG, "Failed to publish offline status: ${throwable.message}")
                            } else {
                                Log.d(TAG, "Published offline status")
                            }
                            continuation.resume(Unit)
                        }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error publishing offline status: ${e.message}")
            }
        }

        mqttClient?.disconnect()
        client = null
        currentProfile = null
        _connectionState.value = ConnectionState.DISCONNECTED
        
        // Clear flows to ensure new connection starts fresh
        synchronized(rawTopicFlows) { rawTopicFlows.clear() }
        synchronized(attributeFlows) { attributeFlows.clear() }
    }

    override fun observeTag(tagAddress: String, jsonPath: String?): Flow<PlcValue> {
        val fullTopic = getFullTopic(tagAddress)
        val rawFlow = synchronized(rawTopicFlows) {
            rawTopicFlows.getOrPut(fullTopic) {
                callbackFlow {
                    val mqttClient = client ?: run {
                        Log.w(TAG, "Cannot observe tag '$tagAddress': not connected")
                        close()
                        return@callbackFlow
                    }

                    Log.d(TAG, "Shared Subscription: Subscribing to $fullTopic")
                    mqttClient.subscribeWith()
                        .topicFilter(fullTopic)
                        .qos(MqttQos.AT_MOST_ONCE)
                        .callback { publish ->
                            val payload = String(publish.payloadAsBytes)
                            trySend(payload)
                        }
                        .send()
                        .whenComplete { _, throwable ->
                            if (throwable != null) {
                                Log.e(TAG, "Failed to subscribe to tag '$fullTopic': ${throwable.message}", throwable)
                                close(throwable)
                            }
                        }

                    awaitClose {
                        Log.d(TAG, "Shared Subscription: Unsubscribing from $fullTopic")
                        mqttClient.unsubscribeWith().topicFilter(fullTopic).send()
                    }
                }.shareIn(
                    scope = communicatorScope,
                    started = SharingStarted.WhileSubscribed(stopTimeoutMillis = SUBSCRIPTION_GRACE_PERIOD_MS),
                    replay = 1
                )
            }
        }

        val settings = currentProfile?.mqttSettings ?: MqttSettings()
        return rawFlow.map { payload ->
            parsePayload(payload, settings, fullTopic, jsonPath)
        }
    }

    private fun publishOnlineStatus() {
        val mqttClient = client
        val settings = currentProfile?.mqttSettings
        if (mqttClient != null && settings != null) {
            mqttClient.publishWith()
                .topic(getFullTopic("status"))
                .payload("online".toByteArray())
                .qos(MqttQos.AT_LEAST_ONCE)
                .retain(true)
                .send()
                .whenComplete { _, throwable ->
                    if (throwable != null) {
                        Log.w(TAG, "Failed to publish online status: ${throwable.message}")
                    } else {
                        Log.d(TAG, "Published online status")
                    }
                }
        }
    }

    private fun parsePayload(payload: String, settings: MqttSettings, topic: String? = null, jsonPathOverride: String? = null): PlcValue {
        val effectivePath = jsonPathOverride ?: if (settings.payloadFormat == MqttPayloadFormat.JSON) settings.jsonKey else null
        
        return if (effectivePath != null) {
            try {
                val jsonElement = Json.parseToJsonElement(payload)
                val valueElement = JsonPathUtils.extractJsonPath(jsonElement, effectivePath)
                if (valueElement != null) {
                    when {
                        valueElement.booleanOrNull != null -> PlcValue.BooleanValue(valueElement.booleanOrNull!!)
                        valueElement.floatOrNull != null -> PlcValue.FloatValue(valueElement.floatOrNull!!)
                        else -> PlcValue.StringValue(valueElement.content)
                    }
                } else {
                    Log.w(TAG, "JSON path '$effectivePath' not found or not primitive in payload from ${topic ?: "unknown"}: $payload")
                    PlcValue.StringValue(payload)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to parse JSON payload from ${topic ?: "unknown"}: $payload", e)
                PlcValue.StringValue(payload)
            }
        } else {
            when {
                payload.equals("true", ignoreCase = true) || payload == "1" || payload.equals("on", ignoreCase = true) -> PlcValue.BooleanValue(true)
                payload.equals("false", ignoreCase = true) || payload == "0" || payload.equals("off", ignoreCase = true) -> PlcValue.BooleanValue(false)
                else -> {
                    val floatVal = payload.toFloatOrNull()
                    if (floatVal != null) PlcValue.FloatValue(floatVal)
                    else PlcValue.StringValue(payload)
                }
            }
        }
    }

    override fun observeAttribute(tagAddress: String, attribute: String): Flow<String> {
        val fullTopic = getFullTopic(tagAddress) + "/" + attribute
        return synchronized(attributeFlows) {
            attributeFlows.getOrPut(fullTopic) {
                callbackFlow {
                    val mqttClient = client ?: run {
                        Log.w(TAG, "Cannot observe attribute '$tagAddress/$attribute': not connected")
                        close()
                        return@callbackFlow
                    }

                    Log.d(TAG, "Shared Subscription: Subscribing to $fullTopic")
                    mqttClient.subscribeWith()
                        .topicFilter(fullTopic)
                        .qos(MqttQos.AT_MOST_ONCE)
                        .callback { publish ->
                            val payload = String(publish.payloadAsBytes)
                            trySend(payload)
                            _attributeUpdates.tryEmit(Triple(tagAddress, attribute, payload))
                        }
                        .send()
                        .whenComplete { _, throwable ->
                            if (throwable != null) {
                                Log.e(TAG, "Failed to subscribe to attribute '$fullTopic': ${throwable.message}", throwable)
                                close(throwable)
                            }
                        }

                    awaitClose {
                        Log.d(TAG, "Shared Subscription: Unsubscribing from $fullTopic")
                        mqttClient.unsubscribeWith().topicFilter(fullTopic).send()
                    }
                }.shareIn(
                    scope = communicatorScope,
                    started = SharingStarted.WhileSubscribed(stopTimeoutMillis = SUBSCRIPTION_GRACE_PERIOD_MS),
                    replay = 1
                )
            }
        }
    }

    override suspend fun writeTag(tagAddress: String, value: PlcValue, shouldRetain: Boolean): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val mqttClient = client
        if (mqttClient == null || _connectionState.value != ConnectionState.CONNECTED) {
            val error = IllegalStateException("Not connected (state: ${_connectionState.value})")
            Log.w(TAG, "Cannot write to tag '$tagAddress': ${error.message}")
            continuation.resume(Result.failure(error))
            return@suspendCancellableCoroutine
        }

        val payload = when (value) {
            is PlcValue.BooleanValue -> value.value.toString()
            is PlcValue.FloatValue -> value.value.toBigDecimal().stripTrailingZeros().toPlainString()
            is PlcValue.IntValue -> value.value.toString()
            is PlcValue.StringValue -> value.value
        }

        val fullTopic = getFullTopic(tagAddress)
        Log.d(TAG, "Publishing to $fullTopic: $payload (retain=$shouldRetain)")

        mqttClient.publishWith()
            .topic(fullTopic)
            .payload(payload.toByteArray())
            .qos(MqttQos.AT_LEAST_ONCE)
            .retain(shouldRetain)
            .send()
            .whenComplete { _, throwable ->
                if (throwable != null) {
                    Log.e(TAG, "Failed to publish to '$fullTopic': ${throwable.message}", throwable)
                    continuation.resume(Result.failure(throwable))
                } else {
                    continuation.resume(Result.success(Unit))
                }
            }
    }
}
