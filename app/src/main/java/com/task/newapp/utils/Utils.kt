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
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.task.newapp.R
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

