package com.example.moviefinder.screen

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moviefinder.auth.AuthViewModel
import com.example.moviefinder.font.poppinsFamily

@Composable
fun UserProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
){
    val authVM = viewModel<AuthViewModel>()
    val user = authVM.currentUser
    val defaultAvatar = "https://i.pinimg.com/736x/9e/83/75/9e837528f01cf3f42119c5aeeed1b336.jpg"
    var showDeleteAccountDailog by remember { mutableStateOf(false) }

    if (showDeleteAccountDailog) {
        AlertDialog(
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null
                )
            },
            onDismissRequest = { showDeleteAccountDailog = false },
            title = {
                Text("Confirm Account Deletion", fontFamily = poppinsFamily)
            },
            text = {
                Text("Do you want to delete your account?", fontFamily = poppinsFamily)
            },
            confirmButton = {
                TextButton(onClick = {
                    user?.delete()
                    showDeleteAccountDailog = false
                    navController.navigate("signin") {
                        popUpTo("signin") { inclusive = true }
                    }
                }) { Text("Confirm", fontFamily = poppinsFamily) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDailog = false }) { Text("Cancel", fontFamily = poppinsFamily) }
            },
        )
    }

    user?.let { userInfo ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = modifier.fillMaxWidth(),
            ) {
                AsyncImage(
                    model = userInfo.photoUrl ?: defaultAvatar,
                    contentDescription = userInfo.displayName,
                    modifier = modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .border(
                            width= 5.dp,
                            color = Color(0xFFD3D1D1),
                            shape = RoundedCornerShape(100.dp)
                        )
                )
            }

            Spacer(modifier.height(30.dp))

            TextField(
                value = userInfo.displayName ?: "",
                onValueChange = {},
                label = { Text("Name", fontFamily = poppinsFamily) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Name"
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            )
            TextField(
                value = userInfo.email ?: "",
                onValueChange = {},
                label = { Text("Email", fontFamily = poppinsFamily) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AlternateEmail,
                        contentDescription = "Email"
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            )
            TextField(
                value = userInfo.uid,
                onValueChange = {},
                label = { Text("ID", fontFamily = poppinsFamily) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.PermIdentity,
                        contentDescription = "ID"
                    )
                },
                modifier = modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            )
            TextField(
                value = userInfo.isEmailVerified.toString(),
                onValueChange = {},
                label = { Text("Email Verifield", fontFamily = poppinsFamily) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.MarkEmailRead,
                        contentDescription = "Verified"
                    )
                },
                modifier = modifier.fillMaxWidth()
            )

            Spacer(modifier.height(30.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        authVM.logout()
                        navController.navigate("signin") {
                            popUpTo("signin") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1A1919)),
                    modifier = modifier
                        .fillMaxWidth()
                        .height(50.dp)
                    ){
                    Text("Sign Out",
                        fontFamily = poppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }
                Spacer(modifier.height(10.dp))
                TextButton(
                    onClick = { showDeleteAccountDailog = true }
                ) {
                    Text("Delete Account",
                        fontFamily = poppinsFamily,
                        color = Color.Red,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}