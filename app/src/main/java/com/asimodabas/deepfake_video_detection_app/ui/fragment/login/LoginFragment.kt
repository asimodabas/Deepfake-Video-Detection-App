package com.asimodabas.deepfake_video_detection_app.ui.fragment.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.asimodabas.deepfake_video_detection_app.R
import com.asimodabas.deepfake_video_detection_app.databinding.FragmentLoginBinding
import com.asimodabas.deepfake_video_detection_app.ui.activity.FlowActivity
import com.asimodabas.deepfake_video_detection_app.util.closeKeyboard
import com.asimodabas.deepfake_video_detection_app.util.toastMessage
import com.asimodabas.deepfake_video_detection_app.util.viewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var auth: FirebaseAuth
    private val viewModel: LoginViewModel by viewModels()
    private val binding by viewBinding(FragmentLoginBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            activity?.let {
                it.startActivity(Intent(it, FlowActivity::class.java))
                it.finish()
            }
        }
        with(binding) {
            loginButton.setOnClickListener {
                closeKeyboard(requireActivity(), requireContext())
                if (dataControl()) {
                    val email = loginEmailEditText.text.toString().trim()
                    val password = loginPasswordEditText.text.toString().trim()
                    viewModel.loginToApp(email, password)
                    observeData()
                } else {
                    toastMessage(requireContext(), getString(R.string.fill_in_the_blanks))
                }
            }
            createButton.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_createFragment)
            }
            forgotPassTextView.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
            }
        }
    }

    private fun observeData() {
        viewModel.success.observe(viewLifecycleOwner) { success ->
            success?.let { value ->
                if (value) {
                    activity?.let {
                        it.startActivity(Intent(it, FlowActivity::class.java))
                        it.finish()
                    }
                }
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                toastMessage(requireContext(), it)
            }
        }
    }

    private fun dataControl(): Boolean =
        binding.loginEmailEditText.text!!.isNotEmpty() && binding.loginPasswordEditText.text!!.isNotEmpty()
}