package com.landvibe.alamemo.ui.fragment.snackbar

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.landvibe.alamemo.R
import com.landvibe.alamemo.adapter.DetailMemoRecyclerViewAdapter
import com.landvibe.alamemo.common.AppDataBase
import com.landvibe.alamemo.model.data.detail.DetailMemo

class DetailMemoDeleteSnackBar(context: Context, rootView: View, private val recyclerViewAdapter: DetailMemoRecyclerViewAdapter,
                               private val position: Int, private val removedDetailMemo: DetailMemo) {

    private var snackBar: Snackbar =
        Snackbar.make(rootView, context.getString(R.string.delete_detail_memo_snackbar_message), Snackbar.LENGTH_SHORT).apply {
            setAction(context.getString(R.string.delete_snackbar_cancel)) { undoDelete() }
            setTextColor(ContextCompat.getColor(context, R.color.white))
            setActionTextColor(ContextCompat.getColor(context, R.color.Red))
            setBackgroundTint(ContextCompat.getColor(context, R.color.black))
        }

    fun showSnackBar() {
        snackBar.show()
    }

    fun undoDelete() {
        recyclerViewAdapter.itemList.add(position, removedDetailMemo)
        AppDataBase.instance.detailMemoDao().insertDetailMemo(removedDetailMemo)
        recyclerViewAdapter.notifyItemInserted(position)
    }
}
