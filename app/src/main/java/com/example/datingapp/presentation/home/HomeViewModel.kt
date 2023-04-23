package com.example.datingapp.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.datingapp.data.local.DataStoreRepository
import com.example.datingapp.data.local.RelationType
import com.example.datingapp.data.local.UserInfo
import com.example.datingapp.data.local.listOfRelationType
import com.example.datingapp.util.Extensions.calculateAge
import com.example.datingapp.util.Extensions.firebaseTo
import com.example.datingapp.util.Extensions.firebaseToImageList
import com.example.datingapp.util.Extensions.fixName
import com.example.datingapp.util.Extensions.toGender
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    private val database = Firebase.database.getReference("users")
    val signInComplete: State<Flow<Boolean?>> = mutableStateOf(dataStoreRepository.readSignIn())
    private val _userListState = MutableStateFlow<List<UserInfo>>(emptyList())
    val userListState: StateFlow<List<UserInfo>> = _userListState

    init {
        getUser()
        completeSignIn()
    }

    private fun completeSignIn() {
        viewModelScope.launch {
            dataStoreRepository.updateSignInResult(true)
        }
    }

    val listOfRelation = mutableListOf<RelationType>()
    private fun getUser() {
        val getData = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = mutableListOf<UserInfo>()
                for (i in snapshot.children) {
                    val name = i.child("name").value.toString().fixName()
                    val interestedGender = i.child("interestedGender").value.toString()
                        .firebaseTo().map { it.toGender() }
                    val relationType = i.child("relationType").value.toString()
                        .firebaseTo().map { it.toInt() }
                    val birthDate = i.child("birthDate").value.toString().calculateAge()
                    val images = i.child("images").value.toString().firebaseToImageList()

                    listOfRelationType.forEachIndexed { index, type ->
                        if (relationType.contains(index)) listOfRelation.add(type)
                    }

                    userList.add(
                        UserInfo(
                            name = name,
                            birthDate = birthDate,
                            relationType = listOfRelation,
                            interestedAge = IntRange(18, 20),
                            interestedGender = interestedGender,
                            picture = images,
                        )
                    )
                }
                _userListState.value = userList
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        database.addValueEventListener(getData)
    }
}
