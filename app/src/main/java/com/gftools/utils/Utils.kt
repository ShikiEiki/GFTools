package com.gftools.utils

import android.graphics.Bitmap
import android.util.Log
import java.io.*

/**
 * Created by FH on 2017/8/10.
 */

fun saveMyBitmap(mBitmap: Bitmap, bitName: String) {
    var f = File("/sdcard/fh_debug")
    if (!f.exists()){
        f.mkdirs()
    }
    f = File("/sdcard/fh_debug/$bitName.jpg")
    if (!f.exists()){
        f.createNewFile()
    }
    var fOut: FileOutputStream? = null
    try {
        fOut = FileOutputStream(f)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
    mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
    try {
        fOut!!.flush()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    try {
        fOut!!.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun lv(msg : String){
    Log.v("FH" , msg)
}

fun execShellCmd(cmd : String){
    var process : Process = Runtime.getRuntime().exec("su")
    var outputStream : OutputStream = process.outputStream
    var dataOutputStream : DataOutputStream = DataOutputStream(outputStream)
    dataOutputStream.writeBytes(cmd)
    dataOutputStream.flush()
    dataOutputStream.close()
    outputStream.close()
}
