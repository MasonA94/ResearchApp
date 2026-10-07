package com.example.researchapp.data

import android.content.Context
import androidx.room3.Room

object DatabaseProvider {

    @Volatile
    private var INSTANCE: PotholeDatabase? = null

    fun getDatabase(context: Context): PotholeDatabase {

        return INSTANCE ?: synchronized(this) {

            val instance = Room.databaseBuilder(
                context.applicationContext,
                PotholeDatabase::class.java,
                "pothole_database"
            ).build()

            INSTANCE = instance

            instance
        }
    }
}