package com.landvibe.alamemo.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.landvibe.alamemo.R
import com.landvibe.alamemo.adapter.IconSelectRecyclerViewAdapter
import com.landvibe.alamemo.adapter.IconTypeSelectRecyclerViewAdapter
import com.landvibe.alamemo.databinding.DialogSelectIconBinding
import com.landvibe.alamemo.viewmodel.ui.IconTypeViewModel

class SelectIconDialog(val iconLiveData: MutableLiveData<String>): DialogFragment() {
    lateinit var binding: DialogSelectIconBinding

    lateinit var iconSelectAdapter: IconSelectRecyclerViewAdapter
    private val iconTypeList = mutableListOf<IconTypeViewModel>()

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout((resources.displayMetrics.widthPixels * 0.9).toInt(), (resources.displayMetrics.heightPixels * 0.9).toInt())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogSelectIconBinding.inflate(inflater, container, false)

        initIconTypeList()
        initIconRecyclerView()
        initIconTypeRecyclerView()
        setUpBtnListener()
        return binding.root
    }

    private fun initIconTypeRecyclerView() {
        binding.selectIconTypeRecyclerView.adapter = IconTypeSelectRecyclerViewAdapter(requireContext(), iconTypeList, iconSelectAdapter)
        binding.selectIconTypeRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.selectIconTypeRecyclerView.itemAnimator = null
    }

    private fun initIconRecyclerView() {
        iconSelectAdapter = IconSelectRecyclerViewAdapter(iconTypeList[0].iconList, this, iconLiveData)
        binding.recyclerView.adapter = iconSelectAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(context, 4)
    }

    private fun initIconTypeList() {
        iconTypeList.add(IconTypeViewModel("얼굴", true, requireContext().getString(R.string.icon_face).split(' ').toMutableList()))
        iconTypeList.add(IconTypeViewModel("손짓", false, requireContext().getString(R.string.icon_gesture).split(' ').toMutableList()))
        iconTypeList.add(IconTypeViewModel("사람", false, requireContext().getString(R.string.icon_people).split(' ').toMutableList()))
        iconTypeList.add(IconTypeViewModel("옷/악세서리", false, requireContext().getString(R.string.icon_clothing_acc).split(' ').toMutableList()))
        iconTypeList.add(IconTypeViewModel("동물/자연", false, requireContext().getString(R.string.icon_animal_nature).split(' ').toMutableList()))
        iconTypeList.add(IconTypeViewModel("음식", false, requireContext().getString(R.string.icon_food).split(' ').toMutableList()))
        iconTypeList.add(IconTypeViewModel("활동", false, requireContext().getString(R.string.icon_activity).split(' ').toMutableList()))
        iconTypeList.add(IconTypeViewModel("탈 것/장소", false, requireContext().getString(R.string.icon_travel_place).split(' ').toMutableList()))
        iconTypeList.add(IconTypeViewModel("사물", false, requireContext().getString(R.string.icon_object).split(' ').toMutableList()))
        iconTypeList.add(IconTypeViewModel("상징", false, requireContext().getString(R.string.icon_symbol).split(' ').toMutableList()))
    }

    private fun setUpBtnListener() {
        binding.cancelBtn.setOnClickListener { dismiss() }
    }
}