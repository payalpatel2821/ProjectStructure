package com.task.newapp.ui.fragments.registration

import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.task.newapp.R
import com.task.newapp.databinding.FragmentRegistrationStep4Binding

class RegistrationStep4Fragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentRegistrationStep4Binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_registration_step4,
            container,
            false
        )
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.edtAccId.isEnabled = false
        binding.ivCopy.setOnClickListener(this)
        binding.tvChangeUsername.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_copy -> {
                val clipboard =
                    requireActivity().getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.text = binding.edtAccId.text.toString()

                Toast.makeText(activity, "How Id copied...", Toast.LENGTH_SHORT)
                    .show()
            }
            R.id.tv_change_username -> {
                binding.edtAccId.isEnabled = true
                binding.edtAccId.isFocusable = true
                binding.edtAccId.requestFocus()
                binding.edtAccId.setSelection(binding.edtAccId.text!!.length)
            }
        }
    }
}