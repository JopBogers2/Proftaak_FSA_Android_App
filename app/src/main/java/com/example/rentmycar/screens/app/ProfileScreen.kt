package com.example.rentmycar.screens.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.rentmycar.api.requests.UserResponse
import com.example.rentmycar.viewmodel.ProfileViewModel
import com.example.rentmycar.viewmodel.ProfileViewState

@Composable
fun ProfileScreen(navController: NavHostController, viewModel: ProfileViewModel = hiltViewModel()) {
    AuthenticatedScreen(navController, viewModel.logoutEvent) {
        val viewState by viewModel.viewState.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.loadUserData()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp, 36.dp)
        ) {
            when (val state = viewState) {

                ProfileViewState.Loading -> CenteredContent {
                    CircularProgressIndicator()
                }

                ProfileViewState.Updated -> {
                    viewModel.loadUserData()
                }

                is ProfileViewState.Error -> CenteredContent {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }

                is ProfileViewState.Success -> {
                    ProfileView(state.user, viewModel, navController)
                }
            }
        }
    }
}

@Composable
fun CenteredContent(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun ProfileView(user: UserResponse, viewModel: ProfileViewModel, navController: NavHostController) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Text(
                text = user.firstName,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = user.lastName,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "@${user.username}", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Score: ${user.score}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(85.dp))
        Button(
            modifier = Modifier.width(220.dp),
            onClick = {
                navController.navigate("edit-profile")
            }) {
            Text(text = "Edit Profile")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(modifier = Modifier.width(220.dp), onClick = { viewModel.logout() }) {
            Text(text = "Logout")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(modifier = Modifier.width(220.dp), colors = ButtonDefaults.buttonColors().copy(
            containerColor = Color.Red
        ), onClick = { showDeleteConfirmation = true }) {
            Text(text = "Delete Profile")
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(text = "Delete Profile") },
            text = { Text("Are you sure you want to delete your profile? This action cannot be undone.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.deleteUser() // Trigger the delete action
                    showDeleteConfirmation = false // Dismiss dialog
                }) {
                    Text("Yes, Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

