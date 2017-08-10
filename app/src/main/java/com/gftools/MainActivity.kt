package com.gftools

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import com.gftools.databinding.ActivityMainBinding
import com.gftools.utils.*
import java.io.*

class MainActivity : BaseActivity() {
    companion object Static{
        lateinit var mMediaProjection : MediaProjection
        lateinit var mVirturlDisplay : VirtualDisplay
        lateinit var mImgReader : ImageReader
        lateinit var activityInstance : MainActivity
        fun startCapture() : Bitmap{
            var image : Image = mImgReader.acquireLatestImage()
            var pixelStride = image.planes[0].pixelStride
            var rowStride = image.planes[0].rowStride
            var rowPadding = rowStride - pixelStride * image.width
            var mBitmap = Bitmap.createBitmap(image.width + rowPadding / pixelStride , image.height , Bitmap.Config.ARGB_8888)
            mBitmap.copyPixelsFromBuffer(image.planes[0].buffer)
            mBitmap = Bitmap.createBitmap(mBitmap , 0 , 0 , image.width , image.height)
            image.close()
            return mBitmap
        }
    }
    enum class UI{
        REPAIRE,
        FACTORY,
        CHOOSE_COMBAT,
        MAIN
    }
    lateinit var mMediaProjectionManager : MediaProjectionManager

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityInstance = this
        binding = DataBindingUtil.inflate<ActivityMainBinding>(LayoutInflater.from(this) , R.layout.activity_main , null , false)
        setContentView(binding.root)
    }

    fun onBtn1Click(view: View?){
        sendCaptureIntent(10086)
    }

    fun onBtn2Click(view: View){
    }
    fun onBtn3Click(view: View){
        sendCaptureIntent(10010)
    }
    lateinit var process : Process
    lateinit var dataOutputStream : DataOutputStream
    fun onBtn4Click(view: View){
        process = Runtime.getRuntime().exec("su")
        dataOutputStream = DataOutputStream(process.outputStream)

        object : Thread(){
            override fun run() {
            }
        }
                .start()
    }

    fun test(){
        launchGF()
        waitSecond(5)
        var bitmapCompare = BitmapCompare(startCapture())
        bitmapCompare.isGunTobeDisassembleChooseUI()
    }

    fun sendCaptureIntent(requestCode: Int) {
        lv("sendCaptureIntent")
        mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent()  , 10086)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        lv("onActivityResult requestCode : " + requestCode + " resultCode : " + resultCode + " data" + data)
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            10086,10010 -> {
                if (resultCode != Activity.RESULT_OK){
                    lv("获取授权错误,错误码 :　" + resultCode)
                    showToast("获取授权错误,错误码 :　" + resultCode)
                    return
                }
                mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data)
                setUpVirturlDisplay()
                lv("开始录屏成功")
                when (requestCode) {
                    10086 -> {
                        var intent = Intent(this@MainActivity, MainService::class.java)
                        intent.putExtra("function", "start")
                        startService(intent)
                    }
                    10010 -> {
                        test()
                    }
                }
            }
        }
    }

    private fun setUpVirturlDisplay(){
        var metrics : DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        var mScreenWidth = metrics.widthPixels
        var mScreenHeight = metrics.heightPixels
        var mScreenDensity = metrics.densityDpi
        mImgReader = ImageReader.newInstance(mScreenWidth , mScreenHeight , 0x1 , 2)
        mVirturlDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture"
                , mScreenWidth , mScreenHeight , mScreenDensity
                , DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR , mImgReader.surface , null , null)
    }


}
