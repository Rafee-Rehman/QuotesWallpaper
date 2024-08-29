package com.example.quoteswallpaper

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun GetImage(
    state: GetImageStates,
    onEvent:(GetImageOnEvent) -> Unit,
){
    val lazyState1 = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var lazyItemIndex1 by remember{ mutableIntStateOf(5) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            onEvent(GetImageOnEvent.SetImageUri(uri))
            onEvent(GetImageOnEvent.UpsertImageUri(uri))
            onEvent(GetImageOnEvent.GetAllImageUri())
        }
    }
    if (state.galleryImageUri == null){
        onEvent(GetImageOnEvent.GetAllImageUri())
        onEvent(GetImageOnEvent.SetImageUri(state.defaultImagesList?.get(5)))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ,
        contentAlignment = Alignment.BottomEnd
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
            ,
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ){
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                ,
                state = lazyState1
            ) {
//                itemsIndexed(items = state.imageUrisList) { index, item ->
                itemsIndexed(
                    items = state.imageList,
                    key = {index, item -> state.imageList[index].uri}
                ) { index, item ->

                    Box(modifier = Modifier,
                        contentAlignment = Alignment.Center
                    ){
                        AsyncImage(item.uri,
                        "Image Loaded from Image List",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(300.dp)
                            .padding(4.dp)
                            .border(.1.dp, Color.Transparent, RoundedCornerShape(4.dp))
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                lazyItemIndex1 = index
                                onEvent(GetImageOnEvent.SetImageUri(item.uri.toUri()))
                            }
                    )
                        if (item.uri == state.galleryImageUri.toString()){
                            Icon(imageVector = Icons.Outlined.Done,
                                contentDescription = "done icon",
                                modifier = Modifier
                                    .offset(0.dp, 50.dp)
                                    .size(40.dp)
                                    .border(0.5.dp, Color.Black, RoundedCornerShape(4.dp)),
                                tint = Color.Green)
                        }
                        if(state.defaultImagesList?.contains(item.uri.toUri()) == false){
                            IconButton(onClick = {
                                onEvent(GetImageOnEvent.DeleteImageUri(item.uri.toUri()))
                                onEvent(GetImageOnEvent.GetAllImageUri())
                                onEvent(GetImageOnEvent.SetImageUri(state.defaultImagesList[5]))
                            },
                                modifier = Modifier
                                    .size(40.dp)
                                    .border(0.5.dp, Color.Black, RoundedCornerShape(4.dp))
                            ){
                                Icon(imageVector = Icons.Outlined.Delete,
                                    contentDescription ="Delete icon",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
                scope.launch {
                    delay(200)
                    lazyState1.animateScrollToItem(lazyItemIndex1, 3)
//                    lazyState1.scrollToItem(lazyItemIndex1, )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        IconButton(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier
                .offset(0.dp, 35.dp)
                .size(60.dp)
//                .clip(RoundedCornerShape(4.dp))
//                .border(.1.dp, Color.Red, RoundedCornerShape(4.dp))
//                .background(Color.Red)

        ) {
            Icon(imageVector = Icons.Default.AddCircle,
                contentDescription ="add",
                tint = Color.Red,
                modifier = Modifier.size(40.dp)
            )
        }
    }

}