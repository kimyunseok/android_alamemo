package com.landvibe.alamemo.model.uimodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TabFragmentViewModel: ViewModel() {
    var title = MutableLiveData<String>()
    var memoEmpty = MutableLiveData<Boolean>()
}