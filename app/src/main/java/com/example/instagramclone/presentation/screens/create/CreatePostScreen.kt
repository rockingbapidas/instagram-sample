package com.example.instagramclone.presentation.screens.create

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    navController: NavController,
    viewModel: CreatePostViewModel = hiltViewModel()
) {
    var caption by remember { mutableStateOf("") }
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val isCreating by viewModel.isCreating.collectAsState()
    val error by viewModel.error.collectAsState()
    
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setSelectedImage(it) }
    }
    
    CreatePostScreenContent(
        caption = caption,
        selectedImageUri = selectedImageUri,
        isCreating = isCreating,
        error = error,
        onCaptionChange = { caption = it },
        onImageSelect = { imagePicker.launch("image/*") },
        onNavigateUp = { navController.navigateUp() },
        onShare = {
            viewModel.createPost(caption)
            navController.navigateUp()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreenContent(
    caption: String,
    selectedImageUri: Uri?,
    isCreating: Boolean,
    error: String?,
    onCaptionChange: (String) -> Unit,
    onImageSelect: () -> Unit,
    onNavigateUp: () -> Unit,
    onShare: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Post") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = onShare,
                        enabled = selectedImageUri != null && !isCreating
                    ) {
                        Text("Share")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (selectedImageUri == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onImageSelect
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Select Image",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            } else {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(selectedImageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = caption,
                onValueChange = onCaptionChange,
                label = { Text("Write a caption...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (isCreating) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreatePostScreenPreview() {
    MaterialTheme {
        CreatePostScreenContent(
            caption = "",
            selectedImageUri = null,
            isCreating = false,
            error = null,
            onCaptionChange = {},
            onImageSelect = {},
            onNavigateUp = {},
            onShare = {}
        )
    }
}
