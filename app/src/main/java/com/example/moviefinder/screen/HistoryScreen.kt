package com.example.moviefinder.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviefinder.auth.AuthViewModel
import com.example.moviefinder.firebase.HistoryViewModel
import com.example.moviefinder.firebase.MovieViewModel
import com.example.moviefinder.model.History
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier
){
    val authVM = viewModel<AuthViewModel>()
    val userId = authVM.currentUser?.uid ?: ""
    val historyVM = viewModel<HistoryViewModel>()
    val histories by historyVM.getAllById(userId).collectAsState(initial = emptyList())
    var sortedBy by remember { mutableStateOf("descending") }

    if (histories.isEmpty()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(top = 330.dp),
        ){
            Text("No viewing history",
                fontSize = 21.sp,
                textAlign = TextAlign.Center,
                modifier = modifier.fillMaxWidth()
            )
        }
    } else {
        var showAllDeleteDialog by remember { mutableStateOf(false) }

        if (showAllDeleteDialog && histories.size >= 1) {
            AlertDialog(
                icon = { Icon(Icons.Default.Warning, contentDescription = null) },
                onDismissRequest = { showAllDeleteDialog = false },
                title = { Text("Confirm Deletion") },
                text = { Text("Do you want to delete all history?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            historyVM.deleteAllHistories(userId)
                            showAllDeleteDialog = false
                        }
                    ) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { showAllDeleteDialog = false }) { Text("Cancel") }
                },
            )
        }

        Column (
            modifier = modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier.height(20.dp))
            Text("Viewing history",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = modifier.fillMaxWidth()
            )
            Spacer(modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier.fillMaxWidth()
            ) {
                Text("Total views: ${histories.size}")
                Row() {
                    TextButton(
                        onClick = { sortedBy = if (sortedBy == "descending") "ascending" else "descending" }
                    ) {
                        Text(sortedBy)
                    }
                    IconButton(
                        onClick = { showAllDeleteDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Delete all"
                        )}
                    }
                }
                Spacer(modifier.height(25.dp))

            val sortedHistories = if (sortedBy == "descending")
                histories.sortedByDescending { history -> history.viewedAt }
            else histories.sortedBy { history -> history.viewedAt }

            sortedHistories.forEach { history ->
                HistoryItem(
                    history = history,
                    historyVM = historyVM,
                    userId = userId,
                )
            }
        }
    }
}

@Composable
fun HistoryItem(
    history: History,
    historyVM: HistoryViewModel,
    userId: String,
    modifier: Modifier = Modifier
){
    val movieVM = viewModel<MovieViewModel>()
    val movie by movieVM.getById(history.movieId).collectAsState(initial = null)
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this history?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        historyVM.deleteHistory(history)
                        showDeleteDialog = false
                    }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    movie?.let { it ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E))
                .padding(12.dp)
        ) {
            Column(modifier = modifier) {
                Text(it.title,
                    color = Color.White,
                    fontSize = 15.sp
                )
                Text(formatTimestamp(history.viewedAt),
                    color = Color.White,
                    fontSize = 15.sp
                )
            }
            Column() {
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                    )
                }
            }
        }
        Spacer(modifier.height(18.dp))
    }
}

fun formatTimestamp(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("MMM d, yyyy h:mm:ss a")
    return sdf.format(timestamp.toDate())
}