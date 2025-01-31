package com.example.moodmoji


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.moodmoji.db.MoodEntity
import com.example.moodmoji.db.MoodViewModel
import com.example.moodmoji.ui.theme.MoodMojiTheme

class Journal : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoodMojiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    JournalScreen(
                        viewModel = MoodViewModel(application = application),
                        innerPadding = innerPadding
                    )
                }
            }
        }
    }
}


@Composable
fun JournalScreen(viewModel: MoodViewModel, innerPadding: PaddingValues) {
    val moods = viewModel.allMoods.collectAsState(initial = emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDE7F6))
            .padding(innerPadding) // Apply the padding
    ) {
        Column {
            // Title
            Text(
                text = "Journal",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )

            // Check if the list is empty
            if (moods.value.isEmpty()) {
                // Show the "No stories added yet" text
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No stories added yet",
                        fontSize = 18.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Show the list of moods
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(moods.value) { mood ->
                        MoodCard(
                            mood = mood,
                            viewModel = viewModel,
                            dialogColor = Color(getEmojiColor(mood.emojiId))
                        )
                    }
                }
            }
        }
    }
}


// Function to get all drawable resource IDs based on the emoji ID
fun getAllEmojiResourceIds(emojiId: Int): List<Int> {
    return emojiList.filter { it.id == emojiId }.map { it.drawableResId }.ifEmpty { listOf() }
}

// Function to get the color of the emoji based on the emoji ID
fun getEmojiColor(emojiId: Int): Int {
    val colorString = emojiList.find { it.id == emojiId }?.color ?: "#FFFFFF"
    return android.graphics.Color.parseColor(colorString) // Parse color string to Int
}




// the edit dialog popup for the mood card-------------------------------------------------
@Composable
fun EditMoodDialog(
    mood: MoodEntity,
    onDismiss: () -> Unit,
    viewModel: MoodViewModel
) {
    var description by remember { mutableStateOf(mood.dayDescription) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val dialogColorEmoji = getEmojiColor(mood.emojiId)
    val dialogColor = Color(dialogColorEmoji)

    Dialog(onDismissRequest = { onDismiss() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(dialogColor, shape = RoundedCornerShape(16.dp))
                .border(1.dp, Color.Black, shape = RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                // Title
                Text(
                    text = "Edit Mood",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 24.dp),
                )

                // TextField for editing description
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Button(
                        onClick = {
                            showConfirmationDialog = true // Show the confirmation dialog
                        },
                        colors = ButtonDefaults.buttonColors(Color.White),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Text("Delete", color = Color.Black)
                    }

                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            // Update operation
                            val updatedMood = mood.copy(dayDescription = description)
                            viewModel.updateMood(updatedMood) // Call updateMood to save changes
                            onDismiss() // Dismiss the dialog
                        },
                        colors = ButtonDefaults.buttonColors(Color.White),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Text("Update", color = Color.Black)
                    }
                }
            }
        }
    }

    if (showConfirmationDialog) {
        ConfirmationDialog( mood = mood,
            onConfirm = {
                viewModel.removeMood(mood) // Call removeMood to delete the mood
                showConfirmationDialog = false // Close the confirmation dialog
                onDismiss() // Close the edit dialog
            },
            onDismiss = {
                showConfirmationDialog = false // Close the confirmation dialog
            }
        )
    }
}



@Composable
fun MoodCard(mood: MoodEntity, viewModel: MoodViewModel, dialogColor: Color) {
    val emojiColor = getEmojiColor(mood.emojiId) // Get the color as an Int
    val cardColor = Color(emojiColor) // Convert the Int to a Color
    var editDescription by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = mood.date,
            fontSize = 16.sp,
            textAlign = TextAlign.End
        )
    }
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp), //0 for no shadow
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, Color.Black, shape = RoundedCornerShape(16.dp))
            .clickable { editDescription = true },
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            val emojiResourceIds = getAllEmojiResourceIds(mood.emojiId)
            emojiResourceIds.forEach { resId ->
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = "Emoji",
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = mood.dayDescription, fontSize = 16.sp)
        }
    }

    if (editDescription) {
        EditMoodDialog(
            mood = mood,
                    onDismiss = {
                editDescription = false
            },
            viewModel = viewModel
        )
    }
}


@Composable
fun ConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    mood: MoodEntity

) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color(getEmojiColor(mood.emojiId)), shape = RoundedCornerShape(16.dp))
                .border(1.dp, Color.Black, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Are you sure you want to delete?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Deleting will remove your day log and you won't be able to input it again, only if its today's log!",
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { onConfirm() },
                        colors = ButtonDefaults.buttonColors(Color.Red),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Text("Yes", color = Color.Black)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(Color.White),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Text("No", color = Color.Black)
                    }
                }
            }
        }
    }
}
