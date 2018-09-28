package me.gr.githubbrowser.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import me.gr.githubbrowser.app.App
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)
}