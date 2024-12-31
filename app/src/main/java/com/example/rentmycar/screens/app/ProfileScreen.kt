package com.example.rentmycar.screens.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (val state = viewState) {
                ProfileViewState.Loading -> CircularProgressIndicator()
                is ProfileViewState.Success -> Text(text = "User Score: ${state.user.score}")
                is ProfileViewState.Error -> Text(text = state.message)
            }
        }
    }
}
