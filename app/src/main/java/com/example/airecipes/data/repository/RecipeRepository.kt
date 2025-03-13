package com.example.airecipes.data.repository

import com.example.airecipes.data.dao.RecipeDao
import com.example.airecipes.data.entity.RecipeEntity
import com.example.airecipes.ui.model.RecipeItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

interface RecipeRepository {
    suspend fun insert(vararg recipe: RecipeItem)
    suspend fun deleteByTitle(titleToDelete: String)
    fun getAll(): Flow<List<RecipeItem>>
}

class RecipeRepositoryImplementation(
    private val recipeDao: RecipeDao
) : RecipeRepository {
    override suspend fun insert(vararg recipe: RecipeItem) =
        recipeDao.insert(*recipe.map {
            RecipeEntity(
                title = it.title,
                cookingTime = it.cookingTime,
                ingredients = it.ingredients,
                instructions = it.instructions,
                imageUrl = it.imageUrl
            )
        }.toTypedArray())

    override suspend fun deleteByTitle(titleToDelete: String) =
        recipeDao.deleteByTitle(titleToDelete)

    override fun getAll(): Flow<List<RecipeItem>> = recipeDao.getAll().transform { recipeList ->
        emit(
            recipeList.map {
                RecipeItem(
                    title = it.title,
                    cookingTime = it.cookingTime,
                    ingredients = it.ingredients,
                    instructions = it.instructions,
                    imageUrl = it.imageUrl,
                    isFavorite = true,
                )
            }
        )
    }

}