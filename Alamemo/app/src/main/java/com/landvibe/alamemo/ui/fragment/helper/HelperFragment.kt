package com.landvibe.alamemo.ui.fragment.helper

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.landvibe.alamemo.BuildConfig
import com.landvibe.alamemo.R
import com.landvibe.alamemo.adapter.HelperRecyclerViewAdapter
import com.landvibe.alamemo.databinding.FragmentHelperBinding
import com.landvibe.alamemo.model.uimodel.HelperViewModel
import com.landvibe.alamemo.ui.BaseFragment

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
            HelperViewModel(requireContext().getString(R.string.helper_function_add), 1),
            HelperViewModel(requireContext().getString(R.string.helper_function_delete), 2),
            HelperViewModel(requireContext().getString(R.string.helper_function_edit), 3),
            HelperViewModel(requireContext().getString(R.string.helper_function_finish), 4),
            HelperViewModel(requireContext().getString(R.string.helper_function_alarm), 5),
            HelperViewModel(requireContext().getString(R.string.helper_function_detail), 6)
        )
        viewDataBinding.functionRecyclerView.adapter = HelperRecyclerViewAdapter(requireContext(), helperList)
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