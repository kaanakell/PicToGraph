package com.eae.busbarar.presentation

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.eae.busbarar.Constants
import com.eae.busbarar.R
import com.eae.busbarar.databinding.ActivityCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null
    private var flashMode = ImageCapture.FLASH_MODE_OFF
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageCaptureButton.setOnClickListener {
            photoCapture()
        }

        if(allPermissionsGrantedByUser()) {
            Toast.makeText(this,
            "We Have Permission",
            Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this, Constants.REQUIRED_PERMISSIONS,
                Constants.REQUEST_CODE_PERMISSION
            )
        }

        initializeCamera()
        cameraCloseButton()
        hideSystemNavigationBars()

    }

    override fun onBackPressed() {
        counter++
        if (counter == 1){
            val intent = Intent(this, OpenCameraActivity::class.java)
            startActivity(intent)
        }
    }

    private fun hideSystemNavigationBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.REQUEST_CODE_PERMISSION){
            if(allPermissionsGrantedByUser()){
                initializeCamera()
            }else{
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGrantedByUser() =
        Constants.REQUIRED_PERMISSIONS.all{
            ContextCompat.checkSelfPermission(
                baseContext, it
            ) == PackageManager.PERMISSION_GRANTED
        }

    private fun cameraCloseButton() {
        binding.closeCamera.setOnClickListener{
            startActivity(Intent(this@CameraActivity, OpenCameraActivity::class.java))
        }
    }


    private fun photoCapture() {
        imageCapture ?: return

        val photoFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), SimpleDateFormat("yyyyMMddHHmmSS", Locale.US).format(System.currentTimeMillis()) + ".jpeg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture!!.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    try {
                        val savedUri = Uri.fromFile(photoFile)
                        savedUri.path?.let { safePath ->
                            val file = File(safePath)
                            file.delete()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    Toast.makeText(
                        this@CameraActivity,
                        "Something went wrong!",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    savedUri.path?.let { safePath ->
                        PreviewActivity.path = safePath
                        startActivity(Intent(this@CameraActivity, PreviewActivity::class.java))
                    }
                }
            }
        )
    }

    private fun initializeCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder()
                .setFlashMode(flashMode)
                .build()
            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                val cameraControl = camera.cameraControl
                val cameraInfo = camera.cameraInfo
                tapToFocusFeature(cameraControl)
                pinchToZoomFeature(cameraControl, cameraInfo)
                sliderBarZoomFeature(cameraControl)
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }


    private fun tapToFocusFeature(cameraControl: CameraControl) {
        binding.previewView.setOnTouchListener(View.OnTouchListener setOnTouchListener@{ view: View, motionEvent: MotionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> return@setOnTouchListener true
                MotionEvent.ACTION_UP -> {

                    val factory = binding.previewView.getMeteringPointFactory()

                    val point = factory.createPoint(motionEvent.x, motionEvent.y)

                    val action = FocusMeteringAction.Builder(point).build()

                    cameraControl.startFocusAndMetering(action)

                    return@setOnTouchListener true
                }
                else -> return@setOnTouchListener false
            }
        })
    }

    private fun pinchToZoomFeature(cameraControl: CameraControl, cameraInfo: CameraInfo) {
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

            override fun onScale(detector: ScaleGestureDetector): Boolean {

                val currentZoomRatio = cameraInfo.zoomState.value?.zoomRatio ?: 0F

                val delta = detector.scaleFactor

                cameraControl.setZoomRatio(currentZoomRatio * delta)

                return true
            }
        }

        val scaleGestureDetector = ScaleGestureDetector(baseContext, listener)

        // Attach the pinch gesture listener to the viewfinder
        binding.previewView.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_DOWN) {
                val factory = binding.previewView.getMeteringPointFactory()
                val point = factory.createPoint(event.x, event.y)
                val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                    .setAutoCancelDuration(5, TimeUnit.SECONDS)
                    .build()
                cameraControl.startFocusAndMetering(action)
            }
            true
        }
    }

    private fun sliderBarZoomFeature(cameraControl: CameraControl) {
        val zoomSlider = findViewById<SeekBar>(R.id.zoom_seek_bar)
        zoomSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                cameraControl.setLinearZoom(progress / 100.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }


}