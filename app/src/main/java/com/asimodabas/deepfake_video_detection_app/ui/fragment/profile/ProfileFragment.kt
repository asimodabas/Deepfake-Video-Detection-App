package com.asimodabas.deepfake_video_detection_app.ui.fragment.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.asimodabas.deepfake_video_detection_app.R
import com.asimodabas.deepfake_video_detection_app.databinding.FragmentProfileBinding
import com.asimodabas.deepfake_video_detection_app.util.toastMessage
import com.asimodabas.deepfake_video_detection_app.util.viewBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val binding by viewBinding(FragmentProfileBinding::bind)
    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getProfileInfo()

        binding.EditProfileTextView.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editFragment)
        }
    }

    private fun getProfileInfo() {
        viewModel.pullUserInfo().observe(viewLifecycleOwner) { dataConfirm ->
            dataConfirm?.let {
                binding.userModel = dataConfirm
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                toastMessage(requireContext(), it)
            }
        }
    }
}