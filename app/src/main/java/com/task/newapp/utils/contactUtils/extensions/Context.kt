package com.task.newapp.utils.contactUtils.extensions

//import com.github.ajalt.reprint.core.Reprint
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.task.newapp.utils.ContactsConstants
import com.task.newapp.utils.contactUtils.helpers.*
import com.task.newapp.utils.showToast

fun Context.getSharedPrefs() = getSharedPreferences(ContactsConstants.PREFS_KEY, Context.MODE_PRIVATE)


fun Context.toast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    try {
        if (ContactsConstants.isOnMainThread()) {
            doToast(this, msg, length)
        } else {
            Handler(Looper.getMainLooper()).post {
                doToast(this, msg, length)
            }
        }
    } catch (e: Exception) {
    }
}

private fun doToast(context: Context, message: String, length: Int) {
    if (context is Activity) {
        if (!context.isFinishing && !context.isDestroyed) {
            Toast.makeText(context, message, length).show()
        }
    } else {
        Toast.makeText(context, message, length).show()
    }
}
val Context.baseConfig: BaseConfig get() = BaseConfig.newInstance(this)

val Context.otgPath: String get() = baseConfig.OTGPath

fun Context.hasPermission(permId: Int) = ContextCompat.checkSelfPermission(this, getPermissionString(permId)) == PackageManager.PERMISSION_GRANTED

fun Context.getPermissionString(id: Int) = when (id) {
   /* ContactsConstants.PERMISSION_READ_STORAGE -> Manifest.permission.READ_EXTERNAL_STORAGE
    ContactsConstants.PERMISSION_WRITE_STORAGE -> Manifest.permission.WRITE_EXTERNAL_STORAGE
    ContactsConstants.PERMISSION_CAMERA -> Manifest.permission.CAMERA
    ContactsConstants.PERMISSION_RECORD_AUDIO -> Manifest.permission.RECORD_AUDIO*/
    ContactsConstants.PERMISSION_WRITE_CONTACTS -> Manifest.permission.WRITE_CONTACTS
    ContactsConstants.PERMISSION_READ_CONTACTS -> Manifest.permission.READ_CONTACTS
    /*ContactsConstants.PERMISSION_READ_CALENDAR -> Manifest.permission.READ_CALENDAR
    ContactsConstants.PERMISSION_WRITE_CALENDAR -> Manifest.permission.WRITE_CALENDAR
    ContactsConstants.PERMISSION_CALL_PHONE -> Manifest.permission.CALL_PHONE
    ContactsConstants.PERMISSION_READ_CALL_LOG -> Manifest.permission.READ_CALL_LOG
    ContactsConstants.PERMISSION_WRITE_CALL_LOG -> Manifest.permission.WRITE_CALL_LOG
    ContactsConstants.PERMISSION_GET_ACCOUNTS -> Manifest.permission.GET_ACCOUNTS
    ContactsConstants.PERMISSION_READ_SMS -> Manifest.permission.READ_SMS
    ContactsConstants.PERMISSION_SEND_SMS -> Manifest.permission.SEND_SMS
    ContactsConstants.PERMISSION_READ_PHONE_STATE -> Manifest.permission.READ_PHONE_STATE*/
    else -> ""
}

fun Context.queryCursor(
    uri: Uri,
    projection: Array<String>,
    selection: String? = null,
    selectionArgs: Array<String>? = null,
    sortOrder: String? = null,
    showErrors: Boolean = false,
    callback: (cursor: Cursor) -> Unit
) {
    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
        cursor?.use {
            if (cursor.moveToFirst()) {
                do {
                    callback(cursor)
                } while (cursor.moveToNext())
            }
        }
    } catch (e: Exception) {
        /*if (showErrors) {
            showErrorToast(e)
        }*/
        showToast(e.message!!)
    }
}

