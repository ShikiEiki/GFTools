package com.gftools

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.IBinder
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
    lateinit var mMediaProjectionManager : MediaProjectionManager

    lateinit var binding : ActivityMainBinding

    lateinit var mainService : MainService

    var serviceConnection = object : ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
            showToastAndLv("绑定服务失败")
            finish()
        }
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is MainService.MyBinder){
                showToastAndLv("绑定服务成功")
                mainService = service.getService()
                mainService.mainActivity = this@MainActivity
                mainService.setStatusListener(object : MainService.OnStatusChangedListener{
                    override fun onStatusChanged(status: Int) {
                        showToastAndLv("onStatusCHanged " + status)
                        mHandler.post(object : Runnable {
                            override fun run() {
                                showToastAndLv("here" + status)
                                when(status){
                                    0 -> {
                                        binding.mainBtn.text = "开始脚本"
                                    }
                                    1 -> {
                                        binding.mainBtn.text = "停止脚本"
                                    }
                                }
                            }
                        })
                    }
                    override fun reportCurrentAction(action: String) {
                    }
                })
                return
            }
            showToastAndLv("绑定未知的服务")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityInstance = this
        binding = DataBindingUtil.inflate<ActivityMainBinding>(LayoutInflater.from(this) , R.layout.activity_main , null , false)
        setContentView(binding.root)
        binding.mainBtn.text = "开始脚本"
        bindService(Intent(this , MainService::class.java) , serviceConnection , Context.BIND_AUTO_CREATE)
        sendCaptureIntent(10086)
    }

    fun onMainBtnClick(view: View){
        if (binding.mainBtn.text.equals("开始脚本")){
            startScript()
        }
        else if (binding.mainBtn.text.equals("停止脚本")){
            stopScript()
        }
    }

    fun startScript(){
        mainService.startScript()
    }

    fun stopScript(){
        mainService.stopScript()
    }

    fun onConfigBtnClick(view: View){
        openActivity(ConfigActivity::class.java)
    }

    fun onDebugBtn1Click(view: View){
    }









    fun onDebugBtn2Click(view: View){

    }
    lateinit var process : Process
    lateinit var dataOutputStream : DataOutputStream
    fun onDebugBtn3Click(view: View){
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
        var bitmapCompare = BaseBitmapCompare(startCapture())
        bitmapCompare.isCorrectTeamSelected(3)
    }

    fun sendCaptureIntent(requestCode: Int) {
        lv("sendCaptureIntent")
        mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent()  , requestCode)
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
//                when (requestCode) {
//                    10086 -> {
//                        var intent = Intent(this@MainActivity, MainService::class.java)
//                        intent.putExtra("function", "start")
//                        startService(intent)
//                    }
//                    10010 -> {
//                        test()
//                    }
//                }
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
