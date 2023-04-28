package com.example.textscanpro

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.icu.text.SimpleDateFormat
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.camera.core.*
import java.io.File
import java.util.*


class Camera : Fragment() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraView: PreviewView
    private lateinit var cameraButton : ImageView
    private lateinit var exitPhoto : ImageView
    private lateinit var exitView : View
    private lateinit var savePhoto : ImageView

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 10
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val TAG = "CameraActivity"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraView = view.findViewById(R.id.cameraView)
        cameraButton = view.findViewById(R.id.cameraButton)
        exitPhoto = view.findViewById(R.id.exit)
        exitView = view.findViewById(R.id.cameraTopView)
        savePhoto = view.findViewById(R.id.saveButton)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED)
        {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }

        cameraButton.setOnClickListener{
            takePhoto()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission is required to use the camera", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .apply {
                    setSurfaceProvider(cameraView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val photoFile = File(requireContext().externalMediaDirs.first(), "${SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                // The image has been captured and saved, so we can now do something with it
                val savedUri = Uri.fromFile(photoFile)
                Log.d(TAG, "Photo captured and saved to $savedUri")

                // Load the captured image into an ImageView for display
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                // val rotatedBitmap = getRotatedBitmap(bitmap, photoFile.absolutePath)
                val imageView = view?.findViewById<ImageView>(R.id.capturedImage)
                imageView?.setImageBitmap(bitmap)

                val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    cameraProvider.unbindAll()
                }, ContextCompat.getMainExecutor(requireContext()))

                exitView.visibility = View.VISIBLE
                exitPhoto.visibility = View.VISIBLE
                savePhoto.visibility = View.VISIBLE

                exitPhoto.setOnClickListener{
                    imageView?.setImageBitmap(null)
                    photoFile.delete()
                    exitPhoto.visibility = View.GONE
                    exitView.visibility = View.GONE
                    savePhoto.visibility = View.GONE
                    startCamera()
                }

                savePhoto.setOnClickListener{
                    MediaScannerConnection.scanFile(requireContext(), arrayOf(photoFile.absolutePath), arrayOf("image/jpeg"), null)
                    Toast.makeText(requireContext(), "Photo saved to ${photoFile.absolutePath}", Toast.LENGTH_SHORT).show()
                    imageView?.setImageBitmap(null)
                    exitPhoto.visibility = View.GONE
                    exitView.visibility = View.GONE
                    savePhoto.visibility = View.GONE
                    startCamera()
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e(TAG, "Error capturing photo: ${exception.message}", exception)
                Toast.makeText(requireContext(), "Error capturing photo: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}