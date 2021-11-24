package com.landvibe.alamemo.model.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.landvibe.alamemo.model.AlamemoRepository


class AlamemoViewModelFactory(private val repository: AlamemoRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(AlamemoRepository::class.java).newInstance(repository)
    }
}