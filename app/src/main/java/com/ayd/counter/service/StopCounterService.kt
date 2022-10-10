package com.ayd.counter.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.ayd.counter.util.Constants.ACTION_SERVICE_CANCEL
import com.ayd.counter.util.Constants.ACTION_SERVICE_START
import com.ayd.counter.util.Constants.ACTION_SERVICE_STOP
import com.ayd.counter.util.Constants.NOTIFICATION_CHANNEL_ID
import com.ayd.counter.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.ayd.counter.util.Constants.NOTIFICATION_ID
import com.ayd.counter.util.Constants.STOPCOUNTER_STATE
import com.ayd.counter.util.formatTime
import com.ayd.counter.util.pad
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


@ExperimentalAnimationApi
@AndroidEntryPoint
class StopCounterService: Service(){

    @Inject
    lateinit var notificationManager: NotificationManager
    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    private val binder = StopCounterService()

    private var duration: Duration = Duration.ZERO
    private lateinit var timer: Timer

    var seconds = mutableStateOf("00")
        private set
    var minutes = mutableStateOf("00")
        private set
    var hours = mutableStateOf("00")
        private set
    var currentState = mutableStateOf(StopCounterState.Idle)
        private set

    override fun onBind(p0: Intent?) = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.getStringExtra(STOPCOUNTER_STATE)) {
            StopCounterState.Started.name -> {
                setStopButton()
                startForegroundService()
                startStopCounter { hours, minutes, seconds ->
                    updateNotification(hours = hours, minutes = minutes, seconds = seconds)
                }
            }

            StopCounterState.Stopped.name -> {
            stopStopCounter()
            setResumeButton()
            }

            StopCounterState.Canceled.name -> {
                stopStopCounter()
                cancelStopCounter()
                stopForegroundService()
            }

        }

        intent?.action.let {
            when(it){
                ACTION_SERVICE_START -> {
                    setStopButton()
                    startForegroundService()
                    startStopCounter { hours, minutes, seconds ->
                        updateNotification(hours = hours, minutes = minutes, seconds = seconds)
                    }
                }
                ACTION_SERVICE_STOP -> {
                    stopStopCounter()
                    setResumeButton()
                }
                ACTION_SERVICE_CANCEL -> {
                    stopStopCounter()
                    cancelStopwatch()
                    stopForegroundService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
            }

    private fun startStopCounter(onTick: (h: String, m: String, s: String) -> Unit) {
        currentState.value = StopwatchState.Started
        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            duration = duration.plus(1.seconds)
            updateTimeUnits()
            onTick(hours.value, minutes.value, seconds.value)
        }
    }

    private fun stopStopCounter() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        currentState.value = StopwatchState.Stopped
    }

    private fun cancelStopwatch() {
        duration = Duration.ZERO
        currentState.value = StopwatchState.Idle
        updateTimeUnits()
    }

    private fun updateTimeUnits() {
        duration.toComponents { hours, minutes, seconds, _ ->
            this@StopCounterService.hours.value = hours.toInt().pad()
            this@StopCounterService.minutes.value = minutes.pad()
            this@StopCounterService.seconds.value = seconds.pad()
        }
    }

    private fun startForegroundService() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun stopForegroundService() {
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun updateNotification(hours: String, minutes: String, seconds: String) {
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentText(
                formatTime(
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds,
                )
            ).build()
        )
    }

    private fun setStopButton() {
        notificationBuilder.mActions.removeAt(0)
        notificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0,
                "Stop",
                ServiceHelper.stopPendingIntent(this)
            )
        )
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun setResumeButton() {
        notificationBuilder.mActions.removeAt(0)
        notificationBuilder.mActions.add(
            0,
            NotificationCompat.Action(
                0,
                "Resume",
                ServiceHelper.resumePendingIntent(this)
            )
        )
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    inner class StopwatchBinder : Binder() {
        fun getService(): StopCounterService = this@StopCounterService
    }
}

enum class StopwatchState {
    Idle,
    Started,
    Stopped,
    Canceled
}