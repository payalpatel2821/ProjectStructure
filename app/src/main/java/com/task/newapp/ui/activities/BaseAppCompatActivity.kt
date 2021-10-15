package com.task.newapp.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.PowerManager
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.task.newapp.App
import com.task.newapp.interfaces.OnSocketEventsListener
import com.task.newapp.models.chat.ChatModel
import com.task.newapp.models.chat.ResponseChatMessage
import com.task.newapp.models.socket.DeleteChatSocket
import com.task.newapp.models.socket.PostSocket
import com.task.newapp.models.socket.SendUserDetailSocket
import com.task.newapp.realmDB.*
import com.task.newapp.realmDB.models.ChatList
import com.task.newapp.realmDB.models.Chats
import com.task.newapp.utils.*
import org.json.JSONObject
import java.util.*

abstract class BaseAppCompatActivity : AppCompatActivity(), OnSocketEventsListener {
    lateinit var onSocketEventsListener: OnSocketEventsListener
    private lateinit var socket: Socket
    private val networkMonitor = NetworkMonitorUtil(this)
    var powerManager: PowerManager? = null

    // Define the callback for what to do when data is received
    private val testReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val resultCode: Int = intent.getIntExtra("resultCode", RESULT_CANCELED)
            if (resultCode == RESULT_OK) {
                val resultValue: String? = intent.getStringExtra("resultValue")
                //Toast.makeText(this, resultValue, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        socket = App.getSocketInstance()
        onSocketEventsListener = this
        powerManager = getSystemService(POWER_SERVICE) as PowerManager
        networkMonitor.result = { isAvailable, type ->
            runOnUiThread {
                when (isAvailable) {
                    true -> {
                        when (type) {
                            ConnectionType.Wifi -> {
                                //internet_status.text = "Wifi Connection"
                                //App.getAppInstance().joinSocket()
                                //showToast("You are now online")

                                joinSocket()

                            }
                            ConnectionType.Cellular -> {
                                // internet_status.text = "Cellular Connection"
                                //  App.getAppInstance().joinSocket()
                                //showToast("You are now online")
                                joinSocket()

                            }
                            else -> {
                            }
                        }
                    }
                    false -> {
                        // internet_status.text = "No Connection"
                        if (App.fastSave.getBoolean(Constants.isLogin, false))
                            disconnectSocket(App.fastSave.getInt(Constants.prefUserId, 0), App.fastSave.getString(Constants.prefUserName, ""))
                        showToast("Internet connection lost")
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        networkMonitor.register()
        joinSocket()
        initSocketListeners()
        LocalBroadcastManager.getInstance(this).registerReceiver(testReceiver, IntentFilter());
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        networkMonitor.unregister()
    }

    override fun onPause() {
        super.onPause()
        if (!isScreenLocked()) {
            destroySocketListeners()

        }
    }

    private fun initSocketListeners() {
        socket.on(SocketConstant.is_online_response, onIsOnlineResponse)
        socket.on(SocketConstant.disconnect_response, onDisconnectResponse)
        socket.on(SocketConstant.post_like_dislike_response, onPostLikeResponse)
        socket.on(SocketConstant.join_response, onJoinResponse)
        socket.on(SocketConstant.new_message_response_private + getCurrentUserId(), onNewMessageResponsePrivate)
        socket.on(SocketConstant.user_typing_response + getCurrentUserId(), onUserTypingResponse)
        socket.on(SocketConstant.user_stop_typing_response + getCurrentUserId(), onUserStopTypingResponse)
        socket.on(SocketConstant.delete_private_chat_response + getCurrentUserId(), onDeletePrivateChatResponse)

        socket.on(SocketConstant.add_post_comment_response, onAddPostCommentResponse)
        socket.on(SocketConstant.delete_post_comment_response, onDeletePostCommentResponse)
        socket.on(SocketConstant.add_post_comment_reply_response, onAddPostCommentReplyResponse)
        socket.on(SocketConstant.add_post_delete_response, onDeletePostResponse)
    }

    private fun destroySocketListeners() {
        socket.off(SocketConstant.is_online_response, onIsOnlineResponse)
        socket.off(SocketConstant.disconnect_response, onDisconnectResponse)
        socket.off(SocketConstant.post_like_dislike_response, onPostLikeResponse)
        socket.off(SocketConstant.join_response, onJoinResponse)
        socket.off(SocketConstant.new_message_response_private + getCurrentUserId(), onNewMessageResponsePrivate)
        socket.off(SocketConstant.user_typing_response + getCurrentUserId(), onUserTypingResponse)
        socket.off(SocketConstant.user_stop_typing_response + getCurrentUserId(), onUserStopTypingResponse)
        socket.off(SocketConstant.delete_private_chat_response + getCurrentUserId(), onDeletePrivateChatResponse)
        socket.off(SocketConstant.add_post_comment_response, onAddPostCommentResponse)
        socket.off(SocketConstant.delete_post_comment_response, onDeletePostCommentResponse)
        socket.off(SocketConstant.add_post_comment_reply_response, onAddPostCommentReplyResponse)
        socket.on(SocketConstant.add_post_delete_response, onDeletePostResponse)
    }


    private val onIsOnlineResponse = Emitter.Listener { args ->
        val data = args[0] as JSONObject
        showLog(Constants.socket_tag, "onIsOnlineResponse--" + Gson().toJson(data))
        val status = data.getString(Constants.status).equals(Constants.online)
        val userId = data.getInt(Constants.receiver_id)
        runOnUiThread {
            // updateUserOnlineStatus(userId, status)
            onSocketEventsListener.onOnlineOfflineSocketEvent(userId, status)
        }

    }

    private val onDisconnectResponse = Emitter.Listener { args ->
        val data = args[0] as JSONObject
        showLog(Constants.socket_tag, "onUserDisconnect..." + Gson().toJson(data))
        val status = data.getString(Constants.status).equals(Constants.online)
        val userId = data.getInt(Constants.user_id)
        runOnUiThread {
            onSocketEventsListener.onOnlineOfflineSocketEvent(userId, false)
        }
    }

    private val onPostLikeResponse = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as String
            val postSocket: PostSocket = Gson().fromJson(data, PostSocket::class.java)
            onSocketEventsListener.onPostLikeDislikeSocketEvent(postSocket)
        }
    }

    private val onAddPostCommentResponse = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as String
            val postSocket: PostSocket = Gson().fromJson(data, PostSocket::class.java)
            onSocketEventsListener.onAddPostCommentSocketEvent(postSocket)
        }
    }

    private val onDeletePostCommentResponse = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as String
            val postSocket: PostSocket = Gson().fromJson(data, PostSocket::class.java)
            onSocketEventsListener.onDeletePostCommentSocketEvent(postSocket)
        }
    }

    private val onAddPostCommentReplyResponse = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as String
            val postSocket: PostSocket = Gson().fromJson(data, PostSocket::class.java)
            onSocketEventsListener.onAddPostCommentReplySocketEvent(postSocket)
        }
    }

    private val onDeletePostResponse = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as String
            val postSocket: PostSocket = Gson().fromJson(data, PostSocket::class.java)
            onSocketEventsListener.onDeletePostSocketEvent(postSocket)
        }
    }

    private val onJoinResponse = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as String
            showLog(Constants.socket_tag, "onUserJoinedTheChat $data")
            val sendUserDetailSocket: SendUserDetailSocket = Gson().fromJson(data, SendUserDetailSocket::class.java)
            getUserStatusEmitEvent(App.fastSave.getInt(Constants.prefUserId, 0), sendUserDetailSocket.userId)
        }
    }

    private val onNewMessageResponsePrivate = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as String
            showLog(Constants.socket_tag, "onNewMessageResponsePrivate $data")
            val chatModel = jsonToPojo(data.toString(), ResponseChatMessage::class.java) as ResponseChatMessage
            insertUpdateChatListDataUsingSocket(chatModel) { chatList ->
                onSocketEventsListener.onNewMessagePrivateSocketEvent(chatList)
            }
        }

    }

    private val onUserTypingResponse = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as String
            showLog(Constants.socket_tag, "onUserTypingResponse $data")
            val sendUserDetailSocket: SendUserDetailSocket = Gson().fromJson(data, SendUserDetailSocket::class.java)
            onSocketEventsListener.onUserTypingSocketEvent(sendUserDetailSocket.userId)

        }
    }

    private val onUserStopTypingResponse = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as String
            showLog(Constants.socket_tag, "onUserStopTypingResponse $data")
            val sendUserDetailSocket: SendUserDetailSocket = Gson().fromJson(data, SendUserDetailSocket::class.java)
            onSocketEventsListener.onUserStopTypingSocketEvent(sendUserDetailSocket.userId)
        }
    }

    private val onDeletePrivateChatResponse = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as String
            showLog(Constants.socket_tag, "onDeletePrivateChatResponse $data")
            val deleteChatSocket: DeleteChatSocket = Gson().fromJson(data, DeleteChatSocket::class.java)
            onSocketEventsListener.onDeletePrivateChatMessageResponse(deleteChatSocket)
        }
    }

    override fun onOnlineOfflineSocketEvent(userId: Int, status: Boolean) {
        updateUserOnlineStatus(userId, status)
    }

    override fun onPostLikeDislikeSocketEvent(postSocket: PostSocket) {
    }

    override fun onNewMessagePrivateSocketEvent(chatList: ChatList) {

    }

    override fun onUserTypingSocketEvent(receiverId: Int) {

    }

    override fun onUserStopTypingSocketEvent(receiverId: Int) {

    }

    override fun onAddPostCommentSocketEvent(postSocket: PostSocket) {

    }

    override fun onDeletePostCommentSocketEvent(postSocket: PostSocket) {

    }

    override fun onAddPostCommentReplySocketEvent(postSocket: PostSocket) {

    }

    override fun onDeletePostSocketEvent(postSocket: PostSocket) {

    }

    override fun onDeletePrivateChatMessageResponse(deleteChatSocket: DeleteChatSocket) {
        val chatIds = deleteChatSocket.chat_ids.split(",")
        if (chatIds.isNotEmpty()) {
            val chatIdArray = chatIds.map { it.toLong() }
            deleteChatContent(chatIdArray)
        }
        /*let allChatlist = RealmManager.sharedInstance.getOnetoOneChatList(senderid: APP_DELEGATE.currentUser?.user?.id ?? 0, receiverid: deleteData.sender_id ?? 0,strFlg: flag.flgprivate.rawValue, id: deleteData.receiver_id ?? 0)

                            if allChatlist.count > 0 {
                                RealmManager.sharedInstance.updateChatslist(fieldkey: DBTables.chatstable.id, id: deleteData.sender_id ?? 0, chatlist: allChatlist.last!, completion: { isSuccess in
                                    if isSuccess{
                                    }
                                })
                                NotificationCenter.default.post(name:.deleteprivatechat, object: deleteData.sender_id ?? 0, userInfo:nil )

                            }*/
        val allChat = getOneToOneChat(getCurrentUserId(), deleteChatSocket.sender_id)
        allChat?.let {
            if (allChat.isNotEmpty()) {
                updateChatsList(deleteChatSocket.sender_id, allChat.last()) { isSuccess ->
                    if (isSuccess)
                        onSocketEventsListener.onDeletePrivateChatMessageResponse(deleteChatSocket)
                }
            }

        }
    }

    private fun insertUpdateChatListDataUsingSocket(chatModel: ResponseChatMessage?, callback: (chatList: ChatList) -> Unit) {
        if (chatModel != null) {
            if (chatModel.data != null) {
                val data = chatModel.data
                val checkChatId = getSingleChatList(data.id)
                if (checkChatId == null) {
                    insertChatListDataUsingSocket(data, data.userId) { chatList ->
                        insertUserData(chatModel)
                        callback.invoke(chatList)
                    }
                } else {
                    updateChatListIdData(data.id, data, true) { chatList ->
                        insertUserData(chatModel)
                        callback.invoke(chatList)
                    }
                }
            }
        }
    }

    private fun insertUserData(chatModel: ResponseChatMessage) {
        chatModel.createRequest?.let { friendRequestModel ->
            insertFriendRequestData(createFriendRequest(friendRequestModel))
        }
        chatModel.sender?.let { sender ->
            insertUserData(prepareOtherUserData(sender)) { users ->

                showLog("CHATS : ", users.toString())
                updateChatListAndUserData(sender.id, users)
            }

        }
        chatModel.receiver?.let { receiver ->
            insertUserData(prepareOtherUserData(receiver)) { users ->
                showLog("CHATS : ", users.toString())
                updateChatListAndUserData(receiver.id, users)
            }
        }
    }

    private fun insertChatListDataUsingSocket(chatModel: ChatModel, senderId: Int, callback: (chatList: ChatList) -> Unit) {
        prepareChatLabelData(chatModel).let { chatList ->
            val checkChatId = getSingleChatList(chatModel.id)
            if (checkChatId == null) {
                insertChatListData(chatList)
            }
            val singleChat = getSingleChat(if (chatModel.isGroupChat == 1) chatModel.groupId else chatModel.userId)
            if (singleChat == null) {
                val chats = Chats()
                chats.id = if (chatModel.isGroupChat == 1) chatModel.groupId else chatModel.userId
                chats.isGroup = chatModel.isGroupChat == 1
                chats.isHook = false
                chats.isArchive = false
                chats.isBlock = false
                chats.chatList = chatList
                chats.userData = chatList.userData
                chats.updatedAt = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
                chats.currentTime = DateTimeUtils.instance?.formatDateTime(Date(), DateTimeUtils.DateFormats.yyyyMMddHHmmss.label).toString()
                insertChatData(chats)

            }
            updateChatsList(if (chatModel.isGroupChat == 1) chatModel.groupId else senderId, chatList) { isSuccess ->
                if (isSuccess) {
                    callback.invoke(chatList)
                }
            }

        }
    }

}