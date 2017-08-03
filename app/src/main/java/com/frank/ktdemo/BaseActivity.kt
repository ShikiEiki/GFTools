package com.frank.ktdemo

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.widget.Toast
import java.io.DataOutputStream
import java.io.OutputStream

/**
 * Created by FH on 2017/8/1.
 */
open class BaseActivity : Activity() {
    var toast : Toast? = null

    var mHandler = Handler()

    fun lv(msg : String){
        Log.v("FH" , msg)
    }

    fun showToast(msg : String){
        mHandler.post(object : Runnable{
            override fun run() {
                lv("++++++++++++ show $msg")
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

    protected fun execShellCmd(cmd : String){
        var process : Process = Runtime.getRuntime().exec("su")
        var outputStream : OutputStream = process.outputStream
        var dataOutputStream : DataOutputStream = DataOutputStream(outputStream)
        dataOutputStream.writeBytes(cmd)
        dataOutputStream.flush()
        dataOutputStream.close()
        outputStream.close()
    }

    protected fun openActivity(cls : Class<*>){
        var intent = Intent(this , cls)
        startActivity(intent)
    }

}