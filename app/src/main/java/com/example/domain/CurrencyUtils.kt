package com.example.domain

import java.text.Normalizer
import java.util.Locale

object CurrencyUtils {

    val defaultQuickAmounts: List<Long> = listOf(
        10_000L,
        20_000L,
        30_000L,
        50_000L,
        100_000L,
        200_000L,
        500_000L
    )

    /**
     * Định dạng số tiền chuẩn Việt Nam: 150000 -> "150.000"
     */
    fun formatNumberDots(amount: Long): String {
        if (amount <= 0L) return "0"
        val raw = amount.toString()
        val sb = StringBuilder()
        var count = 0
        for (i in raw.length - 1 downTo 0) {
            sb.append(raw[i])
            count++
            if (count == 3 && i != 0) {
                sb.append('.')
                count = 0
            }
        }
        return sb.reverse().toString()
    }

    /**
     * Định dạng đầy đủ: 150000 -> "150.000 VNĐ"
     */
    fun formatVnd(amount: Long, suffix: String = "VNĐ"): String {
        return "${formatNumberDots(amount)} $suffix"
    }

    /**
     * Định dạng rút gọn trên nút chọn nhanh: 10000 -> "10.000đ"
     */
    fun formatShortDong(amount: Long): String {
        return "${formatNumberDots(amount)}đ"
    }

    /**
     * Định dạng pill ngắn gọn chuẩn Fintech: 10000 -> "10K", 100000 -> "100K"
     */
    fun formatCompactK(amount: Long): String {
        return if (amount >= 1_000L && amount % 1_000L == 0L) {
            "${amount / 1_000L}K"
        } else {
            formatShortDong(amount)
        }
    }

    /**
     * Loại bỏ toàn bộ ký tự không phải chữ số và chuyển về Long an toàn
     */
    fun parseDigitsToAmount(input: String): Long {
        val digitsOnly = input.filter { it.isDigit() }.trimStart('0')
        if (digitsOnly.isEmpty()) return 0L
        // Giới hạn tối đa 12 chữ số để tránh tràn số
        val safeDigits = digitsOnly.take(12)
        return safeDigits.toLongOrNull() ?: 0L
    }

    /**
     * Loại bỏ dấu Tiếng Việt để chuẩn hóa Tên chủ tài khoản và Nội dung chuyển khoản chuẩn NAPAS
     */
    fun removeVietnameseDiacritics(input: String): String {
        val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
        val withoutMarks = normalized.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        return withoutMarks
            .replace('đ', 'd')
            .replace('Đ', 'D')
    }

    /**
     * Chuẩn hóa tên chủ tài khoản viết hoa không dấu (VD: "Nguyễn Văn An" -> "NGUYEN VAN AN")
     */
    fun normalizeAccountHolderName(input: String): String {
        return removeVietnameseDiacritics(input)
            .uppercase(Locale.ROOT)
            .replace(Regex("[^A-Z0-9 ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    /**
     * Chuẩn hóa nội dung chuyển khoản hợp lệ theo chuẩn NAPAS 247 (ASCII, tối đa 50 ký tự)
     */
    fun sanitizeTransferMessage(input: String, maxLength: Int = 50): String {
        return removeVietnameseDiacritics(input)
            .replace(Regex("[^A-Za-z0-9 .,_-]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
            .take(maxLength)
    }

    /**
     * Đọc số tiền bằng chữ Tiếng Việt giúp người bán kiểm tra nhanh số tiền đã nhập
     */
    fun numberToVietnameseWords(amount: Long): String {
        if (amount <= 0L) return "Chưa nhập số tiền"
        val digits = arrayOf("không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín")
        val units = arrayOf("", "nghìn", "triệu", "tỷ", "nghìn tỷ")

        fun readThreeDigits(num: Int, showZeroHundred: Boolean): String {
            val hundred = num / 100
            val ten = (num % 100) / 10
            val unit = num % 10
            val parts = mutableListOf<String>()

            if (hundred > 0 || showZeroHundred) {
                parts.add("${digits[hundred]} trăm")
            }

            if (ten > 1) {
                parts.add("${digits[ten]} mươi")
                when (unit) {
                    1 -> parts.add("mốt")
                    4 -> parts.add("tư")
                    5 -> parts.add("lăm")
                    else -> if (unit > 0) parts.add(digits[unit])
                }
            } else if (ten == 1) {
                parts.add("mười")
                when (unit) {
                    5 -> parts.add("lăm")
                    else -> if (unit > 0) parts.add(digits[unit])
                }
            } else if (ten == 0 && unit > 0) {
                if (hundred > 0 || showZeroHundred) {
                    parts.add("lẻ")
                }
                parts.add(digits[unit])
            }
            return parts.joinToString(" ")
        }

        var remaining = amount
        val groups = mutableListOf<Int>()
        while (remaining > 0) {
            groups.add((remaining % 1000).toInt())
            remaining /= 1000
        }

        val resultParts = mutableListOf<String>()
        for (i in groups.size - 1 downTo 0) {
            val groupValue = groups[i]
            if (groupValue > 0) {
                val showZeroHundred = i < groups.size - 1
                val groupText = readThreeDigits(groupValue, showZeroHundred)
                val unitName = units.getOrElse(i) { "" }
                if (unitName.isNotEmpty()) {
                    resultParts.add("$groupText $unitName")
                } else {
                    resultParts.add(groupText)
                }
            }
        }

        val sentence = resultParts.joinToString(" ").trim()
        if (sentence.isEmpty()) return "Không đồng"
        val capitalized = sentence.replaceFirstChar { it.titlecase(Locale.ROOT) }
        return "$capitalized đồng chẵn"
    }
}
