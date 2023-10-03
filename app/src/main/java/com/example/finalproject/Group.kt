package com.example.finalproject

import android.os.Parcel
import android.os.Parcelable

data class Group(    var name : String ?= "",
                     var members: MutableList<String> = mutableListOf(),
                     var tasksList: MutableList<String> = mutableListOf(),
                     var admins: MutableList<String> = mutableListOf()) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        mutableListOf<String>().apply {
            parcel.readStringList(this)
        },
        mutableListOf<String>().apply {
            parcel.readStringList(this)
        },
        mutableListOf<String>().apply {
            parcel.readStringList(this)
        }
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeStringList(members)
        parcel.writeStringList(tasksList)
        parcel.writeStringList(admins)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Group> {
        override fun createFromParcel(parcel: Parcel): Group {
            return Group(parcel)
        }

        override fun newArray(size: Int): Array<Group?> {
            return arrayOfNulls(size)
        }
    }

    fun addMember(member: String) {
        members.add(member)
    }

    // Function to remove an item from the list
    fun removeMember(member: String) {
        members.remove(member)
    }
    fun addTask(task: String) {
        tasksList.add(task)
    }

    // Function to remove an item from the list
    fun removeTask(task: String) {
        tasksList.remove(task)
    }
    fun addAdmin(admin: String) {
        admins.add(admin)
    }

    // Function to remove an item from the list
    fun removeAdmin(admin: String) {
        admins.remove(admin)
    }
}
