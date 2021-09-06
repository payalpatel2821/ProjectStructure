package com.task.newapp.workmanager

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.models.chat.ResponseChatMessage
import com.task.newapp.realmDB.*
import com.task.newapp.realmDB.models.ChatContents
import com.task.newapp.realmDB.models.ChatList
import com.task.newapp.realmDB.models.Chats
import com.task.newapp.utils.*
import com.task.newapp.utils.Constants.Companion.ChatContentType
import com.task.newapp.utils.Constants.Companion.ChatContentType.AUDIO
import com.task.newapp.utils.Constants.Companion.ChatContentType.CONTACT
import com.task.newapp.utils.Constants.Companion.ChatContentType.CURRENT
import com.task.newapp.utils.Constants.Companion.ChatContentType.DOCUMENT
import com.task.newapp.utils.Constants.Companion.ChatContentType.IMAGE
import com.task.newapp.utils.Constants.Companion.ChatContentType.LOCATION
import com.task.newapp.utils.Constants.Companion.ChatContentType.PDF
import com.task.newapp.utils.Constants.Companion.ChatContentType.VIDEO
import com.task.newapp.utils.Constants.Companion.ChatContentType.VOICE
import com.task.newapp.utils.Constants.Companion.ChatTypeFlag
import com.task.newapp.utils.Constants.Companion.MessageType
import com.task.newapp.utils.Constants.Companion.MessageType.LABEL
import com.task.newapp.utils.Constants.Companion.MessageType.MIX
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*
import kotlin.collections.ArrayList


class ChatSyncWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    private val mCompositeDisposable = CompositeDisposable()
    val TAG: String = ChatSyncWorker::class.java.simpleName

    private var chatListArray: ArrayList<ChatList> = ArrayList()
    var syncOneToOneChatCount = 0

    override suspend fun doWork(): Result {
        return try {
            try {
                showLog(TAG, "Run work manager")
                //Do Your task here
                // Thread.sleep(10000)

                startDataSync()
                Result.success()

            } catch (e: Exception) {
                showLog(TAG, "exception in doWork ${e.message}")
                Result.failure()
            }
        } catch (e: Exception) {
            showLog(TAG, "exception in doWork ${e.message}")
            Result.failure()
        }
    }

    @WorkerThread
    private fun startDataSync() {
        ContextCompat.getMainExecutor(applicationContext).execute {
            showLog(TAG, "After Delay Run work manager")
            syncOneToOneChatData()
        }
    }

    private fun syncOneToOneChatData() {
        chatListArray.clear()
        val chatArray = ArrayList<ChatList>()
        chatArray.addAll(getAllOneToOneChatListForSync(false))
        if (chatArray.isNotEmpty()) {
            chatListArray.addAll(chatArray)
            chatMessageSyncTask()
        }
    }


    private fun chatMessageSyncTask() {


        // This is where your UI code goes.
        if (syncOneToOneChatCount < chatListArray.size) {
            showLog(TAG, "Total Sync count : ${chatListArray.size}")
            callAPISendChatMessage { isSuccess ->
                if (isSuccess) {
                    syncOneToOneChatCount += 1
                    showLog(TAG, "syncOneToOneChatCount : $syncOneToOneChatCount")
                    chatMessageSyncTask()
                } else {
                    syncOneToOneChatCount = 0
                    syncOneToOneChatData()
                }
            }
        } else {
            syncOneToOneChatCount = 0
            WorkManagerScheduler.cancel(applicationContext)
        }


    }

    private fun callAPISendChatMessage(callback: ((Boolean) -> Unit)? = null) {

        val chatList: ChatList = chatListArray[syncOneToOneChatCount]
        if (chatList.isSync) {
            return
        }
        try {
            if (!applicationContext.isNetworkConnected()) {
                applicationContext.showToast(applicationContext.getString(R.string.no_internet))
                return
            }

            val parameters = MultipartBody.Builder().setType(MultipartBody.FORM)
            parameters.addFormDataPart(Constants.local_id, chatList.localChatId.toString())
                .addFormDataPart(Constants.message_text, chatList.messageText ?: "")
                .addFormDataPart(Constants.is_secret, "0")
                .addFormDataPart(Constants.type, chatList.type ?: "")


            var flag = ChatTypeFlag.PRIVATE

            when {
                chatList.isGroupChat == 1 -> {
                    flag = ChatTypeFlag.GROUPS

                    parameters.addFormDataPart(Constants.group_id, chatList.groupId.toString()).addFormDataPart(Constants.is_group_chat, "1")
                }
                chatList.isBroadcastChat == 1 -> {
                    flag = ChatTypeFlag.BROADCAST
                    if (chatList.receiverId != 0) {

                        parameters.addFormDataPart(Constants.receiver_id, chatList.receiverId.toString())
                            .addFormDataPart(Constants.broadcast_chat_id, chatList.broadcastChatId.toString())
                    }

                    parameters.addFormDataPart(Constants.broadcast_id, chatList.broadcastId.toString())
                        .addFormDataPart(Constants.is_broadcast_chat, "1")
                }
                chatList.isSecret == 1 -> {
                    flag = ChatTypeFlag.SECRET
                }
                else -> {
                    flag = ChatTypeFlag.PRIVATE
                    parameters.addFormDataPart(Constants.receiver_id, chatList.receiverId.toString())
                }


            }
            if (chatList.isReply == 1) {

                parameters.addFormDataPart(Constants.is_reply, chatList.isReply.toString())
                    .addFormDataPart(Constants.chat_id, chatList.chatId.toString())

            }

            if (MessageType.getMessageTypeFromText(chatList.type ?: "") == MIX) {

                chatList.chatContents?.let { chatContent ->
                    when (ChatContentType.getChatContentTypeFromText(chatContent.type)) {
                        IMAGE, VIDEO -> {
                            val captionKey = "${Constants.contents}[${Constants.caption}]"
                            val typeKey = "${Constants.contents}[${Constants.type}]"
                            val thumbKey = "${Constants.contents}[${Constants.thumb}]"
                            val fileKey = "${Constants.contents}[${Constants.file}]"
                            val titleKey = "${Constants.contents}[${Constants.title}]"

                            val requestBody: RequestBody = RequestBody.create(chatContent.type.toMediaTypeOrNull(), chatContent.data!!)
                            parameters.addFormDataPart(captionKey, chatContent.caption ?: "")
                                .addFormDataPart(fileKey, chatContent.title, requestBody)
                                .addFormDataPart(titleKey, chatContent.title)
                                .addFormDataPart(typeKey, chatContent.type)


                        }
                        AUDIO, VOICE -> {
                            val typeKey = "${Constants.contents}[${Constants.type}]"
                            val fileKey = "${Constants.contents}[${Constants.file}]"
                            val durationKey = "${Constants.contents}[${Constants.duration}]"
                            val titleKey = "${Constants.contents}[${Constants.title}]"

                            val requestBody: RequestBody = RequestBody.create(chatContent.type.toMediaTypeOrNull(), chatContent.data!!)
                            parameters.addFormDataPart(durationKey, chatContent.duration.toString())
                                .addFormDataPart(fileKey, chatContent.title, requestBody)
                                .addFormDataPart(titleKey, chatContent.title)
                                .addFormDataPart(typeKey, chatContent.type)
                        }
                        CONTACT -> {
                        }
                        CURRENT -> {
                        }
                        PDF -> {
                        }
                        DOCUMENT -> {
                        }
                        LOCATION -> {
                        }
                    }
                }
            } else if (MessageType.getMessageTypeFromText(chatList.type ?: "") == MessageType.CONTACT) {
            }
            val requestBody: RequestBody = parameters.build()
            showLog(TAG, parameters.toString())

            //------------------------Call API-------------------------
            mCompositeDisposable.add(
                ApiClient.create()
                    .sendChatMessage(requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseChatMessage>() {
                        override fun onNext(responseChatMessage: ResponseChatMessage) {

                            if (responseChatMessage.success == 1) {


                                updateChatList(chatList.id, responseChatMessage) { success ->
                                    if (success)
                                        callback?.invoke(true)
                                    else
                                        callback?.invoke(false)
                                }
                            } else
                                callback?.invoke(false)
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                            callback?.invoke(false)
                        }

                        override fun onComplete() {
                            callback?.invoke(false)
                        }
                    })
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun updateChatList(localId: Long, responseChatMessage: ResponseChatMessage, callback: ((Boolean) -> Unit)? = null) {

        responseChatMessage.createRequest?.let { friendRequestModel ->
            insertFriendRequestData(createFriendRequest(friendRequestModel))
        }
        responseChatMessage.sender?.let { sender ->
            insertUserData(prepareOtherUserData(sender)) { users ->
                showLog("CHATS : ", users.toString())
                updateChatListAndUserData(sender.id, users)
            }
        }

        responseChatMessage.receiver?.let { receiver ->
            insertUserData(prepareOtherUserData(receiver)) { users ->
                showLog("CHATS : ", users.toString())
                updateChatListAndUserData(receiver.id, users)
            }
        }

        responseChatMessage.data?.let { chatModel ->

            when (MessageType.getMessageTypeFromText(chatModel.type)) {
                LABEL -> {
                    updateChatListIdData(localId, chatModel, true)
                }
                else -> {
                    var chatContent: ChatContents? = null
                    chatModel.chatContents?.let {
                        chatContent = ChatContents()
                        createChatContent(chatModel.chatContents)?.let {
                            chatContent = it
                            updateChatContent(localId, it)
                        }
                    }

                    chatModel.contacts?.let { contactsList ->
                        contactsList.forEach { contact ->
                            createChatContent(contact)?.let {
                                chatContent = it
                                updateChatContent(localId, it)
                            }
                        }
                    }

                    updateChatListIdData(chatModel.localId, chatModel, true) { chatList ->
                        if (chatList.isGroupChat == 1) {
                            getSingleGroupDetails(chatList.groupId)?.let { groupInfo ->
                                val otherUsersId = groupInfo.otherUserId?.split(",")
                                otherUsersId?.forEach { ID ->
                                    if (getCurrentUserId() == ID.toInt()) {
                                        return@forEach
                                    }
                                    insertGroupTickData(createGroupTickManageData(chatList, ID.toInt()))
                                }
                            }
                        }

                        updateChatsList(if (chatList.isGroupChat == 1) responseChatMessage.data.groupId else responseChatMessage.data.receiverId, chatList, object : OnRealmTransactionResult {
                            override fun onSuccess(obj: Any) {
                                val data = obj as Chats
                                showLog(TAG, "Is Synced" + data.chatList?.isSync)
                            }
                        })
                    }
                }
            }
        }
        responseChatMessage.data?.let { objectData ->
            if (objectData.isGroupChat == 1) {

            } else if (objectData.isBroadcastChat == 1) {
                if (objectData.otherUserId.isNullOrBlank()) {

                } else {
                    if (objectData.otherUserId.isNotEmpty()) {

                    }
                }
            } else
                sendOneToOneMessageEmitEvent(responseChatMessage)

        }
        callback?.invoke(true)
    }

}

