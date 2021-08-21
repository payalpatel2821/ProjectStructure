// Please note : @LinkingObjects and default values are not represented in the schema and thus will not be part of the generated models
package com.task.newapp.realmDB.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ChatList : RealmObject() {
    @PrimaryKey
    var localChatId: Long = 0
    var id: Long = 0
    var userId: Int = 0
    var receiverId: Int = 0
    var isGroupChat: Int = 0
    var groupId: Int = 0
    var otherUserId: String? = null
    var type: String? = null
    var messageText: String? = null
    var isShared: Int = 0
    var isForward: Int = 0
    var isDeleted: Int = 0
    var deletedForAll: Int = 0
    var deletedBy: Int = 0
    var tick: Int = 0
    var isStar: Int = 0
    var isReply: Int = 0
    var chatId: Int = 0
    var isReplyToStory: Int = 0
    var isStoryReplyBackToReply: Int = 0
    var storyId: Int = 0
    var isSecret: Int = 0
    var isRead: Int = 0
    var deliverTime: String? = null
    var readTime: String? = null
    var createdAt: String? = null
    var updatedAt: String? = null
    var isActivityLabel: Int = 0
    var event: String? = null
    var isBroadcastChat: Int = 0
    var broadcastId: Int = 0
    var broadcastChatId: Int = 0
    var isSync: Boolean = false
    var chatContents: ChatContents? = null
    var contacts: RealmList<ChatContents> = RealmList<ChatContents>()
    var userData: Users? = null
    var groupLabelColor: String? = null
    /*var is_reply_to_message: Int = 0 payal
    var is_replyback_to_reply: Int = 0
    var is_content_reply: Int = 0
    var content_id: Int = 0
    var grp_other_user_id: String? = null
    var entry_id: Int = 0
    var chat_audio: RealmList<ChatAudio> = RealmList()
    var chat_location: ChatLocation? = null
    var chat_contacts: RealmList<ChatContacts> = RealmList()
    var chat_voice: RealmList<ChatVoice> = RealmList()
    var chat_document: RealmList<ChatDocument> = RealmList()*/

    companion object {
        fun create(
            localChatId: Long, id: Long, userId: Int, receiverId: Int, isGroupChat: Int, groupId: Int, otherUserId: String, type: String, messageText: String, isShared: Int,
            isForward: Int, isDeleted: Int, deletedForAll: Int, deletedBy: Int, tick: Int, isReply: Int, isStar: Int, isReplyToStory: Int, isStoryReplyBackToReply: Int,
            storyId: Int, isSecret: Int, isRead: Int, deliverTime: String, readTime: String, createdAt: String, isActivityLabel: Int, event: String, isBroadcastChat: Int,
            broadcastId: Int, chatId: Int, chatContents: ChatContents?, contacts: RealmList<ChatContents>, groupLabelColor: String, isSync: Boolean
        ): ChatList {
            val chatList = ChatList()
            chatList.localChatId = localChatId
            chatList.id = id
            chatList.userId = userId
            chatList.receiverId = receiverId
            chatList.isGroupChat = isGroupChat
            chatList.groupId = groupId
            chatList.otherUserId = otherUserId
            chatList.type = type
            chatList.messageText = messageText
            chatList.isShared = isShared
            chatList.isForward = isForward
            chatList.isDeleted = isDeleted
            chatList.deletedForAll = deletedForAll
            chatList.deletedBy = deletedBy
            chatList.tick = tick
            chatList.isStar = isStar
            chatList.isReply = isReply
            chatList.chatId = chatId
            chatList.isReplyToStory = isReplyToStory
            chatList.isStoryReplyBackToReply = isStoryReplyBackToReply
            chatList.storyId = storyId
            chatList.isSecret = isSecret
            chatList.isRead = isRead
            chatList.deliverTime = deliverTime
            chatList.readTime = readTime
            chatList.createdAt = createdAt
            //chatList.updatedAt = updatedAt
            chatList.isActivityLabel = isActivityLabel
            chatList.event = event
            chatList.isBroadcastChat = isBroadcastChat
            chatList.broadcastId = broadcastId
            chatList.isSync = isSync
            chatList.chatContents = chatContents
            chatList.contacts = contacts
            chatList.groupLabelColor = groupLabelColor

            return chatList

        }
    }
}
