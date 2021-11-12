package com.landvibe.alamemonew.ui.fragment.main

import com.landvibe.alamemonew.R
import com.landvibe.alamemonew.databinding.FragmentTabBinding
import com.landvibe.alamemonew.ui.BaseTabFragment

class MemoFragment: BaseTabFragment<FragmentTabBinding>() {
    override val layoutId: Int = R.layout.fragment_tab
    override val type: Int = 1

    override fun init() {

    }

    override fun setTitle() {
        viewDataBinding.model?.title?.value = context?.getString(R.string.memo_emoji) + " " + context?.getString(R.string.memo)
    }

}