package com.example.finalproject

import android.content.ClipData
import android.content.ClipboardManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.replace
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.finalproject.databinding.ActivityChatRoomBinding
import com.example.finalproject.databinding.ActivityGroupRoomBinding
import com.example.finalproject.fragments.AssignmentsFragment
import com.example.finalproject.fragments.ProfileFragment
import com.example.finalproject.groupMainFragments.GroupProfileFragment
import com.example.finalproject.groupMainFragments.GroupTasksFragment
import com.example.finalproject.groupMainFragments.WeekViewFragment
import com.example.finalproject.utils.Group
import com.example.finalproject.utils.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class GroupRoomActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityGroupRoomBinding
    private lateinit var dataBaseRef: DatabaseReference

    val weekViewFragment = WeekViewFragment()
    val groupProfileFragment = GroupProfileFragment()
    private lateinit var  tasksFragment :GroupTasksFragment

    val bundle = Bundle()
    private lateinit var group: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        group = intent.getParcelableExtra<Group>("group")!!
        supportActionBar?.title = group.name
        tasksFragment = GroupTasksFragment(group.uid)
        bundle.putParcelable("group", group)
        weekViewFragment.arguments = bundle
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,weekViewFragment)
            commit()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.group_bar_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.miProfile ->setCurrFragment(groupProfileFragment)
            R.id.miGroupId ->{
                val groupCode = group.uid.toString()
                val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("groupCode", groupCode)
                editor.apply()
            }
            R.id.miTasks ->{
                setCurrFragment(tasksFragment)
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
}