package com.example.datingapp.presentation.home

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.datingapp.R
import com.example.datingapp.data.local.ConnectionInfo
import com.example.datingapp.data.local.UserInfo
import com.example.datingapp.ui.theme.MaskBrush
import com.example.datingapp.util.Extensions.animateSwipe
import com.example.datingapp.util.Extensions.calculateAge
import com.example.datingapp.util.Extensions.calculateDate
import com.example.datingapp.util.Extensions.fixName
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController, context: Context) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        TopSection(navController)
        SearchSection(context)
    }
}

@Composable
fun TopSection(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(id = R.drawable.google_icon),
            contentDescription = null,
            modifier = Modifier
                .size(42.dp)
                .padding(8.dp)
        )
        Icon(
            imageVector = Icons.Rounded.Tune,
            contentDescription = null,
            modifier = Modifier
                .size(42.dp)
                .padding(8.dp)
                .clickable {
                    navController.navigate("filter")
                }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchSection(
    context: Context,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state: List<UserInfo> by viewModel.userListState.collectAsStateWithLifecycle()
    val stateConnectionInfo: List<ConnectionInfo> by viewModel.userConnectionStatus.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState()
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var turn by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.completeSignIn(true)
        viewModel.getUser()
    }

    if (state.isNotEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .animateSwipe(state.size) {turnValue, newOffset->
                    println(turnValue)
                    turn = turnValue
                    scope.launch {
                        offsetX.snapTo(newOffset)
                    }
                }
        ) {
            HorizontalPager(
                pageCount = state[turn].picture.size,
                state = pagerState,
                userScrollEnabled = false,
                beyondBoundsPageCount = 2,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = MaskBrush,
                            size = Size(size.width, size.height)
                        )
                    }
            ) { page ->
                Box(Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = state[turn].picture[page],
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    if (it.x > size.width / 2) {
                                        scope.launch {
                                            pagerState.animateScrollToPage(
                                                page = page + 1,
                                                animationSpec = tween(500)
                                            )
                                        }
                                    } else {
                                        scope.launch {
                                            pagerState.animateScrollToPage(
                                                page = if (page != 0) page - 1 else return@launch,
                                                animationSpec = tween(500)
                                            )
                                        }
                                    }
                                }
                            }
                    )
                    Image(
                        painter = painterResource(id = R.drawable.nope),
                        contentDescription = null,
                        modifier = Modifier
                            .alpha(-offsetX.value / 300)
                            .rotate(45f)
                            .size(180.dp)
                            .align(Alignment.TopEnd)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.like),
                        contentDescription = null,
                        modifier = Modifier
                            .alpha(offsetX.value / 300)
                            .rotate(-45f)
                            .size(180.dp)
                            .align(Alignment.TopStart)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomStart)
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    val text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                        ) {
                            append(state[turn].name.fixName() + " ")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontSize = 31.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White
                            )
                        ) {
                            append(state[turn].birthDate.calculateAge())
                        }
                    }
                    Text(text = text)
                }
                if (stateConnectionInfo[turn].connectionStatus) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(10.dp)) {
                            drawCircle(Color.Green)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Online now",
                            color = Color.White
                        )
                    }
                } else {
                    stateConnectionInfo[turn].lastConnected?.let {
                        it.toLong().calculateDate()?.let { date ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Canvas(modifier = Modifier.size(10.dp)) {
                                    drawCircle(Color.Gray)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = date,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
                Row {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = viewModel.getCityName(turn,context),
                        color = Color.White
                    )
                }
            }
        }
    }
}