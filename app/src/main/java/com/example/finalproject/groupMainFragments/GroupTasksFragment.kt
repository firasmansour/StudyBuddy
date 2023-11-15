package com.example.finalproject.groupMainFragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.GroupRoomActivity
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentGroupTasksBinding
import com.example.finalproject.fragments.AddTaskPopUpFragment
import com.example.finalproject.utils.AppUtils
import com.example.finalproject.utils.Group
import com.example.finalproject.utils.Task
import com.example.finalproject.utils.WeeklyTasksRvAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class GroupTasksFragment(groupId : String?) : Fragment (),ShowTaskPopUpFragment.ShowPdfDialogListener,AddTaskPopUpFragment.AddTaskDialogListener,WeeklyTasksRvAdapter.OnTaskClickListener{

    private lateinit var binding: FragmentGroupTasksBinding
    private lateinit var tasksRvAdapter : WeeklyTasksRvAdapter
    private val groupuid = groupId
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dataBaseRef:DatabaseReference
    private lateinit var userUid:String
//    private  var group: Group?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupTasksBinding.inflate(inflater,container,false)
        firebaseAuth = FirebaseAuth.getInstance()
        dataBaseRef = FirebaseDatabase.getInstance().reference.child("Groups")
        userUid = firebaseAuth.currentUser!!.uid

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTaskAdpater()
    }

    private fun setTaskAdpater() {
        AppUtils.fetchGroupFromFirebase(groupuid!!){group ->
            tasksRvAdapter = WeeklyTasksRvAdapter(group!!.tasksMap.values.toMutableList(),group.admins.contains(userUid))
            binding.rvTasks.layoutManager =  LinearLayoutManager(context)
            binding.rvTasks.adapter = tasksRvAdapter
            tasksRvAdapter.setOnTaskClickListener(this)
            tasksRvAdapter.onItemClick={
                val popupDialog = ShowTaskPopUpFragment(it.title,it.description,it.date,it.pdfName,it.pdfLink)
                popupDialog.setShowPdfDialogListener(this)
                popupDialog.show(childFragmentManager, "ShowTaskPopUp")
            }
        }
    }

    override fun onShowPdf(pdfName: String, pdfLink: String) {
        val pdfViewerFragment = PdfViewerFragment(pdfName,pdfLink)
        (activity as? GroupRoomActivity)?.setCurrFragment(pdfViewerFragment)
    }

    override fun onAddTask(title: String, description: String, date: String, hour: Int, pdfName: String?, pdfUri: Uri?,pdfLink: String?,taskKey:String?){
     AppUtils.fetchGroupFromFirebase(groupuid!!){group ->
         val storageReference = FirebaseStorage.getInstance().reference.child("Groups/" + groupuid+"/" + "Pdfs/")
         val tmpStorage = storageReference.child("$date/"+title+"_"+pdfName)
         if (pdfUri!=null){
             pdfUri.let { uri ->
                 tmpStorage.putFile(uri).addOnSuccessListener {
                     tmpStorage.downloadUrl.addOnSuccessListener {downloadUri->
                         val task = Task(title, description, date, hour,taskKey,pdfName,downloadUri.toString())
                         group!!.removeTask(taskKey!!)
                         group.addTask(taskKey,task)
                         dataBaseRef.child(group.uid!!).setValue(group)
                         setTaskAdpater()
                     }
                 }
             }
         }else{
             val task = Task(title, description, date, hour,taskKey,pdfName,pdfLink)
             group!!.removeTask(taskKey!!)
             group.addTask(taskKey,task)
             dataBaseRef.child(group.uid!!).setValue(group)
             setTaskAdpater()
         }
     }
    }

    override fun onDelete(key: String) {
        AppUtils.fetchGroupFromFirebase(groupuid!!){group ->
            group!!.removeTask(key)
            dataBaseRef.child(group.uid!!).setValue(group)
            setTaskAdpater()
        }
    }

    override fun onEdit(task: Task) {
        val popupDialog = AddTaskPopUpFragment(task.title,task.description,task.date,task.atHour.toString(),task.pdfName,task.pdfLink,task.key)
        popupDialog.setAddTaskDialogListener(this)
        popupDialog.show(childFragmentManager, "AddTaskPopUp")
    }


}