package com.landvibe.alamemonew.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.landvibe.alamemonew.R
import com.landvibe.alamemonew.common.AppDataBase
import com.landvibe.alamemonew.databinding.HolderMemoBinding
import com.landvibe.alamemonew.model.data.memo.Memo
import com.landvibe.alamemonew.ui.activity.MainActivity
import com.landvibe.alamemonew.ui.fragment.add.MemoAddOrEditFragment
import com.landvibe.alamemonew.ui.fragment.main.DetailFragment
import java.util.*

class MemoRecyclerViewAdapter(val context: Context, val itemList: MutableList<Memo>): RecyclerView.Adapter<MemoRecyclerViewAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = HolderMemoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val memo = itemList[position]

        holder.bind(memo, position)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class Holder(var binding: HolderMemoBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Memo, position: Int) {
            binding.model = item

            itemView.setOnClickListener {
                val bundle = Bundle().apply {
                    putLong("memoId", item.id)
                    putString("memoIcon", item.icon.value.toString())
                    putString("memoTitle", item.title.value.toString())
                    item.type.value?.let { type -> putInt("memoType", type) }
                }

                (context as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_left)
                    .replace(R.id.main_container, DetailFragment().apply { arguments = bundle })
                    .addToBackStack(null)
                    .commit()
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
                //일정 종료처리
                AppDataBase.instance.memoDao().modifyMemo(
                    id = item.id,
                    type = MutableLiveData(4),
                    icon = item.icon,
                    title = item.title,
                    scheduleDateYear = item.scheduleDateYear,
                    scheduleDateMonth = item.scheduleDateMonth,
                    scheduleDateDay = item.scheduleDateDay,
                    scheduleDateHour = item.scheduleDateHour,
                    scheduleDateMinute = item.scheduleDateMinute,
                    alarmStartTimeHour = item.alarmStartTimeHour,
                    alarmStartTimeMinute = item.alarmStartTimeMinute,
                    fixNotify = MutableLiveData(false),
                    setAlarm = MutableLiveData(false),
                    repeatDay = item.repeatDay,
                    alarmStartTimeType = MutableLiveData(1)
                )
                //TODO : 알람 종료시키기

                itemList.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

}