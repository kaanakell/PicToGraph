package com.eae.busbarar.presentation

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.eae.busbarar.R
import com.eae.busbarar.data.model.TokenRequest
import com.google.firebase.messaging.FirebaseMessaging


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
                val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                val macAddress: String = wifiInfo.macAddress

                Log.e("token", token)
                Log.e("mac", "MAC Address : " + macAddress)

                viewModel.uploadToken(TokenRequest(token, macAddress))

            }
        }
    }
}