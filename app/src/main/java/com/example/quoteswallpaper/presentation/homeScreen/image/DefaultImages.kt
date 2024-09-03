package com.example.quoteswallpaper.presentation.homeScreen.image

import android.content.Context
import android.net.Uri
import com.example.quoteswallpaper.R
import javax.inject.Inject
import javax.inject.Named

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
