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

class AddTaskPopUpFragment : DialogFragment() {
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
            var hour =-1
            binding.hourSpinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    hour = position+1
                    Toast.makeText(context,hour.toString(),Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(context,"pick an hour for the task",Toast.LENGTH_SHORT).show()
                }

            }
            if (AppUtils.isDateValid(requireContext(),date)){

                listener?.onAddTask(title,description,date,hour, pdfname, pdfFileUri)
                dismiss()

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

    interface AddTaskDialogListener {
        fun onAddTask(title: String,description: String,date: String,hour:Int,pdfName:String?=null,pdfUri:Uri?=null)
    }
}