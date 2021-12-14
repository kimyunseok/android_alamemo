package com.landvibe.alamemo.ui.fragment.main

import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.landvibe.alamemo.R
import com.landvibe.alamemo.adapter.FragmentPageAdapter
import com.landvibe.alamemo.databinding.FragmentMainBinding
import com.landvibe.alamemo.databinding.HolderTabButtonBinding
import com.landvibe.alamemo.model.uimodel.TabButtonViewModel
import com.landvibe.alamemo.ui.BaseFragment
import com.landvibe.alamemo.ui.fragment.add.MemoAddOrEditFragment
import com.landvibe.alamemo.ui.fragment.helper.HelperFragment

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
            addFragment(memoFragment)
            addFragment(scheduleFragment)
            addFragment(repeatFragment)
            addFragment(finishFragment)
        }

        viewDataBinding.mainViewPager.adapter = pagerAdapter
        viewDataBinding.mainViewPager.offscreenPageLimit = 2
        viewDataBinding.mainViewPager.isUserInputEnabled = false // 스와이프 막기
    }

    private fun initTabLayout() {
        //viewDataBinding.mainTabLayout.setupWithViewPager(viewDataBinding.mainViewPager) ViewPager2에서는 사용불가.
        TabLayoutMediator(viewDataBinding.mainTabLayout, viewDataBinding.mainViewPager) {
                tab, position -> tab.customView = createTabView(position)
        }.attach()
    }

    private fun createTabView(position: Int): View {
        val tabBtnBinding = HolderTabButtonBinding.inflate(layoutInflater)
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