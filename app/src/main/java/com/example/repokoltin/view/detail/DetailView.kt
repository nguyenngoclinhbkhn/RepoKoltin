package com.example.repokoltin.view.detail

import com.example.repokoltin.model.Repo
import com.example.repokoltin.model.RepoRealm

interface DetailView {
    fun onShowDetail(repo: RepoRealm)
    fun onDeleteRepoSuccess()
    fun showDetailFaild()
}