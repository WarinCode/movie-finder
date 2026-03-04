package com.example.firebasekotlin.screen

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviefinder.R
import com.example.moviefinder.auth.AuthViewModel
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption

@Composable
fun SignInScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    var showForgotDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }

    val authVM = viewModel<AuthViewModel>()
    val authState by authVM.authState.collectAsState()
    LaunchedEffect(authState) {
        when(authState) {
            is AuthViewModel.AuthState.Success -> {
                authVM.resetState()
                onLoginSuccess()
            }
            else -> {}
        }
    }

    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = {
                showForgotDialog = false
                resetEmail = ""
            },
            title = { Text("Forgot password") },
            text = {
                Column {
                    Text("กรอก Email ที่ใช้สมัครสมาชิก\nระบบจะส่งลิงก์รีเซ็ตรหัสผ่านให้")
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Email") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        authVM.resetPassword(resetEmail)
                        showForgotDialog = false
                        resetEmail = ""
                    },
                    enabled = resetEmail.isNotBlank(),
                ) { Text("ส่ง Email", color = Color(0xFF6D9E51)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showForgotDialog = false
                    resetEmail = ""
                }) { Text("ยกเลิก", color = Color.Gray) }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Movie", fontSize = 60.sp, fontWeight = FontWeight.Bold)
        Text("Finder", fontSize = 54.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(45.dp))

        //------------------- TextField กรอก Email และ Password -------------------
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter your email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Visibility"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Enter your password") },
            visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = if (passVisible) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = "Visibility"
                    )
                }
            },
            trailingIcon = {
                IconButton(onClick = { passVisible = !passVisible }) {
                    Icon(
                        imageVector = if (passVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Visibility"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        //------------------- ปุ่มลืมรหัสผ่าน -------------------
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = {
                resetEmail = email
                showForgotDialog = true
            }) {
                Text("Forgot password?", color = Color.Blue)
            }
        }
        Spacer(Modifier.height(12.dp))

        //------------------- ปุ่มเข้าสู่ระบบ -------------------
        Button(
            onClick = {
                authVM.loginWithEmail(email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(6.dp),
            enabled = email.isNotBlank() && password.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF282828),
                contentColor = Color.White
            )
        ) { Text("Sign In", color = Color.White, fontWeight = FontWeight.Bold) }

        Spacer(Modifier.height(12.dp))

        //------------------- ปุ่มไปหน้าลงทะเบียน -------------------
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 35.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "or sign in with",
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color.Gray,
                fontSize = 14.sp
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }


        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = modifier.width(250.dp),
        ) {
            IconButton(
                onClick = {
                    authVM.signInWithGoogle(context)
                },
                modifier = Modifier
                    .size(56.dp)
                    .border(1.dp, Color.LightGray, CircleShape)
            ) {
                Image(
                    painter = painterResource(R.drawable.google),
                    contentDescription = "Sign in with Google",
                    modifier = Modifier.size(30.dp)
                )
            }
            IconButton(
                onClick = {
                    authVM.signInWithMicrosoft(activity)
                },
                modifier = Modifier
                    .size(56.dp)
                    .border(1.dp, Color.LightGray, CircleShape)
            ) {
                Image(
                    painter = painterResource(R.drawable.microsoft),
                    contentDescription = "Sign in with Microsoft",
                    modifier = Modifier.size(30.dp)
                )
            }
            IconButton(
                onClick = {
                    authVM.signInWithGithub(activity)
                },
                modifier = Modifier
                    .size(56.dp)
                    .border(1.dp, Color.LightGray, CircleShape)
            ) {
                Image(
                    painter = painterResource(R.drawable.github),
                    contentDescription = "Sign in with Github",
                    modifier = Modifier.size(38.dp)
                )
            }
        }

        Spacer(modifier.height(30.dp))
        TextButton(onClick = onNavigateToRegister) {
            Text("Create Account", color = Color.Blue)
        }
    }
}