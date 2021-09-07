package com.dengetelekom.telsiz

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.MenuItem
import android.view.Surface
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.camera2.internal.annotation.CameraExecutor
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files.createFile
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import com.google.android.play.core.splitinstall.c

import android.provider.MediaStore


class ImageCaptureActivity : AppCompatActivity() {

    private lateinit var btnSavePhoto: Button
    private var viewModel: CameraXViewModel? = null
    private var previewView: PreviewView? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector: CameraSelector? = null
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private lateinit var currentPhotoPath: String
    private var selectedBitmap: Bitmap? = null
    private var imageCapture: ImageCapture? = null
    private var cameraExecutor: Executor? = null
    private val screenAspectRatio: Int
        get() {
            // Get screen metrics used to setup camera for full screen resolution
            val metrics = DisplayMetrics().also { previewView?.display?.getRealMetrics(it) }
            return aspectRatio(metrics.widthPixels, metrics.heightPixels)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_capture)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.image_capture_title)
        val upArrow = ContextCompat.getDrawable(this, R.drawable.back_button_image)

        supportActionBar?.setHomeAsUpIndicator(upArrow)
        btnSavePhoto = findViewById(R.id.btn_capture_image)
        btnSavePhoto.setOnClickListener { savePhoto() }
        setupCamera()
    }

    private fun savePhoto() {
        createImageFile()
        val returnIntent = Intent()
        returnIntent.putExtra("result", currentPhotoPath)
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            try {
                if (selectedBitmap != null) {
                    FileOutputStream(currentPhotoPath).use { out ->
                        selectedBitmap!!.compress(
                            Bitmap.CompressFormat.JPEG,
                            100,
                            out
                        ) // bmp is your Bitmap instance
                    }
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                setResult(RESULT_CANCELED, Intent())
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupCamera() {
        previewView = findViewById(R.id.preview_view)
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(CameraXViewModel::class.java)

        viewModel?.processCameraProvider?.observe(
            this,
            Observer { provider: ProcessCameraProvider? ->
                cameraProvider = provider
                if (isCameraPermissionGranted()) {
                    bindCameraUseCases()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA),
                        PERMISSION_CAMERA_REQUEST
                    )
                }
            })
    }

    private fun bindCameraUseCases() {
        bindPreviewUseCase()
        bindAnalyseUseCase()
    }

    private fun bindPreviewUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }

        previewUseCase = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(Surface.ROTATION_0)
            .build()
        previewUseCase!!.setSurfaceProvider(previewView!!.surfaceProvider)

        try {
            cameraProvider!!.bindToLifecycle(/* lifecycleOwner= */this,
                cameraSelector!!,
                previewUseCase
            )
        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, illegalStateException.message!!)
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, illegalArgumentException.message!!)
        }
    }

    private fun bindAnalyseUseCase() {


        if (cameraProvider == null) return
        if (analysisUseCase != null) cameraProvider!!.unbind(analysisUseCase)

        analysisUseCase = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(Surface.ROTATION_0)
            .build()
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            // We request aspect ratio but no resolution to match preview config, but letting
            // CameraX optimize for whatever specific resolution best fits our use cases
            .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .setTargetRotation(Surface.ROTATION_0)
            .build()
        cameraExecutor = Executors.newSingleThreadExecutor()

        analysisUseCase?.setAnalyzer(cameraExecutor!!, { imageProxy ->
            processImageProxy(imageProxy)
        })

        try {
            cameraProvider!!.bindToLifecycle(
                this, cameraSelector!!, previewUseCase, imageCapture, analysisUseCase
            )

        } catch (illegalStateException: IllegalStateException) {
            Log.e(TAG, illegalStateException.message!!)
        } catch (illegalArgumentException: IllegalArgumentException) {
            Log.e(TAG, illegalArgumentException.message!!)
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun processImageProxy(imageProxy: ImageProxy) {
        val photoFile = createImageFile()

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture!!.takePicture(
            outputOptions,
            cameraExecutor!!,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("IMAGE_CAPTURE", exc.message!!)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    var path = (output.savedUri ?: Uri.fromFile(photoFile))
                    selectedBitmap = when {
                        Build.VERSION.SDK_INT < Build.VERSION_CODES.P -> MediaStore.Images.Media.getBitmap(
                            contentResolver,
                            path
                        )
                        else -> {
                            val source = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                ImageDecoder.createSource(contentResolver, path)
                            } else {
                                TODO("VERSION.SDK_INT < P")
                            }
                            ImageDecoder.decodeBitmap(source)
                        }
                    }

                }
            })
    }


    /**
     *  [androidx.camera.core.ImageAnalysis],[androidx.camera.core.Preview] requires enum value of
     *  [androidx.camera.core.AspectRatio]. Currently it has values of 4:3 & 16:9.
     *
     *  Detecting the most suitable ratio for dimensions provided in @params by counting absolute
     *  of preview ratio to one of the provided values.
     *
     *  @param width - preview width
     *  @param height - preview height
     *  @return suitable aspect ratio
     */
    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_CAMERA_REQUEST) {
            if (isCameraPermissionGranted()) {
                bindCameraUseCases()
            } else {
                Log.e(TAG, "no camera permission")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val TAG = ImageCaptureActivity::class.java.simpleName
        private const val PERMISSION_CAMERA_REQUEST = 1

        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}