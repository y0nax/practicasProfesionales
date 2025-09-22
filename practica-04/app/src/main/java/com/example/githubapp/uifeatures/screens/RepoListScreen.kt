package com.example.githubapp.uifeatures.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.githubapp.uifeatures.components.RepoItem
import com.example.githubapp.uifeatures.viewmodel.RepoViewModel

@Composable
fun RepoListScreen(viewModel: RepoViewModel = viewModel()) {
    var query by remember { mutableStateOf("") }
    val repos by viewModel.repos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Buscar repositorios") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { viewModel.search(query) },
            enabled = !isLoading && query.isNotBlank()
        ) {
            if (isLoading) CircularProgressIndicator() else Text("Buscar")
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(repos) { repo ->
                RepoItem(repo = repo)
            }
        }
    }
}