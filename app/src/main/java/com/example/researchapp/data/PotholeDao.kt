package com.example.researchapp.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PotholeDao {

    @Insert
    suspend fun insert(report: PotholeReport)

    @Query("SELECT * FROM potholes")
    fun getAll(): Flow<List<PotholeReport>>
}