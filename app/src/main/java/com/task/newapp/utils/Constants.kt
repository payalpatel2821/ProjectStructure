package com.task.newapp.utils

import com.task.newapp.R
import java.io.Serializable

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
        const val comment_id = "comment_id"
        const val is_comment_reply = "is_comment_reply"
        const val hastags = "hastags"
        const val offset = "offset"
        const val flag = "flag"
        const val term = "term"
        const val limit = "limit"
        const val latitude = "latitude"
        const val longitude = "longitude"
        const val location = "location"
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
        const val post_comment = "post_comment"
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
        const val broadcast_id = "broadcast_id"
        const val is_secret = "is_secret"
        const val MAX_IMAGE_COUNT_IF_VIDEO_SELECTED = 15
        const val MAX_IMAGE_COUNT = 30
        const val MAX_VIDEO_COUNT = 5
        const val PAGE_SIZE = 20
        const val data = "data"
        const val SIZE_DEFAULT = 2048
        const val SIZE_LIMIT = 4096

        var sInputImageWidth = 0
        var sInputImageHeight = 0
        const val socket_tag = "SOCKET"
        const val status = "status"
        const val online = "online"
        const val isModified = "isModified"
        const val register_type = "register_type"
        const val page = "page"
        const val follow = "follow"
        const val mute_notification = "mute_notification"
        const val friend_id = "friend_id"
        const val is_custom_notification_enable = "is_custom_notification_enable"
        const val notification_tone_id = "notification_tone_id"
        const val vibrate_status = "vibrate_status"
        const val INTENT_SERVICE_PROGRESS = "INTENT_SERVICE_PROGRESS"
        const val INTENT_SERVICE_COMPLETE = "INTENT_SERVICE_COMPLETE"
        const val report_reason = "report_reason"
        const val about = "about"
        const val date_of_birth = "date_of_birth"
        const val anniversary = "anniversary"
        const val current = "current"
        const val is_set = "is_set"
        const val delete_reason = "delete_reason"
        const val group_id = "group_id"
        const val make_admin_id = "make_admin_id"
        const val remove_admin_id = "remove_admin_id"
        const val remove_user = "remove_user"
        const val users = "users"
        const val name = "name"
        const val icon = "icon"


        // bundle value
        const val bundle_custom_notification = "Custom_notification"
        const val bundle_notification_tone = "Notification_Tune"
        const val bundle_vibration = "Vibration"
        const val bundle_email = "email"

        const val bundle_selected_friends = "selected_friends"
        const val bundle_navigate_from = "navigate_from"
        const val bundle_opponent_id = "opponent_id"

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
            FROM_MY_FRIENDS("friend", R.string.my_friends),
            FROM_FRIENDS("friend", R.string.friends)
        }

        enum class PostNavigation(val flag: String, val title: Int) {
            FROM_POST("post", R.string.all_post),
            FROM_MY_POST("post", R.string.my_post),
            FROM_TAGGED_POST("tag_post", R.string.tagged_posts),
            FROM_SAVED_POST("save_post", R.string.saved_posts)
        }

        enum class SelectFriendsNavigation(val fromname: String, val title: Int) : Serializable {
            FROM_CREATE_GROUP("create_group", R.string.create_group),
            FROM_CREATE_BROADCAST("create_broadcast", R.string.create_broadcast),
            FROM_EDIT_GROUP("edit_group", R.string.create_group),
            FROM_EDIT_BROADCAST("edit_broadcast", R.string.create_broadcast)
        }

        enum class FriendRequestStatus(val status: String) {
            ACCEPT("Accept"),
            REJECT("Reject"),
            UNFRIEND("Unfriend")
        }

        //-----------------Pref--------------------
        const val prefUserId = "prefUserId"
        const val prefUser = "prefUser"
        const val prefUserName = "prefUserName"
        const val prefToken = "prefToken"
        const val prefIsRemember = "prefIsRemember"
        const val prefUserNameRemember = "prefUserNameRemember"
        const val prefPasswordRemember = "prefPasswordRemember"

        //----------------------Socket-------------------------

        //On events
        const val userjoinedthechat = "userjoinedthechat"
        const val isonlineresponse = "isonlineresponse"
        const val userdisconnect = "userdisconnect"
        const val join_response = "join_response"
        const val is_online_response = "is_online_response"
        const val disconnect_response = "disconnect_response"
        const val post_delete_response = "post_delete_response"
        const val story_broadcast = "story_broadcast"
        const val story_delete_response = "story_delete_response"
        const val post_like_response = "post_like_response"
        const val newMessage = "newMessage"

        // emit events
        const val join = "join"
        const val isonline = "isonline"
        const val disconnect = "disconnect"
        const val post_like = "post_like"
        const val is_online = "is_online"

    }
}
