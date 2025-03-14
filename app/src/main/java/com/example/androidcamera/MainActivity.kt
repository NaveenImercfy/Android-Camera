package com.example.androidcamera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.androidcamera.api.VisionApiService
import com.example.androidcamera.models.*
import com.example.androidcamera.utils.Base64Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var outputDirectory: File
    private lateinit var previewView: PreviewView
    private lateinit var captureButton: Button
    private lateinit var analyzeButton: Button
    private lateinit var imageView: ImageView
    private lateinit var resultTextView: TextView
    
    private var imageCapture: ImageCapture? = null
    private var capturedImageFile: File? = null
    
    // API key should be stored securely, this is just for demonstration
    private val apiKey = BuildConfig.VISION_API_KEY
    
    // Retrofit service
    private val visionApiService: VisionApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://vision.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VisionApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        previewView = findViewById(R.id.preview_view)
        captureButton = findViewById(R.id.capture_button)
        analyzeButton = findViewById(R.id.analyze_button)
        imageView = findViewById(R.id.image_view)
        resultTextView = findViewById(R.id.result_text_view)
        
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        
        // Set up the listeners for capture and analyze buttons
        captureButton.setOnClickListener { takePhoto() }
        analyzeButton.setOnClickListener { analyzeHandwriting() }
        
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }
    
    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return
        
        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )
        
        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        
        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }
                
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                    
                    // Display the captured image
                    capturedImageFile = photoFile
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    imageView.setImageBitmap(bitmap)
                }
            }
        )
    }
    
    private fun analyzeHandwriting() {
        val imageFile = capturedImageFile ?: run {
            Toast.makeText(this, "Please capture an image first", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Show loading state
        resultTextView.text = "Analyzing handwriting..."
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Convert image to base64
                val base64Image = Base64Utils.imageFileToBase64(imageFile) ?: run {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "Failed to encode image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }
                
                // Create API request
                val request = VisionRequest(
                    requests = listOf(
                        AnnotateImageRequest(
                            image = Image(content = base64Image),
                            features = listOf(
                                Feature(type = "DOCUMENT_TEXT_DETECTION")
                            )
                        )
                    )
                )
                
                // Make API call
                val response = visionApiService.detectText(apiKey, request)
                
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val visionResponse = response.body()
                        val text = visionResponse?.responses?.firstOrNull()?.fullTextAnnotation?.text
                        
                        if (text != null) {
                            resultTextView.text = text
                        } else {
                            resultTextView.text = "No text detected"
                        }
                    } else {
                        resultTextView.text = "Error: ${response.errorBody()?.string()}"
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error analyzing handwriting", e)
                withContext(Dispatchers.Main) {
                    resultTextView.text = "Error: ${e.message}"
                }
            }
        }
    }
    
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            
            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
            
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
            
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this as LifecycleOwner, cameraSelector, preview, imageCapture
                )
                
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
            
        }, ContextCompat.getMainExecutor(this))
    }
    
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
    
    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}