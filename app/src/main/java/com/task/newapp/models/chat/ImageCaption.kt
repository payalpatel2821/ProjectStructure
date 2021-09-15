package com.task.newapp.models.chat

import android.net.Uri
import java.io.File

class ImageCaption(
    val strCaption: String,
    val imgVideoData: Uri,
    var stratTime: Double,
    var endTime: Double,
    var positonBarTime: Double,
    var isTrimming: Boolean,
    var data: File,
) {
}