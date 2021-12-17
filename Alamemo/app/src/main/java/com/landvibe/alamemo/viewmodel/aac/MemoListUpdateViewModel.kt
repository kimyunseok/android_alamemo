package com.landvibe.alamemo.viewmodel.aac

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.repository.DetailMemoRepository
import com.landvibe.alamemo.repository.MemoRepository
import com.landvibe.alamemo.util.EventWrapper

class MemoListUpdateViewModel(private val memoRepository: MemoRepository,
                              private val detailMemoRepository: DetailMemoRepository): ViewModel() {
    private val _recentMemoList = MutableLiveData<EventWrapper<MutableList<Memo>>>()
    val recentMemoList: LiveData<EventWrapper<MutableList<Memo>>>
        get() = _recentMemoList

    var type = -1

    private val _recentDetailMemoList = MutableLiveData<EventWrapper<MutableList<DetailMemo>>>()
    val recentDetailMemoList: LiveData<EventWrapper<MutableList<DetailMemo>>>
        get() = _recentDetailMemoList

    fun getRecentMemoList(_type: Int) {
        Log.d("MemoList Update", "get Recent Memo List - type is $_type")
        type = _type
        val memoList = if(type != 4) {
            memoRepository.getMemoByType(type)
        } else {
            memoRepository.getFinishedMemo()
        }

        _recentMemoList.value = EventWrapper(memoList.toMutableList())
    }

    fun getRecentDetailMemoList(memoId: Long) {
        val detailMemoList = detailMemoRepository.getDetailMemoByMemoId(memoId)
        _recentDetailMemoList.value = EventWrapper(detailMemoList.toMutableList())
    }

    fun changeDetailMemoTypeAndDate(memoId: Long, changedType: Int) {
        val memo = memoRepository.getMemoById(memoId)
        val detailMemoList = detailMemoRepository.getDetailMemoByMemoId(memoId)

        val applyType = if (changedType == 3) {
            1
        } else {
            changedType
        }

        for(detailMemo in detailMemoList) {
            detailMemoRepository.modifyDetailMemoType(
                detailMemo.id,
                applyType,
                detailMemo.icon,
                detailMemo.title,
                memo.scheduleDateYear,
                memo.scheduleDateMonth,
                memo.scheduleDateDay,
                memo.scheduleDateHour,
                memo.scheduleDateMinute)
        }
    }
}