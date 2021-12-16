package com.landvibe.alamemo.viewmodel.aac

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.landvibe.alamemo.R

class HelperDialogViewModel(private val type: Int): ViewModel() {
    val title: String by lazy {

        when (type) {
            1 -> {
                "메모/일정 추가"
            }
            2 -> {
                "메모/일정 삭제"
            }
            3 -> {
               "메모/일정 종료"
            }
            4 -> {
                "알람/상단바 고정 관련"
            }
            5 -> {
                "메모/일정 수정"
            }
            6 -> {
                "세부 메모/일정"
            }
            7 -> {
                "세부 메뉴(메모/일정 공유, 복사 기능 등)"
            }
            else -> {
                ""
            }
        }

    }

    private val _image = MutableLiveData<Int>()
    val image: LiveData<Int>
        get() = _image

    private val _description = MutableLiveData<String>()
    val description: LiveData<String>
        get() = _description

    private val _prevEnable = MutableLiveData<Boolean>()
    val prevEnable: LiveData<Boolean>
        get() = _prevEnable

    private val _nextEnable = MutableLiveData<Boolean>()
    val nextEnable: LiveData<Boolean>
        get() = _nextEnable

    private var curSelectIdx = 0

    private val helperShowModelList: MutableList<HelperShowModel> by lazy {
        when (type) {
            1 -> {
                mutableListOf(
                    HelperShowModel(R.drawable.helper_add_1, "우측 상단 위에\n메모 추가 버튼을 누릅니다."),
                    HelperShowModel(R.drawable.helper_add_2, "유형이 메모인 경우에는\n메모할 내용을\n입력할 수 있습니다."),
                    HelperShowModel(R.drawable.helper_add_3, "아이콘을 누르면\n메모와 저장할\n아이콘을 선택할 수 있습니다."),
                    HelperShowModel(R.drawable.helper_add_4, "유형이 일정인 경우에는\n날짜와 시간을\n선택할 수 있습니다."),
                    HelperShowModel(R.drawable.helper_add_5, "메모와 마찬가지로\n아이콘과 일정의 이름을\n적으시면 됩니다."),
                    HelperShowModel(R.drawable.helper_add_6, "일정의 경우에는\n알람 기능을\n사용하실 수 있습니다."),
                    HelperShowModel(R.drawable.helper_add_7, "유형이 반복 일정인 경우에는\n요일을 선택할 수 있습니다."),
                    HelperShowModel(R.drawable.helper_add_8, "반복 일정의 경우\n해당 요일에 날의\n알람 시간에 맞춰서 알람이 울립니다.")
                )
            }
            2 -> {
                mutableListOf(
                    HelperShowModel(R.drawable.helper_delete_1, "메모를 좌/우로\n스와이프 합니다."),
                    HelperShowModel(R.drawable.helper_delete_2, "메모가 삭제되며\n하단에 스낵바가 나옵니다."),
                    HelperShowModel(R.drawable.helper_delete_3, "삭제 취소를 누르면\n삭제가 취소됩니다.")
                )
            }
            3 -> {
                mutableListOf(
                    HelperShowModel(R.drawable.helper_edit_1, "메모의 우측에 있는\n수정 버튼을 누릅니다."),
                    HelperShowModel(R.drawable.helper_edit_2, "메모 수정 창이 나옵니다.\n수정할 내용을 입력하고\n저장하기를 누릅니다."),
                    HelperShowModel(R.drawable.helper_edit_3, "메모가 수정되어서\n저장됩니다.")
                )
            }
            4 -> {
                mutableListOf(
                    HelperShowModel(R.drawable.helper_finish_1, "메모의 아이콘 왼쪽에 있는\n체크 박스를 체크합니다."),
                    HelperShowModel(R.drawable.helper_finish_2, "메모가 종료처리 되며\n[종료]탭으로 이동됩니다.\n이렇게 종료된 메모는\n알람, 상단바 고정이 해제됩니다.")
                )
            }
            5 -> {
                mutableListOf(
                        HelperShowModel(R.drawable.helper_alarm_fix_noti_1, "일정은 알람 기능을\n사용할 수 있습니다.\n메모를 작성/수정할 때\n시작 날짜와 알람 시간을\n설정할 수 있습니다."),
                    HelperShowModel(R.drawable.helper_alarm_fix_noti_2, "반복 일정도 알람 기능을 사용할 수 있습니다."),
                    HelperShowModel(R.drawable.helper_alarm_fix_noti_3, "알람을 설정하면\n알람 시간에 맞춰서\n알람이 오게 됩니다.\n일정과 반복 일정은 각각\n다른 형식으로 알람이 갑니다."),
                    HelperShowModel(R.drawable.helper_alarm_fix_noti_4, "상단바 고정을 설정하면\n상단바에 메모에 대한 정보가\n고정됩니다.\n각 유형들은 다른 형식으로\n고정됩니다."),
                    HelperShowModel(R.drawable.helper_alarm_fix_noti_5, "상단바 고정과 알람 기능을\n같이 사용할 수 있습니다.")
                )
            }
            6 -> {
                mutableListOf(
                    HelperShowModel(R.drawable.helper_detail_1, "메모를 누릅니다."),
                    HelperShowModel(R.drawable.helper_detail_2, "메모의 세부 메모\n페이지로 이동됩니다.\n여기서 세부 메모/일정을\n추가할 수 있습니다."),
                    HelperShowModel(R.drawable.helper_detail_3, "메모의 세부 유형은\n메모로 고정되고\n일정의 세부 유형은\n일정으로 고정됩니다.")
                )
            }
            7 -> {
                mutableListOf(
                    HelperShowModel(R.drawable.helper_share_1, "여행의 세부 일정을\n위처럼 짠 뒤에\n공유를 하고싶을 수 있습니다."),
                    HelperShowModel(R.drawable.helper_share_2, "이 때 메모/일정을 길게 누릅니다."),
                    HelperShowModel(R.drawable.helper_share_3, "아래에서 메뉴 창이 나오게 되며\n원하는 기능을 선택할 수 있습니다.\n카카오톡으로 일정을\n공유해 보겠습니다."),
                    HelperShowModel(R.drawable.helper_share_4, "위 이미지처럼 다른사람에게\n일정이 공유됩니다."),
                    HelperShowModel(R.drawable.helper_share_5, "세부 일정은\n한 번만 누르면 위 메뉴창이\n나오게 됩니다.")
                )
            } else -> {
            mutableListOf()
        }
        }
    }

    fun initViewModel() {
        setImage(helperShowModelList[curSelectIdx].image)
        setDescription(helperShowModelList[curSelectIdx].description)
        checkBtnEnabled()
    }

    private fun setImage(image: Int) {
        _image.postValue(image)
    }

    private fun setDescription(description: String) {
        _description.postValue(description)
    }

    fun getPrevData() {
        setImage(helperShowModelList[--curSelectIdx].image)
        setDescription(helperShowModelList[curSelectIdx].description)
        checkBtnEnabled()
    }

    fun getNextData() {
        setImage(helperShowModelList[++curSelectIdx].image)
        setDescription(helperShowModelList[curSelectIdx].description)
        checkBtnEnabled()
    }

    private fun checkBtnEnabled() {
        setPrevEnable((curSelectIdx == 0).not())
        setNextEnable((curSelectIdx == (helperShowModelList.size - 1)).not())
    }

    private fun setPrevEnable(prevEnable: Boolean) {
        _prevEnable.postValue(prevEnable)
    }

    private fun setNextEnable(nextEnable: Boolean) {
        _nextEnable.postValue(nextEnable)
    }

    inner class HelperShowModel (val image: Int, val description: String)
}