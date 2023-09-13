package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.finalproject.databinding.ActivityAppBinding
import com.example.finalproject.databinding.FragmentHomeBinding
import com.example.finalproject.fragments.AssignmentsFragment
import com.example.finalproject.fragments.HomeFragment
import com.example.finalproject.fragments.ProfileFragment
import com.google.firebase.auth.FirebaseAuth

class AppActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityAppBinding
    val profileFragment = ProfileFragment()
    val homeFragment = HomeFragment()
    val assignmentsFragment = AssignmentsFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCurrFragment(homeFragment)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.myGroups -> setCurrFragment(homeFragment)
                R.id.myProfile -> setCurrFragment(profileFragment)
                R.id.myAssignments -> setCurrFragment(assignmentsFragment)
            }
            true
        }


        firebaseAuth = FirebaseAuth.getInstance()

        checkUser()
        binding.tmp.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }
    }

    private fun setCurrFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            addToBackStack(null)
            commit()
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