package com.gameaccount.manager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameName: String,
    val server: String? = null,
    val phone: String? = null,
    val redCandles: Int = 0,
    val whiteCandles: Int = 0,
    val heartsCount: Int = 0,
    val dailyPrice: Double,
    val priceUnit: String = "hour",
    val deposit: Double = 0.0,
    val remarks: String? = null,
    val graduationProgress: String? = null,
    val replicaItems: String? = null,
    val status: String = "available", // available, rented, maintenance
    val createdAt: Date = Date(),
    val cardBg: String? = null,
    // 出租信息
    val renterName: String? = null,
    val renterContact: String? = null,
    val rentStartTime: Date? = null,
    val rentEndTime: Date? = null,
    val rentDuration: Int? = null,
    val rentUnit: String? = null,
    val totalCost: Double? = null,
    val needHeartService: Boolean = false,
    val heartServiceStatus: String? = null
)
