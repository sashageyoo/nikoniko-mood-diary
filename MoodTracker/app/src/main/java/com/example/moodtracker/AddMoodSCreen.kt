package com.example.moodtracker

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import java.util.Calendar

val BluePastel = Color(0xFFD0F0FD)
val PinkPastel = Color(0xFFFFE5E5)
val InputBoxColor = Color(0xFFFDFDF5)
val TextColorDark = Color(0xFF5A483C)

@Composable
fun AddMoodScreen(
    onBack: () -> Unit,
    onSave: (MoodEntry) -> Unit
) {
    var selectedDate by remember { mutableStateOf("Pick Date") }
    var selectedMoodIndex by remember { mutableIntStateOf(4) } // Default Happy
    var showMoodPopup by remember { mutableStateOf(false) }

    var selectedWeatherLabel by remember { mutableStateOf("") }
    val selectedSocials = remember { mutableStateListOf<String>() }
    var thoughtText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    // ENIH BUAT VALIDASI FORM
    val isFormValid = selectedDate != "Pick Date" &&
            selectedWeatherLabel.isNotEmpty() &&
            selectedSocials.isNotEmpty() &&
            (thoughtText.isNotEmpty() || selectedImageUri != null)

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    val scrollState = rememberScrollState()
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            selectedDate = "$dayOfMonth ${getMonthName(month)} $year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

    // UI UTAMANYA YAAAAAA
    Box(modifier = Modifier.fillMaxSize().background(CreamBg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(scrollState)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextColorDark
                    )
                }

                Text(
                    text = "Save",
                    fontFamily = NikoFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = if (isFormValid) TextColorDark else Color.Gray,
                    modifier = Modifier.clickable(enabled = isFormValid) {
                        // --- LOGIKA SAVE ---
                        val finalImageUri = selectedImageUri?.let { uri ->
                            saveImageToInternalStorage(context, uri)
                        }

                        val socialsString = selectedSocials.joinToString(",")

                        val newEntry = MoodEntry(
                            date = selectedDate,
                            moodIndex = selectedMoodIndex,
                            weather = selectedWeatherLabel,
                            social = socialsString,
                            notes = thoughtText,
                            imageUri = finalImageUri // Simpan URI file internal (String)
                        )
                        onSave(newEntry)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TANGGAL SELECTOR
            Text(
                text = "• $selectedDate",
                fontFamily = NikoFontFamily,
                color = TextColorDark,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { datePickerDialog.show() }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // MOOD SELECTION (ICON BESAR)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = getMoodIconRes(selectedMoodIndex)),
                    contentDescription = "Selected Mood",
                    modifier = Modifier
                        .size(100.dp)
                        .clickable { showMoodPopup = true }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = getMoodLabel(selectedMoodIndex),
                    fontFamily = NikoFontFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp,
                    color = TextColorDark
                )
                Text(
                    text = "(Tap me to change)",
                    fontFamily = NikoFontFamily, fontSize = 12.sp, color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "what makes you feel\nthis mood?",
                fontFamily = NikoFontFamily,
                fontSize = 20.sp,
                color = TextColorDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // WEATHER SECTION
            Text(
                "Weather",
                fontFamily = NikoFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextColorDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BluePastel)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                val weatherOptions = listOf("Sunny", "Cloudy", "Rainy", "Windy", "Snowy")
                weatherOptions.forEach { weather ->
                    OptionCircle(
                        label = weather,
                        iconRes = getWeatherIconRes(weather),
                        isSelected = selectedWeatherLabel == weather,
                        highlightColor = Color(0xFF6495ED),
                        onClick = { selectedWeatherLabel = weather }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SOCIAL SECTION
            Text(
                "Social",
                fontFamily = NikoFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextColorDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PinkPastel)
                    .padding(16.dp)
            ) {
                val socialList1 = listOf("Family", "Friends", "Lover", "Pets", "Work")
                val socialList2 = listOf("Party", "Date", "Travel", "Nature", "Self")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    socialList1.forEach { item ->
                        OptionCircle(
                            label = item,
                            iconRes = getSocialIconRes(item),
                            isSelected = selectedSocials.contains(item),
                            highlightColor = Color(0xFFFFAFCC),
                            onClick = {
                                if (selectedSocials.contains(item)) selectedSocials.remove(item) else selectedSocials.add(item)
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    socialList2.forEach { item ->
                        OptionCircle(
                            label = item,
                            iconRes = getSocialIconRes(item),
                            isSelected = selectedSocials.contains(item),
                            highlightColor = Color(0xFFFFAFCC),
                            onClick = {
                                if (selectedSocials.contains(item)) selectedSocials.remove(item) else selectedSocials.add(item)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // THOUGHTS INPUT (TEXT + IMAGE)
            Text(
                "What's on your thoughts?",
                fontFamily = NikoFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextColorDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .border(1.dp, TextColorDark, RoundedCornerShape(16.dp))
                    .background(InputBoxColor, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                if (thoughtText.isEmpty() && selectedImageUri == null) {
                    Text(
                        "Write your thoughts here...",
                        color = Color.Gray,
                        fontFamily = NikoFontFamily,
                        fontSize = 14.sp
                    )
                }

                Row(modifier = Modifier.fillMaxSize()) {
                    // INPUT TEXT
                    androidx.compose.foundation.text.BasicTextField(
                        value = thoughtText,
                        onValueChange = { thoughtText = it },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = NikoFontFamily,
                            fontSize = 14.sp,
                            color = TextColorDark
                        ),
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )

                    // PREVIEW IMAGE KALAU ADA
                    if (selectedImageUri != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, TextColorDark, RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Selected Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                Icon(
                    painter = painterResource(id = R.drawable.img_imageicon),
                    contentDescription = "Upload",
                    tint = if (selectedImageUri != null) PinkAccent else Color.Gray,
                    modifier = Modifier.align(Alignment.BottomEnd).size(24.dp).clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                )
            }
            Spacer(modifier = Modifier.height(80.dp))
        }

        Image(
            painter = painterResource(id = R.drawable.nikoniko), contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 20.dp, y = 20.dp)
                .size(120.dp)
        )

        // POP UP SELECTED MOOD
        if (showMoodPopup) {
            MoodSelectionDialog(
                currentSelection = selectedMoodIndex,
                onDismiss = { showMoodPopup = false },
                onSelect = { index ->
                    selectedMoodIndex = index
                    showMoodPopup = false
                }
            )
        }
    }
}
@Composable
fun OptionCircle(
    label: String,
    iconRes: Int,
    isSelected: Boolean,
    highlightColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .size(45.dp)
                .background(
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = if (isSelected) highlightColor else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(28.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontFamily = NikoFontFamily,
            fontSize = 10.sp,
            color = if (isSelected) highlightColor else TextColorDark.copy(alpha = 0.8f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun MoodSelectionDialog(currentSelection: Int, onDismiss: () -> Unit, onSelect: (Int) -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CreamBg),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "How are you today?",
                    fontFamily = NikoFontFamily,
                    fontSize = 16.sp,
                    color = TextColorDark,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val moodLabels = listOf("Terrible", "Sad", "Okay", "Good", "Happy")
                    moodLabels.forEachIndexed { index, label ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = getMoodIconRes(index)),
                                contentDescription = label,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable { onSelect(index) }
                                    .border(
                                        width = if (currentSelection == index) 2.dp else 0.dp,
                                        color = if (currentSelection == index) TextColorDark else Color.Transparent,
                                        shape = CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                label,
                                fontSize = 10.sp,
                                fontFamily = NikoFontFamily,
                                color = TextColorDark
                            )
                        }
                    }
                }
            }
        }
    }
}


fun getMoodLabel(index: Int): String = when (index) {
    0 -> "Terrible"
    1 -> "Sad"
    2 -> "Okay"
    3 -> "Good"
    4 -> "Happy!"
    else -> "Happy"
}

fun getMoodIconRes(index: Int): Int = when (index) {
    0 -> R.drawable.terrible
    1 -> R.drawable.sad
    2 -> R.drawable.okay
    3 -> R.drawable.good
    4 -> R.drawable.happy
    else -> R.drawable.nikoniko
}

fun getWeatherIconRes(label: String): Int = when (label) {
    "Sunny" -> R.drawable.sunny
    "Cloudy" -> R.drawable.cloudy
    "Rainy" -> R.drawable.rainy
    "Windy" -> R.drawable.windy
    "Snowy" -> R.drawable.snow
    else -> R.drawable.nikoniko
}

fun getSocialIconRes(label: String): Int = when (label) {
    "Family" -> R.drawable.family
    "Friends" -> R.drawable.friend
    "Lover" -> R.drawable.lover
    "Pets" -> R.drawable.pets
    "Work" -> R.drawable.work
    "Party" -> R.drawable.party
    "Date" -> R.drawable.date
    "Travel" -> R.drawable.travel
    "Nature" -> R.drawable.nature
    "Self" -> R.drawable.sendiri
    else -> R.drawable.nikoniko
}

fun getMonthName(month: Int): String {
    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    return months.getOrElse(month) { "Jan" }
}