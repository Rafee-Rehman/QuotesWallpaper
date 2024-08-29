package com.example.quoteswallpaper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavGraph1(
    navController: NavHostController = rememberNavController(),
) {
    val vmGetQuote = hiltViewModel<QuotesViewModel>()
    val vmGetImage = hiltViewModel<GetImageViewModel>()
//    val vmGetSetImage = hiltViewModel<GetSetWallpaperViewModel>()
    val stateGetImage by vmGetImage.getImagesStates.collectAsState()
    val statesQuotesScreen by vmGetQuote.quoteStates.collectAsState()
//    val statesGetSetWallpaper by vmGetSetImage.getSetWallpaperStates.collectAsState()
    val onEventGetImage = vmGetImage::onEvent
    val onEventQuotesScreen = vmGetQuote::onEvent
//    val onEventGetSetWallpaper = vmGetSetImage::onEvent

    NavHost(navController = navController, startDestination = ScreenHomeRoute.toString()) {
        composable(ScreenHomeRoute.toString()) {
            ScreenHome(
                stateGetImage = stateGetImage,
                onEventGetImage = onEventGetImage,
                statesQuotesScreen = statesQuotesScreen,
                onEventQuotesScreen = onEventQuotesScreen,
                next = {navController.navigate(
                    GetSetWallpaperRoute.toString()
                )}
            )
        }
        composable(GetSetWallpaperRoute.toString()) {
            GetSetWallpaper(
//                states = statesGetSetWallpaper,
//                onEvent = onEventGetSetWallpaper,
                imageUri = stateGetImage.galleryImageUri,
                quote = statesQuotesScreen.currentQuote
            )
        }
    }
}