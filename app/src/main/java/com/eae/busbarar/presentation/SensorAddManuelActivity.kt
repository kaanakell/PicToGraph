package com.eae.busbarar.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.eae.busbarar.R
import com.eae.busbarar.data.model.*


class SensorAddManuelActivity: AppCompatActivity(), ISensorAddManuel {
    lateinit var textInput: EditText
    private val adapter = SensorAdapter(this)
    private val sensorClicks : ArrayList<String> = arrayListOf()
    private var button: Button? = null
    var listener: ISensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_add_manuel)
        button = findViewById(R.id.submit)
        textInput = findViewById(R.id.input_text)

        button?.setOnClickListener {
            val intent = Intent(this, SensorActivity::class.java)
            intent.putExtra("ENTERED_TEXT", textInput.text.toString())
            startActivity(intent)
        }
    }

    override fun onButtonClick(textInput: String) {
        // Create a new Sensor object
        val newSensor = Sensor(textInput)

        // Get the current list of sensors
        val currentList = adapter?.list

        // Add the new sensor to the list
        val updatedList = currentList + newSensor

        // Set the updated list to the adapter
        adapter?.list = updatedList

        // Notify the adapter about the data change
        adapter?.notifyDataSetChanged()
    }
}
