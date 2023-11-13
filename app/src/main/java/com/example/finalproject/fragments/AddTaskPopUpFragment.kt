package com.example.finalproject.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.DialogFragment
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentAddTaskPopUpBinding
import com.example.finalproject.utils.AppUtils

class AddTaskPopUpFragment(private val origTitle: String?="",
                           private val origDescription: String?="",
                           private val origDate: String?="",
                           private val origHour: String?="",
                           private val origpdfName:String?="No Pdf Yet",
                           private val pdfLink:String?=null,
                           private val taskKey:String?=null) : DialogFragment() {
    private lateinit var binding : FragmentAddTaskPopUpBinding
    private var pdfFileUri: Uri? = null
    private var pdfname: String? = null

    private var listener: AddTaskDialogListener? = null

    fun setAddTaskDialogListener(listener: AddTaskDialogListener) {
        this.listener = listener
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddTaskPopUpBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.taskTitleEt.setText(origTitle)
        binding.taskDescriptionEt.setText(origDescription)
        binding.dateEt.setText(origDate)
        binding.pickHourEt.setText(origHour)
        binding.fileName.setText(origpdfName)
        pdfname = origpdfName
        binding.Close.setOnClickListener{
            dismiss()
        }

        binding.pdfFile.setOnClickListener {
            launcher.launch("application/pdf")
        }

        binding.saveBtn.setOnClickListener {
            val title = binding.taskTitleEt.text.toString()
            val description = binding.taskDescriptionEt.text.toString()
            val date = binding.dateEt.text.toString()

            val hour = binding.pickHourEt.text.toString()



//            binding.hourSpinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
//                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                    hour = position+1
//
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//                    Toast.makeText(context,"pick an hour for the task",Toast.LENGTH_SHORT).show()
//                }
//
//            }

            if (AppUtils.isDateValid(requireContext(),date)){
                if (checkHourInput(hour)){
                    listener?.onAddTask(title,description,date,hour.toInt(), pdfname, pdfFileUri,pdfLink,taskKey)
                    dismiss()
                }else{
                    Toast.makeText(context,"enter a valid hour",Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(context,"date is invalid",Toast.LENGTH_SHORT).show()
            }




            binding.taskTitleEt.text = null
            binding.taskDescriptionEt.text = null
            binding.dateEt.text = null
        }


    }
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        pdfFileUri = uri
        pdfname = uri?.let { DocumentFile.fromSingleUri(requireContext(), it)?.name }

        binding.fileName.text = pdfname.toString()
    }

    private fun checkHourInput(hour:String):Boolean{
        return try {
            val tmp = hour.toInt()
            tmp in 6..24

        }catch (e: NumberFormatException){

            false
        }
    }

    interface AddTaskDialogListener {
        fun onAddTask(title: String,description: String,date: String,hour:Int,pdfName:String?=null,pdfUri:Uri?=null,pdfLink: String?=null,taskKey:String?=null)
    }
}