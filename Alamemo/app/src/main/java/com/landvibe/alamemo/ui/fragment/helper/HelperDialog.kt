package com.landvibe.alamemo.ui.fragment.helper

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.landvibe.alamemo.R
import com.landvibe.alamemo.databinding.DialogHelperBinding
import com.landvibe.alamemo.model.uimodel.HelperDialogViewModel

/**
 * Type에 따라서 보여주는 목록이 다름.
 * */
class HelperDialog(context: Context, val type: Int): Dialog(context) {
    private val binding = DialogHelperBinding.inflate(layoutInflater)
    lateinit var helperShowModelList: MutableList<HelperShowModel>
    private var curSelectIdx = 0

    init {
        setContentView(binding.root)

        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes?.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }

        initList()
        setUpBtnListener()

        binding.model = HelperDialogViewModel(
            MutableLiveData(getTitle()),
            MutableLiveData(helperShowModelList[0].image),
            MutableLiveData(helperShowModelList[0].description),
            prevEnable = MutableLiveData(false),
            nextEnable = MutableLiveData(helperShowModelList.size > 1)
        )
    }

    private fun initList() {
        when (type) {
            1 -> {
                helperShowModelList = mutableListOf(
                    HelperShowModel(R.drawable.helper_add_1, context.getString(R.string.helper_function_add_description_1)),
                    HelperShowModel(R.drawable.helper_add_2, context.getString(R.string.helper_function_add_description_2)),
                    HelperShowModel(R.drawable.helper_add_3, context.getString(R.string.helper_function_add_description_3)),
                    HelperShowModel(R.drawable.helper_add_4, context.getString(R.string.helper_function_add_description_4)),
                    HelperShowModel(R.drawable.helper_add_5, context.getString(R.string.helper_function_add_description_5)),
                    HelperShowModel(R.drawable.helper_add_6, context.getString(R.string.helper_function_add_description_6)),
                    HelperShowModel(R.drawable.helper_add_7, context.getString(R.string.helper_function_add_description_7)),
                    HelperShowModel(R.drawable.helper_add_8, context.getString(R.string.helper_function_add_description_8))
                )
            }
            2 -> {
                helperShowModelList = mutableListOf(
                    HelperShowModel(R.drawable.helper_delete_1, context.getString(R.string.helper_function_delete_description_1)),
                    HelperShowModel(R.drawable.helper_delete_2, context.getString(R.string.helper_function_delete_description_2)),
                    HelperShowModel(R.drawable.helper_delete_3, context.getString(R.string.helper_function_delete_description_3))
                )
            }
            3 -> {
                helperShowModelList = mutableListOf(
                    HelperShowModel(R.drawable.helper_edit_1, context.getString(R.string.helper_function_edit_description_1)),
                    HelperShowModel(R.drawable.helper_edit_2, context.getString(R.string.helper_function_edit_description_2)),
                    HelperShowModel(R.drawable.helper_edit_3, context.getString(R.string.helper_function_edit_description_3))
                )
            }
            4 -> {
                helperShowModelList = mutableListOf(
                    HelperShowModel(R.drawable.helper_finish_1, context.getString(R.string.helper_function_finish_description_1)),
                    HelperShowModel(R.drawable.helper_finish_2, context.getString(R.string.helper_function_finish_description_2))
                )
            }
            5 -> {
                helperShowModelList = mutableListOf(
                    HelperShowModel(R.drawable.helper_alarm_fix_noti_1, context.getString(R.string.helper_function_alarm_fix_noti_description_1)),
                    HelperShowModel(R.drawable.helper_alarm_fix_noti_2, context.getString(R.string.helper_function_alarm_fix_noti_description_2)),
                    HelperShowModel(R.drawable.helper_alarm_fix_noti_3, context.getString(R.string.helper_function_alarm_fix_noti_description_3)),
                    HelperShowModel(R.drawable.helper_alarm_fix_noti_4, context.getString(R.string.helper_function_alarm_fix_noti_description_4)),
                    HelperShowModel(R.drawable.helper_alarm_fix_noti_5, context.getString(R.string.helper_function_alarm_fix_noti_description_5))
                )
            }
            6 -> {
                helperShowModelList = mutableListOf(
                    HelperShowModel(R.drawable.helper_detail_1, context.getString(R.string.helper_function_detail_description_1)),
                    HelperShowModel(R.drawable.helper_detail_2, context.getString(R.string.helper_function_detail_description_2)),
                    HelperShowModel(R.drawable.helper_detail_3, context.getString(R.string.helper_function_detail_description_3))
                )
            }
        }
    }

    private fun setUpBtnListener() {
        binding.cancelBtn.setOnClickListener { onBackPressed() }
        binding.prevBtn.setOnClickListener {
            binding.model?.image?.value = helperShowModelList[--curSelectIdx].image
            binding.model?.description?.value = helperShowModelList[curSelectIdx].description
            checkBtnEnabled()
            binding.invalidateAll()
        }

        binding.nextBtn.setOnClickListener {
            binding.model?.image?.value = helperShowModelList[++curSelectIdx].image
            binding.model?.description?.value = helperShowModelList[curSelectIdx].description
            checkBtnEnabled()
            binding.invalidateAll()
        }
    }

    private fun checkBtnEnabled() {
        binding.model?.prevEnable?.value = (curSelectIdx == 0).not()
        binding.model?.nextEnable?.value = (curSelectIdx == (helperShowModelList.size - 1)).not()
    }

    private fun getTitle(): String {
        return when (type) {
            1 -> {
                context.getString(R.string.helper_function_add)
            }
            2 -> {
                context.getString(R.string.helper_function_delete)
            }
            3 -> {
                context.getString(R.string.helper_function_edit)
            }
            4 -> {
                context.getString(R.string.helper_function_finish)
            }
            5 -> {
                context.getString(R.string.helper_function_alarm)
            }
            6 -> {
                context.getString(R.string.helper_function_detail)
            } else -> {
                ""
            }
        }
    }

    inner class HelperShowModel (val image: Int, val description: String)
}