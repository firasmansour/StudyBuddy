package com.example.finalproject.groupMainFragments

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentGroupProfileBinding
import com.example.finalproject.databinding.FragmentGroupTasksBinding
import com.example.finalproject.utils.AppUtils
import com.example.finalproject.utils.Group
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class GroupProfileFragment : Fragment() ,GroupEditPopUpFragment.EditGroupInfoDialogListener{
    private lateinit var binding: FragmentGroupProfileBinding
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var group: Group
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupProfileBinding.inflate(inflater,container,false)
        firebaseAuth = FirebaseAuth.getInstance()
        group = arguments?.getParcelable<Group>("group")!!

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvName.setText(group.name)
        binding.TVdiscription.setText(group.description)
        binding.membersNum.setText(group.members.size.toString())
        AppUtils.fetchUserFromFirebase(group.owner!!){user->
            if (user != null){
                binding.ownerNameTv.text = user.name
                binding.tvOwnerEmail.text = user.email
            }
        }
        if (group.admins.contains(firebaseAuth.currentUser?.uid)){
            binding.editProfileIcon.visibility = View.VISIBLE
            binding.editProfileIcon.setOnClickListener {
                //edit
                val editGroup = GroupEditPopUpFragment(group.name,group.description)
                editGroup.setEditGroupInfoDialogListener(this)
                editGroup.show(requireActivity().supportFragmentManager, "GroupEditPopUp")
            }
            binding.groupImg.setOnClickListener{
                uploadProfilePic()
            }
        }
        getGroupPic(group.uid.toString())



    }

    private fun getGroupPic(groupUid:String) {
        storageReference = FirebaseStorage.getInstance().reference.child("Groups/$groupUid/GroupImage.jpg")
        val localFile = File.createTempFile("tempImage", "jpg")
        storageReference.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.groupImg.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Toast.makeText(context, "there is no profile pic!", Toast.LENGTH_SHORT).show()
        }

    }
    private fun uploadProfilePic() {
        Intent(Intent.ACTION_GET_CONTENT).also {
            it.type = "image/jpg"
            startActivityForResult(it, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK){

            storageReference = FirebaseStorage.getInstance().getReference("Groups/${group.uid}/GroupImage.jpg")
            storageReference.putFile(data?.data!!).addOnCompleteListener{
                if (it.isSuccessful){
                    Toast.makeText(context, "the image has been uploaded successfully!", Toast.LENGTH_SHORT).show()
                    val localFile = File.createTempFile("tempImage","jpg")
                    storageReference.getFile(localFile).addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                        binding.groupImg.setImageBitmap(bitmap)
                    }.addOnFailureListener{
                        Toast.makeText(context, "failed to retrive the image!!", Toast.LENGTH_SHORT).show()

                    }
                }
                else{
                    Toast.makeText(context, "somthing went wrong the image didnt upload", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun onEditGroup(newGroupName: String, newDescription: String) {
        val dataBaseRef = FirebaseDatabase.getInstance().reference.child("Groups")
        if (group != null) {
            if (newGroupName.isNotEmpty()){
                group.name = newGroupName
                binding.tvName.text = newGroupName
            }
            if (newDescription.isNotEmpty()){
                group.description = newDescription
                binding.TVdiscription.text = newDescription
            }

            dataBaseRef.child(group.uid!!).setValue(group).addOnSuccessListener {
                Toast.makeText(context,"profile edited successfully",Toast.LENGTH_SHORT).show()
            }
        }
    }



}