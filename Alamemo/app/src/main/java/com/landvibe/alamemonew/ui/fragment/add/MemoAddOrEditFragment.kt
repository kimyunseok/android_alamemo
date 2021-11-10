package com.landvibe.alamemonew.ui.fragment.add

import androidx.lifecycle.MutableLiveData
import com.landvibe.alamemonew.R
import com.landvibe.alamemonew.common.AppDataBase
import com.landvibe.alamemonew.databinding.FragmentMemoAddOrEditBinding
import com.landvibe.alamemonew.model.data.memo.Memo
import com.landvibe.alamemonew.ui.BaseFragment
import java.util.*

class MemoAddOrEditFragment: BaseFragment<FragmentMemoAddOrEditBinding>() {
    override val layoutId: Int = R.layout.fragment_memo_add_or_edit

    lateinit var memo: Memo

    override fun init() {
        setUpBtnOnClickListener()
        initMemoModel()
    }

    private fun setUpBtnOnClickListener() {
        viewDataBinding.addCancelBtn.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun initMemoModel() {
        val memoId = arguments?.getLong("memoId")
        memo = if(memoId != null) {
            AppDataBase.instance.memoDao().getMemoById(memoId)
        } else {

            Memo(0,
                type = MutableLiveData<Int>(1),
                icon = MutableLiveData<String>(getString(R.string.memo_emoji)),
                title = MutableLiveData<String>(""),
                scheduleDate = MutableLiveData<Date>(Date(System.currentTimeMillis() ) ),
                alarmStartTime = MutableLiveData<Date>(Date(System.currentTimeMillis() ) ),
                fixNotify = MutableLiveData<Boolean>(false),
                setAlarm = MutableLiveData<Boolean>(false),
                repeatDay = mutableListOf()
            )
        }
        viewDataBinding.model = memo
    }
}