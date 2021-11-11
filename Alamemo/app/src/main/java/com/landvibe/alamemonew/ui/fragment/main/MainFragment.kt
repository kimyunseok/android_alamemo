package com.landvibe.alamemonew.ui.fragment.main

import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.tabs.TabLayoutMediator
import com.landvibe.alamemonew.R
import com.landvibe.alamemonew.adapter.FragmentPageAdapter
import com.landvibe.alamemonew.databinding.FragmentMainBinding
import com.landvibe.alamemonew.databinding.TabButtonBinding
import com.landvibe.alamemonew.model.data.memo.Memo
import com.landvibe.alamemonew.model.uimodel.TabButtonViewModel
import com.landvibe.alamemonew.ui.BaseFragment
import com.landvibe.alamemonew.ui.fragment.add.MemoAddOrEditFragment

class MainFragment: BaseFragment<FragmentMainBinding>() {
    override val layoutId: Int = R.layout.fragment_main

    private val memoFragment = MemoFragment()
    private val scheduleFragment = ScheduleFragment()
    private val repeatFragment = RepeatFragment()
    private val finishFragment = FinishFragment()

    override fun init() {
        setUpBtnOnClickListener()
        initViewPager()
        initTabLayout()
    }

    override fun onResume() {
        super.onResume()
        memoFragment.onResume()
        scheduleFragment.onResume()
        repeatFragment.onResume()
        finishFragment.onResume()
    }

    override fun onPause() {
        super.onPause()
        memoFragment.onPause()
        scheduleFragment.onPause()
        repeatFragment.onPause()
        finishFragment.onPause()
    }

    private fun setUpBtnOnClickListener() {
        viewDataBinding.mainAddMemoButton.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_left)
                .replace(R.id.main_container, MemoAddOrEditFragment())
                .addToBackStack(null)
                .commit()

            onStop()
        }

        viewDataBinding.mainSettingButton.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.sry_making), Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViewPager() {
        val pagerAdapter = FragmentPageAdapter(requireActivity())
        pagerAdapter.apply {
            addFragment(memoFragment)
            addFragment(scheduleFragment)
            addFragment(repeatFragment)
            addFragment(finishFragment)
        }

        viewDataBinding.mainViewPager.adapter = pagerAdapter

        viewDataBinding.mainViewPager.isUserInputEnabled = false // 스와이프 막기
    }

    private fun initTabLayout() {
        //viewDataBinding.mainTabLayout.setupWithViewPager(viewDataBinding.mainViewPager) ViewPager2에서는 사용불가.
        TabLayoutMediator(viewDataBinding.mainTabLayout, viewDataBinding.mainViewPager) {
                tab, position -> tab.customView = createTabView(position)
        }.attach()
    }

    private fun createTabView(position: Int): View {
        val tabBtnBinding = TabButtonBinding.inflate(layoutInflater)
        var tabBtnModel = TabButtonViewModel()
        return when(position) {
            0 -> {
                tabBtnModel.emoji.value = getString(R.string.memo_emoji)
                tabBtnModel.title.value = getString(R.string.memo)
                tabBtnBinding.model = tabBtnModel
                tabBtnBinding.root
            }
            1 -> {
                tabBtnModel.emoji.value = getString(R.string.today_emoji)
                tabBtnModel.title.value = getString(R.string.today_schedule)
                tabBtnBinding.model = tabBtnModel
                tabBtnBinding.root
            }
            2 -> {
                tabBtnModel.emoji.value = getString(R.string.repeat_emoji)
                tabBtnModel.title.value = getString(R.string.repeat_schedule)
                tabBtnBinding.model = tabBtnModel
                tabBtnBinding.root
            }
            3 -> {
                tabBtnModel.emoji.value = getString(R.string.finish_emoji)
                tabBtnModel.title.value = getString(R.string.finish_schedule)
                tabBtnBinding.model = tabBtnModel
                tabBtnBinding.root
            }
            else -> {
                tabBtnBinding.root
            }
        }
    }
}