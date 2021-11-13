package com.landvibe.alamemo.ui.fragment.snackbar

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import com.landvibe.alamemo.R
import com.landvibe.alamemo.adapter.MemoRecyclerViewAdapter
import com.landvibe.alamemo.common.AppDataBase
import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.handler.AlarmHandler
import com.landvibe.alamemo.handler.FixNotifyHandler

class MemoDeleteSnackBar(context: Context, rootView: View, private val recyclerViewAdapter: MemoRecyclerViewAdapter,
                         private val position: Int, private val removedMemo: Memo, private val removedDetailMemo: List<DetailMemo>,
                         private val memoEmpty: MutableLiveData<Boolean>
) {

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
        memoEmpty.value = false

        for(data in removedDetailMemo) {
            AppDataBase.instance.detailMemoDao().insertDetailMemo(data)
        }


        if(removedMemo.setAlarm.value == true) {
            //알람설정 돼 있었다면 다시 알람설정
            AlarmHandler().setMemoAlarm(context, removedMemo)
        }

        if(removedMemo.fixNotify.value == true) {
            //고성설정 돼 있었다면 다시 고정설정
            FixNotifyHandler().setMemoFixNotify(context, removedMemo)
        }
    }
}