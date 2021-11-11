package com.landvibe.alamemonew.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.landvibe.alamemonew.adapter.MemoRecyclerViewAdapter
import com.landvibe.alamemonew.common.AppDataBase
import com.landvibe.alamemonew.databinding.TabFragmentBinding
import com.landvibe.alamemonew.model.data.memo.Memo
import com.landvibe.alamemonew.model.uimodel.TabFragmentViewModel
import java.util.*

abstract class BaseTabFragment<T: TabFragmentBinding>() : Fragment() {
    lateinit var viewDataBinding: T
    abstract val layoutId: Int
    abstract val type: Int

    abstract fun setTitle()
    abstract fun init()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        initViewModel()

        init()
        setRecyclerView()

        return viewDataBinding.root
    }

    override fun onResume() {
        if(this::viewDataBinding.isInitialized) {
            setRecyclerView()
        }
        super.onResume()
    }

    private fun initViewModel() {
        val viewModel = TabFragmentViewModel()
        viewModel.memoEmpty.value = true
        viewDataBinding.model = viewModel
        setTitle() // viewModel설정 후 title 설정.
    }

    private fun setRecyclerView() {
        val itemList = AppDataBase.instance.memoDao().getMemoByType(type).toMutableList()
        itemList.sortWith(compareBy<Memo> {it.scheduleDateYear.value}
            .thenBy { it.scheduleDateMonth.value }
            .thenBy { it.scheduleDateDay.value }
            .thenBy { it.scheduleDateHour.value }
            .thenBy { it.scheduleDateMinute.value }
            .thenBy { it.alarmStartTimeHour.value }
            .thenBy { it.alarmStartTimeMinute.value }
        )

        if(type == 2) {
            val today = System.currentTimeMillis()
            for(idx in 0 until itemList.size) {
                val calendar = Calendar.getInstance()
            }
        }

        if(itemList.isNotEmpty()) {
            viewDataBinding.model?.memoEmpty?.value = false
        }
        viewDataBinding.tabMemoRecycler.adapter = MemoRecyclerViewAdapter(requireContext(), itemList)
        viewDataBinding.tabMemoRecycler.layoutManager = LinearLayoutManager(context)
    }

}