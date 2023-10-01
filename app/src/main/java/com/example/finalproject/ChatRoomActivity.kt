package com.example.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.finalproject.databinding.ActivityAppBinding
import com.example.finalproject.databinding.ActivityChatRoomBinding
import com.example.finalproject.fragments.AssignmentsFragment
import com.example.finalproject.fragments.HomeFragment
import com.example.finalproject.fragments.ProfileFragment
import com.example.finalproject.fragments.SearchUsersFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class ChatRoomActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityChatRoomBinding
    private lateinit var dataBaseRef: DatabaseReference
    val profileFragment = ProfileFragment()
    val bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = intent.getParcelableExtra<User>("user")
        supportActionBar?.title = user?.name
        bundle.putParcelable("user", user)
        profileFragment.arguments = bundle
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,profileFragment)
            commit()
        }
    }
}