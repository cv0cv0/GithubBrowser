package me.gr.githubbrowser.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import me.gr.githubbrowser.common.AbsentLiveData
import me.gr.githubbrowser.common.Resource
import me.gr.githubbrowser.data.Repo
import me.gr.githubbrowser.data.User
import me.gr.githubbrowser.repository.RepoRepository
import me.gr.githubbrowser.repository.UserRepository
import javax.inject.Inject

class UserViewModel @Inject constructor(userRepository: UserRepository, repoRepository: RepoRepository) : ViewModel() {
    private val login = MutableLiveData<String>()

    val repositories: LiveData<Resource<List<Repo>>> = Transformations.switchMap(login) {
        if (it == null) {
            AbsentLiveData.create()
        } else {
            repoRepository.loadRepos(it)
        }
    }

    val user: LiveData<Resource<User>> = Transformations.switchMap(login) {
        if (it == null) {
            AbsentLiveData.create()
        } else {
            userRepository.loadUser(it)
        }
    }

    fun setLogin(login: String?) {
        if (this.login.value != login) this.login.value = login
    }

    fun refresh() {
        login.value?.let { login.value = it }
    }
}