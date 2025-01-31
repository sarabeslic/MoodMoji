package com.example.moodmoji

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.moodmoji.db.MoodEntity
import com.example.moodmoji.db.MoodViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class MainActivity : ComponentActivity() {
    private val moodViewModel: MoodViewModel by viewModels(){
    ViewModelProvider.AndroidViewModelFactory.getInstance(application)
}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp(moodViewModel= moodViewModel)
        }
    }
}


@Composable //splash screen that disappears after 5 seconds-----------------------------------
fun SplashScreen(onSplashFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(5000) // Wait for 5 seconds
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC19EE0)) // Light purple background
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {

            Image(
                painter = painterResource(id = R.drawable.hearts),
                contentDescription = "MoodMoji Logo",
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.Start)
                    .padding(start = 16.dp, top = 16.dp)

            )

            Image(
                painter = painterResource(id = R.drawable.emoji3),
                contentDescription = "MoodMoji Logo",
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.End)
                    .padding(16.dp)

            )
            //image logo-----------
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "MoodMoji Logo",
                modifier = Modifier.size(400.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.cutemeoji),
                contentDescription = "MoodMoji Logo",
                modifier = Modifier.size(130.dp).align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(25.dp))

        }
    }
}




//-------------------------------navigation----------------------------------------------------
enum class Screens {
    Calendar,
    AddMood,
    Journal
}


@Composable
fun MainApp(moodViewModel: MoodViewModel) {
    val navController = rememberNavController()

    // Splash screen
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) { //when the splash screen is shown
        SplashScreen {
            showSplash = false
        }
    } else {

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = { BottomNavigationBar(navController) },

        ) { innerPadding ->

            NavHost(
                navController = navController,
                startDestination = Screens.Calendar.name //CHANGED remove inner padding!
            ) {
                composable(Screens.Calendar.name) {
                    MainScreen(innerPadding = innerPadding, moodViewModel = moodViewModel)
                }
                composable(Screens.AddMood.name) {
                    //added
                    navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>("showBottomBar")
                    PopUpScreen(moodViewModel = moodViewModel, innerPadding = innerPadding, onClose = {
                        navController.navigateUp() //navigate up means go back to the previous screen
                    })

                }
                composable(Screens.Journal.name) {
                    JournalScreen(viewModel = moodViewModel, innerPadding = innerPadding)
                }
            }
        }
    }
}

//-----------------------------navigation bar-------------------------------------------

    @Composable
    fun BottomNavigationBar(navController: NavController) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            ?: Screens.Calendar.name

        //added
        val showBottomBar = remember { mutableStateOf(true) }
        val isAddMoodScreen = currentRoute == Screens.AddMood.name
        LaunchedEffect(key1 = isAddMoodScreen) {
            if (isAddMoodScreen) {
                showBottomBar.value = false
            } else {
                showBottomBar.value = true
            }
        }
        if (showBottomBar.value) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .background(
                        Color.Transparent,
                        RoundedCornerShape(50, 50, 0, 0)
                    ), // Rounded background
                contentAlignment = Alignment.BottomCenter

            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.White,
                            RoundedCornerShape(50, 50, 0, 0)
                        ) // Rounded background
                        .height(65.dp)
                        .border(
                            2.dp, Color.Black, RoundedCornerShape(50, 50, 0, 0)
                        ) // Rounded border avoids central overlap
                        .padding(horizontal = 32.dp), // Adjust padding for alignment
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Calendar Icon
                    IconButton(onClick = { navController.navigate(Screens.Calendar.name) }) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Calendar",
                            Modifier.size(35.dp),
                            tint = if (currentRoute == Screens.Calendar.name) Color.Green else Color.Gray
                        )
                    }

                    // Journal Icon
                    IconButton(onClick = { navController.navigate(Screens.Journal.name) }) {
                        Icon(
                            imageVector = Icons.Filled.Create,
                            contentDescription = "Journal",
                            Modifier.size(35.dp),
                            tint = if (currentRoute == Screens.Journal.name) Color.Green else Color.Gray
                        )
                    }
                }

                // Floating Action Button (Add Mood)----------------------------
                Box(
                    modifier = Modifier
                        .size(70.dp) // Larger size for the FAB
                        .offset(y = (-30.dp)) // Lifted above the border
                        .clip(CircleShape)
                        .background(Color(0xFFC19EE0), shape = CircleShape) // Purple background
                        .border(2.dp, Color.Black, CircleShape) // Optional border
                        .clickable { navController.navigate(Screens.AddMood.name) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Mood",
                        tint = Color.White,
                        modifier = Modifier.size(45.dp)
                    )
                }
            }
        }
    }

        //-------------------------------the edit window that shows when the date that has data is clicked-------------------------------------
        @Composable
        fun MoodDate(
            mood: MoodEntity,
            viewModel: MoodViewModel,
            selectedDate: MutableState<LocalDate>,
        ) {
            var isEditing by remember { mutableStateOf(false) } //to check if we are editing
            var showDialog by remember { mutableStateOf(false) } //to check if we are deleting

            val currentSelectedDate by rememberUpdatedState(selectedDate.value)
            DisposableEffect(currentSelectedDate) {
                isEditing = false
                onDispose { }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //if user is editing, show the fields to edit-----------------
                if (isEditing) {
                    var dayDescription by remember { mutableStateOf(mood.dayDescription) }
                    TextField(
                        value = dayDescription,
                        onValueChange = { dayDescription = it },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    //for the buttons--------------------------
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Button(
                            onClick = { showDialog = true },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color.Black)
                        ) {
                            Text("Delete", color = Color.White)
                        }

                        Button(
                            onClick = {
                                viewModel.updateMood(mood.copy(dayDescription = dayDescription))
                                isEditing = false
                            },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color.Black),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC19EE0))
                        ) {
                            Text("Save", color = Color.White)
                        }
                    }
                } else {
                    // when user is just looking at the data, but no editing------------
                    Text(
                        text = "Entry for ${mood.date}",
                        color = Color.Black,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = mood.dayDescription,
                            color = Color.Black,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        )


                        IconButton(
                            onClick = { isEditing = true },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Create,
                                contentDescription = "Edit",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "Confirm Delete") },
                    text = {
                        Text(
                            text = "Deleting will remove your day log and you won't be able to input it again, only if it's today's log!",
                            color = Color.Red
                        )
                    },

                    confirmButton = {
                        Button(
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color.Black),
                            onClick = {
                                viewModel.removeMood(mood)
                                showDialog = false
                                isEditing = false
                            }
                        ) {
                            Text("Delete")
                        }
                    },

                    dismissButton = {
                        Button(
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color.Black),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC19EE0)),
                            onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    },

                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
                )
            }
        }


        //--------------------main screen that holds the calendar and the mood data-------------------------------------
@Composable
fun MainScreen(innerPadding: PaddingValues, moodViewModel: MoodViewModel) {
    // State for the currently displayed month
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val moodDates = moodViewModel.allMoods.collectAsState(initial = emptyList())
    val date = LocalDate.of(currentMonth.year, currentMonth.month, 1)

    //this is what we need to fix!!!
    val selectedDate = remember { mutableStateOf(date) } //this is the date that is selected, and passed to the calendar and the mood date!!


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDE7F6)) // Light purple background

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp, end = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            CalendarBox( //here passing
                currentMonth = currentMonth,
                moodDates = moodDates.value,
                onMonthChange = { newMonth -> currentMonth = newMonth },
                onDayChange = { newDate -> selectedDate.value = newDate },
                selectedDate = selectedDate
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedDate.value == date) {
                Text(
                    text = "Select a date to view your mood log :)",
                    fontStyle = FontStyle.Italic,
                    color = Color.Gray,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            } else {
                // Find the mood entity for the selected date
                val moodEntity = moodDates.value.find {
                    it.date == selectedDate.value.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
                }
                // Display the MoodDate component if a mood entity is found!!
                if (moodEntity != null) {
                    MoodDate(
                        mood = moodEntity,
                        viewModel = moodViewModel,
                        selectedDate = selectedDate
                    )
                } else {
                    // Display a message if no mood entry is found for the selected date
                    Text(
                        text = "No mood entry for the selected date.",
                        fontStyle = FontStyle.Italic,
                        color = Color.Gray,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}



    //-------------------------------the calendar -----------------------------------------
    @Composable
    fun CalendarBox(
        currentMonth: YearMonth, moodDates: List<MoodEntity>,
        onMonthChange: (YearMonth) -> Unit, selectedDate: MutableState<LocalDate>,
        onDayChange: (LocalDate) -> Unit
    ) {
        // Date formatter to convert LocalDate to String
        val dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
        val selectedDateString = remember { mutableStateOf("") }
        //empty state for the selected date for the first time


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFC19EE0))
                .border(
                    width = 1.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Month Navigation Row------------------
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Previous Month Button---------
                    IconButton(onClick = {
                        onMonthChange(currentMonth.minusMonths(1)) // Pass updated month to parent
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Month",
                            tint = Color.Black
                        )
                    }
                    // Current Month/Year Display
                    Text(
                        text = "${currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${currentMonth.year}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Black
                    )
                    // Next Month Button
                    IconButton(onClick = {
                        onMonthChange(currentMonth.plusMonths(1)) // Pass updated month to parent
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Month",
                            tint = Color.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp)) // Adds space below the month navigation

                // Weekday Headers------------------------------------------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                        Text(
                            text = day,
                            fontSize = 16.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Calendar Grid, date and circles----------------------------------
                LazyVerticalGrid(

                    columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {

                    // Get the number of days in the current month and the first day of the week
                    val daysInMonth = currentMonth.lengthOfMonth()
                    val firstDayOfWeek = LocalDate.of(
                        currentMonth.year,
                        currentMonth.month,
                        1
                    ).dayOfWeek.value % 7

                    // Add empty cells for the first week
                    items(firstDayOfWeek) {
                        Box(modifier = Modifier.aspectRatio(1f)) // Empty cell
                    }

                    // Add day cells
                    items(daysInMonth) { index ->
                        val calendarDate =
                            LocalDate.of(currentMonth.year, currentMonth.month, index + 1)
                        val dateSelectedString =
                            calendarDate.format(dateFormatter) //checked correct format
                        val today = LocalDate.now(ZoneId.systemDefault())

                        // Check if this day is selected matches the selected date
                        val isSelected = selectedDateString.value == dateSelectedString

                        Box(
                            modifier = Modifier
                                .aspectRatio(0.7f)
                                .clickable {
                                    if (calendarDate.isBefore(today) || calendarDate.isEqual(
                                            today
                                        )
                                    ) {
                                        selectedDate.value = calendarDate
                                        selectedDateString.value = dateSelectedString
                                        onDayChange(calendarDate)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .aspectRatio(0.7f)
                                    .clickable {
                                        if (calendarDate.isBefore(today) || calendarDate.isEqual(
                                                today
                                            )
                                        ) {
                                            selectedDate.value = calendarDate
                                            selectedDateString.value = dateSelectedString
                                            onDayChange(calendarDate)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(0.8f)
                                        .clickable {
                                            if (calendarDate.isBefore(today) || calendarDate.isEqual(
                                                    today
                                                )
                                            ) {
                                                selectedDate.value = calendarDate
                                                selectedDateString.value = dateSelectedString
                                                onDayChange(calendarDate)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {

                                        // Display the circles and emojis on the calendar---------------

                                        val mood =
                                            moodDates.find { it.date == dateSelectedString }
                                        if (mood != null) {
                                            //id from the emoji list needs to correspond to the emojiId in the mood entity
                                            val emoji = emojiList.find { it.id == mood.emojiId }

                                            if (emoji != null) {
                                                Image(
                                                    painter = painterResource(id = emoji.drawableResId),
                                                    contentDescription = "Emoji",
                                                    modifier = Modifier.size(35.dp)

                                                        .border(
                                                            width = if (isSelected) 4.dp else if (calendarDate.isEqual(
                                                                    today
                                                                )
                                                            ) 2.dp else 1.dp,
                                                            color = if (isSelected || calendarDate.isEqual(
                                                                    today
                                                                )
                                                            ) Color.Green else Color.Black,
                                                            shape = CircleShape

                                                        )
                                                )
                                            }
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(35.dp)
                                                    .clip(CircleShape)
                                                    .border(
                                                        width = if (isSelected) 4.dp else if (calendarDate.isEqual(
                                                                today
                                                            )
                                                        ) 2.dp else 1.dp,
                                                        color = if (isSelected || calendarDate.isEqual(
                                                                today
                                                            )
                                                        ) Color.Green else Color.Black,
                                                        shape = CircleShape
                                                    )
                                                    .background(
                                                        when {
                                                            calendarDate.isAfter(today) -> Color.LightGray // Future dates
                                                            else -> Color.White // Unselected dates
                                                        }
                                                    )
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${calendarDate.dayOfMonth}",
                                            fontSize = 14.sp,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


