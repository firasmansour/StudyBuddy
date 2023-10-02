package com.example.finalproject.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.ChatRoomActivity
import com.example.finalproject.R
import com.example.finalproject.User
import com.example.finalproject.UsersRvAdapter
import com.example.finalproject.databinding.FragmentProfileBinding
import com.example.finalproject.databinding.FragmentSearchUsersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference

class SearchUsersFragment : Fragment() ,AddUserPopUpFragment.AddUserDialogListener{
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseauth = FirebaseAuth.getInstance()
        dataBaseRef = FirebaseDatabase.getInstance().reference.child("Users")


        binding.rvUsers.layoutManager = LinearLayoutManager(context)

        usersList = mutableListOf()
        usersRvAdapter = UsersRvAdapter(usersList)
        binding.rvUsers.adapter = usersRvAdapter
        usersRvAdapter.onItemClick = {

            val intent = Intent(requireActivity(),ChatRoomActivity::class.java)
            intent.putExtra("user",it)
            startActivity(intent)
        }

        getUsersFromFirebase()


        binding.addUser.setOnClickListener{
            val popupDialog = AddUserPopUpFragment()
            popupDialog.setAddUserDialogListener(this)
            popupDialog.show(childFragmentManager, "addUserPopUp")
        }
    }

    private fun getUsersFromFirebase() {
        dataBaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                usersList.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)

                    if (user != null) {
                        usersList.add(user)
                    }

                }
                usersRvAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
            }


        })

    }

    override fun onAdd(friendEmail: String) {///need to change
        firebaseauth.fetchSignInMethodsForEmail(friendEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods.isNullOrEmpty()) {
                        // Email is not registered or authenticated
                        Toast.makeText(context,"there is no such user",Toast.LENGTH_SHORT).show()
                    } else {
                        // Email is registered and authenticated
                        dataBaseRef.child(firebaseauth.currentUser?.uid.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                // Check if the user exists
                                if (dataSnapshot.exists()) {
                                    val user = dataSnapshot.getValue(User::class.java)
                                    user?.addItem(friendEmail)
                                    dataBaseRef.child(firebaseauth.currentUser?.uid.toString()).setValue(user)
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Handle any errors that may occur during the fetch
                                Toast.makeText(context,"user not found",Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                } else {
                    // An error occurred while checking the email
                    val exception = task.exception
                    Toast.makeText(context,exception?.message,Toast.LENGTH_SHORT).show()

                }
            }

    }
}