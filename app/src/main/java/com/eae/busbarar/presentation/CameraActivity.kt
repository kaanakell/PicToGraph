package com.eae.busbarar.presentation

import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.Surface.ROTATION_0
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import com.eae.busbarar.Constants
import com.eae.busbarar.R
import com.eae.busbarar.databinding.ActivityCameraBinding
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraSelector: CameraSelector
    private var imageCapture: ImageCapture? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var preview: Preview? = null
    private lateinit var imgCaptureExecutor: ExecutorService
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.imageCaptureButton.setOnClickListener {
            photoCapture()
        }

        binding.switchTextInputButton.setOnClickListener {
            startActivity(Intent(this@CameraActivity, SensorAddManuelActivity::class.java))
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
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProvider = cameraProviderFuture.get()
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        imgCaptureExecutor = Executors.newSingleThreadExecutor()

        initializeCamera()
        cameraCloseButton()
        hideSystemNavigationBars()
        supportActionBar?.hide()
    }

    override fun onBackPressed() {
        counter++
        if (counter == 1){
            val intent = Intent(this, OpenCameraActivity::class.java)
            startActivity(intent)
        }
    }

    private fun hideSystemNavigationBars() {
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
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
        val imageCapture = imageCapture ?: return
        val mediaDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val photoFile = File(
            mediaDir,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpeg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                @RequiresApi(Build.VERSION_CODES.R)
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)

                    val inputData = contentResolver.openInputStream(savedUri)?.readBytes()
                    val capturedImageBitmap =
                        BitmapFactory.decodeByteArray(inputData, 0, inputData!!.size)

                    val capturedImageCroppedBitmap: Bitmap?
                    val display = (getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay

                    val imageRotation = getRotation(photoFile)

                    if (display.rotation == ROTATION_0) {
                        val matrix = Matrix()
                        matrix.postRotate(imageRotation)

                        val capturedImageRotatedBitmap = Bitmap.createBitmap(
                            capturedImageBitmap,
                            0,
                            0,
                            capturedImageBitmap.width,
                            capturedImageBitmap.height,
                            matrix,
                            true
                        )

                        //calculate aspect ratio
                        val width: Int = capturedImageRotatedBitmap.width
                        val height: Int = capturedImageRotatedBitmap.height

                        val ratioConstraint: Int = width / 54

                        val widthRatio = 16 * ratioConstraint
                        val heightRatio = 9 * ratioConstraint

                        val leftX1: Int = width / 2 - widthRatio
                        val rightX2: Int = width / 2 + widthRatio
                        val topY1: Int = height / 2 - heightRatio
                        val bottomY2: Int = height / 2 + heightRatio

                        val cropWidth = (rightX2 - leftX1)
                        val cropHeight = (bottomY2 - topY1)

                        //calculate position and size for cropping
                        val cropStartX = leftX1
                        val cropStartY = topY1
                        val cropWidthX = cropWidth
                        val cropHeightY = cropHeight

                        //check limits and make crop
                        capturedImageCroppedBitmap =
                            if (cropStartX + cropWidthX <= capturedImageRotatedBitmap.width && cropStartY + cropHeightY <= capturedImageRotatedBitmap.height) {
                                Bitmap.createBitmap(
                                    capturedImageRotatedBitmap,
                                    cropStartX,
                                    cropStartY,
                                    cropWidthX,
                                    cropHeightY
                                )
                            } else {
                                null
                            }

                        if (capturedImageCroppedBitmap != null) {
                            saveToInternalStorage(capturedImageCroppedBitmap)
                        }
                    }
                }
            })
    }

    private fun saveToInternalStorage(bitmapImage: Bitmap) {
        val cw = ContextWrapper(applicationContext)
        // path to /data/data/yourapp/app_data/imageDir
        val directory = cw.getDir("imageDir", MODE_PRIVATE)
        // Create imageDir
        val mypath = File(directory, System.currentTimeMillis().toString() + ".jpg")
        Log.e(TAG, mypath.toString())
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        startPreviewActivity(mypath.toUri())
    }

    private fun startPreviewActivity(photoUri: Uri) {
        val intent = Intent(this@CameraActivity, CameraPreviewActivity::class.java)
        intent.putExtra("photoUri", photoUri)
        startActivity(intent)
    }

    private fun initializeCamera() {
        cameraProviderFuture.addListener({

            val viewFinder = binding.previewView
            val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
            val aspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
            val rotation = viewFinder.display?.rotation

            preview = Preview.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setTargetRotation(rotation!!).build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setTargetAspectRatio(aspectRatio)
                .setTargetRotation(rotation)
                .build()

            imageAnalysis = ImageAnalysis.Builder().apply {
                setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            }.build()

            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalysis
                )
                val cameraControl = camera.cameraControl
                val cameraInfo = camera.cameraInfo
                cameraTouchControls(cameraControl, cameraInfo)
            } catch (e: Exception) {
                Log.d(TAG, "Use case binding failed")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun getRotation(photoFile: File): Float {
        var orientation = 0

        val exif = ExifInterface(photoFile)

        val exifOrientation: Int = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        when (exifOrientation) {
            ExifInterface.ORIENTATION_ROTATE_270 -> orientation = 270
            ExifInterface.ORIENTATION_ROTATE_180 -> orientation = 180
            ExifInterface.ORIENTATION_ROTATE_90 -> orientation = 90
            ExifInterface.ORIENTATION_NORMAL -> orientation = 0
            else -> {
            }
        }
        return orientation.toFloat()
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private fun cameraTouchControls(cameraControl: CameraControl, cameraInfo: CameraInfo) {
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

            override fun onScale(detector: ScaleGestureDetector): Boolean {

                val currentZoomRatio = cameraInfo.zoomState.value?.zoomRatio ?: 0F

                binding.zoomSeekBar.progress = currentZoomRatio.toInt()

                val delta = detector.scaleFactor

                cameraControl.setZoomRatio(currentZoomRatio * delta)

                return true
            }
        }

        val scaleGestureDetector = ScaleGestureDetector(baseContext, listener)

        binding.previewView.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_DOWN) {
                val factory = binding.previewView.meteringPointFactory
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

    companion object {
        private const val TAG = "CameraXDemo"
        const val FILENAME_FORMAT = "yyyyMMddHHmmss"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0 // aspect ratio 4x3
        private const val RATIO_16_9_VALUE = 16.0 / 9.0 // aspect ratio 16x9
    }

}