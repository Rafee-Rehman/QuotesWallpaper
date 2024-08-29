package com.example.quoteswallpaper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.unit.dp


@Composable
fun ScreenHome(
    stateGetImage: GetImageStates,
    onEventGetImage:(GetImageOnEvent) -> Unit,
    statesQuotesScreen: QuotesStates,
    onEventQuotesScreen: (OnEventQuote) -> Unit,
    next: () -> Unit

){
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ){

        GetImage(state = stateGetImage ,
            onEvent = onEventGetImage)
        Spacer(modifier = Modifier.height(12.dp))

        QuoteScreen(states = statesQuotesScreen,
            onEvent = onEventQuotesScreen)
        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { next() }) {
            Text(text = "Next")
        }
    }
}