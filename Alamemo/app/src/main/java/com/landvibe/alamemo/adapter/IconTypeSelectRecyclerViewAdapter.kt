package com.landvibe.alamemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.landvibe.alamemo.databinding.HolderIconTypeBinding
import com.landvibe.alamemo.viewmodel.ui.IconTypeViewModel
import com.landvibe.alamemo.util.IconTypeDiffUtil

class IconTypeSelectRecyclerViewAdapter(context: Context, val itemList: MutableList<IconTypeViewModel>,
                                        val iconSelectRecyclerViewAdapter: IconSelectRecyclerViewAdapter): RecyclerView.Adapter<IconTypeSelectRecyclerViewAdapter.Holder>() {

    private var lastSelectIdx = 0 // 최초에는 0번째 선택.

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = HolderIconTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val icon = itemList[position]
        holder.bind(icon, position)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class Holder(var binding: HolderIconTypeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(type: IconTypeViewModel, position: Int) {
            binding.model = type

            itemView.setOnClickListener {
                selectType(position)
            }
        }
    }

    private fun selectType(position: Int) {
        itemList[lastSelectIdx].isSelected = false
        itemList[position].isSelected = true
        notifyItemChanged(lastSelectIdx)
        notifyItemChanged(position)
        lastSelectIdx = position

        val oldItemList = iconSelectRecyclerViewAdapter.itemList
        val newItemList = itemList[position].iconList

        val diffUtil = DiffUtil.calculateDiff(IconTypeDiffUtil(oldItemList, newItemList), false)
        diffUtil.dispatchUpdatesTo(iconSelectRecyclerViewAdapter)
        iconSelectRecyclerViewAdapter.itemList = newItemList
    }

}