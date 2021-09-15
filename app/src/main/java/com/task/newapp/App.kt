package com.task.newapp

import android.content.Context
import android.graphics.Typeface
import android.net.TrafficStats
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import androidx.multidex.MultiDexApplication
import com.appizona.yehiahd.fastsave.FastSave
import com.danikula.videocache.HttpProxyCacheServer
import com.github.nkzawa.socketio.client.Socket
import com.luck.picture.lib.app.IApp
import com.luck.picture.lib.app.PictureAppMaster
import com.luck.picture.lib.crash.PictureSelectorCrashUtils
import com.luck.picture.lib.engine.PictureSelectorEngine
import com.task.newapp.utils.GlideImageLoader
import com.task.newapp.utils.instapicker.PictureSelectorEngineImp
import com.task.newapp.utils.joinSocket
import com.testfairy.TestFairy
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.ios.IosEmojiProvider
import iknow.android.utils.BaseUtils
import io.realm.Realm
import io.realm.RealmConfiguration
import lv.chi.photopicker.ChiliPhotoPicker


class App : MultiDexApplication(), IApp, CameraXConfig.Provider {

    companion object {

        @JvmField
        var appInstance: App? = null

        @JvmField
        var realm: Realm? = null

        @JvmField
        var socket: Socket? = null

        lateinit var fastSave: FastSave

        @JvmStatic
        fun getAppInstance(): App {
            return appInstance as App
        }

        @JvmStatic
        fun getRealmInstance(): Realm {
            return realm as Realm
        }

        @JvmStatic
        fun getSocketInstance(): Socket {
            return socket as Socket
        }

        fun setFont(context: Context, str: String?): Typeface? {
            return Typeface.createFromAsset(context.assets, str)
        }

        @JvmStatic
        fun getProxy(context: Context): HttpProxyCacheServer? {
            val app = context.applicationContext as App
            return if (app.proxy == null) newProxy().also { app.proxy = it } else app.proxy
        }

        private fun newProxy(): HttpProxyCacheServer {
            return HttpProxyCacheServer.Builder(appInstance?.appContext)
                .maxCacheSize((1024 * 1024).toLong())
                .build()
        }
    }

    val TAG = App::class.java.simpleName

    override fun onCreate() {
        super.onCreate()
        appInstance = this
        TrafficStats.setThreadStatsTag(1000)

        BaseUtils.init(this)
        //testfairy initialization
        TestFairy.begin(this, "SDK-ZhTY4p68")
        //FastSave pref lib initialization
        FastSave.init(this)
        fastSave = FastSave.getInstance()
        //Realm DB initialization
        Realm.init(this)
       /* thread {

            try {
                TrueTime.build().initialize();
            } catch (e: Exception) {
                showLog(TAG, e.message.toString())
            }
        }*/
        setRealmConfig()
        EmojiManager.install(IosEmojiProvider())
        //ImagePicker lib initialization
        ChiliPhotoPicker.init(
            loader = GlideImageLoader(),
            authority = "$packageName.fileprovider"
        )
        joinSocket()
        setupStrictMode()

        //Add New
        /** PictureSelector日志管理配制开始 **/
        // PictureSelector 绑定监听用户获取全局上下文或其他...
        /** PictureSelector日志管理配制开始  */
        // PictureSelector 绑定监听用户获取全局上下文或其他...
        PictureAppMaster.getInstance().app = this
        // PictureSelector Crash日志监听
        // PictureSelector Crash日志监听
        PictureSelectorCrashUtils.init { t: Thread?, e: Throwable? -> }
        /** PictureSelector日志管理配制结束 **/
    }


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

    override fun getAppContext(): Context? {
        return this
    }

    override fun getPictureSelectorEngine(): PictureSelectorEngine? {
        return PictureSelectorEngineImp()
    }

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }

    private var proxy: HttpProxyCacheServer? = null


}