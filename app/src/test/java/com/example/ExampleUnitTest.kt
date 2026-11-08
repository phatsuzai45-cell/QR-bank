package com.example

import com.example.domain.CurrencyUtils
import com.example.domain.VietQrGenerator
import com.example.domain.VietQrValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun formatVnd_formatsCorrectlyWithDots() {
        assertEquals("150.000 VNĐ", CurrencyUtils.formatVnd(150_000L))
        assertEquals("10.000đ", CurrencyUtils.formatShortDong(10_000L))
        assertEquals("10K", CurrencyUtils.formatCompactK(10_000L))
        assertEquals("100K", CurrencyUtils.formatCompactK(100_000L))
        assertEquals("1.250.000 VNĐ", CurrencyUtils.formatVnd(1_250_000L))
    }

    @Test
    fun vietQrPayload_generatesValidNapasEmvCoPayloadAndCrc16() {
        val result = VietQrGenerator.validateAndBuildPayload(
            bankBin = "970436",
            accountNumber = "1029384756",
            amountVnd = 150_000L,
            transferMessage = "Thanh toán đơn hàng #12"
        )
        assertTrue(result is VietQrValidationResult.Valid)
        val valid = result as VietQrValidationResult.Valid

        // Payload must start with 00020101021238...
        assertTrue(valid.payload.startsWith("00020101021238"))
        // Must contain NAPAS AID A000000727 and QRIBFTTA
        assertTrue(valid.payload.contains("0010A000000727"))
        assertTrue(valid.payload.contains("0208QRIBFTTA"))
        // Must contain Currency 5303704 and exact Amount 5406150000
        assertTrue(valid.payload.contains("5303704"))
        assertTrue(valid.payload.contains("5406150000"))
        // Must contain Country 5802VN and end with 4-char CRC16 hex
        assertTrue(valid.payload.contains("5802VN"))
        assertEquals(4, valid.crcHex.length)
        assertTrue(valid.payload.endsWith("6304${valid.crcHex}"))
    }
}
