package com.task.newapp.workmanager

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat
import androidx.work.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.task.newapp.App
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.models.CommonResponse
import com.task.newapp.models.ResponseMyContact
import com.task.newapp.models.contact.Contact
import com.task.newapp.models.contact.ContactSyncAPIModel
import com.task.newapp.realmDB.clearMyContact
import com.task.newapp.realmDB.insertMyContactData
import com.task.newapp.realmDB.models.ChatList
import com.task.newapp.realmDB.prepareMyContactData
import com.task.newapp.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.lang.reflect.Type
import java.util.concurrent.Executor


class ContactSyncWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    private val mCompositeDisposable = CompositeDisposable()
    val TAG: String = ContactSyncWorker::class.java.simpleName
    var context = appContext
    private var chatListArray: ArrayList<ChatList> = ArrayList()
    var syncOneToOneChatCount = 0

//    override suspend fun doWork(): Result {
//        return try {
//            try {
//                showLog(TAG, "Run work manager")
//                //Do Your task here
//                // Thread.sleep(10000)
//                var callSucceed = false
//                startDataSync() { isSuccess ->
//                    if (isSuccess) {
//                        showLog(TAG, "API Call success")
//                        Result.success()
//                    } else {
//                        showLog(TAG, "API Call Failure")
//                        Result.failure()
//                    }
////                    callSucceed = isSuccess
//                }
////                showLog(TAG, "Work manager success")
////                val output: Data = workDataOf(Constants.callSucceed to callSucceed)
//                Result.success(/*output*/)
//            } catch (e: Exception) {
//                showLog(TAG, "exception in doWork ${e.message}")
//                Result.failure()
//            }
//        } catch (e: Exception) {
//            showLog(TAG, "exception in doWork ${e.message}")
//            Result.failure()
//        }
//    }


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
    private fun startDataSync(callback: ((Boolean) -> Unit)? = null) {
        ContextCompat.getMainExecutor(applicationContext).execute {
            showLog(TAG, "After Delay Run work manager")
            val gson = Gson()
            val contactList = App.fastSave.getString(Constants.contact, "")
            val type: Type = object : TypeToken<List<Contact?>?>() {}.type
            val contacts: ArrayList<ContactSyncAPIModel> = gson.fromJson(contactList, type)
            callAPISyncContactToAPI(contacts) { isSuccess ->
                callback?.invoke(isSuccess)
            }
        }
    }


    private fun callAPISyncContactToAPI(contactSync: java.util.ArrayList<ContactSyncAPIModel>, callback: ((Boolean) -> Unit)? = null) {
        if (!applicationContext.isNetworkConnected()) {
            applicationContext.showToast(applicationContext.getString(R.string.no_internet))
            return
        }

        try {
            mCompositeDisposable.add(
                ApiClient.create()
                    .contactSync(contactSync)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<ResponseMyContact>() {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onNext(response: ResponseMyContact) {
                            showLog("contact sync", "success")
                            if (response.success == 0) {
                                callback?.invoke(false)
                                applicationContext.showToast(response.message)
                            } else {
                                clearMyContact() { isSuccess ->
                                    if (isSuccess) {
                                        insertMyContactData(prepareMyContactData(response.data))
                                        context.sendBroadcast(Intent(Constants.INTENT_CONTACT_COMPLETE))
                                        callback?.invoke(true)
                                    }
                                }
                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                            hideProgressDialog()
                            callback?.invoke(false)
                        }

                        override fun onComplete() {
                            hideProgressDialog()
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}

