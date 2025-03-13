package com.example.airecipes.ui.model

data class RecipeItem(
    val title: String,
    val cookingTime: String,
    val ingredients: String,
    val instructions: String,
    val imageUrl: String,
    val isFavorite: Boolean,
) {
    companion object {
        fun empty() = RecipeItem("", "", "", "", "", false)
    }
}