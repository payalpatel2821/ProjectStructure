package com.task.newapp.realmDB

import com.task.newapp.App
import com.task.newapp.models.*
import com.task.newapp.models.chat.SelectFriendWrapperModel
import com.task.newapp.realmDB.models.*
import com.task.newapp.realmDB.models.ChatContacts
import com.task.newapp.utils.Constants
import com.task.newapp.utils.findIndex
import com.task.newapp.utils.getCurrentUserId
import com.task.newapp.utils.getGroupLabelText
import io.realm.Realm
import io.realm.RealmList
import io.realm.Sort
import java.util.*


/**
 * delete record by ID
 */
/*fun deleteData(id: String) {
    realm.executeTransaction(Realm.Transaction { realm ->
        realm.where(User::class.java).equalTo(User.PROPERTY_user_name, id)
            .findFirst()?.deleteFromRealm()
    })
}*/

/**
 * read all records from table
 */
/*fun readData(): String {

    var string: String = ""
    realm.executeTransaction(Realm.Transaction { realm ->
        val results: RealmResults<User> = realm.where(User::class.java).findAll()
        for (employee in results) {
            string += employee.user_name + " : " + employee.password

        }

    })
    return string
}*/

/**
 * update particular data
 */
/*fun updateData(username: String, password: String) {
    realm.executeTransaction(Realm.Transaction { realm ->

        val results: User? =
            realm.where(User::class.java).equalTo(User.PROPERTY_user_name, username)
                .findFirst()

        if (results != null) {
            results.password = password
            realm.copyToRealm(results)
        }
    })

}*/

/**
 *  insert new record to table
 */
/*fun insertData(username: String, password: String) {
    realm.executeTransaction(Realm.Transaction {
        val user: User = User()
        user.user_name = username
        user.password = password
        it.copyToRealm(user)
    })
}*/

/*
fun filterUserByName(username: String): String {
    var userList: String  = ""
    realm.executeTransaction(Realm.Transaction { realm ->
        val results: RealmResults<User> =
            realm.where(User::class.java).like(User.PROPERTY_user_name, username).findAll()
        userList = results.asJSON()


    })
    return userList
}*/


/**   ------------------------------------ INSERT BEGIN ---------------------------------------------------------------  */

/**
 * Insert user object to the Users Table
 *
 * @param user : contains user data to be stored in DB
 */
fun insertUserData(users: Users) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.copyToRealmOrUpdate(users)
    })
}

/**
 * Insert user object to the Users Table with callback
 *
 * @param users : contains user data to be stored in DB
 * @param onRealmTransactionResult : callback returns to control after successful insertion
 */
fun insertUserData(users: Users, onRealmTransactionResult: OnRealmTransactionResult) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        onRealmTransactionResult.onSuccess(realm.copyToRealmOrUpdate(users))
    })
}

/**
 * Insert friend request data to FriendRequest Table
 *
 * @param request : contains request object
 */
fun insertFriendRequestData(request: RealmList<FriendRequest>) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.copyToRealmOrUpdate(request)

    })
}

/**
 * Insert Block user data to BlockUser Table
 *
 * @param blockUserData : contains block user object data
 */
fun insertBlockUserData(blockUserData: RealmList<BlockUser>) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.copyToRealmOrUpdate(blockUserData)
    })
}

/**
 * Insert LoggedIn user settings data to UserSettings Table
 *
 * @param userSettings : contains user settings data from login response
 */
fun insertUserSettingsData(userSettings: UserSettings) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.copyToRealmOrUpdate(userSettings)
    })
}

/**
 * Insert Friend settings data in bulk to FriendSettings Table
 *
 * @param friendSettings : contains list of Friend Settings data coming from login response
 */
fun insertFriendSettingsData(friendSettings: RealmList<FriendSettings>) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.copyToRealmOrUpdate(friendSettings)
    })
}

/**
 * Insert Groups data in bulk coming from login response
 *
 *
 * @param groups            : contains group details
 * @param groupUser         : contains group users details
 * @param chatlist          : contains group create label data
 * @param addUserChatList   : contains group add user label data
 * @param chats             : contains main list object of chat list
 */
fun insertGroupData(groups: RealmList<Groups>, groupUser: RealmList<GroupUser>, chatlist: RealmList<ChatList>, addUserChatList: RealmList<ChatList>, chats: RealmList<Chats>) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.copyToRealmOrUpdate(groups)
        realm.copyToRealmOrUpdate(groupUser)
        realm.copyToRealmOrUpdate(chatlist)
        realm.copyToRealmOrUpdate(addUserChatList)
        realm.copyToRealmOrUpdate(chats)
    })

}

/**
 * insert hook data in bulk coming from login response
 *
 * @param hook
 */
fun insertHookData(hook: RealmList<UserHook>) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.copyToRealmOrUpdate(hook)
    })
}

/**
 * Insert single hook object into UserHook table
 *
 * @param hook
 */
fun insertHookData(hook: UserHook) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.copyToRealmOrUpdate(hook)
    })
}

fun insertArchiveData(archive: UserArchive) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.copyToRealmOrUpdate(archive)
    })
}

fun insertArchiveData(archive: RealmList<UserArchive>) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.copyToRealmOrUpdate(archive)
    })
}

fun insertBroadcastData(broadcastTable: RealmList<BroadcastTable>) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.copyToRealmOrUpdate(broadcastTable)
    })
}

fun insertSingleBroadcastData(broadcastTable: BroadcastTable) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.copyToRealmOrUpdate(broadcastTable)
    })
}

fun insertChatListData(chatlist: ChatList) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.copyToRealmOrUpdate(chatlist)

    })

}

fun insertChatData(chats: Chats) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.copyToRealmOrUpdate(chats)
    })
}

fun insertChatContent(chatContents: RealmList<ChatContents>) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.copyToRealmOrUpdate(chatContents)
    })

}

fun insertChatContacts(chatContacts: RealmList<ChatContacts>) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.copyToRealmOrUpdate(chatContacts)
    })
}

fun insertChatVoice(chatVoice: RealmList<ChatVoice>) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.copyToRealmOrUpdate(chatVoice)
    })
}

fun insertChatDocument(chatDocument: RealmList<ChatDocument>) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.copyToRealmOrUpdate(chatDocument)
    })
}

fun insertChatLocation(chatLocation: ChatLocation) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.copyToRealmOrUpdate(chatLocation)
    })
}

fun insertChatAudio(chatAudio: RealmList<ChatAudio>) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.copyToRealmOrUpdate(chatAudio)
    })
}

fun insertNotificationToneData(notificationTone: RealmList<NotificationTone>) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.copyToRealmOrUpdate(notificationTone)
    })
}

/**   ------------------------------------ INSERT END ---------------------------------------------------------------  */

/**   ------------------------------------ READ BEGIN ---------------------------------------------------------------  */

fun getUserByUserId(userID: Int): Users? {
    return App.getRealmInstance().where(Users::class.java).equalTo(Users::receiver_id.name, userID).findFirst()
}

fun isLoggedInUser(userID: Int): Boolean {
    val user = getUserByUserId(userID)
    return user != null && user.receiver_id == App.fastSave.getInt(Constants.prefUserId, 0)
}

fun getSingleUserDetails(userId: Int): Users? {
    return App.getRealmInstance().where(Users::class.java).equalTo(Users::receiver_id.name, userId).findFirst()

}

fun getGroupUserById(groupId: Int, groupUserId: Int): GroupUser? {
    return App.getRealmInstance().where(GroupUser::class.java).equalTo(GroupUser::grp_id.name, groupId).and().equalTo(GroupUser::user_id.name, groupUserId).findFirst()
}

fun getAllChats(): List<Chats> {
    val chats = App.getRealmInstance().where(Chats::class.java).findAll()
        .sort(Chats::is_hook.name, Sort.DESCENDING, Chats::current_time.name, Sort.DESCENDING)
        .filter { !it.is_archive }.sortedByDescending { it.is_hook }
    return chats.toList()

}

fun getAllArchivedChat(): List<Chats> {
    return App.getRealmInstance().copyFromRealm(App.getRealmInstance().where(Chats::class.java).equalTo(Chats::is_archive.name, true).findAll().sort(Chats::current_time.name, Sort.DESCENDING))
}

fun getAllBroadcastChat(): List<BroadcastTable> {
    return App.getRealmInstance().copyFromRealm(App.getRealmInstance().where(BroadcastTable::class.java).findAll().sort(BroadcastTable::updated_at.name, Sort.DESCENDING))
}

fun getGroupsFromGroupId(groupId: List<Int>): List<Groups> {
    return App.getRealmInstance().where(Groups::class.java).`in`(Groups::grp_id.name, groupId.toTypedArray()).findAll()
}

fun getSingleChatContent(chatId: Int): ChatContents? {
    return App.getRealmInstance().where(ChatContents::class.java).equalTo(ChatContents::chat_id.name, chatId).findFirst()
}

fun getSingleChatAudio(chatId: Int): ChatAudio? {
    return App.getRealmInstance().where(ChatAudio::class.java).equalTo(ChatAudio::chat_id.name, chatId).findFirst()
}

fun getSingleChatContact(chatId: Int): ChatContacts? {
    return App.getRealmInstance().where(ChatContacts::class.java).equalTo(ChatContacts::chat_id.name, chatId).findFirst()
}

fun getSingleChatLocation(chatId: Int): ChatLocation? {
    return App.getRealmInstance().where(ChatLocation::class.java).equalTo(ChatLocation::chat_id.name, chatId).findFirst()
}

fun getSingleChatVoice(chatId: Int): ChatVoice? {
    return App.getRealmInstance().where(ChatVoice::class.java).equalTo(ChatVoice::chat_id.name, chatId).findFirst()
}

fun getSingleChatDocument(chatId: Int): ChatDocument? {
    return App.getRealmInstance().where(ChatDocument::class.java).equalTo(ChatDocument::chat_id.name, chatId).findFirst()
}

fun getSingleChatListItem(chatId: Int): ChatList? {
    return App.getRealmInstance().where(ChatList::class.java).equalTo(ChatList::chat_id.name, chatId).findFirst()
}

fun getSingleUserChat(id: Int): Chats? {
    return App.getRealmInstance().where(Chats::class.java).equalTo(Chats::id.name, id).findFirst()
}

fun getHookCount(): Int {
    return App.getRealmInstance().where(UserHook::class.java).count().toInt()
}

fun getAllGroups(): List<Groups> {
    return App.getRealmInstance().where(Groups::class.java).findAll().toList()
}

fun getAllGroupsInCommon(otherUserId: Int): List<GroupUser> {
    return App.getRealmInstance().where(GroupUser::class.java).equalTo(GroupUser::user_id.name, otherUserId).findAll()
}


fun getArchivedChatCount(): Int {
    return App.getRealmInstance().where(Chats::class.java).equalTo(Chats::is_archive.name, true).findAll().size
}

fun getChatPosition(chats: ArrayList<Chats>, userId: Int): Int {
    val ids: List<Int> = chats.map { it.id }
    return findIndex(ids, userId) ?: -1
}

fun getMyGroup(): List<Chats> {
    return App.getRealmInstance().where(Chats::class.java).equalTo(Chats::is_group.name, true).findAll().toList()
}

fun getUserNameFromId(groupUserId: List<Int>): String {
    val getUser = App.getRealmInstance().where(Users::class.java)
        .`in`(Users::receiver_id.name, groupUserId.toTypedArray())
        .findAll().filter { it.receiver_id != App.fastSave.getInt(Constants.prefUserId, 0) }
    val getUserName = getUser.map { it.first_name + " " + it.last_name }
    return getUserName.joinToString()
}

/**
 * It returns the common groups list between logged in user and his friend
 *
 * @param userID : contains the id of otherUserId
 * @return
 */
fun getCommonGroup(userID: Int): List<Chats> {
    return App.getRealmInstance().where(Chats::class.java)
        .equalTo(Chats::is_group.name, true).findAll()
        .filter { chats: Chats ->
            (chats.group_user_with_settings.map { it.user_id }).contains(userID)
        }
        .toList()
}

fun getAllNotificationTune(): List<NotificationTone> {
    return App.getRealmInstance().where(NotificationTone::class.java).findAll().toList()
}

fun getSelectedNotificationTuneName(friendId: Int): String {
    return App.getRealmInstance().where(FriendSettings::class.java).equalTo(FriendSettings::friend_id.name, friendId).findFirst()?.sound ?: ""
}

fun getGroupDetail(grpID: Int): Chats {
    return App.getRealmInstance().where(Chats::class.java).equalTo(Chats::is_group.name, true).findAll().first { it.id == grpID }
}

fun getMyGroupSetting(grpID: Int, userID: Int): GroupUser? {
    return App.getRealmInstance().where(GroupUser::class.java).equalTo(GroupUser::grp_id.name, grpID).and().equalTo(GroupUser::user_id.name, userID).findFirst()
}

fun getGroupCreatedUserName(userID: Int): String? {
    return App.getRealmInstance().where(Users::class.java).equalTo(Users::receiver_id.name, userID).findFirst().let { it!!.first_name + " " + it!!.last_name }
}

fun getAllFriends(status: String): List<FriendRequest> {
    return App.getRealmInstance().where(FriendRequest::class.java).limit(20).findAll().filter { it.status == status }
}

fun getSelectedFriends(ids: List<Int>): List<FriendRequest> {
    return App.getRealmInstance().where(FriendRequest::class.java)
        .`in`(FriendRequest::friend_id.name, ids.toTypedArray())
        .or()
        .`in`(FriendRequest::user_id.name, ids.toTypedArray()).findAll()
}

fun getSingleFriendSetting(friendId: Int): FriendSettings? {
    return App.getRealmInstance().where(FriendSettings::class.java).equalTo(FriendSettings::friend_id.name, friendId).findFirst()
}

/**   ------------------------------------ READ END ---------------------------------------------------------------  */

/**   ------------------------------------ UPDATE BEGIN ---------------------------------------------------------------  */

fun updateChatUserData(id: Int, user: Users) {

    val result = App.getRealmInstance().where(Chats::class.java).equalTo(Chats::id.name, id).findFirst()
    if (result != null) {
        result.name = (user.first_name ?: "") + " " + (user.last_name ?: "")
        result.user_data = user
        //App.getRealmInstance().copyToRealm(result)
    }

}

fun updateChatListUserData(id: Int, user: Users) {
    val result = App.getRealmInstance().where(ChatList::class.java).equalTo(ChatList::sender_id.name, id).findFirst()
    if (result != null) {
        result.user_data = user
        // App.getRealmInstance().copyToRealm(result)
    }
}

fun updateChatsList(id: Int, chatlist: ChatList) {

    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        val data = realm.where(Chats::class.java).equalTo(Chats::id.name, id).findFirst()
        if (data != null) {
            data.updated_at = chatlist.created_at
            data.current_time = chatlist.created_at
            data.chat_list = chatlist
            // realm.copyToRealmOrUpdate(data)
        }

    })


}

fun updateChatListAndUserData(id: Int, user: Users) {
    updateChatUserData(id, user)
    updateChatListUserData(id, user)
}

fun updateChatsIsHook(id: Int) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        val data = realm.where(Chats::class.java).equalTo(Chats::id.name, id).findFirst()
        if (data != null) {
            data.is_hook = !data.is_hook
            data.current_time = /*if (data.is_hook) DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString() else*/ data.updated_at
            realm.copyToRealm(data)
        }

    })
}

fun updateChatsIsArchive(id: Int) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        val data = realm.where(Chats::class.java).equalTo(Chats::id.name, id).findFirst()
        if (data != null) {
            data.is_archive = !data.is_archive
            data.current_time = /*if (data.is_hook) DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString() else*/ data.updated_at
            realm.copyToRealm(data)
        }

    })
}

fun updateUserOnlineStatus(id: Int, isOnline: Boolean) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        val data = realm.where(Chats::class.java).equalTo(Chats::id.name, id).findFirst()
        if (data != null) {
            data.is_online = isOnline
            realm.copyToRealm(data)
        }
    })
}

fun updateNotificationStatus(id: Int, isSet: Boolean) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        val data = realm.where(NotificationTone::class.java).equalTo(NotificationTone::id.name, id).findFirst()
        if (data != null) {
            data.is_set = isSet
            realm.copyToRealm(data)
        }
    })

}

fun updateFriendSettings(friendId: Int, friendSettings: FriendSettings) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        var data = realm.where(FriendSettings::class.java).equalTo(FriendSettings::friend_id.name, friendId).findFirst()
        if (data != null) {
            data = friendSettings
            realm.copyToRealmOrUpdate(data)
        }
    })

}

/**   ------------------------------------ UPDATE END ---------------------------------------------------------------  */

/**   ------------------------------------ DELETE  BEGIN ---------------------------------------------------------------  */

fun deleteHooks(ids: List<Int>) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.where(UserHook::class.java).`in`(UserHook::id.name, ids.toTypedArray()).not().findAll().deleteAllFromRealm()

    })
}

fun deleteHook(key: String, id: Int) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.where(UserHook::class.java).equalTo(key, id).findAll().deleteAllFromRealm()

    })
}

fun deleteArchive(key: String, id: Int) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.where(UserArchive::class.java).equalTo(key, id).findAll().deleteAllFromRealm()

    })
}

fun deleteBroadcast(broadcastId: Int) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.where(BroadcastTable::class.java).equalTo(BroadcastTable::broadcast_id.name, broadcastId).findAll().deleteAllFromRealm()
    })

}

fun clearDatabase() {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.deleteAll()

    })
}

/**   ------------------------------------ DELETE END ---------------------------------------------------------------  */


fun prepareLoggedInUserData(user: User): Users {
    val users = Users()
    users.first_name = user.firstName
    users.last_name = user.lastName
    users.profile_image = user.profileImage
    users.profile_color = user.profileColor
    users.receiver_id = user.id
    return users
}

fun prepareOtherUserData(user: OtherUserModel): Users {
    val users = Users()
    users.first_name = user.firstName
    users.last_name = user.lastName
    users.profile_image = user.profileImage
    users.profile_color = user.profileColor
    users.receiver_id = user.id
    return users
}

fun prepareHookData(hookData: List<LoginResponse.HookData>): RealmList<UserHook> {
    val ids = hookData.map { it.id }
    deleteHooks(ids)
    val hookDataList = RealmList<UserHook>()
    val users = RealmList<Users>()
    hookData.let {
        for (hookObj in it) {
            val userHook = UserHook()
            userHook.id = hookObj.id
            userHook.user_id = hookObj.userId
            userHook.friend_id = hookObj.friendId
            userHook.group_id = hookObj.groupId
            userHook.is_group = hookObj.isGroup
            userHook.is_friend = hookObj.isFriend


            if (hookObj.isGroup == 1) {
                updateChatsIsHook(hookObj.groupId)
            } else {
                val isChats = getSingleUserChat(hookObj.friendId)
                if (isChats == null) {
                    val chat = Chats()
                    chat.id = hookObj.friendId
                    chat.is_group = false
                    chat.is_hook = true
                    chat.updated_at = hookObj.updatedAt
                    chat.current_time = hookObj.updatedAt
                    insertChatData(chat)
                } else {
                    updateChatsIsHook(hookObj.friendId)
                }
            }
            hookObj.friend?.let { friend ->
                users.add(prepareOtherUserData(friend))
            }

            hookDataList.add(userHook)

        }
        if (users.size > 0) {
            for (user in users) {
                insertUserData(user, object : OnRealmTransactionResult {
                    override fun onSuccess(obj: Any) {
                        updateChatUserData(user.receiver_id, obj as Users)
                    }

                    override fun onError() {}

                })
            }

        }

    }

    return hookDataList
}

fun prepareChatsData(groupObj: LoginResponse.GetAllGroup): Chats {
    val groups = prepareGroupData(groupObj.groupData)
    val groupUser = prepareGroupUserData(groupObj.groupUserWithSettings)
    val createGroupLabelChatList = groupObj.createGroupLbl?.let { prepareGroupLabelData(it) }
    val addUserGroupChatList = groupObj.addUserInGp?.let { prepareGroupLabelData(it) }

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

fun prepareGroupLabelData(createGroupLbl: ChatLabel): ChatList {
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

fun prepareGroupUserData(groupUserWithSettings: List<LoginResponse.GetAllGroup.GroupUserWithSetting>): RealmList<GroupUser> {
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
        insertUserData(prepareOtherUserData(groupUserWithSetting.user))

    }
    return groupUserSettingsList
}

fun prepareGroupData(groupData: LoginResponse.GetAllGroup.GroupData): Groups {
    val groupObj = Groups()

    groupObj.grp_id = groupData.id
    groupObj.grp_user_id = groupData.userId
    groupObj.grp_name = groupData.name
    groupObj.grp_description = groupData.description
    groupObj.grp_icon = groupData.icon
    groupObj.grp_created_by = groupData.createdBy
    groupObj.grp_total_user = groupData.totalUsers
    groupObj.grp_other_user_id = groupData.otherUserId
    groupObj.grp_updated_at = groupData.updatedAt
    groupObj.grp_created_at = groupData.createdAt
    groupObj.grp_edit_info_permission = groupData.editInfoPermission
    groupObj.grp_send_msg = groupData.sendMsg

    return groupObj

}

fun prepareLoggedInUserSettings(loginResponse: LoginResponse): UserSettings {
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

fun prepareAllGroupDataForDB(getAllGroups: List<LoginResponse.GetAllGroup>): Array<Any> {

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

        chats.add(chatsObj)
    }
    return arrayOf(groups, groupUser, chatList, addUserChatList, chats)

}

fun prepareArchiveData(archiveData: List<LoginResponse.ArchiveData>): RealmList<UserArchive> {
    val archiveList = RealmList<UserArchive>()
    val users = RealmList<Users>()
    archiveData.let {
        for (archiveObj in archiveData) {
            val userArchive = UserArchive()
            userArchive.id = archiveObj.id
            userArchive.user_id = archiveObj.userId
            userArchive.friend_id = archiveObj.isFriend
            userArchive.group_id = archiveObj.groupId
            userArchive.is_group = archiveObj.isGroup
            userArchive.is_friend = archiveObj.isFriend


            if (archiveObj.isGroup == 1) {
                updateChatsIsArchive(archiveObj.groupId)

            } else {
                val isChats = getSingleUserChat(archiveObj.friendId)
                if (isChats == null) {
                    val chat = Chats()
                    chat.id = archiveObj.friendId
                    chat.is_group = false
                    chat.is_archive = true
                    chat.updated_at = archiveObj.updatedAt
                    chat.current_time = archiveObj.updatedAt
                    insertChatData(chat)

                } else {
                    updateChatsIsArchive(archiveObj.friendId)
                }
            }

            archiveObj.archiver?.let { friend ->
                users.add(prepareOtherUserData(friend))
            }
            archiveList.add(userArchive)

        }
        if (users.size > 0) {
            for (user in users) {
                insertUserData(user, object : OnRealmTransactionResult {
                    override fun onSuccess(obj: Any) {
                        updateChatUserData(user.receiver_id, obj as Users)
                    }

                    override fun onError() {}

                })
            }

        }
    }
    return archiveList
}

fun prepareBroadcastData(broadcasts: List<LoginResponse.Broadcast>): RealmList<BroadcastTable> {
    val broadcastList = RealmList<BroadcastTable>()
    broadcasts.let {
        for (broadcastObj in broadcasts) {
            broadcastList.add(prepareSingleBroadcastData(broadcastObj))
        }
    }
    return broadcastList
}

fun prepareSingleBroadcastData(broadcastObj: LoginResponse.Broadcast): BroadcastTable {
    val broadcastTableData = BroadcastTable()
    broadcastTableData.broadcast_id = broadcastObj.id
    broadcastTableData.user_id = broadcastObj.userId
    broadcastTableData.broad_name = broadcastObj.name
    broadcastTableData.broad_icon = broadcastObj.icon
    broadcastTableData.broad_total_user = broadcastObj.totalUsers
    broadcastTableData.broad_other_userid = broadcastObj.otherUserId
    broadcastTableData.created_at = broadcastObj.createdAt
    broadcastTableData.updated_at = broadcastObj.updatedAt
    //broadcastTableData.login_user = broadcastObj.userId
    broadcastTableData.chats = prepareBroadcastChatLabelData(broadcastObj.chat)
    return broadcastTableData
}

fun prepareBroadcastChatLabelData(broadCastChat: ChatLabel): RealmList<ChatList> {
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

fun prepareFriendSettingsDataForDB(friendSettings: List<FriendSetting>): RealmList<FriendSettings> {
    val friendSettingList: RealmList<FriendSettings> = RealmList()
    friendSettings.let {
        for (objUserSetting in friendSettings) {
            /*  val usersObj: Users? = getUserByUserId(objUserSetting.friendId)

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
              }*/
            friendSettingList.add(prepareSingleFriendSettingData(objUserSetting))
        }
    }
    return friendSettingList
}


fun prepareSingleFriendSettingData(objUserSetting: FriendSetting): FriendSettings? {
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
        return friendSettingObj
    }
    return null
}

fun prepareSingleFriendSettingData(objUserSetting: ResponseFriendSetting.Data): FriendSettings? {
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
        return friendSettingObj
    }
    return null
}

/**
 * Prepare friend request database object from the login response to store in FriendRequest table
 *
 * @param friendRequestList : the list of requests
 * @return the RealmList of the FriendRequest DB object which will be directly inserted to the Database
 */
fun prepareRequestDataForDB(friendRequestList: List<LoginResponse.GetAllRequest>): RealmList<FriendRequest> {
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

fun prepareNotificationToneData(notificationList: ArrayList<ResponseNotification.Data>): RealmList<NotificationTone> {
    val notificationToneList = RealmList<NotificationTone>()
    notificationList.let {
        for (notificationObj in notificationList) {
            val notificationTone = NotificationTone()
            notificationTone.id = notificationObj.id
            notificationTone.display_name = notificationObj.name
            notificationTone.notification_url = notificationObj.toneName
            notificationTone.sound_name = notificationObj.sound

            notificationToneList.add(notificationTone)

        }
    }
    return notificationToneList
}

fun prepareSelectFriendWrapperModelList(friendRequest: List<FriendRequest>): ArrayList<SelectFriendWrapperModel> {
    val allFriendsList: ArrayList<SelectFriendWrapperModel> = ArrayList()
    friendRequest.forEach {
        getSingleUserDetails(if (it.user_id == getCurrentUserId()) it.friend_id else it.user_id)?.let { users: Users ->
            val searchUser = SelectFriendWrapperModel(
                users.receiver_id,
                users.first_name ?: "",
                users.last_name ?: "",
                users.user_name ?: "",
                users.profile_image ?: "",
                users.profile_color ?: "",
                isChecked = false,
                isEdit = true
            )
            allFriendsList.add(searchUser)
        }

    }
    return allFriendsList
}

interface OnRealmTransactionResult {

    fun onSuccess(obj: Any)
    fun onError()

}