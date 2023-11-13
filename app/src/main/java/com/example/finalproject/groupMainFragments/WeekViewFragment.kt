package com.example.finalproject.groupMainFragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.databinding.FragmentWeekViewBinding
import com.example.finalproject.fragments.AddTaskPopUpFragment
import com.example.finalproject.fragments.EditTextPopUpFragment
import com.example.finalproject.utils.AppUtils
import com.example.finalproject.utils.AppUtils.daysInWeekArray
import com.example.finalproject.utils.AppUtils.monthYearFromDate
import com.example.finalproject.utils.DaysRvAdapter
import com.example.finalproject.utils.Group
import com.example.finalproject.utils.Task
import com.example.finalproject.utils.User
import com.example.finalproject.utils.WeeklyTasksRvAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.time.LocalDate
import kotlin.properties.Delegates


class WeekViewFragment : Fragment() ,DaysRvAdapter.onItemsListener,AddTaskPopUpFragment.AddTaskDialogListener,WeeklyTasksRvAdapter.OnTaskClickListener{
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dataBaseRef: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var binding: FragmentWeekViewBinding
    private lateinit var daysRvAdapter :DaysRvAdapter
    private lateinit var taskRvAdapter :WeeklyTasksRvAdapter
    private lateinit var group:Group
    private var isUserAdmin  = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeekViewBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        AppUtils.selectedDate = LocalDate.now()
        group = arguments?.getParcelable<Group>("group")!!
        isUserAdmin = group.admins.contains(firebaseAuth.currentUser!!.uid)
        storageReference = FirebaseStorage.getInstance().reference.child("Groups/" + group.uid+"/" + "Pdfs/")
        dataBaseRef = FirebaseDatabase.getInstance().reference.child("Groups")
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setWeekView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.leftBtn.setOnClickListener {
            AppUtils.selectedDate = AppUtils.selectedDate?.minusWeeks(1)
            setWeekView()
        }
        binding.rightBtn.setOnClickListener {
            AppUtils.selectedDate = AppUtils.selectedDate?.plusWeeks(1)
            setWeekView()
        }
        binding.newTaskBtn.setOnClickListener {
            if (isUserAdmin){
                val popupDialog = AddTaskPopUpFragment("","",AppUtils.selectedDate.toString())
                popupDialog.setAddTaskDialogListener(this)
                popupDialog.show(childFragmentManager, "AddTaskPopUp")
            }else{
                Toast.makeText(context,"only the admins can add tasks!",Toast.LENGTH_SHORT).show()
            }
        }



    }
    private fun setWeekView() {
        binding.monthYearTV.setText(monthYearFromDate(AppUtils.selectedDate!!))
        val days: ArrayList<LocalDate> = daysInWeekArray(AppUtils.selectedDate!!)!!
        daysRvAdapter = DaysRvAdapter(days)
        binding.calendarRecyclerView.layoutManager = GridLayoutManager(context, 7)
        binding.calendarRecyclerView.adapter = daysRvAdapter
        daysRvAdapter.setOnItemsListener(this)
        setTaskAdpater()
    }

    private fun setTaskAdpater() {
        val tasksForTheDay = AppUtils.tasksForADay(group.tasksMap,AppUtils.selectedDate)
        taskRvAdapter = WeeklyTasksRvAdapter(tasksForTheDay,isUserAdmin)
        binding.TaskRv.layoutManager =  LinearLayoutManager(context)
        binding.TaskRv.adapter = taskRvAdapter
        taskRvAdapter.setOnTaskClickListener(this)
    }

    override fun onItemClick(date: LocalDate) {
        AppUtils.selectedDate = date
        setWeekView()
    }

    override fun onAddTask(title: String, description: String, date: String, hour: Int, pdfName: String?, pdfUri: Uri?,pdfLink: String?,taskKey:String?) {

        if (taskKey==null){
            if (pdfUri!=null && pdfName!=null){
                val tmpStorage = storageReference.child("$date/"+title+"_"+pdfName)
                pdfUri.let { uri ->
                    tmpStorage.putFile(uri).addOnSuccessListener {
                        tmpStorage.downloadUrl.addOnSuccessListener {downloadUri->
                            dataBaseRef.child(group.uid!!).child("tasksMap").push().key?.let {key->//could make the file saved as the key not that above!!
                                val task = Task(title,description,date,hour, key,pdfName,downloadUri.toString())
                                group.addTask(key,task)
                                dataBaseRef.child(group.uid!!).setValue(group)
                                setTaskAdpater()
                            }
                        }
                    }
                }
            }
            else{
                dataBaseRef.child(group.uid!!).child("tasksMap").push().key?.let { key ->
                    val task = Task(title, description, date, hour,key)
                    group.addTask(key,task)
                    dataBaseRef.child(group.uid!!).setValue(group)
                    setTaskAdpater()
                }
            }
        }else{
            val task = Task(title, description, date, hour,taskKey,pdfName,pdfLink)
            group.removeTask(taskKey)
            group.addTask(taskKey,task)
            dataBaseRef.child(group.uid!!).setValue(group)
            setTaskAdpater()

        }
    }

    override fun onDelete(key: String) {
        group.removeTask(key)
        dataBaseRef.child(group.uid!!).setValue(group)
        setTaskAdpater()
    }

    override fun onEdit(task: Task) {
        val popupDialog = AddTaskPopUpFragment(task.title,task.description,task.date,task.atHour.toString(),task.pdfName,task.pdfLink,task.key)
        popupDialog.setAddTaskDialogListener(this)
        popupDialog.show(childFragmentManager, "AddTaskPopUp")
    }


}