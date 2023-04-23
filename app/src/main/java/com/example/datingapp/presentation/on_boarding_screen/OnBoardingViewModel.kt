package com.example.datingapp.presentation.on_boarding_screen

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.datingapp.data.local.Gender
import com.example.datingapp.data.local.RelationType
import com.example.datingapp.data.local.UserInfo
import com.example.datingapp.data.local.listOfRelationType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor() : ViewModel() {

    private val _userInfo = mutableStateOf(UserInfo())
    val userInfo = _userInfo

    private val firebaseStorage = FirebaseStorage.getInstance()
    private val database = Firebase.database
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid

    fun saveName(name: String) {
        _userInfo.value = userInfo.value.copy(
            name = name
        )
    }

    fun saveBirthdate(birthDate: String) {
        _userInfo.value = userInfo.value.copy(
            birthDate = birthDate
        )
    }

    fun yourGender(gender: Gender) {
        _userInfo.value = userInfo.value.copy(
            gender = gender
        )
    }

    fun showMe(gender: Gender) {
        val list = _userInfo.value.interestedGender

        if (list.contains(gender)) {
            _userInfo.value = userInfo.value.copy(
                interestedGender = list.filter { it != gender }
            )
        } else {
            val newList = arrayListOf<Gender>()
            list.forEach {
                newList.add(it)
            }
            newList.add(gender)
            _userInfo.value = userInfo.value.copy(
                interestedGender = newList.toList()
            )
        }
    }

    fun chooseRelationType(relationType: RelationType) {
        val list = _userInfo.value.relationType

        if (list.contains(relationType)) {
            _userInfo.value = userInfo.value.copy(
                relationType = list.filter { it != relationType }
            )
        } else {
            val newList = arrayListOf<RelationType>()
            list.forEach {
                newList.add(it)
            }
            newList.add(relationType)
            _userInfo.value = userInfo.value.copy(
                relationType = newList.toList()
            )
        }
    }

    private val mutableIndex = mutableStateOf(-1)
    fun addImage(uri: Uri? = null, index: Int? = null) {
        val list = _userInfo.value.picture.toMutableList()

        if (index != null) {
            mutableIndex.value = index
        } else {
            viewModelScope.launch {
                list[mutableIndex.value] = uri
                _userInfo.value = userInfo.value.copy(
                    picture = list.toList()
                )
            }
        }
    }

    fun removeImage(index: Int) {
        val list = _userInfo.value.picture.toMutableList()
        list[index] = null
        _userInfo.value = userInfo.value.copy(
            picture = list.toList()
        )
    }

    private val references = mutableListOf<String>()
    private val relationTypeCodeList = mutableListOf<Int>()
    private val interestedGenderCodeList = mutableListOf<Int>()
    fun saveUserToFirebase(navController: NavController) {
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()
        val uuid3 = UUID.randomUUID()
        val uuid4 = UUID.randomUUID()
        val uuid5 = UUID.randomUUID()
        val uuid6 = UUID.randomUUID()

        listOfRelationType.forEachIndexed { index, relationType ->
            if (_userInfo.value.relationType.contains(relationType)) relationTypeCodeList.add(index)
        }
        Gender.values().forEachIndexed { index, gender ->
            if (_userInfo.value.interestedGender.contains(gender)) interestedGenderCodeList.add(index)
        }

        val storageReference = firebaseStorage.reference

        val image = storageReference
            .child("images")
            .child(userId)

        val listOfImages = _userInfo.value.picture.filterNotNull()
        listOfImages.forEachIndexed { index, uri ->
            val indexedUUID = when (index) {
                0 -> uuid1
                1 -> uuid2
                2 -> uuid3
                3 -> uuid4
                4 -> uuid5
                else -> uuid6
            }
            image.child("$indexedUUID.jpg").putFile(uri).addOnSuccessListener {
                firebaseStorage.reference
                    .child("images")
                    .child(userId)
                    .child("$indexedUUID.jpg").downloadUrl.addOnSuccessListener {
                        references.add(it.toString())

                        if (listOfImages.size == index + 1){
                            val user = hashMapOf<String, Any>()
                            user["images"] = references.toList().ifEmpty { "empty" }
                            user["name"] = _userInfo.value.name
                            user["gender"] = _userInfo.value.gender
                            user["interestedGender"] = interestedGenderCodeList.toList()
                            user["relationType"] = relationTypeCodeList.toList()
                            user["birthDate"] = _userInfo.value.birthDate
                            println(interestedGenderCodeList.toList())
                            println(relationTypeCodeList.toList())
                            database.getReference("users")
                                .child(userId)
                                .setValue(user)
                                .addOnCompleteListener {task->
                                    if (task.isSuccessful) navController.navigate("home")
                                    else println("failed")
                                }
                        }
                    }
            }
        }
    }
}
