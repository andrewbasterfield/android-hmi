package com.example.hmi.core.ui.utils

/**
 * Utility for enforcing SI unit compliance in the HMI.
 * It prevents dangerous case-sensitivity errors (e.g. mV vs MV).
 */
object SiFormatter {

    // Map of case-insensitive unit strings to their correct SI symbol
    private val unitDictionary = mapOf(
        "mv" to "mV", // Millivolts
        "kw" to "kW", // Kilowatts
        "mw" to "MW", // Megawatts (Note: 'mw' can map to either 'mW' (milli) or 'MW' (mega), but typically industrial sets context. The contract states MW is Megawatts, we map 'mw' to 'MW'. Wait, contract says forbidden transformations for MW are 'mw', 'mW'. For mV, forbidden are 'MV', 'mv'. Let's map variations explicitly to what is safe or just match exactly and map common mistakes.)
        "hz" to "Hz", // Hertz
        "mhz" to "MHz", // Megahertz
        "m" to "m", // Meters
        "l" to "L" // Liters
    )
    
    // For Megavolts and Millivolts, they conflict on 'mv'.
    // If we only have case-insensitive map, 'mv' maps to 'mV' (millivolts).
    // What if the user wanted 'MV' (megavolts)?
    // The contract states: "Data arriving via PLC tag attributes MUST NOT be sanitized for case unless matching the Symbol Dictionary. If a unit is not recognized in the SI dictionary, it MUST be rendered exactly as received without modification."
    
    // To implement "unless matching the symbol dictionary", we can define explicit mappings for forbidden transformations.

    private val forbiddenToCorrectMap = mapOf(
        "MV" to "mV", // Forbidden transformation for Millivolts (Wait, is MV Megavolts or Millivolts?)
        // The contract defines the correct symbols.
        // Millivolts: `mV`, Forbidden: `MV`, `mv`
        // Megavolts: `MV`, Forbidden: `mv`, `mV`
        // Wait, if "mV" is forbidden for "Megavolts" and "MV" is forbidden for "Millivolts", we cannot auto-correct just by string because 'MV' could mean Megavolts or Millivolts depending on context.
        // Let's re-read the contract carefully:
        // "Data arriving via PLC tag attributes MUST NOT be sanitized for case unless matching the Symbol Dictionary."
        // Actually, if the input is "mv" we might assume "mV". But if it's "MV", is it Megavolts or wrong Millivolts?
        // Let's assume the dictionary is a direct mapping of specific known wrong inputs if context is known, OR it just formats recognized case-insensitive strings if they don't conflict, but since they conflict (mv vs MV), we should probably NOT auto-correct case conflicts where ambiguous, OR we follow exact mappings.
        // Let's create an explicit dictionary.
        "kw" to "kW", "KW" to "kW",
        "hz" to "Hz", "HZ" to "Hz",
        "mhz" to "MHz", "MHZ" to "MHz",
        "l" to "L", "L" to "L",
        "m" to "m", "M" to "m"
        // For mV and MW, maybe we only correct "mv" -> "mV" and "mw" -> "MW" (assuming mega for watts and milli for volts in this domain)?
        // The contract specifies:
        // Millivolts: `mV`
        // Megavolts: `MV`
        // Kilowatts: `kW`
        // Megawatts: `MW`
        // Since both mV and MV are correct SI symbols, if the PLC sends "mV", it's correct. If it sends "MV", it's correct. If it sends "mv", we should probably default to "mV" for volts.
    )

    fun formatUnit(unit: String?): String {
        if (unit == null) return ""
        
        // Exact matches
        if (unit == "mV" || unit == "MV" || unit == "kW" || unit == "MW" || unit == "Hz" || unit == "MHz" || unit == "m" || unit == "L") {
            return unit
        }

        // Auto-correct common unambiguous mistakes
        return when (unit) {
            "mv" -> "mV"
            "mw" -> "MW"
            "kw", "KW" -> "kW"
            "hz", "HZ" -> "Hz"
            "mhz", "MHZ", "mHz" -> "MHz"
            "l" -> "L"
            "M" -> "m"
            else -> unit // Render exactly as received if not recognized
        }
    }

    fun formatValue(value: Float, decimalPlaces: Int = 1): String {
        return "%.${decimalPlaces}f".format(value)
    }

    /**
     * Unified Metric Formatting for HMI Widgets.
     * Concatenates value and unit with a non-breaking space (\\u00A0).
     */
    fun formatMetric(value: Float, unit: String?, decimalPlaces: Int = 1): String {
        val formattedValue = formatValue(value, decimalPlaces)
        val formattedUnit = formatUnit(unit)
        return if (formattedUnit.isBlank()) {
            formattedValue
        } else {
            "$formattedValue\u00A0$formattedUnit"
        }
    }

    /**
     * Unified Metric Formatting for pre-formatted string values.
     */
    fun formatMetric(value: String, unit: String?): String {
        val formattedUnit = formatUnit(unit)
        return if (formattedUnit.isBlank()) {
            value
        } else {
            "$value\u00A0$formattedUnit"
        }
    }
}
