package com.foodtruckfindermi.client


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import android.widget.*
import androidx.core.app.ServiceCompat.stopForeground

import com.foodtruckfindermi.client.Adapter.MessagesAdapter
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await


class ChatScreen : AppCompatActivity() {

    lateinit var chatListView : ListView
    lateinit var db: FirebaseFirestore
    lateinit var groupID: String
    lateinit var userDoc : DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_screen)

        val chatTitle = findViewById<TextView>(R.id.chatTitle)
        val backButton = findViewById<ImageButton>(R.id.chatBackButton)
        chatListView = findViewById<ListView>(R.id.chatListView)
        val messageEntry = findViewById<EditText>(R.id.chatMessageEntry)
        val sendButton = findViewById<Button>(R.id.sendButton)



        db = Firebase.firestore
        val email = intent.getStringExtra("email")!!
        groupID = intent.getStringExtra("groupID")!!
        userDoc = db.collection("Users").document(email)



        backButton.setOnClickListener {
            onBackPressed()
        }

        sendButton.setOnClickListener {
            runBlocking {
                if (messageEntry.text.toString() != "") {
                    val data = getDocInfo(db.collection("Groups").document(groupID))
                    val messagesCache = data["messages"] as ArrayList<String>
                    val sendersCache = data["senders"] as ArrayList<DocumentReference>
                    val participantsList = data["participants"] as ArrayList<DocumentReference>

                    participantsList.remove(userDoc)
                    val receiver = getDocInfo(participantsList[0])

                    messagesCache.add(messageEntry.text.toString())
                    sendersCache.add(userDoc)

                    db.collection("Groups").document(groupID).set(mapOf("messages" to messagesCache, "senders" to sendersCache), SetOptions.merge())


                    val (_, _, result) = Fuel.post(
                        "http://foodtruckfindermi.com/send-notification",
                        listOf("receiver_id" to receiver.id, "message" to messageEntry.text.toString())).awaitStringResponseResult()

                    result.fold({
                        messageEntry.text.clear()
                        refreshList()


                    },
                        { error -> Log.e("http", "$error") })



                }
            }

        }

        runBlocking {
            val docRef = db.collection("Groups").document(groupID)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {

                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    refreshList()
                }
            }
        }



        refreshList()




    }


    private suspend fun getDocInfo(doc: DocumentReference): DocumentSnapshot {
        return doc.get().await()
    }

    private fun refreshList() {
        val groupDoc = db.collection("Groups").document(groupID)
        //

        runBlocking {
            val docData = getDocInfo(groupDoc)



            val MessagesAdapter = MessagesAdapter(this@ChatScreen,
                docData["messages"] as ArrayList<String>,
                docData["senders"] as ArrayList<DocumentReference>,
                userDoc
            )

            chatListView.adapter = MessagesAdapter
        }
    }
}