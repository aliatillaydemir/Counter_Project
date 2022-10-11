package com.ayd.counter.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import com.ayd.counter.MainActivity
import com.ayd.counter.util.Constants.CANCEL_REQUEST_CODE
import com.ayd.counter.util.Constants.CLICK_REQUEST_CODE
import com.ayd.counter.util.Constants.RESUME_REQUEST_CODE
import com.ayd.counter.util.Constants.STOP_COUNTER_STATE
import com.ayd.counter.util.Constants.STOP_REQUEST_CODE

@ExperimentalAnimationApi
object ServiceHelper {

    private val flag =
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE
        else 0

    fun clickPendingIntent(context: Context): PendingIntent{
        val clickIntent = Intent(context,MainActivity::class.java).apply {
            putExtra(STOP_COUNTER_STATE, StopCounterState.Started.name)
        }
        return PendingIntent.getActivity(
            context,CLICK_REQUEST_CODE, clickIntent, flag
        )
    }

    fun stopPendingIntent(context: Context): PendingIntent{
        val intentStop = Intent(context, StopCounterService::class.java).apply {
            putExtra(STOP_COUNTER_STATE,StopCounterState.Stopped.name)
        }
        return PendingIntent.getService(context, STOP_REQUEST_CODE,intentStop, flag)
    }

    fun resumePendingIntent(context: Context): PendingIntent{
        val resumeIntent = Intent(context, StopCounterService::class.java).apply {
            putExtra(STOP_COUNTER_STATE, StopCounterState.Started.name)
        }
        return PendingIntent.getService(context, RESUME_REQUEST_CODE,resumeIntent,flag)
    }


    fun cancelPendingIntent(context: Context): PendingIntent{
        val cancelIntent = Intent(context, StopCounterService::class.java).apply {
            putExtra(STOP_COUNTER_STATE,StopCounterState.Canceled.name)
        }
        return PendingIntent.getService(context, CANCEL_REQUEST_CODE,cancelIntent,flag)
    }

    fun triggerForegroundService(context: Context, action: String){
        Intent(context, StopCounterService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }


}

