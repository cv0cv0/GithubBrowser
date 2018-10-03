package me.gr.githubbrowser.adapter

import android.databinding.DataBindingComponent
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import me.gr.githubbrowser.data.Contributor
import me.gr.githubbrowser.databinding.ItemContributorBinding
import me.gr.githubbrowser.util.AppExecutors

class ContributorListAdapter(
        private val dataBindingComponent: DataBindingComponent,
        executors: AppExecutors,
        private val onItemClickListener: ((Contributor) -> Unit)?
) : DataBoundListAdapter<Contributor, ItemContributorBinding>(executors, ContributorDiffCallback()) {

    override fun onCreateBinding(parent: ViewGroup): ItemContributorBinding {
        val binding = ItemContributorBinding.inflate(LayoutInflater.from(parent.context), parent, false, dataBindingComponent)
        binding.root.setOnClickListener {
            binding.contributor?.let { contributor ->
                onItemClickListener?.invoke(contributor)
            }
        }
        return binding
    }

    override fun bind(binding: ItemContributorBinding, item: Contributor) {
        binding.contributor = item
    }
}

class ContributorDiffCallback : DiffUtil.ItemCallback<Contributor>() {
    override fun areItemsTheSame(p0: Contributor, p1: Contributor): Boolean {
        return p0.login == p1.login
    }

    override fun areContentsTheSame(p0: Contributor, p1: Contributor): Boolean {
        return p0.avatarUrl == p1.avatarUrl && p0.contributions == p1.contributions
    }
}