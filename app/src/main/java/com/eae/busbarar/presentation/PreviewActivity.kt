package com.eae.busbarar.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.eae.busbarar.databinding.ActivityPreviewBinding
import com.eae.busbarar.data.model.TextRecognitionRequest
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class PreviewActivity : AppCompatActivity() {

    private val viewModel: PreviewViewModel by viewModels()

    private lateinit var binding: ActivityPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        observeLiveData()
    }

    private fun observeLiveData(){
        viewModel.response.observe(this) { response ->
            response?.let {
                Toast.makeText(this, "Uploaded successfully.", Toast.LENGTH_LONG).show()
                viewModel.uploadFileName(response.FileName)
            } ?: run {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()
            }
            binding.layoutLoading.visibility = View.GONE
        }

        viewModel.fileNameResponse.observe(this){response ->
            response?.let {
                SensorActivity.List = listOf(it.DetectedSensor ?: "")
                startActivity(Intent(this, SensorActivity::class.java))
            } ?: run{
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.sensorResponse.observe(this){ response ->
            response?.let {

            } ?: run {

            }
        }
    }

    private fun initView(){
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