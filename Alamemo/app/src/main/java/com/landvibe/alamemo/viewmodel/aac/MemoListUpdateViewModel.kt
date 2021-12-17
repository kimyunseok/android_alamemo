package com.landvibe.alamemo.viewmodel.aac

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
        type = _type
        val memoList = memoRepository.getMemoByType(_type)

        _recentMemoList.value = EventWrapper(memoList.toMutableList())
    }

    fun getRecentDetailMemoList(memoId: Long) {
        val detailMemoList = detailMemoRepository.getDetailMemoByMemoId(memoId)
        _recentDetailMemoList.value = EventWrapper(detailMemoList.toMutableList())
    }

    fun changeDetailMemoType(memoId: Long, changedType: Int) {
        val detailMemoList = detailMemoRepository.getDetailMemoByMemoId(memoId)

        for(detailMemo in detailMemoList) {
            if(changedType == 3) {
                detailMemoRepository.modifyDetailMemoType(detailMemo.id, 1)
            } else {
                detailMemoRepository.modifyDetailMemoType(detailMemo.id, changedType)
            }
        }
    }
}