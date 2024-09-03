package com.example.quoteswallpaper.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Upsert


@Entity
data class ImageUri(
    @PrimaryKey val oldUri : String,
    val uri: String,
)

@Dao
interface ImagesDbDao {
    @Upsert(entity = ImageUri::class)
    suspend fun upsertUri(imageUri: ImageUri)

    @Delete(entity = ImageUri::class)
    suspend fun deleteUri(imageUri: ImageUri)

    @Query("SELECT * FROM ImageUri")
    suspend fun getImageUriTable(): List<ImageUri>
}


@Database(entities = [ImageUri::class], version = 1)
abstract class UriDatabase : RoomDatabase() {
    abstract val dao: ImagesDbDao
}




