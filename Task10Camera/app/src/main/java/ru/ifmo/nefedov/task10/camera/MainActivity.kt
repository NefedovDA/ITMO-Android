package ru.ifmo.nefedov.task10.camera


import android.content.pm.PackageManager
import android.graphics.Matrix
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.obsez.android.lib.filechooser.ChooserDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity(), LifecycleOwner {

    private var alreadyAskDir: Boolean = false
    private var _dirForSave: File? = null
    private val dirForSave: File
        get() = _dirForSave ?: externalMediaDirs.first()

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var textureView: TextureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textureView = view_finder
        savedInstanceState?.getBoolean(FLAG_KEY)?.let { alreadyAskDir = it }
        savedInstanceState?.getString(DIR_PATH_KEY)?.let { _dirForSave = File(it) }

        if (allPermissionsGranted()) {
            if (!alreadyAskDir) {
                alreadyAskDir = true
                askFolder()
            }
            textureView.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun askFolder() {
        ChooserDialog(this@MainActivity)
            .withFilter(true, false)
            .withChosenListener { _, file -> _dirForSave = file }
            .build()
            .show()
    }

    private fun startCamera() {
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(textureView.width, textureView.height))
        }.build()

        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
            val parent = textureView.parent as ViewGroup
            parent.removeView(textureView)
            parent.addView(textureView, 0)

            textureView.surfaceTexture = it.surfaceTexture
            updateTransform(it)
        }

        val imageCaptureConfig = ImageCaptureConfig.Builder().apply {
            setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            setTargetRotation(windowManager.defaultDisplay.rotation)
        }.build()

        val imageCapture = ImageCapture(imageCaptureConfig)
        textureView.setOnClickListener {
            val file = File(dirForSave, "${System.currentTimeMillis()}.jpg")

            imageCapture.takePicture(file, executor,
                object : ImageCapture.OnImageSavedListener {
                    override fun onImageSaved(file: File) {
                        val msg = "Photo capture succeeded: ${file.absolutePath}"
                        textureView.post {
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(
                        imageCaptureError: ImageCapture.ImageCaptureError,
                        message: String,
                        cause: Throwable?
                    ) {
                        val msg = "Photo capture failed: $message"
                        textureView.post {
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                })
        }

        CameraX.bindToLifecycle(this, preview, imageCapture)
    }

    override fun onDestroy() {
        super.onDestroy()
        CameraX.unbindAll()
    }

    private fun updateTransform(output: Preview.PreviewOutput) {
        val metrics = DisplayMetrics().also { textureView.display.getRealMetrics(it) }
        val pWidth = metrics.widthPixels
        val pHeight = metrics.heightPixels

        val w = output.textureSize.width
        val h = output.textureSize.height

        val centerX = textureView.width.toFloat() / 2f
        val centerY = textureView.height.toFloat() / 2f

        val rotationDegrees = when (textureView.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }

        val matrix = Matrix()
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)
        matrix.postScale(
            pWidth.toFloat() / h,
            pHeight.toFloat() / w,
            centerX,
            centerY
        )
        textureView.setTransform(matrix)
    }

    private fun allPermissionsGranted(): Boolean =
        REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(FLAG_KEY, alreadyAskDir)
        outState.putString(DIR_PATH_KEY, _dirForSave?.path)
    }

    companion object {
        const val REQUEST_CODE_PERMISSIONS = 101
        val REQUIRED_PERMISSIONS =
            arrayOf("android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE")

        const val FLAG_KEY = "flag_key"
        const val DIR_PATH_KEY = "dir_path_key"
    }
}
