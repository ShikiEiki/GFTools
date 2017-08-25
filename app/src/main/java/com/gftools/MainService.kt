package com.gftools

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.IBinder
import android.support.v7.app.NotificationCompat
import android.widget.Toast
import java.io.*
import com.gftools.utils.*

/**
 * Created by FH on 2017/8/1.
 */
class MainService : Service() {
    var mHandler = Handler()
    var toast : Toast? = null
    var ready = false
    val mThread = MyThread()
    lateinit var tempBitmapCompare : BaseBitmapCompare

    inner class MyThread : Thread(){
        var keepRun = true
        fun stopThis(){
            keepRun = false
        }

        fun work(){
            if (!launchGF()){
                return
            }
            waitSecond(10)

            //----------------开始判断是什么界面-----------------
            handleProgramStart()

            //---------------进入主界面--------------
            while (keepRun){
                //---------------进入维修界面,完毕后回到主界面----------------
                handleMainUI(MainActivity.UI.REPAIRE)
                //---------------进入战斗选择界面---------------------------
                while (keepRun) {
                    var tempResult = handleMainUI(MainActivity.UI.CHOOSE_COMBAT)
                    if (tempResult == -1) {
                        handleMainUI(MainActivity.UI.FACTORY)
                    } else if (tempResult == -2) {
                        showToastAndLv("现在设置的梯队${GFTools.getMainForceIndex()}无法作战,可能是正在维修等等原因,等待300s")
                        waitSecond(300)
                    } else {
                        break
                    }
                }
            }
        }
        override fun run() {
            sendRunningNotification()
            work()
            MainActivity.mMediaProjection.stop()
            MainActivity.mVirturlDisplay.release()
            showToastAndLv("已停止")
            sendRunAgainNotification()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lv("onStartCommand function : ${intent?.getStringExtra("function")}")
        when(intent?.getStringExtra("function")){
            "start" -> {
                if (!mThread.isAlive){
                    mThread.keepRun = true
                    mThread.start()
                }
            }
            "stop" -> {
                mThread.stopThis()
            }
            "startAgain" -> {
                MainActivity.activityInstance.sendCaptureIntent(10086)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onBind(intent: Intent?): IBinder? {
        lv("onBind")
        return null
    }
    override fun onCreate() {
        lv("onCreate")
        super.onCreate()
    }
    fun sendRunAgainNotification() {
        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var mainIntent = Intent(this, MainService::class.java)
        mainIntent.putExtra("function" , "startAgain")
        var pendingIntent = PendingIntent.getService(this, 0, mainIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        var notification = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("GFtools状态:[!!!已停止!!!]")
                .setContentText("点击重新开始运行")
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .build()
        notificationManager.notify(10086 , notification)
    }

    fun sendRunningNotification() {
        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var mainIntent = Intent(this, MainService::class.java)
        mainIntent.putExtra("function" , "stop")
        var pendingIntent = PendingIntent.getService(this, 0, mainIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        var notification = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("GFtools状态:[>>>运行中>>>]")
                .setContentText("点击停止运行")
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build()
        notificationManager.notify(10086 , notification)
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
        while (mThread.keepRun && past < time){
            Thread.sleep(1000)
            past++
            showToastAndLv("wait $past s" + (if (past==time) " wait end " else ""))
        }
    }

    fun handleProgramStart() {
        while (mThread.keepRun) {
            showToastAndLv("判断当前界面")
            var bitMapCompare = BaseBitmapCompare(MainActivity.startCapture())
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

    fun handleExpeditionFinishUI(bitMapCompare: BaseBitmapCompare) : Boolean{
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

    fun handleChooseCombatUI() : Int{
        when(GFTools.getBattleIndex()){
            0-> return handleChooseCombatUI_52e()
            1-> return handleChooseCombatUI_54e()
        }
        return 1
    }

    /**
     * 返回值为-1代表无法选择战斗,仓库已满,如果已满,会返回MainUI函数才会结束
     * 返回值为1代表选择战斗成功,成功会保证界面停在战斗开始部署UI
     */
    fun handleChooseCombatUI_54e() : Int{
        var noMoreSpace = false
        while (mThread.keepRun){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BitmapCompare54e(MainActivity.startCapture())
            if (bitMapCompare.isCombatChooseUI()){
                if (noMoreSpace){
                    showToastAndLv("仓库已满,点击返回")
                    saveTap(99 , 81)
                    waitSecond(10)
                }
                else if (bitMapCompare.isCorrectZHANYIChoosed()){
                    showToastAndLv("已经选择了正确的战役,点击5-4e")
                    saveTap(1271 , 958)
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
                return -1
            }
            else if (bitMapCompare.isPreCombatUI()){
                bitMapCompare.recycle()
                break
            }
            else {
                if (!handleExpeditionFinishUI(bitMapCompare)){
                    showToastAndLv("未知界面")
                    waitSecond(5)
                }
            }
            bitMapCompare.recycle()
        }
        return 1
    }
    /**
     * 返回值为-1代表无法选择战斗,仓库已满,如果已满,会返回MainUI函数才会结束
     * 返回值为1代表选择战斗成功,成功会保证界面停在战斗开始部署UI
     */
    fun handleChooseCombatUI_52e() : Int{
        var noMoreSpace = false
        while (mThread.keepRun){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BitmapCompare52e(MainActivity.startCapture())
            if (bitMapCompare.isCombatChooseUI()){
                if (noMoreSpace){
                    showToastAndLv("仓库已满,点击返回")
                    saveTap(99 , 81)
                    waitSecond(10)
                }
                else if (bitMapCompare.isCorrectZHANYIChoosed()){
                    showToastAndLv("已经选择了正确的战役,点击5-2e")
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
                return -1
            }
            else if (bitMapCompare.isPreCombatUI()){
                bitMapCompare.recycle()
                break
            }
            else {
                if (!handleExpeditionFinishUI(bitMapCompare)){
                    showToastAndLv("未知界面")
                    waitSecond(5)
                }
            }
            bitMapCompare.recycle()
        }
        return 1
    }

    fun handleArmyDeploy() : Int{
        when (GFTools.getBattleIndex()){
            0 -> return handleArmyDeploy_52e()
            1 -> return handleArmyDeploy_54e()
        }
        return 1
    }

    /**
     * 返回值为-1代表无法选择配置的梯队,可能是正在维修或者正在训练等等,返回时界面将停在部队大地图部署界面
     * 返回1代表部署成功,返回时界面将停在战斗开始界面
     */
    fun handleArmyDeploy_54e() : Int{
        var errorCode : Int = 1
        var stage : Int = 1
        while (mThread.keepRun){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BitmapCompare54e(MainActivity.startCapture())
            if (bitMapCompare.isPreCombatUI()) {
                if (errorCode < 0) {
                    bitMapCompare.recycle()
                    return errorCode
                }
                else if (stage == 1) {
                    if (bitMapCompare.isViewAtLocation1()) {
                        if (bitMapCompare.isCharactorAtHome()) {
                            stage = 2
                            showToastAndLv("发现指挥部里已经部署部队了,上拉画面")
                            saveDrag(1400, 200, 1400, 900, 500)
                            waitSecond(1)
                            saveDrag(1400, 200, 1400, 900, 500)
                            waitSecond(1)
                            saveDrag(1400, 200, 1400, 900, 500)
                            waitSecond(1)
                            saveDrag(1400, 200, 1400, 900, 500)
                            waitSecond(1)
                            saveDrag(1400, 200, 1400, 900, 500)
                            waitSecond(2)
                        }
                        else {
                            showToastAndLv("指挥部里没有部署部队,点击指挥部")
                            saveTap(1212, 584)
                            waitSecond(2)
                        }
                    }
                    else {
                        showToastAndLv("当前视角不在位置1,调整视角")
                        saveDrag(1500, 800, 600, 300, 500)
                        waitSecond(1)
                        saveDrag(1500, 800, 600, 300, 500)
                        waitSecond(1)
                    }
                }
                else if (stage == 2) {
                    if (!bitMapCompare.isViewAtLocation2()) {
                        showToast("发现视角不在位置2,上拉画面")
                        saveDrag(1400, 200, 1400, 900, 500)
                        waitSecond(1)
                        saveDrag(1400, 200, 1400, 900, 500)
                        waitSecond(1)
                        saveDrag(1400, 200, 1400, 900, 500)
                        waitSecond(1)
                        saveDrag(1400, 200, 1400, 900, 500)
                        waitSecond(1)
                        saveDrag(1400, 200, 1400, 900, 500)
                        waitSecond(2)
                    }
                    else if (bitMapCompare.isCharactorAtLocation1() == 0) {
                        showToastAndLv("发现位置1上没有部署部队,点击位置1")
                        saveTap(1313, 527)
                        waitSecond(2)
                    }
                    else {
                        showToastAndLv("位置1上已经部署完毕,点击开始作战")
                        saveTap(1735, 985)
                        waitSecond(3)
                    }
                }
            }
            else if (bitMapCompare.isBattleUI()){
                bitMapCompare.recycle()
                break
            }
            else if (bitMapCompare.isTeamChooseUI()){
                var mainforceIndex = GFTools.getMainForceIndex()
                var cannonFodderIndex = GFTools.getCannonFodderIndex()
                var results : List<String>
                if (stage == 1){
                    results = bitMapCompare.isCorrectTeamSelected(cannonFodderIndex).split("/")
                }
                else {
                    results = bitMapCompare.isCorrectTeamSelected(mainforceIndex).split("/")
                }
                if (errorCode < 0){
                    showToastAndLv("梯队选择发现已经存在的错误${errorCode},点击取消")
                    tap(1479 , 962)
                    waitSecond(2)
                }
                else if (results[0].equals("-1")){
                    showToastAndLv("发现第${(mainforceIndex+1)}梯队不可选择,可能是正在维修或训练等等,梯队部署失败,点击取消")
                    tap(1479 , 962)
                    errorCode = -1
                    waitSecond(2)
                }
                else if (results[1].equals("0")){
                    showToastAndLv("在(${results[2]} , ${results[3]})发现梯队${(mainforceIndex+1)},但是还未选中,点击选中该梯队")
                    tap(results[2].toInt() , results[3].toInt())
                    waitSecond(2)
                }
                else {
                    showToastAndLv("在(${results[2]} , ${results[3]})发现梯队${(mainforceIndex+1)},并且已经选中,点击部署按钮")
                    tap(1763 , 962)
                    waitSecond(2)
                }
            }
            else {
                showToastAndLv("未知界面,wait 5s")
                waitSecond(5)
            }
            bitMapCompare.recycle()
        }
        return errorCode
    }
    /**
     * 返回值为-1代表无法选择配置的梯队,可能是正在维修或者正在训练等等,返回时界面将停在部队大地图部署界面
     * 返回1代表部署成功,返回时界面将停在战斗开始界面
     */
    fun handleArmyDeploy_52e() : Int{
        var errorCode : Int = 1
        while (mThread.keepRun){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BitmapCompare52e(MainActivity.startCapture())
            if (bitMapCompare.isPreCombatUI()){
                if (errorCode < 0){
                    bitMapCompare.recycle()
                    return errorCode
                }
                else if (!bitMapCompare.isViewAtLocation1()){
                    showToastAndLv("当前视角不在位置1,调整视角")
                    saveDrag(1500 , 300 , 800 , 600 , 500)
                    waitSecond(1)
                    saveDrag(1500 , 300 , 800 , 600 , 500)
                    waitSecond(1)
                }
                else {
                    when (bitMapCompare.isCharactorAtLocation1()){
                        0 -> {
                            showToastAndLv("发现初始点上没有部署部队,点击部署按钮")
                            saveTap(1241 , 517)
                            waitSecond(2)
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
                bitMapCompare.recycle()
                break
            }
            else if (bitMapCompare.isTeamChooseUI()){
                var mainforceIndex = GFTools.getMainForceIndex()
                var results = bitMapCompare.isCorrectTeamSelected(mainforceIndex).split("/")
                if (errorCode < 0){
                    showToastAndLv("梯队选择发现已经存在的错误${errorCode},点击取消")
                    tap(1749 , 962)
                    waitSecond(2)
                }
                else if (results[0].equals("-1")){
                    showToastAndLv("发现第${(mainforceIndex+1)}梯队不可选择,可能是正在维修或训练等等,梯队部署失败,点击取消")
                    tap(1749 , 962)
                    errorCode = -1
                    waitSecond(2)
                }
                else if (results[1].equals("0")){
                    showToastAndLv("在第${(results[0].toInt()+1)}行发现梯队${(mainforceIndex+1)},但是还未选中,点击选中该梯队")
                    tap(results[2].toInt() , results[3].toInt())
                    waitSecond(2)
                }
                else {
                    showToastAndLv("在第${(results[0].toInt()+1)}行发现梯队${(mainforceIndex+1)},并且已经选中,点击部署按钮")
                    tap(1763 , 962)
                    waitSecond(2)
                }
            }
            else {
                showToastAndLv("未知界面,wait 5s")
                waitSecond(5)
            }
            bitMapCompare.recycle()
        }
        return errorCode
    }

    fun handleBattleUI(){
        when(GFTools.getBattleIndex()){
            0->handleBattleUI_52e()
            1->handleBattleUI_54e()
        }
    }
    fun handleBattleUI_54e(){
        var stage = 1
        while (mThread.keepRun){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BitmapCompare54e(MainActivity.startCapture())
            if (bitMapCompare.isBattleUI()){
                if (stage < 4){
                    if (!bitMapCompare.isViewAtLocation2()){
                        showToastAndLv("当前视角不在位置1,调整视角")
                        saveDrag(1500 , 300 , 500 , 800 , 500)
                        waitSecond(1)
                        saveDrag(1500 , 300 , 500 , 800 , 500)
                        waitSecond(1)
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
                                        waitSecond(10)
                                    }
                                }
                            }
                            1 -> {
                                showToastAndLv("初始点上的部队没有被选中,点击初始点,再点击2号点")
                                saveTap(1314 , 532)
                                waitSecond(1)
                                saveTap(681 , 461)
                                waitSecond(5)
                            }
                            2 -> {
                                showToastAndLv("初始点上的部队已经被选中,点击2号点以便行进至2号点")
                                saveTap(681 , 461)
                                waitSecond(5)
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
                                        showToastAndLv("在stage == 2时2号点上没有部队,错误,无法处理,等待10s")
                                        waitSecond(10)
                                    }
                                }
                            }
                            1 -> {
                                showToastAndLv("2号点上的部队没有被选中,点击2号点再点击3号点")
                                saveTap(681 , 461)
                                waitSecond(1)
                                saveTap(181 , 630)
                                waitSecond(5)
                            }
                            2 -> {
                                showToastAndLv("2号点上的部队已经被选中,点击3号点以便行进至3号点")
                                saveTap(181 , 630)
                                waitSecond(17)
                            }
                        }
                    }
                    else if (stage == 3){
                        when (bitMapCompare.isCharactorAtLocation3()) {
                            0 -> {
                                showToastAndLv("在stage == 3时3号点上没有部队,错误,无法处理,等待10s")
                                waitSecond(10)
                            }
                            1 -> {
                                showToastAndLv("3号点上的部队没有被选中,点击3号点以便选中")
                                saveTap(181, 630)
                                waitSecond(2)
                            }
                            2 -> {
                                stage = 4
                                waitSecond(1)
                            }
                        }
                    }
                }
                else {
                    if (!bitMapCompare.isViewAtLocation3()) {
                        showToastAndLv("当前视角不在位置3,调整视角")
                        saveDrag(300, 300, 1500, 800, 500)
                        waitSecond(1)
                        saveDrag(300, 300, 1500, 800, 500)
                        waitSecond(1)
                    } else if (stage == 4) {
                        when (bitMapCompare.isCharactorAtLocation4()) {
                            0 -> {
                                showToastAndLv("角色不在4号点,点击4号点")
                                saveTap(1407, 535)
                                waitSecond(2)
                            }
                            else -> {
                                stage = 5
                                waitSecond(1)
                            }
                        }
                    } else if (stage == 5) {
                        when (bitMapCompare.isCharactorAtLocation4()) {
                            0 -> {
                                if (bitMapCompare.isCharactorAtLocation5()) {
                                    showToastAndLv("部队已经行进至5号点点击结束回合")
                                    saveTap(1751, 997)
                                    waitSecond(5)
                                } else {
                                    showToastAndLv("在stage == 5时4号5号点上都没有部队,错误,无法处理,等待10s")
                                    waitSecond(10)
                                }
                            }
                            1 -> {
                                showToastAndLv("4号点上的部队没有被选中,点击4号点再点击5号点")
                                saveTap(1407, 535)
                                waitSecond(1)
                                saveTap(755, 633)
                                waitSecond(5)
                            }
                            2 -> {
                                showToastAndLv("4号点上的部队已经被选中,点击5号点以便行进至5号点")
                                saveTap(755, 633)
                                waitSecond(17)
                            }
                        }
                    }
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
                    waitSecond(3)
                }
            }
            bitMapCompare.recycle()
        }
    }
    fun handleBattleUI_52e(){
        var verticalCorrection = 0
        var stage = 1
        while (mThread.keepRun){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BitmapCompare52e(MainActivity.startCapture())
            if (bitMapCompare.isBattleUI()){
                if (stage < 4){
                    if (bitMapCompare.isRamdomEventDialog()){
                        showToastAndLv("发现随机事件,点击画面中间任意点取消dialog")
                        saveTap(895 , 575 , 200 , 200)
                        waitSecond(17)
                    }
                    else if (!bitMapCompare.isViewAtLocation1()){
                        showToastAndLv("当前视角不在位置1,调整视角")
                        saveDrag(1500 , 300 , 800 , 600 , 500)
                        waitSecond(1)
                        saveDrag(1500 , 300 , 800 , 600 , 500)
                        waitSecond(1)
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
                                waitSecond(2)
                            }
                            2 -> {
                                showToastAndLv("初始点上的部队已经被选中,点击2号点以便行进至2号点")
                                saveTap(616 , 419)
                                waitSecond(17)
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
                                waitSecond(2)
                            }
                            2 -> {
                                showToastAndLv("2号点上的部队已经被选中,点击3号点以便行进至3号点")
                                saveTap(33 , 511)
                                waitSecond(17)
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
                                waitSecond(2)
                            }
                            2 -> {
                                showToastAndLv("3号点上的部队已经被选中,点击4号点以便行进至4号点")
                                saveTap(55, 895)
                                waitSecond(3)
                            }
                        }
                    }
                }
                if (stage == 4){
                    var tempResult = bitMapCompare.isViewAtLocation2(verticalCorrection)
                    if (tempResult == -999){
                        showToastAndLv("stage == 4时,视角不在view2,调整视角")
                        saveDrag(1000 , 300 , 400 , 800 , 1000)
                        waitSecond(1)
                        saveDrag(1000 , 300 , 400 , 800 , 1000)
                        waitSecond(1)
                        saveDrag(1200 , 900 , 1100 , 300 , 2000)
                        waitSecond(4)
                        verticalCorrection = 0
                    }
                    else {
                        verticalCorrection = tempResult
                        tempResult = bitMapCompare.isCharactorAtLocation4_View2(verticalCorrection)
                        if (tempResult == -999){
                            if (bitMapCompare.isCharactorAtLocation5(verticalCorrection) == -999){
                                showToastAndLv("stage == 4时,判断部队不在4号点也不在5号点,可能是视角调整有误差,重新调整视角")
                                saveDrag(1000 , 300 , 400 , 800 , 1000)
                                waitSecond(1)
                                saveDrag(1000 , 300 , 400 , 800 , 1000)
                                waitSecond(1)
                                saveDrag(1200 , 900 , 1200 , 300 , 2000)
                                waitSecond(4)
                                verticalCorrection = 0
                            }
                            else {
                                stage = 5
                                waitSecond(1)
                            }
                        }
                        else {
                            verticalCorrection = tempResult
                            if (bitMapCompare.isCharactorAtLocation4_View2_selected(verticalCorrection) == -999){
                                showToastAndLv("4号点上的部队未选中,点击4号点以便选中")
                                saveTap(57 , 347 + verticalCorrection)
                                waitSecond(2)
                            }
                            else {
                                showToastAndLv("4号点上的部队已经被选中,点击5号点以便行进至5号点")
                                saveTap(44 , 698 + verticalCorrection)
                                waitSecond(17)
                            }
                        }
                    }
                }
                if (stage == 5){
                    showToastAndLv("部队已经行进至5号点点击结束回合")
                    saveTap(1759 , 990)
                    waitSecond(7)
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
                    waitSecond(3)
                }
            }
            bitMapCompare.recycle()
        }
    }
    fun handleLoginUI(){
        var hasClick = false
        while (mThread.keepRun){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BaseBitmapCompare(MainActivity.startCapture())
            if (bitMapCompare.isLoginUI()){
                showToastAndLv("已经进入登录画面,点击进入游戏")
                saveTap(500 , 680)
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
        while (mThread.keepRun){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BaseBitmapCompare(MainActivity.startCapture())
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
        while (mThread.keepRun){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BaseBitmapCompare(MainActivity.startCapture())
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
        while (mThread.keepRun){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BaseBitmapCompare(MainActivity.startCapture())
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
        return true
    }

    fun handleQuickRepair(){
        while (mThread.keepRun){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BaseBitmapCompare(MainActivity.startCapture())
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

    fun handleGunDisassemble(oldBitMapCompare: BaseBitmapCompare?) {
        var bitMapCompare : BaseBitmapCompare?= oldBitMapCompare
        var complete = false
        while (mThread.keepRun) {
            showToastAndLv("判断当前界面")
            if (bitMapCompare == null){
                bitMapCompare = BaseBitmapCompare(MainActivity.startCapture())
            }
            var isFactoryUI = bitMapCompare.isFactoryUI()
            if (isFactoryUI != 0){
                if (complete){
                    showToast("拆解完成,点击返回")
                    saveTap(99 , 79)
                    waitSecond(10)
                }
                else if (isFactoryUI == 1){
                    showToastAndLv("已经进入了工厂界面,但是回收拆解按钮未选中,点击回收拆解按钮")
                    saveTap(126 , 665)
                    waitSecond(2)
                }
                else if (isFactoryUI == 21){
                    showToastAndLv("选中了拆除按钮,但是没有选择拆除枪娘,点击加号")
                    saveTap(437 , 295)
                    waitSecond(2)
                }
                else if (isFactoryUI == 22){
                    showToastAndLv("选中了拆除按钮,也选择了拆除枪娘,点击拆解")
                    saveTap(1751, 930)
                    waitSecond(3)
                }
            }
            else if (bitMapCompare.isGunTobeDisassembleChooseUI()){
                if (!handleGunDisassembleGunChoose(bitMapCompare)){
                    showToastAndLv("没有更多的可以拆解的枪,点击返回")
                    saveTap(99 , 79)
                    waitSecond(10)
                }
            }
            else if (bitMapCompare.isMainUI()){
                bitMapCompare.recycle()
                return
            }
            else if (bitMapCompare.isDialog()){
                showToastAndLv("检测到对话框,推测是没有可拆的对话框,点击确定")
                complete = true
                saveTap(962 , 778)
                waitSecond(2)
            }
            else {
                if (!handleExpeditionFinishUI(bitMapCompare)){
                    showToast("未知界面")
                    waitSecond(5)
                }
            }
            bitMapCompare.recycle()
            bitMapCompare = null
        }
    }

    fun handleGunDisassembleGunChoose(oldBitMapCompare: BaseBitmapCompare?) :Boolean{
        var bitMapCompare : BaseBitmapCompare?= oldBitMapCompare
        var choosedNum : Int
        var noMoreToDisassemble = false
        while (mThread.keepRun) {
            showToastAndLv("判断当前界面")
            if (bitMapCompare == null){
                bitMapCompare = BaseBitmapCompare(MainActivity.startCapture())
            }
            if (bitMapCompare.isGunTobeDisassembleChooseUI()){
                var clickedThisTime = 0
                choosedNum = 0
                for (i in 0..5){
                    if (bitMapCompare.isPiexlEqual((47 + i*268), 174, 255, 255, 255)) {
                        if (bitMapCompare.isPiexlEqual((242 + i*268), 184, 255, 190, 0)) {
                            showToastAndLv("发现第${i+1}个枪可拆卸但是并没有被选中,点击这把枪")
                            saveTap((124 + i*270), 385)
                            waitSecond(1)
                            clickedThisTime++
                        }
                        else {
                            showToastAndLv("发现第${i+1}个枪可拆卸并且已经被选中")
                            choosedNum++
                        }
                    }
                    else {
                        showToastAndLv("发现第${i+1}个枪不可拆卸,忽略")
                    }
                }
                for (i in 0..5){
                    if (bitMapCompare.isPiexlEqual((47 + i*268), 630, 255, 255, 255)) {
                        if (bitMapCompare.isPiexlEqual((242 + i*268), 640, 255, 190, 0)) {
                            showToastAndLv("发现第${i+7}个枪可拆卸但是并没有被选中,点击这把枪")
                            saveTap((124 + i*270), 823)
                            waitSecond(1)
                            clickedThisTime++
                        }
                        else {
                            showToastAndLv("发现第${i+7}个枪可拆卸并且已经被选中")
                            choosedNum++
                        }
                    }
                    else {
                        showToastAndLv("发现第${i+7}个枪不可拆卸,忽略")
                    }
                }
                if (clickedThisTime == 0){
                    if (choosedNum == 0){
                        showToastAndLv("本次没有发现可以点击的枪,也没有选中任何枪,推断是没有可以拆解的枪,点击确定")
                        noMoreToDisassemble = true
                    }
                    else {
                        showToastAndLv("本次没有发现可以点击的枪,但是有选中的枪,说明可拆解的枪已经全部选中,可以拆解,点击确定")
                    }
                    saveTap(1783 , 959)
                    waitSecond(2)
                }
            }
            else if (bitMapCompare.isFactoryUI() != 0){
                return !noMoreToDisassemble
            }
            else {
                showToastAndLv("未知界面")
                waitSecond(5)
            }
            bitMapCompare.recycle()
            bitMapCompare = null
        }
        return false
    }

    /**
     * goto == REPAIRE时:
     * 返回1代表成功,返回时界面将停在MainUI
     *
     * goto == FACTORY时:
     * 返回1代表成功,返回时界面将停在MainUI
     *
     * goto == CHOOSE_COMBAT时:
     * 返回1代表成功,返回是界面将停在MainUi
     * 返回-1代表仓库已满,无法选择战斗,返回时界面将停在MainUI
     * 返回-2代表设定的梯队无法选择,可能是正在训练或者维修,返回时界面将停在MainUI
     *
     *
     */
    fun handleMainUI(goto : MainActivity.UI) : Int{
        var errorCode : Int = 1
        while (mThread.keepRun){
            showToastAndLv("判断当前界面")
            var bitMapCompare = BaseBitmapCompare(MainActivity.startCapture())
            if (goto == MainActivity.UI.CHOOSE_COMBAT){
                if (bitMapCompare.isMainUI()){
                    if (errorCode < 0){
                        bitMapCompare.recycle()
                        break
                    }
                    else {
                        showToast("点击进入战斗选择")
                        tap(1397 , 750)
                        waitSecond(4)
                    }
                }
                else if(bitMapCompare.isCombatChooseUI()){
                    if (errorCode < 0){
                        showToast("发现已经出现的错误${errorCode} , 点击返回基地")
                        tap(102 , 81)
                        waitSecond(10)
                    }
                    else if (handleChooseCombatUI() == 1){
                        if (handleArmyDeploy() == 1){
                            handleBattleUI()
                            bitMapCompare.recycle()
                            break
                        }
                        else {
                            showToastAndLv("部署梯队失败,点击返回任务选择")
                            saveTap(113 , 68)
                            errorCode = -2
                            waitSecond(5)
                        }
                    }
                    else {
                        errorCode = -1
                        bitMapCompare.recycle()
                        break
                    }
                }
                else {
                    if (!handleExpeditionFinishUI(bitMapCompare)){
                        showToastAndLv("未知界面")
                        waitSecond(5)
                    }
                }
            }
            else if (goto == MainActivity.UI.FACTORY){
                if (bitMapCompare.isFactoryUI() != 0){
                    handleGunDisassemble(bitMapCompare)
                    bitMapCompare.recycle()
                    break
                }
                else if (bitMapCompare.isMainUI()){
                    showToastAndLv("点击进入工厂")
                    saveTap(1734 , 521)
                    waitSecond(3)
                }
                else {
                    if (!handleExpeditionFinishUI(bitMapCompare)){
                        showToastAndLv("未知界面")
                        waitSecond(5)
                    }
                }
            }
            else if (goto == MainActivity.UI.REPAIRE){
                if (bitMapCompare.isMainUI()){
                    if (bitMapCompare.isRedDotInRepairBtnInMainUI()){
                        handleRepairUI()
                    }
                    else {
                        showToastAndLv("主界面没有发现维修按钮上有红点,不需要维修")
                    }
                    bitMapCompare.recycle()
                    break
                }
                else {
                    if (!handleExpeditionFinishUI(bitMapCompare)){
                        showToastAndLv("未知界面")
                        waitSecond(5)
                    }
                }
            }
            bitMapCompare.recycle()
        }
        return errorCode
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

    private fun showToastAndLv(msg : String){
        lv(msg)
        showToast(msg)
    }



    fun showToast(msg : String){
//        mHandler.post(object : Runnable{
//            override fun run() {
//                toast?.cancel()
//                toast = Toast.makeText(applicationContext , msg , Toast.LENGTH_SHORT)
//                toast?.show()
//            }
//        })
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

}