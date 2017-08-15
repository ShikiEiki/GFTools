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
        fun setMainForceIndex(mainforceIndex : Int){
            sp.edit().putInt("main_force" , mainforceIndex).apply()
        }
        fun setBattleIndex(battleIndex : Int){
            sp.edit().putInt("battle" , battleIndex).apply()
        }
        fun getBattleIndex() : Int {
            return sp.getInt("battle" , -1)
        }
        fun setCannonFodderIndex(cannonFodderIndex : Int){
            sp.edit().putInt("cannonFodder" , cannonFodderIndex).apply()
        }
        fun getCannonFodderIndex() : Int {
            return sp.getInt("cannonFodder" , -1)
        }
    }
    override fun onCreate() {
        super.onCreate()
        sp = getSharedPreferences("config" , Context.MODE_PRIVATE)
        if (getMainForceIndex() == -1){
            setMainForceIndex(0)
        }
        if (getBattleIndex() == -1){
            setBattleIndex(0)
        }
        if (getCannonFodderIndex() == -1){
            setCannonFodderIndex(3)
        }
    }
}
