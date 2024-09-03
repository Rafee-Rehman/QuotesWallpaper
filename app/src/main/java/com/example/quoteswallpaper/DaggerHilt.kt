package com.example.quoteswallpaper

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.quoteswallpaper.data.ImagesDbDao
import com.example.quoteswallpaper.data.QuotesDao
import com.example.quoteswallpaper.data.QuotesDb
import com.example.quoteswallpaper.data.UriDatabase
import com.example.quoteswallpaper.presentation.homeScreen.image.DefaultDrawables
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


@HiltAndroidApp
class DaggerHiltBaseApp : Application()


@Module
@InstallIn(SingletonComponent::class)
object GetImageModule {
    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext
    @Provides
    @Singleton
    @Named("defDrawables")
    fun provideDefaultDrawables(): Array<Int> = DefaultDrawables().defaultImages
    @Provides
    @Singleton
    fun provideGetImageDatabase(context: Context): UriDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            UriDatabase::class.java,
            "uri_database"
        ).build()
    }
    @Provides
    fun provideUriDao(database: UriDatabase): ImagesDbDao {
        return database.dao
    }

    @Provides
    @Singleton
    fun provideQuotesDatabase(context: Context): QuotesDb {
        return Room.databaseBuilder(
            context.applicationContext,
            QuotesDb::class.java,
            "quotes_database"
        ).build()
    }
    @Provides
    @Singleton
    fun provideQuotesDao(db: QuotesDb): QuotesDao {
        return db.dao
    }

}

