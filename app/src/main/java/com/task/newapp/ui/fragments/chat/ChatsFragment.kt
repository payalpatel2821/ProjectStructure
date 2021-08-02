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
import com.task.newapp.models.ChatModel
import com.task.newapp.models.CommonResponse
import com.task.newapp.models.ResponseGetUnreadMessage
import com.task.newapp.realmDB.*
import com.task.newapp.realmDB.models.*
import com.task.newapp.ui.activities.chat.ArchivedChatsListActivity
import com.task.newapp.ui.activities.chat.OneToOneChatActivity
import com.task.newapp.utils.*
import com.task.newapp.utils.Constants.Companion.MessageType
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.realm.RealmList
import java.util.*
import kotlin.collections.ArrayList
import com.task.newapp.realmDB.insertChatContent as insertChatContent1


class ChatsFragment : Fragment(), View.OnClickListener, ChatListAdapter.OnChatItemClickListener, AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    private lateinit var binding: FragmentChatBinding
    private lateinit var chats: ArrayList<Chats>
    private lateinit var chatsAdapter: ChatListAdapter
    private var archiveCount: Int = 0

    //private lateinit var socket: Socket
    private val mCompositeDisposable = CompositeDisposable()
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data

                val isModified = data!!.getBooleanExtra(Constants.isModified, false)
                if (isModified) {
                    setAdapter()
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
        setAdapter()
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

    private fun setAdapter() {
        prepareChatListAdapterModel(getAllChats())
        if (chats.isNotEmpty()) {
            //if (chatsAdapter == null) {
            chatsAdapter = ChatListAdapter(requireActivity(), this)
            chatsAdapter.setOnItemClickListener(this)
            binding.rvChats.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.rvChats.adapter = chatsAdapter
            //  }
            chatsAdapter.doRefresh(chats)
        }
        //show/hide archive count label
        showHideArchiveCountLabel()
        showHideEmptyView()
    }

    private fun prepareChatListAdapterModel(chatsList: List<Chats>) {
        if (chats.isNotEmpty())
            chats.clear()
        chats.addAll(chatsList)

    }

    private fun insertUnreadMessagedToDB(data: List<ChatModel>) {
        for (messageObj in data) {
            //   prepareChatsData(messageObj)
            val chatAudio = RealmList<ChatAudio>()
            val chatDocument = RealmList<ChatDocument>()
            val chatVoiceMessage = RealmList<ChatVoice>()
            val chatContents = RealmList<ChatContents>()
            val chatContacts = RealmList<ChatContacts>()
            var chatLocation: ChatLocation? = null

            val localId = messageObj.id
            val messageType = MessageType.getMessageTypeFromText(messageObj.type)
            when (messageType) {
                MessageType.TYPE_INDICATOR -> {

                }
                MessageType.LABEL -> {

                }
                MessageType.TEXT -> {

                }
                MessageType.MIX -> {
                    messageObj.chatContents?.let {
                        for (chatContentObj in messageObj.chatContents) {
                            if (getSingleChatContent(chatContentObj.chat_id) == null) {

                                val contents = ChatContents()
                                contents.id = chatContentObj.id
                                contents.content_id = chatContentObj.id
                                contents.chat_id = chatContentObj.chat_id
                                contents.type = chatContentObj.type
                                contents.content = chatContentObj.content
                                contents.caption = chatContentObj.caption
                                contents.size = chatContentObj.size
                                contents.local_path = chatContentObj.local_path
                                contents.created_at = chatContentObj.created_at
                                contents.updated_at = chatContentObj.updated_at
                                contents.delete_for = chatContentObj.delete_for
                                chatContents.add(contents)
                            }
                        }
                        insertChatContent1(chatContents)

                    }
                }
                MessageType.LOCATION -> {
                    messageObj.location?.let {
                        if (messageObj.location.isNotEmpty()) {
                            val chatLocationsObj = messageObj.location[0]

                            if (getSingleChatLocation(chatLocationsObj.chat_id) == null) {

                                chatLocation = ChatLocation()
                                chatLocation!!.location_id = chatLocationsObj.locationid
                                chatLocation!!.id = chatLocationsObj.id
                                chatLocation!!.chat_id = chatLocationsObj.chat_id
                                chatLocation!!.user_id = chatLocationsObj.user_id
                                chatLocation!!.location = chatLocationsObj.location
                                chatLocation!!.latitude = chatLocationsObj.latitude
                                chatLocation!!.longitude = chatLocationsObj.longitude
                                chatLocation!!.created_at = chatLocationsObj.created_at
                                chatLocation!!.updated_at = chatLocationsObj.updated_at
                                chatLocation!!.delete_for = chatLocationsObj.delete_for
                                chatLocation!!.type = chatLocationsObj.type
                                chatLocation!!.size = chatLocationsObj.size
                                chatLocation!!.sharing_time = chatLocationsObj.sharing_time
                                chatLocation!!.end_time = chatLocationsObj.end_time

                                insertChatLocation(chatLocation!!)

                            }

                        }

                    }
                }
                MessageType.CONTACT -> {
                    messageObj.contacts?.let {
                        for (contactObj in messageObj.contacts) {
                            if (getSingleChatContact(contactObj.chat_id) == null) {
                                val contacts = ChatContacts()
                                contacts.contact_id = contactObj.id
                                contacts.id = contactObj.id
                                contacts.chat_id = contactObj.chat_id
                                contacts.name = contactObj.name
                                contacts.number = contactObj.number
                                contacts.email = contactObj.email
                                contacts.profile_image = contactObj.profile_image
                                contacts.profile_color = contactObj.profile_color
                                contacts.size = 0.0
                                contacts.delete_for = ""
                                contacts.flag = messageType.type
                                contacts.created_at = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
                                contacts.updated_at = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
                                chatContacts.add(contacts)
                            }
                        }
                        insertChatContacts(chatContacts)
                    }
                }
                MessageType.AUDIO -> {
                    messageObj.audios?.let {
                        for (audioObj in messageObj.audios) {
                            if (getSingleChatAudio(audioObj.chat_id) == null) {
                                val audio = ChatAudio()
                                audio.audio_local_id = audioObj.id
                                audio.id = audioObj.id
                                audio.chat_id = audioObj.chat_id
                                audio.audio = audioObj.audio
                                audio.audio_filename = getFileNameFromURLString(audioObj.audio)
                                audio.created_at = audioObj.created_at
                                audio.updated_at = audioObj.updated_at
                                audio.size = audioObj.size
                                audio.delete_for = audioObj.delete_for
                                audio.duration = convertDurationStringToSeconds(audioObj.duration)
                                chatAudio.add(audio)
                            }
                        }
                        insertChatAudio(chatAudio)
                    }
                }
                MessageType.VOICE -> {
                    messageObj.voiceMsg?.let {
                        for (voiceMessageObj in messageObj.voiceMsg) {
                            if (getSingleChatVoice(voiceMessageObj.chat_id) == null) {
                                val chatVoice = ChatVoice()
                                chatVoice.voice_filename = getFileNameFromURLString(voiceMessageObj.voice_msg)
                                chatVoice.voice_id = voiceMessageObj.id
                                chatVoice.id = voiceMessageObj.id
                                chatVoice.chat_id = voiceMessageObj.chat_id
                                chatVoice.voice_msg = voiceMessageObj.voice_msg
                                chatVoice.created_at = voiceMessageObj.created_at
                                chatVoice.updated_at = voiceMessageObj.updated_at
                                chatVoice.delete_for = voiceMessageObj.delete_for
                                chatVoice.local_path = voiceMessageObj.local_path
                                chatVoice.size = voiceMessageObj.size
                                chatVoice.duration = convertDurationStringToSeconds(voiceMessageObj.duration)
                                chatVoice.is_download = false
                                chatVoiceMessage.add(chatVoice)

                            }
                        }
                        insertChatVoice(chatVoiceMessage)
                    }
                }
                MessageType.DOCUMENT -> {
                    messageObj.documents?.let {
                        for (documentObj in messageObj.documents) {
                            if (getSingleChatDocument(documentObj.chat_id) == null) {
                                val document = ChatDocument()
                                document.title = getFileNameFromURLString(documentObj.document)
                                document.size = documentObj.size
                                document.id = documentObj.id
                                document.chat_id = documentObj.chat_id
                                document.document = documentObj.document
                                document.no_of_pages = documentObj.no_of_pages
                                document.type = documentObj.type
                                document.flag = documentObj.flag
                                document.local_path = documentObj.local_path
                                document.created_at = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
                                document.updated_at = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
                                document.is_download = false
                                chatDocument.add(document)
                            }
                        }
                        insertChatDocument(chatDocument)
                    }
                }
                MessageType.STORY -> {
                }
                MessageType.VIDEO -> {
                }
                MessageType.LINK -> {
                }
                MessageType.DATE -> {
                }


            }
            //prepare chatlist data
            val grpUser = getGroupUserById(messageObj.groupId, messageObj.userId)
            val chatListObj = ChatList()
            chatListObj.local_chat_id = localId
            chatListObj.id = localId
            chatListObj.user_id = messageObj.userId
            chatListObj.receiver_id = messageObj.receiverId
            chatListObj.sender_id = messageObj.userId
            chatListObj.is_group_chat = messageObj.isGroupChat
            chatListObj.group_id = messageObj.groupId
            chatListObj.type = messageObj.type
            chatListObj.message_text = messageObj.messageText
            chatListObj.is_shared = messageObj.isShared
            chatListObj.is_forward = messageObj.isForward
            chatListObj.is_deleted = messageObj.isDeleted
            chatListObj.deleted_for_all = messageObj.deletedForAll
            chatListObj.deleted_by = messageObj.deletedBy
            chatListObj.tick = messageObj.tick
            chatListObj.is_reply = messageObj.isReply
            chatListObj.is_star = 0
            chatListObj.is_reply_to_message = messageObj.isReplyToMessage
            chatListObj.is_reply_to_story = messageObj.isReplyToStory
            chatListObj.story_id = messageObj.storyId
            chatListObj.is_replyback_to_reply = messageObj.isReplybackToReply
            chatListObj.is_secret = messageObj.isSecret
            chatListObj.is_read = messageObj.isRead
            chatListObj.created_at = messageObj.createdAt
            chatListObj.is_activity_label = messageObj.isActivityLabel
            chatListObj.event = messageObj.event
            chatListObj.is_broadcast_chat = messageObj.isBroadcastChat
            chatListObj.broadcast_id = messageObj.broadcastId
            chatListObj.content_id = messageObj.contentId
            chatListObj.is_content_reply = messageObj.isContentReply
            chatListObj.grp_other_user_id = messageObj.otherUserId
            chatListObj.entry_id = messageObj.entryId
            chatListObj.chat_id = messageObj.chatId
            chatListObj.chat_contents = chatContents
            chatListObj.chat_audio = chatAudio
            chatListObj.chat_contacts = chatContacts
            chatListObj.chat_location = chatLocation
            chatListObj.chat_voice = chatVoiceMessage
            chatListObj.chat_document = chatDocument
            chatListObj.grp_label_color = grpUser?.label_color ?: "#000000"
            chatListObj.isSync = true


            val checkChatListExist = getSingleChatListItem(messageObj.id)
            if (checkChatListExist == null) {

                insertChatListData(chatListObj)

            }
            when {
                messageObj.isGroupChat == 1 -> {
                    val singleUser = getSingleUserChat(messageObj.groupId)

                    val chat = Chats()

                    chat.id = messageObj.userId
                    chat.is_group = true
                    chat.is_hook = false
                    chat.is_archive = false
                    chat.is_block = false
                    chat.chat_list = chatListObj
                    chat.name = messageObj.fullname
                    chat.updated_at = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
                    chat.current_time = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
                    insertChatData(chat)


                }
                messageObj.isSecret == 1 -> {
                    TODO()
                }
                messageObj.isBroadcastChat == 1 -> {
                    TODO()
                }
                else -> {
                    val singleUser = getSingleUserChat(messageObj.userId)
                    val chat = Chats()
                    chat.id = messageObj.userId
                    chat.chat_list = chatListObj
                    if (singleUser == null) {
                        chat.is_group = false
                        chat.is_hook = false
                        chat.is_archive = false
                        chat.is_block = false
                        chat.updated_at = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
                        chat.current_time = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()

                    } else {
                        chat.is_group = singleUser.is_group
                        chat.is_hook = singleUser.is_hook
                        chat.is_archive = singleUser.is_archive
                        chat.is_block = singleUser.is_block
                        chat.current_time = messageObj.createdAt
                        chat.updated_at = messageObj.updatedAt
                    }
                    insertChatData(chat)

                }
            }
            messageObj.sender?.let {
                insertUserData(prepareOtherUserData(messageObj.sender), object : OnRealmTransactionResult {
                    override fun onSuccess(obj: Any) {
                        showLog("CHATS : ", obj.toString())
                        updateChatListAndUserData(messageObj.sender.id, obj as Users)
                    }

                    override fun onError() {
                    }

                })


            }

            messageObj.receiver?.let {
                insertUserData(prepareOtherUserData(messageObj.receiver), object : OnRealmTransactionResult {
                    override fun onSuccess(obj: Any) {
                        showLog("CHATS : ", obj.toString())
                        updateChatListAndUserData(messageObj.receiver.id, obj as Users)
                    }

                    override fun onError() {
                    }
                })

            }

        }
        setAdapter()
    }

    override fun onBlockChatClick(position: Int, chats: Chats) {
        requireActivity().showToast("Block $position")
        callBlockUserAPI(chats)
    }

    override fun onHookChatClick(position: Int, chats: Chats) {
        //requireActivity().showToast("Hook $position")
        if (!chats.is_hook && getHookCount() == 3) {
            requireActivity().showToast(getString(string.error_msg_hook_chat))
        } else
            callHookChatAPI(chats)
    }

    override fun onClearChatClick(position: Int, chats: Chats) {
        //requireActivity().showToast("Clear $position")
        callClearChatAPI(chats)
    }

    override fun onArchiveChatClick(position: Int, chats: Chats) {
        //requireActivity().showToast("Archive $position")
        callArchiveChatAPI(chats)
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        if (chats[position].is_group)
            requireActivity().showToast("Item Clicked $position")
        else
            requireActivity().launchActivity<OneToOneChatActivity> {
                putExtra(Constants.bundle_opponent_id, chats[position].id)
            }

    }

    private fun showHideEmptyView() {
        if (chats.isEmpty()) {
            binding.llEmptyChat.visibility = VISIBLE
            binding.rvChats.visibility = GONE
            binding.divider.visibility = GONE
            binding.searchLayout.llSearch.visibility = GONE
        } else {
            binding.llEmptyChat.visibility = GONE
            binding.rvChats.visibility = VISIBLE
            binding.divider.visibility = VISIBLE
            binding.searchLayout.llSearch.visibility = VISIBLE
        }
    }

    private fun showHideArchiveCountLabel() {
        if (archiveCount == 0) {
            binding.txtArchiveChat.visibility = GONE
        } else {
            binding.txtArchiveChat.visibility = VISIBLE
            binding.txtArchive.text = resources.getString(R.string.archive_count, getArchivedChatCount())
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
                                insertUnreadMessagedToDB(responseGetUnreadMessage.data)
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
            val type = if (chats.is_group) Constants.group else Constants.friend
            val hook_id = chats.id
            val is_hook = if (chats.is_hook) 0 else 1
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
                                    deleteHook(if (type == Constants.group) UserHook.PROPERTY_group_id else UserHook.PROPERTY_friend_id, hook_id)
                                } else {
                                    val userHook = UserHook()
                                    userHook.id = hook_id
                                    userHook.user_id = App.fastSave.getInt(Constants.prefUserId, 0)
                                    if (chats.is_group) {
                                        userHook.friend_id = 0
                                        userHook.group_id = chats.id
                                        userHook.is_group = 1
                                        userHook.is_friend = 0
                                    } else {
                                        userHook.friend_id = chats.id
                                        userHook.group_id = 0
                                        userHook.is_group = 0
                                        userHook.is_friend = 1

                                    }
                                    insertHookData(userHook)

                                }
                                updateChatsIsHook(hook_id)
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

    private fun callArchiveChatAPI(chats: Chats) {
        try {
            if (!requireActivity().isNetworkConnected()) {
                requireActivity().showToast(getString(R.string.no_internet))
                return
            }

            openProgressDialog(activity)
            val type = if (chats.is_group) Constants.group else Constants.friend
            val archive_id = chats.id
            val is_archive = if (chats.is_archive) 0 else 1
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
                                if (chats.is_hook) {
                                    updateChatsIsHook(archive_id)
                                    deleteHook(if (type == Constants.group) UserArchive.PROPERTY_group_id else UserArchive.PROPERTY_friend_id, archive_id)
                                }
                                updateChatsIsArchive(archive_id)
                                if (is_archive == 1) {
                                    deleteArchive(if (type == Constants.group) UserArchive.PROPERTY_group_id else UserArchive.PROPERTY_friend_id, archive_id)
                                } else {
                                    val userArchive = UserArchive()
                                    userArchive.id = archive_id
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

    private fun callBlockUserAPI(chats: Chats) {}

    private fun callClearChatAPI(chats: Chats) {
        try {
            if (!requireActivity().isNetworkConnected()) {
                requireActivity().showToast(getString(R.string.no_internet))
                return
            }

            openProgressDialog(activity)
            val type = if (chats.is_group) Constants.group else Constants.friend
            val receiver_id = chats.id
            val is_secret = 0
            val hashMap: HashMap<String, Any> = hashMapOf(

                Constants.receiver_id to receiver_id,
                Constants.is_secret to is_secret,
            )
            mCompositeDisposable.add(
                ApiClient.create()
                    .deleteChat(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<CommonResponse>() {
                        override fun onNext(commonResponse: CommonResponse) {
                            if (commonResponse.success == 1) {
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

    private fun callMuteAPI(chats: Chats) {}

    fun updateOnlineUser(userId: Int, status: Boolean) {
        val position = getChatPosition(chats, userId)
        showLog("find position :::", position.toString())
        if (position != -1) {
            updateUserOnlineStatus(userId, status)
            chatsAdapter.updateOnlineOfflineStatus(position, status)
        }
    }


}