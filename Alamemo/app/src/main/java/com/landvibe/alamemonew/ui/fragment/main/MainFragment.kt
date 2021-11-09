package com.landvibe.alamemonew.ui.fragment.main

import android.view.View
import com.google.android.material.tabs.TabLayoutMediator
import com.landvibe.alamemonew.R
import com.landvibe.alamemonew.adapter.FragmentPageAdapter
import com.landvibe.alamemonew.databinding.FragmentMainBinding
import com.landvibe.alamemonew.databinding.TabButtonBinding
import com.landvibe.alamemonew.model.TabButtonViewModel
import com.landvibe.alamemonew.ui.BaseFragment

class MainFragment: BaseFragment<FragmentMainBinding>() {
    override val layoutId: Int = R.layout.fragment_main

    override fun init() {
        setUpBtnOnClickListener()

        initViewPager()
        initTabLayout()
    }


    private fun setUpBtnOnClickListener() {
        viewDataBinding.mainAddMemoButton.setOnClickListener {
            //startActivity(Intent(this, AddMemoActivity::class.java))
        }

        viewDataBinding.mainHelperButton.setOnClickListener {
            //startActivity(Intent(this, HelperActivity::class.java))
        }
    }

    private fun initViewPager() {
        val pagerAdapter = FragmentPageAdapter(requireActivity())
        pagerAdapter.apply {
            addFragment(MemoFragment())
            addFragment(TodayFragment())
            addFragment(FutureFragment())
            addFragment(RepeatFragment())
        }
        viewDataBinding.mainViewPager.adapter = pagerAdapter
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
                tabBtnModel.emoji.value = getString(R.string.future_emoji)
                tabBtnModel.title.value = getString(R.string.future_schedule)
                tabBtnBinding.model = tabBtnModel
                tabBtnBinding.root
            }
            3 -> {
                tabBtnModel.emoji.value = getString(R.string.repeat_emoji)
                tabBtnModel.title.value = getString(R.string.repeat_schedule)
                tabBtnBinding.model = tabBtnModel
                tabBtnBinding.root
            }
            else -> {
                tabBtnBinding.root
            }
        }
    }
}