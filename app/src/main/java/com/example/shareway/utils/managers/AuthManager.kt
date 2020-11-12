package com.example.shareway.utils.managers

import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.shareway.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

object AuthManager {

    private const val TAG = "AuthManager"


    fun firebaseAuthWithGoogle(
        googleIdToken: String,
        auth: FirebaseAuth,
        activity: FragmentActivity,
        onSuccess: (user: FirebaseUser) -> Unit,
        onFailure: (message: Exception) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    onSuccess(auth.currentUser!!)
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    onFailure(task.exception!!)
//                    Snackbar.make(
//                        requireView(),
//                        "Authentication Failed.",
//                        Snackbar.LENGTH_SHORT
//                    ).show()
//                    updateUI(null)
                }

                // ...
            }
    }


    fun buildSignInRequest(isSignIn: Boolean, context: Context): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(
                        context.getString(R.string.default_web_client_id)
                    )
                    /**
                     * Only show accounts previously used to sign in
                     * On sign up set to false
                     *
                     * **/
                    .setFilterByAuthorizedAccounts(isSignIn)
                    .build()
            )
            .build()
    }

    fun startLoginProcess(
        oneTapClient: SignInClient,
        signInRequest: BeginSignInRequest,
        onSuccess: (
            signInRequest: BeginSignInResult
        ) -> Unit,
        onFailure: (error: java.lang.Exception) -> Unit
    ) {
        // Check if the user has saved credentials on our app
        // and display the One Tap UI
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                onSuccess(result)

            }
            .addOnFailureListener { e ->
                onFailure(e)
                // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
                Log.e(TAG, "No saved credentials: ${e.localizedMessage}")
            }
    }
}