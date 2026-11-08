package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_history")
data class QrHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountVnd: Long,
    val bankBin: String,
    val bankShortName: String,
    val bankFullName: String,
    val accountNumber: String,
    val accountHolder: String,
    val transferMessage: String,
    val vietQrPayload: String,
    val crcHex: String,
    val createdAt: Long = System.currentTimeMillis()
)
