package com.example.airecipes.ui.screen

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
import androidx.compose.material.icons.filled.FavoriteBorder
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
import com.example.airecipes.R
import com.example.airecipes.ui.model.RecipeItem

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
                recipesList = state.favoritesList,
                onCardClick = state::onCardClick,
                onFavoriteClick = state::onFavoriteToggle,
                modifier = Modifier.fillMaxSize(),
            )
        }
        AnimatedVisibility(state.isQueryFilled) {
            RecipesList(
                title = "Suggested recipes",
                recipesList = state.searchResultsList,
                onCardClick = state::onCardClick,
                onFavoriteClick = state::onFavoriteToggle,
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
        visible = state.selectedItemTitle != null,
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
            item = state.selectedItem,
            onBackPressed = state::onClearSelectedItem,
            onFavoritePressed = state::onFavoriteToggle,
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
    onCardClick: (title: String) -> Unit,
    onFavoriteClick: (title: String) -> Unit,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(),
                style = LocalTextStyle.current.copy(
                    fontSize = 32.sp,
                    lineHeight = 38.sp,
                    fontWeight = FontWeight(700)
                )
            )
        }
        if (recipesList.isNotEmpty()) {
            items(items = recipesList, key = { it.title }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp)
                        .clickable {
                            onCardClick(it.title)
                        }
                        .animateItem(),
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
                            if (it.isFavorite) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clickable { onFavoriteClick(it.title) }
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(),
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