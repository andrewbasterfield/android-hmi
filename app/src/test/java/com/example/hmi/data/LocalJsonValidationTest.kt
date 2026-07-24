package com.example.hmi.data

import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class LocalJsonValidationTest {
    @Test
    fun testUserPayloadAgainstSchema() {
        // Read the schema
        val schemaStr = File("src/main/assets/schemas/full-backup.schema.json").readText()
        val rawSchema = JSONObject(JSONTokener(schemaStr))
        val schema = SchemaLoader.load(rawSchema)
        
        // Read the user payload
        val payloadStr = javaClass.classLoader!!.getResourceAsStream("test_payload.json")!!
            .bufferedReader().readText()
        val jsonObject = JSONObject(payloadStr)
        
        // Validate - this will throw ValidationException if invalid
        schema.validate(jsonObject)
        
        // If we reach here, it's valid
        assertTrue(true)
    }
}
