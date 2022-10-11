package com.ayd.counter

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ayd.counter.service.ServiceHelper
import com.ayd.counter.service.StopCounterService
import com.ayd.counter.service.StopCounterState
import com.ayd.counter.ui.theme.Blue
import com.ayd.counter.ui.theme.Light
import com.ayd.counter.ui.theme.Red
import com.ayd.counter.util.Constants.ACTION_SERVICE_CANCEL
import com.ayd.counter.util.Constants.ACTION_SERVICE_START
import com.ayd.counter.util.Constants.ACTION_SERVICE_STOP

@ExperimentalAnimationApi
@Composable
fun MainScreen(stopCounterService: StopCounterService){
    val context = LocalContext.current
    val hours by stopCounterService.hours
    val minutes by stopCounterService.minutes
    val seconds by stopCounterService.seconds
    val currentState by stopCounterService.currentState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
            .padding(30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(weight = 9f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(targetState = hours, transitionSpec = {addAnim()}) {
                Text(
                    text = hours,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.h1.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = if(hours=="00") Color.White else Blue
                    )
                )
            }

            AnimatedContent(targetState = minutes, transitionSpec = {addAnim()}) {
                Text(
                    text = minutes,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.h1.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = if(minutes=="00") Color.White else Blue
                    )
                )

            }

            AnimatedContent(targetState = seconds, transitionSpec = {addAnim()}) {
                Text(
                    text = seconds,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.h1.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = if(seconds=="00") Color.White else Blue
                    )
                )

            }

        }

        Row(modifier = Modifier.weight(weight = 1f)) {
            Button(modifier = Modifier
                .weight(1f)
                .fillMaxHeight(0.8f),
                onClick =  {
                    ServiceHelper.triggerForegroundService(
                        context = context,
                        action = if(currentState == StopCounterState.Started) ACTION_SERVICE_STOP
                        else ACTION_SERVICE_START
                    )
                }, colors = ButtonDefaults.buttonColors(
                    backgroundColor = if(currentState == StopCounterState.Started) Red else Blue,
                    contentColor = Color.White
                )
                ) {
                    Text(
                        text = if (currentState == StopCounterState.Started) "Stop"
                        else if ((currentState == StopCounterState.Stopped)) "Resume"
                        else "Start"
                    )
            }
            Spacer(modifier = Modifier.width(30.dp))
            Button(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.8f),
                onClick = {
                    ServiceHelper.triggerForegroundService(
                        context = context, action = ACTION_SERVICE_CANCEL
                    )
                },
                enabled = seconds != "00" && currentState != StopCounterState.Started,
                colors = ButtonDefaults.buttonColors(disabledBackgroundColor = Light)
            ) {
                Text(text = "Cancel")
            }

        }

    }

}

@ExperimentalAnimationApi
fun addAnim(duration: Int = 600): ContentTransform{
    return slideInVertically(animationSpec = tween(durationMillis = duration)){height -> height}+ fadeIn(
    animationSpec = tween(durationMillis = duration)
    ) with slideOutVertically(animationSpec = tween(durationMillis = duration)){height -> height}+ fadeOut(
        animationSpec = tween(durationMillis = duration)
    )
}