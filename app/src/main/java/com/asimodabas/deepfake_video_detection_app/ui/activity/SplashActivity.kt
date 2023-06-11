package com.asimodabas.deepfake_video_detection_app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.asimodabas.deepfake_video_detection_app.R
import com.asimodabas.deepfake_video_detection_app.util.Constants.COUNTDOWN_INTERVAL
import com.asimodabas.deepfake_video_detection_app.util.Constants.MILLSIN_FUTURE

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val timer = object : CountDownTimer(MILLSIN_FUTURE, COUNTDOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }
        timer.start()
    }
}