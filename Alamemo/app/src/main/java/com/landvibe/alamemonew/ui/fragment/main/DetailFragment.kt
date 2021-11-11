package com.landvibe.alamemonew.ui.fragment.main

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.landvibe.alamemonew.R
import com.landvibe.alamemonew.adapter.DetailMemoRecyclerViewAdapter
import com.landvibe.alamemonew.common.AppDataBase
import com.landvibe.alamemonew.databinding.FragmentDetailBinding
import com.landvibe.alamemonew.model.data.detail.DetailMemo
import com.landvibe.alamemonew.model.uimodel.DetailFragmentViewModel
import com.landvibe.alamemonew.ui.BaseFragment
import com.landvibe.alamemonew.ui.fragment.add.DetailAddOrEditFragment
import com.landvibe.alamemonew.ui.fragment.dialog.DetailMemoDeleteDialog
import com.landvibe.alamemonew.util.SwipeAction

class DetailFragment: BaseFragment<FragmentDetailBinding>() {
    override val layoutId: Int = R.layout.fragment_detail

    private var memoId: Long? = -1

    override fun init() {
        setBtnOnClickListener()
        getMemoInfo()
        setUpRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        setUpRecyclerView()
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
        val itemList = memoId?.let { memoId -> AppDataBase.instance.detailMemoDao().getDetailMemoByMemoId(memoId).toMutableList() }

        itemList?.sortWith(compareBy<DetailMemo> {it.scheduleDateYear.value}
            .thenBy { it.scheduleDateMonth.value }
            .thenBy { it.scheduleDateDay.value }
            .thenBy { it.scheduleDateHour.value }
            .thenBy { it.scheduleDateMinute.value }
        )
        viewDataBinding.model?.memoEmpty?.value = itemList?.isEmpty()

        if(itemList != null) {
            val adapter = DetailMemoRecyclerViewAdapter(requireContext(), itemList)
            viewDataBinding.detailRecycler.adapter = adapter
            viewDataBinding.detailRecycler.layoutManager = LinearLayoutManager(context)
            ItemTouchHelper(setSwipeToDelete(adapter)).attachToRecyclerView(viewDataBinding.detailRecycler)
        }
    }

    private fun setSwipeToDelete(adapter: DetailMemoRecyclerViewAdapter): SwipeAction {
        return object: SwipeAction() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition

                val dialog = DetailMemoDeleteDialog(requireContext(), adapter, pos)
                dialog.show()
            }

        }
    }
}