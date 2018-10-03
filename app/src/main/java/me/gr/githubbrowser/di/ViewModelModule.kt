package me.gr.githubbrowser.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import me.gr.githubbrowser.viewmodel.RepoViewModel
import me.gr.githubbrowser.viewmodel.SearchViewModel
import me.gr.githubbrowser.viewmodel.UserViewModel
import me.gr.githubbrowser.viewmodel.ViewModelFactory

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(viewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RepoViewModel::class)
    abstract fun bindRepoViewModel(viewModel: RepoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel::class)
    abstract fun bindUserViewModel(viewModel: UserViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}