package com.task.newapp.ui.activities.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.skydoves.balloon.Balloon
import com.task.newapp.App
import com.task.newapp.R
import com.task.newapp.adapter.chat.OneToOneChatAdapter
import com.task.newapp.databinding.ActivityOneToOneChatBinding
import com.task.newapp.realmDB.getOneToOneChat
import com.task.newapp.realmDB.getSingleChat
import com.task.newapp.realmDB.insertChatListData
import com.task.newapp.realmDB.models.ChatContents
import com.task.newapp.realmDB.models.ChatList
import com.task.newapp.realmDB.models.Chats
import com.task.newapp.realmDB.updateChatsList
import com.task.newapp.ui.activities.BaseAppCompatActivity
import com.task.newapp.ui.activities.profile.OtherUserProfileActivity
import com.task.newapp.utils.*
import com.task.newapp.utils.Constants.Companion.MessageType
import com.task.newapp.utils.DialogUtils.ChatAttachmentActionsName
import com.task.newapp.utils.DialogUtils.ChatAttachmentActionsName.*
import com.task.newapp.utils.DialogUtils.DialogCallbacks
import com.task.newapp.workmanager.WorkManagerScheduler
import io.reactivex.disposables.CompositeDisposable
import io.realm.RealmList
import java.util.*
import kotlin.collections.ArrayList

class OneToOneChatActivity : BaseAppCompatActivity(), OnClickListener, OneToOneChatAdapter.OnChatItemClickListener, OnItemClickListener {

    lateinit var binding: ActivityOneToOneChatBinding
    private val mCompositeDisposable = CompositeDisposable()
    private var opponentId: Int = 0
    private var chatObj: Chats? = null
    private var chats = ArrayList<ChatList>()
    private lateinit var chatsAdapter: OneToOneChatAdapter
    private lateinit var dialog: Balloon
    private var isTyping = false
    private var isOpponentTyping = false
    private var isModified = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_one_to_one_chat)
        setSupportActionBar(binding.toolbarLayout.activityMainToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        initView()

    }

    private fun initView() {
        initListeners()
        opponentId = intent.getIntExtra(Constants.bundle_opponent_id, 0)

        getChatObj(opponentId)

        createAttachmentDialog()
        setAdapter()
        isOpponentTyping = intent.getBooleanExtra(Constants.bundle_is_typing, false)
        if (isOpponentTyping) {
            addTypeIndicator()
        } else {
            removeTypeIndicator()
        }
    }

    private fun getChatObj(opponentId: Int) {
        chatObj = getSingleChat(opponentId)
        refreshToolbar(chatObj)
    }

    private fun refreshToolbar(chatObj: Chats?) {
        chatObj?.let {
            binding.toolbarLayout.txtTitle.text = it.name
            binding.toolbarLayout.imgProfile.load(it.userData?.profileImage ?: "", true, it.name, it.userData?.profileColor)
            binding.toolbarLayout.imgOnline.visibility = if (it.isOnline) VISIBLE else GONE
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initListeners() {
        binding.messageLayout.edtTextMessage.addTextChangedListener {
            showHideSendButton(binding.messageLayout.edtTextMessage.text.toString().trim().isNotEmpty())
        }
        binding.rvChatMessages.isClickable = true
        binding.rvChatMessages.isFocusable = true
        binding.rvChatMessages.isFocusableInTouchMode = true

        binding.rvChatMessages.setOnTouchListener { v, event ->
            hideSoftKeyboard(this)
            stopTyping()

            false
        }

    }


    private fun setAdapter() {
        prepareChatListAdapterModel(getOneToOneChat(getCurrentUserId(), opponentId))

        if (!this::chatsAdapter.isInitialized) {
            chatsAdapter = OneToOneChatAdapter(this, this)
            chatsAdapter.setOnItemClickListener(this)
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false).also {
                it.stackFromEnd = true
                binding.rvChatMessages.layoutManager = it
            }

            binding.rvChatMessages.adapter = chatsAdapter
        }
        if (chats.isNotEmpty()) {
            chatsAdapter.setData(chats, true)
            binding.rvChatMessages.scrollToPosition(chatsAdapter.itemCount - 1)
        }


        // showHideEmptyView()
    }

    private fun prepareChatListAdapterModel(chatsList: List<ChatList>?) {
        chatsList?.let {
            if (chats.isNotEmpty())
                chats.clear()
            chats.addAll(chatsList)
        }
    }

    private fun startTyping() {
        if (!isTyping) {
            userTypingEmitEvent(getCurrentUserId(), opponentId, binding.toolbarLayout.txtTitle.text.toString().trim())
            isTyping = true
        }
    }

    private fun stopTyping() {
        if (isTyping) {
            userStopTypingEmitEvent(getCurrentUserId(), opponentId, binding.toolbarLayout.txtTitle.text.toString().trim())
            isTyping = false
        }
    }

    private fun showHideSendButton(isShow: Boolean) {
        if (isShow) {
            startTyping()
            binding.messageLayout.imgSendMessage.visibility = VISIBLE
            binding.messageLayout.imgRecordVoice.visibility = GONE
        } else {
            stopTyping()
            binding.messageLayout.imgRecordVoice.visibility = VISIBLE
            binding.messageLayout.imgSendMessage.visibility = GONE
        }

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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_attachment -> {

                hideSoftKeyboard(this)
                if (this::dialog.isInitialized) {
                    if (dialog.isShowing) {
                        dialog.dismiss()
                        binding.messageLayout.imgAttachment.setColorFilter(ContextCompat.getColor(this, R.color.gray2), android.graphics.PorterDuff.Mode.MULTIPLY)
                    } else {
                        dialog.showAlignBottom(binding.messageLayout.imgAttachment)
                        binding.messageLayout.imgAttachment.setColorFilter(ContextCompat.getColor(this, R.color.theme_color), android.graphics.PorterDuff.Mode.MULTIPLY)
                    }
                }
            }
            R.id.img_record_voice -> {
            }
            R.id.img_send_message -> {
                isModified = true
                prepareNewMessage(binding.messageLayout.edtTextMessage.text.toString().trim(), MessageType.TEXT.type, null, RealmList())
                binding.messageLayout.edtTextMessage.text?.clear()
            }
            R.id.ll_profile_layout -> {
                launchActivity<OtherUserProfileActivity> {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Constants.user_id, opponentId)
                }
            }
        }
    }


    private fun createAttachmentDialog() {
        dialog = DialogUtils().showChatAttachmentIOSDialog(this, binding.messageLayout.imgAttachment, listener = object : DialogCallbacks {
            override fun onPositiveButtonClick() {}
            override fun onNegativeButtonClick() {}
            override fun onDefaultButtonClick(actionName: String) {
                when (ChatAttachmentActionsName.getObjectFromName(actionName)) {
                    CAMERA -> showToast(actionName)
                    PHOTOS -> showToast(actionName)
                    DOCUMENTS -> showToast(actionName)
                    CONTACTS -> showToast(actionName)
                    LOCATION -> showToast(actionName)
                    null -> showToast(actionName)
                }
            }
        })

        dialog.setOnBalloonDismissListener {
            binding.messageLayout.imgAttachment.setColorFilter(ContextCompat.getColor(this, R.color.gray2), android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }


    private fun prepareNewMessage(messageText: String, type: String, chat_contents: ChatContents?, contacts: RealmList<ChatContents>) {
        val newLocalChatId = getNewChatId()
        val chatMessage = ChatList.create(
            newLocalChatId, newLocalChatId, getCurrentUserId(), opponentId, 0, 0, "", type, messageText, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "", "",
            DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString(), 0, "chat", 0, 0,
            0, chat_contents, contacts, "#000000", false
        )
        insertChatListData(chatMessage)
        updateChatsList(opponentId, chatMessage) {}
        if (chatsAdapter.isChatTypingIndicatorAdded())
            chats.add(chats.size - 1, chatMessage)
        else chats.add(chatMessage)
        chatsAdapter.setData(chats, true)
        binding.rvChatMessages.scrollToPosition(chatsAdapter.itemCount - 1)
        WorkManagerScheduler.refreshPeriodicWork(App.getAppInstance())
    }

    private fun addTypeIndicator() {
        if (this::chatsAdapter.isInitialized) {
            if (!chatsAdapter.isChatTypingIndicatorAdded()) {
                val chatMessage = ChatList.create(
                    -1, -1, getCurrentUserId(), opponentId, 0, 0, "", MessageType.TYPE_INDICATOR.type, "", 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "", "",
                    DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString(), 0, "chat", 0, 0,
                    0, null, RealmList(), "#000000", false
                )
                chats.add(chatMessage)
                chatsAdapter.setData(chats, true)
                binding.rvChatMessages.scrollToPosition(chatsAdapter.itemCount - 1)
            }
        }


    }

    private fun removeTypeIndicator() {
        if (this::chatsAdapter.isInitialized) {
            if (chatsAdapter.isChatTypingIndicatorAdded()) {
                chatsAdapter.getChatItemFromLocalId(-1)?.let { chatList ->
                    chats.remove(chatList)
                    chatsAdapter.setData(chats, true)
                    binding.rvChatMessages.scrollToPosition(chatsAdapter.itemCount - 1)
                }
            }
        }
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }

    override fun onNewMessagePrivateSocketEvent(chatList: ChatList) {
        super.onNewMessagePrivateSocketEvent(chatList)
        if (chatList.userId == opponentId) {
            if (chatsAdapter.isChatTypingIndicatorAdded())
                chats.add(chats.size - 1, chatList)
            else chats.add(chatList)
            chatsAdapter.setData(chats, true)
            binding.rvChatMessages.scrollToPosition(chatsAdapter.itemCount - 1)
        }
    }


    override fun onUserTypingSocketEvent(receiverId: Int) {
        super.onUserTypingSocketEvent(receiverId)
        if (receiverId == opponentId) {
            isOpponentTyping = true
            addTypeIndicator()
        }
    }

    override fun onUserStopTypingSocketEvent(receiverId: Int) {
        super.onUserStopTypingSocketEvent(receiverId)
        if (receiverId == opponentId) {
            isOpponentTyping = false
            removeTypeIndicator()
        }
    }

    override fun onOnlineOfflineSocketEvent(userId: Int, status: Boolean) {
        super.onOnlineOfflineSocketEvent(userId, status)
        //  updateUserOnlineStatus(userId, status)
        if (userId == opponentId)
            getChatObj(userId)

    }

    override fun onBackPressed() {
        // super.onBackPressed()
        val intent = Intent().putExtra(Constants.bundle_is_typing, isOpponentTyping).putExtra(Constants.bundle_opponent_id, opponentId)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing)
            stopTyping()
    }
}