package com.example.moviefinder.auth

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.moviefinder.util.dotenv
import com.google.firebase.auth.OAuthProvider

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        object ResetPasswordSent : AuthState()
        data class Error(val message: String) : AuthState()
    }

    //------------------ ลงทะเบียนใช้งาน ------------------
    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "เกิดข้อผิดพลาด")
            }
        }
    }

    //------------------ รีเซตรหัสผ่าน ------------------
    fun resetPassword(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.sendPasswordResetEmail(email).await()
                _authState.value = AuthState.ResetPasswordSent
            } catch (e: FirebaseAuthException) {
                val message = when (e.errorCode) {
                    "ERROR_USER_NOT_FOUND" -> "ไม่พบบัญชีนี้ในระบบ"
                    "ERROR_INVALID_EMAIL"  -> "รูปแบบ Email ไม่ถูกต้อง"
                    else -> "เกิดข้อผิดพลาด กรุณาลองใหม่"
                }
                _authState.value = AuthState.Error(message)
            }
        }
    }

    //------------------ ล็อกอินด้วยอีเมล์ ------------------
    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "เกิดข้อผิดพลาด")
            }
        }
    }



    //------------------ ออกจากระบบ ------------------
    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val credentialManager = CredentialManager.create(context)

                // https://console.cloud.google.com/auth/clients
                val signInWithGoogleOption = GetSignInWithGoogleOption
                    .Builder(dotenv["GOOGLE_WEB_CLIENT_ID"])
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(signInWithGoogleOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val firebaseCredential = GoogleAuthProvider.getCredential(
                        googleIdTokenCredential.idToken, null
                    )
                    auth.signInWithCredential(firebaseCredential).await()
                    _authState.value = AuthState.Success
                }

            } catch (e: GetCredentialException) {
                _authState.value = AuthState.Error("Error: ${e.message}")
            }
        }
    }

    fun signInWithGithub(activity: Activity){
        val provider = OAuthProvider.newBuilder("github.com")
        provider.scopes = listOf("user:email")

        _authState.value = AuthState.Loading

        val pending = auth.pendingAuthResult
        if (pending != null) {
            pending.addOnSuccessListener {
                _authState.value = AuthState.Success
            }.addOnFailureListener {
                _authState.value = AuthState.Error(it.message ?: "GitHub Login Failed")
            }
        } else {
            auth.startActivityForSignInWithProvider(activity, provider.build())
                .addOnSuccessListener { authResult ->
                    _authState.value = AuthState.Success
                }
                .addOnFailureListener { e ->
                    _authState.value = AuthState.Error(e.message ?: "GitHub Login Error")
                }
        }
    }

    fun signInWithMicrosoft(activity: Activity){
        val provider = OAuthProvider.newBuilder("microsoft.com")

        _authState.value = AuthState.Loading

        val pending = auth.pendingAuthResult
        if (pending != null) {
            pending.addOnSuccessListener {
                _authState.value = AuthState.Success
            }.addOnFailureListener {
                _authState.value = AuthState.Error(it.message ?: "Microsoft Login Failed")
            }
        } else {
            auth.startActivityForSignInWithProvider(activity, provider.build())
                .addOnSuccessListener { authResult ->
                    _authState.value = AuthState.Success
                }
                .addOnFailureListener { e ->
                    _authState.value = AuthState.Error(e.message ?: "GitHub Login Error")
                }
        }
    }
}