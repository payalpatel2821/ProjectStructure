package com.avatarfirst.avatargenlib

import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.drawable.BitmapDrawable
import android.text.TextPaint
import java.util.*

/**
 * Created by Korir on 1/21/20.
 */
class AvatarGenerator {
    companion object {
        private lateinit var uiContext: Context
        private var texSize = 0F
        val CIRCLE = 1
        val RECTANGLE = 0

        fun avatarImage(context: Context, size: Int, shape: Int, name: String, color: String): BitmapDrawable {
            return avatarImageGenerate(context, size, shape, name, color)
        }


        private fun avatarImageGenerate(context: Context, size: Int, shape: Int, name: String, color: String): BitmapDrawable {
            uiContext = context

            texSize = calTextSize(size)
            val label = firstCharacter(name)
            val textPaint = textPainter()
            val painter = painter()
            painter.isAntiAlias = true
            val areaRect = Rect(0, 0, size, size)

            if (shape == 0) {
                val firstLetter = firstCharacter(name)
                val r = firstLetter[0]
                //Random rnd = new Random();
                //int color = generateRandomColor();// Color.argb(255, rnd.nextInt(100), rnd.nextInt(150), rnd.nextInt(256));
                val color = Color.parseColor(color)//RandomColors(colorModel).getColor()

                painter.color = color
                painter.shader = LinearGradient(size.toFloat() / 3, size.toFloat() / 4, 0F, size.toFloat(), color, Color.WHITE, Shader.TileMode.CLAMP)

                // painter.color = RandomColors(colorModel).getColor()
            } else {
                painter.color = Color.TRANSPARENT
            }

            val bitmap = Bitmap.createBitmap(size, size, ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawRect(areaRect, painter)

            //reset painter
            if (shape == 0) {
                painter.color = Color.TRANSPARENT
            } else {
                val firstLetter = firstCharacter(name)
                val r = firstLetter[0]
                // painter.color = RandomColors(colorModel).getColor()

                val color = Color.parseColor(color)//RandomColors(colorModel).getColor()
                painter.color = color;
                painter.shader = LinearGradient(size.toFloat() / 3, size.toFloat() / 4, 0F, size.toFloat(), color, Color.WHITE, Shader.TileMode.CLAMP);
            }

            /*   val bounds = RectF(areaRect)
               bounds.right = textPaint.measureText(label, 0, 1)
               bounds.bottom = textPaint.descent() - textPaint.ascent()

               bounds.left += (areaRect.width() - bounds.right) / 2.0f
               bounds.top += (areaRect.height() - bounds.bottom) / 2.0f

               canvas.drawCircle(size.toFloat() / 2, size.toFloat() / 2, size.toFloat() / 2, painter)
               canvas.drawText(label, bounds.left, bounds.top - textPaint.ascent(), textPaint)*/

            val stringWidth = textPaint.measureText(label);
            val xPosition = (canvas.clipBounds.width() / 2f) - (stringWidth / 2f);
            val yPosition = (canvas.clipBounds.height() / 2f) - ((textPaint.ascent() + textPaint.descent()) / 2f);
            canvas.drawCircle(size.toFloat() / 2, size.toFloat() / 2, size.toFloat() / 2, painter);
            canvas.drawText(label, xPosition, yPosition, textPaint);
            return BitmapDrawable(uiContext.resources, bitmap)

        }


        private fun firstCharacter(name: String): String {
            return name.first().toString().toUpperCase(Locale.ROOT)
        }

        private fun textPainter(): TextPaint {
            val textPaint = TextPaint()
            textPaint.isAntiAlias = true
            textPaint.textSize = texSize * uiContext.resources.displayMetrics.scaledDensity
            textPaint.color = Color.WHITE
            return textPaint
        }

        private fun painter(): Paint {
            return Paint()
        }

        private fun calTextSize(size: Int): Float {
            return (size / 3.125).toFloat()
        }
    }
}