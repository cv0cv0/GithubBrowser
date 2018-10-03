package me.gr.githubbrowser.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.findNavController
import me.gr.githubbrowser.adapter.RepoListAdapter
import me.gr.githubbrowser.binding.FragmentDataBindingComponent
import me.gr.githubbrowser.databinding.FragmentSearchBinding
import me.gr.githubbrowser.di.Injectable
import me.gr.githubbrowser.util.AppExecutors
import me.gr.githubbrowser.util.autoCleared
import me.gr.githubbrowser.viewmodel.SearchViewModel
import javax.inject.Inject

class SearchFragment : Fragment(), Injectable {
    @Inject
    private lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    private lateinit var executors: AppExecutors

    private lateinit var viewModel: SearchViewModel
    private var dataBindingComponent = FragmentDataBindingComponent(this)
    private var binding by autoCleared<FragmentSearchBinding>()
    private var adapter by autoCleared<RepoListAdapter>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false, dataBindingComponent)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchViewModel::class.java)
        adapter = RepoListAdapter(dataBindingComponent, executors, true) {
            findNavController().navigate(SearchFragmentDirections.ShowRepo(it.owner.login, it.name))
        }
        binding.repoRecycler.adapter = adapter
        binding.setRetryClick {
            viewModel.refresh()
        }
        initRecyclerView()
        initSearchInputListener()
    }

    private fun initRecyclerView() {
        binding.repoRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == adapter.itemCount - 1) viewModel.loadNextPage()
            }
        })
        viewModel.results.observe(this, Observer {
            binding.resource = it
            binding.resultCount = it?.data?.size ?: 0
            adapter.submitList(it?.data)
        })
        viewModel.loadMoreState.observe(this, Observer {
            if (it == null) {
                binding.isLoading = false
            } else {
                binding.isLoading = it.isRunning
                val errorMessage = it.errorMessageIfNotHandled
                if (errorMessage != null) {
                    Snackbar.make(binding.loadMoreBar, errorMessage, Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun initSearchInputListener() {
        binding.inputEdit.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch(v)
                true
            } else {
                false
            }
        }
        binding.inputEdit.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                doSearch(v)
                true
            } else {
                false
            }
        }
    }

    private fun doSearch(v: View) {
        val query = binding.inputEdit.text.toString()
        dismissKeyboard(v.windowToken)
        binding.query = query
        viewModel.setQuery(query)
    }

    private fun dismissKeyboard(windowToken: IBinder?) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }
}