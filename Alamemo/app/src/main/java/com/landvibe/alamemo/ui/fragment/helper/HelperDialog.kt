package com.landvibe.alamemo.ui.fragment.helper

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.landvibe.alamemo.databinding.DialogHelperBinding
import com.landvibe.alamemo.viewmodel.aac.HelperDialogViewModel
import com.landvibe.alamemo.viewmodel.viewmodelfactory.HelperDialogViewModelFactory

/**
 * Type에 따라서 보여주는 목록이 다름.
 * */
class HelperDialog: DialogFragment() {
    lateinit var binding: DialogHelperBinding

    private val viewModel: HelperDialogViewModel by lazy {
        ViewModelProvider(this, HelperDialogViewModelFactory(type)).get(HelperDialogViewModel::class.java).apply { initViewModel() }
    }

    private val type: Int by lazy {
        arguments?.getInt("type") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogHelperBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.model = viewModel
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        initView()

        return binding.root
    }

    private fun initView() {
        setUpBtnListener()
    }

    private fun setUpBtnListener() {
        binding.cancelBtn.setOnClickListener { dismiss() }

        binding.prevBtn.setOnClickListener {
            viewModel.getPrevData()
        }

        binding.nextBtn.setOnClickListener {
            viewModel.getNextData()
        }
    }

}
