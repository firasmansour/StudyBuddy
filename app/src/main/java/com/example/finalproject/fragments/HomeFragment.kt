package com.example.finalproject.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.ChatRoomActivity
import com.example.finalproject.GroupRoomActivity
import com.example.finalproject.utils.GroupsRvAdapter
import com.example.finalproject.databinding.FragmentHomeBinding
import com.example.finalproject.utils.AppUtils
import com.example.finalproject.utils.Group
import com.example.finalproject.utils.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class HomeFragment : Fragment() ,AddGroupSearchListPopUpFragment.AddGroupDialogListener,CreateNewGroupPopUpFragment.CreateGroupDialogListener{
    private lateinit var dataBaseRef: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: FragmentHomeBinding
    private lateinit var firebaseauth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    private lateinit var groupsRvAdapter: GroupsRvAdapter
    private lateinit var groupsList: MutableList<Group>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentHomeBinding.inflate(inflater, container, false)

        firebaseauth = FirebaseAuth.getInstance()
        dataBaseRef = FirebaseDatabase.getInstance().reference.child("Groups")


        binding.rvGroups.layoutManager = LinearLayoutManager(context)

        groupsList = mutableListOf()
        groupsRvAdapter = GroupsRvAdapter(groupsList)
        binding.rvGroups.adapter = groupsRvAdapter
        return binding.root

    }

    override fun onResume() {
        super.onResume()
        getUserGroupsFromFirebase()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.addGroup.setOnClickListener {
            val popupDialog = AddGroupSearchListPopUpFragment()
            popupDialog.setAddGroupDialogListener(this)
            popupDialog.show(childFragmentManager, "addGroupPopUp")
        }

        binding.createGroup.setOnClickListener {
            val popupDialog2 = CreateNewGroupPopUpFragment()
            popupDialog2.setCreateGroupDialogListener(this)
            popupDialog2.show(childFragmentManager, "createNewGroupPopUp")
        }

        groupsRvAdapter.onItemClick = {
            val intent = Intent(requireActivity(), GroupRoomActivity::class.java)
            intent.putExtra("group",it)
            startActivity(intent)
        }



    }

    private fun getUserGroupsFromFirebase() {


        dataBaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                AppUtils.fetchUserFromFirebase(firebaseauth.currentUser?.uid.toString()){
                    groupsList.clear()
                    for (groupSnapshot in snapshot.children) {
                        if (it!!.groupsList.contains(groupSnapshot.key)){
                            val group = groupSnapshot.getValue(Group::class.java)

                            if (group != null) {

                                groupsList.add(group)
                            }
                        }
                    }
                    groupsRvAdapter.notifyDataSetChanged()
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
            }


        })

    }




    override fun onAddGroup(group : Group) {
        val groupUid = group.uid.toString()
        val tmp = FirebaseDatabase.getInstance().reference.child("Users")
        val userUid = firebaseauth.currentUser?.uid.toString()
        AppUtils.fetchUserFromFirebase(userUid){

            if (!it!!.groupsList.contains(groupUid)){
                it.groupsList.add(groupUid)
                tmp.child(userUid).setValue(it)
                groupsList.add(group)
                groupsRvAdapter.notifyItemInserted(groupsList.size-1)

                //add user to group members list
                if (!group.members.contains(userUid)){
                    group.addMember(userUid)
                    dataBaseRef.child(groupUid).setValue(group)
                }


                Toast.makeText(context,"group added successfully",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"you already in this group",Toast.LENGTH_SHORT).show()
            }


        }
    }

    override fun onCreateNewGroup(groupName: String, isPublic: Int, description: String , imageURI: Uri) {
        val key = dataBaseRef.push().key
        val userUid = firebaseauth.currentUser?.uid.toString()
        val group = Group(groupName,isPublic,key,description)
        val tmp = FirebaseDatabase.getInstance().reference.child("Users")
        group.addAdmin(firebaseauth.currentUser?.uid.toString())
        group.owner = firebaseauth.currentUser?.uid.toString()
        group.addMember(firebaseauth.currentUser?.uid.toString())
        dataBaseRef.child(key.toString()).setValue(group).addOnCompleteListener {
            if (it.isSuccessful){
                //add group to user group list
                AppUtils.fetchUserFromFirebase(userUid){

                    it!!.groupsList.add(key.toString())
                    tmp.child(userUid).setValue(it)

                }

                groupsList.add(group)
                groupsRvAdapter.notifyItemInserted(groupsList.size-1)
                Toast.makeText(context,"group added successfully!" , Toast.LENGTH_SHORT).show()
                storageReference = FirebaseStorage.getInstance().getReference("Groups/"+key.toString()+"/" + "GroupImage"+".jpg")
                storageReference.putFile(imageURI).addOnCompleteListener{ task ->
                    if (task.isSuccessful){
                        Toast.makeText(context, "the image has been uploaded successfully!", Toast.LENGTH_SHORT).show()

                    }
                    else{
                        Toast.makeText(context, "somthing went wrong the image didnt upload", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            else {
                Toast.makeText(context, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }


    }



}