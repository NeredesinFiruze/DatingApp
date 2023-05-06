package com.example.datingapp.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datingapp.data.local.ConnectionInfo
import com.example.datingapp.data.local.DataStoreRepository
import com.example.datingapp.data.local.UserInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    private val database = Firebase.database.getReference("users")
    val signInComplete: State<Flow<Boolean?>> = mutableStateOf(dataStoreRepository.readSignIn())

    var userListState = MutableStateFlow<List<UserInfo>>(emptyList())
        private set

    var userConnectionStatus = MutableStateFlow<List<ConnectionInfo>>(emptyList())
        private set

    fun completeSignIn(changeTo: Boolean) {
        viewModelScope.launch {
            dataStoreRepository.updateSignInResult(changeTo)
        }
    }

//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    fun getCityName(turn: Int, context: Context):String{
//
//        var city: String? = null
//        val geoCoder = Geocoder(context, Locale.getDefault())
//        val address = geoCoder.getFromLocation(
//            userListState.value[turn].locationInfo.first(),
//            userListState.value[turn].locationInfo.last(),
//            1
//        ) { address ->
//            city = if (address[0].locality == null) address[0].subLocality else address[0].locality
//        }
//        return try {
//            city ?:
//            address.
//        }
//        catch (e: Exception){
//            "NOT_FOUND_LOCATION"
//        }
//    }

    fun getUser() {
        val getData = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = mutableListOf<UserInfo>()
                val connectionList = mutableListOf<ConnectionInfo>()

                for (i in snapshot.children) {

                    val userData = i.child("userInfo").getValue(UserInfo::class.java)
                    userData?.let { userList += it }

                    val connectionData = i.child("connectionStatus").getValue(ConnectionInfo::class.java)
                    connectionData?.let { connectionList += it }
                }
                userListState.value = userList
                userConnectionStatus.value = connectionList.toList()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        database.addValueEventListener(getData)
    }
}
