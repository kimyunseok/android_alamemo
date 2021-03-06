package com.landvibe.alamemo.ui.fragment.main

import android.view.View
import com.google.android.material.tabs.TabLayoutMediator
import com.landvibe.alamemo.R
import com.landvibe.alamemo.adapter.FragmentPageAdapter
import com.landvibe.alamemo.databinding.FragmentMainBinding
import com.landvibe.alamemo.databinding.FragmentTabBinding
import com.landvibe.alamemo.databinding.HolderTabButtonBinding
import com.landvibe.alamemo.ui.base.BaseFragment
import com.landvibe.alamemo.ui.base.BaseTabFragment
import com.landvibe.alamemo.ui.fragment.add.MemoAddOrEditFragment
import com.landvibe.alamemo.ui.fragment.helper.HelperFragment
import com.landvibe.alamemo.viewmodel.ui.TabButtonViewModel

class MainFragment: BaseFragment<FragmentMainBinding>() {
    override val layoutId: Int = R.layout.fragment_main

    object MemoFragment: BaseTabFragment<FragmentTabBinding>() { override val type: Int = 1 }
    object ScheduleFragment: BaseTabFragment<FragmentTabBinding>() { override val type: Int = 2 }
    object RepeatFragment: BaseTabFragment<FragmentTabBinding>() { override val type: Int = 3 }
    object FinishFragment: BaseTabFragment<FragmentTabBinding>() { override val type: Int = 4 }

    override fun init() {
        setUpBtnOnClickListener()
        initViewPager()
        initTabLayout()
    }

    private fun setUpBtnOnClickListener() {
        viewDataBinding.mainAddMemoButton.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_left)
                .replace(R.id.main_container, MemoAddOrEditFragment())
                .addToBackStack(null)
                .commit()
        }

        viewDataBinding.mainGuideButton.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_left)
                .replace(R.id.main_container, HelperFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun initViewPager() {
        val pagerAdapter = FragmentPageAdapter(requireActivity())
        pagerAdapter.apply {
            addFragment(MemoFragment)
            addFragment(ScheduleFragment)
            addFragment(RepeatFragment)
            addFragment(FinishFragment)
        }

        viewDataBinding.mainViewPager.adapter = pagerAdapter
        viewDataBinding.mainViewPager.offscreenPageLimit = 3 // State ??? ??? 3????????? ??????
        viewDataBinding.mainViewPager.isUserInputEnabled = false // ???????????? ??????
    }

    private fun initTabLayout() {
        //viewDataBinding.mainTabLayout.setupWithViewPager(viewDataBinding.mainViewPager) ViewPager2????????? ????????????.
        TabLayoutMediator(viewDataBinding.mainTabLayout, viewDataBinding.mainViewPager) {
                tab, position -> tab.customView = createTabView(position)
        }.attach()
    }

    private fun createTabView(position: Int): View {
        val tabBtnBinding = HolderTabButtonBinding.inflate(layoutInflater)
        val tabBtnModel = TabButtonViewModel()
        return when(position) {
            0 -> {
                tabBtnModel.emoji = getString(R.string.memo_emoji)
                tabBtnModel.title = getString(R.string.memo)
                tabBtnBinding.model = tabBtnModel
                tabBtnBinding.root
            }
            1 -> {
                tabBtnModel.emoji = getString(R.string.today_emoji)
                tabBtnModel.title = getString(R.string.today_schedule)
                tabBtnBinding.model = tabBtnModel
                tabBtnBinding.root
            }
            2 -> {
                tabBtnModel.emoji = getString(R.string.repeat_emoji)
                tabBtnModel.title = getString(R.string.repeat_schedule)
                tabBtnBinding.model = tabBtnModel
                tabBtnBinding.root
            }
            3 -> {
                tabBtnModel.emoji = getString(R.string.finish_emoji)
                tabBtnModel.title = getString(R.string.finish_schedule)
                tabBtnBinding.model = tabBtnModel
                tabBtnBinding.root
            }
            else -> {
                tabBtnBinding.root
            }
        }
    }
}