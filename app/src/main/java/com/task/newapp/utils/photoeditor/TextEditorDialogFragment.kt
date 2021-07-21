package com.task.newapp.utils.photoeditor

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.task.newapp.App
import com.task.newapp.App.Companion.setFont
import com.task.newapp.R
import java.util.*

/**
 * Created by Burhanuddin Rashid on 1/16/2018.
 */
class TextEditorDialogFragment : DialogFragment() {
    var fontstyle_tv: TextView? = null
    var fontcounter = 0
    var font: String? = null
    private var mAddTextEditText: EditText? = null
    private var mAddTextDoneTextView: TextView? = null
    private var mInputMethodManager: InputMethodManager? = null
    private var mColorCode = 0
    private var mTextEditor: TextEditor? = null
    private var allfonts: List<FontValue>? = null
    override fun onStart() {
        super.onStart()
        val dialog = dialog
        //Make dialog full screen with transparent background
        if (dialog != null) {
            val width: Int = ViewGroup.LayoutParams.MATCH_PARENT
            val height: Int = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_text_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allfonts = defaultFonts
        mAddTextEditText = view.findViewById<EditText>(R.id.add_text_edit_text)
        mInputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mAddTextDoneTextView = view.findViewById<TextView>(R.id.add_text_done_tv)
        fontstyle_tv = view.findViewById<TextView>(R.id.fontstyle_tv)
        if (editview != null) {
            for (i in allfonts!!.indices) {
                if (editview == allfonts!![i].typeface) {
                    font = allfonts!![i].name
                    fontcounter = i
                }
            }
            mAddTextEditText!!.setTypeface(editview)
        } else {
            mAddTextEditText!!.setTypeface(allfonts!![fontcounter].typeface)
            font = allfonts!![0].name
        }
        fontstyle_tv!!.setText(font!!.split("\\.").toTypedArray()[0])
        fontstyle_tv!!.setOnClickListener(View.OnClickListener {
            if (fontcounter < 14) {
                fontcounter += 1
            } else {
                fontcounter = 0
            }
            font = allfonts!![fontcounter].name
            fontstyle_tv!!.setText(font!!.split("\\.").toTypedArray()[0])
            mAddTextEditText!!.setTypeface(setFont(requireContext(), font))
        })
        //Setup the color picker for text color
        val addTextColorPickerRecyclerView: RecyclerView =
            view.findViewById(R.id.add_text_color_picker_recycler_view)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        addTextColorPickerRecyclerView.setLayoutManager(layoutManager)
        addTextColorPickerRecyclerView.setHasFixedSize(true)
        val colorPickerAdapter = ColorPickerAdapter(requireActivity())
        //This listener will change the text color when clicked on any color from picker
        colorPickerAdapter.setOnColorPickerClickListener(object :
            ColorPickerAdapter.OnColorPickerClickListener {
            override fun onColorPickerClickListener(colorCode: Int) {
                mColorCode = colorCode
                mAddTextEditText!!.setTextColor(colorCode)
            }
        })
        addTextColorPickerRecyclerView.setAdapter(colorPickerAdapter)
        mAddTextEditText!!.setText(requireArguments().getString(EXTRA_INPUT_TEXT))
        mColorCode = requireArguments().getInt(EXTRA_COLOR_CODE)
        mAddTextEditText!!.setTextColor(mColorCode)
        //        mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        //Make a callback on activity when user is done with text editing
        mAddTextDoneTextView!!.setOnClickListener(View.OnClickListener { view ->
            mInputMethodManager!!.hideSoftInputFromWindow(view.windowToken, 0)
            dismiss()
            val inputText: String = mAddTextEditText!!.getText().toString()
            if (!TextUtils.isEmpty(inputText) && mTextEditor != null) {
                mTextEditor!!.onDone(inputText, mColorCode, setFont(requireContext(), font))
            }
        })
    }

    //Callback to listener if user is done with text editing
    fun setOnTextEditorListener(textEditor: TextEditor?) {
        mTextEditor = textEditor
    }

    interface TextEditor {
        fun onDone(inputText: String?, colorCode: Int, font: Typeface?)
    }

    companion object {
        val TAG = TextEditorDialogFragment::class.java.simpleName
        const val EXTRA_INPUT_TEXT = "extra_input_text"
        const val EXTRA_COLOR_CODE = "extra_color_code"
        const val EXTRA_FONT_CODE = "extra_font_code"
        var editview: Typeface? = null

        //Show dialog with provide text and text color
        //Show dialog with default text input as empty and text color white
        @JvmStatic
        @JvmOverloads
        fun show(
            appCompatActivity: AppCompatActivity,
            inputText: String =
                "",
            @ColorInt colorCode: Int = ContextCompat.getColor(appCompatActivity, R.color.white),
            view: Typeface? = null
        ): TextEditorDialogFragment {
            val args = Bundle()
            args.putString(EXTRA_INPUT_TEXT, inputText)
            args.putInt(EXTRA_COLOR_CODE, colorCode)
            val fragment = TextEditorDialogFragment()
            fragment.arguments = args
            fragment.show(appCompatActivity.getSupportFragmentManager(), TAG)
            editview = view
            return fragment
        }

        val defaultFonts: List<FontValue>
            get() {
                val fontstylelist = ArrayList<FontValue>()
                fontstylelist.add(
                    FontValue(
                        "arial.ttf",
                        App.setFont(App.getAppInstance().applicationContext, "arial.ttf")!!
                    )
                )
                fontstylelist.add(
                    FontValue(
                        "arizonia.ttf",
                        App.setFont(App.getAppInstance().applicationContext, "arizonia.ttf")!!
                    )
                )
                fontstylelist.add(
                    FontValue(
                        "athalia.ttf",
                        App.setFont(App.getAppInstance().applicationContext, "athalia.ttf")!!
                    )
                )
                fontstylelist.add(
                    FontValue(
                        "autography.otf",
                        App.setFont(App.getAppInstance().applicationContext, "autography.otf")!!
                    )
                )
                fontstylelist.add(
                    FontValue(
                        "avenir.ttf",
                        App.setFont(App.getAppInstance().applicationContext, "avenir.ttf")!!
                    )
                )
                fontstylelist.add(
                    FontValue(
                        "bebas.ttf",
                        App.setFont(App.getAppInstance().applicationContext, "bebas.ttf")!!
                    )
                )
                fontstylelist.add(
                    FontValue(
                        "bodoni.ttf",
                        App.setFont(App.getAppInstance().applicationContext, "bodoni.ttf")!!
                    )
                )
                fontstylelist.add(
                    FontValue(
                        "cinzel_decorative.ttf",
                        App.setFont(App.getAppInstance().applicationContext, "cinzel_decorative.ttf")!!
                    )
                )
                fontstylelist.add(
                    FontValue(
                        "cormorant.otf",
                        App.setFont(App.getAppInstance().applicationContext, "cormorant.otf")!!
                    )
                )
                fontstylelist.add(
                    FontValue(
                        "dancing_script.ttf",
                        App.setFont(App.getAppInstance().applicationContext, "dancing_script.ttf")!!
                    )
                )
                fontstylelist.add(
                    FontValue(
                        "edwardian.ttf",
                        App.setFont(App.getAppInstance().applicationContext, "edwardian.ttf")!!
                    )
                )
                fontstylelist.add(
                    FontValue(
                        "erase_demi.ttf",
                        App.setFont(App.getAppInstance().applicationContext, "erase_demi.ttf")!!
                    )
                )
                fontstylelist.add(
                    FontValue(
                        "exotc350.ttf",
                        App.setFont(App.getAppInstance().applicationContext, "exotc350.ttf")!!
                    )
                )
                fontstylelist.add(
                    FontValue(
                        "gadugi.ttf",
                        App.setFont(App.getAppInstance().applicationContext, "gadugi.ttf")!!
                    )
                )
                fontstylelist.add(
                    FontValue(
                        "Inter_ui.ttf",
                        App.setFont(App.getAppInstance().applicationContext, "Inter_ui.ttf")!!
                    )
                )
                return fontstylelist
            }
    }
}