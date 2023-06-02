package com.asimodabas.deepfake_video_detection_app.ui.fragment.settings

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsViewModel : ViewModel() {

    private val auth = Firebase.auth

    fun signOut() {
        userSignOutControl()
    }

    private fun userSignOutControl() {
        val activeUser = auth.currentUser
        if (activeUser != null) {
            auth.signOut()
        }
    }
}