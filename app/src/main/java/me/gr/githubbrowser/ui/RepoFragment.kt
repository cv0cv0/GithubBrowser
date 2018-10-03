package me.gr.githubbrowser.ui

import android.arch.lifecycle.ViewModelProvider
import android.support.v4.app.Fragment
import me.gr.githubbrowser.binding.FragmentDataBindingComponent
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
    lateinit var viewModel: RepoViewModel

    var dataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FragmentRepoBinding>()
}