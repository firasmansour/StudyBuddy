package com.example.finalproject.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentAddUserPopUpBinding
import com.example.finalproject.databinding.FragmentEditTextPopUpBinding


class AddUserPopUpFragment : DialogFragment() {
    private lateinit var binding: FragmentAddUserPopUpBinding

    private var listener: AddUserDialogListener? = null

    fun setAddUserDialogListener(listener: AddUserDialogListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddUserPopUpBinding.inflate(inflater,container,false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        binding.CloseBtn.setOnClickListener{
            dismiss()
        }

        binding.AddBtn.setOnClickListener {

            val friendEmail = binding.emailEt.text.toString()

            // Notify the listener with the edited name
            listener?.onAdd(friendEmail)

            dismiss()
        }
    }

    interface AddUserDialogListener {
        fun onAdd(friendEmail: String)
    }
}