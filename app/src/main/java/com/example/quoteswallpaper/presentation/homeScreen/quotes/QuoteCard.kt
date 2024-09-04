package com.example.quoteswallpaper.presentation.homeScreen.quotes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun QuoteCard(states: QuotesStates, onEvent: (OnEventQuote) -> Unit){
    val imageLoadLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            onEvent(OnEventQuote.TextFromImage(uri))
        }
    }
    Column (modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(Color.White)
        .padding(16.dp)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        OutlinedTextField(
            value = states.quotesTextField,
            onValueChange = {
                onEvent(OnEventQuote.UpdateQuotesTextField(it))
            },
            label = { Text(text = "Enter Each quote on new line") },
            modifier = Modifier
//                .fillMaxSize(.7f)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black)
        )
        TextButton(onClick = {
            onEvent(OnEventQuote.UpsertQuote(states.quotesTextField))
            onEvent(OnEventQuote.GetAllQuotes())
            onEvent(OnEventQuote.SetShowAddQuoteCard(false))
        }) {
            Text(text = "Add Quote")
        }
        TextButton(onClick = {imageLoadLauncher.launch("image/*")}) {
            Text(text = "From Image")
        }
    }
}
