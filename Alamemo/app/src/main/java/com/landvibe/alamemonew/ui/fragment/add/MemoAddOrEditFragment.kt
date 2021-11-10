package com.landvibe.alamemonew.ui.fragment.add

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.landvibe.alamemonew.R
import com.landvibe.alamemonew.common.AppDataBase
import com.landvibe.alamemonew.common.GlobalApplication.Companion.memoToMemoViewModel
import com.landvibe.alamemonew.common.GlobalApplication.Companion.memoViewModelToMemo
import com.landvibe.alamemonew.databinding.FragmentMemoAddOrEditBinding
import com.landvibe.alamemonew.model.data.memo.Memo
import com.landvibe.alamemonew.model.uimodel.MemoViewModel
import com.landvibe.alamemonew.ui.BaseFragment
import java.util.*

class MemoAddOrEditFragment: BaseFragment<FragmentMemoAddOrEditBinding>() {
    override val layoutId: Int = R.layout.fragment_memo_add_or_edit

    override fun init() {
        setUpBtnOnClickListener()
        initMemoModel()
    }

    private fun setUpBtnOnClickListener() {
        viewDataBinding.addCancelBtn.setOnClickListener { requireActivity().onBackPressed() }

        viewDataBinding.addOkBtn.setOnClickListener {
            if(viewDataBinding.model?.title?.value.toString().trim() == "") {
                Toast.makeText(requireContext(), getString(R.string.warn_empty_title_message), Toast.LENGTH_SHORT).show()
            } else {
                val memo = Memo(id = 0,
                    type = 1,
                    icon = getString(R.string.memo_emoji),
                    title = "",
                    scheduleDate = Date(System.currentTimeMillis() ),
                    alarmStartTime = Date(System.currentTimeMillis() ),
                    fixNotify = false,
                    setAlarm = false,
                    repeatDay = "",
                    alarmStartTimeType = 1
                )

                if(viewDataBinding.model?.id?.toInt() != 0) {
                    // 메모 id가 존재한다면, 즉 수정하기라면

                    // 1. 뷰모델에 있는 정보 메모 객체로 옮기기
                    memoViewModelToMemo(memo, viewDataBinding.model)

                    // 2. 메모 수정.
                    AppDataBase.instance.memoDao().modifyMemo(
                        id = memo.id,
                        type = memo.type,
                        icon = memo.icon,
                        title = memo.title,
                        scheduleDate = memo.scheduleDate,
                        alarmStartTime = memo.alarmStartTime,
                        fixNotify = memo.fixNotify,
                        setAlarm = memo.setAlarm,
                        repeatDay = memo.repeatDay,
                        alarmStartTimeType = memo.alarmStartTimeType
                    )

                    Toast.makeText(requireContext(), getString(R.string.memo_modify_complete), Toast.LENGTH_SHORT).show()
                } else {
                    //메모 새로 생성하기라면

                    // 1. 뷰모델에 있는 정보 메모 객체로 옮기기
                    memoViewModelToMemo(memo, viewDataBinding.model)

                    // 2. 메모 삽입.
                    AppDataBase.instance.memoDao().insertMemo(memo)
                    Toast.makeText(requireContext(), getString(R.string.memo_save_complete), Toast.LENGTH_SHORT).show()
                }
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

    }

    private fun initMemoModel() {
        val memoId = arguments?.getLong("memoId")

        val memoViewModel = MemoViewModel(
            0,
            type = MutableLiveData<Int>(1),
            icon = MutableLiveData<String>(getString(R.string.memo_emoji)),
            title = MutableLiveData<String>(""),
            scheduleDate = MutableLiveData<Date>(Date(System.currentTimeMillis() ) ),
            alarmStartTime = MutableLiveData<Date>(Date(System.currentTimeMillis() ) ),
            fixNotify = MutableLiveData<Boolean>(false),
            setAlarm = MutableLiveData<Boolean>(false),
            repeatDay = mutableListOf(),
            alarmStartTimeType = MutableLiveData(1)
        )

        if(memoId != null) {
            val getMemo = AppDataBase.instance.memoDao().getMemoById(memoId)
            memoToMemoViewModel(getMemo, memoViewModel)
        }
        viewDataBinding.model = memoViewModel
    }
}