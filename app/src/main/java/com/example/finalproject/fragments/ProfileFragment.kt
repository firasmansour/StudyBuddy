package com.example.finalproject.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.finalproject.CollectPDataActivity
import com.example.finalproject.R
import com.example.finalproject.User
import com.example.finalproject.databinding.ActivityCollectPdataBinding
import com.example.finalproject.databinding.FragmentProfileBinding
import com.example.finalproject.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class ProfileFragment : Fragment() {
    private lateinit var firebaseauth: FirebaseAuth
    private lateinit var binding: FragmentProfileBinding
    private lateinit var dataBaseRef: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var uid:String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        binding =  FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseauth = FirebaseAuth.getInstance()
        uid = firebaseauth.currentUser?.uid.toString()
        dataBaseRef = FirebaseDatabase.getInstance().reference.child("Users")

        if (uid.isNotEmpty()){

            getUserData()
        }

    }

    private fun getUserData() {
        dataBaseRef.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    binding.name.setText(user?.name)
                    binding.userEmail.setText(user?.email)
                    binding.bio.setText(user?.bio)
                    binding.studyField.setText(user?.studyField)
                    getUserPic()
                } else {
                    Toast.makeText(context,"user not found",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun getUserPic() {
        storageReference = FirebaseStorage.getInstance().reference.child("Users/" + "profile_pics/" + uid + ".jpg")
        val localFile = File.createTempFile("tempImage", "jpg")
        storageReference.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.profileImage.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Toast.makeText(context, "there is no profile pic!", Toast.LENGTH_SHORT).show()
        }

    }
}