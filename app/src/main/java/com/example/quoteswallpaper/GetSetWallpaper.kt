package com.example.quoteswallpaper

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun GetSetWallpaper(
//    states: GetSetWallpaperStates,
//    onEvent: (OnEventGetSetWallpaper) -> Unit,
    imageUri: Uri?,
    quote: String,
){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()
    val snackbarHostState = remember { SnackbarHostState() }
    var showOption by remember{ mutableStateOf(false)}
    var showColorCard by remember { mutableStateOf(false) }
    var color by remember { mutableStateOf(Color.Black)    }
    var image: Bitmap? = null


    fun setWallpaper(){
        val wallpaperManager = WallpaperManager.getInstance(context)
        coroutineScope.launch {
            val bitmap = graphicsLayer.toImageBitmap()
            wallpaperManager.setBitmap(bitmap.asAndroidBitmap(), null, true, WallpaperManager.FLAG_SYSTEM)
            wallpaperManager.wallpaperInfo
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
            },
                modifier = Modifier
                    .padding(8.dp)
            ) {
                if (showOption){
                    Column (
                        modifier = Modifier
                    ){
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "more",
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    showColorCard = true
                                    showOption = false
                                }
                        )
                        Icon(imageVector = Icons.Default.Done, contentDescription = "done",
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    setWallpaper()
                                    showOption = false
                                }
                        )
                    }
                }
                else{
                    Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu",
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                showOption = true
                            })
                }
                if (showColorCard) {
                    Dialog(onDismissRequest = {showColorCard= false})
                    {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            modifier = Modifier
                                .wrapContentSize()
                        ){
                            color = colorPickerScreen (
                                { showColorCard = false })
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .drawWithCache {
                    onDrawWithContent {
                        graphicsLayer.record {
                            this@onDrawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    }
                }
        ) {
            ScreenContentToCapture(
                imageUri,
                quote,
                color
            )
        }
    }
}


@Composable
fun ScreenContentToCapture(
    imageUri: Uri?,
    textMain:String = "",
    color: Color = Color.Black
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var textSize by remember{ mutableFloatStateOf(0f) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {

        Box (
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .drawBehind {
                    textSize = this.size.width * .05f
                }
        ){
            Image(
                painter =  rememberAsyncImagePainter(imageUri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            Text(
                textMain,
                modifier = Modifier
                    .padding(top = 32.dp)
                    .padding(32.dp)
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    }
                ,
                lineHeight = textSize.sp,
                fontFamily = FontFamily(Font(R.font.aclonica_regular)),
                fontSize = if (textSize < 45) textSize.sp else 45.sp,
                color = color
            )
        }
    }
}

@Composable
fun colorPickerScreen(
//    imageUri: Uri? = null,
    onClick:()->Unit,
//    utilities: Utilities
):Color {
    var color by remember { mutableStateOf(Color.White) }
    val controller = rememberColorPickerController()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HsvColorPicker(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(10.dp),
            controller = controller,
            onColorChanged = { colorEnvelope: ColorEnvelope ->
                color = colorEnvelope.color
            }
        )
        Box(
            modifier = Modifier
                .size(100.dp) // Size of the circle
                .border(1.dp, Color.LightGray, CircleShape)
                .background(color = color, shape = CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = {onClick()}) {
            Text(text = "Ok")
        }


    }

    return color
}

