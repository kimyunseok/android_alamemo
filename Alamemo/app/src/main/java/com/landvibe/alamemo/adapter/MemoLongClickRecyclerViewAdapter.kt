package com.landvibe.alamemo.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.landvibe.alamemo.R
import com.landvibe.alamemo.databinding.HolderLongClickMenuBinding
import com.landvibe.alamemo.handler.AlarmHandler
import com.landvibe.alamemo.handler.FixNotifyHandler
import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.model.database.AppDataBase
import com.landvibe.alamemo.ui.activity.MainActivity
import com.landvibe.alamemo.ui.fragment.add.MemoAddOrEditFragment
import com.landvibe.alamemo.ui.fragment.main.MainFragment
import com.landvibe.alamemo.ui.snackbar.MemoDeleteSnackBar
import com.landvibe.alamemo.util.MemoUtil
import com.landvibe.alamemo.viewmodel.aac.MemoListUpdateViewModel
import com.landvibe.alamemo.viewmodel.ui.LongClickViewModel

class MemoLongClickRecyclerViewAdapter (val context: Context,
                                        val dialog: BottomSheetDialogFragment,
                                        val memo: Memo,
                                        val memoListUpdateViewModel: MemoListUpdateViewModel):
    RecyclerView.Adapter<MemoLongClickRecyclerViewAdapter.Holder>() {

    private val itemList = mutableListOf(
        LongClickViewModel(context.getString(R.string.memo_menu_share), 1),
        LongClickViewModel(context.getString(R.string.memo_menu_copy), 2),
        LongClickViewModel(context.getString(R.string.memo_menu_finish), 3),
        LongClickViewModel(context.getString(R.string.memo_menu_modify), 4),
        LongClickViewModel(context.getString(R.string.memo_menu_remove), 5),
        LongClickViewModel(context.getString(R.string.memo_menu_close), 6),
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

            if(item.type == 5) {
                binding.title.setTextColor(ContextCompat.getColor(context, R.color.Red))
            }

            itemView.setOnClickListener {
                dialog.dismiss()
                when (item.type) {
                    1 -> {
                        shearMessageBtn()
                    }
                    2 -> {
                        //메모/일정 복사
                        copyMemo()
                    }
                    3 -> {
                        //메모 일정 종료
                        finishMemoBtn()
                    }
                    4 -> {
                        //메모/일정 수정
                        modifyMemoBtn()
                    }
                    5 -> {
                        //메모/일정 삭제
                        removeMemoBtn()

                    }
                    6 -> {
                        //닫기 버튼
                    }
                }

            }
        }
    }

    private fun shearMessageBtn() {
        //메시지, 메신저로 메모/일정 공유하기
        val detailMemoList = AppDataBase.instance.detailMemoDao().getDetailMemoByMemoId(memo.id).toMutableList()

        //상단 바 고정 시 날짜를 표기해주는 용도. 메모는 표기하지 않는다.
        var contentText = when {
            memo.type == 1 -> {
                memo.icon + " " + memo.title
            }
            memo.type != 3 -> {
                memo.icon + " " + memo.title + "\n" +
                        MemoUtil().getScheduleDateFormat(memo.scheduleDateYear, memo.scheduleDateMonth, memo.scheduleDateDay) +
                        " " + MemoUtil().getTimeFormat(memo.scheduleDateHour, memo.scheduleDateMinute)
            }
            else -> {
                memo.icon + " " + memo.title + "\n" +
                        MemoUtil().getRepeatScheduleDateFormat(memo.repeatDay) +
                        " " + MemoUtil().getTimeFormat(memo.scheduleDateHour, memo.scheduleDateMinute)
            }
        }

        MemoUtil().sortDetailMemoList(detailMemoList)

        if(memo.type != 2 && detailMemoList.isEmpty().not()) {
            contentText += "\n"
            //메모, 반복일정의 경우에는 시간표시가 안되므로 '-'로 구분지어줘야 한다.
            contentText += context.getString(
                R.string.notification_fix_notify_slash) +
                    detailMemoList.joinToString(context.getString(R.string.notification_fix_notify_slash_include_line_enter)) { it.icon + " " + it.title }

        } else if(detailMemoList.isEmpty().not()){
            contentText += "\n"
            contentText +=
                detailMemoList.joinToString("\n") { MemoUtil().getScheduleDateFormat(it.scheduleDateYear, it.scheduleDateMonth, it.scheduleDateDay) +
                        " " + MemoUtil().getTimeFormat(it.scheduleDateHour, it.scheduleDateMinute) + " - "+
                        it.icon + " " + it.title }
        }


        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, contentText)
        }
        context.startActivity(intent)
    }

    private fun copyMemo() {
        val clipBoard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(context.getString(R.string.app_name), memo.icon + " " + memo.title)
        clipBoard.setPrimaryClip(clipData)
        Toast.makeText(context, context.getString(R.string.memo_copy_complete), Toast.LENGTH_SHORT).show()
    }

    private fun finishMemoBtn() {
        Toast.makeText(context, context.getString(R.string.memo_complete), Toast.LENGTH_SHORT).show()
        //메모/일정 완료
        AppDataBase.instance.memoDao().setMemoFinish(memo.id)

        if(memo.setAlarm) {
            //알람설정 돼 있었다면 알람해제.
            AlarmHandler().cancelAlarm(context, memo.id)
        }
        if(memo.fixNotify) {
            //고성설정 돼 있었다면 알람해제.
            FixNotifyHandler().cancelFixNotify(context, memo.id)
        }

        memoListUpdateViewModel.getRecentMemoList(memo.type) // 해당 타입 메모 리스트 다시 받아옴
        memoListUpdateViewModel.getRecentMemoList(4) // 종료 시킨 것도 다시 받아옴
    }

    private fun modifyMemoBtn() {
        val bundle = Bundle().apply { putLong("memoId", memo.id) }

        (context as MainActivity).supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_left)
            .replace(R.id.main_container, MemoAddOrEditFragment().apply { arguments = bundle })
            .addToBackStack(null)
            .commit()
    }

    private fun removeMemoBtn() {
        AppDataBase.instance.memoDao().deleteMemoByID(memo.id)

        val detailMemoList = AppDataBase.instance.detailMemoDao().getDetailMemoByMemoId(memo.id).toMutableList()

        for(detailMemo in detailMemoList) {
            AppDataBase.instance.detailMemoDao().deleteDetailMemoByID(detailMemo.id)
        }

        if(memo.setAlarm) {
            //알람설정 돼 있었다면 알람해제.
            AlarmHandler().cancelAlarm(context, memo.id)
        }

        if(memo.fixNotify) {
            //고성설정 돼 있었다면 알람해제.
            FixNotifyHandler().cancelFixNotify(context, memo.id)
        }

        memoListUpdateViewModel.getRecentMemoList(memo.type) // 해당 타입 메모 리스트 다시 받아옴
        if(memo.scheduleFinish) {
            memoListUpdateViewModel.getRecentMemoList(4)
        }

        (context as MainActivity).supportFragmentManager.findFragmentById(R.id.main_container)?.let {
            if(it is MainFragment) {
                MemoDeleteSnackBar(context, it.viewDataBinding.root, memo, detailMemoList, memoListUpdateViewModel).showSnackBar()
            }
        }
    }

}