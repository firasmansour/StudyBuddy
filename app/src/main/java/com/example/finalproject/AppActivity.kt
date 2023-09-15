package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.finalproject.databinding.ActivityAppBinding
import com.example.finalproject.databinding.FragmentHomeBinding
import com.example.finalproject.fragments.AssignmentsFragment
import com.example.finalproject.fragments.HomeFragment
import com.example.finalproject.fragments.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AppActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityAppBinding
    private lateinit var dataBaseRef: DatabaseReference
    val profileFragment = ProfileFragment()
    val homeFragment = HomeFragment()
    val assignmentsFragment = AssignmentsFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataBaseRef = FirebaseDatabase.getInstance().reference.child("Users")


        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,homeFragment)
            commit()
        }

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
        val firbaseUser = firebaseAuth.currentUser
        if (firbaseUser != null){
            dataBaseRef.child(firbaseUser.uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        // The child node exists in the parent node
                        startActivity(Intent(this@AppActivity, CollectPDataActivity::class.java))
                        finish()

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors that occur
                    Toast.makeText(this@AppActivity, databaseError.message, Toast.LENGTH_SHORT).show()

                }
            })

        }
        else{
            startActivity(Intent(this@AppActivity,MainActivity::class.java))
            finish()
        }
    }

}