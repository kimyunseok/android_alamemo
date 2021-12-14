package com.landvibe.alamemo.ui.fragment.main

import com.landvibe.alamemo.R
import com.landvibe.alamemo.databinding.FragmentTabBinding
import com.landvibe.alamemo.ui.BaseTabFragment

class FinishFragment: BaseTabFragment<FragmentTabBinding>() {
    override val layoutId: Int = R.layout.fragment_tab
    override val type: Int = 4 // type 4는 실제로는 쓰지 않는 type. 프래그먼트에서만 사용.

}