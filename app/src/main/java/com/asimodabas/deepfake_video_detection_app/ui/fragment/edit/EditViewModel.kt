package com.asimodabas.deepfake_video_detection_app.ui.fragment.edit

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.asimodabas.deepfake_video_detection_app.model.UserModel
import com.asimodabas.deepfake_video_detection_app.util.Constants.IMAGES
import com.asimodabas.deepfake_video_detection_app.util.Constants.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*

class EditViewModel : ViewModel() {

    val changesSaved = MutableLiveData<Boolean>()
    val deleteAccountConfirmation = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    private val auth = Firebase.auth
    private val storage = Firebase.storage
    private var realTimedb = FirebaseDatabase.getInstance().getReference(USERS)
    private val userUid = auth.currentUser?.uid
    private var liveData: MutableLiveData<UserModel>? = null
    private lateinit var databaseReference: DatabaseReference

    fun updateProfile(
        name: String,
        number: String,
        surname: String,
        selectedImage: Uri?,
        dateOfBirthDateText: String
    ) {
        updateProfileFirebase(name, number, surname, selectedImage, dateOfBirthDateText)
    }

    fun deleteAccount() {
        deleteEverything()
    }

    fun pullUserInfo(): LiveData<UserModel> {
        if (liveData == null) liveData = MutableLiveData()
        databaseReference = realTimedb.child(userUid!!)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userModel = snapshot.getValue(UserModel::class.java)
                    liveData!!.postValue(userModel)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        return liveData!!
    }

    private fun updateProfileFirebase(
        newName: String,
        newNumber: String,
        newSurname: String,
        newSelectedImage: Uri?,
        newdateOfBirthDateText: String
    ) {
        if (newSelectedImage != null) {
            var imageReferenceLink: String?
            val profileImageName: String?
            val reference = storage.reference
            val uuid = UUID.randomUUID()
            profileImageName = "${uuid}.jpeg"
            val imageReference = reference.child(IMAGES).child(profileImageName)
            imageReference.putFile(newSelectedImage).addOnSuccessListener {
                val uploadedImageReference = reference.child(IMAGES).child(profileImageName)
                uploadedImageReference.downloadUrl.addOnSuccessListener { uri ->
                    imageReferenceLink = uri.toString()
                    if (imageReferenceLink != null) {
                        updateDatabase(
                            newName,
                            newNumber,
                            newSurname,
                            newdateOfBirthDateText,
                            imageReferenceLink!!
                        )
                    }
                }
            }.addOnSuccessListener {}.addOnFailureListener { exception ->
                errorMessage.value = exception.localizedMessage
            }
        } else {
            updateNullDatabase(
                newName, newNumber, newSurname, newdateOfBirthDateText
            )
        }
    }

    private fun updateDatabase(
        newName: String,
        newNumber: String,
        newSurname: String,
        newdateOfBirthDateText: String,
        imageReferenceLink: String,
    ) {
        val dataMap = HashMap<String, Any>()
        dataMap["name"] = newName
        dataMap["surname"] = newSurname
        dataMap["number"] = newNumber
        dataMap["dateOfBirth"] = newdateOfBirthDateText
        dataMap["profileImage"] = imageReferenceLink
        realTimedb.child(userUid!!).updateChildren(dataMap).addOnCompleteListener { registration ->
            if (registration.isSuccessful) {
                changesSaved.value = true
            }
        }.addOnFailureListener { error ->
            errorMessage.value = error.localizedMessage
        }
    }

    private fun updateNullDatabase(
        newName: String,
        newNumber: String,
        newSurname: String,
        newdateOfBirthDateText: String,
    ) {
        val dataMap = HashMap<String, Any>()
        dataMap["name"] = newName
        dataMap["surname"] = newSurname
        dataMap["number"] = newNumber
        dataMap["dateOfBirth"] = newdateOfBirthDateText
        realTimedb.child(userUid!!).updateChildren(dataMap).addOnCompleteListener { registration ->
            if (registration.isSuccessful) {
                changesSaved.value = true
            }
        }.addOnFailureListener { error ->
            errorMessage.value = error.localizedMessage
        }
    }

    private fun deleteEverything() {
        val storageRef = storage.reference
        if (userUid != null) {
            realTimedb.child(
                FirebaseAuth.getInstance().uid.toString()
            )
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val myUserModel = snapshot.getValue(UserModel::class.java)
                        if (myUserModel?.profileImageName!!.isNotEmpty()) {
                            realTimedb.child(userUid).removeValue().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    storageRef.child(IMAGES).child(myUserModel.profileImageName)
                                        .delete()
                                    Firebase.auth.signOut()
                                    deleteAccountConfirmation.value = true
                                }
                            }.addOnFailureListener {
                                errorMessage.value = it.localizedMessage
                            }
                        } else {
                            realTimedb.child(userUid).removeValue().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Firebase.auth.signOut()
                                    deleteAccountConfirmation.value = true
                                }
                            }.addOnFailureListener {
                                errorMessage.value = it.localizedMessage
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}