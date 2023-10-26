package com.example.finalproject.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.ChatRoomActivity
import com.example.finalproject.utils.User
import com.example.finalproject.utils.UsersRvAdapter
import com.example.finalproject.databinding.FragmentSearchUsersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserFriendsFragment : Fragment() ,AddUserPopUpFragment.AddUserDialogListener{
    private lateinit var firebaseauth: FirebaseAuth
    private lateinit var binding: FragmentSearchUsersBinding
    private lateinit var dataBaseRef: DatabaseReference
    private lateinit var usersRvAdapter: UsersRvAdapter
    private lateinit var usersList: MutableList<User>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchUsersBinding.inflate(inflater, container, false)

        firebaseauth = FirebaseAuth.getInstance()
        dataBaseRef = FirebaseDatabase.getInstance().reference.child("Users")


        binding.rvUsers.layoutManager = LinearLayoutManager(context)

        usersList = mutableListOf()
        usersRvAdapter = UsersRvAdapter(usersList)
        binding.rvUsers.adapter = usersRvAdapter
        getUsersFromFirebase()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usersRvAdapter.onItemClick = {

            val intent = Intent(requireActivity(),ChatRoomActivity::class.java)
            intent.putExtra("user",it)
            startActivity(intent)
        }

//        getUsersFromFirebase()


        binding.addUser.setOnClickListener{
            val popupDialog = AddUserPopUpFragment()
            popupDialog.setAddUserDialogListener(this)
            popupDialog.show(childFragmentManager, "addUserPopUp")
        }
    }

    private fun getUsersFromFirebase() {

        dataBaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fetchUserFromFirebase(firebaseauth.currentUser?.uid.toString()){
                    usersList.clear()
                    for (userSnapshot in snapshot.children) {
                        if (it!!.friendsList.contains(userSnapshot.key)){
                            val user = userSnapshot.getValue(User::class.java)

                            if (user != null) {

                                usersList.add(user)
                            }
                        }
                    }
                    usersRvAdapter.notifyDataSetChanged()
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
            }


        })

    }


    fun fetchUserUidByEmail(email: String,callback: (String?) -> Unit)  {

        dataBaseRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Check if a user with the specified email exists
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val uid = userSnapshot.key.toString()
                        callback(uid)
                        return
                    }
                } else {
                    Toast.makeText(context,"There is no such User",Toast.LENGTH_SHORT).show()
                    callback(null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context,databaseError.message,Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }

    override fun onAdd(friendEmail: String) {
        if (friendEmail == firebaseauth.currentUser?.email.toString()){
            Toast.makeText(context,"you cant add your self XD,GET SOME FRIENDS! ",Toast.LENGTH_SHORT).show()
            return
        }
        fetchUserUidByEmail(friendEmail) {friendUid ->
            if (friendUid!=null){
                val userUid = firebaseauth.currentUser?.uid.toString()
                dataBaseRef.child(userUid).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val user = dataSnapshot.getValue(User::class.java)
                        if (!user!!.friendsList.contains(friendUid)){
                            user.addFriend(friendUid)
                            dataBaseRef.child(userUid).setValue(user).addOnSuccessListener {
                                Toast.makeText(context,"User have been added",Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(context, "friend already added", Toast.LENGTH_SHORT).show()
                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle any errors that occur
                        Toast.makeText(context, databaseError.message, Toast.LENGTH_SHORT).show()

                    }
                })
            }else{
                Toast.makeText(context,"Somthing went wrong",Toast.LENGTH_SHORT).show()
            }
        }

    }
    fun fetchUserFromFirebase(uid: String, callback: (User?) -> Unit) {
        dataBaseRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Check if the user exists
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)
                    callback(user)
                } else {
                    callback(null) // User not found
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that may occur during the fetch
                Toast.makeText(context,"user not found",Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }
}