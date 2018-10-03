package me.gr.githubbrowser.repository

import android.arch.lifecycle.LiveData
import me.gr.githubbrowser.api.Api
import me.gr.githubbrowser.api.Response
import me.gr.githubbrowser.common.NetworkBoundResource
import me.gr.githubbrowser.common.Resource
import me.gr.githubbrowser.data.User
import me.gr.githubbrowser.data.UserDao
import me.gr.githubbrowser.util.AppExecutors
import javax.inject.Inject

class UserRepository @Inject constructor(
        private val executors: AppExecutors,
        private val userDao: UserDao,
        private val api: Api
) {

    fun loadUser(login: String): LiveData<Resource<User>> {
        return object : NetworkBoundResource<User, User>(executors) {
            override fun createCall(): LiveData<Response<User>> {
                return api.getUser(login)
            }

            override fun loadFromDB(): LiveData<User> {
                return userDao.findByLogin(login)
            }

            override fun shouldFetch(data: User?): Boolean {
                return data == null
            }

            override fun saveCallResult(item: User) {
                userDao.insert(item)
            }
        }.asLiveData()
    }
}