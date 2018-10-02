package me.gr.githubbrowser.util

import android.arch.lifecycle.MutableLiveData
import me.gr.githubbrowser.api.*
import me.gr.githubbrowser.common.Resource
import me.gr.githubbrowser.data.AppDatabase
import me.gr.githubbrowser.data.SearchReult
import java.io.IOException

class FetchNextSearchPageTask(
        private val query: String,
        private val api: Api,
        private val db: AppDatabase
) : Runnable {
    val liveData = MutableLiveData<Resource<Boolean>>()

    override fun run() {
        val current = db.repoDao().search(query).value
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