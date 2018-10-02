package me.gr.githubbrowser.common

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import me.gr.githubbrowser.api.EmptyResponse
import me.gr.githubbrowser.api.ErrorResponse
import me.gr.githubbrowser.api.Response
import me.gr.githubbrowser.api.SuccessResponse
import me.gr.githubbrowser.util.AppExecutors

abstract class NetworkBoundResource<ResultType, RequestType> @MainThread constructor(private val executors: AppExecutors) {
    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        val dbSource = loadFromDB()
        result.value = Resource.loading(null)
        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData ->
                    setValue(Resource.success(newData))
                }
            }
        }
    }

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @MainThread
    protected abstract fun createCall(): LiveData<Response<RequestType>>

    @MainThread
    protected abstract fun loadFromDB(): LiveData<ResultType>

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType)

    @WorkerThread
    protected open fun processResponse(response: SuccessResponse<RequestType>) = response.body

    protected open fun onFetchFailed() {}

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiSource = createCall()
        result.addSource(dbSource) {
            setValue(Resource.loading(it))
        }
        result.addSource(apiSource) { response ->
            result.removeSource(apiSource)
            result.removeSource(dbSource)
            when (response) {
                is SuccessResponse -> {
                    executors.diskIO.execute {
                        saveCallResult(processResponse(response))
                        executors.mainThread.execute {
                            result.addSource(loadFromDB()) {
                                setValue(Resource.success(it))
                            }
                        }
                    }
                }
                is EmptyResponse -> {
                    executors.mainThread.execute {
                        result.addSource(loadFromDB()) {
                            setValue(Resource.success(it))
                        }
                    }
                }
                is ErrorResponse -> {
                    onFetchFailed()
                    result.addSource(dbSource) {
                        setValue(Resource.error(response.errorMessage, it))
                    }
                }
            }
        }
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }
}