package com.ashuai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

private val Background = Color(0xFFF7F8FC)
private val Purple = Color(0xFF6750A4)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                AshuAIHome()
            }
        }
    }
}

@Composable
fun AshuAIHome() {
    var message by remember { mutableStateOf("") }
    var conversation by remember {
        mutableStateOf(
            listOf(
                "Ashu AI" to "আসসালামু আলাইকুম! আমি Ashu AI। কীভাবে সাহায্য করতে পারি?"
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
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Purple, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("A", color = Color.White, fontSize = 25.sp,
                        fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.width(12.dp))

                Column {
                    Text("Ashu AI", fontSize = 24.sp,
                        fontWeight = FontWeight.Bold)
                    Text("Your personal AI assistant",
                        color = Color.Gray, fontSize = 13.sp)
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 18.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                conversation.forEach { (speaker, text) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (speaker == "You")
                                Color(0xFFE9E1FF) else Color.White
                        )
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(speaker, fontWeight = FontWeight.Bold,
                                color = Purple)
                            Spacer(Modifier.height(5.dp))
                            Text(text, fontSize = 15.sp)
                        }
                    }
                }
            }

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ashu AI-কে কিছু জিজ্ঞেস করো...") },
                shape = RoundedCornerShape(16.dp),
                maxLines = 3
            )

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = {
                    val question = message.trim()
                    if (question.isNotEmpty()) {
                        conversation = conversation + ("You" to question)
                        conversation = conversation + (
                            "Ashu AI" to
                                "তোমার বার্তা পেয়েছি। AI API যুক্ত করার পর আমি উত্তর দিতে পারব।"
                        )
                        message = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple
                )
            ) {
                Text("Send message", fontSize = 16.sp)
            }
        }
    }
}
