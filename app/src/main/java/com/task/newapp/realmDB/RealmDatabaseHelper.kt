package com.task.newapp.realmDB

import com.task.newapp.App
import com.task.newapp.models.*
import com.task.newapp.models.chat.*
import com.task.newapp.realmDB.models.*
import com.task.newapp.realmDB.wrapper.ChatsWrapperModel
import com.task.newapp.realmDB.wrapper.SelectFriendWrapperModel
import com.task.newapp.utils.*
import com.task.newapp.utils.Constants.Companion.ChatTypeFlag
import com.task.newapp.utils.Constants.Companion.ChatTypeFlag.*
import com.task.newapp.utils.Constants.Companion.MessageType
import io.realm.Realm
import io.realm.RealmList
import io.realm.Sort
import io.realm.Sort.ASCENDING
import java.util.*
import kotlin.collections.ArrayList


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
    val realm = App.getRealmInstance()
    if (!realm.isInTransaction) {
        App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
            realm.copyToRealmOrUpdate(users)
        })
    } else {
        realm.copyToRealmOrUpdate(users)
    }
}

/**
 * Insert user object to the Users Table with callback
 *
 * @param users : contains user data to be stored in DB
 * @param onRealmTransactionResult : callback returns to control after successful insertion
 */
fun insertUserData(users: Users, callback: (Users) -> Unit) {
    val realm = App.getRealmInstance()
    if (!realm.isInTransaction) {
        App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
            callback.invoke(realm.copyToRealmOrUpdate(users))
        })
    } else {
        callback.invoke(realm.copyToRealmOrUpdate(users))
    }
}

/**
 * Insert friend request data to FriendRequest Table
 *
 * @param request : contains request object
 */
fun insertFriendRequestData(request: RealmList<FriendRequest>) {
    val realm = App.getRealmInstance()
    if (!realm.isInTransaction) {
        App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
            realm.copyToRealmOrUpdate(request)

        })
    } else {
        realm.copyToRealmOrUpdate(request)
    }
}

/**
 * Insert friend request data to FriendRequest Table
 *
 * @param request : contains request object
 */
fun insertFriendRequestData(request: FriendRequest) {
    val realm = App.getRealmInstance()
    if (!realm.isInTransaction) {
        App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
            realm.copyToRealmOrUpdate(request)
        })
    } else {
        realm.copyToRealmOrUpdate(request)
    }
}


/**
 * Insert Block user data to BlockUser Table
 *
 * @param blockUserData : contains block user object data
 */
fun insertBlockUserData(blockUserData: RealmList<BlockUser>) {
    val realm = App.getRealmInstance()
    if (!realm.isInTransaction) {
        App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
            realm.copyToRealmOrUpdate(blockUserData)
        })
    } else {
        realm.copyToRealmOrUpdate(blockUserData)
    }
}

/**
 * Insert LoggedIn user settings data to UserSettings Table
 *
 * @param userSettings : contains user settings data from login response
 */
fun insertUserSettingsData(userSettings: UserSettings) {
    val realm = App.getRealmInstance()
    if (!realm.isInTransaction) {
        App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
            realm.copyToRealmOrUpdate(userSettings)
        })
    } else {
        realm.copyToRealmOrUpdate(userSettings)
    }
}

/**
 * Insert Friend settings data in bulk to FriendSettings Table
 *
 * @param friendSettings : contains list of Friend Settings data coming from login response
 */
fun insertFriendSettingsData(friendSettings: RealmList<FriendSettings>) {
    val realm = App.getRealmInstance()
    if (!realm.isInTransaction) {
        App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
            realm.copyToRealmOrUpdate(friendSettings)
        })
    } else {
        realm.copyToRealmOrUpdate(friendSettings)
    }

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
fun insertGroupData(
    groups: RealmList<Groups>,
    groupUser: RealmList<GroupUser>,
    chatlist: RealmList<ChatList>,
    addUserChatList: RealmList<ChatList>,
    chats: RealmList<Chats>
) {
    val realm = App.getRealmInstance()
    if (!realm.isInTransaction) {
        App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
            realm.copyToRealmOrUpdate(groups)
            realm.copyToRealmOrUpdate(groupUser)
            realm.copyToRealmOrUpdate(chatlist)
            realm.copyToRealmOrUpdate(addUserChatList)
            realm.copyToRealmOrUpdate(chats)
        })
    } else {
        realm.copyToRealmOrUpdate(groups)
        realm.copyToRealmOrUpdate(groupUser)
        realm.copyToRealmOrUpdate(chatlist)
        realm.copyToRealmOrUpdate(addUserChatList)
        realm.copyToRealmOrUpdate(chats)
    }

}

/**
 * insert hook data in bulk coming from login response
 *
 * @param hook
 */
fun insertHookData(hook: RealmList<UserHook>) {
    val realm = App.getRealmInstance()
    if (!realm.isInTransaction) {
        App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
            realm.copyToRealmOrUpdate(hook)
        })
    } else {
        realm.copyToRealmOrUpdate(hook)
    }
}

/**
 * Insert single hook object into UserHook table
 *
 * @param hook
 */
fun insertHookData(hook: UserHook) {
    val realm = App.getRealmInstance()
    if (!realm.isInTransaction) {
        App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
            realm.copyToRealmOrUpdate(hook)
        })
    } else {
        realm.copyToRealmOrUpdate(hook)
    }
}

fun insertArchiveData(archive: UserArchive) {
    val realm = App.getRealmInstance()
    if (!realm.isInTransaction) {
        App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
            realm.copyToRealmOrUpdate(archive)
        })
    } else {
        realm.copyToRealmOrUpdate(archive)
    }
}

fun insertArchiveData(archive: RealmList<UserArchive>) {
    val realm = App.getRealmInstance()
    if (!realm.isInTransaction) {
        App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
            realm.copyToRealmOrUpdate(archive)
        })
    } else {
        realm.copyToRealmOrUpdate(archive)
    }
}

fun insertBroadcastData(broadcastTable: RealmList<BroadcastTable>) {
    val realm = App.getRealmInstance()
    if (!realm.isInTransaction) {
        App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
            realm.copyToRealmOrUpdate(broadcastTable)
        })
    } else {
        realm.copyToRealmOrUpdate(broadcastTable)
    }
}

fun insertSingleBroadcastData(broadcastTable: BroadcastTable) {
    val realm = App.getRealmInstance()
    if (!realm.isInTransaction) {
        App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
            realm.copyToRealmOrUpdate(broadcastTable)
        })
    } else {
        realm.copyToRealmOrUpdate(broadcastTable)
    }
}

fun insertChatListData(chatlist: ChatList) {
    val realm = App.getRealmInstance()
    if (!realm.isInTransaction) {
        App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
            realm.copyToRealmOrUpdate(chatlist)
        })
    } else {
        realm.copyToRealmOrUpdate(chatlist)
    }

}

fun insertChatData(chats: Chats) {
    val realm = App.getRealmInstance()
    if (!realm.isInTransaction) {
        App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
            realm.copyToRealmOrUpdate(chats)
        })
    } else {
        realm.copyToRealmOrUpdate(chats)
    }
}

fun insertChatContent(chatContents: RealmList<ChatContents>) {
    val realm = App.getRealmInstance()
    if (!realm.isInTransaction) {
        App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
            realm.copyToRealmOrUpdate(chatContents)
        })
    } else {
        realm.copyToRealmOrUpdate(chatContents)
    }

}

fun insertNotificationToneData(notificationTone: RealmList<NotificationTone>) {
    val realm = App.getRealmInstance()
    if (!realm.isInTransaction) {
        App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
            realm.copyToRealmOrUpdate(notificationTone)
        })
    } else {
        realm.copyToRealmOrUpdate(notificationTone)
    }
}

fun insertGroupTickData(groupManageTick: GroupManageTick) {
    val realm = App.getRealmInstance()
    if (!realm.isInTransaction) {
        App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
            realm.copyToRealmOrUpdate(groupManageTick)
        })
    } else {
        realm.copyToRealmOrUpdate(groupManageTick)
    }
}

fun insertContactHistoryData(contactHistory: ContactHistory) {
    val realm = App.getRealmInstance()
    if (!realm.isInTransaction) {
        App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
            realm.copyToRealmOrUpdate(contactHistory)
        })
    } else {
        realm.copyToRealmOrUpdate(contactHistory)
    }
}

fun insertMyContactData(myContacts: RealmList<MyContacts>) {
    val realm = App.getRealmInstance()
    if (!realm.isInTransaction) {
        App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
            realm.copyToRealmOrUpdate(myContacts)
        })
    } else {
        realm.copyToRealmOrUpdate(myContacts)
    }
}

/**  ------------------------------------ INSERT END ---------------------------------------------------------------  */

/**   ------------------------------------ READ BEGIN ---------------------------------------------------------------  */

fun getUserByUserId(userID: Int): Users? {
    return App.getRealmInstance().where(Users::class.java).equalTo(Users::receiverId.name, userID)
        .findFirst()
}

fun isLoggedInUser(userID: Int): Boolean {
    val user = getUserByUserId(userID)
    return user != null && user.receiverId == App.fastSave.getInt(Constants.prefUserId, 0)
}

fun getSingleUserDetails(userId: Int): Users? {
    return App.getRealmInstance().where(Users::class.java).equalTo(Users::receiverId.name, userId)
        .findFirst()

}

fun getGroupUserById(groupId: Int, groupUserId: Int): GroupUser? {
    return App.getRealmInstance().where(GroupUser::class.java).equalTo(GroupUser::id.name, groupId)
        .and().equalTo(GroupUser::userId.name, groupUserId).findFirst()
}

fun getAllChats(): List<Chats> {
    val chats = App.getRealmInstance().where(Chats::class.java).findAll()
        .sort(Chats::isHook.name, Sort.DESCENDING, Chats::currentTime.name, Sort.DESCENDING)
        .filter { !it.isArchive }.sortedByDescending { it.isHook }
    return chats.toList()

}

fun getAllArchivedChat(): List<Chats> {
    return App.getRealmInstance().copyFromRealm(
        App.getRealmInstance().where(Chats::class.java).equalTo(Chats::isArchive.name, true)
            .findAll().sort(Chats::currentTime.name, Sort.DESCENDING)
    )
}

fun getAllBroadcastChat(): List<BroadcastTable> {
    return App.getRealmInstance().copyFromRealm(
        App.getRealmInstance().where(BroadcastTable::class.java).findAll()
            .sort(BroadcastTable::updatedAt.name, Sort.DESCENDING)
    )
}

fun getGroupsFromGroupId(groupId: List<Int>): List<Groups> {
    return App.getRealmInstance().where(Groups::class.java)
        .`in`(Groups::id.name, groupId.toTypedArray()).findAll()
}

fun getSingleChatContent(chatId: Int): ChatContents? {
    return App.getRealmInstance().where(ChatContents::class.java)
        .equalTo(ChatContents::chatId.name, chatId).findFirst()
}

fun getSingleChat(id: Int): Chats? {
    return App.getRealmInstance().where(Chats::class.java).equalTo(Chats::id.name, id).findFirst()
}

fun getSingleChatList(chatId: Long): ChatList? {
    return App.getRealmInstance().where(ChatList::class.java).equalTo(ChatList::id.name, chatId)
        .findFirst()
}

fun getSingleGroupDetails(groupID: Int): Groups? {
    /*  let realm = self.getRealm()
      let results = realm.objects(GRP_TABLE.self).filter("\(DBTables.grouptable.grp_id) == %@",groupID).first
      return results*/
    return App.getRealmInstance().where(Groups::class.java).equalTo(Groups::id.name, groupID)
        .findFirst()
}

fun getHookCount(): Int {
    return App.getRealmInstance().where(UserHook::class.java).count().toInt()
}

fun getAllGroups(): List<Groups> {
    return App.getRealmInstance().where(Groups::class.java).findAll().toList()
}

fun getAllGroupsInCommon(otherUserId: Int): List<GroupUser> {
    return App.getRealmInstance().where(GroupUser::class.java)
        .equalTo(GroupUser::userId.name, otherUserId).findAll()
}


fun getArchivedChatCount(): Int {
    return App.getRealmInstance().where(Chats::class.java).equalTo(Chats::isArchive.name, true)
        .findAll().size
}

fun getChatPosition(chats: ArrayList<ChatsWrapperModel>, userId: Int): Int {
    val ids: List<Int> = chats.map { it.chats.id }
    return findIndex(ids, userId) ?: -1
}

fun getMyGroup(): List<Chats> {
    return App.getRealmInstance().where(Chats::class.java).equalTo(Chats::isGroup.name, true)
        .findAll().toList()
}

fun getUserNameFromId(groupUserId: List<Int>): String {
    val getUser = App.getRealmInstance().where(Users::class.java)
        .`in`(Users::receiverId.name, groupUserId.toTypedArray())
        .findAll().filter { it.receiverId != App.fastSave.getInt(Constants.prefUserId, 0) }
    val getUserName = getUser.map { it.firstName + " " + it.lastName }
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
        .equalTo(Chats::isGroup.name, true).findAll()
        .filter { chats: Chats ->
            (chats.groupUsers.map { it.userId }).contains(userID)
        }
        .toList()
}

fun getAllNotificationTune(): List<NotificationTone> {
    return App.getRealmInstance().where(NotificationTone::class.java).findAll().toList()
}

fun getSelectedNotificationTuneName(friendId: Int): String {
    return App.getRealmInstance().where(FriendSettings::class.java).equalTo(FriendSettings::friendId.name, friendId).findFirst()?.toneName ?: ""
}

fun getGroupDetail(grpID: Int): Chats {
    return App.getRealmInstance().where(Chats::class.java).equalTo(Chats::isGroup.name, true)
        .findAll().first { it.id == grpID }
}

fun getMyGroupSetting(grpID: Int, userID: Int): GroupUser? {
    return App.getRealmInstance().where(GroupUser::class.java)
        .equalTo(GroupUser::groupId.name, grpID).and().equalTo(GroupUser::userId.name, userID)
        .findFirst()
}

fun getGroupCreatedUserName(userID: Int): String? {
    return App.getRealmInstance().where(Users::class.java).equalTo(Users::receiverId.name, userID)
        .findFirst().let { it!!.firstName + " " + it!!.lastName }
}

fun getAllFriends(status: String): List<FriendRequest> {
    return App.getRealmInstance().where(FriendRequest::class.java).limit(20).findAll()
        .filter { it.status == status }
}

fun getSelectedFriends(ids: List<Int>): List<FriendRequest> {
    return App.getRealmInstance().where(FriendRequest::class.java)
        .`in`(FriendRequest::friendId.name, ids.toTypedArray())
        .or()
        .`in`(FriendRequest::userId.name, ids.toTypedArray()).findAll()
}

fun getSingleFriendSetting(friendId: Int): FriendSettings? {
    return App.getRealmInstance().where(FriendSettings::class.java)
        .equalTo(FriendSettings::friendId.name, friendId).findFirst()
}

fun getSingleChatListByLocalId(id: Long): ChatList? {
    return App.getRealmInstance().where(ChatList::class.java)
        .equalTo(ChatList::localChatId.name, id).findFirst()

}

fun getSingleChatContentByLocalId(id: Long): ChatContents? {
    return App.getRealmInstance().where(ChatContents::class.java)
        .equalTo(ChatContents::chatId.name, id).findFirst()
}

fun getOneToOneChat(senderId: Int, opponentId: Int): List<ChatList>? {
    return App.getRealmInstance().where(ChatList::class.java)
        .equalTo(ChatList::isGroupChat.name, (0).toInt())
        .and()
        .equalTo(ChatList::isBroadcastChat.name, (0).toInt())
        .and()
        .equalTo(ChatList::isSecret.name, (0).toInt())
        .and()
        .beginGroup()
        .equalTo(ChatList::userId.name, senderId)
        .and()
        .equalTo(ChatList::receiverId.name, opponentId)
        .endGroup()
        .or()
        .beginGroup()
        .equalTo(ChatList::userId.name, opponentId)
        .and()
        .equalTo(ChatList::receiverId.name, senderId)
        .endGroup()
        .findAll()
        .sort(ChatList::createdAt.name, ASCENDING)
}

fun getAllOneToOneChatListForSync(isSync: Boolean): List<ChatList> {
    return App.getRealmInstance().where(ChatList::class.java)
        .equalTo(ChatList::isSync.name, isSync)
        .and()
        .equalTo(ChatList::isForward.name, (0).toInt())
        .findAll().sort(ChatList::createdAt.name, ASCENDING).toList()


}

fun getSelectedNotificationTuneNameBYID(notificationId: Int): String {
    return App.getRealmInstance().where(NotificationTone::class.java)
        .equalTo(NotificationTone::id.name, notificationId).findFirst()?.displayName ?: ""
}

fun getAllContactHistory(): List<ContactHistory> {
    return App.getRealmInstance().where(ContactHistory::class.java).findAll()
}

fun getAllMyContact(): List<MyContacts>? {
    return App.getRealmInstance().where(MyContacts::class.java).findAll().toList()
}


/**   ------------------------------------ READ END ---------------------------------------------------------------  */

/**   ------------------------------------ UPDATE BEGIN ---------------------------------------------------------------  */

fun updateChatUserData(id: Int, user: Users) {
    val result =
        App.getRealmInstance().where(Chats::class.java).equalTo(Chats::id.name, id).findFirst()
    if (result != null) {
        result.name = (user.firstName ?: "") + " " + (user.lastName ?: "")
        result.userData = user
        App.getRealmInstance().copyToRealmOrUpdate(result)
    }

}

fun updateChatListUserData(id: Int, user: Users) {
    val result =
        App.getRealmInstance().where(ChatList::class.java).equalTo(ChatList::userId.name, id)
            .findFirst()
    if (result != null) {
        result.userData = user
        App.getRealmInstance().copyToRealmOrUpdate(result)
    }
}

fun updateChatsList(id: Int, chatList: ChatList?, callback: ((Boolean) -> Unit)? = null) {

    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        val data = realm.where(Chats::class.java).equalTo(Chats::id.name, id).findFirst()?.let {
            realm.copyFromRealm(it)
        }

        if (data != null) {
            data.updatedAt = chatList?.createdAt
            data.currentTime = chatList?.createdAt
            data.chatList = chatList
            realm.copyToRealmOrUpdate(data)
            callback?.invoke(true)
        }

    })


}

fun updateChatsList(id: Int, chatList: ChatList, listener: OnRealmTransactionResult) {

    val data =  App.getRealmInstance().where(Chats::class.java).equalTo(Chats::id.name, id).findFirst()?.let {
        App.getRealmInstance().copyFromRealm(it)
    }
    if (data != null) {
        data.updatedAt = chatList.createdAt
        data.currentTime = chatList.createdAt
        data.chatList = chatList
        listener.onSuccess(App.getRealmInstance().copyToRealmOrUpdate(data))
    }


}

fun updateBroadcastChatList(broadcastId: Int, chatList: ChatList) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        val data = realm.where(BroadcastTable::class.java)
            .equalTo(BroadcastTable::broadcastId.name, broadcastId).findFirst()
        if (data != null) {
            data.chats.add(chatList)
            realm.copyToRealmOrUpdate(data)
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
            data.isHook = !data.isHook
            data.currentTime =
                    /*if (data.is_hook) DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString() else*/
                data.updatedAt
            realm.copyToRealmOrUpdate(data)
        }
    })
}

fun updateChatsIsArchive(id: Int) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        val data = realm.where(Chats::class.java).equalTo(Chats::id.name, id).findFirst()
        if (data != null) {
            data.isArchive = !data.isArchive
            data.currentTime =
                    /*if (data.is_hook) DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString() else*/
                data.updatedAt
            realm.copyToRealmOrUpdate(data)
        }

    })
}

fun updateUserOnlineStatus(id: Int, isOnline: Boolean) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        val data = realm.where(Chats::class.java).equalTo(Chats::id.name, id).findFirst()
        if (data != null) {
            data.isOnline = isOnline
            realm.copyToRealmOrUpdate(data)
        }
    })
}

fun updateNotificationStatus(id: Int, isSet: Boolean) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        val data = realm.where(NotificationTone::class.java).equalTo(NotificationTone::id.name, id)
            .findFirst()
        if (data != null) {
            data.isSet = isSet
            realm.copyToRealmOrUpdate(data)
        }
    })
}

fun updateFriendSettings(friendId: Int, friendSettings: FriendSettings) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        var data =
            realm.where(FriendSettings::class.java).equalTo(FriendSettings::friendId.name, friendId)
                .findFirst()
        if (data != null) {
            data = friendSettings
            realm.copyToRealmOrUpdate(data)
        }
    })

}

fun updateGroupUserSettings(userId: Int, groupId: Int, groupuser: GroupUser) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        var data = realm.where(GroupUser::class.java).equalTo(GroupUser::id.name, groupId).and()
            .equalTo(GroupUser::userId.name, userId).findFirst()
        if (data != null) {
            data = groupuser
            realm.copyToRealmOrUpdate(data)
        }

    })
}


fun updateChatListIdData(id: Long, chatList: ChatModel, isSync: Boolean, callback: ((chatList: ChatList) -> Unit)? = null) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        val data = getSingleChatListByLocalId(id)
        if (data != null) {
            data.id = chatList.id.toLong()
            data.isGroupChat = chatList.isGroupChat
            data.isShared = chatList.isShared
            data.isForward = chatList.isForward
            data.isDeleted = chatList.isDeleted
            data.deletedForAll = chatList.deletedForAll
            data.deletedBy = chatList.deletedBy
            data.type = chatList.type
            data.tick = chatList.tick
            data.isReply = chatList.isReply
            data.isReplyToStory = chatList.isReplyToStory
            data.storyId = chatList.storyId
            data.isSecret = chatList.isSecret
            data.isRead = chatList.isRead
            data.deliverTime = chatList.deliverTime
            data.readTime = chatList.readTime
            data.createdAt = chatList.createdAt
            data.updatedAt = chatList.updatedAt
            data.isActivityLabel = chatList.isActivityLabel
            data.event = chatList.event
            data.isBroadcastChat = chatList.isBroadcastChat
            data.broadcastId = chatList.broadcastId
            data.otherUserId = chatList.otherUserId
            data.chatId = chatList.chatId
            data.isSync = isSync
            realm.copyToRealmOrUpdate(data)
            callback?.invoke(data)
        }
    })

}

fun updateChatContent(chatId: Long, chatContents: ChatContents) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        getSingleChatContentByLocalId(chatId)?.let { data ->
            data.id = chatContents.id
            data.chatId = chatContents.chatId
            data.content = chatContents.content
            data.type = chatContents.type
            data.caption = chatContents.caption
            data.size = chatContents.size
            data.duration = chatContents.duration
            data.title = chatContents.title
            data.name = chatContents.name
            data.number = chatContents.number
            data.email = chatContents.email
            data.profileImage = chatContents.profileImage
            data.location = chatContents.location
            data.latitude = chatContents.latitude
            data.longitude = chatContents.longitude
            data.endTime = chatContents.endTime
            data.sharingTime = chatContents.sharingTime
            data.locationType = chatContents.locationType
            data.deleteFor = chatContents.deleteFor
            data.createdAt = chatContents.createdAt
            data.updatedAt = chatContents.updatedAt
            data.localPath = chatContents.localPath
            realm.copyToRealmOrUpdate(data)
        }
    })
}


/**   ------------------------------------ UPDATE END ---------------------------------------------------------------  */

/**   ------------------------------------ DELETE  BEGIN ---------------------------------------------------------------  */

fun deleteHooks(ids: List<Int>) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.where(UserHook::class.java).`in`(UserHook::id.name, ids.toTypedArray()).not()
            .findAll().deleteAllFromRealm()

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
        realm.where(BroadcastTable::class.java)
            .equalTo(BroadcastTable::broadcastId.name, broadcastId).findAll().deleteAllFromRealm()
    })

}

fun deleteContactHistory() {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.where(ContactHistory::class.java).findAll().deleteAllFromRealm()

    })
}

fun deleteOneContactHistory(id: Int) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.where(ContactHistory::class.java).equalTo(ContactHistory::id.name, id).findAll().deleteAllFromRealm()

    })
}

fun clearDatabase() {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.deleteAll()
    })
}

fun deleteGroups(ids: List<Int>) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.where(Groups::class.java).`in`(Groups::id.name, ids.toTypedArray()).not().findAll()
            .deleteAllFromRealm()

    })
}


fun deleteBroadCasts(ids: List<Int>) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        realm.where(BroadcastTable::class.java)
            .`in`(BroadcastTable::broadcastId.name, ids.toTypedArray()).not().findAll()
            .deleteAllFromRealm()

    })
}

fun clearOneToOneChat(receiverId: Int) {
    App.getRealmInstance().executeTransaction(Realm.Transaction { realm ->
        val data = realm.where(ChatList::class.java).beginGroup()
            .equalTo(ChatList::userId.name, getCurrentUserId()).and()
            .equalTo(ChatList::receiverId.name, receiverId).endGroup()
            .or()
            .beginGroup().equalTo(ChatList::userId.name, receiverId).and()
            .equalTo(ChatList::receiverId.name, getCurrentUserId()).endGroup().findAll()

        data.toList()?.let { chatList ->
            chatList.forEach { item ->
                if (item.contacts.isNotEmpty()) {
                    item.contacts.forEach { chatContactObj ->
                        realm.where(ChatContents::class.java)
                            .equalTo(ChatContents::chatId.name, chatContactObj.chatId).findFirst()
                            ?.deleteFromRealm()
                    }
                }

                item.chatContents?.let {
                    realm.where(ChatContents::class.java)
                        .equalTo(ChatContents::chatId.name, it.chatId).findFirst()
                        ?.deleteFromRealm()
                }
            }
        }
        data.deleteAllFromRealm()

    })
    updateChatsList(receiverId, null)

}

fun clearMyContact(callback: ((isSuccess: Boolean) -> Unit)? = null) {
    App.getRealmInstance().executeTransaction { realm ->
        realm.where(MyContacts::class.java).findAll()?.deleteAllFromRealm()
        callback?.invoke(true)
    }
}

/**   ------------------------------------ DELETE END ---------------------------------------------------------------  */


fun prepareLoggedInUserData(user: User): Users {
    val users = Users()
    users.firstName = user.firstName
    users.lastName = user.lastName
    users.profileImage = user.profileImage
    users.profileColor = user.profileColor
    users.receiverId = user.id
    users.userName = user.accountId
    return users
}

fun prepareOtherUserData(user: OtherUserModel): Users {
    val users = Users()
    users.firstName = user.firstName
    users.lastName = user.lastName
    users.profileImage = user.profileImage
    users.profileColor = user.profileColor
    users.receiverId = user.id
    users.userName = user.accountId
    return users
}

fun prepareHookData(hookData: List<HookData>): RealmList<UserHook> {
    val ids = hookData.map { it.id }
    deleteHooks(ids)
    val hookDataList = RealmList<UserHook>()
    val users = RealmList<Users>()
    hookData.let {
        for (hookObj in it) {
            val userHook = UserHook()
            userHook.id = hookObj.id
            userHook.userId = hookObj.userId
            userHook.friendId = hookObj.friendId
            userHook.groupId = hookObj.groupId
            userHook.isGroup = hookObj.isGroup
            userHook.isFriend = hookObj.isFriend


            if (hookObj.isGroup == 1) {
                updateChatsIsHook(hookObj.groupId)
            } else {
                val isChats = getSingleChat(hookObj.friendId)
                if (isChats == null) {
                    val chat = Chats()
                    chat.id = hookObj.friendId
                    chat.isGroup = false
                    chat.isHook = true
                    chat.updatedAt = hookObj.updatedAt
                    chat.currentTime = hookObj.updatedAt
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
                insertUserData(user) { obj ->

                    updateChatUserData(user.receiverId, obj)
                }
            }
        }
    }

    return hookDataList
}

fun prepareChatsData(groupObj: GetAllGroup): Chats {
    val groups = prepareGroupData(groupObj.groupData)
    val groupUser = prepareGroupUserData(groupObj.groupUserWithSettings)
    //val createGroupLabelChatList = groupObj.createGroupLbl?.let { prepareChatLabelData(it) }
    // val addUserGroupChatList = groupObj.addUserInGp?.let { prepareChatLabelData(it) }

    val chats = Chats()
    chats.id = groupObj.groupData.id
    chats.name = groupObj.groupData.name
    chats.isGroup = true
    chats.currentTime = groupObj.groupData.updatedAt
    chats.updatedAt = groupObj.groupData.updatedAt
    chats.groupData = groups
    chats.groupUsers = groupUser
    // chats.chatList = addUserGroupChatList ?: createGroupLabelChatList

    return chats

}

fun prepareChatLabelData(chatModel: ChatModel): ChatList {


    val randomLocalId = getNewChatId()

    //  val second = Date().time.toString()//DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.ss.label)
    //  randomLocalId += if (second == "") 1 else second.toLong()

    val arrChatContact: RealmList<ChatContents> = RealmList()

    var isContact = false
    if (chatModel.type.lowercase() == MessageType.CONTACT.type.lowercase()) {
        isContact = true
        chatModel.contacts?.forEach {
            val chatContact = createChatContent(it)
            arrChatContact.add(chatContact)
        }
    }
    var messageText = chatModel.messageText
    messageText = createEventWiseLabel(chatModel, messageText ?: "")

    var userLabelColor = "#000000"
    if (chatModel.isGroupChat == 1) {
        val grpUserTable = getGroupUserById(chatModel.groupId, chatModel.userId)
        userLabelColor = grpUserTable?.labelColor ?: "#000000"
    }
    return ChatList.create(
        randomLocalId, chatModel.id, chatModel.userId, chatModel.receiverId, chatModel.isGroupChat, chatModel.groupId, chatModel.otherUserId ?: "",
        chatModel.type, messageText, chatModel.isShared, chatModel.isForward, chatModel.isDeleted, chatModel.deletedForAll, chatModel.deletedBy, chatModel.tick, chatModel.isReply,
        isStar = 0, chatModel.isReplyToStory, chatModel.isStoryReplyBackToReply, chatModel.storyId, chatModel.isSecret, chatModel.isRead, chatModel.deliverTime ?: "",
        chatModel.readTime ?: "", chatModel.createdAt, chatModel.isActivityLabel, chatModel.event, chatModel.isBroadcastChat, chatModel.broadcastId, chatModel.chatId,
        if (isContact) null else chatModel.chatContents?.let { createChatContent(chatModel.chatContents) }, if (isContact) arrChatContact else RealmList<ChatContents>(),
        userLabelColor, isSync = true
    )


}

fun createChatContent(chatContentsModel: ChatContentsModel?): ChatContents? {
    var chatContent: ChatContents? = null
    if (chatContentsModel != null) {
        chatContent = ChatContents()
        val chatContentId = getNewChatId()
        chatContent.contentId = chatContentId
        chatContent.id = chatContentsModel.id ?: 0
        chatContent.chatId = chatContentsModel.chatId ?: 0
        chatContent.content = chatContentsModel.content ?: ""
        chatContent.type = chatContentsModel.type ?: ""
        chatContent.caption = chatContentsModel.caption ?: ""
        chatContent.size = chatContentsModel.size ?: 0.0
        chatContent.duration = chatContentsModel.duration ?: 0.0
        chatContent.title = chatContentsModel.title ?: ""
        chatContent.name = chatContentsModel.name ?: ""
        chatContent.number = chatContentsModel.number ?: ""
        chatContent.email = chatContentsModel.email ?: ""
        chatContent.profileImage = chatContentsModel.profileImage ?: ""
        chatContent.location = chatContentsModel.location ?: ""
        chatContent.latitude = chatContentsModel.latitude ?: 0.0
        chatContent.longitude = chatContentsModel.longitude ?: 0.0
        chatContent.endTime = chatContentsModel.endTime ?: ""
        chatContent.sharingTime = chatContentsModel.sharingTime ?: ""
        chatContent.locationType = chatContentsModel.locationType ?: ""
        chatContent.deleteFor = chatContentsModel.deleteFor ?: ""
        chatContent.createdAt = chatContentsModel.createdAt ?: ""
        chatContent.updatedAt = chatContentsModel.updatedAt ?: ""
        chatContent.localPath = chatContentsModel.localPath ?: ""
        chatContent.isDownload = false
    }
    return chatContent
}

fun prepareGroupUserData(groupUserWithSettings: List<GetAllGroup.GroupUserWithSetting>): RealmList<GroupUser> {
    val groupUserSettingsList = RealmList<GroupUser>()
    for (groupUserWithSetting in groupUserWithSettings) {
        groupUserSettingsList.add(prepareSingleGroupUsersData(groupUserWithSetting))
        insertUserData(prepareOtherUserData(groupUserWithSetting.user))

    }
    return groupUserSettingsList
}

fun prepareGroupData(groupData: GetAllGroup.GroupData): Groups {
    val groupObj = Groups()

    groupObj.id = groupData.id
    groupObj.userId = groupData.userId
    groupObj.name = groupData.name
    groupObj.description = groupData.description
    groupObj.icon = groupData.icon
    groupObj.createdBy = groupData.createdBy
    groupObj.totalUser = groupData.totalUsers
    groupObj.otherUserId = groupData.otherUserId
    groupObj.updatedAt = groupData.updatedAt
    groupObj.createdAt = groupData.createdAt
    groupObj.editInfoPermission = groupData.editInfoPermission
    groupObj.sendMessage = groupData.sendMsg

    return groupObj

}

fun prepareLoggedInUserSettings(loginResponse: LoginResponse): UserSettings {
    val userSetting = loginResponse.userSetting
    val userSettingsObj = UserSettings()

    //Insert loggedIn user info to Users Table
    val loggedInUser: Users = prepareLoggedInUserData(loginResponse.data.user)
    insertUserData(loggedInUser)

    userSettingsObj.id = userSetting.id
    userSettingsObj.userId = userSetting.userId
    userSettingsObj.themeId = userSetting.themeId
    userSettingsObj.wallpaperId = userSetting.wallpaperId
    userSettingsObj.isChatNotification = userSetting.isChatNotification
    userSettingsObj.isNewPostNotify = userSetting.isNewPostNotify
    userSettingsObj.isTagNotification = userSetting.isTagNotification
    userSettingsObj.profileImage = userSetting.profileImage
    userSettingsObj.notificationToneId = userSetting.notificationToneId
    userSettingsObj.isFollowersViewable = userSetting.isFollowersViewable
    userSettingsObj.isFriendEnable = userSetting.isFriendEnable
    userSettingsObj.isImageWallpaper = userSetting.isImageWallpaper
    userSettingsObj.isColorWallpaper = userSetting.isColorWallpaper
    userSettingsObj.wallpaperColor = userSetting.wallPaperColor
    userSettingsObj.isDefaultWallpaper = userSetting.isDefaultWallpaper
    userSettingsObj.isNoWallpaper = userSetting.isNoWallpaper
    userSettingsObj.isGalleryWallpaper = userSetting.isGalleryWallpaper
    userSettingsObj.galleryImage = userSetting.galleryImage
    userSettingsObj.fontSize = userSetting.fontSize
    userSettingsObj.languages = userSetting.languages
    userSettingsObj.isEnterSend = userSetting.isEnterSend
    userSettingsObj.isMediaVisible = userSetting.isMediaVisible
    userSettingsObj.isPhotoAutoDownload = userSetting.isPhotoAutodownload
    userSettingsObj.isAudioAutoDownload = userSetting.isAudioAutodownload
    userSettingsObj.isVideoAutoDownload = userSetting.isVideoAutodownload
    userSettingsObj.isDocumentAutoDownload = userSetting.isDocumentAutodownload
    userSettingsObj.isPhotoAutoDownloadWifi = userSetting.isPhotoAutodownloadWifi
    userSettingsObj.isAudioAutoDownloadWifi = userSetting.isAudioAutodownloadWifi
    userSettingsObj.isVideoAutoDownloadWifi = userSetting.isVideoAutodownloadWifi
    userSettingsObj.isDocumentAutoDownloadWifi = userSetting.isDocumentAutodownloadWifi
    userSettingsObj.isPhotoAutoDownloadRoaming = userSetting.isPhotoAutodownloadRoaming
    userSettingsObj.isAudioAutoDownloadRoaming = userSetting.isAudioAutodownloadRoaming
    userSettingsObj.isVideoAutoDownloadRoaming = userSetting.isVideoAutodownloadRoaming
    userSettingsObj.isDocumentAutoDownloadRoaming = userSetting.isDocumentAutodownloadRoaming
    userSettingsObj.isDeleteRequest = userSetting.isDeleteRequest
    userSettingsObj.storyView = userSetting.storyView
    userSettingsObj.storyDownload = userSetting.storyDownload
    userSettingsObj.vibrateStatus = userSetting.vibrateStatus
    userSettingsObj.isPopupNotification = userSetting.isPopupNotification
    userSettingsObj.useHighPriorityNotification = userSetting.useHighPriorityNotification
    userSettingsObj.lastSeen = userSetting.lastSeen
    userSettingsObj.isVisible = userSetting.isVisible
    userSettingsObj.profileSeen = userSetting.profileSeen
    userSettingsObj.callRingtone = userSetting.callRigtone
    userSettingsObj.callVibrate = userSetting.callVibrate
    userSettingsObj.aboutSeen = userSetting.aboutSeen
    userSettingsObj.whoCanAddMeInGroup = userSetting.whoCanAddMeInGroup
    userSettingsObj.liveLocationSharing = userSetting.liveLocationSharing
    userSettingsObj.isFingerprintLockEnabled = userSetting.isFingerprintLockEnabled
    userSettingsObj.isShowSecurityNotification = userSetting.isShowSecurityNotification
    userSettingsObj.isTwoStepVerificationEnabled = userSetting.isTwoStepVerificationEnabled
    userSettingsObj.groupVibrateStatus = userSetting.groupVibrateStatus
    userSettingsObj.groupIsPopupNotification = userSetting.groupIsPopupNotification
    userSettingsObj.groupUseHighPriorityNotification = userSetting.groupUseHighPriorityNotification
    userSettingsObj.groupNotificationToneId = userSetting.groupNotificationToneId
    userSettingsObj.nearLocation = userSetting.nearLocation
    userSettingsObj.toneName = userSetting.toneName
    userSettingsObj.user = loggedInUser
    userSettingsObj.updatedAt = userSetting.updatedAt
    return userSettingsObj
}

fun prepareAllGroupDataForDB(getAllGroups: List<GetAllGroup>): Array<Any> {

    val ids = getAllGroups.map { it.groupData.id }
    deleteGroups(ids)

    val groups = RealmList<Groups>()
    val groupUser = RealmList<GroupUser>()
    val chatList = RealmList<ChatList>()
    val addUserChatList = RealmList<ChatList>()
    val chats = RealmList<Chats>()
    for (groupObj in getAllGroups) {
        val chatsObj = prepareChatsData(groupObj)

        groups.add(chatsObj.groupData)
        groupUser.addAll(chatsObj.groupUsers)


        groupObj.createGroupLbl?.let {
            chatList.add(prepareChatLabelData(groupObj.createGroupLbl).also {
                chatsObj.chatList = it
            })

        }

        groupObj.addUserInGp?.let {
            addUserChatList.add(prepareChatLabelData(groupObj.addUserInGp).also {
                chatsObj.chatList = it
            })
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
            userArchive.userId = archiveObj.userId
            userArchive.friendId = archiveObj.isFriend
            userArchive.groupId = archiveObj.groupId
            userArchive.isGroup = archiveObj.isGroup
            userArchive.isFriend = archiveObj.isFriend


            if (archiveObj.isGroup == 1) {
                updateChatsIsArchive(archiveObj.groupId)

            } else {
                val isChats = getSingleChat(archiveObj.friendId)
                if (isChats == null) {
                    val chat = Chats()
                    chat.id = archiveObj.friendId
                    chat.isGroup = false
                    chat.isArchive = true
                    chat.updatedAt = archiveObj.updatedAt
                    chat.currentTime = archiveObj.updatedAt
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
                insertUserData(user) { obj ->
                    updateChatUserData(user.receiverId, obj)


                }
            }

        }
    }
    return archiveList
}

fun prepareBroadcastData(broadcasts: List<LoginResponse.Broadcast>): RealmList<BroadcastTable> {

    val ids = broadcasts.map { it.id }
    deleteBroadCasts(ids)

    val broadcastList = RealmList<BroadcastTable>()
    broadcasts.let {
        for (broadcastObj in broadcasts) {
            broadcastList.add(prepareSingleBroadcastData(broadcastObj))
        }
    }
    return broadcastList
}

fun prepareSingleBroadcastData(broadcastObj: LoginResponse.Broadcast): BroadcastTable {
    val broadcastTableData = BroadcastTable.create(
        broadcastObj.id,
        broadcastObj.userId,
        broadcastObj.name,
        broadcastObj.icon ?: "",
        broadcastObj.totalUsers,
        broadcastObj.otherUserId,
        broadcastObj.createdAt,
        broadcastObj.updatedAt,
    )
    //broadcastTableData.broadcastId = broadcastObj.id
    //broadcastTableData.userId = broadcastObj.userId
    //broadcastTableData.broadcastName = broadcastObj.name
    //broadcastTableData.broadcastIcon = broadcastObj.icon
    //broadcastTableData.broadcastTotalUser = broadcastObj.totalUsers
    //broadcastTableData.broadcastOtherUserId = broadcastObj.otherUserId
    //broadcastTableData.createdAt = broadcastObj.createdAt
    //broadcastTableData.updatedAt = broadcastObj.updatedAt
    //broadcastTableData.login_user = broadcastObj.userId
    broadcastTableData.chats = prepareBroadcastChatLabelData(broadcastObj.chat)
    return broadcastTableData
}

fun prepareBroadcastChatLabelData(broadCastChat: ChatModel): RealmList<ChatList> {
    val broadcastChatList = RealmList<ChatList>()
    broadCastChat?.let {
        prepareChatLabelData(it).let { chatlist: ChatList ->
            insertChatListData(chatlist)
            updateBroadcastChatList(it.broadcastId, chatlist)
            broadcastChatList.add(chatlist)
        }
        /*   chatList.localChatId = it.id.toLong()
           chatList.id = it.id.toLong()
           chatList.userId = it.userId
           chatList.receiverId = it.receiverId
           chatList.isGroupChat = it.isGroupChat
           chatList.groupId = it.groupId
           chatList.type = it.type
           chatList.messageText = it.messageText
           chatList.isShared = it.isShared
           chatList.isForward = it.isForward
           chatList.isDeleted = it.isDeleted
           chatList.deletedForAll = it.deletedForAll
           chatList.deletedBy = it.deletedBy
           chatList.tick = it.tick
           chatList.isReply = it.isReply
           chatList.isReplyToStory = it.isReplyToStory
           chatList.storyId = it.storyId
           chatList.isStoryReplyBackToReply = it.isStoryReplyBackToReply
           chatList.isSecret = it.isSecret
           chatList.isRead = it.isRead
           chatList.createdAt = it.createdAt
           chatList.isActivityLabel = it.isActivityLabel
           chatList.event = it.event
           chatList.isBroadcastChat = it.isBroadcastChat
           chatList.broadcastId = it.broadcastId
           chatList.otherUserId = it.otherUserId
           chatList.broadcastChatId = it.broadcastChatId
           chatList.chatId = it.chatId
           chatList.groupLabelColor = ""
           chatList.isSync = true*/
    }
    return broadcastChatList

}

fun prepareBlockUserDataForDB(blockUsers: List<OtherUserModel>): RealmList<BlockUser> {
    val blockedList: RealmList<BlockUser> = RealmList()
    blockUsers.let {
        for (blockUser in blockUsers) {
            insertUserData(prepareOtherUserData(blockUser))
            val objBlockUser = BlockUser()
            objBlockUser.blockedUserId = blockUser.id
            objBlockUser.isBlocked = 1
            objBlockUser.isReported = 0
            blockedList.add(objBlockUser)
        }
    }
    return blockedList
}

fun prepareFriendSettingsDataForDB(friendSettings: List<FriendSetting>): RealmList<FriendSettings> {
    val friendSettingList: RealmList<FriendSettings> = RealmList()
    friendSettings.let {
        for (objUserSetting in friendSettings) {
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
        friendSettingObj.userId = objUserSetting.userId
        friendSettingObj.friendId = objUserSetting.friendId
        friendSettingObj.notificationToneId = objUserSetting.notificationToneId
        friendSettingObj.muteNotification = objUserSetting.muteNotification
        friendSettingObj.isCustomNotificationEnable = objUserSetting.isCustomNotificationEnable
        friendSettingObj.vibrateStatus = objUserSetting.vibrateStatus
        friendSettingObj.isPopupNotification = objUserSetting.isPopupNotification
        friendSettingObj.useHighPriorityNotification = objUserSetting.useHighPriorityNotification
        friendSettingObj.callRingtone = objUserSetting.callRingtone
        friendSettingObj.callVibrate = objUserSetting.callVibrate
        friendSettingObj.toneName = objUserSetting.toneName
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
        friendSettingObj.userId = objUserSetting.userId
        friendSettingObj.friendId = objUserSetting.friendId
        friendSettingObj.notificationToneId = objUserSetting.notificationToneId
        friendSettingObj.muteNotification = objUserSetting.muteNotification
        friendSettingObj.isCustomNotificationEnable = objUserSetting.isCustomNotificationEnable
        friendSettingObj.vibrateStatus = objUserSetting.vibrateStatus
        friendSettingObj.isPopupNotification = objUserSetting.isPopupNotification
        friendSettingObj.useHighPriorityNotification = objUserSetting.useHighPriorityNotification
        friendSettingObj.callRingtone = objUserSetting.callRigtone
        friendSettingObj.callVibrate = objUserSetting.callVibrate
        friendSettingObj.toneName = objUserSetting.toneName
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
fun prepareRequestDataForDB(friendRequestList: List<GetAllRequest>): RealmList<FriendRequest> {
    val requestList: RealmList<FriendRequest> = RealmList<FriendRequest>()
    for (getAllRequest in friendRequestList) {
        val friendRequest = FriendRequest()
        friendRequest.id = getAllRequest.id
        friendRequest.userId = getAllRequest.userId
        friendRequest.friendId = getAllRequest.friendId
        friendRequest.status = getAllRequest.status
        friendRequest.isRequest = getAllRequest.isRequest
        friendRequest.senderRequestCount = getAllRequest.senderRequestCount
        friendRequest.receiverRequestCount = getAllRequest.receiverRequestCount
        friendRequest.createdAt = getAllRequest.createdAt
        friendRequest.updatedAt = getAllRequest.updatedAt

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
            notificationTone.displayName = notificationObj.toneName
            notificationTone.notificationUrl = notificationObj.sound
            // notificationTone.soundName = notificationObj.sound

            notificationToneList.add(notificationTone)

        }
    }
    return notificationToneList
}

fun prepareSelectFriendWrapperModelList(friendRequest: List<FriendRequest>): ArrayList<SelectFriendWrapperModel> {
    val allFriendsList: ArrayList<SelectFriendWrapperModel> = ArrayList()
    friendRequest.forEach {
        getSingleUserDetails(if (it.userId == getCurrentUserId()) it.friendId else it.userId)?.let { users: Users ->
            val searchUser = SelectFriendWrapperModel(
                users.receiverId,
                users.firstName ?: "",
                users.lastName ?: "",
                users.userName ?: "",
                users.profileImage ?: "",
                users.profileColor ?: "",
                isChecked = false,
                isEdit = true
            )
            allFriendsList.add(searchUser)
        }

    }
    return allFriendsList
}

fun createFriendRequest(createRequest: FriendRequestModel): FriendRequest {
    val friendRequest = FriendRequest()
    friendRequest.id = createRequest.id
    friendRequest.userId = createRequest.userId
    friendRequest.friendId = createRequest.friendId
    friendRequest.isRequest = createRequest.isRequest
    friendRequest.status = createRequest.status
    friendRequest.senderRequestCount = createRequest.senderRequestCount
    friendRequest.receiverRequestCount = createRequest.receiverRequestCount
    friendRequest.createdAt = createRequest.createdAt
    friendRequest.updatedAt = createRequest.updatedAt
    return friendRequest
    //addObject(createRequest: fr, update: true)
}

fun createGroupTickManageData(chatList: ChatList, receiverId: Int): GroupManageTick {
    val randomId = getNewChatId()

    val grpTickManage = GroupManageTick()
    grpTickManage.id = randomId
    grpTickManage.userId = getCurrentUserId()
    grpTickManage.groupUserId = receiverId
    grpTickManage.chatId = chatList.id
    grpTickManage.groupId = chatList.groupId
    grpTickManage.tick = chatList.tick
    grpTickManage.isRead = chatList.isRead
    grpTickManage.deliverTime = chatList.deliverTime ?: ""
    grpTickManage.readTime = chatList.readTime ?: ""
    return grpTickManage
}

fun clearAllChatList(receiverId: Int, type: ChatTypeFlag) {
    when (type) {
        GROUPS -> {
        }
        PRIVATE -> clearOneToOneChat(receiverId)
        BROADCAST -> {
        }
        SECRET -> {
        }
    }
}

fun prepareSingleGroupUsersData(groupUserWithSetting: GetAllGroup.GroupUserWithSetting): GroupUser {
    val groupUserObj = GroupUser()
    groupUserObj.id = groupUserWithSetting.id
    groupUserObj.userId = groupUserWithSetting.userId
    groupUserObj.groupId = groupUserWithSetting.groupId
    groupUserObj.labelColor = groupUserWithSetting.labelColor
    groupUserObj.location = groupUserWithSetting.location
    groupUserObj.isAdmin = groupUserWithSetting.isAdmin
    groupUserObj.isAllowToAddPost = groupUserWithSetting.isAllowToAddPost
    groupUserObj.isMuteNotification = groupUserWithSetting.isMuteNotification
    groupUserObj.isReport = groupUserWithSetting.isReport
    groupUserObj.status = groupUserWithSetting.status
    groupUserObj.isDeleted = groupUserWithSetting.isDeleted
    groupUserObj.isAllowToEditInfo = groupUserWithSetting.isAllowToEditInfo
    groupUserObj.muteTime = groupUserWithSetting.muteTime
    groupUserObj.endMuteTime = groupUserWithSetting.endMuteTime
    groupUserObj.mediaViewable = groupUserWithSetting.isMediaViewable
    groupUserObj.customNotificationEnable = groupUserWithSetting.isCustomNotificationEnable
    groupUserObj.vibrateStatus = groupUserWithSetting.vibrateStatus
    groupUserObj.isPopupNotification = groupUserWithSetting.isPopupNotification
    groupUserObj.light = groupUserWithSetting.light
    groupUserObj.useHighPriorityNotification = groupUserWithSetting.useHighPriorityNotification
    groupUserObj.notificationToneId = groupUserWithSetting.notificationToneId
    groupUserObj.createdAt = groupUserWithSetting.createdAt
    groupUserObj.updatedAt = groupUserWithSetting.updatedAt
    return groupUserObj
}

fun prepareContactHistoryData(exploreContact: ResponseIsAppUser.Data): ContactHistory {
    val contactHistory = ContactHistory()
    exploreContact.let {
        contactHistory.id = exploreContact.id
        contactHistory.firstName = exploreContact.firstName
        contactHistory.lastName = exploreContact.lastName
        contactHistory.accountId = exploreContact.accountId
        contactHistory.profileImage = exploreContact.profileImage ?: ""
        contactHistory.profileColor = exploreContact.profileColor
        contactHistory.isVisible = exploreContact.isVisible
        contactHistory.email = exploreContact.email ?: ""
        contactHistory.mobile = exploreContact.mobile ?: ""
        contactHistory.about = exploreContact.about ?: ""
    }
    return contactHistory
}

fun prepareMyContactData(responseMyContact: List<ResponseMyContact.Data>): RealmList<MyContacts> {
    val myContacts: RealmList<MyContacts> = RealmList()
    responseMyContact.forEach { item ->
        if (item.appUserId != getCurrentUserId()) {
            val myContactsModel = MyContacts()
            myContactsModel.contactId = getNewChatId()
            myContactsModel.id = item.id
            myContactsModel.fullName = item.fullName
            myContactsModel.email = item.email ?: ""
            myContactsModel.number = item.number ?: ""
            myContactsModel.isAppUser = item.isAppUser
            myContactsModel.appUserId = item.appUserId
            myContactsModel.registerType = item.registerType ?: ""
            myContactsModel.profileImage = item.profileImage ?: ""
            myContactsModel.profileColor = item.profileColor ?: ""
            myContacts.add(myContactsModel)
        }
    }
    return myContacts
}

interface OnRealmTransactionResult {
    fun onSuccess(obj: Any)
}