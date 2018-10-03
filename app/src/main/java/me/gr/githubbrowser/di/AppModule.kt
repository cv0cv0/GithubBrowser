package me.gr.githubbrowser.di

import android.app.Application
import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
import me.gr.githubbrowser.api.Api
import me.gr.githubbrowser.data.AppDatabase
import me.gr.githubbrowser.api.LiveDataCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideApi(): Api = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
            .build()
            .create(Api::class.java)

    @Singleton
    @Provides
    fun provideDatabase(application: Application): AppDatabase = Room
            .databaseBuilder(application, AppDatabase::class.java, "app.db")
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideUserDao(db: AppDatabase) = db.userDao()

    @Singleton
    @Provides
    fun provideRepoDao(db: AppDatabase) = db.repoDao()
}