package com.example.hmi.protocol.utils

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

object JsonPathUtils {
    /**
     * Extracts a primitive value from a JSON element using a dot-notation path.
     * Supports recursive dot-notation traversal (e.g., "motor.status.temp").
     * 
     * @param root The root JSON element.
     * @param path The dot-separated path.
     * @return The extracted JsonPrimitive, or null if not found or not a primitive.
     */
    fun extractJsonPath(root: JsonElement, path: String): JsonPrimitive? {
        if (path.isBlank()) return null
        
        val keys = path.split(".")
        var current: JsonElement = root
        
        for (key in keys) {
            if (key.isBlank()) return null
            current = (current as? JsonObject)?.get(key) ?: return null
        }
        
        return current as? JsonPrimitive
    }
}
