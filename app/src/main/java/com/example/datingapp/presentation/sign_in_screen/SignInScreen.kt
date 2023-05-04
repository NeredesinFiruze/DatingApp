package com.example.datingapp.presentation.sign_in_screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.datingapp.R
import com.example.datingapp.navigation.Screen
import com.example.datingapp.presentation.sign_in_screen.sign_in_with_google.SignInState
import com.example.datingapp.ui.theme.Brush2
import com.example.datingapp.ui.theme.ContentWhite

@Composable
fun SignInScreen(
    state: SignInState,
    navController: NavController,
    onSignInClick: () -> Unit,
) {
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp

    LaunchedEffect(key1 = state.signInErrorMessage) {
        state.signInErrorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush2)
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column {
            RowButton(
                text = "Google",
                width = (screenWidth / 1.5).dp,
                drawable = R.drawable.google_icon,
                onClick = {onSignInClick()}
            )
            Spacer(modifier = Modifier.height(16.dp))
            RowButton(
                text = "Sign In with Phone",
                width = (screenWidth / 1.5).dp,
                icon = Icons.Default.Email,
                onClick = {navController.navigate(Screen.SignInPhone.route)}
            )
        }
    }
}

@Composable
fun RowButton(
    text: String,
    width: Dp,
    icon: ImageVector? = null,
    drawable: Int? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .width(width)
            .height(60.dp)
            .clip(CircleShape)
            .background(ContentWhite)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .padding(start = 8.dp)
            )
        }
        drawable?.let {
            Image(
                painter = painterResource(id = drawable),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .padding(start = 8.dp)
            )
        }
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            textAlign = Center,
            fontSize = 21.sp,
        )
    }
}