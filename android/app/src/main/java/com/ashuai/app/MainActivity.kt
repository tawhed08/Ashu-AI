package com.ashuai.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.util.Locale

private val Purple = Color(0xFF6750A4)
private val Background = Color(0xFFF7F8FC)

class MainActivity : ComponentActivity() {

    private var tts: TextToSpeech? = null
    private var ttsReady = false

    private val microphonePermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) startListening()
        }

    private var onSpeechResult: ((String) -> Unit)? = null
    private var onSpeechStatus: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true
                tts?.language = Locale("bn", "BD")
            }
        }

        setContent {
            MaterialTheme {
                AshuAIHome(
                    onSpeak = { speak(it) },
                    onListen = { result, status ->
                        onSpeechResult = result
                        onSpeechStatus = status

                        val granted = ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED

                        if (granted) {
                            startListening()
                        } else {
                            microphonePermission.launch(
                                Manifest.permission.RECORD_AUDIO
                            )
                        }
                    }
                )
            }
        }
    }

    private fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            onSpeechStatus?.invoke(
                "এই ফোনে Speech Recognition পাওয়া যাচ্ছে না।"
            )
            return
        }

        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-BD")
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Ashu AI-কে বলুন")
            }

            onSpeechStatus?.invoke("শুনছি...")

            @Suppress("DEPRECATION")
            startActivityForResult(intent, 1001)
        } catch (_: Exception) {
            onSpeechStatus?.invoke(
                "ভয়েস ইনপুট চালু করা যায়নি।"
            )
        }
    }

    @Deprecated("Uses Android speech recognition activity")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001) {
            val text = data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()

            if (resultCode == RESULT_OK && !text.isNullOrBlank()) {
                onSpeechResult?.invoke(text)
                onSpeechStatus?.invoke("কথা শনাক্ত হয়েছে।")
            } else {
                onSpeechStatus?.invoke("কথা শনাক্ত করা যায়নি। আবার চেষ্টা করো।")
            }
        }
    }

    private fun speak(text: String) {
        if (ttsReady) {
            val result = tts?.setLanguage(Locale("bn", "BD"))
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                tts?.setLanguage(Locale.getDefault())
            }
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ashu-response")
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        super.onDestroy()
    }
}

@Composable
private fun AshuAIHome(
    onSpeak: (String) -> Unit,
    onListen: ((String) -> Unit, (String) -> Unit) -> Unit
) {
    var message by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("তোমার কথা শুনতে প্রস্তুত") }
    var conversation by remember {
        mutableStateOf(
            listOf(
                "Ashu AI" to
                    "আসসালামু আলাইকুম! আমি Ashu AI। মাইক্রোফোনে চাপ দিয়ে কথা বলতে পারো।"
            )
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Purple, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "A",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        "Ashu AI",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Your personal AI assistant",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 15.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(conversation) { _, item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (item.first == "You") {
                                Color(0xFFE9E1FF)
                            } else {
                                Color.White
                            }
                        )
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(
                                item.first,
                                color = Purple,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(5.dp))
                            Text(item.second, fontSize = 15.sp)
                        }
                    }
                }
            }

            Text(
                status,
                color = Color.Gray,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("বাংলায় লিখো বা মাইক্রোফোন ব্যবহার করো...") },
                shape = RoundedCornerShape(16.dp),
                maxLines = 3
            )

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        onListen(
                            { recognized -> message = recognized },
                            { newStatus -> status = newStatus }
                        )
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2)
                    )
                ) {
                    Text("🎙 কথা বলো")
                }

                Button(
                    onClick = {
                        val question = message.trim()
                        if (question.isNotEmpty()) {
                            val answer =
                                "তোমার কথা পেয়েছি: $question\n\n" +
                                "এখন আমি প্রাথমিক সংস্করণে আছি। আসল AI যুক্ত করার পর উত্তর দিতে পারব।"

                            conversation = conversation +
                                ("You" to question) +
                                ("Ashu AI" to answer)

                            onSpeak(answer)
                            message = ""
                            status = "বার্তা যোগ হয়েছে"
                        }
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple
                    )
                ) {
                    Text("Send")
                }
            }
        }
    }
}
