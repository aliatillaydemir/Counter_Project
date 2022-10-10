package com.ayd.counter

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ayd.counter.ui.theme.CounterTheme
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalAnimationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var bound by mutableStateOf(false)
    private lateinit var stopcounterService: StopCounterService

    private val connection = object: ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder = service as StopCounterService.StopCounterBinder
            stopcounterService = binder.getService()
            bound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            bound = false
        }
    }

    override fun onStart() {
        super.onStart()

        Intent(this, StopCounterService::class.java).also {
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CounterTheme {
                if(bound){
                    MainScreen(stopcounterService = stopcounterService)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        bound = false
    }


}


