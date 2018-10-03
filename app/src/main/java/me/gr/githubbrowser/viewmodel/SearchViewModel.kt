package me.gr.githubbrowser.viewmodel

import android.arch.lifecycle.*
import me.gr.githubbrowser.common.AbsentLiveData
import me.gr.githubbrowser.common.Resource
import me.gr.githubbrowser.data.Repo
import me.gr.githubbrowser.repository.RepoRepository
import javax.inject.Inject

class SearchViewModel @Inject constructor(repoRepository: RepoRepository) : ViewModel() {
    private val query = MutableLiveData<String>()
    private val nextPageHandler = NextPageHandler(repoRepository)

    val loadMoreState: LiveData<LoadMoreState> = nextPageHandler.loadMoreState
    val results: LiveData<Resource<List<Repo>>> = Transformations.switchMap(query) {
        if (it.isNullOrBlank()) {
            AbsentLiveData.create()
        } else {
            repoRepository.search(it)
        }
    }

    fun setQuery(originalInput: String) {
        val input = originalInput.toLowerCase().trim()
        if (input == query.value) return
        nextPageHandler.reset()
        query.value = input
    }

    fun loadNextPage() {
        query.value?.let {
            if (it.isNotBlank()) nextPageHandler.queryNextPage(it)
        }
    }

    fun refresh() {
        query.value?.let {
            query.value = it
        }
    }
}

class NextPageHandler(private val repository: RepoRepository) : Observer<Resource<Boolean>> {
    private var nextPageLiveDate: LiveData<Resource<Boolean>>? = null
    private var query: String? = null

    val loadMoreState = MutableLiveData<LoadMoreState>()
    private var hasMore = false

    init {
        reset()
    }

    override fun onChanged(t: Resource<Boolean>?) {
        if (t == null) {
            reset()
        } else {
            when (t.status) {
                Resource.Status.SUCCESS -> {
                    hasMore = t.data == true
                    unregister()
                    loadMoreState.value = LoadMoreState(false, null)
                }
                Resource.Status.ERROR -> {
                    hasMore = true
                    unregister()
                    loadMoreState.value = LoadMoreState(false, t.message)
                }
                else -> {
                }
            }
        }
    }

    fun queryNextPage(query: String) {
        if (this.query == query) return
        unregister()
        this.query = query
        nextPageLiveDate = repository.searchNextPage(query)
        loadMoreState.value = LoadMoreState(true, null)
        nextPageLiveDate?.observeForever(this)
    }

    fun reset() {
        unregister()
        hasMore = true
        loadMoreState.value = LoadMoreState(false, null)
    }

    private fun unregister() {
        nextPageLiveDate?.removeObserver(this)
        nextPageLiveDate = null
        if (hasMore) query = null
    }
}

class LoadMoreState(val isRunning: Boolean, private val errorMessage: String?) {
    private var handledError = false

    val errorMessageIfNotHandled: String?
        get() {
            if (handledError) return null
            handledError = true
            return errorMessage
        }
}