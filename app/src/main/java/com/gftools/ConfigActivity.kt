package com.gftools

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.gftools.databinding.ActivityConfigBinding

/**
 * Created by FH on 2017/8/10.
 */

class ConfigActivity : Activity(){
    lateinit var binding : ActivityConfigBinding
    lateinit var adapter : ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(LayoutInflater.from(this) , R.layout.activity_config , null , false)
        setContentView(binding.root)
        adapter = ArrayAdapter<String>(this , android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter.add("第一梯队")
        adapter.add("第二梯队")
        adapter.add("第三梯队")
        adapter.add("第四梯队")
        binding.spinner1.adapter = adapter
        binding.spinner1.setSelection(GFTools.getMainForceIndex())
        binding.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                GFTools.setMainForce(position)
            }
        }
    }
}