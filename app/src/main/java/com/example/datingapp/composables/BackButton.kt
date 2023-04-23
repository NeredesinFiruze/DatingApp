package com.example.datingapp.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    page: Int? = null,
    onClick: ()-> Unit,
) {
    IconButton(
        onClick = { onClick() },
        modifier = modifier
    ) {
        Icon(
            imageVector = if (page == 1)Icons.Rounded.Close else Icons.Rounded.KeyboardArrowLeft,
            contentDescription = null,
            modifier = Modifier
                .padding(16.dp)
        )
    }
}