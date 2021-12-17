package com.landvibe.alamemo.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.landvibe.alamemo.R
import com.landvibe.alamemo.model.database.AppDataBase
import com.landvibe.alamemo.databinding.HolderLongClickMenuBinding
import com.landvibe.alamemo.handler.AlarmHandler
import com.landvibe.alamemo.handler.FixNotifyHandler
import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.viewmodel.ui.LongClickViewModel
import com.landvibe.alamemo.ui.activity.MainActivity
import com.landvibe.alamemo.ui.fragment.add.DetailAddOrEditFragment
import com.landvibe.alamemo.ui.fragment.main.DetailFragment
import com.landvibe.alamemo.ui.snackbar.DetailMemoDeleteSnackBar
import com.landvibe.alamemo.viewmodel.aac.MemoListUpdateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailMemoLongClickRecyclerViewAdapter (val context: Context,
                                              val dialog: BottomSheetDialogFragment,
                                              val detailMemo: DetailMemo,
                                              val memoListUpdateViewModel: MemoListUpdateViewModel):
    RecyclerView.Adapter<DetailMemoLongClickRecyclerViewAdapter.Holder>() {

    private val itemList = mutableListOf(
        LongClickViewModel(context.getString(R.string.memo_menu_copy), 1),
        LongClickViewModel(context.getString(R.string.memo_menu_modify), 2),
        LongClickViewModel(context.getString(R.string.memo_menu_remove), 3),
        LongClickViewModel(context.getString(R.string.memo_menu_close), 4)
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = HolderLongClickMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val icon = itemList[position]
        holder.bind(icon)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class Holder(var binding: HolderLongClickMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LongClickViewModel) {
            binding.model = item

            if(item.type == 3) {
                binding.title.setTextColor(ContextCompat.getColor(context, R.color.Red))
            }

            itemView.setOnClickListener {
                dialog.dismiss()
                when (item.type) {
                    1 -> {
                        //메모/일정 복사
                        copyMemo()
                    }
                    2 -> {
                        //메모/일정 수정
                        modifyMemoBtn()
                    }
                    3 -> {
                        //메모/일정 삭제
                        removeMemoBtn()
                    }
                    4 -> {
                        //닫기 버튼
                    }
                }
            }
        }
    }

    private fun copyMemo() {
        val clipBoard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(context.getString(R.string.app_name), detailMemo.icon + " " + detailMemo.title)
        clipBoard.setPrimaryClip(clipData)
        Toast.makeText(context, context.getString(R.string.memo_copy_complete), Toast.LENGTH_SHORT).show()
    }

    private fun modifyMemoBtn() {
        val bundle = Bundle().apply { putLong("detailMemoId", detailMemo.id) }

        (context as MainActivity).supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_left)
            .replace(R.id.main_container, DetailAddOrEditFragment().apply { arguments = bundle })
            .addToBackStack(null)
            .commit()
    }

    private fun removeMemoBtn() {

        val memoID = detailMemo.memoId
        val detailMemoID = detailMemo.id

        AppDataBase.instance.detailMemoDao().deleteDetailMemoByID(detailMemoID)

        //알람 업데이트
        val tmpMemo = AppDataBase.instance.memoDao().getMemoById(memoID)
        if(tmpMemo.setAlarm) {
            //알람설정 돼 있었다면 알람재설정
            AlarmHandler().setMemoAlarm(context, tmpMemo.id)
        }
        if(tmpMemo.fixNotify) {
            //고성설정 돼 있었다면 고정재설정
            FixNotifyHandler().setMemoFixNotify(context, tmpMemo.id)
        }

        memoListUpdateViewModel.getRecentDetailMemoList(memoID)

        (context as MainActivity).supportFragmentManager.findFragmentById(R.id.main_container)?.let {
            if(it is DetailFragment) {
                it.onResume()
                DetailMemoDeleteSnackBar(context, it.viewDataBinding.root, detailMemo, memoListUpdateViewModel).showSnackBar()
            }
        }

    }
}