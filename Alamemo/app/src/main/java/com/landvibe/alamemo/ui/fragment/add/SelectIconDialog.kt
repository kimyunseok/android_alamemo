package com.landvibe.alamemo.ui.fragment.add

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.landvibe.alamemo.R
import com.landvibe.alamemo.adapter.IconSelectRecyclerViewAdapter
import com.landvibe.alamemo.adapter.IconTypeSelectRecyclerViewAdapter
import com.landvibe.alamemo.databinding.DialogSelectIconBinding
import com.landvibe.alamemo.viewmodel.aac.MemoAddOrEditViewModel
import com.landvibe.alamemo.viewmodel.ui.IconTypeViewModel

class SelectIconDialog(context: Context, val iconLiveData: MutableLiveData<String>): Dialog(context) {
    private val binding = DialogSelectIconBinding.inflate(layoutInflater)

    lateinit var iconSelectAdapter: IconSelectRecyclerViewAdapter
    private val iconTypeList = mutableListOf<IconTypeViewModel>()

    init {
        setContentView(binding.root)

        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes?.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }

        initIconTypeList()
        initIconRecyclerView()
        initIconTypeRecyclerView()
        setUpBtnListener()
    }

    private fun initIconTypeRecyclerView() {
        binding.selectIconTypeRecyclerView.adapter = IconTypeSelectRecyclerViewAdapter(context, iconTypeList, iconSelectAdapter)
        binding.selectIconTypeRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.selectIconTypeRecyclerView.itemAnimator = null
    }

    private fun initIconRecyclerView() {
        iconSelectAdapter = IconSelectRecyclerViewAdapter(context, iconTypeList[0].iconList, this, iconLiveData)
        binding.recyclerView.adapter = iconSelectAdapter
        binding.recyclerView.layoutManager = GridLayoutManager(context, 4)
    }

    private fun initIconTypeList() {
        iconTypeList.add(IconTypeViewModel("얼굴", true, context.getString(R.string.icon_face).split(' ').toMutableList()))
        iconTypeList.add(IconTypeViewModel("손짓", false,context.getString(R.string.icon_gesture).split(' ').toMutableList()))
        iconTypeList.add(IconTypeViewModel("사람", false, context.getString(R.string.icon_people).split(' ').toMutableList()))
        iconTypeList.add(IconTypeViewModel("옷/악세서리", false, context.getString(R.string.icon_clothing_acc).split(' ').toMutableList()))
        iconTypeList.add(IconTypeViewModel("동물/자연", false, context.getString(R.string.icon_animal_nature).split(' ').toMutableList()))
        iconTypeList.add(IconTypeViewModel("음식", false, context.getString(R.string.icon_food).split(' ').toMutableList()))
        iconTypeList.add(IconTypeViewModel("활동", false, context.getString(R.string.icon_activity).split(' ').toMutableList()))
        iconTypeList.add(IconTypeViewModel("탈 것/장소", false, context.getString(R.string.icon_travel_place).split(' ').toMutableList()))
        iconTypeList.add(IconTypeViewModel("사물", false, context.getString(R.string.icon_object).split(' ').toMutableList()))
        iconTypeList.add(IconTypeViewModel("상징", false, context.getString(R.string.icon_symbol).split(' ').toMutableList()))
    }

    private fun setUpBtnListener() {
        binding.cancelBtn.setOnClickListener { onBackPressed() }
    }
}