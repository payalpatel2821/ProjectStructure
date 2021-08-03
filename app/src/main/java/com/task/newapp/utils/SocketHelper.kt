package com.task.newapp.utils

import com.appizona.yehiahd.fastsave.FastSave
import com.github.nkzawa.socketio.client.IO
import com.google.gson.Gson
import com.task.newapp.App
import com.task.newapp.BuildConfig
import com.task.newapp.models.SendUserDetailSocket
import com.task.newapp.models.User
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
    showLog(Constants.socket_tag, "joinSocketEmitEvent :  $joinValue")
    App.getSocketInstance().emit(Constants.join, joinValue)
}

fun disconnectSocketEmitEvent(user_id: Int, user_name: String?) {
    val sendUserDetailSocket = SendUserDetailSocket(user_id, user_name)
    val joinValue = Gson().toJson(sendUserDetailSocket)
    showLog(Constants.socket_tag, "disconnectSocketEmitEvent :$joinValue")
    App.getSocketInstance().emit(Constants.disconnect, joinValue)
}

fun getUserStatusEmitEvent(user_id: Int, receiver_id: Int) {
    val sendUserDetailSocket = SendUserDetailSocket(user_id, receiver_id)
    val isOnlineValue = Gson().toJson(sendUserDetailSocket)
    showLog(Constants.socket_tag, "updateUserStatusEmitEvent :$isOnlineValue")
    App.getSocketInstance().emit(Constants.is_online, isOnlineValue)
}