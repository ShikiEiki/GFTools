package com.gftools

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

/**
 * Created by FH on 2017/8/10.
 */

class GFTools : Application() {
    companion object {
        lateinit var sp : SharedPreferences
        fun getMainForceIndex() : Int {
            return sp.getInt("main_force" , -1)
        }
        fun setMainForce(mainforceIndex : Int){
            sp.edit().putInt("main_force" , mainforceIndex).apply()
        }
    }
    override fun onCreate() {
        super.onCreate()
        sp = getSharedPreferences("config" , Context.MODE_PRIVATE)
        var mainForce = sp.getInt("main_force" , -1)
        if (mainForce == -1){
            sp.edit().putInt("main_force" , 3).apply()
        }
    }


}
