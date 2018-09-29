package me.gr.githubbrowser.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.gr.githubbrowser.MainActivity

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector(modules = [FragmentBuilderModule::class])
    abstract fun contributeMainActivity(): MainActivity
}