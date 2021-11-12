package com.landvibe.alamemo.util

import androidx.recyclerview.widget.DiffUtil
import com.landvibe.alamemo.model.data.memo.Memo

class MemoDiffUtil(val oldList: MutableList<Memo>, val newList: MutableList<Memo>): DiffUtil.Callback() {
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