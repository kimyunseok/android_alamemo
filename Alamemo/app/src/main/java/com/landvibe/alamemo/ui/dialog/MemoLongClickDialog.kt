package com.landvibe.alamemo.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.landvibe.alamemo.R
import com.landvibe.alamemo.adapter.MemoLongClickRecyclerViewAdapter
import com.landvibe.alamemo.databinding.DialogMemoMenuBinding
import com.landvibe.alamemo.model.database.AppDataBase
import com.landvibe.alamemo.repository.DetailMemoRepository
import com.landvibe.alamemo.repository.MemoRepository
import com.landvibe.alamemo.viewmodel.aac.LongClickDialogViewModel
import com.landvibe.alamemo.viewmodel.aac.MemoListUpdateViewModel
import com.landvibe.alamemo.viewmodel.viewmodelfactory.MemoAndDetailMemoViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemoLongClickDialog(val memoListUpdateViewModel: MemoListUpdateViewModel): BottomSheetDialogFragment() {

    lateinit var binding: DialogMemoMenuBinding

    private val viewModel: LongClickDialogViewModel by lazy {
        ViewModelProvider(this, MemoAndDetailMemoViewModelFactory(MemoRepository(), DetailMemoRepository())).get(LongClickDialogViewModel::class.java)
    }

    private val memoId: Long by lazy { arguments?.getLong("memoId", -1)?: -1 }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogMemoMenuBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        setUpView()

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

            //드래그 할 경우에도 전체화면으로 띄우기
//            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
//                override fun onStateChanged(bottomSheet: View, newState: Int) {
//                    if(newState == BottomSheetBehavior.STATE_DRAGGING) {
//                        behavior.state = BottomSheetBehavior.STATE_EXPANDED
//                    }
//                }
//
//                override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
//                }
//
//
//            })
        }
    }

    private fun setUpView() {
        binding.viewModel = viewModel
        if(memoId != (-1).toLong()) {

            viewModel.memo.observe(viewLifecycleOwner) {
                binding.recyclerView.adapter = MemoLongClickRecyclerViewAdapter(requireContext(), this, it, memoListUpdateViewModel)
                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            }

            viewModel.getMemoInfo(memoId)

        }
    }

}