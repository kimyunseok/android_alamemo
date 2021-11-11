package com.landvibe.alamemonew.model.uimodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DetailFragmentViewModel: ViewModel() {
    var title = MutableLiveData<String>()
    var memoEmpty = MutableLiveData<Boolean>()
}