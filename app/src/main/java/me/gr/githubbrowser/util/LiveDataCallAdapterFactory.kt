package me.gr.githubbrowser.util

import android.arch.lifecycle.LiveData
import me.gr.githubbrowser.api.Response
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class LiveDataCallAdapterFactory private constructor() : CallAdapter.Factory() {

    companion object {

        fun create() = LiveDataCallAdapterFactory()
    }

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        if (getRawType(returnType) != LiveData::class.java) {
            return null
        }

        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawObservableType = getRawType(observableType)
        if (rawObservableType != Response::class.java) {
            throw IllegalArgumentException("type must be a resource")
        }
        if (observableType !is ParameterizedType) {
            throw IllegalArgumentException("resource must be parameterized")
        }

        val bodyType = getParameterUpperBound(0, observableType)
        return LiveDataCallAdapter<Any>(bodyType)
    }
}

class LiveDataCallAdapter<R>(private val responseType:Type):CallAdapter<R,LiveData<Response<R>>>{

    override fun responseType(): Type =responseType

    override fun adapt(call: Call<R>): LiveData<Response<R>> {
        // TODO
    }
}