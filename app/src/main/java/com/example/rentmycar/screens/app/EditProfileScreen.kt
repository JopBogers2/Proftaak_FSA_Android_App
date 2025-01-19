package com.example.rentmycar.screens.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.rentmycar.R
import com.example.rentmycar.api.requests.UserUpdateRequest
import com.example.rentmycar.api.responses.UserResponse
import com.example.rentmycar.navigation.BottomNavItem
import com.example.rentmycar.viewmodel.ProfileViewModel
import com.example.rentmycar.viewmodel.ProfileViewState

@Composable
fun EditProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
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
                    navController.popBackStack(BottomNavItem.Profile.route, inclusive = false)
                }

                is ProfileViewState.Error -> CenteredContent {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            modifier = Modifier.width(220.dp),
                            onClick = { viewModel.loadUserData() }) {
                            Text(text = stringResource(R.string.retry))
                        }
                    }
                }

                is ProfileViewState.Success -> {
                    EditProfileView(state.user, viewModel, navController)
                }
            }
        }
    }
}

@Composable
fun EditProfileView(
    userResponse: UserResponse,
    viewModel: ProfileViewModel,
    navController: NavHostController
) {
    var showPassword by remember { mutableStateOf(false) }

    var user by remember {
        mutableStateOf(
            UserUpdateRequest(
                userResponse.firstName,
                userResponse.lastName,
                userResponse.username,
                userResponse.email,
                ""
            )
        )
    }

    val isFormValid = user.firstName.isNotEmpty() &&
            user.lastName.isNotEmpty() &&
            user.username.isNotEmpty() &&
            user.email.isNotEmpty() &&
            user.password.isNotEmpty()

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            TextButton(
                onClick = {
                    navController.popBackStack()
                },
                contentPadding = PaddingValues(all = 0.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back icon",
                )
                Text(stringResource(R.string.back_to_profile))
            }
        }
        Text(
            text = stringResource(R.string.edit_profile),
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = user.firstName,
            onValueChange = { user = user.copy(firstName = it) },
            label = { Text(stringResource(R.string.first_name)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = user.lastName,
            onValueChange = { user = user.copy(lastName = it) },
            label = { Text(stringResource(R.string.last_name)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = user.username,
            onValueChange = { user = user.copy(username = it) },
            label = { Text(stringResource(R.string.username)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = user.email,
            onValueChange = { user = user.copy(email = it) },
            label = { Text(stringResource(R.string.email)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = user.password,
            onValueChange = { user = user.copy(password = it) },
            label = { Text(stringResource(R.string.password)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPassword) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (showPassword) "🙈" else "👁️"
                androidx.compose.material3.IconButton(onClick = { showPassword = !showPassword }) {
                    Text(icon)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.updateUser(user) },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid
        ) {
            Text(stringResource(R.string.update))
        }
    }

}

