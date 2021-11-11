package com.landvibe.alamemonew.util

import androidx.recyclerview.widget.DiffUtil
import com.landvibe.alamemonew.model.data.detail.DetailMemo

class DetailMemoDiffUtil(val oldList: MutableList<DetailMemo>, val newList: MutableList<DetailMemo>): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}