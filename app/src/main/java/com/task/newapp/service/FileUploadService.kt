package com.task.newapp.service

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import com.task.newapp.R
import com.task.newapp.api.ApiClient
import com.task.newapp.models.CommonResponse
import com.task.newapp.models.post.Post_Uri_Model
import com.task.newapp.ui.activities.MainActivity
import com.task.newapp.utils.*
import com.task.newapp.utils.compressor.SiliCompressor
import com.vincent.videocompressor.VideoCompress
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FileUploadService : JobIntentService() {
    //    RestApiService apiService;
    var mDisposable: Disposable? = null
    lateinit var caption: String
    lateinit var switchTurnOff: String
    lateinit var mNotificationHelper: NotificationHelper

    var captionarray: ArrayList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
    var typearray: ArrayList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
    var thumbarray: ArrayList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
    var imagearray: ArrayList<MultipartBody.Part> = ArrayList<MultipartBody.Part>()
    var count = 0
    private lateinit var arrayListMedia: ArrayList<Post_Uri_Model>
    private val mCompositeDisposable = CompositeDisposable()

    var onPostDoneClickListener: OnPostDoneClickListener? = null

    override fun onCreate() {
        super.onCreate()
        mNotificationHelper = NotificationHelper(this)
    }

    private fun onErrors() {
        /**
         * Error occurred in file uploading
         */
        val successIntent = Intent("com.wave.ACTION_CLEAR_NOTIFICATION")
        successIntent.putExtra("notificationId", FileUploadService.NOTIFICATION_ID)
        sendBroadcast(successIntent)
        val resultPendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0 /* Request code */, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val mBuilder: NotificationCompat.Builder = mNotificationHelper.getNotification(
            getString(R.string.error_upload_failed),
            getString(R.string.message_upload_failed), resultPendingIntent
        )

        // Notify notification
        mNotificationHelper?.notify(FileUploadService.NOTIFICATION_RETRY_ID, mBuilder)
    }

//    private fun onErrors(Throwable throwable) {
//        /**
//         * Error occurred in file uploading
//         */
//        Intent successIntent = new Intent("com.wave.ACTION_CLEAR_NOTIFICATION");
//        successIntent.putExtra("notificationId", NOTIFICATION_ID);
//        sendBroadcast(successIntent);
//        PendingIntent resultPendingIntent = PendingIntent . getActivity (this,
//        0 /* Request code */, new Intent(this, MainActivity.class),
//        PendingIntent.FLAG_UPDATE_CURRENT);
//        /**
//         * Add retry action button in notification
//         */
//        Intent retryIntent = new Intent(this, RetryJobReceiver.class);
//        retryIntent.putExtra("notificationId", NOTIFICATION_RETRY_ID);
//        retryIntent.putExtra("mFilePath", mFilePath);
//        retryIntent.setAction(ACTION_RETRY);
//        /**
//         * Add clear action button in notification
//         */
//        Intent clearIntent = new Intent(this, RetryJobReceiver.class);
//        clearIntent.putExtra("notificationId", NOTIFICATION_RETRY_ID);
//        clearIntent.putExtra("mFilePath", mFilePath);
//        clearIntent.setAction(ACTION_CLEAR);
//        PendingIntent retryPendingIntent = PendingIntent . getBroadcast (this, 0, retryIntent, 0);
//        PendingIntent clearPendingIntent = PendingIntent . getBroadcast (this, 0, clearIntent, 0);
//        NotificationCompat.Builder mBuilder =
//        mNotificationHelper.getNotification(
//            getString(R.string.error_upload_failed),
//            getString(R.string.message_upload_failed), resultPendingIntent
//        );
//        // attached Retry action in notification
//        mBuilder.addAction(
//            android.R.drawable.ic_menu_revert, getString(R.string.btn_retry_not),
//            retryPendingIntent
//        );
//        // attached Cancel action in notification
//        mBuilder.addAction(
//            android.R.drawable.ic_menu_revert, getString(R.string.btn_cancel_not),
//            clearPendingIntent
//        );
//        // Notify notification
//        mNotificationHelper.notify(NOTIFICATION_RETRY_ID, mBuilder);
//    }

    /**
     * Send Broadcast to FileProgressReceiver with progress
     *
     * @param progress file uploading progress
     */
    private fun onProgress(progress: Int) {
        val progressIntent = Intent(this, FileProgressReceiver::class.java)
        progressIntent.action = "com.wave.ACTION_PROGRESS_NOTIFICATION"
        progressIntent.putExtra("notificationId", NOTIFICATION_ID)

//        progressIntent.putExtra("progress", (100 * progress).toInt())

        val progressVal = (100 * progress) / arrayListMedia.size
        showLog("progressVal", progressVal.toString())

        progressIntent.putExtra("progress", progressVal)

        sendBroadcast(progressIntent)
    }

    /**
     * Send Broadcast to FileProgressReceiver while file upload successful
     */
    private fun onSuccess() {
        val successIntent = Intent(this, FileProgressReceiver::class.java)
        successIntent.action = "com.wave.ACTION_UPLOADED"
        successIntent.putExtra("notificationId", NOTIFICATION_ID)
        successIntent.putExtra("progress", 100)
        sendBroadcast(successIntent)
    }

    //    private RequestBody createRequestBodyFromFile(File file, String mimeType) {

    //        return RequestBody.create(MediaType.parse(mimeType), file);
    //    }
    //
    //    private RequestBody createRequestBodyFromText(String mText) {
    //        return RequestBody.create(MediaType.parse("text/plain"), mText);
    //    }
    //
    //    /**
    //     * return multi part body in format of FlowableEmitter
    //     */
    //    private MultipartBody.Part createMultipartBody(String filePath, FlowableEmitter<Double> emitter) {
    //        File file = new File(filePath);
    //        return MultipartBody.Part.createFormData("myFile", file.getName(),
    //                createCountingRequestBody(file, MIMEType.IMAGE.value, emitter));
    //    }
    //
    //    private RequestBody createCountingRequestBody(File file, String mimeType,
    //                                                  final FlowableEmitter<Double> emitter) {
    //        RequestBody requestBody = createRequestBodyFromFile(file, mimeType);
    //        return new CountingRequestBody(requestBody, new CountingRequestBody.Listener() {
    //            @Override
    //            public void onRequestProgress(long bytesWritten, long contentLength) {
    //                double progress = (1.0 * bytesWritten) / contentLength;
    //                emitter.onNext(progress);
    //            }
    //        });
    //    }


    companion object {
        private const val TAG = "FileUploadService"
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_RETRY_ID = 2
        lateinit var context: Activity

        /**
         * Unique job ID for this service.
         */
        private const val JOB_ID = 102
        fun enqueueWork(context: Activity, intent: Intent?) {
            this.context = context
            enqueueWork(context!!, FileUploadService::class.java, JOB_ID, intent!!)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onHandleWork(intent: Intent) {
        Log.d(TAG, "onHandleWork: ")

        // get file file here
        caption = intent.getStringExtra("caption").toString()
        switchTurnOff = intent.getStringExtra("switchTurnOff").toString()
        arrayListMedia = intent.getSerializableExtra("mediaItemsArray") as ArrayList<Post_Uri_Model>

        if (caption == null) {
            Log.e(TAG, "onHandleWork: Invalid file URI")
            return
        }

        uploadPost()

//        apiService = RetrofitInstance.getApiService();
//        Flowable<Double> fileObservable = Flowable.create(new FlowableOnSubscribe<Double>() {
//            @Override
//            public void subscribe(FlowableEmitter<Double> emitter) throws Exception {
//                apiService.onFileUpload(
//                        FileUploadService.this.createRequestBodyFromText("info@androidwave.com"),
//                        FileUploadService.this.createMultipartBody(mFilePath, emitter))
//                        .blockingGet();
//                emitter.onComplete();
//            }
//        }, BackpressureStrategy.LATEST);
//        mDisposable = fileObservable.subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Double>() {
//                    @Override
//                    public void accept(Double progress) throws Exception {
//                        // call onProgress()
//                        FileUploadService.this.onProgress(progress);
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        // call onErrors() if error occurred during file upload
//                        FileUploadService.this.onErrors(throwable);
//                    }
//                }, new Action() {
//                    @Override
//                    public void run() throws Exception {
//                        // call onSuccess() while file upload successful
//                        FileUploadService.this.onSuccess();
//                    }
//                });
    }

    private fun uploadPost() {
        try {
            count = 0

            //get all data and compress if required
            if (!arrayListMedia.isNullOrEmpty()) {
                Log.e("uploadPost_arrayList:", arrayListMedia.toString())

                //----------------------------Check and compress---------------------------------

                captionarray = ArrayList()
                typearray = ArrayList()
                thumbarray = ArrayList()
                imagearray = ArrayList()

                openProgressDialog(context)

                arrayListMedia.forEachIndexed { index, postUriModel ->
                    try {
                        val cap_name = "contents[$index][caption]"
                        val type_name = "contents[$index][type]"
                        val thumb_name = "contents[$index][thumb]"
                        val image_name = "contents[$index][file]"

                        captionarray.add(MultipartBody.Part.createFormData(cap_name, caption))
                        typearray.add(MultipartBody.Part.createFormData(type_name, postUriModel.type))

                        val myfile: File = File(postUriModel.file_path)

                        val mainFolder = File(Environment.getExternalStorageDirectory().absolutePath + "/HOW")
                        if (!mainFolder.exists()) {
                            mainFolder.mkdir()
                            mainFolder.mkdirs()
                        }
                        val fileImage = File(mainFolder.absolutePath + "/.temp")
                        if (!fileImage.exists()) {
                            fileImage.mkdir()
                            fileImage.mkdirs()
                        }

                        val storedThumbPath: File = File(fileImage, System.currentTimeMillis().toString() + "_thumb.jpg")

                        //Check image or video
                        if (postUriModel.type == "image") {

//                            if (myfile.length() / 1024 > 25600) {  // More than 25 MB
                            Log.e("uploadPost: >25MB ", myfile.toString())

                            //Compress and Stored in .temp folder
                            val filePath: String = SiliCompressor.with(context).compress(
                                postUriModel.file_path,
                                storedThumbPath
                            )
                            thumbarray.add(prepareFilePart(thumb_name, filePath, "image/*"))
                            //thumbarray.add(prepareFilePart(thumb_name, postUriModel.file_path, "image/*"))
//                            }
//                            else {
//                                Log.e("uploadPost: ", postUriModel.file_path.toString())
//                                thumbarray.add(prepareFilePart(thumb_name, postUriModel.file_path, "image/*"))
//                            }
                            //Original
                            imagearray.add(prepareFilePart(image_name, postUriModel.file_path, "image/*"))
                            count++
                            onProgress(count)

                            if (count == arrayListMedia.size) {
                                showLog("VideoCompress", "callAPIPost_$count")
                                callAPIPost(caption)
                            }

                        } else {
                            //Video

                            if (myfile.length() / 1024 > 51200) {  // More than 51 MB
                                Log.e("uploadPost: >51MB ", myfile.toString())
                                //Compress

                                //Stored in .temp folder
//                                val filePath: String = SiliCompressor.with(activity).compress(
//                                    postUriModel.file_path,
//                                    storedThumbPath
//                                )
//                                thumbarray.add(prepareFilePart(thumb_name, filePath, "image/*"))

                                //-----------------------Compress Video-----------------------------
                                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                                val outputName = "compress$timeStamp.mp4"

                                val outputFile: String = checkCompressfolder().toString() + "/" + outputName  // /HOW/.compressvideo

                                //Stored in .compressvideo folder
//                                val compressVideoPath: String = SiliCompressor.with(activity).compressVideo(
//                                    postUriModel.file_path,
//                                    outputFile
//                                )

                                val destPath: String = outputFile

                                VideoCompress.compressVideoLow(postUriModel.file_path, destPath, object : VideoCompress.CompressListener {
                                    override fun onStart() {
                                        showLog("VideoCompress", "onStart")
//                                        tv_indicator.setText(
//                                            "Compressing..." + "\n"
//                                                    + "Start at: " + SimpleDateFormat("HH:mm:ss", getLocale()).format(Date())
//                                        )
//                                        pb_compress.setVisibility(View.VISIBLE)
//                                        startTime = System.currentTimeMillis()
//                                        Util.writeFile(this@MainActivity, "Start at: " + SimpleDateFormat("HH:mm:ss", getLocale()).format(Date()) + "\n")
                                    }

                                    override fun onSuccess() {
                                        showLog("VideoCompress", "onSuccess")
//                                        val previous: String = tv_indicator.getText().toString()
//                                        tv_indicator.setText(
//                                            (previous + "\n"
//                                                    + "Compress Success!" + "\n"
//                                                    + "End at: " + SimpleDateFormat("HH:mm:ss", getLocale()).format(Date()))
//                                        )
//                                        pb_compress.setVisibility(View.INVISIBLE)
//                                        endTime = System.currentTimeMillis()
//                                        Util.writeFile(this@MainActivity, "End at: " + SimpleDateFormat("HH:mm:ss", getLocale()).format(Date()) + "\n")
//                                        Util.writeFile(this@MainActivity, "Total: " + ((endTime - startTime) / 1000) + "s" + "\n")
//                                        Util.writeFile(this@MainActivity)

                                        //---------------------------New added-----------------------------
                                        val retriever = MediaMetadataRetriever()
                                        retriever.setDataSource(destPath)
                                        val extractedImage = retriever.getFrameAtTime(100, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                                        val bitmap: Bitmap = if (extractedImage!!.height > 500 && extractedImage.width > 500) {
                                            Bitmap.createScaledBitmap(extractedImage, extractedImage.width / 3, extractedImage.height / 3, true)
                                        } else {
                                            Bitmap.createScaledBitmap(extractedImage, extractedImage.width, extractedImage.height, true)
                                        }

                                        //------------------------------thumb------------------------------
                                        if (storeImage(bitmap, storedThumbPath)) {
                                            thumbarray.add(prepareFilePart(thumb_name, storedThumbPath.path, "image/*"))
                                        }

                                        //Original
                                        imagearray.add(prepareFilePart(image_name, destPath, "video/*"))

                                        count++
                                        onProgress(count)
                                        if (count == arrayListMedia.size) {
                                            showLog("VideoCompress", "callAPIPost_$count")
                                            callAPIPost(caption)
                                        }
                                    }

                                    override fun onFail() {
                                        count++
                                        onProgress(count)
                                        showLog("VideoCompress", "onFail")

                                        if (count == arrayListMedia.size) {
                                            showLog("VideoCompress", "callAPIPost_$count")
                                            callAPIPost(caption)
                                        }

//                                        tv_indicator.setText("Compress Failed!")
//                                        pb_compress.setVisibility(View.INVISIBLE)
//                                        endTime = System.currentTimeMillis()
//                                        Util.writeFile(this@MainActivity, "Failed Compress!!!" + SimpleDateFormat("HH:mm:ss", getLocale()).format(Date()))
                                    }

                                    override fun onProgress(percent: Float) {
                                        showLog("VideoCompress", "onProgress_$percent")
//                                        tv_progress.setText("$percent%")
                                    }
                                })

//                                //---------------------------New added-----------------------------
//                                val retriever = MediaMetadataRetriever()
//                                retriever.setDataSource(destPath)
//                                val extractedImage = retriever.getFrameAtTime(100, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
//                                val bitmap: Bitmap = if (extractedImage!!.height > 500 && extractedImage.width > 500) {
//                                    Bitmap.createScaledBitmap(extractedImage, extractedImage.width / 3, extractedImage.height / 3, true)
//                                } else {
//                                    Bitmap.createScaledBitmap(extractedImage, extractedImage.width, extractedImage.height, true)
//                                }
//
//                                //------------------------------thumb------------------------------
//                                if (storeImage(bitmap, storedThumbPath)) {
//                                    thumbarray.add(prepareFilePart(thumb_name, storedThumbPath.path, "image/*"))
//                                }
//
//                                //Original
//                                imagearray.add(prepareFilePart(image_name, destPath, "video/*"))

                            } else {
                                Log.e("uploadPost: ", postUriModel.file_path.toString())

                                //Stored in .temp folder
//                                val filePath: String = SiliCompressor.with(activity).compressVideo(
//                                    postUriModel.file_path,
//                                    storedThumbPath.path
//                                )

                                VideoCompress.compressVideoLow(postUriModel.file_path, storedThumbPath.path, object : VideoCompress.CompressListener {
                                    override fun onStart() {
                                        showLog("VideoCompress", "onStart")
//                                        tv_indicator.setText(
//                                            "Compressing..." + "\n"
//                                                    + "Start at: " + SimpleDateFormat("HH:mm:ss", getLocale()).format(Date())
//                                        )
//                                        pb_compress.setVisibility(View.VISIBLE)
//                                        startTime = System.currentTimeMillis()
//                                        Util.writeFile(this@MainActivity, "Start at: " + SimpleDateFormat("HH:mm:ss", getLocale()).format(Date()) + "\n")
                                    }

                                    override fun onSuccess() {
                                        count++
                                        onProgress(count)
                                        showLog("VideoCompress", "onSuccess")

                                        if (count == arrayListMedia.size) {
                                            showLog("VideoCompress", "callAPIPost_$count")
                                            callAPIPost(caption)
                                        }

//                                        val previous: String = tv_indicator.getText().toString()
//                                        tv_indicator.setText(
//                                            (previous + "\n"
//                                                    + "Compress Success!" + "\n"
//                                                    + "End at: " + SimpleDateFormat("HH:mm:ss", getLocale()).format(Date()))
//                                        )
//                                        pb_compress.setVisibility(View.INVISIBLE)
//                                        endTime = System.currentTimeMillis()
//                                        Util.writeFile(this@MainActivity, "End at: " + SimpleDateFormat("HH:mm:ss", getLocale()).format(Date()) + "\n")
//                                        Util.writeFile(this@MainActivity, "Total: " + ((endTime - startTime) / 1000) + "s" + "\n")
//                                        Util.writeFile(this@MainActivity)

                                        //---------------------------New added-----------------------------
                                        thumbarray.add(prepareFilePart(thumb_name, storedThumbPath.path, "image/*"))
                                    }

                                    override fun onFail() {
                                        count++
                                        onProgress(count)
                                        showLog("VideoCompress", "onFail")

                                        if (count == arrayListMedia.size) {
                                            showLog("VideoCompress", "callAPIPost_$count")
                                            callAPIPost(caption)
                                        }
//                                        tv_indicator.setText("Compress Failed!")
//                                        pb_compress.setVisibility(View.INVISIBLE)
//                                        endTime = System.currentTimeMillis()
//                                        Util.writeFile(this@MainActivity, "Failed Compress!!!" + SimpleDateFormat("HH:mm:ss", getLocale()).format(Date()))
                                    }

                                    override fun onProgress(percent: Float) {
                                        showLog("VideoCompress", "onProgress_$percent")
//                                        tv_progress.setText("$percent%")
                                    }
                                })

//                                thumbarray.add(prepareFilePart(thumb_name, filePath, "image/*"))
//
//                                //Original
                                imagearray.add(prepareFilePart(image_name, postUriModel.file_path, "video/*"))
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        count++
                        onProgress(count)

                        if (count == arrayListMedia.size) {
                            showLog("VideoCompress", "callAPIPost_$count")
                            callAPIPost(caption)
                        }
                    }
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callAPIPost(title: String) {
        try {
            val isComment = switchTurnOff

            val turn_off_comment: RequestBody = isComment.toRequestBody("text/plain".toMediaTypeOrNull())
            val hastags: RequestBody = "".toRequestBody("text/plain".toMediaTypeOrNull())
            val title: RequestBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val type: RequestBody = "simple".toRequestBody("text/plain".toMediaTypeOrNull())
            val latitude: RequestBody = "0".toRequestBody("text/plain".toMediaTypeOrNull())
            val longitude: RequestBody = "0".toRequestBody("text/plain".toMediaTypeOrNull())
            val location: RequestBody = "".toRequestBody("text/plain".toMediaTypeOrNull())

            //openProgressDialog(activity)

            mCompositeDisposable.add(
                ApiClient.create()
                    .addPost(turn_off_comment, hastags, title, type, latitude, longitude, location, captionarray, typearray, thumbarray, imagearray)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<CommonResponse>() {
                        override fun onNext(commonResponse: CommonResponse) {

//                            if (commonResponse.success == 1) {
                            context.showToast(commonResponse.message)

                            onSuccess()

                            //Close bottom sheet and refresh post list
//                            dismiss()
                            onPostDoneClickListener?.onPostClick()

//                            }
                        }

                        override fun onError(e: Throwable) {
                            Log.v("onError: ", e.toString())
                            hideProgressDialog()
                            onErrors()
                        }

                        override fun onComplete() {
                            hideProgressDialog()
                        }
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
            hideProgressDialog()
        }
    }

    /**
     * interface for post done click
     *
     */
    interface OnPostDoneClickListener {
        fun onPostClick()
    }

    fun setListener(listener: OnPostDoneClickListener) {
        onPostDoneClickListener = listener
    }


}