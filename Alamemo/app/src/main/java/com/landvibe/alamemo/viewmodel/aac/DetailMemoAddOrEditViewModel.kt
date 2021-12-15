package com.landvibe.alamemo.viewmodel.aac

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.repository.DetailMemoRepository
import com.landvibe.alamemo.repository.MemoRepository
import com.landvibe.alamemo.util.MemoUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class DetailMemoAddOrEditViewModel(private val detailMemoRepository: DetailMemoRepository,
                                   private val memoRepository: MemoRepository): ViewModel() {
    private val _detailMemoInfo = MutableLiveData<DetailMemo>()
    val detailMemoInfo: LiveData<DetailMemo>
        get() = _detailMemoInfo
    val currentDetailMemo: DetailMemo?
        get() = _detailMemoInfo.value

    private val _detailMemoSaveComplete = MutableLiveData<Boolean>()
    val detailMemoSaveComplete: LiveData<Boolean>
        get() = _detailMemoSaveComplete

    lateinit var memo: Memo

    fun initDetailMemo(memoId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            memo = memoRepository.getMemoById(memoId)

            val calendar = Calendar.getInstance()

            val detailMemo = DetailMemo(id = 0,
                memoId = memo.id,
                type = (1),
                icon = ("📝"),
                title = (""),
                scheduleDateYear = (calendar.get(Calendar.YEAR)),
                scheduleDateMonth = (calendar.get(Calendar.MONTH)),
                scheduleDateDay = (calendar.get(Calendar.DAY_OF_MONTH)),
                scheduleDateHour = (calendar.get(Calendar.HOUR_OF_DAY)),
                scheduleDateMinute = (calendar.get(Calendar.MINUTE)),
                memoScheduleDateYear = memo.scheduleDateYear,
                memoScheduleDateMonth = memo.scheduleDateMonth,
                memoScheduleDateDay = memo.scheduleDateDay,
                showDateFormat = "${(calendar.get(Calendar.YEAR))}년 ${(calendar.get(Calendar.MONTH)).plus(1)}월 " +
                        "${(calendar.get(Calendar.DAY_OF_MONTH))}일 ${calendar.get(Calendar.DAY_OF_WEEK)}요일"
            )
            _detailMemoInfo.postValue(detailMemo)
        }
    }

    fun getDetailMemoInfoById(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val detailMemo = detailMemoRepository.getDetailMemoById(id)
            memo = memoRepository.getMemoById(detailMemo.memoId)

            _detailMemoInfo.postValue(detailMemo)
            checkScheduleTime()
        }
    }

    fun saveDetailMemo() {
        CoroutineScope(Dispatchers.IO).launch {
            if(currentDetailMemo?.title?.trim() == "") {
                _detailMemoSaveComplete.postValue(false)
            } else {
                if(currentDetailMemo?.type == 1) {
                    //메모라면 날짜를 오늘로 수정.
                    setMemoScheduleTimeToday()
                }

                if (currentDetailMemo?.id?.toInt() != 0) {
                    //만일 세부사항 수정하기라면

                    currentDetailMemo?.let {
                        detailMemoRepository.modifyDetailMemo(
                            id = it.id,
                            type = it.type,
                            icon = it.icon,
                            title = it.title,
                            scheduleDateYear = it.scheduleDateYear,
                            scheduleDateMonth = it.scheduleDateMonth,
                            scheduleDateDay = it.scheduleDateDay,
                            scheduleDateHour = it.scheduleDateHour,
                            scheduleDateMinute = it.scheduleDateMinute,
                        )
                        _detailMemoSaveComplete.postValue(true)
                    }
                } else {
                    //새로 생성이라면
                    currentDetailMemo?.let {
                        detailMemoRepository.insertDetailMemo(it)
                        _detailMemoSaveComplete.postValue(true)
                    }
                }
            }
        }
    }

    private fun setMemoScheduleTimeToday() {
        val calendar = Calendar.getInstance()
        setScheduleDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        setSceduleTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
        MemoUtil().setDetailMemoDate(currentDetailMemo)
    }

    private fun checkScheduleTime() {
        val todayCalendar = Calendar.getInstance()
        val checkCalendar = Calendar.getInstance().apply {
            currentDetailMemo?.scheduleDateYear?.let { set(Calendar.YEAR, it) }
            currentDetailMemo?.scheduleDateMonth?.let { set(Calendar.MONTH, it) }
            currentDetailMemo?.scheduleDateDay?.let { set(Calendar.DAY_OF_MONTH, it) }
            currentDetailMemo?.scheduleDateHour?.let { set(Calendar.HOUR_OF_DAY, it) }
            currentDetailMemo?.scheduleDateMinute?.let { set(Calendar.MINUTE, it) }
        }

        //메모에 설정된 시간이 이전 시간이라면 오늘 시간으로 변경한다.
        if(checkCalendar.timeInMillis < todayCalendar.timeInMillis) {
            setMemoScheduleTimeToday()
        }
    }

    fun setScheduleDate(year: Int, month: Int, day: Int) {
        currentDetailMemo?.let {
            it.scheduleDateYear = year
            it.scheduleDateMonth = month
            it.scheduleDateDay = day
        }
        MemoUtil().setDetailMemoDate(currentDetailMemo)
    }

    fun setSceduleTime(hour: Int, minute: Int) {
        currentDetailMemo?.let {
            it.scheduleDateHour = hour
            it.scheduleDateMinute = minute
        }
        MemoUtil().setDetailMemoDate(currentDetailMemo)
    }

    fun getMaxDate(): Long {
        val calendar = Calendar.getInstance()

        currentDetailMemo?.let { it ->
            it.memoScheduleDateYear?.let { year -> calendar.set(Calendar.YEAR, year) }
            it.memoScheduleDateMonth?.let { month ->calendar.set(Calendar.MONTH, month) }
            it.memoScheduleDateDay?.let { day -> calendar.set(Calendar.DAY_OF_MONTH, day) }
        }

        return calendar.time.time
    }

}