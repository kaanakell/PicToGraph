package com.eae.busbarar.presentation

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Secure
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.eae.busbarar.R
import com.eae.busbarar.data.model.TokenRequest
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OpenCameraActivity : AppCompatActivity() {
    private val viewModel: OpenCameraViewModel by viewModels()
    private var button: Button? = null
    private var counter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opencamera)
        button = findViewById(R.id.openCamera)
        button?.setOnClickListener { _ : View? -> openCameraActivityButton() }
        createToken()
        observeLiveData()
    }

    private fun openCameraActivityButton() {
        val intent = Intent(this, CameraActivity::class.java)
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