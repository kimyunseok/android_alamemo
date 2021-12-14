package com.landvibe.alamemo.ui.fragment.main.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.landvibe.alamemo.R
import com.landvibe.alamemo.adapter.DetailMemoLongClickRecyclerViewAdapter
import com.landvibe.alamemo.model.database.AppDataBase
import com.landvibe.alamemo.databinding.DialogMemoMenuBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailMemoClickDialog: BottomSheetDialogFragment() {

    lateinit var binding: DialogMemoMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogMemoMenuBinding.inflate(inflater, container, false)

        CoroutineScope(Dispatchers.Main).launch {
            setUpView()
        }

        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.TopRadiusBottomSheepDialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 팝업 생성 시 전체화면으로 띄우기
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        if(bottomSheet != null) {
            val behavior = BottomSheetBehavior.from<View>(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private suspend fun setUpView() {
        val detailMemoId = arguments?.getLong("detailMemoId", -1)
        if(detailMemoId != null && detailMemoId != (-1).toLong()) {
            val detailMemo = AppDataBase.instance.detailMemoDao().getDetailMemoById(detailMemoId)

            //1. 클릭 타이틀 설정
            binding.titleIncludeIcon = detailMemo.icon + " " + detailMemo.title

            //2. 아래 메뉴 설정
            binding.recyclerView.adapter = DetailMemoLongClickRecyclerViewAdapter(requireContext(), this, detailMemo)
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

}