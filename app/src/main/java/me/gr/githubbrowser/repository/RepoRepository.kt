package me.gr.githubbrowser.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import me.gr.githubbrowser.api.*
import me.gr.githubbrowser.common.AbsentLiveData
import me.gr.githubbrowser.common.NetworkBoundResource
import me.gr.githubbrowser.common.Resource
import me.gr.githubbrowser.data.*
import me.gr.githubbrowser.util.AppExecutors
import me.gr.githubbrowser.util.RateLimiter
import java.io.IOException
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

    fun loadRepo(owner: String, name: String): LiveData<Resource<Repo>> {
        return object : NetworkBoundResource<Repo, Repo>(executors) {
            override fun createCall(): LiveData<Response<Repo>> {
                return api.getRepo(owner, name)
            }

            override fun loadFromDB(): LiveData<Repo> {
                return repoDao.load(owner, name)
            }

            override fun shouldFetch(data: Repo?): Boolean {
                return data == null
            }

            override fun saveCallResult(item: Repo) {
                repoDao.insert(item)
            }
        }.asLiveData()
    }

    fun loadContributors(owner: String, name: String): LiveData<Resource<List<Contributor>>> {
        return object : NetworkBoundResource<List<Contributor>, List<Contributor>>(executors) {
            override fun createCall(): LiveData<Response<List<Contributor>>> {
                return api.getContributors(owner, name)
            }

            override fun loadFromDB(): LiveData<List<Contributor>> {
                return repoDao.loadContributors(owner, name)
            }

            override fun shouldFetch(data: List<Contributor>?): Boolean {
                return data == null || data.isEmpty()
            }

            override fun saveCallResult(item: List<Contributor>) {
                item.forEach {
                    it.repoName = name
                    it.repoOwner = owner
                }
                db.runInTransaction {
                    repoDao.createRepoIfNotExists(
                            Repo(
                                    id = Repo.UNKNOWN_ID,
                                    name = name,
                                    fullName = "$owner/$name",
                                    description = "",
                                    owner = Repo.Owner(owner, null),
                                    stars = 0
                            )
                    )
                    repoDao.insertContributors(item)
                }
            }
        }.asLiveData()
    }

    fun search(query: String): LiveData<Resource<List<Repo>>> {
        return object : NetworkBoundResource<List<Repo>, SearchResponse>(executors) {
            override fun createCall(): LiveData<Response<SearchResponse>> {
                return api.searchRepos(query)
            }

            override fun loadFromDB(): LiveData<List<Repo>> {
                return Transformations.switchMap(repoDao.search(query)) {
                    if (it == null) {
                        AbsentLiveData.create()
                    } else {
                        repoDao.loadOrdered(it.repoIds)
                    }
                }
            }

            override fun shouldFetch(data: List<Repo>?): Boolean {
                return data == null
            }

            override fun saveCallResult(item: SearchResponse) {
                val repoIds = item.items.map { it.id }
                val searchResult = SearchReult(
                        query = query,
                        repoIds = repoIds,
                        totalCount = item.totalCount,
                        next = item.nextPage
                )
                db.runInTransaction {
                    repoDao.insertRepos(item.items)
                    repoDao.insert(searchResult)
                }
            }

        }.asLiveData()
    }

    fun searchNextPage(query: String): LiveData<Resource<Boolean>> {
        val fetchNextSearchPageTask = FetchNextSearchPageTask(query, api, db)
        executors.networkIO.execute(fetchNextSearchPageTask)
        return fetchNextSearchPageTask.liveData
    }
}

class FetchNextSearchPageTask(
        private val query: String,
        private val api: Api,
        private val db: AppDatabase
) : Runnable {
    val liveData = MutableLiveData<Resource<Boolean>>()

    override fun run() {
        val current = db.repoDao().findSearchResult(query)
        if (current == null) {
            liveData.postValue(null)
            return
        }

        val nextPage = current.next
        if (nextPage == null) {
            liveData.postValue(Resource.success(false))
            return
        }

        val newValue = try {
            val response = api.searchRepos(query, nextPage).execute()
            val apiResponse = Response.create(response)
            when (apiResponse) {
                is SuccessResponse -> {
                    val ids = arrayListOf<Int>()
                    ids.addAll(current.repoIds)
                    ids.addAll(apiResponse.body.items.map { it.id })
                    val merged = SearchReult(
                            query = query,
                            repoIds = ids,
                            totalCount = apiResponse.body.totalCount,
                            next = apiResponse.nextPage
                    )
                    db.runInTransaction {
                        db.repoDao().insert(merged)
                        db.repoDao().insertRepos(apiResponse.body.items)
                    }
                    Resource.success(apiResponse.nextPage != null)
                }
                is EmptyResponse -> {
                    Resource.success(false)
                }
                is ErrorResponse -> {
                    Resource.error(apiResponse.errorMessage, true)
                }
            }
        } catch (e: IOException) {
            Resource.error(e.message!!, true)
        }
        liveData.postValue(newValue)
    }
}