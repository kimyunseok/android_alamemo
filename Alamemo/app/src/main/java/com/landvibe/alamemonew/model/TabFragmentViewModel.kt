package com.landvibe.alamemonew.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TabFragmentViewModel: ViewModel() {
    var title = MutableLiveData<String>()
    var memoEmpty = MutableLiveData<Boolean>()
    var memoOpen = MutableLiveData<Boolean>()
}