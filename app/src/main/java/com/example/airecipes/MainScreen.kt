package com.example.airecipes

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException

class MainScreenState(
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
    var selectedItemId by mutableStateOf<String?>(null)
    var resultsList by mutableStateOf<List<RecipeItem>>(emptyList())
    var isLoading by mutableStateOf(false)

    fun onQueryChange(newValue: String) {
        queryValue = newValue
    }

    fun onSearchPress() {
        coroutineScope.launch {
            isLoading = true
            resultsList = generateInitialListForInput(queryValue)
            isLoading = false
        }
    }

    fun onRegenerateListPress() {
        coroutineScope.launch {
            isLoading = true
            resultsList = generateDifferentListForInput(queryValue)
            isLoading = false
        }
    }

    fun onCardClick(itemId: String) {
        selectedItemId = itemId
    }

    fun onClearSelectedItem() {
        selectedItemId = null
    }

    private suspend fun generateInitialListForInput(input: String) =
        generateItemsForInput("Find 5 recipes that are described by this: $input. ")

    private suspend fun generateDifferentListForInput(input: String) =
        generateItemsForInput(
            "Find 5 recipes that are described by this: $input. " +
                    "They should all be different from the last set of 5 recipes you generated."
        )

    private suspend fun generateItemsForInput(input: String): List<RecipeItem> {
        val itemsList = mutableListOf<RecipeItem>()

        val prompt = input +
                "Use this schema: { \"id\": str, \"title\": str, \"cookingTime\": str, \"ingredients\": str, \"instructions\": str, \"imageUrl\": str}. " +
                "Generate a random UUID for each unique recipe. Get an image URL from the web for each recipe. " +
                "Ingredients should be each on their own line with bullet points." +
                "Always return a json array - no top level object!"

        generativeModel.generateContent(prompt).text?.let { responseText ->
            try {
                val responseArray = JSONArray(responseText)
                for (index in 0..<responseArray.length()) {
                    val item = responseArray.getJSONObject(index)
                    itemsList.add(
                        RecipeItem(
                            id = item.getString("id"),
                            title = item.getString("title"),
                            cookingTime = item.getString("cookingTime"),
                            ingredients = item.getString("ingredients"),
                            instructions = item.getString("instructions"),
                            imageUrl = item.getString("imageUrl"),
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
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) = remember {
    MainScreenState(coroutineScope)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    state: MainScreenState,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp)
    ) {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            inputField = {
                SearchBarDefaults.InputField(
                    query = state.queryValue,
                    onQueryChange = state::onQueryChange,
                    onSearch = {
                        state.onSearchPress()
                        focusManager.clearFocus()
                    },
                    expanded = false,
                    onExpandedChange = {},
                    placeholder = { Text("What do you feel like eating?") },
                    trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                )
            },
            expanded = false,
            onExpandedChange = {},
            content = {}
        )
        AnimatedVisibility(!state.isQueryFilled) {
            RecipesList(
                title = "Favorites",
                recipesList = emptyList(),
                onCardClick = state::onCardClick,
                modifier = Modifier.fillMaxSize(),
            )
        }
        AnimatedVisibility(state.isQueryFilled) {
            RecipesList(
                title = "Suggested recipes",
                recipesList = state.resultsList,
                onCardClick = state::onCardClick,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                endItem = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Button(
                            modifier = Modifier
                                .width(178.dp)
                                .height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            onClick = { state.onRegenerateListPress() },
                        ) {
                            Text(
                                text = "I don't like these",
                                style = LocalTextStyle.current.copy(
                                    fontSize = 16.sp,
                                    lineHeight = 16.sp,
                                    fontWeight = FontWeight(600)
                                ),
                            )
                        }
                    }
                }
            )
        }
    }
    AnimatedVisibility(
        visible = state.selectedItemId != null,
        enter = slideIn(
            animationSpec = spring(stiffness = Spring.StiffnessMedium),
            initialOffset = { fullSize -> IntOffset(fullSize.width, 0) },
        ),
        exit = slideOut(
            spring(stiffness = Spring.StiffnessMedium),
            targetOffset = { fullSize -> IntOffset(fullSize.width, 0) },
        ),
    ) {
        RecipeDetailsScreen(
            item = state.resultsList.firstOrNull { it.id == state.selectedItemId }
                ?: RecipeItem.empty(),
            onBackPressed = state::onClearSelectedItem,
            modifier = modifier,
        )
    }
    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun RecipesList(
    title: String,
    recipesList: List<RecipeItem>,
    onCardClick: (id: String) -> Unit,
    modifier: Modifier = Modifier,
    endItem: (@Composable () -> Unit)? = null,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(key = "List Title") {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                style = LocalTextStyle.current.copy(
                    fontSize = 32.sp,
                    lineHeight = 38.sp,
                    fontWeight = FontWeight(700)
                )
            )
        }
        if (recipesList.isNotEmpty()) {
            items(items = recipesList, key = { it.id }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp)
                        .clickable {
                            onCardClick(it.id)
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(
                            model = it.imageUrl,
                            contentDescription = null,
                            placeholder = painterResource(R.drawable.ic_image_placeholder),
                            error = painterResource(R.drawable.ic_image_placeholder),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(1f)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.Start,
                        ) {
                            Text(
                                text = it.title,
                                style = LocalTextStyle.current.copy(
                                    fontSize = 16.sp,
                                    lineHeight = 22.sp,
                                    fontWeight = FontWeight(600)
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = it.cookingTime,
                                style = LocalTextStyle.current.copy(
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    fontWeight = FontWeight(400)
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
            if (endItem != null) {
                item(key = "End Item") {
                    endItem.invoke()
                }
            }
        } else {
            item(key = "Empty State Text") {
                Text(
                    text = "No recipes in this list!",
                    modifier = Modifier.fillMaxWidth(),
                    style = LocalTextStyle.current.copy(
                        fontSize = 26.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight(500)
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen(
        state = rememberMainScreenState()
    )
}