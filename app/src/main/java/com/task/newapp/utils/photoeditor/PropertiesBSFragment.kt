package com.task.newapp.utils.photoeditor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.task.newapp.R

class PropertiesBSFragment : BottomSheetDialogFragment(), SeekBar.OnSeekBarChangeListener {
    private var mProperties: Properties? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("erase", iserase.toString() + "::")
        return inflater.inflate(R.layout.fragment_bottom_properties_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvColor: RecyclerView = view.findViewById(R.id.rvColors)
        val sbOpacity: SeekBar = view.findViewById<SeekBar>(R.id.sbOpacity)
        val sbBrushSize: SeekBar = view.findViewById<SeekBar>(R.id.sbSize)
        val txtBrushSize: TextView = view.findViewById<TextView>(R.id.txtBrushSize)
        val txtOpacity: TextView = view.findViewById<TextView>(R.id.txtOpacity)
        if (iserase) {
            rvColor.setVisibility(View.INVISIBLE)
            sbOpacity.setVisibility(View.GONE)
            sbBrushSize.setVisibility(View.VISIBLE)
            txtBrushSize.setVisibility(View.VISIBLE)
            txtOpacity.setVisibility(View.GONE)
        } else {
            rvColor.setVisibility(View.VISIBLE)
            sbOpacity.setVisibility(View.VISIBLE)
            sbBrushSize.setVisibility(View.VISIBLE)
            txtBrushSize.setVisibility(View.VISIBLE)
            txtOpacity.setVisibility(View.VISIBLE)
        }
        sbOpacity.setOnSeekBarChangeListener(this)
        sbBrushSize.setOnSeekBarChangeListener(this)
        val layoutManager =
            LinearLayoutManager(getActivity()/*, LinearLayoutManager.HORIZONTAL, false*/)
        rvColor.setLayoutManager(layoutManager)
        rvColor.setHasFixedSize(true)
        val colorPickerAdapter = ColorPickerAdapter(requireActivity())
        colorPickerAdapter.setOnColorPickerClickListener(object :
            ColorPickerAdapter.OnColorPickerClickListener {
            override fun onColorPickerClickListener(colorCode: Int) {
                if (mProperties != null) {
                    dismiss()
                    mProperties!!.onColorChanged(colorCode)
                }
            }
        })
        rvColor.setAdapter(colorPickerAdapter)
    }

    fun setPropertiesChangeListener(properties: Properties?) {
        mProperties = properties
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        when (seekBar.getId()) {
            R.id.sbOpacity -> if (mProperties != null) {
                mProperties!!.onOpacityChanged(i)
            }
            R.id.sbSize -> if (mProperties != null) {
                mProperties!!.onBrushSizeChanged(i, iserase)
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
    interface Properties {
        fun onColorChanged(colorCode: Int)
        fun onOpacityChanged(opacity: Int)
        fun onBrushSizeChanged(brushSize: Int, iserase: Boolean)
    }

    companion object {
        @JvmField
        var iserase = false
    }
}