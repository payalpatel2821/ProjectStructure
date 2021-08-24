package com.task.newapp.ui.fragments.chat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.task.newapp.App
import com.task.newapp.R
import com.task.newapp.R.string
import com.task.newapp.adapter.chat.ChatListAdapter
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.FragmentChatBinding
import com.task.newapp.models.CommonResponse
import com.task.newapp.models.chat.ChatModel
import com.task.newapp.models.chat.ResponseGetUnreadMessage
import com.task.newapp.models.chat.ResponseGroupData
import com.task.newapp.realmDB.*
import com.task.newapp.realmDB.models.ChatList
import com.task.newapp.realmDB.models.Chats
import com.task.newapp.realmDB.models.UserArchive
import com.task.newapp.realmDB.models.UserHook
import com.task.newapp.realmDB.wrapper.ChatsWrapperModel
import com.task.newapp.ui.activities.chat.ArchivedChatsListActivity
import com.task.newapp.ui.activities.chat.OneToOneChatActivity
import com.task.newapp.utils.*
import com.task.newapp.utils.Constants.Companion.ChatTypeFlag.GROUPS
import com.task.newapp.utils.Constants.Companion.ChatTypeFlag.PRIVATE
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList


class ChatsFragment : Fragment(), View.OnClickListener, ChatListAdapter.OnChatItemClickListener, AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    private lateinit var binding: FragmentChatBinding
    private lateinit var chats: ArrayList<ChatsWrapperModel>
    private lateinit var chatsAdapter: ChatListAdapter
    private var archiveCount: Int = 0
    var isShowSearchBar = false

    //private lateinit var socket: Socket
    private val mCompositeDisposable = CompositeDisposable()
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data

                val isModified = data!!.getBooleanExtra(Constants.isModified, false)
                if (isModified) {
                    setAdapter(true)
                }
            }
        }

    private var resultOneToOneChatLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val isTyping = data.getBooleanExtra(Constants.bundle_is_typing, false)
                    val opponentId = data.getIntExtra(Constants.bundle_opponent_id, 0)
                    showHideTypingIndicator(opponentId, isTyping)
                    setAdapter(false)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        chats = ArrayList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
        //destroySocketListeners()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.txt_archive -> {
                resultLauncher.launch(Intent(requireActivity(), ArchivedChatsListActivity::class.java))
            }
        }
    }

    private fun initListeners() {
        binding.txtArchive.setOnClickListener(this)
    }

    private fun initView() {
        initListeners()
        initSearchView()
        setAdapter(true)
        callGetUnreadMessageAPI()

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
                Log.d("called", "this is called.")
                binding.searchLayout.searchView.isActivated = true
                binding.searchLayout.searchView.setQuery("", false)
                binding.searchLayout.searchView.isIconified = true
                binding.searchLayout.searchView.onActionViewExpanded()
                binding.searchLayout.searchView.clearFocus()
                binding.searchLayout.searchView.findViewById<View>(R.id.search_button).performClick()
            })
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        Log.e("search", "search text change")
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    private fun setAdapter(isRefresh: Boolean) {
        prepareChatListAdapterModel(getAllChats(), isRefresh)
        if (chats.isNotEmpty()) {
//            if (!this::chatsAdapter.isInitialized) {
            chatsAdapter = ChatListAdapter(requireActivity(), this)
            chatsAdapter.setOnItemClickListener(this)
            binding.rvChats.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.rvChats.adapter = chatsAdapter
//            }
            if (isRefresh)
                chatsAdapter.doRefresh(chats)
            else
                chatsAdapter.setData(chats, true)
        }
        //show/hide archive count label
        showHideArchiveCountLabel()
        showHideEmptyView()
    }

    private fun prepareChatListAdapterModel(chatsList: List<Chats>, isRefresh: Boolean) {
        if (isRefresh) {
            if (chats.isNotEmpty())
                chats.clear()
            chatsList.forEach { chat ->
                chats.add(ChatsWrapperModel(chat))

            }
        } else {
            val tempList = ArrayList<ChatsWrapperModel>()
            chatsList.forEachIndexed { index, chatsItem ->
                val position = getChatPosition(this.chats, chatsItem.id)
                tempList.add(ChatsWrapperModel(chatsItem, if (position != -1) this.chats[position].isTyping else false))
            }
            chats.clear()
            chats.addAll(tempList)
        }
    }

    private fun insertUnreadMessagedToDB(data: List<ChatModel>, callback: (Boolean) -> Unit) {
        for (messageObj in data) {
            val checkChatExist = getSingleChatList(messageObj.id)
            if (checkChatExist != null) {
                continue
            }

            prepareChatLabelData(messageObj).let { chatList ->

                insertChatListData(chatList)
                var isUpdate = false
                val checkChatId = getSingleChatList(messageObj.id)
                if (checkChatId != null) {
                    isUpdate = true
                }
                when {
                    messageObj.isGroupChat == 1 -> {
                        val singleGroup = getSingleChat(messageObj.groupId)
                        if (singleGroup == null) {
                            val chat = Chats()
                            chat.id = messageObj.groupId
                            chat.isGroup = true
                            chat.isHook = false
                            chat.isArchive = false
                            chat.isBlock = false
                            chat.chatList = chatList
                            chat.updatedAt = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
                            chat.currentTime = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
                            //RealmManager.sharedInstance.addObject(object: chat, update: false)
                            insertChatData(chat)
                        } /*else {
                            chat.isGroup = singleGroup.isGroup
                            chat.isHook = singleGroup.isHook
                            chat.isArchive = singleGroup.isArchive
                            chat.isBlock = singleGroup.isBlock
                            chat.updatedAt = messageObj.updatedAt
                            chat.currentTime = messageObj.createdAt
                        }*/
                        if (isUpdate)
                            updateChatsList(messageObj.groupId, chatList) {}

                    }
                    messageObj.isSecret == 1 -> {

                    }
                    messageObj.isBroadcastChat == 1 -> {

                    }
                    else -> {
                        val singleUser = getSingleChat(messageObj.userId)
                        if (singleUser == null) {
                            val chat = Chats()
                            chat.id = messageObj.userId
                            chat.chatList = chatList
                            chat.isGroup = false
                            chat.isHook = false
                            chat.isArchive = false
                            chat.isBlock = false
                            chat.updatedAt = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
                            chat.currentTime = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
                            insertChatData(chat)

                        } /*else {
                            chat.isGroup = singleUser.isGroup
                            chat.isHook = singleUser.isHook
                            chat.isArchive = singleUser.isArchive
                            chat.isBlock = singleUser.isBlock
                            chat.currentTime = messageObj.createdAt
                            chat.updatedAt = messageObj.updatedAt
                        }*/

                        if (isUpdate) {
                            updateChatsList(chatList.userId, chatList) {}
                        }
                    }
                }
                messageObj.request?.let {
                    insertFriendRequestData(createFriendRequest(messageObj.request))
                }

                messageObj.sender?.let {
                    insertUserData(prepareOtherUserData(messageObj.sender)) { users ->

                        showLog("CHATS : ", users.toString())
                        updateChatListAndUserData(messageObj.sender.id, users)
                    }
                }

                messageObj.receiver?.let {
                    insertUserData(prepareOtherUserData(messageObj.receiver)) { users ->

                        showLog("CHATS : ", users.toString())
                        updateChatListAndUserData(messageObj.receiver.id, users)
                    }

                }
            }

        }
        callback.invoke(true)
    }

    override fun onBlockChatClick(position: Int, chats: Chats) {
        requireActivity().showToast("Block $position")
        callBlockUserAPI(chats)
    }

    override fun onHookChatClick(position: Int, chats: Chats) {
        if (!chats.isHook && getHookCount() == 3) {
            requireActivity().showToast(getString(string.error_msg_hook_chat))
        } else
            callHookChatAPI(chats)
    }

    override fun onClearChatClick(position: Int, chats: Chats) {
        callClearChatAPI(chats) { isSuccess ->
            if (isSuccess) {
                setAdapter(false)
            }

        }
    }

    override fun onArchiveChatClick(position: Int, chats: Chats) {
        callArchiveChatAPI(chats)
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        if (chats[position].chats.isGroup)
            requireActivity().showToast("Coming Soon...")
        else
            resultOneToOneChatLauncher.launch(
                Intent(requireActivity(), OneToOneChatActivity::class.java).putExtra(Constants.bundle_opponent_id, chats[position].chats.id)
                    .putExtra(Constants.bundle_is_typing, chats[position].isTyping)
            )
        /*  requireActivity().launchActivity<OneToOneChatActivity> {
              putExtra(Constants.bundle_opponent_id, chats[position].chats.id)
              putExtra(Constants.bundle_is_typing, chats[position].isTyping)
          }*/

    }

    private fun showHideEmptyView() {
        if (chats.isEmpty()) {
            binding.llEmptyChat.visibility = VISIBLE
            binding.rvChats.visibility = GONE
            binding.divider.visibility = GONE
            // binding.searchLayout.llSearch.visibility = GONE
        } else {
            binding.llEmptyChat.visibility = GONE
            binding.rvChats.visibility = VISIBLE
            binding.divider.visibility = VISIBLE
            //   binding.searchLayout.llSearch.visibility = VISIBLE
        }
    }

    fun showHideSearchView(show: Boolean) {
        if (show) {
            binding.searchLayout.llSearch.visibility = VISIBLE
            isShowSearchBar = true
        } else {
            binding.searchLayout.llSearch.visibility = GONE
            isShowSearchBar = false
        }

    }

    private fun showHideArchiveCountLabel() {
        archiveCount = getArchivedChatCount()
        if (archiveCount == 0) {
            binding.txtArchive.visibility = GONE
        } else {
            binding.txtArchive.visibility = VISIBLE
            binding.txtArchive.text = resources.getString(string.archive_count, archiveCount)
        }
    }

    private fun callGetUnreadMessageAPI() {
        try {
            if (!requireActivity().isNetworkConnected()) {
                requireActivity().showToast(getString(R.string.no_internet))
                return
            }

            openProgressDialog(activity)

            mCompositeDisposable.add(
                ApiClient.create()
                    .getUnreadMessage()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseGetUnreadMessage>() {
                        override fun onNext(responseGetUnreadMessage: ResponseGetUnreadMessage) {
                            if (responseGetUnreadMessage.success == 1) {
                                insertUnreadMessagedToDB(responseGetUnreadMessage.data) { isSuccess ->
                                    if (isSuccess)
                                        setAdapter(true)
                                }
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

    private fun callGetUpdatedAllGroupAPI() {
        try {
            if (!requireActivity().isNetworkConnected()) {
                requireActivity().showToast(getString(R.string.no_internet))
                return
            }

            openProgressDialog(activity)

            mCompositeDisposable.add(
                ApiClient.create()
                    .getUpdatedAllGroup()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseGroupData>() {
                        override fun onNext(responseGroupData: ResponseGroupData) {
                            if (responseGroupData.success == 1) {
                                /* insertUnreadMessagedToDB(responseGroupData.data) { isSuccess ->
                                     if (isSuccess)
                                         setAdapter(true)
                                 }*/
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


    private fun callHookChatAPI(chats: Chats) {
        try {
            if (!requireActivity().isNetworkConnected()) {
                requireActivity().showToast(getString(R.string.no_internet))
                return
            }

            openProgressDialog(activity)
            val type = if (chats.isGroup) Constants.group else Constants.friend
            val hook_id = chats.id
            val is_hook = if (chats.isHook) 0 else 1
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.type to type,
                Constants.hook_id to hook_id,
                Constants.is_hook to is_hook,
            )
            mCompositeDisposable.add(
                ApiClient.create()
                    .hookChat(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<CommonResponse>() {
                        override fun onNext(commonResponse: CommonResponse) {
                            if (commonResponse.success == 1) {
                                if (is_hook != 1) {
                                    deleteHook(if (type == Constants.group) UserHook::groupId.name else UserHook::friendId.name, hook_id)
                                } else {
                                    val userHook = UserHook()
                                    userHook.id = hook_id
                                    userHook.userId = App.fastSave.getInt(Constants.prefUserId, 0)
                                    if (chats.isGroup) {
                                        userHook.friendId = 0
                                        userHook.groupId = chats.id
                                        userHook.isGroup = 1
                                        userHook.isFriend = 0
                                    } else {
                                        userHook.friendId = chats.id
                                        userHook.groupId = 0
                                        userHook.isGroup = 0
                                        userHook.isFriend = 1

                                    }
                                    insertHookData(userHook)

                                }
                                updateChatsIsHook(hook_id)
                                setAdapter(true)
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

    private fun callArchiveChatAPI(chats: Chats) {
        try {
            if (!requireActivity().isNetworkConnected()) {
                requireActivity().showToast(getString(R.string.no_internet))
                return
            }

            openProgressDialog(activity)
            val type = if (chats.isGroup) Constants.group else Constants.friend
            val archive_id = chats.id
            val is_archive = if (chats.isArchive) 0 else 1
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.type to type,
                Constants.archive_id to archive_id,
                Constants.is_archive to is_archive,
            )
            mCompositeDisposable.add(
                ApiClient.create()
                    .archiveChat(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<CommonResponse>() {
                        override fun onNext(commonResponse: CommonResponse) {
                            if (commonResponse.success == 1) {
                                if (chats.isHook) {
                                    updateChatsIsHook(archive_id)
                                    deleteHook(if (type == Constants.group) UserArchive::groupId.name else UserArchive::friendId.name, archive_id)
                                }
                                updateChatsIsArchive(archive_id)
                                if (is_archive == 1) {
                                    deleteArchive(if (type == Constants.group) UserArchive::groupId.name else UserArchive::friendId.name, archive_id)
                                } else {
                                    val userArchive = UserArchive()
                                    userArchive.id = archive_id
                                    userArchive.userId = App.fastSave.getInt(Constants.prefUserId, 0)
                                    if (chats.isGroup) {
                                        userArchive.friendId = 0
                                        userArchive.groupId = chats.id
                                        userArchive.isGroup = 1
                                        userArchive.isFriend = 0
                                    } else {
                                        userArchive.friendId = chats.id
                                        userArchive.groupId = 0
                                        userArchive.isGroup = 0
                                        userArchive.isFriend = 1

                                    }
                                    if (chats.isHook) {
                                        deleteHook(if (chats.isGroup) UserHook::groupId.name else UserHook::friendId.name, chats.id)
                                    }
                                    insertArchiveData(userArchive)
                                }
                                setAdapter(true)
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

    private fun callBlockUserAPI(chats: Chats) {}

    private fun callClearChatAPI(chats: Chats, callback: (Boolean) -> Unit) {
        try {
            if (!requireActivity().isNetworkConnected()) {
                requireActivity().showToast(getString(R.string.no_internet))
                return
            }

            openProgressDialog(activity)
            val type = if (chats.isGroup) Constants.group else Constants.friend
            val receiverId = chats.id
            val isSecret = 0
            val hashMap: HashMap<String, Any> = hashMapOf(

                Constants.receiver_id to receiverId,
                Constants.is_secret to isSecret,
            )
            mCompositeDisposable.add(
                ApiClient.create()
                    .deleteChat(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<CommonResponse>() {
                        override fun onNext(commonResponse: CommonResponse) {
                            if (commonResponse.success == 1) {
                                clearAllChatList(receiverId, if (chats.isGroup) GROUPS else PRIVATE)
                                callback.invoke(true)
                            } else
                                callback.invoke(false)
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                            hideProgressDialog()
                            callback.invoke(false)
                        }

                        override fun onComplete() {
                            hideProgressDialog()
                            callback.invoke(false)
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
            callback.invoke(false)
        }
    }

    private fun callMuteAPI(chats: Chats) {}

    fun updateOnlineUser(userId: Int, status: Boolean) {
        val position = getChatPosition(chats, userId)
        showLog("find position :::", position.toString())
        if (position != -1) {
            updateUserOnlineStatus(userId, status)
            chatsAdapter.updateOnlineOfflineStatus(position, status)
        }
    }

    fun updateChatListItem(chatList: ChatList) {
        chats.forEachIndexed { index, chats ->
            getSingleChat(chats.chats.id)?.let {
                this.chats[index] = chats
                chatsAdapter.updateNewMessage(index, chats)
            }

        }
    }

    fun showHideTypingIndicator(userId: Int, isTyping: Boolean) {
        val position = getChatPosition(chats, userId)
        if (position != -1) {
            chats[position].isTyping = isTyping
            chatsAdapter.updateTypeIndicator(position, isTyping)
        }
    }
}