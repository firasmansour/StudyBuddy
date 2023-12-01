package com.example.finalproject.groupMainFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentGroupEditPopUpBinding
import com.example.finalproject.fragments.EditTextPopUpFragment


class GroupEditPopUpFragment(private val origGroupName: String?,private val origGroupDiscreption: String?) : DialogFragment() {
    private lateinit var binding: FragmentGroupEditPopUpBinding

    private var listener: EditGroupInfoDialogListener? = null

    fun setEditGroupInfoDialogListener(listener: EditGroupInfoDialogListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGroupEditPopUpBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.GroupNameEt.setText(origGroupName)
        binding.DescriptionEt.setText(origGroupDiscreption)

        binding.Close.setOnClickListener{
            dismiss()
        }
        binding.saveBtn.setOnClickListener {
            val newDescription = binding.DescriptionEt.text.toString()
            val newGroupName = binding.GroupNameEt.text.toString()

            // Notify the listener with the edited name
            listener?.onEditGroup(newGroupName,newDescription)
            binding.DescriptionEt.text = null
            binding.GroupNameEt.text = null
            dismiss()
        }

    }

    interface EditGroupInfoDialogListener {
        fun onEditGroup(newGroupName: String,newDescription:String)
    }
}