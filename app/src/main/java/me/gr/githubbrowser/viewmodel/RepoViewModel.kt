package me.gr.githubbrowser.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import me.gr.githubbrowser.common.Resource
import me.gr.githubbrowser.data.Contributor
import me.gr.githubbrowser.data.Repo
import me.gr.githubbrowser.repository.RepoRepository
import me.gr.githubbrowser.common.AbsentLiveData
import javax.inject.Inject

class RepoViewModel @Inject constructor(repository: RepoRepository) : ViewModel() {
    private val repoId = MutableLiveData<RepoId>()
    val repo: LiveData<Resource<Repo>> = Transformations.switchMap(repoId) {
        it.ifExists { owner, name -> repository.loadRepo(owner, name) }
    }
    val contributors: LiveData<Resource<List<Contributor>>> = Transformations.switchMap(repoId) {
        it.ifExists { owner, name -> repository.loadContributors(owner, name) }
    }

    fun refresh() {
        val owner = repoId.value?.owner
        val name = repoId.value?.name
        if (owner != null && name != null) {
            repoId.value = RepoId(owner, name)
        }
    }

    fun setId(owner: String, name: String) {
        val newValue = RepoId(owner, name)
        if (repoId.value == newValue) return
        repoId.value = newValue
    }

    data class RepoId(val owner: String, val name: String) {
        fun <T> ifExists(f: (String, String) -> LiveData<T>): LiveData<T> {
            return if (owner.isBlank() || name.isBlank()) {
                AbsentLiveData.create()
            } else {
                f(owner, name)
            }
        }
    }
}