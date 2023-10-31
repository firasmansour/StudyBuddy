package com.example.finalproject.utils

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.databinding.EachGroupItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class GroupsRvAdapter(

    private var List: MutableList<Group>

) : RecyclerView.Adapter<GroupsRvAdapter.GroupsRvViewHolder>(){
    private lateinit var firebaseauth: FirebaseAuth
    private lateinit var storageReference: StorageReference

    fun setFilteredList(filteredList: MutableList<Group>){
        this.List = filteredList
        notifyDataSetChanged()
    }

    var onItemClick : ((Group) -> Unit)? = null
    inner class GroupsRvViewHolder(val binding: EachGroupItemBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsRvViewHolder {
        val binding = EachGroupItemBinding.inflate(LayoutInflater.from(parent.context))
        firebaseauth = FirebaseAuth.getInstance()
        return GroupsRvViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupsRvViewHolder, position: Int) {

        with(holder) {
            with(List[position]) {
                binding.groupNameText.text = this.name
                //emplement fitching the profile pic!!
                storageReference = FirebaseStorage.getInstance().reference.child("Groups/" + this.uid +"/" + "GroupImage" + ".jpg")
                val localFile = File.createTempFile("tempImage", "jpg")
                storageReference.getFile(localFile).addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                    binding.groupImage.setImageBitmap(bitmap)
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
