package com.landvibe.alamemo.viewmodel.aac

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.repository.DetailMemoRepository
import com.landvibe.alamemo.repository.MemoRepository

class LongClickDialogViewModel(private val memoRepository: MemoRepository,
                               private val detailMemoRepository: DetailMemoRepository): ViewModel() {

    private val _memo = MutableLiveData<Memo>()
    val memo: LiveData<Memo>
        get() = _memo
    val memoValue: Memo?
        get() = memo.value

    private val _detailMemo = MutableLiveData<DetailMemo>()
    val detailMemo: LiveData<DetailMemo>
        get() = _detailMemo
    val detailMemoValue: DetailMemo?
        get() = detailMemo.value

    var titleIncludeIcon = ""

    fun getMemoInfo(memoId: Long) {
        val memo = memoRepository.getMemoById(memoId)
        _memo.postValue(memo)

        titleIncludeIcon = memo.icon + " " + memo.title
    }

    fun getDetailMemoInfo(detailMemoId: Long) {
        val detailMemo = detailMemoRepository.getDetailMemoById(detailMemoId)
        _detailMemo.postValue(detailMemo)

        titleIncludeIcon = detailMemo.icon + " " + detailMemo.title
    }

}