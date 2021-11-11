package com.landvibe.alamemonew.ui.fragment.main

import com.landvibe.alamemonew.R
import com.landvibe.alamemonew.databinding.FragmentDetailBinding
import com.landvibe.alamemonew.ui.BaseFragment

class DetailFragment: BaseFragment<FragmentDetailBinding>() {
    override val layoutId: Int = R.layout.fragment_detail

    private var memoId: Long? = -1

    override fun init() {
        getMemoInfo()
    }

    private fun getMemoInfo() {
        if(arguments != null) {
            memoId = arguments?.getLong("memoId")
        }
        val memoTitle = arguments?.getString("memoTitle", "-")
        viewDataBinding.title = memoTitle
    }
}