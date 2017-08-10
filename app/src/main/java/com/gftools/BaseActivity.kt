package com.gftools

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.support.v7.app.NotificationCompat
import android.widget.Toast
import com.gftools.utils.*

/**
 * Created by FH on 2017/8/1.
 */
open class BaseActivity : Activity() {
    protected var toast : Toast? = null

    protected var mHandler = Handler()

    protected fun showToast(msg : String){
        mHandler.post(object : Runnable{
            override fun run() {
                toast?.cancel()

                toast = Toast.makeText(applicationContext , msg , Toast.LENGTH_SHORT)
//                if (toast == null){
//                    toast = Toast.makeText(applicationContext , msg , Toast.LENGTH_SHORT)
//                }
//                else {
//                    toast?.setText(msg)
//                }
                toast?.show()
            }
        })
    }

    fun goHome(){
        val mHomeIntent = Intent(Intent.ACTION_MAIN)
        mHomeIntent.addCategory(Intent.CATEGORY_HOME)
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        startActivity(mHomeIntent)
    }

    protected fun waitSecond(time : Int){
        var past = 0
        while (past < time){
            Thread.sleep(1000)
            past++
            showToastAndLv("wait $past s" + (if (past==time) " wait end " else ""))
        }
    }


    protected fun showToastAndLv(msg : String){
        lv(msg)
        showToast(msg)
    }

    protected fun openActivity(cls : Class<*>){
        var intent = Intent(this , cls)
        startActivity(intent)
    }

    fun launchGF() : Boolean{
        var packs = packageManager.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES)
        for (pack in packs){
            if (pack.packageName.contains("girlsfrontline")){
                lv(pack.packageName)
                var intent = packageManager.getLaunchIntentForPackage(pack.packageName)
                lv("intent " + intent)
                startActivity(intent)
                return true
            }
        }
        showToastAndLv("本机中找不到少女前线的APP")
        return false
    }

}