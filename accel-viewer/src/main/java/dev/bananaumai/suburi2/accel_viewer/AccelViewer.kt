package dev.bananaumai.suburi2.accel_viewer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select

@ExperimentalCoroutinesApi
class AccelViewer : AppCompatActivity() {
    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accel_viewer)

        lifecycleScope.launch {
            val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            val delay = SensorManager.SENSOR_DELAY_NORMAL
            val handler = Handler(mainLooper)
            sensorEventFlow(sensor, sensorManager, delay, handler).collect { event ->
                Log.d("SensorEvent", "receive $event")
            }
        }
    }
}

@ExperimentalCoroutinesApi
fun sensorEventFlow(sensor: Sensor, manager: SensorManager, delay: Int, handler: Handler): Flow<SensorEvent> {
    return channelFlow<SensorEvent> {
        val side = Channel<SensorEvent>()
        launch {
            for (event in side) {
                // do something here
            }
        }
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == sensor.type) {
                    launch {
                        select<Unit> {
                            onSend(event) {}
                            side.onSend(event) {}
                        }
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // do something
            }
        }
        try {
            manager.registerListener(listener, sensor, delay, handler)
            awaitClose()
        } finally {
            side.close()
            manager.unregisterListener(listener, sensor)
        }
    }
}
