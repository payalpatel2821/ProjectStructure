package com.task.newapp.ui.activities.chat

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.task.newapp.R
import com.task.newapp.databinding.ActivityOneToOneChatBinding
import io.reactivex.disposables.CompositeDisposable

class OneToOneChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityOneToOneChatBinding
    private val mCompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_one_to_one_chat)
     //   setSupportActionBar(binding.toolbarLayout.activityMainToolbar)
     //   supportActionBar?.setDisplayShowTitleEnabled(false)
        initView()

    }

    private fun initView() {
        binding.toolbarLayout.txtTitle.text = getString(R.string.title_broadcast_list)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()

        }

        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.chat_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }


}