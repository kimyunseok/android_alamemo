package com.landvibe.alamemo.ui.fragment.main

import android.os.Bundle
import android.util.Log
import android.view.animation.AlphaAnimation
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.landvibe.alamemo.R
import com.landvibe.alamemo.adapter.DetailMemoRecyclerViewAdapter
import com.landvibe.alamemo.databinding.FragmentDetailBinding
import com.landvibe.alamemo.handler.AlarmHandler
import com.landvibe.alamemo.handler.FixNotifyHandler
import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.repository.DetailMemoRepository
import com.landvibe.alamemo.repository.MemoRepository
import com.landvibe.alamemo.ui.base.BaseFragment
import com.landvibe.alamemo.ui.fragment.add.DetailAddOrEditFragment
import com.landvibe.alamemo.ui.snackbar.DetailMemoDeleteSnackBar
import com.landvibe.alamemo.util.DetailMemoDiffUtil
import com.landvibe.alamemo.util.MemoUtil
import com.landvibe.alamemo.util.SwipeAction
import com.landvibe.alamemo.viewmodel.aac.DetailFragmentViewModel
import com.landvibe.alamemo.viewmodel.aac.MemoListUpdateViewModel
import com.landvibe.alamemo.viewmodel.viewmodelfactory.MemoAndDetailMemoViewModelFactory

class DetailFragment: BaseFragment<FragmentDetailBinding>() {
    override val layoutId: Int = R.layout.fragment_detail

    private val viewModel: DetailFragmentViewModel by lazy {
        ViewModelProvider(this, MemoAndDetailMemoViewModelFactory(MemoRepository(),  DetailMemoRepository())).get(
            DetailFragmentViewModel::class.java) }

    lateinit var memoListUpdateViewModel: MemoListUpdateViewModel

    lateinit var recyclerViewAdapter: DetailMemoRecyclerViewAdapter

    override fun init() {
        setBtnOnClickListener()
        setViewModel()
        setUpObserver()
        getDetailMemoList()
    }

    private fun setViewModel() {
        memoListUpdateViewModel = ViewModelProvider(requireActivity(), MemoAndDetailMemoViewModelFactory(MemoRepository(), DetailMemoRepository())).get(
            MemoListUpdateViewModel::class.java)

        arguments?.getLong("memoId")?.let { viewModel.memoId = it }

        val memoIcon = arguments?.getString("memoIcon", "-")
        val memoTitle = arguments?.getString("memoTitle", "-")

        viewModel.setEmpty(true)
        viewModel.title = "$memoIcon $memoTitle"
        viewDataBinding.model = viewModel
    }

    private fun setUpObserver() {
        viewModel.detailMemoList.observe(viewLifecycleOwner) {
            MemoUtil().sortDetailMemoList(it)
            setUpRecyclerView(it)
            viewModel.setEmpty(it.isEmpty())
        }

        viewModel.removedDetailMemo.observe(viewLifecycleOwner) {
            it.contentIfNotHandled?.let { removedDetailMemo ->
                DetailMemoDeleteSnackBar(requireContext(), viewDataBinding.root, removedDetailMemo, memoListUpdateViewModel).showSnackBar()
            }
        }

        viewModel.memoForAlarmSetting.observe(viewLifecycleOwner) {
            if(it.setAlarm) {
                //알람설정 돼 있었다면 알람재설정
                AlarmHandler().setMemoAlarm(requireContext(), it.id)
            }

            if(it.fixNotify) {
                //고성설정 돼 있었다면 고정재설정
                FixNotifyHandler().setMemoFixNotify(requireContext(), it.id)
            }
        }

        memoListUpdateViewModel.recentDetailMemoList.observe(viewLifecycleOwner) {
            if(this::recyclerViewAdapter.isInitialized) {
                Log.d("detail MemoList Update", "Detail MemoList has been Updated")
                it.contentIfNotHandled?.let { newList ->
                    refreshItemList(newList)
                    viewModel.setEmpty(newList.isEmpty())
                }
            }
        }
    }

    private fun getDetailMemoList() {
        viewModel.getDetailMemoByMemoId(viewModel.memoId)
    }

    private fun setUpRecyclerView(itemList: MutableList<DetailMemo>) {
        setAnimation()

        recyclerViewAdapter = DetailMemoRecyclerViewAdapter(requireContext(), itemList, memoListUpdateViewModel)
        viewDataBinding.detailRecycler.adapter = recyclerViewAdapter
        viewDataBinding.detailRecycler.layoutManager = LinearLayoutManager(context)
        viewDataBinding.detailRecycler.itemAnimator = null // 약간 깜빡이는 현상 제거
        ItemTouchHelper(setSwipeToDelete()).attachToRecyclerView(viewDataBinding.detailRecycler)
    }

    private fun setSwipeToDelete(): SwipeAction {
        return object: SwipeAction() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                val memoID = recyclerViewAdapter.itemList[position].memoId
                val detailMemo = recyclerViewAdapter.itemList[position]

                viewModel.deleteDetailMemoByID(detailMemo.id)
                viewModel.saveRemovedDetailMemo(detailMemo)

                recyclerViewAdapter.itemList.removeAt(position)
                recyclerViewAdapter.notifyItemRemoved(position)

                viewModel.setEmpty(recyclerViewAdapter.itemList.isEmpty())
                
                //알람 업데이트
                viewModel.getMemoByIdForAlarm(memoID)
            }

        }
    }

    private fun setAnimation() {
        val fadeAnimation = AlphaAnimation(0F, 1F)
        fadeAnimation.duration = 500
        viewDataBinding.detailRecycler.animation = fadeAnimation
    }

    private fun setBtnOnClickListener() {
        //세부일정 적기 버튼
        viewDataBinding.detailAddContentButton.setOnClickListener {
            val memoType = arguments?.getInt("memoType")

            val bundle = Bundle().apply {
                putLong("memoId", viewModel.memoId)
                memoType?.let { memoType -> putInt("memoType", memoType) }
            }

            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_left)
                .replace(R.id.main_container, DetailAddOrEditFragment().apply { arguments = bundle })
                .addToBackStack(null)
                .commit()
        }

        //뒤로가기 버튼
        viewDataBinding.detailBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun refreshItemList(newItemList: MutableList<DetailMemo>) {
        val oldItemList = recyclerViewAdapter.itemList

        MemoUtil().sortDetailMemoList(newItemList)

        //recyclerViewAdapter.notifyDataSetChanged() - 대체하기 권장하지 않는 코드

        //대체 코드
        val diffUtil = DiffUtil.calculateDiff(DetailMemoDiffUtil(oldItemList, newItemList), false)
        diffUtil.dispatchUpdatesTo(recyclerViewAdapter)
        recyclerViewAdapter.itemList = newItemList
    }
}