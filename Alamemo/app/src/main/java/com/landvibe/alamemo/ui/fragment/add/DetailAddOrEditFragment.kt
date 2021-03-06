package com.landvibe.alamemo.ui.fragment.add

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.landvibe.alamemo.R
import com.landvibe.alamemo.databinding.FragmentDetailAddOrEditBinding
import com.landvibe.alamemo.handler.AlarmHandler
import com.landvibe.alamemo.handler.FixNotifyHandler
import com.landvibe.alamemo.repository.DetailMemoRepository
import com.landvibe.alamemo.repository.MemoRepository
import com.landvibe.alamemo.ui.base.BaseFragment
import com.landvibe.alamemo.ui.dialog.SelectIconDialog
import com.landvibe.alamemo.viewmodel.aac.DetailMemoAddOrEditViewModel
import com.landvibe.alamemo.viewmodel.aac.MemoListUpdateViewModel
import com.landvibe.alamemo.viewmodel.viewmodelfactory.MemoAndDetailMemoViewModelFactory

class DetailAddOrEditFragment: BaseFragment<FragmentDetailAddOrEditBinding>() {
    override val layoutId: Int = R.layout.fragment_detail_add_or_edit

    private val viewModel by lazy {
        ViewModelProvider(this,
            MemoAndDetailMemoViewModelFactory(MemoRepository(),
                DetailMemoRepository())).get(DetailMemoAddOrEditViewModel::class.java)
    }

    lateinit var memoListUpdateViewModel: MemoListUpdateViewModel

    private val memoId: Long by lazy {
        arguments?.getLong("memoId") ?: -1L
    }

    override fun init() {
        initViewModel()
        setUpObserver()
        setBtnOnClickListener()
    }

    private fun initViewModel() {
        memoListUpdateViewModel = ViewModelProvider(requireActivity(), MemoAndDetailMemoViewModelFactory(MemoRepository(), DetailMemoRepository())).get(
            MemoListUpdateViewModel::class.java)

        val detailMemoId = arguments?.getLong("detailMemoId", -1)

        if(detailMemoId != null && detailMemoId != (-1).toLong()) {
            viewModel.getDetailMemoInfoById(detailMemoId)
        } else {
            viewModel.initDetailMemo(memoId)
        }

        viewDataBinding.viewModel = viewModel
    }

    private fun setUpObserver() {
        viewModel.detailMemoSaveComplete.observe(viewLifecycleOwner) {
            if(it) {
                if(viewModel.detailMemoIdValue != 0L) {
                    //Memo Edit
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.detail_memo_modify_complete),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    //Memo First Save
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.detail_memo_save_complete),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                resetAlarmAndFixNotify()

                memoListUpdateViewModel.getRecentDetailMemoList(memoId)

                requireActivity().supportFragmentManager.popBackStack()

            } else {
                Toast.makeText(requireContext(), getString(R.string.warn_empty_title_message), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setBtnOnClickListener() {
        //???????????? ??????
        viewDataBinding.addOkBtn.setOnClickListener {
            viewModel.saveDetailMemo()
        }

        //???????????? ??????
        viewDataBinding.addCancelBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        
        //????????? ?????? ??????
        viewDataBinding.addIconSelectBtn.setOnClickListener {
            SelectIconDialog(viewModel.detailMemoIcon).show(childFragmentManager, null)
        }

        //???????????? ?????? ??????
        viewDataBinding.addShowDateDialogBtn.setOnClickListener {
            showCalendarDialogBtn()
        }
    }

    private fun showCalendarDialogBtn() {
        val yearValue = viewDataBinding.viewModel?.scheduleDateYear
        val monthValue = viewDataBinding.viewModel?.scheduleDateMonth
        val dayOfMonthValue = viewDataBinding.viewModel?.scheduleDateDay

        val calendarOnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            viewModel.setScheduleDate(year, month, dayOfMonth)
        }

        if(yearValue != null && monthValue != null && dayOfMonthValue != null) {
            val calendarDialog = DatePickerDialog(requireContext(), calendarOnDateSetListener, yearValue, monthValue, dayOfMonthValue)
                .apply {
                    datePicker.minDate = System.currentTimeMillis()
                    val maxDate = viewDataBinding.viewModel?.maxDateValue
                    maxDate?.let { datePicker.maxDate = it }
                }
            calendarDialog.show()
        }
    }

    private fun resetAlarmAndFixNotify() {
        val memo = viewDataBinding.viewModel?.memo

        //????????????.
        if(memo?.setAlarm == true) {
            AlarmHandler().cancelAlarm(requireContext(), memo.id)
            AlarmHandler().setMemoAlarm(requireContext(), memo.id)
        }
        //????????? ?????? ??????
        if(memo?.fixNotify == true) {
            FixNotifyHandler().cancelFixNotify(requireContext(), memo.id)
            FixNotifyHandler().setMemoFixNotify(requireContext(), memo.id)
        }
    }
}