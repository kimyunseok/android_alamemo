package com.landvibe.alamemo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.landvibe.alamemo.adapter.MemoRecyclerViewAdapter
import com.landvibe.alamemo.databinding.FragmentTabBinding
import com.landvibe.alamemo.handler.AlarmHandler
import com.landvibe.alamemo.handler.FixNotifyHandler
import com.landvibe.alamemo.model.data.memo.Memo
import com.landvibe.alamemo.repository.DetailMemoRepository
import com.landvibe.alamemo.repository.MemoRepository
import com.landvibe.alamemo.ui.snackbar.MemoDeleteSnackBar
import com.landvibe.alamemo.util.MemoUtil
import com.landvibe.alamemo.util.SwipeAction
import com.landvibe.alamemo.viewmodel.aac.TabFragmentViewModel
import com.landvibe.alamemo.viewmodel.viewmodelfactory.MemoAndDetailMemoViewModelFactory
import java.util.*

abstract class BaseTabFragment<T: FragmentTabBinding>() : Fragment() {
    lateinit var viewDataBinding: T
    abstract val layoutId: Int
    abstract val type: Int

    lateinit var recyclerViewAdapter: MemoRecyclerViewAdapter

    private val viewModel: TabFragmentViewModel by lazy {
        ViewModelProvider(this, MemoAndDetailMemoViewModelFactory(MemoRepository(), DetailMemoRepository())).get(
            TabFragmentViewModel::class.java)
    }

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
                finishScheduleBeforeCurrentTime(it)
            }

            MemoUtil().sortMemoList(it)
            setRecyclerView(it)

            viewModel.setEmpty(it.isEmpty())
        }

        viewModel.removedMemo.observe(viewLifecycleOwner) {
            MemoDeleteSnackBar(requireContext(), viewDataBinding.root, viewModel.savedMemo, viewModel.savedDetailMemoList).showSnackBar()
        }
    }

    private fun setRecyclerView(itemList: MutableList<Memo>) {
        setRecyclerViewAnimation()

        recyclerViewAdapter = MemoRecyclerViewAdapter(requireContext(), itemList)
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
                viewModel.saveRemovedMemo(tmpMemo)
                viewModel.saveRemovedDetailMemoListByMemoId(tmpMemo.id)

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

    private fun finishScheduleBeforeCurrentTime(itemList: MutableList<Memo>) {
        //일정 중에서 오늘날짜보다 지난것들은 종료처리.
        val today = System.currentTimeMillis()
        for(data in itemList) {
            val calendar = Calendar.getInstance()
            data.scheduleDateYear.let { calendar.set(Calendar.YEAR, it) }
            data.scheduleDateMonth.let { calendar.set(Calendar.MONTH, it) }
            data.scheduleDateDay.let { calendar.set(Calendar.DAY_OF_MONTH, it) }
            //시간은 상관없이 당일의 모든 일정을 보여주도록 하기위해 비교하는 날의 시간은 23:59분으로 맞춤.
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            val checkDay = calendar.time.time

            if(checkDay < today) {
                viewModel.setMemoFinish(data.id)
                itemList.remove(data)
            }
        }
    }

}