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


class WeeklyTasksRvAdapter(

    private val List: MutableList<User>

) : RecyclerView.Adapter<WeeklyTasksRvAdapter.WeeklyTasksRvViewHolder>(){
    private lateinit var firebaseauth: FirebaseAuth
    private lateinit var storageReference: StorageReference

//    var onItemClick : ((User) -> Unit)? = null
    inner class WeeklyTasksRvViewHolder(val binding: EachUserItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyTasksRvViewHolder {
        val binding = EachUserItemBinding.inflate(LayoutInflater.from(parent.context))
        firebaseauth = FirebaseAuth.getInstance()
        return WeeklyTasksRvViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return List.size
    }

    override fun onBindViewHolder(holder: WeeklyTasksRvViewHolder, position: Int) {

    }


}
