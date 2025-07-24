package com.example.drdo

import android.os.Bundle
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.drdo.data.Attachment
import com.example.drdo.data.Task
import com.example.drdo.ManageWorksViewModel
import com.example.drdo.SetWorksViewModelFactory
import com.example.drdo.ui.theme.DrDoTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.background
import com.example.drdo.scheduleDailyNotification
import com.example.drdo.scheduleDeadlineAlarm
import kotlinx.coroutines.launch
import android.content.pm.PackageManager
import android.provider.Settings
import android.app.AlarmManager
import android.content.Intent
import android.content.Context
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import android.media.MediaPlayer
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.foundation.Image
import android.Manifest
import android.media.MediaRecorder
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import java.io.File
import android.app.Activity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextDecoration
import android.widget.Toast
import android.os.Build
import com.example.drdo.cancelDailyExactNotification
import com.example.drdo.scheduleDailyExactNotification

// Move AttachmentUi data class to top-level
data class AttachmentUi(val type: String, val name: String, val uri: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Request POST_NOTIFICATIONS permission for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
        setContent {
            DrDoTheme {
                val navController = rememberNavController()
                // Check for intent extra to navigate to Manage Works
                LaunchedEffect(Unit) {
                    if (intent?.getStringExtra("navigateTo") == "manage_works") {
                        navController.navigate("manage_works")
                    }
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "entry",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("entry") { EntryScreen(navController) }
                        composable("set_works") { SetWorksScreen(navController) }
                        composable("manage_works") { ManageWorksScreen(navController) }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            // Optionally handle permission result
        }
    }
}

@Composable
fun EntryScreen(navController: NavHostController) {
    val darkBlue = Color(0xFF181B36)
    val green = Color(0xFF19C900)
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF6F7FB), Color(0xFFE9ECF5), Color(0xFFD1D8EC)),
        startY = 0f,
        endY = 1000f
    )
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        // Top content (title, subtitle, author)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Dr. DO",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = darkBlue,
                letterSpacing = 2.sp
            )
            Text(
                text = "Task Manager",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = green,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
                letterSpacing = 1.sp
            )
            Divider(
                color = green,
                thickness = 2.dp,
                modifier = Modifier
                    .width(80.dp)
                    .padding(bottom = 8.dp)
            )
            Text(
                text = "Boost your productivity with easy task setup and management",
                fontSize = 15.sp,
                color = Color(0xFF222B45),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 18.sp)) {
                        append("Made By ")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = green, fontSize = 18.sp)) {
                        append("Deep Rudra")
                    }
                },
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
        // Blue section with buttons at the bottom
        // Place battery optimization prompt above the card section
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clickable {
                        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                        context.startActivity(intent)
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Disable battery optimization",
                    color = green,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp
                )
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Go",
                    tint = green,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(340.dp)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(darkBlue)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(vertical = 32.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { navController.navigate("set_works") },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = green),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp, pressedElevation = 16.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp)
                ) {
                    Text(
                        text = "SET WORK",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { navController.navigate("manage_works") },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = green),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp, pressedElevation = 16.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(56.dp)
                ) {
                    Text(
                        text = "MANAGE WORK",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SetWorksScreen(navController: NavHostController) {
    val green = Color(0xFF19C900)
    val darkBlue = Color(0xFF181B36)
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF6F7FB), Color(0xFFE9ECF5), Color(0xFFD1D8EC)),
        startY = 0f,
        endY = 1000f
    )
    // Context and ViewModel
    val context = LocalContext.current
    val viewModel: SetWorksViewModel = viewModel(
        factory = SetWorksViewModelFactory(context.applicationContext as android.app.Application)
    )
    val scope = rememberCoroutineScope()
    val calendar = remember { Calendar.getInstance() }

    // State variables
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var reminder10Min by remember { mutableStateOf(true) }
    var reminder1Day by remember { mutableStateOf(true) }
    var showAddLinkDialog by remember { mutableStateOf(false) }
    var newLink by remember { mutableStateOf("") }
    var showRecordDialog by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var recorder: MediaRecorder? by remember { mutableStateOf(null) }
    var audioFilePath by remember { mutableStateOf("") }
    var attachments by remember { mutableStateOf(listOf<AttachmentUi>()) }
    // File/image pickers and recordVoice stub (as in SetWorksScreen)
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val name = uri.lastPathSegment ?: "File"
            attachments = attachments + AttachmentUi("file", name, uri.toString())
        }
    }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val name = uri.lastPathSegment ?: "Image"
            attachments = attachments + AttachmentUi("image", name, uri.toString())
        }
    }
    fun recordVoice() {
        isRecording = true
        val fileName = "voice_${System.currentTimeMillis()}.3gp"
        val file = File(context.filesDir, fileName)
        audioFilePath = file.absolutePath
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
    }

    fun startRecording(context: Context) {
        isRecording = true
        val fileName = "voice_${System.currentTimeMillis()}.3gp"
        val file = File(context.filesDir, fileName)
        audioFilePath = file.absolutePath
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
    }
    fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        isRecording = false
        if (audioFilePath.isNotBlank()) {
            attachments = attachments + AttachmentUi("voice", audioFilePath.substringAfterLast('/'), audioFilePath)
        }
    }

    // Helper to get date and time in milliseconds
    fun getDateTimeMillis(): Long? {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return try {
            val dateTime = sdf.parse("$date $time")
            dateTime?.time ?: System.currentTimeMillis() // Fallback to current time if parsing fails
        } catch (e: Exception) {
            null
        }
    }

    // Snackbar for saved task
    var showSavedSnackbar by remember { mutableStateOf(false) }
    LaunchedEffect(showSavedSnackbar) {
        if (showSavedSnackbar) {
            Toast.makeText(context, "Task saved!", Toast.LENGTH_SHORT).show()
            showSavedSnackbar = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top app bar with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = green)
                }
            }
            // Make only the main content scrollable
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                Card(
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = darkBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Set Up a New Task",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = green,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Task Title*", color = green) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = green,
                                unfocusedBorderColor = Color.LightGray,
                                cursorColor = green,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Task Description (optional)", color = green) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = green,
                                unfocusedBorderColor = Color.LightGray,
                                cursorColor = green,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = {
                                    val now = calendar
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            date = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                                            calendar.set(Calendar.YEAR, year)
                                            calendar.set(Calendar.MONTH, month)
                                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                        },
                                        now.get(Calendar.YEAR),
                                        now.get(Calendar.MONTH),
                                        now.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = green),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text(if (date.isEmpty()) "Pick Date" else date, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(
                                onClick = {
                                    val now = calendar
                                    TimePickerDialog(
                                        context,
                                        { _, hour, minute ->
                                            time = String.format("%02d:%02d", hour, minute)
                                            calendar.set(Calendar.HOUR_OF_DAY, hour)
                                            calendar.set(Calendar.MINUTE, minute)
                                        },
                                        now.get(Calendar.HOUR_OF_DAY),
                                        now.get(Calendar.MINUTE),
                                        false
                                    ).show()
                                },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = green),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text(if (time.isEmpty()) "Pick Time" else time, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = reminder10Min, onCheckedChange = { reminder10Min = it }, colors = CheckboxDefaults.colors(checkedColor = green))
                            Text("Alarm 10 minutes before task", color = Color.White)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = reminder1Day, onCheckedChange = { reminder1Day = it }, colors = CheckboxDefaults.colors(checkedColor = green))
                            Text("Use notification", color = Color.White)
                        }
                        // Attachments Section
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Attachments",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = green,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Card for Record Voice
                            Box(
                                modifier = Modifier
                                    .width(140.dp)
                                    .height(48.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(listOf(green, Color(0xFF1DB954))),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        showRecordDialog = true
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Record Voice", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            // Card for Pick File
                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(48.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(listOf(green, Color(0xFF1DB954))),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { filePickerLauncher.launch("application/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Pick File", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            // Card for Pick Image
                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(48.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(listOf(green, Color(0xFF1DB954))),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { imagePickerLauncher.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Pick Image", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            // Card for Add Link
                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(48.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(listOf(green, Color(0xFF1DB954))),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { showAddLinkDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Add Link", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = green)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            if (attachments.isEmpty()) {
                                Text("No attachments added yet.", color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
                            } else {
                                attachments.forEachIndexed { idx, att ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${att.type.capitalize()}: ${att.uri.substringAfterLast('/')}" , color = Color.White, modifier = Modifier.weight(1f))
                                        IconButton(onClick = {
                                            attachments = attachments.filterIndexed { i, _ -> i != idx }
                                        }) {
                                            Icon(Icons.Filled.Delete, contentDescription = "Remove", tint = green)
                                        }
                                    }
                                }
                            }
                        }
                        if (showAddLinkDialog) {
                            AlertDialog(
                                onDismissRequest = { showAddLinkDialog = false },
                                title = { Text("Add Web Link", color = green) },
                                text = {
                                    OutlinedTextField(
                                        value = newLink,
                                        onValueChange = { newLink = it },
                                        label = { Text("URL", color = green) },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = green,
                                            unfocusedBorderColor = Color.LightGray,
                                            cursorColor = green
                                        )
                                    )
                                },
                                confirmButton = {
                                    TextButton(onClick = {
                                        if (newLink.isNotBlank()) {
                                            attachments = attachments + AttachmentUi("link", newLink, newLink)
                                            newLink = ""
                                            showAddLinkDialog = false
                                        }
                                    }) { Text("Add", color = green, fontWeight = FontWeight.Bold) }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showAddLinkDialog = false }) { Text("Cancel", color = Color.Gray) }
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val dateTimeMillis = getDateTimeMillis()
                                if (dateTimeMillis != null) {
                                    val dbAttachments = attachments.map {
                                        Attachment(
                                            id = 0,
                                            taskId = 0,
                                            type = it.type,
                                            uri = it.uri
                                        )
                                    }
                                    scope.launch {
                                        val newTaskId = viewModel.saveTaskWithAttachments(
                                            title = title,
                                            description = description.takeIf { it.isNotBlank() },
                                            dateTime = dateTimeMillis,
                                            reminder10Min = reminder10Min,
                                            reminder1Day = reminder1Day,
                                            attachments = dbAttachments
                                        )
                                        val savedTask = Task(
                                            id = newTaskId,
                                            title = title,
                                            description = description.takeIf { it.isNotBlank() },
                                            dateTime = dateTimeMillis,
                                            reminder10Min = reminder10Min,
                                            reminder1Day = reminder1Day
                                        )
                                        // Check for exact alarm permission every time
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                                            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                            if (!alarmManager.canScheduleExactAlarms()) {
                                                Toast.makeText(
                                                    context,
                                                    "Please enable 'Allow exact alarms' in system settings for reminders to work.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                                context.startActivity(intent)
                                                return@launch
                                            }
                                        }
                                        // Extract hour and minute from the selected time
                                        val calendar = Calendar.getInstance().apply { timeInMillis = dateTimeMillis }
                                        val hour = calendar.get(Calendar.HOUR_OF_DAY)
                                        val minute = calendar.get(Calendar.MINUTE)
                                        scheduleDailyExactNotification(context, savedTask, hour, minute)
                                        scheduleDeadlineAlarm(context, savedTask)
                                        showSavedSnackbar = true
                                        title = ""
                                        description = ""
                                        date = ""
                                        time = ""
                                        reminder10Min = true
                                        reminder1Day = true
                                        attachments = emptyList()
                                    }
                                }
                            },
                            enabled = title.isNotBlank() && date.isNotBlank() && time.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = green),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text("Save Task", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }
    if (showRecordDialog) {
        val activity = context as? Activity
        AlertDialog(
            onDismissRequest = {
                showRecordDialog = false
                if (isRecording) stopRecording()
            },
            title = { Text("Voice Recorder", color = green) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (isRecording) "Recording..." else "Ready to record.", color = Color.White)
                }
            },
            confirmButton = {
                if (!isRecording) {
                    TextButton(onClick = {
                        activity?.let {
                            if (ContextCompat.checkSelfPermission(it, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                startRecording(context)
                                Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show()
                            } else {
                                ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.RECORD_AUDIO), 2001)
                                Toast.makeText(context, "Microphone permission required", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) { Text("Start Recording", color = green, fontWeight = FontWeight.Bold) }
                } else {
                    TextButton(onClick = {
                        stopRecording()
                        showRecordDialog = false
                        Toast.makeText(context, "Recording saved", Toast.LENGTH_SHORT).show()
                    }) { Text("Stop & Save", color = green, fontWeight = FontWeight.Bold) }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    if (isRecording) stopRecording()
                    showRecordDialog = false
                }) { Text("Cancel", color = Color.Gray) }
            },
            containerColor = darkBlue
        )
    }
}

@Composable
fun ManageWorksScreen(navController: NavHostController) {
    val darkBlue = Color(0xFF181B36)
    val green = Color(0xFF19C900)
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF6F7FB), Color(0xFFE9ECF5), Color(0xFFD1D8EC)),
        startY = 0f,
        endY = 1000f
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Top app bar with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 0.dp, end = 0.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = green)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Manage Works",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = green,
                    fontSize = 28.sp,
                )
            }
            val context = LocalContext.current
            val viewModel: ManageWorksViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return ManageWorksViewModel(context.applicationContext as android.app.Application) as T
                    }
                }
            )
            val tasks by viewModel.tasks.collectAsState()
            var showAttachmentsForTaskId by remember { mutableStateOf<Long?>(null) }
            var showDeleteDialogForTaskId by remember { mutableStateOf<Long?>(null) }
            var editTaskId by remember { mutableStateOf<Long?>(null) }
            if (tasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tasks yet! Add a new task to get started.", color = green, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(tasks) { task ->
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = darkBlue),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = task.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = green
                                    )
                                    Text(
                                        text = "Deadline: " + java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(java.util.Date(task.dateTime)),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                                Button(
                                    onClick = { showAttachmentsForTaskId = task.id },
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(containerColor = green),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text("Contents", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                IconButton(onClick = { editTaskId = task.id }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Edit Task", tint = green)
                                }
                                IconButton(onClick = { showDeleteDialogForTaskId = task.id }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete Task", tint = green)
                                }
                            }
                        }
                    }
                }
            }
            // Attachments Dialog
            showAttachmentsForTaskId?.let { taskId ->
                val attachments by viewModel.getAttachmentsForTask(taskId).collectAsState()
                val context = LocalContext.current
                val uriHandler = LocalUriHandler.current
                var playingAudioIndex by remember { mutableStateOf(-1) }
                var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
                AlertDialog(
                    onDismissRequest = {
                        showAttachmentsForTaskId = null
                        mediaPlayer?.release()
                        mediaPlayer = null
                        playingAudioIndex = -1
                    },
                    title = { Text("Task Contents", color = green) },
                    text = {
                        if (attachments.isEmpty()) {
                            Text("No attachments for this task.", color = Color.Gray)
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                itemsIndexed(attachments) { idx, att ->
                                    val displayName = if (att.type == "link") att.uri else att.uri.substringAfterLast('/')
                                    Card(
                                        elevation = CardDefaults.cardElevation(2.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = darkBlue),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            when (att.type) {
                                                "image" -> {
                                                    val imageUri = Uri.parse(att.uri)
                                                    Image(
                                                        painter = rememberAsyncImagePainter(imageUri),
                                                        contentDescription = displayName,
                                                        modifier = Modifier
                                                            .size(48.dp)
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .clickable {
                                                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                                                    setDataAndType(imageUri, "image/*")
                                                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                                }
                                                                context.startActivity(intent)
                                                            },
                                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                                    )
                                                }
                                                "voice" -> {
                                                    Icon(
                                                        painter = painterResource(id = android.R.drawable.ic_btn_speak_now),
                                                        contentDescription = "Audio",
                                                        modifier = Modifier.size(36.dp),
                                                        tint = green
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Button(onClick = {
                                                        mediaPlayer?.release()
                                                        mediaPlayer = MediaPlayer().apply {
                                                            setDataSource(context, Uri.parse(att.uri))
                                                            setOnPreparedListener { start(); playingAudioIndex = idx }
                                                            setOnCompletionListener { playingAudioIndex = -1 }
                                                            prepareAsync()
                                                        }
                                                    }, colors = ButtonDefaults.buttonColors(containerColor = green)) {
                                                        Text(if (playingAudioIndex == idx) "Playing..." else "Play Audio", color = Color.White)
                                                    }
                                                }
                                                "file" -> {
                                                    Icon(
                                                        painter = painterResource(id = android.R.drawable.ic_menu_save),
                                                        contentDescription = "File",
                                                        modifier = Modifier.size(36.dp),
                                                        tint = green
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = displayName,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        color = Color.White,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Button(onClick = {
                                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                                            setDataAndType(Uri.parse(att.uri), "application/*")
                                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                        }
                                                        context.startActivity(intent)
                                                    }, colors = ButtonDefaults.buttonColors(containerColor = green)) {
                                                        Text("Open File", color = Color.White)
                                                    }
                                                }
                                                "link" -> {
                                                    Icon(
                                                        painter = painterResource(id = android.R.drawable.ic_menu_view),
                                                        contentDescription = "Link",
                                                        modifier = Modifier.size(36.dp),
                                                        tint = green
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = displayName,
                                                        color = green,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .clickable {
                                                                val safeUri = if (att.uri.startsWith("http://") || att.uri.startsWith("https://")) att.uri else "https://${att.uri}"
                                                                if (safeUri.isNotBlank()) {
                                                                    uriHandler.openUri(safeUri)
                                                                }
                                                            }
                                                    )
                                                }
                                                else -> {
                                                    Icon(
                                                        painter = painterResource(id = android.R.drawable.ic_menu_help),
                                                        contentDescription = "Other",
                                                        modifier = Modifier.size(36.dp),
                                                        tint = green
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(displayName, color = Color.White)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showAttachmentsForTaskId = null
                            mediaPlayer?.release()
                            mediaPlayer = null
                            playingAudioIndex = -1
                        }) { Text("Close", color = green, fontWeight = FontWeight.Bold) }
                    }
                )
            }
            // Delete Confirmation Dialog
            showDeleteDialogForTaskId?.let { taskId ->
                val task = tasks.find { it.id == taskId }
                if (task != null) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialogForTaskId = null },
                        title = { Text("Delete Task", color = green) },
                        text = { Text("Are you sure you want to delete this task?", color = Color.White) },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.deleteTask(task)
                                showDeleteDialogForTaskId = null
                            }) { Text("Delete", color = green, fontWeight = FontWeight.Bold) }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialogForTaskId = null }) { Text("Cancel", color = Color.Gray) }
                        },
                        containerColor = darkBlue
                    )
                }
            }
            // Edit Task Dialog (unchanged)
            editTaskId?.let { taskId ->
                val task = tasks.find { it.id == taskId }
                val attachments by viewModel.getAttachmentsForTask(taskId).collectAsState()
                if (task != null) {
                    EditTaskDialog(
                        task = task,
                        attachments = attachments,
                        onDismiss = { editTaskId = null },
                        onSave = { updatedTask, updatedAttachments ->
                            // Cancel old alarm
                            cancelDailyExactNotification(context, task)
                            // Schedule new alarm for updated task
                            val calendar = Calendar.getInstance().apply { timeInMillis = updatedTask.dateTime }
                            val hour = calendar.get(Calendar.HOUR_OF_DAY)
                            val minute = calendar.get(Calendar.MINUTE)
                            scheduleDailyExactNotification(context, updatedTask, hour, minute)
                            viewModel.updateTask(updatedTask)
                            // Remove all old attachments and add new ones (simple approach)
                            attachments.forEach { viewModel.deleteAttachment(it) }
                            updatedAttachments.forEach { viewModel.addAttachment(it.copy(taskId = taskId)) }
                            editTaskId = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EditTaskDialog(
    task: Task,
    attachments: List<Attachment>,
    onDismiss: () -> Unit,
    onSave: (Task, List<Attachment>) -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description ?: "") }
    var date by remember { mutableStateOf(java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(task.dateTime))) }
    var time by remember { mutableStateOf(java.text.SimpleDateFormat("HH:mm").format(java.util.Date(task.dateTime))) }
    var reminder10Min by remember { mutableStateOf(task.reminder10Min) }
    var reminder1Day by remember { mutableStateOf(task.reminder1Day) }
    var editAttachments by remember { mutableStateOf(attachments) }
    var showAddLinkDialog by remember { mutableStateOf(false) }
    var newLink by remember { mutableStateOf("") }
    val context = LocalContext.current
    val calendar = remember { java.util.Calendar.getInstance() }

    // File/image pickers and recordVoice stub (as in SetWorksScreen)
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val name = uri.lastPathSegment ?: "File"
            editAttachments = editAttachments + Attachment(0, task.id, "file", uri.toString())
        }
    }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val name = uri.lastPathSegment ?: "Image"
            editAttachments = editAttachments + Attachment(0, task.id, "image", uri.toString())
        }
    }
    fun recordVoice() {
        editAttachments = editAttachments + Attachment(0, task.id, "voice", "dummy_voice_uri")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Task") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title*") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Task Description (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = {
                        val now = calendar
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                date = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                                calendar.set(java.util.Calendar.YEAR, year)
                                calendar.set(java.util.Calendar.MONTH, month)
                                calendar.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth)
                            },
                            now.get(java.util.Calendar.YEAR),
                            now.get(java.util.Calendar.MONTH),
                            now.get(java.util.Calendar.DAY_OF_MONTH)
                        ).show()
                    }) {
                        Text(if (date.isEmpty()) "Pick Date" else date)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = {
                        val now = calendar
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                time = String.format("%02d:%02d", hour, minute)
                                calendar.set(java.util.Calendar.HOUR_OF_DAY, hour)
                                calendar.set(java.util.Calendar.MINUTE, minute)
                            },
                            now.get(java.util.Calendar.HOUR_OF_DAY),
                            now.get(java.util.Calendar.MINUTE),
                            false
                        ).show()
                    }) {
                        Text(if (time.isEmpty()) "Pick Time" else time)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = reminder10Min, onCheckedChange = { reminder10Min = it })
                    Text("Alarm 10 minutes before task")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = reminder1Day, onCheckedChange = { reminder1Day = it })
                    Text("Notification 1 day before task")
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Always show all add buttons and the list, with a placeholder if empty
                Text(
                    text = "Attachments",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Card for Record Voice
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(48.dp)
                            .background(
                                brush = Brush.horizontalGradient(listOf(Color(0xFF43EA7A), Color(0xFF1DB954))),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { recordVoice() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Record Voice", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    // Card for Pick File
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(48.dp)
                            .background(
                                brush = Brush.horizontalGradient(listOf(Color(0xFF43EA7A), Color(0xFF1DB954))),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { filePickerLauncher.launch("application/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Pick File", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    // Card for Pick Image
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(48.dp)
                            .background(
                                brush = Brush.horizontalGradient(listOf(Color(0xFF43EA7A), Color(0xFF1DB954))),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Pick Image", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    // Card for Add Link
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(48.dp)
                            .background(
                                brush = Brush.horizontalGradient(listOf(Color(0xFF43EA7A), Color(0xFF1DB954))),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { showAddLinkDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Add Link", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (editAttachments.isEmpty()) {
                        Text("No attachments added yet.", color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
                    } else {
                        editAttachments.forEachIndexed { idx, att ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${att.type.capitalize()}: ${att.uri}", modifier = Modifier.weight(1f))
                                IconButton(onClick = {
                                    editAttachments = editAttachments.filterIndexed { i, _ -> i != idx }
                                }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Remove")
                                }
                            }
                        }
                    }
                }
                if (showAddLinkDialog) {
                    AlertDialog(
                        onDismissRequest = { showAddLinkDialog = false },
                        title = { Text("Add Web Link") },
                        text = {
                            OutlinedTextField(
                                value = newLink,
                                onValueChange = { newLink = it },
                                label = { Text("URL") },
                                singleLine = true
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                if (newLink.isNotBlank()) {
                                    editAttachments = editAttachments + Attachment(0, task.id, "link", newLink)
                                    newLink = ""
                                    showAddLinkDialog = false
                                }
                            }) { Text("Add") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showAddLinkDialog = false }) { Text("Cancel") }
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                // Convert date and time to millis
                val dateTimeMillis = try {
                    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm")
                    val dt = sdf.parse("$date $time")
                    dt?.time ?: task.dateTime
                } catch (e: Exception) { task.dateTime }
                onSave(
                    task.copy(
                        title = title,
                        description = description.takeIf { it.isNotBlank() },
                        dateTime = dateTimeMillis,
                        reminder10Min = reminder10Min,
                        reminder1Day = reminder1Day
                    ),
                    editAttachments
                )
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}