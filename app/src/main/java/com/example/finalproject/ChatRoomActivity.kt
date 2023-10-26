package com.example.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.finalproject.databinding.ActivityChatRoomBinding
import com.example.finalproject.fragments.ProfileFragment
import com.example.finalproject.utils.User
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

        firebaseAuth = FirebaseAuth.getInstance()
        val user = intent.getParcelableExtra<User>("user")
        supportActionBar?.title = user?.name
        bundle.putParcelable("user", user)
        profileFragment.arguments = bundle
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,profileFragment)
            commit()
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.app_bar_menu,menu)
//        val adduser = menu?.findItem(R.id.miAddUser)
//        adduser?.isVisible = false
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when(item.itemId){
//            R.id.miProfile ->{
//                val user = intent.getParcelableExtra<User>("user")
//                supportActionBar?.title = user?.name
//                bundle.putParcelable("user", user)
//                profileFragment.arguments = bundle
//                supportFragmentManager.beginTransaction().apply {
//                    replace(R.id.flFragment,profileFragment)
//                    commit()
//                }
//            }
//            R.id.miSignOut ->{
//                firebaseAuth.signOut()
//                val intent = Intent(this@ChatRoomActivity, MainActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                startActivity(intent)
//                finish()
//
//            }
//        }
//
//
//        return true
//    }
}