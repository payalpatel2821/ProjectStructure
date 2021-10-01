package com.task.newapp.ui.activities.chat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialcab.attached.AttachedCab
import com.afollestad.materialcab.attached.destroy
import com.afollestad.materialcab.attached.isActive
import com.afollestad.materialcab.attached.isDestroyed
import com.afollestad.materialcab.createCab
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.skydoves.balloon.Balloon
import com.task.newapp.App
import com.task.newapp.R
import com.task.newapp.adapter.chat.OneToOneChatAdapter
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityOneToOneChatBinding
import com.task.newapp.models.CommonResponse
import com.task.newapp.models.chat.ContactInfo
import com.task.newapp.models.chat.DocumentInfo
import com.task.newapp.models.chat.ImageCaption
import com.task.newapp.models.chat.VoiceInfo
import com.task.newapp.realmDB.*
import com.task.newapp.realmDB.models.ChatContents
import com.task.newapp.realmDB.models.ChatList
import com.task.newapp.realmDB.models.Chats
import com.task.newapp.realmDB.wrapper.ChatListWrapperModel
import com.task.newapp.ui.activities.BaseAppCompatActivity
import com.task.newapp.ui.activities.profile.OtherUserProfileActivity
import com.task.newapp.utils.*
import com.task.newapp.utils.Constants.Companion.ChatContentType
import com.task.newapp.utils.Constants.Companion.MessageType
import com.task.newapp.utils.DialogUtils.ChatAttachmentActionsName
import com.task.newapp.utils.DialogUtils.ChatAttachmentActionsName.*
import com.task.newapp.utils.DialogUtils.DialogCallbacks
import com.task.newapp.utils.recorderview.AudioPlayer
import com.task.newapp.utils.recorderview.AudioRecordView
import com.task.newapp.utils.recorderview.AudioRecorder
import com.task.newapp.utils.recorderview.toast
import com.task.newapp.workmanager.WorkManagerScheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.realm.RealmList
import lv.chi.photopicker.MediaPickerFragment
import java.io.File
import java.io.PrintWriter
import java.net.URLConnection
import java.util.*
import kotlin.collections.ArrayList


class OneToOneChatActivity : BaseAppCompatActivity(), OnClickListener,
    OneToOneChatAdapter.OnChatItemClickListener, OnItemClickListener, OnItemLongClickListener, AudioRecordView.Callback, MediaPickerFragment.Callback {

    private val TAG: String = OneToOneChatActivity::class.java.simpleName
    var select_captions = java.util.ArrayList<String>()
    var select_time = java.util.ArrayList<String>()
    var targetList = java.util.ArrayList<String>()
    var return_mediatype = java.util.ArrayList<Int>()
    lateinit var binding: ActivityOneToOneChatBinding
    private val mCompositeDisposable = CompositeDisposable()
    private var opponentId: Int = 0
    private var chatObj: Chats? = null
    private var chats = ArrayList<ChatListWrapperModel>()
    private lateinit var chatsAdapter: OneToOneChatAdapter
    private lateinit var dialog: Balloon
    private var isTyping = false
    private var isOpponentTyping = false
    private var isModified = false
    private var mainCab: AttachedCab? = null

    private val file: File by lazy {
        val f = File("$externalCacheDir${File.separator}audio.pcm")
        if (!f.exists()) {
            f.createNewFile()
        }
        f
    }
    private val tmpFile: File by lazy {
        val f = File("$externalCacheDir${File.separator}tmp.pcm")
        if (!f.exists()) {
            f.createNewFile()
        }
        f
    }
    private var audioRecord: AudioRecorder? = null
    private var audioPlayer: AudioPlayer? = null
    private var audioResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            //the selected audio.
            val resultData: Intent? = result.data
            if (resultData?.data != null) {
                val uri: Uri? = resultData?.data
                shareAudioFile(uri)
            } else {
                resultData?.clipData?.let { data ->
                    for (i in 0 until data.itemCount) {
                        showLog("AUDIO URIs : ", data.getItemAt(i).uri.toString())
                        shareAudioFile(data.getItemAt(i).uri)
                    }

                }
            }
        }
    }

    private var documentResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            //the selected audio.
            val resultData: Intent? = result.data
            if (resultData?.data != null) {
                val uri: Uri? = resultData?.data
                showLog("AUDIO URIs : ", uri.toString())
                shareDocumentFile(uri)
            } else {
                resultData?.clipData?.let { data ->
                    for (i in 0 until data.itemCount) {
                        showLog("AUDIO URIs : ", data.getItemAt(i).uri.toString())
                        shareDocumentFile(data.getItemAt(i).uri)
                    }

                }
            }
        }
    }
    private var cameraResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val file = File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg")

            val selectedImage = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file)
//            showLog("path=====", selectedImage.path.toString())
            showLog("path=====", selectedImage!!.path.toString())
            shareImageVideoFile((selectedImage), "", "")
        }
    }
    private var galleryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // val selectedMediaUri: Uri = result.data?.data!!
            val resultData: Intent? = result.data
            if (resultData?.data != null) {
                val selectedMediaUri: Uri? = resultData?.data
                if (selectedMediaUri.toString().contains("image")) {
                    //handle image
                    showLog("path=====", getPathFromURI(this@OneToOneChatActivity, selectedMediaUri!!).toString())
                } else if (selectedMediaUri.toString().contains("video")) {
                    //handle video
                    showLog("path=====", getPathFromURI(this@OneToOneChatActivity, selectedMediaUri!!).toString())
                }
                // shareImageVideoFile(selectedMediaUri)
            } else {
                resultData?.clipData?.let { data ->
                    for (i in 0 until data.itemCount) {
                        val selectedMediaUri: Uri? = data.getItemAt(i).uri
                        if (selectedMediaUri.toString().contains("image")) {
                            //handle image
                            showLog("path=====", getPathFromURI(this@OneToOneChatActivity, selectedMediaUri!!).toString())
                        } else if (selectedMediaUri.toString().contains("video")) {
                            //handle video
                            showLog("path=====", getPathFromURI(this@OneToOneChatActivity, selectedMediaUri!!).toString())

                        }
                        //    shareImageVideoFile(selectedMediaUri)
                    }
                }
            }
        }
    }
    private var viewPagerResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            targetList = ArrayList()
            select_captions = ArrayList()
            select_time = ArrayList()
            return_mediatype = ArrayList()
            targetList = result.data!!.getStringArrayListExtra("select_urls")!!
            select_captions = result.data!!.getStringArrayListExtra("select_captions")!!
            select_time = result.data!!.getStringArrayListExtra("select_time")!!
            return_mediatype = result.data!!.getIntegerArrayListExtra("urls_mediatype")!!

            targetList.forEachIndexed { index, s ->
                shareImageVideoFile(Uri.parse(targetList[index]), select_captions[index], select_time[index])
            }

            showLog("data", "$targetList $select_captions $select_time $return_mediatype")
        }
    }

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
        binding.messageLayout.audioRecordView.apply {
            activity = this@OneToOneChatActivity
            callback = this@OneToOneChatActivity
        }

    }

    @SuppressLint("RestrictedApi")
    private fun createCab() {
        if (mainCab == null || mainCab.isDestroyed()) {
            mainCab = createCab(R.id.cab_stub) {
                //title(R.string.title_broadcast_list)
                backgroundColor(R.color.bottomsheet_bg)

                setTheme(R.style.ToolbarIconTintStyle)
                titleColor(R.color.gray4)
                popupTheme(R.style.ThemeOverlay_AppCompat_Light)

                menu(R.menu.chat_options_menu)
                fadeIn()
                onCreate { cab, menu ->
                    if (menu is MenuBuilder) {
                        menu.setOptionalIconsVisible(true)
                    }
                    // menuInflater.inflate(R.menu.chat_options_menu, menu)
                    cab.title(literal = "${chatsAdapter.getSelectedMessageCount()} Selected")
                }

                onSelection { item ->
                    when (item.itemId) {
                        R.id.action_delete -> deleteMessage()
                    }
                    true
                }
                onDestroy { cab ->
                    if (chatsAdapter.getSelectedMessageCount() > 0)
                        chatsAdapter.deselectAllMessages()
                    true
                }

            }

        } else {
            mainCab?.let { cab ->
                cab.fadeIn()
                cab.title(literal = "${chatsAdapter.getSelectedMessageCount()} Selected")

            }
        }
    }

    private fun getChatObj(opponentId: Int) {
        chatObj = getSingleChat(opponentId)
        refreshToolbar(chatObj)
    }

    private fun refreshToolbar(chatObj: Chats?) {
        chatObj?.let {
            binding.toolbarLayout.txtTitle.text = it.name
            if (!this.isFinishing) {
                binding.toolbarLayout.imgProfile.load(
                    it.userData?.profileImage ?: "",
                    true,
                    it.name,
                    it.userData?.profileColor
                )
            }
            binding.toolbarLayout.imgOnline.visibility = if (it.isOnline) VISIBLE else GONE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListeners() {
        binding.messageLayout.edtTextMessage.addTextChangedListener {
            showHideSendButton(
                binding.messageLayout.edtTextMessage.text.toString().trim().isNotEmpty()
            )
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
            chatsAdapter.setOnItemLongClickListener(this)
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
            chatsList.forEach { chatListItem ->
                chats.add(ChatListWrapperModel(chatListItem))
            }
            //chats.addAll(chatsList)
        }
    }

    private fun startTyping() {
        if (!isTyping) {
            userTypingEmitEvent(
                getCurrentUserId(),
                opponentId,
                binding.toolbarLayout.txtTitle.text.toString().trim()
            )
            isTyping = true
        }
    }

    private fun stopTyping() {
        if (isTyping) {
            userStopTypingEmitEvent(
                getCurrentUserId(),
                opponentId,
                binding.toolbarLayout.txtTitle.text.toString().trim()
            )
            isTyping = false
        }
    }

    private fun showHideSendButton(isShow: Boolean) {
        if (isShow) {
            startTyping()
            binding.messageLayout.imgSendMessage.visibility = VISIBLE
            binding.messageLayout.audioRecordView.visibility = GONE
        } else {
            stopTyping()
            binding.messageLayout.audioRecordView.visibility = VISIBLE
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
        //menuInflater.inflate(R.menu.chat_menu, menu)
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
                        binding.messageLayout.imgAttachment.setColorFilter(
                            ContextCompat.getColor(
                                this,
                                R.color.gray2
                            ), android.graphics.PorterDuff.Mode.MULTIPLY
                        )
                    } else {
                        dialog.showAlignBottom(binding.messageLayout.imgAttachment)
                        binding.messageLayout.imgAttachment.setColorFilter(
                            ContextCompat.getColor(
                                this,
                                R.color.theme_color
                            ), android.graphics.PorterDuff.Mode.MULTIPLY
                        )
                    }
                }
            }
            R.id.audio_record_view -> {
            }
            R.id.img_send_message -> {
                isModified = true
                prepareNewMessage(
                    binding.messageLayout.edtTextMessage.text.toString().trim(),
                    MessageType.TEXT.type,
                    null,
                    RealmList()
                )
                binding.messageLayout.edtTextMessage.text?.clear()
            }
            R.id.ll_toolbar_content -> {
                if (binding.toolbarLayout.actionsLayout.isVisible) {
                    binding.toolbarLayout.actionsLayout.visibility = GONE
                } else {
                    binding.toolbarLayout.actionsLayout.visibility = VISIBLE
                }
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
        dialog = DialogUtils().showChatAttachmentIOSDialog(
            this,
            binding.messageLayout.imgAttachment,
            listener = object : DialogCallbacks {
                override fun onPositiveButtonClick() {}
                override fun onNegativeButtonClick() {}
                override fun onDefaultButtonClick(actionName: String) {
                    when (ChatAttachmentActionsName.getObjectFromName(actionName)) {
                        CAMERA -> {
                            runWithPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                                takePhoto()
                            }
                        }
                        PHOTOS -> {
                            select_captions = java.util.ArrayList<String>()
                            select_time = java.util.ArrayList<String>()
                            targetList = java.util.ArrayList<String>()
                            return_mediatype = java.util.ArrayList<Int>()
                            runWithPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                                MediaPickerFragment.newInstance(
                                    multiple = true,
                                    allowCamera = true,
                                    pickerType = MediaPickerFragment.PickerType.ANY,
                                    maxSelection = 30,
                                    theme = R.style.ChiliPhotoPicker_Light,
                                ).show(supportFragmentManager, "picker")
                            }
                        }
                        DOCUMENTS -> {
                            runWithPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                                val intentUpload = Intent()
                                intentUpload.type = "*/*"
                                intentUpload.action = Intent.ACTION_GET_CONTENT
                                intentUpload.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                documentResultLauncher.launch(intentUpload)
                            }
                        }
                        CONTACTS -> {
                            val picker: ContactPicker? = ContactPicker.create(
                                activity = this@OneToOneChatActivity,
                                onContactPicked = {
                                    val contactArray = ArrayList<ContactInfo>()
                                    contactArray.add(ContactInfo(it.name ?: "", it.number, it.email.toString().replace("[", "").replace("]", ""), ""))
                                    showLog("TAG", it.name + ": " + it.number + ": " + it.email)
                                    saveContactChatLocalAndSendToServer(contactArray)
                                },
                                onFailure = { showLog("TAG", it.localizedMessage) })

                            picker?.pick()
                        }
                        LOCATION -> showToast(actionName)
                        AUDIO -> {
                            runWithPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                                val intentUpload = Intent()
                                intentUpload.type = "audio/*"
                                intentUpload.action = Intent.ACTION_GET_CONTENT
                                intentUpload.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                audioResultLauncher.launch(intentUpload)
                            }
                        }
                    }
                }
            })

        dialog.setOnBalloonDismissListener {
            binding.messageLayout.imgAttachment.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.gray2
                ), android.graphics.PorterDuff.Mode.MULTIPLY
            )
        }
    }

    private var imageUri: Uri? = null
    fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file = File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg")
        imageUri = FileProvider.getUriForFile(this, this.applicationContext.packageName + ".fileprovider", file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
//        val photo = File(Environment.getExternalStorageDirectory(), "Pic" + DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMdd.label) + ".jpg")
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo))
//        imageUri = Uri.fromFile(photo)
        cameraResultLauncher.launch(intent)
    }

    fun openGallery() {
//        val pickIntent = Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI)
//        pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        val intent = Intent()
        intent.type = "image/* video/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        galleryResultLauncher.launch(intent)
    }

    private fun prepareNewMessage(messageText: String, type: String, chat_contents: ChatContents?, contacts: RealmList<ChatContents>) {

        val newLocalChatId = getNewChatId()
        if (type == MessageType.CONTACT.type) {
            contacts.forEach { obj ->
                obj.chatId = newLocalChatId
            }
        } else {
            chat_contents?.let {
                chat_contents.chatId = newLocalChatId
            }
        }
        val chatMessage = ChatList.create(
            localChatId = newLocalChatId, id = newLocalChatId, userId = getCurrentUserId(), receiverId = opponentId, isGroupChat = 0, groupId = 0, otherUserId = "",
            type = type, messageText = messageText, isShared = 0, isForward = 0, isDeleted = 0, deletedForAll = 0, deletedBy = 0, tick = 0, isReply = 0, isStar = 0,
            isReplyToStory = 0, isStoryReplyBackToReply = 0, storyId = 0, isSecret = 0, isRead = 0, deliverTime = "", readTime = "",
            createdAt = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString(), isActivityLabel = 0, event = "chat",
            isBroadcastChat = 0, broadcastId = 0, chatId = 0, chatContents = chat_contents, contacts = contacts, groupLabelColor = "#000000", isSync = false
        )
        insertChatListData(chatMessage)
        val singleChat = getSingleChat(opponentId)
        if (singleChat == null) {
            val chat = Chats()
            chat.id = opponentId
            chat.isGroup = false
            chat.isHook = false
            chat.isArchive = false
            chat.isBlock = false
            chat.chatList = chatMessage
            chat.updatedAt = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
            chat.currentTime = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
            insertChatData(chat)

        } else {
            updateChatsList(opponentId, chatMessage)
        }
        if (chatsAdapter.isChatTypingIndicatorAdded())
            chats.add(chats.size - 1, ChatListWrapperModel(chatMessage))
        else chats.add(ChatListWrapperModel(chatMessage))
        chatsAdapter.setData(chats, true)
        binding.rvChatMessages.scrollToPosition(chatsAdapter.itemCount - 1)
        WorkManagerScheduler.refreshPeriodicWork(App.getAppInstance())
    }

    private fun addTypeIndicator() {
        if (this::chatsAdapter.isInitialized) {
            if (!chatsAdapter.isChatTypingIndicatorAdded()) {
                val chatMessage = ChatList.create(
                    localChatId = -1, id = -1, userId = getCurrentUserId(), receiverId = opponentId, isGroupChat = 0, groupId = 0, otherUserId = "",
                    type = MessageType.TYPE_INDICATOR.type, messageText = "", isShared = 0, isForward = 0, isDeleted = 0, deletedForAll = 0, deletedBy = 0,
                    tick = 0, isReply = 0, isStar = 0, isReplyToStory = 0, isStoryReplyBackToReply = 0, storyId = 0, isSecret = 0, isRead = 0, deliverTime = "",
                    readTime = "", createdAt = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString(),
                    isActivityLabel = 0, event = "chat", isBroadcastChat = 0, broadcastId = 0, chatId = 0, chatContents = null, contacts = RealmList(),
                    groupLabelColor = "#000000", isSync = false
                )
                chats.add(ChatListWrapperModel(chatMessage))
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

    override fun onNewMessagePrivateSocketEvent(chatList: ChatList) {
        super.onNewMessagePrivateSocketEvent(chatList)
        if (chatList.userId == opponentId) {
            if (chatsAdapter.isChatTypingIndicatorAdded())
                chats.add(chats.size - 1, ChatListWrapperModel(chatList))
            else chats.add(ChatListWrapperModel(chatList))
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

    override fun onResume() {
        super.onResume()
        //  requestPermission()
    }

    override fun onBackPressed() {
        // super.onBackPressed()
        val intent = Intent().putExtra(Constants.bundle_is_typing, isOpponentTyping)
            .putExtra(Constants.bundle_opponent_id, opponentId)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing)
            stopTyping()
    }

    override fun onRecordStart() {
        toast("onRecordStart")

        runWithPermissions(Manifest.permission.RECORD_AUDIO) {
            clearFile(tmpFile)

            audioRecord = AudioRecorder(ParcelFileDescriptor.open(tmpFile, ParcelFileDescriptor.MODE_READ_WRITE))
            audioRecord?.start()
        }
    }

    override fun isReady() = true

    override fun onRecordEnd() {
        toast("onEnd")
        audioRecord?.stop()

        tmpFile.copyTo(file, true)
        shareVoiceFile()
    }

    override fun onRecordCancel() {
        toast("onCancel")
        audioRecord?.stop()
    }

    private fun clearFile(f: File) {
        PrintWriter(f).run {
            print("")
            close()
        }
    }

    private fun shareVoiceFile() {
        val duration = convertDurationStringToSeconds(binding.messageLayout.audioRecordView._binding.timeTv.text.toString())
        if (binding.messageLayout.audioRecordView._binding.timeTv.text.toString() != "00:00") {
            val voiceInfo = VoiceInfo(file.readBytes(), file.absolutePath, duration.toDouble(), file.name, file.length().toDouble())
            saveVoiceChatLocalAndSendToServer(voiceInfo)
        }
    }

    private fun shareAudioFile(uri: Uri?) {
        val audioFile = File(getPathFromURI(this, uri!!))
        showLog("path=====", audioFile.toString())
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(App.getAppInstance(), uri)
        val duration: String? = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val millSecond = duration?.toInt()
        val voiceInfo = VoiceInfo(audioFile.readBytes(), audioFile.absolutePath, millSecond?.toDouble() ?: 0.0, audioFile.name, audioFile.length().toDouble())
        saveAudioChatLocalAndSendToServer(voiceInfo)
    }

    private fun shareImageVideoFile(uri: Uri?, caption: String?, time: String) {
        val file = File(uri!!.path)
        file.setReadable(true, false);
        showLog("path=====", file.toString())

        if (isImageFile(file.path)) {
            val imageCaption = ImageCaption(caption ?: "", uri!!, 0.0, 0.0, 0.0, false, data = file)
            saveImageChatLocalAndSendToServer(imageCaption)
        } else {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(file.path)
            val duration: String? = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val millSecond = duration?.toDouble()
            val imageCaption = ImageCaption(caption ?: "", uri!!, 0.0, millSecond ?: 0.0, 0.0, false, file)
            saveImageChatLocalAndSendToServer(imageCaption)
        }
    }

    private fun shareDocumentFile(uri: Uri?) {
        val documentFile = File(getPathFromURI(this, uri!!))
//        val documentpath = Uri.fromFile(documentFile)
        showLog("path====", documentFile.toString())
        val documentInfo = DocumentInfo(ChatContentType.DOCUMENT.contentType, "", documentFile!!.readBytes(), documentFile.absolutePath, 0.0, documentFile.name, documentFile.length().toDouble(), 0)
        saveDocumentChatLocalAndSendToServer(documentInfo)
    }

    private fun saveDocumentChatLocalAndSendToServer(documentInfo: DocumentInfo) {
        val chatContents = ChatContents()
        val chatContentId = getNewChatId()

        chatContents.contentId = chatContentId
        chatContents.id = chatContentId
        chatContents.content = documentInfo.localUrl
        chatContents.type = documentInfo.chatType
        chatContents.caption = ""
        chatContents.size = 0.0
        chatContents.duration = 0.0
        chatContents.title = documentInfo.fileName
        chatContents.localPath = documentInfo.localUrl
        chatContents.createdAt = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
        chatContents.updatedAt = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
        chatContents.isDownload = true
        chatContents.data = documentInfo.data

        showLog("Chat content data:", documentInfo.localUrl)
        prepareNewMessage(messageText = "", type = MessageType.MIX.type, chat_contents = chatContents, contacts = RealmList())

    }

    private fun saveVoiceChatLocalAndSendToServer(voiceInfo: VoiceInfo) {
        val chatContentId = getNewChatId()
        val chatContents = ChatContents()
        chatContents.contentId = chatContentId
        chatContents.id = chatContentId
        chatContents.content = ""
        chatContents.type = Constants.Companion.ChatContentType.VOICE.contentType
        chatContents.caption = ""
        chatContents.size = voiceInfo.size
        chatContents.duration = voiceInfo.duration
        chatContents.title = voiceInfo.filename
        chatContents.email = ""
        chatContents.profileImage = ""
        chatContents.createdAt = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
        chatContents.updatedAt = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
        chatContents.localPath = voiceInfo.localurl
        chatContents.isDownload = true
        chatContents.data = voiceInfo.voicedata

        showLog("Chat content data:", voiceInfo.voicedata.toString())
        prepareNewMessage(messageText = "", type = MessageType.MIX.type, chat_contents = chatContents, contacts = RealmList())
    }

    private fun saveAudioChatLocalAndSendToServer(voiceInfo: VoiceInfo) {
        val chatContentId = getNewChatId()
        val chatContents = ChatContents()
        chatContents.contentId = chatContentId
        chatContents.id = chatContentId
        chatContents.content = ""
        chatContents.type = Constants.Companion.ChatContentType.AUDIO.contentType
        chatContents.caption = ""
        chatContents.size = voiceInfo.size
        chatContents.duration = voiceInfo.duration
        chatContents.title = voiceInfo.filename
        chatContents.email = ""
        chatContents.profileImage = ""
        chatContents.createdAt = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
        chatContents.updatedAt = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
        chatContents.localPath = voiceInfo.localurl
        chatContents.isDownload = true
        chatContents.data = voiceInfo.voicedata

        showLog("Chat content data:", voiceInfo.voicedata.toString())
        prepareNewMessage(messageText = "", type = MessageType.MIX.type, chat_contents = chatContents, contacts = RealmList())
    }

    private fun saveImageChatLocalAndSendToServer(imageCaption: ImageCaption) {
        val chatContentId = getNewChatId()
        val chatContents = ChatContents()
        chatContents.contentId = chatContentId
        chatContents.id = chatContentId
        chatContents.content = ""
        chatContents.type = if (URLConnection.guessContentTypeFromName(imageCaption.imgVideoData.path).contains("image")) ChatContentType.IMAGE.contentType else ChatContentType.VIDEO.contentType
        chatContents.caption = ""
        chatContents.size = 0.0
        chatContents.duration = imageCaption.endTime
        chatContents.title = imageCaption.data.name
        chatContents.email = ""
        chatContents.profileImage = ""
        chatContents.createdAt = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
        chatContents.updatedAt = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
        chatContents.localPath = imageCaption.data.absolutePath
        chatContents.isDownload = true
        chatContents.data = imageCaption.data.readBytes()
        showLog("Chat content data:", imageCaption.data.path.toString())
        prepareNewMessage(messageText = "", type = MessageType.MIX.type, chat_contents = chatContents, contacts = RealmList())

    }

    private fun saveContactChatLocalAndSendToServer(contactArray: ArrayList<ContactInfo>) {
        val chatContactArray = RealmList<ChatContents>()
        contactArray.forEach {
            val chatContent = ChatContents()
            val chatContentId = getNewChatId()
            chatContent.contentId = chatContentId
            chatContent.id = chatContentId
            chatContent.content = ""
            chatContent.type = ChatContentType.CONTACT.contentType
            chatContent.name = it.name
            chatContent.number = it.number
            chatContent.email = it.email
            chatContent.profileImage = it.profileImg
            chatContent.caption = ""
            chatContent.caption = ""
            chatContent.size = 0.0
            chatContent.duration = 0.0
            chatContent.title = ""
            chatContent.localPath = ""
            chatContent.createdAt = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
            chatContent.updatedAt = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
            chatContent.isDownload = true
            chatContactArray.add(chatContent)
        }
        if (chatContactArray.isNotEmpty()) {
            showLog("Chat content data:", contactArray.map { it.name }.toString())
            prepareNewMessage(messageText = "", type = MessageType.CONTACT.type, null, contacts = chatContactArray)
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // mainCab?.slideDown()
        if (mainCab.isActive()) {
            val count = chatsAdapter.getSelectedMessageCount()
            if (count > 0)
                mainCab?.title(literal = "${chatsAdapter.getSelectedMessageCount()} Selected")
            else
                mainCab?.destroy()
        }
    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
        createCab()
        return true
    }

    override fun onIncomingMessageItemClick(isIncoming: Boolean, position: Int, chats: ChatListWrapperModel) {
        if (mainCab.isActive()) {
            if (isIncoming) {
                mainCab?.getMenu()?.findItem(R.id.action_delete)?.isVisible = chatsAdapter.getIncomingSelectedMessageCount() == 0

            }
        }
    }

    override fun onMediaPicked(mediaItems: ArrayList<Uri>) {
        showLog("item selected: ", mediaItems.toString())

        mediaItems.forEach { uri ->
            targetList.add(uri.toString())

            if (isImageFile(uri.toString())) {
                return_mediatype.add(1)
            } else {
                return_mediatype.add(0)
            }
        }

        val captionarr = Array(mediaItems.size) { "" }
        select_captions.addAll(captionarr)

        var timearr = Array(mediaItems.size) { "" }
        select_time.addAll(timearr)


        val intent = Intent(this@OneToOneChatActivity, ViewPagerActivity::class.java)
        intent.putStringArrayListExtra("select_urls", targetList/*Gson().toJson(mediaItems)*/)
        intent.putStringArrayListExtra("select_captions", select_captions)
        intent.putStringArrayListExtra("select_time", select_time)
        intent.putIntegerArrayListExtra("urls_mediatype", return_mediatype)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        viewPagerResultLauncher.launch(intent)

    }

    /*
    @IBAction func btnClickDeleteMsg(_ sender: UIButton) {

        var chatids = ""
        var localchatids = ""

        arrDeleteChat.forEach { obj in
            if obj.isSync{
                if chatids == ""{
                    chatids = "\(obj.chatid)"
                }else{
                    chatids = chatids + "," + "\(obj.chatid)"
                }
            }else{
                if localchatids == ""{
                    localchatids = "\(obj.primaryid)"
                }else{
                    localchatids = localchatids + "," + "\(obj.primaryid)"
                }
            }
        }


        let parameters = [
            Parameters.Chatlist.chat_id: chatids] as [String : Any]

            let chatviewModel = ChatViewModel()
            chatviewModel.CallWS_DeleteChat(flags: selInfoChatData?.flagType ?? "", senderid: currentUserId, receiverid: self.selInfoChatData?.selid ?? 0, chatids: chatids, param: parameters){ (isSuccess) in
                if isSuccess{
                       let chatids = self.arrDeleteChat.filter({$0.isSync == true}).map({$0.chatid})
                        let isPlayingAudioCount = self.arrDeleteChat.filter({$0.primaryid == currentPlayingChatID})
                        if isPlayingAudioCount.count > 0{
                            self.stopAudioVoicePlaying()
                        }

                        if self.selInfoChatData?.flagType.lowercased() == flag.broadcast.rawValue.lowercased(){

                            let arrChatIDS = chatids
                            var bchatids = [Int64]()
                            var bchatcontactids = [Int64]()

                            arrChatIDS.forEach { broadcastid in
                                let allBroadcastData = RealmManager.sharedInstance.getAllBroadcastChatidwise(broadcast_chat_id:  Int(broadcastid))
                                if allBroadcastData.count > 0 {
                                     bchatids = allBroadcastData.filter({$0.isSync == true && $0.type != chatcontentType.contact.rawValue}).map({$0.id})
                                    bchatcontactids = allBroadcastData.filter({$0.isSync == true && $0.type == chatcontentType.contact.rawValue}).map({$0.id})
                                }
                            }
                            if bchatids.count > 0 {
                                RealmManager.sharedInstance.deleteChatContent(query: "\(DBTables.chatcontents.chat_id) IN %@", ids: bchatids)
                            }
                            if bchatcontactids.count > 0 {
                                RealmManager.sharedInstance.deleteChatContent(query: "\(DBTables.chatcontents.chat_id) IN %@", ids: bchatcontactids)
                            }

                        }

                        if chatids.count > 0 {
                            RealmManager.sharedInstance.deleteChatContent(query: "\(DBTables.chatcontents.chat_id) IN %@", ids: chatids)
                        }

                        let contactchatids = self.arrDeleteChat.filter({$0.isSync == true && $0.messageType == chatcontentType.contact.rawValue}).map({$0.chatid})

                        if contactchatids.count > 0{
                            RealmManager.sharedInstance.deleteChatContact(query: "\(DBTables.chatcontents.id) IN %@", ids: contactchatids)
                        }



                        let selindex = self.arrDeleteChat.map({$0.index})
                        if selindex.count > 0{
                            _ = self.arrChatslist.remove(elementsAtIndices: selindex)
                            selindex.forEach { index in
                                if let index = self.arrDeleteChat.firstIndex(where: {$0.index == index}) {
                                    self.arrDeleteChat.remove(at: index)
                                }
                            }
                        }
                        self.deleteWithoutSync()
                }else{
                    // Delete without Sync
                    self.deleteWithoutSync()
                }
            }


    }*/
    private fun deleteMessage() {
        val chatIds = chatsAdapter.getSelectedMessage().map { it.chatList.id }.joinToString(",")
        showLog("Delete selected : ", chatIds)
        callAPIDeleteSelectedMessage(chatIds)
    }

    private fun callAPIDeleteSelectedMessage(chatIds: String, callback: ((Boolean) -> Unit)? = null) {
        try {
            if (!isNetworkConnected()) {
                showToast(getString(R.string.no_internet))
                return
            }

            openProgressDialog(this)
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.chat_id to chatIds

            )
            mCompositeDisposable.add(
                ApiClient.create()
                    .deleteMessageFromChat(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<CommonResponse>() {
                        override fun onNext(commonResponse: CommonResponse) {
                            if (commonResponse.success == 1) {
                                deletePrivateMessageEmitEvent(chatIds, getCurrentUserId(), opponentId)
                                callback?.invoke(true)
                            } else
                                callback?.invoke(false)
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                            hideProgressDialog()
                            callback?.invoke(false)
                        }

                        override fun onComplete() {
                            hideProgressDialog()
                            callback?.invoke(false)
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
            callback?.invoke(false)
        }
    }
}