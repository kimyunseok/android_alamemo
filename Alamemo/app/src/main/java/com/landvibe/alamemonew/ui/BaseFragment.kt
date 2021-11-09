package com.landvibe.alamemonew.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.landvibe.alamemonew.R
import com.landvibe.alamemonew.model.TabFragmentViewModel

abstract class BaseFragment<T: ViewDataBinding>: Fragment() {
    lateinit var viewDataBinding: T
    abstract val layoutId: Int

    abstract fun init()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        viewDataBinding.lifecycleOwner = this

        init()
        return viewDataBinding.root
    }

}