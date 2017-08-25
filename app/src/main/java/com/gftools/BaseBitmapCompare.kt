package com.gftools

import android.graphics.Bitmap
import com.gftools.utils.*

/**
 * Created by FH on 2017/8/10.
 */

open class BaseBitmapCompare(bitmap : Bitmap){
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

    fun isPiexlEqual_allowError(x : Int , y : Int , r : Int , g : Int, b : Int , allowMaxError : Int) : Boolean{
        var (pr , pg , pb) = getPixelRGB(x , y)
        lv("对比像素颜色:点坐标[$x , $y] 图中颜色 ($pr , $pg , $pb) 期望颜色 ($r , $g , $b)")
        if ((pr in (r-allowMaxError)..(r+allowMaxError))
                && (pg in (g-allowMaxError)..(g+allowMaxError))
                && (pb in (b-allowMaxError)..(b+allowMaxError))
                ){
            lv("对比像素坐标[$x , $y]与期望在允许最大误差 $allowMaxError 的情况下一致,返回true")
            return true
        }
        lv("对比像素坐标[$x , $y]与期望在允许最大误差 $allowMaxError 的情况下仍然不同,返回false")
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

    fun isFactoryUI() : Int{
        if (isPiexlEqual(326 , 47 , 247 , 251 , 247)
                && isPiexlEqual(275 , 91 , 255 , 255 , 255)
                && isPiexlEqual(321 , 100 , 255 , 251 , 255)
                && isPiexlEqual(413 , 38 , 164 , 166 , 164)
                && isPiexlEqual(489 , 37 , 164 , 166 , 164)
                && isPiexlEqual(123 , 953 , 123, 121 , 123)
                && isPiexlEqual(162 , 967 , 115, 117 , 115)
                ){
            if (isPiexlEqual(149 , 628 , 255 , 255 , 255)
                    ){
                lv("是工厂界面 , 但是回收未选中")
                return 1
            }
            else {
                if (isPiexlEqual(473 , 295 , 255 , 255 , 255)){
                    lv("是工厂界面 , 并且回收已选中,但是还没有选中要分解的枪")
                    return 21
                }
                else {
                    lv("是工厂界面 , 并且回收已选中,并且已经选中了要分解的枪")
                    return 22
                }
            }
        }
        else {
            lv("不是不是不是不是不是工厂界面")
            return 0
        }
    }

    fun isGunTobeDisassembleChooseUI() : Boolean {
        if (isPiexlEqual(324 , 47 , 247 , 251 , 247)
                && isPiexlEqual(320 , 99 , 247 , 251 , 247)
                && isPiexlEqual(271 , 93 , 255 , 255 , 255)
                && isPiexlEqual(117 , 34 , 255 , 255 , 255)
                && isPiexlEqual(120, 118 , 255 , 255 , 255)
                ){
            lv("是拆解枪娘选择界面")
            return true
        }
        else {
            lv("不是不是不是不是拆解枪娘选择界面")
            return false
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
                && isPiexlEqual(1551 , 939 , 255 , 255 , 255)
                && isPiexlEqual(1584, 949 , 255 , 255 , 255)
                ){
            lv("是战斗界面")
            return true
        }
        else {
            lv("不是不是不是不是战斗界面")
            return false
        }
    }


    fun isTeamChooseUI() : Boolean{
        if (isPiexlEqual(275 , 152 , 255 , 255 , 255)
                && isPiexlEqual(362 , 158 , 255 , 255 , 255)
                && isPiexlEqual(449 , 161 , 255 , 255 , 255)
                && isPiexlEqual(513 , 185 , 255 , 255 , 255)
                && isPiexlEqual(589 , 161 , 255 , 255 , 255)
                ){
            lv("是部署队伍选择界面")
            return true
        }
        else {
            lv("不是不是不是不是部署队伍选择界面")
            return false
        }
    }

    fun isCorrectTeamSelected(teamNo : Int) : String{
        when (teamNo){
            0 -> {
                for (i:Int in 204 .. 881){
                    if (isPiexlEqual_allowError(125 , i  , 49 , 57 , 49 , 10)
                            && isPiexlEqual_allowError(132 , i+242-225 , 49 , 53 , 49 , 10)
                            && isPiexlEqual_allowError(132 , i+267-225 , 49 , 49 , 49 , 10)
                            ){
                        if (isPiexlEqual_allowError(18 , i+251-225, 255 , 255 , 255 , 10)){
                            lv("发现梯队${(teamNo+1)},但是还未选中,推断中心点是(97,${i-225+251})")
                            return "$i/0/97/${i-225+251}"
                        }
                        else if (isPiexlEqual_allowError(18 , i+251-225, 255 , 182 , 0 , 10)){
                            lv("发现梯队${(teamNo+1)},并且已经选中,推断中心点是(97,${i-225+251})")
                            return "$i/1/97/${i-225+251}"
                        }
                    }
                }
                lv("在当前界面没有发现梯队${(teamNo+1)}")
                return "-1/0"
            }
            1 -> {
                for (i in 204 .. 881){
                    if (isPiexlEqual_allowError(123 , i , 49 , 57 , 49 , 10)
                            && isPiexlEqual_allowError(136 , i+398-377 , 49 , 53 , 49 , 10)
                            && isPiexlEqual_allowError(142 , i+420-377, 49 , 53 , 49 , 10)
                            ){
                        if (isPiexlEqual_allowError(18 , i+407-377 , 255 , 255 , 255 , 10)){
                            lv("发现梯队${(teamNo+1)},但是还未选中,推断中心点是(97,${i-377+409})")
                            return "$i/0/97/${i-377+409}"
                        }
                        else if (isPiexlEqual_allowError(18 , i+407-377 , 255 , 182 , 0 , 10)){
                            lv("发现梯队${(teamNo+1)},并且已经选中,推断中心点是(97,${i-377+409})")
                            return "$i/1/97/${i-377+409}"
                        }
                    }
                }
                lv("在当前界面没有发现梯队${(teamNo+1)}")
                return "-1/0"
            }
            2 -> {
                for (i in 204 .. 881){
                    if (isPiexlEqual_allowError(122 , i , 49 , 49 , 49 , 10)
                            && isPiexlEqual_allowError(132 , i+538-523 , 49 , 53 , 49 , 10)
                            && isPiexlEqual_allowError(123 , i+552-523 , 49 , 49 , 49 , 10)
                            ){
                        if (isPiexlEqual_allowError(18 , i+549-523 , 255 , 255 , 255 , 10)){
                            lv("发现梯队${(teamNo+1)},但是还未选中,推断中心点是(97,${i-523+549})")
                            return "$i/0/97/${i-523+549}"
                        }
                        else if (isPiexlEqual_allowError(18 , i+549-523 , 255 , 182 , 0 , 10)){
                            lv("发现梯队${(teamNo+1)},并且已经选中,推断中心点是(97,${i-523+549})")
                            return "$i/1/97/${i-523+549}"
                        }
                    }
                }
                lv("在当前界面没有发现梯队${(teamNo+1)}")
                return "-1/0"
            }
            3 -> {
                for (i in 204 .. 881){
                    if (isPiexlEqual_allowError(141 , i , 49 , 57 , 49 , 10)
                            && isPiexlEqual_allowError(121 , i+700-657 , 49 , 49 , 49 , 10)
                            && isPiexlEqual_allowError(142 , i+711-657 , 49 , 49 , 49 , 10)
                            ){
                        if (isPiexlEqual_allowError(18 , i+693-657 , 255 , 255 , 255 , 10)){
                            lv("发现梯队${(teamNo+1)},但是还未选中,推断中心点是(97,${i+693-657})")
                            return "$i/0/97/${i+693-657}"
                        }
                        else if (isPiexlEqual_allowError(18 , i+693-657 , 255 , 182 , 0, 10)){
                            lv("发现梯队${(teamNo+1)},并且已经选中,推断中心点是(97,${i+693-657})")
                            lv("---------------------------" + "$i/1/97/${i+693-657}")
                            return "$i/1/97/${i+693-657}"
                        }
                    }
                }
                lv("在当前界面没有发现梯队${(teamNo+1)}")
                return "-1/0"
            }
        }
        return ""
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

    fun isPreCombatUI() : Boolean{
        if (isPiexlEqual(103 , 58 , 255 , 255 , 255)
                && isPiexlEqual(169 , 65 , 255 , 255 , 255)
                && isPiexlEqual(142 , 116 , 255 , 255 , 255)
                && isPiexlEqual(305 , 46 , 255 , 255 , 255)
                && isPiexlEqual(345 , 69 , 255 , 255 , 255)
                && isPiexlEqual(705 , 85 , 255 , 255 , 255)
                && isPiexlEqual(677 , 103 , 255 , 255 , 255)
                && isPiexlEqual(691 , 57 , 255 , 255 , 255)
                && isPiexlEqual(703 , 115 , 255 , 255 , 255)
                ){
            lv("是开始作战前的部署画面")
            return true
        }
        else {
            lv("不是不是不是不是开始作战前的部署画面")
            return false
        }
    }

    open fun isCorrectZHANYIChoosed() : Boolean{
        return true
    }
    open fun isViewAtLocation1() : Boolean{
        return true
    }
    open fun isCharactorAtLocation1() : Int{
        return 1
    }

}
