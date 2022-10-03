package com.example.cameraeae.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cameraeae.databinding.ActivityPreviewBinding
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

        viewModel.success.observe(this){ success ->
            success?.let{
                Toast.makeText(this, "File Name True", Toast.LENGTH_LONG).show()
            } ?: run{
                Toast.makeText(this,"Something went wrong!", Toast.LENGTH_LONG).show()
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