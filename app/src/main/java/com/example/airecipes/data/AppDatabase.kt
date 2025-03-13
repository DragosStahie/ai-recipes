package com.example.airecipes.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.airecipes.data.dao.RecipeDao
import com.example.airecipes.data.entity.RecipeEntity

@Database(
    entities = [RecipeEntity::class],
    version = 1,
    exportSchema = true,
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
}