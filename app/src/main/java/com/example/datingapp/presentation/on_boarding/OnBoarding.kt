@file:Suppress("KotlinConstantConditions")

package com.example.datingapp.presentation.on_boarding

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceAround
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.datingapp.R
import com.example.datingapp.composables.*
import com.example.datingapp.data.local.Gender
import com.example.datingapp.data.local.listOfRelationType
import com.example.datingapp.ui.theme.*
import com.example.datingapp.composables.CameraView
import com.example.datingapp.composables.BackButton
import com.example.datingapp.composables.Block
import com.example.datingapp.composables.CustomButton
import com.example.datingapp.composables.DateTextField
import com.example.datingapp.composables.EmojiText
import com.example.datingapp.composables.NameTextField
import com.example.datingapp.composables.rememberImeState
import com.example.datingapp.ui.theme.GrayLight
import com.example.datingapp.ui.theme.GrayNormal
import com.example.datingapp.ui.theme.PinkColor
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executors

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoarding(navController: NavController, context: Context) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val pageCount = 6
    val value by animateFloatAsState(
        targetValue = pagerState.currentPage.toFloat() / 5f,
        animationSpec = tween(300)
    )
    Box(
        modifier = Modifier
            .height(8.dp)
            .fillMaxWidth(value)
            .background(PinkColor)
    )
    HorizontalPager(
        pageCount = pageCount,
        state = pagerState,
        userScrollEnabled = false,
        modifier = Modifier
            .fillMaxSize()
    ) { page ->
        when (page) {
            5 -> {
                FirstPage(navController) {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = page + 1,
                            animationSpec = tween(500)
                        )
                    }
                }
            }

            1 -> {
                SecondPage {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = if (it) page + 1 else page - 1,
                            animationSpec = tween(500)
                        )
                    }
                }
            }

            2 -> {
                ThirdPage {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = if (it) page + 1 else page - 1,
                            animationSpec = tween(500)
                        )
                    }
                }
            }

            3 -> {
                FourthPage {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = if (it) page + 1 else page - 1,
                            animationSpec = tween(500)
                        )
                    }
                }
            }

            4 -> {
                FifthPage {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = if (it) page + 1 else page - 1,
                            animationSpec = tween(500)
                        )
                    }
                }
            }

            0 -> {
                SixthPage(navController, context = context) {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = page - 1,
                            animationSpec = tween(500)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FirstPage(
    navController: NavController,
    viewModel: OnBoardingViewModel = hiltViewModel(),
    pageClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val focusState = LocalFocusManager.current

    LaunchedEffect(key1 = imeState.value) {
        if (imeState.value) {
            scrollState.scrollTo(Int.MAX_VALUE)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Column {
            BackButton(page = 1) {
                if (imeState.value) focusState.clearFocus()
                else navController.navigate("sign-in")
            }
            Spacer(modifier = Modifier.height(80.dp))
            Text(
                text = "Your Name",
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            NameTextField { pageClick() }
            Text(
                text = "Uygulama'da bu adla görünecek ve bunu değiştiremeyeceksin",
                color = Color.LightGray
            )
        }
        val condition = viewModel.userInfo.value.name.isNotEmpty()
        CustomButton(
            text = "NEXT",
            modifier = Modifier
                .align(BottomCenter),
            enabled = condition,
            onClick = {
                if (condition) {
                    focusState.clearFocus()
                    pageClick()
                }
            }
        )
    }
}

@Composable
fun SecondPage(
    viewModel: OnBoardingViewModel = hiltViewModel(),
    pageClick: (Boolean) -> Unit,
) {
    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val focusState = LocalFocusManager.current

    LaunchedEffect(key1 = imeState.value) {
        if (imeState.value) {
            scrollState.scrollTo(Int.MAX_VALUE)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Column {
            BackButton {
                if (imeState.value) focusState.clearFocus()
                else pageClick(false)
            }
            Spacer(modifier = Modifier.height(60.dp))
            Text(
                text = "Your Birthdate",
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(60.dp))
            DateTextField()
            Text(
                text = "Everyone can see your age",
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth(),
                color = GrayLight
            )
        }

        val condition = viewModel.userInfo.value.birthDate.lastOrNull() != 'Y' &&
                viewModel.userInfo.value.birthDate.lastOrNull() != null

        CustomButton(
            text = "NEXT",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(BottomCenter),
            enabled = condition,
            onClick = {
                if (condition) {
                    focusState.clearFocus()
                    pageClick(true)
                }
            }
        )
    }
}

@Composable
fun ThirdPage(
    viewModel: OnBoardingViewModel = hiltViewModel(),
    pageClick: (Boolean) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Column {
            BackButton { pageClick(false) }
            Spacer(modifier = Modifier.height(60.dp))
            Text(
                text = "Your Gender",
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(60.dp))

            Gender.values().forEach {
                if (it != Gender.NONE) {
                    CustomButton(
                        text = it.name,
                        enabled = it == viewModel.userInfo.value.gender,
                        onClick = {
                            viewModel.yourGender(it)
                        }
                    )
                }
            }
        }
        val condition = viewModel.userInfo.value.gender != Gender.NONE
        CustomButton(
            text = "NEXT",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(BottomCenter),
            enabled = condition,
            onClick = { if (condition) pageClick(true) }
        )
    }
}

@Composable
fun FourthPage(
    viewModel: OnBoardingViewModel = hiltViewModel(),
    pageClick: (Boolean) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Column {
            BackButton { pageClick(false) }
            Spacer(modifier = Modifier.height(60.dp))
            Text(
                text = "Show Me",
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(60.dp))

            Gender.values().forEach {
                if (it != Gender.NONE) {
                    CustomButton(
                        text = it.name,
                        enabled = viewModel.userInfo.value.interestedGender.contains(it)
                    ) {
                        viewModel.showMe(it)
                    }
                }
            }
        }
        val condition = viewModel.userInfo.value.interestedGender.isNotEmpty()
        CustomButton(
            text = "NEXT",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(BottomCenter),
            enabled = condition,
            onClick = { if (condition) pageClick(true) },
        )
    }
}

@Composable
fun FifthPage(
    viewModel: OnBoardingViewModel = hiltViewModel(),
    pageClick: (Boolean) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Center
    ) {
        BackButton(modifier = Modifier.align(TopStart)) { pageClick(false) }
        Column {
            Row {
                listOfRelationType.forEachIndexed { index, it ->
                    if (index <= 2)
                        Block(
                            enable = viewModel.userInfo.value.relationType.contains(it),
                            onClick = {
                                viewModel.chooseRelationType(it)
                            },
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Column(
                                verticalArrangement = SpaceAround,
                                horizontalAlignment = CenterHorizontally
                            ) {
                                EmojiText(text = it.emoji)
                                Text(
                                    text = it.desc,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    color = GrayNormal
                                )
                            }
                        }
                }
            }
            Row {
                listOfRelationType.forEachIndexed { index, it ->
                    if (index > 2)
                        Block(
                            enable = viewModel.userInfo.value.relationType.contains(it),
                            onClick = {
                                viewModel.chooseRelationType(it)
                            },
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Column(
                                verticalArrangement = SpaceAround,
                                horizontalAlignment = CenterHorizontally
                            ) {
                                EmojiText(text = it.emoji)
                                Text(
                                    text = it.desc,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    color = GrayNormal
                                )
                            }
                        }
                }
            }
        }
        CustomButton(
            text = "NEXT",
            enabled = viewModel.userInfo.value.relationType.isNotEmpty(),
            modifier = Modifier
                .align(BottomCenter),
            onClick = { pageClick(true) }
        )
    }
}

@Composable
fun SixthPage(
    navController: NavController,
    viewModel: OnBoardingViewModel = hiltViewModel(),
    context: Context,
    pageClick: () -> Unit,
) {
    val cameraExecutor = Executors.newSingleThreadExecutor()!!
    val shouldShowCamera = remember { mutableStateOf(false) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                shouldShowCamera.value = true
            }
        }
    )
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Center
    ) {





        Icons.Default.AddAPhoto
        Icons.Default.AddPhotoAlternate





        BackButton(Modifier.align(TopStart)) { pageClick() }
        Column(Modifier.padding(horizontal = 8.dp)) {
            Row(Modifier.fillMaxWidth()) {
                viewModel.userInfo.value.picture.forEachIndexed { index, uri ->
                    if (index <= 2)
                        Block(
                            onClick = {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                viewModel.addImage(index = index)
                            },
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            if (uri == null) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = null,
                                        tint = Color.Red
                                    )
                                }
                            } else {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(model = uri),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    IconButton(
                                        onClick = {
                                            viewModel.removeImage(index)
                                        },
                                        modifier = Modifier
                                            .align(TopEnd)
                                            .clip(CircleShape)
                                            .size(34.dp)
                                            .padding(4.dp)
                                            .background(Color.Red)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Remove,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                }
            }
            Row(Modifier.fillMaxWidth()) {
                viewModel.userInfo.value.picture.forEachIndexed { index, uri ->
                    if (index > 2)
                        Block(
                            onClick = {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                viewModel.addImage(index = index)
                            },
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            if (uri == null) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = null,
                                        tint = Color.Red
                                    )
                                }
                            } else {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(model = uri),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    IconButton(
                                        onClick = {
                                            viewModel.removeImage(index)
                                        },
                                        modifier = Modifier
                                            .align(TopEnd)
                                            .clip(CircleShape)
                                            .size(34.dp)
                                            .padding(4.dp)
                                            .background(Color.Red)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Remove,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                }
            }
        }

        val condition = viewModel.userInfo.value.picture.count { it != null } > 1
        CustomButton(
            text = "COMPLETE",
            onClick = { if (condition) viewModel.saveUserToFirebase(navController) },
            enabled = condition,
            modifier = Modifier.align(BottomCenter)
        )

        if (shouldShowCamera.value) {
            Box(modifier = Modifier.fillMaxSize()) {
                CameraView(
                    outputDirectory = getOutputDirectory(context),
                    executor = cameraExecutor,
                    onImageCaptured = {
                        shouldShowCamera.value = false
                        viewModel.addImage(uri = it)
                        cameraExecutor.shutdown()
                    },
                    onError = {}
                )
            }
        }
    }
}

fun getOutputDirectory(context: Context): File {
    val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
        File(it, context.resources.getString(R.string.app_name)).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
}