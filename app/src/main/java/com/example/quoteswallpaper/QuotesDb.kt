package com.example.quoteswallpaper

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Upsert


@Entity
data class Quotes(
    val quotes: String,
    @PrimaryKey(autoGenerate = true) val id:Int = 0
)

@Dao
interface QuotesDao{
    @Upsert
    suspend fun upsertQuote(quote: Quotes)

    @Delete
    suspend fun deleteQuote(quote: Quotes)

    @Query("SELECT * FROM Quotes")
    suspend fun getQuotesTable(): List<Quotes>
}

@Database(entities = [Quotes::class], version = 1)
abstract class QuotesDb (): RoomDatabase(){
    abstract val dao : QuotesDao
}