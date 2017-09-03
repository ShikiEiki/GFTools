package com.gftools;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;

import com.gftools.databinding.TestActivityBinding;

/**
 * Created by FH on 2017/9/3.
 */

public class TestActivity extends BaseActivity{
    TestActivityBinding binding;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.w("FH" , "!!!!");
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(this) , R.layout.test_activity , null , false);
        setContentView(binding.getRoot());
    }
}
