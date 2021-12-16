package com.landvibe.alamemo.viewmodel.aac

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.repository.DetailMemoRepository
import com.landvibe.alamemo.repository.MemoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemoListUpdateViewModel(private val memoRepository: MemoRepository,
                              private val detailMemoRepository: DetailMemoRepository): ViewModel() {
    private val _recentMemoList = MutableLiveData<MutableList<Memo>>()
    val recentMemoList: LiveData<MutableList<Memo>>
        get() = _recentMemoList

    var type = -1

    private val _recentDetailMemoList = MutableLiveData<MutableList<DetailMemo>>()
    val recentDetailMemoList: LiveData<MutableList<DetailMemo>>
        get() = _recentDetailMemoList

    fun getRecentMemoList(_type: Int) {
        type = _type
        CoroutineScope(Dispatchers.IO).launch {
            val memoList = if(_type != 4) {
                memoRepository.getMemoByType(_type)
            } else {
                memoRepository.getFinishMemo()
            }

            _recentMemoList.postValue(memoList.toMutableList())
        }
    }

    fun getRecentDetailMemoList(memoId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val detailMemoList = detailMemoRepository.getDetailMemoByMemoId(memoId)
            _recentDetailMemoList.postValue(detailMemoList.toMutableList())
        }
    }
}