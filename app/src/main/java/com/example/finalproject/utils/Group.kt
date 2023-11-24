package com.example.finalproject.utils

import android.os.Parcel
import android.os.Parcelable

data class Group(
    var name : String ?= "",
    var isPublic: Int = 1,// 1 is true , 0 is false
    var uid: String ?= "",
    var description: String ?= "",
    var members: MutableList<String> = mutableListOf(),
    var tasksMap: HashMap<String,Task> = hashMapOf(),
    var admins: MutableList<String> = mutableListOf(),
    var owner :String ?= "" ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        mutableListOf<String>().apply {
            parcel.readStringList(this)
        },
        parcel.readHashMap(Task::class.java.classLoader) as HashMap<String, Task>,
        mutableListOf<String>().apply {
            parcel.readStringList(this)
        },
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(isPublic)
        parcel.writeString(uid)
        parcel.writeString(description)
        parcel.writeStringList(members)
        parcel.writeMap(tasksMap)
        parcel.writeStringList(admins)
        parcel.writeString(owner)
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
    fun addTask(key:String,task: Task) {
        tasksMap.put(key,task)
    }

    // Function to remove an item from the list
    fun removeTask(key: String) {
        tasksMap.remove(key)
    }
    fun addAdmin(admin: String) {
        admins.add(admin)
    }

    // Function to remove an item from the list
    fun removeAdmin(admin: String) {
        admins.remove(admin)
    }
}
