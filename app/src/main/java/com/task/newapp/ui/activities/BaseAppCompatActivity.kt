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
import com.task.newapp.models.socket.SendUserDetailSocket
import com.task.newapp.realmDB.*
import com.task.newapp.realmDB.models.ChatList
import com.task.newapp.utils.*
import org.json.JSONObject

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
        socket.on(SocketConstant.post_like_response, onPostLikeResponse)
        socket.on(SocketConstant.join_response, onJoinResponse)
        socket.on(SocketConstant.new_message_response_private + getCurrentUserId(), onNewMessageResponsePrivate)
        socket.on(SocketConstant.user_typing_response + getCurrentUserId(), onUserTypingResponse)
        socket.on(SocketConstant.user_stop_typing_response + getCurrentUserId(), onUserStopTypingResponse)

    }

    private fun destroySocketListeners() {
        socket.off(SocketConstant.is_online_response, onIsOnlineResponse)
        socket.off(SocketConstant.disconnect_response, onDisconnectResponse)
        socket.off(SocketConstant.post_like_response, onPostLikeResponse)
        socket.off(SocketConstant.join_response, onJoinResponse)
        socket.off(SocketConstant.new_message_response_private + getCurrentUserId(), onNewMessageResponsePrivate)
        socket.off(SocketConstant.user_typing_response + getCurrentUserId(), onUserTypingResponse)
        socket.off(SocketConstant.user_stop_typing_response + getCurrentUserId(), onUserStopTypingResponse)
    }


    private val onIsOnlineResponse = Emitter.Listener { args ->
        val data = args[0] as JSONObject
        showLog(Constants.socket_tag, "onIsOnlineResponse--" + Gson().toJson(data))
        val status = data.getString(Constants.status).equals(Constants.online)
        val userId = data.getInt(Constants.receiver_id)
        showLog(Constants.socket_tag, "IsOnline userID : $userId")
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
        showLog(Constants.socket_tag, "Disconnect userID : $userId")
        runOnUiThread {
            onSocketEventsListener.onOnlineOfflineSocketEvent(userId, false)
        }
    }

    private val onPostLikeResponse = Emitter.Listener { args ->
        val data = args[0] as String

        runOnUiThread {
            onSocketEventsListener.onPostLikeDislikeSocketEvent()
            /*  val data = args[0] as String
              if (data != null) {
                  val postSocket = Gson().fromJson(data, PostSocket::class.java)
                  if (fastSave.getInt(Constants.prefUserId, 0) != postSocket.user_id
                      && post_id == postSocket.post_id
                  ) {
                      //post_data_count(postSocket.getPost_id())
                  }
              }*/
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

    override fun onOnlineOfflineSocketEvent(userId: Int, status: Boolean) {
        updateUserOnlineStatus(userId, status)
    }

    override fun onPostLikeDislikeSocketEvent() {

    }

    override fun onNewMessagePrivateSocketEvent(chatList: ChatList) {

    }

    override fun onUserTypingSocketEvent(receiverId: Int) {

    }

    override fun onUserStopTypingSocketEvent(receiverId: Int) {

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
            updateChatsList(if (chatModel.isGroupChat == 1) chatModel.groupId else senderId, chatList) { isSuccess ->
                if (isSuccess)
                    callback.invoke(chatList)
            }

        }
    }

}