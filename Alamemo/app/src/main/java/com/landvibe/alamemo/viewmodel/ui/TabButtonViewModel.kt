package com.landvibe.alamemo.viewmodel.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TabButtonViewModel: ViewModel() {
    var emoji = MutableLiveData<String>()
    var title = MutableLiveData<String>()
}