package com.example.finalproject.utils

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.databinding.EachUserItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File


class GroupMembersRvAdapter(

    private val List: MutableList<User>,
    private val AdminsList : MutableList<String>,
    private val owner : String ?= "",
    private val isAdmin:Boolean

) : RecyclerView.Adapter<GroupMembersRvAdapter.UsersRvViewHolder>(){
    private lateinit var firebaseauth: FirebaseAuth
    private lateinit var storageReference: StorageReference

    var onItemClick : ((User) -> Unit)? = null

    private var listener: OnMemberLongClickListener? = null

    fun setOnMemberLongClickListener(listener: OnMemberLongClickListener) {
        this.listener = listener
    }
    inner class UsersRvViewHolder(val binding: EachUserItemBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersRvViewHolder {
        val binding = EachUserItemBinding.inflate(LayoutInflater.from(parent.context))
        firebaseauth = FirebaseAuth.getInstance()
        return UsersRvViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersRvViewHolder, position: Int) {

        with(holder) {
            with(List[position]) {
                binding.userNameText.text = this.name
                binding.emailText.text = this.email
                //emplement fitching the profile pic!!
                storageReference = FirebaseStorage.getInstance().reference.child("Users/" + "profile_pics/" + this.email + ".jpg")
                val localFile = File.createTempFile("tempImage", "jpg")
                storageReference.getFile(localFile).addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                    binding.profileImage.setImageBitmap(bitmap)
                }


                binding.root.setOnClickListener {
                    onItemClick?.invoke(this)
                }
                AppUtils.fetchUserUidByEmail(this.email.toString()){userUid->
                    if (AdminsList.contains(userUid)){
                        if ( owner == userUid){
                            binding.isAdmin.text = "Owner"
                        }
                        binding.isAdmin.visibility = View.VISIBLE
                    }
                }


            }
            if (isAdmin){
                holder.itemView.setOnLongClickListener {
                    // Show the PopupMenu
                    showPopupMenu(holder.itemView, List[position])
                    true
                }
            }

        }


    }

    private fun showPopupMenu(view: View, user: User) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.popup_member_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.miMakeAdmin -> {
                    // Handle miMakeAdmin click for the specific data item
                    listener?.OnMakeAdmin(user)
                    Log.d("makeAdmin",user.email.toString())
                    true
                }
                R.id.miRemoveAdmin -> {
                    // Handle miRemoveAdmin click for the specific data item
                    listener?.OnRemoveAdmin(user)
                    Log.d("RemoveAdmin",user.email.toString())
                    true
                }
                R.id.miKick -> {
                    // Handle miKick click for the specific data item
                    listener?.OnKickMember(user)
                    Log.d("kickMember",user.email.toString())
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }


    override fun getItemCount(): Int {
        return List.size
    }


    interface OnMemberLongClickListener{
        fun OnMakeAdmin(member:User)
        fun OnKickMember(member:User)
        fun OnRemoveAdmin(member:User)
    }
}