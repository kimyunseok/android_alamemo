package com.landvibe.alamemonew.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import com.landvibe.alamemonew.databinding.ActivitySplashBinding
import com.landvibe.alamemonew.ui.activity.MainActivity

/*
    앱 실행시 로딩 화면을 하는 스플래시 액티비티
    https://bongcando.tistory.com/2
    를 참조하였다.
 */
class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding

    val DURATION: Long = 1000 //1초간 스플래시 화면을 보여줌 (ms)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)

        fadeOutAnim()
        Handler(Looper.getMainLooper()).postDelayed({ //delay를 위한 handler
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, DURATION)
    }

    private fun fadeOutAnim() {
        val fadeOutAnim = AlphaAnimation(1.0F, 0F)
        fadeOutAnim.duration = DURATION

        binding.splashLayout.startAnimation(fadeOutAnim)
        Handler(Looper.getMainLooper()).postDelayed({
            binding.splashLayout.visibility = View.GONE
        }, DURATION)
    }
}