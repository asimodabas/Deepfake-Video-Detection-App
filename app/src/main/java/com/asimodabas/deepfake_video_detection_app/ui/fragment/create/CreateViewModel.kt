package com.asimodabas.deepfake_video_detection_app.ui.fragment.create

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.asimodabas.deepfake_video_detection_app.model.User
import com.asimodabas.deepfake_video_detection_app.util.Constants.IMAGES
import com.asimodabas.deepfake_video_detection_app.util.Constants.USERS
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*

class CreateViewModel : ViewModel() {

    val errorMessage = MutableLiveData<String?>()
    val dataConfirmation = MutableLiveData<Boolean>()
    private var auth = Firebase.auth
    private val storage = Firebase.storage
    private var realTimedb = FirebaseDatabase.getInstance().getReference(USERS)

    fun registerToApp(
        email: String,
        number: String,
        password: String,
        name: String,
        surname: String,
        selectedImage: Uri?,
        dateofBirth: String,
        userPassw: String,
    ) {
        registerToAppUsingFirebase(
            email,
            password,
            name,
            surname,
            number,
            selectedImage,
            dateofBirth,
            userPassw,
        )
    }

    private fun registerToAppUsingFirebase(
        email: String,
        password: String,
        name: String,
        surname: String,
        number: String,
        selectedImage: Uri?,
        dateofBirth: String,
        userPassw: String,
    ) {
        errorMessage.value = null
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { authState ->
            if (authState.isSuccessful) {
                val sdf = SimpleDateFormat("dd-M-yyyy")
                val registrationTime = sdf.format(Date())
                val activeUserUid = auth.currentUser!!.uid
                if (selectedImage != null) {
                    var imageReferenceLink: String?
                    val profileImageName: String?
                    val reference = storage.reference
                    val uuid = UUID.randomUUID()
                    profileImageName = "${uuid}.jpeg"
                    val imageReference = reference.child(IMAGES).child(profileImageName)
                    imageReference.putFile(selectedImage).addOnSuccessListener {
                        val uploadedImageReference = reference.child(IMAGES).child(profileImageName)
                        uploadedImageReference.downloadUrl.addOnSuccessListener { uri ->
                            imageReferenceLink = uri.toString()
                            if (imageReferenceLink != null) {
                                realTimedb.child(activeUserUid).setValue(
                                    User(
                                        name,
                                        surname,
                                        email,
                                        activeUserUid,
                                        number,
                                        userPassw,
                                        dateofBirth,
                                        imageReferenceLink,
                                        profileImageName,
                                        registrationTime
                                    )
                                )
                            }
                        }
                    }.addOnSuccessListener {
                        dataConfirmation.value = true
                    }.addOnFailureListener { exception ->
                        errorMessage.value = exception.localizedMessage
                    }
                } else {
                    realTimedb.child(activeUserUid).setValue(
                        User(
                            name,
                            surname,
                            email,
                            activeUserUid,
                            number,
                            userPassw,
                            dateofBirth,
                            null,
                            null,
                            registrationTime
                        )
                    )
                }
            }
        }.addOnSuccessListener {
            dataConfirmation.value = true
        }.addOnFailureListener { authError ->
            errorMessage.value = authError.localizedMessage
        }
    }
}