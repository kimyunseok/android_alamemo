package com.landvibe.alamemo.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.landvibe.alamemo.R
import com.landvibe.alamemo.common.AppDataBase
import com.landvibe.alamemo.databinding.HolderMemoBinding
import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.ui.activity.MainActivity
import com.landvibe.alamemo.ui.fragment.add.MemoAddOrEditFragment
import com.landvibe.alamemo.ui.fragment.main.DetailFragment
import com.landvibe.alamemo.handler.AlarmHandler
import com.landvibe.alamemo.handler.FixNotifyHandler

class MemoRecyclerViewAdapter(val context: Context, var itemList: MutableList<Memo>): RecyclerView.Adapter<MemoRecyclerViewAdapter.Holder>() {

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
                    type = item.type,
                    icon = item.icon,
                    title = item.title,
                    scheduleDateYear = item.scheduleDateYear,
                    scheduleDateMonth = item.scheduleDateMonth,
                    scheduleDateDay = item.scheduleDateDay,
                    scheduleDateHour = item.scheduleDateHour,
                    scheduleDateMinute = item.scheduleDateMinute,
                    alarmStartTimeHour = item.alarmStartTimeHour,
                    alarmStartTimeMinute = item.alarmStartTimeMinute,
                    scheduleFinish = MutableLiveData(true),
                    fixNotify = MutableLiveData(false),
                    setAlarm = MutableLiveData(false),
                    repeatDay = item.repeatDay,
                    alarmStartTimeType = MutableLiveData(1)
                )

                if(item.setAlarm.value == true) {
                    //알람설정 돼 있었다면 알람해제.
                    AlarmHandler().cancelAlarm(context, item.id)
                }
                if(item.fixNotify.value == true) {
                    //고성설정 돼 있었다면 알람해제.
                    FixNotifyHandler().cancelFixNotify(context, item.id)
                }

                (context as MainActivity).supportFragmentManager.findFragmentById(R.id.main_container)?.onResume()
            }
        }
    }

}