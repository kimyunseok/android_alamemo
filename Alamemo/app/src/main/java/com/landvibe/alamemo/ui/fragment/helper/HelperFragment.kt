package com.landvibe.alamemo.ui.fragment.helper

import android.annotation.SuppressLint
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.landvibe.alamemo.BuildConfig
import com.landvibe.alamemo.R
import com.landvibe.alamemo.adapter.HelperRecyclerViewAdapter
import com.landvibe.alamemo.databinding.FragmentHelperBinding
import com.landvibe.alamemo.viewmodel.ui.HelperViewModel
import com.landvibe.alamemo.ui.base.BaseFragment

class HelperFragment: BaseFragment<FragmentHelperBinding>() {
    override val layoutId: Int = R.layout.fragment_helper

    override fun init() {
        setUpBtnClickListener()
        setUpFunctionRecyclerView()
        setUpVersion()
    }

    private fun setUpBtnClickListener() {
        viewDataBinding.backButton.setOnClickListener { requireActivity().onBackPressed() }
        viewDataBinding.mailToDeveloperBtn.setOnClickListener { sendEmailToDeveloper() }
    }

    private fun setUpFunctionRecyclerView() {
        val helperList = mutableListOf(
            HelperViewModel("메모/일정 추가", 1),
            HelperViewModel("메모/일정 삭제", 2),
            HelperViewModel("메모/일정 종료", 3),
            HelperViewModel("알람/상단바 고정 관련", 4),
            HelperViewModel("메모/일정 수정", 5),
            HelperViewModel( "세부 메모/일정", 6),
            HelperViewModel( "세부 메뉴(메모/일정 공유, 복사 기능 등)", 7)
        )
        viewDataBinding.functionRecyclerView.adapter = HelperRecyclerViewAdapter(childFragmentManager, helperList)
        viewDataBinding.functionRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    //개발자에게 이메일을 보내는 메서드. https://deumdroid.tistory.com/ 참고
    @SuppressLint("QueryPermissionsNeeded")
    private fun sendEmailToDeveloper() {
        val emailAddress = "psknal27@gmail.com"

        val intent = Intent(Intent.ACTION_SEND)
            .apply {
                type = "plain/text"

                putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
                putExtra(Intent.EXTRA_SUBJECT, "")
                putExtra(Intent.EXTRA_TEXT, "")
            }
        startActivity(intent)
    }

    private fun setUpVersion() {
        viewDataBinding.version = requireContext().getString(R.string.helper_app_version) + " " + BuildConfig.VERSION_NAME
    }
}