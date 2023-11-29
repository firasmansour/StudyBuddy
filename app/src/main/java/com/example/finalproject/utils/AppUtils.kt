package com.example.finalproject.utils

import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


object AppUtils {

    fun fetchUserFromFirebase(uid: String, callback: (User?) -> Unit) {
        val tmp =  FirebaseDatabase.getInstance().reference.child("Users")
        tmp.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Check if the user exists
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)
                    callback(user)
                } else {
                    callback(null) // User not found
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that may occur during the fetch
                callback(null)
            }
        })
    }

    fun fetchGroupFromFirebase(groupUid: String, callback: (Group?) -> Unit) {
        val tmp =  FirebaseDatabase.getInstance().reference.child("Groups")
        try {
            tmp.child(groupUid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Check if the user exists
                    if (dataSnapshot.exists()) {
                        val group = dataSnapshot.getValue(Group::class.java)
                        callback(group)
                    } else {
                        callback(null) // User not found
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors that may occur during the fetch

                    callback(null)
                }
            })
        }catch (e:Exception){
            Log.d("fetch group error",e.message.toString())
        }

    }

    fun fetchUserUidByEmail(email: String,callback: (String?) -> Unit)  {
        val tmp =  FirebaseDatabase.getInstance().reference.child("Users")
        tmp.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Check if a user with the specified email exists
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val uid = userSnapshot.key.toString()
                        callback(uid)
                        return
                    }
                } else {
                    callback(null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(null)
            }
        })
    }


    ///////calender utiles\

    var selectedDate: LocalDate? = null


    fun monthYearFromDate(date: LocalDate): String? {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }
    fun formattedDate(date: LocalDate): String? {
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        return date.format(formatter)
    }
    fun monthDayFromDate(date: LocalDate): String? {
        val formatter = DateTimeFormatter.ofPattern("MMMM d")
        return date.format(formatter)
    }
    fun formattedShortTime(time: LocalTime): String? {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return time.format(formatter)
    }
    fun daysInWeekArray(selectedDate: LocalDate?): ArrayList<LocalDate>? {
        val days = ArrayList<LocalDate>()
        var current: LocalDate = sundayForDate(selectedDate!!)!!
        val endDate = current.plusWeeks(1)
        while (current.isBefore(endDate)) {
            days.add(current)
            current = current.plusDays(1)
        }
        return days
    }

    private fun sundayForDate(current: LocalDate): LocalDate? {
        var current = current
        val oneWeekAgo = current.minusWeeks(1)
        while (current.isAfter(oneWeekAgo)) {
            if (current.dayOfWeek == DayOfWeek.SUNDAY) return current
            current = current.minusDays(1)
        }
        return null
    }

    fun isDateValid(context: Context,dateString: String): Boolean {
        try {
            // Attempt to parse the date string
            val parsedDate = LocalDate.parse(dateString)

            // If parsing is successful, the date is valid
            return true
        } catch (e: DateTimeParseException) {
            // If an exception occurs during parsing, the date is not valid
            return false
        }
    }
    fun tasksForADay(items:HashMap<String,Task>,selectedDate: LocalDate?): ArrayList<Task> {
        val filteredTasks = ArrayList<Task>()
        for (item in items){
            val date = LocalDate.parse(item.value.date)
            if (date == selectedDate){
                filteredTasks.add(item.value)
            }
        }
        filteredTasks.sortBy { LocalTime.parse(it.time) }//change to time
        return filteredTasks
    }
    fun dailyViewTasks(items:HashMap<String,Task>,selectedDate: LocalDate?): ArrayList<Task> {
        val filteredTasks = ArrayList<Task>()
        for (item in items){
            val date = LocalDate.parse(item.value.date)
            if (date == selectedDate){
                filteredTasks.add(item.value)
            }
        }
        for (hour in 6..22){
            if (filteredTasks.find { LocalTime.parse(it.time).hour == hour && LocalTime.parse(it.time).minute == 0 } == null){
                filteredTasks.add(Task("","","",LocalTime.of(hour,0).toString()))
            }
        }
        filteredTasks.sortBy { LocalTime.parse(it.time)  }//change to time
        return filteredTasks
    }


    fun deleteFolderRecursive(storage: FirebaseStorage, folderPath: String) {
        val storageRef = storage.reference.child(folderPath)

        // Delete all items (files and subfolders) within the folder
        storageRef.listAll()
            .addOnSuccessListener { result ->
                result.items.forEach { itemRef ->
                    itemRef.delete()
                        .addOnSuccessListener {
                            // Item deleted successfully
                            Log.d("deleteRes",itemRef.name)
                        }
                        .addOnFailureListener { e ->
                            // Handle the error
                            Log.d("deleteRes",e.message.toString())
                        }
                }

                // Recursively delete subfolders
                result.prefixes.forEach { subfolderRef ->
                    val subfolderPath = subfolderRef.toString()
                    deleteFolderRecursive(storage, subfolderPath)
                }

                // After deleting all items, delete the folder itself
                storageRef.delete()
                    .addOnSuccessListener {
                        Log.d("deleteRes","Success")
                    }
                    .addOnFailureListener { e ->
                        // Handle the error
                        Log.d("deleteRes",e.message.toString())
                    }
            }
            .addOnFailureListener { e ->
                // Handle the error
                Log.d("deleteRes",e.message.toString())
            }
    }
}