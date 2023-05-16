package com.example.datingapp.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.datingapp.composables.BackButton
import com.example.datingapp.composables.rememberImeState
import com.example.datingapp.ui.theme.ChatColor1
import com.example.datingapp.ui.theme.ChatColor2
import com.example.datingapp.ui.theme.GrayNormal
import com.example.datingapp.ui.theme.PinkColor
import com.example.datingapp.util.Extensions.fixName

@Composable
fun ChatScreen(
    navController: NavController,
    chatViewModel: ChatViewModel,
) {
    val scrollState = rememberScrollState()
    val lazyScrollState = rememberLazyListState()
    val imeState = rememberImeState()
    val focusState = LocalFocusManager.current
    val messages by chatViewModel.messages.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = imeState.value) {
        if (imeState.value) {
            scrollState.scrollTo(Int.MAX_VALUE)
        }
    }

    LaunchedEffect(Unit) {
        chatViewModel.getMessages()
    }

    ConstraintLayout(Modifier.fillMaxSize()) {

        val (topBar, chat, messageBar) = createRefs()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(chat) {
                    // top.linkTo(topBar.bottom)
                    bottom.linkTo(messageBar.top)
                },
            contentPadding = PaddingValues(16.dp),
            state = lazyScrollState
        ) {
            items(messages) {
                MessageItem(chatViewModel = chatViewModel, messageData = it)
            }
        }

        ChatTopInfo(
            modifier = Modifier
                .constrainAs(topBar) {
                    top.linkTo(parent.top)
                },
            chatViewModel = chatViewModel
        ) {
            if (imeState.value) focusState.clearFocus()
            else navController.popBackStack()
        }

        WriteMessage(
            modifier = Modifier
                .padding(start = 4.dp, bottom = 4.dp, top = 2.dp, end = 4.dp)
                .constrainAs(messageBar) {
                    bottom.linkTo(parent.bottom)
                }
                .verticalScroll(scrollState),
            onSend = {
                chatViewModel.sendMessage(it)
            }
        )
    }
}

@Composable
fun MessageItem(
    chatViewModel: ChatViewModel,
    messageData: MessageData,
) {
    val localWidth = LocalConfiguration.current.screenWidthDp.dp
    val isFromMe = messageData.from != chatViewModel.userInfo.value.uid
    val messageShape = if (isFromMe) RoundedCornerShape(
        topStart = 8.dp,
        bottomStart = 8.dp,
        bottomEnd = 8.dp
    )
    else RoundedCornerShape(
        topEnd = 8.dp,
        bottomStart = 8.dp,
        bottomEnd = 8.dp
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 2.dp),
        horizontalAlignment = if (isFromMe) Alignment.End else Alignment.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = localWidth * 9 / 12)
                .clip(messageShape)
                .background(if (isFromMe) ChatColor1 else ChatColor2),
        ) {
            Text(
                text = messageData.message,
                modifier = Modifier.padding(8.dp),
                textAlign = if (isFromMe) TextAlign.End else TextAlign.Start
            )
        }
    }
}

@Composable
fun ChatTopInfo(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel,
    onBackClick: () -> Unit,
) {
    val userInfo = chatViewModel.userInfo.value
    Row(
        Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color(0xFFF0CFCF))
            .then(modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton { onBackClick() }
            AsyncImage(
                model = userInfo.picture[0],
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
                    .size(50.dp)
                    .aspectRatio(1f)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            text = userInfo.name.fixName(),
            fontSize = 21.sp,
            modifier = Modifier.padding(end = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteMessage(
    modifier: Modifier = Modifier,
    onSend: (String) -> Unit,
) {
    val text = remember { mutableStateOf("") }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextField(
            value = text.value,
            onValueChange = { text.value = it },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).padding(end = 4.dp),
            placeholder = { Text(text = "message", color = GrayNormal) },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = PinkColor,
                containerColor = PinkColor.copy(alpha = .2f)
            )
        )

        IconButton(
            onClick = {
                if (text.value.isNotEmpty()) onSend(text.value)
                text.value = ""
            },
            modifier = Modifier
                .clip(CircleShape)
                .background(PinkColor.copy(alpha = .6f))
                .padding(2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}