package com.example.finalproject.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentAddGroupSearchListPopUpBinding
import com.example.finalproject.databinding.FragmentJoinPrivateGroupPopUpBinding


class JoinPrivateGroupPopUpFragment : DialogFragment() {
    private lateinit var binding: FragmentJoinPrivateGroupPopUpBinding

    private var listener: JoinPrivateGroupDialogListener? = null

    fun setJoinPrivateGroupDialogListener(listener: JoinPrivateGroupDialogListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentJoinPrivateGroupPopUpBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.CloseBtn.setOnClickListener {
            dismiss()
        }
        binding.joinBtn.setOnClickListener {
            val groupCode = binding.groupCodeEt.text.toString()
            Log.d("OnJoinPressed",groupCode)
            if (groupCode != ""){
                listener?.onJoin(groupCode)
                dismiss()
            }


        }

    }


    interface JoinPrivateGroupDialogListener{
        fun onJoin(groupUid:String)
    }
}