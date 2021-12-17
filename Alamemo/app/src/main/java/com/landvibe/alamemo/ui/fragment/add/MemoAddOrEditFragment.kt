package com.landvibe.alamemo.ui.fragment.add

import android.app.DatePickerDialog
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.landvibe.alamemo.R
import com.landvibe.alamemo.databinding.FragmentMemoAddOrEditBinding
import com.landvibe.alamemo.handler.AlarmHandler
import com.landvibe.alamemo.handler.FixNotifyHandler
import com.landvibe.alamemo.repository.DetailMemoRepository
import com.landvibe.alamemo.repository.MemoRepository
import com.landvibe.alamemo.viewmodel.aac.MemoAddOrEditViewModel
import com.landvibe.alamemo.viewmodel.viewmodelfactory.MemoViewModelFactory
import com.landvibe.alamemo.ui.base.BaseFragment
import com.landvibe.alamemo.ui.dialog.SelectIconDialog
import com.landvibe.alamemo.viewmodel.aac.MemoListUpdateViewModel
import com.landvibe.alamemo.viewmodel.viewmodelfactory.MemoAndDetailMemoViewModelFactory

class MemoAddOrEditFragment: BaseFragment<FragmentMemoAddOrEditBinding>() {
    override val layoutId: Int = R.layout.fragment_memo_add_or_edit

    private val viewModel by lazy {
        ViewModelProvider(this, MemoViewModelFactory(MemoRepository())).get(MemoAddOrEditViewModel::class.java)
    }

    private val memoListUpdateViewModel: MemoListUpdateViewModel by lazy {
        ViewModelProvider(requireActivity(), MemoAndDetailMemoViewModelFactory(MemoRepository(), DetailMemoRepository())).get(
            MemoListUpdateViewModel::class.java)
    }

    // 메모 수정 시 불러오는 memoId, 메모 최초 작성시에는 해당 값이 -1
    private val memoId: Long by lazy {
        arguments?.getLong("memoId", -1) ?: -1L
    }

    override fun init() {
        initViewModel()
        setUpObserver()
        setUpBtnOnClickListener()

        setUpExpandAnimation()
    }

    private fun initViewModel() {
        viewDataBinding.viewModel = viewModel

        if(memoId != -1L) {
            viewModel.getMemoInfoById(memoId)
        } else {
            viewModel.initMemo()
        }
    }

    private fun setUpObserver() {
        viewModel.memoSaveComplete.observe(this) {
            when (it) {
                -1L -> {
                    Toast.makeText(requireContext(), getString(R.string.warn_empty_title_message), Toast.LENGTH_SHORT).show()
                }
                -2L -> {
                    Toast.makeText(requireContext(), getString(R.string.warn_type_message), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    viewModel.memoIdValue.let { id ->
                        //알람 설정과 고정바 설정은 수정됐을 수도 있으므로 일단 해제시켜놓는다.
                        AlarmHandler().cancelAlarm(requireContext(), id)
                        FixNotifyHandler().cancelFixNotify(requireContext(), id)

                        //알람설정.
                        if(viewModel.setAlarm) {
                            AlarmHandler().setMemoAlarm(requireContext(), id)
                        }
                        //상단바 고정 설정
                        if(viewModel.fixNotify) {
                            FixNotifyHandler().setMemoFixNotify(requireContext(), id)
                        }
                    }

                    if(memoId != -1L) {
                        //Memo Edit
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.memo_modify_complete),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        //Memo First Save
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.memo_save_complete),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    if(viewModel.prevType != -1 && viewModel.prevType != viewModel.type) {
                        memoListUpdateViewModel.getRecentMemoList(viewModel.prevType)
                    }
                    memoListUpdateViewModel.getRecentMemoList(viewModel.type)

                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun setUpBtnOnClickListener() {
        viewDataBinding.addCancelBtn.setOnClickListener { requireActivity().onBackPressed() }

        //저장하기 버튼
        viewDataBinding.addOkBtn.setOnClickListener {
            viewModel.saveMemo()
        }

        //아이콘 선택 버튼
        viewDataBinding.addIconSelectBtn.setOnClickListener {
            SelectIconDialog(viewModel.memoIcon).show(childFragmentManager, null)
        }
        
        //달력으로 보기 버튼
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
                .apply { datePicker.minDate = System.currentTimeMillis() }
            calendarDialog.show()
        }
    }

    private fun setUpExpandAnimation() {
        viewDataBinding.addOrEditMemoLayout.let { it.animation = expandAction(it) }
    }

    /**
     * https://android-dev.tistory.com/59
     * 블로그를 참고했다.
     */
    private fun expandAction(view: View) : Animation {
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val actualHeight = view.measuredHeight

        view.layoutParams.height = 0
        view.visibility = View.VISIBLE

        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                view.layoutParams.height = if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT
                else (actualHeight * interpolatedTime).toInt()

                view.requestLayout()
            }
        }

        animation.duration = 500 + (actualHeight / view.context.resources.displayMetrics.density).toLong()

        view.startAnimation(animation)

        return animation
    }

}