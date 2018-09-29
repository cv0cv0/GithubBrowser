package me.gr.githubbrowser.di

import dagger.Module
import dagger.Provides
import me.gr.githubbrowser.api.Api
import me.gr.githubbrowser.util.LiveDataCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideApi():Api=Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
            .build()
            .create(Api::class.java)
}