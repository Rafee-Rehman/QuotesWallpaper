package com.example.quoteswallpaper.presentation.homeScreen.image

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quoteswallpaper.data.ImageUri
import com.example.quoteswallpaper.data.ImagesDbDao
import com.example.quoteswallpaper.common.Utilities
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class GetImageViewModel @Inject constructor(
    private val defaultImages: DefaultImages,
    private val imageUriRepo: ImagesDbDao,
    private val utilities: Utilities
    ) : ViewModel() {

    private val _getImagesStates = MutableStateFlow(GetImageStates())
    val getImagesStates = _getImagesStates.asStateFlow()


    fun onEvent(event: GetImageOnEvent) {
        when(event){
            is GetImageOnEvent.SetImageUri -> {
                _getImagesStates.update {
                    it.copy(
                        galleryImageUri = event.uri,
                        )
                }
            }
            is GetImageOnEvent.UpsertImageUri -> {
                val bitmap = utilities.uriToBitmap(event.newUri!!)
                val randStr = LocalTime.now().toString()
                viewModelScope.launch {
                    imageUriRepo.upsertUri(
                        ImageUri(
                        uri = if(event.newUri in defaultImages.defImages()) event.newUri.toString()
                        else
                            utilities.saveImageToInternalStorage(bitmap!!, randStr).toString(),
                        oldUri = event.newUri.toString(),
                    )
                    )
                }
            }
            is GetImageOnEvent.DeleteImageUri -> {
                getImagesStates.value.imageUrisList.forEach {
                    if (it.uri == event.delUri.toString()){
                        viewModelScope.launch {
                            imageUriRepo.deleteUri(ImageUri(it.oldUri, it.uri))
                            utilities.deleteImageFromUri(it.uri.toUri())
                        }
                    }
                }
            }
            is GetImageOnEvent.GetAllImageUri -> {
                viewModelScope.launch {
                    if (!getImagesStates.value.defaultImagesAdded) { // DefaultImages will be added one time as app starts
                        _getImagesStates.update {
                            it.copy(
                                defaultImagesAdded = true,
                                defaultImagesList = defaultImages.defImages()
                            )
                        }
                        defaultImages.defImages().forEach {
                            imageUriRepo.upsertUri(
                                ImageUri(
                                    uri = it.toString(),
                                    oldUri = it.toString()
                                )
                            )
                        }
                    }
                    _getImagesStates.update {
                        it.copy(
                            imageUrisList = imageUriRepo.getImageUriTable(),
//                            timesGetAllImagesUri = it.timesGetAllImagesUri + 1
                        )
                    }
                    if (getImagesStates.value.imageUrisList.isNotEmpty()) {
                        var imgList = emptySet<ImageBitmapR>()
                        getImagesStates.value.imageUrisList.forEach { uri ->
                            imgList = imgList.plus(
                                ImageBitmapR(
                                    img = utilities.uriToBitmap(uri.uri.toUri())!!
                                        .asImageBitmap(), uri = uri.uri
                                )
                            )
                        }
                        _getImagesStates.update {
                            it.copy(
                                imageList = imgList.toList(),
//                                timesUpdateImageR = getImagesStates.value.timesUpdateImageR + 1
                            )
                        }
                    }
                }
            }
//            is GetImageOnEvent.UpdateImageListR -> {
//            }
//            is GetImageOnEvent.GenerateThumbnail -> {
//            }
//            is GetImageOnEvent.TimesUpdateImageR ->{
//                _getImagesStates.update {
//                    it.copy(
//                    )
//                }
//            }
        }
    }
}

interface GetImageOnEvent{
    class SetImageUri(val uri:Uri?): GetImageOnEvent
    class DeleteImageUri(val delUri: Uri?): GetImageOnEvent
    class UpsertImageUri(val newUri: Uri?): GetImageOnEvent
    class GetAllImageUri: GetImageOnEvent
//    class UpdateImageListR: GetImageOnEvent
//    class GenerateThumbnail(val uri: Uri?): GetImageOnEvent

//    class TimesUpdateImageR(val int:Int):GetImageOnEvent
}

data class GetImageStates (
    val defaultImagesAdded: Boolean = false,
    val defaultImagesList: List<Uri>? = null,
    val galleryImageUri: Uri? = null,
    val imageUrisList: List<ImageUri> = emptyList(),
    val imageList: List<ImageBitmapR> = emptyList(),
//    val imageThumbnail: ImageBitmap? = null,
//
//    val timesUpdateImageR: Int = 0,
//    val timesGetAllImagesUri: Int = 0
)

data class ImageBitmapR(
    val img: ImageBitmap? = null,
    val uri: String = ""
)




