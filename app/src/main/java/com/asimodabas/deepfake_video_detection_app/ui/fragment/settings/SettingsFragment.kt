package com.asimodabas.deepfake_video_detection_app.ui.fragment.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.asimodabas.deepfake_video_detection_app.R
import com.asimodabas.deepfake_video_detection_app.databinding.FragmentSettingsBinding
import com.asimodabas.deepfake_video_detection_app.util.viewBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding by viewBinding(FragmentSettingsBinding::bind)
    private val viewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}