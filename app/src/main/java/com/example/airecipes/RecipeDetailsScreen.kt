package com.example.airecipes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@Composable
fun RecipeDetailsScreen(
    item: RecipeItem,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
    ) {
        Box {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                placeholder = painterResource(R.drawable.ic_image_placeholder),
                error = painterResource(R.drawable.ic_image_placeholder),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { onBackPressed() }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = item.title,
                    style = LocalTextStyle.current.copy(
                        fontSize = 24.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight(600)
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.cookingTime,
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
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = "Ingredients:",
                style = LocalTextStyle.current.copy(
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight(600)
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = item.ingredients,
                style = LocalTextStyle.current.copy(
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight(400)
                ),
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = "Instructions:",
                style = LocalTextStyle.current.copy(
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight(600)
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = item.instructions,
                style = LocalTextStyle.current.copy(
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight(400)
                ),
            )
        }
    }
}

@Preview
@Composable
private fun RecipeDetailsScreenPreview() {
    RecipeDetailsScreen(
        item = RecipeItem(
            id = "",
            title = "Mashed Potatoes",
            cookingTime = "20 min",
            ingredients = "5 pounds potatoes\\n\" +\n" +
                    "\"2 large cloves garlic, minced\\n\" +\n" +
                    "\"fine sea salt\\n\" +\n" +
                    "\"6 tablespoons butter\\n\" +\n" +
                    "\"1 cup whole milk\\n\" +\n" +
                    "\"4 ounces cream cheese, room temperature\\n\" +\n" +
                    "\"toppings: chopped fresh chives or green onions, freshly-cracked black pepper",
            instructions = "\"Cut the potatoes. Again, feel free to peel your potatoes or leave the skins on. (I always leave them on for the extra nutrients and flavor.)  Then cut your potatoes into evenly-sized chunks, about an inch or so thick. Then transfer them to a large stockpot full of cold water until all of the potatoes are cut and ready to go.\\n\" +\n" +
                    "\"Boil the potatoes. Once all of your potatoes are cut, be sure that there is enough cold water in the pan so that the water line sits about 1 inch above the potatoes. Add the garlic and 1 tablespoon salt to the water. Then turn on high heat until the water comes to a boil. And boil the potatoes for about 10-12 minutes until a knife inserted in the middle of a potato goes in with almost no resistance. Carefully drain out all of the water.\\n\" +\n" +
                    "\"Prepare your melted butter mixture. Meanwhile, as the potatoes are boiling, heat your butter, milk and an additional 2 teaspoons of sea salt together either in a small saucepan or in the microwave until the butter is just melted. (You want to avoid boiling the milk.)  Set aside until ready to use.\\n\" +\n" +
                    "\"Pan-dry the potatoes. Return the potatoes to the hot stockpot, and then place the stockpot back on the hot burner, turning the heat down to low. Using two oven mitts, carefully hold the handles on the stockpot and shake it gently on the burner for about 1 minute to help cook off some of the remaining steam within the potatoes. Then remove the stockpot entirely from the heat.\\n\" +\n" +
                    "\"Mash the potatoes.  Using your preferred kind of masher (see above), mash the potatoes to your desired consistency.\\n\" +\n" +
                    "\"Stir everything together. Then pour half of the melted butter mixture over the potatoes, and fold it in with a wooden spoon or spatula until potatoes have soaked up the liquid. Repeat with the remaining butter. And then again with the cream cheese. Fold each addition in until just combined to avoid overmixing, or else you will end up with gummy potatoes.\\n\" +\n" +
                    "\"Taste and season. One final time, adding in extra salt (plus black pepper, if you would like) to taste.\\n\" +\n" +
                    "\"Serve warm. Then serve warm, garnished with any extra toppings that you might like, and enjoy!! ♡\"",
            imageUrl = ""
        ),
        onBackPressed = {}
    )
}