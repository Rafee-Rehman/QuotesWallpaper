package com.example.quoteswallpaper

import android.app.WallpaperManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.util.Size
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Named
import kotlin.random.Random


class DefaultImages @Inject constructor(
    private val context: Context,
    @Named("defDrawables") private val drawables: Array<Int>
){
    fun defImages(): List<Uri> {
        var images = arrayOf<Uri>()
        drawables.forEach {
            val imgUri = Uri.parse("android.resource://${context.packageName}/${it}")
            images += imgUri
        }
        return images.toList()
    }
}

data class DefaultDrawables(
    val defaultImages: Array<Int> = arrayOf(
        R.drawable.defaultwallpaper,
        R.drawable.defaultwallpaper1,
        R.drawable.defaultwallpaper2,
        R.drawable.defaultwallpaper3,
        R.drawable.defaultwallpaper4,
        R.drawable.defaultwallpaper5
    )
)

class Utilities @Inject constructor(private val context: Context){
    fun provideToast(ss:String): Toast {
        return Toast.makeText(context, ss, Toast.LENGTH_SHORT)
    }
    fun saveImageToInternalStorage(bitmap: Bitmap, fileName: String): Uri? {
        return try {
            // Create a file in the internal storage
            val file = File(context.filesDir, fileName)
            val fos = FileOutputStream(file)

            // Compress the bitmap and write it to the file
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)

            // Close the FileOutputStream
            fos.flush()
            fos.close()

            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun setWallpaper(bitmap: Bitmap){
        val wallpaperManager = WallpaperManager.getInstance(context)
//        val bitmap = graphicsLayer.toImageBitmap()
        wallpaperManager.setBitmap(bitmap,
            null, true,
            WallpaperManager.FLAG_SYSTEM)
        wallpaperManager.wallpaperInfo
    }

    suspend fun loadImageFromInternalStorage(fileName: String): Bitmap? {
        return try {
            // Create a file in the internal storage
            val file = File(context.filesDir, fileName)

            // Open the file as an input stream
            val fis = FileInputStream(file)

            // Decode the stream into a bitmap
            val bitmap = BitmapFactory.decodeStream(fis)

            // Close the FileInputStream
            fis.close()

            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getImageNameFromUri(uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    return it.getString(nameIndex)
                }
            }
        }
        return null
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun generateThumbnail(uri: Uri): Bitmap{
        val thumbnail: Bitmap =
            context.contentResolver.loadThumbnail(
                uri, Size(640, 480), null)
        return thumbnail
    }

    suspend fun textRecognizer(uri: Uri):String?{
        var extractedText = ""
        var formattedText:String? = null
        // Example bitmap image
//    val bitmap: Bitmap = getBitmapFromUri(context, uri)
//    val bitmap: Bitmap = BitmapFactory.decodeFile("R.drawable.sdf")

        // Initialize the text recognizer
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        // Create an InputImage from the bitmap
//        val image = InputImage.fromBitmap(bitmap, 0)
        val image = InputImage.fromFilePath(context, uri)

        // Process the image and extract text
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // Task completed successfully
                extractedText = visionText.text
                // Do something with the extracted text
                formattedText = extractedText.replace("\n", " ")

            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                e.printStackTrace()
            }
        delay(1000)
        return formattedText
    }

    fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            // Open an InputStream from the URI
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            // Decode the InputStream into a Bitmap
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteImageFromInternalStorage(fileName: String): Boolean {
        // Create a file object for the image file
        val file = File(context.filesDir, fileName)

        // Check if the file exists and delete it
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }

    fun deleteImageFromUri(uri: Uri): Boolean {
        return try {
            // Get the file path from the Uri
            val file = File(uri.path ?: "")

            // Delete the file
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun generateRandomString(length: Int = 3): String {
        // Define the character pool (A-Z)
        val charPool: List<Char> = ('a'..'z').toList() //+ ('A'..'Z')

        // Generate the random string
        return (1..length)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
    fun SaveLocal(key:String, value:String){
        val sharedData: SharedPreferences =
            context.getSharedPreferences("TextWallpaperAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedData.edit()
        editor.putString(key, value)
        editor.apply()
        Toast.makeText(context, "Saved Local", Toast.LENGTH_SHORT).show()
    }


    fun retrieveLocal(key:String): Uri? {

// Retrieving the string from SharedPreferences
        val sharedData: SharedPreferences =
            context.getSharedPreferences("TextWallpaperAppPrefs", Context.MODE_PRIVATE)
        val myString = sharedData.getString(key, null)
        if (myString != null) {
            val myStringUri = Uri.parse(myString)
            return myStringUri
        }
        return null
    }

}



