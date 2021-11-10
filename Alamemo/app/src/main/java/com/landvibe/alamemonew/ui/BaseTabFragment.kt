package com.landvibe.alamemonew.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.landvibe.alamemonew.databinding.TabFragmentBinding
import com.landvibe.alamemonew.model.uimodel.TabFragmentViewModel

abstract class BaseTabFragment<T: TabFragmentBinding>() : Fragment() {
    lateinit var viewDataBinding: T
    abstract val layoutId: Int

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

        return viewDataBinding.root
    }

    private fun initViewModel() {
        val viewModel = TabFragmentViewModel()
        viewModel.memoOpen.value = true
        viewModel.memoEmpty.value = true
        viewDataBinding.model = viewModel
        setTitle() // viewModel설정 후 title 설정.
    }

}