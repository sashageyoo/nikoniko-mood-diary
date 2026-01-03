package com.example.moodtracker

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.min

val BarColor1 = Color(0xFFD0D0D0)
val BarColor2 = Color(0xFF90CAF9)
val BarColor3 = Color(0xFF80DEEA)
val BarColor4 = Color(0xFF80CBC4)
val BarColor5 = Color(0xFFA5D6A7)
val BlueGender = Color(0xFF6495ED)

@Composable
fun ProfileScreen(
    name: String,
    birthDate: String,
    gender: String,
    profileImageUri: Uri? = null,
    historyList: List<MoodEntry>,
    habitsList: List<Habit>,
    onDelete: (MoodEntry) -> Unit,
    onEditProfile: (String, String, String, Uri?) -> Unit
) {
    val scrollState = rememberScrollState()
    var showEditDialog by remember { mutableStateOf(false) }

    // LOGIKA HITUNGANNYA

    // 1. MOOD SCORE
    val moodWeights = listOf(0, 3, 6, 8, 10)
    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

    fun getMoodsInLast7Days(): List<MoodEntry> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -7)
        val limitDate = cal.time
        return historyList.filter { entry ->
            try {
                val entryDate = sdf.parse(entry.date)
                entryDate != null && (entryDate.after(limitDate) || entryDate == limitDate)
            } catch (e: Exception) { false }
        }
    }

    fun getMoodsInCurrentMonth(): List<MoodEntry> {
        val currentCal = Calendar.getInstance()
        val currentMonth = currentCal.get(Calendar.MONTH)
        val currentYear = currentCal.get(Calendar.YEAR)

        return historyList.filter { entry ->
            try {
                val entryDate = sdf.parse(entry.date)
                if (entryDate != null) {
                    val entryCal = Calendar.getInstance()
                    entryCal.time = entryDate
                    entryCal.get(Calendar.MONTH) == currentMonth &&
                            entryCal.get(Calendar.YEAR) == currentYear
                } else false
            } catch (e: Exception) { false }
        }
    }

    fun calculateAverageScore(entries: List<MoodEntry>): Float {
        if (entries.isEmpty()) return 0f
        val totalScore = entries.sumOf { moodWeights[it.moodIndex] }
        return totalScore.toFloat() / entries.size
    }

    val moodsLast7Days = getMoodsInLast7Days()
    val moodsCurrentMonth = getMoodsInCurrentMonth()

    val avgScoreWeek = calculateAverageScore(moodsLast7Days)
    val avgScoreMonth = calculateAverageScore(moodsCurrentMonth)
    val avgScoreAllTime = calculateAverageScore(historyList)

    val weeklyLabel = when {
        moodsLast7Days.isEmpty() -> "-"
        avgScoreWeek >= 8.5 -> "Happy!"
        avgScoreWeek >= 6.5 -> "Good"
        avgScoreWeek >= 4.5 -> "Okay"
        avgScoreWeek >= 2.5 -> "Sad"
        else -> "Terrible"
    }

    // 2. HABIT PROGRESS
    val totalHabits = habitsList.size
    val maxWeeklyStreak = totalHabits * 7
    val maxMonthlyStreak = totalHabits * 30

    val currentWeeklyStreak = habitsList.sumOf { min(it.streak, 7) }
    val currentMonthlyStreak = habitsList.sumOf { min(it.streak, 30) }
    val currentAllTimeStreak = habitsList.sumOf { it.streak }

    val weeklyProgress = if (maxWeeklyStreak > 0) currentWeeklyStreak.toFloat() / maxWeeklyStreak.toFloat() else 0f
    val monthlyProgress = if (maxMonthlyStreak > 0) currentMonthlyStreak.toFloat() / maxMonthlyStreak.toFloat() else 0f

    val weeklyProgressInt = (weeklyProgress * 100).toInt()
    val monthlyProgressInt = (monthlyProgress * 100).toInt()
    val allTimeProgressInt = if (habitsList.isNotEmpty()) (currentAllTimeStreak * 2).coerceAtMost(100) else 0

    // 3. MOOD COUNT CHART
    var chartRange by remember { mutableStateOf("Last 30 Days") }
    var showChartDropdown by remember { mutableStateOf(false) }

    fun getMoodsInLast30Days(): List<MoodEntry> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -30)
        val limitDate = cal.time
        return historyList.filter { entry ->
            try {
                val entryDate = sdf.parse(entry.date)
                entryDate != null && (entryDate.after(limitDate) || entryDate == limitDate)
            } catch (e: Exception) { false }
        }
    }

    val chartData = if (chartRange == "Last 7 Days") moodsLast7Days else getMoodsInLast30Days()
    val moodCounts = IntArray(5) { 0 }
    chartData.forEach { moodCounts[it.moodIndex]++ }
    val maxCount = moodCounts.maxOrNull()?.takeIf { it > 0 } ?: 5

    // UI

    Box(modifier = Modifier.fillMaxSize().background(CreamBg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(scrollState)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Profile", fontFamily = NikoFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkBrown)
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = DarkBrown)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // 1. KARTU PROFIL USER
            Card(
                colors = CardDefaults.cardColors(containerColor = GreenCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { showEditDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileImageUri != null) {
                            AsyncImage(model = profileImageUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_person_24),
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(name.ifEmpty { "User" }, fontFamily = NikoFontFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkBrown)
                        Text(birthDate.ifEmpty { "-" }, fontFamily = NikoFontFamily, fontSize = 12.sp, color = DarkBrown.copy(alpha = 0.7f))

                        val genderColor = if (gender.equals("Laki-laki", ignoreCase = true)) BlueGender else PinkAccent
                        Text(gender.ifEmpty { "-" }, fontFamily = NikoFontFamily, fontSize = 14.sp, color = genderColor, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. HABIT PROGRESS
            Text("Habit Progress", fontFamily = NikoFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkBrown)
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = GreenCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Box(modifier = Modifier.height(20.dp).fillMaxWidth()) {
                            Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)).background(Color.White))
                            Box(modifier = Modifier.fillMaxWidth(weeklyProgress).fillMaxHeight().clip(RoundedCornerShape(10.dp)).background(PinkAccent))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("$weeklyProgressInt%", fontFamily = NikoFontFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = DarkBrown)
                        Text("Last 7 days", fontFamily = NikoFontFamily, fontSize = 12.sp, color = DarkBrown.copy(alpha=0.6f))
                    }

                    Image(painter = painterResource(id = R.drawable.img_habit_plant), contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text("This month", fontFamily = NikoFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkBrown)
                        Text("$monthlyProgressInt%", fontFamily = NikoFontFamily, fontSize = 12.sp, color = DarkBrown.copy(alpha=0.6f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("All time", fontFamily = NikoFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkBrown)
                        Text("$allTimeProgressInt%", fontFamily = NikoFontFamily, fontSize = 12.sp, color = DarkBrown.copy(alpha=0.6f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. MOOD SCORE CARD
            Text("Mood Score", fontFamily = NikoFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkBrown)
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = GreenCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val avgIcon = when {
                                avgScoreWeek >= 8.5 -> R.drawable.happy
                                avgScoreWeek >= 6.5 -> R.drawable.good
                                avgScoreWeek >= 4.5 -> R.drawable.okay
                                avgScoreWeek >= 2.5 -> R.drawable.sad
                                else -> R.drawable.terrible
                            }

                            Image(painter = painterResource(id = avgIcon), contentDescription = null, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(String.format("%.1f", avgScoreWeek), fontFamily = NikoFontFamily, fontWeight = FontWeight.Bold, fontSize = 32.sp, color = DarkBrown)
                        }
                        Text(weeklyLabel, fontFamily = NikoFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkBrown)
                        Text("Last 7 days", fontFamily = NikoFontFamily, fontSize = 12.sp, color = DarkBrown.copy(alpha=0.6f))
                    }

                    Column {
                        Text("This month", fontFamily = NikoFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkBrown)
                        Text(String.format("%.1f", avgScoreMonth), fontFamily = NikoFontFamily, fontSize = 12.sp, color = DarkBrown.copy(alpha=0.6f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("All Time", fontFamily = NikoFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkBrown)
                        Text(String.format("%.1f", avgScoreAllTime), fontFamily = NikoFontFamily, fontSize = 12.sp, color = DarkBrown.copy(alpha=0.6f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. MOOD COUNT CHART
            Text("Mood Count", fontFamily = NikoFontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkBrown)
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = GreenCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Box(modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.TopEnd)) {
                        Row(
                            modifier = Modifier.clickable { showChartDropdown = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(chartRange, fontFamily = NikoFontFamily, fontSize = 12.sp, color = DarkBrown.copy(alpha=0.7f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = DarkBrown)
                        }
                        DropdownMenu(
                            expanded = showChartDropdown,
                            onDismissRequest = { showChartDropdown = false },
                            modifier = Modifier.background(CreamBg)
                        ) {
                            DropdownMenuItem(text = { Text("Last 7 Days", fontFamily = NikoFontFamily, color = DarkBrown) }, onClick = { chartRange = "Last 7 Days"; showChartDropdown = false })
                            DropdownMenuItem(text = { Text("Last 30 Days", fontFamily = NikoFontFamily, color = DarkBrown) }, onClick = { chartRange = "Last 30 Days"; showChartDropdown = false })
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.fillMaxHeight().padding(bottom = 24.dp), verticalArrangement = Arrangement.SpaceBetween) {
                            Text("$maxCount", fontSize = 10.sp, color = Color.Gray)
                            Text("${maxCount/2}", fontSize = 10.sp, color = Color.Gray)
                            Text("0", fontSize = 10.sp, color = Color.Gray)
                        }

                        val colors = listOf(BarColor1, BarColor2, BarColor3, BarColor4, BarColor5)
                        for (i in 0..4) {
                            BarItem(value = moodCounts[i], max = maxCount, color = colors[i], iconRes = getMoodIconRes(i))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }

        if (showEditDialog) {
            EditProfileDialog(
                currentName = name,
                currentBirthDate = birthDate,
                currentGender = gender,
                currentImageUri = profileImageUri,
                onDismiss = { showEditDialog = false },
                onSave = { newName, newDate, newGender, newUri ->
                    onEditProfile(newName, newDate, newGender, newUri)
                    showEditDialog = false
                }
            )
        }
    }
}

@Composable
fun RowScope.BarItem(value: Int, max: Int, color: Color, iconRes: Int) {
    Column(
        modifier = Modifier.weight(1f).fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (value > 0) {
                Text(value.toString(), fontSize = 10.sp, color = DarkBrown, fontFamily = NikoFontFamily)
                Spacer(modifier = Modifier.height(4.dp))

                val heightFraction = (value.toFloat() / max.toFloat()).coerceIn(0.05f, 1f)
                Box(
                    modifier = Modifier
                        .width(30.dp)
                        .fillMaxHeight(heightFraction)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(color)
                )
            } else {
                Box(modifier = Modifier.width(30.dp).height(2.dp).background(Color.Gray.copy(alpha=0.3f)))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Gray))
        Spacer(modifier = Modifier.height(8.dp))
        Image(painter = painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(24.dp))
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentBirthDate: String,
    currentGender: String,
    currentImageUri: Uri?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, Uri?) -> Unit
) {
    var tempName by remember { mutableStateOf(currentName) }
    var tempDate by remember { mutableStateOf(currentBirthDate) }
    var tempGender by remember { mutableStateOf(currentGender) }
    var tempUri by remember { mutableStateOf(currentImageUri) }

    val photoPicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) tempUri = uri
    }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            tempDate = "$dayOfMonth ${getMonthName(month)} $year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CreamBg)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Edit Profile", fontFamily = NikoFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkBrown)
                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.White).clickable {
                    photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }, contentAlignment = Alignment.Center) {
                    if (tempUri != null) {
                        AsyncImage(model = tempUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    } else {
                        Icon(painter = painterResource(id = R.drawable.baseline_person_24), contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(40.dp))
                    }
                }
                Text("Change Photo", fontSize = 10.sp, color = PinkAccent, modifier = Modifier.padding(top = 4.dp), fontFamily = NikoFontFamily)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkBrown,
                        unfocusedBorderColor = DarkBrown,
                        focusedLabelColor = DarkBrown,
                        unfocusedLabelColor = DarkBrown,
                        cursorColor = DarkBrown,
                        focusedTextColor = DarkBrown,
                        unfocusedTextColor = DarkBrown
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tempDate,
                    onValueChange = {},
                    label = { Text("Birth Date") },
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, null,
                            Modifier.clickable { datePickerDialog.show() },
                            tint = DarkBrown
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkBrown,
                        unfocusedBorderColor = DarkBrown,
                        focusedLabelColor = DarkBrown,
                        unfocusedLabelColor = DarkBrown,
                        disabledTextColor = DarkBrown,
                        disabledBorderColor = DarkBrown,
                        disabledLabelColor = DarkBrown,
                        disabledTrailingIconColor = DarkBrown
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = tempGender == "Laki-laki",
                        onClick = { tempGender = "Laki-laki" },
                        colors = RadioButtonDefaults.colors(selectedColor = PinkAccent, unselectedColor = DarkBrown)
                    )
                    Text("Laki-laki", fontSize = 12.sp, color = DarkBrown, fontFamily = NikoFontFamily)

                    Spacer(modifier = Modifier.width(8.dp))

                    RadioButton(
                        selected = tempGender == "Perempuan",
                        onClick = { tempGender = "Perempuan" },
                        colors = RadioButtonDefaults.colors(selectedColor = PinkAccent, unselectedColor = DarkBrown)
                    )
                    Text("Perempuan", fontSize = 12.sp, color = DarkBrown, fontFamily = NikoFontFamily)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(onClick = { onSave(tempName, tempDate, tempGender, tempUri) },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkAccent), modifier = Modifier.fillMaxWidth()) {
                    Text("Save Changes", color = Color.White, fontFamily = NikoFontFamily)
                }
            }
        }
    }
}