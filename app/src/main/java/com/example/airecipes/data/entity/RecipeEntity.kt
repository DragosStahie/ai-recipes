package com.example.airecipes.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "RECIPE")
data class RecipeEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "TITLE")
    val title: String,

    @ColumnInfo(name = "COOKING_TIME")
    val cookingTime: String,

    @ColumnInfo(name = "INGREDIENTS")
    val ingredients: String,

    @ColumnInfo(name = "INSTRUCTIONS")
    val instructions: String,

    @ColumnInfo(name = "IMAGE_URL")
    val imageUrl: String,
)