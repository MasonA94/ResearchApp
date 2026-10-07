package com.example.researchapp.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "potholes")
data class PotholeReport(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val latitude: Double,
    val longitude: Double,
    val description: String,
    val severity: Float,
    val bikeLane: Boolean,
    val obscured: Boolean
)
