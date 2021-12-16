package com.landvibe.alamemo.ui.snackbar

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.landvibe.alamemo.R
import com.landvibe.alamemo.handler.AlarmHandler
import com.landvibe.alamemo.handler.FixNotifyHandler
import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.model.database.AppDataBase
import com.landvibe.alamemo.viewmodel.aac.MemoListUpdateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemoDeleteSnackBar(context: Context, rootView: View, val memo: Memo?, val detailMemoList: MutableList<DetailMemo>?,
                         val memoListUpdateViewModel: MemoListUpdateViewModel) {

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

    private fun undoDelete(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            memo?.let {
                AppDataBase.instance.memoDao().insertMemo(it)

                if(it.setAlarm) {
                    //알람설정 돼 있었다면 다시 알람설정
                    AlarmHandler().setMemoAlarm(context, it.id)
                }

                if(it.fixNotify) {
                    //고성설정 돼 있었다면 다시 고정설정
                    FixNotifyHandler().setMemoFixNotify(context, it.id)
                }
                memoListUpdateViewModel.getRecentMemoList(it.type)
            }

            detailMemoList?.let {
                for(data in it) {
                    AppDataBase.instance.detailMemoDao().insertDetailMemo(data)
                }
            }
        }
    }
}