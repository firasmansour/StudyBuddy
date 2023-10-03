package com.example.finalproject

import android.os.Parcel
import android.os.Parcelable

data class User(
    var name : String ?= "",
    var email : String ?= "",
    var studyField : String ?= "",
    var bio : String ?= "",
    var friendsList: MutableList<String> = mutableListOf(),
    var tasksList: MutableList<String> = mutableListOf(),
    var groupsList: MutableList<String> = mutableListOf()): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
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
        parcel.writeString(email)
        parcel.writeString(studyField)
        parcel.writeString(bio)
        parcel.writeStringList(friendsList)
        parcel.writeStringList(tasksList)
        parcel.writeStringList(groupsList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

    fun addFriend(friend: String) {
        friendsList.add(friend)
    }

    // Function to remove an item from the list
    fun removeFriend(friend: String) {
        friendsList.remove(friend)
    }
    fun addTask(task: String) {
        tasksList.add(task)
    }

    // Function to remove an item from the list
    fun removeTask(task: String) {
        tasksList.remove(task)
    }
    fun addGroup(group: String) {
        groupsList.add(group)
    }

    // Function to remove an item from the list
    fun removeGroup(group: String) {
        groupsList.remove(group)
    }
}
