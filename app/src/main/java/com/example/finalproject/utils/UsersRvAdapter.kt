package com.example.finalproject.utils

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.databinding.EachUserItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class UsersRvAdapter(

    private val List: MutableList<User>

) : RecyclerView.Adapter<UsersRvAdapter.UsersRvViewHolder>(){
    private lateinit var firebaseauth: FirebaseAuth
    private lateinit var storageReference: StorageReference

    var onItemClick : ((User) -> Unit)? = null
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


            }
        }


    }

    override fun getItemCount(): Int {
        return List.size
    }
}
