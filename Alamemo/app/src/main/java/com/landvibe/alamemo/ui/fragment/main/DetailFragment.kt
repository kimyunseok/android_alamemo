package com.landvibe.alamemo.ui.fragment.main

import android.os.Bundle
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.landvibe.alamemo.R
import com.landvibe.alamemo.adapter.DetailMemoRecyclerViewAdapter
import com.landvibe.alamemo.common.AppDataBase
import com.landvibe.alamemo.databinding.FragmentDetailBinding
import com.landvibe.alamemo.handler.AlarmHandler
import com.landvibe.alamemo.handler.FixNotifyHandler
import com.landvibe.alamemo.model.data.detail.DetailMemo
import com.landvibe.alamemo.model.uimodel.DetailFragmentViewModel
import com.landvibe.alamemo.ui.BaseFragment
import com.landvibe.alamemo.ui.fragment.add.DetailAddOrEditFragment
import com.landvibe.alamemo.ui.fragment.snackbar.DetailMemoDeleteSnackBar
import com.landvibe.alamemo.util.DetailMemoDiffUtil
import com.landvibe.alamemo.util.SwipeAction

class DetailFragment: BaseFragment<FragmentDetailBinding>() {
    override val layoutId: Int = R.layout.fragment_detail

    private var memoId: Long? = -1

    lateinit var recyclerViewAdapter: DetailMemoRecyclerViewAdapter

    override fun init() {
        setBtnOnClickListener()
        getMemoInfo()
        setUpRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        refreshRecyclerView()
    }

    private fun getMemoInfo() {
        if(arguments != null) {
            memoId = arguments?.getLong("memoId")
        }

        val memoIcon = arguments?.getString("memoIcon", "-")
        val memoTitle = arguments?.getString("memoTitle", "-")

        val viewModel = DetailFragmentViewModel()
        viewModel.memoEmpty.value = true
        viewModel.title.value = "$memoIcon $memoTitle"
        viewDataBinding.model = viewModel
    }

    private fun setBtnOnClickListener() {
        //세부일정 적기 버튼
        viewDataBinding.detailAddContentButton.setOnClickListener {
            val memoType = arguments?.getInt("memoType")

            val bundle = Bundle().apply {
                memoId?.let { memoId -> putLong("memoId", memoId) }
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

    private fun setUpRecyclerView() {
        setAnimation()
        val itemList = memoId?.let { memoId -> AppDataBase.instance.detailMemoDao().getDetailMemoByMemoId(memoId).toMutableList() }

        sortDetailMemoList(itemList)

        if(itemList != null) {
            recyclerViewAdapter = DetailMemoRecyclerViewAdapter(requireContext(), itemList)
            viewDataBinding.detailRecycler.adapter = recyclerViewAdapter
            viewDataBinding.detailRecycler.layoutManager = LinearLayoutManager(context)
            viewDataBinding.detailRecycler.itemAnimator = null // 약간 깜빡이는 현상 제거
            ItemTouchHelper(setSwipeToDelete()).attachToRecyclerView(viewDataBinding.detailRecycler)
        }
        viewDataBinding.model?.memoEmpty?.value = itemList?.isEmpty()
    }

    private fun setSwipeToDelete(): SwipeAction {
        return object: SwipeAction() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                //TODO : 알람삭제
                //TODO : 상단바 고정 삭제
                val memoID = recyclerViewAdapter.itemList[position].memoId
                val detailMemoID = recyclerViewAdapter.itemList[position].id
                val tmpDetailMemo = recyclerViewAdapter.itemList[position]

                AppDataBase.instance.detailMemoDao().deleteDetailMemoByID(detailMemoID)

                recyclerViewAdapter.itemList.removeAt(position)
                recyclerViewAdapter.notifyItemRemoved(position)
                viewDataBinding.model?.memoEmpty?.value = recyclerViewAdapter.itemList.isEmpty()
                
                //알람 업데이트
                val tmpMemo = AppDataBase.instance.memoDao().getMemoById(memoID)
                if(tmpMemo.setAlarm.value == true) {
                    //알람설정 돼 있었다면 알람재설정
                    AlarmHandler().setMemoAlarm(requireContext(), tmpMemo)
                }
                if(tmpMemo.fixNotify.value == true) {
                    //고성설정 돼 있었다면 고정재설정
                    FixNotifyHandler().setMemoFixNotify(requireContext(), tmpMemo)
                }

                viewDataBinding.model?.memoEmpty?.let {
                    memoEmpty ->
                    DetailMemoDeleteSnackBar(requireContext(), viewDataBinding.root, recyclerViewAdapter, position, tmpDetailMemo,
                        memoEmpty
                    ).showSnackBar()
                }
            }

        }
    }

    private fun setAnimation() {
        val fadeAnimation = AlphaAnimation(0F, 1F)
        fadeAnimation.duration = 500
        viewDataBinding.detailRecycler.animation = fadeAnimation
    }

    private fun refreshRecyclerView() {
        val itemList = memoId?.let { memoId -> AppDataBase.instance.detailMemoDao().getDetailMemoByMemoId(memoId).toMutableList() }

        sortDetailMemoList(itemList)

        //recyclerViewAdapter.notifyDataSetChanged() - 대체하기 권장하지 않는 코드

        //대체 코드
        val oldItemList = recyclerViewAdapter.itemList
        if(itemList != null) {
            val diffUtil = DiffUtil.calculateDiff(DetailMemoDiffUtil(oldItemList, itemList), false)
            diffUtil.dispatchUpdatesTo(recyclerViewAdapter)
            recyclerViewAdapter.itemList = itemList
        }

        viewDataBinding.model?.memoEmpty?.value = itemList?.isEmpty()

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