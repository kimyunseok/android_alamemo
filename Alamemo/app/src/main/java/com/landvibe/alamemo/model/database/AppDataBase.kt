package com.landvibe.alamemo.model.database

import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.landvibe.alamemo.common.GlobalApplication
import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.model.data.memo.Memo

@Database(entities = [Memo::class, DetailMemo::class], version = 1)
@TypeConverters(AppDataBase.Converter::class)
abstract class AppDataBase: RoomDatabase() {
    abstract fun memoDao(): MemoDao
    abstract fun detailMemoDao(): DetailMemoDao

    companion object {
        val instance = Room.databaseBuilder(
            GlobalApplication.instance,
            AppDataBase::class.java,"Alamemo.db"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    class Converter {
        @TypeConverter
        fun convertCharMutableListToString(data: MutableList<Char>): String {
            var ret = ""
            for(charData in data) {
                ret += charData
            }
            return ret
        }

        @TypeConverter
        fun convertStringToCharMutableList(data: String): MutableList<Char> {
            val ret = mutableListOf<Char>()
            for(charData in data) {
                ret.add(charData)
            }
            return ret
        }
    }

}