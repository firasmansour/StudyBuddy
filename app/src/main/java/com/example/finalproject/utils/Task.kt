package com.example.finalproject.utils

import android.os.Parcel
import android.os.Parcelable

data class Task(var title : String ?= "",
                var description : String ?= "",
                var uploadDayDate : Int ?= -1,
                var uploadmonthDate : Int ?= -1,
                var uploadYearDate : Int ?= -1,
                var pdfLink : String ?= "",
                var pdfName : String ?= ""): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeInt(uploadDayDate!!)
        parcel.writeInt(uploadmonthDate!!)
        parcel.writeInt(uploadYearDate!!)
        parcel.writeString(pdfLink)
        parcel.writeString(pdfName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
