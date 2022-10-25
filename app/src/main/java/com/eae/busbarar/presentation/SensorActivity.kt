package com.eae.busbarar.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eae.busbarar.databinding.ActivitySensorBinding


class SensorActivity : AppCompatActivity(), ISensor{

    private lateinit var binding: ActivitySensorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = SensorAdapter(this)
        binding.recyclerView.adapter = adapter
        adapter.list = List ?: listOf()
    }

    override fun onBackPressed() {
        startActivity(Intent(this, CameraActivity::class.java))
        finish()
    }

    override fun onBackCamera() {
        startActivity(Intent(this, CameraActivity::class.java))
        finish()
    }


    companion object {
        var List: List<String>? = null
    }

    override fun onItemClick(item: String) {
        SensorInfoActivity.sensorId = item
        startActivity(Intent(this, SensorInfoActivity::class.java))
    }
}