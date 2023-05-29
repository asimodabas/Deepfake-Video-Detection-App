package com.asimodabas.deepfake_video_detection_app.model

import androidx.databinding.BindingAdapter
import com.asimodabas.deepfake_video_detection_app.R
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

data class UserModel(
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val userUid: String = "",
    val number: String = "",
    val userPassw: String = "",
    val dateOfBirth: String = "",
    var profileImage: String = "",
    var profileImageName: String = "",
    val registrationTime: String = ""
) {
    companion object {
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view: CircleImageView, imageUrl: String?) {
            if (imageUrl.isNullOrBlank()) {
                Glide.with(view.context).load(R.drawable.ic_logo).into(view)
            } else {
                Glide.with(view.context).load(imageUrl).into(view)
            }
        }
    }
}

