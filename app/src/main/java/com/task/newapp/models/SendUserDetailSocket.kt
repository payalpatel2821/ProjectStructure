package com.task.newapp.models

import com.google.gson.annotations.SerializedName

class SendUserDetailSocket {
    @SerializedName("user_id")
    var userId: Int

    @SerializedName("receiver_id")
    var receiverId = 0

    @SerializedName("user_name")
    var userName: String? = null

    @SerializedName("flag")
    var flag: String? = null

    @SerializedName("other_user_id")
    var otherUserId: String? = null

    constructor(user_id: Int, user_name: String?) {
        this.userId = user_id
        this.userName = user_name
    }

    constructor(user_id: Int, receiver_id: Int) {
        this.userId = user_id
        this.receiverId = receiver_id
    }

    constructor(user_id: Int, receiver_id: Int, user_name: String?, flag: String?) {
        this.userId = user_id
        this.receiverId = receiver_id
        this.userName = user_name
        this.flag = flag
    }

    constructor(user_id: Int, receiver_id: Int, user_name: String?, other_user_id: String?, flag: String?) {
        this.userId = user_id
        this.receiverId = receiver_id
        this.userName = user_name
        this.otherUserId = other_user_id
        this.flag = flag
    }
}