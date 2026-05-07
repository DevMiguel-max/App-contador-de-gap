package com.example.gapcounter.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamilyimport androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gapcounter.CounterUiState
import com.example.gapcounter.ui.theme.*

// ── Tela Principal ─────────────────────────────────────────────────────────────
@Composable
fun CounterScreen(
    state: CounterUiState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDeep)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Cabeçalho ────────────────────────────────────────────────────
            SystemHeader(state = state)

            Spacer(modifier = Modifier.height(12.dp))

            // ── Display principal do contador ─────────────────────────────────
            CounterDisplay(state = state)

            Spacer(modifier = Modifier.height(12.dp))

            // ── Barra de perigo ───────────────────────────────────────────────
            DangerBar(dangerLevel = state.dangerLevel, isDanger = state.isDanger)

            Spacer(modifier = Modifier.height(16.dp))

            // ── Botões de controle principal (play/pause/reset) ───────────────
            PlaybackControls(
                isRunning  = state.isRunning,
                onStart    = onStart,
                onPause    = onPause,
                onReset    = onReset,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Botões manuais +1 / -1 ────────────────────────────────────────
            ManualControls(
                onIncrement = onIncrement,
                onDecrement = onDecrement,
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ── Log do sistema ────────────────────────────────────────────────
            SystemLog(
                logs     = state.logs,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Rodapé de status ──────────────────────────────────────────────
            StatusFooter(state = state)
        }
    }
}

// ── Cabeçalho ──────────────────────────────────────────────────────────────────
@Composable
private fun SystemHeader(state: CounterUiState) {
    // Pisca o cursor terminal
    val infiniteTransition = rememberInfiniteTransition(label = "blink")
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0f, label = "blink",
        animationSpec = infiniteRepeatable(
            animation    = tween(600, easing = LinearEasing),
            repeatMode   = RepeatMode.Reverse
        )
    )

    TerminalCard {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MonoText(
                    text  = "ET-1155 SYSTEM v2.4.7",
                    color = GreenPrimary,
                    size  = 13
                )
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .alpha(if (state.isRunning) blinkAlpha else 1f)
                        .background(
                            color = if (state.isRunning) GreenPrimary else RedDanger,
                            shape = RoundedCornerShape(50)
                        )
                )
                Spacer(Modifier.width(6.dp))
                MonoText(
                    text  = if (state.isRunning) "ONLINE" else "STANDBY",
                    color = if (state.isRunning) GreenPrimary else YellowWarn,
                    size  = 11
                )
            }
            Spacer(Modifier.height(2.dp))
            MonoText(
                text  = "CONTADOR_GAP.EXE // MODO INSTÁVEL",
                color = TextSecondary,
                size  = 10
            )
        }
    }
}

// ── Display do número ──────────────────────────────────────────────────────────
@Composable
private fun CounterDisplay(state: CounterUiState) {
    // Cor animada conforme perigo
    val countColor by animateColorAsState(
        targetValue = if (state.isDanger) RedDanger else GreenPrimary,
        animationSpec = tween(400),
        label = "countColor"
    )

    // Escala pulsante quando em modo perigo
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.04f, label = "pulse",
        animationSpec = infiniteRepeatable(
            animation  = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    TerminalCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MonoText(
                text  = "// CONTADOR DE GAP //",
                color = TextSecondary,
                size  = 11
            )
            Spacer(Modifier.height(8.dp))

            // Número principal com animação de escala
            val scale = if (state.isDanger) pulse else 1f
            Text(
                text      = state.count.toString(),
                fontSize  = (96 * scale).sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                color     = countColor,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(4.dp))
            MonoText(
                text  = if (state.isDanger) "[ PERIGO ]" else "[ NORMAL ]",
                color = countColor,
                size  = 13
            )
        }
    }
}

// ── Barra de perigo ─────────────────────────────────────────────────────────────
@Composable
private fun DangerBar(dangerLevel: Float, isDanger: Boolean) {
    val barColor by animateColorAsState(
        targetValue = when {
            dangerLevel > 0.8f -> RedDanger
            dangerLevel > 0.5f -> YellowWarn
            else               -> GreenPrimary
        },
        animationSpec = tween(300),
        label = "barColor"
    )

    val animatedProgress by animateFloatAsState(
        targetValue    = dangerLevel,
        animationSpec  = tween(500, easing = FastOutSlowInEasing),
        label          = "progress"
    )

    TerminalCard {
        Column(modifier = Modifier.padding(10.dp)) {
            Row {
                MonoText(text = "NÍVEL DE PERIGO", color = TextSecondary, size = 10)
                Spacer(Modifier.weight(1f))
                MonoText(
                    text  = "${(dangerLevel * 100).toInt()}%",
                    color = barColor,
                    size  = 10
                )
            }
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF1A1A1A))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(GreenPrimary, barColor)
                            ),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }
    }
}

// ── Controles Play / Pause / Reset ─────────────────────────────────────────────
@Composable
private fun PlaybackControls(
    isRunning: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Play / Pause
        GapButton(
            text     = if (isRunning) "⏸  PAUSAR" else "▶  INICIAR",
            color    = if (isRunning) YellowWarn else GreenPrimary,
            modifier = Modifier.weight(1f),
            onClick  = if (isRunning) onPause else onStart
        )
        // Reset
        GapButton(
            text     = "⟳  RESET",
            color    = RedDanger,
            modifier = Modifier.weight(0.7f),
            onClick  = onReset
        )
    }
}

// ── Botões Manuais +1 / -1 ─────────────────────────────────────────────────────
@Composable
private fun ManualControls(
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        GapButton(
            text     = "+1 GAP\nINCREMENTAR",
            color    = GreenPrimary,
            modifier = Modifier.weight(1f),
            onClick  = onIncrement
        )
        GapButton(
            text     = "-1 GAP\nDECREMENTAR",
            color    = CyanAccent,
            modifier = Modifier.weight(1f),
            onClick  = onDecrement
        )
    }
}

// ── Log do sistema ─────────────────────────────────────────────────────────────
@Composable
private fun SystemLog(
    logs: List<String>,
    modifier: Modifier = Modifier
) {
    TerminalCard(modifier = modifier) {
        Column(modifier = Modifier.padding(10.dp).fillMaxSize()) {
            MonoText(text = "▶ LOG DO SISTEMA", color = GreenDim, size = 11)
            Spacer(Modifier.height(6.dp))
            LazyColumn(
                modifier       = Modifier.fillMaxSize(),
                reverseLayout  = false,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(logs) { log ->
                    MonoText(text = log, color = TextSecondary, size = 10)
                }
            }
        }
    }
}

// ── Rodapé de status ───────────────────────────────────────────────────────────
@Composable
private fun StatusFooter(state: CounterUiState) {
    val elapsed = state.elapsedSeconds
    val h   = elapsed / 3600
    val m   = (elapsed % 3600) / 60
    val s   = elapsed % 60
    val time = "%02d:%02d:%02d".format(h, m, s)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(1.dp, BorderColor),
                shape  = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        MonoText(text = time,                          color = GreenPrimary, size = 10)
        MonoText(text = "PID:????",                    color = TextSecondary, size = 10)
        MonoText(text = "MEM: ${state.memUsage}%",     color = YellowWarn,   size = 10)
        MonoText(
            text  = if (state.isRunning) "ONLINE" else "OFFLINE",
            color = if (state.isRunning) GreenPrimary else RedDanger,
            size  = 10
        )
    }
}

// ── Componentes reutilizáveis ──────────────────────────────────────────────────

@Composable
fun TerminalCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(1.dp, BorderColor),
                shape  = RoundedCornerShape(8.dp)
            )
            .background(
                color = SurfaceDark,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        content()
    }
}

@Composable
fun GapButton(
    text: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick  = onClick,
        modifier = modifier.height(56.dp),
        shape    = RoundedCornerShape(8.dp),
        border   = BorderStroke(1.5.dp, color),
        colors   = ButtonDefaults.outlinedButtonColors(
            containerColor = color.copy(alpha = 0.08f),
            contentColor   = color
        )
    ) {
        Text(
            text       = text,
            fontFamily = FontFamily.Monospace,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign  = TextAlign.Center,
            color      = color
        )
    }
}

@Composable
fun MonoText(
    text: String,
    color: Color,
    size: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text       = text,
        fontFamily = FontFamily.Monospace,
        fontSize   = size.sp,
        color      = color,
        modifier   = modifier
    )
}
