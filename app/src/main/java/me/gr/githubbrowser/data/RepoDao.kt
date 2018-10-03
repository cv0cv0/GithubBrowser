package me.gr.githubbrowser.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.util.SparseIntArray

@Dao
abstract class RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg repos: Repo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertContributors(contributors: List<Contributor>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRepos(repositories: List<Repo>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun createRepoIfNotExists(repo: Repo): Long

    @Query("select * from repo where owner_login=:ownerLogin and name=:name")
    abstract fun load(ownerLogin: String, name: String): LiveData<Repo>

    @Query("select login,avatarUrl,repoName,repoOwner,contributions from contributor where repoName=:name and repoOwner=:owner order by contributions desc")
    abstract fun loadContributors(owner: String, name: String): LiveData<List<Contributor>>

    @Query("select * from repo where owner_login=:owner order by stars desc")
    abstract fun loadRepositories(owner: String): LiveData<List<Repo>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(result: SearchReult)

    @Query("select * from searchreult where `query`=:query")
    abstract fun search(query: String): LiveData<SearchReult>

    @Query("select * from searchreult where `query`=:query")
    abstract fun findSearchResult(query: String): SearchReult?

    fun loadOrdered(repoIds: List<Int>): LiveData<List<Repo>> {
        val order = SparseIntArray()
        repoIds.withIndex().forEach {
            order.put(it.value, it.index)
        }

        return Transformations.map(loadById(repoIds)) { repositories ->
            repositories.sortedBy { order[it.id] }
            repositories
        }
    }

    @Query("select * from repo where id in (:repoIds)")
    protected abstract fun loadById(repoIds: List<Int>): LiveData<List<Repo>>
}