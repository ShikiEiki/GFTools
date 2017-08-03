package com.frank.ktdemo

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
import com.frank.ktdemo.databinding.ActivityMainBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : BaseActivity() {
    enum class UI{
        REPAIRE,
        FACTORY,
        CHOOSE_COMBAT,
        MAIN
    }
    lateinit var mMediaProjectionManager : MediaProjectionManager
    lateinit var mMediaProjection : MediaProjection
    lateinit var mVirturlDisplay : VirtualDisplay
    lateinit var mImgReader : ImageReader
    lateinit var binding : ActivityMainBinding
    var ready : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate<ActivityMainBinding>(LayoutInflater.from(this) , R.layout.activity_main , null , false)
        setContentView(binding.root)
    }

    fun onBtn1Click(view: View){
        object : Thread(){
            override fun run() {
                showToastAndLv("开始程序")
                waitSecond(2)
                ready = false
                sendCaptureIntent()
                waitTillReady()
                showToastAndLv("回Home")
                goHome()
                waitSecond(10)
//                mHandler.post { binding.imgview.setImageBitmap(startCapture()) }

                //--------------过闪屏---------------
                handleSplashUI()

                //---------------过登录进入游戏--------------
                handleLoginUI()

                //---------------进入主界面--------------
                showToastAndLv("进入主界面等待15秒")
                waitSecond(15)
                handleMainUI(UI.REPAIRE)

                //---------------进入维修界面----------------
                handleRepairUI()


                showToastAndLv("完毕")
            }
        }.start()
    }

    fun handleLoginUI(){
        var hasClick = false
        while (true){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BitMapCompare(startCapture())
            if (bitMapCompare.isLoginUI()){
                showToastAndLv("已经进入登录画面,点击进入游戏")
                tap(500 , 680)
                hasClick = true
                waitSecond(5)
            }
            else {
                if (hasClick){
                    return
                }
                else {
                    showToastAndLv("未知界面")
                    waitSecond(5)
                }
            }
        }
    }
    fun handleSplashUI(){
        var hasClick = false
        while (true){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BitMapCompare(startCapture())
            if (bitMapCompare.isSplashUI()){
                var (r , g , b) = bitMapCompare.getPixelRGB(1090 , 907)
                if (r > 100){
                    showToastAndLv("已经进入游戏闪屏,点击画面空白处")
                    tap(1472 , 912)
                    hasClick = true
                    waitSecond(5)
                }
                else {
                    showToastAndLv("已经进入游戏闪屏画面,但是游戏还未准备完成")
                    waitSecond(5)
                }
            }
            else {
                if (hasClick){
                    return
                }
                else {
                    showToastAndLv("未知画面")
                    waitSecond(5)
                }
            }
        }
    }
    fun handleRepairUI(){
        var hasFinishRepair = false
        while (true){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BitMapCompare(startCapture())
            if (bitMapCompare.isMainUI()) {
                if (hasFinishRepair){
                    return
                }
                else {
                    showToastAndLv("还停留在主界面,点击维修")
                    tap(1373, 368)
                    waitSecond(5)
                }
            }
            else if (bitMapCompare.isRepairUI()){
                if (bitMapCompare.hasRepairBlank()){
                    showToastAndLv("有维修空位,点击空位")
                    tap(1313 , 463)
                    waitSecond(2)
                    while (!handleRepairChoose()){
                        waitSecond(3)
                    }
                    showToastAndLv("所有角色维修完毕,点击返回")
                    tap(78 , 72)
                    hasFinishRepair = true
                    waitSecond(10)
                }
                else {
                    showToastAndLv("没有维修空位了,等待10s")
                    waitSecond(10)
                }
            }
            else {
                showToastAndLv("未知界面")
                waitSecond(5)
            }
        }
    }

    fun handleRepairChoose() : Boolean{
        var noNeedRepair = false
        while (true){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BitMapCompare(startCapture())
            if (bitMapCompare.isRepairUI()){
                if (noNeedRepair){
                    return true
                }
                else {
                    showToastAndLv("还停留在维修主界面,点击空位")
                    tap(1313, 463)
                    waitSecond(2)
                }
            }
            else if (bitMapCompare.isDialog()){
                showToastAndLv("发现对话框,推断是没有需要修复的角色的提示框,点击确定")
                tap(963 , 778)
                waitSecond(2)
                noNeedRepair = true
            }
            else if (bitMapCompare.isRepaireChooseUI()){
                showToastAndLv("是维修角色选择对话框")
                if (bitMapCompare.isFirstCharactorNeedRepare()){
                    showToastAndLv("第一个角色需要维修,点击第一个角色")
                    tap(135 , 341)
                    waitSecond(2)
                    showToastAndLv("点击右下角确定按钮")
                    tap(1871 , 967)
                    waitSecond(2)
                    handleQuickRepair()
                    return false
                }
                else {
                    showToastAndLv("没有需要维修的角色,点击取消")
                    tap(91 , 73)
                    waitSecond(2)
                    return true
                }
            }
            else {
                showToastAndLv("未知界面")
                waitSecond(5)
            }
        }
    }

    fun handleQuickRepair(){
        while (true){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BitMapCompare(startCapture())
            when(bitMapCompare.isQuickRepairConfirmDialog()){
                1 -> {
                    showToastAndLv("是快修确定窗口,快修未选中,点击快修")
                    tap(469 , 760)
                    waitSecond(2)
                }
                2 -> {
                    showToastAndLv("是快修确定窗口,快修已选中,点击确定")
                    tap(1398 , 759)
                    waitSecond(3)
                    while (true){
                        bitMapCompare = BitMapCompare(startCapture())
                        if (bitMapCompare.isDialog()){
                            showToastAndLv("快修完成dialog,点击确定")
                            tap(981 , 786)
                        }
                        else {
                            showToastAndLv("未知界面")
                            waitSecond(5)
                        }
                    }
                }
                0 -> {
                    if (bitMapCompare.isRepairUI()){
                        showToastAndLv("是维修主界面")
                        return
                    }
                    else {
                        showToastAndLv("未知界面")
                        waitSecond(5)
                    }
                }
            }
        }
    }

    fun handleMainUI(goto : UI){
        while (true){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BitMapCompare(startCapture())
            if (bitMapCompare.isExpeditionFinishUI()){
                showToastAndLv("远征完成,点击空白处")
                tap(1670 , 914)
                waitSecond(3)
            }
            else if (bitMapCompare.isGoExpeditionAgainUI()){
                showToastAndLv("确定是否要继续远征,点击确定")
                tap(1111 , 782)
                waitSecond(3)
            }
            else if (bitMapCompare.isMainUI()){
                when (goto){
                    UI.REPAIRE -> {
                        showToastAndLv("主界面,点击维修")
                        tap(1373 , 368)
                        waitSecond(5)
                    }
                    UI.FACTORY -> {
                        //TODO
                    }
                    UI.CHOOSE_COMBAT -> {
                        //TODO
                    }
                }
                break
            }
            else {
                showToastAndLv("未知界面")
                waitSecond(5)
            }
        }
    }

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


    fun tap(x: Int , y: Int){
        lv("点击 ($x , $y)")
        execShellCmd("input tap $x $y")
    }
    fun onBtn2Click(view: View){
        openActivity(GetClickPositionActivity::class.java)
        finish()
    }
    fun onBtn3Click(view: View){
        var f = File("/sdcard/fhhhhhh/fhfh.jpg")
        f.mkdirs()
    }
    fun onBtn4Click(view: View){
        ready = true
    }

    fun goHome(){
        val mHomeIntent = Intent(Intent.ACTION_MAIN)
        mHomeIntent.addCategory(Intent.CATEGORY_HOME)
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        startActivity(mHomeIntent)
    }

    private fun waitTillReady(){
        while (!ready){
            showToastAndLv("check ready ============ [not ready] ========== wait 1s")
            Thread.sleep(1000)
        }
        showToastAndLv("check ready =========== [ready] =========== continue")
    }

    private fun waitSecond(time : Int){
        var past = 0
        while (past < time){
            Thread.sleep(1000)
            past++
            showToastAndLv("wait $past s" + (if (past==time) " wait end " else ""))
        }
    }

    private fun showToastAndLv(msg : String){
        lv(msg)
        showToast(msg)
    }

    private fun sendCaptureIntent() {
        lv("sendCaptureIntent")
        mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent()  , 10086)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        lv("onActivityResult requestCode : " + requestCode + " resultCode : " + resultCode + " data" + data)
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 10086 && resultCode != Activity.RESULT_OK){
            lv("获取授权错误,错误码 :　" + resultCode)
            showToast("获取授权错误,错误码 :　" + resultCode)
            return
        }
        mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode , data)
        setUpVirturlDisplay()
        lv("开始录屏成功 ready = true")
        ready = true
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

    private fun startCapture() : Bitmap{
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


    inner class BitMapCompare(bitmap : Bitmap){
        var mBitMap : Bitmap
        init {
            mBitMap = bitmap
            saveMyBitmap(mBitMap , "" + System.currentTimeMillis())
        }
        fun isPiexlEqual(x : Int , y : Int , r : Int , g : Int, b : Int) : Boolean{
            var (pr , pg , pb) = getPixelRGB(x , y)
            lv("对比像素颜色:点坐标[$x , $y] 图中颜色 ($pr , $pg , $pb) 期望颜色 ($r , $g , $b)")
            if (pr == r && pg == g && pb == b){
                lv("对比像素坐标[$x , $y]与期望一致,返回true")
                return true
            }
            lv("对比像素坐标[$x , $y]与期望不同,返回false")
            return false
        }

        fun getPixelRGB(x : Int , y : Int) : IntArray{
            lv("try to get piexl RGB x:$x y:$y bitmap width:${mBitMap.width} height:${mBitMap.height}")
            var returnArray = IntArray(3)
            var pixel = mBitMap.getPixel(x, y)
            returnArray[0] = pixel.and(0x00ff0000) shr 16
            returnArray[1] = pixel.and(0x0000ff00) shr 8
            returnArray[2] = pixel.and(0x000000ff)
            lv("get piexl RGB success : R==${returnArray[0]} G==${returnArray[1]} B==${returnArray[2]}")
            return returnArray
        }

        fun isSplashUI() : Boolean{
            if (isPiexlEqual(1500 , 995 , 255 , 186 , 0)
                    && isPiexlEqual(1100 , 995 , 255 , 186 , 0)
                    && isPiexlEqual(800 , 995 , 255 , 186 , 0)
                    && isPiexlEqual(500 , 995 , 255 , 186 , 0)
                    && isPiexlEqual(200 , 995 , 255 , 186 , 0)
                    && isPiexlEqual(1700 , 995 , 255 , 186 , 0)
                    ){
                lv("是闪屏界面")
                return true
            }
            lv("不是不是不是不是闪屏界面")
            return false
        }

        fun isLoginUI() : Boolean{
            if (isPiexlEqual(560 , 680 , 255 , 190 , 0)
                    && isPiexlEqual(500 , 680 , 255 , 190 , 0)
                    && isPiexlEqual(406 , 656 , 255 , 255 , 255)
                    && isPiexlEqual(306 , 655 , 255 , 255 , 255)
                    && isPiexlEqual(237 , 658 , 255 , 255 , 255)
                    ){
                lv("是登录界面")
                return true
            }
            else {
                lv("不是不是不是不是登录界面")
                return false
            }
        }

        fun isQuickRepairConfirmDialog() : Int{
            if (isPiexlEqual(1340 , 460 , 255 , 255 , 255)
                    && isPiexlEqual(1277 , 549 , 255 , 255 , 255)
                    && isPiexlEqual(1455 , 545 , 255 , 255 , 255)
                    && isPiexlEqual(1128 , 720 , 255 , 255 , 255)
                    && isPiexlEqual(484 , 382 , 255 , 255 , 255)
                    ){
                if (isPiexlEqual(514 , 739 , 255 , 255 , 255)
                        && isPiexlEqual(512 , 794 , 255 , 255 , 255)){
                    lv("是快修确定dialog , 并且快修已选中")
                    return 2
                }
                else {
                    lv("是快修确定dialog , 并且快修未选中")
                    return 1
                }
            }
            else {
                lv("不是不是不是不是不是快修确定dialog")
                return 0
            }
        }

        fun isFirstCharactorNeedRepare() : Boolean{
            if (isPiexlEqual(221 , 210 , 255 , 85 , 16)
                    && isPiexlEqual(221 , 234 , 255 , 85 , 16)
                    && isPiexlEqual(226 , 252 , 255 , 85 , 16)
                    && isPiexlEqual(117 , 230 , 255 , 85 , 16)
                    && isPiexlEqual(27 , 230 , 255 , 85 , 16)
                    ){
                lv("第一个角色需要维修")
                return true
            }
            else {
                lv("第一个角色不不不不不不不需要维修")
                return false
            }
        }
        fun isRepaireChooseUI() : Boolean{
            if (isPiexlEqual(1814 , 232 , 255 , 255 , 255)
                    && isPiexlEqual(1899 , 272 , 255 , 255 , 255)
                    && isPiexlEqual(1824 , 384 , 255 , 255 , 255)
                    && isPiexlEqual(1896 , 446 , 255 , 255 , 255)
                    && isPiexlEqual(1700 , 914 , 255 , 186 , 0)
                    && isPiexlEqual(1832 , 911 , 255 , 186 , 0)
                    ){
                lv("是维修角色选择页")
                return true
            }
            else {
                lv("不是不是不是不是维修角色选择页")
                return false
            }
        }
        fun isDialog() : Boolean{
            if (isPiexlEqual(850 , 743 , 255 , 255 , 255)
                    && isPiexlEqual(957 , 743 , 255 , 255 , 255)
                    && isPiexlEqual(1065 , 743 , 255 , 255 , 255)
                    ){
                lv("是对话框")
                return true
            }
            else {
                lv("不是不是不是不是对话框")
                return false
            }
        }
        fun hasRepairBlank() : Boolean {
            if (isPiexlEqual(1314 , 437 , 255 , 255 , 255)
                    && isPiexlEqual(1313 , 489 , 255 , 255 , 255)
                    && isPiexlEqual(1260 , 409 , 255 , 255 , 255)
                    && isPiexlEqual(1282 , 460 , 255 , 255 , 255)
                    && isPiexlEqual(1313 , 463 , 255 , 255 , 255)
                    ){
                lv("有维修空位")
                return true
            }
            else {
                lv("没有没有没有维修空位")
                return false
            }
        }

        fun isRepairUI() : Boolean{
            if (isPiexlEqual(320 , 35 , 255 , 255 , 255)
                    && isPiexlEqual(344 , 62 , 255 , 255 , 255)
                    && isPiexlEqual(301 , 93 , 255 , 255 , 255)
                    && isPiexlEqual(298 , 117 , 255 , 255 , 255)
                    && isPiexlEqual(301 , 74 , 255 , 255 , 255)
                    ){
                lv("是维修界面")
                return true
            }
            else {
                lv("不是不是不是不是维修界面")
                return false
            }
        }
        fun isRedDotInRepairBtnInMainUI() : Boolean{
            if (isPiexlEqual(1527 , 267 , 255 , 255 , 255)
                    && isPiexlEqual(1527, 291, 255 , 255 , 255)
                    ){
                lv("主界面维修按钮上有红点")
                return true
            }
            else {
                lv("主界面维修按钮上没有红点")
                return false
            }
        }
        fun isMainUI() : Boolean{
            if (isPiexlEqual(1672 , 1032 , 255 , 255 , 255)
                    && isPiexlEqual(1248 , 1009 , 255 , 255 , 255)
                    && isPiexlEqual(1675 , 320 , 255 , 255 , 255)
                    && isPiexlEqual(1276 , 505 , 239 , 239 , 239)
                    && isPiexlEqual(1675 , 320 , 255 , 255 , 255)
                    ){
                lv("是主界面")
                return true
            }
            else {
                lv("不是不是不是不是主界面")
                return false
            }
        }

        fun isExpeditionFinishUI() : Boolean{
            if (isPiexlEqual(171 , 511 , 255 , 255 , 255)
                    && isPiexlEqual(173 , 595 , 255 , 255 , 255)
                    && isPiexlEqual(444 , 588 , 255 , 255 , 255)
                    && isPiexlEqual(443 , 505 , 255 , 255 , 255)
                    && isPiexlEqual(292 , 422 , 255 , 255 , 255)
                    ){
                lv("是远征结束界面")
                return true
            }
            else {
                lv("不是不是不是不是远征结束界面")
                return false
            }
        }

        fun isGoExpeditionAgainUI() : Boolean{
            if (isPiexlEqual(686 , 705 , 255 , 255 , 255)
                    && isPiexlEqual(903 , 710 , 255 , 255 , 255)
                    && isPiexlEqual(734 , 439 , 255 , 255 , 255)
                    && isPiexlEqual(1003 , 437 , 255 , 255 , 255)
                    && isPiexlEqual(1226 , 451 , 255 , 255 , 255)
                    ){
                lv("是再次远征界面")
                return true
            }
            else {
                lv("不是不是不是不是再次远征界面")
                return false
            }
        }
    }
}
