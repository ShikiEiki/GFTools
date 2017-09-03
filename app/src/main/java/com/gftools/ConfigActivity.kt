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
    lateinit var adapter1: ArrayAdapter<String>
    lateinit var adapter2: ArrayAdapter<String>
    lateinit var adapter3: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(LayoutInflater.from(this) , R.layout.activity_config , null , false)
        setContentView(binding.root)
        adapter1 = ArrayAdapter<String>(this , android.R.layout.simple_spinner_item)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter1.add("第一梯队")
        adapter1.add("第二梯队")
        adapter1.add("第三梯队")
        adapter1.add("第四梯队")
        binding.mainForceSpinner.adapter = adapter1
        binding.mainForceSpinner.setSelection(GFTools.getMainForceIndex())
        binding.mainForceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                GFTools.setMainForceIndex(position)
            }
        }

        adapter2 = ArrayAdapter<String>(this , android.R.layout.simple_spinner_item)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter2.add("5-2E(4战小兵)")
        adapter2.add("5-4E(3战小兵+Boss)")
        binding.battleSpinner.adapter = adapter2
        binding.battleSpinner.setSelection(GFTools.getBattleIndex())
        binding.battleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                GFTools.setBattleIndex(position)
            }
        }

        adapter3 = ArrayAdapter<String>(this , android.R.layout.simple_spinner_item)
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter3.add("第一梯队")
        adapter3.add("第二梯队")
        adapter3.add("第三梯队")
        adapter3.add("第四梯队")
        binding.cannonFodderSpinner.adapter = adapter3
        binding.cannonFodderSpinner.setSelection(GFTools.getCannonFodderIndex())
        binding.cannonFodderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                GFTools.setCannonFodderIndex(position)
            }
        }
    }
}