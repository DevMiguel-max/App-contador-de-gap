package com.example.gapcounter.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Paleta Cyberpunk / Terminal ──────────────────────────────────────────────
val GreenPrimary    = Color(0xFF00FF41)   // verde matrix principal
val GreenDim        = Color(0xFF00C832)   // verde um pouco mais escuro
val GreenGlow       = Color(0x4000FF41)   // verde semitransparente (brilho)
val RedDanger       = Color(0xFFFF0033)   // vermelho de perigo
val RedGlow         = Color(0x40FF0033)   // vermelho semitransparente
val YellowWarn      = Color(0xFFFFCC00)   // amarelo aviso
val CyanAccent      = Color(0xFF00E5FF)   // ciano destaque
val SurfaceDark     = Color(0xFF111111)   // superfície de card
val BackgroundDeep  = Color(0xFF080808)   // fundo principal
val BorderColor     = Color(0xFF1E3A1E)   // borda verde escura
val TextSecondary   = Color(0xFF5A8C5A)   // texto secundário esverdeado

private val CyberpunkColorScheme = darkColorScheme(
    primary           = GreenPrimary,
    onPrimary         = BackgroundDeep,
    secondary         = CyanAccent,
    onSecondary       = BackgroundDeep,
    error             = RedDanger,
    background        = BackgroundDeep,
    onBackground      = GreenPrimary,
    surface           = SurfaceDark,
    onSurface         = GreenPrimary,
    outline           = BorderColor,
)

@Composable
fun GapCounterTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CyberpunkColorScheme,
        content = content
    )
}
