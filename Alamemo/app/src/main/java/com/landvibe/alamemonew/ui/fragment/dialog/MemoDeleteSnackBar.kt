package com.landvibe.alamemonew.ui.fragment.dialog

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.landvibe.alamemonew.R
import com.landvibe.alamemonew.adapter.MemoRecyclerViewAdapter
import com.landvibe.alamemonew.common.AppDataBase
import com.landvibe.alamemonew.model.data.detail.DetailMemo
import com.landvibe.alamemonew.model.data.memo.Memo
import com.landvibe.alamemonew.util.AlarmHandler
import com.landvibe.alamemonew.util.FixNotifyHandler

class MemoDeleteSnackBar(context: Context, rootView: View, private val recyclerViewAdapter: MemoRecyclerViewAdapter,
                         private val position: Int, private val removedMemo: Memo, private val removedDetailMemo: List<DetailMemo>) {

    private var snackBar: Snackbar =
        Snackbar.make(rootView, context.getString(R.string.delete_memo_snackbar_message), Snackbar.LENGTH_SHORT).apply {
            setAction(context.getString(R.string.delete_snackbar_cancel)) { undoDelete(context) }
            setTextColor(ContextCompat.getColor(context, R.color.white))
            setActionTextColor(ContextCompat.getColor(context, R.color.Red))
            setBackgroundTint(ContextCompat.getColor(context, R.color.black))
        }

    fun showSnackBar() {
        snackBar.show()
    }

    fun undoDelete(context: Context) {
        recyclerViewAdapter.itemList.add(position, removedMemo)
        AppDataBase.instance.memoDao().insertMemo(removedMemo)
        recyclerViewAdapter.notifyItemInserted(position)

        for(data in removedDetailMemo) {
            AppDataBase.instance.detailMemoDao().insertDetailMemo(data)
        }


        if(removedMemo.setAlarm.value == true) {
            //알람설정 돼 있었다면 다시 알람설정
            AlarmHandler().cancelAlarm(context, removedMemo.id.toInt())
        }

        if(removedMemo.fixNotify.value == true) {
            //고성설정 돼 있었다면 다시 고정설정
            FixNotifyHandler().cancelFixNotify(context, removedMemo.id.toInt())
        }
    }
}