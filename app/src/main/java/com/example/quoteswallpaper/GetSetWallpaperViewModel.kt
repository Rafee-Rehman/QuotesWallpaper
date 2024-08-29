package com.example.quoteswallpaper

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class GetSetWallpaperViewModel @Inject constructor(
    private val utilities: Utilities,
) : ViewModel() {
    private val _getSetWallpaperStates = MutableStateFlow(GetSetWallpaperStates())
    val getSetWallpaperStates = _getSetWallpaperStates.asStateFlow()

    fun onEvent(event: OnEventGetSetWallpaper){
        when (event) {
            is OnEventGetSetWallpaper.SetCurrentImageUri -> {
                _getSetWallpaperStates.update {
                    it.copy(
                        currentImageUri = event.string
                    )
                }
            }
            is OnEventGetSetWallpaper.SetCurrentQuote -> {
                _getSetWallpaperStates.update {
                    it.copy(
                        currentQuote = event.string
                    )
                }
            }
            is OnEventGetSetWallpaper.SetWallpaper -> {
                utilities.setWallpaper(
                    event.image
                )
            }
        }
    }
}


data class GetSetWallpaperStates (
    val currentImageUri: String = "",
    val currentQuote: String = "",
)

interface OnEventGetSetWallpaper{
    class SetCurrentImageUri(val string: String): OnEventGetSetWallpaper
    class SetCurrentQuote(val string: String): OnEventGetSetWallpaper
    class SetWallpaper(val image: Bitmap): OnEventGetSetWallpaper

}