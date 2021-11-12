package com.landvibe.alamemo.ui.fragment.add

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.landvibe.alamemo.adapter.IconSelectRecyclerViewAdapter
import com.landvibe.alamemo.databinding.DialogSelectIconBinding

class SelectIconDialog(context: Context, icon: MutableLiveData<String>): Dialog(context) {
    init {
        val binding = DialogSelectIconBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.attributes?.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT

        }

        val iconList = mutableListOf("☺", "😥", "🥩", "🍳", "☕", "⚽",
            "⚾", "🏀", "🏊", "🎼", "🎧", "🎤", "🚗", "✈", "🚌", "🚈", "🚢",
            "✏", "📝", "🏫")

        binding.recyclerView.adapter = IconSelectRecyclerViewAdapter(context, iconList, this, icon)
        binding.recyclerView.layoutManager = GridLayoutManager(context, 4)
    }
}