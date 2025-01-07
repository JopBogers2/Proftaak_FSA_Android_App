package com.example.rentmycar.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.rentmycar.navigation.AppNavItem
import com.example.rentmycar.navigation.BottomNavItem
import com.example.rentmycar.viewmodel.AuthViewModel
import com.example.rentmycar.viewmodel.AuthViewState

@Composable
fun LoginScreen(navController: NavHostController, viewModel: AuthViewModel = hiltViewModel()) {
    val viewState by viewModel.viewState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val isFormValid = email.isNotEmpty() && password.isNotEmpty()

    LaunchedEffect(viewState) {
        when (viewState) {
            is AuthViewState.Success -> navController.navigate(BottomNavItem.Home.route)
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome Back!",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
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
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid && viewState != AuthViewState.Loading
        ) {
            if (viewState == AuthViewState.Loading) {
                androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Login")
            }
        }

        when (val state = viewState) {
            is AuthViewState.Error -> {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = state.message, color = Color.Red)
            }
            else -> {}
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Don't have an account? Register here.",
            color = Color.Blue,
            modifier = Modifier
                .clickable { navController.navigate(AppNavItem.Register.route) }
                .padding(8.dp)
        )
    }

}
