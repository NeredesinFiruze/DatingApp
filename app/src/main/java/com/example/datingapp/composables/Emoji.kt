package com.example.datingapp.composables

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView

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