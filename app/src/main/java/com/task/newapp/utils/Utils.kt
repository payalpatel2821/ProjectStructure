package com.task.newapp.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast


fun Context.isNetworkAvailable(): Boolean {
    var isNetwork = false
    val cm: ConnectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val builder: NetworkRequest.Builder = NetworkRequest.Builder()

    cm.registerNetworkCallback(
        builder.build(),
        object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                isNetwork = true
                return true
            }

            override fun onLost(network: Network) {
                isNetwork = false
            }
        }
    )
    return isNetwork
}

/**
 * Email Validation
 */
fun isValidEmail(target: CharSequence?): Boolean {
    return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
}

/**
 * Add Intent extras
 */
inline fun <reified T : Any> Context.launchActivity(
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivity(intent, options)
    } else {
        startActivity(intent)
    }
}

inline fun <reified T : Any> newIntent(context: Context): Intent =
    Intent(context, T::class.java)

/**
 * show toast
 */
fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

//    /**
//    open dialog
//     */
//    var dialog: Dialog? = null
//    fun openProgressDialog(activity: Activity?) {
////    hideProgressDialog()
//
//        try {
//            if (dialog != null) {
//                dialog!!.dismiss()
//                dialog = null
//            }
//
//            dialog = Dialog(activity!!)
//            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            dialog!!.setContentView(R.layout.progress_layout)
//            dialog!!.setCanceledOnTouchOutside(true)
//            Glide.with(activity!!).load(R.drawable.loader).into(dialog!!.progress)
//            if (dialog != null && !dialog!!.isShowing) {
//                dialog!!.show()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    /**
//     * Dismiss Dialog
//     */
//    fun hideProgressDialog() {
//        try {
//            if (dialog != null) {
//                dialog!!.hide()
//                dialog!!.dismiss()
//                dialog = null
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }
