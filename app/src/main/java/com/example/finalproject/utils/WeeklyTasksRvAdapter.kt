package com.example.finalproject.utils

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.databinding.EachTaskItemBinding
import com.example.finalproject.databinding.EachUserItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.time.LocalDate


class WeeklyTasksRvAdapter(

    private val List: MutableList<Task>,
    private val isAdmin:Boolean

) : RecyclerView.Adapter<WeeklyTasksRvAdapter.WeeklyTasksRvViewHolder>(){
    private lateinit var firebaseauth: FirebaseAuth
    private lateinit var storageReference: StorageReference


    private var listener: OnTaskClickListener? = null

    fun setOnTaskClickListener(listener: OnTaskClickListener) {
        this.listener = listener
    }

    var onItemClick : ((Task) -> Unit)? = null
    inner class WeeklyTasksRvViewHolder(val binding: EachTaskItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyTasksRvViewHolder {
        val binding = EachTaskItemBinding.inflate(LayoutInflater.from(parent.context))
        firebaseauth = FirebaseAuth.getInstance()
        return WeeklyTasksRvViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return List.size
    }

    override fun onBindViewHolder(holder: WeeklyTasksRvViewHolder, position: Int) {
        with(holder) {
            with(List[position]) {
                binding.taskTitle.setText(this.title)
                if (!isAdmin){
                    binding.deleteTask.visibility = View.GONE
                    binding.editTask.visibility = View.GONE
                }else{
                    binding.deleteTask.setOnClickListener {
                        listener?.onDelete(this.key!!)
                    }
                    binding.editTask.setOnClickListener {
                        listener?.onEdit(this)
                    }
                }

                binding.root.setOnClickListener {
                    onItemClick?.invoke(this)
                }




            }
        }

    }

    interface OnTaskClickListener{
        fun onDelete(key: String)
        fun onEdit(task: Task)
    }



}
