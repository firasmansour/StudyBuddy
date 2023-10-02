package com.example.finalproject.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentEditTextPopUpBinding
import com.example.finalproject.databinding.FragmentProfileBinding


class EditTextPopUpFragment(private val originalName: String,private val originalStudy: String,private val originalBio: String) : DialogFragment() {
    private lateinit var binding: FragmentEditTextPopUpBinding

    private var listener: EditInfoDialogListener? = null

    fun setEditNameDialogListener(listener: EditInfoDialogListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentEditTextPopUpBinding.inflate(inflater,container,false)





        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.NameEt.setText(originalName)
        binding.StudyEt.setText(originalStudy)
        binding.BioEt.setText(originalBio)

        binding.Close.setOnClickListener{
            dismiss()
        }

        binding.saveBtn.setOnClickListener {
            val editedName = binding.NameEt.text.toString()
            val editedStudy = binding.StudyEt.text.toString()
            val editedBio = binding.BioEt.text.toString()

            // Notify the listener with the edited name
            listener?.onEdited(editedName,editedStudy,editedBio)
            binding.NameEt.text = null
            binding.StudyEt.text = null
            binding.BioEt.text = null
            dismiss()
        }


    }
    interface EditInfoDialogListener {
        fun onEdited(newName: String,newStudy: String,newBio: String)
    }


}