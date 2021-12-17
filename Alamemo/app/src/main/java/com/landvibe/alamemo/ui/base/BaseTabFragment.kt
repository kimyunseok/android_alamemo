package com.landvibe.alamemo.ui.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.landvibe.alamemo.R
import com.landvibe.alamemo.adapter.MemoRecyclerViewAdapter
import com.landvibe.alamemo.databinding.FragmentTabBinding
import com.landvibe.alamemo.handler.AlarmHandler
import com.landvibe.alamemo.handler.FixNotifyHandler
import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.repository.DetailMemoRepository
import com.landvibe.alamemo.repository.MemoRepository
import com.landvibe.alamemo.ui.snackbar.MemoDeleteSnackBar
import com.landvibe.alamemo.util.MemoDiffUtil
import com.landvibe.alamemo.util.MemoUtil
import com.landvibe.alamemo.util.SwipeAction
import com.landvibe.alamemo.viewmodel.aac.MemoListUpdateViewModel
import com.landvibe.alamemo.viewmodel.aac.TabFragmentViewModel
import com.landvibe.alamemo.viewmodel.viewmodelfactory.MemoAndDetailMemoViewModelFactory

abstract class BaseTabFragment<T: FragmentTabBinding>() : Fragment() {
    lateinit var viewDataBinding: T
    private val layoutId: Int = R.layout.fragment_tab
    abstract val type: Int

    lateinit var recyclerViewAdapter: MemoRecyclerViewAdapter

    private val viewModel: TabFragmentViewModel by lazy {
        ViewModelProvider(this, MemoAndDetailMemoViewModelFactory(MemoRepository(), DetailMemoRepository())).get(
            TabFragmentViewModel::class.java)
    }
    lateinit var memoListUpdateViewModel: MemoListUpdateViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        viewDataBinding.lifecycleOwner = viewLifecycleOwner

        initViewModel()

        setUpObserver()
        setTitle()
        getMemoList()

        return viewDataBinding.root
    }

    private fun initViewModel() {
        memoListUpdateViewModel = ViewModelProvider(requireActivity(), MemoAndDetailMemoViewModelFactory(MemoRepository(), DetailMemoRepository())).get(
            MemoListUpdateViewModel::class.java)
        viewModel.setEmpty(true)
        viewDataBinding.model = viewModel
    }

    private fun setTitle() {
        viewModel.setTitle(type)
    }

    private fun getMemoList() {
        viewModel.getMemoList(type)
    }

    private fun setUpObserver() {
        viewModel.memoList.observe(viewLifecycleOwner) {
            if(type == 2) {
                //일정 중에서 오늘날짜보다 지난것들은 종료처리.
                MemoUtil().finishScheduleBeforeCurrentTime(it)
            }

            MemoUtil().sortMemoList(it, type)

            setRecyclerView(it)

            viewModel.setEmpty(it.isEmpty())
        }

        memoListUpdateViewModel.recentMemoList.observe(viewLifecycleOwner) {
            Log.d("memoList Update", "Type : $type -> MemoList has been Updated")
            if(this::recyclerViewAdapter.isInitialized.not()) {
                it.contentIfNotHandled?.let { newList ->
                    setRecyclerView(newList)
                    viewModel.setEmpty(newList.isEmpty())
                }
            }
            else if(memoListUpdateViewModel.type == type) {
                it.contentIfNotHandled?.let { newList ->
                    refreshItemList(newList)
                    viewModel.setEmpty(newList.isEmpty())
                }
            }
        }

        viewModel.removedMemoAndDetailMemoList.observe(viewLifecycleOwner) {
            Log.d("memo is", it.toString())
            it.contentIfNotHandled?.let { removedMemoAndDetailMemo ->
                MemoDeleteSnackBar(
                    requireContext(),
                    viewDataBinding.root,
                    removedMemoAndDetailMemo.removedMemo,
                    removedMemoAndDetailMemo.removedDetailMemoList,
                    memoListUpdateViewModel
                ).showSnackBar()
            }
        }
    }

    private fun setRecyclerView(itemList: MutableList<Memo>) {
        setRecyclerViewAnimation()

        recyclerViewAdapter = MemoRecyclerViewAdapter(requireContext(), itemList, memoListUpdateViewModel)
        viewDataBinding.tabMemoRecycler.adapter = recyclerViewAdapter
        viewDataBinding.tabMemoRecycler.layoutManager = LinearLayoutManager(context)
        viewDataBinding.tabMemoRecycler.itemAnimator = null // 약간 깜빡이는 현상 제거
        ItemTouchHelper(setSwipeToDelete()).attachToRecyclerView(viewDataBinding.tabMemoRecycler)
    }

    private fun setSwipeToDelete(): SwipeAction {
        return object: SwipeAction() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                val tmpMemo = recyclerViewAdapter.itemList[position]
                viewModel.saveRemovedMemoAndDetailMemoList(tmpMemo)

                if(tmpMemo.setAlarm) {
                    //알람설정 돼 있었다면 알람해제.
                    AlarmHandler().cancelAlarm(requireContext(), tmpMemo.id)
                }

                if(tmpMemo.fixNotify) {
                    //고성설정 돼 있었다면 알람해제.
                    FixNotifyHandler().cancelFixNotify(requireContext(), tmpMemo.id)
                }

                viewModel.deleteDetailMemoByMemoID(tmpMemo.id)
                viewModel.deleteMemoByID(tmpMemo.id)

                recyclerViewAdapter.itemList.removeAt(position)
                recyclerViewAdapter.notifyItemRemoved(position)
                viewModel.setEmpty(recyclerViewAdapter.itemList.isEmpty())
            }

        }
    }

    private fun setRecyclerViewAnimation() {
        val fadeAnimation = AlphaAnimation(0F, 1F)
        fadeAnimation.duration = 500
        viewDataBinding.tabMemoRecycler.animation = fadeAnimation
    }

    private fun refreshItemList(newItemList: MutableList<Memo>) {
        val oldItemList = recyclerViewAdapter.itemList

        if(type == 2) {
            //일정 중에서 오늘날짜보다 지난것들은 종료처리.
            MemoUtil().finishScheduleBeforeCurrentTime(newItemList)
        }

        MemoUtil().sortMemoList(newItemList, type)

        //recyclerViewAdapter.notifyDataSetChanged() - 대체하기 권장하지 않는 코드

        //대체 코드
        val diffUtil = DiffUtil.calculateDiff(MemoDiffUtil(oldItemList, newItemList), false)
        diffUtil.dispatchUpdatesTo(recyclerViewAdapter)
        recyclerViewAdapter.itemList = newItemList
    }

}