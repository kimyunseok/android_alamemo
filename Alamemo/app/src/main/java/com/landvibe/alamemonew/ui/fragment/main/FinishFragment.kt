package com.landvibe.alamemonew.ui.fragment.main

import com.landvibe.alamemonew.R
import com.landvibe.alamemonew.databinding.FragmentTabBinding
import com.landvibe.alamemonew.ui.BaseTabFragment

class FinishFragment: BaseTabFragment<FragmentTabBinding>() {
    override val layoutId: Int = R.layout.fragment_tab
    override val type: Int = 4 // type 4는 실제로는 쓰지 않는 type. 프래그먼트에서만 사용.

    override fun init() {

    }

    override fun setTitle() {
        viewDataBinding.model?.title?.value = context?.getString(R.string.finish_emoji) + " " + context?.getString(R.string.finish_schedule)
    }


}