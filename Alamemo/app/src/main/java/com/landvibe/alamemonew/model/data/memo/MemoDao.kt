package com.landvibe.alamemonew.model.data.memo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MemoDao {
    @Query("SELECT * FROM Memo")
    fun getAllMemo(): List<Memo>

    @Query("SELECT * FROM Memo Where id = :id")
    fun getMemoById(id: Long): Memo

    @Query("SELECT * FROM Memo Where type = :type")
    fun getMemoByType(type: Int): List<Memo>

    @Insert
    fun insertMemo(memo: Memo)

    @Delete
    fun deleteMemo(memo: Memo)
}