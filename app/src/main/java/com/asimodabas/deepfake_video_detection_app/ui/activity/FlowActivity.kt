package com.asimodabas.deepfake_video_detection_app.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.asimodabas.deepfake_video_detection_app.R
import com.asimodabas.deepfake_video_detection_app.databinding.ActivityFlowBinding

class FlowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlowBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.fContainerFlow)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.profileFragment, R.id.homeFragment, R.id.settingsFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}