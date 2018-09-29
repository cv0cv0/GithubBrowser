package me.gr.githubbrowser.api

import timber.log.Timber
import java.util.regex.Pattern

sealed class Response<T> {

    companion object {

        fun <T> create(error: Throwable): ErrorResponse<T> {
            return ErrorResponse(error.message ?: "unknown error")
        }

        fun <T> create(response: retrofit2.Response<T>): Response<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                if (body == null || response.code() == 204) {
                    EmptyResponse()
                } else {
                    SuccessResponse(body, response.headers()?.get("link"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody.isNullOrEmpty()) response.message() else errorBody
                ErrorResponse(errorMessage ?: "unknown error")
            }
        }
    }
}

class EmptyResponse<T> : Response<T>()

class ErrorResponse<T>(val errorMessage: String) : Response<T>()

data class SuccessResponse<T>(val body: T, val links: Map<String, String>) : Response<T>() {

    constructor(body: T, linkHeader: String?) : this(body, linkHeader?.extractLinks() ?: emptyMap())

    val nextPage: Int? by lazy(LazyThreadSafetyMode.NONE) {
        links[NEXT_LINK]?.let { next ->
            val matcher = PAGE_PATTERN.matcher(next)
            if (!matcher.find() || matcher.groupCount() != 1) {
                null
            } else {
                try {
                    Integer.parseInt(matcher.group(1))
                } catch (e: NumberFormatException) {
                    Timber.w("cannot parse next page from %s", next)
                    null
                }
            }
        }
    }

    companion object {
        private val LINK_PATTERN = Pattern.compile("<([^>]*)>[\\s]*;[\\s]*rel=\"([a-zA-Z0-9]+)\"")
        private val PAGE_PATTERN = Pattern.compile("\\bpage=(\\d+)")
        private const val NEXT_LINK = "next"

        private fun String.extractLinks(): Map<String, String> {
            val links = mutableMapOf<String, String>()
            val matcher = LINK_PATTERN.matcher(this)

            while (matcher.find()) {
                val count = matcher.groupCount()
                if (count == 2) {
                    links[matcher.group(2)] = matcher.group(1)
                }
            }
            return links
        }
    }
}