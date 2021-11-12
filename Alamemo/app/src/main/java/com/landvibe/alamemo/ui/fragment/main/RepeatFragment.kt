package com.landvibe.alamemo.ui.fragment.main

import com.landvibe.alamemo.R
import com.landvibe.alamemo.databinding.FragmentTabBinding
import com.landvibe.alamemo.ui.BaseTabFragment

class RepeatFragment: BaseTabFragment<FragmentTabBinding>() {
    override val layoutId: Int = R.layout.fragment_tab
    override val type: Int = 3

    override fun init() {

    }

    override fun setTitle() {
        viewDataBinding.model?.title?.value = context?.getString(R.string.repeat_emoji) + " " + context?.getString(R.string.repeat_schedule)
    }

}