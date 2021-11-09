package com.landvibe.alamemonew.ui.fragment.main

import com.landvibe.alamemonew.R
import com.landvibe.alamemonew.databinding.TabFragmentBinding
import com.landvibe.alamemonew.ui.BaseTabFragment

class MemoFragment: BaseTabFragment<TabFragmentBinding>() {
    override val layoutId: Int = R.layout.tab_fragment

    override fun init() {

    }

    override fun setTitle() {
        viewDataBinding.model?.title?.value = context?.getString(R.string.memo_emoji) + " " + context?.getString(R.string.memo)
    }

}