package com.example.hmi.core.ui.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class SiFormatterTest {

    @Test
    fun testSiFormatter_ValidUnits_RemainUnchanged() {
        assertEquals("mV", SiFormatter.formatUnit("mV"))
        assertEquals("MV", SiFormatter.formatUnit("MV"))
        assertEquals("kW", SiFormatter.formatUnit("kW"))
        assertEquals("MW", SiFormatter.formatUnit("MW"))
        assertEquals("Hz", SiFormatter.formatUnit("Hz"))
        assertEquals("MHz", SiFormatter.formatUnit("MHz"))
        assertEquals("m", SiFormatter.formatUnit("m"))
        assertEquals("L", SiFormatter.formatUnit("L"))
    }

    @Test
    fun testSiFormatter_InvalidUnits_AutoCorrected() {
        assertEquals("mV", SiFormatter.formatUnit("mv"))
        assertEquals("MW", SiFormatter.formatUnit("mw"))
        assertEquals("kW", SiFormatter.formatUnit("kw"))
        assertEquals("kW", SiFormatter.formatUnit("KW"))
        assertEquals("Hz", SiFormatter.formatUnit("hz"))
        assertEquals("Hz", SiFormatter.formatUnit("HZ"))
        assertEquals("MHz", SiFormatter.formatUnit("mhz"))
        assertEquals("MHz", SiFormatter.formatUnit("MHZ"))
        assertEquals("MHz", SiFormatter.formatUnit("mHz"))
        assertEquals("m", SiFormatter.formatUnit("M"))
        assertEquals("L", SiFormatter.formatUnit("l"))
    }

    @Test
    fun testSiFormatter_UnknownUnits_RemainUnchanged() {
        assertEquals("PSI", SiFormatter.formatUnit("PSI"))
        assertEquals("psi", SiFormatter.formatUnit("psi"))
        assertEquals("RPM", SiFormatter.formatUnit("RPM"))
        assertEquals("V", SiFormatter.formatUnit("V")) // Assuming V isn't autocorrected explicitly
    }
}
