package com.frank.ktdemo

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import com.frank.ktdemo.databinding.ActivityGetClickPositionBinding

/**
 * Created by FH on 2017/8/1.
 */

class GetClickPositionActivity : BaseActivity() {
    lateinit var binding : ActivityGetClickPositionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(LayoutInflater.from(this) , R.layout.activity_get_click_position , null , false)
        setContentView(binding.root)
        binding.root.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event != null){
                    when (event.action){
                        MotionEvent.ACTION_UP -> showToast("x : " + event.x + " rawx : " + event.rawX + "\r\ny : " + event.getY() + " rawy : " + event.getY())
                    }
                }
                return true
            }
        })
    }

}