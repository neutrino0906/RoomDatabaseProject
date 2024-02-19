package com.example.roomdatabaseproject.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application){

//    val readAllData: LiveData<User>
    private val repository: UserRepository

    init {
        val userDao = UserDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    fun addUser(user: User){
        viewModelScope.launch(Dispatchers.IO){
            repository.addUser(user)
        }
    }

    fun deleteUser(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteUser()
        }
    }

    suspend fun readUser(email: String): User{
        var a : User = User(0,"","","","","")
//        viewModelScope.launch(Dispatchers.IO) {
            a = repository.readUser(email)
//        }
        return a
    }

}