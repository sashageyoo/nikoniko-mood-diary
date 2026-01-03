package com.example.moodtracker

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import java.util.Calendar
import java.util.Locale

val NikoFontFamily = FontFamily(Font(R.font.skynight_fontlot, FontWeight.Normal))
val CreamBg = Color(0xFFFEFAE0)
val PinkAccent = Color(0xFFFFAFCC)
val GreenCard = Color(0xFFF1F5D6)
val DarkBrown = Color(0xFF5A483C)

@Composable
fun HomeScreen(
    name: String,
    profileImageUri: Uri?,
    habits: List<Habit>,
    moodList: List<MoodEntry>,
    onHabitClick: (Habit) -> Unit,
    onAddMoodClick: () -> Unit,
    onProfileClick: () -> Unit,
    onMenuClick: () -> Unit,
    onMoodCardClick: () -> Unit
) {
    // 1. MOOD HARI ININYAH APAH
    val todayDateString = remember {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) ?: "January"
        val year = calendar.get(Calendar.YEAR)
        "$day $month $year"
    }

    val todaysMood = remember(moodList) {
        moodList.firstOrNull { it.date == todayDateString }
    }

    // 2. FILTER HABIT
    val activeHabits = remember(habits) { habits.filter { !it.isCompleted } }
    val completedHabits = remember(habits) { habits.filter { it.isCompleted } }

    // LOGIKA PROGRESS BAR
    val totalPoints = habits.sumOf { it.points }
    val currentPoints = habits.filter { it.isCompleted }.sumOf { it.points }
    val progressPercentage = if (totalPoints > 0) currentPoints.toFloat() / totalPoints.toFloat() else 0f
    val progressInt = (progressPercentage * 100).toInt()

    // MASCOT MOOD ALIAS SI NIKO
    val mascotIcon = when {
        progressInt >= 80 -> R.drawable.img_niko_veryhappy
        progressInt >= 60 -> R.drawable.img_niko_happy
        progressInt >= 40 -> R.drawable.img_niko_okay
        progressInt >= 20 -> R.drawable.img_niko_sad
        progressInt >= 1 -> R.drawable.img_niko_exhausted
        else -> R.drawable.img_niko_dead
    }

    // STATE BUAT POPUP DIALOG
    var selectedHabit by remember { mutableStateOf<Habit?>(null) }
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().background(CreamBg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            // A. PROGRESS BAR & MASCOT
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painter = painterResource(id = R.drawable.img_habit_plant), contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.width(24.dp).height(120.dp).clip(RoundedCornerShape(12.dp)).background(Color.White).border(1.dp, Color.Black, RoundedCornerShape(12.dp)), contentAlignment = Alignment.BottomCenter) {
                        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(progressPercentage).background(PinkAccent))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$progressInt%", fontFamily = NikoFontFamily, fontSize = 12.sp, color = DarkBrown)
                }
                Spacer(modifier = Modifier.weight(1f))
                Image(painter = painterResource(id = mascotIcon), contentDescription = "Niko Mood", modifier = Modifier.size(120.dp))
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // B. HEADER PROFIL
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Hello, $name!", fontSize = 20.sp, fontFamily = NikoFontFamily, fontWeight = FontWeight.Bold, color = DarkBrown)
                    Text("How are you feeling?", fontSize = 12.sp, color = DarkBrown, fontFamily = NikoFontFamily) // Ganti DarkBrown
                }
                Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(25.dp)).background(Color.White).clickable { onProfileClick() }, contentAlignment = Alignment.Center) {
                    if (profileImageUri != null) AsyncImage(model = profileImageUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    else Image(painter = painterResource(id = R.drawable.baseline_person_24), contentDescription = null, modifier = Modifier.size(30.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // C. MY MOOD TUDEYYY
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("My Mood Today", fontSize = 20.sp, fontFamily = NikoFontFamily, fontWeight = FontWeight.Bold, color = DarkBrown)
                    Text(todayDateString, fontSize = 12.sp, color = DarkBrown, fontFamily = NikoFontFamily)
                }
                Text(">", fontSize = 24.sp, fontFamily = NikoFontFamily, color = DarkBrown, modifier = Modifier.clickable { onMoodCardClick() })
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (todaysMood == null) {
                // BELUM ISI MOOD (ISI BANG)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.LightGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("How was your day?", fontFamily = NikoFontFamily, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkBrown)
                            Text("Track your mood now", fontFamily = NikoFontFamily, fontSize = 12.sp, color = DarkBrown)
                        }
                        IconButton(
                            onClick = onAddMoodClick,
                            modifier = Modifier
                                .size(48.dp)
                                .background(PinkAccent, CircleShape)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_add_reaction_24),
                                contentDescription = "Add Mood",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            } else {
                // SUDAH ISI MOOD JD KEK GINIH
                Box(modifier = Modifier.clickable { onMoodCardClick() }) {
                    MoodHistoryCardSimple(todaysMood)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // D. HABITS
            Text("My Habits Activity", fontSize = 20.sp, fontFamily = NikoFontFamily, fontWeight = FontWeight.Bold, color = DarkBrown)
            Spacer(modifier = Modifier.height(12.dp))
            if (activeHabits.isEmpty() && completedHabits.isNotEmpty()) {
                Text("All habits completed! Good job!", fontFamily = NikoFontFamily, color = DarkBrown, fontSize = 12.sp)
            } else if (activeHabits.isEmpty()) {
                Text("No habits found.", fontFamily = NikoFontFamily, color = DarkBrown, fontSize = 12.sp)
            } else {
                activeHabits.forEach { habit ->
                    HabitItem(habit = habit, onClick = { selectedHabit = habit })
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // COMPLETED HABITS
            Text("My Completed Habits", fontSize = 20.sp, fontFamily = NikoFontFamily, fontWeight = FontWeight.Bold, color = DarkBrown)
            Spacer(modifier = Modifier.height(12.dp))
            if (completedHabits.isEmpty()) {
                Card(colors = CardDefaults.cardColors(containerColor = GreenCard), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 80.dp)) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("niko is still dead :(, complete\nyour habits to make niko alive!", fontFamily = NikoFontFamily, fontSize = 12.sp, color = DarkBrown, textAlign = TextAlign.Center)
                    }
                }
            } else {
                completedHabits.forEach { habit ->
                    HabitItem(habit = habit, onClick = { selectedHabit = habit })
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        //  POPUP DIALOG
        if (selectedHabit != null) {
            HabitDetailDialog(
                habit = selectedHabit!!,
                onDismiss = { selectedHabit = null },
                onComplete = { updatedHabit ->
                    onHabitClick(updatedHabit)
                    selectedHabit = null
                }
            )
        }
    }
}

@Composable
fun MoodHistoryCardSimple(entry: MoodEntry) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GreenCard),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = getMoodIconRes(entry.moodIndex)),
                contentDescription = null,
                modifier = Modifier.size(45.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.width(90.dp)
            ) {
                // Weather
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val weather = entry.weather ?: ""
                    Image(
                        painter = painterResource(id = getWeatherIconRes(weather)),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (weather.isNotEmpty()) weather else "-",
                        fontFamily = NikoFontFamily,
                        fontSize = 11.sp,
                        color = DarkBrown,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Social
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = DarkBrown, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = entry.social?.replace(",", ", ") ?: "-",
                        fontFamily = NikoFontFamily,
                        fontSize = 10.sp,
                        color = DarkBrown.copy(alpha = 0.8f),
                        lineHeight = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .padding(vertical = 4.dp)
                    .background(DarkBrown.copy(alpha = 0.2f))
            )
            Spacer(modifier = Modifier.width(8.dp))

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Teks Thoughts
                Column(modifier = Modifier.weight(1f)) {
                    Text("Thoughts", fontFamily = NikoFontFamily, fontSize = 9.sp, color = DarkBrown.copy(alpha = 0.5f))
                    Text(
                        text = if (entry.notes.isNotEmpty()) entry.notes else "-",
                        fontFamily = NikoFontFamily,
                        fontSize = 11.sp,
                        color = DarkBrown,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 13.sp
                    )
                }

                if (entry.imageUri != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    AsyncImage(
                        model = Uri.parse(entry.imageUri),
                        contentDescription = "Img",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, DarkBrown.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}
@Composable
fun HabitItem(habit: Habit, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GreenCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = habit.iconRes),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    habit.title,
                    fontSize = 16.sp,
                    fontFamily = NikoFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                )
                Text(
                    habit.description,
                    fontSize = 10.sp,
                    fontFamily = NikoFontFamily,
                    color = DarkBrown
                )
            }

            if (habit.isCompleted) {
                Icon(Icons.Default.Check, contentDescription = "Done", tint = PinkAccent, modifier = Modifier.size(24.dp))
            } else {
                Image(painter = painterResource(id = R.drawable.img_habit_plant), contentDescription = null, modifier = Modifier.size(16.dp))
                Text("+${habit.points}", fontSize = 14.sp, fontFamily = NikoFontFamily, fontWeight = FontWeight.Bold, color = DarkBrown)
            }

            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Detail",
                tint = DarkBrown.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HabitDetailDialog(habit: Habit, onDismiss: () -> Unit, onComplete: (Habit) -> Unit) {
    var isCompletedTemp by remember { mutableStateOf(habit.isCompleted) }
    val reminders = remember { mutableStateListOf<String>().apply {
        if(habit.reminders.isNotEmpty()) addAll(habit.reminders.split(","))
    }}
    val context = LocalContext.current

    val displayedStreak = if (isCompletedTemp && !habit.isCompleted) habit.streak + 1
    else if (!isCompletedTemp && habit.isCompleted) (if(habit.streak > 0) habit.streak - 1 else 0)
    else habit.streak

    val statusChanged = isCompletedTemp != habit.isCompleted
    val remindersChanged = reminders.joinToString(",") != habit.reminders

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = CreamBg), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                // Header
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = DarkBrown, modifier = Modifier.clickable { onDismiss() })
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painter = painterResource(id = R.drawable.img_habit_plant), contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Unspecified)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+${habit.points}", fontFamily = NikoFontFamily, fontWeight = FontWeight.Bold, color = DarkBrown)
                    }
                }

                Text(habit.title, fontFamily = NikoFontFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkBrown)
                Spacer(modifier = Modifier.height(16.dp))
                Image(painter = painterResource(id = habit.iconRes), contentDescription = null, modifier = Modifier.size(80.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(habit.description, fontFamily = NikoFontFamily, fontSize = 12.sp, color = DarkBrown, textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(24.dp))

                Text("Status", fontFamily = NikoFontFamily, fontSize = 12.sp, color = DarkBrown)
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(if (isCompletedTemp) GreenCard else Color.White).border(1.dp, DarkBrown, RoundedCornerShape(12.dp)).clickable { isCompletedTemp = !isCompletedTemp }, contentAlignment = Alignment.Center) {
                }
                Text(
                    text = if (isCompletedTemp) "completed!" else "not yet completed :(",
                    fontFamily = NikoFontFamily,
                    fontSize = 12.sp,
                    color = DarkBrown
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text("Your Habit Streak", fontFamily = NikoFontFamily, fontSize = 12.sp, color = DarkBrown)
                Card(colors = CardDefaults.cardColors(containerColor = GreenCard), modifier = Modifier.padding(top = 8.dp)) {
                    Column(modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$displayedStreak", fontFamily = NikoFontFamily, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkBrown)
                        Text("Total Day", fontFamily = NikoFontFamily, fontSize = 10.sp, color = DarkBrown)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Your Reminder Set", fontFamily = NikoFontFamily, fontSize = 12.sp, color = DarkBrown, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    reminders.forEach { time ->
                        Box(modifier = Modifier.padding(bottom = 8.dp).height(30.dp).border(1.dp, DarkBrown, RoundedCornerShape(8.dp)).background(Color.White, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp), contentAlignment = Alignment.Center) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(time, fontFamily = NikoFontFamily, fontSize = 12.sp, color = DarkBrown)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(12.dp).clickable { reminders.remove(time) }, tint = DarkBrown)
                            }
                        }
                    }
                    Box(modifier = Modifier.height(30.dp).border(1.dp, DarkBrown, RoundedCornerShape(8.dp)).clickable {
                        val cal = Calendar.getInstance()
                        TimePickerDialog(context, { _, hour, minute ->
                            val amPm = if (hour >= 12) "PM" else "AM"; val hour12 = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
                            val minStr = if (minute < 10) "0$minute" else "$minute"; val hourStr = if (hour12 < 10) "0$hour12" else "$hour12"
                            val timeString = "$hourStr:$minStr $amPm"
                            reminders.add(timeString)
                            scheduleHabitNotification(context, timeString, habit.title)
                        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
                    }.padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp), tint = DarkBrown)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                val buttonText = when { !isCompletedTemp && !statusChanged && !remindersChanged -> "Complete!"; statusChanged || remindersChanged -> "Save Changes"; else -> "Save" }
                Button(onClick = {
                    val finalStatus = if (!isCompletedTemp && !statusChanged && !remindersChanged) true else isCompletedTemp
                    val newStreak = if (finalStatus && !habit.isCompleted) habit.streak + 1 else if (!finalStatus && habit.isCompleted) (if(habit.streak > 0) habit.streak - 1 else 0) else habit.streak
                    val newTotalCount = if (finalStatus && !habit.isCompleted) habit.totalCount + 1 else if (!finalStatus && habit.isCompleted) (if (habit.totalCount > 0) habit.totalCount - 1 else 0) else habit.totalCount
                    val newDate = if (finalStatus) System.currentTimeMillis() else habit.lastCompletedDate

                    onComplete(habit.copy(isCompleted = finalStatus, reminders = reminders.joinToString(","), streak = newStreak, totalCount = newTotalCount, lastCompletedDate = newDate))
                }, colors = ButtonDefaults.buttonColors(containerColor = PinkAccent), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth().height(50.dp)) {
                    Text(buttonText, fontFamily = NikoFontFamily, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

fun scheduleHabitNotification(context: Context, timeString: String, habitTitle: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, HabitNotificationReceiver::class.java).apply {
        putExtra("HABIT_TITLE", habitTitle)
    }

    val requestCode = (habitTitle + timeString).hashCode()

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val sdf = java.text.SimpleDateFormat("hh:mm aa", java.util.Locale.ENGLISH)
    val date = sdf.parse(timeString)
    val calNow = java.util.Calendar.getInstance()
    val calAlarm = java.util.Calendar.getInstance()

    if (date != null) {
        calAlarm.time = date
        calAlarm.set(java.util.Calendar.YEAR, calNow.get(java.util.Calendar.YEAR))
        calAlarm.set(java.util.Calendar.MONTH, calNow.get(java.util.Calendar.MONTH))
        calAlarm.set(java.util.Calendar.DAY_OF_MONTH, calNow.get(java.util.Calendar.DAY_OF_MONTH))

        if (calAlarm.before(calNow)) {
            calAlarm.add(java.util.Calendar.DAY_OF_MONTH, 1)
        }

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calAlarm.timeInMillis, pendingIntent)
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calAlarm.timeInMillis, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calAlarm.timeInMillis, pendingIntent)
            }
            Toast.makeText(context, "Reminder set for $timeString", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(context, "Permission Error: Cek Manifest", Toast.LENGTH_SHORT).show()
        }
    }
}