package com.example.roomdatabaseproject.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
class User (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val uid : String = "kajdiahvv34bn4b234b24",
    val name: String,
    val email: String,
    val phoneNo : String,
    val occupation : String = "Businessman",
    val password : String = "example"
)