package com.landvibe.alamemo.common

import android.app.Application
import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

/*
안드로이드 기본 어플 state를 관리하는 객체.
Manifest <application> 태그 안에 name에 클래스를지정해서 사용할 수 있다.
App의 instatnce를 선언함으로서 AppDatabase에서 companion object와 연계가능.
 */

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance : GlobalApplication
            private set //

        //데이터바인딩에서 텍스트스타일 정해주기위한 메서드
        @BindingAdapter("setTextBold")
        @JvmStatic
        fun setTextBold(textView: TextView, boolean: Boolean) {
            if (boolean) {
                textView.setTypeface(null, Typeface.BOLD)
            } else {
                textView.setTypeface(null, Typeface.NORMAL)
            }
        }

        @BindingAdapter("bind_image")
        @JvmStatic
        fun bindImage(view: ImageView, res: Int?) {
            Glide.with(view.context)
                .load(res)
                .into(view)
        }
    }
}