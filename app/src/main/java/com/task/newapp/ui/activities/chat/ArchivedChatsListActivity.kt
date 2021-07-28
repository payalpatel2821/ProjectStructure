package com.task.newapp.ui.activities.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.task.newapp.App
import com.task.newapp.R
import com.task.newapp.adapter.chat.ArchivedChatListAdapter
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityArchivedChatsListBinding
import com.task.newapp.interfaces.OnSocketEventsListener
import com.task.newapp.models.CommonResponse
import com.task.newapp.realmDB.*
import com.task.newapp.realmDB.models.Chats
import com.task.newapp.realmDB.models.UserArchive
import com.task.newapp.realmDB.models.UserHook
import com.task.newapp.ui.activities.BaseAppCompatActivity
import com.task.newapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

class ArchivedChatsListActivity : BaseAppCompatActivity(), View.OnClickListener, ArchivedChatListAdapter.OnChatItemClickListener, AdapterView.OnItemClickListener, OnSocketEventsListener,
    SearchView.OnQueryTextListener {
    private var chats = ArrayList<Chats>()
    private lateinit var chatsAdapter: ArchivedChatListAdapter
    lateinit var binding: ActivityArchivedChatsListBinding
    private val mCompositeDisposable = CompositeDisposable()
    private var isModified = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_archived_chats_list)
        onSocketEventsListener = this
        initView()
    }

    private fun initView() {
        binding.toolbarLayout.txtTitle.text = getString(R.string.title_archived_list)
        setSupportActionBar(binding.toolbarLayout.activityMainToolbar)
        initSearchView()
        setAdapter()

    }

    private fun initSearchView() {
        binding.searchLayout.searchView.setOnQueryTextListener(this)
        binding.searchLayout.searchView.onActionViewExpanded()
        binding.searchLayout.searchView.clearFocus()

        binding.searchLayout.searchView.setOnCloseListener {
//            issearch = false
//            if (!isAPICallRunning) initData("", 0, "main")
            false
        }
        binding.searchLayout.searchView.findViewById<View>(R.id.search_close_btn)
            .setOnClickListener(View.OnClickListener {
                binding.searchLayout.searchView.isActivated = true
                binding.searchLayout.searchView.setQuery("", false)
                binding.searchLayout.searchView.isIconified = true
                binding.searchLayout.searchView.onActionViewExpanded()
                binding.searchLayout.searchView.clearFocus()
                binding.searchLayout.searchView.findViewById<View>(R.id.search_button).performClick()
            })
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
//        issearch = true
//        searchtxt = query
        Log.e("search", "search text change")
//        if (!isAPICallRunning) initData(searchtxt, 0, "main")
//        search = false
//        initScrollListener()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
        }
    }

    private fun setAdapter() {
        prepareChatListAdapterModel(getAllArchivedChat())
        if (chats.isNotEmpty()) {
            //if (chatsAdapter == null) {
            chatsAdapter = ArchivedChatListAdapter(this, this)
            chatsAdapter.setOnItemClickListener(this)
            binding.rvArchivedChat.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.rvArchivedChat.adapter = chatsAdapter
            //  }
            chatsAdapter.doRefresh(chats)
        }

        showHideEmptyView()
    }

    private fun prepareChatListAdapterModel(chatsList: List<Chats>) {
        if (chats.isNotEmpty())
            chats.clear()
        chats.addAll(chatsList)

    }

    override fun onClearChatClick(position: Int, chats: Chats) {
        showToast("Clear $position")
    }

    override fun onUnarchiveChatClick(position: Int, chats: Chats) {
        showToast("Archive $position")
        callUnarchiveChatAPI(chats)
    }

    override fun onItemClick(adapterView: AdapterView<*>?, p1: View?, position: Int, id: Long) {
        showToast("Item Clicked $position")
    }

    private fun showHideEmptyView() {
        if (chats.isEmpty()) {
            //binding.llEmptyChat.visibility = View.VISIBLE
            binding.rvArchivedChat.visibility = View.GONE
            //binding.divider.visibility = View.GONE
            //binding.searchView.visibility = View.GONE
        } else {
            //binding.llEmptyChat.visibility = View.GONE
            binding.rvArchivedChat.visibility = View.VISIBLE
            //binding.divider.visibility = View.VISIBLE
            //binding.searchView.visibility = View.VISIBLE
        }
    }

    private fun callUnarchiveChatAPI(chats: Chats) {
        try {
            if (!isNetworkConnected()) {
                showToast(getString(R.string.no_internet))
                return
            }

            openProgressDialog(this)
            val type = if (chats.is_group) Constants.group else Constants.friend
            val archiveId = chats.id
            val isArchive = if (chats.is_archive) 0 else 1
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.type to type,
                Constants.archive_id to archiveId,
                Constants.is_archive to isArchive,
            )
            mCompositeDisposable.add(
                ApiClient.create()
                    .archiveChat(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<CommonResponse>() {
                        override fun onNext(commonResponse: CommonResponse) {
                            isModified = true
                            if (commonResponse.success == 1) {
                                if (chats.is_hook) {
                                    updateChatsIsHook(archiveId)
                                    deleteHook(if (type == Constants.group) UserArchive.PROPERTY_group_id else UserArchive.PROPERTY_friend_id, archiveId)
                                }
                                updateChatsIsArchive(archiveId)
                                if (isArchive == 1) {
                                    deleteArchive(if (type == Constants.group) UserArchive.PROPERTY_group_id else UserArchive.PROPERTY_friend_id, archiveId)
                                } else {
                                    val userArchive = UserArchive()
                                    userArchive.id = archiveId
                                    userArchive.user_id = App.fastSave.getInt(Constants.prefUserId, 0)
                                    if (chats.is_group) {
                                        userArchive.friend_id = 0
                                        userArchive.group_id = chats.id
                                        userArchive.is_group = 1
                                        userArchive.is_friend = 0
                                    } else {
                                        userArchive.friend_id = chats.id
                                        userArchive.group_id = 0
                                        userArchive.is_group = 0
                                        userArchive.is_friend = 1

                                    }
                                    if (chats.is_hook) {
                                        deleteHook(if (chats.is_group) UserHook.PROPERTY_group_id else UserHook.PROPERTY_friend_id, chats.id)
                                    }
                                    insertArchiveData(userArchive)
                                }
                                setAdapter()
                            }

                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                            hideProgressDialog()
                        }

                        override fun onComplete() {
                            hideProgressDialog()
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * update adapter when user comes online or goes offline
     *
     * @param userId : UserId of online/offline user
     * @param status : if user is online it contains true otherwise false
     */
    private fun updateOnlineUser(userId: Int, status: Boolean) {
        val position = getChatPosition(chats, userId)
        showLog("find position :::", position.toString())
        if (position != -1) {
            chatsAdapter.updateOnlineOfflineStatus(position, status)
        }
    }

    override fun onBackPressed() {
        // super.onBackPressed()
        val intent = Intent().putExtra(Constants.isModified, isModified)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onOnlineOfflineSocketEvent(userId: Int, status: Boolean) {
        updateOnlineUser(userId, status)
    }

    override fun onPostLikeDislikeEvent() {

    }
}