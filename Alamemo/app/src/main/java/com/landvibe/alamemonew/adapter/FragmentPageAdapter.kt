package com.landvibe.alamemonew.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * FragmentStatePagerAdapter Deprecated.
 * Alternate FragmentStateAdapter
 * https://furang-note.tistory.com/26
 * */
class FragmentPageAdapter(fragmentActivity : FragmentActivity) : FragmentStateAdapter(fragmentActivity){
    private var fragmentList : ArrayList<Fragment> = ArrayList()

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
        notifyItemInserted(fragmentList.size - 1)
    }

    fun removeFragment() {
        fragmentList.removeLast()
        notifyItemRemoved(fragmentList.size)
    }

}