package com.landvibe.alamemonew.ui.fragment.add

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.landvibe.alamemonew.R
import com.landvibe.alamemonew.common.AppDataBase
import com.landvibe.alamemonew.databinding.FragmentMemoAddOrEditBinding
import com.landvibe.alamemonew.model.data.memo.Memo
import com.landvibe.alamemonew.ui.BaseFragment
import java.util.*

class MemoAddOrEditFragment: BaseFragment<FragmentMemoAddOrEditBinding>() {
    override val layoutId: Int = R.layout.fragment_memo_add_or_edit

    override fun init() {
        setUpBtnOnClickListener()
        initMemoModel()

        setUpObserver()
    }

    private fun setUpBtnOnClickListener() {
        viewDataBinding.addCancelBtn.setOnClickListener { requireActivity().onBackPressed() }

        viewDataBinding.addOkBtn.setOnClickListener {
            if(viewDataBinding.model?.title?.value.toString().trim() == "") {
                Toast.makeText(requireContext(), getString(R.string.warn_empty_title_message), Toast.LENGTH_SHORT).show()
            } else {
                val model = viewDataBinding.model

                if (model != null) {
                    if (viewDataBinding.model?.id?.toInt() != 0) {
                        // 메모 id가 존재한다면, 즉 수정하기라면
                        // 2. 메모 수정.
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
                        AppDataBase.instance.memoDao().insertMemo(model)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.memo_save_complete),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT).show()
                }
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

    }

    private fun initMemoModel() {
        val memoId = arguments?.getLong("memoId")

        val memo = if(memoId != null) {
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
                fixNotify = MutableLiveData(false),
                setAlarm = MutableLiveData(false),
                repeatDay = mutableListOf(),
                alarmStartTimeType = MutableLiveData(1)
            )
        }

        viewDataBinding.model = memo
    }

    private fun setUpObserver() {
        viewDataBinding.model?.scheduleDateYear?.observe(this) {
            viewDataBinding.addDateShowTv.text = viewDataBinding.model?.showDateFormat?.value
        }
    }

}