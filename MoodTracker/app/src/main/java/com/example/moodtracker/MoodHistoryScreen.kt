package com.example.moodtracker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun MoodHistoryScreen(
    historyList: List<MoodEntry>,
    onBack: () -> Unit,
    onDelete: (MoodEntry) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(CreamBg)) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkBrown)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mood List", fontFamily = NikoFontFamily, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkBrown)
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (historyList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No mood history yet.\nAdd one!",
                        fontFamily = NikoFontFamily,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
                    items(historyList) { entry ->
                        MoodHistoryCard(entry, onDelete = { onDelete(entry) })
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        Image(
            painter = painterResource(id = R.drawable.nikoniko),
            contentDescription = null,
            modifier = Modifier.align(Alignment.BottomEnd).offset(x = 20.dp, y = 20.dp).size(100.dp)
        )
    }
}

@Composable
fun MoodHistoryCard(entry: MoodEntry, onDelete: (() -> Unit)? = null) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(entry.date, fontFamily = NikoFontFamily, fontSize = 12.sp, color = Color.Gray)

            if (onDelete != null) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp).clickable { onDelete() }
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(GreenCard)
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painter = painterResource(id = getMoodIconRes(entry.moodIndex)), contentDescription = null, modifier = Modifier.size(50.dp))
                Text(getMoodLabel(entry.moodIndex), fontFamily = NikoFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkBrown)
            }

            Column(modifier = Modifier.weight(1.5f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Weather
                    val weatherLabel = entry.weather ?: "-"
                    MiniIconColumn(
                        iconRes = getWeatherIconRes(weatherLabel),
                        label = weatherLabel
                    )
                    val socialList = entry.social?.split(",") ?: emptyList()

                    socialList.take(2).forEach { socialLabel ->
                        MiniIconColumn(
                            iconRes = getSocialIconRes(socialLabel.trim()),
                            label = socialLabel.trim()
                        )
                    }
                }
            }

            Column(modifier = Modifier.weight(1.5f)) {
                Box(modifier = Modifier.fillMaxWidth().height(60.dp).background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(8.dp)).padding(8.dp)) {
                    Text(
                        text = if(entry.notes.isNotEmpty()) entry.notes else "No notes.",
                        fontFamily = NikoFontFamily, fontSize = 10.sp, color = DarkBrown,
                        maxLines = 3, overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (entry.imageUri != null && entry.imageUri != "null") {
                        AsyncImage(
                            model = entry.imageUri,
                            contentDescription = "Mood Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(painter = painterResource(id = R.drawable.nikoniko), contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MiniIconColumn(iconRes: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(30.dp).background(Color.Gray.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
            Image(painter = painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(18.dp))
        }
        Text(label, fontFamily = NikoFontFamily, fontSize = 8.sp, color = DarkBrown, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}