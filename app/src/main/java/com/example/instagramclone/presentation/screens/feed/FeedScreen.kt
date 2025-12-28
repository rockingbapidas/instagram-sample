package com.example.instagramclone.presentation.screens.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
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
import com.example.instagramclone.domain.model.Ad
import com.example.instagramclone.domain.model.FeedItem
import com.example.instagramclone.domain.model.Post
import com.example.instagramclone.presentation.screens.component.PostItem
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
fun FeedScreen(
    navController: NavController,
    viewModel: FeedViewModel = hiltViewModel()
) {
    val pagingItems = viewModel.feedPagingFlow.collectAsLazyPagingItems()
    val hasNewPosts by viewModel.hasNewPosts.collectAsState()

    FeedScreenContent(
        pagingItems = pagingItems,
        hasNewPosts = hasNewPosts,
        onRefresh = { pagingItems.refresh() },
        onNewPostsShown = { viewModel.onNewPostsShown() },
        onCreatePostClick = { navController.navigate("create_post") },
        onScrollChanged = { items, index -> viewModel.onScrollChanged(items, index) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedScreenContent(
    pagingItems: LazyPagingItems<FeedItem>,
    hasNewPosts: Boolean,
    onRefresh: () -> Unit,
    onNewPostsShown: () -> Unit,
    onCreatePostClick: () -> Unit,
    onScrollChanged: (List<FeedItem>, Int) -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Observe scroll state for prefetching
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { index ->
                val items = (0 until pagingItems.itemCount).mapNotNull { pagingItems[it] }
                onScrollChanged(items, index)
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Instagram Clone") },
                actions = {
                    IconButton(onClick = onCreatePostClick) {
                        Icon(Icons.Default.Add, contentDescription = "Create Post")
                    }
                }
            )
        }
    ) { paddingValues ->
        val isRefreshing = pagingItems.loadState.refresh is LoadState.Loading

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = pullToRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (isRefreshing && pagingItems.itemCount == 0) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(count = pagingItems.itemCount, key = { index ->
                            when (val item = pagingItems.peek(index)) {
                                is FeedItem.PostItem -> item.post.id
                                is FeedItem.AdItem -> item.ad.id
                                else -> index
                            }
                        }) { index ->
                            val item = pagingItems[index]
                            if (item != null) {
                                when (item) {
                                    is FeedItem.PostItem -> PostItem(post = item.post)
                                    is FeedItem.AdItem -> AdItem(ad = item.ad)
                                }
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

                // New Posts Banner
                if (hasNewPosts) {
                    ExtendedFloatingActionButton(
                        text = { Text("New Posts") },
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        onClick = {
                            onNewPostsShown()
                            onRefresh()
                            coroutineScope.launch {
                                listState.animateScrollToItem(0)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        // Initial Refresh Error Snackbar
        val refreshState = pagingItems.loadState.refresh
        if (refreshState is LoadState.Error && pagingItems.itemCount == 0) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { pagingItems.retry() }) {
                            Text("Retry")
                        }
                    }
                ) {
                    Text(refreshState.error.message ?: "Unknown Error")
                }
            }
        }
    }
}

@Composable
private fun AdItem(ad: Ad) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "SPONSORED",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(ad.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Ad Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                ad.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(ad.content, style = MaterialTheme.typography.bodyMedium)
            Button(onClick = { /* Open Ad */ }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Learn More")
            }
        }
    }
}

@Preview
@Composable
private fun FeedScreenPreview() {
    val dummyPosts: List<FeedItem> = listOf(
        FeedItem.PostItem(
            Post(
                id = "1",
                username = "user1",
                imageUrl = "https://example.com/image1.jpg",
                caption = "Caption 1",
                likes = 10,
                comments = listOf(),
                timestamp = System.currentTimeMillis()
            )
        ),
        FeedItem.PostItem(
            Post(
                id = "2",
                username = "user2",
                imageUrl = "https://example.com/image2.jpg",
                caption = "Caption 2",
                likes = 20,
                comments = listOf(),
                timestamp = System.currentTimeMillis()
            )
        )
    )
    val pagingItems = flowOf(PagingData.from(dummyPosts)).collectAsLazyPagingItems()

    MaterialTheme {
        FeedScreenContent(
            pagingItems = pagingItems,
            hasNewPosts = true,
            onRefresh = {},
            onNewPostsShown = {},
            onCreatePostClick = {},
            onScrollChanged = { _, _ -> }
        )
    }
}
