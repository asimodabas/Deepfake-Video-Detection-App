package com.asimodabas.deepfake_video_detection_app.ui.fragment.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.asimodabas.deepfake_video_detection_app.R
import com.asimodabas.deepfake_video_detection_app.databinding.FragmentSettingsBinding
import com.asimodabas.deepfake_video_detection_app.ui.activity.MainActivity
import com.asimodabas.deepfake_video_detection_app.util.Constants.SHARED_NAME
import com.asimodabas.deepfake_video_detection_app.util.UIMode
import com.asimodabas.deepfake_video_detection_app.util.toastMessage
import com.asimodabas.deepfake_video_detection_app.util.viewBinding
import kotlinx.coroutines.launch
import java.util.*

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding by viewBinding(FragmentSettingsBinding::bind)
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var settingDataStore: SettingDataStore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingDataStore = SettingDataStore(requireContext())
        observeUIPreferences()
        initView()

        with(binding) {
            linearTitulo.setOnClickListener {
                if (linearLanguage.isGone) {
                    TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                    linearLanguage.isGone = false
                } else {
                    TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                    linearLanguage.isGone = true
                }
            }

            tvEnglish.setOnClickListener {
                languageEnglish()
                toastMessage(requireContext(), getString(R.string.language_changed_en))
            }
            tvTurkish.setOnClickListener {
                languageTurkish()
                toastMessage(requireContext(), getString(R.string.language_changed_tr))
            }
            logoutButton.setOnClickListener {
                viewModel.signOut()
                observeDataSignOut()
                requireContext().getSharedPreferences(SHARED_NAME, 0).edit().clear().apply()
            }
        }
    }

    private fun observeDataSignOut() {
        activity?.let { activity ->
            activity.startActivity(Intent(activity, MainActivity::class.java))
            activity.finish()
        }
    }

    private fun observeUIPreferences() {
        settingDataStore.uiModeFlow.asLiveData().observe(viewLifecycleOwner) { uiMode ->
            setCheckedMode(uiMode)
        }
    }

    private fun setCheckedMode(uiMode: UIMode?) {
        when (uiMode) {
            UIMode.LIGHT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.applyModeSwitch.isChecked = false
            }
            UIMode.DARK -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.applyModeSwitch.isChecked = true
            }
            else -> {}
        }
    }

    private fun initView() {
        binding.applyModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                when (isChecked) {
                    true -> settingDataStore.setDarkMode(UIMode.DARK)
                    false -> settingDataStore.setDarkMode(UIMode.LIGHT)
                }
            }
        }
    }

    private fun languageEnglish() {
        setLocale("en")
        ActivityCompat.recreate(requireActivity())
    }

    private fun languageTurkish() {
        setLocale("tr")
        ActivityCompat.recreate(requireActivity())
    }

    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        requireContext().resources.updateConfiguration(
            config, requireContext().resources.displayMetrics
        )

        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putString("mylang", language)
        }.apply()
    }
}