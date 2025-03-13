package com.example.airecipes.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.airecipes.data.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg recipe: RecipeEntity)

    @Query("DELETE FROM RECIPE WHERE TITLE = :titleToDelete")
    suspend fun deleteByTitle(titleToDelete: String)

    @Query("SELECT * FROM RECIPE")
    fun getAll(): Flow<List<RecipeEntity>>
}