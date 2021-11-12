package com.landvibe.alamemo.util

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/*
 스와이프 해서 데이터를 삭제하게 하는 클래스.
 https://www.tutorialsbuzz.com/2019/10/android-recyclerview-swipe-delete-ItemTouchHelper-kotlin.html
 사이트를 참조했음.
 */
abstract class SwipeAction : ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val swipeFlag = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(0, swipeFlag)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }
}