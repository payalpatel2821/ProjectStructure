package com.task.newapp.utils.shadowpost

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.widget.ImageView
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur
import com.task.newapp.utils.shadowpost.ShadowImageView.Companion.DOWNSCALE_FACTOR


object BlurShadow {

    private var renderScript: RenderScript? = null

    fun init(context: Context) {
        if (renderScript == null)
            renderScript = RenderScript.create(context)
    }

    fun blur(view: ImageView, width: Int, height: Int, radius: Float): Bitmap? {
        var src = getBitmapForView(view, DOWNSCALE_FACTOR, width, height) ?: return null
        var input = Allocation.createFromBitmap(renderScript, src)
        var output = Allocation.createTyped(renderScript, input.type)
        var script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        script.apply {
            setRadius(radius)
            setInput(input)
            forEach(output)
        }
        output.copyTo(src)
        return src
    }

    private fun getBitmapForView(view: ImageView, downscaleFactor: Float, width: Int, height: Int): Bitmap? {
        var bitmap = Bitmap.createBitmap(
            (width * downscaleFactor).toInt(),
            (height * downscaleFactor).toInt(),
            Bitmap.Config.ARGB_8888
        )

        var canvas = Canvas(bitmap)
        var matrix = Matrix()
        matrix.preScale(downscaleFactor, downscaleFactor)

        canvas.setMatrix(matrix)
        view.draw(canvas)
        return bitmap
    }
}