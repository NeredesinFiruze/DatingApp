package com.example.datingapp.presentation.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.datingapp.presentation.home.HomeViewModel

@Composable
fun ChatListScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel,
) {
    val userList by homeViewModel.userListState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit){
        homeViewModel.getUser()
    }

    Column {
        LazyColumn{
            items(userList.filter { it.uid != chatViewModel.userId }){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 16.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable {
                            chatViewModel.setUserInfo(it)
                            navController.navigate("chat")
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = it.picture.first(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(2.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = it.name, fontSize = 24.sp)
                }
            }
        }
    }
}