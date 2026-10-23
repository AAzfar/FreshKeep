package com.personal.freshkeep.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    // 1. Structural Animation Drivers
    val alphaAnimation = remember { Animatable(0f) }
    val scaleAnimation = remember { Animatable(0.6f) }

    // 2. Sequential Logic Execution Module (Fires immediately on app boot)
    LaunchedEffect(key1 = true) {
        // Run concurrent animations over 1000 milliseconds
        launch {
            alphaAnimation.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000)
            )
        }
        launch {
            scaleAnimation.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000)
            )
        }
        
        // Holding delay (1.5 seconds) simulating mainframe system checks / DB initializations
        delay(1500)
        
        // Trigger routing callback to dashboard entry point
        onAnimationFinished()
    }

    // 3. Visual Layout Elements
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Solid white background matching logo design parameters
        contentAlignment = Alignment.Center
    ) {
        // App Launcher Icon Target Image
        Image(
            painter = painterResource(id = com.personal.freshkeep.R.drawable.ic_launcher_foreground),
            contentDescription = "FreshKeep Logo",
            modifier = Modifier
                .size(180.dp)
                .alpha(alphaAnimation.value)
                .scale(scaleAnimation.value)
        )
    }
}
