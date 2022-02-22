package com.erm.composegames.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.erm.composegames.ui.Screens

@Preview
@Composable
fun HomeScreen(modifier: Modifier = Modifier, screenSelected: ((Screens) -> Unit)? = null) {
    Column {
        Screens.games.forEach { screen ->
            Row(
                modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colors.surface)
                    .clickable(onClick = { screenSelected?.invoke(screen) })
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = screen.icon, contentDescription = "Play ${screen.name}")
                Text(modifier = Modifier.padding(8.dp), text = screen.name)
            }
        }
    }
}