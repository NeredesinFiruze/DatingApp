package com.example.datingapp.presentation.sign_in.sign_in_with_phone

import android.content.Context
import android.content.IntentFilter
import android.telephony.TelephonyManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.datingapp.composables.BackButton
import com.example.datingapp.composables.CustomButton
import com.example.datingapp.ui.theme.GrayNormal
import com.example.datingapp.ui.theme.PinkColor
import com.example.datingapp.ui.theme.TextColor
import com.example.datingapp.util.SmsBroadcastReceiver
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

val smsCode = MutableStateFlow("")

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SignInWithPhone(
    navController: NavController,
    telephonyManager: TelephonyManager,
    context: Context,
    phoneViewModel: PhoneViewModel = hiltViewModel(),
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val lifecyleOwner = LocalLifecycleOwner.current.lifecycle
    val localContext = LocalContext.current

    DisposableEffect(lifecyleOwner,localContext){
        val smsReceiver = SmsBroadcastReceiver()

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        localContext.registerReceiver(smsReceiver, intentFilter, SmsRetriever.SEND_PERMISSION, null)

        val observer = LifecycleEventObserver { _, _ ->
            SmsRetriever.getClient(context).startSmsUserConsent(null)
        }
        lifecyleOwner.addObserver(observer)

        onDispose {
            localContext.unregisterReceiver(smsReceiver)
        }
    }

    Column {
        BackButton {
            if (pagerState.currentPage == 1) {
                scope.launch {
                    pagerState.animateScrollToPage(page = 1, animationSpec = tween(300))
                }
            } else {
                navController.popBackStack()
            }
        }
        HorizontalPager(
            pageCount = 2,
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> {
                    FirstPhonePage(
                        phoneViewModel = phoneViewModel,
                        telephonyManager = telephonyManager,
                        context = context,
                        onSuccess = {
                            scope.launch {
                                pagerState.animateScrollToPage(page = 1, animationSpec = tween(300))
                            }
                        }
                    )
                }

                1 -> {
                    SecondPhonePage(
                        phoneViewModel = phoneViewModel,
                        context = context,
                        onSuccess = {
                            scope.launch {
                                val destination = phoneViewModel.destination()
                                navController.navigate(destination)
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstPhonePage(
    phoneViewModel: PhoneViewModel,
    telephonyManager: TelephonyManager,
    context: Context,
    onSuccess: () -> Unit,
) {
    val focusState = LocalFocusManager.current
    val defaultCountryCode = telephonyManager.simCountryIso.uppercase(Locale.getDefault())
    var phoneNumber by remember { mutableStateOf("") }
    var isListVisible by remember { mutableStateOf(false) }
    val countries = phoneViewModel.countries
    val indexCode = phoneViewModel.phoneData.value.countryCodeIndex

    val iconRotateValue = remember { mutableStateOf(0f) }
    val animateIcon by animateFloatAsState(
        targetValue = iconRotateValue.value,
        animationSpec = tween(400)
    )

    LaunchedEffect(Unit) {
        phoneViewModel
            .changeIndex(countries.indexOfFirst { it.first == defaultCountryCode })
    }

    LaunchedEffect(key1 = isListVisible) {
        iconRotateValue.value += 180f
    }

    Column {
        ConstraintLayout(
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            val (
                title,
                tCode,
                tCodeList,
                desc,
                nextButton,
            ) = createRefs()

            Text(
                text = "My number",
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .constrainAs(title) {
                        top.linkTo(parent.top, 16.dp)
                        start.linkTo(parent.start, 16.dp)
                    },
                fontSize = 32.sp,
                color = TextColor,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier
                    .constrainAs(tCode) {
                        top.linkTo(title.bottom, margin = 16.dp)
                        start.linkTo(title.start)
                        end.linkTo(parent.end, 16.dp)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clickable { isListVisible = !isListVisible }
                        .padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = countries[indexCode].first.toString(),
                        fontSize = 21.sp,
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier
                            .rotate(animateIcon)
                    )
                }
                TextField(
                    value = phoneNumber,
                    onValueChange = { if (it.length <= 10) phoneNumber = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.clip(CircleShape),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            this@Column.AnimatedVisibility(
                visible = isListVisible,
                modifier = Modifier
                    .constrainAs(tCodeList) {
                        start.linkTo(tCode.start)
                        top.linkTo(tCode.bottom)
                    }
                    .zIndex(2f)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .background(Color.White)
                ) {
                    itemsIndexed(countries) { index, country ->
                        Text(
                            text = "${country.first} (${country.second})",
                            fontWeight = if (index == indexCode) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .clickable {
                                    isListVisible = false
                                    phoneViewModel.changeIndex(index)
                                },
                            fontSize = 21.sp
                        )
                    }
                }
            }

            Text(
                text = "sms desc",
                modifier = Modifier.constrainAs(desc) {
                    top.linkTo(tCode.bottom, 24.dp)
                    start.linkTo(parent.start, 24.dp)
                    end.linkTo(parent.end, 24.dp)
                }
            )
            val condition = phoneNumber.length > 9
            CustomButton(
                text = "NEXT",
                enabled = condition,
                onClick = {
                    if (condition){
                        focusState.clearFocus()
                        val code = countries[indexCode].second
                        phoneViewModel.getOTP(
                            phoneNumber = code.toString() + phoneNumber,
                            context = context,
                            onSuccess = onSuccess
                        )
                    }
                },
                modifier = Modifier
                    .constrainAs(nextButton) {
                        top.linkTo(desc.bottom, 16.dp)
                    }
                    .zIndex(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SecondPhonePage(
    phoneViewModel: PhoneViewModel,
    context: Context,
    onSuccess: () -> Unit,
) {
    val focusState = LocalFocusManager.current
    val focusRequesters = remember { List(6) { FocusRequester() } }
    val textFieldValues = remember { mutableStateListOf("", "", "", "", "", "") }
    val scope = rememberCoroutineScope()

    val code by smsCode.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = code) {
        if (code.length != 6) return@LaunchedEffect
        scope.launch {
            code.forEachIndexed { index: Int, c: Char ->
                textFieldValues[index] = c.toString()
            }.also {
                smsCode.emit("")
            }
        }
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            val localWidth = LocalConfiguration.current.screenWidthDp.dp

            for (i in 0..5) {
                val value = textFieldValues[i]
                val focusRequester = focusRequesters[i]

                TextField(
                    value = value,
                    onValueChange = { newValue->
                        if (newValue.all { it.isDigit() }) {
                            textFieldValues[i] =
                                if (textFieldValues[i].isEmpty()) newValue
                                else if (newValue.isEmpty()) newValue
                                else if(newValue.first() == newValue.last()) newValue.first().toString()
                                else newValue.replace(textFieldValues[i].first().toString(),"")

                            val nextIndex = i + 1
                            if (nextIndex < 6 && newValue.isNotEmpty()) {
                                focusRequesters[nextIndex].requestFocus()
                            }
                        }
                    },
                    modifier = Modifier
                        .width(localWidth / 7)
                        .focusRequester(focusRequester)
                        .padding(horizontal = 4.dp)
                        .onKeyEvent {
                            if (it.key == Key.Backspace) {
                                textFieldValues[i] = ""
                                val previousIndex = i - 1
                                if (previousIndex >= 0) {
                                    focusRequesters[previousIndex].requestFocus()
                                }
                            }
                            false
                        },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        cursorColor = PinkColor,
                        focusedIndicatorColor = PinkColor,
                        unfocusedIndicatorColor = Color.LightGray
                    ),
                    singleLine = true,
                    textStyle = TextStyle(
                        color = GrayNormal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
        val condition = textFieldValues.joinToString("").length == 6
        CustomButton(
            text = "DONE",
            enabled = condition
        ) {
            if (condition){
                focusState.clearFocus()
                phoneViewModel.signInWithPhoneAuthCredential(
                    PhoneAuthProvider.getCredential(
                        phoneViewModel.phoneData.value.verificationId,
                        textFieldValues.joinToString("")
                    ),
                    context = context,
                    onSuccess = onSuccess
                )
            }
        }
    }
}