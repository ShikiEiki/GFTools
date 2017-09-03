package com.gftools;

import java.util.ArrayList;
import java.util.Collections;

//用来调试算法题目的类,与主程序逻辑无关
public class Solution {
    public ArrayList<String> Permutation(String str) {
        ArrayList<String> result = new ArrayList<String>();
        char[] chars = str.toCharArray();
        if (str == null || str.length() == 0){

        }
        else if (str.length() == 1){
            result.add(str);
        }
        else {
            xxx(chars , chars.length , result);
        }
        Collections.sort(result);
        return result;
    }
    public void xxx (char[] chars , int limit , ArrayList<String> result){
        if (limit == 2){
            result.add(String.copyValueOf(chars));
            if (chars[chars.length - 1] != chars[chars.length - 2]){
                char temp = chars[chars.length - 1];
                chars[chars.length - 1] = chars[chars.length - 2];
                chars[chars.length - 2] = temp;
                result.add(String.copyValueOf(chars));
            }
            return;
        }
        xxx(chars , limit - 1 , result);
        for (int i = 1; i < chars.length; i++) {
            char chara = chars[i];
            boolean b = false;
            for (int j = 0 ; j < i ; j++) {
                char charb = chars[j];
                if (chara == charb){
                    b = true;
                    break;
                }
            }
            if (!b){
                char temp = chars[0];
                chars[0] = chars[i];
                chars[i] = temp;
                xxx(chars , limit-1 , result);
            }
        }
    }
}
