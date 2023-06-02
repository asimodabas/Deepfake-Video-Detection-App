package com.asimodabas.deepfake_video_detection_app.ui.fragment.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.asimodabas.deepfake_video_detection_app.R
import com.asimodabas.deepfake_video_detection_app.databinding.FragmentHomeBinding
import com.asimodabas.deepfake_video_detection_app.util.viewBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val viewModel: HomeViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webViewSetup()
    }

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun webViewSetup() = with(binding){
        homeWebView.webViewClient = WebViewClient()
        homeWebView.apply {

            loadUrl("https://www.google.com.tr/")
            //10.0.2.2:8000

            settings.javaScriptEnabled = true
            settings.safeBrowsingEnabled= true
        }
    }
}