package com.landvibe.alamemo.ui.activity

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.landvibe.alamemo.R
import com.landvibe.alamemo.databinding.ActivityMainBinding
import com.landvibe.alamemo.handler.AlarmHandler
import com.landvibe.alamemo.handler.FixNotifyHandler
import com.landvibe.alamemo.ui.base.BaseActivity
import com.landvibe.alamemo.ui.fragment.main.DetailFragment
import com.landvibe.alamemo.ui.fragment.main.MainFragment
import com.landvibe.alamemo.util.NotificationChannelMaker

/**
MainActivity는 자동으로 menifests에 추가됨. 따로 추가할 필요가 없다.
p.s 아이콘 출저 https://material.io/resources/icons/?icon=check&style=outline
https://www.iconfinder.com/iconsets/miscellaneous-80
https://www.iconfinder.com/iconsets/app-custom-ui-1

폰트 출저 : 본 서체의 저작권 및 지적 재산권은 국립한글박물관에 있습니다.
http://www.print.or.kr/bbs/board.php?bo_table=B52&wr_id=26&page=0&page=0
 */

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private var backKeyWaitingTime:Long = 0

    override val layoutId: Int = R.layout.activity_main

    override fun onBackPressed() {
        if(supportFragmentManager.findFragmentById(R.id.main_container) is MainFragment &&
            System.currentTimeMillis() - backKeyWaitingTime >= 2000) { // 메인 프래그먼트이고 2초 전에 뒤로가기를 한번 더 누르면 앱 종료
            backKeyWaitingTime = System.currentTimeMillis()
            Toast.makeText(this, getString(R.string.one_more_back_exit), 1 * 200).show()
        } else {
            super.onBackPressed()
        }
    }

    override fun init() {
        NotificationChannelMaker().createAlarmNotificationChannel(applicationContext) // 노티피케이션 채널 생성
        NotificationChannelMaker().createFixNotificationChannel(applicationContext)
        setUpFixNotifyAndAlarm(applicationContext)

        setUpFragment()
    }

    private fun setUpFragment() {
        val memoId = intent?.getLongExtra("memoId", -1)
        val memoType= intent?.getIntExtra("memoType", -1)
        if(memoId != null && memoId != (-1).toLong() && memoType != null && memoType != -1) {
            //상단바를 통해서 들어온 경우.
            val memoIcon = intent?.getStringExtra("memoIcon")
            val memoTitle = intent?.getStringExtra("memoTitle")

            val bundle = Bundle().apply {
                putLong("memoId", memoId)
                putString("memoIcon", memoIcon)
                putString("memoTitle", memoTitle)
                putInt("memoType", memoType)
            }

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_container, DetailFragment().apply { arguments = bundle })
                .commit()
        } else {
            //그냥 앱을 킨 경우.
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_container, MainFragment())
                .commit()
        }
    }

    //알람 설정을 해준다.
    private fun setUpFixNotifyAndAlarm(context: Context) {
        AlarmHandler().setUpAllMemoAlarm(context)
        FixNotifyHandler().setUpAllFixNotify(context)
    }
}