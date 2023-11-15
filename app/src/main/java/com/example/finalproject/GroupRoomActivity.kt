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
import com.example.finalproject.groupMainFragments.GroupMembersFragment
import com.example.finalproject.groupMainFragments.GroupProfileFragment
import com.example.finalproject.groupMainFragments.GroupTasksFragment
import com.example.finalproject.groupMainFragments.WeekViewFragment
import com.example.finalproject.utils.AppUtils
import com.example.finalproject.utils.Group
import com.example.finalproject.utils.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class GroupRoomActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityGroupRoomBinding
    private lateinit var dataBaseRef: DatabaseReference
    private lateinit var storageReference: StorageReference

    private val weekViewFragment = WeekViewFragment()
    private val groupProfileFragment = GroupProfileFragment()
    private val groupMembersFragment = GroupMembersFragment()
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
        tasksFragment = GroupTasksFragment(group.uid!!)
        bundle.putParcelable("group", group)
        weekViewFragment.arguments = bundle
        groupMembersFragment.arguments = bundle
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
            R.id.miTasks ->setCurrFragment(tasksFragment)
            R.id.miMembers->setCurrFragment(groupMembersFragment)
            R.id.miLeave->{
                val dataBaseGroupRef = FirebaseDatabase.getInstance().reference.child("Groups")
                val dataBaseUserRef = FirebaseDatabase.getInstance().reference.child("Users")
                val userUid = firebaseAuth.currentUser?.uid.toString()

                dataBaseUserRef.child(userUid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        if (dataSnapshot.exists()) {
                            val user = dataSnapshot.getValue(User::class.java)
                            user!!.groupsList.remove(group.uid)
                            dataBaseUserRef.child(userUid).setValue(user)
                            dataBaseGroupRef.child(group.uid!!).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {

                                    if (dataSnapshot.exists()) {
                                        val updatedGroup = dataSnapshot.getValue(Group::class.java)
                                        updatedGroup!!.removeMember(userUid)
                                        if (updatedGroup.admins.contains(userUid)){
                                            updatedGroup.removeAdmin(userUid)
                                        }
                                        if (updatedGroup.members.isEmpty()){
                                            dataBaseGroupRef.child(group.uid!!).removeValue()
                                            AppUtils.deleteFolderRecursive(FirebaseStorage.getInstance(),"Groups/"+group.uid)
                                            finish()
                                        }else{
                                            dataBaseGroupRef.child(group.uid!!).setValue(updatedGroup)
                                            finish()
                                        }


                                    }
                                }
                                override fun onCancelled(databaseError: DatabaseError) {
                                }
                            })
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
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