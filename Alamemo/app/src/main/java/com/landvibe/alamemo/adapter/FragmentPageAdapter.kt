package com.landvibe.alamemo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.landvibe.alamemo.databinding.FragmentTabBinding
import com.landvibe.alamemo.ui.base.BaseTabFragment

/**
 * FragmentStatePagerAdapter Deprecated.
 * Alternate FragmentStateAdapter
 * https://furang-note.tistory.com/26
 * */
class FragmentPageAdapter(fragmentActivity : FragmentActivity) : FragmentStateAdapter(fragmentActivity){
    var fragmentList : ArrayList<BaseTabFragment<FragmentTabBinding>> = ArrayList()

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun addFragment(fragment: BaseTabFragment<FragmentTabBinding>) {
        fragmentList.add(fragment)
        notifyItemInserted(fragmentList.size - 1)
    }

    fun removeFragment() {
        fragmentList.removeLast()
        notifyItemRemoved(fragmentList.size)
    }

}