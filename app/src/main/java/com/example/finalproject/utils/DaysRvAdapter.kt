package com.example.finalproject.utils

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.databinding.CalendarCellBinding
import com.example.finalproject.databinding.EachUserItemBinding
import com.example.finalproject.fragments.AddGroupSearchListPopUpFragment
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.time.LocalDate

class DaysRvAdapter (
    private val days: ArrayList<LocalDate>
) : RecyclerView.Adapter<DaysRvAdapter.DaysRvViewHolder>(){

//    var onItemClick : ((LocalDate) -> Unit)? = null

    private var listener: onItemsListener? = null

    fun setOnItemsListener(listener: onItemsListener) {
        this.listener = listener
    }
    inner class DaysRvViewHolder(val binding: CalendarCellBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaysRvViewHolder {
        val binding = CalendarCellBinding.inflate(LayoutInflater.from(parent.context))
        return DaysRvViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return days.size
    }

    override fun onBindViewHolder(holder: DaysRvViewHolder, position: Int) {
        with(holder) {
            with(days[position]) {
                if (this == null){
                    binding.cellDayText.setText("")
                }
                else{
                    binding.cellDayText.setText(this.dayOfMonth.toString())
                    if(this.equals(AppUtils.selectedDate))
                        binding.root.setBackgroundColor(Color.LTGRAY);
                }

                binding.root.setOnClickListener {
                    listener?.onItemClick(this)
                }
            }
        }
    }

    interface onItemsListener{
        fun onItemClick(date:LocalDate)
    }
}