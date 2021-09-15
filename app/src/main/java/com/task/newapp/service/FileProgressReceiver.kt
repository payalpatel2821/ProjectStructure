package com.task.newapp.service

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.task.newapp.R
import com.task.newapp.service.NotificationHelper
import com.task.newapp.service.FileProgressReceiver
import com.task.newapp.ui.activities.MainActivity
import java.util.*

class FileProgressReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "FileProgressReceiver"
        const val ACTION_CLEAR_NOTIFICATION = "com.wave.ACTION_CLEAR_NOTIFICATION"
        const val ACTION_PROGRESS_NOTIFICATION = "com.wave.ACTION_PROGRESS_NOTIFICATION"
        const val ACTION_UPLOADED = "com.wave.ACTION_UPLOADED"
        const val NOTIFICATION_ID = 1
    }

    var mNotificationHelper: NotificationHelper? = null
    var notification: NotificationCompat.Builder? = null

    override fun onReceive(mContext: Context, intent: Intent) {
        mNotificationHelper = NotificationHelper(mContext)
        // Get notification id
        val notificationId = intent.getIntExtra("notificationId", 1)
        // Receive progress
        val progress = intent.getIntExtra("progress", 0)
        when (Objects.requireNonNull(intent.action)) {
            ACTION_PROGRESS_NOTIFICATION -> {
                notification = mNotificationHelper!!.getNotification(
                    mContext.getString(R.string.uploading),
                    mContext.getString(R.string.in_progress), progress
                )
                mNotificationHelper!!.notify(NOTIFICATION_ID, notification)
            }
            ACTION_CLEAR_NOTIFICATION -> {
                mNotificationHelper!!.cancelNotification(notificationId)

//                val resultIntent = Intent(mContext, MainActivity::class.java)
//                val resultPendingIntent = PendingIntent.getActivity(
//                    mContext,
//                    0 /* Request code */, resultIntent,
//                    PendingIntent.FLAG_UPDATE_CURRENT
//                )
//                notification = mNotificationHelper!!.getNotification(
//                    mContext.getString(R.string.message_upload_success),
//                    mContext.getString(R.string.file_upload_successful), resultPendingIntent
//                )
//                mNotificationHelper!!.notify(NOTIFICATION_ID, notification)

            }
            ACTION_UPLOADED -> {
                val resultIntent = Intent(mContext, MainActivity::class.java)
                val resultPendingIntent = PendingIntent.getActivity(
                    mContext,
                    0 /* Request code */, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                notification = mNotificationHelper!!.getNotification(
                    mContext.getString(R.string.message_upload_success),
                    mContext.getString(R.string.file_upload_successful), resultPendingIntent
                )
                mNotificationHelper!!.notify(NOTIFICATION_ID, notification)
            }
            else -> {
            }
        }
    }
}