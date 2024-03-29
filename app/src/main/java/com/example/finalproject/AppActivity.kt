package com.example.finalproject

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.finalproject.databinding.ActivityAppBinding
import com.example.finalproject.fragments.AssignmentsFragment
import com.example.finalproject.fragments.HomeFragment
import com.example.finalproject.fragments.ProfileFragment
import com.example.finalproject.fragments.UserFriendsFragment
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
    val UserFriendsFragment = UserFriendsFragment()

    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val groupCode = sharedPreferences.getString("groupCode", "")
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("groupCode", groupCode)
        clipboardManager.setPrimaryClip(clipData)
//        val text = clipboardManager.primaryClip?.getItemAt(0)?.text.toString()
//        Toast.makeText(this,text,Toast.LENGTH_SHORT).show()

    }
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
                R.id.miUsers -> setCurrFragment(UserFriendsFragment)
                R.id.myAssignments -> setCurrFragment(assignmentsFragment)
            }
            true
        }


        firebaseAuth = FirebaseAuth.getInstance()

        checkUser()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.miProfile -> setCurrFragment(profileFragment)
            R.id.miSignOut ->{
                firebaseAuth.signOut()
                checkUser()
            }
        }


        return true
    }

    fun setCurrFragment(fragment: Fragment){
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