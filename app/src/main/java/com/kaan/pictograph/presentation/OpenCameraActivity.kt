package com.kaan.pictograph.presentation

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Secure
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kaan.pictograph.R
import com.kaan.pictograph.data.model.TokenRequest
import com.kaan.pictograph.databinding.ActivityOpencameraBinding
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OpenCameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOpencameraBinding
    private val viewModel: OpenCameraViewModel by viewModels()
    private var counter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpencameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.openCameraButton.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
        binding.openAddManuelButton.setOnClickListener {
            startActivity(Intent(this, SensorAddManuelActivity::class.java))
        }
        binding.apply {

            topAppBar.setNavigationOnClickListener {
                menuDrawerLayout.open()
            }

            navigationView.setNavigationItemSelectedListener {
                when(it.itemId) {
                    R.id.firstItem -> {
                        Toast.makeText(this@OpenCameraActivity, "Already Here", Toast.LENGTH_SHORT).show()
                    }
                    R.id.secondItem -> {
                        startActivity(Intent(this@OpenCameraActivity, CameraActivity::class.java))
                        Toast.makeText(this@OpenCameraActivity, "Camera Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.thirdItem -> {
                        startActivity(Intent(this@OpenCameraActivity, SensorAddManuelActivity::class.java))
                        Toast.makeText(this@OpenCameraActivity, "Activity Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.forthItem -> {
                        startActivity(Intent(this@OpenCameraActivity, ChartActivity::class.java))
                        Toast.makeText(this@OpenCameraActivity, "Charts Opened", Toast.LENGTH_SHORT).show()
                    }
                    R.id.fifthItem -> {
                        startActivity(Intent(this@OpenCameraActivity, AlertScreenActivity::class.java))
                        Toast.makeText(this@OpenCameraActivity, "Alert Screen Opened", Toast.LENGTH_SHORT).show()
                    }
                }
                it.isChecked = true
                menuDrawerLayout.close()
                true
            }
        }
        createToken()
        observeLiveData()
        hideSystemNavigationBars()
    }

    private fun hideSystemNavigationBars() {
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        counter++
        if (counter == 2) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                val deviceID = Secure.getString(this.contentResolver, Secure.ANDROID_ID)
                Log.e("deviceID", deviceID)
                Log.e("token", token)
                viewModel.uploadToken(TokenRequest(token, "AA:AA:AA:AA:AA:AA"))

            }
        }
    }

    private fun observeLiveData() {
        viewModel.response.observe(this) { response ->
            response?.let {

            } ?: run {

            }
        }
    }
}