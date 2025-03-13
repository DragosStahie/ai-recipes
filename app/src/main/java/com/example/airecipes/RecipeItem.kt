package com.example.airecipes

data class RecipeItem(
    val id: String,
    val title: String,
    val cookingTime: String,
    val ingredients: String,
    val instructions: String,
    val imageUrl: String,
) {
    companion object {
        fun empty() = RecipeItem("", "", "", "", "", "")
    }
}