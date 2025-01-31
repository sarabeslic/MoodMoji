package com.example.moodmoji

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodmoji.db.MoodEntity
import com.example.moodmoji.db.MoodViewModel
import com.example.moodmoji.ui.theme.MoodMojiTheme
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class PopUp : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoodMojiTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        PopUpScreen(
                            moodViewModel = MoodViewModel(application = application),
                            innerPadding = innerPadding, onClose = { finish() }
                        )
                    }
                }
            }
        }
    }




@Composable
fun PopUpScreen(moodViewModel: MoodViewModel, innerPadding: PaddingValues, onClose: () -> Unit) {
    var dayDescription by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf<Int?>(null) }

    val currentDate by remember { mutableStateOf(LocalDate.now(ZoneId.systemDefault())) } //to get the current date and the right time zone
    val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))

    val context = LocalContext.current // Get the current context for showing Toast messages!
    val scrollState = rememberScrollState() // Remember the scroll state of the Column


    val moodDates = moodViewModel.allMoods.collectAsState(initial = emptyList()).value
    val moodDatesList = moodDates.map { it.date }


    if (moodDatesList.contains(formattedDate)) {
         ImputedMood(formattedDate=formattedDate, moodDatesList = moodDatesList,
             moodDates = moodDates, innerPadding = innerPadding, onClose = onClose)
    } else {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2EBFB))
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,

            ) {
            // Top Bar with "X" Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onClose) { // Navigate back when "X" is clicked
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Black
                    )
                }
            }

            // Question and Date
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
            ) {
                Text(
                    text = "How are you feeling today?",
                    lineHeight = 45.sp,
                    fontSize = 45.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                )

                //today's date
                Text(
                    text = formattedDate,
                    fontSize = 24.sp,
                    color = Color.DarkGray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            EmojiPicker(
                selectedEmoji = selectedEmoji,
                onEmojiSelected = { emojiNumber ->
                    selectedEmoji = emojiNumber
                    Toast.makeText(
                        context,
                        "You selected ${context.resources.getResourceEntryName(emojiList[emojiNumber - 1].drawableResId)}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            Spacer(modifier = Modifier.height(16.dp)) // Add space between Emoji Picker and Input Field
            // Input Field
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp)
            ) {
                BasicTextField(
                    value = dayDescription,
                    onValueChange = { dayDescription = it },
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (dayDescription.isEmpty()) {
                                Text(
                                    text = "Write your day...",
                                    color = Color.DarkGray,
                                    modifier = Modifier.padding(start = 10.dp, top = 10.dp)

                                )
                            }
                            Box(
                                modifier = Modifier.padding(start = 10.dp, top = 10.dp) // Align input text
                            ) {
                                innerTextField()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .padding(12.dp)
                        .size(130.dp)
                )

                Spacer(modifier = Modifier.height(8.dp)) // Add space between TextField and Button

                // this will add the data to the database, add the functionality later..and close the popup automatically
                Button(
                    onClick = {
                        if(selectedEmoji!=null){
                            val mood = MoodEntity (
                                emojiId = selectedEmoji!!,
                                date = formattedDate,
                                dayDescription = dayDescription
                            )
                            moodViewModel.addMood(mood)
                        }

                        else{
                            Toast.makeText(context, "Please select an emoji", Toast.LENGTH_SHORT).show()
                        }

                    },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .width(150.dp)
                        .background(Color(0xFFc19ee0), shape = CircleShape) // Set background color
                        .border(1.dp, Color.Black, shape = CircleShape), // Set border size and color
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFc19ee0))
                ) {
                    Text(
                        text = "LOG MOOD",
                        color = Color.Black
                    )
                }
            }
            //here test

        }

    }
}




//-------------------------the emoji grid picker -------------------------------------------------
// Define a list of pairs where each pair contains the emoji resource ID and its corresponding number
data class Emoji(
    val drawableResId: Int,
    val id: Int,
    val description: String,
    val color: String,
    val quote: String
)

val emojiList = listOf(
    Emoji(R.drawable.overjoyed, 1, "Friendly", "#fcc280", "You're a social butterfly with great vibes!"),
    Emoji(R.drawable.sad, 2, "Sad", "#8395f7", "Don't worry, this too shall pass."),
    Emoji(R.drawable.calm, 3, "Calm", "#66e0c6", "Zen mode: Activated. Nothing can ruffle your feathers."),
    Emoji(R.drawable.happy, 4, "Happy", "#e2b5f2", "You’re glowing like sunshine on a perfect day!"),
    Emoji(R.drawable.angry, 5, "Angry", "#ff9999", "You're one bad Wi-Fi signal away from flipping a table. Deep breaths, champ!"),
    Emoji(R.drawable.love, 6,"Love", "#ffb3d9", "Love is all around... probably because you keep swiping right!"),
    Emoji(R.drawable.playful, 7, "Playful", "#ffe34d", "You’re the life of the party—no questions asked!"),
    Emoji(R.drawable.uncomfortable, 8, "Uncomfortable", "#a9e091", "Awkward? Own it. You’re hilariously relatable!"),
    Emoji(R.drawable.upset, 9, "Upset", "#d48cb5", "Scream into a pillow. It’s therapeutic, I promise!"),
    Emoji(R.drawable.fear, 10, "Fear", "#e1a9fe", "Nature's way of saying, 'Run now, think later!' But hey, you got this."),
    Emoji(R.drawable.worry, 11, "Worry", "#afe0f6", "Worrying is like paying interest on a loan you haven’t taken yet. Chill out!"),
    Emoji(R.drawable.bored, 12, "Bored", "#e6e6e6", "Bored? That's the universe politely telling you to take a nap or start a hobby.")


    )

@Composable
fun EmojiPicker(selectedEmoji: Int?, onEmojiSelected: (Int) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display emojis in a grid layout (3 per row)
        emojiList.chunked(3).forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                row.forEach { (emojiResId, emojiNumber) ->
                    EmojiImage(
                        emojiResId = emojiResId,
                        isSelected = emojiNumber == selectedEmoji,
                        onClick = { onEmojiSelected(emojiNumber) }
                    )
                }
            }
        }
    }
}


//-------------------------the emoji image button-------------------------------------------------
@Composable
fun EmojiImage(
    emojiResId: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(90.dp)
            .clickable { onClick() }
            .padding(2.dp)
            .border(
                width = if (isSelected) 7.dp else 0.dp,
                color = if (isSelected) Color.Green else Color.Transparent,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = emojiResId),
            contentDescription = "Emoji",
            modifier = Modifier.size(75.dp) // Slightly smaller than the button
        )
    }
}




@Composable
fun ImputedMood(formattedDate: String, moodDatesList: List<String>,
                moodDates: List<MoodEntity>, innerPadding: PaddingValues, onClose: () -> Unit) {

    val bgColor = moodDates.firstOrNull { it.date == formattedDate }
        ?.emojiId
        ?.let { emojiId ->
            emojiList.find { it.id == emojiId }?.color?.let { colorString ->
                Color(android.graphics.Color.parseColor(colorString))
            }
        } ?: Color(0xFFF2EBFB) // Default background color if no match is found

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor) // Use the calculated background color
            .padding(16.dp)
    ) {
        // Top Bar with "X" Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onClose) { // Navigate back when "X" is clicked
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Black
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center, // Center content vertically
            horizontalAlignment = Alignment.CenterHorizontally, // Center content horizontally

        ) {

            Text(
                text = "Your mood is logged " +
                        "for today!",
                fontSize = 42.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                textAlign = TextAlign.Center,

            )
            Spacer(modifier = Modifier.height(16.dp)) // Add space between text and date
            Text(
                text = formattedDate,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (moodDatesList.contains(formattedDate)) {
                val mood = moodDates.find { it.date == formattedDate }
                mood?.let {
                    val emoji = emojiList.find { emoji -> emoji.id == it.emojiId }
                    emoji?.let { emojiData ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),

                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = emojiData.drawableResId),
                                    contentDescription = "Emoji",
                                    modifier = Modifier.size(160.dp).border(8.dp, Color.White, shape = CircleShape)
                                )
                                Spacer(modifier = Modifier.height(16.dp)) // Space between emoji and description

                                Text(
                                    text = emojiData.description,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    fontStyle = FontStyle.Italic,
                                    text = emojiData.quote,
                                    fontSize = 28.sp,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


