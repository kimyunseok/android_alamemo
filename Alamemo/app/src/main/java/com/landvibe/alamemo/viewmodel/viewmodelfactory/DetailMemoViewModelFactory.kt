package com.landvibe.alamemo.viewmodel.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.landvibe.alamemo.repository.DetailMemoRepository

class DetailMemoViewModelFactory(private val repository: DetailMemoRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(DetailMemoRepository::class.java).newInstance(repository)
    }
}