package com.task.newapp.utils.photoeditor

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.task.newapp.R
import java.util.*

class StickerBSFragment : BottomSheetDialogFragment() {
    var stickerarray: ArrayList<Drawable>? = null
    private var mStickerListener: StickerListener? = null
    private val mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    fun setStickerListener(stickerListener: StickerListener?) {
        mStickerListener = stickerListener
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView =
            View.inflate(getContext(), R.layout.fragment_bottom_sticker_emoji_dialog, null)
        dialog.setContentView(contentView)
        val params: CoordinatorLayout.LayoutParams =
            (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior: CoordinatorLayout.Behavior<*> = params.getBehavior()!!
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            (behavior as BottomSheetBehavior<*>).setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        (contentView.parent as View).setBackgroundColor(getResources().getColor(R.color.transparent))
        val rvEmoji: RecyclerView = contentView.findViewById(R.id.rvEmoji)
        stickerarray = ArrayList<Drawable>()
        for (j in 1..50) {
            val drawable: Drawable = getResources().getDrawable(
                getResources()
                    .getIdentifier("sticker_$j", "drawable", requireContext().getPackageName())
            )
            stickerarray!!.add(drawable)
        }
        val gridLayoutManager = GridLayoutManager(getActivity(), 3)
        rvEmoji.setLayoutManager(gridLayoutManager)
        val stickerAdapter = StickerAdapter()
        rvEmoji.setAdapter(stickerAdapter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun convertEmoji(emoji: String): String {
        var returnedEmoji = ""
        returnedEmoji = try {
            val convertEmojiToInt = emoji.substring(2).toInt(16)
            getEmojiByUnicode(convertEmojiToInt)
        } catch (e: NumberFormatException) {
            ""
        }
        return returnedEmoji
    }

    private fun getEmojiByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }

    interface StickerListener {
        fun onStickerClick(bitmap: Bitmap?)
    }

     inner class StickerAdapter : RecyclerView.Adapter<StickerAdapter.ViewHolder?>() {
        //        int[] stickerList = new int[]{R.drawable.aa, R.drawable.bb};
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_sticker, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.imgSticker.setImageDrawable(stickerarray!![position])
        }



        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var imgSticker: ImageView

            init {
                imgSticker = itemView.findViewById(R.id.imgSticker)
                itemView.setOnClickListener {
                    if (mStickerListener != null) {
                        mStickerListener!!.onStickerClick(
                            (stickerarray!![layoutPosition] as BitmapDrawable).getBitmap()
                        )
                    }
                    dismiss()
                }
            }
        }

         override fun getItemCount(): Int {
            return stickerarray!!.size
         }
     }
}