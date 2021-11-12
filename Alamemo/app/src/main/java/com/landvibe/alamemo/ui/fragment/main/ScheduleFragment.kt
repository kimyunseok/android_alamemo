package com.landvibe.alamemo.ui.fragment.main

import com.landvibe.alamemo.R
import com.landvibe.alamemo.databinding.FragmentTabBinding
import com.landvibe.alamemo.ui.BaseTabFragment

class ScheduleFragment: BaseTabFragment<FragmentTabBinding>() {
    override val layoutId: Int = R.layout.fragment_tab
    override val type: Int = 2

    override fun init() {

    }

    override fun setTitle() {
        viewDataBinding.model?.title?.value = context?.getString(R.string.today_emoji) + " " + context?.getString(R.string.today_schedule)
    }


}