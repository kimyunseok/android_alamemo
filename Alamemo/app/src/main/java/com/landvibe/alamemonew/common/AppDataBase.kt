package com.landvibe.alamemonew.common

import androidx.room.*
import com.landvibe.alamemonew.model.data.memo.Memo
import com.landvibe.alamemonew.model.data.memo.MemoDao
import java.util.*

@Database(entities = [Memo::class], version = 1)
@TypeConverters(AppDataBase.Converter::class)
abstract class AppDataBase: RoomDatabase() {
    abstract fun memoDao(): MemoDao
    //abstract fun contentDao(): ContentDao

    companion object {
        val instance = Room.databaseBuilder(
            GlobalApplication.instance,
            AppDataBase::class.java,"Alamemo.db"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    inner class Converter {
        @TypeConverter
        fun convertTimeToDate(time: Long): Date {
            return Date(time)
        }

        @TypeConverter
        fun convertDateToTime(date: Date): Long {
            return date.time
        }
    }

}