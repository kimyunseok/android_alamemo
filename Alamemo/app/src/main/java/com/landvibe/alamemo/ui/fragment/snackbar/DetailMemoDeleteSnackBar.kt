package com.landvibe.alamemo.ui.fragment.snackbar

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import com.landvibe.alamemo.R
import com.landvibe.alamemo.adapter.DetailMemoRecyclerViewAdapter
import com.landvibe.alamemo.common.AppDataBase
import com.landvibe.alamemo.handler.AlarmHandler
import com.landvibe.alamemo.handler.FixNotifyHandler
import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.ui.activity.MainActivity
import com.landvibe.alamemo.ui.fragment.main.DetailFragment
import com.landvibe.alamemo.ui.fragment.main.MainFragment

class DetailMemoDeleteSnackBar(context: Context, rootView: View, private val removedDetailMemo: DetailMemo) {

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
        AppDataBase.instance.detailMemoDao().insertDetailMemo(removedDetailMemo)

        //세부메모프래그먼트라면 onResume()호출.
        (context as MainActivity).supportFragmentManager.findFragmentById(R.id.main_container)?.let{
            if(it is DetailFragment) {
                it.onResume()
            }
        }

        val memoID = removedDetailMemo.memoId
        val tmpMemo = AppDataBase.instance.memoDao().getMemoById(memoID)

        if(tmpMemo.setAlarm.value == true) {
            //알람설정 돼 있었다면 알람재설정
            AlarmHandler().setMemoAlarm(context, tmpMemo)
        }
        if(tmpMemo.fixNotify.value == true) {
            //고성설정 돼 있었다면 고정재설정
            FixNotifyHandler().setMemoFixNotify(context, tmpMemo)
        }
    }
}
