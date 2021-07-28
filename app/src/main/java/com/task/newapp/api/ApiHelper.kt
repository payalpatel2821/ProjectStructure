package com.task.newapp.api

//import okhttp3.RequestBody
import com.task.newapp.models.*
import com.task.newapp.models.post.ResponseGetAllPost
import com.task.newapp.models.post.ResponseGetPostLikeUnlike
import com.task.newapp.models.post.ResponsePostComment
import com.task.newapp.models.post.ResponsePostCommentDetails
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
    fun allpostcomment(@Body hashMap: HashMap<String, Any>): Observable<ResponsePostCommentDetails>

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
        @Part captionarray: List<MultipartBody.Part>,
        @Part typearray: List<MultipartBody.Part>,
        @Part thumbarray: List<MultipartBody.Part>,
        @Part imagevideoarray: List<MultipartBody.Part>
    ): Observable<CommonResponse>


    @Multipart
    @POST(add_post)
    fun addPostThought(
        @Part("turn_off_comment") turn_off_comment: MultipartBody.Part
    ): Observable<CommonResponse>

    @POST(set_profile_group_page_friend_post)
    fun getPageList(@Body hashMap: HashMap<String, Any>): Observable<ResponsePageList>

    @POST(set_friendsetting_url)
    fun setFriendSetting(@Body hashMap: HashMap<String, Any>): Observable<ResponseFriendSetting>

    @POST(set_block_unblock_report_url)
    fun setBlockUnblockReportSetting(@Body hashMap: HashMap<String, Any>): Observable<ResponseBlockReportUser>

    @POST(get_notification_tone)
    fun getNotificationTune(@Body hashMap: HashMap<String, Any>): Observable<ResponseNotification>

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
}