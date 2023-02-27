package com.eae.busbarar.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bumptech.glide.Glide
import com.eae.busbarar.databinding.ActivityPreviewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class PreviewActivity : AppCompatActivity() {

    private val viewModel: PreviewViewModel by viewModels()
    private val chartViewModel: ChartActivityViewModel by viewModels()
    private lateinit var binding: ActivityPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeView()
        observeLiveDataResponse()
        hideSystemNavigationBars()
        binding.buttonUpload.performClick()
        binding.buttonUpload.visibility = View.INVISIBLE
    }

    private fun hideSystemNavigationBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun observeLiveDataResponse() {
        viewModel.response.observe(this) { response ->
            response?.let {
                Toast.makeText(this, "Uploaded successfully.", Toast.LENGTH_LONG).show()
                viewModel.uploadFileName(response.FileName)
            } ?: run {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, CameraActivity::class.java))
            }
            binding.layoutLoading.visibility = View.GONE
            Toast.makeText(this, "Please wait for Sensor Respond.", Toast.LENGTH_LONG).show()
            binding.respondBar.visibility = View.VISIBLE
        }

        viewModel.fileNameResponse.observe(this){response ->
            response?.let {
                SensorActivity.list = SensorActivity.list + listOf(it.DetectedSensor ?: "EMPTY")
                startActivity(Intent(this, SensorActivity::class.java))
                finish()
            } ?: run {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, CameraActivity::class.java))
            }
        }

        chartViewModel.sensorResponse.observe(this) { response ->
            response?.let {

            } ?: run {

            }
        }
    }

    private fun initializeView() {
        Glide.with(this).load(path).into(binding.imageView)

        binding.buttonBack.setOnClickListener {
            finish()
        }
        binding.buttonUpload.setOnClickListener {
            binding.layoutLoading.visibility = View.VISIBLE
            Log.e("demo", "$path")
            viewModel.uploadImage(File(path ?: ""))
        }
    }

    companion object {
        var path: String? = null
    }

}