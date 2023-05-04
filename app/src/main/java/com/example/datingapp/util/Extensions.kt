package com.example.datingapp.util

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.roundToInt

object Extensions {

    fun Modifier.animateSwipe(
        lastUserIndex: Int,
        turnStart: Int,
        onTurnChanged: (Int, Float) -> Unit,
    ) = composed {

        var turn by remember { mutableStateOf(turnStart) }
        val isLastUser by remember { derivedStateOf { lastUserIndex == turn + 1 } }

        val offsetX = remember { Animatable(0f) }
        val offsetY = remember { Animatable(0f) }

        val scope = rememberCoroutineScope()
        var isDragging by remember { mutableStateOf(false) }

        var scale by remember { mutableStateOf(1f) }
        val scaleAnimate by animateFloatAsState(
            targetValue = scale,
            animationSpec = tween(500)
        )

        LaunchedEffect(isDragging) {
            scale = if (isDragging) .9f else 1f
        }
        LaunchedEffect(key1 = turn, key2 = offsetX.value) {
            onTurnChanged(turn, offsetX.value)
        }

        this
            .scale(scaleAnimate)
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
                            if (offsetX.value > 400) {
                                offsetX.animateTo(1000f, tween(400)).endReason
                                if (isLastUser) turn = 0 else turn++
                            }
                            if (offsetX.value < -400) {
                                offsetX.animateTo(-1000f, tween(400)).endReason
                                if (isLastUser) turn = 0 else turn++
                            }
                            offsetX.snapTo(0f)
                            offsetY.snapTo(0f)
                        }
                        isDragging = false
                    },
                    onDragCancel = {
                        scope.launch {
                            if (offsetX.value > 400) {
                                if (isLastUser) turn = 0 else turn++
                                offsetX.animateTo(1000f, tween(400)).endReason
                            }
                            if (offsetX.value < -400) {
                                if (isLastUser) turn = 0 else turn++
                                offsetX.animateTo(-1000f, tween(400)).endReason
                            }
                            offsetX.snapTo(0f)
                            offsetY.snapTo(0f)
                        }
                        isDragging = false
                    },
                    onDrag = { change, dragAmount ->
                        scope.launch {
                            change.consume()
                            val newOffsetX = offsetX.value + dragAmount.x
                            val newOffsetY = offsetY.value + dragAmount.y
                            offsetX.snapTo(newOffsetX)
                            offsetY.snapTo(newOffsetY)
                        }
                    }
                )
            }
            .graphicsLayer {
                transformOrigin = if (offsetX.value > 0) TransformOrigin(1f, 0f)
                else TransformOrigin(0f, 0f)

                alpha = 1 - abs(offsetX.value / 3000)
                rotationZ = if (offsetX.value > 100) {
                    (100 - abs(offsetX.value)) / 15
                } else if (offsetX.value < -100) {
                    -(100 - abs(offsetX.value)) / 15
                } else 0f
            }
    }

    fun Long.calculateDate(): String? {
        val now = System.currentTimeMillis()
        val difference = now - this

        if (difference / (1000 * 3600 * 24) > 7) return null
        if (difference / (1000 * 3600 * 24) > 1) return "active recently"
        if (difference / (1000 * 3600) > 1) return "active ${difference / (1000 * 3600)} hour ago"
        return "active less than hour ago"
    }

    fun Long.getTimeAgo(): String {
        val now = System.currentTimeMillis()

        val seconds = ((now - this) / 1000).toInt()
        if (seconds < 60) {
            return "biraz önce"
        }

        val minutes = seconds / 60
        if (minutes < 60) {
            return "$minutes dakika önce"
        }

        val hours = minutes / 60
        if (hours < 24) {
            return "$hours saat önce"
        }

        val dateFormatter = if (now - this < 7 * 24 * 60 * 60 * 1000) {
            SimpleDateFormat("EEEE", Locale.getDefault())
        } else {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        }
        dateFormatter.timeZone = TimeZone.getDefault()
        val date = dateFormatter.format(Date(this))
        return when {
            hours < 48 -> "dün"
            hours < 24 * 7 -> "$hours gün önce"
            else -> date
        }
    }

    fun String.calculateAge(): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = formatter.parse(this)
        val calendarBirth = Calendar.getInstance()
        calendarBirth.time = date!!
        val calendarNow = Calendar.getInstance()
        var age = calendarNow.get(Calendar.YEAR) - calendarBirth.get(Calendar.YEAR)
        if (calendarNow.get(Calendar.DAY_OF_YEAR) < calendarBirth.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age.toString()
    }

    fun String.fixName(): String =
        this.lowercase()
            .replaceFirstChar { it.uppercase() }
}