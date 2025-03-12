package com.example.airecipes

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerationConfig
import kotlinx.coroutines.launch
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var queryValue by remember { mutableStateOf("") }
    val isQueryFilled by remember(queryValue) { derivedStateOf { queryValue.isNotBlank() } }
    var selectedItemId by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var resultsList by remember { mutableStateOf<List<RecipeItem>>(emptyList()) }
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
                    query = queryValue,
                    onQueryChange = { newValue -> queryValue = newValue },
                    onSearch = {
                        coroutineScope.launch {
                            resultsList = generateItemsForInput(queryValue)
                        }
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
        AnimatedVisibility(!isQueryFilled) {
            RecipesList(
                title = "Favorites",
                recipesList = emptyList(),
                onCardClick = { selectedId ->
                    selectedItemId = selectedId
                },
                modifier = Modifier.fillMaxSize(),
            )
        }
        AnimatedVisibility(isQueryFilled) {
            RecipesList(
                title = "Suggested recipes",
                recipesList = resultsList,
                onCardClick = { selectedId ->
                    selectedItemId = selectedId
                },
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
                            onClick = {
                                coroutineScope.launch {
                                    resultsList = generateItemsForInput(queryValue)
                                }
                            },
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
        visible = selectedItemId != null,
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
            item = resultsList.firstOrNull { it.id == selectedItemId } ?: RecipeItem.empty(),
            onBackPressed = {
                selectedItemId = null
            },
            modifier = modifier,
        )
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
        if (endItem != null) {
            item(key = "End Item") {
                endItem.invoke()
            }
        }
    }
}

private suspend fun generateItemsForInput(input: String): List<RecipeItem> {
    val itemsList = mutableListOf<RecipeItem>()

    val configs = GenerationConfig.builder()
    configs.responseMimeType = "application/json"
    val generativeModel =
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.apiKey,
            generationConfig = configs.build()
        )

    val prompt =
        "Find 5 recipes that are described by this: $input. Use this schema: { \"id\": str, \"title\": str, \"cookingTime\": str, \"ingredients\": str, \"instructions\": str, \"imageUrl\": str}. Generate a random UUID for each unique recipe. Get an image URL from the web for each recipe. Ingredients should be each on their own line with bullet points."
    generativeModel.generateContent(prompt).text?.let { responseText ->
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
    }

    return itemsList
}

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

@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen()
}