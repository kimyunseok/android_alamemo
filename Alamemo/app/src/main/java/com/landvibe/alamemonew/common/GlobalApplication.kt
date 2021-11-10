package com.landvibe.alamemonew.common

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.landvibe.alamemonew.model.data.memo.Memo
import com.landvibe.alamemonew.model.uimodel.MemoViewModel
import java.util.*

/*
안드로이드 기본 어플 state를 관리하는 객체.
Manifest <application> 태그 안에 name에 클래스를지정해서 사용할 수 있다.
App의 instatnce를 선언함으로서 AppDatabase에서 companion object와 연계가능.
 */

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance : GlobalApplication
            private set //

        fun memoToMemoViewModel(memo: Memo, memoViewModel: MemoViewModel) {
            memoViewModel.apply {
                id = memo.id

                type = if(memo.type != 4) {
                    MutableLiveData(memo.type)
                } else {
                    MutableLiveData(1)
                }
                
                icon = MutableLiveData<String>(memo.icon)
                title = MutableLiveData<String>(memo.title)
                scheduleDate = MutableLiveData<Date>(memo.scheduleDate)
                alarmStartTime = MutableLiveData<Date>(memo.alarmStartTime)
                fixNotify = MutableLiveData<Boolean>(memo.fixNotify)
                setAlarm = MutableLiveData<Boolean>(memo.setAlarm)
                repeatDay = memo.repeatDay.toCharArray().toMutableList()
                alarmStartTimeType = MutableLiveData(memo.alarmStartTimeType)
            }
        }

        fun memoViewModelToMemo(memo: Memo, memoViewModel: MemoViewModel?) {
            memo.apply {
                if(memoViewModel != null) {
                    id = memoViewModel.id
                    memoViewModel.type.value?.let {memo.type = it}
                    memoViewModel.icon.value?.let {memo.icon = it}
                    memoViewModel.title.value?.let {memo.title = it}
                    memoViewModel.scheduleDate.value?.let {memo.scheduleDate = it}
                    memoViewModel.alarmStartTime.value?.let {memo.alarmStartTime = it}
                    memoViewModel.fixNotify.value?.let {memo.fixNotify = it}
                    memoViewModel.setAlarm.value?.let {memo.setAlarm = it}
                    memoViewModel.alarmStartTimeType.value?.let {memo.alarmStartTimeType = it}
                    memo.repeatDay = ""
                    for(day in memoViewModel.repeatDay) {
                        repeatDay += memoViewModel.repeatDay
                    }
                }
            }
        }
    }
}