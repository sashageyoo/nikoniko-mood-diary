package com.example.moodtracker

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class OnboardingPageData(
    @DrawableRes val imageRes: Int,
    val title: String,
    val description: String
)

val onboardingPages = listOf(
    OnboardingPageData(
        imageRes = R.drawable.nikoniko,
        title = "nikoniko!",
        description = "mood diary"
    ),
    OnboardingPageData(
        imageRes = R.drawable.img_nikoniko_slide1,
        title = "hello, i'm niko!",
        description = "i'm here to be your\nmood tracker friend ^^"
    ),
    OnboardingPageData(
        imageRes = R.drawable.img_nikoniko_slide2,
        title = "i keep your mood stay tracked",
        description = "i'll be here to remember\nyour days with you"
    ),
    OnboardingPageData(
        imageRes = R.drawable.img_nikoniko_slide3,
        title = "build your good habits\nwith me!",
        description = "trust me, it's fun and will\nmake you happy!"
    )
)
@Composable
fun OnBoardingPage(onNavigateToHome: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize().background(CreamBg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedContent(
                targetState = currentPage,
                label = "ImageChange"
            ) { pageIndex ->
                Image(
                    painter = painterResource(id = onboardingPages[pageIndex].imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(250.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = onboardingPages[currentPage].title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = NikoFontFamily,
                color = DarkBrown,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = onboardingPages[currentPage].description,
                fontSize = 16.sp,
                fontFamily = NikoFontFamily,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                repeat(onboardingPages.size) { index ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (index == currentPage) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (index == currentPage) PinkAccent else Color.LightGray)
                    )
                }
            }
        }

        Button(
            onClick = {
                if (currentPage < onboardingPages.size - 1) {
                    currentPage++
                } else {
                    onNavigateToHome()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = PinkAccent),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp, start = 24.dp, end = 24.dp)
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = if (currentPage == onboardingPages.size - 1) "Get Started" else "Next",
                fontSize = 18.sp,
                fontFamily = NikoFontFamily,
                color = Color.White
            )
        }
    }
}