package com.example.quoteswallpaper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.quoteswallpaper.ui.theme.QuotesWallpaperTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

//    private val vmGetQuote by viewModels<QuotesViewModel>()
//    private val vmGetImage by viewModels<GetImageViewModel>()
    @Inject
    lateinit var getImagedb: UriDatabase
    @Inject
    lateinit var quotesDb: QuotesDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuotesWallpaperTheme {
                NavGraph1()

//                val stateGetImage by vmGetImage.getImagesStates.collectAsState()
//                val stateGetQuote by vmGetQuote.quoteStates.collectAsState()
//                ScreenHome(stateGetImage = stateGetImage,
//                    onEventGetImage = vmGetImage::onEvent,
//                    statesQuotesScreen = stateGetQuote,
//                    onEventQuotesScreen = vmGetQuote::onEvent,
//                    next = {})
//                GetSetWallpaper(
//                    states = GetSetWallpaperStates(),
//                    onEvent = {},
//                    statesImage = stateGetImage,
//                    statesQuotesScreen = stateGetQuote
//                )
            }
        }
    }
}
