package com.task.newapp

import android.net.TrafficStats
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.appizona.yehiahd.fastsave.FastSave
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.task.newapp.models.Send_Userdetail_Soket
import com.task.newapp.models.User
import com.task.newapp.utils.Constants
import com.task.newapp.utils.GlideImageLoader
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.ios.IosEmojiProvider
import io.realm.Realm
import io.realm.RealmConfiguration
import lv.chi.photopicker.ChiliPhotoPicker
import java.net.URI


class App : MultiDexApplication() {

    companion object {

        @JvmField
        var appInstance: App? = null

        @JvmField
        var realm: Realm? = null

        lateinit var fastSave: FastSave
        @JvmStatic
        fun getAppInstance(): App {
            return appInstance as App
        }

        @JvmStatic
        fun getRealmInstance(): Realm {
            return realm as Realm

        }

        var socket: Socket? = null
    }

    val TAG = App::class.java.simpleName

    override fun onCreate() {
        super.onCreate()
        appInstance = this
        TrafficStats.setThreadStatsTag(1000)

        //FastSave pref lib initialization
        FastSave.init(this)
        fastSave = FastSave.getInstance()
        //Realm DB initialization
        Realm.init(this)
        setRealmConfig()
        EmojiManager.install(IosEmojiProvider())
        //ImagePicker lib initialization
        ChiliPhotoPicker.init(
            loader = GlideImageLoader(),
            authority = "$packageName.fileprovider"
        )

        joinSocket()
        setupStrictMode()
    }

    fun joinSocket() {
        try {
            if (socket == null) {
                socket = IO.socket(URI.create(BuildConfig.port_url))
                socket!!.connect()
            } else if (!socket!!.connected()) {
                socket!!.connect()
            }
            var prefUser = FastSave.getInstance().getObject(Constants.prefUser, User::class.java)
            Log.e("joinSocket : ", prefUser.id.toString())

            if (prefUser != null) {
                val send_userdetail_soket = Send_Userdetail_Soket(
                    prefUser.id,
                    prefUser.uName
                )
                val joinvalue = Gson().toJson(send_userdetail_soket)
                socket!!.emit(Constants.join, joinvalue)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    fun disconnectSocket(user_id: Int, user_name: String?) {
//        Log.e(, "disconnectSocket :", user_id.toString())
//        val send_userdetail_soket = Send_Userdetail_Soket(user_id, user_name)
//        val joinvalue = Gson().toJson(send_userdetail_soket)
//        socket!!.emit(Constants.disconnect, joinvalue)
//        socket!!.disconnect()
//        App.mSocket.close()
//    }

    private fun setupStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(ThreadPolicy.Builder().detectAll().permitDiskWrites().permitDiskReads().penaltyLog().build())
            StrictMode.setVmPolicy(VmPolicy.Builder().detectAll().penaltyLog().build())
        }
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }

    private fun setRealmConfig() {
        val config = RealmConfiguration.Builder()
            .name("Database.realm")
            .allowWritesOnUiThread(true)
            .allowQueriesOnUiThread(true)
            .deleteRealmIfMigrationNeeded()
            .build()

        Realm.setDefaultConfiguration(config)
        realm = Realm.getDefaultInstance()
    }
}