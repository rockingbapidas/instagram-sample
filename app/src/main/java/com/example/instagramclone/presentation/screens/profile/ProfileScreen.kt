package com.example.instagramclone.presentation.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.instagramclone.domain.model.FeedItem
import com.example.instagramclone.domain.model.Post
import com.example.instagramclone.domain.model.User
import com.example.instagramclone.presentation.screens.component.PostGridItem
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val pagingItems = viewModel.posts.collectAsLazyPagingItems()
    val listState = rememberLazyListState()

    // Observe scroll state for prefetching
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { index ->
                val items = (0 until pagingItems.itemCount)
                    .mapNotNull { pagingItems[it] }
                viewModel.onScrollChanged(items, index)
            }
    }

    ProfileScreenContent(
        profile = profile,
        pagingItems = pagingItems,
        onEditProfile = viewModel::editProfile,
        onLogout = {
            viewModel.logout()
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreenContent(
    profile: User?,
    pagingItems: LazyPagingItems<FeedItem.PostItem>,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val showMenu = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(profile?.username ?: "Profile") },
                actions = {
                    IconButton(onClick = { showMenu.value = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { paddingValues ->
        val isRefreshing = pagingItems.loadState.refresh is LoadState.Loading

        if (profile == null) {
            // Show loading state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val currentProfile = profile
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Profile Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Picture
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .padding(4.dp)
                    ) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(currentProfile.profilePictureUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile picture",
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

                    // Stats
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(count = currentProfile.postCount.toString(), label = "Posts")
                        StatItem(count = currentProfile.followers.toString(), label = "Followers")
                        StatItem(count = currentProfile.following.toString(), label = "Following")
                    }
                }

                // Bio
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = currentProfile.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentProfile.bio,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Edit Profile Button
                OutlinedButton(
                    onClick = onEditProfile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Edit Profile")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Posts Grid
                if (isRefreshing && pagingItems.itemCount == 0) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(1.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            count = pagingItems.itemCount,
                            key = { index ->
                                pagingItems[index]?.post?.id ?: index
                            }
                        ) { index ->
                            val post = pagingItems[index]?.post
                            if (post != null) {
                                PostGridItem(post = post)
                            }
                        }

                        // Append Loader or Error
                        item {
                            val appendState = pagingItems.loadState.append
                            if (appendState is LoadState.Loading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else if (appendState is LoadState.Error) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Failed to load more posts",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedButton(onClick = { pagingItems.retry() }) {
                                        Text("Retry")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showMenu.value) {
        AlertDialog(
            onDismissRequest = { showMenu.value = false },
            title = { Text("Profile Options") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            onLogout()
                            showMenu.value = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Logout", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMenu.value = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun StatItem(count: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    val dummyUser = User(
        id = "1",
        username = "johndoe",
        email = "john@example.com",
        displayName = "John Doe",
        bio = "Android Developer",
        profilePictureUrl = "",
        followers = 100,
        following = 50,
        postCount = 10
    )

    val dummyPosts = List(10) { index ->
        FeedItem.PostItem(
            Post(
                id = "$index",
                username = "johndoe",
                imageUrl = "https://example.com/image.jpg",
                caption = "Post $index",
                likes = index * 10,
                comments = emptyList(),
                timestamp = System.currentTimeMillis()
            )
        )
    }

    val pagingItems = flowOf(PagingData.from(dummyPosts)).collectAsLazyPagingItems()

    MaterialTheme {
        ProfileScreenContent(
            profile = dummyUser,
            pagingItems = pagingItems,
            onEditProfile = {},
            onLogout = {}
        )
    }
}
