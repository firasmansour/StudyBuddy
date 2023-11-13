package com.example.finalproject.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.AppActivity
import com.example.finalproject.GroupRoomActivity
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentAssignmentsBinding
import com.example.finalproject.groupMainFragments.PdfViewerFragment
import com.example.finalproject.groupMainFragments.ShowTaskPopUpFragment
import com.example.finalproject.utils.AppUtils
import com.example.finalproject.utils.Group
import com.example.finalproject.utils.Task
import com.example.finalproject.utils.User
import com.example.finalproject.utils.WeeklyTasksRvAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AssignmentsFragment : Fragment() ,ShowTaskPopUpFragment.ShowPdfDialogListener{
    private lateinit var binding : FragmentAssignmentsBinding
    private lateinit var allUserTasks:MutableList<Task>
    private lateinit var tasksRvAdapter : WeeklyTasksRvAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dataBaseRef :DatabaseReference
    private lateinit var userUid:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAssignmentsBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        dataBaseRef = FirebaseDatabase.getInstance().reference.child("Groups")
        allUserTasks = mutableListOf()
        userUid = firebaseAuth.currentUser!!.uid

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTaskAdpater()

    }

    private fun getAllUserTasks(){
        dataBaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allUserTasks.clear()
                for (groupSnapshot in snapshot.children) {
                    val group = groupSnapshot.getValue(Group::class.java)
                    if (group!!.members.contains(userUid)) {
                        allUserTasks.addAll(group.tasksMap.values.toMutableList())
                    }
                }
                tasksRvAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
            }


        })
    }
    private fun setTaskAdpater() {

        tasksRvAdapter = WeeklyTasksRvAdapter(allUserTasks,false)
        binding.rvTasks.layoutManager =  LinearLayoutManager(context)
        binding.rvTasks.adapter = tasksRvAdapter
        getAllUserTasks()
        tasksRvAdapter.onItemClick={
            val popupDialog = ShowTaskPopUpFragment(it.title,it.description,it.date,it.pdfName,it.pdfLink)
            popupDialog.setShowPdfDialogListener(this)
            popupDialog.show(childFragmentManager, "ShowTaskPopUp")
        }

    }

    override fun onShowPdf(pdfName: String, pdfLink: String) {
        val pdfViewerFragment = PdfViewerFragment(pdfName,pdfLink)
        (activity as? AppActivity)?.setCurrFragment(pdfViewerFragment)
    }

}