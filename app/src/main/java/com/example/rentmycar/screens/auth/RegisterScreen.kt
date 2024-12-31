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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.rentmycar.navigation.BottomNavItem
import com.example.rentmycar.viewmodel.AuthViewModel
import com.example.rentmycar.viewmodel.AuthViewState

@Composable
fun RegisterScreen(navController: NavHostController, viewModel: AuthViewModel = hiltViewModel()) {

    val viewState by viewModel.viewState.collectAsState()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    LaunchedEffect(viewState) {
        when (viewState) {
            is AuthViewState.Success -> navController.navigate(BottomNavItem.Home.route)
            else -> {}
        }
    }

    val isFormValid = firstName.isNotEmpty() &&
            lastName.isNotEmpty() &&
            username.isNotEmpty() &&
            email.isNotEmpty() &&
            password.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create an Account",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
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
            onClick = { viewModel.register(firstName, lastName, username, email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid && viewState != AuthViewState.Loading
        ) {
            if (viewState == AuthViewState.Loading) {
                androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Register")
            }
        }
        when (val state = viewState) {
            is AuthViewState.Error -> {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = state.message, color = androidx.compose.ui.graphics.Color.Red)
            }

            else -> {}
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Already have an account? Login here.",
            color = androidx.compose.ui.graphics.Color.Blue,
            modifier = Modifier
                .clickable { navController.navigate("login") }
                .padding(8.dp)
        )
    }
}
