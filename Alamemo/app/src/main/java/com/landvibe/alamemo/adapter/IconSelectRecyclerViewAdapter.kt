package com.landvibe.alamemo.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.landvibe.alamemo.databinding.HolderIconBinding
import com.landvibe.alamemo.viewmodel.aac.MemoAddOrEditViewModel

class IconSelectRecyclerViewAdapter(context: Context, var itemList: MutableList<String>,
                                    val dialog: Dialog, val iconLiveData: MutableLiveData<String>
): RecyclerView.Adapter<IconSelectRecyclerViewAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = HolderIconBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val icon = itemList[position]
        holder.bind(icon)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class Holder(var binding: HolderIconBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(_icon: String) {
            binding.icon = _icon

            itemView.setOnClickListener {
                iconLiveData.postValue(_icon)
                dialog.dismiss()
            }
        }
    }

}