package com.example.roomdatabaseproject.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add_user(user: User)


    @Query("SELECT * FROM user_table WHERE email LIKE :e")
    suspend fun readUser(e:String): User

    @Query("DELETE FROM user_table")
    suspend fun deleteUser()

}