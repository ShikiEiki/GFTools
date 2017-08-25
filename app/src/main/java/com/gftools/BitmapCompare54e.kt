package com.gftools

import android.graphics.Bitmap
import com.gftools.utils.*

/**
 * Created by FH on 2017/8/10.
 */

class BitmapCompare54e(bitmap : Bitmap) : BaseBitmapCompare(bitmap) {
    override fun isCorrectZHANYIChoosed() : Boolean{
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

    override fun isViewAtLocation1() : Boolean{
        if (isPiexlEqual(844 , 520 , 255 , 255 , 255)
                && isPiexlEqual(1050 , 334 , 255 , 255 , 255)
                && isPiexlEqual(1276, 287 , 255 , 255 , 255)
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
        if (isPiexlEqual(1389 , 825, 255 , 255 , 255)
                && isPiexlEqual(1408 , 893 , 255 , 255 , 255)
                && isPiexlEqual(793 , 473 , 255 , 255 , 255)
                && isPiexlEqual(1368 , 754 , 255 , 255 , 255)
                && isPiexlEqual(872 , 486 , 255 , 255 , 255)
                ){
            lv("视角在位置2")
            return true
        }
        lv("视角不在不在不在位置2")
        return false
    }

    fun isViewAtLocation3() : Boolean{
        if (isPiexlEqual(784 , 449, 206 , 105 , 16)
                && isPiexlEqual(410 , 700 , 222 , 125 , 66)
                && isPiexlEqual(451 , 997 , 255 , 255 , 255)
                && isPiexlEqual(871 , 992 , 255 , 255 , 255)
                && isPiexlEqual(1299 , 737 , 255 , 255 , 255)
                ){
            lv("视角在位置2")
            return true
        }
        lv("视角不在不在不在位置2")
        return false
    }

    fun isCharactorAtHome() : Boolean{
        return isPiexlEqual(1364 , 483 , 255 , 255 , 255)
                && isPiexlEqual(1453, 490 , 255 , 255 , 255)
                && isPiexlEqual(1507, 499 , 255 , 255 , 255)
    }

    override fun isCharactorAtLocation1() : Int{
        if (isPiexlEqual(1464 , 434 , 255 , 255 , 255)
                && isPiexlEqual(1552 , 436 , 255 , 255 , 255)
                && isPiexlEqual(1605 , 447 , 255 , 255 , 255)
                ){
            if (isPiexlEqual(1314 , 365 , 255 , 186 , 0)
                    && isPiexlEqual(1432 , 491 , 255 , 186 , 0)
                    && isPiexlEqual(1191 , 492 , 255 , 186 , 0)
                    && isPiexlEqual(1313 , 599 , 255 , 186 , 0)
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
        if (isPiexlEqual(762 , 358 , 255 , 255 , 255)
                && isPiexlEqual(974 , 383 , 255 , 255 , 255)
                && isPiexlEqual(880 , 363 , 255 , 255 , 255)
                ){
            if (isPiexlEqual(681 , 300 , 255 , 186 , 0)
                    && isPiexlEqual(563 , 428 , 255 , 186 , 0)
                    && isPiexlEqual(681 , 542 , 255 , 186 , 0)
                    && isPiexlEqual(807 , 428 , 255 , 186 , 0)
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
        if (isPiexlEqual(258 , 523 , 255 , 255 , 255)
                && isPiexlEqual(340 , 542 , 255 , 255 , 255)
                && isPiexlEqual(469 , 550 , 255 , 255 , 255)
                ){
            if (isPiexlEqual(59 , 594 , 255 , 186 , 0)
                    && isPiexlEqual(179 , 469 , 255 , 186 , 0)
                    && isPiexlEqual(291 , 595 , 255 , 186 , 0)
                    && isPiexlEqual(178 , 695 , 255 , 186 , 0)
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

    fun isCharactorAtLocation4() : Int{
        if (isPiexlEqual(1493 , 448 , 255 , 255 , 255)
                && isPiexlEqual(1601 , 440 , 255 , 255 , 255)
                && isPiexlEqual(1695 , 456 , 255 , 255 , 255)
                ){
            if (isPiexlEqual(1403 , 372 , 255 , 186 , 0)
                    && isPiexlEqual(1402 , 615 , 255 , 186 , 0)
                    && isPiexlEqual(1283 , 499 , 255 , 186 , 0)
                    && isPiexlEqual(1525 , 501 , 255 , 186 , 0)
                    ){
                lv("角色在位置4,并且已选中")
                return 2
            }
            else{
                lv("角色在位置4,但是未选中")
                return 1
            }
        }
        else {
            lv("角色不在不在不在不在位置4")
            return 0
        }
    }

    fun isCharactorAtLocation5() : Boolean{
        if (isPiexlEqual(852 , 537 , 255 , 255 , 255)
                && isPiexlEqual(959 , 525 , 255 , 255 , 255)
                && isPiexlEqual(1051 , 547 , 255 , 255 , 255)
                ){
            lv("角色在位置5")
            return true
        }
        else {
            lv("角色不在不在不在不在位置5")
            return false
        }
    }
}
