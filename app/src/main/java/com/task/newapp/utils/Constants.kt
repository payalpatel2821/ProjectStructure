package com.task.newapp.utils

import com.task.newapp.R

class Constants {
    companion object {
        const val deviceToken = "c926RJ-JQS62C7bolZsMrq:APA91bF-_8V1mRc-cpuKlTmw2kL7iYIua9HI4uZye76jR1lII7gDZT8HOABpBIubisYO7bNnyDbYNNVYoiX47bwkRODU6vAWJjz9z3wNLBCSni5dyzTjc91xQ3FAWDalu4BwZvA4p0h0"
        const val user_name = "user_name"
        const val device_token = "device_token"
        const val device_type = "device_type"
        const val device_type_android = "Android"
        const val email = "email"
        const val mobile = "mobile"
        const val typeCode = "type"
        const val comment_text = "comment_text"
        const val post_id = "post_id"
        const val offset = "offset"
        const val limit = "limit"
        const val latitude = "latitude"
        const val longitude = "longitude"
        const val password = "password"
        const val password_confirmation = "password_confirmation"
        const val code = "code"
        const val first_name = "first_name"
        const val last_name = "last_name"
        const val account_id = "account_id"
        const val profile_image = "profile_image"
        const val isLogin = "isLogin"
        const val isFirstTime = "isFirstTime"
        const val isVisible = "is_visible"
        const val nearLocation = "near_location"
        const val follow_id = "follow_id"
        const val user_id = "user_id"
        const val tag_follow = "tag_follow"
        const val tag_message = "tag_message"
        const val id = "id"
        const val title = "title"
        const val type = "type"
        const val hook_id = "hook_id"
        const val is_hook = "is_hook"
        const val group = "group"
        const val friend = "friend"
        const val archive_id = "archive_id"
        const val is_archive = "is_archive"
        const val receiver_id = "receiver_id"
        const val is_secret = "is_secret"

        enum class RegistrationStepsEnum(val index: Int) {
            STEP_1(0), //Basic information
            STEP_2(1), //Validate yourself
            STEP_3(2), //set password
            STEP_4(3)  //set username
        }


        enum class MessageEvents(val eventName: String) {

            CREATE("create"),
            ADD_USER("add_user"),
            REMOVE_USER("remove_user"),
            CHAT("chat"),
            DATE("date");

            companion object {
                fun getMessageEventFromName(name: String): MessageEvents {
                    for (events in values()) {
                        if (events.eventName.equals(name)) {
                            return events
                        }
                    }
                    return MessageEvents.CHAT
                }
            }

        }

        enum class MessageType(val type: String) {
            TYPE_INDICATOR("type_indicator"),
            LABEL("lbl"),
            TEXT("text"),
            MIX("mix"),
            LOCATION("location"),
            CONTACT("contact"),
            AUDIO("audio"),
            VOICE("voice"),
            DOCUMENT("document"),
            STORY("story"),
            VIDEO("video"),
            LINK("link"),
            DATE("date");

            companion object {
                fun getMessageTypeFromText(text: String): MessageType {
                    for (obj in values()) {
                        if (obj.type == text) {
                            return obj
                        }
                    }
                    return TEXT
                }
            }
        }

        enum class ChatContentType(val contentType: String) {
            IMAGE("image"),
            VIDEO("video"),
            AUDIO("audio"),
            VOICE("voice"),
            CONTACT("contact"),
            CURRENT("current"),
            PDF("pdf"),
            DOCUMENT("document"),
            LOCATION("location")

        }

        enum class ProfileNavigation(val fromname: String, val title: Int) {
            FROM_FOLLOWINGS("following", R.string.following),
            FROM_FOLLOWERS("follower", R.string.followers),
            FROM_PROFILE_VIEWS("profile_view", R.string.profile_views),
            FROM_FRIENDS("friend", R.string.friends);
        }

        enum class PostNavigation(val flag : String,val title:Int){
            FROM_POST("post",R.string.my_post),
            FROM_TAGGED_POST("tag_post",R.string.tagged_posts),
            FROM_SAVED_POST("save_post",R.string.saved_posts)
        }

        //-----------------Pref--------------------
        const val prefUserId = "prefUserId"
        const val prefUser = "prefUser"
        const val prefToken = "prefToken"
        const val prefIsRemember = "prefIsRemember"
        const val prefUserNameRemember = "prefUserNameRemember"
        const val prefPasswordRemember = "prefPasswordRemember"

        //----------------------Socket-------------------------
        const val post_delete_response = "post_delete_response"
        const val story_broadcast = "story_broadcast"
        const val story_delete_response = "story_delete_response"
        const val post_like = "post_like"
        const val post_like_response = "post_like_response"
        const val disconnect = "disconnect"

        // emit event
        const val join = "join"
        const val isonline = "isonline"
    }
}
