package com.example.githubapp.uifeatures.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.githubapp.data.model.Repo

@Composable
fun RepoItem(repo: Repo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = repo.name, style = MaterialTheme.typography.titleLarge)
            Text(text = repo.description ?: "Sin descripción")
            Text(text = "⭐ ${repo.stars}")
            Text(text = repo.language ?: "Lenguaje no especificado")
        }
    }
}