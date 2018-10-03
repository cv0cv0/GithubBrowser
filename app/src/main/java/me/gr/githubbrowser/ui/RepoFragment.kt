package me.gr.githubbrowser.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import me.gr.githubbrowser.adapter.ContributorListAdapter
import me.gr.githubbrowser.binding.FragmentDataBindingComponent
import me.gr.githubbrowser.common.OnRetryClickListener
import me.gr.githubbrowser.databinding.FragmentRepoBinding
import me.gr.githubbrowser.di.Injectable
import me.gr.githubbrowser.util.AppExecutors
import me.gr.githubbrowser.util.autoCleared
import me.gr.githubbrowser.viewmodel.RepoViewModel
import javax.inject.Inject

class RepoFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var executors: AppExecutors

    private lateinit var viewModel: RepoViewModel
    private var dataBindingComponent = FragmentDataBindingComponent(this)
    private var binding by autoCleared<FragmentRepoBinding>()
    private var adapter by autoCleared<ContributorListAdapter>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRepoBinding.inflate(inflater, container, false, dataBindingComponent)
        binding.retryClick = object : OnRetryClickListener {
            override fun onRetryClick() {
                viewModel.refresh()
            }
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RepoViewModel::class.java)

        val params = RepoFragmentArgs.fromBundle(arguments)
        viewModel.setId(params.owner, params.name)

        viewModel.repo.observe(this, Observer {
            binding.repo = it?.data
            binding.resource = it
        })

        adapter = ContributorListAdapter(dataBindingComponent, executors) {
            findNavController().navigate(RepoFragmentDirections.showUser(it.login))
        }
        binding.contributorRecycler.adapter = adapter
        initContributorList()
    }

    private fun initContributorList() {
        viewModel.contributors.observe(this, Observer {
            adapter.submitList(it?.data ?: emptyList())
        })
    }
}