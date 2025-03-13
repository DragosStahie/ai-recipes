package com.example.airecipes.ui.screen

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.airecipes.BuildConfig
import com.example.airecipes.data.repository.RecipeRepository
import com.example.airecipes.ui.model.RecipeItem
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.koin.compose.koinInject


class MainScreenState(
    private val recipeRepository: RecipeRepository,
    private val coroutineScope: CoroutineScope
) {
    private val configs = GenerationConfig.builder().apply { responseMimeType = "application/json" }
    private val safetySettings = listOf(
        SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.LOW_AND_ABOVE),
        SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.LOW_AND_ABOVE),
        SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.LOW_AND_ABOVE),
        SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.LOW_AND_ABOVE),
    )

    private val generativeModel =
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.apiKey,
            generationConfig = configs.build(),
            safetySettings = safetySettings,
        )

    var queryValue by mutableStateOf("")
    val isQueryFilled by derivedStateOf { queryValue.isNotBlank() }
    var selectedItemTitle by mutableStateOf<String?>(null)
    val selectedItem by derivedStateOf {
        val activeList = when {
            queryValue.isBlank() -> favoritesList
            else -> searchResultsList
        }

        return@derivedStateOf activeList.firstOrNull { it.title == selectedItemTitle }
            ?: RecipeItem.empty()
    }
    var searchResultsList by mutableStateOf<List<RecipeItem>>(emptyList())
    var favoritesList by mutableStateOf<List<RecipeItem>>(emptyList())
    var isLoading by mutableStateOf(false)

    init {
        coroutineScope.launch {
            recipeRepository.getAll().collect { favoriteRecipes ->
                favoritesList = favoriteRecipes
                searchResultsList =
                    searchResultsList.map { it.copy(isFavorite = isItemFavorite(it.title)) }
            }
        }
    }

    fun onQueryChange(newValue: String) {
        queryValue = newValue
    }

    fun onSearchPress() {
        coroutineScope.launch {
            isLoading = true
            searchResultsList = generateInitialListForInput(queryValue)
            isLoading = false
        }
    }

    fun onRegenerateListPress() {
        coroutineScope.launch {
            isLoading = true
            searchResultsList = generateDifferentListForInput(queryValue)
            isLoading = false
        }
    }

    fun onCardClick(itemTitle: String) {
        selectedItemTitle = itemTitle
    }

    fun onClearSelectedItem() {
        selectedItemTitle = null
    }

    fun onFavoriteToggle(itemTitle: String) {
        coroutineScope.launch {
            if (isItemFavorite(itemTitle)) {
                recipeRepository.deleteByTitle(itemTitle)
            } else {
                recipeRepository.insert(
                    searchResultsList.firstOrNull { it.title == itemTitle } ?: return@launch
                )
            }
        }
    }

    private fun isItemFavorite(itemTitle: String) =
        favoritesList.firstOrNull { it.title == itemTitle } != null

    private suspend fun generateInitialListForInput(input: String) =
        generateItemsForInput("Find 5 recipes that are described by this: $input. ")

    private suspend fun generateDifferentListForInput(input: String) =
        generateItemsForInput(
            "Find 5 recipes that are described by this: $input. " +
                    "They should be completely different recipes compared to the last time."
        )

    private suspend fun generateItemsForInput(input: String): List<RecipeItem> {
        val itemsList = mutableListOf<RecipeItem>()

        val prompt = input +
                "Use this schema: { \"id\": str, \"title\": str, \"cookingTime\": str, \"ingredients\": str, \"instructions\": str, \"imageUrl\": str}." +
                "Each recipe must have an unique name." +
                "Get an image URL from the web for each recipe." +
                "Ingredients should be each on their own line with bullet points." +
                "Always return a json array - no top level object!"

        generativeModel.generateContent(prompt).text?.let { responseText ->
            try {
                val responseArray = JSONArray(responseText)
                for (index in 0..<responseArray.length()) {
                    val item = responseArray.getJSONObject(index)
                    val itemId = item.getString("id")
                    itemsList.add(
                        RecipeItem(
                            title = item.getString("title"),
                            cookingTime = item.getString("cookingTime"),
                            ingredients = item.getString("ingredients"),
                            instructions = item.getString("instructions"),
                            imageUrl = item.getString("imageUrl"),
                            isFavorite = isItemFavorite(itemId)
                        )
                    )
                }
            } catch (e: JSONException) {
                Log.e("JSON_PARSING", e.toString())
            }
        }

        return itemsList
    }
}

@Composable
fun rememberMainScreenState(
    recipeRepository: RecipeRepository = koinInject(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) = remember {
    MainScreenState(
        recipeRepository = recipeRepository,
        coroutineScope = coroutineScope
    )
}