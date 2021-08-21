package com.task.newapp.utils

import com.appizona.yehiahd.fastsave.FastSave
import com.github.nkzawa.socketio.client.IO
import com.google.gson.Gson
import com.task.newapp.App
import com.task.newapp.BuildConfig
import com.task.newapp.models.socket.SendUserDetailSocket
import com.task.newapp.models.User
import com.task.newapp.models.chat.ResponseChatMessage
import java.net.URI

fun joinSocket() {
    try {
        if (App.socket == null) {   //if socket object is null then first initialize the object and then connect
            App.socket = IO.socket(URI.create(BuildConfig.port_url))
            App.socket!!.connect()

        } else if (!App.socket!!.connected()) {
            App.socket!!.connect()
        }
        val prefUser = FastSave.getInstance().getObject(Constants.prefUser, User::class.java)

        if (prefUser != null) {
            joinSocketEmitEvent(prefUser.id, prefUser.uName)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun disconnectSocket(user_id: Int, user_name: String?) {
    disconnectSocketEmitEvent(user_id, user_name)
    App.socket!!.disconnect()
    App.socket!!.close()
}

/*------------------- Emit Events -----------------------*/

fun joinSocketEmitEvent(userId: Int, userName: String) {
    val sendUserDetailSocket = SendUserDetailSocket(userId, userName)
    val joinValue = Gson().toJson(sendUserDetailSocket)

    showLog(Constants.socket_tag, "joinSocketEmitEvent :  $joinValue : ${App.getSocketInstance().connected()}")
    App.getSocketInstance().emit(SocketConstant.join, joinValue)
}

fun disconnectSocketEmitEvent(user_id: Int, user_name: String?) {
    val sendUserDetailSocket = SendUserDetailSocket(user_id, user_name)
    val joinValue = Gson().toJson(sendUserDetailSocket)
    showLog(Constants.socket_tag, "disconnectSocketEmitEvent :$joinValue")
    App.getSocketInstance().emit(SocketConstant.disconnect, joinValue)
}

fun getUserStatusEmitEvent(user_id: Int, receiver_id: Int) {
    val sendUserDetailSocket = SendUserDetailSocket(user_id, receiver_id)
    val isOnlineValue = Gson().toJson(sendUserDetailSocket)
    showLog(Constants.socket_tag, "updateUserStatusEmitEvent :$isOnlineValue")
    App.getSocketInstance().emit(SocketConstant.is_online, isOnlineValue)
}

fun sendOneToOneMessageEmitEvent(message: ResponseChatMessage) {
    App.getSocketInstance().emit(SocketConstant.new_message_private, Gson().toJson(message))

}

fun userTypingEmitEvent(userId: Int, receiverId: Int, userName: String) {
    val sendUserDetailSocket = SendUserDetailSocket(userId, receiverId, userName)
    val userTypingPayload = Gson().toJson(sendUserDetailSocket)
    showLog(Constants.socket_tag, "userTypingEmitEvent :$userTypingPayload")
    App.getSocketInstance().emit(SocketConstant.user_typing, userTypingPayload)
}

fun userStopTypingEmitEvent(userId: Int, receiverId: Int, userName: String) {
    val sendUserDetailSocket = SendUserDetailSocket(userId, receiverId, userName)
    val userStopTypingPayload = Gson().toJson(sendUserDetailSocket)
    App.getSocketInstance().emit(SocketConstant.user_stop_typing, userStopTypingPayload)

}