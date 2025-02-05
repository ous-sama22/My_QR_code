package com.example.my_qr_code

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 * The main activity of the application, providing navigation options.
 *
 * Offers two buttons: one to navigate to the QR Code scanner and another to the generator.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Called when the activity is starting.
     *
     * Initializes the layout and sets click listeners to navigate to the respective activities.
     *
     * @param savedInstanceState If re-initializing, this Bundle contains the most recent data.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Locate navigation buttons
        val scanQrButton: Button = findViewById(R.id.btn_scan_qr)
        val generateQrButton: Button = findViewById(R.id.btn_generate_qr)

        // Start the QR scanner activity when the scan button is clicked
        scanQrButton.setOnClickListener {
            val intent = Intent(this, ScanQRActivity::class.java)
            startActivity(intent)
        }

        // Start the QR generator activity when the generate button is clicked
        generateQrButton.setOnClickListener {
            val intent = Intent(this, GenerateQR::class.java)
            startActivity(intent)
        }
    }
}
