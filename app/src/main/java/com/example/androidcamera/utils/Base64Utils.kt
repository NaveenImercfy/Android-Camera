package com.example.androidcamera.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

/**
 * Utility class for encoding and decoding images to/from Base64 format
 */
object Base64Utils {

    /**
     * Convert a Bitmap to a Base64 encoded string
     *
     * @param bitmap The bitmap to convert
     * @param quality The JPEG compression quality (0-100)
     * @return Base64 encoded string of the bitmap
     */
    fun bitmapToBase64(bitmap: Bitmap, quality: Int = 100): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    /**
     * Convert a Base64 encoded string to a Bitmap
     *
     * @param base64String The Base64 encoded string
     * @return The decoded Bitmap
     */
    fun base64ToBitmap(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    /**
     * Convert an image file to a Base64 encoded string
     *
     * @param imageFile The image file to convert
     * @return Base64 encoded string of the image file
     */
    fun imageFileToBase64(imageFile: File): String? {
        return try {
            val fileInputStream = FileInputStream(imageFile)
            val bytes = ByteArray(imageFile.length().toInt())
            fileInputStream.read(bytes)
            fileInputStream.close()
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Compress a bitmap to reduce its size before Base64 encoding
     *
     * @param bitmap The bitmap to compress
     * @param maxSizeKB The maximum size in KB
     * @return Base64 encoded string of the compressed bitmap
     */
    fun compressAndEncodeImage(bitmap: Bitmap, maxSizeKB: Int): String {
        var quality = 100
        var base64String: String
        
        do {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
            
            // Calculate size in KB
            val sizeKB = base64String.length * 3 / 4 / 1024
            
            if (sizeKB > maxSizeKB && quality > 10) {
                quality -= 10
            } else {
                break
            }
        } while (true)
        
        return base64String
    }
}