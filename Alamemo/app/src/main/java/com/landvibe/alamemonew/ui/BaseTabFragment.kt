package com.landvibe.alamemonew.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.landvibe.alamemonew.adapter.MemoRecyclerViewAdapter
import com.landvibe.alamemonew.common.AppDataBase
import com.landvibe.alamemonew.databinding.TabFragmentBinding
import com.landvibe.alamemonew.model.uimodel.TabFragmentViewModel

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
        viewDataBinding.lifecycleOwner = this
        initViewModel()

        init()
        setRecyclerView()

        return viewDataBinding.root
    }

    private fun initViewModel() {
        val viewModel = TabFragmentViewModel()
        viewModel.memoOpen.value = true
        viewModel.memoEmpty.value = true
        viewDataBinding.model = viewModel
        setTitle() // viewModel설정 후 title 설정.
    }

    private fun setRecyclerView() {
        val itemList = AppDataBase.instance.memoDao().getMemoByType(type)
        if(itemList.isNotEmpty()) {
            viewDataBinding.model?.memoEmpty?.value = false
        }
        viewDataBinding.tabMemoRecycler.adapter = MemoRecyclerViewAdapter(requireContext(), itemList.toMutableList())
        viewDataBinding.tabMemoRecycler.layoutManager = LinearLayoutManager(context)
    }

}