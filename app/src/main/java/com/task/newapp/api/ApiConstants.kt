package com.task.newapp.api

const val login_url = "/api_v1/login"
const val reset_password_url = "api_v1/forgot-password-reset-submit"
const val send_code_forgot_url = "/api_v1/forgot-password-code"
const val verify_OTP_url = "/api_v1/forgot-password-verify-code"
const val register_url = "/api_v1/register"
const val check_username_url = "/api_v1/update-accointid/{user}"
const val get_username_url = "/api_v1/generate-accointid/{user}"
const val send_code_normal_url = "/api_v1/user-mail-verification"
const val verify_OTP_normal_url = "/api_v1/user-check-verification"
const val get_unread_messages = "/api_v1/get_unread_message"
const val hook_chat = "api_v1/hook-chat"
const val archive_chat = "api_v1/archive-chat"
const val delete_chat = "api_v1/delete_chat"
const val get_all_posts = "/api_v1/get-all-posts/{limit}/{offset}"
const val add_postlikeunlike = "/api_v1/post_like"
const val post_comment_onoff = "/api_v1/post-comment-on-off"
const val post_delete = "/api_v1/post/{id}"
const val post_comment = "/api_v1/post_comment"
const val postSaveUnsave = "/api_v1/post-save"
const val allpostcomment = "/api_v1/post-comment-detail"
const val get_myprofile_url = "/api_v1/view_profile/{user}"
const val set_usersetting_url = "/api_v2/user-settings"
const val get_user_follower_following_url = "/api_v1/user-follower-following"
const val get_user_profileviewer = "/api_v1/profile_view_list"
const val set_user_follow_unfollow = "/api_v1/follow"
const val set_profile_group_page_friend_post = "/api_v1/profile_group_page_friend_post"
const val set_friendsetting_url = "/api_v1/friends-setting"
const val set_block_unblock_report_url = "/api_v1/block-report"
const val get_notification_tone = "/api_v1/get-notification-tone"
const val add_post = "/api_v1/post"
const val post_pattern = "/api_v1/post-pattern"
const val search_contacts = "/api_v1/search-contacts"
const val add_postcomment = "/api_v1/post_comment"
const val commentdelete = "/api_v1/post_comment_remove"
const val update_profile_pic = "/api_v1/update-profile-picture"
const val update_profile_detail = "/api_v1/update-profile-detail"
const val change_password = "/api_v1/change-password"
const val change_email = "/api_v1/update-email-request"
const val check_verify_email = "/api_v1/check-email-request"
const val delete_account = "/api_v1/request-delete-account"
const val exit_group = "/api_v1/exit-group/{id}"
const val delete_group = "/api_v1/delete-group/{id}"
const val report_group = "/api_v1/report-group-user/{id}"
const val add_remove_admin = "/api_v1/add-remove-admin"
const val add_participates = "/api_v1/add-participates"
const val set_group_setting_url = "/api_v1/group-settings"
const val delete_msg_from_chat="api_v1/delete_msg_from_chat"

const val create_broadcast = "/api_v1/broadcast"
const val delete_broadcast = "/api_v1/broadcast/{id}";
const val send_chat_message = "/api_v1/chat";
const val updated_all_group = "/api_v1/updated-all-group"
const val get_post_detail = "/api_v1/post-details/{id}"
const val post_data_count = "/api_v1/post-data-count/{id}"


const val contact_sync = "/api_v2/contact-sync"
const val search_sync_contact = "/api_v2/search-sync-contact"
const val getSettings = "/api_v2/settings"

