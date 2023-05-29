package com.asimodabas.deepfake_video_detection_app.ui.fragment.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.asimodabas.deepfake_video_detection_app.model.UserModel
import com.asimodabas.deepfake_video_detection_app.util.Constants.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileViewModel : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    private val getUuid = FirebaseAuth.getInstance().uid.toString()
    private var liveData: MutableLiveData<UserModel>? = null
    private lateinit var databaseReference: DatabaseReference

    fun pullUserInfo(): LiveData<UserModel> {
        if (liveData == null) liveData = MutableLiveData()
        databaseReference =
            FirebaseDatabase.getInstance().getReference(USERS).child(getUuid)
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
}