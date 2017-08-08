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
import com.gftools.R
import com.gftools.databinding.ActivityMainBinding
import java.io.*


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
                //----------------开始判断是什么界面-----------------
                handleProgramStart()

                //---------------进入主界面--------------
                while (true){
                    //---------------进入维修界面,完毕后回到主界面----------------
                    handleMainUI(UI.REPAIRE)

                    //---------------进入战斗选择界面---------------------------
                    if (handleMainUI(UI.CHOOSE_COMBAT).equals("full")){
                        showToastAndLv("仓库已满,等待300s")
                        waitSecond(300)
                    }
                }

                showToastAndLv("完毕")
            }
        }.start()
    }

    fun handleProgramStart() {
        while (true) {
            showToastAndLv("判断当前界面")
            var bitMapCompare = BitMapCompare(startCapture())
            if (bitMapCompare.isSplashUI()){
                handleSplashUI()
                handleLoginUI()
                showToastAndLv("进入主界面等待15秒")
                waitSecond(15)
                bitMapCompare.recycle()
                return
            }
            else if (bitMapCompare.isMainUI()){
                bitMapCompare.recycle()
                return
            }
            else {
                if (!handleExpeditionFinishUI(bitMapCompare)){
                    showToastAndLv("未知界面,请进入游戏")
                    waitSecond(5)
                }
            }
            bitMapCompare.recycle()
        }
    }

    fun handleExpeditionFinishUI(bitMapCompare: BitMapCompare) : Boolean{
        if (bitMapCompare.isExpeditionFinishUI()){
            showToastAndLv("远征完成,点击空白处")
            saveTap(1670 , 914 , 200 , 200)
            waitSecond(3)
            bitMapCompare.recycle()
            return true
        }
        else if (bitMapCompare.isGoExpeditionAgainUI()){
            showToastAndLv("确定是否要继续远征,点击确定")
            saveTap(1111 , 782)
            waitSecond(3)
            bitMapCompare.recycle()
            return true
        }
        bitMapCompare.recycle()
        return false
    }

    fun handleChooseCombatUI() : Boolean{
        var noMoreSpace = false
        while (true){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BitMapCompare(startCapture())
            if (bitMapCompare.isCombatChooseUI()){
                if (noMoreSpace){
                    showToastAndLv("仓库已满,点击返回")
                    saveTap(99 , 81)
                    waitSecond(10)
                }
                else if (bitMapCompare.isCorrectZHANYIChoosed()){
                    showToastAndLv("已经选择了正确的战役,点击5-2")
                    saveTap(1338 , 602)
                    waitSecond(2)
                }
                else {
                    showToastAndLv("还未选择正确的战役,滑动战役条,并且点击紧急")
                    saveDrag(403 , 912 , 383 , 238)
                    waitSecond(1)
                    saveDrag(403 , 912 , 383 , 238)
                    waitSecond(2)
                    saveTap(344 , 479)
                    waitSecond(2)
                    saveTap(1651 , 282)
                    waitSecond(2)
                }
            }
            else if (bitMapCompare.isCombatConfirmDialog()){
                if (noMoreSpace){
                    showToastAndLv("仓库已满,点击返回")
                    saveTap(353 , 139)
                    waitSecond(2)
                }
                else {
                    showToastAndLv("战斗选择确认对话框,点击普通作战")
                    saveTap(1127 , 875)
                    waitSecond(2)
                }
            }
            else if (bitMapCompare.isDialog()){
                showToastAndLv("发现对话框,推断是仓库已满,点击确定")
                noMoreSpace = true
                saveTap(962 , 778)
                waitSecond(2)
            }
            else if (bitMapCompare.isMainUI() && noMoreSpace){
                bitMapCompare.recycle()
                return false
            }
            else if (bitMapCompare.isPreCombatUI()){
                handleArmyDeploy()
                bitMapCompare.recycle()
                return true
            }
            else {
                if (!handleExpeditionFinishUI(bitMapCompare)){
                    showToastAndLv("未知界面")
                    waitSecond(5)
                }
            }
            bitMapCompare.recycle()
        }
    }

    fun handleArmyDeploy(){
        while (true){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BitMapCompare(startCapture())
            if (bitMapCompare.isPreCombatUI()){
                if (!bitMapCompare.isViewAtLocation1()){
                    showToastAndLv("当前视角不在位置1,调整视角")
                    saveDrag(1500 , 300 , 800 , 600 , 500)
                    waitSecond(2)
                    saveDrag(1500 , 300 , 800 , 600 , 500)
                    waitSecond(3)
                }
                else {
                    when (bitMapCompare.isCharactorAtLocation1()){
                        0 -> {
                            showToastAndLv("发现初始点上没有部署部队,点击部署按钮")
                            saveTap(1241 , 517)
                            waitSecond(3)
                        }
                        else -> {
                            showToastAndLv("初始点上已经部署完毕,点击开始作战")
                            saveTap(1735 , 985)
                            waitSecond(3)
                        }
                    }
                }
            }
            else if (bitMapCompare.isBattleUI()){
                handleBattleUI()
                bitMapCompare.recycle()
                return
            }
            else {
                when (bitMapCompare.isCharactorSetupChooseUI()){
                    1 -> {
                        showToastAndLv("选择梯队界面,第4梯队未被选中,点击第4梯队")
                        saveTap(88 , 694)
                        waitSecond(2)
                    }
                    2 -> {
                        showToastAndLv("选择梯队界面,第4梯队已经选中,点击部署")
                        saveTap(1763 , 962)
                        waitSecond(3)
                    }
                    else -> {
                        showToastAndLv("未知界面,wait 5s")
                        waitSecond(5)
                    }
                }
            }
            bitMapCompare.recycle()
        }
    }

    fun handleBattleUI(){
        var stage = 1
        while (true){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BitMapCompare(startCapture())
            if (bitMapCompare.isBattleUI()){
                if (stage < 4){
                    if (bitMapCompare.isRamdomEventDialog()){
                        showToastAndLv("发现随机事件,点击画面中间任意点取消dialog")
                        saveTap(895 , 575 , 200 , 200)
                        waitSecond(20)
                    }
                    else if (!bitMapCompare.isViewAtLocation1()){
                        showToastAndLv("当前视角不在位置1,调整视角")
                        saveDrag(1500 , 300 , 800 , 600 , 500)
                        waitSecond(2)
                        saveDrag(1500 , 300 , 800 , 600 , 500)
                        waitSecond(3)
                    }
                    else if (stage == 1){
                        when (bitMapCompare.isCharactorAtLocation1()){
                            0 -> {
                                when (bitMapCompare.isCharactorAtLocation2()){
                                    1,2 -> {
                                        stage = 2
                                        waitSecond(1)
                                    }
                                    0 -> {
                                        showToastAndLv("在stage == 1时初始点上没有部队,错误,无法处理,等待10s")
                                        waitSecond(300)
                                    }
                                }
                            }
                            1 -> {
                                showToastAndLv("初始点上的部队没有被选中,点击初始点以便选中")
                                saveTap(1222 , 509)
                                waitSecond(3)
                            }
                            2 -> {
                                showToastAndLv("初始点上的部队已经被选中,点击2号点以便行进至2号点")
                                saveTap(616 , 419)
                                waitSecond(20)
                            }
                        }
                    }
                    else if (stage == 2){
                        when (bitMapCompare.isCharactorAtLocation2()){
                            0 -> {
                                when (bitMapCompare.isCharactorAtLocation3()){
                                    1,2 -> {
                                        stage = 3
                                        waitSecond(1)
                                    }
                                    0 -> {
                                        showToastAndLv("在stage == 2时2号线上没有部队,错误,无法处理,等待10s")
                                        waitSecond(300)
                                    }
                                }
                            }
                            1 -> {
                                showToastAndLv("2号点上的部队没有被选中,点击2号点以便选中")
                                saveTap(616 , 419)
                                waitSecond(3)
                            }
                            2 -> {
                                showToastAndLv("2号点上的部队已经被选中,点击3号点以便行进至3号点")
                                saveTap(33 , 511)
                                waitSecond(5)
                            }
                        }
                    }
                    else if (stage == 3){
                        when (bitMapCompare.isCharactorAtLocation3()) {
                            0 -> {
                                if (bitMapCompare.isCharactorAtLocation4_View1()) {
                                    stage = 4
                                    waitSecond(1)
                                } else {
                                    showToastAndLv("在stage == 3时3号点上没有部队,错误,无法处理,等待10s")
                                    waitSecond(300)
                                }
                            }
                            1 -> {
                                showToastAndLv("3号点上的部队没有被选中,点击3号点以便选中")
                                saveTap(33, 511)
                                waitSecond(3)
                            }
                            2 -> {
                                showToastAndLv("3号点上的部队已经被选中,点击4号点以便行进至4号点")
                                saveTap(55, 895)
                                waitSecond(5)
                            }
                        }
                    }
                }
                if (stage == 4){
                    if (bitMapCompare.isViewAtLocation2()){
                        showToastAndLv("stage == 4时,视角不在view2,调整视角")
                        saveDrag(1000 , 300 , 400 , 800 , 1000)
                        waitSecond(2)
                        saveDrag(1000 , 300 , 400 , 800 , 1000)
                        waitSecond(2)
                        saveDrag(1200 , 900 , 1100 , 300 , 2000)
                        waitSecond(4)
                    }
                    else {
                        when (bitMapCompare.isCharactorAtLocation4_View2()){
                            0 -> {
                                if (bitMapCompare.isCharactorAtLocation5()){
                                    stage = 5
                                    waitSecond(1)
                                }
                                else {
                                    showToastAndLv("stage == 4时,判断部队不在4号点也不在5号点,可能是视角调整有误差,重新调整视角")
                                    saveDrag(1000 , 300 , 400 , 800 , 1000)
                                    waitSecond(2)
                                    saveDrag(1000 , 300 , 400 , 800 , 1000)
                                    waitSecond(2)
                                    saveDrag(1200 , 900 , 1200 , 300 , 2000)
                                    waitSecond(4)
                                }
                            }
                            1 -> {
                                showToastAndLv("4号点上的部队未选中,点击4号点以便选中")
                                saveTap(57 , 347)
                                waitSecond(3)
                            }
                            2 -> {
                                showToastAndLv("4号点上的部队已经被选中,点击5号点以便行进至5号点")
                                saveTap(44 , 698)
                                waitSecond(20)
                            }
                        }
                    }
                }
                if (stage == 5){
                    showToastAndLv("部队已经行进至5号点点击结束回合")
                    saveTap(1759 , 990)
                    waitSecond(10)
                }
            }
            else if (bitMapCompare.isMainUI()){
                bitMapCompare.recycle()
                return
            }
            else {
                if (!handleExpeditionFinishUI(bitMapCompare)){
                    showToastAndLv("未知界面,可能是战斗界面或者结算界面或者获得新枪界面等等,点击屏幕中心")
                    saveTap(895 , 575 , 200 , 200)
                    waitSecond(5)
                }
            }
            bitMapCompare.recycle()
        }
    }
    fun handleLoginUI(){
        var hasClick = false
        while (true){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BitMapCompare(startCapture())
            if (bitMapCompare.isLoginUI()){
                showToastAndLv("已经进入登录画面,点击进入游戏")
                saveTap(500 , 680 , 200, 200)
                hasClick = true
                waitSecond(5)
            }
            else {
                if (hasClick){
                    bitMapCompare.recycle()
                    return
                }
                else {
                    showToastAndLv("未知界面")
                    waitSecond(5)
                }
            }
            bitMapCompare.recycle()
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
                    saveTap(1472 , 912 , 200 , 200)
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
                    bitMapCompare.recycle()
                    return
                }
                else {
                    showToastAndLv("未知画面")
                    waitSecond(5)
                }
            }
            bitMapCompare.recycle()
        }
    }
    fun handleRepairUI(){
        var hasFinishRepair = false
        while (true){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BitMapCompare(startCapture())
            if (bitMapCompare.isMainUI()) {
                if (hasFinishRepair){
                    bitMapCompare.recycle()
                    return
                }
                else {
                    showToastAndLv("还停留在主界面,点击维修")
                    saveTap(1373, 368)
                    waitSecond(5)
                }
            }
            else if (bitMapCompare.isRepairUI()){
                if (bitMapCompare.hasRepairBlank()){
                    showToastAndLv("有维修空位,点击空位")
                    saveTap(1313 , 463)
                    waitSecond(2)
                    while (!handleRepairChoose()){
                        waitSecond(3)
                    }
                    showToastAndLv("所有角色维修完毕,点击返回")
                    saveTap(78 , 72)
                    hasFinishRepair = true
                    waitSecond(10)
                }
                else {
                    showToastAndLv("没有维修空位了,等待10s")
                    waitSecond(10)
                }
            }
            else {
                if (!handleExpeditionFinishUI(bitMapCompare)){
                    showToastAndLv("未知界面")
                    waitSecond(5)
                }
            }
            bitMapCompare.recycle()
        }
    }

    fun handleRepairChoose() : Boolean{
        var noNeedRepair = false
        while (true){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BitMapCompare(startCapture())
            if (bitMapCompare.isRepairUI()){
                if (noNeedRepair){
                    bitMapCompare.recycle()
                    return true
                }
                else {
                    showToastAndLv("还停留在维修主界面,点击空位")
                    saveTap(1313, 463)
                    waitSecond(2)
                }
            }
            else if (bitMapCompare.isDialog()){
                showToastAndLv("发现对话框,推断是没有需要修复的角色的提示框,点击确定")
                saveTap(963 , 778)
                waitSecond(2)
                noNeedRepair = true
            }
            else if (bitMapCompare.isRepaireChooseUI()){
                showToastAndLv("是维修角色选择对话框")
                if (bitMapCompare.isFirstCharactorNeedRepare()){
                    showToastAndLv("第一个角色需要维修,点击第一个角色")
                    saveTap(135 , 341)
                    waitSecond(2)
                    showToastAndLv("点击右下角确定按钮")
                    saveTap(1871 , 967)
                    waitSecond(2)
                    handleQuickRepair()
                    bitMapCompare.recycle()
                    return false
                }
                else {
                    showToastAndLv("没有需要维修的角色,点击取消")
                    saveTap(91 , 73)
                    waitSecond(2)
                    bitMapCompare.recycle()
                    return true
                }
            }
            else {
                if (!handleExpeditionFinishUI(bitMapCompare)){
                    showToastAndLv("未知界面")
                    waitSecond(5)
                }
            }
            bitMapCompare.recycle()
        }
    }

    fun handleQuickRepair(){
        while (true){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BitMapCompare(startCapture())
            when(bitMapCompare.isQuickRepairConfirmDialog()){
                1 -> {
                    showToastAndLv("是快修确定窗口,快修未选中,点击快修")
                    saveTap(469 , 760)
                    waitSecond(2)
                }
                2 -> {
                    showToastAndLv("是快修确定窗口,快修已选中,点击确定")
                    saveTap(1398 , 759)
                    waitSecond(3)
                }
                0 -> {
                    if (bitMapCompare.isRepairUI()){
                        showToastAndLv("是维修主界面")
                        bitMapCompare.recycle()
                        return
                    }
                    else if (bitMapCompare.isDialog()){
                        showToastAndLv("快修完成dialog,点击确定")
                        saveTap(981 , 786)
                        waitSecond(2)
                    }
                    else {
                        if (!handleExpeditionFinishUI(bitMapCompare)){
                            showToastAndLv("未知界面")
                            waitSecond(5)
                        }
                    }
                }
            }
            bitMapCompare.recycle()
        }
    }

    fun handleMainUI(goto : UI) : String{
        while (true){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BitMapCompare(startCapture())
            if (bitMapCompare.isMainUI()){
                when (goto){
                    UI.REPAIRE -> {
                        if (bitMapCompare.isRedDotInRepairBtnInMainUI()){
                            showToastAndLv("主界面发现维修按钮上有红点,需要维修,点击维修")
                            saveTap(1373 , 368)
                            waitSecond(5)
                            handleRepairUI()
                        }
                        else {
                            showToastAndLv("主界面没有发现维修按钮上有红点,不需要维修")
                        }
                        bitMapCompare.recycle()
                        return "repair_finish"
                    }
                    UI.FACTORY -> {
                        //TODO
                        bitMapCompare.recycle()
                        return ""
                    }
                    UI.CHOOSE_COMBAT -> {
                        showToastAndLv("点击进入战斗选择")
                        saveTap(1397 , 750)
                        waitSecond(5)
                    }
                }
            }
            else if (bitMapCompare.isCombatChooseUI()){
                when (goto){
                    UI.CHOOSE_COMBAT -> {
                        if (handleChooseCombatUI()){
                            bitMapCompare.recycle()
                            return ""
                        }
                        else {
                            bitMapCompare.recycle()
                            return "full"
                        }
                    }
                    else -> {
                        showToastAndLv("发现进入了战斗选择画面,但是意图并不是进入这里,返回主界面,点击返回")
                        saveTap(106 , 72)
                        waitSecond(5)
                    }
                }
            }
            else {
                if (!handleExpeditionFinishUI(bitMapCompare)){
                    showToastAndLv("未知界面")
                    waitSecond(5)
                }
            }
            bitMapCompare.recycle()
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

    fun saveTap(x : Int , y: Int , xRandomOffsetRage : Int , yRandomOffsetRage : Int){
        var xChanged = (x + Math.random()*xRandomOffsetRage*2 - xRandomOffsetRage).toInt()
        var yChanged = (y + Math.random()*yRandomOffsetRage*2 - yRandomOffsetRage).toInt()
        lv("安全点击 ($x , $y),x轴偏移量 $xRandomOffsetRage , y轴偏移量 $yRandomOffsetRage , 最终点击位置是 ($xChanged , $yChanged)")
        tap(xChanged , yChanged)
    }

    fun saveTap (x: Int , y: Int){
        saveTap(x , y , 20 , 20)
    }
    fun drag(fromX : Int , fromY : Int , toX : Int , toY : Int){
        lv("拖动: 从($fromX , $fromY) 到 ($toX , $toY)")
        execShellCmd("input swipe $fromX $fromY $toX $toY")
    }

    fun drag(fromX : Int , fromY : Int , toX : Int , toY : Int , duration : Int){
        lv("拖动: 从($fromX , $fromY) 到 ($toX , $toY) , 用时 $duration ms")
        execShellCmd("input swipe $fromX $fromY $toX $toY $duration")
    }

    fun saveDrag(fromX : Int , fromY : Int , toX : Int , toY : Int , duration : Int , xOffset : Int , yOffset : Int){
        var fromXChanged : Int = ((Math.random()*xOffset*2 - xOffset) + fromX).toInt()
        var toXChanged : Int = fromXChanged - fromX + toX
        var fromYChanged : Int = ((Math.random()*yOffset*2 - yOffset) + fromY).toInt()
        var toYChanged : Int = fromYChanged- fromY+ toY
        lv("安全拖动: 从($fromX , $fromY) 到 ($toX , $toY) , 用时 $duration ms ,x轴偏移 $xOffset ,y轴偏移 $yOffset , 最终拖动为从从($fromXChanged , $fromYChanged) 到 ($toXChanged, $toYChanged)")
        drag(fromXChanged , fromYChanged , toXChanged , toYChanged , duration)
    }

    fun saveDrag(fromX : Int , fromY : Int , toX : Int , toY : Int , xOffset: Int , yOffset: Int){
        var fromXChanged : Int = ((Math.random()*xOffset*2 - xOffset) + fromX).toInt()
        var toXChanged : Int = fromXChanged - fromX + toX
        var fromYChanged : Int = ((Math.random()*yOffset*2 - yOffset) + fromY).toInt()
        var toYChanged : Int = fromYChanged- fromY+ toY
        lv("安全拖动: 从($fromX , $fromY) 到 ($toX , $toY) ,x轴偏移 $xOffset ,y轴偏移 $yOffset , 最终拖动为从从($fromXChanged , $fromYChanged) 到 ($toXChanged, $toYChanged)")
        drag(fromXChanged , fromYChanged , toXChanged , toYChanged)
    }

    fun saveDrag(fromX: Int , fromY: Int , toX: Int , toY: Int){
        saveDrag(fromX, fromY, toX, toY, 20 , 20)
    }

    fun saveDrag(fromX: Int , fromY: Int , toX: Int , toY: Int , duration: Int){
        saveDrag(fromX, fromY, toX, toY, duration , 20 , 20)
    }

    fun onBtn2Click(view: View){
        dataOutputStream.writeBytes("intput swipe 999 555 444 111")
        dataOutputStream.flush()
//        Runtime.getRuntime().exec("input swipe 999 , 555 , 444 , 111")
//        openActivity(GetClickPositionActivity::class.java)
//        finish()
    }
    fun onBtn3Click(view: View){
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
                var bitmapCompare = BitMapCompare(startCapture())
                bitmapCompare.isPreCombatUI()
            }
        }.start()
    }
    lateinit var process : Process
    lateinit var dataOutputStream : DataOutputStream
    fun onBtn4Click(view: View){
        process = Runtime.getRuntime().exec("su")
        dataOutputStream = DataOutputStream(process.outputStream)

        object : Thread(){
            override fun run() {

//                waitSecond(10)
//                execShellCmd("input swipe 414 428 600 428")


//                execShellCmd("sendevent /dev/input/event2 0001 0330 00000001")
//                execShellCmd("sendevent /dev/input/event2 0003 0058 00000001")
//                execShellCmd("sendevent /dev/input/event2 0003 0053 00000290")
//                execShellCmd("sendevent /dev/input/event2 0003 0054 00000469")
//                execShellCmd("sendevent /dev/input/event2 0000 0002 00000000")
//                execShellCmd("sendevent /dev/input/event2 0003 0053 00000490")
//                execShellCmd("sendevent /dev/input/event2 0003 0054 00000290")
//                execShellCmd("sendevent /dev/input/event2 0000 0002 00000000")
//                execShellCmd("sendevent /dev/input/event2 0000 0000 00000000")
//                execShellCmd("sendevent /dev/input/event2 0003 0053 00000292")
//                execShellCmd("sendevent /dev/input/event2 0003 0054 00000470")
//                execShellCmd("sendevent /dev/input/event2 0000 0002 00000000")
//                execShellCmd("sendevent /dev/input/event2 0003 0053 00000487")
//                execShellCmd("sendevent /dev/input/event2 0003 0054 00000289")
//                execShellCmd("sendevent /dev/input/event2 0000 0002 00000000")
//                execShellCmd("sendevent /dev/input/event2 0000 0000 00000000")
//                send("0003","0039","00001291")
//                send("0001","014a","00000001")
//                send("0001","0145","00000001")
//                send("0003","0035","0000012a")
//                send("0003","0036","000005fc")
//                send("0003","0030","00000007")
//                send("0000","0000","00000000")
//                send("0003","002f","00000001")
//                send("0003","0039","00001292")
//                send("0003","0035","000001bb")
//                send("0003","0036","00000215")
//                send("0000","0000","00000000")
//                send("0003","002f","00000000")
//                send("0003","0031","00000006")
//                send("0000","0000","00000000")
//                send("0003","002f","00000001")
//                send("0003","0030","00000005")
//                send("0000","0000","00000000")
//                send("0003","0030","00000003")
//                send("0000","0000","00000000")
//                send("0003","002f","00000000")
//                send("0003","0035","00000133")
//                send("0003","0036","000005e6")
//                send("0000","0000","00000000")
//                send("0003","0035","0000013d")
//                send("0003","0036","000005cf")
//                send("0003","0031","00000005")
//                send("0000","0000","00000000")
//                send("0003","002f","00000001")
//                send("0003","0035","000001b8")
//                send("0003","0036","00000230")
//                send("0003","0030","00000004")
//                send("0000","0000","00000000")
//                send("0003","002f","00000000")
//                send("0003","0035","00000148")
//                send("0003","0036","000005b6")
//                send("0003","0030","00000006")
//                send("0000","0000","00000000")
//                send("0003","002f","00000001")
//                send("0003","0036","00000245")
//                send("0003","0030","00000003")
//                send("0000","0000","00000000")
//                send("0003","002f","00000000")
//                send("0003","0035","00000156")
//                send("0003","0036","00000599")
//                send("0000","0000","00000000")
//                send("0003","002f","00000001")
//                send("0003","0035","000001bb")
//                send("0003","0036","0000025e")
//                send("0003","0030","00000004")
//                send("0003","0031","00000002")
//                send("0000","0000","00000000")
//                send("0003","002f","00000000")
//                send("0003","0035","0000015a")
//                send("0003","0036","0000058f")
//                send("0003","0030","00000007")
//                send("0003","0031","00000004")
//                send("0000","0000","00000000")
//                send("0003","002f","00000001")
//                send("0003","0035","000001c1")
//                send("0003","0036","0000027f")
//                send("0003","0030","00000005")
//                send("0003","0031","00000003")
//                send("0000","0000","00000000")
//                send("0003","002f","00000000")
//                send("0003","0039","ffffffff")
//                send("0003","002f","00000001")
//                send("0003","0039","ffffffff")
//                send("0001","014a","00000000")
//                send("0001","0145","00000000")
//                send("0000","0000","00000000")
            }
        }
                .start()
    }

    fun send(str1 : String , str2 : String , str3 : String){
        var sendmsg : String
        if (str3.equals("ffffffff")){
            sendmsg = "sendevent /dev/input/event2 ${Integer.parseInt(str1 , 16)} ${Integer.parseInt(str2 , 16)} " + "4294967295"
        }
        else {
            sendmsg = "sendevent /dev/input/event2 ${Integer.parseInt(str1 , 16)} ${Integer.parseInt(str2 , 16)} ${Integer.parseInt(str3 , 16)}"
        }
        lv(sendmsg)
        execShellCmd(sendmsg)
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
        fun recycle(){
            mBitMap.recycle()
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
                    lv("是快修确定dialog , 并且快修未选中")
                    return 1
                }
                else {
                    lv("是快修确定dialog , 并且快修已选中")
                    return 2
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
                    && isPiexlEqual(1700 , 914 , 255 , 190 , 0)
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
                    && isPiexlEqual(96 , 128 , 255 , 190 , 0)
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

        fun isCorrectZHANYIChoosed() : Boolean{
            if (isPiexlEqual(344 , 479 , 255 , 178 , 0)
                    && isPiexlEqual(515 , 392 , 255 , 178 , 0)
                    && isPiexlEqual(890 , 239 , 255 , 255 , 255)
                    && isPiexlEqual(1063 , 245 , 255 , 255 , 255)
                    && isPiexlEqual(1013 , 606 , 255 , 255 , 255)
                    && isPiexlEqual(1100 , 617 , 140 , 138 , 140)
                    && isPiexlEqual(1199 , 625 , 255 , 255 , 255)
                    ){
                lv("已经切换到了正确的战役")
                return true
            }
            else {
                lv("还未还未还未还未切换到正确的战役")
                return false
            }
        }
        fun isCombatConfirmDialog() : Boolean{
            if (isPiexlEqual(813 , 837 , 99 , 125 , 41)
                    && isPiexlEqual(817 , 899 , 99 , 125 , 41)
                    && isPiexlEqual(1148 , 835 , 255 , 178 , 0)
                    && isPiexlEqual(1147 , 910 , 255 , 178 , 0)
                    && isPiexlEqual(712 , 172 , 255 , 255 , 255)
                    ){
                lv("是战斗选择确认dialog")
                return true
            }
            else {
                lv("不是不是不是不是战斗选择确认dialog")
                return false
            }
        }
        fun isCombatChooseUI() : Boolean{
            if (isPiexlEqual(282 , 52 , 255 , 255 , 255)
                    && isPiexlEqual(310 , 78 , 255 , 255 , 255)
                    && isPiexlEqual(272 , 114 , 255 , 255 , 255)
                    && isPiexlEqual(1499 , 107 , 255 , 255 , 255)
                    && isPiexlEqual(1481 , 44 , 255 , 255 , 255)
                    ){
                lv("是战斗选择界面")
                return true
            }
            else {
                lv("不是不是不是不是战斗选择界面")
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

        fun isBattleUI() : Boolean{
            if (isPiexlEqual(144 , 59 , 255 , 255 , 255)
                    && isPiexlEqual(280 , 45 , 115 , 117 , 115)
                    && isPiexlEqual(461 , 61 , 255 , 85 , 0)
                    && isPiexlEqual(1700 , 974 , 58 , 105 , 107)
                    && isPiexlEqual(1832 , 973 , 49 , 101 , 99)
                    ){
                lv("是战斗界面")
                return true
            }
            else {
                lv("不是不是不是不是战斗界面")
                return false
            }
        }
        fun isPreCombatUI() : Boolean{
            if (isPiexlEqual(103 , 58 , 255 , 255 , 255)
                    && isPiexlEqual(169 , 65 , 255 , 255 , 255)
                    && isPiexlEqual(142 , 116 , 255 , 255 , 255)
                    && isPiexlEqual(305 , 46 , 255 , 255 , 255)
                    && isPiexlEqual(345 , 69 , 255 , 255 , 255)
                    && isPiexlEqual(1593 , 597 , 99 , 113 , 49)
                    && isPiexlEqual(1693 , 596 , 58 , 85 , 74)
                    && isPiexlEqual(411 , 16 , 8 , 8 , 8)
                    && isPiexlEqual(388 , 39 , 8 , 8 , 8)
                    ){
                lv("是开始作战前的部署画面")
                return true
            }
            else {
                lv("不是不是不是不是开始作战前的部署画面")
                return false
            }
        }

        fun isViewAtLocation1() : Boolean{
            if (isPiexlEqual(1139 , 442 , 148 , 198 , 247)
                    && isPiexlEqual(1147 , 476 , 148 , 198 , 247)
                    && isPiexlEqual(1296 , 486 , 156 , 202 , 255)
                    && isPiexlEqual(1154 , 592 , 156 , 202 , 255)
                    && isPiexlEqual(1292 , 583 , 148 , 194 , 247)
                    ){
                lv("视角在位置1")
                return true
            }
            else {
                lv("视角不在不在不在位置1")
                return false
            }
        }
        fun isViewAtLocation2() : Boolean{
            if (isPiexlEqual(581 , 380 , 255 , 255 , 255)
                    && isPiexlEqual(544 , 585 , 255 , 255 , 255)
                    && isPiexlEqual(763 , 633 , 255 , 255 , 255)
                    && isPiexlEqual(962 , 381 , 255 , 255 , 255)
                    && isPiexlEqual(537 , 977 , 255 , 255 , 255)
                    ){
                lv("视角在位置2")
                return true
            }
            else {
                lv("视角不在不在不在位置2")
                return false
            }
        }


        fun isCharactorSetupChooseUI() : Int{
            if (isPiexlEqual(275 , 152 , 255 , 255 , 255)
                    && isPiexlEqual(362 , 158 , 255 , 255 , 255)
                    && isPiexlEqual(449 , 161 , 255 , 255 , 255)
                    && isPiexlEqual(513 , 185 , 255 , 255 , 255)
                    && isPiexlEqual(589 , 161 , 255 , 255 , 255)
                    ){
                if (isPiexlEqual(66 , 648 , 247 , 174 , 0)
                        && isPiexlEqual(165 , 688 , 247 , 174 , 0)
                        && isPiexlEqual(86 , 730 , 247 , 174 , 0)
                        ){
                    lv("是角色部署选择界面,并且第4梯队已选中")
                    return 2
                }
                else{
                    lv("是角色部署选择界面,并且第4梯队未选中")
                    return 1
                }
            }
            else {
                lv("不是不是不是不是角色部署选择界面")
                return 0
            }
        }

        fun isCharactorAtLocation1() : Int{
            if (isPiexlEqual(1311 , 410 , 255 , 255 , 255)
                    && isPiexlEqual(1385 , 419 , 255 , 255 , 255)
                    && isPiexlEqual(1420 , 399 , 255 , 255 , 255)
                    ){
                if (isPiexlEqual(1219 , 349 , 255 , 186 , 0)
                        && isPiexlEqual(1220 , 589 , 255 , 186 , 0)
                        && isPiexlEqual(1343 , 469 , 255 , 186 , 0)
                        && isPiexlEqual(1103 , 468 , 255 , 186 , 0)
                        ){
                    lv("角色在位置1,并且已选中")
                    return 2
                }
                else{
                    lv("角色在位置1,但是未选中")
                    return 1
                }
            }
            else {
                lv("角色不在不在不在不在位置1")
                return 0
            }
        }

        fun isCharactorAtLocation2() : Int{
            if (isPiexlEqual(702 , 323 , 255 , 255 , 255)
                    && isPiexlEqual(787 , 322 , 255 , 255 , 255)
                    && isPiexlEqual(813 , 304 , 255 , 255 , 255)
                    ){
                if (isPiexlEqual(614 , 255 , 255 , 186 , 0)
                        && isPiexlEqual(502 , 375 , 255 , 186 , 0)
                        && isPiexlEqual(735 , 375 , 255 , 186 , 0)
                        && isPiexlEqual(613 , 488 , 255 , 186 , 0)
                        ){
                    lv("角色在位置2,并且已选中")
                    return 2
                }
                else{
                    lv("角色在位置2,但是未选中")
                    return 1
                }
            }
            else {
                lv("角色不在不在不在不在位置2")
                return 0
            }
        }

        fun isCharactorAtLocation3() : Int{
            if (isPiexlEqual(117 , 392 , 255 , 255 , 255)
                    && isPiexlEqual(187 , 403 , 255 , 255 , 255)
                    && isPiexlEqual(241 , 413 , 255 , 255 , 255)
                    ){
                if (isPiexlEqual(35 , 343 , 255 , 186 , 0)
                        && isPiexlEqual(151 , 463 , 255 , 186 , 0)
                        && isPiexlEqual(36 , 578 , 255 , 190 , 0)
                        ){
                    lv("角色在位置3,并且已选中")
                    return 2
                }
                else{
                    lv("角色在位置3,但是未选中")
                    return 1
                }
            }
            else {
                lv("角色不在不在不在不在位置3")
                return 0
            }
        }
        fun isCharactorAtLocation4_View1() : Boolean{
            if (isPiexlEqual(143 , 786 , 255 , 255 , 255)
                    && isPiexlEqual(225, 805, 255 , 255 , 255)
                    && isPiexlEqual(269, 811 , 255 , 255 , 255)
                    ){
                lv("在View1的视角下角色在位置4")
                return true
            }
            else {
                lv("在View1的视角下角色不在不在不在不在位置4")
                return false
            }
        }
        fun isCharactorAtLocation4_View2() : Int{
            if (isPiexlEqual(117 , 237 , 115 , 158 , 230)
//                    && isPiexlEqual(290 , 220 , 173 , 239 , 255)
//                    && isPiexlEqual(332 , 228 , 255 , 206 , 0)
                    ){
                if (isPiexlEqual(64 , 169 , 255 , 186 , 0)
//                        && isPiexlEqual(177 , 288 , 255 , 186 , 0)
                        || isPiexlEqual(62 , 401 , 255 , 186 , 0)
                        ){
                    //由于此处滑动判定不准,采用两个条件有一个满足即判定为已选中
                    lv("角色在视角2下的位置4,并且已选中")
                    return 2
                }
                else{
                    lv("角色在视角2下的位置4,但是未选中")
                    return 1
                }
            }
            else {
                lv("角色不在不在不在不在视角2下的位置4")
                return 0
            }
        }

        fun isCharactorAtLocation5() : Boolean{
            if (isPiexlEqual(92 , 561 , 107 , 158 , 230)
                    ){
                lv("角色在位置5")
                return true
            }
            else {
                lv("角色不在不在不在不在位置5")
                return false
            }
        }

        fun isRamdomEventDialog() : Boolean{
            if (isPiexlEqual(649 , 558 , 255 , 85 , 0)
                    && isPiexlEqual(734 , 546 , 255 , 85 , 0)
                    && isPiexlEqual(819 , 557 , 255 , 85 , 0)
                    && isPiexlEqual(811 , 599 , 255 , 85 , 0)
                    && isPiexlEqual(656 , 604 , 255 , 85 , 0)
                    ){
                    lv("是随机点事件dialog")
                    return true
            }
            else {
                lv("不是不是不是不是随机点事件dialog")
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
