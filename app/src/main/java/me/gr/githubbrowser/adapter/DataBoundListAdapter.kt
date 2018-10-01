package me.gr.githubbrowser.adapter

import android.databinding.ViewDataBinding
import android.support.v7.recyclerview.extensions.AsyncDifferConfig
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import me.gr.githubbrowser.util.AppExecutors

abstract class DataBoundListAdapter<T, V : ViewDataBinding>(
        executors: AppExecutors,
        diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, DataBoundListAdapter.DataBoundViewHolder<V>>(
        AsyncDifferConfig.Builder<T>(diffCallback)
                .setBackgroundThreadExecutor(executors.diskIO)
                .build()
) {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): DataBoundViewHolder<V> {
        val binding = onCreateBinding(p0)
        return DataBoundViewHolder(binding)
    }

    override fun onBindViewHolder(p0: DataBoundViewHolder<V>, p1: Int) {
        bind(p0.binding, getItem(p1))
        p0.binding.executePendingBindings()
    }

    protected abstract fun onCreateBinding(parent: ViewGroup): V

    protected abstract fun bind(binding: V, item: T)

    class DataBoundViewHolder<out T : ViewDataBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)
}