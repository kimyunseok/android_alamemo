package com.landvibe.alamemo.ui.fragment.add

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.landvibe.alamemo.R
import com.landvibe.alamemo.common.AppDataBase
import com.landvibe.alamemo.databinding.FragmentDetailAddOrEditBinding
import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.ui.BaseFragment
import com.landvibe.alamemo.handler.AlarmHandler
import com.landvibe.alamemo.handler.FixNotifyHandler
import java.util.*

class DetailAddOrEditFragment: BaseFragment<FragmentDetailAddOrEditBinding>() {
    override val layoutId: Int = R.layout.fragment_detail_add_or_edit

    override fun init() {
        initDetailMemoModel()
        setBtnOnClickListener()
    }

    private fun initDetailMemoModel() {
        val memoId = arguments?.getLong("memoId")
        val detailMemoId = arguments?.getLong("detailMemoId", -1)
        var type = arguments?.getInt("memoType")

        if(type == 3) {
            //반복 일정의 경우 세부메모만 가능.
            type = 1
        }

        val detailMemo = if(detailMemoId != null && detailMemoId != (-1).toLong()) {
            AppDataBase.instance.detailMemoDao().getDetailMemoById(detailMemoId)
        } else {
            val calendar = Calendar.getInstance()
            memoId?.let { memoId ->

                val memo = AppDataBase.instance.memoDao().getMemoById(memoId)

                DetailMemo(
                    id = 0,
                    memoId = memoId,
                    type = MutableLiveData(type),
                    icon = MutableLiveData( getString(R.string.memo_emoji) ),
                    title = MutableLiveData(""),
                    scheduleDateYear = MutableLiveData(calendar.get(Calendar.YEAR)),
                    scheduleDateMonth = MutableLiveData(calendar.get(Calendar.MONTH)),
                    scheduleDateDay = MutableLiveData(calendar.get(Calendar.DAY_OF_MONTH)),
                    scheduleDateHour = MutableLiveData(calendar.get(Calendar.HOUR_OF_DAY)),
                    scheduleDateMinute = MutableLiveData(calendar.get(Calendar.MINUTE)),
                    memoScheduleDateYear = memo.scheduleDateYear.value,
                    memoScheduleDateMonth = memo.scheduleDateMonth.value,
                    memoScheduleDateDay = memo.scheduleDateDay.value
                )
            }
        }

        viewDataBinding.model = detailMemo
    }

    private fun setBtnOnClickListener() {
        //저장하기 버튼
        viewDataBinding.addOkBtn.setOnClickListener {
            if(viewDataBinding.model?.title?.value.toString().trim() == "") {
                Toast.makeText(requireContext(), getString(R.string.warn_empty_title_message), Toast.LENGTH_SHORT).show()
            } else {
                val model = viewDataBinding.model
                if (model != null) {

                    if(model.type.value == 1) {
                        //메모라면 날짜를 오늘로 수정.
                        model.setMemoScheduleTimeToday()
                    }

                    if (viewDataBinding.model?.id?.toInt() != 0) {
                        //만일 세부사항 수정하기라면
                        AppDataBase.instance.detailMemoDao().modifyDetailMemo(
                            id = model.id,
                            type = model.type,
                            icon = model.icon,
                            title = model.title,
                            scheduleDateYear = model.scheduleDateYear,
                            scheduleDateMonth = model.scheduleDateMonth,
                            scheduleDateDay = model.scheduleDateDay,
                            scheduleDateHour = model.scheduleDateHour,
                            scheduleDateMinute = model.scheduleDateMinute,
                        )
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.detail_memo_modify_complete),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        //새로 생성이라면
                        AppDataBase.instance.detailMemoDao().insertDetailMemo(model)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.detail_memo_save_complete),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    reSetAlarmAndFixNotify()

                } else {
                    Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT).show()
                }
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        //뒤로가기 버튼
        viewDataBinding.addCancelBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        
        //아이콘 선택 버튼
        viewDataBinding.addIconSelectBtn.setOnClickListener {
            viewDataBinding.model?.icon?.let { iconLiveData ->
                SelectIconDialog(requireContext(), iconLiveData).show()
            }
        }

        //달력으로 보기 버튼
        viewDataBinding.addShowDateDialogBtn.setOnClickListener {
            showCalendarDialogBtn()
        }
    }

    private fun showCalendarDialogBtn() {
        val yearValue = viewDataBinding.model?.scheduleDateYear?.value
        val monthValue = viewDataBinding.model?.scheduleDateMonth?.value
        val dayOfMonthValue = viewDataBinding.model?.scheduleDateDay?.value

        val calendarOnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            viewDataBinding.model?.scheduleDateYear?.value = year
            viewDataBinding.model?.scheduleDateMonth?.value = month
            viewDataBinding.model?.scheduleDateDay?.value = dayOfMonth
            viewDataBinding.model?.getDateFormat()
        }

        if(yearValue != null && monthValue != null && dayOfMonthValue != null) {
            val calendarDialog = DatePickerDialog(requireContext(), calendarOnDateSetListener, yearValue, monthValue, dayOfMonthValue)
                .apply {
                    datePicker.minDate = System.currentTimeMillis()
                    val maxDate = viewDataBinding.model?.getMaxDate()
                    maxDate?.let { datePicker.maxDate = it }
                }
            calendarDialog.show()
        }
    }

    private fun reSetAlarmAndFixNotify() {
        val memo = viewDataBinding.model?.memoId?.let { AppDataBase.instance.memoDao().getMemoById(it) }

        //알람설정.
        if(memo?.setAlarm?.value == true) {
            AlarmHandler().cancelAlarm(requireContext(), memo.id)
            AlarmHandler().setMemoAlarm(requireContext(), memo)
        }
        //상단바 고정 설정
        if(memo?.fixNotify?.value == true) {
            FixNotifyHandler().cancelFixNotify(requireContext(), memo.id)
            FixNotifyHandler().setMemoFixNotify(requireContext(), memo)
        }
    }
}