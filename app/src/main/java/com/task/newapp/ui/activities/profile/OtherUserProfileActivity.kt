package com.task.newapp.ui.activities.profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.task.newapp.R
import com.task.newapp.databinding.ActivityOtherUserBinding
import com.task.newapp.utils.getDisplayMatrix
import com.task.newapp.utils.showLog
import kotlin.math.roundToInt

class OtherUserProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityOtherUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_other_user)

        binding.layoutContentOtherprofile.txtUsername.isSelected = true

        binding.ivProfile.layoutParams.height = ((getDisplayMatrix().heightPixels / 1.7).roundToInt())
        var sheetBehavior = BottomSheetBehavior.from(binding.layoutContentOtherprofile.bottomSheet)

        sheetBehavior.state = BottomSheetBehavior.STATE_DRAGGING
        binding.layoutContentOtherprofile.bottomSheet.requestLayout()


        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                showLog("behaviour",newState.toString())
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    try {
                        sheetBehavior.state = BottomSheetBehavior.STATE_DRAGGING
                        binding.layoutContentOtherprofile.bottomSheet.requestLayout()
                    } catch (e: Exception) {
                        sheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                        binding.layoutContentOtherprofile.bottomSheet.requestLayout()
                    }
                }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

        }) // You can also attach the listener here itself.
    }
}