package com.landvibe.alamemonew.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.landvibe.alamemonew.R
import com.landvibe.alamemonew.common.AppDataBase
import com.landvibe.alamemonew.common.GlobalApplication.Companion.memoToMemoViewModel
import com.landvibe.alamemonew.databinding.HolderMemoBinding
import com.landvibe.alamemonew.model.data.memo.Memo
import com.landvibe.alamemonew.model.uimodel.MemoViewModel
import com.landvibe.alamemonew.ui.activity.MainActivity
import com.landvibe.alamemonew.ui.fragment.add.MemoAddOrEditFragment
import java.util.*

class MemoRecyclerViewAdapter(val context: Context, val itemList: MutableList<Memo>): RecyclerView.Adapter<MemoRecyclerViewAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = HolderMemoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val memo = itemList[position]

        val item = MemoViewModel(
            0,
            type = MutableLiveData<Int>(1),
            icon = MutableLiveData<String>(context.getString(R.string.memo_emoji)),
            title = MutableLiveData<String>(""),
            scheduleDate = MutableLiveData<Date>(Date(System.currentTimeMillis() ) ),
            alarmStartTime = MutableLiveData<Date>(Date(System.currentTimeMillis() ) ),
            fixNotify = MutableLiveData<Boolean>(false),
            setAlarm = MutableLiveData<Boolean>(false),
            repeatDay = mutableListOf(),
            alarmStartTimeType = MutableLiveData(1)
        )

        memoToMemoViewModel(memo, item)

        holder.bind(item, memo, position)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class Holder(var binding: HolderMemoBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MemoViewModel, memo: Memo, position: Int) {
            binding.model = item

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
                //일정 종료처리
                AppDataBase.instance.memoDao().modifyMemo(memo.id, 4,
                    memo.icon,
                    memo.title,
                    memo.scheduleDate,
                    memo.alarmStartTime,
                    memo.fixNotify,
                    memo.setAlarm,
                    memo.repeatDay,
                    memo.alarmStartTimeType
                )
                notifyItemRemoved(position)
            }
        }
    }

}