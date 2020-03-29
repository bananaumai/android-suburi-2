package dev.bananaumai.suburi.shared_pref

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private val tag = "Bnn"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch(CoroutineName("ROOT") + Dispatchers.Default) {
            val sharedPref = getSharedPreferences("test", Context.MODE_PRIVATE)

            sharedPref.edit {
                clear()
                putString("foo", "bar")
                commit()
            }

            Log.d(tag, "[${Thread.currentThread().name}][${coroutineContext[CoroutineName]}] sharedPref is init with ${sharedPref.all}")

            val j1 = launch(CoroutineName("SharedPrefReader") + Dispatchers.Default) {
                delay(100)
                Log.d(tag, "[${Thread.currentThread().name}][${coroutineContext[CoroutineName]}] ${sharedPref.all}")
                delay(100)
                Log.d(tag, "[${Thread.currentThread().name}][${coroutineContext[CoroutineName]}] ${sharedPref.all}")
            }

            val j2 = launch(CoroutineName("SharedPrefWriter") + Dispatchers.Default) {
                sharedPref.edit {
                    clear()
                    putString("bar", "buzz")
                    Log.d(tag, "[${Thread.currentThread().name}][${coroutineContext[CoroutineName]}] clear and put")
                    delay(100)
                    commit()
                    Log.d(tag, "[${Thread.currentThread().name}][${coroutineContext[CoroutineName]}] commit")
                }
            }

            joinAll(j1, j2)

            val len = 100
            updateSharedPref(sharedPref, len)

            Log.d(tag, "[${Thread.currentThread().name}][${coroutineContext[CoroutineName]}] sharedPref is now ${sharedPref.all}")

            val orig = sharedPref.all

            launch(CoroutineName("SharedPrefReader2") + Dispatchers.Default) {
                repeat(100000) {
                    val kvs = sharedPref.all
                    if (kvs != orig) {
                        Log.e(tag, "[${Thread.currentThread().name}][${coroutineContext[CoroutineName]}] oops : $kvs")
                    }
                }
                Log.d(tag, "[${Thread.currentThread().name}][${coroutineContext[CoroutineName]}] done")
            }

            launch(CoroutineName("SharedPrefWriter2") + Dispatchers.Default) {
                repeat(1000) {
                    updateSharedPref(sharedPref, len)
                }
                Log.d(tag, "[${Thread.currentThread().name}][${coroutineContext[CoroutineName]}] done")
            }
        }
    }

    private fun updateSharedPref(sharedPref: SharedPreferences, len: Int) {
        sharedPref.edit {
            clear()
            repeat (len) {
                putString("k_$it", "v_$it")
            }
            commit()
        }
    }
}
