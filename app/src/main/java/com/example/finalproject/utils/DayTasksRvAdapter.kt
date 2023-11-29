package com.example.finalproject.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.databinding.EachHourTaskItemBinding
import java.time.LocalTime

class DayTasksRvAdapter(
    private val list: MutableList<Task>
): RecyclerView.Adapter<DayTasksRvAdapter.DayTasksRvViewHolder>() {


    private var listener: OnTaskClickListener? = null

    fun setOnTaskClickListener(listener: OnTaskClickListener) {
        this.listener = listener
    }

    inner class DayTasksRvViewHolder(val binding: EachHourTaskItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayTasksRvViewHolder {
        val binding = EachHourTaskItemBinding.inflate(LayoutInflater.from(parent.context))

        return DayTasksRvViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DayTasksRvViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.timeTV.text = AppUtils.formattedShortTime(LocalTime.of(this.atHour!!,0))
                if (this.key!=""){
                    binding.taskTitleTv.text = this.title
                    binding.root.setOnClickListener{
                        listener?.onTaskClick(this)
                    }
                }else{
                    binding.taskTitleTv.visibility = View.INVISIBLE
                }



            }
        }
    }
    interface OnTaskClickListener{
        fun onTaskClick(task:Task)
    }
}