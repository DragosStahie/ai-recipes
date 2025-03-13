package com.example.airecipes

import android.app.Application

class AIRecipesApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        setupKoin()
    }
}