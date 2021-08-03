package com.task.newapp.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.task.newapp.App
import com.task.newapp.interfaces.OnSocketEventsListener
import com.task.newapp.models.SendUserDetailSocket
import com.task.newapp.realmDB.updateUserOnlineStatus
import com.task.newapp.utils.*
import org.json.JSONObject

abstract class BaseAppCompatActivity : AppCompatActivity() {
    lateinit var onSocketEventsListener: OnSocketEventsListener

    private lateinit var socket: Socket

    private val networkMonitor = NetworkMonitorUtil(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        socket = App.getSocketInstance()
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
                        // showToast("Connection lost")
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
        destroySocketListeners()
        if (isFinishing && App.fastSave.getBoolean(Constants.isLogin, false))
            disconnectSocket(App.fastSave.getInt(Constants.prefUserId, 0), App.fastSave.getString(Constants.prefUserName, ""))
    }

    private fun initSocketListeners() {
        socket.on(Constants.is_online_response, onIsOnlineResponse)
        socket.on(Constants.disconnect_response, onDisconnectResponse)
        socket.on(Constants.post_like_response, onPostLikeResponse)
        socket.on(Constants.join_response, onJoinResponse)
    }

    private fun destroySocketListeners() {
        socket.off(Constants.is_online_response, onIsOnlineResponse)
        socket.off(Constants.disconnect_response, onDisconnectResponse)
        socket.off(Constants.post_like_response, onPostLikeResponse)
        socket.off(Constants.join_response, onJoinResponse)
    }


    private val onIsOnlineResponse = Emitter.Listener { args ->
        val data = args[0] as JSONObject
        showLog(Constants.socket_tag, "onIsOnlineResponse--" + Gson().toJson(data))
        val status = data.getString(Constants.status).equals(Constants.online)
        val userId = data.getInt(Constants.receiver_id)
        showLog(Constants.socket_tag, "IsOnline userID : $userId")
        runOnUiThread {
            updateUserOnlineStatus(userId, status)
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
            updateUserOnlineStatus(userId, status)
            onSocketEventsListener.onOnlineOfflineSocketEvent(userId, false)
        }
    }

    private val onPostLikeResponse = Emitter.Listener { args ->
        val data = args[0] as String

        runOnUiThread {
            onSocketEventsListener.onPostLikeDislikeEvent()
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
}