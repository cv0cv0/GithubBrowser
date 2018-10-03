package me.gr.githubbrowser.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.gr.githubbrowser.ui.RepoFragment
import me.gr.githubbrowser.ui.SearchFragment
import me.gr.githubbrowser.ui.UserFragment

@Module
abstract class FragmentBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment

    @ContributesAndroidInjector
    abstract fun contributeRepoFragment(): RepoFragment

    @ContributesAndroidInjector
    abstract fun contributorUserFragment(): UserFragment
}