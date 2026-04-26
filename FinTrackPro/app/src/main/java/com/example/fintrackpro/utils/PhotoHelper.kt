package com.example.fintrackpro.utils

import android.content.Context
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PhotoHelper {
    fun createImageFile(context: Context): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageDir = File(context.filesDir, "expense_photos")
        if (!imageDir.exists()) imageDir.mkdirs()
        return try {
            File.createTempFile("JPEG_${timeStamp}_", ".jpg", imageDir)
        } catch (e: IOException) {
            null
        }
    }
}