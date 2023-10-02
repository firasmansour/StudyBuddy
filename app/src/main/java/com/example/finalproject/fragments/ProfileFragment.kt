package com.example.finalproject.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
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

class ProfileFragment : Fragment(),EditTextPopUpFragment.EditInfoDialogListener{
    private lateinit var firebaseauth: FirebaseAuth
    private lateinit var binding: FragmentProfileBinding
    private lateinit var dataBaseRef: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var uid:String
    private var friend: User ?= null
    private lateinit var orgName:String
    private lateinit var orgStudy:String
    private lateinit var orgBio:String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        binding =  FragmentProfileBinding.inflate(inflater, container, false)

        friend = arguments?.getParcelable<User>("user")
        firebaseauth = FirebaseAuth.getInstance()
        uid = firebaseauth.currentUser?.uid.toString()
        dataBaseRef = FirebaseDatabase.getInstance().reference.child("Users")

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        if (friend!= null){
            binding.profileImage.isClickable = false
            binding.edit.visibility = View.GONE

            binding.name.setText(friend?.name)
            binding.userEmail.setText(friend?.email)
            binding.bio.setText(friend?.bio)
            binding.studyField.setText(friend?.studyField)
            getUserPic(friend?.email.toString())
        }
        else if (uid.isNotEmpty()){


            fetchUserFromFirebase(uid) { user ->
                if (user != null) {
                    binding.name.setText(user.name)
                    binding.userEmail.setText(user.email)
                    binding.bio.setText(user.bio)
                    binding.studyField.setText(user.studyField)
                    getUserPic(user.email.toString())
                    orgName = user.name.toString()
                    orgStudy = user.studyField.toString()
                    orgBio = user.bio.toString()

                }
            }

            binding.profileImage.setOnClickListener {
                uploadProfilePic()
            }
            binding.edit.setOnClickListener {
                val popupDialog = EditTextPopUpFragment(orgName,orgStudy,orgBio)
                popupDialog.setEditDialogListener(this)
                popupDialog.show(childFragmentManager, "EditTextPopUp")

            }

    }

    }


    fun fetchUserFromFirebase(uid: String, callback: (User?) -> Unit) {
        dataBaseRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Check if the user exists
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)
                    callback(user)
                } else {
                    callback(null) // User not found
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that may occur during the fetch
                Toast.makeText(context,"user not found",Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }

    private fun getUserPic(email:String) {
        storageReference = FirebaseStorage.getInstance().reference.child("Users/" + "profile_pics/" + email + ".jpg")
        val localFile = File.createTempFile("tempImage", "jpg")
        storageReference.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.profileImage.setImageBitmap(bitmap)
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

            storageReference = FirebaseStorage.getInstance().getReference("Users/"+"profile_pics/" + firebaseauth.currentUser?.email +".jpg")
            storageReference.putFile(data?.data!!).addOnCompleteListener{
                if (it.isSuccessful){
                    Toast.makeText(context, "the image has been uploaded successfully!", Toast.LENGTH_SHORT).show()
                    val localFile = File.createTempFile("tempImage","jpg")
                    storageReference.getFile(localFile).addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                        binding.profileImage.setImageBitmap(bitmap)
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

    override fun onEdited(newName: String, newStudy: String, newBio: String) {
        fetchUserFromFirebase(uid) { user ->
            if (user != null) {
                if (newName.isNotEmpty()){
                    user.name = newName
                    binding.name.text = newName
                }
                if (newStudy.isNotEmpty()){
                    user.studyField = newStudy
                    binding.studyField.text = newStudy
                }
                if (newBio.isNotEmpty()){
                    user.bio = newBio
                    binding.bio.text = newBio
                }
                dataBaseRef.child(uid).setValue(user).addOnSuccessListener {
                    Toast.makeText(context,"profile edited successfully",Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context,"user not found",Toast.LENGTH_SHORT).show()
            }
        }
    }
}