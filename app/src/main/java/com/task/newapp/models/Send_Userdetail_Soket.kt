package com.task.newapp.models

import com.google.gson.annotations.SerializedName

class Send_Userdetail_Soket {
    @SerializedName("user_id")
    var user_id: Int

    @SerializedName("receiver_id")
    var receiver_id = 0

    @SerializedName("user_name")
    var user_name: String? = null

    @SerializedName("flag")
    var flag: String? = null

    @SerializedName("other_user_id")
    var other_user_id: String? = null

    constructor(user_id: Int, user_name: String?) {
        this.user_id = user_id
        this.user_name = user_name
    }

    constructor(user_id: Int, receiver_id: Int) {
        this.user_id = user_id
        this.receiver_id = receiver_id
    }

    constructor(user_id: Int, receiver_id: Int, user_name: String?, flag: String?) {
        this.user_id = user_id
        this.receiver_id = receiver_id
        this.user_name = user_name
        this.flag = flag
    }

    constructor(user_id: Int, receiver_id: Int, user_name: String?, other_user_id: String?, flag: String?) {
        this.user_id = user_id
        this.receiver_id = receiver_id
        this.user_name = user_name
        this.other_user_id = other_user_id
        this.flag = flag
    }
}