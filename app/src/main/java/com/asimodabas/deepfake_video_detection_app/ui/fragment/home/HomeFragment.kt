package com.asimodabas.deepfake_video_detection_app.ui.fragment.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.asimodabas.deepfake_video_detection_app.R
import com.asimodabas.deepfake_video_detection_app.databinding.FragmentHomeBinding
import com.asimodabas.deepfake_video_detection_app.util.viewBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}