package com.landvibe.alamemonew.ui.fragment.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.landvibe.alamemonew.R
import com.landvibe.alamemonew.adapter.MemoRecyclerViewAdapter
import com.landvibe.alamemonew.common.AppDataBase
import com.landvibe.alamemonew.ui.activity.MainActivity

class MemoDeleteDialog(context: Context, recyclerViewAdapter: MemoRecyclerViewAdapter, position: Int): AlertDialog.Builder(context) {
    init {
        setTitle(context.getString(R.string.delete_didalog_title))
        setMessage(context.getString(R.string.delete_didalog_message))
        setIcon(R.drawable.iconfinder_warn)

        val dialogListener = DialogInterface.OnClickListener { _, button ->
            when(button) {
                DialogInterface.BUTTON_POSITIVE -> {
                    //TODO : 알람삭제
                    //TODO : 상단바 고정 삭제
                    //TODO : 세부일정도 다 삭제하기
                    val memoID = recyclerViewAdapter.itemList[position].id

                    AppDataBase.instance.detailMemoDao().deleteDetailMemoByMemoID(memoID)
                    AppDataBase.instance.memoDao().deleteMemoByID(memoID)
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    //아니요 버튼
                }
            }
            //버튼 누른 후 onResume
            (context as MainActivity).supportFragmentManager.findFragmentById(R.id.main_container)?.onResume()
        }

        setPositiveButton(context.getString(R.string.yes), dialogListener)
        setNegativeButton(context.getString(R.string.no), dialogListener)
    }
}