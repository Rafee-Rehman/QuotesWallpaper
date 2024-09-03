package com.example.quoteswallpaper.presentation.finalScreen

import android.net.Uri
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quoteswallpaper.common.Utilities
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
                        currentImageUri = event.uri
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
                viewModelScope.launch {
                    utilities.setWallpaper(
                        event.gl)
                }

            }
        }
    }
}


data class GetSetWallpaperStates (
    val currentImageUri: Uri? = null,
    val currentQuote: String = "",
)

interface OnEventGetSetWallpaper{
    class SetCurrentImageUri(val uri: Uri?): OnEventGetSetWallpaper
    class SetCurrentQuote(val string: String): OnEventGetSetWallpaper
    class SetWallpaper(val gl: GraphicsLayer): OnEventGetSetWallpaper

}