package me.gr.githubbrowser.adapter

import android.databinding.DataBindingComponent
import me.gr.githubbrowser.data.Repo
import me.gr.githubbrowser.util.AppExecutors

class RepoListAdapter(
        private val dataBindingComponent: DataBindingComponent,
        executors: AppExecutors,
        private val showFullName:Boolean,
        private val onItemClickListener:((Repo)->Unit)?
) {

}