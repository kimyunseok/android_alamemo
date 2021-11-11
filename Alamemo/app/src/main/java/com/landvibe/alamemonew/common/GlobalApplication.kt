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
            var calendar: Calendar
            memoViewModel.apply {
                id = memo.id
                type = MutableLiveData(memo.type)
                icon = MutableLiveData<String>(memo.icon)
                title = MutableLiveData<String>(memo.title)

                calendar = Calendar.getInstance()
                calendar.time = memo.scheduleDate
                scheduleDateYear = MutableLiveData<Int>(calendar.get(Calendar.YEAR))
                scheduleDateMonth = MutableLiveData<Int>(calendar.get(Calendar.MONTH) + 1)
                scheduleDateDay = MutableLiveData<Int>(calendar.get(Calendar.DAY_OF_MONTH))
                scheduleDateHour = MutableLiveData<Int>(calendar.get(Calendar.HOUR_OF_DAY))
                scheduleDateMinute = MutableLiveData<Int>(calendar.get(Calendar.MINUTE))

                calendar = Calendar.getInstance()
                calendar.time = memo.alarmStartTime
                alarmStartTimeHour = MutableLiveData<Int>(calendar.get(Calendar.HOUR_OF_DAY))
                alarmStartTimeMinute = MutableLiveData<Int>(calendar.get(Calendar.MINUTE))

                fixNotify = MutableLiveData<Boolean>(memo.fixNotify)
                setAlarm = MutableLiveData<Boolean>(memo.setAlarm)
                repeatDay = memo.repeatDay.toCharArray().toMutableList()
                alarmStartTimeType = MutableLiveData(memo.alarmStartTimeType)
            }
        }

        fun memoViewModelToMemo(memo: Memo, memoViewModel: MemoViewModel?) {
            var calendar: Calendar

            memo.apply {
                if(memoViewModel != null) {
                    id = memoViewModel.id
                    memoViewModel.type.value?.let {memo.type = it}
                    memoViewModel.icon.value?.let {memo.icon = it}
                    memoViewModel.title.value?.let {memo.title = it}
                    
                    calendar = Calendar.getInstance()
                    memoViewModel.scheduleDateYear.value?.let { year -> calendar.set(Calendar.YEAR, year) }
                    memoViewModel.scheduleDateMonth.value?.let { month -> calendar.set(Calendar.MONTH, month) }
                    memoViewModel.scheduleDateDay.value?.let { day -> calendar.set(Calendar.DAY_OF_MONTH, day) }
                    memoViewModel.scheduleDateHour.value?.let { hour -> calendar.set(Calendar.HOUR_OF_DAY, hour) }
                    memoViewModel.scheduleDateMinute.value?.let { minute -> calendar.set(Calendar.MINUTE, minute) }
                    memo.scheduleDate = calendar.time
                    
                    calendar = Calendar.getInstance()
                    memoViewModel.alarmStartTimeHour.value?.let { hour -> calendar.set(Calendar.HOUR_OF_DAY, hour) }
                    memoViewModel.alarmStartTimeMinute.value?.let { minute -> calendar.set(Calendar.MINUTE, minute) }
                    memo.alarmStartTime = calendar.time

                    memoViewModel.fixNotify.value?.let {memo.fixNotify = it}
                    memoViewModel.setAlarm.value?.let {memo.setAlarm = it}
                    memoViewModel.alarmStartTimeType.value?.let {memo.alarmStartTimeType = it}
                    memo.repeatDay = ""
                    for(day in memoViewModel.repeatDay) {
                        repeatDay += day
                    }
                }
            }
        }
    }
}