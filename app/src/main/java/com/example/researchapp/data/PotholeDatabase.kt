package com.example.researchapp.data

import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(
    entities = [PotholeReport::class],
    version = 1
)
abstract class PotholeDatabase : RoomDatabase() {

    abstract fun potholeDao(): PotholeDao
}