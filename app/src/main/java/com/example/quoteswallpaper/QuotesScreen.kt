package com.example.quoteswallpaper

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.launch


@Composable
fun QuoteScreen(
    states: QuotesStates,
    onEvent: (OnEventQuote) -> Unit,
){
    val lazyState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var lazyItemIndex1 by remember{ mutableIntStateOf(0) }

    if (states.quotesList.isEmpty()){
        onEvent(OnEventQuote.GetAllQuotes())
    }
    if(states.currentQuote.isEmpty() && states.quotesList.isNotEmpty()){
        onEvent(OnEventQuote.SetCurrentQuote(states.quotesList[0].quotes))
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd
        ){
            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
//                .border(width = 0.2.dp, Color.LightGray, RoundedCornerShape(4.dp))
                ,
                state = lazyState

            ) {
                itemsIndexed(states.quotesList){index, item ->
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .border(.1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                        .padding(2.dp)
                        .clickable {
                            lazyItemIndex1 = index
                            onEvent(OnEventQuote.SetCurrentQuote(item.quotes))
                        }
                        ,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(text = item.quotes, modifier = Modifier.fillMaxWidth(.7f))
                        if (item.quotes == states.currentQuote){
                            Icon(imageVector = Icons.Outlined.Done,
                                contentDescription = "done icon",
                                tint = Color.Green)
                        }
                        IconButton(onClick = {
                            onEvent(
                                OnEventQuote.DeleteQuote(
                                    Quotes(
                                        quotes = item.quotes,
                                        id = item.id
                                    )
                                )
                            )
                            onEvent(OnEventQuote.GetAllQuotes())
                        }) {
                            Icon(imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.LightGray)
                        }
                    }
                }
                scope.launch {
                    lazyState.animateScrollToItem(lazyItemIndex1)
                }
            }
            IconButton(onClick = {
                onEvent(OnEventQuote.UpdateQuotesTextField(""))
                onEvent(OnEventQuote.SetShowAddQuoteCard(bool = true))
            },
                modifier = Modifier
                    .offset(0.dp, 35.dp)
                    .size(60.dp)
//                    .clip(RoundedCornerShape(4.dp))
//                    .border(.1.dp, Color.Red, RoundedCornerShape(4.dp))
//                    .background(Color.Red)
            ) {
                Icon(imageVector = Icons.Default.AddCircle,
                    contentDescription ="add",
                    tint = Color.Red,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        if(states.showAddQuoteCard){
            Dialog(onDismissRequest = {
                onEvent(OnEventQuote.SetShowAddQuoteCard(false)) }
            ) {
                QuoteCard(states = states, onEvent = onEvent)
            }
        }
    }
}

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
            label = { Text(text = "Enter Each quote on new line")},
            modifier = Modifier
//                .fillMaxSize(.7f)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black)
        )
        TextButton(onClick = {
            onEvent(OnEventQuote.UpsertQuote(states.quotesTextField))
            onEvent(OnEventQuote.SetShowAddQuoteCard(false))
        }) {
            Text(text = "Add Quote")
        }
        TextButton(onClick = {imageLoadLauncher.launch("image/*")}) {
            Text(text = "From Image")
        }
    }
}


//@Composable
//fun colorPickerScreen1(
//    imageUri: Uri? = null,
//    onClick:()->Unit,
////    utilities: Utilities
//):Color {
//    var color by remember { mutableStateOf(Color.White) }
//    val controller = rememberColorPickerController()
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        HsvColorPicker(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(300.dp)
//                .padding(10.dp),
//            controller = controller,
//            onColorChanged = { colorEnvelope: ColorEnvelope ->
//                color = colorEnvelope.color
//            }
//        )
//        Box(
//            modifier = Modifier
//                .size(100.dp) // Size of the circle
//                .border(1.dp, Color.LightGray, CircleShape)
//                .background(color = color, shape = CircleShape)
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        TextButton(onClick = {onClick()}) {
//            Text(text = "Ok")
//        }
//
//
//    }
//
//    return color
//}



//Selection based on position
//@Composable
//fun QuoteScreen1(
//    states: QuotesStates,
//    onEvent: (OnEventQuote) -> Unit,
//    statesImage: GetImageStates,
//){
//    var showColorCard by remember { mutableStateOf(false) }
//    val lazyState = rememberLazyListState()
//    val layoutInfo by remember { derivedStateOf { lazyState.layoutInfo.visibleItemsInfo } }
//    val scope = rememberCoroutineScope()
//    var lazyItemIndex1 by remember{ mutableIntStateOf(0) }
//    val lazyColumnHeight = 60
//
//    onEvent(OnEventQuote.GetAllQuotes())
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//        ,
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ){
//        Text(text = "Quotes List")
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceAround,
//            verticalAlignment = Alignment.CenterVertically
//        ){
//            LazyColumn(modifier = Modifier
//                .fillMaxWidth(.85f)
//                .height(lazyColumnHeight.dp)
//                .border(width = 0.2.dp, Color.LightGray, RoundedCornerShape(4.dp))
//                ,
//                state = lazyState
//
//            ) {
//                itemsIndexed(states.quotesList){index, item ->
//                    if (index == 0){
//                        Spacer(modifier = Modifier.height((lazyColumnHeight*.4).dp))
//                    }
//                    Row(modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(start = 4.dp, end = 4.dp,)
////                        .clickable {
////                            lazyItemIndex1 = index
////                            onEvent(OnEventQuote.SetCurrentQuote(item.quotes))
////                        }
//                        ,
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ){
//                        Text(text = item.quotes)
//                        IconButton(onClick = {
//                            onEvent(
//                                OnEventQuote.DeleteQuote(
//                                    Quotes(
//                                        quotes = item.quotes,
//                                        id = item.id
//                                    )
//                                )
//                            )
//                            onEvent(OnEventQuote.GetAllQuotes())
//                        }) {
//                            Icon(imageVector = Icons.Default.Delete,
//                                contentDescription = "Delete",
//                                tint = Color.LightGray)
//                        }
//                    }
//                }
////                scope.launch {
////                    lazyState.animateScrollToItem(lazyItemIndex1)
////                }
//            }
//            IconButton(onClick = {
//                onEvent(OnEventQuote.UpdateQuotesTextField(""))
//                onEvent(OnEventQuote.SetShowAddQuoteCard(bool = true))
//            }) {
//                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
//            }
//
//
//        }
//        if(states.showAddQuoteCard){
//            Dialog(onDismissRequest = {
//                onEvent(OnEventQuote.SetShowAddQuoteCard(false)) }
//            ) {
//                QuoteCard(states = states, onEvent = onEvent)
//            }
//        }
//        if (layoutInfo.isNotEmpty()){
//            onEvent(
//                OnEventQuote.SetCurrentQuote(
//                    states.quotesList[
//                        layoutInfo[
//                            layoutInfo.size - 1
//                        ].index
//                    ].quotes
//                )
//            )
//        }
//        Text(text = states.currentQuote +  "  "+layoutInfo.size)
//
//        TextButton(
//            onClick = { showColorCard = !showColorCard }
//        ) {
//            Text(text = "Set\nColor",
//                textAlign = TextAlign.Center,
//                softWrap = false,
//                overflow = TextOverflow.Clip,
//            )
//
//        }
//        if (showColorCard) {
//            Dialog(onDismissRequest = {showColorCard= false})
//            {
//                Surface(
//                    shape = RoundedCornerShape(8.dp),
//                    color = Color.White,
//                    modifier = Modifier
//                        .wrapContentSize()
//                ){
//                    onEvent(
//                        OnEventQuote.SetQuoteColor(
//                            (colorPickerScreen(
//                                imageUri = statesImage.galleryImageUri,
//                                { showColorCard = false })
//                                    )
//                        )
//                    )
//                }
//            }
//        }
//    }
//}

//fun LazyListState.animateScrollAndCentralizeItem(index: Int, scope: CoroutineScope) {
//    val itemInfo = this.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
//    scope.launch {
//        if (itemInfo != null) {
//            val center = this@animateScrollAndCentralizeItem.layoutInfo.viewportEndOffset / 2
//            val childCenter = itemInfo.offset + itemInfo.size / 2
//            this@animateScrollAndCentralizeItem.animateScrollBy((childCenter - center).toFloat())
//        } else {
//            this@animateScrollAndCentralizeItem.animateScrollToItem(index)
//        }
//    }
//}
