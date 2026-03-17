package com.example.hmi.data

import androidx.compose.ui.graphics.Color

/**
 * Industrial-standard colors available in the HMI.
 * Ordered by: White, Primary (Default), RGB, CMY, Gray, Black.
 */
object ColorPalette {
    val Red = Color(0xFFD32F2F)
    val Green = Color(0xFF388E3C)
    val Blue = Color(0xFF1976D2)
    val Cyan = Color(0xFF00ACC1)
    val Magenta = Color(0xFFD81B60)
    val Yellow = Color(0xFFFBC02D)
    val Gray = Color(0xFF757575)
    val Black = Color(0xFF000000)
    val White = Color(0xFFFFFFFF)

    /**
     * List of colors available for selection, mapped to their 64-bit internal ULong values.
     * "Default" is represented as null to use theme-standard colors.
     */
    val Items = listOf(
        "White" to White.value.toLong(),
        "Default" to null,
        "Red" to Red.value.toLong(),
        "Green" to Green.value.toLong(),
        "Blue" to Blue.value.toLong(),
        "Cyan" to Cyan.value.toLong(),
        "Magenta" to Magenta.value.toLong(),
        "Yellow" to Yellow.value.toLong(),
        "Gray" to Gray.value.toLong(),
        "Black" to Black.value.toLong()
    )
}
