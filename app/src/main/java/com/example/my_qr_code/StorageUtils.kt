/**
 * StorageUtils.kt
 * Utility object for handling storage permissions and operations.
 *
 * This utility provides methods to:
 * - Check storage permissions
 * - Request storage permissions
 * - Access public directories
 *
 * Handles different permission requirements for various Android versions.
 */
package com.example.my_qr_code

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

object StorageUtils {
    /** Permission request code for storage access */
    const val STORAGE_PERMISSION_CODE = 1002

    /**
     * Checks if the app has required storage permissions.
     * Handles different permission models for Android 11+ and below.
     *
     * @param activity The activity context for permission checking
     * @return Boolean indicating if required permissions are granted
     */
    fun checkStoragePermissions(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val write = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            val read = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Requests storage permissions from the user.
     * For Android 11+: Opens system settings for all files access permission
     * For Android 10 and below: Requests read/write permissions
     *
     * @param activity The activity context for permission requesting
     */
    fun requestStoragePermissions(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.fromParts("package", activity.packageName, null)
                activity.startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                activity.startActivity(intent)
            }
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    /**
     * Gets the public directory for file storage.
     * @param type The type of public directory (e.g., Environment.DIRECTORY_PICTURES)
     * @return File object representing the requested public directory
     */
    fun getPublicDirectory(type: String): File {
        return Environment.getExternalStoragePublicDirectory(type)
    }
}