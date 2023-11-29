package com.example.finalproject.utils

import android.os.Parcel
import android.os.Parcelable
import java.time.Year

data class Task(var title : String ?= "",
                var description : String ?= "",
                var date : String ?= "2023-11-11",
                var time : String ?= "",
                var key: String ?= "",
                var pdfName : String ?= "",
                var pdfLink : String ?= ""): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(date)
        parcel.writeString(time)
        parcel.writeString(key)
        parcel.writeString(pdfName)
        parcel.writeString(pdfLink)
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
