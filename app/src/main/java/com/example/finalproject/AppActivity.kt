package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.finalproject.databinding.ActivityAppBinding
import com.example.finalproject.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth

class AppActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityAppBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()

        checkUser()
        binding.tmp.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }
    }
    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
            startActivity(Intent(this@AppActivity,MainActivity::class.java))
            finish()
        }
    }

}