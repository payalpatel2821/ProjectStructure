package com.task.newapp.ui.activities

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.task.newapp.R
import com.task.newapp.databinding.ActivityMainBinding
import com.task.newapp.ui.fragments.registration.ChatsFragment
import com.task.newapp.ui.fragments.registration.RegistrationStep1Fragment
import com.task.newapp.utils.setBlurLayout
import com.task.newapp.utils.showToast
import com.task.newapp.utils.setBlurLayout
import com.task.newapp.utils.showToast
import eightbitlab.com.blurview.RenderScriptBlur


class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityMainBinding

    val chatsFragment = ChatsFragment()
    val secondFragment = ChatsFragment()
    val thirdFragment = ChatsFragment()

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

        binding.bottomBar.onItemSelected = { position ->
            showToast("Item $position selected")
            setCurrentFragment(chatsFragment)

            when (position) {
                0 -> {
                    setCurrentFragment(chatsFragment)
                }
                1 -> {
                    setCurrentFragment(chatsFragment)
                }
                2 -> {
                    setCurrentFragment(chatsFragment)
                }
                3 -> {
                    setCurrentFragment(chatsFragment)
                }
                4 -> {
                    toggleRightDrawer()
                }
            }
        }

        binding.bottomBar.onItemReselected = { position ->
            showToast("Item $position re-selected")

            when (position) {
                0 -> {
                    setCurrentFragment(chatsFragment)
                }
                1 -> {
                    setCurrentFragment(chatsFragment)
                }
                2 -> {
                    setCurrentFragment(chatsFragment)
                }
                3 -> {
                    setCurrentFragment(chatsFragment)
                }
                4 -> {
                    toggleRightDrawer()
                }
            }
        }
//
//        binding.bottomBar.onItemLongClick = {
//            showToast("Item $it long click")
//        }

        setCurrentFragment(chatsFragment)
    }

    private fun toggleRightDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            binding.drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.activity_main_content, fragment)
            commit()
        }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_center -> {

            }
        }
    }
}