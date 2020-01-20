package dev.bananaumai.singleton_sample

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// see note https://developer.android.com/reference/android/app/Application
// also see
// * https://stackoverflow.com/questions/42853189/android-singleton-instance-vs-service
// * https://stackoverflow.com/questions/35649033/bind-service-from-singleton
class Foo {
    companion object {
        private const val TAG = "Foo"

        private val job = Job()
        private val scope = CoroutineScope(job)

        @Volatile
        private var isRunning = false

        fun run() {
            if (!isRunning) {
                scope.launch {
                    while(true) {
                        delay(1000L)
                        Log.d(TAG, "hello")
                    }
                }
                Log.i(TAG, "started to run")
                isRunning = true
            } else {
                Log.i(TAG, "already run")
            }
        }
    }
}
