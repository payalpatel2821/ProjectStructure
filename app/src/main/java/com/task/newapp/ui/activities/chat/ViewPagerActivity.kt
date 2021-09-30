package com.task.newapp.ui.activities.chat

import android.Manifest
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.task.newapp.R
import com.task.newapp.adapter.chat.Image_rv_adapter
import com.task.newapp.utils.exo_video.OnItemClickListener
import com.task.newapp.utils.exo_video.Sel_Media_RecyclerAdapter
import com.task.newapp.utils.exo_video.Sel_Media_RecyclerView
import com.task.newapp.utils.isImageFile
import lv.chi.photopicker.MediaPickerFragment
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class ViewPagerActivity : AppCompatActivity(), View.OnClickListener, OnItemClickListener, MediaPickerFragment.Callback {


    lateinit var image_rv_adapter: Image_rv_adapter
    lateinit var iv_delete: ImageView
    lateinit var iv_crop: ImageView
    lateinit var iv_edit: ImageView
    lateinit var imageSend: ImageView
    lateinit var caption_arr: ArrayList<String>
    lateinit var time_arr: ArrayList<String>
    lateinit var mRecyclerView: Sel_Media_RecyclerView

    var imageurilist: ArrayList<Uri?>? = ArrayList()
    var thumburilist: ArrayList<String?>? = ArrayList()
    var imagearray = ArrayList<String?>()
    var captionarr: List<String> = ArrayList()
    var timearr: List<String> = ArrayList()
    var uritype: ArrayList<Int>? = ArrayList()
    lateinit var receiver_name: TextView
    var EDIT_IMAGE = 1000
    lateinit var mAdapter: Sel_Media_RecyclerAdapter
    var compressprogress: ProgressBar? = null
    lateinit var progreelay: LinearLayout

    var abc = 0
    var finalOutputFile: String? = null
    private var HorizontalLayout: LinearLayoutManager? = null
    private val CAMERA_REQUEST = 5
    private fun initGlide(): RequestManager {
        val options = RequestOptions()
        return Glide.with(applicationContext)
            .setDefaultRequestOptions(options)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //  set status text dark
        window.statusBarColor = ContextCompat.getColor(this@ViewPagerActivity, R.color.black)
        iv_delete = findViewById(R.id.iv_delete)
        iv_crop = findViewById(R.id.iv_crop)
        imageSend = findViewById(R.id.imageSend)
        iv_edit = findViewById(R.id.iv_edit)
        compressprogress = findViewById(R.id.compressprogress)
        progreelay = findViewById(R.id.progreelay)
        progreelay.setOnClickListener(View.OnClickListener { })
        receiver_name = findViewById(R.id.receiver_name)
        trim_time = findViewById(R.id.trim_time)

        val ivBack = findViewById<ImageView>(R.id.iv_back)
        ivBack.setOnClickListener {
            onBackPressed()
        }
        intent.getStringArrayListExtra("select_urls")!!.forEach { uri -> imageurilist!!.add(Uri.parse(uri)) }
        caption_arr = ArrayList()
        time_arr = ArrayList()
        uritype = ArrayList()
        caption_arr = intent.getStringArrayListExtra("select_captions")!!
        time_arr = intent.getStringArrayListExtra("select_time")!!
        uritype = intent.getIntegerArrayListExtra("urls_mediatype")
        mRecyclerView = findViewById(R.id.exoPlayerRecyclerView)
        mRecyclerView.layoutManager = GridLayoutManager(this@ViewPagerActivity, 1, GridLayoutManager.HORIZONTAL, false)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(mRecyclerView)
        setAdapter()
        iv_delete.setOnClickListener(this)
        iv_crop.setOnClickListener(this)
        imageSend.setOnClickListener(this)
        iv_edit.setOnClickListener(this)
    }

    fun setAdapter() {
        mRecyclerView.setMediaObjects(imageurilist, uritype, caption_arr, time_arr)
        mAdapter = Sel_Media_RecyclerAdapter(this@ViewPagerActivity, imageurilist, uritype, caption_arr, time_arr, initGlide())
        mRecyclerView.adapter = mAdapter
        mAdapter.setClickListener(this)
        img_rv = findViewById(R.id.img_rv)
        HorizontalLayout = LinearLayoutManager(
            this@ViewPagerActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        img_rv.layoutManager = HorizontalLayout
        image_rv_adapter = Image_rv_adapter(this@ViewPagerActivity, imageurilist, uritype)
        img_rv.adapter = image_rv_adapter
    }

    override fun onClick(view: View?, position: Int) {

        runWithPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            MediaPickerFragment.newInstance(
                multiple = true,
                allowCamera = true,
                pickerType = MediaPickerFragment.PickerType.ANY,
                maxSelection = 30,
                theme = R.style.ChiliPhotoPicker_Light,
            ).show(supportFragmentManager, "picker")
        }

    }

    fun setTrimVideo(position: Int, videouri: String?) {
        imageurilist!![position] = Uri.parse(videouri)
        runOnUiThread { image_rv_adapter!!.notifyDataSetChanged() }
    }

    override fun onClick(view: View) {
        val current: Int = mRecyclerView.currentItem
        when (view.id) {
            R.id.iv_delete -> try {
                imageurilist!!.removeAt(current)
                caption_arr!!.removeAt(current)
                time_arr!!.removeAt(current)
                uritype!!.removeAt(current)
                if (imageurilist!!.size == 0) {
                    onBackPressed()
                } else {
                    Log.println(Log.ASSERT, "caption_arr-size--=--", caption_arr!!.size.toString() + "")
                    Log.println(Log.ASSERT, "imageurilist-size--=--", imageurilist!!.size.toString() + "")
                    Log.println(Log.ASSERT, "uritype-size--=--", uritype!!.size.toString() + "")
                    mAdapter.notifyItemRemoved(current)
                    image_rv_adapter!!.notifyItemRemoved(current)
                    if (mAdapter.itemCount == 0) {
                        finish()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            R.id.iv_crop -> {
            }
            R.id.imageSend -> {
                mRecyclerView.toggleVolume()
                imageSend!!.isClickable = false
                compressprogress!!.visibility = View.VISIBLE
                progreelay!!.visibility = View.VISIBLE
                addCompressFile(abc)
            }
            R.id.iv_edit -> {
            }

        }
    }

    private fun addCompressFile(i: Int) {
        val temp: String = if (uritype!![i] == 1) {
            "image"
        } else {
            "video"
        }
        CompressFilesTask(imageurilist!![i]!!.path!!, temp).execute()
    }

    fun compressImage(context: Context?, inputFile: String): String {
        val myfile = File(inputFile)
        val compressedImageFile: File
//        compressedImageFile = if (myfile.length() / 1024 > 25600) {
//            Log.e("image compress", "sccessfully")
//            Compressor.getDefault(this@ViewPagerActivity).compressToFile(File(inputFile))
//        } else {
//            Log.e("image compress", "ntott")
//            myfile
//        }

//        File compressedImageFile = Compressor.getDefault(ViewPagerActivity.this).compressToFile(new File(inputFile));
        imagearray.add(/*compressedImageFile.absolutePath*/inputFile)
        abc++
        if (abc < imageurilist!!.size) {
            addCompressFile(abc)
        } else {
            runOnUiThread {
                compressprogress!!.visibility = View.GONE
                progreelay!!.visibility = View.GONE
                // Stuff that updates the UI
            }
            val intent = Intent()
            intent.putStringArrayListExtra("select_urls", imagearray)
            intent.putStringArrayListExtra("select_captions", caption_arr)
            intent.putStringArrayListExtra("select_time", time_arr)
            intent.putIntegerArrayListExtra("urls_mediatype", uritype)
            setResult(RESULT_OK, intent)
            finish()
        }
        return /*compressedImageFile.absolutePath*/inputFile
    }

    fun compressvideo(context: Context?, inputFile: String?): String? {
        val myfile = File(inputFile)
        runOnUiThread {
            Log.e("video compress", "sccessfully")
            finalOutputFile = inputFile
            imagearray.add(inputFile)
            abc++
            if (abc < imageurilist!!.size) {
                addCompressFile(abc)
            } else {
                runOnUiThread {
                    compressprogress!!.visibility = View.GONE
                    progreelay!!.visibility = View.GONE
                }
                val intent = Intent()
                intent.putStringArrayListExtra("select_urls", imagearray)
                intent.putStringArrayListExtra("select_captions", caption_arr)
                intent.putStringArrayListExtra("select_time", time_arr)
                intent.putIntegerArrayListExtra("urls_mediatype", uritype)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
        return finalOutputFile
    }

    private fun handleEditorImage(data: Intent?) {
        var newFilePath = data!!.getStringExtra("OUTPUT_PATH")
        val isImageEdit = data.getBooleanExtra("IS_IMAGE_EDITED", false)
        if (isImageEdit) {
        } else {
            newFilePath = data.getStringExtra("SOURCE_PATH")
        }
        imageurilist!!.removeAt(mRecyclerView.getCurrentItem())
        imageurilist!!.add(mRecyclerView.getCurrentItem(), Uri.parse(newFilePath))
        mAdapter.notifyDataSetChanged()
        image_rv_adapter!!.notifyDataSetChanged()

        //----------------------Add New for scan file---------------------------
        MediaScannerConnection.scanFile(this, arrayOf(newFilePath), null) { path, uri ->
            Log.i("ExternalStorage", "Scanned $path:")
            Log.i("ExternalStorage", "-> uri=$uri")
        }
    }

    override fun onBackPressed() {
        finish()
    }

    private inner class CompressFilesTask(var filepath: String, var temp: String) : AsyncTask<String, String, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg strings: String): String? {
            return if (temp == "image") {
                compressImage(this@ViewPagerActivity, filepath)
            } else {
                compressvideo(this@ViewPagerActivity, filepath)
            }
        }

        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
        }
    }

    companion object {
        lateinit var img_rv: RecyclerView
        lateinit var trim_time: TextView
    }

    override fun onMediaPicked(mediaItems: ArrayList<Uri>) {
        var imageList: ArrayList<String?>? = ArrayList()
        imageurilist!!.forEach { uri ->
            imageList!!.add(uri.toString())
        }
        mediaItems.forEach { uri ->

            imageList?.add(uri.toString())
            caption_arr.add("")
            time_arr.add("")

            if (isImageFile(uri.toString())) {
                uritype?.add(1)
            } else {
                uritype?.add(0)
            }
        }

        val intent = Intent(this@ViewPagerActivity, ViewPagerActivity::class.java)
        intent.putStringArrayListExtra("select_urls", imageList)
        intent.putStringArrayListExtra("select_captions", caption_arr)
        intent.putStringArrayListExtra("select_time", time_arr)
        intent.putIntegerArrayListExtra("urls_mediatype", uritype)
        startActivity(intent)
        finish()
    }
}