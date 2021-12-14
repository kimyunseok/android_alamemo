package com.landvibe.alamemo.ui.fragment.snackbar

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.landvibe.alamemo.R
import com.landvibe.alamemo.handler.AlarmHandler
import com.landvibe.alamemo.handler.FixNotifyHandler
import com.landvibe.alamemo.viewmodel.aac.DetailFragmentViewModel

class DetailMemoDeleteSnackBar(context: Context, rootView: View, private val viewModel: DetailFragmentViewModel) {

    private var snackBar: Snackbar =
        Snackbar.make(rootView, context.getString(R.string.delete_detail_memo_snackbar_message), Snackbar.LENGTH_SHORT).apply {
            setAction(context.getString(R.string.delete_snackbar_cancel)) { undoDelete(context) }
            setTextColor(ContextCompat.getColor(context, R.color.white))
            setActionTextColor(ContextCompat.getColor(context, R.color.Red))
            setBackgroundTint(ContextCompat.getColor(context, R.color.black))
        }

    fun showSnackBar() {
        snackBar.show()
    }

    private fun undoDelete(context: Context) {
        viewModel.savedDetailMemo?.let {
            viewModel.insertDetailMemo(it)
        }

        viewModel.savedMemoForAlarmSetting?.let {
            if(it.setAlarm) {
                //알람설정 돼 있었다면 알람재설정
                AlarmHandler().setMemoAlarm(context, it)
            }
            if(it.fixNotify) {
                //고성설정 돼 있었다면 고정재설정
                FixNotifyHandler().setMemoFixNotify(context, it)
            }
        }
    }
}
