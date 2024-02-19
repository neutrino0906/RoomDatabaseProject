package com.example.roomdatabaseproject.data

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {

//    val readAllData: LiveData<User> = userDao.readAll()

    suspend fun readUser(email: String): User{
        return userDao.readUser(email)
    }

    suspend fun addUser(user:User){
        userDao.add_user(user)
    }

    suspend fun deleteUser(){
        userDao.deleteUser()
    }

}