package com.landvibe.alamemo.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.landvibe.alamemo.R
import com.landvibe.alamemo.databinding.HolderDetailBinding
import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.ui.activity.MainActivity
import com.landvibe.alamemo.ui.dialog.DetailMemoClickDialog
import com.landvibe.alamemo.ui.fragment.add.DetailAddOrEditFragment
import com.landvibe.alamemo.viewmodel.aac.MemoListUpdateViewModel
import com.landvibe.alamemo.viewmodel.ui.DetailMemoHolderViewModel

class DetailMemoRecyclerViewAdapter(val context: Context, var itemList: MutableList<DetailMemo>, val memoListUpdateViewModel: MemoListUpdateViewModel):
    RecyclerView.Adapter<DetailMemoRecyclerViewAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = HolderDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class Holder(var binding: HolderDetailBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DetailMemo) {
            binding.viewModel = DetailMemoHolderViewModel(item)

            itemView.setOnClickListener {
                DetailMemoClickDialog(memoListUpdateViewModel).apply {
                    arguments = Bundle().apply { putLong("detailMemoId", item.id) }
                }.show((context as MainActivity).supportFragmentManager, "click")
                true
            }

            binding.detailEditBtn.setOnClickListener {
                val bundle = Bundle().apply { putLong("detailMemoId", item.id) }

                (context as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_left)
                    .replace(R.id.main_container, DetailAddOrEditFragment().apply { arguments = bundle })
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

}