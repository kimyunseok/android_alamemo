package com.landvibe.alamemo.model.uimodel

import androidx.lifecycle.MutableLiveData

data class HelperDialogViewModel (var title: MutableLiveData<String>, var image: MutableLiveData<Int>, var description: MutableLiveData<String>,
                                  var prevEnable: MutableLiveData<Boolean>, var nextEnable: MutableLiveData<Boolean>)