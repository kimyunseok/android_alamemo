package com.landvibe.alamemo.viewmodel.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.landvibe.alamemo.repository.MemoRepository

class MemoViewModelFactory(private val repository: MemoRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(MemoRepository::class.java).newInstance(repository)
    }
}