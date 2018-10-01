package me.gr.githubbrowser.data

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import com.google.gson.annotations.SerializedName

@Entity(
        indices = [Index("id"), Index("owner_login")],
        primaryKeys = ["name", "owner_login"]
)
data class Repo(
        val id: Int,
        val name: String,
        @field:SerializedName("full_name")
        val fullName: String,
        val description: String?,
        @field:Embedded(prefix = "owner_")
        val owner: Owner,
        @field:SerializedName("stargazers_count")
        val stars: Int
) {

    companion object {
        const val UNKNOWN_ID = -1
    }

    data class Owner(val login: String, val url: String?)
}