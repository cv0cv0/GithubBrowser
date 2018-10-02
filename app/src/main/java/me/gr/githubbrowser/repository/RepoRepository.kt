package me.gr.githubbrowser.repository

import android.arch.lifecycle.LiveData
import me.gr.githubbrowser.api.Api
import me.gr.githubbrowser.api.Response
import me.gr.githubbrowser.common.NetworkBoundResource
import me.gr.githubbrowser.common.Resource
import me.gr.githubbrowser.data.AppDatabase
import me.gr.githubbrowser.data.Repo
import me.gr.githubbrowser.data.RepoDao
import me.gr.githubbrowser.util.AppExecutors
import me.gr.githubbrowser.util.RateLimiter
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepoRepository @Inject constructor(
        private val executors: AppExecutors,
        private val db: AppDatabase,
        private val repoDao: RepoDao,
        private val api: Api
) {
    private val repoListRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    fun loadRepos(owner: String): LiveData<Resource<List<Repo>>> {
        return object : NetworkBoundResource<List<Repo>, List<Repo>>(executors) {
            override fun createCall(): LiveData<Response<List<Repo>>> {
                return api.getRepos(owner)
            }

            override fun loadFromDB(): LiveData<List<Repo>> {
                return repoDao.loadRepositories(owner)
            }

            override fun shouldFetch(data: List<Repo>?): Boolean {
                return data == null || data.isEmpty() || repoListRateLimit.shouldFetch(owner)
            }

            override fun saveCallResult(item: List<Repo>) {
                repoDao.insertRepos(item)
            }

        }.asLiveData()
    }
}