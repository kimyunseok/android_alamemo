package com.landvibe.alamemo.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.landvibe.alamemo.R
import com.landvibe.alamemo.databinding.HolderMemoBinding
import com.landvibe.alamemo.ui.activity.MainActivity
import com.landvibe.alamemo.ui.fragment.add.MemoAddOrEditFragment
import com.landvibe.alamemo.ui.fragment.main.DetailFragment
import com.landvibe.alamemo.handler.AlarmHandler
import com.landvibe.alamemo.handler.FixNotifyHandler
import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.model.database.AppDataBase
import com.landvibe.alamemo.viewmodel.ui.MemoHolderViewModel
import com.landvibe.alamemo.ui.dialog.MemoLongClickDialog
import com.landvibe.alamemo.viewmodel.aac.MemoListUpdateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemoRecyclerViewAdapter(val context: Context, var itemList: MutableList<Memo>, val memoListUpdateViewModel: MemoListUpdateViewModel): RecyclerView.Adapter<MemoRecyclerViewAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = HolderMemoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val memo = itemList[position]

        holder.bind(memo)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class Holder(var binding: HolderMemoBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Memo) {
            binding.viewModel = MemoHolderViewModel(item)

            itemView.setOnClickListener {
                val bundle = Bundle().apply {
                    putLong("memoId", item.id)
                    putString("memoIcon", item.icon)
                    putString("memoTitle", item.title)
                    putInt("memoType", item.type)
                }

                memoListUpdateViewModel.getRecentDetailMemoList(item.id)

                (context as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_left)
                    .replace(R.id.main_container, DetailFragment().apply { arguments = bundle })
                    .addToBackStack(null)
                    .commit()
            }

            itemView.setOnLongClickListener {
                MemoLongClickDialog(memoListUpdateViewModel).apply {
                    arguments = Bundle().apply { putLong("memoId", item.id) }
                }.show((context as MainActivity).supportFragmentManager, "longClick")
                true
            }

            binding.memoEditBtn.setOnClickListener {
                val bundle = Bundle().apply { putLong("memoId", item.id) }

                (context as MainActivity).supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_left)
                .replace(R.id.main_container, MemoAddOrEditFragment().apply { arguments = bundle })
                .addToBackStack(null)
                .commit()
            }
            
            binding.holderCompleteCb.setOnClickListener {
                Toast.makeText(context, context.getString(R.string.memo_complete), Toast.LENGTH_SHORT).show()

                notifyItemRemoved(itemList.indexOf(item))
                itemList.remove(item)

                //일정 종료처리
                AppDataBase.instance.memoDao().setMemoFinish(item.id)

                memoListUpdateViewModel.getRecentMemoList(item.type) // 해당 타입 메모 리스트 다시 받아옴
                memoListUpdateViewModel.getRecentMemoList(4) // 종료 시킨 것도 다시 받아옴

                if(item.setAlarm) {
                    //알람설정 돼 있었다면 알람해제.
                    AlarmHandler().cancelAlarm(context, item.id)
                }
                if(item.fixNotify) {
                    //고성설정 돼 있었다면 알람해제.
                    FixNotifyHandler().cancelFixNotify(context, item.id)
                }
            }
        }
    }

}