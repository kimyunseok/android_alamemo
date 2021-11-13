package com.landvibe.alamemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.landvibe.alamemo.databinding.HolderHelperBinding
import com.landvibe.alamemo.model.uimodel.HelperViewModel
import com.landvibe.alamemo.ui.fragment.helper.HelperDialog

class HelperRecyclerViewAdapter(val context: Context, var itemList: MutableList<HelperViewModel>, ): RecyclerView.Adapter<HelperRecyclerViewAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = HolderHelperBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val icon = itemList[position]
        holder.bind(icon)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class Holder(var binding: HolderHelperBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(helper: HelperViewModel) {
            binding.model = helper

            itemView.setOnClickListener {
                HelperDialog(context, helper.type).show()
            }
        }
    }

}