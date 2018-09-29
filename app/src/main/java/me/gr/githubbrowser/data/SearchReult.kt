package me.gr.githubbrowser.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.TypeConverter
import android.arch.persistence.room.TypeConverters
import timber.log.Timber

@Entity(primaryKeys = ["query"])
@TypeConverters(Converters::class)
data class SearchReult(
        val query: String,
        val repoIds: List<Int>,
        val totalCount: Int,
        val next: Int?
)

class Converters {

    @TypeConverter
    fun stringToIntList(data: String?): List<Int>? = data?.let {
        it.split(",").map { split ->
            try {
                split.toInt()
            } catch (e: NumberFormatException) {
                Timber.e(e, "Cannot convert $split to number")
                null
            }
        }
    }?.filterNotNull()

    @TypeConverter
    fun intListToString(ints: List<Int>?) = ints?.joinToString(separator = ",")
}