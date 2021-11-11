package com.landvibe.alamemonew.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.landvibe.alamemonew.databinding.TabFragmentBinding
import com.landvibe.alamemonew.ui.BaseTabFragment

/**
 * FragmentStatePagerAdapter Deprecated.
 * Alternate FragmentStateAdapter
 * https://furang-note.tistory.com/26
 * */
class FragmentPageAdapter(fragmentActivity : FragmentActivity) : FragmentStateAdapter(fragmentActivity){
    var fragmentList : ArrayList<BaseTabFragment<TabFragmentBinding>> = ArrayList()

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun addFragment(fragment: BaseTabFragment<TabFragmentBinding>) {
        fragmentList.add(fragment)
        notifyItemInserted(fragmentList.size - 1)
    }

    fun removeFragment() {
        fragmentList.removeLast()
        notifyItemRemoved(fragmentList.size)
    }

}