package com.example.datingapp.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.datingapp.ui.theme.Brush1
import com.example.datingapp.ui.theme.ContentWhite
import com.example.datingapp.ui.theme.TransparentBrush

@Composable
fun Block(
    modifier: Modifier = Modifier,
    enable: Boolean = false,
    shape: Shape = RoundedCornerShape(8.dp),
    aspectRatio: Float = 1f,
    onClick: ()-> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(aspectRatio)
            .padding(4.dp)
            .border(2.dp, if (enable) Brush1 else TransparentBrush, shape)
            .clip(shape)
            .then(modifier)
            .clickable { onClick() },
        backgroundColor = ContentWhite,
        contentColor = Color.Red,
        shape = shape,
        content = content
    )
}