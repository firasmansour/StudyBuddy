package com.example.finalproject.fragments

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.DialogFragment
import com.example.finalproject.databinding.FragmentAddTaskPopUpBinding
import com.example.finalproject.utils.AppUtils
import java.time.LocalTime
import java.util.Calendar


class AddTaskPopUpFragment(private val origTitle: String?="",
                           private val origDescription: String?="",
                           private val origDate: String?="",
                           private val origTime: String?="",
                           private val origpdfName:String?="No Pdf Yet",
                           private val pdfLink:String?=null,
                           private val taskKey:String?=null) : DialogFragment() {
    private lateinit var binding : FragmentAddTaskPopUpBinding
    private var pdfFileUri: Uri? = null
    private var pdfname: String? = null
    private var hour: Int = 0
    private var min: Int = 0

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
        binding.fileName.setText(origpdfName)
        pdfname = origpdfName
        binding.Close.setOnClickListener{
            dismiss()
        }
        if (origTime!=""){
            val tmpTime = LocalTime.parse(origTime)
            binding.tvTime.text = tmpTime.hour.toString() + ":" + tmpTime.minute.toString()
            hour = tmpTime.hour
            min = tmpTime.minute
        }

        binding.pdfFile.setOnClickListener {
            launcher.launch("application/pdf")
        }
        binding.timePicker.setOnClickListener {
            if (origTime!= ""){
                val tmpTime = LocalTime.parse(origTime)
                openTimePicker(tmpTime.hour,tmpTime.minute)
            }else{
                openTimePicker(Calendar.getInstance().get(Calendar.HOUR),Calendar.getInstance().get(Calendar.MINUTE))
            }
        }

        binding.saveBtn.setOnClickListener {
            val title = binding.taskTitleEt.text.toString()
            val description = binding.taskDescriptionEt.text.toString()
            val date = binding.dateEt.text.toString()


            if (AppUtils.isDateValid(requireContext(),date)){
                if (hour in 6..22){
                    val tmpTime = LocalTime.of(hour,min).toString()
                    listener?.onAddTask(title,description,date,tmpTime, pdfname, pdfFileUri,pdfLink,taskKey)
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

    private fun openTimePicker(initHour:Int,initMin:Int) {
        val calendar: Calendar = Calendar.getInstance()


        val timePickerDialog = TimePickerDialog(requireContext(),
            OnTimeSetListener { view, hourOfDay, minute ->
                min = minute ; hour = hourOfDay;binding.tvTime.text = hourOfDay.toString() + ":" + minute.toString()
                              },
            initHour,
            initMin,
            false
        )
        timePickerDialog.show()
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        pdfFileUri = uri
        pdfname = uri?.let { DocumentFile.fromSingleUri(requireContext(), it)?.name }

        binding.fileName.text = pdfname.toString()
    }

//    private fun checkHourInput(hour:Int):Boolean{
//        return try {
//            val tmp = hour.toInt()
//            tmp in 6..24
//
//        }catch (e: NumberFormatException){
//
//            false
//        }
//    }

    interface AddTaskDialogListener {
        fun onAddTask(title: String,description: String,date: String,time:String,pdfName:String?=null,pdfUri:Uri?=null,pdfLink: String?=null,taskKey:String?=null)
    }
}