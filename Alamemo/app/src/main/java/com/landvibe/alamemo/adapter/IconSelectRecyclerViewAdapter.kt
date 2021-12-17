package com.landvibe.alamemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.landvibe.alamemo.databinding.HolderIconBinding

class IconSelectRecyclerViewAdapter(var itemList: MutableList<String>,
                                    val dialog: DialogFragment, val iconLiveData: MutableLiveData<String>
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