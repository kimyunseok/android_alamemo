package com.landvibe.alamemo.ui.fragment.helper

import androidx.recyclerview.widget.LinearLayoutManager
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
    }

    private fun setUpBtnClickListener() {
        viewDataBinding.backButton.setOnClickListener { requireActivity().onBackPressed() }
        viewDataBinding.mailToDeveloperBtn.setOnClickListener {  }
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
}