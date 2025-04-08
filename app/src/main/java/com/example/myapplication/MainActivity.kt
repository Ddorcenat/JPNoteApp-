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
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val viewModel: TravelViewModel = viewModel()

                NavHost(navController = navController, startDestination = Screen.Welcome.route) {
                    composable(Screen.Welcome.route) { WelcomeScreen(navController) }
                    composable(Screen.MainMenu.route) { MainMenuScreen(navController) }
                    composable(Screen.StartJournal.route) { TravelJournalScreen(viewModel, navController) }
                    composable(Screen.DisplayJournals.route) { DisplayJournalsScreen(viewModel, navController) }
                }
            }
        }
    }
}

// Data model
data class JournalEntry(
    val text: String,
    val imageUrl: String = "",
    val date: String = ""
)

// ViewModel
class TravelViewModel(application: Application) : AndroidViewModel(application) {
    var entries = mutableStateOf(listOf<JournalEntry>())
        private set

    fun addEntry(entry: JournalEntry) {
        entries.value = entries.value + entry
    }

    fun deleteEntry(entry: JournalEntry) {
        entries.value = entries.value - entry
    }

    fun updateEntry(oldEntry: JournalEntry, newEntry: JournalEntry) {
        entries.value = entries.value.map {
            if (it == oldEntry) newEntry else it
        }
    }
}

// Screens
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

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp)
    ) {
        Column {
            OutlinedTextField(
                value = journalText,
                onValueChange = { journalText = it },
                label = { Text("Write your journal entry here...", color = Color.White) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { launcher.launch("image/*") }) { Text("Pick Image") }

            Spacer(modifier = Modifier.height(8.dp))

            imageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (journalText.isNotBlank()) {
                    val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                    val entry = JournalEntry(text = journalText, imageUrl = imageUri?.toString() ?: "", date = date)
                    viewModel.addEntry(entry)
                    Toast.makeText(context, "Journal saved!", Toast.LENGTH_SHORT).show()
                    journalText = ""
                    imageUri = null
                } else {
                    Toast.makeText(context, "Please write something!", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Save Entry")
            }
        }

        Button(onClick = { navController.navigate(Screen.MainMenu.route) }, modifier = Modifier.align(Alignment.BottomCenter)) {
            Text("Return")
        }
    }
}

@Composable
fun DisplayJournalsScreen(viewModel: TravelViewModel, navController: NavController) {
    val entries by viewModel.entries

    Box(modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp)) {
        LazyColumn {
            items(entries) { entry ->
                JournalEntryItem(entry) { viewModel.deleteEntry(entry) }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Button(onClick = { navController.navigate(Screen.MainMenu.route) }, modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)) {
            Text("Return")
        }
    }
}

@Composable
fun JournalEntryItem(entry: JournalEntry, onDelete: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().background(Color.White).padding(8.dp)
    ) {
        Text(text = entry.text)
        if (entry.imageUrl.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(entry.imageUrl),
                contentDescription = "Journal Image",
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.Crop
            )
        }
        Text(text = entry.date, style = MaterialTheme.typography.labelSmall)
        Button(onClick = onDelete) { Text("Delete") }
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
        Greeting("Android")
    }
}
