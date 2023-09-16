package com.example.finalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.databinding.EachUserItemBinding

class UsersRvAdapter(
    private val List: MutableList<User>
) : RecyclerView.Adapter<UsersRvAdapter.UsersRvViewHolder>(){
    inner class UsersRvViewHolder(val binding: EachUserItemBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersRvViewHolder {
        val binding = EachUserItemBinding.inflate(LayoutInflater.from(parent.context))
        return UsersRvViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersRvViewHolder, position: Int) {

        with(holder) {
            with(List[position]) {
                binding.userNameText.text = this.name
                binding.emailText.text = this.email
                //emplement fitching the profile pic!!
            }
        }


    }

    override fun getItemCount(): Int {
        return List.size
    }
}