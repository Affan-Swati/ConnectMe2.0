package com.affan.i220916

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.camera.view.PreviewView
import androidx.exifinterface.media.ExifInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class StoryCamera : AppCompatActivity() {
    private lateinit var cameraPreview: PreviewView
    private lateinit var imageView: ImageView
    private lateinit var videoPreview: VideoView
    private lateinit var cameraButton: ImageView
    private lateinit var flipCamButton: ImageView
    private lateinit var galleryButton: ImageView
    private lateinit var crossButton: ImageView
    private lateinit var nextButton: TextView
    private var imageCapture: ImageCapture? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var cameraExecutor: ExecutorService
    private var isGalleryImageSelected = false
    private var selectedMediaUri: Uri? = null
    private var crossButtonClickedOnce = false

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val REQUEST_CODE_GALLERY = 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_camera)

        cameraPreview = findViewById(R.id.cameraPreview)
        imageView = findViewById(R.id.imageView)
        videoPreview = findViewById(R.id.videoPreview)
        cameraButton = findViewById(R.id.camera_button)
        flipCamButton = findViewById(R.id.flip_cam)
        galleryButton = findViewById(R.id.gallery_preview)
        crossButton = findViewById(R.id.cross_button)
        nextButton = findViewById(R.id.H1)
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Setup video controls
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoPreview)
        videoPreview.setMediaController(mediaController)

        cameraButton.setOnClickListener {
            if (!isGalleryImageSelected) {
                capturePhoto()
            }
        }

        flipCamButton.setOnClickListener {
            if (!isGalleryImageSelected) {
                flipCamera()
            }
        }

        galleryButton.setOnClickListener { openGallery() }
        crossButton.setOnClickListener { handleCrossButtonClick() }
        nextButton.setOnClickListener { uploadStoryToFirebase() }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Get current display rotation
            val rotation = windowManager.defaultDisplay.rotation

            // Configure preview
            val preview = Preview.Builder()
                .setTargetRotation(rotation)
                .build()
                .also {
                    it.setSurfaceProvider(cameraPreview.surfaceProvider)
                }

            // Configure ImageCapture
            imageCapture = ImageCapture.Builder()
                .setTargetRotation(rotation)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e("StoryCamera", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun capturePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(externalCacheDir, "story_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    runOnUiThread {
                        cameraPreview.visibility = ImageView.GONE
                        imageView.visibility = ImageView.VISIBLE
                        imageView.setImageURI(savedUri)
                        selectedMediaUri = savedUri
                    }
                }
                override fun onError(exception: ImageCaptureException) {
                    Log.e("StoryCamera", "Photo capture failed: ${exception.message}", exception)
                }
            })
    }

    private fun flipCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    @Deprecated("Deprecated in Android")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                isGalleryImageSelected = true
                selectedMediaUri = uri
                val mimeType = contentResolver.getType(uri)

                if (mimeType?.startsWith("image") == true) {
                    imageView.setImageURI(uri)
                    imageView.visibility = ImageView.VISIBLE
                    videoPreview.visibility = VideoView.GONE
                } else if (mimeType?.startsWith("video") == true) {
                    videoPreview.setVideoURI(uri)
                    videoPreview.requestFocus()
                    videoPreview.visibility = VideoView.VISIBLE
                    imageView.visibility = ImageView.GONE
                    videoPreview.start()
                }
                cameraPreview.visibility = ImageView.GONE
            }
        }
    }

    private fun handleCrossButtonClick() {
        if (crossButtonClickedOnce) {
            finish()
        } else {
            clearSelection()
            crossButtonClickedOnce = true
        }
    }

    private fun clearSelection() {
        isGalleryImageSelected = false
        selectedMediaUri = null
        imageView.visibility = ImageView.GONE
        videoPreview.visibility = VideoView.GONE
        videoPreview.stopPlayback()
        cameraPreview.visibility = ImageView.VISIBLE
        startCamera()
    }

    private fun uploadStoryToFirebase() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedMediaUri == null) {
            Toast.makeText(this, "No media selected", Toast.LENGTH_SHORT).show()
            return
        }

        val mimeType = contentResolver.getType(selectedMediaUri!!)
        val mediaType = if (mimeType?.startsWith("video") == true) "video" else "image"
        val base64Data: String = try {
            when (mediaType) {
                "image" -> {
                    var bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(selectedMediaUri!!))

                    // Fix rotation using EXIF data
                    contentResolver.openInputStream(selectedMediaUri!!)?.use { stream ->
                        val exif = ExifInterface(stream)
                        val orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL
                        )
                        bitmap = rotateBitmap(bitmap, orientation)
                    }

                    // Convert to Base64
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
                    "data:image/jpeg;base64,${Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.NO_WRAP)}"
                }
                "video" -> {
                    val inputStream: InputStream? = contentResolver.openInputStream(selectedMediaUri!!)
                    val bytes = inputStream?.readBytes() ?: byteArrayOf()
                    "data:video/mp4;base64,${Base64.encodeToString(bytes, Base64.NO_WRAP)}"
                }
                else -> throw IllegalArgumentException("Unsupported media type")
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error processing file: ${e.message}", Toast.LENGTH_SHORT).show()
            return
        }

        val storyData = hashMapOf(
            "userId" to user.uid,
            "timestamp" to System.currentTimeMillis(),
            "mediaData" to base64Data,
            "mediaType" to mediaType
        )

        FirebaseDatabase.getInstance().getReference("stories")
            .child(user.uid).push().setValue(storyData)
            .addOnSuccessListener {
                Toast.makeText(this, "Story uploaded successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload story", Toast.LENGTH_SHORT).show()
            }
    }

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}