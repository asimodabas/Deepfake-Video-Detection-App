package com.asimodabas.deepfake_video_detection_app.ui.fragment.forgot_password

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.asimodabas.deepfake_video_detection_app.R
import com.asimodabas.deepfake_video_detection_app.databinding.FragmentForgotPasswordBinding
import com.asimodabas.deepfake_video_detection_app.util.toastMessage
import com.asimodabas.deepfake_video_detection_app.util.viewBinding

class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {

    private val binding by viewBinding(FragmentForgotPasswordBinding::bind)
    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        buttonSend.setOnClickListener {
            if (binding.editTextEMail.text.isNotEmpty()) {
                val email = binding.editTextEMail.text.toString()
                viewModel.forgotPassword(email)
                observeData()
                findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
            } else {
                toastMessage(requireContext(), getString(R.string.please_enter_your_email_adress))
            }
        }
    }

    private fun observeData() {
        viewModel.success.observe(viewLifecycleOwner) { success ->
            success?.let {
                if (it) {
                    toastMessage(requireContext(), getString(R.string.confirmation_email_sent))
                }
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                toastMessage(requireContext(), error)
            }
        }
    }
}