package com.example.datingapp.presentation.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.datingapp.navigation.Screen
import com.example.datingapp.presentation.home.HomeViewModel
import com.example.datingapp.util.Extensions.fixName
import com.example.datingapp.util.Extensions.getTimeAgo

@Composable
fun ChatListScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel,
) {
    val userList by homeViewModel.userListState.collectAsStateWithLifecycle()
    val lastMessages = chatViewModel.lastMessage.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        homeViewModel.getUser()
        chatViewModel.getLastMessages()
    }

    Column {
        LazyColumn {
            items(userList.filter { it.uid != chatViewModel.userId }) { userInfo ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            chatViewModel.setUserInfo(userInfo)
                            navController.navigate(Screen.Chat.route)
                        },
                ) {
                    val thisIsLastMessage = lastMessages.value.firstOrNull {
                        if (it.to == chatViewModel.userId) userInfo.uid == it.from
                        else userInfo.uid == it.to
                    }

                    Row (verticalAlignment = Alignment.CenterVertically){
                        AsyncImage(
                            model = userInfo.picture.first(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxHeight()
                                .size(80.dp)
                                .padding(2.dp)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))

                        Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                            Text(text = userInfo.name.fixName(), fontSize = 28.sp)

                            if (thisIsLastMessage != null) {
                                Text(
                                    text = thisIsLastMessage.message,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    thisIsLastMessage?.timestamp?.toLong()?.getTimeAgo()?.let {
                        Text(
                            text = it,
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )
                    }
                }
            }
        }
    }
}