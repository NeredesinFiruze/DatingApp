@file:Suppress("KotlinConstantConditions")

package com.example.datingapp.presentation.on_boarding

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.AddPhotoAlternate
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
import androidx.compose.ui.platform.LocalConfiguration
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
import com.example.datingapp.navigation.Screen
import com.example.datingapp.presentation.sign_in.sign_in_with_google.GoogleAuthUiClient
import com.example.datingapp.ui.theme.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executors

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoarding(
    navController: NavController,
    context: Context,
    locationManager: LocationManager,
    googleAuthUiClient: GoogleAuthUiClient,
    viewModel: OnBoardingViewModel = hiltViewModel(),
) {
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
            0 -> {
                FirstPage(
                    navController =navController,
                    context =context,
                    locationManager = locationManager,
                    googleAuthUiClient =googleAuthUiClient,
                    viewModel = viewModel,
                    pageClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                page = page + 1,
                                animationSpec = tween(500)
                            )
                        }
                    },
                    toPage = {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                page = it,
                                animationSpec = tween(250 * it)
                            )
                        }
                    }
                )
            }

            1 -> {
                SecondPage(viewModel) {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = if (it) page + 1 else page - 1,
                            animationSpec = tween(500)
                        )
                    }
                }
            }

            2 -> {
                ThirdPage(viewModel) {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = if (it) page + 1 else page - 1,
                            animationSpec = tween(500)
                        )
                    }
                }
            }

            3 -> {
                FourthPage(viewModel) {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = if (it) page + 1 else page - 1,
                            animationSpec = tween(500)
                        )
                    }
                }
            }

            4 -> {
                FifthPage(viewModel) {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = if (it) page + 1 else page - 1,
                            animationSpec = tween(500)
                        )
                    }
                }
            }

            5 -> {
                SixthPage(navController, context, viewModel) {
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FirstPage(
    navController: NavController,
    context: Context,
    locationManager: LocationManager,
    googleAuthUiClient: GoogleAuthUiClient,
    viewModel: OnBoardingViewModel,
    pageClick: () -> Unit,
    toPage: (Int)-> Unit
) {
    val scrollState = rememberScrollState()
    val imeState = rememberImeState()
    val focusState = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    SideEffect {
        permissionsState.launchMultiplePermissionRequest()
    }

    LaunchedEffect(key1 = imeState.value) {
        if (imeState.value) {
            scrollState.scrollTo(Int.MAX_VALUE)
        }
    }

    //this launch will be run, every location settings change
    LaunchedEffect(key1 = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        if (permissionsState.allPermissionsGranted) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //if gps enabled
                viewModel.setLocation(context)
            } else {
                //if not enabled, then open dialog
                viewModel.openLocationDialog(context)
            }
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
                else {
                    navController.navigate(Screen.SignIn.route)
                    scope.launch {
                        googleAuthUiClient.signOut()
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
            Text(
                text = "Your Name",
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            NameTextField(viewModel) { pageClick() }
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
                    viewModel.saveToLocalDatabase()
                    pageClick()
                }
            }
        )
        if (viewModel.isHaveData.value){
            CustomDialog(
                title = "We found an unfinished registration process.\n Would you like to continue from where you left off?",
                onDismiss = viewModel::dismissDialog,
                onAccept = {
                    scope.launch {
                        val toPageFunction = viewModel.getUserInfoFromLocalDataAndGoToPage()
                        toPageFunction.hashCode()
                        toPage(toPageFunction)
                    }
                }
            )
        }
    }
}

@Composable
fun SecondPage(
    viewModel: OnBoardingViewModel,
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
            DateTextField(viewModel)
            Text(
                text = "Everyone can see your age",
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth(),
                color = GrayLight
            )
        }

        val condition = viewModel.userInfo.value.birthDate.lastOrNull() != 'Y' &&
                viewModel.userInfo.value.birthDate.lastOrNull() != null &&
                viewModel.userInfo.value.birthDate.all { it.isDigit() || it == '/' }

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
                    viewModel.saveToLocalDatabase()
                    pageClick(true)
                }
            }
        )
    }
}

@Composable
fun ThirdPage(
    viewModel: OnBoardingViewModel,
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

            Gender.values().forEachIndexed { index, gender ->
                CustomButton(
                    text = gender.name,
                    enabled = index == viewModel.userInfo.value.gender,
                    onClick = {
                        viewModel.yourGender(index)
                    }
                )
            }
        }
        val condition = viewModel.userInfo.value.gender != -1
        CustomButton(
            text = "NEXT",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(BottomCenter),
            enabled = condition,
            onClick = {
                if (condition) {
                    viewModel.saveToLocalDatabase()
                    pageClick(true)
                }
            }
        )
    }
}

@Composable
fun FourthPage(
    viewModel: OnBoardingViewModel,
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

            Gender.values().forEachIndexed { index, gender ->
                CustomButton(
                    text = gender.name,
                    enabled = viewModel.userInfo.value.interestedGender.contains(index)
                ) {
                    viewModel.showMe(index)
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
            onClick = {
                if (condition) {
                    viewModel.saveToLocalDatabase()
                    pageClick(true)
                }
            }
        )
    }
}

@Composable
fun FifthPage(
    viewModel: OnBoardingViewModel,
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
                            enable = viewModel.userInfo.value.relationType.contains(index),
                            modifier = Modifier
                                .weight(1f),
                            onClick = { viewModel.chooseRelationType(index) }
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
                            enable = viewModel.userInfo.value.relationType.contains(index),
                            modifier = Modifier
                                .weight(1f),
                            onClick = { viewModel.chooseRelationType(index) }
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
        val condition = viewModel.userInfo.value.relationType.isNotEmpty()
        CustomButton(
            text = "NEXT",
            enabled = condition,
            modifier = Modifier
                .align(BottomCenter),
            onClick = {
                if (condition){
                    viewModel.saveToLocalDatabase()
                    pageClick(true)
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SixthPage(
    navController: NavController,
    context: Context,
    viewModel: OnBoardingViewModel,
    pageClick: () -> Unit,
) {
//    LaunchedEffect(Unit){
//        viewModel.test()
//    }
    val localWidth = LocalConfiguration.current.screenWidthDp.dp
    val cameraExecutor = Executors.newSingleThreadExecutor()!!
    var shouldShowCamera by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.setLocation(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Center
    ) {
        BackButton(Modifier.align(TopStart)) { pageClick() }

        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
        ) {
            FlowRow(Modifier.fillMaxWidth()) {
                viewModel.userInfo.value.picture.forEachIndexed { index, uri ->

                    var showMediaIcons by remember { mutableStateOf(false) }

                    Block(
                        modifier = Modifier
                            .width(localWidth / 4)
                            .weight(1f),
                        aspectRatio = .8f,
                        onClick = { showMediaIcons = !showMediaIcons }
                    ) {
                        if (uri == null) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Center
                            ) {
                                if (showMediaIcons){
                                    MediaIcons(
                                        viewModel = viewModel,
                                        index = index,
                                        showCamera = { shouldShowCamera = true }
                                    )
                                }else{
                                    Icon(
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = null,
                                        tint = PinkColor
                                    )
                                }
                            }
                        } else {
                            CustomImageBox(
                                uri = uri,
                                onClickRemove = {
                                    viewModel.removeImage(index)
                                    showMediaIcons = false
                                }
                            )
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

        if (shouldShowCamera) {
            Box(modifier = Modifier.fillMaxSize()) {
                CameraView(
                    outputDirectory = getOutputDirectory(context),
                    executor = cameraExecutor,
                    onImageCaptured = {
                        shouldShowCamera = false
                        viewModel.addImageFromCamera(uri = it)
                        cameraExecutor.shutdown()
                    },
                    onError = {}
                )
            }
        }
    }
}

@Composable
fun MediaIcons(
    viewModel: OnBoardingViewModel,
    index: Int,
    showCamera: ()-> Unit
) {
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                showCamera()
            }
        }
    )

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { listOfUri ->
            viewModel.addImageFromGallery(listOfUri)
        }
    )

    Row {
        IconButton(
            onClick = {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                viewModel.addImageFromCamera(index = index)
            }
        ) {
            Icon(
                imageVector = Icons.Rounded.AddAPhoto,
                contentDescription = null,
                tint = GreenColor,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = {
                multiplePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        ) {
            Icon(
                imageVector = Icons.Rounded.AddPhotoAlternate,
                contentDescription = null,
                tint = GreenColor,
            )
        }
    }
}

@Composable
fun CustomImageBox(uri: String,onClickRemove: ()-> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = rememberAsyncImagePainter(model = uri),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        IconButton(
            onClick = onClickRemove,
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

fun getOutputDirectory(context: Context): File {
    val mediaDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.let {
        File(it, context.resources.getString(R.string.app_name)).apply { mkdirs() }
    }
    return mediaDir ?: context.filesDir
}