package com.landvibe.alamemonew.ui.fragment.add

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.landvibe.alamemonew.R
import com.landvibe.alamemonew.common.AppDataBase
import com.landvibe.alamemonew.databinding.FragmentMemoAddOrEditBinding
import com.landvibe.alamemonew.model.data.memo.Memo
import com.landvibe.alamemonew.ui.BaseFragment
import com.landvibe.alamemonew.handler.AlarmHandler
import com.landvibe.alamemonew.handler.FixNotifyHandler
import java.util.*

class MemoAddOrEditFragment: BaseFragment<FragmentMemoAddOrEditBinding>() {
    override val layoutId: Int = R.layout.fragment_memo_add_or_edit

    override fun init() {
        setUpBtnOnClickListener()
        initMemoModel()
    }

    private fun setUpBtnOnClickListener() {
        viewDataBinding.addCancelBtn.setOnClickListener { requireActivity().onBackPressed() }

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

                    if(model.type.value != 3) {
                        //반복 일정 아니라면, 반복 요일 선택했던 거 다 지움.
                        model.repeatDay.clear()
                    }

                    if (viewDataBinding.model?.id?.toInt() != 0) {
                        // 메모 id가 존재한다면, 즉 수정하기라면
                        // 메모 수정.
                        AppDataBase.instance.memoDao().modifyMemo(
                            id = model.id,
                            type = model.type,
                            icon = model.icon,
                            title = model.title,
                            scheduleDateYear = model.scheduleDateYear,
                            scheduleDateMonth = model.scheduleDateMonth,
                            scheduleDateDay = model.scheduleDateDay,
                            scheduleDateHour = model.scheduleDateHour,
                            scheduleDateMinute = model.scheduleDateMinute,
                            alarmStartTimeHour = model.alarmStartTimeHour,
                            alarmStartTimeMinute = model.alarmStartTimeMinute,
                            MutableLiveData(false),
                            fixNotify = model.fixNotify,
                            setAlarm = model.setAlarm,
                            repeatDay = model.repeatDay,
                            alarmStartTimeType = model.alarmStartTimeType
                        )

                        Toast.makeText(
                            requireContext(),
                            getString(R.string.memo_modify_complete),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        //메모 새로 생성하기라면

                        // 메모 삽입.
                        model.id = AppDataBase.instance.memoDao().insertMemo(model)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.memo_save_complete),
                            Toast.LENGTH_SHORT
                        ).show()


                    }

                    //알람 설정과 고정바 설정은 수정됐을 수도 있으므로 일단 해제시켜놓는다.
                    AlarmHandler().cancelAlarm(requireContext(), model.id)
                    FixNotifyHandler().cancelFixNotify(requireContext(), model.id)

                    //알람설정.
                    if(model.setAlarm.value == true) {
                        AlarmHandler().setMemoAlarm(requireContext(), model)
                    }
                    //상단바 고정 설정
                    if(model.fixNotify.value == true) {
                        FixNotifyHandler().setMemoFixNotify(requireContext(), model)
                    }

                } else {
                    Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT).show()
                }

                requireActivity().supportFragmentManager.popBackStack()
            }
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

    private fun initMemoModel() {
        val memoId = arguments?.getLong("memoId", -1)

        val memo = if(memoId != null && memoId != (-1).toLong()) {
            AppDataBase.instance.memoDao().getMemoById(memoId)
        } else {
            val calendar = Calendar.getInstance()

            Memo(id = 0,
                type = MutableLiveData(1),
                icon = MutableLiveData( getString(R.string.memo_emoji) ),
                title = MutableLiveData(""),
                scheduleDateYear = MutableLiveData(calendar.get(Calendar.YEAR)),
                scheduleDateMonth = MutableLiveData(calendar.get(Calendar.MONTH)),
                scheduleDateDay = MutableLiveData(calendar.get(Calendar.DAY_OF_MONTH)),
                scheduleDateHour = MutableLiveData(calendar.get(Calendar.HOUR_OF_DAY)),
                scheduleDateMinute = MutableLiveData(calendar.get(Calendar.MINUTE)),
                alarmStartTimeHour = MutableLiveData(calendar.get(Calendar.HOUR_OF_DAY)),
                alarmStartTimeMinute = MutableLiveData(calendar.get(Calendar.MINUTE)),
                scheduleFinish = MutableLiveData(false),
                fixNotify = MutableLiveData(false),
                setAlarm = MutableLiveData(false),
                repeatDay = mutableListOf(),
                alarmStartTimeType = MutableLiveData(1)
            )
        }

        viewDataBinding.model = memo
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
                .apply { datePicker.minDate = System.currentTimeMillis() }
            calendarDialog.show()
        }
    }

}