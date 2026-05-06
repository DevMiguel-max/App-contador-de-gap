package com.example.gapcounter

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ── Estado imutável da tela ───────────────────────────────────────────────────
data class CounterUiState(
    val count: Int          = 0,
    val isRunning: Boolean  = false,
    val elapsedSeconds: Long = 0L,
    val logs: List<String>  = emptyList(),
    val dangerLevel: Float  = 0f,          // 0..1 para a barra de perigo
    val isDanger: Boolean   = false,
    val memUsage: Int       = 0
)

// ── ViewModel ─────────────────────────────────────────────────────────────────
class CounterViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val PREFS_NAME  = "gap_prefs"
        private const val KEY_COUNT   = "count"
        private const val DANGER_THRESHOLD = 50   // gap >= 50 ativa modo perigo
        private const val MAX_LOG_LINES    = 30
    }

    // SharedPreferences simples — sem dependências extras
    private val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(
        CounterUiState(count = prefs.getInt(KEY_COUNT, 0))
    )
    val state: StateFlow<CounterUiState> = _state.asStateFlow()

    private var timerJob: Job?   = null
    private var clockJob: Job?   = null
    private var memJob: Job?     = null

    init {
        val saved = prefs.getInt(KEY_COUNT, 0)
        addLog("ET-1155 SYSTEM v2.4.7")
        addLog("INICIANDO MÓDULO GAP...")
        addLog("VERIFICANDO DRIVERS... FALHOU")
        addLog("MODO INSTÁVEL ATIVADO")
        addLog("DADOS CARREGADOS >> COUNT=$saved")
        updateDangerLevel(saved)
        startClockAndMem()
    }

    // ── Controles públicos ────────────────────────────────────────────────────

    fun start() {
        if (_state.value.isRunning) return
        _state.update { it.copy(isRunning = true) }
        addLog("▶ CONTADOR INICIADO")
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                incrementInternal(auto = true)
            }
        }
    }

    fun pause() {
        timerJob?.cancel()
        timerJob = null
        _state.update { it.copy(isRunning = false) }
        addLog("⏸ CONTADOR PAUSADO >> GAP=${_state.value.count}")
    }

    fun reset() {
        timerJob?.cancel()
        timerJob = null
        _state.update {
            it.copy(
                count          = 0,
                isRunning      = false,
                elapsedSeconds = 0L,
                dangerLevel    = 0f,
                isDanger       = false
            )
        }
        saveCount(0)
        addLog("⚠ SISTEMA RESETADO")
        addLog("GAP = 0 // MEMÓRIA LIMPA")
    }

    fun increment() {
        incrementInternal(auto = false)
    }

    fun decrement() {
        val newCount = (_state.value.count - 1).coerceAtLeast(0)
        _state.update { it.copy(count = newCount) }
        saveCount(newCount)
        updateDangerLevel(newCount)
        addLog("-1 GAP REMOVIDO >> TOTAL: $newCount")
    }

    // ── Lógica interna ────────────────────────────────────────────────────────

    private fun incrementInternal(auto: Boolean) {
        val newCount = _state.value.count + 1
        _state.update { it.copy(count = newCount) }
        saveCount(newCount)
        updateDangerLevel(newCount)
        if (!auto) addLog("+1 GAP ADICIONADO >> TOTAL: $newCount")
        if (newCount == DANGER_THRESHOLD) addLog("⚠⚠ NÍVEL DE PERIGO ATINGIDO!")
    }

    private fun updateDangerLevel(count: Int) {
        val level    = (count.toFloat() / DANGER_THRESHOLD).coerceIn(0f, 1f)
        val isDanger = count >= DANGER_THRESHOLD
        _state.update { it.copy(dangerLevel = level, isDanger = isDanger) }
    }

    private fun saveCount(count: Int) {
        prefs.edit().putInt(KEY_COUNT, count).apply()
    }

    private fun addLog(message: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        _state.update { state ->
            val newLogs = (listOf("[$time] $message") + state.logs)
                .take(MAX_LOG_LINES)
            state.copy(logs = newLogs)
        }
    }

    /** Clock de tempo decorrido + simulação de memória — roda sempre */
    private fun startClockAndMem() {
        clockJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                _state.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
        memJob = viewModelScope.launch {
            while (true) {
                delay(3000L)
                // Simula uso de memória (oscila entre 40-85 %)
                val mem = (40..85).random()
                _state.update { it.copy(memUsage = mem) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        clockJob?.cancel()
        memJob?.cancel()
    }
}
