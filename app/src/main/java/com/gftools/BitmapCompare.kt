package com.gftools

import android.graphics.Bitmap
import com.gftools.utils.*

/**
 * Created by FH on 2017/8/10.
 */

class BitmapCompare(bitmap : Bitmap){
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


    /**
     * 判断是否在视角2,会自动添加-30至30像素的修正偏移量自动重试,如果加上偏移量也判断不是视角2,则返回-999,
     * 如果加上偏移量判断是视角2,则返回偏移量
     */
    fun isViewAtLocation2(originCorrection : Int) : Int{
        if (isPiexlEqual(581 , 380+originCorrection , 255 , 255 , 255)
                && isPiexlEqual(544, 585+originCorrection , 255 , 255 , 255)
                && isPiexlEqual(763, 633+originCorrection , 255 , 255 , 255)
                && isPiexlEqual(962, 381+originCorrection , 255 , 255 , 255)
                && isPiexlEqual(537, 977+originCorrection , 255 , 255 , 255)
                ){
            lv("视角在位置2,需要添加竖直修正量为 $originCorrection")
            return originCorrection
        }
        for (i : Int in -30 .. 30){
            if (isPiexlEqual(581 , 380+i , 255 , 255 , 255)
                    && isPiexlEqual(544 , 585+i , 255 , 255 , 255)
                    && isPiexlEqual(763 , 633+i , 255 , 255 , 255)
                    && isPiexlEqual(962 , 381+i , 255 , 255 , 255)
                    && isPiexlEqual(537 , 977+i , 255 , 255 , 255)
                    ){
                lv("视角在位置2,需要添加竖直修正量为 $i")
                return i
            }
        }
        lv("视角不在不在不在位置2")
        return -999
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
                for (i:Int in 0 .. 4){
                    if (isPiexlEqual(125 , 225+(148*i) , 49 , 57 , 49)
                            && isPiexlEqual(132 , 242+(148*i) , 49 , 53 , 49)
                            && isPiexlEqual(132 , 267+(148*i) , 49 , 49 , 49)
                            ){
                        if (isPiexlEqual(18 , 251+(148*i) , 239 , 170 , 0)){
                            lv("在第${(i+1)}行发现梯队${(teamNo+1)},并且已经选中")
                            return "$i/1"
                        }
                        else {
                            lv("在第${(i+1)}行发现梯队${(teamNo+1)},但是还未选中")
                            return "$i/0"
                        }
                    }
                }
                lv("在当前界面没有发现梯队${(teamNo+1)}")
                return "-1/0"
            }
            1 -> {
                for (i in 0 .. 4){
                    if (isPiexlEqual(123 , 377+(148*(i-1)) , 49 , 57 , 49)
                            && isPiexlEqual(136 , 398+(148*(i-1)) , 49 , 53 , 49)
                            && isPiexlEqual(142 , 420+(148*(i-1)) , 49 , 53 , 49)
                            ){
                        if (isPiexlEqual(18 , 251+(148*i) , 239 , 170 , 0)){
                            lv("在第${(i+1)}行发现梯队${(teamNo+1)},并且已经选中")
                            return "$i/1"
                        }
                        else {
                            lv("在第${(i+1)}行发现梯队${(teamNo+1)},但是还未选中")
                            return "$i/0"
                        }
                    }
                }
                lv("在当前界面没有发现梯队${(teamNo+1)}")
                return "0/0"
            }

            2 -> {
                for (i in 0 .. 4){
                    if (isPiexlEqual(122 , 523+(148*(i-2)) , 49 , 49 , 49)
                            && isPiexlEqual(132 , 538+(148*(i-2)) , 49 , 53 , 49)
                            && isPiexlEqual(123 , 552+(148*(i-2)) , 49 , 49 , 49)
                            ){
                        if (isPiexlEqual(18 , 251+(148*i) , 239 , 170 , 0)){
                            lv("在第${(i+1)}行发现梯队${(teamNo+1)},并且已经选中")
                            return "$i/1"
                        }
                        else {
                            lv("在第${(i+1)}行发现梯队${(teamNo+1)},但是还未选中")
                            return "$i/0"
                        }
                    }
                }
                lv("在当前界面没有发现梯队${(teamNo+1)}")
                return "0/0"
            }
            3 -> {
                for (i in 0 .. 4){
                    if (isPiexlEqual(141 , 657+(148*(i-3)) , 49 , 57 , 49)
                            && isPiexlEqual(121 , 700+(148*(i-3)) , 49 , 49 , 49)
                            && isPiexlEqual(142 , 711+(148*(i-3)) , 49 , 49 , 49)
                            ){
                        if (isPiexlEqual(18 , 251+(148*i) , 239 , 170 , 0)){
                            lv("在第${(i+1)}行发现梯队${(teamNo+1)},并且已经选中")
                            return "$i/1"
                        }
                        else {
                            lv("在第${(i+1)}行发现梯队${(teamNo+1)},但是还未选中")
                            return "$i/0"
                        }
                    }
                }
                lv("在当前界面没有发现梯队${(teamNo+1)}")
                return "0/0"
            }
        }
        return ""
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
    fun isCharactorAtLocation4_View2(correction : Int) : Int{
        if (isPiexlEqual(152 , 270+correction , 255 , 255 , 255)
                    && isPiexlEqual(235 , 272+correction , 255 , 255 , 255)
                    && isPiexlEqual(261 , 259+correction , 255 , 255 , 255)
                ){
            lv("角色在视角2下的位置4,需要竖直修正量$correction")
            return correction
        }
        else {
            for (i : Int in -30..30){
                if (isPiexlEqual(152 , 270+i , 255 , 255 , 255)
                        && isPiexlEqual(235 , 272+i, 255 , 255 , 255)
                        && isPiexlEqual(261 , 259+i, 255 , 255 , 255)
                        ){
                    lv("角色在视角2下的位置4,需要竖直修正量$i")
                    return i
                }
            }
            lv("角色不在不在不在不在视角2下的位置4")
            return -999
        }
    }

    fun isCharactorAtLocation4_View2_selected(correction : Int) : Int{
        if (isPiexlEqual(63 , 200+correction , 255 , 186 , 0)
                && isPiexlEqual(176 , 323+correction , 255 , 186, 0)
                && isPiexlEqual(63 , 431+correction , 255 , 186 , 0)
                ){
            lv("角色在视角2下的位置4并且被选中,需要竖直修正量$correction")
            return correction
        }
        else {
            for (i : Int in -30..30){
                if (isPiexlEqual(63 , 200+i , 255 , 186 , 0)
                        && isPiexlEqual(176 , 323+i, 255 , 186, 0)
                        && isPiexlEqual(63 , 431+i, 255 , 186 , 0)
                        ){
                    lv("角色在视角2下的位置4并且被选中,需要竖直修正量$i")
                    return i
                }
            }
            lv("角色在视角2下的位置4,并没有被选中")
            return -999
        }
    }

    fun isCharactorAtLocation5(correction: Int) : Int{
        if (isPiexlEqual(128 , 594+correction , 255 , 255 , 255)
            && isPiexlEqual(186 , 592+correction , 255 , 255 , 255)
            && isPiexlEqual(328 , 604+correction , 255 , 255 , 255)
                ){
            lv("角色在位置5,需要竖直修正量$correction")
            return correction
        }
        else {
            for (i : Int in -30..30){
                if (isPiexlEqual(128 , 594+i , 255 , 255 , 255)
                        && isPiexlEqual(186 , 592+i, 255 , 255 , 255)
                        && isPiexlEqual(328 , 604+i, 255 , 255 , 255)
                        ){
                    lv("角色在位置5,需要竖直修正量$i")
                    return i
                }
            }
            lv("角色不在不在不在不在位置5")
            return -999
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
