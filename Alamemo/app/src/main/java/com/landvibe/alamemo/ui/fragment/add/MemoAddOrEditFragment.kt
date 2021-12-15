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
import com.landvibe.alamemo.repository.MemoRepository
import com.landvibe.alamemo.viewmodel.aac.MemoAddOrEditViewModel
import com.landvibe.alamemo.viewmodel.viewmodelfactory.MemoViewModelFactory
import com.landvibe.alamemo.ui.BaseFragment
import com.landvibe.alamemo.util.MemoUtil

class MemoAddOrEditFragment: BaseFragment<FragmentMemoAddOrEditBinding>() {
    override val layoutId: Int = R.layout.fragment_memo_add_or_edit

    private val viewModel by lazy {
        ViewModelProvider(this, MemoViewModelFactory(MemoRepository())).get(MemoAddOrEditViewModel::class.java)
    }

    override fun init() {
        initViewModel()
        setUpObserver()
        setUpBtnOnClickListener()

        setUpAnimation()
    }

    private fun initViewModel() {
        viewDataBinding.viewModel = viewModel

        val memoId = arguments?.getLong("memoId", -1)

        if(memoId != null && memoId != (-1).toLong()) {
            viewModel.getMemoInfoById(memoId)
        } else {
            viewModel.initMemo()
        }
    }

    private fun setUpObserver() {
        viewModel.memoSaveComplete.observe(this) {
            if(it) {
                //알람 설정과 고정바 설정은 수정됐을 수도 있으므로 일단 해제시켜놓는다.
                if(viewModel.memoIdValue != 0L) {
                    AlarmHandler().cancelAlarm(requireContext(), viewModel.memoIdValue)
                    FixNotifyHandler().cancelFixNotify(requireContext(), viewModel.memoIdValue)
                }

                //알람설정.
                if(viewModel.setAlarm) {
                    AlarmHandler().setMemoAlarm(requireContext(), viewModel.memoIdValue)
                }
                //상단바 고정 설정
                if(viewModel.fixNotify) {
                    FixNotifyHandler().setMemoFixNotify(requireContext(), viewModel.memoIdValue)
                }

                if(viewModel.memoIdValue != 0L) {
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

                requireActivity().supportFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), getString(R.string.warn_empty_title_message), Toast.LENGTH_SHORT).show()
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
            SelectIconDialog(requireContext(), viewModel.memoIcon).show()
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

    private fun setUpAnimation() {
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