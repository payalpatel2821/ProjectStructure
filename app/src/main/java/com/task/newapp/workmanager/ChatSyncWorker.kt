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
import com.task.newapp.utils.Constants.Companion.MessageType
import com.task.newapp.utils.Constants.Companion.MessageType.LABEL
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
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
        ContextCompat.getMainExecutor(applicationContext).execute(
            Runnable {
                showLog(TAG, "After Delay Run work manager")
                syncOneToOneChatData()
            })
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
            callAPISendChatMessage() { isSuccess ->
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
            //openProgressDialog(activity)
            val hashMap: HashMap<String, Any> = hashMapOf(
                Constants.local_id to chatList.localChatId,
                Constants.message_text to chatList.messageText!!,
                Constants.is_secret to 0,
                Constants.type to MessageType.TEXT.type
            )

            hashMap[Constants.receiver_id] = chatList.receiverId
            showLog(TAG, hashMap.toString())

            //------------------------Call API-------------------------

            mCompositeDisposable.add(
                ApiClient.create()
                    .sendChatMessage(hashMap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseChatMessage>() {
                        override fun onNext(responseChatMessage: ResponseChatMessage) {
                            hideProgressDialog()

                            if (responseChatMessage.success == 1) {


                                updateChatList(responseChatMessage) { success ->
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
                            hideProgressDialog()
                            callback?.invoke(false)
                        }

                        override fun onComplete() {
                            hideProgressDialog()
                            callback?.invoke(false)
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun updateChatList(responseChatMessage: ResponseChatMessage, callback: ((Boolean) -> Unit)? = null) {

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
                    updateChatListIdData(chatModel.localId, chatModel, true) {

                    }
                }
                else -> {
                    var chatContent: ChatContents? = null
                    chatModel.chatContents?.let {
                        chatContent = ChatContents()
                        createChatContent(chatModel.chatContents)?.let {
                            chatContent = it
                            updateChatContent(chatModel.localId, it)
                        }
                    }

                    chatModel.contacts?.let { contactsList ->
                        contactsList.forEach { contact ->
                            createChatContent(contact)?.let {
                                chatContent = it
                                updateChatContent(chatModel.localId, it)
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

        sendOneToOneMessageEmitEvent(responseChatMessage)
        callback?.invoke(true)
    }

}

