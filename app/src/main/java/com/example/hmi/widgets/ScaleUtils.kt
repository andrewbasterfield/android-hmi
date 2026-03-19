package com.example.hmi.widgets

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

/**
 * Utility for calculating "nice" tick intervals for scales and gauges.
 * Based on the "Nice Numbers for Graph Labels" algorithm by Paul Heckbert.
 */
object ScaleUtils {

    /**
     * Calculates a logical step size for tick marks given a range.
     * @param range The total span of the scale (max - min).
     * @param targetTicks Desired number of tick marks (default 5-10).
     * @return A "nice" step value (e.g., 0.1, 0.2, 0.5, 1, 2, 5, 10...).
     */
    fun calculateNiceStep(range: Float, targetTicks: Int = 6): Float {
        if (range <= 0) return 1f
        
        val rawStep = range / targetTicks
        val magnitude = 10.0.pow(floor(log10(rawStep.toDouble()))).toFloat()
        val residual = rawStep / magnitude

        val niceResidual = when {
            residual < 1.5 -> 1f
            residual < 3.0 -> 2f
            residual < 7.0 -> 5f
            else -> 10f
        }

        return niceResidual * magnitude
    }

    /**
     * Generates a list of tick values within a range based on a step size.
     */
    fun generateTicks(min: Float, max: Float, step: Float): List<Float> {
        val ticks = mutableListOf<Float>()
        var current = ceil(min / step) * step
        
        // Use a small epsilon to handle floating point precision issues
        val epsilon = step / 1000f
        
        while (current <= max + epsilon) {
            ticks.add(current.toFloat())
            current += step
        }
        return ticks
    }
}
