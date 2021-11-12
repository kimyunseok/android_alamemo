package com.landvibe.alamemo.model.uimodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TabButtonViewModel: ViewModel() {
    var emoji = MutableLiveData<String>()
    var title = MutableLiveData<String>()
}