package com.task.newapp.ui.activities

import android.Manifest
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.aghajari.zoomhelper.ZoomHelper
import com.appizona.yehiahd.fastsave.FastSave
import com.google.gson.Gson
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.task.newapp.App
import com.task.newapp.R
import com.task.newapp.databinding.ActivityMainBinding
import com.task.newapp.models.socket.PostSocket
import com.task.newapp.realmDB.clearDatabase
import com.task.newapp.realmDB.models.ChatList
import com.task.newapp.ui.activities.chat.SelectFriendsActivity
import com.task.newapp.ui.activities.chat.broadcast.BroadcastListActivity
import com.task.newapp.ui.activities.profile.MyProfileActivity
import com.task.newapp.ui.activities.setting.NotificationsActivity
import com.task.newapp.ui.fragments.chat.ChatsFragment
import com.task.newapp.ui.fragments.contact.ContactBottomSheet
import com.task.newapp.ui.fragments.post.PostFragment
import com.task.newapp.utils.*
import com.task.newapp.utils.Constants.Companion.SelectFriendsNavigation
import com.task.newapp.utils.contactUtils.ContactsHelper
import com.task.newapp.workmanager.WorkManagerScheduler
import io.reactivex.disposables.CompositeDisposable


class MainActivity : BaseAppCompatActivity(), View.OnClickListener, PostFragment.OnHideShowBottomSheet {

    private lateinit var contactBottomSheetFragment: ContactBottomSheet
    lateinit var binding: ActivityMainBinding
    val chatsFragment = ChatsFragment()
    val postFragment = PostFragment()
    val thirdFragment = ChatsFragment()
    private val mCompositeDisposable = CompositeDisposable()

    override fun onStart() {
        super.onStart()
        runWithPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS) {
            ContactsHelper(this).getContacts { (contacts, contactSync) ->
                val gson = Gson()
                val json: String = gson.toJson(contactSync)
                FastSave.getInstance().saveString(Constants.contact, json)
                //callAPISyncContactToAPI(contactSync as ArrayList<ContactSyncAPIModel>, mCompositeDisposable)
                WorkManagerScheduler.refreshPeriodicWorkContact(/*App.getAppInstance()*/this)

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        onSocketEventsListener = this
        WorkManagerScheduler.refreshPeriodicWork(App.getAppInstance())
        callAPIGetAllNotification(mCompositeDisposable)
        initView()
        //Add New
        ZoomHelper.getInstance().minScale = 1f
    }

    override fun onDestroy() {
        super.onDestroy()
        WorkManagerScheduler.cancel(App.getAppInstance())
        if (isFinishing && App.fastSave.getBoolean(Constants.isLogin, false))
            disconnectSocket(App.fastSave.getInt(Constants.prefUserId, 0), App.fastSave.getString(Constants.prefUserName, ""))
    }

    override fun onStop() {
        super.onStop()
    }

    private fun initView() {
        setSupportActionBar(binding.activityMainToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.imgProfile.load(url = getCurrentUserProfilePicture(), isProfile = true, name = getCurrentUserFullName(), color = getCurrentUserProfileColor())
        setupNavigationDrawer()
        setupBottomNavigationBar()
        contactBottomSheetFragment = ContactBottomSheet()
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
            //showToast("Item $position selected")

            when (position) {
                0 -> {
                    setCurrentFragment(chatsFragment)
                }
                1 -> {
                    setCurrentFragment(postFragment)
                }
                2 -> {
                    setCurrentFragment(postFragment)
                }
                3 -> {
                    setCurrentFragment(postFragment)
                }
                4 -> {
                    toggleRightDrawer()
                }
            }
        }

        binding.bottomBar.onItemReselected = { position ->
            //showToast("Item $position re-selected")

            when (position) {
                0 -> {
                    setCurrentFragment(chatsFragment)
                }
                1 -> {
                    setCurrentFragment(postFragment)
                }
                2 -> {
                    setCurrentFragment(postFragment)
                }
                3 -> {
                    setCurrentFragment(postFragment)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_broadcast_list ->
                launchActivity<BroadcastListActivity> { }

            R.id.action_create_group -> launchActivity<SelectFriendsActivity> {
                putExtra(Constants.bundle_navigate_from, SelectFriendsNavigation.FROM_CREATE_GROUP)
            }

            R.id.action_secret_chat, R.id.action_emergency_alert -> showToast("Coming soon...")

            R.id.action_search -> {
                if (binding.bottomBar.getActiveItem() == 0) {
                    if (chatsFragment.isShowSearchBar) {
                        chatsFragment.showHideSearchView(false)
                        item.icon = ContextCompat.getDrawable(this, R.drawable.ic_search_chat)
                    } else {
                        chatsFragment.showHideSearchView(true)
                        item.icon = ContextCompat.getDrawable(this, R.drawable.ic_close_contact)
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setCurrentFragment(fragment: Fragment) {
        if (fragment is PostFragment) {
            binding.activityMainAppbarlayout.visibility = View.GONE
            postFragment.setOnHideShowBottomSheet(this)
        } else {
            binding.activityMainAppbarlayout.visibility = View.VISIBLE
        }

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.activity_main_content, fragment)
            commit()
        }
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
//                showToast(getString(R.string.notifications))
                launchActivity<NotificationsActivity> {  }
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
                // showToast(getString(R.string.logout))
                clearDatabase()
                disconnectSocket(App.fastSave.getInt(Constants.prefUserId, 0), App.fastSave.getString(Constants.prefUserName, ""))
                finish()
            }
            R.id.img_profile -> {
                launchActivity<MyProfileActivity> { }
            }
            R.id.img_center -> {
                contactBottomSheetFragment!!.show(supportFragmentManager, "Dialog")

//                launchActivity<ContactActivity> { }
            }
        }
    }

    override fun onBackPressed() {
        if (binding.bottomBar.getActiveItem() == 1) {
            closeMomentSheet()
            closeThoughtSheet()
            postFragment.expandCommentSheet()
        } else {
            super.onBackPressed()
        }

        var orientation = resources.configuration.orientation
        when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onOnlineOfflineSocketEvent(userId: Int, status: Boolean) {
        super.onOnlineOfflineSocketEvent(userId, status)
        if (binding.bottomBar.getActiveItem() == 0) {
            chatsFragment.updateOnlineUser(userId, status)
        }
    }

    override fun onPostLikeDislikeSocketEvent(postSocket: PostSocket) {
        super.onPostLikeDislikeSocketEvent(postSocket)
        showLog("MainActivity", "Post like Dislike")
        if (binding.bottomBar.getActiveItem() == 1) {
            postFragment.postLikeDislike(postSocket)
        }
    }

    override fun onAddPostCommentSocketEvent(postSocket: PostSocket) {
        showLog("MainActivity", "onAddPostCommentSocketEvent")
        if (binding.bottomBar.getActiveItem() == 1) {
            if (postFragment.isOpenCommentBottomSheet) {
                postFragment.myBottomSheetPostCommentListFragment!!.addPostComment(postSocket)
            } else {
                postFragment.addPostComment(postSocket)
            }
        }
    }

    override fun onAddPostCommentReplySocketEvent(postSocket: PostSocket) {
        showLog("MainActivity", "onAddPostCommentReplySocketEvent")
        if (binding.bottomBar.getActiveItem() == 1) {
            if (postFragment.isOpenCommentBottomSheet) {
                postFragment.myBottomSheetPostCommentListFragment!!.addPostCommentReply(postSocket)
            } else {
                postFragment.addPostCommentReply(postSocket)
            }
        }
    }

    override fun onDeletePostCommentSocketEvent(postSocket: PostSocket) {
        showLog("MainActivity", "onAddPostCommentSocketEvent")
        if (binding.bottomBar.getActiveItem() == 1) {
            if (postFragment.isOpenCommentBottomSheet) {
                postFragment.myBottomSheetPostCommentListFragment!!.deletePostComment(postSocket)
            } else {
                postFragment.deletePostComment(postSocket)
            }
        }
    }

    override fun onDeletePostSocketEvent(postSocket: PostSocket) {
        showLog("MainActivity", "onAddPostCommentSocketEvent")
        if (binding.bottomBar.getActiveItem() == 1) {
//            if (postFragment.isOpenCommentBottomSheet) {
////                postFragment.myBottomSheetPostCommentListFragment!!.deletePostComment(postSocket)
//            } else {
            postFragment.deletePost(postSocket)
//            }
        }
    }

    override fun onNewMessagePrivateSocketEvent(chatList: ChatList) {
        super.onNewMessagePrivateSocketEvent(chatList)
        if (binding.bottomBar.getActiveItem() == 0) {
            chatsFragment.updateChatListItem(chatList)
        }
    }

    override fun onUserTypingSocketEvent(receiverId: Int) {
        super.onUserTypingSocketEvent(receiverId)
        if (binding.bottomBar.getActiveItem() == 0) {
            chatsFragment.showHideTypingIndicator(receiverId, true)
        }
    }

    override fun onUserStopTypingSocketEvent(receiverId: Int) {
        super.onUserStopTypingSocketEvent(receiverId)
        if (binding.bottomBar.getActiveItem() == 0) {
            chatsFragment.showHideTypingIndicator(receiverId, false)
        }
    }

    override fun hideShowBottomSheet(visibility: Int) {
        binding.blurView.visibility = if (visibility == View.VISIBLE) View.VISIBLE else View.GONE
    }

    private fun closeMomentSheet() {
//        if (postFragment.isInitializedMoment()) {
        postFragment.myBottomSheetMoment?.let {
            if (postFragment.myBottomSheetMoment!!.isVisible) {
                postFragment.myBottomSheetMoment!!.dismiss()
            }
        }
//        }
    }

    private fun closeThoughtSheet() {
//        if (postFragment.isInitializedThought()) {
        postFragment.myBottomSheetThought?.let {
            if (postFragment.myBottomSheetThought!!.isVisible) {
                postFragment.myBottomSheetThought!!.dismiss()
            }
//            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return ZoomHelper.getInstance().dispatchTouchEvent(ev!!, this) || super.dispatchTouchEvent(ev)
    }

    override fun onResume() {
        super.onResume()

//        ContactsHelper(this).getContacts { contacts ->
//            val gson = Gson()
//            val json: String = gson.toJson(contacts)
//            FastSave.getInstance().saveString(Constants.contact, json)
////            setAdapter(contacts)
//        }
    }

}