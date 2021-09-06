package com.task.newapp.ui.activities.chat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.skydoves.balloon.Balloon
import com.task.newapp.App
import com.task.newapp.R
import com.task.newapp.adapter.chat.OneToOneChatAdapter
import com.task.newapp.databinding.ActivityOneToOneChatBinding
import com.task.newapp.models.chat.VoiceInfo
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
import com.task.newapp.utils.recorderview.AudioPlayer
import com.task.newapp.utils.recorderview.AudioRecordView
import com.task.newapp.utils.recorderview.AudioRecorder
import com.task.newapp.utils.recorderview.toast
import com.task.newapp.workmanager.WorkManagerScheduler
import io.reactivex.disposables.CompositeDisposable
import io.realm.RealmList
import java.io.File
import java.io.PrintWriter
import java.util.*
import kotlin.collections.ArrayList


class OneToOneChatActivity : BaseAppCompatActivity(), OnClickListener,
    OneToOneChatAdapter.OnChatItemClickListener, OnItemClickListener, AudioRecordView.Callback {
    companion object {
        const val REQUEST_CAMERA_PERMISSION_RESULT = 123
    }

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
    private var audioResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                //the selected audio.
                val resultData: Intent? = result.data
                if (resultData?.data != null) {
                    val uri: Uri? = resultData?.data
                    shareVoiceFile(uri)
                } else {
                    resultData?.clipData?.let { data ->
                        for (i in 0 until data.itemCount) {
                            showLog("AUDIO URIs : ", data.getItemAt(i).uri.toString())
                            shareVoiceFile(data.getItemAt(i).uri)
                        }

                    }
                }
            }
        }

    private fun shareVoiceFile(uri: Uri?) {
        val audioFile = File(getPathFromURI(this, uri!!))
        showLog("path=====", audioFile.toString())
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(App.getAppInstance(), uri)
        val duration: String? = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val millSecond = duration?.toInt()
        val voiceInfo = VoiceInfo(audioFile.readBytes(), audioFile.absolutePath, millSecond?.toDouble() ?: 0.0, audioFile.name, audioFile.length().toDouble())
        saveAudioChatLocalAndSendToServer(voiceInfo)
    }

    private var cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImage = imageUri!!
                showLog("path=====", selectedImage.path.toString())

            }
        }

    private var galleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // val selectedMediaUri: Uri = result.data?.data!!
                val resultData: Intent? = result.data
                if (resultData?.data != null) {
                    val selectedMediaUri: Uri? = resultData?.data
                    if (selectedMediaUri.toString().contains("image")) {
                        //handle image
                        showLog("path=====", selectedMediaUri?.path.toString())
                    } else if (selectedMediaUri.toString().contains("video")) {
                        //handle video
                        showLog("path=====", selectedMediaUri?.path.toString())
                    }
                } else {
                    resultData?.clipData?.let { data ->
                        for (i in 0 until data.itemCount) {
                            val selectedMediaUri: Uri? = data.getItemAt(i).uri
                            if (selectedMediaUri.toString().contains("image")) {
                                //handle image
                                showLog("path=====", selectedMediaUri?.path.toString())
                            } else if (selectedMediaUri.toString().contains("video")) {
                                //handle video
                                showLog("path=====", selectedMediaUri?.path.toString())

                            }
                        }
                    }
                }
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

    private fun getChatObj(opponentId: Int) {
        chatObj = getSingleChat(opponentId)
        refreshToolbar(chatObj)
    }

    private fun refreshToolbar(chatObj: Chats?) {
        chatObj?.let {
            binding.toolbarLayout.txtTitle.text = it.name
            binding.toolbarLayout.imgProfile.load(
                it.userData?.profileImage ?: "",
                true,
                it.name,
                it.userData?.profileColor
            )
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
                            takePhoto()
                        }
                        PHOTOS -> {
                            openGallery()
                        }
                        DOCUMENTS -> showToast(actionName)
                        CONTACTS -> showToast(actionName)
                        LOCATION -> showToast(actionName)
                        AUDIO -> {
                            val intentUpload = Intent()
                            intentUpload.type = "audio/*"
                            intentUpload.action = Intent.ACTION_GET_CONTENT
                            intentUpload.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            audioResultLauncher.launch(intentUpload)
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
        val photo = File(Environment.getExternalStorageDirectory(), "Pic" + DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMdd.label) + ".jpg")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo))
        imageUri = Uri.fromFile(photo)
        cameraResultLauncher.launch(intent)
    }

    fun openGallery() {
        val pickIntent = Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/* video/*"
        pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryResultLauncher.launch(pickIntent)
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
            newLocalChatId, newLocalChatId, getCurrentUserId(), opponentId, 0, 0, "", type, messageText, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "", "",
            DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString(), 0, "chat", 0, 0,
            0, chat_contents, contacts, "#000000", false
        )
        insertChatListData(chatMessage)
        updateChatsList(opponentId, chatMessage)
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
                    -1, -1, getCurrentUserId(), opponentId, 0, 0, "", MessageType.TYPE_INDICATOR.type, "",
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "",
                    "", DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString(), 0, "chat", 0,
                    0, 0, null, RealmList(), "#000000", false
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

    override fun onResume() {
        super.onResume()
        requestPermission()
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

        clearFile(tmpFile)

        audioRecord = AudioRecorder(ParcelFileDescriptor.open(tmpFile, ParcelFileDescriptor.MODE_READ_WRITE))
        audioRecord?.start()
    }

    override fun isReady() = true

    override fun onRecordEnd() {
        toast("onEnd")
        audioRecord?.stop()

        tmpFile.copyTo(file, true)
        shareAudioFile()
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

    private fun requestPermission() {
        @Suppress("ControlFlowWithEmptyBody")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                    Toast.makeText(
                        this,
                        "App required access to audio", Toast.LENGTH_SHORT
                    ).show()
                }
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_CAMERA_PERMISSION_RESULT)
            }
        } else {
            // put your code for Version < Marshmallow
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CAMERA_PERMISSION_RESULT) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Application will not have audio on record", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun shareAudioFile() {
        val duration = convertDurationStringToSeconds(binding.messageLayout.audioRecordView._binding.timeTv.text.toString())
        if (binding.messageLayout.audioRecordView._binding.timeTv.text.toString() != "00:00") {
            val voiceInfo = VoiceInfo(file.readBytes(), file.absolutePath, duration.toDouble(), file.name, file.length().toDouble())
            saveVoiceChatLocalAndSendToServer(voiceInfo)
        }

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

    private fun saveImageChatLocalAndSendToServer(voiceInfo: VoiceInfo) {
        val chatContentId = getNewChatId()
        val chatContents = ChatContents()
        chatContents.contentId = chatContentId
        chatContents.id = chatContentId
        chatContents.content = ""
        chatContents.type = Constants.Companion.ChatContentType.IMAGE.contentType
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
}