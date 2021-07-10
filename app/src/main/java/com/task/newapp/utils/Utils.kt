package com.task.newapp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.task.newapp.R
import com.task.newapp.realmDB.getUserByUserId
import eightbitlab.com.blurview.BlurView


/**
 * check network
 */
fun Context.isNetworkConnected(): Boolean {
    var result = false
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNetwork =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            connectivityManager.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }

            }
        }
    }

    return result
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


/**
 * Show Log
 */
@SuppressLint("LogNotTimber")
fun showLog(name: String, value: String) {
    Log.e(name, value)
}

/**
 * show keyboard
 *
 * @param mContext
 * @param view
 */
fun showSoftKeyboard(mContext: Context, view: View?) {
    try {
        val imm = mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * hide keyboard if visible
 *
 * @param mActivity
 * @param view
 */
fun hideSoftKeyboard(mActivity: Activity, view: View) {
    // Set up touch listener for non-text box views to hide keyboard.
    if (view !is EditText) {
        view.setOnTouchListener { v, event ->
            hideSoftKeyboard(mActivity)
            false
        }
    }

    //If a layout container, iterate over children and seed recursion.
    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            val innerView = view.getChildAt(i)
            hideSoftKeyboard(mActivity, innerView)
        }
    }
}

fun Context.getDisplayMatrix(): DisplayMetrics {
    return resources.displayMetrics
}

/**
 * hide keyboard if visible
 *
 * @param mActivity
 */
fun hideSoftKeyboard(mActivity: Activity) {
    try {
        val imm = mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        // Find the currently focused view, so we can grab the correct window token from it.
        var view = mActivity.currentFocus
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(mActivity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    } catch (e: Exception) {
        e.printStackTrace()
    }
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
/**
open dialog
 */
var dialog: Dialog? = null
fun openProgressDialog(activity: Activity?) {
//    hideProgressDialog()

    try {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }

        dialog = Dialog(activity!!)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setContentView(R.layout.progress_layout)
        dialog!!.setCanceledOnTouchOutside(true)
        //Glide.with(activity!!).load(R.drawable.loader).into(dialog!!.progress)
        if (dialog != null && !dialog!!.isShowing) {
            dialog!!.show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Dismiss Dialog
 */
fun hideProgressDialog() {
    try {
        if (dialog != null) {
            dialog!!.hide()
            dialog!!.dismiss()
            dialog = null
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

}

fun enableOrDisableButton(context: Context, isEnable: Boolean, button: Button) {
    if (isEnable) {
        button.background =
            ContextCompat.getDrawable(context, R.drawable.btn_rect_rounded_bg)
        button.isEnabled = true
    } else {
        button.background =
            ContextCompat.getDrawable(context, R.drawable.btn_rect_rounded_bg_disable)
        button.isEnabled = false
    }
}

fun requestFocus(context: Context, view: View) {
    if (view.requestFocus()) {
        // open the soft keyboard
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun setBlurLayout(activity: Activity?, root: ViewGroup, blurView: BlurView) {
    //set background, if your root layout doesn't have one
    val windowBackground = activity!!.window.decorView.background

    blurView.setupWith(root)
        .setFrameClearDrawable(windowBackground)
        .setBlurRadius(55F).setBlurAutoUpdate(true).setBlurEnabled(true)
        .setHasFixedTransformationMatrix(true)
}

fun getGroupLabelText(userId: Int, event: String, isCurrentUser: Boolean, messageText: String): String {
    val user = getUserByUserId(userId)
    val message = ""
    if (user != null) {
        if (Constants.Companion.MessageEvents.getMessageEventFromName(event) == Constants.Companion.MessageEvents.CREATE) {
            return if (isCurrentUser)
                "You $messageText"
            else
                "${user.first_name} ${user.last_name} $messageText"

        } else if (Constants.Companion.MessageEvents.getMessageEventFromName(event) == Constants.Companion.MessageEvents.ADD_USER) {
            return "${user.first_name} ${user.last_name} added you"
        }
    }
    return message
}

fun getFileNameFromURLString(URLString: String): String {
    return URLString.substring(URLString.lastIndexOf('/') + 1);

}

fun convertDurationStringToSeconds(duration: String): String {
    var duration = "0"
    val arrDuration = duration?.split(":")
    var totalSec = 0
    if (arrDuration.isNotEmpty()) {
        val min = arrDuration[0].toInt()
        val sec = arrDuration[1].toInt()
        totalSec = (min * 60) + sec
        return totalSec.toString()
    } else {
        return duration
    }
}

/**
 * Loads image with Glide into the [ImageView].
 *
 * @param url url to load
 * @param previousUrl url that already loaded in this target. Needed to prevent white flickering.
 * @param round if set, the image will be round.
 * @param cornersRadius the corner radius to set. Only used if [round] is `false`(by default).
 * @param crop if set to `true` then [CenterCrop] will be used. Default is `false` so [FitCenter] is used.
 */
@SuppressLint("CheckResult")
fun ImageView.load(
    url: String,
    previousUrl: String? = null,
    round: Boolean = false,
    cornersRadius: Int = 0,
    crop: Boolean = false
) {

    val requestOptions = when {
        round -> RequestOptions.circleCropTransform()

        cornersRadius > 0 -> {
            RequestOptions().transforms(
                if (crop) CenterCrop() else FitCenter(),
                RoundedCorners(cornersRadius)
            )
        }

        else -> null
    }

    Glide
        .with(context)
        .load(url)
        .let {
            // Apply request options
            if (requestOptions != null) {
                it.apply(requestOptions)
            } else {
                it
            }
        }
        .let {
            // Workaround for the white flickering.
            // See https://github.com/bumptech/glide/issues/527
            // Thumbnail changes must be the same to catch the memory cache.
            if (previousUrl != null) {
                it.thumbnail(
                    Glide
                        .with(context)
                        .load(previousUrl)
                        .let {
                            // Apply request options
                            if (requestOptions != null) {
                                it.apply(requestOptions)
                            } else {
                                it
                            }
                        }
                )
            } else {
                it
            }
        }
        .into(this)
}
