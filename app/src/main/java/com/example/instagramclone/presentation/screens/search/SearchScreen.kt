package com.example.instagramclone.presentation.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.instagramclone.domain.model.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val posts by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    SearchScreenContent(
        searchQuery = searchQuery,
        posts = posts,
        isLoading = isLoading,
        onSearchQueryChange = { 
            searchQuery = it
            viewModel.search(it)
        },
        onSearch = { viewModel.search(searchQuery) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenContent(
    searchQuery: String,
    posts: List<Post>,
    isLoading: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            onSearch = { onSearch() },
            active = false,
            onActiveChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }
        ) {}
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(1.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(posts) { post ->
                    PostGridItem(post = post)
                }
            }
        }
    }
}

@Composable
fun PostGridItem(post: Post) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(1.dp)
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(post.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Post thumbnail",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    val dummyPosts = List(10) { index ->
        Post(
            id = "$index",
            username = "user$index",
            imageUrl = "https://example.com/image$index.jpg",
            caption = "Caption $index",
            likes = index * 10,
            comments = emptyList(),
            timestamp = System.currentTimeMillis()
        )
    }

    MaterialTheme {
        SearchScreenContent(
            searchQuery = "Nature",
            posts = dummyPosts,
            isLoading = false,
            onSearchQueryChange = {},
            onSearch = {}
        )
    }
}
