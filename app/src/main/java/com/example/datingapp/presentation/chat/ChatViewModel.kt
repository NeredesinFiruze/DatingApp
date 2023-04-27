package com.example.datingapp.presentation.chat

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.datingapp.data.local.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel@Inject constructor(): ViewModel() {

    private val _messages = MutableStateFlow<List<MessageData>>(emptyList())
    val messages: StateFlow<List<MessageData>> = _messages

    val userInfo = mutableStateOf(UserInfo())
    private val database = Firebase.database.getReference("users")
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    fun setUserInfo(user: UserInfo) {
        userInfo.value = user
    }

    fun getMessages(){
        if (userId.isNullOrEmpty())return

        val databaseRef = FirebaseDatabase.getInstance().getReference("users")
            .child(userId)
            .child("chats")
            .child(userInfo.value.uid)
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val message = mutableListOf<MessageData>()
                for (messageSnapshot in dataSnapshot.children) {

                    val messageData = messageSnapshot.getValue(MessageData::class.java)
                    messageData?.let {
                        message.add(it)
                    }
                }
                _messages.value = message.toList()
            }

            override fun onCancelled(databaseError: DatabaseError) = Unit
        })
    }

    fun sendMessage(message: String){
        if (userId.isNullOrEmpty())return

        val user = hashMapOf<String, String>()
        user["from"] = userId
        user["message"] = message
        user["timestamp"] = System.currentTimeMillis().toString()

        database
            .child(userId)
            .child("chats")
            .child(userInfo.value.uid)
            .push()
            .setValue(user)

        database
            .child(userInfo.value.uid)
            .child("chats")
            .child(userId)
            .push()
            .setValue(user)
    }
}

data class MessageData(
    val from: String = "",
    val message: String = "",
    // This timestamp extends from firebase
    val timestamp: String = System.currentTimeMillis().toString(),
)