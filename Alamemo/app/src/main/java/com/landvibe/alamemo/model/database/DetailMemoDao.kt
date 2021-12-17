package com.landvibe.alamemo.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.landvibe.alamemo.model.data.detail.DetailMemo

@Dao
interface DetailMemoDao {
    @Query("SELECT * FROM DetailMemo Where memoId = :memoId ")
    fun getDetailMemoByMemoId(memoId: Long): List<DetailMemo>

    @Query("SELECT * FROM DetailMemo Where id = :id")
    fun getDetailMemoById(id: Long): DetailMemo

    @Insert
    suspend fun suspendInsertDetailMemo(detailMemo: DetailMemo)
    @Insert
    fun insertDetailMemo(detailMemo: DetailMemo)

    @Delete
    suspend fun deleteDetailMemo(detailMemo: DetailMemo)

    @Query("DELETE FROM DetailMemo Where id = :id")
    suspend fun suspendDeleteDetailMemoByID(id: Long)

    @Query("DELETE FROM DetailMemo Where id = :id")
    fun deleteDetailMemoByID(id: Long)

    @Query("DELETE FROM DetailMemo Where memoId = :memoId")
    suspend fun deleteDetailMemoByMemoID(memoId: Long)

    @Query("UPDATE DetailMemo SET type = :type, icon = :icon, title = :title, scheduleDateYear = :scheduleDateYear, scheduleDateMonth = :scheduleDateMonth, scheduleDateDay = :scheduleDateDay, scheduleDateHour = :scheduleDateHour, scheduleDateMinute = :scheduleDateMinute Where id = :id")
    suspend fun suspendModifyDetailMemo(id: Long, type: Int, icon: String,
                                        title: String, scheduleDateYear: Int,
                                        scheduleDateMonth: Int, scheduleDateDay: Int,
                                        scheduleDateHour: Int, scheduleDateMinute: Int
    )


    @Query("UPDATE DetailMemo SET type = :type, icon = :icon, title = :title, scheduleDateYear = :scheduleDateYear, scheduleDateMonth = :scheduleDateMonth, scheduleDateDay = :scheduleDateDay, scheduleDateHour = :scheduleDateHour, scheduleDateMinute = :scheduleDateMinute Where id = :id")
    fun modifyDetailMemo(id: Long, type: Int, icon: String,
                                 title: String, scheduleDateYear: Int,
                                 scheduleDateMonth: Int, scheduleDateDay: Int,
                                 scheduleDateHour: Int, scheduleDateMinute: Int
    )
}