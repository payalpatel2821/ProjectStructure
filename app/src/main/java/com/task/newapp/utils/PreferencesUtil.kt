package com.task.newapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.task.newapp.R



fun Context.getSharedPreferences(): SharedPreferences {
    return getSharedPreferences(
        getString(R.string.app_name).replace(" ", "_"),
        Context.MODE_PRIVATE
    )
}

//fun Context.setLoginToken(data: String) {
//    val editor = getSharedPreferences().edit()
//    editor.putString(LoginToken, data)
//    editor.apply()
//}
//
//fun Context.getLoginToken(): String {
//    return getSharedPreferences().getString(LoginToken, "")!!
//}
//
//fun Context.setLoginId(data: Int) {
//    val editor = getSharedPreferences().edit()
//    editor.putInt(LoginId, data)
//    editor.apply()
//}
//
//fun Context.getLoginId(): Int {
//    return getSharedPreferences().getInt(LoginId, 0)!!
//}
//
//fun Context.setLatitude(data: Float) {
//    val editor = getSharedPreferences().edit()
//    editor.putFloat(Latitude, data)
//    editor.apply()
//}
//
//fun Context.getLatitude(): Float {
//    return getSharedPreferences().getFloat(Latitude, 0f)!!
//}
//
//fun Context.setLongitude(data: Float) {
//    val editor = getSharedPreferences().edit()
//    editor.putFloat(Longitude, data)
//    editor.apply()
//}
//
//fun Context.getLongitude(): Float {
//    return getSharedPreferences().getFloat(Longitude, 0f)!!
//}
//
//fun Context.clearData() {
//    val editor = getSharedPreferences().edit()
//    editor.clear() //clear all stored data
//    editor.apply()
//}
//
///**
// * Robot Captcha
// */
//fun Context.setRobot(data: Boolean) {
//    val editor = getSharedPreferences().edit()
//    editor.putBoolean(Robot, data)
//    editor.apply()
//}
//
//fun Context.getRobot(): Boolean {
//    return getSharedPreferences().getBoolean(Robot, false)!!
//}
//
///**
// * isLogin
// */
//fun Context.setIsLogin(data: Boolean) {
//    val editor = getSharedPreferences().edit()
//    editor.putBoolean(IsLogin, data)
//    editor.apply()
//}
//
//fun Context.getIsLogin(): Boolean {
//    return getSharedPreferences().getBoolean(IsLogin, false)!!
//}
//
///**
// * FirstName
// */
//fun Context.setFirstName(name: String) {
//    val editor = getSharedPreferences().edit()
//    editor.putString(FirstName, name)
//    editor.apply()
//}
//
//fun Context.getFirstName(): String {
//    return getSharedPreferences().getString(FirstName, "")!!
//}
//
///**
// * LastName
// */
//fun Context.setLastName(name: String) {
//    val editor = getSharedPreferences().edit()
//    editor.putString(LastName, name)
//    editor.apply()
//}
//
//fun Context.getLastName(): String {
//    return getSharedPreferences().getString(LastName, "")!!
//}
//
///**
// * Email
// */
//fun Context.setEmail(name: String) {
//    val editor = getSharedPreferences().edit()
//    editor.putString(Email, name)
//    editor.apply()
//}
//
//fun Context.getEmail(): String {
//    return getSharedPreferences().getString(Email, "")!!
//}
//
///**
// * Profile Image
// */
//fun Context.setProfileImage(name: String) {
//    val editor = getSharedPreferences().edit()
//    editor.putString(ProfileImage, name)
//    editor.apply()
//}
//
//fun Context.getProfileImage(): String {
//    return getSharedPreferences().getString(ProfileImage, "")!!
//}
//
///**
// * Authorization Token
// */
//fun Context.setDeviceToken(data: String) {
//    val editor = getSharedPreferences().edit()
//    editor.putString(DeviceToken, data)
//    editor.apply()
//}
//
//fun Context.getDeviceToken(): String {
//    return getSharedPreferences().getString(DeviceToken, "")!!
//}
//
///**
// * Language
// */
//fun Context.setLanguage(data: String) {
//    val editor = getSharedPreferences().edit()
//    editor.putString(language, data)
//    editor.apply()
//}
//
//fun Context.getLanguage(): String {
//    return getSharedPreferences().getString(language, "")!!
//}

