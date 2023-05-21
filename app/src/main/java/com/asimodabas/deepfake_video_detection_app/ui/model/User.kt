package com.asimodabas.deepfake_video_detection_app.ui.model

class User(
    val name: String,
    val surname: String,
    val email: String,
    val userUid: String,
    val number: String,
    val userPassw: String,
    val dateOfBirth: String,
    var profileImage: String?,
    var profileImageName: String?,
    val registrationTime: String,
)