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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var queryValue by remember { mutableStateOf("") }
    val isQueryFilled by remember(queryValue) { derivedStateOf { queryValue.isNotBlank() } }
    var detailsScreenVisible by remember { mutableStateOf(false) }
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
                    onSearch = {},
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
                recipesList = listOf("Recipe 1", "Recipe 2", "Recipe 3", "Recipe 4"),
                onCardClick = { detailsScreenVisible = true },
                modifier = Modifier.fillMaxSize(),
            )
        }
        AnimatedVisibility(isQueryFilled) {
            RecipesList(
                title = "Suggested recipes",
                recipesList = listOf(
                    "Recipe 1",
                    "Fish and Chips classic",
                    "A recipe with a much longer title that should stretch across multiple rows",
                    "Last recipe",
                    "Last recipe2",
                    "Last recipe3",
                    "Last recipe4",
                    "Last recipe5"
                ),
                onCardClick = { detailsScreenVisible = true },
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
                            onClick = {},
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
        visible = detailsScreenVisible,
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
            modifier = modifier,
            onBackPressed = {
                detailsScreenVisible = false
            },
        )
    }
}

@Composable
fun RecipesList(
    title: String,
    recipesList: List<String>,
    onCardClick: () -> Unit,
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
        items(items = recipesList, key = { it }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .clickable {
                        onCardClick()
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
                        model = null,
                        contentDescription = null,
                        fallback = painterResource(R.drawable.ic_image_placeholder),
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
                            text = it,
                            style = LocalTextStyle.current.copy(
                                fontSize = 16.sp,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight(600)
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = it,
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
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen()
}