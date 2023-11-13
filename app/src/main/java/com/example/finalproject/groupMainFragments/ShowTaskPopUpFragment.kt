package com.example.finalproject.groupMainFragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentShowTaskPopUpBinding
import com.example.finalproject.fragments.AddTaskPopUpFragment
import com.example.finalproject.utils.Task
import com.google.android.play.integrity.internal.t


class ShowTaskPopUpFragment(private val title: String?="",
                            private val description: String?="",
                            private val date: String?="",
                            private val pdfName:String?="",
                            private val pdfLink:String?=null) : DialogFragment() {
    private lateinit var binding : FragmentShowTaskPopUpBinding

    private var listener: ShowPdfDialogListener? = null

    fun setShowPdfDialogListener(listener: ShowPdfDialogListener) {
        this.listener = listener
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShowTaskPopUpBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.titleTextView.text = title
        binding.descriptionTv.text = description
        binding.fileName.text = pdfName

        binding.Close.setOnClickListener {
            dismiss()
        }
        if (pdfLink==null || pdfLink == ""){

            binding.pdfFile.visibility = View.GONE
        }else{
            binding.pdfFile.setOnClickListener {
                listener?.onShowPdf(pdfName.toString(),pdfLink)
                dismiss()
            }
        }


    }
    interface ShowPdfDialogListener{
        fun onShowPdf( pdfName:String, pdfLink: String)

    }


}