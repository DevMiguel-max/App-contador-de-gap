package com.example.gapcounter
 
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.gapcounter.ui.CounterScreen
import com.example.gapcounter.ui.theme.GapCounterTheme
 
class MainActivity : ComponentActivity() {
 
    private val viewModel: CounterViewModel by viewModels()
 
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
 
        setContent {
            GapCounterTheme {
                val state by viewModel.state.collectAsState()
 
                CounterScreen(
                    state       = state,
                    onStart     = viewModel::start,
                    onPause     = viewModel::pause,
                    onReset     = viewModel::reset,
                    onIncrement = viewModel::increment,
                    onDecrement = viewModel::decrement,
                )
            }
        }
    }
}
