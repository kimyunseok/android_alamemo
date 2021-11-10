package com.landvibe.alamemonew.ui.fragment.main

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.landvibe.alamemonew.R
import com.landvibe.alamemonew.adapter.MemoRecyclerViewAdapter
import com.landvibe.alamemonew.common.AppDataBase
import com.landvibe.alamemonew.databinding.TabFragmentBinding
import com.landvibe.alamemonew.ui.BaseTabFragment

class MemoFragment: BaseTabFragment<TabFragmentBinding>() {
    override val layoutId: Int = R.layout.tab_fragment
    override val type: Int = 1

    override fun init() {

    }

    override fun setTitle() {
        viewDataBinding.model?.title?.value = context?.getString(R.string.memo_emoji) + " " + context?.getString(R.string.memo)
    }

}