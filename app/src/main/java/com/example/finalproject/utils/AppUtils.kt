package com.example.finalproject.utils

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object AppUtils {

    fun fetchUserFromFirebase(context: Context,uid: String, callback: (User?) -> Unit) {
        val tmp =  FirebaseDatabase.getInstance().reference.child("Users")
        tmp.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
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
                Toast.makeText(context,"user not found", Toast.LENGTH_SHORT).show()
                callback(null)
            }
        })
    }

    fun fetchGroupFromFirebase(context: Context,groupUid: String, callback: (Group?) -> Unit) {
        val tmp =  FirebaseDatabase.getInstance().reference.child("Groups")
        try {
            tmp.child(groupUid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Check if the user exists
                    if (dataSnapshot.exists()) {
                        val group = dataSnapshot.getValue(Group::class.java)
                        callback(group)
                    } else {
                        callback(null) // User not found
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors that may occur during the fetch
                    Toast.makeText(context,"group not found", Toast.LENGTH_SHORT).show()
                    callback(null)
                }
            })
        }catch (e:Exception){
            Toast.makeText(context,"group not found", Toast.LENGTH_SHORT).show()
        }

    }

    fun fetchUserUidByEmail(context: Context,email: String,callback: (String?) -> Unit)  {
        val tmp =  FirebaseDatabase.getInstance().reference.child("Users")
        tmp.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
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

}