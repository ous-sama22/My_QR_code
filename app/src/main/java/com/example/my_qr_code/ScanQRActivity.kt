package com.example.my_qr_code

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

/**
 * Activity responsible for scanning QR codes.
 *
 * This activity initiates a scan using the ZXing library after ensuring camera permissions.
 * The scanned result is displayed, and tapping the result navigates to the QR generator.
 */
class ScanQRActivity : AppCompatActivity() {

    private lateinit var btnScan: Button
    private lateinit var tvResult: TextView

    private val CAMERA_PERMISSION_REQUEST_CODE = 1001

    /**
     * Called when the activity is starting.
     *
     * Initializes UI elements, sets a click listener on the scan button to start the scanner,
     * and allows navigation to the QR generator by clicking on the result.
     *
     * @param savedInstanceState If re-initializing, this Bundle contains the most recent data.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qractivity)

        // Locate UI components
        btnScan = findViewById(R.id.btnScan)
        tvResult = findViewById(R.id.tvResult)


        // Start the QR scanning process when the button is clicked
        btnScan.setOnClickListener {
            startQRScanner()
        }
    }

    /**
     * Starts the QR scanner after checking for camera permissions.
     *
     * If the camera permission is not granted, the app requests it.
     */
    private fun startQRScanner() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            initiateScan()
        }
    }

    /**
     * Initiates the QR code scan using ZXing's [IntentIntegrator].
     */
    private fun initiateScan() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Scan a QR Code")
        integrator.setCameraId(0) // Use default camera
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(false)
        integrator.initiateScan()
    }

    /**
     * Handles the result of the permission request.
     *
     * If camera permission is granted, the QR scanner is initiated.
     *
     * @param requestCode  The request code passed in requestPermissions.
     * @param permissions  The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                initiateScan()
            } else {
                Toast.makeText(this, "La permission de la caméra est nécessaire.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Receives the result from the QR scanning activity.
     *
     * If a QR code is successfully scanned, its contents are displayed; otherwise,
     * an appropriate message is shown.
     *
     * @param requestCode The integer request code originally supplied.
     * @param resultCode  The integer result code returned by the child activity.
     * @param data        An [Intent] carrying the result data.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                tvResult.text = buildString {
                    append("Scanned Result:\n")
                    append(result.contents)
                }
            } else {
                tvResult.text = "No result found"
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
