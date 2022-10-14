package com.eae.busbarar.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eae.busbarar.R
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        Toast.makeText(this,"There is no back action",Toast.LENGTH_LONG).show();
        return
    }

    fun backToCamera(v: View){
        startActivity(Intent(this, CameraActivity::class.java))
    }


    companion object {
        var List: List<String>? = null
    }

    override fun onItemClick(item: String) {
        SensorInfoActivity.sensorId = item
        startActivity(Intent(this, SensorInfoActivity::class.java))
    }
}