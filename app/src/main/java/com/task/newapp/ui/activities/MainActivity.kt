package com.task.newapp.ui.activities

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.task.newapp.R
import com.task.newapp.databinding.ActivityMainBinding
import com.task.newapp.utils.setBlurLayout
import com.task.newapp.utils.showToast
import com.task.newapp.utils.setBlurLayout
import com.task.newapp.utils.showToast
import eightbitlab.com.blurview.RenderScriptBlur


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setBlurLayout(this@MainActivity, binding.root as ViewGroup, binding.navDrawerLayout.blurView)
        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this, binding.drawerLayout,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        ) {
            override fun onDrawerClosed(drawerView: View) {
                // Triggered once the drawer closes
                super.onDrawerClosed(drawerView)
                try {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }

            override fun onDrawerOpened(drawerView: View) {
                // Triggered once the drawer opens
                super.onDrawerOpened(drawerView)
                try {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }
        }
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        //        binding.bottomBar.setActiveItem(1)
//        binding.bottomBar.setBadge(2)
//        binding.bottomBar.removeBadge(2)

//        binding.bottomBar.onItemSelected = {
//            showToast("Item $it selected")
//        }
//
//        binding.bottomBar.onItemReselected = {
//            showToast("Item $it re-selected")
//        }
//
//        binding.bottomBar.onItemLongClick = {
//            showToast("Item $it long click")
//        }

    }

    private fun toggleRightDrawer() {
        if (binding.drawerLayout.isDrawerOpen(binding.drawerLayout)) {
            binding.drawerLayout.closeDrawer(binding.drawerLayout)
        } else {
            binding.drawerLayout.openDrawer(binding.drawerLayout)
        }
    }
}