package com.example.datingapp.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.datingapp.ui.theme.GrayNormal
import com.example.datingapp.ui.theme.PinkColor

@Composable
fun CustomDialog(
    title: String,
    onDismiss: () -> Unit,
    onAccept: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .height(140.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .width(80.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(GrayNormal)
                    ) {
                        Text(
                            text = "No",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDismiss() },
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .width(80.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(PinkColor)
                    ) {
                        Text(
                            text = "Yes",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAccept() },
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun Sd() {
    CustomDialog(title = "new stuff", onDismiss = { /*TODO*/ }) {
        
    }
}