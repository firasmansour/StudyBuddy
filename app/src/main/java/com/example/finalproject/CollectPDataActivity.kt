package com.example.finalproject

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.example.finalproject.databinding.ActivityCollectPdataBinding
import com.example.finalproject.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.net.URI

class CollectPDataActivity : AppCompatActivity() {
    private lateinit var firebaseauth: FirebaseAuth
    private lateinit var binding: ActivityCollectPdataBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var dataBaseRef: DatabaseReference
    private lateinit var imageURI: Uri
    private lateinit var storageReference: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectPdataBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseauth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        dataBaseRef = database.reference.child("Users")


        //picking a profile pic
        binding.profileImage.setOnClickListener{
            uploadProfilePic()
        }

        binding.saveBtn.setOnClickListener {
            val email = intent.getStringExtra("EXTRA_EMAIL").toString()
            val pass = intent.getStringExtra("EXTRA_PASS").toString()
            registerUser(email,pass)
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
            imageURI = data?.data!!
            storageReference = FirebaseStorage.getInstance().getReference("Users/" + firebaseauth.currentUser?.uid +".jpg")
            storageReference.putFile(imageURI).addOnCompleteListener{
                if (it.isSuccessful){
                    Toast.makeText(this@CollectPDataActivity, "the image has been uploaded successfully!", Toast.LENGTH_SHORT).show()
                    val localFile = File.createTempFile("tempImage","jpg")
                    storageReference.getFile(localFile).addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                        binding.profileImage.setImageBitmap(bitmap)
                    }.addOnFailureListener{
                        Toast.makeText(this@CollectPDataActivity, "failed to retrive the image!!", Toast.LENGTH_SHORT).show()

                    }
                }
                else{
                    Toast.makeText(this@CollectPDataActivity, "somthing went wrong the image didnt upload", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun registerUser(email: String, pass: String) {
        firebaseauth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->

            if (task.isSuccessful){
                val uid = firebaseauth.currentUser?.uid
                var name = binding.etName.text.toString()
                val studyField = binding.etStudy.text.toString()
                val bio = binding.etBio.text.toString()
                if (name == null){
                    name = firebaseauth.currentUser?.displayName.toString()
                }
                val user = User(name,studyField,bio)
                if (uid != null){
                    dataBaseRef.child(uid).setValue(user).addOnCompleteListener{
                        if (it.isSuccessful){
                            Toast.makeText(this@CollectPDataActivity,"user saved in database",Toast.LENGTH_SHORT).show()

                            registerUser(email,pass)
                        }
                        else{
                            Toast.makeText(this@CollectPDataActivity,it.exception?.message,Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                startActivity(Intent(this@CollectPDataActivity, AppActivity::class.java))
                finish()
            }
            else
                Toast.makeText(this@CollectPDataActivity, task.exception.toString(), Toast.LENGTH_SHORT).show()

        }
    }

}