package com.eae.busbarar.presentation

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Secure
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.eae.busbarar.R
import com.eae.busbarar.data.model.TokenRequest
import com.eae.busbarar.databinding.ActivityOpencameraBinding
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
        binding.openCamera.setOnClickListener {
            startActivity(Intent(this, OpenCameraActivity::class.java))
        }
        binding.openAddManuel.setOnClickListener {
            startActivity(Intent(this, SensorAddManuelActivity::class.java))
        }
        binding.apply {

            topAppBar?.setNavigationOnClickListener {
                drawerLayout.open()
            }

            navView.setNavigationItemSelectedListener {
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
                        //startActivity(Intent(this@ChartActivity, SensorAddManuelActivity::class.java))
                        Toast.makeText(this@OpenCameraActivity, "Alert Screen Opened", Toast.LENGTH_SHORT).show()
                    }
                }
                it.isChecked = true
                drawerLayout.close()
                true
            }
        }
        createToken()
        observeLiveData()
    }

    private fun openCameraActivityButton() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }

    private fun openAddManuelActivityButton() {
        val intent = Intent(this, SensorAddManuelActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
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