package com.example.datingapp.presentation.on_boarding

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.datingapp.data.local.UserInfo
import com.example.datingapp.navigation.Screen
import com.google.android.gms.location.LocationServices
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
    private val database = Firebase.database.getReference("users")
    private val userId = FirebaseAuth.getInstance().currentUser?.uid!!

    @SuppressLint("MissingPermission")
    fun setLocation(context: Context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                val latitude = location?.latitude
                val longitude = location?.longitude

                if (latitude != null && longitude != null)
                    _userInfo.value = userInfo.value.copy(
                        locationInfo = listOf(latitude, longitude)
                    )
            }
    }

    fun openLocationDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Location is not enabled. please open for better matches")
            .setCancelable(false)
            .setPositiveButton("Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
        val alert = builder.create()
        alert.show()

    }

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

    fun yourGender(gender: Int) {
        _userInfo.value = userInfo.value.copy(
            gender = gender
        )
    }

    fun showMe(gender: Int) {
        val list = _userInfo.value.interestedGender

        if (list.contains(gender)) {
            _userInfo.value = userInfo.value.copy(
                interestedGender = list.filter { it != gender }
            )
        } else {
            val newList = arrayListOf<Int>()
            list.forEach {
                newList.add(it)
            }
            newList.add(gender)
            _userInfo.value = userInfo.value.copy(
                interestedGender = newList.toList()
            )
        }
    }

    fun chooseRelationType(relationType: Int) {
        val list = _userInfo.value.relationType

        if (list.contains(relationType)) {
            _userInfo.value = userInfo.value.copy(
                relationType = list.filter { it != relationType }
            )
        } else {
            val newList = arrayListOf<Int>()
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
    fun addImageFromCamera(uri: Uri? = null, index: Int? = null) {
        val list = _userInfo.value.picture.toMutableList()

        if (index != null) {
            mutableIndex.value = index
        } else {
            viewModelScope.launch {
                list[mutableIndex.value] = uri.toString()
                _userInfo.value = userInfo.value.copy(
                    picture = list.toList()
                )
            }
        }
    }

    fun addImageFromGallery(uris: List<Uri>){
        val list: MutableList<String?> = _userInfo.value.picture.filterNotNull().toMutableList()
        uris.forEach {
            if (list.size < 6) list.add(it.toString())
        }

        while (list.size < 6){
            list.add(null)
        }

        _userInfo.value = userInfo.value.copy(
            picture = list.toList()
        )
    }

    fun removeImage(index: Int) {
        val list = _userInfo.value.picture.toMutableList()
        list[index] = null
        _userInfo.value = userInfo.value.copy(
            picture = list.toList()
        )
    }

    val isFrontCamera = mutableStateOf(false)
    fun rotateCamera() {
        isFrontCamera.value = !isFrontCamera.value
    }

//    fun test() {
//        _userInfo.value = userInfo.value.copy(
//            uid = userId,
//            name = "Mert",
//            birthDate = "12/02/1989",
//            picture = listOf(
//                "file:///storage/emulated/0/Android/media/com.example.datingapp/Dating%20App/2023-04-29-12-17-49-464.jpg",
//                "file:///storage/emulated/0/Android/media/com.example.datingapp/Dating%20App/2023-04-29-12-17-54-247.jpg",
//                "file:///storage/emulated/0/Android/media/com.example.datingapp/Dating%20App/2023-04-29-12-17-59-901.jpg",
//                null,
//                null,
//                null
//            ),
//            gender = 0,
//            interestedGender = listOf(1, 0),
//            interestedAge = null,
//            relationType = listOf(1, 4, 5, 2),
//            locationInfo = listOf(37.4, -122.084)
//        )
//    }

    private val references = mutableListOf<String>()
    fun saveUserToFirebase(navController: NavController) {
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()
        val uuid3 = UUID.randomUUID()
        val uuid4 = UUID.randomUUID()
        val uuid5 = UUID.randomUUID()
        val uuid6 = UUID.randomUUID()

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
            image.child("$indexedUUID.jpg").putFile(uri.toUri()).addOnSuccessListener {
                firebaseStorage.reference
                    .child("images")
                    .child(userId)
                    .child("$indexedUUID.jpg").downloadUrl.addOnSuccessListener {
                        references.add(it.toString())

                        if (listOfImages.size == index + 1) {//if all image downloaded, do that below
                            _userInfo.value = userInfo.value.copy(
                                picture = references.toList(),
                                uid = userId
                            )

                            database
                                .child(userId)
                                .child("userInfo")
                                .setValue(UserInfo::class.java.cast(_userInfo.value))
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) navController.navigate(Screen.Home.route)
                                    else println("failed")
                                }
                        }
                    }
            }
        }
    }
}
