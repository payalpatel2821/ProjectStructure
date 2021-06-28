package com.task.newapp.ui.activities.profile

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MotionEventCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.task.newapp.R
import com.task.newapp.databinding.ActivityMyProfileBinding
import com.task.newapp.utils.getDisplayMatrix
import com.task.newapp.utils.showLog
import kotlin.math.roundToInt


class MyProfileActivity : AppCompatActivity() {

    lateinit var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback
    lateinit var binding: ActivityMyProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_profile)

        binding.ivProfile.layoutParams.height = ((getDisplayMatrix().heightPixels / 1.7).roundToInt())
        var sheetBehavior = BottomSheetBehavior.from(binding.layoutContentMyprofile.bottomSheet)

        sheetBehavior.state = BottomSheetBehavior.STATE_DRAGGING
        binding.layoutContentMyprofile.bottomSheet.requestLayout()


        sheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                showLog("behaviour",newState.toString())
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    try {
                        sheetBehavior.state = BottomSheetBehavior.STATE_DRAGGING
                        binding.layoutContentMyprofile.bottomSheet.requestLayout()
                    } catch (e: Exception) {
                        sheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                        binding.layoutContentMyprofile.bottomSheet.requestLayout()
                    }
                }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

        }) // You can also attach the listener here itself.


    }
}