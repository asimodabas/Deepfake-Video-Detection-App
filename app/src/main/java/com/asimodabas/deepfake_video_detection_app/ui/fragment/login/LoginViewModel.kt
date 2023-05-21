package com.asimodabas.deepfake_video_detection_app.ui.fragment.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val success = MutableLiveData<Boolean>()
    private val auth = Firebase.auth

    fun loginToApp(email: String, password: String) {
        loginUsingFirebase(email, password)
    }

    private fun loginUsingFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(
            email, password
        ).addOnSuccessListener {
            success.value = true
        }.addOnFailureListener { error ->
            errorMessage.value = error.localizedMessage
        }
    }
}