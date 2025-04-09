package com.example.myapplication

import android.app.Application
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.*
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.TravelViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import com.example.myapplication.viewmodel.TravelViewModel
import com.example.myapplication.data.JournalEntryEntity
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.compose.foundation.background


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val viewModel: TravelViewModel = viewModel(
                    factory = TravelViewModelFactory(context.applicationContext as Application)
                )


                NavHost(navController = navController, startDestination = Screen.Welcome.route) {
                    composable(Screen.Welcome.route) { WelcomeScreen(navController) }
                    composable(Screen.MainMenu.route) { MainMenuScreen(navController) }
                    composable(Screen.StartJournal.route) { TravelJournalScreen(viewModel, navController) }
                    composable(Screen.DisplayJournals.route) { DisplayJournalsScreen(viewModel, navController) }
                    composable("edit_journal/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                        if (id != null) {
                            EditJournalScreen(navController, id)
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun WelcomeScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.img_background_1_0197copy),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Welcome to your daily Journal", style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate(Screen.MainMenu.route) }) { Text("Start") }
        }
    }
}

@Composable
fun MainMenuScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_2_image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = { navController.navigate(Screen.StartJournal.route) }) { Text("Start Journal") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { navController.navigate(Screen.DisplayJournals.route) }) { Text("Display Journals") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { navController.popBackStack() }) { Text("Return") }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelJournalScreen(viewModel: TravelViewModel, navController: NavController) {
    val context = LocalContext.current
    var journalText by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedMood by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text("How are you feeling today?", color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            val moods = listOf("😄", "😊", "😐", "😢", "😠")

            Row {
                moods.forEach { mood ->
                    Text(
                        text = mood,
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable { selectedMood = mood }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedMood.isNotEmpty()) Color.Green else Color.Gray
                )
            ) {
                Text(text = if (selectedMood.isNotEmpty()) "Mood Selected" else "Pick a Mood")
            }

            Spacer(modifier = Modifier.height(24.dp))

            var userName by remember { mutableStateOf("") }
            var userLocation by remember { mutableStateOf("") }

            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Enter your name", color = Color.White) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = userLocation,
                onValueChange = { userLocation = it },
                label = { Text("Enter your location", color = Color.White) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = journalText,
                onValueChange = { journalText = it },
                label = { Text("Write your journal entry here...", color = Color.White) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { launcher.launch("image/*") }) {
                Text("Pick Image")
            }

            Spacer(modifier = Modifier.height(16.dp))

            imageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (journalText.isNotBlank()) {
                        val date = SimpleDateFormat("MMMM d, yyyy 'at' h:mm a", Locale.getDefault()).format(Date())
                        val entry = JournalEntryEntity(
                            text = journalText,
                            imageUrl = imageUri?.toString() ?: "",
                            date = date,
                            mood = selectedMood,
                            name = userName,
                            location = userLocation,
                        )
                        viewModel.addEntry(entry)
                        Toast.makeText(context, "Journal saved!", Toast.LENGTH_SHORT).show()
                        journalText = ""
                        imageUri = null
                        selectedMood = ""
                    } else {
                        Toast.makeText(context, "Please write something!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Save Entry")
            }

            Spacer(modifier = Modifier.height(48.dp))
        }

        Button(
            onClick = { navController.navigate(Screen.MainMenu.route) },
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 360.dp)
        ) {
            Text("Return")
        }
    }
}

@Composable
fun DisplayJournalsScreen(viewModel: TravelViewModel, navController: NavController) {
    val entries by viewModel.allJournalEntries.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_3_generatedimage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            LazyColumn {
                items(entries) { entry ->
                    JournalEntryItem(entry, navController) { viewModel.deleteEntry(entry) }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Button(
                onClick = { navController.navigate(Screen.MainMenu.route) },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text("Return")
            }
        }
    }
}

@Composable
fun JournalEntryItem(entry: JournalEntryEntity, navController: NavController, onDelete: () -> Unit)
 {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp)
    ) {
        if (entry.name.isNotEmpty() || entry.location.isNotEmpty()) {
            Text(
                text = "${entry.name} • ${entry.location}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
        }

        Text(text = entry.text)


        if (entry.mood.isNotEmpty()) {
            Text(
                text = "Mood: ${entry.mood}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (entry.imageUrl.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(entry.imageUrl),
                contentDescription = "Journal Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        }

        Text(
            text = "🕒 ${entry.date}",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { showDialog = true }) {
                Text("Delete")
            }

            Button(onClick = {
                navController.navigate("edit_journal/${entry.id}")
            }) {
                Text("Edit")
            }
        }

        // ✅ Delete Confirmation Dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Confirm Deletion") },
                text = { Text("Are you sure you want to delete this entry?") },
                confirmButton = {
                    Button(onClick = {
                        onDelete()
                        showDialog = false
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}


@Composable
fun EditJournalScreen(navController: NavController, journalId: Int) {
    val context = LocalContext.current
    val viewModel: TravelViewModel = viewModel(
        factory = TravelViewModelFactory(context.applicationContext as Application)
    )

    val entry: JournalEntryEntity? = viewModel.getJournalById(journalId).collectAsState(initial = null).value

    var updatedText by remember { mutableStateOf("") }
    var updatedMood by remember { mutableStateOf("") }

    // Pre-fill text when loaded
    LaunchedEffect(entry) {
        entry?.let {
            updatedText = it.text
            updatedMood = it.mood
        }
    }

    entry?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Edit Journal", color = Color.White)

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = updatedText,
                onValueChange = { updatedText = it },
                label = { Text("Update your entry", color = Color.White) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = updatedMood,
                onValueChange = { updatedMood = it },
                label = { Text("Mood", color = Color.White) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                viewModel.updateEntry(
                    oldEntry = it,
                    newEntry = it.copy(text = updatedText, mood = updatedMood)
                )
                Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }) {
                Text("Save Edit")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = { navController.popBackStack() }) {
                Text("Cancel")
            }
        }
    } ?: run {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading entry... :)", color = Color.White)
        }
    }
}



    // Optional Greeting preview
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android") /*This might need some improvements */
    }
}

