package com.asimodabas.deepfake_video_detection_app.ui.fragment.update_password

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.asimodabas.deepfake_video_detection_app.R
import com.asimodabas.deepfake_video_detection_app.databinding.FragmentUpdatePasswordBinding
import com.asimodabas.deepfake_video_detection_app.ui.activity.MainActivity
import com.asimodabas.deepfake_video_detection_app.util.Constants.CURRENT_USER_MAIL
import com.asimodabas.deepfake_video_detection_app.util.Constants.USERS
import com.asimodabas.deepfake_video_detection_app.util.toastMessage
import com.asimodabas.deepfake_video_detection_app.util.viewBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class UpdatePasswordFragment : Fragment(R.layout.fragment_update_password) {

    private val binding by viewBinding(FragmentUpdatePasswordBinding::bind)
    private val viewModel: UpdatePasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonChangePassword.setOnClickListener {
            if (checkEmptyView()) {
                val newPassword = binding.editTextNewPassword.text.toString().trim()
                val newPasswordAgain = binding.editTextNewPasswordAgain.text.toString().trim()
                val oldPassword = binding.editTextOldPassword.text.toString().trim()
                if (oldPassword == newPassword) {
                    toastMessage(
                        requireContext(), getString(R.string.please_enter_different_password)
                    )
                } else {
                    if (newPassword == newPasswordAgain) {
                        val credential =
                            EmailAuthProvider.getCredential(CURRENT_USER_MAIL, oldPassword)
                        val user = Firebase.auth.currentUser!!
                        val map = mapOf<String, Any>("userPassw" to newPassword)
                        FirebaseDatabase.getInstance().getReference(USERS)
                            .child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(map)
                        user.reauthenticate(credential).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                viewModel.updatePassword(newPassword)
                                viewModel.updatePasswordData.observe(viewLifecycleOwner) { data ->
                                    data?.let {
                                        if (it) {
                                            toastMessage(
                                                requireContext(),
                                                getString(R.string.password_changed)
                                            )
                                            viewModel.signOut()
                                            observeDataSignOut()
                                        }
                                    }
                                }
                                viewModel.updatePasswordError.observe(viewLifecycleOwner) { error ->
                                    error?.let {
                                        toastMessage(requireContext(), it)
                                    }
                                }
                            }
                        }.addOnFailureListener {
                            it.localizedMessage?.let { it1 -> toastMessage(requireContext(), it1) }
                        }
                    } else {
                        toastMessage(requireContext(), getString(R.string.password_must_match))
                    }
                }
            } else {
                toastMessage(requireContext(), getString(R.string.fill_in_the_blanks))
            }
        }
    }

    private fun observeDataSignOut() {
        viewModel.isThereEntry.observe(viewLifecycleOwner) { login ->
            login?.let {
                if (it) {
                    activity?.let { activity ->
                        activity.startActivity(Intent(activity, MainActivity::class.java))
                        activity.finish()
                    }
                }
            }
        }
    }

    private fun checkEmptyView(): Boolean =
        binding.editTextNewPassword.text.isNotEmpty() && binding.editTextNewPasswordAgain.text.isNotEmpty() && binding.editTextOldPassword.text.isNotEmpty()
}