package com.example.quoteswallpaper

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuotesViewModel @Inject constructor(
    private val quotesRepo : QuotesDao,
    private val utilities: Utilities
    ) :ViewModel() {

    private val _quoteStates = MutableStateFlow(QuotesStates())
    val quoteStates = _quoteStates.asStateFlow()

    fun onEvent(event: OnEventQuote){
        when(event){
            is OnEventQuote.SetCurrentQuote -> {
                _quoteStates.update {
                    it.copy(
                        currentQuote = event.string
                    )
                }
            }
            is OnEventQuote.SetQuoteColor -> {

            }
            is OnEventQuote.SetShowAddQuoteCard -> {
                _quoteStates.update {
                    it.copy(
                        showAddQuoteCard = event.bool
                    )
                }
            }
            is OnEventQuote.UpdateQuotesTextField -> {
                _quoteStates.update {
                    it.copy(
                        quotesTextField = event.string
                    )
                }
            }
            is OnEventQuote.GetAllQuotes -> {
                    viewModelScope.launch {
                        _quoteStates.update {
                            it.copy(
                                quotesList = quotesRepo.getQuotesTable()
                            )
                        }
                    }
            }
            is OnEventQuote.UpsertQuote -> {
                if (event.string.isNotEmpty()){
                    viewModelScope.launch {
                        event.string.lines().forEach {line->
                            if (line.isNotEmpty()){
                                quotesRepo.upsertQuote(Quotes(line))
                            }
                        }
                    }
                }
            }
            is OnEventQuote.DeleteQuote ->{
                viewModelScope.launch {
                    quotesRepo.deleteQuote(event.quote)
                }
            }
            is OnEventQuote.TextFromImage -> {
                    viewModelScope.launch {
                        _quoteStates.update {
                            it.copy(
                                quotesTextField = utilities.textRecognizer(event.uri!!)!!
                            )
                        }
                    }
            }
        }
    }
}

data class QuotesStates (
    val currentQuote: String = "",
    val quotesList: List<Quotes> = emptyList(),
    var quotesTextField : String = "",
    val quoteColor: Color = Color.Gray,
    val showAddQuoteCard: Boolean = false,
    )
interface OnEventQuote{
    class SetCurrentQuote(val string: String): OnEventQuote
    class UpdateQuotesTextField (val string: String): OnEventQuote
    class GetAllQuotes: OnEventQuote
    class UpsertQuote(val string: String): OnEventQuote
    class DeleteQuote(val quote: Quotes): OnEventQuote
    class SetShowAddQuoteCard(val bool: Boolean): OnEventQuote
    class TextFromImage(val uri: Uri?): OnEventQuote
    class SetQuoteColor(val color:Color): OnEventQuote
}



//    fun getRandomLineFromString(context: Context, ss: String?): String {
//        try {
//            val stringList: List<String> = if (!ss.isNullOrEmpty()) ss.lines() else emptyList()
//            val randLine = stringList[Random.nextInt(stringList.size)].ifEmpty {
//                stringList[Random.nextInt(stringList.size)]
//            }
//            _currentQuote.value = randLine
//            GlobalParams.currentQuote = randLine
//            return randLine
//        } catch (e: Exception) {
//            Toast.makeText(context, "Enter Some Quotes in given field", Toast.LENGTH_SHORT).show()
//            return ""
//        }
//    }
