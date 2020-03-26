package com.example.repokoltin.view.main

import com.example.repokoltin.model.Repo
import com.example.repokoltin.model.RepoRealm

interface MainView {
    fun onShowRepoSearchSuccess(listRepo: List<Repo>)
    fun onShowRepoSearchError()
    fun onShowRepoLocalSuccess(listRepo: ArrayList<RepoRealm>)
    fun onShowRepoLocalError()
    fun logoutSuccess()

}