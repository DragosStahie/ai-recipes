package com.example.airecipes

import android.app.Application
import androidx.room.Room
import com.example.airecipes.data.AppDatabase
import com.example.airecipes.data.dao.RecipeDao
import com.example.airecipes.data.repository.RecipeRepository
import com.example.airecipes.data.repository.RecipeRepositoryImplementation
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun Application.setupKoin() {
    startKoin {
        androidContext(this@setupKoin)
        modules(appModule)
    }
}


val appModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(androidApplication(), AppDatabase::class.java, "AI Recipes DB").build()
    }

    // DATA
    factory<RecipeDao> { get<AppDatabase>().recipeDao() }
    single<RecipeRepository> { RecipeRepositoryImplementation(get()) }
}