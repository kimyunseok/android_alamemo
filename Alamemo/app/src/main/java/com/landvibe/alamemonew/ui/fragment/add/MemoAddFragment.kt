package com.landvibe.alamemonew.ui.fragment.add

import com.landvibe.alamemonew.R
import com.landvibe.alamemonew.databinding.FragmentAddBinding
import com.landvibe.alamemonew.ui.BaseFragment

class MemoAddFragment: BaseFragment<FragmentAddBinding>() {
    override val layoutId: Int = R.layout.fragment_add

    override fun init() {
        setUpBtnOnClickListener()
    }

    private fun setUpBtnOnClickListener() {
        viewDataBinding.addCancelBtn.setOnClickListener { requireActivity().onBackPressed() }
    }
}