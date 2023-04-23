package com.example.datingapp.composables

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.datingapp.ui.theme.Brush1
import com.example.datingapp.ui.theme.ContentWhite
import com.example.datingapp.ui.theme.TransparentBrush


@Composable
fun Block(
    modifier: Modifier = Modifier,
    enable: Boolean = false,
    shape: Shape = RoundedCornerShape(8.dp),
    onClick: ()-> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .clip(shape)
            .padding(4.dp)
            .border(2.dp, if (enable) Brush1 else TransparentBrush, shape)
            .aspectRatio(1f)
            .clickable { onClick() }
            .then(modifier),
        contentColor = Color.Red,
        backgroundColor = ContentWhite,
        content = content,
        elevation = 1.dp,
        shape = shape
    )
}

@Composable
fun EmojiText(
    text: String,
    size: Float = 18F
) {
    AndroidView(
        factory = { context ->
            AppCompatTextView(context).apply {
                setTextColor(Color.Black.toArgb())
                this.text = text
                textSize = size
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
        },
    )
}