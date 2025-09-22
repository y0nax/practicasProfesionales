package com.example.githubapp.data.api

import com.example.githubapp.data.model.Repo
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {
    @GET("search/repositories")
    suspend fun searchRepos(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): RepoSearchResponse
}

data class RepoSearchResponse(
    val items: List<Repo>
)