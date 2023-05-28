package com.asimodabas.deepfake_video_detection_app.ui.fragment.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.asimodabas.deepfake_video_detection_app.databinding.FragmentProfileBinding
import com.asimodabas.deepfake_video_detection_app.util.viewBinding

class ProfileFragment : Fragment() {

    private val binding by viewBinding(FragmentProfileBinding::bind)
    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}