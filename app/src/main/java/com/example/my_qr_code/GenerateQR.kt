package com.example.my_qr_code

import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream

class GenerateQR : AppCompatActivity() {
    private var generatedQRCode: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_qr)

        setupViews()
    }

    private fun setupViews() {
        val inputText: EditText = findViewById(R.id.et_input_text)
        val generateButton: Button = findViewById(R.id.btn_generate)
        val saveButton: Button = findViewById(R.id.btn_save_qr_code)
        val qrCodeImageView: ImageView = findViewById(R.id.iv_qr_code)

        generateButton.setOnClickListener {
            val text = inputText.text.toString()
            if (text.isNotEmpty()) {
                val qrCode = generateQRCode(text)
                generatedQRCode = qrCode
                qrCodeImageView.setImageBitmap(qrCode)
            } else {
                Toast.makeText(this, "Veuillez saisir du texte.", Toast.LENGTH_SHORT).show()
            }
        }

        saveButton.setOnClickListener {
            if (!StorageUtils.checkStoragePermissions(this)) {
                StorageUtils.requestStoragePermissions(this)
                return@setOnClickListener
            }

            generatedQRCode?.let {
                saveQRCodeImage(it)
            } ?: run {
                Toast.makeText(this, "Générez d'abord un QR Code.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateQRCode(text: String): Bitmap {
        val writer = QRCodeWriter()
        val size = 512
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)

        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    private fun saveQRCodeImage(bitmap: Bitmap) {
        val filename = "QRCode_${System.currentTimeMillis()}.png"
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveImageWithMediaStore(bitmap, filename)
            } else {
                saveImageToDirectory(bitmap, filename)
            }
            Toast.makeText(this, "QR Code enregistré dans le dossier Pictures", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("GenerateQR", "Error saving QR code", e)
            Toast.makeText(this, "Erreur lors de l'enregistrement du QR Code.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageWithMediaStore(bitmap: Bitmap, filename: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let { contentResolver.openOutputStream(it) }?.use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
    }

    private fun saveImageToDirectory(bitmap: Bitmap, filename: String) {
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, filename)
        FileOutputStream(file).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == StorageUtils.STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                generatedQRCode?.let { saveQRCodeImage(it) }
            } else {
                Toast.makeText(this, "Permission de stockage nécessaire pour sauvegarder les QR codes", Toast.LENGTH_LONG).show()
            }
        }
    }
}