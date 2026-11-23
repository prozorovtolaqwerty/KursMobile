package com.example.weather.ui

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

fun <T> StateFlow<T>.observeState(owner: LifecycleOwner, onEach: (T) -> Unit) {
    owner.lifecycleScope.launch {
        owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collect(onEach)
        }
    }
}
