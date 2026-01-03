package com.example.moodtracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moodtracker.ui.theme.MoodTrackerTheme
import kotlinx.coroutines.delay
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SETUP BUAT NOTIF CENELNYA
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "habit_channel", "Habit Reminders", NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Channel for habit alarms" }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        setContent {
            MoodTrackerTheme {
                val viewModel: MoodViewModel = viewModel()
                val moodHistoryList by viewModel.moodList.collectAsState()
                val habitsList by viewModel.habitList.collectAsState()

                val navController = rememberNavController()
                val context = LocalContext.current

                // 1. SETUP PENYIMPANAN
                val sharedPreferences = remember {
                    context.getSharedPreferences("MoodTrackerPrefs", Context.MODE_PRIVATE)
                }

                // 2. STATE DATA
                var userName by remember { mutableStateOf(sharedPreferences.getString("KEY_NAME", "") ?: "") }
                var userBirthDate by remember { mutableStateOf(sharedPreferences.getString("KEY_DATE", "") ?: "") }
                var userGender by remember { mutableStateOf(sharedPreferences.getString("KEY_GENDER", "") ?: "") }

                val savedImageString = sharedPreferences.getString("KEY_PROFILE_IMAGE", null)
                var userProfileUri by remember {
                    mutableStateOf(if (savedImageString != null) Uri.parse(savedImageString) else null)
                }

                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        if (isGranted) {
                            Toast.makeText(context, "Notifikasi Diizinkan!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Mohon izinkan notifikasi agar alarm berjalan", Toast.LENGTH_LONG).show()
                        }
                    }
                )

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val permission = android.Manifest.permission.POST_NOTIFICATIONS
                        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                            notificationPermissionLauncher.launch(permission)
                        }
                    }
                }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentRoute == "home" || currentRoute == "profile" || currentRoute == "history") {

                            val pinkAccent = Color(0xFFFFAFCC)
                            val darkBrown = Color(0xFF5A483C)

                            NavigationBar(
                                containerColor = pinkAccent, // UBAH BACKGROUND JADI PINK
                                contentColor = darkBrown,
                                tonalElevation = 8.dp
                            ) {
                                // 1. HOME ITEM
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            painterResource(id = R.drawable.img_home),
                                            contentDescription = "Home",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = { Text("Home", fontFamily = NikoFontFamily, fontWeight = FontWeight.Bold) },
                                    selected = currentRoute == "home",
                                    onClick = {
                                        navController.navigate("home") {
                                            popUpTo("home") { inclusive = true }
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = darkBrown,
                                        selectedTextColor = darkBrown,
                                        indicatorColor = Color.White.copy(alpha = 0.4f), // Indikator Putih Transparan
                                        unselectedIconColor = darkBrown.copy(alpha = 0.5f),
                                        unselectedTextColor = darkBrown.copy(alpha = 0.5f)
                                    )
                                )

                                // 2. ADD MOOD
                                NavigationBarItem(
                                    icon = {
                                        Box(
                                            modifier = Modifier
                                                .size(52.dp)
                                                .shadow(4.dp, CircleShape)
                                                .background(Color.White, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.baseline_add_reaction_24),
                                                contentDescription = "Add Mood",
                                                tint = pinkAccent,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    },
                                    label = { },
                                    selected = false,
                                    onClick = { navController.navigate("add_mood") },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color.Transparent
                                    )
                                )
                                // 3. PROFILE ITEM
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            painterResource(id = R.drawable.baseline_person_24),
                                            contentDescription = "Profile",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    label = { Text("Profile", fontFamily = NikoFontFamily, fontWeight = FontWeight.Bold) },
                                    selected = currentRoute == "profile",
                                    onClick = {
                                        navController.navigate("profile") {
                                            popUpTo("home") { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = darkBrown,
                                        selectedTextColor = darkBrown,
                                        indicatorColor = Color.White.copy(alpha = 0.4f),
                                        unselectedIconColor = darkBrown.copy(alpha = 0.5f),
                                        unselectedTextColor = darkBrown.copy(alpha = 0.5f)
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // 1. SPLASH SCREEN
                        composable("splash") {
                            SplashScreen(
                                onTimeout = {
                                    val nextRoute = if (userName.isNotEmpty()) "home" else "onboarding_slides"
                                    navController.navigate(nextRoute) {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 2. ONBOARDING SLIDES
                        composable("onboarding_slides") {
                            OnBoardingPage(
                                onNavigateToHome = {
                                    navController.navigate("user_input") {
                                        popUpTo("onboarding_slides") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 3. USER INPUT
                        composable("user_input") {
                            UserInputScreen(
                                onSaveData = { name, date, gender ->
                                    userName = name
                                    userBirthDate = date
                                    userGender = gender

                                    val editor = sharedPreferences.edit()
                                    editor.putString("KEY_NAME", name)
                                    editor.putString("KEY_DATE", date)
                                    editor.putString("KEY_GENDER", gender)
                                    editor.apply() // Save!

                                    navController.navigate("home") {
                                        popUpTo("user_input") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 4. HOME SCREEN
                        composable("home") {
                            HomeScreen(
                                name = userName,
                                profileImageUri = userProfileUri,
                                habits = habitsList,
                                moodList = moodHistoryList,
                                onHabitClick = { habit ->
                                    viewModel.checkAndResetStreak(habit)
                                    viewModel.updateHabit(habit)
                                },
                                onAddMoodClick = { navController.navigate("add_mood") },
                                onProfileClick = { navController.navigate("profile") },
                                onMenuClick = { },
                                onMoodCardClick = { navController.navigate("history") }
                            )
                        }

                        // 5. ADD MOOD SCREEN
                        composable("add_mood") {
                            AddMoodScreen(
                                onBack = { navController.popBackStack() },
                                onSave = { entry ->
                                    viewModel.addMood(entry)
                                    Toast.makeText(context, "Mood Saved!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                            )
                        }


                        // 6. PROFILE SCREEN
                        composable("profile") {
                            ProfileScreen(
                                name = userName,
                                birthDate = userBirthDate,
                                gender = userGender,
                                profileImageUri = userProfileUri,
                                historyList = moodHistoryList,
                                habitsList = habitsList,
                                onDelete = { viewModel.deleteMood(it) },
                                onEditProfile = { newName, newDate, newGender, newUri ->
                                    userName = newName
                                    userBirthDate = newDate
                                    userGender = newGender

                                    if (newUri != null && newUri != userProfileUri) {
                                        val savedUriString = saveImageToInternalStorage(context, newUri)
                                        userProfileUri = savedUriString?.let { Uri.parse(it) }

                                        val editor = sharedPreferences.edit()
                                        editor.putString("KEY_PROFILE_IMAGE", savedUriString)
                                        editor.apply()
                                    } else {

                                    }

                                    val editor = sharedPreferences.edit()
                                    editor.putString("KEY_NAME", newName)
                                    editor.putString("KEY_DATE", newDate)
                                    editor.putString("KEY_GENDER", newGender)
                                    editor.apply()
                                }
                            )
                        }
                        // 7. HISTORY SCREEN
                        composable("history") {
                            MoodHistoryScreen(
                                historyList = moodHistoryList,
                                onBack = { navController.popBackStack() },
                                onDelete = { viewModel.deleteMood(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val creamBg = Color(0xFFFEFAE0)

    LaunchedEffect(Unit) {
        delay(2500)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(creamBg),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.nikoniko),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "NikoNiko",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5A483C) // DarkBrown
            )
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator(
                color = Color(0xFFFFAFCC), // PinkAccent
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun UserInputScreen(onSaveData: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var expandedGender by remember { mutableStateOf(false) }
    val genderOptions = listOf("Laki-laki", "Perempuan")

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val darkBrown = Color(0xFF5A483C)

    fun showDatePicker() {
        android.app.DatePickerDialog(
            context,
            { _, year, month, day ->
                birthDate = "$day/${month + 1}/$year"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEFAE0))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Tell us about you",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = darkBrown,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Panggilan") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = darkBrown,
                    unfocusedBorderColor = darkBrown,
                    focusedLabelColor = darkBrown,
                    unfocusedLabelColor = darkBrown,
                    cursorColor = darkBrown,
                    focusedTextColor = darkBrown,
                    unfocusedTextColor = darkBrown
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth().noRippleClickable { showDatePicker() }) {
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = {},
                    label = { Text("Tanggal Lahir") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        Icon(painterResource(id = android.R.drawable.ic_menu_my_calendar), null, tint = darkBrown)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = darkBrown,
                        disabledBorderColor = darkBrown,
                        disabledLabelColor = darkBrown,
                        disabledTrailingIconColor = darkBrown
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    label = { Text("Jenis Kelamin") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().noRippleClickable { expandedGender = true },
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        Icon(painterResource(id = android.R.drawable.arrow_down_float), null, tint = darkBrown)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = darkBrown,
                        disabledBorderColor = darkBrown,
                        disabledLabelColor = darkBrown,
                        disabledTrailingIconColor = darkBrown
                    )
                )

                DropdownMenu(
                    expanded = expandedGender,
                    onDismissRequest = { expandedGender = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    genderOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = darkBrown) },
                            onClick = { gender = option; expandedGender = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            val isButtonEnabled = name.isNotEmpty() && birthDate.isNotEmpty() && gender.isNotEmpty()

            Button(
                onClick = { onSaveData(name, birthDate, gender) },
                enabled = isButtonEnabled,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFAFCC),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFD3B8AE),
                    disabledContentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Start Tracking!", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) { onClick() }
}
fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileName = "img_${System.currentTimeMillis()}.jpg"
        val file = java.io.File(context.filesDir, fileName)
        val outputStream = java.io.FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        android.net.Uri.fromFile(file).toString()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}