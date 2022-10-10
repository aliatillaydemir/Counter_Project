package com.ayd.counter.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import com.ayd.counter.MainActivity
import com.ayd.counter.util.Constants.CLICK_REQUEST_CODE
import com.ayd.counter.util.Constants.STOPCOUNTER_STATE

@ExperimentalAnimationApi
object ServiceHelper {

    private val flag =
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE
        else 0

    fun clickPendingIntent(context: Context): PendingIntent{
        val clickIntent = Intent(context,MainActivity::class.java)
        return PendingIntent.getActivity(
            context,CLICK_REQUEST_CODE, clickIntent, flag
        )
    }

    fun stopPendingIntent(context: Context): PendingIntent{
        val intentStop = Intent(context, StopCounterService::class.java).apply {
            putExtra(STOPCOUNTER_STATE,StopCounterState.Stopped.name)
        }
    }


}