package com.landvibe.alamemo.viewmodel.aac

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.repository.DetailMemoRepository
import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.repository.MemoRepository
import com.landvibe.alamemo.util.EventWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TabFragmentViewModel(private val memoRepository: MemoRepository, private val detailMemoRepository: DetailMemoRepository): ViewModel() {
    var title: String = ""

    private val _memoEmpty = MutableLiveData<Boolean>()
    val memoEmpty: LiveData<Boolean>
        get() = _memoEmpty

    private val _memoList = MutableLiveData<MutableList<Memo>>()
    val memoList: LiveData<MutableList<Memo>>
        get() = _memoList

    private val _removedMemoAndDetailMemoList = MutableLiveData<EventWrapper<RemovedMemoAndDetailMemoList>>()
    val removedMemoAndDetailMemoList: LiveData<EventWrapper<RemovedMemoAndDetailMemoList>>
        get() = _removedMemoAndDetailMemoList

    private val _newMemoList = MutableLiveData<MutableList<Memo>>()
    val newMemoList: LiveData<MutableList<Memo>>
        get() = _newMemoList

    fun setEmpty(isEmpty: Boolean) {
        _memoEmpty.postValue(isEmpty)
    }

    fun getMemoList(type: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val memoList = if(type != 4) {
                memoRepository.getMemoByType(type)
            } else {
                memoRepository.getFinishMemo()
            }
            Log.d("get Memo From Room", memoList.toString())
            _memoList.postValue(memoList.toMutableList())
        }
    }

    fun saveRemovedMemoAndDetailMemoList(memo: Memo) {
        val detailMemoList = detailMemoRepository.getDetailMemoByMemoId(memo.id).toMutableList()
        _removedMemoAndDetailMemoList.postValue(EventWrapper(RemovedMemoAndDetailMemoList(memo, detailMemoList)))
    }

    fun deleteMemoByID(memoId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            memoRepository.deleteMemoByID(memoId)
        }
    }

    fun deleteDetailMemoByMemoID(memoId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            detailMemoRepository.deleteDetailMemoByMemoID(memoId)
        }
    }

    fun setMemoFinish(memoId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            memoRepository.setMemoFinish(memoId)
        }
    }

    fun setTitle(type: Int) {
        when (type) {
            1 -> {
                title = "📝 메모"
            }
            2 -> {
                title = "📋 일정"
            }
            3 -> {
                title = "⏰ 반복 일정"
            }
            4 -> {
                title = "🏁 종료"
            }
        }
    }

    fun getRecentMemoList(type: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val memoList = if(type != 4) {
                memoRepository.getMemoByType(type)
            } else {
                memoRepository.getFinishMemo()
            }
            Log.d("get Memo From Room", memoList.toString())
            _newMemoList.postValue(memoList.toMutableList())
        }
    }

    data class RemovedMemoAndDetailMemoList(val removedMemo: Memo, val removedDetailMemoList: MutableList<DetailMemo>)
}