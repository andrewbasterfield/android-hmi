package com.example.hmi.protocol

import android.util.Log
import com.google.gson.JsonParser
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
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
    }

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    private var reconnectAttempts = 0
    private var lastConnectedTime = 0L
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private var client: Mqtt3AsyncClient? = null
    private var currentProfile: PlcConnectionProfile? = null

    private val _attributeUpdates = MutableSharedFlow<Triple<String, String, String>>(replay = 16, extraBufferCapacity = 64)
    override val attributeUpdates: Flow<Triple<String, String, String>> = _attributeUpdates.asSharedFlow()

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
            }
            .addDisconnectedListener { context ->
                // context.cause returns an Optional-like object from HiveMQ
                // Check if it has a value by examining toString() representation
                val cause = context.cause
                val hasError = !cause.toString().contains("Optional.empty")
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
            .automaticReconnectWithDefaultConfig()
            
        if (settings.username != null && settings.password != null) {
            clientBuilder.simpleAuth()
                .username(settings.username)
                .password(settings.password.toByteArray())
                .applySimpleAuth()
        }
        
        // FR-010: Last Will and Testament
        clientBuilder.willPublish()
            .topic(settings.statusTopic)
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

                    // Publish online status
                    mqttClient.publishWith()
                        .topic(settings.statusTopic)
                        .payload("online".toByteArray())
                        .qos(MqttQos.AT_LEAST_ONCE)
                        .retain(true)
                        .send()

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
                        .topic(settings.statusTopic)
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
    }

    override fun observeTag(tagAddress: String): Flow<PlcValue> = callbackFlow {
        val mqttClient = client ?: run {
            Log.w(TAG, "Cannot observe tag '$tagAddress': not connected")
            return@callbackFlow
        }
        val fullTopic = getFullTopic(tagAddress)
        val settings = currentProfile?.mqttSettings ?: MqttSettings()

        Log.d(TAG, "Subscribing to tag: $fullTopic")
        mqttClient.subscribeWith()
            .topicFilter(fullTopic)
            .qos(MqttQos.AT_MOST_ONCE)
            .callback { publish ->
                val payload = String(publish.payloadAsBytes)
                val value = parsePayload(payload, settings, fullTopic)
                trySend(value)
            }
            .send()
            .whenComplete { _, throwable ->
                if (throwable != null) {
                    Log.e(TAG, "Failed to subscribe to tag '$fullTopic': ${throwable.message}", throwable)
                    close(throwable)
                }
            }

        awaitClose {
            Log.d(TAG, "Unsubscribing from tag: $fullTopic")
            mqttClient.unsubscribeWith().topicFilter(fullTopic).send()
        }
    }

    private fun parsePayload(payload: String, settings: MqttSettings, topic: String? = null): PlcValue {
        return if (settings.payloadFormat == MqttPayloadFormat.JSON && settings.jsonKey != null) {
            try {
                val jsonObject = JsonParser.parseString(payload).asJsonObject
                val valueElement = jsonObject.get(settings.jsonKey)
                if (valueElement != null && valueElement.isJsonPrimitive) {
                    val primitive = valueElement.asJsonPrimitive
                    when {
                        primitive.isBoolean -> PlcValue.BooleanValue(primitive.asBoolean)
                        primitive.isNumber -> PlcValue.FloatValue(primitive.asFloat)
                        else -> PlcValue.StringValue(primitive.asString)
                    }
                } else {
                    Log.w(TAG, "JSON key '${settings.jsonKey}' not found or not primitive in payload from ${topic ?: "unknown"}: $payload")
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

    override fun observeAttribute(tagAddress: String, attribute: String): Flow<String> = callbackFlow {
        val mqttClient = client ?: run {
            Log.w(TAG, "Cannot observe attribute '$tagAddress/$attribute': not connected")
            return@callbackFlow
        }
        val fullTopic = getFullTopic(tagAddress) + "/" + attribute

        Log.d(TAG, "Subscribing to attribute: $fullTopic")
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
            Log.d(TAG, "Unsubscribing from attribute: $fullTopic")
            mqttClient.unsubscribeWith().topicFilter(fullTopic).send()
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
            is PlcValue.FloatValue -> value.value.toString()
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
