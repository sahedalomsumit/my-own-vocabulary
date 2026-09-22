package com.sahed.my_own_vocabulary.data.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.sahed.my_own_vocabulary.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class AuthRepository(private val context: Context) {
    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    private val credentialManager = CredentialManager.create(context)

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    init {
        try {
            _currentUser.value = auth.currentUser
            auth.addAuthStateListener { firebaseAuth ->
                _currentUser.value = firebaseAuth.currentUser
            }
        } catch (e: Exception) {
            Log.w("AuthRepository", "Firebase Auth initialization listener error: ${e.message}")
        }
    }

    val isUserSignedIn: Boolean
        get() = try {
            auth.currentUser != null
        } catch (e: Exception) {
            false
        }

    /**
     * Executes modern Google Sign-In using Android Credential Manager and Firebase Auth.
     */
    suspend fun signInWithGoogle(activity: Activity): Result<FirebaseUser?> {
        return try {
            val serverClientId = try {
                context.getString(R.string.default_web_client_id)
            } catch (e: Exception) {
                "288077659729-defaultclientid.apps.googleusercontent.com"
            }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )

            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(authCredential).await()
                _currentUser.value = authResult.user
                Result.success(authResult.user)
            } else {
                Result.failure(Exception("Unexpected credential returned: ${credential::class.java.simpleName}"))
            }
        } catch (e: GetCredentialCancellationException) {
            Log.d("AuthRepository", "User cancelled Google Sign-In")
            Result.failure(e)
        } catch (e: GetCredentialException) {
            Log.e("AuthRepository", "Credential Manager error: ${e.message}", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Sign-in error: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Allows anonymous/guest sign-in for testing or offline exploration.
     */
    suspend fun signInAsGuest(): Result<FirebaseUser?> {
        return try {
            val authResult = auth.signInAnonymously().await()
            _currentUser.value = authResult.user
            Result.success(authResult.user)
        } catch (e: Exception) {
            Log.w("AuthRepository", "Guest sign-in fallback: ${e.message}")
            // Proceed as local guest if anonymous auth is not enabled on the Firebase console
            Result.success(null)
        }
    }

    /**
     * Signs out of Firebase and clears local credential state.
     */
    fun signOut() {
        try {
            auth.signOut()
            _currentUser.value = null
        } catch (e: Exception) {
            Log.e("AuthRepository", "Sign out error: ${e.message}")
        }
    }
}
