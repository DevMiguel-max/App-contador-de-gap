package com.example.gapcounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gapcounter.ui.CounterScreen
import com.example.gapcounter.ui.theme.GapCounterTheme
import androidx.compose.runtime.getValue

class MainActivity : ComponentActivity() {

    // ViewModel com escopo da Activity — sem memory leaks
    private val viewModel: CounterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GapCounterTheme {
                // Coleta o StateFlow de forma segura com o ciclo de vida
                val state by viewModel.state.collectAsStateWithLifecycle()

                CounterScreen(
                    state        = state,
                    onStart      = viewModel::start,
                    onPause      = viewModel::pause,
                    onReset      = viewModel::reset,
                    onIncrement  = viewModel::increment,
                    onDecrement  = viewModel::decrement,
                    modifier     = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                )
            }
        }
    }
}
