package com.hamidjonhamidov.cvforkhamidjon.di

import android.app.Application
import android.preference.PreferenceManager
import androidx.room.Room
import com.bumptech.glide.Glide
import com.hamidjonhamidov.cvforkhamidjon.data_requests.persistence.AppDatabase
import com.hamidjonhamidov.cvforkhamidjon.util.constants.DATABASE_CONSTANTS
import com.hamidjonhamidov.cvforkhamidjon.util.glide.GlideManager
import com.hamidjonhamidov.cvforkhamidjon.util.glide.GlideRequestManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule{

    @JvmStatic
    @Singleton
    @Provides
    fun provideGlideRequestManager(
        application: Application
    ) : GlideManager =
        GlideRequestManager(
            Glide.with(application)
        )

    @JvmStatic
    @Singleton
    @Provides
    fun provideAppDb(app: Application) =
        Room
            .databaseBuilder(app, AppDatabase::class.java, DATABASE_CONSTANTS.APP_DATABASE)
            .fallbackToDestructiveMigration()
            .build()
}