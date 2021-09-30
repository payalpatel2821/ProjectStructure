package com.task.newapp.api

//import okhttp3.RequestBody
import com.task.newapp.models.*
import com.task.newapp.models.chat.CreateBroadcastResponse
import com.task.newapp.models.chat.ResponseChatMessage
import com.task.newapp.models.chat.ResponseGetUnreadMessage
import com.task.newapp.models.chat.ResponseGroupData
import com.task.newapp.models.contact.ContactSyncAPIModel
import com.task.newapp.models.post.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiHelper {

    @POST(login_url)
    fun doLogin(@Body hashMap: HashMap<String, Any>): Observable<LoginResponse>

    @POST(reset_password_url)
    fun doResetPassword(@Body hashMap: HashMap<String, Any>): Observable<CommonResponse>

    @POST(send_code_forgot_url)
    fun sendCodeForgotPass(@Body hashMap: HashMap<String, Any>): Observable<ResponseSendCode>

    @POST(verify_OTP_url)
    fun verifyOTPForgotPass(@Body hashMap: HashMap<String, Any>): Observable<ResponseVerifyOTP>

    @POST(register_url)
    fun doRegister(@Body data: RequestBody): Observable<ResponseRegister>

    @GET(check_username_url)
    fun checkUsername(@Path("user") user: String): Observable<ResponseVerifyOTP>

    @GET(get_username_url)
    fun getUsername(@Path("user") user: String): Observable<ResponseGetUsername>

    @POST(send_code_normal_url)
    fun sendCodeNormalUrl(@Body hashMap: HashMap<String, Any>): Observable<ResponseSendCode>

    @POST(verify_OTP_normal_url)
    fun verifyOTPNormal(@Body hashMap: HashMap<String, Any>): Observable<ResponseVerifyOTP>

    @GET(get_unread_messages)
    fun getUnreadMessage(): Observable<ResponseGetUnreadMessage>

    @POST(hook_chat)
    fun hookChat(@Body hashMap: HashMap<String, Any>): Observable<CommonResponse>

    @POST(archive_chat)
    fun archiveChat(@Body hashMap: HashMap<String, Any>): Observable<CommonResponse>

    @POST(delete_chat)
    fun deleteChat(@Body hashMap: HashMap<String, Any>): Observable<CommonResponse>

    @GET(get_all_posts)
    fun get_all_posts(@Path("limit") limit: Int, @Path("offset") offset: Int): Observable<ResponseGetAllPost>

    @POST(add_postlikeunlike)
    fun add_postlikeunlike(@Body hashMap: HashMap<String, Any>): Observable<ResponseGetPostLikeUnlike>

    @POST(postSaveUnsave)
    fun postSaveUnsave(@Body hashMap: HashMap<String, Any>): Observable<CommonResponse>

    @POST(allpostcomment)
    fun allpostcomment(@Body hashMap: HashMap<String, Any>): Observable<ResponseGetAllPostComments>

    @POST(post_comment)
    fun post_comment(@Body hashMap: HashMap<String, Any>): Observable<ResponsePostComment>

    @GET(get_myprofile_url)
    fun getMyProfile(@Path("user") user: String): Observable<ResponseMyProfile>

    @POST(set_usersetting_url)
    fun setUserSetting(@Body hashMap: HashMap<String, Any>): Observable<ResponseUserSetting>

    @POST(get_user_follower_following_url)
    fun getUserFollowerFollowing(@Body hashMap: HashMap<String, Any>): Observable<ResponseUserFollowingFollower>

    @POST(get_user_profileviewer)
    fun getUserProfileViwer(@Body hashMap: HashMap<String, Any>): Observable<ResponseUserFollowingFollower>

    @POST(set_user_follow_unfollow)
    fun setUserFollowUnfollow(@Body hashMap: HashMap<String, Any>): Observable<ResponseFollowUnfollow>

    @POST(set_profile_group_page_friend_post)
    fun setProfileGroupPageFriendPost(@Body hashMap: HashMap<String, Any>): Observable<ResponsePostList>

    @POST(set_profile_group_page_friend_post)
    fun getFriendList(@Body hashMap: HashMap<String, Any>): Observable<ResponseUserFollowingFollower>

    @Multipart
    @POST(add_post)
    fun addPost(
        @Part("turn_off_comment") turn_off_comment: RequestBody,
        @Part("hastags") hastags: RequestBody,
        @Part("title") title: RequestBody,
        @Part("type") type: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("location") location: RequestBody,
        @Part("user_tags") user_tags: RequestBody,
        @Part captionarray: List<MultipartBody.Part>,
        @Part typearray: List<MultipartBody.Part>,
        @Part thumbarray: List<MultipartBody.Part>,
        @Part imagevideoarray: List<MultipartBody.Part>
    ): Observable<CommonResponse>


    @POST(add_post)
    fun addPostThought(
//        @Part("turn_off_comment") turn_off_comment: RequestBody,
//        @Part("hastags") hastags: RequestBody,
//        @Part("title") title: RequestBody,
//        @Part("type") type: RequestBody,
//        @Part("latitude") latitude: RequestBody,
//        @Part("longitude") longitude: RequestBody,
//        @Part("location") location: RequestBody,
//        @Part("user_tags") user_tags: RequestBody,

//        @Part thought_type: MultipartBody.Part,
//        @Part background_type: MultipartBody.Part,
//        @Part color: MultipartBody.Part,
//        @Part pattern_id: MultipartBody.Part,
//        @Part alignment: MultipartBody.Part,
//        @Part is_bold: MultipartBody.Part,
//        @Part is_italic: MultipartBody.Part,
//        @Part is_underline: MultipartBody.Part,
//        @Part font_color: MultipartBody.Part,
//        @Part content: MultipartBody.Part

        @Body data: RequestBody

    ): Observable<CommonResponse>

    @POST(set_profile_group_page_friend_post)
    fun getPageList(@Body hashMap: HashMap<String, Any>): Observable<ResponsePageList>

    @POST(set_friendsetting_url)
    fun setFriendSetting(@Body hashMap: HashMap<String, Any>): Observable<ResponseFriendSetting>

    @POST(set_block_unblock_report_url)
    fun setBlockUnblockReportSetting(@Body hashMap: HashMap<String, Any>): Observable<ResponseBlockReportUser>

    @POST(get_notification_tone)
    fun getNotificationTune(@Body hashMap: HashMap<String, Any>): Observable<ResponseNotification>

    @POST(post_pattern)
    fun post_pattern(@Body hashMap: HashMap<String, Any>): Observable<ResponsePattern>

    @POST(search_contacts)
    fun search_contacts(@Body hashMap: HashMap<String, Any>): Observable<ResponseFriendsList>

    @POST(add_postcomment)
    fun add_postcomment(@Body hashMap: HashMap<String, Any>): Observable<ResponseAddPostComment>

    @POST(commentdelete)
    fun commentdelete(@Body hashMap: HashMap<String, Any>): Observable<ResponseGetPostComment>

    @POST(update_profile_pic)
    fun changeProfilePic(@Body data: RequestBody): Observable<ResponseFollowUnfollow>

    @POST(update_profile_detail)
    fun updateProfile(@Body data: RequestBody): Observable<ResponseMyProfile>

    @POST(change_password)
    fun changePassword(@Body data: HashMap<String, Any>): Observable<CommonResponse>

    @POST(change_email)
    fun changeEmailId(@Body hashMap: HashMap<String, Any>): Observable<CommonResponse>

    @POST(check_verify_email)
    fun verifyEmailCode(@Body hashMap: HashMap<String, Any>): Observable<CommonResponse>

    @POST(delete_account)
    fun deleteAccount(@Body hashMap: HashMap<String, Any>): Observable<CommonResponse>

    @GET(exit_group)
    fun exitGroup(@Path("id") group_id: Int): Observable<ResponseExitReportGroup>

    @GET(delete_group)
    fun deleteGroup(@Path("id") group_id: Int): Observable<CommonResponse>

    @GET(report_group)
    fun reportGroup(@Path("id") group_id: Int): Observable<ResponseExitReportGroup>

    @POST(add_remove_admin)
    fun makeOrRemoveAdmin(@Body hashMap: HashMap<String, Any>): Observable<ResponseAddRemoveAdmin>

    @POST(add_participates)
    fun addOrRemoveUser(@Body hashMap: HashMap<String, Any>): Observable<ResponseAddRemoveAdmin>

    @POST(create_broadcast)
    fun createBroadcast(@Body data: RequestBody): Observable<CreateBroadcastResponse>

    //    @GET(create_broadcast)
    //    Call<Get_Broadcast> get_Broadcast(@Header("Authorization") String token);
    @DELETE(delete_broadcast)
    fun deleteBroadcast(@Path("id") broadcast_id: Int): Observable<CommonResponse>

    @GET(get_post_detail)
    fun get_post_detail(@Path("id") post_id: Int): Observable<ResponseGetAllPost>

    @POST(set_group_setting_url)
    fun setGroupSetting(@Body hashMap: HashMap<String, Any>): Observable<ResponseGroupSetting>

    @POST(send_chat_message)
    fun sendChatMessage(@Body data: RequestBody): Observable<ResponseChatMessage>

    @GET(updated_all_group)
    fun getUpdatedAllGroup(): Observable<ResponseGroupData>

    @GET(post_data_count)
    fun postDataCount(@Path("id") postId: String): Observable<ResponsePostLikeDataCount>

    @POST(post_comment_onoff)
    fun postCommentOnOff(@Body hashMap: HashMap<String, Any>): Observable<CommonResponse>

    @DELETE(post_delete)
    fun postDelete(@Path("id") postId: String): Observable<CommonResponse>

    @POST(search_contacts)
    fun searchAppUser(@Body hashMap: HashMap<String, Any>): Observable<ResponseIsAppUser>

    @POST(contact_sync)
    fun contactSync(@Body contactSyncAPIModel: List<ContactSyncAPIModel>): Observable<ResponseMyContact>

    @POST(search_sync_contact)
    fun searchContactSync(@Body hashMap: HashMap<String, Any>): Observable<ResponseIsAppUser>

    @POST(getSettings)
    fun getSettings(@Body hashMap: HashMap<String, Any>): Observable<ResponseGetSetting>

}