package dev.bananaumai.suburi2.avocado

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.future
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

class Avocado {
    companion object {
        private val tag = Avocado::class.java.simpleName
        val color = "green"
    }

    private val job = Job()
    private val scope = CoroutineScope(job)

    protected fun finalize() {
        job.cancel()
        Log.d(tag, "Avocado was disposed")
    }

    fun doSomethingAsync() {
        scope.launch {
            delay(5000)
            Log.d(tag, "doSomethingAsync is done")
        }
    }

    fun doSomethingAsyncCompletable(): CompletableFuture<String> {
        return scope.future {
            delay(5000)
            "doSomethingAsyncCompletable is done"
        }
    }
}
