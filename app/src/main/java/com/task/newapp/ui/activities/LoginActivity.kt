package com.task.newapp.ui.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.appizona.yehiahd.fastsave.FastSave
import com.task.newapp.App
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.databinding.ActivityLoginBinding
import com.task.newapp.models.*
import com.task.newapp.models.LoginResponse.GetAllGroup.*
import com.task.newapp.realmDB.*
import com.task.newapp.realmDB.models.*
import com.task.newapp.utils.*
import com.task.newapp.utils.Constants.Companion.MessageEvents.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.realm.RealmList
import java.util.*


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityLoginBinding
    private val mCompositeDisposable = CompositeDisposable()
    private var flagUser: Boolean = false
    private var flagPass: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        initView()
        setupKeyboardListener(binding.scrollview) // call in OnCreate or similar

    }

    private fun initView() {
        setRememberMe()
        binding.edtUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                validateUserName()
            }
        })
        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                validatePassword()
            }
        })
    }

    private fun setRememberMe() {
        if (FastSave.getInstance().getBoolean(Constants.prefIsRemember, false)) {
            binding.edtUsername.setText(App.fastSave.getString(Constants.prefUserNameRemember, "").toString())

            binding.edtPassword.setText(App.fastSave.getString(Constants.prefPasswordRemember, "").toString())

            flagUser = true
            flagPass = true
        }
        binding.chkRemember.isChecked = FastSave.getInstance().getBoolean(Constants.prefIsRemember, false)

        checkAndEnable()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable.clear()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login -> {
                if (!isNetworkConnected()) {
                    showToast(getString(R.string.no_internet))
                    return
                }

                if (!validatePassword()) {
                    return
                }
                if (!validateUserName()) {
                    return
                }
                callAPILogin()
            }
            R.id.txt_register -> {
                launchActivity<RegistrationActivity> {
                    putExtra("From", Constants.Companion.ProfileNavigation.FROM_FOLLOWERS.name)
                }
            }
            R.id.txt_forgot_password -> {
                launchActivity<ForgotPasswordActivity> { }
            }
        }
    }

    private fun saveRemember() {
        App.fastSave.saveBoolean(Constants.prefIsRemember, binding.chkRemember.isChecked)
        App.fastSave.saveString(Constants.prefUserNameRemember, if (binding.chkRemember.isChecked) binding.edtUsername.text.toString().trim() else "")
        App.fastSave.saveString(Constants.prefPasswordRemember, if (binding.chkRemember.isChecked) binding.edtPassword.text.toString().trim() else "")
    }

    private fun saveUserInfoToPref(loginResponse: LoginResponse) {
        App.fastSave.saveString(Constants.prefToken, loginResponse.data.token)
        App.fastSave.saveObject(Constants.prefUser, loginResponse.data.user)
        App.fastSave.saveInt(Constants.prefUserId, loginResponse.data.user.id)
        App.fastSave.saveString(Constants.prefUserName, loginResponse.data.user.firstName + " " + loginResponse.data.user.lastName)
        App.fastSave.saveBoolean(Constants.isLogin, true)
        showLog("token", loginResponse.data.token.toString())
    }

    private fun callAPILogin() {
        try {
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.user_name to binding.edtUsername.text.toString(),
                Constants.password to binding.edtPassword.text.toString(),
                Constants.latitude to "0",
                Constants.longitude to "0",
                Constants.device_token to Constants.deviceToken,
                Constants.device_type to Constants.device_type_android
            )

            openProgressDialog(this)

            mCompositeDisposable.add(
                ApiClient.create()
                    .doLogin(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<LoginResponse>() {
                        override fun onNext(loginResponse: LoginResponse) {
                            Log.v("onNext: ", loginResponse.toString())
                            showToast(loginResponse.message)

                            if (loginResponse.success == 1) {
                                saveRemember()
                                saveUserInfoToPref(loginResponse)
                                insertLoginResponseToDatabase(loginResponse)
                                //Next Screen
                                launchActivity<MainActivity> { }
                                finish()
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                            hideProgressDialog()
                            FastSave.getInstance().saveBoolean(Constants.isLogin, false)
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

    fun validatePassword(): Boolean {
        when {
            binding.edtPassword.text.toString().trim().isEmpty() -> {
                binding.inputLayoutPassword.error = getString(R.string.enter_password)
                binding.inputLayoutPassword.endIconDrawable!!.setTint(resources.getColor(R.color.red))
                requestFocus(this, binding.edtPassword)
                flagPass = false
                checkAndEnable()
                return false
            }
            binding.edtPassword.text.toString().length < 6 -> {
                binding.inputLayoutPassword.error = getString(R.string.enter_pass_validate)
                binding.inputLayoutPassword.endIconDrawable!!.setTint(resources.getColor(R.color.red))
                requestFocus(this, binding.edtPassword)
                flagPass = false
                checkAndEnable()
                return false
            }
            else -> {
                binding.inputLayoutPassword.isErrorEnabled = false
                binding.inputLayoutPassword.endIconDrawable!!.setTint(resources.getColor(R.color.black))
                flagPass = true
                checkAndEnable()
            }
        }
        return true
    }

    private fun validateUserName(): Boolean {
        if (binding.edtUsername.text.toString().trim().isEmpty()) {
            binding.inputLayoutName.error = getString(R.string.enter_your_username_or_phone_number)
            binding.inputLayoutName.endIconDrawable!!.setTint(resources.getColor(R.color.red))
            flagUser = false
            checkAndEnable()
            return false
        } else {
            binding.inputLayoutName.isErrorEnabled = false
            binding.inputLayoutName.endIconDrawable!!.setTint(resources.getColor(R.color.black))
            flagUser = true
            checkAndEnable()
        }
        return true
    }

    private fun checkAndEnable() {
        enableOrDisableButton(this@LoginActivity, flagUser && flagPass, binding.btnLogin)
    }

    /**
     * Insert Login response data to the Realm Database tables
     *
     * @param loginResponse : response object get from the login API
     */
    private fun insertLoginResponseToDatabase(loginResponse: LoginResponse) {
        insertUserSettingsData(prepareLoggedInUserSettings(loginResponse))
        insertFriendRequestData(prepareRequestDataForDB(loginResponse.getAllRequest))
        val (groups, groupUser, chatList, addUserChatList, chats) = prepareAllGroupDataForDB(loginResponse.getAllGroup)
        insertGroupData(
            groups as RealmList<Groups>,
            groupUser as RealmList<GroupUser>,
            chatList as RealmList<ChatList>,
            addUserChatList as RealmList<ChatList>,
            chats as RealmList<Chats>
        )
        loginResponse.hookData?.let {
            insertHookData(prepareHookData(loginResponse.hookData))
        }
        insertArchiveData(prepareArchiveData(loginResponse.archiveData))
        insertBlockUserData(prepareBlockUserDataForDB(loginResponse.blockUser))
        insertBroadcastData(prepareBroadcastData(loginResponse.broadcast))
        insertFriendSettingsData(prepareFriendSettingsDataForDB(loginResponse.friendSettings))


    }

    /*
    */
    /**
     * Prepare friend request database object from the login response to store in FriendRequest table
     *
     * @param friendRequestList : the list of requests
     * @return the RealmList of the FriendRequest DB object which will be directly inserted to the Database
     *//*
    private fun prepareRequestDataForDB(friendRequestList: List<LoginResponse.GetAllRequest>): RealmList<FriendRequest> {
        val requestList: RealmList<FriendRequest> = RealmList<FriendRequest>()
        for (getAllRequest in friendRequestList) {
            val friendRequest = FriendRequest()
            friendRequest._id = getAllRequest.id
            friendRequest.user_id = getAllRequest.userId
            friendRequest.friend_id = getAllRequest.friendId
            friendRequest.status = getAllRequest.status
            friendRequest.is_request = getAllRequest.isRequest
            friendRequest.sender_request_count = getAllRequest.senderRequestCount
            friendRequest.receiver_request_count = getAllRequest.receiverRequestCount
            friendRequest.created_at = getAllRequest.createdAt
            friendRequest.updated_at = getAllRequest.updatedAt

            requestList.add(friendRequest)

            getAllRequest.user?.let {
                if (getAllRequest.user?.id != App.fastSave.getInt(Constants.prefUserId, 0)) {
                    insertUserData(prepareOtherUserData(getAllRequest.user))
                }
            }

            getAllRequest.friend?.let {
                if (getAllRequest.friend.id != App.fastSave.getInt(Constants.prefUserId, 0)) {
                    insertUserData(prepareOtherUserData(getAllRequest.friend))
                }
            }

        }
        return requestList
    }

    fun prepareBlockUserDataForDB(blockUsers: List<OtherUserModel>): RealmList<BlockUser> {
        val blockedList: RealmList<BlockUser> = RealmList()
        blockUsers.let {
            for (blockUser in blockUsers) {
                insertUserData(prepareOtherUserData(blockUser))
                val objBlockUser = BlockUser()
                objBlockUser.blocked_user_id = blockUser.id
                objBlockUser.is_blocked = 1
                objBlockUser.is_reported = 0
                blockedList.add(objBlockUser)
            }
        }
        return blockedList
    }

    private fun prepareFriendSettingsDataForDB(friendSettings: List<FriendSetting>): RealmList<FriendSettings> {
        val friendSettingList: RealmList<FriendSettings> = RealmList()
        friendSettings.let {
            for (objUserSetting in friendSettings) {
                val usersObj: Users? = getUserByUserId(objUserSetting.friendId)

                if (usersObj != null) {
                    val friendSettingObj = FriendSettings()
                    friendSettingObj.id = objUserSetting.id
                    friendSettingObj.user_id = objUserSetting.userId
                    friendSettingObj.friend_id = objUserSetting.friendId
                    friendSettingObj.notification_tone_id = objUserSetting.notificationToneId
                    friendSettingObj.mute_notification = objUserSetting.muteNotification
                    friendSettingObj.is_custom_notification_enable = objUserSetting.isCustomNotificationEnable
                    friendSettingObj.vibrate_status = objUserSetting.vibrateStatus
                    friendSettingObj.is_popup_notification = objUserSetting.isPopupNotification
                    friendSettingObj.use_high_priority_notification = objUserSetting.useHighPriorityNotification
                    friendSettingObj.call_ringtone = objUserSetting.callRigtone
                    friendSettingObj.call_vibrate = objUserSetting.callVibrate
                    friendSettingObj.sound = objUserSetting.sound
                    friendSettingObj.user = usersObj
                    friendSettingList.add(friendSettingObj)
                }
            }
        }
        return friendSettingList
    }

    private fun prepareLoggedInUserSettings(loginResponse: LoginResponse): UserSettings {
        val userSetting = loginResponse.userSetting
        val userSettingsObj = UserSettings()

        //Insert loggedIn user info to Users Table
        val loggedInUser: Users = prepareLoggedInUserData(loginResponse.data.user)
        insertUserData(loggedInUser)

        userSettingsObj.id = userSetting.id
        userSettingsObj.user_id = userSetting.userId
        userSettingsObj.theme_id = userSetting.themeId
        userSettingsObj.wallpaper_id = userSetting.wallpaperId
        userSettingsObj.is_chat_notification = userSetting.isChatNotification
        userSettingsObj.is_new_post_notify = userSetting.isNewPostNotify
        userSettingsObj.is_tag_notification = userSetting.isTagNotification
        userSettingsObj.profile_image = userSetting.profileImage
        userSettingsObj.notification_tone_id = userSetting.notificationToneId
        userSettingsObj.is_followers_viewable = userSetting.isFollowersViewable
        userSettingsObj.is_friend_enable = userSetting.isFriendEnable
        userSettingsObj.is_image_wallpaper = userSetting.isImageWallpaper
        userSettingsObj.is_color_wallpaper = userSetting.isColorWallpaper
        userSettingsObj.wall_paper_color = userSetting.wallPaperColor
        userSettingsObj.is_default_wallpaper = userSetting.isDefaultWallpaper
        userSettingsObj.is_no_wallpaper = userSetting.isNoWallpaper
        userSettingsObj.is_gallery_wallpaper = userSetting.isGalleryWallpaper
        userSettingsObj.gallery_image = userSetting.galleryImage
        userSettingsObj.font_size = userSetting.fontSize
        userSettingsObj.languages = userSetting.languages
        userSettingsObj.is_enter_send = userSetting.isEnterSend
        userSettingsObj.is_media_visible = userSetting.isMediaVisible
        userSettingsObj.is_photo_autodownload = userSetting.isPhotoAutodownload
        userSettingsObj.is_audio_autodownload = userSetting.isAudioAutodownload
        userSettingsObj.is_video_autodownload = userSetting.isVideoAutodownload
        userSettingsObj.is_document_autodownload = userSetting.isDocumentAutodownload
        userSettingsObj.is_photo_autodownload_wifi = userSetting.isPhotoAutodownloadWifi
        userSettingsObj.is_audio_autodownload_wifi = userSetting.isAudioAutodownloadWifi
        userSettingsObj.is_video_autodownload_wifi = userSetting.isVideoAutodownloadWifi
        userSettingsObj.is_document_autodownload_wifi = userSetting.isDocumentAutodownloadWifi
        userSettingsObj.is_photo_autodownload_roaming = userSetting.isPhotoAutodownloadRoaming
        userSettingsObj.is_audio_autodownload_roaming = userSetting.isAudioAutodownloadRoaming
        userSettingsObj.is_video_autodownload_roaming = userSetting.isVideoAutodownloadRoaming
        userSettingsObj.is_document_autodownload_roaming = userSetting.isDocumentAutodownloadRoaming
        userSettingsObj.is_delete_request = userSetting.isDeleteRequest
        userSettingsObj.story_view = userSetting.storyView
        userSettingsObj.story_download = userSetting.storyDownload
        userSettingsObj.vibrate_status = userSetting.vibrateStatus
        userSettingsObj.is_popup_notification = userSetting.isPopupNotification
        userSettingsObj.use_high_priority_notification = userSetting.useHighPriorityNotification
        userSettingsObj.last_seen = userSetting.lastSeen
        userSettingsObj.is_visible = userSetting.isVisible
        userSettingsObj.profile_seen = userSetting.profileSeen
        userSettingsObj.call_ringtone = userSetting.callRigtone
        userSettingsObj.call_vibrate = userSetting.callVibrate
        userSettingsObj.about_seen = userSetting.aboutSeen
        userSettingsObj.who_can_add_me_in_group = userSetting.whoCanAddMeInGroup
        userSettingsObj.live_location_sharing = userSetting.liveLocationSharing
        userSettingsObj.is_fingerprint_lock_enabled = userSetting.isFingerprintLockEnabled
        userSettingsObj.is_show_security_notification = userSetting.isShowSecurityNotification
        userSettingsObj.is_two_step_verification_enabled = userSetting.isTwoStepVerificationEnabled
        userSettingsObj.group_vibrate_status = userSetting.groupVibrateStatus
        userSettingsObj.group_is_popup_notification = userSetting.groupIsPopupNotification
        userSettingsObj.group_use_high_priority_notification = userSetting.groupUseHighPriorityNotification
        userSettingsObj.group_notification_tone_id = userSetting.groupNotificationToneId
        userSettingsObj.near_location = userSetting.nearLocation
        userSettingsObj.sound = userSetting.sound
        userSettingsObj.user = loggedInUser
        userSettingsObj.updated_at = userSetting.updatedAt
        return userSettingsObj
    }

    private fun prepareAllGroupDataForDB(getAllGroups: List<LoginResponse.GetAllGroup>): Array<Any> {

        val groups = RealmList<Groups>()
        val groupUser = RealmList<GroupUser>()
        val chatList = RealmList<ChatList>()
        val addUserChatList = RealmList<ChatList>()
        val chats = RealmList<Chats>()
        for (groupObj in getAllGroups) {
            val chatsObj = prepareChatsData(groupObj)
            groups.add(chatsObj.group_data)
            groupUser.addAll(chatsObj.group_user_with_settings)

            groupObj.createGroupLbl?.let {
                chatList.add(prepareGroupLabelData(groupObj.createGroupLbl))
            }

            groupObj.addUserInGp?.let {
                addUserChatList.add(prepareGroupLabelData(groupObj.addUserInGp))
            }
            //chatList.add(chatsObj.chat_list)
            chats.add(chatsObj)
        }
        return arrayOf(groups, groupUser, chatList, addUserChatList, chats)

    }

    private fun prepareChatsData(groupObj: LoginResponse.GetAllGroup): Chats {
        val groups = prepareGroupData(groupObj.groupData)
        val groupUser = prepareGroupUserData(groupObj.groupUserWithSettings)
        val createGroupLabelChatList = prepareGroupLabelData(groupObj.createGroupLbl)
        val addUserGroupChatList = prepareGroupLabelData(groupObj.addUserInGp)

        val chats = Chats()
        chats.id = groupObj.groupData.id
        chats.name = groupObj.groupData.name
        chats.is_group = true
        chats.current_time = groupObj.groupData.updatedAt
        chats.updated_at = groupObj.groupData.updatedAt
        chats.group_data = groups
        chats.group_user_with_settings = groupUser
        chats.chat_list = addUserGroupChatList ?: createGroupLabelChatList

        return chats

    }

    private fun prepareGroupLabelData(createGroupLbl: ChatLabel): ChatList {
        createGroupLbl?.let {
            val chatList = ChatList()
            val groupUser = getGroupUserById(createGroupLbl.groupId, createGroupLbl.userId)

            chatList.local_chat_id = createGroupLbl.id
            chatList.id = createGroupLbl.id
            chatList.user_id = createGroupLbl.userId
            chatList.receiver_id = createGroupLbl.receiverId
            chatList.sender_id = createGroupLbl.senderId
            chatList.is_group_chat = createGroupLbl.isGroupChat
            chatList.group_id = createGroupLbl.groupId
            chatList.type = createGroupLbl.type
            chatList.message_text = getGroupLabelText(createGroupLbl.userId, createGroupLbl.event, isLoggedInUser(createGroupLbl.userId), createGroupLbl.messageText ?: "")
            chatList.is_shared = createGroupLbl.isShared
            chatList.is_forward = createGroupLbl.isForward
            chatList.is_deleted = createGroupLbl.isDeleted
            chatList.deleted_for_all = createGroupLbl.deletedForAll
            chatList.deleted_by = createGroupLbl.deletedBy
            chatList.tick = createGroupLbl.tick
            chatList.is_reply = createGroupLbl.isReply
            chatList.is_reply_to_message = createGroupLbl.isReplyToMessage
            chatList.is_reply_to_story = createGroupLbl.isReplyToStory
            chatList.story_id = createGroupLbl.storyId
            chatList.is_replyback_to_reply = createGroupLbl.isReplybackToReply
            chatList.is_secret = createGroupLbl.isSecret
            chatList.is_read = createGroupLbl.isRead
            chatList.created_at = createGroupLbl.createdAt
            chatList.is_activity_label = createGroupLbl.isActivityLabel
            chatList.event = createGroupLbl.event
            chatList.is_broadcast_chat = createGroupLbl.isBroadcastChat
            chatList.broadcast_id = createGroupLbl.broadcastId
            chatList.content_id = createGroupLbl.contentId
            chatList.is_content_reply = createGroupLbl.isContentReply
            chatList.grp_other_user_id = createGroupLbl.otherUserId
            chatList.broadcast_chat_id = createGroupLbl.broadcastChatId
            chatList.entry_id = createGroupLbl.entryId
            chatList.chat_id = createGroupLbl.chatId
            chatList.chat_contents = RealmList()
            chatList.chat_audio = RealmList()
            chatList.chat_contacts = RealmList()
            chatList.chat_location = null
            chatList.chat_document = RealmList()
            chatList.grp_label_color = if (groupUser?.label_color != null) groupUser.label_color else "#000000"
            chatList.isSync = true

            return chatList

        }


    }

    private fun prepareGroupUserData(groupUserWithSettings: List<GroupUserWithSetting>): RealmList<GroupUser> {
        val groupUserSettingsList = RealmList<GroupUser>()
        for (groupUserWithSetting in groupUserWithSettings) {
            val groupUserObj = GroupUser()
            groupUserObj.id = groupUserWithSetting.id
            groupUserObj.user_id = groupUserWithSetting.userId
            groupUserObj.grp_id = groupUserWithSetting.groupId
            groupUserObj.label_color = groupUserWithSetting.labelColor
            groupUserObj.location = groupUserWithSetting.location
            groupUserObj.is_admin = groupUserWithSetting.isAdmin
            groupUserObj.is_allow_to_add_post = groupUserWithSetting.isAllowToAddPost
            groupUserObj.is_mute_notification = groupUserWithSetting.isMuteNotification
            groupUserObj.is_report = groupUserWithSetting.isReport
            groupUserObj.status = groupUserWithSetting.status
            groupUserObj.is_deleted = groupUserWithSetting.isDeleted
            groupUserObj.is_allow_to_edit_info = groupUserWithSetting.isAllowToEditInfo
            groupUserObj.mute_time = groupUserWithSetting.muteTime
            groupUserObj.end_mute_time = groupUserWithSetting.endMuteTime
            groupUserObj.media_viewable = groupUserWithSetting.isMediaViewable
            groupUserObj.custom_notification_enable = groupUserWithSetting.isCustomNotificationEnable
            groupUserObj.vibrate_status = groupUserWithSetting.vibrateStatus
            groupUserObj.is_popup_notification = groupUserWithSetting.isPopupNotification
            groupUserObj.light = groupUserWithSetting.light
            groupUserObj.high_priority_notification = groupUserWithSetting.useHighPriorityNotification
            groupUserObj.notification_tone_id = groupUserWithSetting.notificationToneId
            groupUserObj.created_at = groupUserWithSetting.createdAt
            groupUserObj.updated_at = groupUserWithSetting.updatedAt
            groupUserSettingsList.add(groupUserObj)

        }
        return groupUserSettingsList
    }

    private fun prepareGroupData(groupData: GroupData): Groups {
        val groupObj = Groups()

        groupObj.grp_id = groupData.id
        groupObj.grp_user_id = groupData.userId
        groupObj.grp_name = groupData.name
        groupObj.grp_description = groupData.description
        groupObj.grp_icon = groupData.icon
        groupObj.grp_total_user = groupData.totalUsers
        groupObj.grp_other_user_id = groupData.otherUserId
        groupObj.grp_updated_at = groupData.updatedAt
        groupObj.grp_created_at = groupData.createdAt
        groupObj.grp_edit_info_permission = groupData.editInfoPermission
        groupObj.grp_send_msg = groupData.sendMsg

        return groupObj

    }


    private fun prepareArchiveData(archiveData: List<LoginResponse.ArchiveData>): RealmList<UserArchive> {
        val archiveList = RealmList<UserArchive>()
        archiveData.let {
            for (archiveObj in archiveData) {
                val userArchive = UserArchive()
                userArchive.id = archiveObj.id
                userArchive.user_id = archiveObj.userId
                userArchive.friend_id = archiveObj.isFriend
                userArchive.group_id = archiveObj.groupId
                userArchive.is_group = archiveObj.isGroup
                userArchive.is_friend = archiveObj.isFriend
                archiveList.add(userArchive)
            }
        }
        return archiveList


    }

    private fun prepareBroadcastData(broadcasts: List<LoginResponse.Broadcast>): RealmList<BroadcastTable> {
        val broadcastList = RealmList<BroadcastTable>()
        broadcasts.let {
            for (broadcastObj in broadcasts) {
                val broadcastTableData = BroadcastTable()
                broadcastTableData.broadcast_id = broadcastObj.id
                broadcastTableData.user_id = broadcastObj.userId
                broadcastTableData.broad_name = broadcastObj.name
                broadcastTableData.broad_icon = broadcastObj.icon
                broadcastTableData.broad_total_user = broadcastObj.totalUsers
                broadcastTableData.broad_other_userid = broadcastObj.otherUserId
                broadcastTableData.created_at = broadcastObj.createdAt
                broadcastTableData.updated_at = broadcastObj.updatedAt
                broadcastTableData.login_user = broadcastObj.userId
                broadcastTableData.chats = prepareBroadcastChatLabelData(broadcastObj.chat)
                broadcastList.add(broadcastTableData)

            }
        }
        return broadcastList
    }

    private fun prepareBroadcastChatLabelData(broadCastChat: ChatLabel): RealmList<ChatList> {
        val broadcastChatList = RealmList<ChatList>()
        broadCastChat?.let {
            val chatList = ChatList()
            chatList.local_chat_id = broadCastChat.id
            chatList.id = broadCastChat.id
            chatList.user_id = broadCastChat.userId
            chatList.receiver_id = broadCastChat.receiverId
            chatList.sender_id = broadCastChat.senderId
            chatList.is_group_chat = broadCastChat.isGroupChat
            chatList.group_id = broadCastChat.groupId
            chatList.type = broadCastChat.type
            chatList.message_text = broadCastChat.messageText
            chatList.is_shared = broadCastChat.isShared
            chatList.is_forward = broadCastChat.isForward
            chatList.is_deleted = broadCastChat.isDeleted
            chatList.deleted_for_all = broadCastChat.deletedForAll
            chatList.deleted_by = broadCastChat.deletedBy
            chatList.tick = broadCastChat.tick
            chatList.is_reply = broadCastChat.isReply
            chatList.is_reply_to_message = broadCastChat.isReplyToMessage
            chatList.is_reply_to_story = broadCastChat.isReplyToStory
            chatList.story_id = broadCastChat.storyId
            chatList.is_replyback_to_reply = broadCastChat.isReplybackToReply
            chatList.is_secret = broadCastChat.isSecret
            chatList.is_read = broadCastChat.isRead
            chatList.created_at = broadCastChat.createdAt
            chatList.is_activity_label = broadCastChat.isActivityLabel
            chatList.event = broadCastChat.event
            chatList.is_broadcast_chat = broadCastChat.isBroadcastChat
            chatList.broadcast_id = broadCastChat.broadcastId
            chatList.content_id = broadCastChat.contentId
            chatList.is_content_reply = broadCastChat.isContentReply
            chatList.grp_other_user_id = broadCastChat.otherUserId
            chatList.broadcast_chat_id = broadCastChat.broadcastChatId
            chatList.entry_id = broadCastChat.entryId
            chatList.chat_id = broadCastChat.chatId
            chatList.chat_contents = RealmList()
            chatList.chat_audio = RealmList()
            chatList.chat_contacts = RealmList()
            chatList.chat_location = null
            chatList.chat_document = RealmList()
            chatList.grp_label_color = ""
            chatList.isSync = true
            broadcastChatList.add(chatList)
        }
        return broadcastChatList

    }*/

}