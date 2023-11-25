package com.example.finalproject.groupMainFragments

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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class GroupProfileFragment : Fragment() {
    private lateinit var binding: FragmentGroupProfileBinding
    private lateinit var storageReference: StorageReference
    private lateinit var group: Group
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupProfileBinding.inflate(inflater,container,false)
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
}