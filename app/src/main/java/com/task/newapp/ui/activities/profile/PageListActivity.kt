package com.task.newapp.ui.activities.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.View
import androidx.databinding.DataBindingUtil
import com.task.newapp.R
import com.task.newapp.databinding.ActivityPageListBinding
import com.task.newapp.databinding.ActivityPostListBinding
import com.task.newapp.utils.flipAnimation

class PageListActivity : AppCompatActivity() {

    lateinit var binding: ActivityPageListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_page_list)
        binding.segmentText.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.rb_mobile -> {
                }
                R.id.rb_email -> {
                }
            }
        }

    }

    fun onClick(view:View){
        when(view!!.id){
            R.id.iv_back->{
                onBackPressed()
            }
        }
    }
}