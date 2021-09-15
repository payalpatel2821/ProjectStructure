package com.task.newapp.ui.activities.chat

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
import com.task.newapp.R
import com.task.newapp.adapter.chat.Image_rv_adapter
import com.task.newapp.utils.exo_video.OnItemClickListener
import com.task.newapp.utils.exo_video.Sel_Media_RecyclerAdapter
import com.task.newapp.utils.exo_video.Sel_Media_RecyclerView
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class ViewPagerActivity : AppCompatActivity(), View.OnClickListener, OnItemClickListener {


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

    //    @Override
    //    protected void onResume() {
    //        super.onResume();
    //        if (mRecyclerView != null) {
    //            if (mRecyclerView.trimmer_view != null) {
    //                mRecyclerView.trimmer_view.playingAnimation();
    //                mRecyclerView.playVideo(true);
    //            }
    //        }
    //    }
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

        val iv_back = findViewById<ImageView>(R.id.iv_back)
        iv_back.setOnClickListener {
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
        iv_delete.setOnClickListener(this)
        iv_crop.setOnClickListener(this)
        imageSend.setOnClickListener(this)
        iv_edit.setOnClickListener(this)
    }

    override fun onClick(view: View?, position: Int) {

        mRecyclerView.trimmer_view.pauseRedProgressAnimation();
        mRecyclerView.toggleVolume();
    }

    fun setTrimVideo(position: Int, videouri: String?) {
        imageurilist!![position] = Uri.parse(videouri)
        runOnUiThread { image_rv_adapter!!.notifyDataSetChanged() }
    }

    override fun onClick(view: View) {
        val current: Int = mRecyclerView.currentItem
        when (view.id) {
            R.id.iv_delete -> try {
//                Commons.logEvent(this@ViewPagerActivity, "Delete", "", "ViewPagerActivity", "DeleteClick")

                //Add New By Hardik
//              ArrayList<String> imageurilistClone = (ArrayList<String>) imageurilist.clone();
//              imageurilistClone.remove(current);
//              imageurilist.clear();
//              imageurilist = (ArrayList<String>) imageurilistClone.clone();
                imageurilist!!.removeAt(current)
//                thumburilist!!.removeAt(current)
                caption_arr!!.removeAt(current)
                time_arr!!.removeAt(current)
                uritype!!.removeAt(current)
                if (imageurilist!!.size == 0) {
                    onBackPressed()
                } else {
                    Log.println(Log.ASSERT, "caption_arr-size--=--", caption_arr!!.size.toString() + "")
                    Log.println(Log.ASSERT, "imageurilist-size--=--", imageurilist!!.size.toString() + "")
                    Log.println(Log.ASSERT, "uritype-size--=--", uritype!!.size.toString() + "")
//                  mRecyclerView.setMediaObjects(imageurilist, uritype, caption_arr, time_arr);
//                  mAdapter.setMediaObjects(imageurilist, uritype, caption_arr, time_arr);
                    mAdapter.notifyItemRemoved(current)
                    image_rv_adapter!!.notifyItemRemoved(current)
                    if (mAdapter.itemCount == 0) {
                        finish()
                    }
                    //                        image_rv_adapter.notifyDataSetChanged();
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            R.id.iv_crop -> {
            }/*Commons.logEvent(this@ViewPagerActivity, "Crop", "", "ViewPagerActivity", "CropClick")*/
            R.id.imageSend -> {
                Log.e("send", "click")
                mRecyclerView.toggleVolume()
                //mRecyclerView.trimmer_view.pauseRedProgressAnimation();
                imageSend!!.isClickable = false
//                Commons.logEvent(this@ViewPagerActivity, "SendImage", "", "ViewPagerActivity", "ImageSendClick")
                compressprogress!!.visibility = View.VISIBLE
                progreelay!!.visibility = View.VISIBLE
                addcompressfile(abc)
            }
            R.id.iv_edit -> {
//                Commons.logEvent(this@ViewPagerActivity, "Edit", "", "ViewPagerActivity", "EditClick")
//                var intent = Intent()
//                intent = Intent(this@ViewPagerActivity, EditImageActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//                intent.putExtra("filepath", imageurilist!![current])
//                startActivityForResult(intent, EDIT_IMAGE)
            }
        }
    }

    fun addcompressfile(i: Int) {
        val temp: String
        temp = if (uritype!![i] == 1) {
            "image"
        } else {
            "video"
        }
        CompressFilesTask(imageurilist!![i]!!.path!!, temp).execute()
    }

    fun compressimage(context: Context?, inputFile: String): String {
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
            addcompressfile(abc)
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
//        if (myfile.length() / 1024 < 51200) {
        runOnUiThread {
            Log.e("video compress", "sccessfully")
            finalOutputFile = inputFile
            imagearray.add(inputFile)
            abc++
            if (abc < imageurilist!!.size) {
                addcompressfile(abc)
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
        }
//        } else {
//            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//            val outputName = "compress$timeStamp.mp4"
//            val outputFile: String = checkcompressfolder().toString() + "/" + outputName
//            var complexCommand = arrayOf<String?>()
//
////        complexCommand = new String[]{"-y", "-i", inputFile, "-r", "25",
////                "-vcodec", "mpeg4", "-b:v", "700k", "-b:a", "48000", "-ac", "2", "-ar", "22050", outputFile};
//
////            complexCommand = new String[]{"-y", "-i", inputFile, "-r", "25",
////                    "-b:v", "6000k", "-b:a", "48000", "-ac", "2", "-ar", "22050", "-preset", "ultrafast", outputFile};
//            complexCommand = arrayOf(
//                "-y",
//                "-i", inputFile,
//                "-c:v", "libx264",
//                "-c:a", "aac",
//                "-vf", "scale=720:-2",
//                "-crf", "28",
//                "-b:a", "48000",
//                "-preset", "ultrafast",
//                outputFile
//            )
//            finalOutputFile = outputFile
//            FFmpegCmd.exec(complexCommand, 0, object : OnEditorListener() {
//                fun onSuccess() {
//                    runOnUiThread {
//                        Log.e("video compress", "sccessfully")
//                        imagearray.add(finalOutputFile)
//                        abc++
//                        if (abc < thumburilist!!.size) {
//                            addcompressfile(abc)
//                        } else {
//                            runOnUiThread {
//                                compressprogress!!.visibility = View.GONE
//                                progreelay!!.visibility = View.GONE
//                                // Stuff that updates the UI
//                            }
//                            val intent = Intent()
//                            intent.putStringArrayListExtra("select_urls", imagearray)
//                            intent.putStringArrayListExtra("select_captions", caption_arr)
//                            intent.putStringArrayListExtra("select_time", time_arr)
//                            intent.putIntegerArrayListExtra("urls_mediatype", uritype)
//                            setResult(RESULT_OK, intent)
//                            finish()
//                        }
//                    }
//                }
//
//                fun onFailure() {
//                    imageSend!!.isClickable = true
//                    runOnUiThread {
//                        Log.e("video compress", "Failed")
//                        //                        Toast.makeText(context, "Failed to trim", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                fun onProgress(progress: Float) {
//                    Log.println(Log.ASSERT, "progress--==", progress.toString() + "")
//                }
//            })
//        }
        return finalOutputFile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == EDIT_IMAGE) {
////            Commons.logEvent(this@ViewPagerActivity, "EditImageResult", "", "ViewPagerActivity", "onActivityResult")
//            handleEditorImage(data)
//        } else if (requestCode == CAMERA_REQUEST) {
//            if (data != null) {
////                Commons.logEvent(this@ViewPagerActivity, "CameraImageResult", "", "ViewPagerActivity", "onActivityResult")
//                imageurilist!!.clear()
//                thumburilist!!.clear()
//                uritype!!.clear()
//                time_arr!!.clear()
//                caption_arr!!.clear()
//                imageurilist!!.addAll(data.getStringArrayListExtra(Pix.IMAGE_RESULTS)!!)
//                thumburilist!!.addAll(data.getStringArrayListExtra(Pix.IMAGE_RESULTS)!!)
//                uritype!!.addAll(data.getIntegerArrayListExtra(Pix.RESULTS_TYPE)!!)
//
//                //Add New
////                for (int i = 0; i < imageurilist.size(); i++) {
////                    for (int j = i + 1; j < imageurilist.size(); j++) {
////                        if (imageurilist.get(i).equals(imageurilist.get(j))) {
////                            imageurilist.remove(j);
////                            thumburilist.remove(j);
////                            uritype.remove(j);
////                            j--;
////                        }
////                    }
////                }
//                captionarr = Arrays.asList(*arrayOfNulls(thumburilist!!.size))
//                caption_arr!!.addAll(captionarr)
//                timearr = Arrays.asList(*arrayOfNulls(thumburilist!!.size))
//                time_arr!!.addAll(timearr)
//
////                Log.println(Log.ASSERT, "sdadasdasd-size--=--", imageurilist.size() + "");
////                Log.println(Log.ASSERT, "thumburilist-size--=--", thumburilist.size() + "");
////                Log.println(Log.ASSERT, "caption_arr-size--=--", captionarr.size() + "");
////                Log.println(Log.ASSERT, "uritype-size--=--", uritype.size() + "");
//
////            mRecyclerView.setMediaObjects(imageurilist, uritype, caption_arr);
////            mAdapter.notifyDataSetChanged();
//                mRecyclerView.setMediaObjects(imageurilist, uritype, caption_arr, time_arr)
//                mAdapter.setMediaObjects(imageurilist, uritype, caption_arr, time_arr)
//                image_rv_adapter.setData(thumburilist, uritype)
//            }
//        }
    }

    private fun handleEditorImage(data: Intent?) {
        var newFilePath = data!!.getStringExtra("OUTPUT_PATH")
        val isImageEdit = data.getBooleanExtra("IS_IMAGE_EDITED", false)
        if (isImageEdit) {
//            Toast.makeText(this, getString(R.string.save_path, newFilePath), Toast.LENGTH_LONG).show();
        } else {
            newFilePath = data.getStringExtra("SOURCE_PATH")
        }
        imageurilist!!.removeAt(mRecyclerView.getCurrentItem())
        imageurilist!!.add(mRecyclerView.getCurrentItem(), Uri.parse(newFilePath))
//        thumburilist!!.removeAt(mRecyclerView.getCurrentItem())
//        thumburilist!!.add(mRecyclerView.getCurrentItem(), newFilePath)
        mAdapter.notifyDataSetChanged()
        image_rv_adapter!!.notifyDataSetChanged()

        //----------------------Add New for scan file---------------------------
        MediaScannerConnection.scanFile(this, arrayOf(newFilePath), null) { path, uri ->
            Log.i("ExternalStorage", "Scanned $path:")
            Log.i("ExternalStorage", "-> uri=$uri")
        }
    }

    override fun onBackPressed() {
//        if (imageurilist!!.size == 0) {
        finish()
//        } else {
//            imageurilist = ArrayList()
//            var files: Array<File> = File(checktrimfolder()).listFiles()
//            if (files.size > 0) {
//                for (i in files.indices) {
//                    files[i].delete()
//                }
//            }
//            val outputPath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "/HOW/TrimedVideos/"
//            files = File(outputPath).listFiles()
//            if (files != null && files.size > 0) {
//                for (i in files.indices) {
//                    files[i].delete()
//                }
//            }
//            finish()
//        }
    }

    private inner class CompressFilesTask(var filepath: String, var temp: String) : AsyncTask<String, String, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg strings: String): String? {
            return if (temp == "image") {
//                Commons.logEvent(this@ViewPagerActivity, "CompressTask", "Image", "ViewPagerActivity", "AsyncTask")
                compressimage(this@ViewPagerActivity, filepath)
            } else {
//                Commons.logEvent(this@ViewPagerActivity, "CompressTask", "Video", "ViewPagerActivity", "AsyncTask")
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
}