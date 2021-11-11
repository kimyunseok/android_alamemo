package com.landvibe.alamemonew.ui.fragment.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.landvibe.alamemonew.R
import com.landvibe.alamemonew.adapter.DetailMemoRecyclerViewAdapter
import com.landvibe.alamemonew.common.AppDataBase
import com.landvibe.alamemonew.ui.activity.MainActivity

class DetailMemoDeleteDialog(context: Context, recyclerViewAdapter: DetailMemoRecyclerViewAdapter, position: Int): AlertDialog.Builder(context) {
    init {
        setTitle(context.getString(R.string.delete_didalog_title))
        setMessage(context.getString(R.string.delete_didalog_message))
        setIcon(R.drawable.iconfinder_warn)

        val dialogListener = DialogInterface.OnClickListener { _, button ->
            when(button) {
                DialogInterface.BUTTON_POSITIVE -> {
                    //TODO : 알람삭제
                    //TODO : 상단바 고정 삭제
                    val detailMemoID = recyclerViewAdapter.itemList[position].id

                    AppDataBase.instance.detailMemoDao().deleteDetailMemoByID(detailMemoID)
                    (context as MainActivity).supportFragmentManager.findFragmentById(R.id.main_container)?.onResume()
                }
                else -> {
                    //아니요 버튼, 뒤로가기 등
                }
            }
        }

        setPositiveButton(context.getString(R.string.yes), dialogListener)
        setNegativeButton(context.getString(R.string.no), dialogListener)
        (context as MainActivity).supportFragmentManager.findFragmentById(R.id.main_container)?.onResume()
    }
}