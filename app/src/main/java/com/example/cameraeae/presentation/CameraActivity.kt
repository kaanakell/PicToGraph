package com.example.cameraeae.presentation

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.example.cameraeae.Constants
import com.example.cameraeae.R
import com.example.cameraeae.databinding.ActivityCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraActivity : AppCompatActivity(){

    private lateinit var binding: ActivityCameraBinding

    private var imageCapture: ImageCapture? = null
    private lateinit var  cameraExecutor: ExecutorService
    private var flashMode = ImageCapture.FLASH_MODE_OFF
    private var camera: Camera? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageCaptureButton.setOnClickListener {
            capturePhoto()
        }

        binding.flashSwitch.setOnClickListener {
            if (flashMode == ImageCapture.FLASH_MODE_OFF) {
                flashMode = ImageCapture.FLASH_MODE_ON
                binding.flashSwitch.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.flash_on))
            } else {
                flashMode = ImageCapture.FLASH_MODE_OFF
                binding.flashSwitch.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.flash_off))
            }
        }

        if (flashMode == ImageCapture.FLASH_MODE_OFF) {
            binding.flashSwitch.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.flash_off))
        } else {
            binding.flashSwitch.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.flash_on))
        }
        initializeCamera()
    }


    private fun capturePhoto() {
        val imageCapture = imageCapture ?: return
        imageCapture.flashMode = flashMode

        val photoFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), SimpleDateFormat("yyyyMMddHHmmSS", Locale.US).format(System.currentTimeMillis()) + ".jpeg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
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

    private fun initializeCamera(){
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
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch(e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.REQUEST_CODE_PERMISSION){
            if(allPermissionGranted()){
                //our code
                initializeCamera()
            }else{
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    private fun allPermissionGranted() =
        Constants.REQUIRED_PERMISSIONS.all{
            ContextCompat.checkSelfPermission(
                baseContext, it
            ) == PackageManager.PERMISSION_GRANTED
        }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun pinchToZoomCameraX() {

        val listener: SimpleOnScaleGestureListener =
            object : SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    try {
                        val camera: Camera = camera ?: return false
                        val zoomState: LiveData<ZoomState> = camera.cameraInfo.zoomState

                        var currentZoomRatio: Float = 0f
                        currentZoomRatio = zoomState.value!!.zoomRatio

                        val delta: Float = detector.scaleFactor
                        camera.cameraControl.setZoomRatio(currentZoomRatio * delta)

                        val linearValue: Float = zoomState.value!!.linearZoom
                        val mat: Float = linearValue * 100

                        // Update SeekBar
                        binding.zoomSeekBar.progress = mat.toInt()
                        Log.i(Constants.TAG, "onScale: SeekBar progress = ${mat.toInt()}")

                        return true
                    } catch (e: Exception) {
                        Log.e(Constants.TAG, "onScale: Exception = ${e.message}")
                        return false
                    }
                }
            }

        val scaleGestureDetector = ScaleGestureDetector(baseContext, listener)

        binding.previewView.setOnTouchListener { view, event ->
            view.performClick()
            scaleGestureDetector.onTouchEvent(event)
            return@setOnTouchListener true
        }

        binding.zoomSeekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (!fromUser) {
                        Log.i(Constants.TAG, "onProgressChanged: fromUser = $fromUser")
                        return
                    }
                    val percentage: Float = progress / 100F
                    camera!!.cameraControl.setLinearZoom(percentage)
                    Log.i(Constants.TAG, "onProgressChanged: progress   = $progress ")
                    Log.i(Constants.TAG, "onProgressChanged: percentage = $percentage ")
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            }
        )
    }




    /*inline fun View.afterMeasured(crossinline block: () -> Unit) {
        if (measuredWidth > 0 && measuredHeight > 0) {
            block()
        } else {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (measuredWidth > 0 && measuredHeight > 0) {
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                        block()
                    }
                }
            })
        }
    }*/

    /*private fun autoFocus(){

        viewBinding.viewFinder.afterMeasured {
            val autoFocusPoint = SurfaceOrientedMeteringPointFactory(1f, 1f)
                .createPoint(.5f, .5f)
            try {
                val autoFocusAction = FocusMeteringAction.Builder(
                    autoFocusPoint,
                    FocusMeteringAction.FLAG_AF
                ).apply {
                    //start auto-focusing after 2 seconds
                    setAutoCancelDuration(2, TimeUnit.SECONDS)
                }.build()
                camera?.cameraControl?.startFocusAndMetering(autoFocusAction)
            } catch (e: CameraInfoUnavailableException) {
                Log.d("ERROR", "cannot access camera", e)
            }
        }

    }*/

    /*private fun touchFocus(){
        viewBinding.viewFinder.afterMeasured {
            viewBinding.viewFinder.setOnTouchListener { _, event ->
                return@setOnTouchListener when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        true
                   }
                    MotionEvent.ACTION_UP -> {
                        val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                            viewBinding.viewFinder.width.toFloat(), viewBinding.viewFinder.height.toFloat()
                        )
                        val autoFocusPoint = factory.createPoint(event.x, event.y)
                        try {
                            camera?.cameraControl?.startFocusAndMetering(
                                FocusMeteringAction.Builder(
                                    autoFocusPoint,
                                    FocusMeteringAction.FLAG_AF
                                ).apply {
                                    //focus only when the user tap the preview
                                    disableAutoCancel()
                                }.build()
                            )

                        } catch (e: CameraInfoUnavailableException) {
                            Log.d("ERROR", "cannot access camera", e)
                        }
                        viewBinding.viewFinder.performClick()
                        true
                    }

                    else -> false // Unhandled event.
                }
            }
        }
    }*/



}