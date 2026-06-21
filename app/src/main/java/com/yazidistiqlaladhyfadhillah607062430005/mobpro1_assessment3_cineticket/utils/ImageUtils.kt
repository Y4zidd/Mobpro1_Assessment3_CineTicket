package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageUtils {
    fun copyUriToInternalStorage(context: Context, uri: Uri): String {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return ""
            val fileName = "ticket_img_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
}
