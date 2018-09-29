package me.gr.githubbrowser.api

import com.google.gson.annotations.SerializedName
import me.gr.githubbrowser.data.Repo

data class SearchResponse(
        @SerializedName("total_count")
        val totalCount: Int = 0,
        val items: List<Repo>
) {
    var nextPage: Int? = null
}