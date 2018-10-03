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
import me.gr.githubbrowser.adapter.RepoListAdapter
import me.gr.githubbrowser.binding.FragmentDataBindingComponent
import me.gr.githubbrowser.common.OnRetryClickListener
import me.gr.githubbrowser.databinding.FragmentUserBinding
import me.gr.githubbrowser.di.Injectable
import me.gr.githubbrowser.util.AppExecutors
import me.gr.githubbrowser.util.autoCleared
import me.gr.githubbrowser.viewmodel.UserViewModel
import javax.inject.Inject

class UserFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var executors: AppExecutors

    private lateinit var viewModel: UserViewModel
    private var dataBindingComponent = FragmentDataBindingComponent(this)
    private var binding by autoCleared<FragmentUserBinding>()
    private var adapter by autoCleared<RepoListAdapter>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false, dataBindingComponent)
        binding.retryClick = object : OnRetryClickListener {
            override fun onRetryClick() {
                viewModel.refresh()
            }
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel::class.java)

        val params = UserFragmentArgs.fromBundle(arguments)
        viewModel.setLogin(params.login)

        viewModel.user.observe(this, Observer {
            binding.user = it?.data
            binding.resource = it
        })

        adapter = RepoListAdapter(dataBindingComponent, executors, false) {
            findNavController().navigate(UserFragmentDirections.showRepo(it.owner.login, it.name))
        }
        binding.repoRecycler.adapter = adapter
        initRepoList()
    }

    private fun initRepoList() {
        viewModel.repositories.observe(this, Observer {
            adapter.submitList(it?.data)
        })
    }
}