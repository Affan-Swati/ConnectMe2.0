package com.affan.i220916

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.camera.view.PreviewView
import java.io.File

class StoryCamera : AppCompatActivity() {
    private lateinit var cameraPreview: PreviewView
    private lateinit var imageView: ImageView
    private lateinit var cameraButton: ImageView
    private lateinit var flipCamButton: ImageView
    private lateinit var galleryButton: ImageView
    private lateinit var crossButton: ImageView
    private var imageCapture: ImageCapture? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var isGalleryImageSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_camera)

        cameraPreview = findViewById(R.id.cameraPreview)
        imageView = findViewById(R.id.imageView)
        cameraButton = findViewById(R.id.camera_button)
        flipCamButton = findViewById(R.id.flip_cam)
        galleryButton = findViewById(R.id.gallery_preview)
        crossButton = findViewById(R.id.cross_button)

        // Start Camera Preview
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Capture Photo
        cameraButton.setOnClickListener {
            if (!isGalleryImageSelected) {
                capturePhoto()
            }
        }

        // Flip Camera
        flipCamButton.setOnClickListener {
            if (!isGalleryImageSelected) {
                flipCamera()
            }
        }

        // Open Gallery
        galleryButton.setOnClickListener {
            openGallery()
        }

        // Clear Selection when pressing the "X" (cross) button
        crossButton.setOnClickListener {
            clearSelection()
        }
    }

    // Function to start the camera
    private fun startCamera() {
        if (isGalleryImageSelected) return  // Don't start the camera if gallery image is selected

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(cameraPreview.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e("StoryCamera", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    // Function to capture photo
    private fun capturePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(externalCacheDir, "story_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)

                    // Hide camera preview and show captured image
                    runOnUiThread {
                        cameraPreview.visibility = ImageView.GONE
                        imageView.visibility = ImageView.VISIBLE
                        imageView.setImageURI(savedUri)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("StoryCamera", "Photo capture failed: ${exception.message}", exception)
                }
            })
    }

    // Function to flip camera
    private fun flipCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera()
    }

    // Open gallery
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let { uri ->
                runOnUiThread {
                    isGalleryImageSelected = true  // Mark gallery image as selected
                    cameraPreview.visibility = ImageView.GONE
                    imageView.visibility = ImageView.VISIBLE
                    imageView.setImageURI(uri)

                    // Disable Camera Capture & Flip Buttons
                    cameraButton.alpha = 0.5f
                    cameraButton.isEnabled = false
                    flipCamButton.alpha = 0.5f
                    flipCamButton.isEnabled = false
                }
            }
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }

    // Function to clear gallery selection and restart the camera
    private fun clearSelection() {
        if (isGalleryImageSelected || imageView.visibility == ImageView.VISIBLE) {
            // If a gallery image or a camera photo was taken, reset and show camera
            isGalleryImageSelected = false
            imageView.visibility = ImageView.GONE
            cameraPreview.visibility = ImageView.VISIBLE

            // Re-enable Camera & Flip Buttons
            cameraButton.alpha = 1f
            cameraButton.isEnabled = true
            flipCamButton.alpha = 1f
            flipCamButton.isEnabled = true

            startCamera()
        } else {
            // If no image is selected from either source, exit the activity
            finish()
        }
    }

    // Check if permissions are granted
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}
