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
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.landvibe.alamemo.R
import com.landvibe.alamemo.model.database.AppDataBase
import com.landvibe.alamemo.databinding.HolderLongClickMenuBinding
import com.landvibe.alamemo.handler.AlarmHandler
import com.landvibe.alamemo.handler.FixNotifyHandler
import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.model.uimodel.LongClickViewModel
import com.landvibe.alamemo.ui.activity.MainActivity
import com.landvibe.alamemo.ui.fragment.add.MemoAddOrEditFragment
import com.landvibe.alamemo.ui.fragment.main.MainFragment
import com.landvibe.alamemo.ui.fragment.snackbar.MemoDeleteSnackBar
import com.landvibe.alamemo.viewmodel.ui.MemoHolderViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemoLongClickRecyclerViewAdapter (val context: Context,
                                        val dialog: BottomSheetDialogFragment,
                                        val memo: Memo,
                                        val memoHolderViewModel: MemoHolderViewModel):
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

        CoroutineScope(Dispatchers.Main).launch {
            //메시지, 메신저로 메모/일정 공유하기
            val detailMemoList = AppDataBase.instance.detailMemoDao().getDetailMemoByMemoId(memo.id).toMutableList()

            //상단 바 고정 시 날짜를 표기해주는 용도. 메모는 표기하지 않는다.
            var contentText = if(memo.type == 1) {
                memo.icon + " " + memo.title
            } else {
                memo.icon + " " + memo.title + "\n" +
                        memoHolderViewModel.getDateFormat() + " " + memoHolderViewModel.getTimeFormat()
            }

            sortDetailMemoList(detailMemoList)

            if(memo.type != 2 && detailMemoList.isEmpty().not()) {
                contentText += "\n\n"
                //메모, 반복일정의 경우에는 시간표시가 안되므로 '-'로 구분지어줘야 한다.
                contentText += context.getString(
                    R.string.notification_fix_notify_slash) +
                        detailMemoList.joinToString(context.getString(R.string.notification_fix_notify_slash_include_line_enter)) { it.icon.value.toString() + " " + it.title.value.toString() }

            } else if(detailMemoList.isEmpty().not()){
                contentText += "\n\n"
                contentText +=
                    detailMemoList.joinToString("\n") { memoHolderViewModel.getDateFormat() + " " + memoHolderViewModel.getTimeFormat() + " - "+
                            memo.icon.toString() + " " + memo.title }
            }


            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, contentText)
            }
            context.startActivity(intent)
        }
    }

    private fun copyMemo() {
        val clipBoard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(context.getString(R.string.app_name), memo.icon.value + " " + memo.title.value)
        clipBoard.setPrimaryClip(clipData)
        Toast.makeText(context, context.getString(R.string.memo_copy_complete), Toast.LENGTH_SHORT).show()
    }

    private fun finishMemoBtn() {
        //메모/일정 완료
        AppDataBase.instance.memoDao().modifyMemo(
            id = memo.id,
            type = memo.type,
            icon = memo.icon,
            title = memo.title,
            scheduleDateYear = memo.scheduleDateYear,
            scheduleDateMonth = memo.scheduleDateMonth,
            scheduleDateDay = memo.scheduleDateDay,
            scheduleDateHour = memo.scheduleDateHour,
            scheduleDateMinute = memo.scheduleDateMinute,
            alarmStartTimeHour = memo.alarmStartTimeHour,
            alarmStartTimeMinute = memo.alarmStartTimeMinute,
            scheduleFinish = MutableLiveData(true),
            fixNotify = MutableLiveData(false),
            setAlarm = MutableLiveData(false),
            repeatDay = memo.repeatDay,
            alarmStartTimeType = MutableLiveData(1)
        )

        if(memo.setAlarm.value == true) {
            //알람설정 돼 있었다면 알람해제.
            AlarmHandler().cancelAlarm(context, memo.id)
        }
        if(memo.fixNotify.value == true) {
            //고성설정 돼 있었다면 알람해제.
            FixNotifyHandler().cancelFixNotify(context, memo.id)
        }

        (context as MainActivity).supportFragmentManager.findFragmentById(R.id.main_container)?.onResume()
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
        val removedDetailMemoList = AppDataBase.instance.detailMemoDao().getDetailMemoByMemoId(memo.id)

        if(memo.setAlarm.value == true) {
            //알람설정 돼 있었다면 알람해제.
            AlarmHandler().cancelAlarm(context, memo.id)
        }

        if(memo.fixNotify.value == true) {
            //고성설정 돼 있었다면 알람해제.
            FixNotifyHandler().cancelFixNotify(context, memo.id)
        }

        AppDataBase.instance.detailMemoDao().deleteDetailMemoByMemoID(memo.id)
        AppDataBase.instance.memoDao().deleteMemoByID(memo.id)

        (context as MainActivity).supportFragmentManager.findFragmentById(R.id.main_container)?.let {
            if(it is MainFragment) {
                it.onResume()
                MemoDeleteSnackBar(context, it.viewDataBinding.root, memo, removedDetailMemoList).showSnackBar()
            }
        }
    }

    private fun sortDetailMemoList(itemList: MutableList<DetailMemo>?) {
        itemList?.sortWith(compareBy<DetailMemo> {it.scheduleDateYear.value}
            .thenBy { it.scheduleDateMonth.value }
            .thenBy { it.scheduleDateDay.value }
            .thenBy { it.scheduleDateHour.value }
            .thenBy { it.scheduleDateMinute.value }
        )
    }
}