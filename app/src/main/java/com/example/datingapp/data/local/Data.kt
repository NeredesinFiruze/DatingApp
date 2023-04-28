package com.example.datingapp.data.local

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class UserInfo(
    val uid: String = "",
    val name: String = "",
    val birthDate: String = "",
    val picture: List<Uri?> = listOf(null,null,null, null, null, null),
    val gender: Gender = Gender.NONE,
    val interestedGender: List<Gender> = emptyList(),
    val interestedAge: IntRange? = null,
    val relationType: List<RelationType> = emptyList(),
)

enum class Gender{
    MALE, FEMALE, NONE
}

data class RelationType(
    val emoji: String,
    val desc: String,
)

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector,
    val badgeCount: Int = 0
)

val listOfBottomNavItem = listOf(
    BottomNavItem(
        name = "Home",
        route = "home",
        icon = Icons.Default.Home
    ),
    BottomNavItem(
        name = "Chat",
        route = "chat-list",
        icon = Icons.Default.Chat,
        badgeCount = 0
    ),
    BottomNavItem(
        name = "Settings",
        route = "settings",
        icon = Icons.Default.Settings
    )
)

val listOfRelationType: List<RelationType> = listOf(
    RelationType(
        emoji = "\uD83D\uDC98",
        desc = "uzun süreli ilişki"
    ),
    RelationType(
        emoji = "\uD83D\uDE0D",
        desc = "uzun ilişki ama kısa da olur"
    ),
    RelationType(
        emoji = "\uD83E\uDD42",
        desc = "kısa ilişki ama uzun da olur"
    ),
    RelationType(
        emoji = "\uD83C\uDF89",
        desc = "kısa süreli eğlence"
    ),
    RelationType(
        emoji = "\uD83D\uDC4B",
        desc = "yeni arkadaşlar"
    ),
    RelationType(
        emoji = "\uD83E\uDD14",
        desc = "henüz karar vermedim"
    )
)

data class ConnectionInfo(
    val connectionStatus: Boolean = false,
    val lastConnected: String? = null,
)