package com.landvibe.alamemonew.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.*
import com.landvibe.alamemonew.adapter.MemoRecyclerViewAdapter
import com.landvibe.alamemonew.common.AppDataBase
import com.landvibe.alamemonew.databinding.TabFragmentBinding
import com.landvibe.alamemonew.model.data.memo.Memo
import com.landvibe.alamemonew.model.uimodel.TabFragmentViewModel
import com.landvibe.alamemonew.ui.fragment.dialog.MemoDeleteDialog
import com.landvibe.alamemonew.util.AboutDay
import com.landvibe.alamemonew.util.MemoDiffUtil
import com.landvibe.alamemonew.util.SwipeAction
import java.util.*

abstract class BaseTabFragment<T: TabFragmentBinding>() : Fragment() {
    lateinit var viewDataBinding: T
    abstract val layoutId: Int
    abstract val type: Int

    lateinit var recyclerViewAdapter: MemoRecyclerViewAdapter

    abstract fun setTitle()
    abstract fun init()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        initViewModel()
        viewDataBinding.lifecycleOwner = this

        init()
        setRecyclerView()

        return viewDataBinding.root
    }

    override fun onResume() {
        super.onResume()

        //화면 재구성 시 필요.
        if(this::viewDataBinding.isInitialized) {
            refreshRecyclerView()
        }
    }

    private fun initViewModel() {
        val viewModel = TabFragmentViewModel()
        viewModel.memoEmpty.value = true
        viewDataBinding.model = viewModel
        setTitle() // viewModel설정 후 title 설정.
    }

    private fun setRecyclerView() {
        setAnimation()
        val itemList = AppDataBase.instance.memoDao().getMemoByType(type).toMutableList()

        itemList.sortWith(compareBy<Memo> {it.scheduleDateYear.value}
            .thenBy { it.scheduleDateMonth.value }
            .thenBy { it.scheduleDateDay.value }
            .thenBy { it.scheduleDateHour.value }
            .thenBy { it.scheduleDateMinute.value }
            .thenBy { it.alarmStartTimeHour.value }
            .thenBy { it.alarmStartTimeMinute.value }
        )

        recyclerViewAdapter = MemoRecyclerViewAdapter(requireContext(), itemList)
        viewDataBinding.tabMemoRecycler.adapter = recyclerViewAdapter
        viewDataBinding.tabMemoRecycler.layoutManager = LinearLayoutManager(context)
        viewDataBinding.tabMemoRecycler.itemAnimator = null // 약간 깜빡이는 현상 제거
        ItemTouchHelper(setSwipeToDelete(recyclerViewAdapter)).attachToRecyclerView(viewDataBinding.tabMemoRecycler)

        viewDataBinding.model?.memoEmpty?.value = itemList.isEmpty()
    }

    private fun refreshRecyclerView() {
        val itemList = AppDataBase.instance.memoDao().getMemoByType(type).toMutableList()
        itemList.sortWith(compareBy<Memo> {it.scheduleDateYear.value}
            .thenBy { it.scheduleDateMonth.value }
            .thenBy { it.scheduleDateDay.value }
            .thenBy { it.scheduleDateHour.value }
            .thenBy { it.scheduleDateMinute.value }
            .thenBy { it.alarmStartTimeHour.value }
            .thenBy { it.alarmStartTimeMinute.value }
        )
        //recyclerViewAdapter.notifyDataSetChanged() - 대체하기 권장하지 않는 코드

        //대체 코드
        val oldItemList = recyclerViewAdapter.itemList
        val diffUtil = DiffUtil.calculateDiff(MemoDiffUtil(oldItemList, itemList), false)
        diffUtil.dispatchUpdatesTo(recyclerViewAdapter)
        recyclerViewAdapter.itemList = itemList

        viewDataBinding.model?.memoEmpty?.value = itemList.isEmpty()

    }

    private fun setSwipeToDelete(adapter: MemoRecyclerViewAdapter): SwipeAction {
        return object: SwipeAction() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition

                val dialog = MemoDeleteDialog(requireContext(), adapter, pos)
                dialog.show()
            }

        }
    }

    private fun setAnimation() {
        val fadeAnimation = AlphaAnimation(0F, 1F)
        fadeAnimation.duration = 500
        viewDataBinding.tabMemoRecycler.animation = fadeAnimation
    }
}