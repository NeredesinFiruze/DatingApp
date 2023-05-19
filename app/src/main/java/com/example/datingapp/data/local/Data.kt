package com.example.datingapp.data.local

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey

data class UserInfo(
    val uid: String = "",
    val name: String = "",
    val birthDate: String = "",
    val picture: List<String?> = listOf(null, null, null, null, null, null),
    val gender: Int = -1,
    val interestedGender: List<Int> = emptyList(),
    val interestedAge: IntRange? = null,
    val relationType: List<Int> = emptyList(),
    val locationInfo: List<Double> = emptyList(),
){
    fun toEntity(): UserInfoEntity =
        UserInfoEntity(
            name, birthDate, gender, interestedGender, relationType, locationInfo
        )

}

@Entity("userInfo")
data class UserInfoEntity(
    val name: String = "",
    val birthDate: String = "",
    val gender: Int = -1,
    val interestedGender: List<Int> = emptyList(),
    val relationType: List<Int> = emptyList(),
    val locationInfo: List<Double> = emptyList(),
    @PrimaryKey val id: Int = 1
){
    fun toUserInfo(): UserInfo =
        UserInfo(
            name = name,
            birthDate = birthDate,
            gender = gender,
            interestedGender = interestedGender,
            relationType = relationType,
            locationInfo = locationInfo
        )
}

enum class Gender {
    MALE, FEMALE
}

data class RelationType(
    val emoji: String,
    val desc: String,
)

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector,
    val badgeCount: Int = 0,
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
        desc = "long-term relationship"
    ),
    RelationType(
        emoji = "\uD83D\uDE0D",
        desc = "long-term but can also be short"
    ),
    RelationType(
        emoji = "\uD83E\uDD42",
        desc = "short-term but can also be long"
    ),
    RelationType(
        emoji = "\uD83C\uDF89",
        desc = "short time fun"
    ),
    RelationType(
        emoji = "\uD83D\uDC4B",
        desc = "new friends"
    ),
    RelationType(
        emoji = "\uD83E\uDD14",
        desc = "not decided yet"
    )
)

data class ConnectionInfo(
    val connectionStatus: Boolean = false,
    val lastConnected: String? = null,
)