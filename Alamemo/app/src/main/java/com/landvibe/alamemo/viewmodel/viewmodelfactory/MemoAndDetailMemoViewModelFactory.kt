package com.landvibe.alamemo.viewmodel.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.landvibe.alamemo.repository.DetailMemoRepository
import com.landvibe.alamemo.repository.MemoRepository

class MemoAndDetailMemoViewModelFactory(private val memoRepository: MemoRepository,
                                        private val detailMemoRepository: DetailMemoRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass
            .getConstructor(
                MemoRepository::class.java,
                DetailMemoRepository::class.java)
            .newInstance(
                memoRepository,
                detailMemoRepository)
    }
}