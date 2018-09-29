package me.gr.githubbrowser.api

import android.arch.lifecycle.LiveData
import me.gr.githubbrowser.data.Contributor
import me.gr.githubbrowser.data.Repo
import me.gr.githubbrowser.data.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("users/{login}")
    fun getUser(@Path("login") login: String): LiveData<Response<User>>

    @GET("users/{login}/repos")
    fun getRepos(@Path("login") login: String): LiveData<Response<List<Repo>>>

    @GET("repos/{owner}/{name}")
    fun getRepo(@Path("owner") owner: String, @Path("name") name: String): LiveData<Response<Repo>>

    @GET("repos/{owner}/{name}/contributors")
    fun getContributors(@Path("owner") owner: String, @Path("name") name: String): LiveData<Response<List<Contributor>>>

    @GET("search/repositories")
    fun seachRepos(@Query("q") query: String): LiveData<Response<SearchResponse>>

    @GET("search/repositories")
    fun seachRepos(@Query("q") query: String, @Query("page") page: Int): Call<SearchResponse>
}