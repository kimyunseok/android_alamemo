package com.landvibe.alamemo.viewmodel.aac

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.repository.DetailMemoRepository

class DetailMemoAddOrEditViewModel(private val detailMemoRepository: DetailMemoRepository): ViewModel() {
    private val _detailMemoInfo = MutableLiveData<DetailMemo>()
    val detailMemoInfo: LiveData<DetailMemo>
        get() = _detailMemoInfo
}