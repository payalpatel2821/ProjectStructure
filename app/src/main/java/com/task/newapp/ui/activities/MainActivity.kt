package com.task.newapp.ui.activities

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.appizona.yehiahd.fastsave.FastSave
import com.task.newapp.App
import com.task.newapp.R
import com.task.newapp.databinding.ActivityMainBinding
import com.task.newapp.interfaces.OnSocketEventsListener
import com.task.newapp.realmDB.clearDatabase
import com.task.newapp.ui.activities.profile.GroupProfileActivity
import com.task.newapp.ui.activities.profile.MyProfileActivity
import com.task.newapp.ui.activities.profile.OtherUserProfileActivity
import com.task.newapp.ui.fragments.chat.ChatsFragment
import com.task.newapp.ui.fragments.registration.PostFragment
import com.task.newapp.utils.*

class MainActivity : BaseAppCompatActivity(), View.OnClickListener, OnSocketEventsListener {

    lateinit var binding: ActivityMainBinding

    val chatsFragment = ChatsFragment()
    val postFragment = PostFragment()
    val thirdFragment = ChatsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        onSocketEventsListener = this
        setupNavigationDrawer()
        setupBottomNavigationBar()
    }

    private fun setupNavigationDrawer() {
        setBlurLayout(this@MainActivity, binding.root as ViewGroup, binding.navDrawerLayout.blurView)
        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this, binding.drawerLayout,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        ) {
            override fun onDrawerClosed(drawerView: View) {
                // Triggered once the drawer closes
                super.onDrawerClosed(drawerView)
                try {
                    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }

            override fun onDrawerOpened(drawerView: View) {
                // Triggered once the drawer opens
                super.onDrawerOpened(drawerView)
                try {
                    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                } catch (e: Exception) {
                    e.stackTrace
                }
            }
        }
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setupBottomNavigationBar() {
        binding.bottomBar.setActiveItem(0)
//        binding.bottomBar.setBadge(2)
//        binding.bottomBar.removeBadge(2)

        binding.bottomBar.onItemSelected = { position ->
            showToast("Item $position selected")

            when (position) {
                0 -> {
                    setCurrentFragment(chatsFragment)
                }
                1 -> {
                    setCurrentFragment(postFragment)
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
                    setCurrentFragment(postFragment)
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
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.activity_main_content, fragment)
            commit()
        }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.txt_chat_appearance -> {
                showToast(getString(R.string.chat_appearance))
            }
            R.id.txt_notifications -> {
                showToast(getString(R.string.notifications))
            }
            R.id.txt_post -> {
                showToast(getString(R.string.post))
            }
            R.id.txt_story -> {
                showToast(getString(R.string.story))
            }
            R.id.txt_privacy -> {
                showToast(getString(R.string.privacy))
            }
            R.id.txt_data_storage -> {
                showToast(getString(R.string.data_storage))
            }
            R.id.txt_help -> {
                showToast(getString(R.string.help))
            }
            R.id.txt_about -> {
                showToast(getString(R.string.about))
            }
            R.id.txt_recent_calls -> {
                showToast(getString(R.string.recent_calls))
            }
            R.id.txt_logout -> {
                FastSave.getInstance().saveBoolean(Constants.isLogin, false)
                launchActivity<LoginActivity> { }
                showToast(getString(R.string.logout))
                clearDatabase()
                disconnectSocket(App.fastSave.getInt(Constants.prefUserId, 0), App.fastSave.getString(Constants.prefUserName, ""))
                finish()

            }
            R.id.img_profile->{
                launchActivity<MyProfileActivity> {  }
            }
            R.id.img_center -> {
//                launchActivity<OtherUserProfileActivity> {  }
                launchActivity<GroupProfileActivity> { }
            }
        }
    }

    override fun onBackPressed() {
        if (binding.bottomBar.getActiveItem() == 1) {
            if (postFragment.myBottomSheetMoment.isVisible) {
                postFragment.myBottomSheetMoment.dismiss()
            }
            if (postFragment.myBottomSheetThought.isVisible) {
                postFragment.myBottomSheetThought.dismiss()
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onOnlineOfflineSocketEvent(userId: Int, status: Boolean) {
        if (binding.bottomBar.getActiveItem() == 0) {
            chatsFragment.updateOnlineUser(userId, status)
        }
    }

    override fun onPostLikeDislikeEvent() {
        if (binding.bottomBar.getActiveItem() == 1) {
            postFragment.postLikeDislike()
        }
    }


}