package com.landvibe.alamemo.viewmodel.aac

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.repository.DetailMemoRepository
import com.landvibe.alamemo.repository.MemoRepository
import com.landvibe.alamemo.util.AboutDay
import com.landvibe.alamemo.util.MemoUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class DetailMemoAddOrEditViewModel(private val detailMemoRepository: DetailMemoRepository,
                                   private val memoRepository: MemoRepository): ViewModel() {
    //메모관련 - Binding 목적

    private val _detailMemoId = MutableLiveData<Long>()
    val detailMemoId: LiveData<Long>
        get() = _detailMemoId
    val detailMemoIdValue = detailMemoId.value as Long

    private val _memoId = MutableLiveData<Long>()
    val memoId: LiveData<Long>
        get() = _memoId
    val memoIdValue = memoId.value as Long

    private val _detailMemoType = MutableLiveData<Int>()
    val detailMemoType: LiveData<Int>
        get() = _detailMemoType
    private val type = detailMemoType.value as Int

    val detailMemoIcon = MutableLiveData<String>()
    private val icon = detailMemoIcon.value as String

    val detailMemoTitle = MutableLiveData<String>()
    private val title = detailMemoTitle.value as String

    val detailMemoScheduleDateYear = MutableLiveData<Int>()
    val scheduleDateYear = detailMemoScheduleDateYear.value as Int

    val detailMemoScheduleDateMonth = MutableLiveData<Int>()
    val scheduleDateMonth = detailMemoScheduleDateMonth.value as Int

    val detailMemoScheduleDateDay = MutableLiveData<Int>()
    val scheduleDateDay = detailMemoScheduleDateDay.value as Int

    val detailMemoScheduleDateHour= MutableLiveData<Int>()
    val scheduleDateHour = detailMemoScheduleDateHour.value as Int

    val detailMemoScheduleDateMinute = MutableLiveData<Int>()
    val scheduleDateMinute = detailMemoScheduleDateMinute.value as Int

    private val _memoScheduleDateYear = MutableLiveData<Int>()
    private val memoScheduleDateYear: LiveData<Int>
        get() = _memoScheduleDateYear
    private val memoScheduleDateYearValue = memoScheduleDateYear.value as Int

    private val _memoScheduleDateMonth = MutableLiveData<Int>()
    private val memoScheduleDateMonth: LiveData<Int>
        get() = _memoScheduleDateMonth
    private val memoScheduleDateMonthValue = memoScheduleDateMonth.value as Int

    private val _memoScheduleDateDay = MutableLiveData<Int>()
    private val memoScheduleDateDay: LiveData<Int>
        get() = _memoScheduleDateDay
    private val memoScheduleDateDayValue = memoScheduleDateDay.value as Int

    private val _detailMemoScheduleDateFormat = MutableLiveData<String>()
    val detailMemoScheduleDateFormat: LiveData<String>
        get() = _detailMemoScheduleDateFormat

    private val _detailMemoSaveComplete = MutableLiveData<Boolean>()
    val detailMemoSaveComplete: LiveData<Boolean>
        get() = _detailMemoSaveComplete

    lateinit var memo: Memo

    fun initDetailMemo(memoId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            memo = memoRepository.getMemoById(memoId)

            val calendar = Calendar.getInstance()

            _detailMemoId.postValue(0L)
            _memoId.postValue(memoId)
            _detailMemoType.postValue(1)
            detailMemoIcon.postValue("📝")
            detailMemoTitle.postValue("")
            detailMemoScheduleDateYear.postValue(calendar.get(Calendar.YEAR))
            detailMemoScheduleDateMonth.postValue(calendar.get(Calendar.MONTH))
            detailMemoScheduleDateDay.postValue(calendar.get(Calendar.DAY_OF_MONTH))
            detailMemoScheduleDateHour.postValue(calendar.get(Calendar.HOUR_OF_DAY))
            detailMemoScheduleDateMinute.postValue(calendar.get(Calendar.MINUTE))
            _memoScheduleDateYear.postValue(memo.scheduleDateYear)
            _memoScheduleDateMonth.postValue(memo.scheduleDateMonth)
            _memoScheduleDateDay.postValue(memo.scheduleDateDay)

            val dayOfWeek = AboutDay.AboutDayOfWeek().getDayOfWeekByDate(scheduleDateYear, scheduleDateMonth, scheduleDateDay)
            _detailMemoScheduleDateFormat.postValue("${(calendar.get(Calendar.YEAR))}년 ${(calendar.get(Calendar.MONTH)).plus(1)}월 " +
                    "${(calendar.get(Calendar.DAY_OF_MONTH))}일 ${dayOfWeek}요일")
        }
    }

    fun getDetailMemoInfoById(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val detailMemo = detailMemoRepository.getDetailMemoById(id)
            memo = memoRepository.getMemoById(detailMemo.memoId)

            _detailMemoId.postValue(id)
            _memoId.postValue(detailMemo.memoId)
            _detailMemoType.postValue(detailMemo.type)
            detailMemoIcon.postValue(detailMemo.icon)
            detailMemoTitle.postValue(detailMemo.title)
            detailMemoScheduleDateYear.postValue(detailMemo.scheduleDateYear)
            detailMemoScheduleDateMonth.postValue(detailMemo.scheduleDateMonth)
            detailMemoScheduleDateDay.postValue(detailMemo.scheduleDateDay)
            detailMemoScheduleDateHour.postValue(detailMemo.scheduleDateHour)
            detailMemoScheduleDateMinute.postValue(detailMemo.scheduleDateMinute)
            _memoScheduleDateYear.postValue(memo.scheduleDateYear)
            _memoScheduleDateMonth.postValue(memo.scheduleDateMonth)
            _memoScheduleDateDay.postValue(memo.scheduleDateDay)

            val dayOfWeek = AboutDay.AboutDayOfWeek().getDayOfWeekByDate(scheduleDateYear, scheduleDateMonth, scheduleDateDay)

            _detailMemoScheduleDateFormat.postValue("${memo.scheduleDateYear}년 ${(memo.scheduleDateMonth).plus(1)}월 " +
                    "${memo.scheduleDateDay}일 ${dayOfWeek}요일")

            checkScheduleTime()
        }
    }

    fun saveDetailMemo() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("DetailMemoAddOrEdit", "Detail Memo Save To Room")
            if(title.trim() == "") {
                _detailMemoSaveComplete.postValue(false)
            } else {
                if(type == 1) {
                    //메모라면 날짜를 오늘로 수정.
                    setMemoScheduleTimeToday()
                }

                if (detailMemoIdValue != 0L) {
                    //만일 세부사항 수정하기라면

                    detailMemoRepository.modifyDetailMemo(
                        id = detailMemoIdValue,
                        type = type,
                        icon = icon,
                        title = title,
                        scheduleDateYear = scheduleDateYear,
                        scheduleDateMonth = scheduleDateMonth,
                        scheduleDateDay = scheduleDateDay,
                        scheduleDateHour = scheduleDateHour,
                        scheduleDateMinute = scheduleDateMinute,
                    )
                } else {
                    //새로 생성이라면

                    detailMemoRepository.insertDetailMemo(
                        DetailMemo(
                        id = detailMemoIdValue,
                        memoId = memoIdValue,
                        type = type,
                        icon = icon,
                        title = title,
                        scheduleDateYear = scheduleDateYear,
                        scheduleDateMonth = scheduleDateMonth,
                        scheduleDateDay = scheduleDateDay,
                        scheduleDateHour = scheduleDateHour,
                        scheduleDateMinute = scheduleDateMinute
                    )
                    )

                }

                _detailMemoSaveComplete.postValue(true)
            }
        }
    }

    private fun setMemoScheduleTimeToday() {
        val calendar = Calendar.getInstance()
        setScheduleDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        setSceduleTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
        resetMemoScheduleDateFormatByDate()
    }

    private fun checkScheduleTime() {
        val todayCalendar = Calendar.getInstance()
        val checkCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, scheduleDateYear)
            set(Calendar.MONTH, scheduleDateMonth)
            set(Calendar.DAY_OF_MONTH, scheduleDateDay)
            set(Calendar.HOUR_OF_DAY, scheduleDateHour)
            set(Calendar.MINUTE, scheduleDateMinute)
        }

        //메모에 설정된 시간이 이전 시간이라면 오늘 시간으로 변경한다.
        if(checkCalendar.timeInMillis < todayCalendar.timeInMillis) {
            setMemoScheduleTimeToday()
        }
    }

    fun setScheduleDate(year: Int, month: Int, day: Int) {
        detailMemoScheduleDateYear.postValue(year)
        detailMemoScheduleDateMonth.postValue(month)
        detailMemoScheduleDateMonth.postValue(day)
        resetMemoScheduleDateFormatByDate()
    }

    fun setSceduleTime(hour: Int, minute: Int) {
        detailMemoScheduleDateHour.postValue(hour)
        detailMemoScheduleDateMinute.postValue(minute)
    }

    fun getMaxDate(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, memoScheduleDateYearValue)
        calendar.set(Calendar.MONTH, memoScheduleDateMonthValue)
        calendar.set(Calendar.DAY_OF_MONTH, memoScheduleDateDayValue)

        return calendar.time.time
    }

    private fun resetMemoScheduleDateFormatByDate() {
        _detailMemoScheduleDateFormat.postValue(MemoUtil().getScheduleDateFormat(scheduleDateYear, scheduleDateMonth, scheduleDateDay))
    }
}