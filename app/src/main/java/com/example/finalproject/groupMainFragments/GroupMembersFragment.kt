package com.example.finalproject.groupMainFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentGroupMembersBinding
import com.example.finalproject.utils.AppUtils
import com.example.finalproject.utils.Group
import com.example.finalproject.utils.GroupMembersRvAdapter
import com.example.finalproject.utils.User
import com.example.finalproject.utils.UsersRvAdapter
import com.example.finalproject.utils.WeeklyTasksRvAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class GroupMembersFragment : Fragment(),GroupMembersRvAdapter.OnMemberLongClickListener {
    private lateinit var binding :FragmentGroupMembersBinding
    private lateinit var dataBaseUsersRef: DatabaseReference
    private lateinit var membersRvAdapter: GroupMembersRvAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userUid:String
    private lateinit var group:Group
    private lateinit var membersList: MutableList<User>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupMembersBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        dataBaseUsersRef = FirebaseDatabase.getInstance().reference.child("Users")
        userUid = firebaseAuth.currentUser!!.uid
        group = arguments?.getParcelable<Group>("group")!!
        membersList = mutableListOf()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMembersAdpater()
    }
    private fun setMembersAdpater() {
        membersList.clear()
        binding.rvMembers.layoutManager = LinearLayoutManager(context)
        membersRvAdapter = GroupMembersRvAdapter(membersList,group.admins.contains(userUid))
        membersRvAdapter.setOnMemberLongClickListener(this)
        binding.rvMembers.adapter = membersRvAdapter

        getMembers()
    }

    private fun getMembers() {
        dataBaseUsersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                membersList.clear()
                for (memberSnapshot in snapshot.children) {
                    if (group.members.contains(memberSnapshot.key)){
                        val member = memberSnapshot.getValue(User::class.java)

                        if (member != null) {

                            membersList.add(member)
                        }
                    }
                }
                membersRvAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
            }


        })
    }

    override fun OnMakeAdmin(member: User) {
        val dataBaseGroupRef = FirebaseDatabase.getInstance().reference.child("Groups")
        AppUtils.fetchUserUidByEmail(requireContext(),member.email.toString()){memberUid->
            group.addAdmin(memberUid.toString())
            dataBaseGroupRef.child(group.uid!!).setValue(group)
        }
    }

    override fun OnKickMember(member: User) {
        if (member.email.toString() != firebaseAuth.currentUser?.email){
            val dataBaseGroupRef = FirebaseDatabase.getInstance().reference.child("Groups")
            AppUtils.fetchUserUidByEmail(requireContext(),member.email.toString()){memberUid->
                group.removeMember(memberUid.toString())
                if (group.admins.contains(memberUid)){
                    group.removeAdmin(memberUid.toString())
                }
                dataBaseGroupRef.child(group.uid!!).setValue(group)
                member.groupsList.remove(group.uid)
                dataBaseUsersRef.child(memberUid.toString()).setValue(member)
            }
            setMembersAdpater()
        }else{
            Toast.makeText(context,"you cant kick yourself :p",Toast.LENGTH_SHORT).show()
        }

    }


}