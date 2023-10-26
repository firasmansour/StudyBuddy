package com.example.finalproject.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.finalproject.databinding.FragmentCreateNewGroupPopUpBinding
import com.example.finalproject.utils.Group
import com.example.finalproject.utils.User
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class CreateNewGroupPopUpFragment : DialogFragment() {
    private lateinit var binding: FragmentCreateNewGroupPopUpBinding
    private lateinit var imageURI : Uri
    private var listener: CreateGroupDialogListener? = null

    fun setCreateGroupDialogListener(listener: CreateGroupDialogListener) {
        this.listener = listener
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateNewGroupPopUpBinding.inflate(inflater,container,false)
        imageURI = Uri.parse("groupImage"+ binding.groupImage)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.CloseBtn.setOnClickListener {
            dismiss()
        }

        binding.groupImage.setOnClickListener{
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/jpg"
                startActivityForResult(it, 0)
            }
        }

        binding.createBtn.setOnClickListener {
            val groupName = binding.GroupNameEt.text.toString()
            val description = binding.descriptionEt.text.toString()
            val isPublic =  if (binding.isPublic.isChecked)  1  else 0
            listener?.onCreateNewGroup(groupName,isPublic,description,imageURI)
            dismiss()

        }

    }

    interface CreateGroupDialogListener{
        fun onCreateNewGroup(groupName:String ,isPublic:Int ,description:String,imageURI : Uri)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK){
            imageURI = data?.data!!
            binding.groupImage.setImageURI(imageURI)

        }
    }

}