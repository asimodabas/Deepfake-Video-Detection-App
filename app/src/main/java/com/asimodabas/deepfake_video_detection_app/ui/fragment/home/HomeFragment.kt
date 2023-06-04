package com.asimodabas.deepfake_video_detection_app.ui.fragment.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.asimodabas.deepfake_video_detection_app.R
import com.asimodabas.deepfake_video_detection_app.databinding.FragmentHomeBinding
import com.asimodabas.deepfake_video_detection_app.util.viewBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<Intent>
    private var uploadMessage: ValueCallback<Array<Uri>>? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    Activity.RESULT_OK -> goToUpload(result)
                    Activity.RESULT_CANCELED -> goToUpload(result)
                }
            }
        webViewSetup()
    }

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun webViewSetup() = with(binding) {

        homeWebView.webViewClient = WebViewClient()
        homeWebView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                uploadMessage?.onReceiveValue(null)
                uploadMessage = null

                uploadMessage = filePathCallback
                fileChooserParams?.createIntent()
                val intent = fileChooserParams!!.createIntent()
                try {
                    permissionLauncher.launch(intent)
                } catch (e: ActivityNotFoundException) {
                    uploadMessage = null
                    Toast.makeText(requireContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG)
                        .show()
                    return false
                }
                return true
            }
        }

        homeWebView.apply {
            loadUrl("10.0.2.2:8000")
            settings.allowContentAccess = true
            settings.allowFileAccess = true
            settings.javaScriptEnabled = true
            settings.safeBrowsingEnabled = true
        }
    }

    private fun goToUpload(result: ActivityResult) {
        if (uploadMessage == null) return
        uploadMessage?.onReceiveValue(
            WebChromeClient.FileChooserParams.parseResult(
                result.resultCode, result.data
            )
        )
        uploadMessage = null
    }
}