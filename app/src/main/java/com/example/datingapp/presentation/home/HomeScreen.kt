package com.example.datingapp.presentation.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.datingapp.R
import com.example.datingapp.data.local.UserInfo
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        TopSection(navController)
        SearchSection()
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
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state: List<UserInfo> by viewModel.userListState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    var turn by remember { mutableStateOf(0) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val layoutDirection = LocalLayoutDirection.current
    val directionFactor = if (layoutDirection == LayoutDirection.Rtl) -1 else 1
    val sizePx = with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val dragSizePx = sizePx * 1.5f

    if (state.isNotEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(
                        x = (offsetX.value).roundToInt(),
                        y = (offsetY.value).roundToInt()
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = {
                            scope.launch {
                                if (abs(offsetX.value) > 400) {
                                    if ( state[turn].picture.size == turn) turn = 0
                                    else turn++
                                    println("turn is $turn")
                                }

                                offsetX.snapTo(0f)
                                offsetY.snapTo(0f)
                            }
                            isDragging = false
                        },
                        onDragCancel = {
                            scope.launch {
                                if (offsetX.value > 400) turn++
                                if (offsetX.value < -400) turn++
                                offsetX.snapTo(0f)
                                offsetY.snapTo(0f)
                            }
                            isDragging = false
                        },
                        onDrag = { change, dragAmount ->
                            scope.launch {
                                val newOffsetX = offsetX.value + dragAmount.x * directionFactor
                                val newOffsetY = offsetY.value + dragAmount.y
                                change.consume()
                                offset += dragAmount
                                if (newOffsetX + newOffsetY < dragSizePx) {
                                    offsetX.snapTo(newOffsetX)
                                    offsetY.snapTo(newOffsetY)
                                } else if (offsetX.value + newOffsetY < dragSizePx) {
                                    offsetY.snapTo(newOffsetY)
                                } else if (newOffsetX + offsetY.value < dragSizePx) {
                                    offsetX.snapTo(newOffsetX)
                                }
                            }
                        }
                    )
                }
                .graphicsLayer {
                    transformOrigin = if (offsetX.value > 0) TransformOrigin(1f, 1f)
                    else TransformOrigin(0f, 1f)

                    scaleX = if (isDragging) scaleX * .9f else scaleX
                    scaleY = if (isDragging) scaleY * .9f else scaleY
                    println(offsetX.value)
                    alpha = 1 - abs(offsetX.value / 3000)
                    rotationZ = if (offsetX.value > 100) {
                        -(100 - abs(offsetX.value)) / 15
                    } else if (offsetX.value < -100) {
                        (100 - abs(offsetX.value)) / 15
                    } else 0f
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
            ) { page ->
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
                            append(state[turn].name + " ")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontSize = 31.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White
                            )
                        ) {
                            append(state[turn].birthDate)
                        }
                    }
                    Text(text = text)
                }
                /*TODO("eksik")*/
            }
        }
    }
}