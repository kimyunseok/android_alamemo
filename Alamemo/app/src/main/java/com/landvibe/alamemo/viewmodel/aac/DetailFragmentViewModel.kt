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

class DetailFragmentViewModel(private val memoRepository: MemoRepository,
                              private val detailMemoRepository: DetailMemoRepository): ViewModel() {
    var title = ""

    var memoId = -1L

    private val _memoEmpty = MutableLiveData<Boolean>()
    val memoEmpty: LiveData<Boolean>
        get() = _memoEmpty

    private val _detailMemoList = MutableLiveData<MutableList<DetailMemo>>()
    val detailMemoList: LiveData<MutableList<DetailMemo>>
        get() = _detailMemoList

    private val _removedDetailMemo = MutableLiveData<DetailMemo>()
    val removedDetailMemo : LiveData<DetailMemo>
        get() = _removedDetailMemo
    val savedDetailMemo: DetailMemo?
        get() = _removedDetailMemo.value

    private val _memoForAlarmSetting = MutableLiveData<Memo>()
    val memoForAlarmSetting: LiveData<Memo>
        get() = _memoForAlarmSetting
    val savedMemoForAlarmSetting: Memo?
        get() = _memoForAlarmSetting.value

    fun setEmpty(isEmpty: Boolean) {
        _memoEmpty.postValue(isEmpty)
    }

    fun getDetailMemoByMemoId(memoId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val detailMemoList = detailMemoRepository.getDetailMemoByMemoId(memoId)
            _detailMemoList.postValue(detailMemoList.toMutableList())
        }
    }

    fun insertDetailMemo(detailMemo: DetailMemo) {
        CoroutineScope(Dispatchers.IO).launch {
            detailMemoRepository.insertDetailMemo(detailMemo)
        }
    }

    fun deleteDetailMemoByID(detailMemoId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            detailMemoRepository.deleteDetailMemoByID(detailMemoId)
        }
    }

    fun saveRemovedDetailMemo(detailMemo: DetailMemo) {
        CoroutineScope(Dispatchers.IO).launch {
            _removedDetailMemo.postValue(detailMemo)
        }
    }

    fun getMemoByIdForAlarm(memoId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val memo = memoRepository.getMemoById(memoId)
            _memoForAlarmSetting.postValue(memo)
        }
    }
}