package com.example.finalproject.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.databinding.FragmentAddGroupSearchListPopUpBinding
import com.example.finalproject.utils.AppUtils
import com.example.finalproject.utils.Group
import com.example.finalproject.utils.GroupsRvAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AddGroupSearchListPopUpFragment : DialogFragment() ,JoinPrivateGroupPopUpFragment.JoinPrivateGroupDialogListener{

    private lateinit var binding: FragmentAddGroupSearchListPopUpBinding
    private lateinit var groupsRvAdapter: GroupsRvAdapter
    private lateinit var publicGroupsList: MutableList<Group>
    private lateinit var dataBaseRef: DatabaseReference
    private lateinit var firebaseauth: FirebaseAuth
    private lateinit var filteredList:MutableList<Group>

    private var listener: AddGroupDialogListener? = null

    fun setAddGroupDialogListener(listener: AddGroupDialogListener) {
        this.listener = listener
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddGroupSearchListPopUpBinding.inflate(inflater,container,false)
        binding.rvPublicGroups.layoutManager = LinearLayoutManager(context)
        publicGroupsList = mutableListOf()
        groupsRvAdapter = GroupsRvAdapter(publicGroupsList)
        binding.rvPublicGroups.adapter = groupsRvAdapter

        dataBaseRef = FirebaseDatabase.getInstance().reference.child("Groups")
        firebaseauth = FirebaseAuth.getInstance()

        getPublicGroupsFromFirebase()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.CloseBtn.setOnClickListener {
            dismiss()
        }

        groupsRvAdapter.onItemClick = {
            listener?.onAddGroup(it)
            dismiss()
        }
        binding.joinGroup.setOnClickListener {
            val popupDialog = JoinPrivateGroupPopUpFragment()
            popupDialog.setJoinPrivateGroupDialogListener(this)
            popupDialog.show(childFragmentManager, "joinPrivateGroupPopUp")
        }

        binding.searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText.toString())
                return true
            }
        })

    }

    private fun filterList(text: String) {
        var filteredList:MutableList<Group> = mutableListOf()

        for (group in publicGroupsList){
            if (group.name!!.toLowerCase().contains(text.toLowerCase())){
                filteredList.add(group)
            }
        }
        groupsRvAdapter.setFilteredList(filteredList)

    }

    interface AddGroupDialogListener{
    fun onAddGroup(group : Group)
}


    private fun getPublicGroupsFromFirebase() {


        dataBaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                publicGroupsList.clear()
                for (groupSnapshot in snapshot.children) {

                    val group = groupSnapshot.getValue(Group::class.java)

                    if (group != null && group.isPublic == 1) {

                        publicGroupsList.add(group)
                    }
                }
                groupsRvAdapter.notifyDataSetChanged()



            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
            }


        })

    }

    override fun onJoin(groupUid: String) {

        AppUtils.fetchGroupFromFirebase(requireContext(), groupUid) {
            listener?.onAddGroup(it!!)
            dismiss()
        }

    }

}