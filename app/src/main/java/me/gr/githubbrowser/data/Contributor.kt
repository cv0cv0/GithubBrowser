package me.gr.githubbrowser.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import com.google.gson.annotations.SerializedName

@Entity(
        primaryKeys = ["repoName", "repoOwner", "login"],
        foreignKeys = [
            ForeignKey(
                    entity = Repo::class,
                    parentColumns = ["name", "owner_login"],
                    childColumns = ["repoName", "repoOwner"],
                    onUpdate = ForeignKey.CASCADE,
                    deferred = true
            )]
)
data class Contributor(
        val login: String,
        val contributions: Int,
        @field:SerializedName("avatar_url")
        val avatarUrl: String?
) {
    lateinit var repoName: String
    lateinit var repoOwner: String
}