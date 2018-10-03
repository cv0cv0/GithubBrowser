package me.gr.githubbrowser.api

import android.arch.lifecycle.LiveData
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

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

class LiveDataCallAdapter<R>(private val responseType: Type) : CallAdapter<R, LiveData<Response<R>>> {

    override fun responseType(): Type = responseType

    override fun adapt(call: Call<R>): LiveData<Response<R>> {
        return object : LiveData<Response<R>>() {
            private var started = AtomicBoolean(false)

            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<R> {
                        override fun onFailure(call: Call<R>, t: Throwable) {
                            postValue(Response.create(t))
                        }

                        override fun onResponse(call: Call<R>, response: retrofit2.Response<R>) {
                            postValue(Response.create(response))
                        }
                    })
                }
            }
        }
    }
}