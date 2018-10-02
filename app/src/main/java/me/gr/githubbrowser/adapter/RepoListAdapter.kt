package me.gr.githubbrowser.adapter

import android.databinding.DataBindingComponent
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import me.gr.githubbrowser.data.Repo
import me.gr.githubbrowser.databinding.ItemRepoBinding
import me.gr.githubbrowser.util.AppExecutors

class RepoListAdapter(
        private val dataBindingComponent: DataBindingComponent,
        executors: AppExecutors,
        private val showFullName: Boolean,
        private val onItemClickListener: ((Repo) -> Unit)?
) : DataBoundListAdapter<Repo, ItemRepoBinding>(executors, RepoDiffCallback()) {

    override fun onCreateBinding(parent: ViewGroup): ItemRepoBinding {
        val binding = ItemRepoBinding.inflate(LayoutInflater.from(parent.context), parent, false, dataBindingComponent)
        binding.showFullName = showFullName
        binding.root.setOnClickListener {
            binding.repo?.let { repo ->
                onItemClickListener?.invoke(repo)
            }
        }
        return binding
    }

    override fun bind(binding: ItemRepoBinding, item: Repo) {
        binding.repo = item
    }
}

class RepoDiffCallback : DiffUtil.ItemCallback<Repo>() {
    override fun areItemsTheSame(p0: Repo, p1: Repo): Boolean {
        return p0.owner == p1.owner && p0.name == p1.name
    }

    override fun areContentsTheSame(p0: Repo, p1: Repo): Boolean {
        return p0.description == p1.description && p0.stars == p1.stars
    }
}