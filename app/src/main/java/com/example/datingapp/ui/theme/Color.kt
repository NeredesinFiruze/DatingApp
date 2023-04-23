package com.example.datingapp.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val GrayLight =  Color(0xFFD4D7DE)
val GrayNormal =  Color(0xFF7D8592)
val ContentWhite = Color(0xFFF1F2F4)
val TextColor = Color(0xFF21252E)
val PinkColor = Color(0xFFFC2E79)

val Brush1 = Brush.horizontalGradient(
    colors = listOf(
        PinkColor,
        Color(0xFFFF7654)
    )
)

val TransparentBrush = Brush.horizontalGradient(
    colors = listOf(
        Color.Transparent,
        Color.Transparent
    )
)