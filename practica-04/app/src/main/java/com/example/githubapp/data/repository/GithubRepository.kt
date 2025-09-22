package com.example.githubapp.data.repository

import com.example.githubapp.data.api.GithubApi
import com.example.githubapp.data.model.Repo

class GithubRepository(private val api: GithubApi) {
    suspend fun searchRepos(query: String, page: Int = 1): List<Repo> {
        return api.searchRepos(query, page).items
    }
}