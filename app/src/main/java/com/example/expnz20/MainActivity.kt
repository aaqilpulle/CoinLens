package com.example.expnz20

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Build
import android.graphics.ImageDecoder
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.window.DialogProperties

import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.FragmentActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin

fun Context.getActivity(): Activity? {
    var currentContext = this
    while (currentContext is android.content.ContextWrapper) {
        if (currentContext is Activity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}

// ============================================================================
// BRAND LOGO COLOR PALETTE (DEEP FOREST EMERALD & CORAL ROSE)
// ============================================================================
val BrandForestDark = Color(0xFF0B2B26)      // Exact Deep Forest background from logo
val BrandForestSurface = Color(0xFF133A33)   // Elevated Forest card surface
val BrandForestVariant = Color(0xFF1C4E45)   // Highlighted Forest container
val BrandCoralPink = Color(0xFFE38391)       // Exact Coral Rose from logo wallet & arrow
val BrandCoralLight = Color(0xFFF3A6B2)      // Soft Rose highlight
val BrandMintGreen = Color(0xFF34D399)       // Accessible Mint Green (Inflow / Positive)
val BrandWarmAmber = Color(0xFFFBBF24)       // Warm Gold Accent
val BrandCoralRed = Color(0xFFF87171)        // High-visibility Coral Red (Outflow / Negative)
val BrandSkyBlue = Color(0xFF38BDF8)         // Sky Blue Accent
val BrandPurple = Color(0xFFC084FC)          // Soft Lavender Purple

val SlateGray = Color(0xFFCBD5E1)

// Customizable User/Partner Color Presets
val UserColorPalette = listOf(
    "Coral Rose" to 0xFFE38391L,
    "Mint Emerald" to 0xFF34D399L,
    "Sky Blue" to 0xFF38BDF8L,
    "Warm Amber" to 0xFFFBBF24L,
    "Lavender Purple" to 0xFFC084FCL,
    "Vivid Teal" to 0xFF2DD4BFL,
    "Sunset Orange" to 0xFFFB923CL,
    "Hot Pink" to 0xFFF472B6L
)

val LocalIsDarkTheme = staticCompositionLocalOf { true }

// ============================================================================
// APPLE-INSPIRED LIQUID GLASS DESIGN ENGINE (GLOBAL REFRACTION, MENISCUS & RIM)
// ============================================================================
@Composable
fun LiquidGlassAmbientBackground(isDarkMode: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "liquid_glass_ambient")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 22000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb_phase"
    )

    val baseBg = if (isDarkMode) BrandForestDark else Color(0xFFEAF4F1)

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(baseBg)
    ) {
        val w = size.width
        val h = size.height
        val maxDim = max(w, h)

        // Orb 1: Coral Rose refractive bloom (top-left drifting)
        val c1 = Offset(
            x = w * (0.20f + 0.14f * cos(phase)),
            y = h * (0.16f + 0.10f * sin(phase))
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    BrandCoralPink.copy(alpha = if (isDarkMode) 0.24f else 0.18f),
                    BrandCoralPink.copy(alpha = if (isDarkMode) 0.06f else 0.04f),
                    Color.Transparent
                ),
                center = c1,
                radius = maxDim * 0.56f
            ),
            radius = maxDim * 0.56f,
            center = c1
        )

        // Orb 2: Mint Emerald caustic glow (mid-right drifting)
        val c2 = Offset(
            x = w * (0.82f + 0.12f * sin(phase * 0.85f)),
            y = h * (0.44f + 0.14f * cos(phase * 0.85f))
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    BrandMintGreen.copy(alpha = if (isDarkMode) 0.18f else 0.14f),
                    BrandSkyBlue.copy(alpha = if (isDarkMode) 0.06f else 0.04f),
                    Color.Transparent
                ),
                center = c2,
                radius = maxDim * 0.54f
            ),
            radius = maxDim * 0.54f,
            center = c2
        )

        // Orb 3: Sky Blue & Lavender deep liquid refraction (bottom-left drifting)
        val c3 = Offset(
            x = w * (0.26f + 0.15f * sin(phase + 1.4f)),
            y = h * (0.82f + 0.10f * cos(phase + 1.4f))
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    BrandSkyBlue.copy(alpha = if (isDarkMode) 0.16f else 0.12f),
                    BrandPurple.copy(alpha = if (isDarkMode) 0.07f else 0.05f),
                    Color.Transparent
                ),
                center = c3,
                radius = maxDim * 0.58f
            ),
            radius = maxDim * 0.58f,
            center = c3
        )

        // Orb 4: Warm Gold subtle prism highlight (bottom-right drifting)
        val c4 = Offset(
            x = w * (0.76f + 0.10f * cos(phase * 1.15f)),
            y = h * (0.86f + 0.08f * sin(phase * 1.15f))
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    BrandWarmAmber.copy(alpha = if (isDarkMode) 0.12f else 0.10f),
                    Color.Transparent
                ),
                center = c4,
                radius = maxDim * 0.44f
            ),
            radius = maxDim * 0.44f,
            center = c4
        )
    }
}

enum class LiquidGlassRole {
    ORB,
    HEADER,
    FOOTER,
    STATIC
}

private val nextLiquidOrbId = AtomicInteger(1)

@Stable
class LiquidScrollBoundaryState {
    var headerTopPx by mutableFloatStateOf(0f)
    var headerBottomPx by mutableFloatStateOf(0f)
    var headerLeftPx by mutableFloatStateOf(0f)
    var headerWidthPx by mutableFloatStateOf(0f)

    var footerTopPx by mutableFloatStateOf(Float.MAX_VALUE)
    var footerBottomPx by mutableFloatStateOf(Float.MAX_VALUE)
    var footerLeftPx by mutableFloatStateOf(0f)
    var footerWidthPx by mutableFloatStateOf(0f)

    private val headerContacts = mutableStateMapOf<Int, Pair<Float, Float>>()
    private val footerContacts = mutableStateMapOf<Int, Pair<Float, Float>>()

    val headerTouchIntensity: Float
        get() = headerContacts.values.maxOfOrNull { it.first } ?: 0f

    val headerTouchCenterX: Float
        get() = headerContacts.values.maxByOrNull { it.first }?.second ?: 0f

    val footerTouchIntensity: Float
        get() = footerContacts.values.maxOfOrNull { it.first } ?: 0f

    val footerTouchCenterX: Float
        get() = footerContacts.values.maxByOrNull { it.first }?.second ?: 0f

    fun updateContact(id: Int, topIntensity: Float, bottomIntensity: Float, centerX: Float) {
        if (topIntensity > 0.015f) {
            headerContacts[id] = topIntensity to centerX
        } else {
            headerContacts.remove(id)
        }
        if (bottomIntensity > 0.015f) {
            footerContacts[id] = bottomIntensity to centerX
        } else {
            footerContacts.remove(id)
        }
    }

    fun removeContact(id: Int) {
        headerContacts.remove(id)
        footerContacts.remove(id)
    }
}

val LocalLiquidScrollBoundary = staticCompositionLocalOf { LiquidScrollBoundaryState() }
val LocalOnHorizontalChildTouchChange = staticCompositionLocalOf<(Boolean) -> Unit> { {} }

@Composable
fun Modifier.lockPagerOnTouch(): Modifier {
    val onTouchChange = LocalOnHorizontalChildTouchChange.current
    DisposableEffect(Unit) {
        onDispose { onTouchChange(false) }
    }
    return this.pointerInput(onTouchChange) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            onTouchChange(true)
            try {
                do {
                    val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                } while (event.changes.any { it.pressed })
            } finally {
                onTouchChange(false)
            }
        }
    }
}

private class LiquidOrbBoundsHolder(
    var topPx: Float = -10000f,
    var bottomPx: Float = -10000f,
    var lastTopQuant: Int = 0,
    var lastBotQuant: Int = 0
)

@Composable
fun Modifier.liquidGlass(
    shape: Shape = RoundedCornerShape(22.dp),
    tintColor: Color = BrandForestSurface,
    intensity: Float = 1f,
    accentRimColor: Color = BrandCoralPink,
    role: LiquidGlassRole = LiquidGlassRole.ORB
): Modifier {
    val boundaryState = LocalLiquidScrollBoundary.current
    val orbId = remember { nextLiquidOrbId.incrementAndGet() }
    val boundsHolder = remember { LiquidOrbBoundsHolder() }

    if (role == LiquidGlassRole.ORB) {
        DisposableEffect(orbId) {
            onDispose { boundaryState.removeContact(orbId) }
        }
    }

    val edgeZonePx = with(LocalDensity.current) { 20.dp.toPx() }.coerceAtLeast(16f)

    return this
        .onGloballyPositioned { coords ->
            if (!coords.isAttached) return@onGloballyPositioned
            val b = coords.boundsInRoot()
            when (role) {
                LiquidGlassRole.HEADER -> {
                    if (abs(boundaryState.headerTopPx - b.top) > 0.5f) boundaryState.headerTopPx = b.top
                    if (abs(boundaryState.headerBottomPx - b.bottom) > 0.5f) boundaryState.headerBottomPx = b.bottom
                    if (abs(boundaryState.headerLeftPx - b.left) > 0.5f) boundaryState.headerLeftPx = b.left
                    if (abs(boundaryState.headerWidthPx - b.width) > 0.5f) boundaryState.headerWidthPx = b.width
                }
                LiquidGlassRole.FOOTER -> {
                    if (abs(boundaryState.footerTopPx - b.top) > 0.5f) boundaryState.footerTopPx = b.top
                    if (abs(boundaryState.footerBottomPx - b.bottom) > 0.5f) boundaryState.footerBottomPx = b.bottom
                    if (abs(boundaryState.footerLeftPx - b.left) > 0.5f) boundaryState.footerLeftPx = b.left
                    if (abs(boundaryState.footerWidthPx - b.width) > 0.5f) boundaryState.footerWidthPx = b.width
                }
                LiquidGlassRole.ORB -> {
                    boundsHolder.topPx = b.top
                    boundsHolder.bottomPx = b.bottom
                    val hB = boundaryState.headerBottomPx
                    val fT = boundaryState.footerTopPx
                    // Only trigger subtle edge refraction when the orb's actual top/bottom edge crosses the header/footer
                    val topC = if (hB > 0f && b.height > 0f) {
                        val dist = abs(b.top - hB)
                        if (dist < edgeZonePx) {
                            val t = 1f - (dist / edgeZonePx)
                            (t * t).coerceIn(0f, 1f)
                        } else 0f
                    } else 0f
                    val botC = if (fT < Float.MAX_VALUE && b.height > 0f) {
                        val dist = abs(b.bottom - fT)
                        if (dist < edgeZonePx) {
                            val t = 1f - (dist / edgeZonePx)
                            (t * t).coerceIn(0f, 1f)
                        } else 0f
                    } else 0f
                    val topQ = (topC * 12f).roundToInt()
                    val botQ = (botC * 12f).roundToInt()
                    if (topQ != boundsHolder.lastTopQuant || botQ != boundsHolder.lastBotQuant) {
                        boundsHolder.lastTopQuant = topQ
                        boundsHolder.lastBotQuant = botQ
                        boundaryState.updateContact(orbId, topC, botC, b.center.x)
                    }
                }
                LiquidGlassRole.STATIC -> Unit
            }
        }
        .clip(shape)
        .drawWithCache {
            val outline = shape.createOutline(size, layoutDirection, this)
            val safeIntensity = intensity.coerceIn(0.65f, 1.5f)
            val strokePx = 1.35.dp.toPx()

            val solidTint = tintColor.copy(alpha = 1f)

            val baseFrostBrush = Brush.linearGradient(
                colors = listOf(
                    solidTint.copy(alpha = (0.86f * safeIntensity).coerceIn(0.78f, 0.95f)),
                    solidTint.copy(alpha = (0.82f * safeIntensity).coerceIn(0.75f, 0.93f)),
                    solidTint.copy(alpha = (0.88f * safeIntensity).coerceIn(0.80f, 0.96f))
                ),
                start = Offset.Zero,
                end = Offset(size.width, size.height)
            )

            val meniscusBrush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = (0.14f * safeIntensity).coerceIn(0f, 0.24f)),
                    Color.White.copy(alpha = (0.03f * safeIntensity).coerceIn(0f, 0.08f)),
                    Color.Transparent
                ),
                startY = 0f,
                endY = (size.height * 0.35f).coerceAtLeast(1f)
            )

            val sheenBrush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.White.copy(alpha = (0.04f * safeIntensity).coerceIn(0f, 0.10f)),
                    Color.Transparent
                ),
                start = Offset(size.width * 0.15f, 0f),
                end = Offset(size.width * 0.85f, size.height)
            )

            val rimBrush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = (0.68f * safeIntensity).coerceIn(0f, 0.92f)),
                    Color.White.copy(alpha = (0.20f * safeIntensity).coerceIn(0f, 0.48f)),
                    accentRimColor.copy(alpha = (0.58f * safeIntensity).coerceIn(0f, 0.88f)),
                    Color.White.copy(alpha = (0.42f * safeIntensity).coerceIn(0f, 0.78f))
                ),
                start = Offset.Zero,
                end = Offset(size.width, size.height)
            )

            val edgeGlowDepthPx = 16.dp.toPx()

            onDrawWithContent {
                drawOutline(outline = outline, brush = baseFrostBrush)
                drawOutline(outline = outline, brush = meniscusBrush)
                drawOutline(outline = outline, brush = sheenBrush)

                val topContact = if (role == LiquidGlassRole.ORB && boundaryState.headerBottomPx > 0f && boundsHolder.bottomPx > boundsHolder.topPx) {
                    val dist = abs(boundsHolder.topPx - boundaryState.headerBottomPx)
                    if (dist < edgeZonePx) {
                        val t = 1f - (dist / edgeZonePx)
                        (t * t).coerceIn(0f, 1f)
                    } else 0f
                } else 0f

                val bottomContact = if (role == LiquidGlassRole.ORB && boundaryState.footerTopPx < Float.MAX_VALUE && boundsHolder.bottomPx > boundsHolder.topPx) {
                    val dist = abs(boundsHolder.bottomPx - boundaryState.footerTopPx)
                    if (dist < edgeZonePx) {
                        val t = 1f - (dist / edgeZonePx)
                        (t * t).coerceIn(0f, 1f)
                    } else 0f
                } else 0f

                val headerReactive = if (role == LiquidGlassRole.HEADER) boundaryState.headerTouchIntensity else 0f
                val footerReactive = if (role == LiquidGlassRole.FOOTER) boundaryState.footerTouchIntensity else 0f

                // Subtle top-edge meniscus refraction when the orb's top edge touches the Header
                if (role == LiquidGlassRole.ORB && topContact > 0.02f && size.width > 0f && size.height > 0f) {
                    val bandH = edgeGlowDepthPx.coerceAtMost(size.height * 0.25f).coerceAtLeast(2f)
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = (0.11f * topContact).coerceIn(0f, 0.14f)),
                                accentRimColor.copy(alpha = (0.07f * topContact).coerceIn(0f, 0.10f)),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = bandH
                        ),
                        topLeft = Offset.Zero,
                        size = Size(size.width, bandH)
                    )
                }

                // Subtle bottom-edge meniscus refraction when the orb's bottom edge touches the Footer
                if (role == LiquidGlassRole.ORB && bottomContact > 0.02f && size.width > 0f && size.height > 0f) {
                    val bandH = edgeGlowDepthPx.coerceAtMost(size.height * 0.25f).coerceAtLeast(2f)
                    val startY = (size.height - bandH).coerceAtLeast(0f)
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                accentRimColor.copy(alpha = (0.07f * bottomContact).coerceIn(0f, 0.10f)),
                                Color.White.copy(alpha = (0.11f * bottomContact).coerceIn(0f, 0.14f))
                            ),
                            startY = startY,
                            endY = size.height
                        ),
                        topLeft = Offset(0f, startY),
                        size = Size(size.width, bandH)
                    )
                }

                // Subtle Header bottom-edge shimmer when an orb edge passes it
                if (role == LiquidGlassRole.HEADER && headerReactive > 0.02f && size.width > 0f && size.height > 0f) {
                    val bandH = 12.dp.toPx().coerceAtMost(size.height * 0.35f).coerceAtLeast(2f)
                    val startY = (size.height - bandH).coerceAtLeast(0f)
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = (0.10f * headerReactive).coerceIn(0f, 0.13f))
                            ),
                            startY = startY,
                            endY = size.height
                        ),
                        topLeft = Offset(0f, startY),
                        size = Size(size.width, bandH)
                    )
                }

                // Subtle Footer top-edge shimmer when an orb edge passes it
                if (role == LiquidGlassRole.FOOTER && footerReactive > 0.02f && size.width > 0f && size.height > 0f) {
                    val bandH = 12.dp.toPx().coerceAtMost(size.height * 0.35f).coerceAtLeast(2f)
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = (0.10f * footerReactive).coerceIn(0f, 0.13f)),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = bandH
                        ),
                        topLeft = Offset.Zero,
                        size = Size(size.width, bandH)
                    )
                }

                drawContent()

                val activeEdgeBoost = max(
                    max(topContact, bottomContact),
                    max(headerReactive, footerReactive)
                )
                drawOutline(
                    outline = outline,
                    brush = rimBrush,
                    style = Stroke(width = strokePx * (1f + 0.22f * activeEdgeBoost))
                )
            }
        }
}

@Composable
fun rememberHeaderAwareTopPadding(orbSpacing: Dp): Dp {
    val boundaryState = LocalLiquidScrollBoundary.current
    val density = LocalDensity.current
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val headerBottomDp = if (boundaryState.headerBottomPx > 0f) {
        with(density) { boundaryState.headerBottomPx.toDp() }
    } else {
        statusBarTop + 72.dp
    }
    return headerBottomDp + orbSpacing
}

// Global package-level Liquid Glass Card, Clickable Card, OutlinedButton & AlertDialog wrappers
// Automatically upgrades every Card, Clickable Orb/Tile, OutlinedButton, and AlertDialog across all tabs!
@Composable
fun Card(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val defaultTint = if (isDark) Color(0xFF0E2F29) else Color(0xFFF7FBF9)
    val glassBaseTint = if (colors.containerColor != Color.Unspecified && colors.containerColor != Color.Transparent) {
        when {
            colors.containerColor.alpha < 0.70f -> {
                lerp(
                    defaultTint,
                    colors.containerColor.copy(alpha = 1f),
                    colors.containerColor.alpha.coerceIn(0.12f, 0.24f)
                )
            }
            isDark && colors.containerColor.red < 0.2f && colors.containerColor.green < 0.35f -> {
                defaultTint
            }
            else -> {
                colors.containerColor.copy(alpha = 1f)
            }
        }
    } else {
        defaultTint
    }
    val rimAccent = border?.brush?.let { brush ->
        if (brush is SolidColor) brush.value else BrandCoralPink
    } ?: if (isDark) BrandCoralPink else BrandMintGreen

    val zeroElevation = if (elevation == CardDefaults.cardElevation()) {
        CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
    } else {
        CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
    }

    androidx.compose.material3.Card(
        modifier = modifier
            .liquidGlass(
                shape = shape,
                tintColor = glassBaseTint,
                intensity = if (isDark) 1.05f else 1.12f,
                accentRimColor = rimAccent
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = if (colors.contentColor != Color.Unspecified) colors.contentColor else if (isDark) Color.White else BrandForestDark
        ),
        elevation = zeroElevation,
        border = border,
        content = content
    )
}

@Composable
fun Card(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val defaultTint = if (isDark) Color(0xFF0C2A25) else Color(0xFFF4FAF7)
    val glassBaseTint = if (colors.containerColor != Color.Unspecified && colors.containerColor != Color.Transparent) {
        when {
            colors.containerColor.alpha < 0.70f -> {
                lerp(
                    defaultTint,
                    colors.containerColor.copy(alpha = 1f),
                    colors.containerColor.alpha.coerceIn(0.12f, 0.24f)
                )
            }
            isDark && colors.containerColor.red < 0.2f && colors.containerColor.green < 0.35f -> {
                defaultTint
            }
            else -> {
                colors.containerColor.copy(alpha = 1f)
            }
        }
    } else {
        defaultTint
    }
    val rimAccent = border?.brush?.let { brush ->
        if (brush is SolidColor) brush.value else BrandCoralPink
    } ?: if (isDark) BrandCoralPink else BrandMintGreen

    val zeroElevation = if (elevation == CardDefaults.cardElevation()) {
        CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
    } else {
        CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
    }

    // Clickable cards/orbs have higher intensity (1.22f) so they look more solid and readable
    androidx.compose.material3.Card(
        onClick = onClick,
        modifier = modifier
            .liquidGlass(
                shape = shape,
                tintColor = glassBaseTint,
                intensity = 1.22f,
                accentRimColor = rimAccent
            ),
        enabled = enabled,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = if (colors.contentColor != Color.Unspecified) colors.contentColor else if (isDark) Color.White else BrandForestDark
        ),
        elevation = zeroElevation,
        border = border,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun OutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.outlinedShape,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = BorderStroke(1.2.dp, BrandCoralPink.copy(alpha = 0.65f)),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val solidButtonContainer = if (colors.containerColor != Color.Unspecified && colors.containerColor != Color.Transparent) {
        colors.containerColor
    } else if (isDark) {
        BrandForestVariant.copy(alpha = 0.88f)
    } else {
        Color.White.copy(alpha = 0.92f)
    }
    val solidButtonContent = if (colors.contentColor != Color.Unspecified) {
        colors.contentColor
    } else if (isDark) {
        Color.White
    } else {
        BrandForestDark
    }

    androidx.compose.material3.OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = solidButtonContainer,
            contentColor = solidButtonContent
        ),
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(28.dp),
    containerColor: Color = Color.Unspecified,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = Color.Unspecified,
    textContentColor: Color = Color.Unspecified,
    tonalElevation: Dp = 0.dp,
    properties: DialogProperties = DialogProperties()
) {
    val isDark = LocalIsDarkTheme.current
    val baseGlass = if (containerColor != Color.Unspecified && containerColor != Color.Transparent) {
        containerColor.copy(alpha = 0.94f)
    } else if (isDark) {
        BrandForestDark.copy(alpha = 0.94f)
    } else {
        Color.White.copy(alpha = 0.95f)
    }
    val zeroTonalElevation = if (tonalElevation > 0.dp) 0.dp else 0.dp
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        modifier = modifier
            .liquidGlass(
                shape = shape,
                tintColor = baseGlass,
                intensity = 1.25f,
                accentRimColor = BrandCoralPink,
                role = LiquidGlassRole.STATIC
            ),
        dismissButton = dismissButton,
        icon = icon,
        title = title,
        text = text,
        shape = shape,
        containerColor = Color.Transparent,
        iconContentColor = iconContentColor,
        titleContentColor = if (titleContentColor != Color.Unspecified) titleContentColor else if (isDark) Color.White else BrandForestDark,
        textContentColor = if (textContentColor != Color.Unspecified) textContentColor else if (isDark) Color.White.copy(alpha = 0.95f) else BrandForestDark.copy(alpha = 0.95f),
        tonalElevation = zeroTonalElevation,
        properties = properties
    )
}

// ============================================================================
// CUSTOM VECTOR APP LOGO COMPOSABLE (MATCHES UPLOADED WALLET + ARROW IMAGE)
// ============================================================================
@Composable
fun AppLogoIcon(
    modifier: Modifier = Modifier,
    size: Dp = 38.dp,
    backgroundColor: Color = BrandForestDark,
    foregroundColor: Color = BrandCoralPink
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.26f))
            .background(backgroundColor)
            .border(1.dp, foregroundColor.copy(alpha = 0.35f), RoundedCornerShape(size * 0.26f)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(size * 0.16f)) {
            val w = this.size.width
            val h = this.size.height
            val strokeW = w * 0.105f

            // Top flap of wallet
            val flapPath = Path().apply {
                moveTo(w * 0.16f, h * 0.28f)
                lineTo(w * 0.62f, h * 0.12f)
                quadraticTo(w * 0.72f, h * 0.09f, w * 0.75f, h * 0.24f)
            }
            drawPath(
                path = flapPath,
                color = foregroundColor,
                style = Stroke(width = strokeW * 0.85f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Main wallet body outline
            drawRoundRect(
                color = foregroundColor,
                topLeft = Offset(w * 0.10f, h * 0.28f),
                size = Size(w * 0.76f, h * 0.60f),
                cornerRadius = CornerRadius(w * 0.14f, w * 0.14f),
                style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )

            // Wallet clasp on right side
            drawRoundRect(
                color = foregroundColor,
                topLeft = Offset(w * 0.68f, h * 0.49f),
                size = Size(w * 0.18f, h * 0.18f),
                cornerRadius = CornerRadius(w * 0.09f, w * 0.09f),
                style = Stroke(width = strokeW * 0.85f)
            )

            // Rising trend zig-zag arrow cutting across wallet
            val arrowLinePath = Path().apply {
                moveTo(w * 0.06f, h * 0.88f)
                lineTo(w * 0.40f, h * 0.50f)
                lineTo(w * 0.54f, h * 0.64f)
                lineTo(w * 0.90f, h * 0.20f)
            }
            drawPath(
                path = arrowLinePath,
                color = foregroundColor,
                style = Stroke(width = strokeW * 1.25f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Arrowhead at top right
            val arrowHeadPath = Path().apply {
                moveTo(w * 0.95f, h * 0.14f)
                lineTo(w * 0.68f, h * 0.20f)
                lineTo(w * 0.89f, h * 0.40f)
                close()
            }
            drawPath(path = arrowHeadPath, color = foregroundColor)
        }
    }
}

// ============================================================================
// ACCESSIBLE DIRECTIONAL ARROW BADGE FOR COLOR-BLIND USERS
// ============================================================================
@Composable
fun CashflowDirectionAmount(
    amount: Double,
    suffix: String = "/wk",
    decimals: Int = 2,
    isWealthHidden: Boolean = false,
    fontSize: Int = 14
) {
    val isPositive = amount >= 0.0
    val badgeColor = if (isWealthHidden) SlateGray else if (isPositive) BrandMintGreen else BrandCoralPink
    val arrowIcon = if (isPositive) Icons.Default.NorthEast else Icons.Default.SouthEast


    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = badgeColor.copy(alpha = 0.16f),
            border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = arrowIcon,
                    contentDescription = if (isPositive) "Money In (Positive)" else "Money Out (Expense)",
                    tint = badgeColor,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        Text(
            text = if (isWealthHidden) "$ ••••••$suffix" else "${formatCurrency(amount, decimals)}$suffix",
            fontWeight = FontWeight.ExtraBold,
            fontSize = fontSize.sp,
            color = if (isWealthHidden) MaterialTheme.colorScheme.onSurface else badgeColor
        )
    }
}

// ============================================================================
// DATA MODELS & APP CONFIGURATION
// ============================================================================
data class UserAppConfig(
    val isSetupCompleted: Boolean = false,
    val isCoupleMode: Boolean = true,
    val person1Name: String = "User 1",
    val person2Name: String = "Partner",
    val person1ColorHex: Long = 0xFFE38391L, // Default: Logo Coral Rose
    val person2ColorHex: Long = 0xFF34D399L, // Default: Mint Emerald
    val property1Name: String = "Primary Residence",
    val property2Name: String = "Investment Property",
    val property1Url: String = "",
    val property2Url: String = "",
    val isProperty1Rented: Boolean = false,
    val isProperty2Rented: Boolean = false,
    val hasSecondProperty: Boolean = false,
    val primaryRentWeekly: Double = 0.0,
    val secondaryRentWeekly: Double = 0.0,
    // Module Toggles
    val showTotalWealth: Boolean = true,
    val showCashFlow: Boolean = true,
    val showProperties: Boolean = true,
    val showSuperAndShares: Boolean = true,
    val showOtherAssets: Boolean = true,
    val showLoans: Boolean = true,
    val showWhatIf: Boolean = true,
    val useExpnzAi: Boolean = true,
    // Custom Colors
    val colorWealthHex: Long = 0xFFFBBF24L, // Amber
    val colorCashFlowHex: Long = 0xFF34D399L, // Mint
    val colorPropertiesHex: Long = 0xFF38BDF8L, // Blue
    val colorSuperSharesHex: Long = 0xFFC084FCL, // Purple
    val colorOtherAssetsHex: Long = 0xFFE38391L, // Coral
    val colorLoansHex: Long = 0xFFF87171L, // Red
    val colorWhatIfHex: Long = 0xFF818CF8L, // Indigo
    // Partner Sharing & Couple Sync
    val primaryOwnerEmail: String = "",
    val partnerEmail: String = "",
    val sharedHouseholdId: String = ""
)

enum class AppSection(
    val title: String,
    val shortLabel: String,
    val icon: ImageVector,
) {
    TOTAL_WEALTH("Total Wealth", "Total", Icons.Default.PieChart),
    CASH_FLOW("Personal Cash Flow", "Cash Flow", Icons.Default.AccountBalanceWallet),
    PROPERTIES("Properties", "Properties", Icons.Default.HomeWork),
    SUPER_SHARES("Super & Shares", "Super/Shares", Icons.AutoMirrored.Filled.TrendingUp),
    OTHER_ASSETS("Other Assets", "Assets", Icons.Default.DirectionsCar),
    LOANS("Home Loans & Offset", "Loans", Icons.Default.QueryStats),
    WHAT_IF("Scenario Simulator", "What-If", Icons.Default.Science),
    SETTINGS("Settings", "Settings", Icons.Default.Settings);

    fun getColor(appConfig: UserAppConfig): Color {
        return when (this) {
            TOTAL_WEALTH -> Color(appConfig.colorWealthHex)
            CASH_FLOW -> Color(appConfig.colorCashFlowHex)
            PROPERTIES -> Color(appConfig.colorPropertiesHex)
            SUPER_SHARES -> Color(appConfig.colorSuperSharesHex)
            OTHER_ASSETS -> Color(appConfig.colorOtherAssetsHex)
            LOANS -> Color(appConfig.colorLoansHex)
            WHAT_IF -> Color(appConfig.colorWhatIfHex)
            SETTINGS -> SlateGray
        }
    }
}

data class CashflowItem(
    val id: String,
    val name: String,
    val weeklyAmount: Double,
    val isLockedMortgageOrPropertyLink: Boolean = false,
    val iconName: String = "Default",
    val customSubtitle: String? = null
)

data class LoanConfig(
    val name: String,
    val startDateStr: String,
    val originalLoanAmount: Double,
    val originalTermYears: Int,
    val startLoanBalance: Double,
    val remainingTermYears: Double,
    val interestRatePa: Double,
    val offsetBalance: Double
)

data class ShareParcel(
    val id: String,
    val code: String,
    val owner: String,
    val units: Double,
    val purchasePrice: Double,
    val currentPrice: Double
)

data class OtherAsset(
    val id: String,
    val name: String,
    val owner: String,
    val value: Double
)

data class SuperAccount(
    val id: String,
    val fundName: String,
    val owner: String,
    val balance: Double
)

data class AmortizationRow(
    val fortnightNumber: Int,
    val paymentDate: String,
    val payment: Double,
    val monthlyInterestCharged: Double,
    val accruedInterest: Double,
    val closingBalance: Double
)

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val auth = remember { FirebaseAuth.getInstance() }
            var currentUser by remember { mutableStateOf(auth.currentUser) }

            val sharedPrefs = remember { applicationContext.getSharedPreferences("expnz_public_wealth_v2", MODE_PRIVATE) }
            var isDarkMode by remember { mutableStateOf(sharedPrefs.getBoolean("dark_mode", true)) }

            val colorScheme = if (isDarkMode) {
                darkColorScheme(
                    primary = BrandCoralPink,
                    onPrimary = BrandForestDark,
                    secondary = BrandMintGreen,
                    tertiary = BrandWarmAmber,
                    error = BrandCoralRed,
                    background = Color.Transparent,
                    surface = BrandForestSurface.copy(alpha = 0.88f),
                    onBackground = Color.White,
                    onSurface = Color.White,
                    surfaceVariant = BrandForestVariant.copy(alpha = 0.85f),
                    onSurfaceVariant = Color.White
                )
            } else {
                lightColorScheme(
                    primary = BrandForestDark,
                    onPrimary = Color.White,
                    secondary = BrandCoralPink,
                    tertiary = BrandWarmAmber,
                    error = BrandCoralRed,
                    background = Color.Transparent,
                    surface = Color.White.copy(alpha = 0.90f),
                    onBackground = BrandForestDark,
                    onSurface = BrandForestDark,
                    surfaceVariant = Color(0xFFDCECE7).copy(alpha = 0.88f),
                    onSurfaceVariant = BrandForestDark
                )
            }

            CompositionLocalProvider(LocalIsDarkTheme provides isDarkMode) {
                MaterialTheme(colorScheme = colorScheme) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Global animated multi-orb ambient refraction canvas behind the entire app
                        LiquidGlassAmbientBackground(isDarkMode = isDarkMode)

                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = Color.Transparent
                        ) {
                            if (currentUser == null) {
                                LoginScreen(
                                    onLoginSuccess = { user -> currentUser = user }
                                )
                            } else {
                                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                                    val isExpanded = maxWidth > 600.dp
                                    ExpenseTrackerApp(
                                        currentUser = currentUser!!,
                                        onSignOutUser = {
                                            auth.signOut()
                                            currentUser = null
                                        },
                                        isExpanded = isExpanded,
                                        isDarkMode = isDarkMode,
                                        onDarkModeChange = { newMode ->
                                            isDarkMode = newMode
                                            sharedPrefs.edit { putBoolean("dark_mode", newMode) }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// FINANCIAL CALCULATIONS, AMORTIZATION & COMPOUND PROJECTION ENGINE
// ============================================================================
fun calculateFortnightlyRepayment(originalLoan: Double, ratePa: Double, totalFortnights: Int = 780): Double {
    if (originalLoan <= 0.0) return 0.0
    val safeFortnights = totalFortnights.coerceAtLeast(26)
    if (ratePa <= 0.0) return (originalLoan / safeFortnights).roundToInt().toDouble()
    val r = (ratePa / 100.0) / 26.0
    val factor = (1.0 + r).pow(safeFortnights.toDouble())
    return ((originalLoan * (r * factor)) / (factor - 1.0)).roundToInt().toDouble()
}

fun calculateEffectiveFortnightlyRepayment(
    loan: LoanConfig,
    rateOverridePa: Double = loan.interestRatePa
): Double {
    val effectivePrincipal = if (loan.originalLoanAmount > 0.0) {
        max(loan.originalLoanAmount, loan.startLoanBalance)
    } else {
        loan.startLoanBalance
    }
    if (effectivePrincipal <= 0.0) return 0.0
    val totalFn = (loan.originalTermYears * 26).coerceAtLeast(26)
    return calculateFortnightlyRepayment(effectivePrincipal, rateOverridePa, totalFn)
}

fun generateAmortizationSchedule(
    config: LoanConfig,
    maxFortnights: Int = 780
): List<AmortizationRow> {
    val rows = mutableListOf<AmortizationRow>()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    var currentDate = try {
        LocalDate.parse(config.startDateStr, formatter)
    } catch (_: Exception) {
        LocalDate.now()
    }

    var balance = config.startLoanBalance.coerceAtLeast(0.0)
    var accrued = 0.0
    val fnPayment = calculateEffectiveFortnightlyRepayment(config)

    rows.add(
        AmortizationRow(
            fortnightNumber = 0,
            paymentDate = currentDate.format(formatter),
            payment = 0.0,
            monthlyInterestCharged = 0.0,
            accruedInterest = 0.0,
            closingBalance = balance
        )
    )

    for (fn in 1..maxFortnights) {
        if (balance <= 0.01) break
        val prevDate = currentDate
        currentDate = prevDate.plusDays(14)

        val netBalance = max(0.0, balance - config.offsetBalance)
        val fnInterest = netBalance * (config.interestRatePa / 100.0) * 14.0 / 365.25

        val monthlyCharged: Double
        if (currentDate.monthValue != prevDate.monthValue) {
            monthlyCharged = accrued + fnInterest
            accrued = 0.0
        } else {
            monthlyCharged = 0.0
            accrued += fnInterest
        }

        val actualPayment = if (balance + monthlyCharged < fnPayment) balance + monthlyCharged else fnPayment
        balance = max(0.0, balance - actualPayment + monthlyCharged)

        rows.add(
            AmortizationRow(
                fortnightNumber = fn,
                paymentDate = currentDate.format(formatter),
                payment = actualPayment,
                monthlyInterestCharged = monthlyCharged,
                accruedInterest = accrued,
                closingBalance = balance
            )
        )
    }
    return rows
}

fun getElapsedFortnightsSinceStart(startDateStr: String): Int {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val start = LocalDate.parse(startDateStr, formatter)
        val today = LocalDate.now()
        val days = ChronoUnit.DAYS.between(start, today)
        if (days <= 0) 0 else (days / 14).toInt()
    } catch (_: Exception) {
        0
    }
}

enum class CustomLeverImpactType(val label: String, val unitSuffix: String) {
    WEEKLY_CASHFLOW("Weekly Cashflow / Savings ($/wk)", "/wk"),
    ANNUAL_ASSET_CHANGE("Annual Investment / Asset Boost ($/yr)", "/yr"),
    LUMP_SUM_OFFSET("Upfront Lump Sum to Offset / Cash ($)", " lump"),
    INTEREST_RATE_DELTA("Extra Loan Interest Rate Delta (% p.a.)", "%")
}

data class CustomWhatIfLever(
    val id: String,
    val title: String,
    val impactType: CustomLeverImpactType,
    val minValue: Float,
    val maxValue: Float,
    val stepSize: Float,
    val currentValue: Float,
    val isEnabled: Boolean = true
)

data class WhatIfProjectionResult(
    val pathASeries: List<Double>,
    val pathBSeries: List<Double>,
    val deficitYear: Int?,
    val weeklyRateDeltaProp1: Double,
    val weeklyRateDeltaProp2: Double,
    val totalWeeklyRateDeltaCost: Double,
    val customWeeklyCashflowTotal: Double,
    val customAnnualAssetBoostTotal: Double,
    val customLumpSumTotal: Double,
    val customRateDeltaTotal: Float
)

fun computeWhatIfProjection(
    horizonYears: Int,
    basePropertyVal: Double,
    baseSharesAndSuperVal: Double,
    baseOtherAssetsVal: Double,
    baseOffsetsVal: Double,
    baseLiabilitiesVal: Double,
    baseSurplusWeekly: Double,
    prop1Loan: LoanConfig,
    prop2Loan: LoanConfig,
    hasSecondProperty: Boolean,
    showLoans: Boolean,
    lifestyleAdjustmentWeekly: Float,
    rateShockProp1: Float,
    rateShockProp2: Float,
    rentalIncomeGrowthWeekly: Float,
    superBoostWeekly: Float,
    customLevers: List<CustomWhatIfLever>,
    milestones: List<LifeMilestoneBlock>
): WhatIfProjectionResult {
    val activeCustomLevers = customLevers.filter { it.isEnabled }
    val customWeeklyCashflow = activeCustomLevers
        .filter { it.impactType == CustomLeverImpactType.WEEKLY_CASHFLOW }
        .sumOf { it.currentValue.toDouble() }
    val customAnnualAssetBoost = activeCustomLevers
        .filter { it.impactType == CustomLeverImpactType.ANNUAL_ASSET_CHANGE }
        .sumOf { it.currentValue.toDouble() }
    val customLumpSum = activeCustomLevers
        .filter { it.impactType == CustomLeverImpactType.LUMP_SUM_OFFSET }
        .sumOf { it.currentValue.toDouble() }
    val customRateDelta = activeCustomLevers
        .filter { it.impactType == CustomLeverImpactType.INTEREST_RATE_DELTA }
        .sumOf { it.currentValue.toDouble() }.toFloat()

    val effectiveShock1 = rateShockProp1 + customRateDelta
    val effectiveShock2 = rateShockProp2 + customRateDelta

    val weeklyRateDelta1 = if (showLoans) {
        val basePmt1 = calculateEffectiveFortnightlyRepayment(prop1Loan)
        val shockedRate1 = (prop1Loan.interestRatePa + effectiveShock1).coerceAtLeast(0.5)
        val shockedPmt1 = calculateEffectiveFortnightlyRepayment(prop1Loan, shockedRate1)
        (shockedPmt1 - basePmt1) / 2.0
    } else 0.0

    val weeklyRateDelta2 = if (showLoans && hasSecondProperty) {
        val basePmt2 = calculateEffectiveFortnightlyRepayment(prop2Loan)
        val shockedRate2 = (prop2Loan.interestRatePa + effectiveShock2).coerceAtLeast(0.5)
        val shockedPmt2 = calculateEffectiveFortnightlyRepayment(prop2Loan, shockedRate2)
        (shockedPmt2 - basePmt2) / 2.0
    } else if (rateShockProp2 != 0f) {
        // Allow Property 2 Rate Lever to model a $500k investment loan if 2nd property is not yet enabled
        val fallbackLoan = if (prop2Loan.startLoanBalance > 0.0 || prop2Loan.originalLoanAmount > 0.0) {
            prop2Loan
        } else {
            LoanConfig("Property 2", LocalDate.now().toString(), 500000.0, 30, 500000.0, 30.0, 6.25, 0.0)
        }
        val basePmt2 = calculateEffectiveFortnightlyRepayment(fallbackLoan)
        val shockedRate2 = (fallbackLoan.interestRatePa + effectiveShock2).coerceAtLeast(0.5)
        val shockedPmt2 = calculateEffectiveFortnightlyRepayment(fallbackLoan, shockedRate2)
        (shockedPmt2 - basePmt2) / 2.0
    } else 0.0

    val totalWeeklyRateCost = weeklyRateDelta1 + weeklyRateDelta2

    val baseAssets = basePropertyVal + baseSharesAndSuperVal + baseOtherAssetsVal + baseOffsetsVal
    val baseNetWorth = baseAssets - baseLiabilitiesVal

    // Path A: Status Quo Baseline Compounding Trajectory
    val pathASeries = mutableListOf<Double>()
    var aProp = basePropertyVal
    var aSharesSuper = baseSharesAndSuperVal
    var aLiquid = baseOffsetsVal
    var aLiab = baseLiabilitiesVal
    pathASeries.add(baseNetWorth)

    val safeHorizon = horizonYears.coerceIn(1, 40)
    repeat(safeHorizon) {
        val annualSurplus = baseSurplusWeekly * 52.0
        aProp *= 1.04
        aSharesSuper *= 1.075
        aLiquid += annualSurplus
        val debtPaydown = (aLiab * 0.035).coerceAtMost(aLiab)
        aLiab = (aLiab - debtPaydown).coerceAtLeast(0.0)
        val nw = (aProp + aSharesSuper + baseOtherAssetsVal + aLiquid) - aLiab
        pathASeries.add(nw)
    }

    // Path B: Simulated What-If Plan Compounding Trajectory
    val pathBSeries = mutableListOf<Double>()
    var bProp = basePropertyVal
    var bSharesSuper = baseSharesAndSuperVal
    var bOther = baseOtherAssetsVal
    var bLiquid = baseOffsetsVal + customLumpSum
    var bLiab = baseLiabilitiesVal
    var bExtraSuper = 0.0
    var foundDeficitYear: Int? = null

    pathBSeries.add(baseNetWorth + customLumpSum)

    val activeMilestones = milestones.filter { it.isEnabled }
    for (year in 1..safeHorizon) {
        var activeWeeklyDelta =
            lifestyleAdjustmentWeekly.toDouble() - totalWeeklyRateCost + rentalIncomeGrowthWeekly.toDouble() + customWeeklyCashflow
        var propAssetAdd = 0.0
        var investAssetAdd = 0.0
        var otherAssetAdd = 0.0
        var liabAdd = 0.0

        activeMilestones.forEach { m ->
            if (year >= m.startYear && year < m.startYear + m.durationYears) {
                activeWeeklyDelta += m.weeklyCashflowImpact
            }
            if (year == m.startYear) {
                when (m.category) {
                    MilestoneCategory.PROPERTY -> propAssetAdd += m.assetImpact
                    MilestoneCategory.INVESTMENT -> investAssetAdd += m.assetImpact
                    else -> otherAssetAdd += m.assetImpact
                }
                liabAdd += m.liabilityImpact
            }
        }

        val annualNetSurplus = (baseSurplusWeekly + activeWeeklyDelta) * 52.0
        val annualSuperBoost = superBoostWeekly.toDouble() * 52.0

        bProp = (bProp + propAssetAdd) * 1.04
        bSharesSuper = (bSharesSuper + investAssetAdd + customAnnualAssetBoost) * 1.075
        bExtraSuper = (bExtraSuper + annualSuperBoost) * 1.08
        bOther += otherAssetAdd

        bLiquid += annualNetSurplus
        if (bLiquid < 0.0 && foundDeficitYear == null) {
            foundDeficitYear = year
        }

        val debtPaydown = ((bLiab + liabAdd).coerceAtLeast(0.0) * 0.035).coerceAtMost((bLiab + liabAdd).coerceAtLeast(0.0))
        bLiab = (bLiab + liabAdd - debtPaydown).coerceAtLeast(0.0)

        val nw = (bProp + bSharesSuper + bExtraSuper + bOther + bLiquid) - bLiab
        pathBSeries.add(nw)
    }

    return WhatIfProjectionResult(
        pathASeries = pathASeries,
        pathBSeries = pathBSeries,
        deficitYear = foundDeficitYear,
        weeklyRateDeltaProp1 = weeklyRateDelta1,
        weeklyRateDeltaProp2 = weeklyRateDelta2,
        totalWeeklyRateDeltaCost = totalWeeklyRateCost,
        customWeeklyCashflowTotal = customWeeklyCashflow,
        customAnnualAssetBoostTotal = customAnnualAssetBoost,
        customLumpSumTotal = customLumpSum,
        customRateDeltaTotal = customRateDelta
    )
}

// ============================================================================
// MAIN APPLICATION COMPOSABLE
// ============================================================================
@Composable
fun ExpenseTrackerApp(
    currentUser: FirebaseUser,
    onSignOutUser: () -> Unit,
    isExpanded: Boolean,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val userPrefKey = "expnz_user_${currentUser.uid}"
    val sharedPrefs = remember(currentUser.uid) { context.getSharedPreferences(userPrefKey, Context.MODE_PRIVATE) }
    val db = remember { Firebase.firestore }
    val currentUserEmail = remember(currentUser.email) { (currentUser.email ?: "").trim().lowercase() }

    var activeHouseholdUid by remember(currentUser.uid) {
        mutableStateOf(
            sharedPrefs.getString("linked_household_uid", null)?.takeIf { it.isNotBlank() } ?: currentUser.uid
        )
    }
    val userDocRef = remember(activeHouseholdUid) {
        db.collection("users").document(activeHouseholdUid).collection("wealth_data").document("main")
    }

    fun loadAppConfigFromPrefs(): UserAppConfig {
        return UserAppConfig(
            isSetupCompleted = sharedPrefs.getBoolean("setup_completed", false),
            isCoupleMode = sharedPrefs.getBoolean("is_couple_mode", false),
            person1Name = sharedPrefs.getString("person1_name", "Me") ?: "Me",
            person2Name = sharedPrefs.getString("person2_name", "Partner") ?: "Partner",
            person1ColorHex = sharedPrefs.getLong("person1_color", 0xFFE38391L),
            person2ColorHex = sharedPrefs.getLong("person2_color", 0xFF34D399L),
            property1Name = sharedPrefs.getString("property1_name", "Primary Home") ?: "Primary Home",
            property2Name = sharedPrefs.getString("property2_name", "Investment Property") ?: "Investment Property",
            property1Url = sharedPrefs.getString("property1_url", "") ?: "",
            property2Url = sharedPrefs.getString("property2_url", "") ?: "",
            isProperty1Rented = sharedPrefs.getBoolean("is_prop1_rented", false),
            isProperty2Rented = sharedPrefs.getBoolean("is_prop2_rented", false),
            hasSecondProperty = sharedPrefs.getBoolean("has_second_property", false),
            primaryRentWeekly = sharedPrefs.getFloat("primary_rent_weekly", 0f).toDouble(),
            secondaryRentWeekly = sharedPrefs.getFloat("secondary_rent_weekly", 0f).toDouble(),
            showTotalWealth = sharedPrefs.getBoolean("mod_total_wealth", true),
            showCashFlow = sharedPrefs.getBoolean("mod_cash_flow", true),
            showProperties = sharedPrefs.getBoolean("mod_properties", true),
            showSuperAndShares = sharedPrefs.getBoolean("mod_super_shares", true),
            showOtherAssets = sharedPrefs.getBoolean("mod_other_assets", true),
            showLoans = sharedPrefs.getBoolean("mod_loans", true),
            showWhatIf = sharedPrefs.getBoolean("mod_what_if", true),
            useExpnzAi = sharedPrefs.getBoolean("use_expnz_ai", true),
            colorWealthHex = sharedPrefs.getLong("color_wealth", 0xFFFBBF24L),
            colorCashFlowHex = sharedPrefs.getLong("color_cashflow", 0xFF34D399L),
            colorPropertiesHex = sharedPrefs.getLong("color_properties", 0xFF38BDF8L),
            colorSuperSharesHex = sharedPrefs.getLong("color_supershares", 0xFFC084FCL),
            colorOtherAssetsHex = sharedPrefs.getLong("color_otherassets", 0xFFE38391L),
            colorLoansHex = sharedPrefs.getLong("color_loans", 0xFFF87171L),
            colorWhatIfHex = sharedPrefs.getLong("color_whatif", 0xFF818CF8L),
            primaryOwnerEmail = sharedPrefs.getString("primary_owner_email", currentUserEmail) ?: currentUserEmail,
            partnerEmail = sharedPrefs.getString("partner_email", "") ?: "",
            sharedHouseholdId = sharedPrefs.getString("shared_household_id", activeHouseholdUid) ?: activeHouseholdUid
        )
    }

    var appConfig by remember { mutableStateOf(loadAppConfigFromPrefs()) }
    var showColorPickerDialog by remember { mutableStateOf(false) }

    val isLoggedInAsPartner = appConfig.isCoupleMode && (
        activeHouseholdUid != currentUser.uid ||
            (currentUserEmail.isNotBlank() && appConfig.partnerEmail.isNotBlank() &&
                currentUserEmail.equals(appConfig.partnerEmail.trim(), ignoreCase = true))
        )
    val activeLoggedInPersonName = if (isLoggedInAsPartner) appConfig.person2Name else appConfig.person1Name

    val geminiMessages = remember {
        mutableStateListOf(
            ChatMessage(
                text = "Hello $activeLoggedInPersonName. I am the EXPNZ AI Assistant. Looking at your EXPNZ Wealth overview, how can I assist you with your goals today?",
                isFromUser = false
            )
        )
    }
    LaunchedEffect(activeLoggedInPersonName) {
        if (geminiMessages.size == 1 && !geminiMessages[0].isFromUser) {
            geminiMessages[0] = ChatMessage(
                text = "Hello $activeLoggedInPersonName. I am the EXPNZ AI Assistant. Looking at your EXPNZ Wealth overview, how can I assist you with your goals today?",
                isFromUser = false
            )
        }
    }

    val person1Color = Color(appConfig.person1ColorHex)
    val person2Color = Color(appConfig.person2ColorHex)

    fun updateFirestore(key: String, value: Any) {
        userDocRef.update(key, value).addOnFailureListener {
            userDocRef.set(hashMapOf(key to value), SetOptions.merge())
        }
    }

    fun persistAppConfigToLocalPrefs(cfg: UserAppConfig) {
        sharedPrefs.edit {
            putBoolean("setup_completed", cfg.isSetupCompleted)
            putBoolean("is_couple_mode", cfg.isCoupleMode)
            putString("person1_name", cfg.person1Name)
            putString("person2_name", cfg.person2Name)
            putLong("person1_color", cfg.person1ColorHex)
            putLong("person2_color", cfg.person2ColorHex)
            putString("property1_name", cfg.property1Name)
            putString("property2_name", cfg.property2Name)
            putString("property1_url", cfg.property1Url)
            putString("property2_url", cfg.property2Url)
            putBoolean("is_prop1_rented", cfg.isProperty1Rented)
            putBoolean("is_prop2_rented", cfg.isProperty2Rented)
            putBoolean("has_second_property", cfg.hasSecondProperty)
            putFloat("primary_rent_weekly", cfg.primaryRentWeekly.toFloat())
            putFloat("secondary_rent_weekly", cfg.secondaryRentWeekly.toFloat())
            putBoolean("mod_total_wealth", cfg.showTotalWealth)
            putBoolean("mod_cash_flow", cfg.showCashFlow)
            putBoolean("mod_properties", cfg.showProperties)
            putBoolean("mod_super_shares", cfg.showSuperAndShares)
            putBoolean("mod_other_assets", cfg.showOtherAssets)
            putBoolean("mod_loans", cfg.showLoans)
            putBoolean("mod_what_if", cfg.showWhatIf)
            putBoolean("use_expnz_ai", cfg.useExpnzAi)
            putLong("color_wealth", cfg.colorWealthHex)
            putLong("color_cashflow", cfg.colorCashFlowHex)
            putLong("color_properties", cfg.colorPropertiesHex)
            putLong("color_supershares", cfg.colorSuperSharesHex)
            putLong("color_otherassets", cfg.colorOtherAssetsHex)
            putLong("color_loans", cfg.colorLoansHex)
            putLong("color_whatif", cfg.colorWhatIfHex)
            putString("primary_owner_email", cfg.primaryOwnerEmail)
            putString("partner_email", cfg.partnerEmail)
            putString("shared_household_id", cfg.sharedHouseholdId)
        }
    }

    fun saveAppConfig(newConfig: UserAppConfig) {
        val enrichedConfig = newConfig.copy(
            primaryOwnerEmail = newConfig.primaryOwnerEmail.ifBlank {
                if (!isLoggedInAsPartner) currentUserEmail else ""
            },
            sharedHouseholdId = activeHouseholdUid
        )
        appConfig = enrichedConfig
        persistAppConfigToLocalPrefs(enrichedConfig)
        val configMap = hashMapOf(
            "isSetupCompleted" to enrichedConfig.isSetupCompleted,
            "isCoupleMode" to enrichedConfig.isCoupleMode,
            "person1Name" to enrichedConfig.person1Name,
            "person2Name" to enrichedConfig.person2Name,
            "person1ColorHex" to enrichedConfig.person1ColorHex,
            "person2ColorHex" to enrichedConfig.person2ColorHex,
            "property1Name" to enrichedConfig.property1Name,
            "property2Name" to enrichedConfig.property2Name,
            "property1Url" to enrichedConfig.property1Url,
            "property2Url" to enrichedConfig.property2Url,
            "isProperty1Rented" to enrichedConfig.isProperty1Rented,
            "isProperty2Rented" to enrichedConfig.isProperty2Rented,
            "hasSecondProperty" to enrichedConfig.hasSecondProperty,
            "primaryRentWeekly" to enrichedConfig.primaryRentWeekly,
            "secondaryRentWeekly" to enrichedConfig.secondaryRentWeekly,
            "showTotalWealth" to enrichedConfig.showTotalWealth,
            "showCashFlow" to enrichedConfig.showCashFlow,
            "showProperties" to enrichedConfig.showProperties,
            "showSuperAndShares" to enrichedConfig.showSuperAndShares,
            "showOtherAssets" to enrichedConfig.showOtherAssets,
            "showLoans" to enrichedConfig.showLoans,
            "showWhatIf" to enrichedConfig.showWhatIf,
            "useExpnzAi" to enrichedConfig.useExpnzAi,
            "colorWealthHex" to enrichedConfig.colorWealthHex,
            "colorCashFlowHex" to enrichedConfig.colorCashFlowHex,
            "colorPropertiesHex" to enrichedConfig.colorPropertiesHex,
            "colorSuperSharesHex" to enrichedConfig.colorSuperSharesHex,
            "colorOtherAssetsHex" to enrichedConfig.colorOtherAssetsHex,
            "colorLoansHex" to enrichedConfig.colorLoansHex,
            "colorWhatIfHex" to enrichedConfig.colorWhatIfHex,
            "primaryOwnerEmail" to enrichedConfig.primaryOwnerEmail,
            "partnerEmail" to enrichedConfig.partnerEmail,
            "sharedHouseholdId" to enrichedConfig.sharedHouseholdId
        )
        updateFirestore("app_config", configMap)

        // Publish the invite lookup & household code in Firestore when owned by currentUser
        if (activeHouseholdUid == currentUser.uid) {
            val cleanPartnerEmail = enrichedConfig.partnerEmail.trim().lowercase()
            val inviteCode = currentUser.uid.take(6).uppercase()
            val invitePayload = hashMapOf(
                "householdOwnerUid" to currentUser.uid,
                "primaryOwnerName" to enrichedConfig.person1Name,
                "primaryOwnerEmail" to enrichedConfig.primaryOwnerEmail,
                "partnerName" to enrichedConfig.person2Name,
                "partnerEmail" to cleanPartnerEmail,
                "inviteCode" to inviteCode,
                "updatedAt" to System.currentTimeMillis()
            )
            db.collection("household_codes").document(inviteCode).set(invitePayload)
            if (cleanPartnerEmail.isNotBlank() && cleanPartnerEmail.contains("@")) {
                db.collection("partner_invites").document(cleanPartnerEmail).set(invitePayload)
            }
        }
    }

    fun linkToPartnerHousehold(targetHouseholdUid: String) {
        val cleanUid = targetHouseholdUid.trim()
        if (cleanUid.isNotBlank() && cleanUid != activeHouseholdUid) {
            sharedPrefs.edit { putString("linked_household_uid", cleanUid) }
            db.collection("users").document(currentUser.uid)
                .collection("wealth_data").document("main")
                .set(hashMapOf("linked_household_uid" to cleanUid), SetOptions.merge())
                .addOnCompleteListener {
                    activeHouseholdUid = cleanUid
                }
        }
    }

    fun joinHouseholdByCodeOrEmail(input: String, onResult: (Boolean, String) -> Unit) {
        val trimmed = input.trim()
        if (trimmed.isBlank()) {
            onResult(false, "Please enter a valid 6-digit Invite Code or Partner Email.")
            return
        }
        if (trimmed.contains("@")) {
            db.collection("partner_invites").document(trimmed.lowercase()).get()
                .addOnSuccessListener { doc ->
                    val ownerUid = doc.getString("householdOwnerUid")
                    if (!ownerUid.isNullOrBlank()) {
                        linkToPartnerHousehold(ownerUid)
                        onResult(true, "Connected to shared household!")
                    } else {
                        onResult(false, "No invitation found for $trimmed. Ask your partner to invite this email in Settings.")
                    }
                }
                .addOnFailureListener {
                    onResult(false, "Could not verify invitation: ${it.localizedMessage ?: "Network error"}")
                }
        } else {
            val codeKey = trimmed.uppercase()
            db.collection("household_codes").document(codeKey).get()
                .addOnSuccessListener { doc ->
                    val ownerUid = doc.getString("householdOwnerUid")
                    if (!ownerUid.isNullOrBlank()) {
                        linkToPartnerHousehold(ownerUid)
                        onResult(true, "Connected to shared household ($codeKey)!")
                    } else {
                        onResult(false, "Invalid Invite Code '$codeKey'. Check the 6-character code in your partner's Settings.")
                    }
                }
                .addOnFailureListener {
                    onResult(false, "Could not verify code: ${it.localizedMessage ?: "Network error"}")
                }
        }
    }

    // Default starter items for new public users
    val defaultPerson1Items = emptyList<CashflowItem>()
    val defaultPerson2Items = emptyList<CashflowItem>()
    val defaultProp1Items = emptyList<CashflowItem>()
    val defaultProp2Items = emptyList<CashflowItem>()
    val defaultShares = emptyList<ShareParcel>()
    val defaultSuperAccounts = emptyList<SuperAccount>()
    val defaultOtherAssets = emptyList<OtherAsset>()

    fun loadListFromPrefs(key: String, defaults: List<CashflowItem>): List<CashflowItem> {
        val raw = sharedPrefs.getString(key, null) ?: return defaults
        return try {
            val arr = JSONArray(raw)
            val list = mutableListOf<CashflowItem>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                list.add(
                    CashflowItem(
                        id = o.optString("id", "item_$i"),
                        name = o.getString("name"),
                        weeklyAmount = o.getDouble("amount"),
                        iconName = o.optString("iconName", "Default")
                    )
                )
            }
            if (list.isEmpty()) defaults else list
        } catch (_: Exception) {
            defaults
        }
    }

    fun persistListToLocalPrefs(key: String, list: List<CashflowItem>) {
        val jsonArr = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("id", it.id)
            obj.put("name", it.name)
            obj.put("amount", it.weeklyAmount)
            obj.put("iconName", it.iconName)
            jsonArr.put(obj)
        }
        sharedPrefs.edit { putString(key, jsonArr.toString()) }
    }

    fun saveList(key: String, list: List<CashflowItem>) {
        persistListToLocalPrefs(key, list)

        val serializedList = list.map {
            hashMapOf(
                "id" to it.id,
                "name" to it.name,
                "amount" to it.weeklyAmount,
                "isLockedMortgageOrPropertyLink" to it.isLockedMortgageOrPropertyLink,
                "iconName" to it.iconName
            )
        }
        updateFirestore(key, serializedList)
    }

    fun saveLoanConfig(key: String, cfg: LoanConfig) {
        val serializedCfg = hashMapOf(
            "name" to cfg.name,
            "startDateStr" to cfg.startDateStr,
            "originalLoanAmount" to cfg.originalLoanAmount,
            "originalTermYears" to cfg.originalTermYears,
            "startLoanBalance" to cfg.startLoanBalance,
            "remainingTermYears" to cfg.remainingTermYears,
            "interestRatePa" to cfg.interestRatePa,
            "offsetBalance" to cfg.offsetBalance
        )
        updateFirestore(key, serializedCfg)
    }

    // State Lists & Incomes
    var isWealthHidden by rememberSaveable { mutableStateOf(false) }

    var person1Income by remember { mutableDoubleStateOf(sharedPrefs.getFloat("person1_inc", 0f).toDouble()) }
    var person2Income by remember { mutableDoubleStateOf(sharedPrefs.getFloat("person2_inc", 0f).toDouble()) }

    var person1Items by remember { mutableStateOf(loadListFromPrefs("person1_items", defaultPerson1Items)) }
    var person2Items by remember { mutableStateOf(loadListFromPrefs("person2_items", defaultPerson2Items)) }
    var prop1Items by remember { mutableStateOf(loadListFromPrefs("prop1_items", defaultProp1Items)) }
    var prop2Items by remember { mutableStateOf(loadListFromPrefs("prop2_items", defaultProp2Items)) }
    var sharesList by remember { mutableStateOf(defaultShares) }
    var superAccountsList by remember { mutableStateOf(defaultSuperAccounts) }
    var otherAssetsList by remember { mutableStateOf(defaultOtherAssets) }

    var prop1EstimatedValue by remember { mutableDoubleStateOf(sharedPrefs.getFloat("prop1_val", 0f).toDouble()) }
    var prop2EstimatedValue by remember { mutableDoubleStateOf(sharedPrefs.getFloat("prop2_val", 0f).toDouble()) }

    var prop1Loan by remember {
        mutableStateOf(
            LoanConfig(
                name = appConfig.property1Name,
                startDateStr = sharedPrefs.getString("p1_loan_start", LocalDate.now().toString()) ?: LocalDate.now().toString(),
                originalLoanAmount = sharedPrefs.getFloat("p1_orig_loan", 0f).toDouble(),
                originalTermYears = 30,
                startLoanBalance = sharedPrefs.getFloat("p1_start_bal", 0f).toDouble(),
                remainingTermYears = 30.0,
                interestRatePa = sharedPrefs.getFloat("p1_rate", 0f).toDouble(),
                offsetBalance = sharedPrefs.getFloat("p1_offset", 0f).toDouble()
            )
        )
    }

    var prop2Loan by remember {
        mutableStateOf(
            LoanConfig(
                name = appConfig.property2Name,
                startDateStr = sharedPrefs.getString("p2_loan_start", LocalDate.now().toString()) ?: LocalDate.now().toString(),
                originalLoanAmount = sharedPrefs.getFloat("p2_orig_loan", 0f).toDouble(),
                originalTermYears = 30,
                startLoanBalance = sharedPrefs.getFloat("p2_start_bal", 0f).toDouble(),
                remainingTermYears = 30.0,
                interestRatePa = sharedPrefs.getFloat("p2_rate", 0f).toDouble(),
                offsetBalance = sharedPrefs.getFloat("p2_offset", 0f).toDouble()
            )
        )
    }

    fun saveSuperAccounts(list: List<SuperAccount>) {
        superAccountsList = list
        val serialized = list.map {
            hashMapOf(
                "id" to it.id,
                "fundName" to it.fundName,
                "owner" to it.owner,
                "balance" to it.balance
            )
        }
        updateFirestore("super_accounts", serialized)
    }

    fun saveOtherAssets(list: List<OtherAsset>) {
        otherAssetsList = list
        val serialized = list.map {
            hashMapOf(
                "id" to it.id,
                "name" to it.name,
                "owner" to it.owner,
                "value" to it.value
            )
        }
        updateFirestore("other_assets", serialized)
    }

    fun saveSharesList(list: List<ShareParcel>) {
        sharesList = list
        val serializedList = list.map {
            hashMapOf(
                "id" to it.id,
                "code" to it.code,
                "owner" to it.owner,
                "units" to it.units,
                "purchasePrice" to it.purchasePrice,
                "currentPrice" to it.currentPrice
            )
        }
        updateFirestore("shares_portfolio", serializedList)
    }

    fun savePropertyValuations(p1Val: Double, p2Val: Double) {
        prop1EstimatedValue = p1Val
        prop2EstimatedValue = p2Val
        sharedPrefs.edit {
            putFloat("prop1_val", p1Val.toFloat())
            putFloat("prop2_val", p2Val.toFloat())
        }
        updateFirestore("prop1_market_value", p1Val)
        updateFirestore("prop2_market_value", p2Val)
    }

    fun saveProp1Loan(updated: LoanConfig) {
        prop1Loan = updated
        sharedPrefs.edit {
            putString("p1_loan_start", updated.startDateStr)
            putFloat("p1_orig_loan", updated.originalLoanAmount.toFloat())
            putFloat("p1_start_bal", updated.startLoanBalance.toFloat())
            putFloat("p1_rate", updated.interestRatePa.toFloat())
            putFloat("p1_offset", updated.offsetBalance.toFloat())
        }
        saveLoanConfig("prop1_loan", updated)
    }

    fun saveProp2Loan(updated: LoanConfig) {
        prop2Loan = updated
        sharedPrefs.edit {
            putString("p2_loan_start", updated.startDateStr)
            putFloat("p2_orig_loan", updated.originalLoanAmount.toFloat())
            putFloat("p2_start_bal", updated.startLoanBalance.toFloat())
            putFloat("p2_rate", updated.interestRatePa.toFloat())
            putFloat("p2_offset", updated.offsetBalance.toFloat())
        }
        saveLoanConfig("prop2_loan", updated)
    }

    // Biometric Security State (Unlocked once per session, never times out)
    var isBiometricEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("biometric_enabled", false)) }
    var isBiometricAuthenticated by remember { mutableStateOf(!isBiometricEnabled) }
    var lastActiveTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Auto-discover if this logged-in email was invited as a partner to a shared household
    LaunchedEffect(currentUser.uid, currentUserEmail) {
        // 0. Ensure this user's own 6-character Household Invite Code is registered in Firestore
        if (activeHouseholdUid == currentUser.uid) {
            val cleanPartnerEmail = appConfig.partnerEmail.trim().lowercase()
            val inviteCode = currentUser.uid.take(6).uppercase()
            val invitePayload = hashMapOf(
                "householdOwnerUid" to currentUser.uid,
                "primaryOwnerName" to appConfig.person1Name,
                "primaryOwnerEmail" to appConfig.primaryOwnerEmail.ifBlank { currentUserEmail },
                "partnerName" to appConfig.person2Name,
                "partnerEmail" to cleanPartnerEmail,
                "inviteCode" to inviteCode,
                "updatedAt" to System.currentTimeMillis()
            )
            db.collection("household_codes").document(inviteCode).set(invitePayload)
            if (cleanPartnerEmail.isNotBlank() && cleanPartnerEmail.contains("@")) {
                db.collection("partner_invites").document(cleanPartnerEmail).set(invitePayload)
            }
        }

        // 1. Check user's own profile document for an existing linked_household_uid
        db.collection("users").document(currentUser.uid)
            .collection("wealth_data").document("main")
            .get()
            .addOnSuccessListener { ownDoc ->
                val linkedUid = ownDoc.getString("linked_household_uid")
                if (!linkedUid.isNullOrBlank() && linkedUid != activeHouseholdUid) {
                    linkToPartnerHousehold(linkedUid)
                }
            }

        // 2. Listen in real time to partner_invites/{currentUserEmail} so any invite links immediately
        if (currentUserEmail.isNotBlank() && currentUserEmail.contains("@")) {
            db.collection("partner_invites").document(currentUserEmail)
                .addSnapshotListener { inviteSnap, err ->
                    if (err != null || inviteSnap == null || !inviteSnap.exists()) return@addSnapshotListener
                    val ownerUid = inviteSnap.getString("householdOwnerUid")
                    if (!ownerUid.isNullOrBlank() && ownerUid != activeHouseholdUid) {
                        linkToPartnerHousehold(ownerUid)
                    }
                }
        }
    }

    // Real-time bidirectional sync from active Household Firestore document (shared between both partners)
    LaunchedEffect(activeHouseholdUid) {
        userDocRef.addSnapshotListener { snapshot, e ->
            if (e != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
            val data = snapshot.data ?: return@addSnapshotListener

            val linkedRedirect = data["linked_household_uid"] as? String
            if (!linkedRedirect.isNullOrBlank() && linkedRedirect != activeHouseholdUid) {
                linkToPartnerHousehold(linkedRedirect)
                return@addSnapshotListener
            }

            @Suppress("UNCHECKED_CAST")
            val cfgMap = data["app_config"] as? Map<String, Any>
            if (cfgMap != null) {
                val remoteCfg = UserAppConfig(
                    isSetupCompleted = cfgMap["isSetupCompleted"] as? Boolean ?: appConfig.isSetupCompleted,
                    isCoupleMode = cfgMap["isCoupleMode"] as? Boolean ?: appConfig.isCoupleMode,
                    person1Name = cfgMap["person1Name"] as? String ?: appConfig.person1Name,
                    person2Name = cfgMap["person2Name"] as? String ?: appConfig.person2Name,
                    person1ColorHex = (cfgMap["person1ColorHex"] as? Number)?.toLong() ?: appConfig.person1ColorHex,
                    person2ColorHex = (cfgMap["person2ColorHex"] as? Number)?.toLong() ?: appConfig.person2ColorHex,
                    property1Name = cfgMap["property1Name"] as? String ?: appConfig.property1Name,
                    property2Name = cfgMap["property2Name"] as? String ?: appConfig.property2Name,
                    property1Url = cfgMap["property1Url"] as? String ?: appConfig.property1Url,
                    property2Url = cfgMap["property2Url"] as? String ?: appConfig.property2Url,
                    isProperty1Rented = cfgMap["isProperty1Rented"] as? Boolean ?: appConfig.isProperty1Rented,
                    isProperty2Rented = cfgMap["isProperty2Rented"] as? Boolean ?: appConfig.isProperty2Rented,
                    hasSecondProperty = cfgMap["hasSecondProperty"] as? Boolean ?: appConfig.hasSecondProperty,
                    primaryRentWeekly = (cfgMap["primaryRentWeekly"] as? Number)?.toDouble() ?: appConfig.primaryRentWeekly,
                    secondaryRentWeekly = (cfgMap["secondaryRentWeekly"] as? Number)?.toDouble() ?: appConfig.secondaryRentWeekly,
                    showTotalWealth = cfgMap["showTotalWealth"] as? Boolean ?: appConfig.showTotalWealth,
                    showCashFlow = cfgMap["showCashFlow"] as? Boolean ?: appConfig.showCashFlow,
                    showProperties = cfgMap["showProperties"] as? Boolean ?: appConfig.showProperties,
                    showSuperAndShares = cfgMap["showSuperAndShares"] as? Boolean ?: appConfig.showSuperAndShares,
                    showOtherAssets = cfgMap["showOtherAssets"] as? Boolean ?: appConfig.showOtherAssets,
                    showLoans = cfgMap["showLoans"] as? Boolean ?: appConfig.showLoans,
                    showWhatIf = cfgMap["showWhatIf"] as? Boolean ?: appConfig.showWhatIf,
                    useExpnzAi = cfgMap["useExpnzAi"] as? Boolean ?: appConfig.useExpnzAi,
                    colorWealthHex = (cfgMap["colorWealthHex"] as? Number)?.toLong() ?: appConfig.colorWealthHex,
                    colorCashFlowHex = (cfgMap["colorCashFlowHex"] as? Number)?.toLong() ?: appConfig.colorCashFlowHex,
                    colorPropertiesHex = (cfgMap["colorPropertiesHex"] as? Number)?.toLong() ?: appConfig.colorPropertiesHex,
                    colorSuperSharesHex = (cfgMap["colorSuperSharesHex"] as? Number)?.toLong() ?: appConfig.colorSuperSharesHex,
                    colorOtherAssetsHex = (cfgMap["colorOtherAssetsHex"] as? Number)?.toLong() ?: appConfig.colorOtherAssetsHex,
                    colorLoansHex = (cfgMap["colorLoansHex"] as? Number)?.toLong() ?: appConfig.colorLoansHex,
                    colorWhatIfHex = (cfgMap["colorWhatIfHex"] as? Number)?.toLong() ?: appConfig.colorWhatIfHex,
                    primaryOwnerEmail = cfgMap["primaryOwnerEmail"] as? String ?: appConfig.primaryOwnerEmail,
                    partnerEmail = cfgMap["partnerEmail"] as? String ?: appConfig.partnerEmail,
                    sharedHouseholdId = cfgMap["sharedHouseholdId"] as? String ?: activeHouseholdUid
                )
                appConfig = remoteCfg
                persistAppConfigToLocalPrefs(remoteCfg)
            }

            (data["person1_inc"] as? Number)?.toDouble()?.let {
                person1Income = it
                sharedPrefs.edit { putFloat("person1_inc", it.toFloat()) }
            }
            (data["person2_inc"] as? Number)?.toDouble()?.let {
                person2Income = it
                sharedPrefs.edit { putFloat("person2_inc", it.toFloat()) }
            }

            @Suppress("UNCHECKED_CAST")
            fun parseList(key: String, current: List<CashflowItem>): List<CashflowItem> {
                val rawList = data[key] as? List<Map<String, Any>> ?: return current
                val parsed = rawList.mapNotNull {
                    try {
                        CashflowItem(
                            id = it["id"] as? String ?: return@mapNotNull null,
                            name = it["name"] as? String ?: return@mapNotNull null,
                            weeklyAmount = (it["amount"] as? Number)?.toDouble() ?: return@mapNotNull null,
                            isLockedMortgageOrPropertyLink = it["isLockedMortgageOrPropertyLink"] as? Boolean ?: false,
                            iconName = it["iconName"] as? String ?: "Default"
                        )
                    } catch (_: Exception) { null }
                }
                persistListToLocalPrefs(key, parsed)
                return parsed
            }

            person1Items = parseList("person1_items", person1Items)
            person2Items = parseList("person2_items", person2Items)
            prop1Items = parseList("prop1_items", prop1Items)
            prop2Items = parseList("prop2_items", prop2Items)

            @Suppress("UNCHECKED_CAST")
            val superRaw = data["super_accounts"] as? List<Map<String, Any>>
            if (superRaw != null) {
                superAccountsList = superRaw.mapNotNull {
                    try {
                        SuperAccount(
                            id = it["id"] as? String ?: return@mapNotNull null,
                            fundName = it["fundName"] as? String ?: return@mapNotNull null,
                            owner = it["owner"] as? String ?: appConfig.person1Name,
                            balance = (it["balance"] as? Number)?.toDouble() ?: 0.0
                        )
                    } catch (_: Exception) { null }
                }
            }

            @Suppress("UNCHECKED_CAST")
            val sharesRaw = data["shares_portfolio"] as? List<Map<String, Any>>
            if (sharesRaw != null) {
                sharesList = sharesRaw.mapNotNull {
                    try {
                        ShareParcel(
                            id = it["id"] as? String ?: return@mapNotNull null,
                            code = it["code"] as? String ?: return@mapNotNull null,
                            owner = it["owner"] as? String ?: appConfig.person1Name,
                            units = (it["units"] as? Number)?.toDouble() ?: 0.0,
                            purchasePrice = (it["purchasePrice"] as? Number)?.toDouble() ?: 0.0,
                            currentPrice = (it["currentPrice"] as? Number)?.toDouble() ?: 0.0
                        )
                    } catch (_: Exception) { null }
                }
            }

            (data["prop1_market_value"] as? Number)?.toDouble()?.let {
                prop1EstimatedValue = it
                sharedPrefs.edit { putFloat("prop1_val", it.toFloat()) }
            }
            (data["prop2_market_value"] as? Number)?.toDouble()?.let {
                prop2EstimatedValue = it
                sharedPrefs.edit { putFloat("prop2_val", it.toFloat()) }
            }

            @Suppress("UNCHECKED_CAST")
            val otherAssetsRaw = data["other_assets"] as? List<Map<String, Any>>
            if (otherAssetsRaw != null) {
                otherAssetsList = otherAssetsRaw.mapNotNull {
                    try {
                        OtherAsset(
                            id = it["id"] as? String ?: return@mapNotNull null,
                            name = it["name"] as? String ?: return@mapNotNull null,
                            owner = it["owner"] as? String ?: appConfig.person1Name,
                            value = (it["value"] as? Number)?.toDouble() ?: 0.0
                        )
                    } catch (_: Exception) { null }
                }
            }

            @Suppress("UNCHECKED_CAST")
            val p1LoanData = data["prop1_loan"] as? Map<String, Any>
            if (p1LoanData != null) {
                prop1Loan = LoanConfig(
                    name = p1LoanData["name"] as? String ?: appConfig.property1Name,
                    startDateStr = p1LoanData["startDateStr"] as? String ?: LocalDate.now().toString(),
                    originalLoanAmount = (p1LoanData["originalLoanAmount"] as? Number)?.toDouble() ?: 500000.0,
                    originalTermYears = (p1LoanData["originalTermYears"] as? Number)?.toInt() ?: 30,
                    startLoanBalance = (p1LoanData["startLoanBalance"] as? Number)?.toDouble() ?: 465000.0,
                    remainingTermYears = (p1LoanData["remainingTermYears"] as? Number)?.toDouble() ?: 28.0,
                    interestRatePa = (p1LoanData["interestRatePa"] as? Number)?.toDouble() ?: 6.29,
                    offsetBalance = (p1LoanData["offsetBalance"] as? Number)?.toDouble() ?: 45000.0
                )
            }

            @Suppress("UNCHECKED_CAST")
            val p2LoanData = data["prop2_loan"] as? Map<String, Any>
            if (p2LoanData != null) {
                prop2Loan = LoanConfig(
                    name = p2LoanData["name"] as? String ?: appConfig.property2Name,
                    startDateStr = p2LoanData["startDateStr"] as? String ?: LocalDate.now().toString(),
                    originalLoanAmount = (p2LoanData["originalLoanAmount"] as? Number)?.toDouble() ?: 320000.0,
                    originalTermYears = (p2LoanData["originalTermYears"] as? Number)?.toInt() ?: 30,
                    startLoanBalance = (p2LoanData["startLoanBalance"] as? Number)?.toDouble() ?: 295000.0,
                    remainingTermYears = (p2LoanData["remainingTermYears"] as? Number)?.toDouble() ?: 27.0,
                    interestRatePa = (p2LoanData["interestRatePa"] as? Number)?.toDouble() ?: 6.49,
                    offsetBalance = (p2LoanData["offsetBalance"] as? Number)?.toDouble() ?: 0.0
                )
            }
        }
    }

    if (isBiometricEnabled && !isBiometricAuthenticated) {
        BiometricLockScreen(
            appSubtitle = "EXPNZ",
            onUnlockClick = {
                val fragmentActivity = context as? FragmentActivity
                if (fragmentActivity != null) {
                    showBiometricPrompt(
                        activity = fragmentActivity,
                        title = "Unlock EXPNZ",
                        subtitle = "Scan fingerprint to access your financial data",
                        onSuccess = {
                            isBiometricAuthenticated = true
                            lastActiveTime = System.currentTimeMillis()
                        },
                        onError = { err ->
                            Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            onSignOutClick = onSignOutUser
        )

        LaunchedEffect(Unit) {
            val fragmentActivity = context as? FragmentActivity
            if (fragmentActivity != null) {
                showBiometricPrompt(
                    activity = fragmentActivity,
                    title = "Unlock EXPNZ",
                    subtitle = "Scan fingerprint to access your financial data",
                    onSuccess = {
                        isBiometricAuthenticated = true
                        lastActiveTime = System.currentTimeMillis()
                    },
                    onError = { }
                )
            }
        }
        return
    }

    // FIRST-TIME SETUP WIZARD CHECK
    if (!appConfig.isSetupCompleted) {
        OnboardingSetupScreen(
            initialConfig = appConfig,
            initialPerson1Income = person1Income,
            initialPerson2Income = person2Income,
            initialProp1Value = prop1EstimatedValue,
            initialProp1LoanBalance = prop1Loan.startLoanBalance,
            initialProp1Rate = prop1Loan.interestRatePa,
            initialProp1Offset = prop1Loan.offsetBalance,
            initialPrimaryRent = appConfig.primaryRentWeekly,
            onCompleteSetup = { completedConfig, p1Inc, p2Inc, p1Val, p1LoanBal, p1Rate, p1Off, primaryRent, customSupers, customShares, customOtherAssets ->
                person1Income = p1Inc
                person2Income = p2Inc
                sharedPrefs.edit {
                    putFloat("person1_inc", p1Inc.toFloat())
                    putFloat("person2_inc", p2Inc.toFloat())
                    putFloat("primary_rent_weekly", primaryRent.toFloat())
                }
                updateFirestore("person1_inc", p1Inc)
                updateFirestore("person2_inc", p2Inc)
                updateFirestore("primary_rent_weekly", primaryRent)

                savePropertyValuations(p1Val, prop2EstimatedValue)
                saveProp1Loan(
                    prop1Loan.copy(
                        name = completedConfig.property1Name,
                        originalLoanAmount = max(p1LoanBal, prop1Loan.originalLoanAmount),
                        startLoanBalance = p1LoanBal,
                        interestRatePa = p1Rate,
                        offsetBalance = p1Off
                    )
                )
                saveProp2Loan(prop2Loan.copy(name = completedConfig.property2Name))

                saveSuperAccounts(customSupers)

                val updatedAssets = customOtherAssets.ifEmpty { otherAssetsList.map { it.copy(owner = completedConfig.person1Name) } }
                saveOtherAssets(updatedAssets)

                saveSharesList(customShares)

                saveAppConfig(completedConfig.copy(isSetupCompleted = true, primaryRentWeekly = primaryRent))
            }
        )
        return
    }

    // Build Dynamic Active Navigation Sections based on User Toggles (Loans moved into Settings)
    val activeSections = remember(appConfig) {
        buildList {
            if (appConfig.showTotalWealth) add(AppSection.TOTAL_WEALTH)
            if (appConfig.showCashFlow) add(AppSection.CASH_FLOW)
            if (appConfig.showProperties) add(AppSection.PROPERTIES)
            if (appConfig.showSuperAndShares) add(AppSection.SUPER_SHARES)
            if (appConfig.showOtherAssets) add(AppSection.OTHER_ASSETS)
            if (appConfig.showWhatIf) add(AppSection.WHAT_IF)
            add(AppSection.SETTINGS)
        }
    }

    val propertiesNavLabel = if (appConfig.isProperty1Rented) "Rent" else "Properties"
    val propertiesSectionTitle = if (appConfig.isProperty1Rented) "Rental & Bills" else AppSection.PROPERTIES.title

    // Pager State to handle horizontal swipe gestures between tabs
    val pagerState = rememberPagerState(pageCount = { activeSections.size })
    val coroutineScope = rememberCoroutineScope()

    // Keep currentSection in sync when user swipes horizontally
    val currentSection = activeSections.getOrElse(pagerState.currentPage) { activeSections.first() }

    // Dynamic Mortgage Repayments (consistent across all tabs using loan's originalTermYears & showLoans toggle)
    val prop1FortnightlyPmt = calculateEffectiveFortnightlyRepayment(prop1Loan)
    val prop1WeeklyMortgage = if (appConfig.showLoans) -(prop1FortnightlyPmt / 2.0) else 0.0

    val prop2FortnightlyPmt = if (appConfig.hasSecondProperty) {
        calculateEffectiveFortnightlyRepayment(prop2Loan)
    } else 0.0
    val prop2WeeklyMortgage = if (appConfig.showLoans && appConfig.hasSecondProperty) -(prop2FortnightlyPmt / 2.0) else 0.0

    val p1AutoFortnights = getElapsedFortnightsSinceStart(prop1Loan.startDateStr)
    val p1Schedule = generateAmortizationSchedule(prop1Loan, 780)
    val p1CurrentRow = p1Schedule.getOrNull(p1AutoFortnights.coerceIn(0, p1Schedule.size - 1)) ?: p1Schedule.first()
    val prop1LiveBalance = if (appConfig.showLoans) p1CurrentRow.closingBalance else 0.0

    val p2AutoFortnights = getElapsedFortnightsSinceStart(prop2Loan.startDateStr)
    val p2Schedule = generateAmortizationSchedule(prop2Loan, 780)
    val p2CurrentRow = p2Schedule.getOrNull(p2AutoFortnights.coerceIn(0, p2Schedule.size - 1)) ?: p2Schedule.first()
    val prop2LiveBalance = if (appConfig.showLoans && appConfig.hasSecondProperty) p2CurrentRow.closingBalance else 0.0

    val prop1HousingCost = if (appConfig.isProperty1Rented) -appConfig.primaryRentWeekly else prop1WeeklyMortgage
    val prop1NetWeekly = prop1HousingCost + prop1Items.sumOf { it.weeklyAmount }

    val prop2RentIncome = if (appConfig.hasSecondProperty) appConfig.secondaryRentWeekly else 0.0
    val prop2Mortgage = if (appConfig.hasSecondProperty) prop2WeeklyMortgage else 0.0
    val prop2Expenses = if (appConfig.hasSecondProperty) prop2Items.sumOf { it.weeklyAmount } else 0.0
    val prop2NetWeekly = prop2RentIncome + prop2Mortgage + prop2Expenses

    val combinedPropertyNet = prop1NetWeekly + prop2NetWeekly
    val splitDivisor = if (appConfig.isCoupleMode) 2.0 else 1.0
    val perPersonPropertyShare = combinedPropertyNet / splitDivisor

    // Hoisted What-If Simulation Controls, Custom Levers & Milestone State (Empty by default so user adds what they like)
    var whatIfHorizonYears by rememberSaveable { mutableIntStateOf(15) }
    var whatIfLifestyleAdjustmentWeekly by rememberSaveable { mutableFloatStateOf(0f) }
    var whatIfRateShockProp1 by rememberSaveable { mutableFloatStateOf(0f) }
    var whatIfRateShockProp2 by rememberSaveable { mutableFloatStateOf(0f) }
    var whatIfRentalGrowthWeekly by rememberSaveable { mutableFloatStateOf(0f) }
    var whatIfSuperBoostWeekly by rememberSaveable { mutableFloatStateOf(0f) }

    val whatIfMilestoneList = remember {
        mutableStateListOf<LifeMilestoneBlock>()
    }
    val whatIfCustomLevers = remember {
        mutableStateListOf<CustomWhatIfLever>()
    }

    // Transparent Global AI Assistant Context Computation (Unmasked & Available across all tabs & scenarios)
    val prop1Val = if (appConfig.showProperties) prop1EstimatedValue else 0.0
    val prop2Val = if (appConfig.showProperties && appConfig.hasSecondProperty) prop2EstimatedValue else 0.0
    val superVal = if (appConfig.showSuperAndShares) superAccountsList.sumOf { it.balance } else 0.0
    val sharesVal = if (appConfig.showSuperAndShares) sharesList.sumOf { it.units * it.currentPrice } else 0.0
    val otherVal = if (appConfig.showOtherAssets) otherAssetsList.sumOf { it.value } else 0.0
    val offsetsVal = if (appConfig.showLoans) prop1Loan.offsetBalance + (if (appConfig.hasSecondProperty) prop2Loan.offsetBalance else 0.0) else 0.0

    val exactTotalAssets = prop1Val + prop2Val + superVal + sharesVal + otherVal + offsetsVal
    val exactTotalLiabilities = prop1LiveBalance + prop2LiveBalance
    val exactNetWorth = exactTotalAssets - exactTotalLiabilities

    val p1TotalExpensesContext = perPersonPropertyShare + person1Items.sumOf { it.weeklyAmount }
    val p1SurplusContext = person1Income + p1TotalExpensesContext
    val p2TotalExpensesContext = if (appConfig.isCoupleMode) (perPersonPropertyShare + person2Items.sumOf { it.weeklyAmount }) else 0.0
    val p2SurplusContext = if (appConfig.isCoupleMode) (person2Income + p2TotalExpensesContext) else 0.0
    val combinedSurplusContext = p1SurplusContext + p2SurplusContext

    val p1ExpensesListStr = person1Items.joinToString { "${it.name}: ${formatCurrency(abs(it.weeklyAmount), 2)}/wk" }
    val p2ExpensesListStr = if (appConfig.isCoupleMode) person2Items.joinToString { "${it.name}: ${formatCurrency(abs(it.weeklyAmount), 2)}/wk" } else ""

    val whatIfBaseSurplusWeekly = run {
        val p1Exp = person1Items.sumOf { it.weeklyAmount }
        val p2Exp = if (appConfig.isCoupleMode) person2Items.sumOf { it.weeklyAmount } else 0.0
        val p1Surp = person1Income + p1Exp
        val p2Surp = if (appConfig.isCoupleMode) (person2Income + p2Exp) else 0.0
        p1Surp + p2Surp + combinedPropertyNet
    }

    // Unified Multi-Year What-If Projection (up to 30 years so AI can answer 1Y, 5Y, 10Y, 15Y, 20Y, 30Y questions)
    val maxProjectionHorizon = max(whatIfHorizonYears, 30)
    val fullWhatIfProj = computeWhatIfProjection(
        horizonYears = maxProjectionHorizon,
        basePropertyVal = prop1Val + prop2Val,
        baseSharesAndSuperVal = sharesVal + superVal,
        baseOtherAssetsVal = otherVal,
        baseOffsetsVal = offsetsVal,
        baseLiabilitiesVal = exactTotalLiabilities,
        baseSurplusWeekly = whatIfBaseSurplusWeekly,
        prop1Loan = prop1Loan,
        prop2Loan = prop2Loan,
        hasSecondProperty = appConfig.hasSecondProperty,
        showLoans = appConfig.showLoans,
        lifestyleAdjustmentWeekly = whatIfLifestyleAdjustmentWeekly,
        rateShockProp1 = whatIfRateShockProp1,
        rateShockProp2 = whatIfRateShockProp2,
        rentalIncomeGrowthWeekly = whatIfRentalGrowthWeekly,
        superBoostWeekly = whatIfSuperBoostWeekly,
        customLevers = whatIfCustomLevers,
        milestones = whatIfMilestoneList
    )

    val whatIfEndPathANetWorth = fullWhatIfProj.pathASeries.getOrElse(whatIfHorizonYears) { exactNetWorth }
    val whatIfEndPathBNetWorth = fullWhatIfProj.pathBSeries.getOrElse(whatIfHorizonYears) { exactNetWorth }
    val whatIfNetDifference = whatIfEndPathBNetWorth - whatIfEndPathANetWorth
    val whatIfDeficitYear = fullWhatIfProj.deficitYear?.takeIf { it <= whatIfHorizonYears }

    val prop1ItemsStr = prop1Items.joinToString { "${it.name}: ${formatCurrency(abs(it.weeklyAmount), 2)}/wk" }
    val prop2ItemsStr = if (appConfig.hasSecondProperty) prop2Items.joinToString { "${it.name}: ${formatCurrency(abs(it.weeklyAmount), 2)}/wk" } else ""

    val detailedFinancialContext = buildString {
        append("CURRENTLY LOGGED-IN USER SPEAKING TO YOU: $activeLoggedInPersonName (${if (isLoggedInAsPartner) "Partner / Person 2, Email: ${currentUser.email ?: appConfig.partnerEmail}" else "Primary Account Holder / Person 1, Email: ${currentUser.email ?: appConfig.primaryOwnerEmail}"}). ")
        append("IMPORTANT RULE: Always address the user directly by their own name '$activeLoggedInPersonName' (NEVER call them '${if (isLoggedInAsPartner) appConfig.person1Name else appConfig.person2Name}'). When they say 'my income', 'my expenses', 'my super', or 'my shares', they mean $activeLoggedInPersonName's (${if (isLoggedInAsPartner) "Person 2" else "Person 1"}) items. ")
        append("EXPLICIT COMPUTED FINANCIAL TOTALS & UNMASKED GLOBAL APP DATA (CURRENT & FUTURE): ")
        append("Exact Net Worth Today = ${formatCurrency(exactNetWorth, 2)}. ")
        append("Exact Total Assets Today = ${formatCurrency(exactTotalAssets, 2)} (Properties: ${formatCurrency(prop1Val + prop2Val, 0)}, Shares: ${formatCurrency(sharesVal, 0)}, Super: ${formatCurrency(superVal, 0)}, Other Assets: ${formatCurrency(otherVal, 0)}, Offset Accounts: ${formatCurrency(offsetsVal, 0)}). ")
        append("Exact Total Liabilities Today = ${formatCurrency(exactTotalLiabilities, 2)} (Property 1 Mortgage: ${formatCurrency(prop1LiveBalance, 0)}, Property 2 Mortgage: ${formatCurrency(prop2LiveBalance, 0)}). ")

        append("\n\n1. PERSONAL CASH FLOW TAB (UNMASKED): ")
        append("Person 1 (${appConfig.person1Name}): Income = ${formatCurrency(person1Income, 2)}/wk, Total Expenses (incl. property share) = ${formatCurrency(abs(p1TotalExpensesContext), 2)}/wk, Net Surplus = ${formatCurrency(p1SurplusContext, 2)}/wk. ")
        if (p1ExpensesListStr.isNotBlank()) append("Person 1 Itemized Expenses: $p1ExpensesListStr. ")

        if (appConfig.isCoupleMode) {
            append("Person 2 (${appConfig.person2Name}): Income = ${formatCurrency(person2Income, 2)}/wk, Total Expenses (incl. property share) = ${formatCurrency(abs(p2TotalExpensesContext), 2)}/wk, Net Surplus = ${formatCurrency(p2SurplusContext, 2)}/wk. ")
            if (p2ExpensesListStr.isNotBlank()) append("Person 2 Itemized Expenses: $p2ExpensesListStr. ")
            append("COMBINED WEEKLY NET SURPLUS = ${formatCurrency(combinedSurplusContext, 2)}/wk (${formatCurrency(combinedSurplusContext * 52.0, 0)}/yr). ")
        }

        append("\n\n2. PROPERTIES TAB (UNMASKED): ")
        append("Property 1 (${appConfig.property1Name}): Market Value = ${formatCurrency(prop1EstimatedValue, 0)}, Mortgage Balance = ${formatCurrency(prop1LiveBalance, 0)}, Net Equity = ${formatCurrency(prop1EstimatedValue - prop1LiveBalance, 0)}, Status = ${if (appConfig.isProperty1Rented) "Rented (${formatCurrency(appConfig.primaryRentWeekly, 0)}/wk)" else "Owner Occupied"}, Weekly Net Cashflow = ${formatCurrency(prop1NetWeekly, 2)}/wk. ")
        if (prop1ItemsStr.isNotBlank()) append("Property 1 Bills/Items: $prop1ItemsStr. ")

        if (appConfig.hasSecondProperty) {
            append("Property 2 (${appConfig.property2Name}): Market Value = ${formatCurrency(prop2EstimatedValue, 0)}, Mortgage Balance = ${formatCurrency(prop2LiveBalance, 0)}, Net Equity = ${formatCurrency(prop2EstimatedValue - prop2LiveBalance, 0)}, Status = Rented (${formatCurrency(appConfig.secondaryRentWeekly, 0)}/wk), Weekly Net Cashflow = ${formatCurrency(prop2NetWeekly, 2)}/wk. ")
            if (prop2ItemsStr.isNotBlank()) append("Property 2 Bills/Items: $prop2ItemsStr. ")
        }

        append("\n\n3. LOANS & OFFSET TAB (UNMASKED AMORTIZATION): ")
        append("Property 1 Loan (${prop1Loan.name}): Original Loan = ${formatCurrency(prop1Loan.originalLoanAmount, 0)}, Term = ${prop1Loan.originalTermYears} yrs, Start Balance = ${formatCurrency(prop1Loan.startLoanBalance, 0)}, Current Live Balance = ${formatCurrency(prop1LiveBalance, 0)}, Interest Rate = ${prop1Loan.interestRatePa}%, Offset Balance = ${formatCurrency(prop1Loan.offsetBalance, 0)}, Net Balance Charging Interest = ${formatCurrency(max(0.0, prop1LiveBalance - prop1Loan.offsetBalance), 0)}, Fortnightly Repayment = ${formatCurrency(prop1FortnightlyPmt, 0)}, Weekly Equiv = ${formatCurrency(abs(prop1WeeklyMortgage), 2)}. ")

        if (appConfig.hasSecondProperty) {
            append("Property 2 Loan (${prop2Loan.name}): Original Loan = ${formatCurrency(prop2Loan.originalLoanAmount, 0)}, Term = ${prop2Loan.originalTermYears} yrs, Start Balance = ${formatCurrency(prop2Loan.startLoanBalance, 0)}, Current Live Balance = ${formatCurrency(prop2LiveBalance, 0)}, Interest Rate = ${prop2Loan.interestRatePa}%, Offset Balance = ${formatCurrency(prop2Loan.offsetBalance, 0)}, Net Balance Charging Interest = ${formatCurrency(max(0.0, prop2LiveBalance - prop2Loan.offsetBalance), 0)}, Fortnightly Repayment = ${formatCurrency(prop2FortnightlyPmt, 0)}, Weekly Equiv = ${formatCurrency(abs(prop2WeeklyMortgage), 2)}. ")
        }

        append("\n\n4. SUPER & SHARES TAB (UNMASKED): ")
        if (superAccountsList.isNotEmpty()) {
            append("Superannuation (Total ${formatCurrency(superVal, 0)}): ${superAccountsList.joinToString { "${it.fundName} (${it.owner}): ${formatCurrency(it.balance, 0)}" }}. ")
        }
        if (sharesList.isNotEmpty()) {
            val totalSharesCost = sharesList.sumOf { it.units * it.purchasePrice }
            val totalSharesGain = sharesVal - totalSharesCost
            append("Shares Portfolio (${sharesList.size} holdings, Total Market Value ${formatCurrency(sharesVal, 0)}, Cost Basis ${formatCurrency(totalSharesCost, 0)}, Net Gain/Loss ${formatCurrency(totalSharesGain, 0)}): ${sharesList.joinToString { "${it.code} (${it.owner}, ${it.units} units @ ${formatCurrency(it.currentPrice, 2)}, Purchase Price ${formatCurrency(it.purchasePrice, 2)}, Market Value ${formatCurrency(it.units * it.currentPrice, 0)}, Gain/Loss ${formatCurrency((it.currentPrice - it.purchasePrice) * it.units, 0)})" }}. ")
        }

        append("\n\n5. OTHER ASSETS TAB (UNMASKED): ")
        if (otherAssetsList.isNotEmpty()) {
            append("Other Assets (Total ${formatCurrency(otherVal, 0)}): ${otherAssetsList.joinToString { "${it.name} (${it.owner}): ${formatCurrency(it.value, 0)}" }}. ")
        }

        append("\n\n6. WHAT-IF SCENARIO SIMULATOR (ACTIVE LEVERS, CUSTOM LEVERS, MILESTONES & FUTURE PROJECTIONS): ")
        append("Active Timeframe = $whatIfHorizonYears Years. ")
        append("Extra Weekly Savings/Spending = ${if (whatIfLifestyleAdjustmentWeekly >= 0) "+$" else "-$"}${abs(whatIfLifestyleAdjustmentWeekly)}/wk. ")
        append("Property 1 Rate Change (${appConfig.property1Name}) = ${if (whatIfRateShockProp1 >= 0) "+" else ""}${whatIfRateShockProp1}% (Weekly repayment delta: ${formatCurrency(fullWhatIfProj.weeklyRateDeltaProp1, 2)}/wk). ")
        if (appConfig.hasSecondProperty) {
            append("Property 2 Rate Change (${appConfig.property2Name}) = ${if (whatIfRateShockProp2 >= 0) "+" else ""}${whatIfRateShockProp2}% (Weekly repayment delta: ${formatCurrency(fullWhatIfProj.weeklyRateDeltaProp2, 2)}/wk). ")
        }
        if (whatIfSuperBoostWeekly != 0f) append("Extra Super Salary Sacrifice = +$${whatIfSuperBoostWeekly}/wk. ")
        if (whatIfRentalGrowthWeekly != 0f) append("Rental Cashflow Change = ${if (whatIfRentalGrowthWeekly >= 0) "+$" else "-$"}${abs(whatIfRentalGrowthWeekly)}/wk. ")

        if (whatIfCustomLevers.isNotEmpty()) {
            append("Custom User Levers (${whatIfCustomLevers.size}): ${whatIfCustomLevers.joinToString { "${it.title}: ${it.currentValue}${it.impactType.unitSuffix} (${it.impactType.label})" }}. ")
        }

        val activeMilestones = whatIfMilestoneList.filter { it.isEnabled }
        if (activeMilestones.isNotEmpty()) {
            append("Active Life Milestones (${activeMilestones.size}): ${activeMilestones.joinToString { "${it.title} [Category: ${it.category.label}, Start Year: ${it.startYear}, Duration: ${it.durationYears} yrs, Weekly Impact: ${formatCurrency(it.weeklyCashflowImpact, 0)}/wk, Asset Impact: ${formatCurrency(it.assetImpact, 0)}, Loan Impact: ${formatCurrency(it.liabilityImpact, 0)}]" }}. ")
        } else {
            append("No active milestone events. ")
        }

        append("Selected Horizon ($whatIfHorizonYears Yrs) Results: ")
        append("Path A (Status Quo Net Worth in $whatIfHorizonYears Yrs) = ${formatCurrency(whatIfEndPathANetWorth, 0)}. ")
        append("Path B (What-If Plan Net Worth in $whatIfHorizonYears Yrs) = ${formatCurrency(whatIfEndPathBNetWorth, 0)}. ")
        append("Net Difference = ${if (whatIfNetDifference >= 0) "+" else ""}${formatCurrency(whatIfNetDifference, 0)}. ")
        append("Cashflow Deficit Year = ${if (whatIfDeficitYear != null) "Year $whatIfDeficitYear" else "None"}. ")

        val checkpoints = listOf(1, 5, 10, 15, 20, 30).filter { it <= fullWhatIfProj.pathASeries.lastIndex }
        append("Multi-Year Future Trajectory Checkpoints (Status Quo -> What-If Plan): ")
        append(checkpoints.joinToString(" | ") { yr ->
            "Yr $yr: ${formatCurrency(fullWhatIfProj.pathASeries[yr], 0)} -> ${formatCurrency(fullWhatIfProj.pathBSeries[yr], 0)}"
        })
    }

    // Live AI Read/Write Action Executor — allows the AI to edit any mutable data in the app when asked
    fun executeAiActions(rawResponse: String): Pair<String, List<String>> {
        val actionRegex = Regex("""\[\[ACTION:([^]]+)]]""")
        val appliedSummaries = mutableListOf<String>()

        actionRegex.findAll(rawResponse).forEach { match ->
            val payload = match.groupValues[1].trim()
            val parts = payload.split("|").map { it.trim() }
            if (parts.isEmpty()) return@forEach
            val actionType = parts.first().uppercase()
            val params = mutableMapOf<String, String>()
            parts.drop(1).forEach { kv ->
                val idx = kv.indexOf('=')
                if (idx > 0) {
                    val k = kv.substring(0, idx).trim().lowercase()
                    val v = kv.substring(idx + 1).trim()
                    params[k] = v
                }
            }

            when (actionType) {
                "UPDATE_INCOME" -> {
                    val personNum = params["person"]?.toIntOrNull() ?: 1
                    val amount = params["weeklyamount"]?.replace("$", "")?.replace(",", "")?.toDoubleOrNull()
                    if (amount != null && amount >= 0.0) {
                        if (personNum == 2 && appConfig.isCoupleMode) {
                            person2Income = amount
                            sharedPrefs.edit { putFloat("person2_inc", amount.toFloat()) }
                            updateFirestore("person2_inc", amount)
                            appliedSummaries.add("Updated ${appConfig.person2Name} income to ${formatCurrency(amount, 2)}/wk")
                        } else {
                            person1Income = amount
                            sharedPrefs.edit { putFloat("person1_inc", amount.toFloat()) }
                            updateFirestore("person1_inc", amount)
                            appliedSummaries.add("Updated ${appConfig.person1Name} income to ${formatCurrency(amount, 2)}/wk")
                        }
                    }
                }
                "ADD_EXPENSE", "UPDATE_EXPENSE" -> {
                    val personNum = params["person"]?.toIntOrNull() ?: 1
                    val name = params["name"]?.takeIf { it.isNotBlank() } ?: "Expense"
                    val rawAmt = params["weeklyamount"]?.replace("$", "")?.replace(",", "")?.toDoubleOrNull()
                    val iconName = params["icon"] ?: "Receipt"
                    if (rawAmt != null) {
                        val signedAmt = if (rawAmt > 0) -rawAmt else rawAmt
                        if (personNum == 2 && appConfig.isCoupleMode) {
                            val existingIdx = person2Items.indexOfFirst { it.name.equals(name, ignoreCase = true) }
                            val updated = if (existingIdx >= 0) {
                                person2Items.toMutableList().apply {
                                    set(existingIdx, this[existingIdx].copy(name = name, weeklyAmount = signedAmt))
                                }
                            } else {
                                person2Items + CashflowItem("p2_${System.currentTimeMillis()}", name, signedAmt, iconName = iconName)
                            }
                            person2Items = updated
                            saveList("person2_items", updated)
                            appliedSummaries.add("Saved $name (${formatCurrency(abs(signedAmt), 2)}/wk) for ${appConfig.person2Name}")
                        } else {
                            val existingIdx = person1Items.indexOfFirst { it.name.equals(name, ignoreCase = true) }
                            val updated = if (existingIdx >= 0) {
                                person1Items.toMutableList().apply {
                                    set(existingIdx, this[existingIdx].copy(name = name, weeklyAmount = signedAmt))
                                }
                            } else {
                                person1Items + CashflowItem("p1_${System.currentTimeMillis()}", name, signedAmt, iconName = iconName)
                            }
                            person1Items = updated
                            saveList("person1_items", updated)
                            appliedSummaries.add("Saved $name (${formatCurrency(abs(signedAmt), 2)}/wk) for ${appConfig.person1Name}")
                        }
                    }
                }
                "DELETE_EXPENSE" -> {
                    val personNum = params["person"]?.toIntOrNull() ?: 1
                    val name = params["name"]?.takeIf { it.isNotBlank() }
                    if (name != null) {
                        if (personNum == 2) {
                            val updated = person2Items.filterNot { it.name.contains(name, ignoreCase = true) }
                            person2Items = updated
                            saveList("person2_items", updated)
                        } else {
                            val updated = person1Items.filterNot { it.name.contains(name, ignoreCase = true) }
                            person1Items = updated
                            saveList("person1_items", updated)
                        }
                        appliedSummaries.add("Removed '$name' expense")
                    }
                }
                "UPDATE_PROPERTY" -> {
                    val propNum = params["property"]?.toIntOrNull() ?: 1
                    val marketVal = params["marketvalue"]?.replace("$", "")?.replace(",", "")?.toDoubleOrNull()
                    val rentVal = params["rentweekly"]?.replace("$", "")?.replace(",", "")?.toDoubleOrNull()
                    val newName = params["name"]?.takeIf { it.isNotBlank() }
                    val isRentedFlag = params["isrented"]?.toBooleanStrictOrNull()

                    if (propNum == 2) {
                        var cfg = appConfig.copy(hasSecondProperty = true)
                        if (newName != null) cfg = cfg.copy(property2Name = newName)
                        if (rentVal != null) cfg = cfg.copy(secondaryRentWeekly = abs(rentVal))
                        if (isRentedFlag != null) cfg = cfg.copy(isProperty2Rented = isRentedFlag)
                        saveAppConfig(cfg)
                        if (marketVal != null && marketVal > 0) savePropertyValuations(prop1EstimatedValue, marketVal)
                        appliedSummaries.add("Updated Property 2 (${cfg.property2Name})")
                    } else {
                        var cfg = appConfig
                        if (newName != null) cfg = cfg.copy(property1Name = newName)
                        if (rentVal != null) cfg = cfg.copy(primaryRentWeekly = abs(rentVal))
                        if (isRentedFlag != null) cfg = cfg.copy(isProperty1Rented = isRentedFlag)
                        saveAppConfig(cfg)
                        if (marketVal != null && marketVal > 0) savePropertyValuations(marketVal, prop2EstimatedValue)
                        appliedSummaries.add("Updated Property 1 (${cfg.property1Name})")
                    }
                }
                "ADD_PROPERTY_ITEM" -> {
                    val propNum = params["property"]?.toIntOrNull() ?: 1
                    val name = params["name"]?.takeIf { it.isNotBlank() } ?: "Property Expense"
                    val rawAmt = params["weeklyamount"]?.replace("$", "")?.replace(",", "")?.toDoubleOrNull()
                    if (rawAmt != null) {
                        val signedAmt = if (rawAmt > 0) -rawAmt else rawAmt
                        if (propNum == 2) {
                            val updated = prop2Items + CashflowItem("pr2_${System.currentTimeMillis()}", name, signedAmt)
                            prop2Items = updated
                            saveList("prop2_items", updated)
                            appliedSummaries.add("Added $name (${formatCurrency(abs(signedAmt), 2)}/wk) to ${appConfig.property2Name}")
                        } else {
                            val updated = prop1Items + CashflowItem("pr1_${System.currentTimeMillis()}", name, signedAmt)
                            prop1Items = updated
                            saveList("prop1_items", updated)
                            appliedSummaries.add("Added $name (${formatCurrency(abs(signedAmt), 2)}/wk) to ${appConfig.property1Name}")
                        }
                    }
                }
                "DELETE_PROPERTY_ITEM" -> {
                    val propNum = params["property"]?.toIntOrNull() ?: 1
                    val name = params["name"]?.takeIf { it.isNotBlank() }
                    if (name != null) {
                        if (propNum == 2) {
                            val updated = prop2Items.filterNot { it.name.contains(name, ignoreCase = true) }
                            prop2Items = updated
                            saveList("prop2_items", updated)
                        } else {
                            val updated = prop1Items.filterNot { it.name.contains(name, ignoreCase = true) }
                            prop1Items = updated
                            saveList("prop1_items", updated)
                        }
                        appliedSummaries.add("Removed '$name' from property bills")
                    }
                }
                "UPDATE_LOAN" -> {
                    val propNum = params["property"]?.toIntOrNull() ?: 1
                    val bal = params["balance"]?.replace("$", "")?.replace(",", "")?.toDoubleOrNull()
                    val rate = params["rate"]?.replace("%", "")?.toDoubleOrNull()
                    val offset = params["offset"]?.replace("$", "")?.replace(",", "")?.toDoubleOrNull()
                    val orig = params["originalloan"]?.replace("$", "")?.replace(",", "")?.toDoubleOrNull()
                    val term = params["termyears"]?.toIntOrNull()
                    if (propNum == 2) {
                        val updated = prop2Loan.copy(
                            startLoanBalance = bal ?: prop2Loan.startLoanBalance,
                            originalLoanAmount = orig ?: max(prop2Loan.originalLoanAmount, bal ?: prop2Loan.startLoanBalance),
                            interestRatePa = rate ?: prop2Loan.interestRatePa,
                            offsetBalance = offset ?: prop2Loan.offsetBalance,
                            originalTermYears = term ?: prop2Loan.originalTermYears
                        )
                        saveProp2Loan(updated)
                        appliedSummaries.add("Updated ${prop2Loan.name} Loan parameters")
                    } else {
                        val updated = prop1Loan.copy(
                            startLoanBalance = bal ?: prop1Loan.startLoanBalance,
                            originalLoanAmount = orig ?: max(prop1Loan.originalLoanAmount, bal ?: prop1Loan.startLoanBalance),
                            interestRatePa = rate ?: prop1Loan.interestRatePa,
                            offsetBalance = offset ?: prop1Loan.offsetBalance,
                            originalTermYears = term ?: prop1Loan.originalTermYears
                        )
                        saveProp1Loan(updated)
                        appliedSummaries.add("Updated ${prop1Loan.name} Loan parameters")
                    }
                }
                "ADD_SHARE", "UPDATE_SHARE" -> {
                    val code = params["code"]?.trim()?.uppercase()?.takeIf { it.isNotBlank() }
                    val owner = params["owner"]?.takeIf { it.isNotBlank() } ?: appConfig.person1Name
                    val units = params["units"]?.replace(",", "")?.toDoubleOrNull()
                    val pPrice = params["purchaseprice"]?.replace("$", "")?.replace(",", "")?.toDoubleOrNull()
                    val cPrice = params["currentprice"]?.replace("$", "")?.replace(",", "")?.toDoubleOrNull()
                    if (code != null) {
                        val existingIdx = sharesList.indexOfFirst { it.code.equals(code, ignoreCase = true) }
                        val updated = if (existingIdx >= 0) {
                            val curr = sharesList[existingIdx]
                            sharesList.toMutableList().apply {
                                set(
                                    existingIdx,
                                    curr.copy(
                                        owner = owner,
                                        units = units ?: curr.units,
                                        purchasePrice = pPrice ?: curr.purchasePrice,
                                        currentPrice = cPrice ?: pPrice ?: curr.currentPrice
                                    )
                                )
                            }
                        } else {
                            val u = units ?: 1.0
                            val pp = pPrice ?: cPrice ?: 10.0
                            val cp = cPrice ?: pp
                            sharesList + ShareParcel("sp_${System.currentTimeMillis()}", code, owner, u, pp, cp)
                        }
                        saveSharesList(updated)
                        appliedSummaries.add("Saved share holding $code")
                    }
                }
                "DELETE_SHARE" -> {
                    val code = params["code"]?.trim()
                    if (!code.isNullOrBlank()) {
                        val updated = sharesList.filterNot { it.code.equals(code, ignoreCase = true) }
                        saveSharesList(updated)
                        appliedSummaries.add("Removed share holding $code")
                    }
                }
                "ADD_SUPER", "UPDATE_SUPER" -> {
                    val fund = params["fundname"]?.takeIf { it.isNotBlank() } ?: "Super Fund"
                    val owner = params["owner"]?.takeIf { it.isNotBlank() } ?: appConfig.person1Name
                    val bal = params["balance"]?.replace("$", "")?.replace(",", "")?.toDoubleOrNull()
                    if (bal != null) {
                        val existingIdx = superAccountsList.indexOfFirst {
                            it.fundName.equals(fund, ignoreCase = true) || it.owner.equals(owner, ignoreCase = true)
                        }
                        val updated = if (existingIdx >= 0 && actionType == "UPDATE_SUPER") {
                            superAccountsList.toMutableList().apply {
                                set(existingIdx, this[existingIdx].copy(fundName = fund, owner = owner, balance = bal))
                            }
                        } else {
                            superAccountsList + SuperAccount("sup_${System.currentTimeMillis()}", fund, owner, bal)
                        }
                        saveSuperAccounts(updated)
                        appliedSummaries.add("Saved Super account $fund (${formatCurrency(bal, 0)})")
                    }
                }
                "DELETE_SUPER" -> {
                    val fund = params["fundname"]?.takeIf { it.isNotBlank() }
                    if (fund != null) {
                        val updated = superAccountsList.filterNot { it.fundName.contains(fund, ignoreCase = true) }
                        saveSuperAccounts(updated)
                        appliedSummaries.add("Deleted Super account $fund")
                    }
                }
                "ADD_OTHER_ASSET", "UPDATE_OTHER_ASSET" -> {
                    val name = params["name"]?.takeIf { it.isNotBlank() } ?: "Asset"
                    val owner = params["owner"]?.takeIf { it.isNotBlank() } ?: appConfig.person1Name
                    val valAmt = params["value"]?.replace("$", "")?.replace(",", "")?.toDoubleOrNull()
                    if (valAmt != null) {
                        val existingIdx = otherAssetsList.indexOfFirst { it.name.equals(name, ignoreCase = true) }
                        val updated = if (existingIdx >= 0) {
                            otherAssetsList.toMutableList().apply {
                                set(existingIdx, this[existingIdx].copy(name = name, owner = owner, value = valAmt))
                            }
                        } else {
                            otherAssetsList + OtherAsset("oa_${System.currentTimeMillis()}", name, owner, valAmt)
                        }
                        saveOtherAssets(updated)
                        appliedSummaries.add("Saved asset $name (${formatCurrency(valAmt, 0)})")
                    }
                }
                "DELETE_OTHER_ASSET" -> {
                    val name = params["name"]?.takeIf { it.isNotBlank() }
                    if (name != null) {
                        val updated = otherAssetsList.filterNot { it.name.contains(name, ignoreCase = true) }
                        saveOtherAssets(updated)
                        appliedSummaries.add("Removed asset $name")
                    }
                }
                "UPDATE_WHAT_IF" -> {
                    params["horizonyears"]?.toIntOrNull()?.let { whatIfHorizonYears = it.coerceIn(1, 40) }
                    params["savingsweekly"]?.replace("$", "")?.toFloatOrNull()?.let { whatIfLifestyleAdjustmentWeekly = it.coerceIn(-2000f, 5000f) }
                    params["ratechangeprop1"]?.replace("%", "")?.toFloatOrNull()?.let { whatIfRateShockProp1 = it.coerceIn(-5f, 8f) }
                    params["ratechangeprop2"]?.replace("%", "")?.toFloatOrNull()?.let { whatIfRateShockProp2 = it.coerceIn(-5f, 8f) }
                    params["superboostweekly"]?.replace("$", "")?.toFloatOrNull()?.let { whatIfSuperBoostWeekly = it.coerceIn(0f, 3000f) }
                    params["rentchangeweekly"]?.replace("$", "")?.toFloatOrNull()?.let { whatIfRentalGrowthWeekly = it.coerceIn(-2000f, 5000f) }
                    appliedSummaries.add("Updated What-If Simulator levers")
                }
                "ADD_CUSTOM_LEVER", "UPDATE_CUSTOM_LEVER" -> {
                    val name = params["name"]?.takeIf { it.isNotBlank() } ?: "Custom Lever"
                    val value = params["value"]?.replace("$", "")?.replace("%", "")?.toFloatOrNull() ?: 0f
                    val minV = params["min"]?.toFloatOrNull() ?: -500f
                    val maxV = params["max"]?.toFloatOrNull() ?: 500f
                    val stepV = params["step"]?.toFloatOrNull() ?: 25f
                    val impactStr = params["impacttype"]?.uppercase() ?: "WEEKLY_CASHFLOW"
                    val impact = CustomLeverImpactType.entries.find { it.name == impactStr } ?: CustomLeverImpactType.WEEKLY_CASHFLOW
                    val existingIdx = whatIfCustomLevers.indexOfFirst { it.title.equals(name, ignoreCase = true) }
                    if (existingIdx >= 0) {
                        whatIfCustomLevers[existingIdx] = whatIfCustomLevers[existingIdx].copy(currentValue = value)
                    } else {
                        whatIfCustomLevers.add(
                            CustomWhatIfLever(
                                id = "cl_${System.currentTimeMillis()}",
                                title = name,
                                impactType = impact,
                                minValue = if (value < minV) value else minV,
                                maxValue = if (value > maxV) value else maxV,
                                stepSize = stepV,
                                currentValue = value,
                                isEnabled = true
                            )
                        )
                    }
                    appliedSummaries.add("Saved custom lever '$name' = $value${impact.unitSuffix}")
                }
                "DELETE_CUSTOM_LEVER" -> {
                    val name = params["name"]?.takeIf { it.isNotBlank() }
                    if (name != null) {
                        whatIfCustomLevers.removeAll { it.title.contains(name, ignoreCase = true) }
                        appliedSummaries.add("Removed custom lever '$name'")
                    }
                }
                "ADD_MILESTONE" -> {
                    val title = params["title"]?.takeIf { it.isNotBlank() } ?: "Custom Milestone"
                    val catStr = params["category"]?.uppercase() ?: "PURCHASE"
                    val cat = MilestoneCategory.entries.find { it.name == catStr } ?: MilestoneCategory.PURCHASE
                    val startY = (params["startyear"]?.toIntOrNull() ?: 2).coerceIn(1, 30)
                    val durY = (params["durationyears"]?.toIntOrNull() ?: 1).coerceIn(1, 30)
                    val wk = params["weeklycashflow"]?.replace("$", "")?.replace(",", "")?.toDoubleOrNull() ?: 0.0
                    val ast = params["assetimpact"]?.replace("$", "")?.replace(",", "")?.toDoubleOrNull() ?: 0.0
                    val liab = params["liabilityimpact"]?.replace("$", "")?.replace(",", "")?.toDoubleOrNull() ?: 0.0
                    whatIfMilestoneList.add(
                        LifeMilestoneBlock(
                            id = "m_${System.currentTimeMillis()}",
                            title = title,
                            category = cat,
                            startYear = startY,
                            durationYears = durY,
                            weeklyCashflowImpact = wk,
                            assetImpact = ast,
                            liabilityImpact = liab,
                            isEnabled = true
                        )
                    )
                    appliedSummaries.add("Added milestone '$title' (Yr $startY)")
                }
                "DELETE_MILESTONE" -> {
                    val title = params["title"]?.takeIf { it.isNotBlank() }
                    if (title != null) {
                        whatIfMilestoneList.removeAll { it.title.contains(title, ignoreCase = true) }
                        appliedSummaries.add("Removed milestone '$title'")
                    }
                }
                "UPDATE_PROFILE" -> {
                    var cfg = appConfig
                    params["person1name"]?.takeIf { it.isNotBlank() }?.let { cfg = cfg.copy(person1Name = it) }
                    params["person2name"]?.takeIf { it.isNotBlank() }?.let { cfg = cfg.copy(person2Name = it) }
                    params["iscouplemode"]?.toBooleanStrictOrNull()?.let { cfg = cfg.copy(isCoupleMode = it) }
                    params["hassecondproperty"]?.toBooleanStrictOrNull()?.let { cfg = cfg.copy(hasSecondProperty = it) }
                    saveAppConfig(cfg)
                    appliedSummaries.add("Updated profile settings")
                }
            }
        }

        val cleanedText = rawResponse.replace(actionRegex, "").trim()
        return Pair(cleanedText, appliedSummaries)
    }

    val headerSubtitle = remember(appConfig) {
        if (appConfig.isCoupleMode) {
            "EXPNZ • ${appConfig.person1Name.uppercase()} & ${appConfig.person2Name.uppercase()}"
        } else {
            "EXPNZ • ${appConfig.person1Name.uppercase()}"
        }
    }

    // HEADER WITH BRAND LOGO — FLOATING APPLE LIQUID GLASS CAPSULE
    val topBarContent = @Composable {
        val headerGlassShape = RoundedCornerShape(26.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        shape = headerGlassShape,
                        tintColor = BrandForestDark,
                        intensity = 1.15f,
                        accentRimColor = currentSection.getColor(appConfig),
                        role = LiquidGlassRole.HEADER
                    ),
                shape = headerGlassShape,
                color = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp, start = 14.dp, end = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AppLogoIcon(size = 40.dp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = headerSubtitle,
                                color = BrandCoralPink,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.2.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (currentSection == AppSection.PROPERTIES) propertiesSectionTitle else currentSection.title,
                                color = Color.White,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Transparent,
                            modifier = Modifier
                                .size(40.dp)
                                .liquidGlass(
                                    shape = CircleShape,
                                    tintColor = BrandForestSurface,
                                    intensity = 1.2f,
                                    accentRimColor = if (isWealthHidden) BrandCoralPink else Color.White,
                                    role = LiquidGlassRole.STATIC
                                )
                        ) {
                            IconButton(onClick = { isWealthHidden = !isWealthHidden }) {
                                Icon(
                                    if (isWealthHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Privacy Mask",
                                    tint = if (isWealthHidden) BrandCoralPink else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showColorPickerDialog) {
        UserColorPickerDialog(
            appConfig = appConfig,
            onDismiss = { showColorPickerDialog = false },
            onSaveColors = { p1Hex, p2Hex ->
                saveAppConfig(appConfig.copy(person1ColorHex = p1Hex, person2ColorHex = p2Hex))
                showColorPickerDialog = false
            }
        )
    }

    val liquidBoundaryState = remember { LiquidScrollBoundaryState() }
    var isHorizontalChildTouched by remember { mutableStateOf(false) }

    CompositionLocalProvider(
        LocalLiquidScrollBoundary provides liquidBoundaryState,
        LocalOnHorizontalChildTouchChange provides { isHorizontalChildTouched = it }
    ) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent()
                        lastActiveTime = System.currentTimeMillis()
                    }
                }
            }
    ) {
        if (isExpanded) {
            Row(modifier = Modifier.fillMaxSize()) {
                val railShape = RoundedCornerShape(28.dp)
                NavigationRail(
                    containerColor = Color.Transparent,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Start + WindowInsetsSides.Vertical))
                        .padding(start = 10.dp, top = 10.dp, bottom = 10.dp)
                        .liquidGlass(
                            shape = railShape,
                            tintColor = BrandForestDark,
                            intensity = 1.15f,
                            accentRimColor = currentSection.getColor(appConfig),
                            role = LiquidGlassRole.STATIC
                        )
                ) {
                    Spacer(Modifier.height(16.dp))
                    activeSections.forEachIndexed { index, section ->
                        NavigationRailItem(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            icon = { Icon(section.icon, contentDescription = section.title) },
                            label = { Text(if (section == AppSection.PROPERTIES) propertiesNavLabel else section.shortLabel, fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = BrandForestDark,
                                selectedTextColor = section.getColor(appConfig),
                                indicatorColor = section.getColor(appConfig),
                                unselectedIconColor = Color.White.copy(alpha = 0.75f),
                                unselectedTextColor = Color.White.copy(alpha = 0.75f)
                            )
                        )
                    }
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    HorizontalPager(
                        state = pagerState,
                        userScrollEnabled = !isHorizontalChildTouched,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val section = activeSections[page]
                        when (section) {
                            AppSection.TOTAL_WEALTH -> TotalWealthScreen(
                                appConfig = appConfig,
                                prop1MarketValue = prop1EstimatedValue,
                                prop2MarketValue = prop2EstimatedValue,
                                sharesList = sharesList,
                                superAccountsList = superAccountsList,
                                otherAssetsList = otherAssetsList,
                                prop1Loan = prop1Loan,
                                prop2Loan = prop2Loan,
                                person1Items = person1Items,
                                person2Items = person2Items,
                                isWealthHidden = isWealthHidden,
                                onNavigateToSettings = {
                                    coroutineScope.launch {
                                        val settingsIdx = activeSections.indexOf(AppSection.SETTINGS)
                                        if (settingsIdx >= 0) pagerState.animateScrollToPage(settingsIdx)
                                    }
                                },
                                onNavigateToWhatIf = {
                                    coroutineScope.launch {
                                        val whatIfIdx = activeSections.indexOf(AppSection.WHAT_IF)
                                        if (whatIfIdx >= 0) pagerState.animateScrollToPage(whatIfIdx)
                                    }
                                }
                            )
                            AppSection.CASH_FLOW -> MoneyPlanScreen(
                                appConfig = appConfig,
                                person1Color = person1Color,
                                person2Color = person2Color,
                                person1Income = person1Income,
                                person2Income = person2Income,
                                isWealthHidden = isWealthHidden,
                                isPartnerLoggedIn = isLoggedInAsPartner,
                                onUpdateIncome = { isPerson1, newInc ->
                                    if (isPerson1) {
                                        person1Income = newInc
                                        sharedPrefs.edit { putFloat("person1_inc", newInc.toFloat()) }
                                        updateFirestore("person1_inc", newInc)
                                    } else {
                                        person2Income = newInc
                                        sharedPrefs.edit { putFloat("person2_inc", newInc.toFloat()) }
                                        updateFirestore("person2_inc", newInc)
                                    }
                                },
                                perPersonPropertyShare = perPersonPropertyShare,
                                person1Items = person1Items,
                                person2Items = person2Items,
                                onAddPerson1 = { name, amt, iconName ->
                                    val updated = person1Items + CashflowItem("p1_${System.currentTimeMillis()}", name, amt, iconName = iconName)
                                    person1Items = updated
                                    saveList("person1_items", updated)
                                },
                                onEditPerson1 = { id, name, amt, iconName ->
                                    val updated = person1Items.map { if (it.id == id) it.copy(name = name, weeklyAmount = amt, iconName = iconName) else it }
                                    person1Items = updated
                                    saveList("person1_items", updated)
                                },
                                onDeletePerson1 = { id ->
                                    val updated = person1Items.filterNot { it.id == id }
                                    person1Items = updated
                                    saveList("person1_items", updated)
                                },
                                onAddPerson2 = { name, amt, iconName ->
                                    val updated = person2Items + CashflowItem("p2_${System.currentTimeMillis()}", name, amt, iconName = iconName)
                                    person2Items = updated
                                    saveList("person2_items", updated)
                                },
                                onEditPerson2 = { id, name, amt, iconName ->
                                    val updated = person2Items.map { if (it.id == id) it.copy(name = name, weeklyAmount = amt, iconName = iconName) else it }
                                    person2Items = updated
                                    saveList("person2_items", updated)
                                },
                                onDeletePerson2 = { id ->
                                    val updated = person2Items.filterNot { it.id == id }
                                    person2Items = updated
                                    saveList("person2_items", updated)
                                }
                            )
                            AppSection.PROPERTIES -> PropertiesScreen(
                                appConfig = appConfig,
                                prop1WeeklyMortgage = prop1WeeklyMortgage,
                                prop1Items = prop1Items,
                                isWealthHidden = isWealthHidden,
                                onAddProp1 = { name, amt, iconName ->
                                    val updated = prop1Items + CashflowItem("pr1_${System.currentTimeMillis()}", name, amt, iconName = iconName)
                                    prop1Items = updated
                                    saveList("prop1_items", updated)
                                },
                                onEditProp1 = { id, name, amt, iconName ->
                                    val updated = prop1Items.map { if (it.id == id) it.copy(name = name, weeklyAmount = amt, iconName = iconName) else it }
                                    prop1Items = updated
                                    saveList("prop1_items", updated)
                                },
                                onDeleteProp1 = { id ->
                                    val updated = prop1Items.filterNot { it.id == id }
                                    prop1Items = updated
                                    saveList("prop1_items", updated)
                                },
                                prop2WeeklyMortgage = prop2WeeklyMortgage,
                                prop2Items = prop2Items,
                                onAddProp2 = { name, amt, iconName ->
                                    val updated = prop2Items + CashflowItem("pr2_${System.currentTimeMillis()}", name, amt, iconName = iconName)
                                    prop2Items = updated
                                    saveList("prop2_items", updated)
                                },
                                onEditProp2 = { id, name, amt, iconName ->
                                    val updated = prop2Items.map { if (it.id == id) it.copy(name = name, weeklyAmount = amt, iconName = iconName) else it }
                                    prop2Items = updated
                                    saveList("prop2_items", updated)
                                },
                                onDeleteProp2 = { id ->
                                    val updated = prop2Items.filterNot { it.id == id }
                                    prop2Items = updated
                                    saveList("prop2_items", updated)
                                },
                                prop1MarketValue = prop1EstimatedValue,
                                prop2MarketValue = prop2EstimatedValue,
                                prop1LoanBalance = prop1LiveBalance,
                                prop2LoanBalance = prop2LiveBalance,
                                onUpdateAppConfig = { saveAppConfig(it) }
                            )
                            AppSection.SUPER_SHARES -> SuperAndSharesScreen(
                                appConfig = appConfig,
                                person1Color = person1Color,
                                person2Color = person2Color,
                                sharesList = sharesList,
                                superAccountsList = superAccountsList,
                                isWealthHidden = isWealthHidden,
                                onAddShare = { code, owner, units, pPrice, cPrice ->
                                    val upperCode = code.trim().uppercase()
                                    val existingIndex = sharesList.indexOfFirst { it.code.equals(upperCode, ignoreCase = true) && it.owner.equals(owner, ignoreCase = true) }
                                    if (existingIndex >= 0) {
                                        val existing = sharesList[existingIndex]
                                        val totalUnits = existing.units + units
                                        val avgPPrice = ((existing.units * existing.purchasePrice) + (units * pPrice)) / totalUnits
                                        val updatedParcel = existing.copy(
                                            units = totalUnits,
                                            purchasePrice = avgPPrice,
                                            currentPrice = cPrice
                                        )
                                        val updatedList = sharesList.toMutableList().apply { set(existingIndex, updatedParcel) }
                                        saveSharesList(updatedList)
                                    } else {
                                        val updated = sharesList + ShareParcel("sp_${System.currentTimeMillis()}", upperCode, owner, units, pPrice, cPrice)
                                        saveSharesList(updated)
                                    }
                                },
                                onEditShare = { id, code, owner, units, pPrice, cPrice ->
                                    val updated = sharesList.map { if (it.id == id) it.copy(code = code.uppercase(), owner = owner, units = units, purchasePrice = pPrice, currentPrice = cPrice) else it }
                                    saveSharesList(updated)
                                },
                                onDeleteShare = { id ->
                                    val updated = sharesList.filterNot { it.id == id }
                                    saveSharesList(updated)
                                },
                                onUpdateSharesList = { saveSharesList(it) },
                                onAddSuperAccount = { fundName, owner, bal ->
                                    val updated = superAccountsList + SuperAccount("sup_${System.currentTimeMillis()}", fundName, owner, bal)
                                    saveSuperAccounts(updated)
                                },
                                onEditSuperAccount = { id, fundName, owner, bal ->
                                    val updated = superAccountsList.map { if (it.id == id) it.copy(fundName = fundName, owner = owner, balance = bal) else it }
                                    saveSuperAccounts(updated)
                                },
                                onDeleteSuperAccount = { id ->
                                    val updated = superAccountsList.filterNot { it.id == id }
                                    saveSuperAccounts(updated)
                                }
                            )
                            AppSection.OTHER_ASSETS -> OtherAssetsScreen(
                                appConfig = appConfig,
                                person1Color = person1Color,
                                person2Color = person2Color,
                                otherAssetsList = otherAssetsList,
                                isWealthHidden = isWealthHidden,
                                onAddOtherAsset = { name, owner, valAmt ->
                                    val updated = otherAssetsList + OtherAsset("oa_${System.currentTimeMillis()}", name, owner, valAmt)
                                    saveOtherAssets(updated)
                                },
                                onEditOtherAsset = { id, name, owner, valAmt ->
                                    val updated = otherAssetsList.map { if (it.id == id) it.copy(name = name, owner = owner, value = valAmt) else it }
                                    saveOtherAssets(updated)
                                },
                                onDeleteOtherAsset = { id ->
                                    val updated = otherAssetsList.filterNot { it.id == id }
                                    saveOtherAssets(updated)
                                }
                            )
                            AppSection.LOANS -> LoansAndOffsetScreen(
                                appConfig = appConfig,
                                prop1Loan = prop1Loan,
                                prop2Loan = prop2Loan,
                                isWealthHidden = isWealthHidden,
                                onUpdateProp1 = { saveProp1Loan(it) },
                                onUpdateProp2 = { saveProp2Loan(it) }
                            )
                            AppSection.WHAT_IF -> WhatIfScenarioScreen(
                                appConfig = appConfig,
                                person1Income = person1Income,
                                person2Income = person2Income,
                                person1Items = person1Items,
                                person2Items = person2Items,
                                prop1Items = prop1Items,
                                prop2Items = prop2Items,
                                prop1WeeklyMortgage = prop1WeeklyMortgage,
                                prop2WeeklyMortgage = prop2WeeklyMortgage,
                                prop1MarketValue = prop1EstimatedValue,
                                prop2MarketValue = prop2EstimatedValue,
                                prop1Loan = prop1Loan,
                                prop2Loan = prop2Loan,
                                sharesList = sharesList,
                                superAccountsList = superAccountsList,
                                otherAssetsList = otherAssetsList,
                                isWealthHidden = isWealthHidden,
                                horizonYears = whatIfHorizonYears,
                                onHorizonYearsChange = { whatIfHorizonYears = it },
                                lifestyleAdjustmentWeekly = whatIfLifestyleAdjustmentWeekly,
                                onLifestyleAdjustmentWeeklyChange = { whatIfLifestyleAdjustmentWeekly = it },
                                rateShockProp1 = whatIfRateShockProp1,
                                onRateShockProp1Change = { whatIfRateShockProp1 = it },
                                rateShockProp2 = whatIfRateShockProp2,
                                onRateShockProp2Change = { whatIfRateShockProp2 = it },
                                rentalIncomeGrowthWeekly = whatIfRentalGrowthWeekly,
                                onRentalIncomeGrowthWeeklyChange = { whatIfRentalGrowthWeekly = it },
                                superBoostWeekly = whatIfSuperBoostWeekly,
                                onSuperBoostWeeklyChange = { whatIfSuperBoostWeekly = it },
                                customLevers = whatIfCustomLevers,
                                milestoneList = whatIfMilestoneList,
                                financialContextSummary = detailedFinancialContext,
                                onExecuteAiActions = ::executeAiActions
                            )
                            AppSection.SETTINGS -> SettingsScreen(
                                appConfig = appConfig,
                                onUpdateAppConfig = { updatedCfg ->
                                    saveAppConfig(updatedCfg)
                                    if (updatedCfg.property1Name != prop1Loan.name) {
                                        saveProp1Loan(prop1Loan.copy(name = updatedCfg.property1Name))
                                    }
                                    if (updatedCfg.property2Name != prop2Loan.name) {
                                        saveProp2Loan(prop2Loan.copy(name = updatedCfg.property2Name))
                                    }
                                },
                                onOpenColorPicker = { showColorPickerDialog = true },
                                isDarkMode = isDarkMode,
                                onDarkModeChange = onDarkModeChange,
                                isBiometricEnabled = isBiometricEnabled,
                                onBiometricChange = { enabled ->
                                    isBiometricEnabled = enabled
                                    sharedPrefs.edit { putBoolean("biometric_enabled", enabled) }
                                },
                                onRerunSetupWizard = {
                                    saveAppConfig(appConfig.copy(isSetupCompleted = false))
                                },
                                onSignOut = onSignOutUser,
                                prop1Loan = prop1Loan,
                                prop2Loan = prop2Loan,
                                isWealthHidden = isWealthHidden,
                                onUpdateProp1Loan = { saveProp1Loan(it) },
                                onUpdateProp2Loan = { saveProp2Loan(it) },
                                householdInviteCode = activeHouseholdUid.take(6).uppercase(),
                                isPartnerSession = isLoggedInAsPartner,
                                onJoinHousehold = { codeOrEmail, cb -> joinHouseholdByCodeOrEmail(codeOrEmail, cb) }
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                    ) {
                        topBarContent()
                    }
                    if (appConfig.useExpnzAi) {
                        FloatingAiAssistantWidget(
                            financialContextSummary = detailedFinancialContext,
                            personName = activeLoggedInPersonName,
                            messages = geminiMessages,
                            onAddImportedShares = { newParcels: List<ShareParcel> ->
                                val updated = sharesList + newParcels
                                saveSharesList(updated)
                            },
                            onExecuteAiActions = ::executeAiActions
                        )
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = !isHorizontalChildTouched,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val section = activeSections[page]
                    when (section) {
                        AppSection.TOTAL_WEALTH -> TotalWealthScreen(
                            appConfig = appConfig,
                            prop1MarketValue = prop1EstimatedValue,
                            prop2MarketValue = prop2EstimatedValue,
                            sharesList = sharesList,
                            superAccountsList = superAccountsList,
                            otherAssetsList = otherAssetsList,
                            prop1Loan = prop1Loan,
                            prop2Loan = prop2Loan,
                            person1Items = person1Items,
                            person2Items = person2Items,
                            isWealthHidden = isWealthHidden,
                            onNavigateToSettings = {
                                coroutineScope.launch {
                                    val settingsIdx = activeSections.indexOf(AppSection.SETTINGS)
                                    if (settingsIdx >= 0) pagerState.animateScrollToPage(settingsIdx)
                                }
                            },
                            onNavigateToWhatIf = {
                                coroutineScope.launch {
                                    val whatIfIdx = activeSections.indexOf(AppSection.WHAT_IF)
                                    if (whatIfIdx >= 0) pagerState.animateScrollToPage(whatIfIdx)
                                }
                            }
                        )
                        AppSection.CASH_FLOW -> MoneyPlanScreen(
                            appConfig = appConfig,
                            person1Color = person1Color,
                            person2Color = person2Color,
                            person1Income = person1Income,
                            person2Income = person2Income,
                            isWealthHidden = isWealthHidden,
                            isPartnerLoggedIn = isLoggedInAsPartner,
                            onUpdateIncome = { isPerson1, newInc ->
                                if (isPerson1) {
                                    person1Income = newInc
                                    sharedPrefs.edit { putFloat("person1_inc", newInc.toFloat()) }
                                    updateFirestore("person1_inc", newInc)
                                } else {
                                    person2Income = newInc
                                    sharedPrefs.edit { putFloat("person2_inc", newInc.toFloat()) }
                                    updateFirestore("person2_inc", newInc)
                                }
                            },
                            perPersonPropertyShare = perPersonPropertyShare,
                            person1Items = person1Items,
                            person2Items = person2Items,
                            onAddPerson1 = { name, amt, iconName ->
                                val updated = person1Items + CashflowItem("p1_${System.currentTimeMillis()}", name, amt, iconName = iconName)
                                person1Items = updated
                                saveList("person1_items", updated)
                            },
                            onEditPerson1 = { id, name, amt, iconName ->
                                val updated = person1Items.map { if (it.id == id) it.copy(name = name, weeklyAmount = amt, iconName = iconName) else it }
                                person1Items = updated
                                saveList("person1_items", updated)
                            },
                            onDeletePerson1 = { id ->
                                val updated = person1Items.filterNot { it.id == id }
                                person1Items = updated
                                saveList("person1_items", updated)
                            },
                            onAddPerson2 = { name, amt, iconName ->
                                val updated = person2Items + CashflowItem("p2_${System.currentTimeMillis()}", name, amt, iconName = iconName)
                                person2Items = updated
                                saveList("person2_items", updated)
                            },
                            onEditPerson2 = { id, name, amt, iconName ->
                                val updated = person2Items.map { if (it.id == id) it.copy(name = name, weeklyAmount = amt, iconName = iconName) else it }
                                person2Items = updated
                                saveList("person2_items", updated)
                            },
                            onDeletePerson2 = { id ->
                                val updated = person2Items.filterNot { it.id == id }
                                person2Items = updated
                                saveList("person2_items", updated)
                            }
                        )
                        AppSection.PROPERTIES -> PropertiesScreen(
                            appConfig = appConfig,
                            prop1WeeklyMortgage = prop1WeeklyMortgage,
                            prop1Items = prop1Items,
                            isWealthHidden = isWealthHidden,
                            onAddProp1 = { name, amt, iconName ->
                                val updated = prop1Items + CashflowItem("pr1_${System.currentTimeMillis()}", name, amt, iconName = iconName)
                                prop1Items = updated
                                saveList("prop1_items", updated)
                            },
                            onEditProp1 = { id, name, amt, iconName ->
                                val updated = prop1Items.map { if (it.id == id) it.copy(name = name, weeklyAmount = amt, iconName = iconName) else it }
                                prop1Items = updated
                                saveList("prop1_items", updated)
                            },
                            onDeleteProp1 = { id ->
                                val updated = prop1Items.filterNot { it.id == id }
                                prop1Items = updated
                                saveList("prop1_items", updated)
                            },
                            prop2WeeklyMortgage = prop2WeeklyMortgage,
                            prop2Items = prop2Items,
                            onAddProp2 = { name, amt, iconName ->
                                val updated = prop2Items + CashflowItem("pr2_${System.currentTimeMillis()}", name, amt, iconName = iconName)
                                prop2Items = updated
                                saveList("prop2_items", updated)
                            },
                            onEditProp2 = { id, name, amt, iconName ->
                                val updated = prop2Items.map { if (it.id == id) it.copy(name = name, weeklyAmount = amt, iconName = iconName) else it }
                                prop2Items = updated
                                saveList("prop2_items", updated)
                            },
                            onDeleteProp2 = { id ->
                                val updated = prop2Items.filterNot { it.id == id }
                                prop2Items = updated
                                saveList("prop2_items", updated)
                            },
                            prop1MarketValue = prop1EstimatedValue,
                            prop2MarketValue = prop2EstimatedValue,
                            prop1LoanBalance = prop1LiveBalance,
                            prop2LoanBalance = prop2LiveBalance,
                            onUpdateAppConfig = { saveAppConfig(it) }
                        )
                        AppSection.SUPER_SHARES -> SuperAndSharesScreen(
                            appConfig = appConfig,
                            person1Color = person1Color,
                            person2Color = person2Color,
                            sharesList = sharesList,
                            superAccountsList = superAccountsList,
                            isWealthHidden = isWealthHidden,
                            onAddShare = { code, owner, units, pPrice, cPrice ->
                                val upperCode = code.trim().uppercase()
                                val existingIndex = sharesList.indexOfFirst { it.code.equals(upperCode, ignoreCase = true) && it.owner.equals(owner, ignoreCase = true) }
                                if (existingIndex >= 0) {
                                    val existing = sharesList[existingIndex]
                                    val totalUnits = existing.units + units
                                    val avgPPrice = ((existing.units * existing.purchasePrice) + (units * pPrice)) / totalUnits
                                    val updatedParcel = existing.copy(
                                        units = totalUnits,
                                        purchasePrice = avgPPrice,
                                        currentPrice = cPrice
                                    )
                                    val updatedList = sharesList.toMutableList().apply { set(existingIndex, updatedParcel) }
                                    saveSharesList(updatedList)
                                } else {
                                    val updated = sharesList + ShareParcel("sp_${System.currentTimeMillis()}", upperCode, owner, units, pPrice, cPrice)
                                    saveSharesList(updated)
                                }
                            },
                            onEditShare = { id, code, owner, units, pPrice, cPrice ->
                                val updated = sharesList.map { if (it.id == id) it.copy(code = code.uppercase(), owner = owner, units = units, purchasePrice = pPrice, currentPrice = cPrice) else it }
                                saveSharesList(updated)
                            },
                            onDeleteShare = { id ->
                                val updated = sharesList.filterNot { it.id == id }
                                saveSharesList(updated)
                            },
                            onUpdateSharesList = { saveSharesList(it) },
                            onAddSuperAccount = { fundName, owner, bal ->
                                val updated = superAccountsList + SuperAccount("sup_${System.currentTimeMillis()}", fundName, owner, bal)
                                saveSuperAccounts(updated)
                            },
                            onEditSuperAccount = { id, fundName, owner, bal ->
                                val updated = superAccountsList.map { if (it.id == id) it.copy(fundName = fundName, owner = owner, balance = bal) else it }
                                saveSuperAccounts(updated)
                            },
                            onDeleteSuperAccount = { id ->
                                val updated = superAccountsList.filterNot { it.id == id }
                                saveSuperAccounts(updated)
                            }
                        )
                        AppSection.OTHER_ASSETS -> OtherAssetsScreen(
                            appConfig = appConfig,
                            person1Color = person1Color,
                            person2Color = person2Color,
                            otherAssetsList = otherAssetsList,
                            isWealthHidden = isWealthHidden,
                            onAddOtherAsset = { name, owner, valAmt ->
                                val updated = otherAssetsList + OtherAsset("oa_${System.currentTimeMillis()}", name, owner, valAmt)
                                saveOtherAssets(updated)
                            },
                            onEditOtherAsset = { id, name, owner, valAmt ->
                                val updated = otherAssetsList.map { if (it.id == id) it.copy(name = name, owner = owner, value = valAmt) else it }
                                saveOtherAssets(updated)
                            },
                            onDeleteOtherAsset = { id ->
                                val updated = otherAssetsList.filterNot { it.id == id }
                                saveOtherAssets(updated)
                            }
                        )
                        AppSection.LOANS -> LoansAndOffsetScreen(
                            appConfig = appConfig,
                            prop1Loan = prop1Loan,
                            prop2Loan = prop2Loan,
                            isWealthHidden = isWealthHidden,
                            onUpdateProp1 = { saveProp1Loan(it) },
                            onUpdateProp2 = { saveProp2Loan(it) }
                        )
                        AppSection.WHAT_IF -> WhatIfScenarioScreen(
                            appConfig = appConfig,
                            person1Income = person1Income,
                            person2Income = person2Income,
                            person1Items = person1Items,
                            person2Items = person2Items,
                            prop1Items = prop1Items,
                            prop2Items = prop2Items,
                            prop1WeeklyMortgage = prop1WeeklyMortgage,
                            prop2WeeklyMortgage = prop2WeeklyMortgage,
                            prop1MarketValue = prop1EstimatedValue,
                            prop2MarketValue = prop2EstimatedValue,
                            prop1Loan = prop1Loan,
                            prop2Loan = prop2Loan,
                            sharesList = sharesList,
                            superAccountsList = superAccountsList,
                            otherAssetsList = otherAssetsList,
                            isWealthHidden = isWealthHidden,
                            horizonYears = whatIfHorizonYears,
                            onHorizonYearsChange = { whatIfHorizonYears = it },
                            lifestyleAdjustmentWeekly = whatIfLifestyleAdjustmentWeekly,
                            onLifestyleAdjustmentWeeklyChange = { whatIfLifestyleAdjustmentWeekly = it },
                            rateShockProp1 = whatIfRateShockProp1,
                            onRateShockProp1Change = { whatIfRateShockProp1 = it },
                            rateShockProp2 = whatIfRateShockProp2,
                            onRateShockProp2Change = { whatIfRateShockProp2 = it },
                            rentalIncomeGrowthWeekly = whatIfRentalGrowthWeekly,
                            onRentalIncomeGrowthWeeklyChange = { whatIfRentalGrowthWeekly = it },
                            superBoostWeekly = whatIfSuperBoostWeekly,
                            onSuperBoostWeeklyChange = { whatIfSuperBoostWeekly = it },
                            customLevers = whatIfCustomLevers,
                            milestoneList = whatIfMilestoneList,
                            financialContextSummary = detailedFinancialContext,
                            onExecuteAiActions = ::executeAiActions
                        )
                        AppSection.SETTINGS -> SettingsScreen(
                            appConfig = appConfig,
                            onUpdateAppConfig = { updatedCfg ->
                                saveAppConfig(updatedCfg)
                                if (updatedCfg.property1Name != prop1Loan.name) {
                                    saveProp1Loan(prop1Loan.copy(name = updatedCfg.property1Name))
                                }
                                if (updatedCfg.property2Name != prop2Loan.name) {
                                    saveProp2Loan(prop2Loan.copy(name = updatedCfg.property2Name))
                                }
                            },
                            onOpenColorPicker = { showColorPickerDialog = true },
                            isDarkMode = isDarkMode,
                            onDarkModeChange = onDarkModeChange,
                            isBiometricEnabled = isBiometricEnabled,
                            onBiometricChange = { enabled ->
                                isBiometricEnabled = enabled
                                sharedPrefs.edit { putBoolean("biometric_enabled", enabled) }
                            },
                            onRerunSetupWizard = {
                                saveAppConfig(appConfig.copy(isSetupCompleted = false))
                            },
                            onSignOut = onSignOutUser,
                            prop1Loan = prop1Loan,
                            prop2Loan = prop2Loan,
                            isWealthHidden = isWealthHidden,
                            onUpdateProp1Loan = { saveProp1Loan(it) },
                            onUpdateProp2Loan = { saveProp2Loan(it) },
                            householdInviteCode = activeHouseholdUid.take(6).uppercase(),
                            isPartnerSession = isLoggedInAsPartner,
                            onJoinHousehold = { codeOrEmail, cb -> joinHouseholdByCodeOrEmail(codeOrEmail, cb) }
                        )
                    }
                }

                // Fixed Top Bar overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                ) {
                    topBarContent()
                }

                // Floating Apple Liquid Glass Pill Dock Footer
                val dockShape = RoundedCornerShape(32.dp)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                            .liquidGlass(
                                shape = dockShape,
                                tintColor = BrandForestDark,
                                intensity = 1.20f,
                                accentRimColor = currentSection.getColor(appConfig),
                                role = LiquidGlassRole.FOOTER
                            )
                    ) {
                        activeSections.forEachIndexed { index, section ->
                            NavigationBarItem(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                icon = { Icon(section.icon, contentDescription = section.title) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BrandForestDark,
                                    indicatorColor = section.getColor(appConfig).copy(alpha = 0.92f),
                                    unselectedIconColor = Color.White.copy(alpha = 0.78f)
                                )
                            )
                        }
                    }
                }

                if (appConfig.useExpnzAi) {
                    FloatingAiAssistantWidget(
                        financialContextSummary = detailedFinancialContext,
                        personName = activeLoggedInPersonName,
                        messages = geminiMessages,
                        onAddImportedShares = { newParcels: List<ShareParcel> ->
                            val updated = sharesList + newParcels
                            saveSharesList(updated)
                        },
                        onExecuteAiActions = ::executeAiActions
                    )
                }
            }
        }
    }
    }
}

// ============================================================================
// CUSTOM USER & PARTNER COLOR PICKER DIALOG
// ============================================================================
@Composable
fun UserColorPickerDialog(
    appConfig: UserAppConfig,
    onDismiss: () -> Unit,
    onSaveColors: (Long, Long) -> Unit
) {
    var selectedP1Hex by remember { mutableLongStateOf(appConfig.person1ColorHex) }
    var selectedP2Hex by remember { mutableLongStateOf(appConfig.person2ColorHex) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Default.Palette, contentDescription = null, tint = BrandCoralPink)
                Text("Pick Profile Colours", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Choose the accent colour for ${appConfig.person1Name}${if (appConfig.isCoupleMode) " and ${appConfig.person2Name}" else ""} across all menus:",
                    fontSize = 13.sp,
                    color = SlateGray
                )

                // Person 1 Color Picker
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${appConfig.person1Name}'s Colour", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(selectedP1Hex))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(UserColorPalette) { (label, hex) ->
                            val isSelected = selectedP1Hex == hex
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(hex))
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                                    .clickable { selectedP1Hex = hex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = label, tint = BrandForestDark, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }

                // Person 2 Color Picker (if Couple Mode)
                if (appConfig.isCoupleMode) {
                    HorizontalDivider()
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("${appConfig.person2Name}'s Colour", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(selectedP2Hex))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(UserColorPalette) { (label, hex) ->
                                val isSelected = selectedP2Hex == hex
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(hex))
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.3f),
                                            shape = CircleShape
                                        )
                                        .clickable { selectedP2Hex = hex },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = label, tint = BrandForestDark, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSaveColors(selectedP1Hex, selectedP2Hex) },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark)
            ) {
                Text("Apply Colours", fontWeight = FontWeight.ExtraBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ============================================================================
// FIRST-TIME SETUP & ONBOARDING WIZARD
// ============================================================================
@Composable
fun OnboardingSetupScreen(
    initialConfig: UserAppConfig,
    initialPerson1Income: Double,
    initialPerson2Income: Double,
    initialProp1Value: Double,
    initialProp1LoanBalance: Double,
    initialProp1Rate: Double,
    initialProp1Offset: Double,
    initialPrimaryRent: Double,
    onCompleteSetup: (
        config: UserAppConfig,
        p1Inc: Double,
        p2Inc: Double,
        prop1Val: Double,
        prop1LoanBal: Double,
        prop1Rate: Double,
        prop1Offset: Double,
        primaryRent: Double,
        customSupers: List<SuperAccount>,
        customShares: List<ShareParcel>,
        customOtherAssets: List<OtherAsset>
    ) -> Unit
) {
    var step by rememberSaveable { mutableIntStateOf(1) }

    // Step 1: Personal Profile & Colors State
    var isCoupleMode by rememberSaveable { mutableStateOf(initialConfig.isCoupleMode) }
    var person1Name by rememberSaveable { mutableStateOf(if (initialConfig.person1Name == "User 1") "" else initialConfig.person1Name) }
    var person2Name by rememberSaveable { mutableStateOf(if (initialConfig.person2Name == "Partner") "" else initialConfig.person2Name) }
    var person1ColorHex by rememberSaveable { mutableLongStateOf(initialConfig.person1ColorHex) }
    var person2ColorHex by rememberSaveable { mutableLongStateOf(initialConfig.person2ColorHex) }
    var person1IncomeStr by rememberSaveable { mutableStateOf(initialPerson1Income.toInt().toString()) }
    var person2IncomeStr by rememberSaveable { mutableStateOf(initialPerson2Income.toInt().toString()) }

    // Step 2: Module Toggles State
    var showTotalWealth by rememberSaveable { mutableStateOf(initialConfig.showTotalWealth) }
    var showCashFlow by rememberSaveable { mutableStateOf(initialConfig.showCashFlow) }
    var showProperties by rememberSaveable { mutableStateOf(initialConfig.showProperties) }
    var useExpnzAi by rememberSaveable { mutableStateOf(initialConfig.useExpnzAi) }
    var showLoans by rememberSaveable { mutableStateOf(initialConfig.showLoans) }
    var showSuperAndShares by rememberSaveable { mutableStateOf(initialConfig.showSuperAndShares) }
    var showOtherAssets by rememberSaveable { mutableStateOf(initialConfig.showOtherAssets) }

    // Step 3: Property & Loan Quick-Setup
    var property1Name by rememberSaveable { mutableStateOf(initialConfig.property1Name) }
    var isProperty1Rented by rememberSaveable { mutableStateOf(initialConfig.isProperty1Rented) }
    var hasSecondProperty by rememberSaveable { mutableStateOf(initialConfig.hasSecondProperty) }
    var property2Name by rememberSaveable { mutableStateOf(initialConfig.property2Name) }
    var isProperty2Rented by rememberSaveable { mutableStateOf(initialConfig.isProperty2Rented) }
    var prop1ValueStr by rememberSaveable { mutableStateOf(initialProp1Value.toInt().toString()) }
    var prop1LoanBalStr by rememberSaveable { mutableStateOf(initialProp1LoanBalance.toInt().toString()) }
    var prop1RateStr by rememberSaveable { mutableStateOf(initialProp1Rate.toString()) }
    var prop1OffsetStr by rememberSaveable { mutableStateOf(initialProp1Offset.toInt().toString()) }
    var prop1RentStr by rememberSaveable { mutableStateOf(initialPrimaryRent.toInt().toString()) }

    // Step 4: Super & Shares Quick-Setup
    val customShares = remember { mutableStateListOf<ShareParcel>() }
    val customSuperAccounts = remember { mutableStateListOf<SuperAccount>() }
    
    // Step 5: Other Assets Quick-Setup
    var otherAssetName by rememberSaveable { mutableStateOf("") }
    var otherAssetValueStr by rememberSaveable { mutableStateOf("") }

    val steps = mutableListOf(1, 2)
    if (showProperties || showLoans) steps.add(3)
    if (showSuperAndShares) steps.add(4)
    if (showOtherAssets) steps.add(5)
    val totalSteps = steps.size
    val currentLogicalStep = steps.getOrNull(step - 1) ?: steps.last()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                AppLogoIcon(size = 68.dp)
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "STEP $step OF $totalSteps",
                    color = BrandCoralPink,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = when (currentLogicalStep) {
                        1 -> "Welcome! Let's Set Up Your Profile"
                        2 -> "Choose What You Want to Track"
                        3 -> "Your Property & Home Loan Details"
                        4 -> "Super & Shares Configuration"
                        5 -> "Other Assets Setup"
                        else -> ""
                    },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = when (currentLogicalStep) {
                        1 -> "Personalize your name, pick your favourite profile colours, and choose solo or partner tracking."
                        2 -> "Toggle on the financial sections you want to use right now. You can always enable or disable any menu later in Settings!"
                        3 -> "Enter your primary property and mortgage details to power live equity and offset amortization."
                        4 -> "Enter some starting values for your Superannuation and Share portfolio."
                        5 -> "Add an initial asset or savings to complete your wealth profile."
                        else -> ""
                    },
                    fontSize = 13.sp,
                    color = SlateGray,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (currentLogicalStep == 1) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("Tracking Mode", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FilterChip(
                                selected = !isCoupleMode,
                                onClick = { isCoupleMode = false },
                                label = { Text("Individual (Solo)", modifier = Modifier.padding(vertical = 6.dp)) },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = isCoupleMode,
                                onClick = { isCoupleMode = true },
                                label = { Text("Couple / Partner", modifier = Modifier.padding(vertical = 6.dp)) },
                                leadingIcon = { Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = person1Name,
                            onValueChange = { person1Name = it },
                            label = { Text(if (isCoupleMode) "Your First Name (Person 1)" else "Your First Name") },
                            placeholder = { Text("e.g. Alex") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = person1IncomeStr,
                            onValueChange = { person1IncomeStr = it },
                            label = { Text("${person1Name.ifBlank { "Your" }} Weekly Take-Home Income ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Text("Pick ${person1Name.ifBlank { "Your" }} Profile Colour:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(person1ColorHex))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(UserColorPalette) { (label, hex) ->
                                val isSel = person1ColorHex == hex
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(hex))
                                        .border(if (isSel) 3.dp else 1.dp, if (isSel) Color.White else Color.Transparent, CircleShape)
                                        .clickable { person1ColorHex = hex },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSel) Icon(Icons.Default.Check, contentDescription = label, tint = BrandForestDark, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        AnimatedVisibility(visible = isCoupleMode) {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                HorizontalDivider()
                                OutlinedTextField(
                                    value = person2Name,
                                    onValueChange = { person2Name = it },
                                    label = { Text("Partner's First Name (Person 2)") },
                                    placeholder = { Text("e.g. Sam") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = person2IncomeStr,
                                    onValueChange = { person2IncomeStr = it },
                                    label = { Text("${person2Name.ifBlank { "Partner's" }} Weekly Take-Home Income ($)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Text("Pick ${person2Name.ifBlank { "Partner's" }} Profile Colour:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(person2ColorHex))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(UserColorPalette) { (label, hex) ->
                                        val isSel = person2ColorHex == hex
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color(hex))
                                                .border(if (isSel) 3.dp else 1.dp, if (isSel) Color.White else Color.Transparent, CircleShape)
                                                .clickable { person2ColorHex = hex },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSel) Icon(Icons.Default.Check, contentDescription = label, tint = BrandForestDark, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (currentLogicalStep == 2) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Select Which Menus & Features to Enable",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Everything is available below. Pick what suits your goals today — you can toggle any feature on or off later in Settings.",
                            fontSize = 12.sp,
                            color = SlateGray
                        )

                        Spacer(Modifier.height(4.dp))

                        FeatureToggleRow(
                            title = "Total Wealth Dashboard (Front Screen)",
                            subtitle = "Show net worth summary & 3D asset allocation chart on the home screen",
                            icon = Icons.Default.PieChart,
                            accent = BrandCoralPink,
                            checked = showTotalWealth,
                            onCheckedChange = { showTotalWealth = it }
                        )

                        HorizontalDivider()

                        FeatureToggleRow(
                            title = "Personal Finances & Cash Flow",
                            subtitle = "Track weekly income, personal bills, groceries, and weekly surplus",
                            icon = Icons.Default.AccountBalanceWallet,
                            accent = BrandMintGreen,
                            checked = showCashFlow,
                            onCheckedChange = { showCashFlow = it }
                        )

                        HorizontalDivider()

                        FeatureToggleRow(
                            title = "Home Loans & Offset Tracker",
                            subtitle = "Track mortgage balance, interest rate changes, offset savings & fortnightly payoff schedule",
                            icon = Icons.Default.QueryStats,
                            accent = BrandCoralLight,
                            checked = showLoans,
                            onCheckedChange = { showLoans = it }
                        )

                        HorizontalDivider()

                        FeatureToggleRow(
                            title = "Properties & Real Estate",
                            subtitle = "Track property market valuations, net equity, rental income & council/water rates",
                            icon = Icons.Default.HomeWork,
                            accent = BrandWarmAmber,
                            checked = showProperties,
                            onCheckedChange = { showProperties = it }
                        )

                        HorizontalDivider()

                        FeatureToggleRow(
                            title = "Shares & Superannuation",
                            subtitle = "Track ASX stock parcels with live price sync and retirement Super accounts",
                            icon = Icons.AutoMirrored.Filled.TrendingUp,
                            accent = BrandSkyBlue,
                            checked = showSuperAndShares,
                            onCheckedChange = { showSuperAndShares = it }
                        )

                        HorizontalDivider()

                        FeatureToggleRow(
                            title = "Other Assets (Cars, Cash, Collectibles)",
                            subtitle = "Track vehicles, high-interest savings, and other personal assets",
                            icon = Icons.Default.DirectionsCar,
                            accent = BrandPurple,
                            checked = showOtherAssets,
                            onCheckedChange = { showOtherAssets = it }
                        )

                        HorizontalDivider()

                        FeatureToggleRow(
                            title = "EXPNZ AI Chatbot (Gemini)",
                            subtitle = "Use the built-in AI assistant to ask financial advice and interact with your dashboard data directly",
                            icon = Icons.AutoMirrored.Filled.Chat,
                            accent = BrandCoralPink,
                            checked = useExpnzAi,
                            onCheckedChange = { useExpnzAi = it }
                        )
                    }
                }
            }
        }

        if (currentLogicalStep == 3) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("Primary Property & Mortgage Setup", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                        OutlinedTextField(
                            value = property1Name,
                            onValueChange = { property1Name = it },
                            label = { Text("Primary Property Address") },
                            placeholder = { Text("e.g. 123 Smith St, Sydney") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Text("Do you own this property (or have a mortgage) or do you rent it?", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = !isProperty1Rented,
                                onClick = { isProperty1Rented = false },
                                label = { Text("Own / Mortgage") }
                            )
                            FilterChip(
                                selected = isProperty1Rented,
                                onClick = { isProperty1Rented = true },
                                label = { Text("Rent (Tenant)") }
                            )
                        }

                        if (isProperty1Rented) {
                            OutlinedTextField(
                                value = prop1RentStr,
                                onValueChange = { prop1RentStr = it },
                                label = { Text("Weekly Rent Amount ($)") },
                                placeholder = { Text("e.g. 450") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        } else {
                            if (showProperties) {
                                OutlinedTextField(
                                    value = prop1ValueStr,
                                    onValueChange = { prop1ValueStr = it },
                                    label = { Text("Estimated Market Value ($)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            if (showLoans) {
                                OutlinedTextField(
                                    value = prop1LoanBalStr,
                                    onValueChange = { prop1LoanBalStr = it },
                                    label = { Text("Current Home Loan Balance ($)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedTextField(
                                        value = prop1RateStr,
                                        onValueChange = { prop1RateStr = it },
                                        label = { Text("Interest Rate (% p.a.)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    OutlinedTextField(
                                        value = prop1OffsetStr,
                                        onValueChange = { prop1OffsetStr = it },
                                        label = { Text("Offset Balance ($)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }
                            }
                        }

                        if (!isProperty1Rented) {
                            HorizontalDivider()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Track a Second / Investment Property?", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Enable if you also have a rental or second property", fontSize = 12.sp, color = SlateGray)
                                }
                                Switch(
                                    checked = hasSecondProperty,
                                    onCheckedChange = { hasSecondProperty = it }
                                )
                            }

                            AnimatedVisibility(visible = hasSecondProperty) {
                                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                    OutlinedTextField(
                                        value = property2Name,
                                        onValueChange = { property2Name = it },
                                        label = { Text("Second Property Address") },
                                        placeholder = { Text("e.g. 456 Jones Rd, Melbourne") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                        Text("Is this an investment property or a holiday home you own, or are you renting it as a tenant?", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            FilterChip(
                                                selected = !isProperty2Rented,
                                                onClick = { isProperty2Rented = false },
                                                label = { Text("Own / Investment") }
                                            )
                                            FilterChip(
                                                selected = isProperty2Rented,
                                                onClick = { isProperty2Rented = true },
                                                label = { Text("Rent (Tenant)") }
                                            )
                                        }
                                }
                            }
                        } else {
                            LaunchedEffect(Unit) { hasSecondProperty = false }
                        }
                    }
                }
            }
        }

        if (currentLogicalStep == 4) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("Superannuation & Shares Setup", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Optionally enter details below, or skip for now to configure later.", fontSize = 13.sp, color = SlateGray)
                        
                        Text("Super Accounts", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandCoralPink)
                        customSuperAccounts.forEachIndexed { index, account ->
                            OutlinedTextField(
                                value = account.fundName,
                                onValueChange = { customSuperAccounts[index] = account.copy(fundName = it) },
                                label = { Text("Super Fund Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = if (account.balance == 0.0) "" else account.balance.toString(),
                                onValueChange = { customSuperAccounts[index] = account.copy(balance = it.toDoubleOrNull() ?: 0.0) },
                                label = { Text("Total Super Balance ($)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        Button(onClick = { customSuperAccounts.add(SuperAccount("sup_${System.currentTimeMillis()}_${customSuperAccounts.size}", "New Super Fund", person1Name, 0.0)) }) {
                            Text("Add Super Account")
                        }
                        
                        HorizontalDivider()
                        
                        Text("Share Parcels", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandMintGreen)
                        customShares.forEachIndexed { index, share ->
                            OutlinedTextField(
                                value = share.code,
                                onValueChange = { customShares[index] = share.copy(code = it) },
                                label = { Text("Share Ticker (e.g. CBA)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = if (share.units == 0.0) "" else share.units.toString(),
                                onValueChange = { customShares[index] = share.copy(units = it.toDoubleOrNull() ?: 0.0) },
                                label = { Text("Number of Units") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        Button(onClick = { customShares.add(ShareParcel("shr_${System.currentTimeMillis()}_${customShares.size}", "TICKER", person1Name, 0.0, 0.0, 0.0)) }) {
                            Text("Add Share Parcel")
                        }
                    }
                }
            }
        }

        if (currentLogicalStep == 5) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text("Other Assets Setup", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Add an initial asset (like a Car or Savings Account) or skip for now.", fontSize = 13.sp, color = SlateGray)
                        
                        OutlinedTextField(
                            value = otherAssetName,
                            onValueChange = { otherAssetName = it },
                            label = { Text("Asset Name (Optional)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = otherAssetValueStr,
                            onValueChange = { otherAssetValueStr = it },
                            label = { Text("Asset Value (Optional)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }

        // Navigation Buttons
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        if (step < totalSteps) {
                            step++
                        } else {
                            val anySelected = showTotalWealth || showCashFlow || showProperties || showLoans || showSuperAndShares || showOtherAssets
                            val finalShowWealth = showTotalWealth || !anySelected

                            val finalConfig = UserAppConfig(
                                isSetupCompleted = true,
                                isCoupleMode = isCoupleMode,
                                person1Name = person1Name.trim().ifBlank { "Me" },
                                person2Name = person2Name.trim().ifBlank { "Partner" },
                                person1ColorHex = person1ColorHex,
                                person2ColorHex = person2ColorHex,
                                property1Name = property1Name.trim().ifBlank { "Primary Home" },
                                property2Name = property2Name.trim().ifBlank { "Investment Property" },
                                isProperty1Rented = isProperty1Rented,
                                isProperty2Rented = isProperty2Rented,
                                hasSecondProperty = hasSecondProperty,
                                showTotalWealth = finalShowWealth,
                                showCashFlow = showCashFlow,
                                showProperties = showProperties,
                                showSuperAndShares = showSuperAndShares,
                                showOtherAssets = showOtherAssets,
                                showLoans = showLoans,
                                useExpnzAi = useExpnzAi
                            )
                            val supers = customSuperAccounts.toList()
                            val shares = customShares.toList()
                            val p1N = person1Name.trim().ifBlank { "Me" }
                            
                            val otherAssets = mutableListOf<OtherAsset>()
                            if (otherAssetName.isNotBlank() && otherAssetValueStr.isNotBlank()) {
                                otherAssets.add(OtherAsset("ast_${System.currentTimeMillis()}", otherAssetName, p1N, otherAssetValueStr.toDoubleOrNull() ?: 0.0))
                            }

                            onCompleteSetup(
                                finalConfig,
                                person1IncomeStr.toDoubleOrNull() ?: 1200.0,
                                person2IncomeStr.toDoubleOrNull() ?: 1000.0,
                                prop1ValueStr.toDoubleOrNull() ?: 750000.0,
                                prop1LoanBalStr.toDoubleOrNull() ?: 465000.0,
                                prop1RateStr.toDoubleOrNull() ?: 6.29,
                                prop1OffsetStr.toDoubleOrNull() ?: 45000.0,
                                prop1RentStr.toDoubleOrNull() ?: 500.0,
                                supers,
                                shares,
                                otherAssets
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandCoralPink,
                        contentColor = BrandForestDark
                    )
                ) {
                    Text(
                        text = if (step < totalSteps) "Continue" else "Launch My Dashboard",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (step > 1) {
                    OutlinedButton(
                        onClick = { step-- },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Back", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.padding(end = 8.dp)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, fontSize = 12.sp, color = SlateGray)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = accent,
                checkedTrackColor = accent.copy(alpha = 0.45f)
            )
        )
    }
}

// ============================================================================
// LOGIN SCREEN
// ============================================================================
@Composable
fun LoginScreen(
    onLoginSuccess: (FirebaseUser) -> Unit
) {
    val auth = remember { FirebaseAuth.getInstance() }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 450.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppLogoIcon(size = 72.dp)

                Text(
                    text = "EXPNZ • PERSONAL WEALTH",
                    color = BrandCoralPink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )

                Text(
                    text = if (isSignUp) "Create Your Account" else "Welcome Back",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (isSignUp) "Sign up to customize your personal finance & wealth modules" else "Sign in to access your customized financial dashboard",
                    color = SlateGray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = null
                    },
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = BrandCoralPink)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = { Text("Password") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = BrandCoralPink)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = BrandCoralRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "Please enter both email and password."
                            return@Button
                        }
                        isLoading = true
                        errorMessage = null
                        if (isSignUp) {
                            auth.createUserWithEmailAndPassword(email.trim(), password)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        auth.currentUser?.let { onLoginSuccess(it) }
                                    } else {
                                        errorMessage = task.exception?.localizedMessage ?: "Sign up failed."
                                    }
                                }
                        } else {
                            auth.signInWithEmailAndPassword(email.trim(), password)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        auth.currentUser?.let { onLoginSuccess(it) }
                                    } else {
                                        errorMessage = task.exception?.localizedMessage ?: "Authentication failed."
                                    }
                                }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = BrandForestDark,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (isSignUp) "Sign Up" else "Sign In",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = SlateGray.copy(alpha = 0.3f))
                    Text(" OR ", color = SlateGray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 8.dp))
                    HorizontalDivider(modifier = Modifier.weight(1f), color = SlateGray.copy(alpha = 0.3f))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = {
                            isLoading = true
                            errorMessage = null
                            val activity = context.getActivity()
                            if (activity != null) {
                                val provider = com.google.firebase.auth.OAuthProvider.newBuilder("google.com")
                                auth.startActivityForSignInWithProvider(activity, provider.build())
                                    .addOnSuccessListener { authResult ->
                                        isLoading = false
                                        authResult.user?.let { onLoginSuccess(it) }
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        errorMessage = "Google Sign-In error: ${e.localizedMessage ?: e.toString()}"
                                    }
                            } else {
                                isLoading = false
                                errorMessage = "Activity not found"
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SlateGray.copy(alpha = 0.3f))
                    ) {
                        Text("Google", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    }
                    
                    OutlinedButton(
                        onClick = {
                            isLoading = true
                            errorMessage = null
                            auth.signInAnonymously()
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        auth.currentUser?.let { onLoginSuccess(it) }
                                    } else {
                                        errorMessage = task.exception?.localizedMessage ?: "Guest sign-in failed."
                                    }
                                }
                        },
                        enabled = !isLoading,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SlateGray.copy(alpha = 0.3f))
                    ) {
                        Text("Guest", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSignUp) "Already have an account?" else "Don't have an account?",
                        fontSize = 13.sp,
                        color = SlateGray
                    )
                    TextButton(
                        onClick = {
                            isSignUp = !isSignUp
                            errorMessage = null
                        }
                    ) {
                        Text(
                            text = if (isSignUp) "Sign In" else "Sign Up",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandCoralPink
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// BIOMETRIC HELPER FUNCTIONS & LOCK SCREEN
// ============================================================================
fun isBiometricAvailable(context: Context): Boolean {
    val biometricManager = BiometricManager.from(context)
    val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
    return biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
}

fun showBiometricPrompt(
    activity: FragmentActivity,
    title: String = "Biometric Authentication",
    subtitle: String = "Scan fingerprint or face to unlock",
    negativeButtonText: String = "Cancel",
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errString.toString())
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(title)
        .setSubtitle(subtitle)
        .setNegativeButtonText(negativeButtonText)
        .setAllowedAuthenticators(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
        )
        .build()

    biometricPrompt.authenticate(promptInfo)
}

@Composable
fun BiometricLockScreen(
    appSubtitle: String,
    onUnlockClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                AppLogoIcon(size = 72.dp)

                Text(
                    text = appSubtitle.uppercase(),
                    color = BrandCoralPink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )

                Text(
                    text = "App Locked",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Scan your fingerprint to access your financial data",
                    color = SlateGray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = onUnlockClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark)
                ) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Unlock with Fingerprint", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }

                OutlinedButton(
                    onClick = onSignOutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sign Out", color = BrandCoralRed, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

// ============================================================================
// SETTINGS SCREEN (WITH HOME LOANS & OFFSET, DYNAMIC MENU TOGGLES & PROFILE)
// ============================================================================
@Composable
fun SettingsScreen(
    appConfig: UserAppConfig,
    onUpdateAppConfig: (UserAppConfig) -> Unit,
    onOpenColorPicker: () -> Unit,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    isBiometricEnabled: Boolean,
    onBiometricChange: (Boolean) -> Unit,
    onRerunSetupWizard: () -> Unit,
    onSignOut: (() -> Unit)? = null,
    prop1Loan: LoanConfig = LoanConfig("Property 1", "2024-01-01", 600000.0, 30, 580000.0, 28.0, 6.15, 25000.0),
    prop2Loan: LoanConfig = LoanConfig("Property 2", "2024-01-01", 500000.0, 30, 480000.0, 28.0, 6.25, 10000.0),
    isWealthHidden: Boolean = false,
    onUpdateProp1Loan: (LoanConfig) -> Unit = {},
    onUpdateProp2Loan: (LoanConfig) -> Unit = {},
    householdInviteCode: String = "",
    isPartnerSession: Boolean = false,
    onJoinHousehold: (String, (Boolean, String) -> Unit) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val user = auth.currentUser
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showInvitePartnerDialog by remember { mutableStateOf(false) }
    var showJoinHouseholdDialog by remember { mutableStateOf(false) }
    var showLoansSubScreen by rememberSaveable { mutableStateOf(false) }

    val orbSpacing = 14.dp
    val headerTopPad = rememberHeaderAwareTopPadding(orbSpacing)

    fun maskVal(amt: Double, decimals: Int = 0): String =
        if (isWealthHidden) "$ ••••••" else formatCurrency(amt, decimals)

    if (showLoansSubScreen) {
        Box(modifier = Modifier.fillMaxSize()) {
            LoansAndOffsetScreen(
                appConfig = appConfig,
                prop1Loan = prop1Loan,
                prop2Loan = prop2Loan,
                isWealthHidden = isWealthHidden,
                onUpdateProp1 = onUpdateProp1Loan,
                onUpdateProp2 = onUpdateProp2Loan,
                topPadding = headerTopPad + 56.dp + orbSpacing
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = headerTopPad, start = 16.dp, end = 16.dp),
                shape = RoundedCornerShape(14.dp),
                color = BrandForestDark,
                border = BorderStroke(1.dp, BrandCoralPink.copy(alpha = 0.35f)),
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { showLoansSubScreen = false },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Settings",
                            tint = BrandCoralPink,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Back to Settings", color = BrandCoralPink, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                    }
                    Text(
                        "Home Loans & Offset",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
        return
    }

    val p1AutoFn = remember(prop1Loan.startDateStr) { getElapsedFortnightsSinceStart(prop1Loan.startDateStr) }
    val p1Sched = remember(prop1Loan) { generateAmortizationSchedule(prop1Loan, 780) }
    val p1Balance = (p1Sched.getOrNull(p1AutoFn.coerceIn(0, p1Sched.size - 1)) ?: p1Sched.first()).closingBalance

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = headerTopPad, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(orbSpacing)
    ) {
        // SECTION 0: HOME LOANS & OFFSET AMORTIZATION (MOVED FROM FOOTER TO SETTINGS)
        item {
            Card(
                onClick = { showLoansSubScreen = true },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, BrandCoralPink.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BrandCoralPink.copy(alpha = 0.18f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Icon(
                                    Icons.Default.QueryStats,
                                    contentDescription = null,
                                    tint = BrandCoralPink,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Column {
                                Text(
                                    "Home Loans & Offset",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "Amortization schedule, interest rates & offset simulator",
                                    fontSize = 12.sp,
                                    color = SlateGray
                                )
                            }
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Open Home Loans & Offset",
                            tint = BrandCoralPink
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(prop1Loan.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateGray)
                                Text(maskVal(p1Balance, 0), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Interest Rate", fontSize = 11.sp, color = SlateGray)
                                Text("${String.format(Locale.getDefault(), "%.2f", prop1Loan.interestRatePa)}% p.a.", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandCoralPink)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Offset Balance", fontSize = 11.sp, color = SlateGray)
                                Text(maskVal(prop1Loan.offsetBalance, 0), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandMintGreen)
                            }
                        }
                    }

                    Button(
                        onClick = { showLoansSubScreen = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark)
                    ) {
                        Icon(Icons.Default.QueryStats, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Open Home Loans & Offset Tracker", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }

        // SECTION 1: MODULE & MENU TOGGLES
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Active App Features & Menus",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = BrandCoralPink
                    )
                    Text(
                        "Toggle menus on or off anytime — your navigation bar and wealth calculations update automatically.",
                        fontSize = 12.sp,
                        color = SlateGray
                    )

                    HorizontalDivider()

                    FeatureToggleRow(
                        title = "Total Wealth Dashboard",
                        subtitle = "Front screen net worth & 3D asset breakdown",
                        icon = Icons.Default.PieChart,
                        accent = BrandCoralPink,
                        checked = appConfig.showTotalWealth,
                        onCheckedChange = { onUpdateAppConfig(appConfig.copy(showTotalWealth = it)) }
                    )

                    FeatureToggleRow(
                        title = "Personal Finances & Cash Flow",
                        subtitle = "Weekly budgets, take-home income & expenses",
                        icon = Icons.Default.AccountBalanceWallet,
                        accent = BrandMintGreen,
                        checked = appConfig.showCashFlow,
                        onCheckedChange = { onUpdateAppConfig(appConfig.copy(showCashFlow = it)) }
                    )

                    FeatureToggleRow(
                        title = "Include Home Loans & Offset in Wealth",
                        subtitle = "Include mortgage balances & offsets in net worth calculations",
                        icon = Icons.Default.QueryStats,
                        accent = BrandCoralLight,
                        checked = appConfig.showLoans,
                        onCheckedChange = { onUpdateAppConfig(appConfig.copy(showLoans = it)) }
                    )

                    FeatureToggleRow(
                        title = "Properties & Real Estate",
                        subtitle = "Property valuations, equity & property expenses",
                        icon = Icons.Default.HomeWork,
                        accent = BrandWarmAmber,
                        checked = appConfig.showProperties,
                        onCheckedChange = { onUpdateAppConfig(appConfig.copy(showProperties = it)) }
                    )

                    FeatureToggleRow(
                        title = "Shares & Superannuation",
                        subtitle = "ASX stock portfolio & retirement Super funds",
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        accent = BrandSkyBlue,
                        checked = appConfig.showSuperAndShares,
                        onCheckedChange = { onUpdateAppConfig(appConfig.copy(showSuperAndShares = it)) }
                    )

                    FeatureToggleRow(
                        title = "Other Assets",
                        subtitle = "Cars, savings accounts & personal assets",
                        icon = Icons.Default.DirectionsCar,
                        accent = BrandPurple,
                        checked = appConfig.showOtherAssets,
                        onCheckedChange = { onUpdateAppConfig(appConfig.copy(showOtherAssets = it)) }
                    )

                    FeatureToggleRow(
                        title = "Scenario Simulator (What-If)",
                        subtitle = "Australian wealth modeling, RBA rate levers & custom milestones",
                        icon = Icons.Default.Science,
                        accent = Color(appConfig.colorWhatIfHex),
                        checked = appConfig.showWhatIf,
                        onCheckedChange = { onUpdateAppConfig(appConfig.copy(showWhatIf = it)) }
                    )
                }
            }
        }

        // SECTION 2: PROFILE, COLOURS & HOUSEHOLD CONFIGURATION
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Profile, Names & Properties", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                if (appConfig.isCoupleMode) "${appConfig.person1Name} & ${appConfig.person2Name} (Couple Mode)"
                                else "${appConfig.person1Name} (Individual Mode)",
                                fontSize = 12.sp,
                                color = SlateGray
                            )
                        }
                        OutlinedButton(
                            onClick = { showEditProfileDialog = true },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Edit Info")
                        }
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(Color(appConfig.person1ColorHex)))
                            if (appConfig.isCoupleMode) {
                                Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(Color(appConfig.person2ColorHex)))
                            }
                            Column {
                                Text("Personal & Partner Colours", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Customize pill & badge colours inside menus", fontSize = 11.sp, color = SlateGray)
                            }
                        }
                        TextButton(onClick = onOpenColorPicker) {
                            Text("Pick Colours", color = BrandCoralPink, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Setup Wizard", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("Re-run the initial onboarding wizard", fontSize = 12.sp, color = SlateGray)
                        }
                        TextButton(onClick = onRerunSetupWizard) {
                            Text("Run Setup Wizard", color = BrandCoralPink, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // SECTION 2B: INVITE PARTNER TO SHARE APP (SHARED COUPLE ACCOUNT & REAL-TIME SYNC)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, BrandMintGreen.copy(alpha = 0.45f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BrandMintGreen.copy(alpha = 0.18f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Icon(
                                    Icons.Default.GroupAdd,
                                    contentDescription = null,
                                    tint = BrandMintGreen,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Column {
                                Text(
                                    "Invite Partner to Share App",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    when {
                                        isPartnerSession -> "Logged in as ${appConfig.person2Name} • Shared with ${appConfig.person1Name}"
                                        appConfig.partnerEmail.isNotBlank() -> "Invited: ${appConfig.person2Name} (${appConfig.partnerEmail})"
                                        appConfig.isCoupleMode -> "Share live read/write access with ${appConfig.person2Name}"
                                        else -> "Enable Couple Mode & invite your partner to share live data"
                                    },
                                    fontSize = 12.sp,
                                    color = SlateGray
                                )
                            }
                        }
                        if (isPartnerSession || appConfig.partnerEmail.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BrandMintGreen.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, BrandMintGreen)
                            ) {
                                Text(
                                    if (isPartnerSession) "LINKED" else "SHARED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrandMintGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    if (householdInviteCode.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Household Invite Code", fontSize = 11.sp, color = SlateGray, fontWeight = FontWeight.Bold)
                                    Text(householdInviteCode, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = BrandMintGreen, letterSpacing = 1.5.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Sync Mode", fontSize = 11.sp, color = SlateGray)
                                    Text("Real-Time Read & Write", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandCoralPink)
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showInvitePartnerDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandMintGreen, contentColor = BrandForestDark)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                if (appConfig.partnerEmail.isNotBlank()) "Manage Invite" else "Invite Partner",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                        OutlinedButton(
                            onClick = { showJoinHouseholdDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Link, contentDescription = null, tint = BrandCoralPink, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Link Partner Code",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = BrandCoralPink,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // SECTION 3: APPEARANCE & BIOMETRICS
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Dark Emerald Theme", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Toggle logo deep forest dark theme", fontSize = 12.sp, color = SlateGray)
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = onDarkModeChange,
                        colors = SwitchDefaults.colors(checkedThumbColor = BrandCoralPink, checkedTrackColor = BrandCoralPink.copy(alpha = 0.5f))
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Fingerprint / Face Unlock", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Require biometric scan on app launch", fontSize = 12.sp, color = SlateGray)
                    }
                    Switch(
                        checked = isBiometricEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled) {
                                if (isBiometricAvailable(context)) {
                                    val fragmentActivity = context as? FragmentActivity
                                    if (fragmentActivity != null) {
                                        showBiometricPrompt(
                                            activity = fragmentActivity,
                                            title = "Confirm Fingerprint",
                                            subtitle = "Scan fingerprint to enable biometric lock",
                                            onSuccess = {
                                                onBiometricChange(true)
                                                Toast.makeText(context, "Fingerprint unlock enabled", Toast.LENGTH_SHORT).show()
                                            },
                                            onError = { err ->
                                                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    } else {
                                        onBiometricChange(true)
                                    }
                                } else {
                                    Toast.makeText(context, "Biometric unlock is not set up or available on this device.", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                onBiometricChange(false)
                                Toast.makeText(context, "Fingerprint unlock disabled", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = BrandMintGreen, checkedTrackColor = BrandMintGreen.copy(alpha = 0.5f))
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Account", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            text = if (user?.email != null) "Signed in as ${user.email}" else "Signed in as Guest",
                            fontSize = 12.sp,
                            color = SlateGray
                        )
                    }
                    Button(
                        onClick = { onSignOut?.invoke() },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandCoralRed, contentColor = BrandForestDark),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Sign Out", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showEditProfileDialog) {
        EditUserProfileDialog(
            config = appConfig,
            onDismiss = { showEditProfileDialog = false },
            onSave = { updated ->
                onUpdateAppConfig(updated)
                showEditProfileDialog = false
            }
        )
    }

    if (showInvitePartnerDialog) {
        var partnerNameInput by remember { mutableStateOf(appConfig.person2Name) }
        var partnerEmailInput by remember { mutableStateOf(appConfig.partnerEmail) }

        AlertDialog(
            onDismissRequest = { showInvitePartnerDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.GroupAdd, contentDescription = null, tint = BrandMintGreen)
                    Text("Invite Partner to Share App", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Enter your partner's name and email below. When your partner creates their own EXPNZ account (or signs in) using this email, they will automatically connect to your shared household with full real-time read & write access.",
                        fontSize = 12.sp,
                        color = SlateGray
                    )
                    OutlinedTextField(
                        value = partnerNameInput,
                        onValueChange = { partnerNameInput = it },
                        label = { Text("Partner's Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = partnerEmailInput,
                        onValueChange = { partnerEmailInput = it },
                        label = { Text("Partner's Email Address") },
                        placeholder = { Text("partner@example.com") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (householdInviteCode.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = BrandMintGreen.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, BrandMintGreen.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("Backup Household Invite Code: $householdInviteCode", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = BrandMintGreen)
                                Text("Your partner can also enter this code under Settings > Link Partner Code.", fontSize = 11.sp, color = SlateGray)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cleanEmail = partnerEmailInput.trim().lowercase()
                        val cleanName = partnerNameInput.trim().ifBlank { "Partner" }
                        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
                            Toast.makeText(context, "Please enter a valid partner email address", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        onUpdateAppConfig(
                            appConfig.copy(
                                isCoupleMode = true,
                                person2Name = cleanName,
                                partnerEmail = cleanEmail
                            )
                        )
                        showInvitePartnerDialog = false

                        val shareText = buildString {
                            append("Hi $cleanName! I've invited you to share our household wealth & budget tracker on EXPNZ.\n\n")
                            append("1. Open EXPNZ and Sign Up (or Sign In) with your email: $cleanEmail\n")
                            append("2. Your account will automatically sync with our shared household data (or use Invite Code: $householdInviteCode in Settings).")
                        }
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Join our shared EXPNZ Household")
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Send Partner Invite"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandMintGreen, contentColor = BrandForestDark)
                ) {
                    Text("Save & Share Invite", fontWeight = FontWeight.ExtraBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showInvitePartnerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showJoinHouseholdDialog) {
        var codeOrEmailInput by remember { mutableStateOf("") }
        var joinStatusMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showJoinHouseholdDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Link, contentDescription = null, tint = BrandCoralPink)
                    Text("Link to Partner's Household", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Enter the 6-character Household Invite Code from your partner's Settings (or enter your invited email address) to access and edit the same live wealth dataset.",
                        fontSize = 12.sp,
                        color = SlateGray
                    )
                    OutlinedTextField(
                        value = codeOrEmailInput,
                        onValueChange = {
                            codeOrEmailInput = it
                            joinStatusMessage = null
                        },
                        label = { Text("6-Digit Invite Code or Invited Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    joinStatusMessage?.let { msg ->
                        Text(msg, fontSize = 12.sp, color = BrandCoralPink, fontWeight = FontWeight.SemiBold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onJoinHousehold(codeOrEmailInput) { success, msg ->
                            if (success) {
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                showJoinHouseholdDialog = false
                            } else {
                                joinStatusMessage = msg
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark)
                ) {
                    Text("Link Household", fontWeight = FontWeight.ExtraBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showJoinHouseholdDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EditUserProfileDialog(
    config: UserAppConfig,
    onDismiss: () -> Unit,
    onSave: (UserAppConfig) -> Unit
) {
    var isCouple by remember { mutableStateOf(config.isCoupleMode) }
    var p1Name by remember { mutableStateOf(config.person1Name) }
    var p2Name by remember { mutableStateOf(config.person2Name) }
    var prop1Name by remember { mutableStateOf(config.property1Name) }
    var isProp1Rented by remember { mutableStateOf(config.isProperty1Rented) }
    var hasProp2 by remember { mutableStateOf(config.hasSecondProperty) }
    var prop2Name by remember { mutableStateOf(config.property2Name) }
    var isProp2Rented by remember { mutableStateOf(config.isProperty2Rented) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile & Labels", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !isCouple,
                        onClick = { isCouple = false },
                        label = { Text("Solo Mode") }
                    )
                    FilterChip(
                        selected = isCouple,
                        onClick = { isCouple = true },
                        label = { Text("Couple Mode") }
                    )
                }
                OutlinedTextField(
                    value = p1Name,
                    onValueChange = { p1Name = it },
                    label = { Text("Primary User Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (isCouple) {
                    OutlinedTextField(
                        value = p2Name,
                        onValueChange = { p2Name = it },
                        label = { Text("Partner Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                OutlinedTextField(
                    value = prop1Name,
                    onValueChange = { prop1Name = it },
                    label = { Text("Property 1 Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Is Property 1 Rented?", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Switch(checked = isProp1Rented, onCheckedChange = { isProp1Rented = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Track 2nd Property?", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Switch(checked = hasProp2, onCheckedChange = { hasProp2 = it })
                }
                if (hasProp2) {
                    OutlinedTextField(
                        value = prop2Name,
                        onValueChange = { prop2Name = it },
                        label = { Text("Property 2 Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Is Property 2 Rented / Investment?", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Switch(checked = isProp2Rented, onCheckedChange = { isProp2Rented = it })
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        config.copy(
                            isCoupleMode = isCouple,
                            person1Name = p1Name.trim().ifBlank { "Me" },
                            person2Name = p2Name.trim().ifBlank { "Partner" },
                            property1Name = prop1Name.trim().ifBlank { "Primary Home" },
                            isProperty1Rented = isProp1Rented,
                            hasSecondProperty = hasProp2,
                            property2Name = prop2Name.trim().ifBlank { "Investment Property" },
                            isProperty2Rented = isProp2Rented
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark)
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ============================================================================
// TAB 1: MONEY PLAN / PERSONAL CASH FLOW (WITH CUSTOM USER COLORS & IN/OUT ARROWS)
// ============================================================================
@Composable
fun MoneyPlanScreen(
    appConfig: UserAppConfig,
    person1Color: Color,
    person2Color: Color,

    person1Income: Double,
    person2Income: Double,
    onUpdateIncome: (Boolean, Double) -> Unit,
    perPersonPropertyShare: Double,
    person1Items: List<CashflowItem>,
    person2Items: List<CashflowItem>,
    isWealthHidden: Boolean = false,
    isPartnerLoggedIn: Boolean = false,
    onAddPerson1: (String, Double, String) -> Unit,
    onEditPerson1: (String, String, Double, String) -> Unit,
    onDeletePerson1: (String) -> Unit,
    onAddPerson2: (String, Double, String) -> Unit,
    onEditPerson2: (String, String, Double, String) -> Unit,
    onDeletePerson2: (String) -> Unit
) {
    var selectedPerson by rememberSaveable(isPartnerLoggedIn) {
        mutableIntStateOf(if (appConfig.isCoupleMode && isPartnerLoggedIn) 1 else 0)
    }
    LaunchedEffect(appConfig.isCoupleMode, isPartnerLoggedIn) {
        if (!appConfig.isCoupleMode) {
            selectedPerson = 0
        } else if (isPartnerLoggedIn) {
            selectedPerson = 1
        }
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<CashflowItem?>(null) }
    var showIncomeDialog by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf("Default") }

    fun maskVal(amt: Double, decimals: Int = 2): String =
        if (isWealthHidden) "$ ••••••" else formatCurrency(amt, decimals)

    val p1TotalExpenses = perPersonPropertyShare + person1Items.sumOf { it.weeklyAmount }
    val p1Surplus = person1Income + p1TotalExpenses

    val p2TotalExpenses = if (appConfig.isCoupleMode) (perPersonPropertyShare + person2Items.sumOf { it.weeklyAmount }) else 0.0
    val p2Surplus = if (appConfig.isCoupleMode) (person2Income + p2TotalExpenses) else 0.0

    val combinedSurplus = p1Surplus + p2Surplus
    val combinedIncome = person1Income + (if (appConfig.isCoupleMode) person2Income else 0.0)

    val orbSpacing = 14.dp
    val headerTopPad = rememberHeaderAwareTopPadding(orbSpacing)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = headerTopPad, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(orbSpacing)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                if (appConfig.isCoupleMode) "COMBINED WEEKLY SURPLUS" else "WEEKLY NET SURPLUS",
                                color = BrandCoralPink,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(Modifier.height(4.dp))
                            CashflowDirectionAmount(
                                amount = combinedSurplus,
                                suffix = "/wk",
                                decimals = 2,
                                isWealthHidden = isWealthHidden,
                                fontSize = 22
                            )
                        }
                        Surface(
                            color = Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.NorthEast, contentDescription = "Income In", tint = BrandMintGreen, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "In: ${maskVal(combinedIncome)}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (appConfig.isCoupleMode && isPartnerLoggedIn) {
                            PersonSummaryPill(
                                title = appConfig.person2Name,
                                income = person2Income,
                                surplus = p2Surplus,
                                accent = person2Color,
                                isSelected = selectedPerson == 1,
                                isWealthHidden = isWealthHidden,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedPerson = 1 }
                            )
                            PersonSummaryPill(
                                title = appConfig.person1Name,
                                income = person1Income,
                                surplus = p1Surplus,
                                accent = person1Color,
                                isSelected = selectedPerson == 0,
                                isWealthHidden = isWealthHidden,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedPerson = 0 }
                            )
                        } else {
                            PersonSummaryPill(
                                title = appConfig.person1Name,
                                income = person1Income,
                                surplus = p1Surplus,
                                accent = person1Color,
                                isSelected = selectedPerson == 0,
                                isWealthHidden = isWealthHidden,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedPerson = 0 }
                            )
                            if (appConfig.isCoupleMode) {
                                PersonSummaryPill(
                                    title = appConfig.person2Name,
                                    income = person2Income,
                                    surplus = p2Surplus,
                                    accent = person2Color,
                                    isSelected = selectedPerson == 1,
                                    isWealthHidden = isWealthHidden,
                                    modifier = Modifier.weight(1f),
                                    onClick = { selectedPerson = 1 }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            val activeName = if (selectedPerson == 0) appConfig.person1Name else appConfig.person2Name
            val activeColor = if (selectedPerson == 0) person1Color else person2Color

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = activeColor, contentColor = BrandForestDark),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add to $activeName", fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                OutlinedButton(
                    onClick = { showIncomeDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = activeColor)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit Income", color = activeColor, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }

        item {
            val activeName = if (selectedPerson == 0) appConfig.person1Name else appConfig.person2Name
            val activeExpenses = if (selectedPerson == 0) p1TotalExpenses else p2TotalExpenses
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$activeName's Weekly Breakdown", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                    CashflowDirectionAmount(
                        amount = activeExpenses,
                        suffix = "/wk",
                        decimals = 2,
                        isWealthHidden = isWealthHidden,
                        fontSize = 13
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Sort By:", fontSize = 11.sp, color = SlateGray, fontWeight = FontWeight.Bold)
                    listOf("Default", "Highest $", "Lowest $", "A-Z").forEach { option ->
                        FilterChip(
                            selected = sortBy == option,
                            onClick = { sortBy = option },
                            label = { Text(option, fontSize = 10.sp) }
                        )
                    }
                }
            }
        }

        val showHousingCostInCashflow = perPersonPropertyShare != 0.0
        if (showHousingCostInCashflow) {
            item {
                val shareLabel = if (perPersonPropertyShare >= 0) {
                    if (appConfig.isCoupleMode) "Property Net Income Share (50% Split)" else "Net Property Cashflow Income"
                } else {
                    if (appConfig.isProperty1Rented) {
                        if (appConfig.isCoupleMode) "Rent & Housing Share (50% Split)" else "Weekly Rent & Housing Cost"
                    } else {
                        if (appConfig.isCoupleMode) "Property Share (50% Net Property Cost)" else "Net Property Housing Cost"
                    }
                }
                ExpenseItemCard(
                    item = CashflowItem(
                        id = "locked_prop",
                        name = shareLabel,
                        weeklyAmount = perPersonPropertyShare,
                        isLockedMortgageOrPropertyLink = true,
                        iconName = "Home"
                    ),
                    accentColor = if (perPersonPropertyShare >= 0) BrandMintGreen else (if (appConfig.isProperty1Rented) BrandCoralPink else BrandWarmAmber),
                    isWealthHidden = isWealthHidden,
                    onDelete = null
                )
            }
        }

        val rawActiveList = if (selectedPerson == 0) person1Items else person2Items
        val activeList = when (sortBy) {
            "Highest $" -> rawActiveList.sortedBy { it.weeklyAmount }
            "Lowest $" -> rawActiveList.sortedByDescending { it.weeklyAmount }
            "A-Z" -> rawActiveList.sortedBy { it.name.lowercase() }
            else -> rawActiveList
        }
        val activeAccent = if (selectedPerson == 0) person1Color else person2Color

        items(activeList, key = { it.id }) { item ->
            ExpenseItemCard(
                item = item,
                accentColor = activeAccent,
                isWealthHidden = isWealthHidden,
                onEdit = { itemToEdit = item },
                onDelete = {
                    if (selectedPerson == 0) onDeletePerson1(item.id) else onDeletePerson2(item.id)
                }
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    if (showAddDialog || itemToEdit != null) {
        val activeName = if (selectedPerson == 0) appConfig.person1Name else appConfig.person2Name
        val isEdit = itemToEdit != null
        AddCashflowDialog(
            targetTitle = if (isEdit) "Edit ${itemToEdit?.name}" else "$activeName's Plan",
            initialItem = itemToEdit,
            defaultIsExpense = true,
            onDismiss = { showAddDialog = false; itemToEdit = null },
            onConfirm = { name, amount, iconName ->
                if (isEdit) {
                    if (selectedPerson == 0) onEditPerson1(itemToEdit!!.id, name, amount, iconName)
                    else onEditPerson2(itemToEdit!!.id, name, amount, iconName)
                } else {
                    if (selectedPerson == 0) onAddPerson1(name, amount, iconName)
                    else onAddPerson2(name, amount, iconName)
                }
                showAddDialog = false
                itemToEdit = null
            }
        )
    }

    if (showIncomeDialog) {
        val isPerson1 = selectedPerson == 0
        val activeName = if (isPerson1) appConfig.person1Name else appConfig.person2Name
        EditSingleValueDialog(
            title = "Update $activeName's Weekly Income",
            label = "Weekly Take-Home Income ($)",
            initialValue = if (isPerson1) person1Income else person2Income,
            onDismiss = { showIncomeDialog = false },
            onConfirm = { newVal ->
                onUpdateIncome(isPerson1, newVal)
                showIncomeDialog = false
            }
        )
    }
}

@Composable
fun PersonSummaryPill(
    title: String,
    income: Double,
    surplus: Double,
    accent: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    isWealthHidden: Boolean = false,
    onClick: () -> Unit
) {
    val isPositive = surplus >= 0
    val arrowIcon = if (isPositive) Icons.Default.NorthEast else Icons.Default.SouthEast

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = if (isSelected) accent else Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.5.dp, if (isSelected) Color.White else accent.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    color = if (isSelected) BrandForestDark else Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (isSelected) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandForestDark, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Icon(
                    Icons.Default.NorthEast,
                    contentDescription = "Income In",
                    tint = if (isSelected) BrandForestDark else BrandMintGreen,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    "In: ${if (isWealthHidden) "$ ••••••" else formatCurrency(income)}/wk",
                    color = if (isSelected) BrandForestDark.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                Icon(
                    arrowIcon,
                    contentDescription = if (isPositive) "Positive Surplus" else "Negative Deficit",
                    tint = if (isSelected) BrandForestDark else if (isPositive) BrandMintGreen else BrandCoralPink,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    "Net: ${if (isWealthHidden) "$ ••••••" else formatCurrency(surplus)}/wk",
                    color = if (isSelected) BrandForestDark else if (isWealthHidden) Color.White else if (isPositive) BrandMintGreen else BrandCoralPink,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

// ============================================================================
// TAB 2: PROPERTIES SCREEN (WITH IN/OUT ARROWS)
// ============================================================================
@Composable
fun PropertiesScreen(
    appConfig: UserAppConfig,
    prop1WeeklyMortgage: Double,
    prop1Items: List<CashflowItem>,
    onAddProp1: (String, Double, String) -> Unit,
    onEditProp1: (String, String, Double, String) -> Unit,
    onDeleteProp1: (String) -> Unit,
    prop2WeeklyMortgage: Double,
    prop2Items: List<CashflowItem>,
    onAddProp2: (String, Double, String) -> Unit,
    onEditProp2: (String, String, Double, String) -> Unit,
    onDeleteProp2: (String) -> Unit,
    prop1MarketValue: Double,
    prop2MarketValue: Double,
    prop1LoanBalance: Double,
    prop2LoanBalance: Double,
    isWealthHidden: Boolean = false,
    onUpdateAppConfig: (UserAppConfig) -> Unit
) {
    var selectedProperty by remember { mutableIntStateOf(0) }
    LaunchedEffect(appConfig.hasSecondProperty) {
        if (!appConfig.hasSecondProperty) selectedProperty = 0
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<CashflowItem?>(null) }
    var showAddressEditDialog by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf("Default") }

    fun maskVal(amt: Double, decimals: Int = 0): String = if (isWealthHidden) "$ ••••••" else formatCurrency(amt, decimals)



    val prop1HousingCost = if (appConfig.isProperty1Rented) -appConfig.primaryRentWeekly else prop1WeeklyMortgage
    val prop1Net = prop1HousingCost + prop1Items.sumOf { it.weeklyAmount }

    val prop2RentIncome = if (appConfig.hasSecondProperty) appConfig.secondaryRentWeekly else 0.0
    val prop2Mortgage = if (appConfig.hasSecondProperty) prop2WeeklyMortgage else 0.0
    val prop2Expenses = if (appConfig.hasSecondProperty) prop2Items.sumOf { it.weeklyAmount } else 0.0
    val prop2Net = prop2RentIncome + prop2Mortgage + prop2Expenses

    val orbSpacing = 14.dp
    val headerTopPad = rememberHeaderAwareTopPadding(orbSpacing)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = headerTopPad, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(orbSpacing)
    ) {
        if (appConfig.hasSecondProperty) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PropertySelectCard(
                        title = appConfig.property1Name,
                        subtitle = "Primary Residence",
                        netWeekly = prop1Net,
                        accent = BrandCoralPink,
                        isSelected = selectedProperty == 0,
                        isWealthHidden = isWealthHidden,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedProperty = 0 }
                    )
                    PropertySelectCard(
                        title = appConfig.property2Name,
                        subtitle = "Second / Investment",
                        netWeekly = prop2Net,
                        accent = BrandMintGreen,
                        isSelected = selectedProperty == 1,
                        isWealthHidden = isWealthHidden,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedProperty = 1 }
                    )
                }
            }
        }

        item {
            val isProp1 = selectedProperty == 0
            val activePropName = if (isProp1) appConfig.property1Name else appConfig.property2Name
            val estVal = if (isProp1) prop1MarketValue else prop2MarketValue
            val liveBal = if (isProp1) prop1LoanBalance else prop2LoanBalance
            val netEquity = estVal - liveBal

            var showRentEditDialog by remember { mutableStateOf(false) }
            var showMgmtFeeDialog by remember { mutableStateOf(false) }

            if (isProp1) {
                val isRented = appConfig.isProperty1Rented
                if (isRented) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(activePropName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                        IconButton(onClick = { showAddressEditDialog = true }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit Address", modifier = Modifier.size(14.dp), tint = SlateGray)
                                        }
                                    }
                                    Text("Rental Summary (Primary Home)", fontSize = 12.sp, color = SlateGray)
                                }
                                IconButton(onClick = { showRentEditDialog = true }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Rent", modifier = Modifier.size(16.dp), tint = BrandCoralPink)
                                }
                            }

                            val currentRent = appConfig.primaryRentWeekly
                            LoanDetailRow("Weekly Gross Rent", maskVal(currentRent, 2), highlightColor = BrandMintGreen)

                            FilledTonalButton(
                                onClick = { showMgmtFeeDialog = true },
                                modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = BrandCoralPink,
                                    contentColor = BrandForestDark
                                )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Add Property Management Fee", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(activePropName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                        IconButton(onClick = { showAddressEditDialog = true }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit Address", modifier = Modifier.size(14.dp), tint = SlateGray)
                                        }
                                    }
                                    Text("Valuation & Equity", fontSize = 12.sp, color = SlateGray)
                                }
                            }

                            LoanDetailRow("Est. Market Value", maskVal(estVal, 0), highlightColor = BrandMintGreen)
                            if (appConfig.showLoans) {
                                LoanDetailRow("Synced Mortgage Balance", maskVal(liveBal, 0), highlightColor = BrandCoralPink)
                            }
                            LoanDetailRow("Calculated Net Equity", maskVal(netEquity, 0), highlightColor = BrandWarmAmber)
                        }
                    }
                }
            } else {
                // Property 2: Investment Property — Show BOTH Valuation & Equity AND Rental Summary together in one unified view!
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(activePropName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                        IconButton(onClick = { showAddressEditDialog = true }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit Address", modifier = Modifier.size(14.dp), tint = SlateGray)
                                        }
                                    }
                                    Text("Valuation & Equity", fontSize = 12.sp, color = SlateGray)
                                }
                            }

                            LoanDetailRow("Est. Market Value", maskVal(estVal, 0), highlightColor = BrandMintGreen)
                            if (appConfig.showLoans) {
                                LoanDetailRow("Synced Mortgage Balance", maskVal(liveBal, 0), highlightColor = BrandCoralPink)
                            }
                            LoanDetailRow("Calculated Net Equity", maskVal(netEquity, 0), highlightColor = BrandWarmAmber)
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Investment Rental Summary", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Text("Rental Income & Management", fontSize = 12.sp, color = SlateGray)
                                }
                                IconButton(onClick = { showRentEditDialog = true }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Rent", modifier = Modifier.size(16.dp), tint = BrandCoralPink)
                                }
                            }

                            val currentRent = appConfig.secondaryRentWeekly
                            LoanDetailRow("Weekly Gross Rent", maskVal(currentRent, 2), highlightColor = BrandMintGreen)

                            FilledTonalButton(
                                onClick = { showMgmtFeeDialog = true },
                                modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = BrandCoralPink,
                                    contentColor = BrandForestDark
                                )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Add Property Management Fee", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }

            if (showRentEditDialog) {
                val currentRent = if (isProp1) appConfig.primaryRentWeekly else appConfig.secondaryRentWeekly
                EditSingleValueDialog(
                    title = "Update Weekly Rent",
                    label = "Weekly Rent ($)",
                    initialValue = currentRent,
                    onDismiss = { showRentEditDialog = false },
                    onConfirm = { newRent ->
                        if (isProp1) {
                            onUpdateAppConfig(appConfig.copy(primaryRentWeekly = newRent))
                        } else {
                            onUpdateAppConfig(appConfig.copy(secondaryRentWeekly = newRent))
                        }
                        showRentEditDialog = false
                    }
                )
            }

            if (showMgmtFeeDialog) {
                val currentRent = if (isProp1) appConfig.primaryRentWeekly else appConfig.secondaryRentWeekly
                ManagementFeePercentageDialog(
                    weeklyRent = currentRent,
                    onDismiss = { showMgmtFeeDialog = false },
                    onConfirm = { name, amount ->
                        if (isProp1) {
                            onAddProp1(name, amount, "Utility")
                        } else {
                            onAddProp2(name, amount, "Utility")
                        }
                        showMgmtFeeDialog = false
                    }
                )
            }

            if (showAddressEditDialog) {
                EditAddressDialog(
                    initialAddress = activePropName,
                    onDismiss = { showAddressEditDialog = false },
                    onConfirm = { newAddress ->
                        if (isProp1) onUpdateAppConfig(appConfig.copy(property1Name = newAddress))
                        else onUpdateAppConfig(appConfig.copy(property2Name = newAddress))
                        showAddressEditDialog = false
                    }
                )
            }
        }

        item {
            val propName = if (selectedProperty == 0) appConfig.property1Name else appConfig.property2Name
            val btnColor = if (selectedProperty == 0) BrandCoralPink else BrandMintGreen
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = btnColor, contentColor = BrandForestDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Transaction to $propName", fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }

        item {
            val propName = if (selectedProperty == 0) appConfig.property1Name else appConfig.property2Name
            val netVal = if (selectedProperty == 0) prop1Net else prop2Net
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$propName Cashflow", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                    CashflowDirectionAmount(
                        amount = netVal,
                        suffix = "/wk",
                        decimals = 2,
                        isWealthHidden = isWealthHidden,
                        fontSize = 14
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Sort By:", fontSize = 11.sp, color = SlateGray, fontWeight = FontWeight.Bold)
                    listOf("Default", "Highest $", "Lowest $", "A-Z").forEach { option ->
                        FilterChip(
                            selected = sortBy == option,
                            onClick = { sortBy = option },
                            label = { Text(option, fontSize = 10.sp) }
                        )
                    }
                }
            }
        }

        item {
            val isProp1 = selectedProperty == 0
            val isRented = if (isProp1) appConfig.isProperty1Rented else (appConfig.isProperty2Rented || appConfig.secondaryRentWeekly > 0.0)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (isRented) {
                    var showRentEditDialog by remember { mutableStateOf(false) }
                    val currentRentAmt = if (isProp1) -appConfig.primaryRentWeekly else appConfig.secondaryRentWeekly
                    ExpenseItemCard(
                        item = CashflowItem(
                            id = "rent_sync",
                            name = "Weekly Gross Rent",
                            weeklyAmount = currentRentAmt,
                            isLockedMortgageOrPropertyLink = true,
                            iconName = "Home"
                        ),
                        accentColor = if (currentRentAmt >= 0) BrandMintGreen else BrandCoralPink,
                        isWealthHidden = isWealthHidden,
                        onEdit = { showRentEditDialog = true },
                        onDelete = null
                    )
                    if (showRentEditDialog) {
                        val editRentBase = if (isProp1) appConfig.primaryRentWeekly else appConfig.secondaryRentWeekly
                        EditSingleValueDialog(
                            title = "Update Weekly Rent Amount",
                            label = "Weekly Rent ($)",
                            initialValue = editRentBase,
                            onDismiss = { showRentEditDialog = false },
                            onConfirm = { newRent ->
                                if (isProp1) {
                                    onUpdateAppConfig(appConfig.copy(primaryRentWeekly = newRent))
                                } else {
                                    onUpdateAppConfig(appConfig.copy(secondaryRentWeekly = newRent))
                                }
                                showRentEditDialog = false
                            }
                        )
                    }
                }
                if (appConfig.showLoans) {
                    val weeklyMort = if (selectedProperty == 0) prop1WeeklyMortgage else prop2WeeklyMortgage
                    ExpenseItemCard(
                        item = CashflowItem(
                            id = "mort_sync",
                            name = "Mortgage Payment (Auto-Synced from Loan Tab)",
                            weeklyAmount = weeklyMort,
                            isLockedMortgageOrPropertyLink = true,
                            iconName = "Home"
                        ),
                        accentColor = BrandCoralPink,
                        isWealthHidden = isWealthHidden,
                        onDelete = null
                    )
                }
            }
        }

        val rawActiveList = if (selectedProperty == 0) prop1Items else prop2Items
        val activeList = when (sortBy) {
            "Highest $" -> rawActiveList.sortedBy { it.weeklyAmount }
            "Lowest $" -> rawActiveList.sortedByDescending { it.weeklyAmount }
            "A-Z" -> rawActiveList.sortedBy { it.name.lowercase() }
            else -> rawActiveList
        }
        val activeAccent = if (selectedProperty == 0) BrandCoralPink else BrandMintGreen

        items(activeList, key = { it.id }) { item ->
            ExpenseItemCard(
                item = item,
                accentColor = if (item.weeklyAmount >= 0) BrandMintGreen else activeAccent,
                isWealthHidden = isWealthHidden,
                onEdit = { itemToEdit = item },
                onDelete = {
                    if (selectedProperty == 0) onDeleteProp1(item.id) else onDeleteProp2(item.id)
                }
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    if (showAddDialog || itemToEdit != null) {
        val propName = if (selectedProperty == 0) appConfig.property1Name else appConfig.property2Name
        val isEdit = itemToEdit != null
        AddCashflowDialog(
            targetTitle = if (isEdit) "Edit ${itemToEdit?.name}" else propName,
            initialItem = itemToEdit,
            defaultIsExpense = true,
            onDismiss = { showAddDialog = false; itemToEdit = null },
            onConfirm = { name, amount, iconName ->
                if (isEdit) {
                    if (selectedProperty == 0) onEditProp1(itemToEdit!!.id, name, amount, iconName)
                    else onEditProp2(itemToEdit!!.id, name, amount, iconName)
                } else {
                    if (selectedProperty == 0) onAddProp1(name, amount, iconName)
                    else onAddProp2(name, amount, iconName)
                }
                showAddDialog = false
                itemToEdit = null
            }
        )
    }
}

@Composable
fun PropertySelectCard(
    title: String,
    subtitle: String,
    netWeekly: Double,
    accent: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    isWealthHidden: Boolean = false,
    onClick: () -> Unit
) {
    val isPositive = netWeekly >= 0
    val arrowIcon = if (isPositive) Icons.Default.NorthEast else Icons.Default.SouthEast

    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) accent else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                title,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isSelected) BrandForestDark else MaterialTheme.colorScheme.onSurface
            )
            Text(
                subtitle,
                fontSize = 11.sp,
                color = if (isSelected) BrandForestDark.copy(alpha = 0.8f) else SlateGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(
                    imageVector = arrowIcon,
                    contentDescription = if (isPositive) "Net Inflow" else "Net Outflow",
                    tint = if (isSelected) BrandForestDark else if (isPositive) BrandMintGreen else BrandCoralPink,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = if (isWealthHidden) "$ ••••••/wk" else "${formatCurrency(netWeekly)}/wk",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = if (isSelected) BrandForestDark else if (isWealthHidden) MaterialTheme.colorScheme.onSurface else if (isPositive) BrandMintGreen else BrandCoralPink
                )
            }
        }
    }
}

// ============================================================================
// TAB 3: LOANS, DYNAMIC INTEREST RATES & FORTNIGHTLY AMORTIZATION TRACKER
// ============================================================================
@Composable
fun LoansAndOffsetScreen(
    appConfig: UserAppConfig,
    prop1Loan: LoanConfig,
    prop2Loan: LoanConfig,
    isWealthHidden: Boolean = false,
    onUpdateProp1: (LoanConfig) -> Unit,
    onUpdateProp2: (LoanConfig) -> Unit,
    topPadding: Dp = 115.dp
) {
    var selectedLoanIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(appConfig.hasSecondProperty) {
        if (!appConfig.hasSecondProperty) selectedLoanIndex = 0
    }

    val activeLoan = if (selectedLoanIndex == 0) prop1Loan else prop2Loan
    val onUpdateActive: (LoanConfig) -> Unit = if (selectedLoanIndex == 0) onUpdateProp1 else onUpdateProp2

    fun maskVal(amt: Double, decimals: Int = 0): String = if (isWealthHidden) "$ ••••••" else formatCurrency(amt, decimals)

    val schedule = remember(activeLoan) { generateAmortizationSchedule(activeLoan, 780) }

    val autoElapsedFortnights = remember(activeLoan.startDateStr) {
        getElapsedFortnightsSinceStart(activeLoan.startDateStr)
    }
    var simulatedFortnight by remember(selectedLoanIndex, autoElapsedFortnights) {
        mutableIntStateOf(autoElapsedFortnights.coerceIn(0, max(0, schedule.size - 1)))
    }

    val currentRow = schedule.getOrNull(simulatedFortnight.coerceIn(0, schedule.size - 1)) ?: schedule.first()
    val nextRow = schedule.getOrNull((simulatedFortnight + 1).coerceAtMost(schedule.size - 1))

    val liveBalance = currentRow.closingBalance
    val netBalanceChargingInterest = max(0.0, liveBalance - activeLoan.offsetBalance)
    val effectiveRate = if (liveBalance > 0) activeLoan.interestRatePa * (netBalanceChargingInterest / liveBalance) else 0.0
    val annualOffsetSavings = activeLoan.offsetBalance * (activeLoan.interestRatePa / 100.0)
    val monthlyOffsetSavings = annualOffsetSavings / 12.0
    val fortnightlyOffsetSavings = annualOffsetSavings / 26.0
    val actualPayoffYearsLeft = ((schedule.size - 1 - simulatedFortnight).coerceAtLeast(0)) / 26.0
    val yearsSaved = (activeLoan.remainingTermYears - actualPayoffYearsLeft).coerceAtLeast(0.0)

    var showRateModal by remember { mutableStateOf(false) }
    var showFullSchedule by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = topPadding, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (appConfig.hasSecondProperty) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { selectedLoanIndex = 0 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedLoanIndex == 0) BrandCoralPink else MaterialTheme.colorScheme.surface,
                            contentColor = if (selectedLoanIndex == 0) BrandForestDark else MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("${appConfig.property1Name} Loan", fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Button(
                        onClick = { selectedLoanIndex = 1 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedLoanIndex == 1) BrandMintGreen else MaterialTheme.colorScheme.surface,
                            contentColor = if (selectedLoanIndex == 1) BrandForestDark else MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("${appConfig.property2Name} Loan", fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "${activeLoan.name.uppercase()} • FORTNIGHT #$simulatedFortnight",
                                color = BrandCoralPink,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                maskVal(liveBalance, 0),
                                color = Color.White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                "As of ${currentRow.paymentDate} (Next: ${nextRow?.paymentDate ?: "Paid Off"})",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                        FilledTonalButton(
                            onClick = { showRateModal = true },
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = BrandForestDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("${String.format(Locale.getDefault(), "%.2f", activeLoan.interestRatePa)}%", color = BrandForestDark, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.12f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Fortnight Progression", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(
                                if (simulatedFortnight == autoElapsedFortnights) "Synced to Today's Date"
                                else "Previewing Fortnight #$simulatedFortnight",
                                color = BrandMintGreen,
                                fontSize = 11.sp
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            FilledIconButton(
                                onClick = { if (simulatedFortnight > 0) simulatedFortnight-- },
                                modifier = Modifier.size(34.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White.copy(alpha = 0.15f))
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Prev Fortnight", tint = Color.White)
                            }
                            if (simulatedFortnight != autoElapsedFortnights) {
                                TextButton(onClick = { simulatedFortnight = autoElapsedFortnights }) {
                                    Text("Today", color = BrandCoralPink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            FilledIconButton(
                                onClick = { if (simulatedFortnight < schedule.size - 1) simulatedFortnight++ },
                                modifier = Modifier.size(34.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = BrandCoralPink)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Next Fortnight", tint = BrandForestDark)
                            }
                        }
                    }
                }
            }
        }

        item {
            val fnPmt = calculateEffectiveFortnightlyRepayment(activeLoan)
            val wkPmt = fnPmt / 2.0

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Loan Parameters & Live Offset Summary", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                        TextButton(onClick = { showRateModal = true }) {
                            Text("Edit Loan", color = BrandCoralPink, fontWeight = FontWeight.Bold)
                        }
                    }

                    LoanDetailRow("Original Loan Amount (30 Yrs)", maskVal(activeLoan.originalLoanAmount, 0))
                    LoanDetailRow("Start Balance (${activeLoan.startDateStr})", maskVal(activeLoan.startLoanBalance, 0))
                    LoanDetailRow("Current Remaining Balance", maskVal(liveBalance, 0), highlightColor = BrandCoralPink)
                    LoanDetailRow("Offset Account Balance", maskVal(activeLoan.offsetBalance, 0), highlightColor = BrandMintGreen)
                    LoanDetailRow("Net Balance Charging Interest", maskVal(netBalanceChargingInterest, 0))
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    LoanDetailRow("Standard Interest Rate", "${String.format(Locale.getDefault(), "%.2f", activeLoan.interestRatePa)}% p.a.")
                    LoanDetailRow("Effective Rate With Offset", "${String.format(Locale.getDefault(), "%.2f", effectiveRate)}% p.a.", highlightColor = BrandMintGreen)
                    LoanDetailRow("Calculated Fortnightly Repayment", maskVal(fnPmt, 0))
                    LoanDetailRow("Calculated Weekly Repayment", maskVal(wkPmt, 2))
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    LoanDetailRow("Interest Saved (Per Fortnight)", maskVal(fortnightlyOffsetSavings, 2), highlightColor = BrandMintGreen)
                    LoanDetailRow("Interest Saved (Per Month)", maskVal(monthlyOffsetSavings, 2), highlightColor = BrandMintGreen)
                    LoanDetailRow("Interest Saved (Per Year)", maskVal(annualOffsetSavings, 0), highlightColor = BrandMintGreen)
                    LoanDetailRow("Estimated Payoff Time Left", "${String.format(Locale.getDefault(), "%.1f", actualPayoffYearsLeft)} Years")
                    LoanDetailRow("Years Saved By Offset & Advance", "${String.format(Locale.getDefault(), "%.2f", yearsSaved)} Years Saved", highlightColor = BrandCoralPink)
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Offset Account Simulator", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(maskVal(activeLoan.offsetBalance, 0), fontWeight = FontWeight.ExtraBold, color = BrandMintGreen)
                    }
                    Slider(
                        value = activeLoan.offsetBalance.toFloat().coerceIn(0f, 400000f),
                        onValueChange = { newOffset ->
                            val stepped = ((newOffset / 1000f).roundToInt() * 1000).toDouble()
                            onUpdateActive(activeLoan.copy(offsetBalance = stepped))
                        },
                        valueRange = 0f..400000f,
                        modifier = Modifier.lockPagerOnTouch(),
                        colors = SliderDefaults.colors(thumbColor = BrandCoralPink, activeTrackColor = BrandCoralPink)
                    )
                    Text(
                        "Slide to adjust offset balance — automatically updates your fortnightly interest accrual and payoff schedule below.",
                        fontSize = 11.sp,
                        color = SlateGray
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Fortnightly Schedule (${activeLoan.name})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                TextButton(onClick = { showFullSchedule = !showFullSchedule }) {
                    Text(if (showFullSchedule) "Show Next 15" else "Show 60 Fortnights", color = BrandCoralPink)
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Fn # / Date", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
                    Text("Payment", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("Int. Charged", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f))
                    Text("Accrued", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("Balance", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                }
            }
        }

        val displayRows = schedule.drop(simulatedFortnight).take(if (showFullSchedule) 60 else 15)
        itemsIndexed(displayRows) { idx, row ->
            val isCurrent = idx == 0
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) BrandCoralPink.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.3f)) {
                        Text(
                            "#${row.fortnightNumber}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isCurrent) BrandCoralPink else MaterialTheme.colorScheme.onSurface
                        )
                        Text(row.paymentDate, fontSize = 10.sp, color = SlateGray)
                    }
                    Text(
                        maskVal(row.payment, 0),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        if (row.monthlyInterestCharged > 0) maskVal(row.monthlyInterestCharged, 2) else "-",
                        fontSize = 11.sp,
                        fontWeight = if (row.monthlyInterestCharged > 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (row.monthlyInterestCharged > 0) BrandCoralRed else SlateGray,
                        modifier = Modifier.weight(1.1f)
                    )
                    Text(
                        if (row.accruedInterest > 0) maskVal(row.accruedInterest, 2) else "-",
                        fontSize = 11.sp,
                        color = SlateGray,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        maskVal(row.closingBalance, 0),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isCurrent) BrandCoralPink else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1.2f)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    if (showRateModal) {
        EditLoanConfigDialog(
            loan = activeLoan,
            onDismiss = { showRateModal = false },
            onSave = { updated ->
                onUpdateActive(updated)
                showRateModal = false
            }
        )
    }
}

// ============================================================================
// AUSTRALIAN WHAT-IF WEALTH & SCENARIO EXPLORER
// ============================================================================

enum class MilestoneCategory(val label: String, val icon: ImageVector, val defaultColor: Color) {
    PROPERTY("Property & Housing", Icons.Default.HomeWork, BrandCoralPink),
    CAREER("Career, Pay & Leave", Icons.Default.Work, BrandMintGreen),
    PURCHASE("Lifestyle & Purchases", Icons.Default.DirectionsCar, BrandWarmAmber),
    INVESTMENT("Super, ETFs & Investing", Icons.AutoMirrored.Filled.TrendingUp, BrandSkyBlue)
}

data class LifeMilestoneBlock(
    val id: String,
    val title: String,
    val category: MilestoneCategory,
    val startYear: Int,
    val durationYears: Int,
    val weeklyCashflowImpact: Double,
    val assetImpact: Double,
    val liabilityImpact: Double,
    var isEnabled: Boolean = true
)

private data class AussieScenarioTile(
    val id: String,
    val title: String,
    val badge: String,
    val icon: ImageVector,
    val color: Color,
    val onApply: () -> Unit
)

private data class AussieMilestoneTemplate(
    val label: String,
    val title: String,
    val category: MilestoneCategory,
    val startYear: Int,
    val durationYears: Int,
    val weeklyCashflow: Double,
    val assetImpact: Double,
    val liabilityImpact: Double
)

@Composable
fun WhatIfScenarioScreen(
    appConfig: UserAppConfig,
    person1Income: Double,
    person2Income: Double,
    person1Items: List<CashflowItem>,
    person2Items: List<CashflowItem>,
    prop1Items: List<CashflowItem> = emptyList(),
    prop2Items: List<CashflowItem> = emptyList(),
    prop1WeeklyMortgage: Double,
    prop2WeeklyMortgage: Double,
    prop1MarketValue: Double,
    prop2MarketValue: Double,
    prop1Loan: LoanConfig,
    prop2Loan: LoanConfig,
    sharesList: List<ShareParcel>,
    superAccountsList: List<SuperAccount>,
    otherAssetsList: List<OtherAsset>,
    isWealthHidden: Boolean,
    horizonYears: Int = 15,
    onHorizonYearsChange: (Int) -> Unit = {},
    lifestyleAdjustmentWeekly: Float = 0f,
    onLifestyleAdjustmentWeeklyChange: (Float) -> Unit = {},
    rateShockProp1: Float = 0f,
    onRateShockProp1Change: (Float) -> Unit = {},
    rateShockProp2: Float = 0f,
    onRateShockProp2Change: (Float) -> Unit = {},
    rentalIncomeGrowthWeekly: Float = 0f,
    onRentalIncomeGrowthWeeklyChange: (Float) -> Unit = {},
    superBoostWeekly: Float = 0f,
    onSuperBoostWeeklyChange: (Float) -> Unit = {},
    customLevers: SnapshotStateList<CustomWhatIfLever> = remember { mutableStateListOf() },
    milestoneList: SnapshotStateList<LifeMilestoneBlock> = remember { mutableStateListOf() },
    financialContextSummary: String = "",
    onExecuteAiActions: (String) -> Pair<String, List<String>> = { it to emptyList() }
) {
    fun maskVal(valAmt: Double, decimals: Int = 0): String {
        return if (isWealthHidden) "$ ••••••" else formatCurrency(valAmt, decimals)
    }

    var activeScenarioId by rememberSaveable { mutableStateOf("status_quo") }

    // Baseline Financial Calculations (matching Total Wealth & Cash Flow tabs)
    val totalPropertyVal = if (appConfig.showProperties) {
        prop1MarketValue + (if (appConfig.hasSecondProperty) prop2MarketValue else 0.0)
    } else 0.0
    val totalSuperVal = if (appConfig.showSuperAndShares) superAccountsList.sumOf { it.balance } else 0.0
    val totalSharesVal = if (appConfig.showSuperAndShares) sharesList.sumOf { it.units * it.currentPrice } else 0.0
    val totalOtherAssetsVal = if (appConfig.showOtherAssets) otherAssetsList.sumOf { it.value } else 0.0
    val totalOffsets = if (appConfig.showLoans) {
        prop1Loan.offsetBalance + (if (appConfig.hasSecondProperty) prop2Loan.offsetBalance else 0.0)
    } else 0.0

    val totalAssets = totalPropertyVal + totalSuperVal + totalSharesVal + totalOtherAssetsVal + totalOffsets

    val p1AutoFortnights = getElapsedFortnightsSinceStart(prop1Loan.startDateStr)
    val p1Schedule = remember(prop1Loan) { generateAmortizationSchedule(prop1Loan, 780) }
    val p1CurrentRow = p1Schedule.getOrNull(p1AutoFortnights.coerceIn(0, p1Schedule.size - 1)) ?: p1Schedule.first()
    val p1LiveBalance = if (appConfig.showLoans) p1CurrentRow.closingBalance else 0.0

    val p2AutoFortnights = getElapsedFortnightsSinceStart(prop2Loan.startDateStr)
    val p2Schedule = remember(prop2Loan) { generateAmortizationSchedule(prop2Loan, 780) }
    val p2CurrentRow = p2Schedule.getOrNull(p2AutoFortnights.coerceIn(0, p2Schedule.size - 1)) ?: p2Schedule.first()
    val p2LiveBalance = if (appConfig.showLoans && appConfig.hasSecondProperty) p2CurrentRow.closingBalance else 0.0

    val totalLiabilities = p1LiveBalance + p2LiveBalance
    val baseNetWorth = totalAssets - totalLiabilities

    val p1Expenses = person1Items.sumOf { it.weeklyAmount }
    val p2Expenses = if (appConfig.isCoupleMode) person2Items.sumOf { it.weeklyAmount } else 0.0
    val prop1HousingCost = if (appConfig.isProperty1Rented) -appConfig.primaryRentWeekly else prop1WeeklyMortgage
    val prop1NetWeekly = prop1HousingCost + prop1Items.sumOf { it.weeklyAmount }
    val prop2NetWeekly = if (appConfig.hasSecondProperty) {
        appConfig.secondaryRentWeekly + prop2WeeklyMortgage + prop2Items.sumOf { it.weeklyAmount }
    } else 0.0

    val p1Surplus = person1Income + p1Expenses
    val p2Surplus = if (appConfig.isCoupleMode) (person2Income + p2Expenses) else 0.0
    val baseSurplusWeekly = p1Surplus + p2Surplus + prop1NetWeekly + prop2NetWeekly

    // Dialog & AI States
    var showAddMilestoneDialog by remember { mutableStateOf(false) }
    var prefillTemplate by remember { mutableStateOf<AussieMilestoneTemplate?>(null) }
    var milestoneToEdit by remember { mutableStateOf<LifeMilestoneBlock?>(null) }
    var showAddCustomLeverDialog by remember { mutableStateOf(false) }
    var customLeverToEdit by remember { mutableStateOf<CustomWhatIfLever?>(null) }
    var whatIfChatInput by remember { mutableStateOf("") }
    val whatIfChatHistory = remember { mutableStateListOf<ChatMessage>() }
    var isWhatIfAiLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val milestoneFingerprint = milestoneList.map {
        "${it.id}_${it.title}_${it.startYear}_${it.durationYears}_${it.weeklyCashflowImpact}_${it.assetImpact}_${it.liabilityImpact}_${it.isEnabled}"
    }
    val customLeversFingerprint = customLevers.map {
        "${it.id}_${it.title}_${it.impactType}_${it.minValue}_${it.maxValue}_${it.stepSize}_${it.currentValue}_${it.isEnabled}"
    }

    val proj = remember(
        baseNetWorth, baseSurplusWeekly, horizonYears,
        totalSharesVal, totalSuperVal, totalPropertyVal, totalOtherAssetsVal, totalOffsets, totalLiabilities,
        appConfig.showLoans, appConfig.hasSecondProperty, prop1Loan, prop2Loan,
        lifestyleAdjustmentWeekly, rateShockProp1, rateShockProp2, rentalIncomeGrowthWeekly, superBoostWeekly,
        milestoneFingerprint, customLeversFingerprint
    ) {
        computeWhatIfProjection(
            horizonYears = horizonYears,
            basePropertyVal = totalPropertyVal,
            baseSharesAndSuperVal = totalSharesVal + totalSuperVal,
            baseOtherAssetsVal = totalOtherAssetsVal,
            baseOffsetsVal = totalOffsets,
            baseLiabilitiesVal = totalLiabilities,
            baseSurplusWeekly = baseSurplusWeekly,
            prop1Loan = prop1Loan,
            prop2Loan = prop2Loan,
            hasSecondProperty = appConfig.hasSecondProperty,
            showLoans = appConfig.showLoans,
            lifestyleAdjustmentWeekly = lifestyleAdjustmentWeekly,
            rateShockProp1 = rateShockProp1,
            rateShockProp2 = rateShockProp2,
            rentalIncomeGrowthWeekly = rentalIncomeGrowthWeekly,
            superBoostWeekly = superBoostWeekly,
            customLevers = customLevers.toList(),
            milestones = milestoneList.toList()
        )
    }

    val pathASeries = proj.pathASeries
    val pathBSeries = proj.pathBSeries
    val deficitYear = proj.deficitYear
    val weeklyMortgageDelta1 = proj.weeklyRateDeltaProp1
    val weeklyMortgageDelta2 = proj.weeklyRateDeltaProp2
    val totalWeeklyRateShockCost = proj.totalWeeklyRateDeltaCost

    val endPathANetWorth = pathASeries.lastOrNull() ?: baseNetWorth
    val endPathBNetWorth = pathBSeries.lastOrNull() ?: baseNetWorth
    val netWorthDifference = endPathBNetWorth - endPathANetWorth

    val year1MilestoneWeekly = milestoneList.filter { it.isEnabled && it.startYear <= 1 }.sumOf { it.weeklyCashflowImpact }
    val weeklyCashflowDeltaNow =
        lifestyleAdjustmentWeekly.toDouble() - totalWeeklyRateShockCost + rentalIncomeGrowthWeekly.toDouble() + proj.customWeeklyCashflowTotal + year1MilestoneWeekly
    val simulatedWeeklySurplusNow = baseSurplusWeekly + weeklyCashflowDeltaNow

    val hasAnyChanges = lifestyleAdjustmentWeekly != 0f ||
            rateShockProp1 != 0f ||
            rateShockProp2 != 0f ||
            rentalIncomeGrowthWeekly != 0f ||
            superBoostWeekly != 0f ||
            customLevers.any { it.isEnabled && abs(it.currentValue) > 0.001f } ||
            milestoneList.any { it.isEnabled }

    fun resetAllToStatusQuo() {
        activeScenarioId = "status_quo"
        onLifestyleAdjustmentWeeklyChange(0f)
        onRateShockProp1Change(0f)
        onRateShockProp2Change(0f)
        onRentalIncomeGrowthWeeklyChange(0f)
        onSuperBoostWeeklyChange(0f)
        customLevers.forEachIndexed { idx, cl ->
            customLevers[idx] = cl.copy(currentValue = 0f)
        }
        milestoneList.clear()
    }

    // Australian Square Tiles Scenario Explorer
    val scenarioTiles = listOf(
        AussieScenarioTile(
            id = "status_quo",
            title = "Status Quo",
            badge = "Baseline",
            icon = Icons.Default.Refresh,
            color = SlateGray,
            onApply = { resetAllToStatusQuo() }
        ),
        AussieScenarioTile(
            id = "rba_cut",
            title = "RBA Rate Cut",
            badge = "-0.50% (2 Cuts)",
            icon = Icons.AutoMirrored.Filled.TrendingDown,
            color = BrandMintGreen,
            onApply = {
                activeScenarioId = "rba_cut"
                onLifestyleAdjustmentWeeklyChange(0f)
                onRateShockProp1Change(-0.5f)
                onRateShockProp2Change(if (appConfig.hasSecondProperty) -0.5f else 0f)
                onRentalIncomeGrowthWeeklyChange(0f)
                onSuperBoostWeeklyChange(0f)
                milestoneList.clear()
            }
        ),
        AussieScenarioTile(
            id = "offset_smash",
            title = "Offset Smash",
            badge = "+$200/wk Saved",
            icon = Icons.Default.Savings,
            color = BrandSkyBlue,
            onApply = {
                activeScenarioId = "offset_smash"
                onLifestyleAdjustmentWeeklyChange(200f)
                onRateShockProp1Change(0f)
                onRateShockProp2Change(0f)
                onRentalIncomeGrowthWeeklyChange(0f)
                onSuperBoostWeeklyChange(0f)
                milestoneList.clear()
            }
        ),
        AussieScenarioTile(
            id = "super_boost",
            title = "Super Boost",
            badge = "+$150/wk Sacr.",
            icon = Icons.Default.AccountBalance,
            color = BrandPurple,
            onApply = {
                activeScenarioId = "super_boost"
                onLifestyleAdjustmentWeeklyChange(0f)
                onRateShockProp1Change(0f)
                onRateShockProp2Change(0f)
                onRentalIncomeGrowthWeeklyChange(0f)
                onSuperBoostWeeklyChange(150f)
                milestoneList.clear()
            }
        ),
        AussieScenarioTile(
            id = "rba_hike",
            title = "RBA Rate Stress",
            badge = "+1.00% (4 Hikes)",
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            color = BrandWarmAmber,
            onApply = {
                activeScenarioId = "rba_hike"
                onLifestyleAdjustmentWeeklyChange(0f)
                onRateShockProp1Change(1.0f)
                onRateShockProp2Change(if (appConfig.hasSecondProperty) 1.0f else 0f)
                onRentalIncomeGrowthWeeklyChange(0f)
                onSuperBoostWeeklyChange(0f)
                milestoneList.clear()
            }
        ),
        AussieScenarioTile(
            id = "inv_property",
            title = "Buy Inv. Property",
            badge = "$750k in Yr 2",
            icon = Icons.Default.HomeWork,
            color = BrandCoralPink,
            onApply = {
                activeScenarioId = "inv_property"
                onLifestyleAdjustmentWeeklyChange(0f)
                onRateShockProp1Change(0f)
                onRateShockProp2Change(0f)
                onRentalIncomeGrowthWeeklyChange(0f)
                onSuperBoostWeeklyChange(0f)
                milestoneList.clear()
                milestoneList.add(
                    LifeMilestoneBlock(
                        id = "m_au_prop",
                        title = "Buy Aussie Investment Property ($750k)",
                        category = MilestoneCategory.PROPERTY,
                        startYear = 2,
                        durationYears = 25,
                        weeklyCashflowImpact = -150.0,
                        assetImpact = 750000.0,
                        liabilityImpact = 600000.0,
                        isEnabled = true
                    )
                )
            }
        ),
        AussieScenarioTile(
            id = "etf_dca",
            title = "ASX ETF Plan",
            badge = "+$150/wk VAS/VGS",
            icon = Icons.AutoMirrored.Filled.ShowChart,
            color = BrandMintGreen,
            onApply = {
                activeScenarioId = "etf_dca"
                onLifestyleAdjustmentWeeklyChange(150f)
                onRateShockProp1Change(0f)
                onRateShockProp2Change(0f)
                onRentalIncomeGrowthWeeklyChange(0f)
                onSuperBoostWeeklyChange(50f)
                milestoneList.clear()
            }
        ),
        AussieScenarioTile(
            id = "parental_leave",
            title = "Parental Leave",
            badge = "1 Yr Career Break",
            icon = Icons.Default.ChildCare,
            color = BrandCoralLight,
            onApply = {
                activeScenarioId = "parental_leave"
                onLifestyleAdjustmentWeeklyChange(0f)
                onRateShockProp1Change(0f)
                onRateShockProp2Change(0f)
                onRentalIncomeGrowthWeeklyChange(0f)
                onSuperBoostWeeklyChange(0f)
                milestoneList.clear()
                milestoneList.add(
                    LifeMilestoneBlock(
                        id = "m_au_parental",
                        title = "Parental Leave / 1 Yr Reduced Income",
                        category = MilestoneCategory.CAREER,
                        startYear = 2,
                        durationYears = 1,
                        weeklyCashflowImpact = -650.0,
                        assetImpact = 0.0,
                        liabilityImpact = 0.0,
                        isEnabled = true
                    )
                )
            }
        )
    )

    val quickMilestoneTemplates = listOf(
        AussieMilestoneTemplate("🏡 Investment Property", "Buy Investment Property ($750k)", MilestoneCategory.PROPERTY, 2, 25, -150.0, 750000.0, 600000.0),
        AussieMilestoneTemplate("💼 Pay Rise (+$200/wk)", "Career Promotion / Pay Rise", MilestoneCategory.CAREER, 1, 15, 200.0, 0.0, 0.0),
        AussieMilestoneTemplate("👶 Parental Leave (1 Yr)", "Parental Leave (12 Months)", MilestoneCategory.CAREER, 2, 1, -650.0, 0.0, 0.0),
        AussieMilestoneTemplate("🚗 Upgrade Car / EV ($45k)", "Buy New EV / Family SUV", MilestoneCategory.PURCHASE, 2, 1, 0.0, -45000.0, 0.0),
        AussieMilestoneTemplate("🔨 Home Renovation ($60k)", "Kitchen & Living Renovation", MilestoneCategory.PROPERTY, 3, 1, 0.0, 40000.0, 60000.0),
        AussieMilestoneTemplate("🎓 Private School Fees", "Kids High School Fees (6 Yrs)", MilestoneCategory.PURCHASE, 4, 6, -350.0, 0.0, 0.0),
        AussieMilestoneTemplate("✈️ Europe / Big Holiday", "Major Family Holiday ($18k)", MilestoneCategory.PURCHASE, 2, 1, 0.0, -18000.0, 0.0),
        AussieMilestoneTemplate("💰 Bonus / Inheritance", "Lump Sum Windfall ($50k)", MilestoneCategory.INVESTMENT, 3, 1, 0.0, 50000.0, 0.0)
    )

    val orbSpacing = 16.dp
    val headerTopPad = rememberHeaderAwareTopPadding(orbSpacing)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = headerTopPad, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(orbSpacing)
    ) {
        // ====================================================================
        // 1. SQUARE TILES SCENARIO EXPLORER (AUSTRALIAN EDITION)
        // ====================================================================
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Scenario Explorer", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        Text("Tap a square tile to test popular Australian scenarios", fontSize = 11.sp, color = SlateGray)
                    }
                    if (hasAnyChanges) {
                        TextButton(
                            onClick = { resetAllToStatusQuo() },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = BrandCoralPink, modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Reset", color = BrandCoralPink, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                LazyRow(
                    modifier = Modifier.lockPagerOnTouch(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(scenarioTiles) { tile ->
                        val isSelected = (tile.id == "status_quo" && !hasAnyChanges) || (tile.id == activeScenarioId && hasAnyChanges)
                        Card(
                            onClick = tile.onApply,
                            modifier = Modifier.size(116.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) tile.color else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color.White else tile.color.copy(alpha = 0.45f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        tile.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) BrandForestDark else tile.color,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = "Active",
                                            tint = BrandForestDark,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        tile.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) BrandForestDark else MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        tile.badge,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) BrandForestDark.copy(alpha = 0.8f) else tile.color,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // 2. HERO LIVE PROJECTION & TRAJECTORY COMPARISON CARD
        // ====================================================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Header + Timeframe Selector Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Projected Net Worth", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            Text("In $horizonYears Years (Today: ${maskVal(baseNetWorth, 0)})", fontSize = 11.sp, color = SlateGray)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(5, 10, 15, 20, 30).forEach { yrs ->
                                val selected = horizonYears == yrs
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onHorizonYearsChange(yrs) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selected) BrandCoralPink else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "${yrs}Y",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (selected) BrandForestDark else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Side-by-Side Current Path vs What-If Plan
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("STATUS QUO", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = SlateGray)
                                Text(
                                    maskVal(endPathANetWorth, 0),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "Surplus: ${maskVal(baseSurplusWeekly, 0)}/wk",
                                    fontSize = 11.sp,
                                    color = SlateGray
                                )
                            }
                        }

                        val isPositiveDiff = netWorthDifference >= 0
                        val planAccent = if (!hasAnyChanges) BrandSkyBlue else if (isPositiveDiff) BrandMintGreen else BrandCoralPink
                        val isDarkPlanOrb = LocalIsDarkTheme.current
                        val planHeaderColor = if (isDarkPlanOrb) planAccent else BrandForestDark
                        val planValueColor = if (isDarkPlanOrb) Color.White else BrandForestDark
                        val planSubColor = if (isDarkPlanOrb) planAccent else BrandForestSurface
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = planAccent.copy(alpha = 0.16f)),
                            border = BorderStroke(1.5.dp, planAccent)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("WHAT-IF PLAN", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = planHeaderColor)
                                Text(
                                    maskVal(endPathBNetWorth, 0),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = planValueColor
                                )
                                Text(
                                    "Surplus: ${maskVal(simulatedWeeklySurplusNow, 0)}/wk",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = planSubColor
                                )
                            }
                        }
                    }

                    // Visual Dual-Line Wealth Trajectory Chart
                    WhatIfTrajectoryChart(
                        pathASeries = pathASeries,
                        pathBSeries = pathBSeries,
                        horizonYears = horizonYears,
                        isPositiveDiff = netWorthDifference >= 0
                    )

                    // Net Difference Pill + Weekly Cashflow Pill
                    val diffColor = if (abs(netWorthDifference) < 1.0) SlateGray else if (netWorthDifference > 0) BrandMintGreen else BrandCoralPink
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = diffColor.copy(alpha = 0.14f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    if (abs(netWorthDifference) < 1.0) "No changes active yet"
                                    else if (netWorthDifference > 0) "Net Wealth Gain in $horizonYears Yrs"
                                    else "Net Wealth Cost in $horizonYears Yrs",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    if (abs(weeklyCashflowDeltaNow) < 0.5) "Weekly cashflow unchanged"
                                    else "Weekly cashflow: ${if (weeklyCashflowDeltaNow > 0) "+" else ""}${maskVal(weeklyCashflowDeltaNow, 0)}/wk",
                                    fontSize = 11.sp,
                                    color = SlateGray
                                )
                            }
                            Text(
                                text = if (abs(netWorthDifference) < 1.0) "$0"
                                else "${if (netWorthDifference > 0) "+" else ""}${maskVal(netWorthDifference, 0)}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp,
                                color = diffColor
                            )
                        }
                    }

                    if (deficitYear != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = BrandCoralRed.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, BrandCoralRed)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = BrandCoralRed, modifier = Modifier.size(18.dp))
                                Text(
                                    "Cash Reserve Alert: Liquid offset/cash reserves drop below $0 in Year $deficitYear under this scenario.",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandCoralRed
                                )
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // 3. INTERACTIVE AUSTRALIAN FINANCIAL LEVERS (SLIDERS + TYPED INPUT + PRESETS + CUSTOM LEVERS)
        // ====================================================================
        item {
            val baseRate1 = prop1Loan.interestRatePa
            val shockedRate1 = (baseRate1 + rateShockProp1).coerceAtLeast(0.5)
            val baseRate2 = prop2Loan.interestRatePa
            val shockedRate2 = (baseRate2 + rateShockProp2).coerceAtLeast(0.5)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Interactive Levers", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            Text("Slide, tap presets, or tap any value/keyboard icon to type an exact number", fontSize = 11.sp, color = SlateGray)
                        }
                        OutlinedButton(
                            onClick = {
                                customLeverToEdit = null
                                showAddCustomLeverDialog = true
                            },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = BrandCoralPink, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Custom Lever", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandCoralPink)
                        }
                    }

                    // LEVER 1: EXTRA WEEKLY OFFSET / SAVINGS
                    WhatIfInteractiveLeverCard(
                        title = "1. Extra Weekly Offset / Savings",
                        subtitle = if (lifestyleAdjustmentWeekly == 0f) "Slide right to save more, left to spend more, or type any $/wk"
                        else "${if (lifestyleAdjustmentWeekly > 0) "+" else ""}${maskVal(lifestyleAdjustmentWeekly * 52.0, 0)} per year",
                        value = lifestyleAdjustmentWeekly,
                        valueRange = -500f..1000f,
                        stepSize = 25f,
                        isPercentage = false,
                        unitSuffix = "/wk",
                        accentColor = if (lifestyleAdjustmentWeekly >= 0f) BrandMintGreen else BrandCoralPink,
                        presets = listOf(-100f to "-$100", 0f to "$0", 100f to "+$100", 200f to "+$200", 350f to "+$350"),
                        onValueChange = { newVal ->
                            activeScenarioId = "custom"
                            onLifestyleAdjustmentWeeklyChange(newVal)
                        }
                    )

                    HorizontalDivider()

                    // LEVER 2: PROPERTY 1 INTEREST RATE CHANGE
                    WhatIfInteractiveLeverCard(
                        title = "2. ${appConfig.property1Name} Interest Rate Δ",
                        subtitle = when {
                            abs(weeklyMortgageDelta1) < 0.5 -> "${prop1Loan.name}: ${String.format(Locale.getDefault(), "%.2f", baseRate1)}% p.a."
                            weeklyMortgageDelta1 < 0 -> "Saves +${maskVal(abs(weeklyMortgageDelta1), 0)}/wk (${String.format(Locale.getDefault(), "%.2f", shockedRate1)}% p.a.)"
                            else -> "Costs -${maskVal(weeklyMortgageDelta1, 0)}/wk extra (${String.format(Locale.getDefault(), "%.2f", shockedRate1)}% p.a.)"
                        },
                        value = rateShockProp1,
                        valueRange = -3.0f..4.0f,
                        stepSize = 0.25f,
                        isPercentage = true,
                        unitSuffix = "%",
                        accentColor = if (rateShockProp1 <= 0f) BrandMintGreen else BrandCoralPink,
                        presets = listOf(-1.0f to "-1.0%", -0.5f to "-0.5%", 0f to "0.0%", 0.5f to "+0.5%", 1.0f to "+1.0%"),
                        onValueChange = { newVal ->
                            activeScenarioId = "custom"
                            onRateShockProp1Change(newVal)
                        }
                    )

                    HorizontalDivider()

                    // LEVER 3: PROPERTY 2 INTEREST RATE CHANGE (DEDICATED LEVER PER PROPERTY)
                    WhatIfInteractiveLeverCard(
                        title = "3. ${if (appConfig.hasSecondProperty) appConfig.property2Name else "Property 2"} Interest Rate Δ",
                        subtitle = if (!appConfig.hasSecondProperty) {
                            "Enable 2nd property in Settings or test hypothetical 2nd property rate changes"
                        } else {
                            when {
                                abs(weeklyMortgageDelta2) < 0.5 -> "${prop2Loan.name}: ${String.format(Locale.getDefault(), "%.2f", baseRate2)}% p.a."
                                weeklyMortgageDelta2 < 0 -> "Saves +${maskVal(abs(weeklyMortgageDelta2), 0)}/wk (${String.format(Locale.getDefault(), "%.2f", shockedRate2)}% p.a.)"
                                else -> "Costs -${maskVal(weeklyMortgageDelta2, 0)}/wk extra (${String.format(Locale.getDefault(), "%.2f", shockedRate2)}% p.a.)"
                            }
                        },
                        value = rateShockProp2,
                        valueRange = -3.0f..4.0f,
                        stepSize = 0.25f,
                        isPercentage = true,
                        unitSuffix = "%",
                        accentColor = if (rateShockProp2 <= 0f) BrandMintGreen else BrandWarmAmber,
                        presets = listOf(-1.0f to "-1.0%", -0.5f to "-0.5%", 0f to "0.0%", 0.5f to "+0.5%", 1.0f to "+1.0%"),
                        onValueChange = { newVal ->
                            activeScenarioId = "custom"
                            onRateShockProp2Change(newVal)
                        }
                    )

                    HorizontalDivider()

                    // LEVER 4: SUPERANNUATION SALARY SACRIFICE BOOST
                    WhatIfInteractiveLeverCard(
                        title = "4. Extra Super Salary Sacrifice",
                        subtitle = if (superBoostWeekly == 0f) "Concessional Super contributions compounding at 8% p.a."
                        else "+${maskVal(superBoostWeekly * 52.0, 0)}/yr boosted into Super",
                        value = superBoostWeekly,
                        valueRange = 0f..600f,
                        stepSize = 25f,
                        isPercentage = false,
                        unitSuffix = "/wk",
                        accentColor = BrandSkyBlue,
                        presets = listOf(0f to "$0", 50f to "+$50", 100f to "+$100", 150f to "+$150", 250f to "+$250"),
                        onValueChange = { newVal ->
                            activeScenarioId = "custom"
                            onSuperBoostWeeklyChange(newVal.coerceAtLeast(0f))
                        }
                    )

                    HorizontalDivider()

                    // LEVER 5: RENTAL INCOME / PROPERTY CASHFLOW CHANGE
                    WhatIfInteractiveLeverCard(
                        title = "5. Weekly Rent / Property Cashflow",
                        subtitle = "Model higher rental income (+$/wk) or higher property holding costs (-$/wk)",
                        value = rentalIncomeGrowthWeekly,
                        valueRange = -300f..500f,
                        stepSize = 25f,
                        isPercentage = false,
                        unitSuffix = "/wk",
                        accentColor = if (rentalIncomeGrowthWeekly >= 0f) BrandWarmAmber else BrandCoralPink,
                        presets = listOf(-100f to "-$100", -50f to "-$50", 0f to "$0", 50f to "+$50", 150f to "+$150"),
                        onValueChange = { newVal ->
                            activeScenarioId = "custom"
                            onRentalIncomeGrowthWeeklyChange(newVal)
                        }
                    )

                    // USER-DEFINED CUSTOM LEVERS
                    if (customLevers.isNotEmpty()) {
                        HorizontalDivider()
                        Text(
                            "Custom Levers (${customLevers.size})",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = BrandCoralPink
                        )
                        customLevers.forEachIndexed { idx, cLever ->
                            val isRateType = cLever.impactType == CustomLeverImpactType.INTEREST_RATE_DELTA
                            val span = (cLever.maxValue - cLever.minValue).coerceAtLeast(1f)
                            val q1 = cLever.minValue + span * 0.25f
                            val q3 = cLever.minValue + span * 0.75f
                            fun fmtPreset(v: Float): String = if (isRateType) {
                                "${if (v > 0f) "+" else ""}${String.format(Locale.getDefault(), "%.2f", v)}%"
                            } else {
                                "${if (v > 0f) "+" else if (v < 0f) "-" else ""}$${abs(v.roundToInt())}"
                            }
                            val customPresets = listOf(
                                cLever.minValue to fmtPreset(cLever.minValue),
                                q1 to fmtPreset(q1),
                                0f.coerceIn(cLever.minValue, cLever.maxValue) to fmtPreset(0f.coerceIn(cLever.minValue, cLever.maxValue)),
                                q3 to fmtPreset(q3),
                                cLever.maxValue to fmtPreset(cLever.maxValue)
                            ).distinctBy { (it.first * 100).roundToInt() }

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = BorderStroke(1.dp, BrandCoralPink.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = cLever.impactType.label,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = BrandCoralPink
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            IconButton(
                                                onClick = {
                                                    customLeverToEdit = cLever
                                                    showAddCustomLeverDialog = true
                                                },
                                                modifier = Modifier.size(26.dp)
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit Custom Lever", tint = SlateGray, modifier = Modifier.size(14.dp))
                                            }
                                            IconButton(
                                                onClick = { customLevers.removeAt(idx) },
                                                modifier = Modifier.size(26.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete Custom Lever", tint = BrandCoralPink, modifier = Modifier.size(14.dp))
                                            }
                                            Switch(
                                                checked = cLever.isEnabled,
                                                onCheckedChange = { enabled ->
                                                    customLevers[idx] = cLever.copy(isEnabled = enabled)
                                                }
                                            )
                                        }
                                    }

                                    WhatIfInteractiveLeverCard(
                                        title = cLever.title,
                                        subtitle = "Range: ${fmtPreset(cLever.minValue)} to ${fmtPreset(cLever.maxValue)} (${cLever.impactType.unitSuffix})",
                                        value = cLever.currentValue,
                                        valueRange = cLever.minValue..cLever.maxValue,
                                        stepSize = cLever.stepSize,
                                        isPercentage = isRateType,
                                        unitSuffix = cLever.impactType.unitSuffix,
                                        accentColor = if (cLever.isEnabled) BrandMintGreen else SlateGray,
                                        presets = customPresets,
                                        onValueChange = { newVal ->
                                            activeScenarioId = "custom"
                                            customLevers[idx] = cLever.copy(currentValue = newVal, isEnabled = true)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // 4. CUSTOM LIFE MILESTONES (EMPTY BY DEFAULT — ADD ANYTHING YOU LIKE)
        // ====================================================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Life Milestones & Events", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            Text("Starts empty — add any future purchase, property or career move", fontSize = 11.sp, color = SlateGray)
                        }
                        Button(
                            onClick = {
                                prefillTemplate = null
                                showAddMilestoneDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Custom Event", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    // 1-Tap Quick-Add Australian Templates Row
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Quick-Add Australian Events (tap to customize & add):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateGray)
                        LazyRow(
                            modifier = Modifier.lockPagerOnTouch(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(quickMilestoneTemplates) { tmpl ->
                                AssistChip(
                                    onClick = {
                                        prefillTemplate = tmpl
                                        showAddMilestoneDialog = true
                                    },
                                    label = { Text(tmpl.label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                                )
                            }
                        }
                    }

                    if (milestoneList.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.EventNote, contentDescription = null, tint = SlateGray, modifier = Modifier.size(28.dp))
                                Text("No milestone events active", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text(
                                    "Your simulation is completely clean. Tap 'Custom Event' or any Quick-Add chip above to layer life events onto your timeline.",
                                    fontSize = 11.sp,
                                    color = SlateGray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        milestoneList.forEachIndexed { index, milestone ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { milestoneList[index] = milestone.copy(isEnabled = !milestone.isEnabled) },
                                color = if (milestone.isEnabled) milestone.category.defaultColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, if (milestone.isEnabled) milestone.category.defaultColor else SlateGray.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = milestone.category.defaultColor,
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(milestone.category.icon, contentDescription = null, tint = BrandForestDark, modifier = Modifier.padding(8.dp))
                                        }
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text(milestone.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            val details = buildList {
                                                add("Yr ${milestone.startYear}")
                                                if (milestone.weeklyCashflowImpact != 0.0) {
                                                    add("${if (milestone.weeklyCashflowImpact > 0) "+" else ""}$${milestone.weeklyCashflowImpact.toInt()}/wk (${milestone.durationYears}y)")
                                                }
                                                if (milestone.assetImpact != 0.0) {
                                                    add("Assets: ${if (milestone.assetImpact > 0) "+" else ""}${formatCurrency(milestone.assetImpact, 0)}")
                                                }
                                                if (milestone.liabilityImpact > 0.0) {
                                                    add("Loan: +${formatCurrency(milestone.liabilityImpact, 0)}")
                                                }
                                            }.joinToString(" • ")
                                            Text(details, fontSize = 11.sp, color = SlateGray)
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        IconButton(
                                            onClick = { milestoneToEdit = milestone },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit Milestone", tint = SlateGray, modifier = Modifier.size(16.dp))
                                        }
                                        IconButton(
                                            onClick = { milestoneList.removeAt(index) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete Milestone", tint = BrandCoralPink, modifier = Modifier.size(16.dp))
                                        }
                                        Switch(
                                            checked = milestone.isEnabled,
                                            onCheckedChange = { milestoneList[index] = milestone.copy(isEnabled = it) },
                                            colors = SwitchDefaults.colors(checkedThumbColor = BrandForestDark, checkedTrackColor = milestone.category.defaultColor)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // 5. CONVERSATIONAL WHAT-IF AI ADVISOR (WITH ONE-TAP AUSSIE PROMPTS)
        // ====================================================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrandCoralPink, modifier = Modifier.size(20.dp))
                        Column {
                            Text("Ask EXPNZ AI About This Scenario", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                            Text("Get tailored Australian guidance on your numbers", fontSize = 11.sp, color = SlateGray)
                        }
                    }

                    val samplePrompts = listOf(
                        "Offset vs Super salary sacrifice for us?",
                        "Can we afford a $750k investment property?",
                        "Impact of two 0.25% RBA rate cuts?"
                    )
                    LazyRow(
                        modifier = Modifier.lockPagerOnTouch(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(samplePrompts) { prompt ->
                            SuggestionChip(
                                onClick = { whatIfChatInput = prompt },
                                label = { Text(prompt, fontSize = 11.sp) }
                            )
                        }
                    }

                    if (whatIfChatHistory.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            whatIfChatHistory.forEach { msg ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (msg.isFromUser) BrandCoralPink.copy(alpha = 0.2f) else BrandForestDark,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = (if (msg.isFromUser) "You: " else "AI: ") + msg.text,
                                        fontSize = 12.sp,
                                        color = if (msg.isFromUser) MaterialTheme.colorScheme.onSurface else Color.White,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = whatIfChatInput,
                            onValueChange = { whatIfChatInput = it },
                            placeholder = { Text("Ask any Australian What-If question...", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 2
                        )
                        Button(
                            onClick = {
                                if (whatIfChatInput.isNotBlank() && !isWhatIfAiLoading) {
                                    val q = whatIfChatInput.trim()
                                    whatIfChatInput = ""
                                    whatIfChatHistory.add(ChatMessage(q, true))
                                    isWhatIfAiLoading = true
                                    coroutineScope.launch {
                                        val pathAEndNW = pathASeries.lastOrNull() ?: baseNetWorth
                                        val pathBEndNW = pathBSeries.lastOrNull() ?: baseNetWorth
                                        val nwImpact = pathBEndNW - pathAEndNW
                                        val activeMilestonesStr = milestoneList.filter { it.isEnabled }.let { list ->
                                            if (list.isEmpty()) "None"
                                            else list.joinToString {
                                                "'${it.title}' (Start Yr ${it.startYear}, Duration ${it.durationYears}Y, Assets: ${formatCurrency(it.assetImpact, 0)}, Loan: ${formatCurrency(it.liabilityImpact, 0)}, Weekly Cashflow: ${formatCurrency(it.weeklyCashflowImpact, 2)}/wk)"
                                            }
                                        }

                                        val whatIfContextSummary = buildString {
                                            append("EXPLICIT COMPUTED WHAT-IF SIMULATION DATA (AUSTRALIA): ")
                                            append("Time Horizon = $horizonYears Years. ")
                                            append("Baseline Net Worth = ${formatCurrency(baseNetWorth, 2)}. ")
                                            append("Baseline Weekly Surplus = ${formatCurrency(baseSurplusWeekly, 2)}/wk. ")
                                            append("Status Quo Net Worth in ${horizonYears}Y = ${formatCurrency(pathAEndNW, 2)}. ")
                                            append("What-If Plan Net Worth in ${horizonYears}Y = ${formatCurrency(pathBEndNW, 2)}. ")
                                            append("Net Wealth Impact = ${if (nwImpact >= 0) "+" else ""}${formatCurrency(nwImpact, 2)}. ")
                                            append("Extra Weekly Offset/Savings = ${formatCurrency(lifestyleAdjustmentWeekly.toDouble(), 2)}/wk. ")
                                            append("Property 1 Rate Change = ${rateShockProp1}% (Prop 2 Rate Change = ${rateShockProp2}%, Total weekly repayment delta: ${formatCurrency(totalWeeklyRateShockCost, 2)}/wk). ")
                                            append("Extra Super Salary Sacrifice = ${formatCurrency(superBoostWeekly.toDouble(), 2)}/wk. ")
                                            append("Active Milestones: $activeMilestonesStr. ")
                                        }

                                        val rawReply = AiAssistantManager.askGeminiAssistant(
                                            q,
                                            if (financialContextSummary.isNotBlank()) "$financialContextSummary\n$whatIfContextSummary" else whatIfContextSummary
                                        )
                                        val (cleanReply, appliedList) = onExecuteAiActions(rawReply)
                                        whatIfChatHistory.add(ChatMessage(text = cleanReply, isFromUser = false, appliedActions = appliedList))
                                        isWhatIfAiLoading = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(50.dp)
                        ) {
                            if (isWhatIfAiLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = BrandForestDark, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddCustomLeverDialog) {
        AddEditCustomLeverDialog(
            existingLever = customLeverToEdit,
            onDismiss = {
                showAddCustomLeverDialog = false
                customLeverToEdit = null
            },
            onConfirm = { title, impactType, minVal, maxVal, stepVal, currVal ->
                activeScenarioId = "custom"
                if (customLeverToEdit != null) {
                    val idx = customLevers.indexOfFirst { it.id == customLeverToEdit!!.id }
                    if (idx >= 0) {
                        customLevers[idx] = customLeverToEdit!!.copy(
                            title = title,
                            impactType = impactType,
                            minValue = minVal,
                            maxValue = maxVal,
                            stepSize = stepVal,
                            currentValue = currVal.coerceIn(minVal, maxVal)
                        )
                    }
                } else {
                    customLevers.add(
                        CustomWhatIfLever(
                            id = "clever_${System.currentTimeMillis()}",
                            title = title,
                            impactType = impactType,
                            minValue = minVal,
                            maxValue = maxVal,
                            stepSize = stepVal,
                            currentValue = currVal.coerceIn(minVal, maxVal),
                            isEnabled = true
                        )
                    )
                }
                showAddCustomLeverDialog = false
                customLeverToEdit = null
            }
        )
    }

    if (showAddMilestoneDialog) {
        AddCustomMilestoneDialog(
            initialTemplate = prefillTemplate,
            onDismiss = {
                showAddMilestoneDialog = false
                prefillTemplate = null
            },
            onConfirm = { title, cat, startY, durationY, weeklyCash, assetAmt, liabAmt ->
                activeScenarioId = "custom"
                milestoneList.add(
                    LifeMilestoneBlock(
                        id = "m_${System.currentTimeMillis()}",
                        title = title,
                        category = cat,
                        startYear = startY,
                        durationYears = durationY,
                        weeklyCashflowImpact = weeklyCash,
                        assetImpact = assetAmt,
                        liabilityImpact = liabAmt,
                        isEnabled = true
                    )
                )
                showAddMilestoneDialog = false
                prefillTemplate = null
            }
        )
    }

    if (milestoneToEdit != null) {
        EditCustomMilestoneDialog(
            milestone = milestoneToEdit!!,
            onDismiss = { milestoneToEdit = null },
            onConfirm = { title, cat, startY, durationY, weeklyCash, assetAmt, liabAmt ->
                activeScenarioId = "custom"
                val idx = milestoneList.indexOfFirst { it.id == milestoneToEdit!!.id }
                if (idx >= 0) {
                    milestoneList[idx] = milestoneToEdit!!.copy(
                        title = title,
                        category = cat,
                        startYear = startY,
                        durationYears = durationY,
                        weeklyCashflowImpact = weeklyCash,
                        assetImpact = assetAmt,
                        liabilityImpact = liabAmt
                    )
                }
                milestoneToEdit = null
            }
        )
    }
}

@Composable
private fun WhatIfInteractiveLeverCard(
    title: String,
    subtitle: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    stepSize: Float,
    isPercentage: Boolean,
    unitSuffix: String,
    accentColor: Color,
    presets: List<Pair<Float, String>>,
    onValueChange: (Float) -> Unit
) {
    var showNumberEntry by remember { mutableStateOf(false) }
    var typedValueText by remember(value, showNumberEntry) {
        mutableStateOf(
            if (isPercentage) String.format(Locale.getDefault(), "%.2f", value)
            else value.roundToInt().toString()
        )
    }

    fun applyTypedNumber() {
        val parsed = typedValueText.replace("$", "").replace("%", "").replace("+", "").trim().toFloatOrNull()
        if (parsed != null) {
            onValueChange(parsed.coerceIn(valueRange.start, valueRange.endInclusive))
        }
        showNumberEntry = false
    }

    val formattedBadge = if (isPercentage) {
        when {
            abs(value) < 0.005f -> "0.00%"
            value > 0f -> "+${String.format(Locale.getDefault(), "%.2f", value)}%"
            else -> "${String.format(Locale.getDefault(), "%.2f", value)}%"
        }
    } else {
        val rounded = value.roundToInt()
        when {
            rounded == 0 -> "$0 $unitSuffix"
            rounded > 0 -> "+$$rounded $unitSuffix"
            else -> "-$${abs(rounded)} $unitSuffix"
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    subtitle,
                    fontSize = 11.sp,
                    color = SlateGray
                )
            }
            Spacer(Modifier.width(8.dp))
            Surface(
                onClick = { showNumberEntry = !showNumberEntry },
                shape = RoundedCornerShape(10.dp),
                color = accentColor.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = formattedBadge,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor
                    )
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Type exact number",
                        tint = accentColor,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }

        if (showNumberEntry) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = typedValueText,
                    onValueChange = { typedValueText = it },
                    label = {
                        Text(
                            if (isPercentage) "Type exact % (${valueRange.start}% to +${valueRange.endInclusive}%)"
                            else "Type exact amount ($${valueRange.start.roundToInt()} to $${valueRange.endInclusive.roundToInt()})"
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (isPercentage) KeyboardType.Decimal else KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                )
                Button(
                    onClick = { applyTypedNumber() },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor, contentColor = BrandForestDark),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Apply", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledIconButton(
                onClick = {
                    val next = if (isPercentage) {
                        ((value - stepSize) * (1f / stepSize)).roundToInt() * stepSize
                    } else {
                        ((value - stepSize) / stepSize).roundToInt() * stepSize
                    }
                    onValueChange(next.coerceIn(valueRange.start, valueRange.endInclusive))
                },
                modifier = Modifier.size(34.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
            }

            Slider(
                value = value.coerceIn(valueRange.start, valueRange.endInclusive),
                onValueChange = { raw ->
                    val stepped = if (stepSize > 0f) {
                        (raw / stepSize).roundToInt() * stepSize
                    } else raw
                    onValueChange(stepped.coerceIn(valueRange.start, valueRange.endInclusive))
                },
                valueRange = valueRange,
                modifier = Modifier
                    .weight(1f)
                    .lockPagerOnTouch(),
                colors = SliderDefaults.colors(
                    thumbColor = accentColor,
                    activeTrackColor = accentColor
                )
            )

            FilledIconButton(
                onClick = {
                    val next = if (isPercentage) {
                        ((value + stepSize) * (1f / stepSize)).roundToInt() * stepSize
                    } else {
                        ((value + stepSize) / stepSize).roundToInt() * stepSize
                    }
                    onValueChange(next.coerceIn(valueRange.start, valueRange.endInclusive))
                },
                modifier = Modifier.size(34.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = accentColor.copy(alpha = 0.25f))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase", tint = accentColor, modifier = Modifier.size(16.dp))
            }
        }

        if (presets.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                presets.forEach { (presetVal, lbl) ->
                    val isSelected = abs(value - presetVal) < (if (isPercentage) 0.04f else 0.5f)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onValueChange(presetVal) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) accentColor.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = if (isSelected) BorderStroke(1.dp, accentColor) else null
                    ) {
                        Text(
                            text = lbl,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddEditCustomLeverDialog(
    existingLever: CustomWhatIfLever? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, CustomLeverImpactType, Float, Float, Float, Float) -> Unit
) {
    var title by remember { mutableStateOf(existingLever?.title ?: "") }
    var selectedType by remember { mutableStateOf(existingLever?.impactType ?: CustomLeverImpactType.WEEKLY_CASHFLOW) }
    var minValStr by remember { mutableStateOf((existingLever?.minValue ?: -200f).toString()) }
    var maxValStr by remember { mutableStateOf((existingLever?.maxValue ?: 500f).toString()) }
    var stepValStr by remember { mutableStateOf((existingLever?.stepSize ?: 25f).toString()) }
    var currValStr by remember { mutableStateOf((existingLever?.currentValue ?: 0f).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingLever == null) "Add Custom Simulator Lever" else "Edit Custom Lever", fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Lever Name (e.g. Side Hustle, Childcare, Solar Savings)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("How does this lever impact your plan?", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    CustomLeverImpactType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = {
                                selectedType = type
                                if (existingLever == null) {
                                    when (type) {
                                        CustomLeverImpactType.WEEKLY_CASHFLOW -> {
                                            minValStr = "-300"
                                            maxValStr = "500"
                                            stepValStr = "25"
                                            currValStr = "100"
                                        }
                                        CustomLeverImpactType.ANNUAL_ASSET_CHANGE -> {
                                            minValStr = "0"
                                            maxValStr = "25000"
                                            stepValStr = "500"
                                            currValStr = "5000"
                                        }
                                        CustomLeverImpactType.LUMP_SUM_OFFSET -> {
                                            minValStr = "-100000"
                                            maxValStr = "200000"
                                            stepValStr = "5000"
                                            currValStr = "25000"
                                        }
                                        CustomLeverImpactType.INTEREST_RATE_DELTA -> {
                                            minValStr = "-2.0"
                                            maxValStr = "3.0"
                                            stepValStr = "0.25"
                                            currValStr = "0.5"
                                        }
                                    }
                                }
                            },
                            label = { Text("${type.label} (${type.unitSuffix})", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = minValStr,
                        onValueChange = { minValStr = it },
                        label = { Text("Min Value") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = maxValStr,
                        onValueChange = { maxValStr = it },
                        label = { Text("Max Value") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = stepValStr,
                        onValueChange = { stepValStr = it },
                        label = { Text("Step Size") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = currValStr,
                        onValueChange = { currValStr = it },
                        label = { Text("Starting Value") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val minV = minValStr.toFloatOrNull() ?: -200f
                        val maxV = (maxValStr.toFloatOrNull() ?: 500f).coerceAtLeast(minV + 1f)
                        val stepV = (stepValStr.toFloatOrNull() ?: 25f).coerceAtLeast(0.05f)
                        val currV = (currValStr.toFloatOrNull() ?: 0f).coerceIn(minV, maxV)
                        onConfirm(title.trim(), selectedType, minV, maxV, stepV, currV)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark)
            ) {
                Text(if (existingLever == null) "Add Lever" else "Save Lever", fontWeight = FontWeight.ExtraBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun WhatIfTrajectoryChart(
    pathASeries: List<Double>,
    pathBSeries: List<Double>,
    horizonYears: Int,
    isPositiveDiff: Boolean
) {
    if (pathASeries.size < 2 || pathBSeries.size < 2) return

    val allValues = pathASeries + pathBSeries
    val minVal = allValues.minOrNull() ?: 0.0
    val maxVal = allValues.maxOrNull() ?: 1.0
    val range = (maxVal - minVal).coerceAtLeast(1000.0)
    val planColor = if (isPositiveDiff) BrandMintGreen else BrandCoralPink

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Wealth Trajectory (Now → Yr $horizonYears)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateGray)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SlateGray))
                        Text("Status Quo", fontSize = 10.sp, color = SlateGray)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(planColor))
                        Text("What-If Plan", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = planColor)
                    }
                }
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                val w = size.width
                val h = size.height
                val padV = 10.dp.toPx()
                val usableH = (h - padV * 2f).coerceAtLeast(10f)
                val count = max(pathASeries.size, pathBeriesSize(pathBSeries)).coerceAtLeast(2)

                fun pointFor(idx: Int, value: Double): Offset {
                    val x = (idx.toFloat() / (count - 1).toFloat()) * w
                    val normY = ((value - minVal) / range).toFloat().coerceIn(0f, 1f)
                    val y = padV + usableH * (1f - normY)
                    return Offset(x, y)
                }

                // Draw Baseline Path A
                val pathA = Path()
                pathASeries.forEachIndexed { idx, v ->
                    val pt = pointFor(idx, v)
                    if (idx == 0) pathA.moveTo(pt.x, pt.y) else pathA.lineTo(pt.x, pt.y)
                }
                drawPath(
                    path = pathA,
                    color = SlateGray.copy(alpha = 0.65f),
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Draw Simulated Path B
                val pathB = Path()
                pathBSeries.forEachIndexed { idx, v ->
                    val pt = pointFor(idx, v)
                    if (idx == 0) pathB.moveTo(pt.x, pt.y) else pathB.lineTo(pt.x, pt.y)
                }
                drawPath(
                    path = pathB,
                    color = planColor,
                    style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // End dots
                val lastA = pointFor(pathASeries.lastIndex, pathASeries.last())
                val lastB = pointFor(pathBSeries.lastIndex, pathBSeries.last())
                drawCircle(color = SlateGray, radius = 4.dp.toPx(), center = lastA)
                drawCircle(color = planColor, radius = 5.dp.toPx(), center = lastB)
            }
        }
    }
}

private fun pathBeriesSize(list: List<Double>): Int = list.size

@Composable
private fun AddCustomMilestoneDialog(
    initialTemplate: AussieMilestoneTemplate? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, MilestoneCategory, Int, Int, Double, Double, Double) -> Unit
) {
    var title by remember { mutableStateOf(initialTemplate?.title ?: "") }
    var selectedCategory by remember { mutableStateOf(initialTemplate?.category ?: MilestoneCategory.PROPERTY) }
    var startYearStr by remember { mutableStateOf((initialTemplate?.startYear ?: 2).toString()) }
    var durationYearsStr by remember { mutableStateOf((initialTemplate?.durationYears ?: 10).toString()) }
    val initWeekly = initialTemplate?.weeklyCashflow ?: 0.0
    var isPositiveWeekly by remember { mutableStateOf(initWeekly >= 0.0) }
    var weeklyCashflowStr by remember { mutableStateOf(abs(initWeekly).toInt().toString()) }
    var assetImpactStr by remember { mutableStateOf((initialTemplate?.assetImpact ?: 0.0).toInt().toString()) }
    var liabilityImpactStr by remember { mutableStateOf((initialTemplate?.liabilityImpact ?: 0.0).toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Life Milestone Event", fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Event Name (e.g. Buy Property, Pay Rise, EV)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Category", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        MilestoneCategory.entries.take(2).forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat.label, fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        MilestoneCategory.entries.drop(2).forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat.label, fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startYearStr,
                        onValueChange = { startYearStr = it },
                        label = { Text("Starts in Year") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = durationYearsStr,
                        onValueChange = { durationYearsStr = it },
                        label = { Text("Duration (Yrs)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("Weekly Cashflow Impact ($/wk)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = !isPositiveWeekly,
                        onClick = { isPositiveWeekly = false },
                        label = { Text("- Expense/Cost", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = isPositiveWeekly,
                        onClick = { isPositiveWeekly = true },
                        label = { Text("+ Income/Saved", fontSize = 11.sp) }
                    )
                }

                OutlinedTextField(
                    value = weeklyCashflowStr,
                    onValueChange = { weeklyCashflowStr = it },
                    label = { Text("Weekly Amount ($/wk)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = assetImpactStr,
                        onValueChange = { assetImpactStr = it },
                        label = { Text("Asset / Cash Δ ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = liabilityImpactStr,
                        onValueChange = { liabilityImpactStr = it },
                        label = { Text("New Loan ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val sy = (startYearStr.toIntOrNull() ?: 1).coerceIn(1, 30)
                        val dy = (durationYearsStr.toIntOrNull() ?: 1).coerceIn(1, 30)
                        val rawWc = abs(weeklyCashflowStr.toDoubleOrNull() ?: 0.0)
                        val wc = if (isPositiveWeekly) rawWc else -rawWc
                        val ai = assetImpactStr.toDoubleOrNull() ?: 0.0
                        val li = (liabilityImpactStr.toDoubleOrNull() ?: 0.0).coerceAtLeast(0.0)
                        onConfirm(title.trim(), selectedCategory, sy, dy, wc, ai, li)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark)
            ) {
                Text("Add to Plan", fontWeight = FontWeight.ExtraBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditCustomMilestoneDialog(
    milestone: LifeMilestoneBlock,
    onDismiss: () -> Unit,
    onConfirm: (String, MilestoneCategory, Int, Int, Double, Double, Double) -> Unit
) {
    var title by remember { mutableStateOf(milestone.title) }
    var selectedCategory by remember { mutableStateOf(milestone.category) }
    var startYearStr by remember { mutableStateOf(milestone.startYear.toString()) }
    var durationYearsStr by remember { mutableStateOf(milestone.durationYears.toString()) }
    var isPositiveWeekly by remember { mutableStateOf(milestone.weeklyCashflowImpact >= 0.0) }
    var weeklyCashflowStr by remember { mutableStateOf(abs(milestone.weeklyCashflowImpact).toInt().toString()) }
    var assetImpactStr by remember { mutableStateOf(milestone.assetImpact.toInt().toString()) }
    var liabilityImpactStr by remember { mutableStateOf(milestone.liabilityImpact.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Life Milestone", fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Milestone Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Category", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        MilestoneCategory.entries.take(2).forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat.label, fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        MilestoneCategory.entries.drop(2).forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat.label, fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startYearStr,
                        onValueChange = { startYearStr = it },
                        label = { Text("Starts in Year") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = durationYearsStr,
                        onValueChange = { durationYearsStr = it },
                        label = { Text("Duration (Yrs)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = !isPositiveWeekly,
                        onClick = { isPositiveWeekly = false },
                        label = { Text("- Expense/Cost", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = isPositiveWeekly,
                        onClick = { isPositiveWeekly = true },
                        label = { Text("+ Income/Saved", fontSize = 11.sp) }
                    )
                }

                OutlinedTextField(
                    value = weeklyCashflowStr,
                    onValueChange = { weeklyCashflowStr = it },
                    label = { Text("Weekly Amount ($/wk)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = assetImpactStr,
                        onValueChange = { assetImpactStr = it },
                        label = { Text("Asset / Cash Δ ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = liabilityImpactStr,
                        onValueChange = { liabilityImpactStr = it },
                        label = { Text("New Loan ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val sy = (startYearStr.toIntOrNull() ?: milestone.startYear).coerceIn(1, 30)
                        val dy = (durationYearsStr.toIntOrNull() ?: milestone.durationYears).coerceIn(1, 30)
                        val rawWc = abs(weeklyCashflowStr.toDoubleOrNull() ?: abs(milestone.weeklyCashflowImpact))
                        val wc = if (isPositiveWeekly) rawWc else -rawWc
                        val ai = assetImpactStr.toDoubleOrNull() ?: milestone.assetImpact
                        val li = (liabilityImpactStr.toDoubleOrNull() ?: milestone.liabilityImpact).coerceAtLeast(0.0)
                        onConfirm(title.trim(), selectedCategory, sy, dy, wc, ai, li)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark)
            ) {
                Text("Save Changes", fontWeight = FontWeight.ExtraBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun LoanDetailRow(label: String, value: String, highlightColor: Color? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, color = SlateGray)
        Text(
            value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = highlightColor ?: MaterialTheme.colorScheme.onSurface
        )
    }
}

// ============================================================================
// TOTAL WEALTH DASHBOARD (MODULAR & CUSTOMIZABLE)
// ============================================================================
@Composable
fun DonutChart(
    data: List<Pair<String, Double>>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    isMasked: Boolean = false
) {
    val total = data.sumOf { it.second }
    if (total <= 0) return

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(190.dp)) {
            val strokeWidth = 28.dp.toPx()
            val inset = strokeWidth / 2f
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)

            var startAngle = -90f
            data.forEachIndexed { index, pair ->
                val sweepAngle = ((pair.second / total) * 360f).toFloat()
                if (sweepAngle > 0f) {
                    val gap = if (data.size > 1) 4f else 0f
                    val actualSweep = max(1f, sweepAngle - gap)

                    drawArc(
                        color = colors.getOrElse(index) { Color.Gray },
                        startAngle = startAngle + (gap / 2f),
                        sweepAngle = actualSweep,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round),
                        topLeft = Offset(inset, inset),
                        size = arcSize
                    )
                    startAngle += sweepAngle
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Total Assets", fontSize = 11.sp, color = SlateGray, fontWeight = FontWeight.Medium)
            Text(
                if (isMasked) "$ ••••••" else formatCurrency(total, 0),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun TotalWealthScreen(
    appConfig: UserAppConfig,
    prop1MarketValue: Double,
    prop2MarketValue: Double,
    sharesList: List<ShareParcel>,
    superAccountsList: List<SuperAccount>,
    otherAssetsList: List<OtherAsset>,
    prop1Loan: LoanConfig,
    prop2Loan: LoanConfig,
    person1Items: List<CashflowItem> = emptyList(),
    person2Items: List<CashflowItem> = emptyList(),
    isWealthHidden: Boolean,
    onNavigateToSettings: () -> Unit,
    onNavigateToWhatIf: () -> Unit = {}
) {
    fun maskVal(valAmt: Double, decimals: Int = 0): String {
        return if (isWealthHidden) "$ ••••••" else formatCurrency(valAmt, decimals)
    }

    var subscriptionLeaks by remember { mutableStateOf<List<SubscriptionLeak>>(emptyList()) }
    var isScanningLeaks by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isScanningLeaks = true
        coroutineScope.launch {
            val allExpensesStr = (person1Items + person2Items).joinToString { "${it.name}: $${abs(it.weeklyAmount)}/wk" }
            subscriptionLeaks = AiAssistantManager.detectSubscriptionLeaks(allExpensesStr)
            isScanningLeaks = false
        }
    }

    val totalPropertyVal = if (appConfig.showProperties) {
        prop1MarketValue + (if (appConfig.hasSecondProperty) prop2MarketValue else 0.0)
    } else 0.0

    val totalSuperVal = if (appConfig.showSuperAndShares) superAccountsList.sumOf { it.balance } else 0.0
    val totalSharesVal = if (appConfig.showSuperAndShares) sharesList.sumOf { it.units * it.currentPrice } else 0.0
    val totalOtherAssetsVal = if (appConfig.showOtherAssets) otherAssetsList.sumOf { it.value } else 0.0
    val totalOffsets = if (appConfig.showLoans) {
        prop1Loan.offsetBalance + (if (appConfig.hasSecondProperty) prop2Loan.offsetBalance else 0.0)
    } else 0.0

    val totalAssets = totalPropertyVal + totalSuperVal + totalSharesVal + totalOtherAssetsVal + totalOffsets

    val p1AutoFortnights = getElapsedFortnightsSinceStart(prop1Loan.startDateStr)
    val p1Schedule = generateAmortizationSchedule(prop1Loan, 780)
    val p1CurrentRow = p1Schedule.getOrNull(p1AutoFortnights.coerceIn(0, p1Schedule.size - 1)) ?: p1Schedule.first()
    val p1LiveBalance = if (appConfig.showLoans) p1CurrentRow.closingBalance else 0.0

    val p2AutoFortnights = getElapsedFortnightsSinceStart(prop2Loan.startDateStr)
    val p2Schedule = generateAmortizationSchedule(prop2Loan, 780)
    val p2CurrentRow = p2Schedule.getOrNull(p2AutoFortnights.coerceIn(0, p2Schedule.size - 1)) ?: p2Schedule.first()
    val p2LiveBalance = if (appConfig.showLoans && appConfig.hasSecondProperty) p2CurrentRow.closingBalance else 0.0

    val totalLiabilities = p1LiveBalance + p2LiveBalance
    val netWorth = totalAssets - totalLiabilities

    val dynamicChartPairs = buildList {
        if (appConfig.showProperties && totalPropertyVal > 0) add(Triple("Properties", totalPropertyVal, BrandCoralPink))
        if (appConfig.showSuperAndShares && totalSuperVal > 0) add(Triple("Superannuation", totalSuperVal, BrandWarmAmber))
        if (appConfig.showSuperAndShares && totalSharesVal > 0) add(Triple("Shares Portfolio", totalSharesVal, BrandSkyBlue))
        if (appConfig.showOtherAssets && totalOtherAssetsVal > 0) add(Triple("Other Assets", totalOtherAssetsVal, BrandPurple))
        if (appConfig.showLoans && totalOffsets > 0) add(Triple("Offset / Cash", totalOffsets, BrandMintGreen))
    }

    val orbSpacing = 16.dp
    val headerTopPad = rememberHeaderAwareTopPadding(orbSpacing)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = headerTopPad, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(orbSpacing)
    ) {
        // COMBINED NET WEALTH CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(Modifier.width(24.dp))
                        Text(
                            if (appConfig.isCoupleMode) "COMBINED NET WEALTH" else "TOTAL NET WEALTH",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BrandCoralPink,
                            letterSpacing = 1.2.sp
                        )
                        Icon(
                            if (isWealthHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = SlateGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    CashflowDirectionAmount(
                        amount = netWorth,
                        suffix = "",
                        decimals = 0,
                        isWealthHidden = isWealthHidden,
                        fontSize = 30
                    )
                    Spacer(Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.NorthEast, contentDescription = "Assets", tint = BrandMintGreen, modifier = Modifier.size(14.dp))
                                Text("Total Tracked Assets", fontSize = 12.sp, color = SlateGray)
                            }
                            Text(maskVal(totalAssets, 0), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BrandMintGreen)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.SouthEast, contentDescription = "Liabilities", tint = BrandCoralPink, modifier = Modifier.size(14.dp))
                                Text("Total Liabilities", fontSize = 12.sp, color = SlateGray)
                            }
                            Text(maskVal(totalLiabilities, 0), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BrandCoralPink)
                        }
                    }
                }
            }
        }

        // WASTED SPEND & SUBSCRIPTION LEAK AUDIT CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = BrandCoralPink, modifier = Modifier.size(20.dp))
                            Text("Wasted Spend & Subscription Audit", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        IconButton(
                            onClick = {
                                isScanningLeaks = true
                                coroutineScope.launch {
                                    val allExpensesStr = (person1Items + person2Items).joinToString { "${it.name}: $${abs(it.weeklyAmount)}/wk" }
                                    subscriptionLeaks = AiAssistantManager.detectSubscriptionLeaks(allExpensesStr)
                                    isScanningLeaks = false
                                }
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Scan Leaks", tint = SlateGray, modifier = Modifier.size(16.dp))
                        }
                    }

                    if (isScanningLeaks) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = BrandCoralPink, strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("AI scanning expenses for subscription leaks...", fontSize = 12.sp, color = SlateGray)
                        }
                    } else if (subscriptionLeaks.isEmpty()) {
                        Text("No hidden subscription leaks or duplicate charges detected. Great job!", fontSize = 12.sp, color = SlateGray)
                    } else {
                        subscriptionLeaks.forEach { leak ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BrandCoralPink.copy(alpha = 0.1f),
                                border = BorderStroke(1.dp, BrandCoralPink.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(leak.merchant, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Est. Annual Impact: ${formatCurrency(leak.annualImpact, 0)}/yr (${formatCurrency(leak.monthlyAmount, 2)}/mo)", fontSize = 11.sp, color = BrandCoralPink)
                                    }
                                    Button(
                                        onClick = {
                                            subscriptionLeaks = subscriptionLeaks.filter { it.merchant != leak.merchant }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Flag / Audit", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }



        if (dynamicChartPairs.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Asset Allocation", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                                if (appConfig.showWhatIf) {
                                    TextButton(onClick = onNavigateToWhatIf) {
                                        Text("Simulate", fontSize = 12.sp, color = BrandMintGreen, fontWeight = FontWeight.Bold)
                                    }
                                }
                                TextButton(onClick = onNavigateToSettings) {
                                    Text("Customize", fontSize = 12.sp, color = BrandCoralPink, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        DonutChart(
                            data = dynamicChartPairs.map { it.first to it.second },
                            colors = dynamicChartPairs.map { it.third },
                            isMasked = isWealthHidden,
                            modifier = Modifier.fillMaxWidth().height(200.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        dynamicChartPairs.forEach { triple ->
                            val pct = if (totalAssets > 0) (triple.second / totalAssets * 100) else 0.0
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(12.dp).background(triple.third, CircleShape))
                                    Spacer(Modifier.width(8.dp))
                                    Text(triple.first, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }
                                Text(
                                    if (isWealthHidden) "•••••• (${String.format(Locale.getDefault(), "%.1f", pct)}%)"
                                    else "${formatCurrency(triple.second, 0)} (${String.format(Locale.getDefault(), "%.1f", pct)}%)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.NorthEast, contentDescription = null, tint = BrandMintGreen, modifier = Modifier.size(18.dp))
                        Text("Tracked Assets Breakdown", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandMintGreen)
                    }
                    if (appConfig.showProperties) {
                        LoanDetailRow("${appConfig.property1Name} (Est. Market)", maskVal(prop1MarketValue, 0))
                        if (appConfig.hasSecondProperty) {
                            LoanDetailRow("${appConfig.property2Name} (Est. Market)", maskVal(prop2MarketValue, 0))
                        }
                    }
                    if (appConfig.showSuperAndShares) {
                        LoanDetailRow("Superannuation Total", maskVal(totalSuperVal, 0))
                        LoanDetailRow("Shares Portfolio", maskVal(totalSharesVal, 0))
                    }
                    if (appConfig.showOtherAssets) {
                        LoanDetailRow("Other Assets", maskVal(totalOtherAssetsVal, 0))
                    }
                    if (appConfig.showLoans) {
                        LoanDetailRow("${appConfig.property1Name} Offset", maskVal(prop1Loan.offsetBalance, 0))
                        if (appConfig.hasSecondProperty) {
                            LoanDetailRow("${appConfig.property2Name} Offset", maskVal(prop2Loan.offsetBalance, 0))
                        }
                    }
                }
            }
        }

        if (appConfig.showLoans) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.SouthEast, contentDescription = null, tint = BrandCoralPink, modifier = Modifier.size(18.dp))
                            Text("Liabilities Breakdown", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandCoralPink)
                        }
                        LoanDetailRow("${appConfig.property1Name} Loan Balance", maskVal(p1LiveBalance, 0), highlightColor = BrandCoralPink)
                        if (appConfig.hasSecondProperty) {
                            LoanDetailRow("${appConfig.property2Name} Loan Balance", maskVal(p2LiveBalance, 0), highlightColor = BrandCoralPink)
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// SUPER & SHARES TRACKING SCREEN (WITH CUSTOM USER COLORS & GAIN/LOSS ARROWS)
// ============================================================================
@Composable
fun SuperAndSharesScreen(
    appConfig: UserAppConfig,
    person1Color: Color,
    person2Color: Color,

    sharesList: List<ShareParcel>,
    superAccountsList: List<SuperAccount>,
    isWealthHidden: Boolean = false,
    onAddShare: (String, String, Double, Double, Double) -> Unit,
    onEditShare: (String, String, String, Double, Double, Double) -> Unit,
    onDeleteShare: (String) -> Unit,
    onUpdateSharesList: (List<ShareParcel>) -> Unit,
    onAddSuperAccount: (String, String, Double) -> Unit,
    onEditSuperAccount: (String, String, String, Double) -> Unit,
    onDeleteSuperAccount: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableIntStateOf(0) }
    var ownerFilter by remember { mutableStateOf("All") }
    var showAddShareDialog by remember { mutableStateOf(false) }
    var shareToEdit by remember { mutableStateOf<ShareParcel?>(null) }
    var showAddSuperDialog by remember { mutableStateOf(false) }
    var superToEdit by remember { mutableStateOf<SuperAccount?>(null) }
    var isSyncing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val prefs = context.getSharedPreferences("expnz_public_wealth_v2", Context.MODE_PRIVATE)
    var lastSyncTime by remember { mutableLongStateOf(prefs.getLong("last_asx_sync", 0L)) }

    LaunchedEffect(selectedCategory) {
        if (selectedCategory == 0) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastSyncTime > 15 * 60 * 1000) { // 15 mins
                if (!isSyncing) {
                    isSyncing = true
                    val updated = sharesList.map { parcel ->
                        val livePrice = fetchAsxLivePrice(parcel.code)
                        if (livePrice != null && livePrice > 0.0) {
                            parcel.copy(currentPrice = livePrice)
                        } else {
                            parcel
                        }
                    }
                    onUpdateSharesList(updated)
                    val newSyncTime = System.currentTimeMillis()
                    prefs.edit { putLong("last_asx_sync", newSyncTime) }
                    lastSyncTime = newSyncTime
                    isSyncing = false
                }
            }
        }
    }

    val ownerFilterOptions = remember(appConfig) {
        if (appConfig.isCoupleMode) listOf("All", appConfig.person1Name, appConfig.person2Name)
        else listOf("All", appConfig.person1Name)
    }

    fun maskVal(amt: Double, decimals: Int = 2): String = if (isWealthHidden) "$ ••••••" else formatCurrency(amt, decimals)

    val filteredShares = if (ownerFilter == "All") sharesList else sharesList.filter { it.owner.equals(ownerFilter, ignoreCase = true) }
    val filteredSuper = if (ownerFilter == "All") superAccountsList else superAccountsList.filter { it.owner.equals(ownerFilter, ignoreCase = true) }

    val totalSharesVal = filteredShares.sumOf { it.units * it.currentPrice }
    val totalCostBasis = filteredShares.sumOf { it.units * it.purchasePrice }
    val totalSharesGainLoss = totalSharesVal - totalCostBasis
    val totalReturnPct = if (totalCostBasis > 0) (totalSharesGainLoss / totalCostBasis) * 100 else 0.0

    val totalSuperVal = filteredSuper.sumOf { it.balance }

    val orbSpacing = 14.dp
    val headerTopPad = rememberHeaderAwareTopPadding(orbSpacing)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = headerTopPad, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(orbSpacing)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CategorySelectCard(
                    title = "Shares Portfolio",
                    subtitle = "ASX Investments",
                    totalValue = totalSharesVal,
                    accent = BrandCoralPink,
                    isSelected = selectedCategory == 0,
                    isWealthHidden = isWealthHidden,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedCategory = 0 }
                )
                CategorySelectCard(
                    title = "Superannuation",
                    subtitle = "Retirement Funds",
                    totalValue = totalSuperVal,
                    accent = BrandWarmAmber,
                    isSelected = selectedCategory == 1,
                    isWealthHidden = isWealthHidden,
                    modifier = Modifier.weight(1f),
                    onClick = { selectedCategory = 1 }
                )
            }
        }

        if (selectedCategory == 0) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            ownerFilterOptions.forEach { filter ->
                                FilterChip(
                                    selected = ownerFilter == filter,
                                    onClick = { ownerFilter = filter },
                                    label = { Text(filter, fontSize = 12.sp) }
                                )
                            }
                        }

                        val lastSyncString = if (lastSyncTime == 0L) "Never" else {
                            val sdf = java.text.SimpleDateFormat("dd MMM HH:mm", Locale.getDefault())
                            sdf.format(java.util.Date(lastSyncTime))
                        }
                        Text("Synced: $lastSyncString", fontSize = 10.sp, color = SlateGray, fontWeight = FontWeight.Bold)
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showAddShareDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Add Share", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        OutlinedButton(
                            onClick = {
                                if (!isSyncing) {
                                    isSyncing = true
                                    scope.launch {
                                        val updated = sharesList.map { parcel ->
                                            val livePrice = fetchAsxLivePrice(parcel.code)
                                            if (livePrice != null && livePrice > 0.0) {
                                                parcel.copy(currentPrice = livePrice)
                                            } else {
                                                parcel
                                            }
                                        }
                                        onUpdateSharesList(updated)
                                        val newTime = System.currentTimeMillis()
                                        prefs.edit { putLong("last_asx_sync", newTime) }
                                        lastSyncTime = newTime
                                        isSyncing = false
                                        Toast.makeText(context, "Updated ASX live prices", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            enabled = !isSyncing,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(Modifier.width(6.dp))
                                Text("Syncing...", fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            } else {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = BrandCoralPink)
                                Spacer(Modifier.width(6.dp))
                                Text("Sync ASX Prices", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandCoralPink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("$ownerFilter Shares Portfolio Total", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandCoralPink)
                        Text(maskVal(totalSharesVal, 2), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                        HorizontalDivider()
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Total Cost Basis", fontSize = 12.sp, color = SlateGray)
                                Text(maskVal(totalCostBasis, 2), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Total Gain / Loss", fontSize = 12.sp, color = SlateGray)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(
                                        if (totalSharesGainLoss >= 0) Icons.Default.NorthEast else Icons.Default.SouthEast,
                                        contentDescription = null,
                                        tint = if (totalSharesGainLoss >= 0) BrandMintGreen else BrandCoralPink,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        if (isWealthHidden) "$ •••••• (••••%)" else "${formatCurrency(totalSharesGainLoss, 2)} (${String.format(Locale.getDefault(), "%.2f", totalReturnPct)}%)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isWealthHidden) MaterialTheme.colorScheme.onSurface else if (totalSharesGainLoss >= 0) BrandMintGreen else BrandCoralPink
                                    )
                                }
                            }
                        }
                    }
                }
            }

            items(filteredShares, key = { it.id }) { parcel ->
                val mktValue = parcel.units * parcel.currentPrice
                val cost = parcel.units * parcel.purchasePrice
                val gain = mktValue - cost
                val gainPct = if (cost > 0) (gain / cost) * 100 else 0.0
                val isPrimaryOwner = parcel.owner.equals(appConfig.person1Name, ignoreCase = true)
                val ownerBadgeColor = if (isPrimaryOwner) person1Color else person2Color

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = BrandCoralPink.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        parcel.code.uppercase(),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = BrandCoralPink
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = ownerBadgeColor.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, ownerBadgeColor.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        parcel.owner,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ownerBadgeColor
                                    )
                                }
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                if (isWealthHidden) "${parcel.units} units @ $ •••••• (Current: $ ••••••)"
                                else "${parcel.units} units @ ${formatCurrency(parcel.purchasePrice, 2)} (Current: ${formatCurrency(parcel.currentPrice, 2)})",
                                fontSize = 12.sp,
                                color = SlateGray
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(maskVal(mktValue, 2), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Icon(
                                    if (gain >= 0) Icons.Default.NorthEast else Icons.Default.SouthEast,
                                    contentDescription = if (gain >= 0) "Gain" else "Loss",
                                    tint = if (gain >= 0) BrandMintGreen else BrandCoralPink,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    if (isWealthHidden) "$ •••••• (••••%)" else "${formatCurrency(gain, 2)} (${String.format(Locale.getDefault(), "%.1f", gainPct)}%)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isWealthHidden) MaterialTheme.colorScheme.onSurface else if (gain >= 0) BrandMintGreen else BrandCoralPink
                                )
                            }
                            Row {
                                IconButton(onClick = { shareToEdit = parcel }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = SlateGray, modifier = Modifier.size(16.dp))
                                }
                                IconButton(onClick = { onDeleteShare(parcel.id) }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = BrandCoralRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        } else {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        ownerFilterOptions.forEach { filter ->
                            FilterChip(
                                selected = ownerFilter == filter,
                                onClick = { ownerFilter = filter },
                                label = { Text(filter) }
                            )
                        }

                    }
                    Button(
                        onClick = { showAddSuperDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandWarmAmber, contentColor = BrandForestDark),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Add Super Account", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("$ownerFilter Superannuation Total", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandWarmAmber)
                        Text(maskVal(totalSuperVal, 0), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            items(filteredSuper, key = { it.id }) { superAcc ->
                val isPrimaryOwner = superAcc.owner.equals(appConfig.person1Name, ignoreCase = true)
                val ownerBadgeColor = if (isPrimaryOwner) person1Color else person2Color

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(ownerBadgeColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Savings, contentDescription = null, tint = ownerBadgeColor, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(superAcc.fundName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = ownerBadgeColor.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, ownerBadgeColor.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        superAcc.owner,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ownerBadgeColor
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(maskVal(superAcc.balance, 0), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(Modifier.width(6.dp))
                            IconButton(onClick = { superToEdit = superAcc }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = SlateGray, modifier = Modifier.size(16.dp))
                            }
                            IconButton(onClick = { onDeleteSuperAccount(superAcc.id) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = BrandCoralRed, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    val ownerChoices = if (appConfig.isCoupleMode) listOf(appConfig.person1Name, appConfig.person2Name) else listOf(appConfig.person1Name)

    if (showAddShareDialog) {
        AddEditShareParcelDialog(
            parcel = null,
            ownerOptions = ownerChoices,
            onDismiss = { showAddShareDialog = false },
            onConfirm = { code, owner, units, pPrice, cPrice ->
                onAddShare(code, owner, units, pPrice, cPrice)
                showAddShareDialog = false
            }
        )
    }

    shareToEdit?.let { parcel ->
        AddEditShareParcelDialog(
            parcel = parcel,
            ownerOptions = ownerChoices,
            onDismiss = { shareToEdit = null },
            onConfirm = { code, owner, units, pPrice, cPrice ->
                onEditShare(parcel.id, code, owner, units, pPrice, cPrice)
                shareToEdit = null
            }
        )
    }

    if (showAddSuperDialog) {
        AddEditSuperDialog(
            superAcc = null,
            ownerOptions = ownerChoices,
            onDismiss = { showAddSuperDialog = false },
            onConfirm = { name, owner, bal ->
                onAddSuperAccount(name, owner, bal)
                showAddSuperDialog = false
            }
        )
    }

    superToEdit?.let { superAcc ->
        AddEditSuperDialog(
            superAcc = superAcc,
            ownerOptions = ownerChoices,
            onDismiss = { superToEdit = null },
            onConfirm = { name, owner, bal ->
                onEditSuperAccount(superAcc.id, name, owner, bal)
                superToEdit = null
            }
        )
    }
}

@Composable
fun CategorySelectCard(
    title: String,
    subtitle: String,
    totalValue: Double,
    accent: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    isWealthHidden: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) accent else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                title,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = if (isSelected) BrandForestDark else MaterialTheme.colorScheme.onSurface
            )
            Text(
                subtitle,
                fontSize = 11.sp,
                color = if (isSelected) BrandForestDark.copy(alpha = 0.8f) else SlateGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isWealthHidden) "$ ••••••" else formatCurrency(totalValue, 0),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = if (isSelected) BrandForestDark else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ============================================================================
// OTHER ASSETS SCREEN (WITH CUSTOM USER COLORS)
// ============================================================================
@Composable
fun OtherAssetsScreen(
    appConfig: UserAppConfig,
    person1Color: Color,
    person2Color: Color,

    otherAssetsList: List<OtherAsset>,
    isWealthHidden: Boolean = false,
    onAddOtherAsset: (String, String, Double) -> Unit,
    onEditOtherAsset: (String, String, String, Double) -> Unit,
    onDeleteOtherAsset: (String) -> Unit
) {
    var ownerFilter by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }
    var assetToEdit by remember { mutableStateOf<OtherAsset?>(null) }

    val ownerFilterOptions = remember(appConfig) {
        if (appConfig.isCoupleMode) listOf("All", appConfig.person1Name, appConfig.person2Name)
        else listOf("All", appConfig.person1Name)
    }

    fun maskVal(amt: Double, decimals: Int = 0): String = if (isWealthHidden) "$ ••••••" else formatCurrency(amt, decimals)

    val filteredList = if (ownerFilter == "All") otherAssetsList else otherAssetsList.filter { it.owner.equals(ownerFilter, ignoreCase = true) }

    val totalValue = filteredList.sumOf { it.value }
    val p1Total = otherAssetsList.filter { it.owner.equals(appConfig.person1Name, ignoreCase = true) }.sumOf { it.value }
    val p2Total = otherAssetsList.filter { it.owner.equals(appConfig.person2Name, ignoreCase = true) }.sumOf { it.value }

    val orbSpacing = 16.dp
    val headerTopPad = rememberHeaderAwareTopPadding(orbSpacing)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = headerTopPad, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(orbSpacing)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    ownerFilterOptions.forEach { filter ->
                        FilterChip(
                            selected = ownerFilter == filter,
                            onClick = { ownerFilter = filter },
                            label = { Text(filter) }
                        )
                    }

                }
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add Asset", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("$ownerFilter Other Assets Total", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandCoralPink)
                    Text(maskVal(totalValue, 0), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    if (appConfig.isCoupleMode) {
                        HorizontalDivider()
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("${appConfig.person1Name}'s Assets", fontSize = 12.sp, color = SlateGray)
                                Text(maskVal(p1Total, 0), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = person1Color)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${appConfig.person2Name}'s Assets", fontSize = 12.sp, color = SlateGray)
                                Text(maskVal(p2Total, 0), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = person2Color)
                            }
                        }
                    }
                }
            }
        }

        items(filteredList, key = { it.id }) { asset ->
            val isPrimaryOwner = asset.owner.equals(appConfig.person1Name, ignoreCase = true)
            val ownerBadgeColor = if (isPrimaryOwner) person1Color else person2Color

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(ownerBadgeColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (asset.name.contains("Car", ignoreCase = true) || asset.name.contains("Vehicle", ignoreCase = true))
                                    Icons.Default.DirectionsCar else Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = ownerBadgeColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(asset.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = ownerBadgeColor.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, ownerBadgeColor.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    asset.owner,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ownerBadgeColor
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(maskVal(asset.value, 0), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.width(6.dp))
                        IconButton(onClick = { assetToEdit = asset }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = SlateGray, modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = { onDeleteOtherAsset(asset.id) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = BrandCoralRed, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }

    val ownerChoices = if (appConfig.isCoupleMode) listOf(appConfig.person1Name, appConfig.person2Name) else listOf(appConfig.person1Name)

    if (showAddDialog) {
        AddEditOtherAssetDialog(
            asset = null,
            ownerOptions = ownerChoices,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, owner, valAmt ->
                onAddOtherAsset(name, owner, valAmt)
                showAddDialog = false
            }
        )
    }

    assetToEdit?.let { asset ->
        AddEditOtherAssetDialog(
            asset = asset,
            ownerOptions = ownerChoices,
            onDismiss = { assetToEdit = null },
            onConfirm = { name, owner, valAmt ->
                onEditOtherAsset(asset.id, name, owner, valAmt)
                assetToEdit = null
            }
        )
    }
}

// ============================================================================
// SHARED COMPONENTS & DIALOGS (WITH COLOR-BLIND IN/OUT ARROWS)
// ============================================================================
@Composable
fun ExpenseItemCard(
    item: CashflowItem,
    accentColor: Color,
    isWealthHidden: Boolean = false,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                val iconVector = when (item.iconName) {
                    "Home" -> Icons.Default.Home
                    "Food" -> Icons.Default.Restaurant
                    "Car" -> Icons.Default.DirectionsCar
                    "Shopping" -> Icons.Default.ShoppingCart
                    "Health" -> Icons.Default.Favorite
                    "Utility" -> Icons.Default.Bolt
                    "Entertainment" -> Icons.Default.Movie
                    else -> Icons.Default.AttachMoney
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(iconVector, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!item.customSubtitle.isNullOrBlank()) {
                        Text(
                            text = item.customSubtitle,
                            fontSize = 11.sp,
                            color = SlateGray,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else if (item.isLockedMortgageOrPropertyLink) {
                        Text(
                            text = "Auto-synced across Property & Loan modules",
                            fontSize = 11.sp,
                            color = SlateGray
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                CashflowDirectionAmount(
                    amount = item.weeklyAmount,
                    suffix = "/wk",
                    decimals = 2,
                    isWealthHidden = isWealthHidden,
                    fontSize = 14
                )
                if (onEdit != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = SlateGray, modifier = Modifier.size(17.dp))
                    }
                }
                if (onDelete != null) {
                    Spacer(modifier = Modifier.width(2.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = SlateGray, modifier = Modifier.size(17.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AddCashflowDialog(
    targetTitle: String,
    initialItem: CashflowItem? = null,
    defaultIsExpense: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String) -> Unit
) {
    var name by remember(initialItem) { mutableStateOf(initialItem?.name ?: "") }
    var amountStr by remember(initialItem) { mutableStateOf(initialItem?.weeklyAmount?.let { abs(it).toString() } ?: "") }
    var isExpense by remember(initialItem) { mutableStateOf(initialItem?.let { it.weeklyAmount < 0 } ?: defaultIsExpense) }
    var frequency by remember { mutableIntStateOf(0) }

    val availableIcons = listOf(
        "Default" to Icons.Default.AttachMoney,
        "Home" to Icons.Default.Home,
        "Food" to Icons.Default.Restaurant,
        "Car" to Icons.Default.DirectionsCar,
        "Shopping" to Icons.Default.ShoppingCart,
        "Health" to Icons.Default.Favorite,
        "Utility" to Icons.Default.Bolt,
        "Entertainment" to Icons.Default.Movie
    )
    var selectedIcon by remember(initialItem) { mutableStateOf(initialItem?.iconName ?: availableIcons[0].first) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(targetTitle, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Description (e.g. Strata, Gym, Rent)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(
                        selected = isExpense,
                        onClick = { isExpense = true },
                        leadingIcon = { Icon(Icons.Default.SouthEast, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        label = { Text("Expense Out (-)") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = !isExpense,
                        onClick = { isExpense = false },
                        leadingIcon = { Icon(Icons.Default.NorthEast, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        label = { Text("Income In (+)") }
                    )
                }
                Text("Frequency (auto-converts to Weekly):", fontSize = 12.sp, color = SlateGray)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val frequencies = listOf("Weekly", "Fortnight", "Monthly", "Quarterly", "Yearly")
                    itemsIndexed(frequencies) { index, label ->
                        FilterChip(
                            selected = frequency == index,
                            onClick = { frequency = index },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Select Icon:", fontSize = 12.sp, color = SlateGray)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(availableIcons) { (iconName, iconVector) ->
                        FilterChip(
                            selected = selectedIcon == iconName,
                            onClick = { selectedIcon = iconName },
                            label = { Icon(iconVector, contentDescription = iconName, modifier = Modifier.size(18.dp)) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val raw = abs(amountStr.toDoubleOrNull() ?: 0.0)
                    if (name.isNotBlank() && raw > 0.0) {
                        val weeklyVal = when (frequency) {
                            1 -> raw / 2.0
                            2 -> (raw * 12.0) / 52.0
                            3 -> (raw * 4.0) / 52.0
                            4 -> raw / 52.0
                            else -> raw
                        }
                        onConfirm(name.trim(), if (isExpense) -weeklyVal else weeklyVal, selectedIcon)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark)
            ) {
                Text("Save Item", fontWeight = FontWeight.ExtraBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditSingleValueDialog(
    title: String,
    label: String,
    initialValue: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var textVal by remember { mutableStateOf(initialValue.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = textVal,
                onValueChange = { textVal = it },
                label = { Text(label) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val v = textVal.toDoubleOrNull()
                    if (v != null && v >= 0) onConfirm(v)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark)
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditLoanConfigDialog(
    loan: LoanConfig,
    onDismiss: () -> Unit,
    onSave: (LoanConfig) -> Unit
) {
    var nameStr by remember { mutableStateOf(loan.name) }
    var startDateStr by remember { mutableStateOf(loan.startDateStr) }
    var rateStr by remember { mutableStateOf(loan.interestRatePa.toString()) }
    var startBalStr by remember { mutableStateOf(loan.startLoanBalance.toInt().toString()) }
    var offsetStr by remember { mutableStateOf(loan.offsetBalance.toInt().toString()) }
    var origLoanStr by remember { mutableStateOf(loan.originalLoanAmount.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update ${loan.name} Loan", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Updating the interest rate or balance automatically recalculates your fortnightly mortgage repayment and schedule.",
                    fontSize = 12.sp,
                    color = SlateGray
                )
                OutlinedTextField(
                    value = nameStr,
                    onValueChange = { nameStr = it },
                    label = { Text("Loan / Property Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = rateStr,
                    onValueChange = { rateStr = it },
                    label = { Text("Interest Rate (% p.a.)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = offsetStr,
                    onValueChange = { offsetStr = it },
                    label = { Text("Offset Account Balance ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = startBalStr,
                    onValueChange = { startBalStr = it },
                    label = { Text("Schedule Start Loan Balance ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = origLoanStr,
                    onValueChange = { origLoanStr = it },
                    label = { Text("Original 30-Yr Loan Amount ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = startDateStr,
                    onValueChange = { startDateStr = it },
                    label = { Text("Schedule Start Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newRate = rateStr.toDoubleOrNull() ?: loan.interestRatePa
                    val newOffset = offsetStr.toDoubleOrNull() ?: loan.offsetBalance
                    val newStartBal = startBalStr.toDoubleOrNull() ?: loan.startLoanBalance
                    val newOrig = origLoanStr.toDoubleOrNull() ?: loan.originalLoanAmount
                    onSave(
                        loan.copy(
                            name = nameStr.trim().ifBlank { loan.name },
                            startDateStr = startDateStr.trim().ifBlank { loan.startDateStr },
                            interestRatePa = newRate,
                            offsetBalance = newOffset,
                            startLoanBalance = newStartBal,
                            originalLoanAmount = newOrig
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark)
            ) {
                Text("Update Loan", fontWeight = FontWeight.ExtraBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddEditSuperDialog(
    superAcc: SuperAccount?,
    ownerOptions: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double) -> Unit
) {
    var fundName by remember { mutableStateOf(superAcc?.fundName ?: "") }
    var owner by remember { mutableStateOf(superAcc?.owner ?: ownerOptions.first()) }
    var balStr by remember { mutableStateOf(superAcc?.balance?.toInt()?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (superAcc == null) "Add Superannuation Fund" else "Edit Super Fund", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = fundName,
                    onValueChange = { fundName = it },
                    label = { Text("Fund Name (e.g. Hostplus, AustralianSuper)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (ownerOptions.size > 1) {
                    Text("Owner:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ownerOptions.forEach { option ->
                            FilterChip(selected = owner == option, onClick = { owner = option }, label = { Text(option) })
                        }
                    }
                }

                OutlinedTextField(
                    value = balStr,
                    onValueChange = { balStr = it },
                    label = { Text("Super Balance ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val v = balStr.toDoubleOrNull() ?: 0.0
                    if (fundName.isNotBlank() && v >= 0) {
                        onConfirm(fundName.trim(), owner, v)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark)
            ) {
                Text("Save Super Fund", fontWeight = FontWeight.ExtraBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddEditShareParcelDialog(
    parcel: ShareParcel?,
    ownerOptions: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, Double, Double) -> Unit
) {
    var code by remember { mutableStateOf(parcel?.code ?: "VAS") }
    var owner by remember { mutableStateOf(parcel?.owner ?: ownerOptions.first()) }
    var unitsStr by remember { mutableStateOf(parcel?.units?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "") }
    var pPriceStr by remember { mutableStateOf(parcel?.purchasePrice?.toString() ?: "") }
    var cPriceStr by remember { mutableStateOf(parcel?.currentPrice?.toString() ?: "") }
    var isFetchingPrice by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun fetchPriceForTicker(ticker: String) {
        if (ticker.isBlank()) return
        isFetchingPrice = true
        scope.launch {
            val price = fetchAsxLivePrice(ticker)
            if (price != null && price > 0.0) {
                cPriceStr = String.format(Locale.getDefault(), "%.2f", price)
                if (pPriceStr.isBlank()) {
                    pPriceStr = cPriceStr
                }
            }
            isFetchingPrice = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (parcel == null) "Add Share Parcel" else "Edit Share Parcel", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it.uppercase() },
                        label = { Text("ASX Ticker (e.g. VAS, IVV, BHP)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedButton(
                        onClick = { fetchPriceForTicker(code) },
                        enabled = !isFetchingPrice && code.isNotBlank(),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isFetchingPrice) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Fetch Live", fontSize = 11.sp, color = BrandCoralPink)
                        }
                    }
                }

                if (ownerOptions.size > 1) {
                    Text("Owner:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ownerOptions.forEach { option ->
                            FilterChip(selected = owner == option, onClick = { owner = option }, label = { Text(option) })
                        }
                    }
                }

                OutlinedTextField(
                    value = unitsStr,
                    onValueChange = { unitsStr = it },
                    label = { Text("Number of Units / Shares") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = pPriceStr,
                    onValueChange = { pPriceStr = it },
                    label = { Text("Purchase Price per Share ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = cPriceStr,
                    onValueChange = { cPriceStr = it },
                    label = { Text("Current Price per Share ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val u = unitsStr.toDoubleOrNull() ?: 0.0
                    val pp = pPriceStr.toDoubleOrNull() ?: 0.0
                    val cp = cPriceStr.toDoubleOrNull() ?: pp
                    if (code.isNotBlank() && u > 0) {
                        onConfirm(code.trim().uppercase(), owner, u, pp, cp)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark)
            ) {
                Text("Save Parcel", fontWeight = FontWeight.ExtraBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddEditOtherAssetDialog(
    asset: OtherAsset?,
    ownerOptions: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double) -> Unit
) {
    var name by remember { mutableStateOf(asset?.name ?: "") }
    var owner by remember { mutableStateOf(asset?.owner ?: ownerOptions.first()) }
    var valStr by remember { mutableStateOf(asset?.value?.toInt()?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (asset == null) "Add Other Asset" else "Edit Asset", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Asset Name (e.g. SUV Car, Emergency Savings)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (ownerOptions.size > 1) {
                    Text("Owner:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ownerOptions.forEach { option ->
                            FilterChip(selected = owner == option, onClick = { owner = option }, label = { Text(option) })
                        }
                    }
                }

                OutlinedTextField(
                    value = valStr,
                    onValueChange = { valStr = it },
                    label = { Text("Current Asset Value ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val v = valStr.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && v >= 0) {
                        onConfirm(name.trim(), owner, v)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark)
            ) {
                Text("Save Asset", fontWeight = FontWeight.ExtraBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditAddressDialog(
    initialAddress: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var valStr by remember { mutableStateOf(initialAddress) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Property Address", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = valStr,
                onValueChange = { valStr = it },
                label = { Text("Property Address") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { if (valStr.isNotBlank()) onConfirm(valStr.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark)
            ) {
                Text("Save Address", fontWeight = FontWeight.ExtraBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ManagementFeePercentageDialog(
    weeklyRent: Double,
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var percentStr by remember { mutableStateOf("5.0") }
    val pct = percentStr.toDoubleOrNull() ?: 5.0
    val calculatedWeeklyFee = weeklyRent * (pct / 100.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Property Management Fee", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Weekly Gross Rent: ${formatCurrency(weeklyRent, 2)}/wk", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = BrandMintGreen)
                OutlinedTextField(
                    value = percentStr,
                    onValueChange = { percentStr = it },
                    label = { Text("Management Fee Percentage (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Calculated Fee: ${formatCurrency(calculatedWeeklyFee, 2)}/wk (${pct}% of rent)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandCoralPink)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (pct > 0) {
                        onConfirm("Property Management Fee (${pct}%)", -calculatedWeeklyFee)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark)
            ) {
                Text("Add Fee", fontWeight = FontWeight.ExtraBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

fun formatCurrency(value: Double, decimals: Int = 2): String {
    val sign = if (value < 0) "-" else if (value > 0) "+" else ""
    val absVal = abs(value)
    return if (decimals == 0) {
        val formatted = String.format(Locale.getDefault(), "%,.0f", absVal)
        "${if (value < 0) "-" else ""}$$formatted"
    } else {
        val formatted = String.format(Locale.getDefault(), "%,.${decimals}f", absVal)
        "$sign$$formatted"
    }
}

// ============================================================================
// ASX LIVE PRICE LOOKUP ENGINE
// ============================================================================
val sampleAsxLivePrices = mapOf(
    "VAS" to 108.57,
    "IVV" to 524.30,
    "NDQ" to 43.50,
    "BHP" to 43.80,
    "CBA" to 144.20,
    "CSL" to 298.50,
    "WES" to 71.20,
    "NAB" to 38.40,
    "WBC" to 30.80,
    "ANZ" to 31.50,
    "MQG" to 225.00,
    "TLS" to 3.92,
    "WOW" to 31.40,
    "WDS" to 28.60,
    "RIO" to 122.50,
    "PLS" to 3.15,
    "FMG" to 19.80,
    "STW" to 75.80,
    "VGS" to 128.90,
    "A200" to 134.50,
    "DHHF" to 33.20,
    "JBH" to 82.50,
    "HLI" to 3.80
)

suspend fun fetchAsxLivePrice(ticker: String): Double? = withContext(Dispatchers.IO) {
    val cleanCode = ticker.trim().uppercase().replace(".AX", "")
    if (cleanCode.isBlank()) return@withContext null

    try {
        val url = URL("https://www.asx.com.au/asx/1/share/$cleanCode")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 6000
            readTimeout = 6000
            setRequestProperty("User-Agent", "Mozilla/5.0")
            setRequestProperty("Accept", "application/json")
        }
        if (conn.responseCode == 200) {
            val responseText = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(responseText)
            val lastPrice = json.optDouble("last_price", Double.NaN)
            if (!lastPrice.isNaN() && lastPrice > 0.0) return@withContext lastPrice
        }
    } catch (_: Exception) {}

    val symbol = "$cleanCode.AX"
    try {
        val url = URL("https://query1.finance.yahoo.com/v8/finance/chart/$symbol?interval=1d&range=1d")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 6000
            readTimeout = 6000
            setRequestProperty("User-Agent", "Mozilla/5.0")
            setRequestProperty("Accept", "application/json")
        }
        if (conn.responseCode == 200) {
            val responseText = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(responseText)
            val result = json.optJSONObject("chart")?.optJSONArray("result")
            if (result != null && result.length() > 0) {
                val meta = result.getJSONObject(0).optJSONObject("meta")
                val regPrice = meta?.optDouble("regularMarketPrice", Double.NaN)
                if (regPrice != null && !regPrice.isNaN() && regPrice > 0.0) return@withContext regPrice
            }
        }
    } catch (_: Exception) {}

    return@withContext sampleAsxLivePrices[cleanCode]
}

@Composable
fun FloatingAiAssistantWidget(
    financialContextSummary: String,
    personName: String,
    messages: MutableList<ChatMessage>,
    onAddImportedShares: (List<ShareParcel>) -> Unit,
    onExecuteAiActions: (String) -> Pair<String, List<String>> = { it to emptyList() }
) {
    var isExpanded by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var attachedBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var attachedFileContent by remember { mutableStateOf<String?>(null) }
    var attachedFileName by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isSpeechEnabled by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
            }
        }
        tts = textToSpeech
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    // 1. Media Gallery Image Picker
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                attachedBitmap = bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Could not load selected image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 2. Camera Capture Launcher + Runtime Permission
    val cameraCaptureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            attachedBitmap = bitmap
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                cameraCaptureLauncher.launch(null)
            } catch (_: Exception) {
                Toast.makeText(context, "Camera unavailable on this device", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Camera permission is required to capture receipts or statements", Toast.LENGTH_SHORT).show()
        }
    }

    // 3. Document / File Upload Launcher (CSV, TXT, JSON, Statements)
    val filePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                var displayName = "uploaded_file"
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0 && cursor.moveToFirst()) {
                        displayName = cursor.getString(nameIndex) ?: "uploaded_file"
                    }
                }
                val content = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { reader ->
                    reader.readText().take(25_000)
                }
                if (!content.isNullOrBlank()) {
                    attachedFileName = displayName
                    attachedFileContent = content
                } else {
                    Toast.makeText(context, "Attached file ($displayName)", Toast.LENGTH_SHORT).show()
                    attachedFileName = displayName
                    attachedFileContent = "[Binary or non-text file attached: $displayName]"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Unable to read selected file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 4. Voice Input Speech Recognizer + Runtime Permission
    val speechRecognizerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                inputText = spokenText
            }
        }
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to your EXPNZ AI Assistant...")
            }
            try {
                speechRecognizerLauncher.launch(intent)
            } catch (_: Exception) {
                Toast.makeText(context, "Speech recognition not available", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Microphone permission is required for voice input", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendCurrentMessage(customPrompt: String? = null) {
        val rawPrompt = customPrompt ?: inputText.trim()
        if ((rawPrompt.isNotBlank() || attachedBitmap != null || attachedFileContent != null) && !isLoading) {
            val question = rawPrompt.ifBlank {
                when {
                    attachedBitmap != null -> "Analyze this attached photo/screenshot and update my financial data if applicable."
                    attachedFileName != null -> "Analyze my uploaded file ($attachedFileName) and summarize or import any relevant items."
                    else -> "Review my financial plan."
                }
            }
            if (customPrompt == null) inputText = ""

            val bitmapToSend = attachedBitmap
            val fileContentToSend = attachedFileContent
            val fileNameToSend = attachedFileName
            attachedBitmap = null
            attachedFileContent = null
            attachedFileName = null

            messages.add(
                ChatMessage(
                    text = question,
                    isFromUser = true,
                    attachedBitmap = bitmapToSend,
                    attachedFileName = fileNameToSend
                )
            )
            isLoading = true

            coroutineScope.launch {
                val appliedActions = mutableListOf<String>()
                var finalReply: String

                val wantsShareImport = bitmapToSend != null &&
                        (question.contains("share", ignoreCase = true) ||
                                question.contains("pearler", ignoreCase = true) ||
                                question.contains("stock", ignoreCase = true) ||
                                question.contains("portfolio", ignoreCase = true))

                if (wantsShareImport) {
                    val importedItems = AiAssistantManager.parsePearlerScreenshotWithAi(bitmapToSend)
                    if (importedItems.isNotEmpty()) {
                        val newParcels = importedItems.map { item ->
                            ShareParcel(
                                id = "imported_${System.currentTimeMillis()}_${item.ticker}",
                                code = item.ticker,
                                owner = personName,
                                units = item.quantity,
                                purchasePrice = item.averagePrice,
                                currentPrice = if (item.averagePrice > 0.0) item.averagePrice else 10.0
                            )
                        }
                        onAddImportedShares(newParcels)
                        appliedActions.add("Imported ${importedItems.size} share parcels (${importedItems.joinToString { it.ticker }})")
                        finalReply = "I extracted ${importedItems.size} share parcels (${importedItems.joinToString { it.ticker }}) from your image and added them to your Shares tab."
                    } else {
                        val rawAi = AiAssistantManager.askGeminiAssistant(
                            prompt = question,
                            contextSummary = financialContextSummary,
                            bitmap = bitmapToSend,
                            fileContent = fileContentToSend,
                            fileName = fileNameToSend
                        )
                        val (cleaned, executed) = onExecuteAiActions(rawAi)
                        finalReply = cleaned
                        appliedActions.addAll(executed)
                    }
                } else {
                    val rawAi = AiAssistantManager.askGeminiAssistant(
                        prompt = question,
                        contextSummary = financialContextSummary,
                        bitmap = bitmapToSend,
                        fileContent = fileContentToSend,
                        fileName = fileNameToSend
                    )
                    val (cleaned, executed) = onExecuteAiActions(rawAi)
                    finalReply = cleaned
                    appliedActions.addAll(executed)
                }

                messages.add(
                    ChatMessage(
                        text = finalReply,
                        isFromUser = false,
                        appliedActions = appliedActions
                    )
                )
                if (isSpeechEnabled) {
                    tts?.speak(finalReply, TextToSpeech.QUEUE_FLUSH, null, null)
                }
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 104.dp, end = 16.dp, start = 16.dp, top = 16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        if (!isExpanded) {
            // Draggable Floating 3D Liquid Glass Orb Chatbot Button (no internal square box)
            Surface(
                onClick = { isExpanded = true },
                shape = CircleShape,
                color = Color.Transparent,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                modifier = Modifier
                    .size(60.dp)
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .liquidGlass(
                        shape = CircleShape,
                        tintColor = BrandForestDark,
                        intensity = 1.30f,
                        accentRimColor = BrandCoralPink,
                        role = LiquidGlassRole.STATIC
                    )
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = "Open EXPNZ AI Chatbot",
                        tint = BrandCoralPink,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        } else {
            // Professional Full-Featured AI Chatbot Window — Solid Opaque Background (Non-Transparent)
            val isDark = LocalIsDarkTheme.current
            val aiPanelShape = RoundedCornerShape(24.dp)
            val solidAiBg = if (isDark) BrandForestSurface else Color(0xFFF6FAF8)
            val solidAiSubSurface = if (isDark) BrandForestVariant else Color(0xFFE3EFEA)
            val solidAiTextColor = if (isDark) Color.White else BrandForestDark

            Surface(
                shape = aiPanelShape,
                color = solidAiBg,
                tonalElevation = 0.dp,
                shadowElevation = 18.dp,
                border = BorderStroke(1.5.dp, BrandCoralPink.copy(alpha = 0.55f)),
                modifier = Modifier
                    .widthIn(min = 320.dp, max = 400.dp)
                    .fillMaxWidth()
                    .height(580.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(solidAiBg)
                ) {
                    // 1. Professional Chatbot Header (Solid BrandForestDark)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BrandForestDark)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = BrandCoralPink,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = BrandForestDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    "EXPNZ AI Copilot",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(BrandMintGreen)
                                    )
                                    Text(
                                        "Live Read & Write Access • Current + 30Y Data",
                                        color = BrandMintGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            IconButton(
                                onClick = { isSpeechEnabled = !isSpeechEnabled },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    if (isSpeechEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                                    contentDescription = "Toggle Voice Readout",
                                    tint = if (isSpeechEnabled) BrandMintGreen else SlateGray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    messages.clear()
                                    messages.add(
                                        ChatMessage(
                                            "Chat reset. I have full live read and write access to all your current tabs and 30-year projections. Ask me anything or tell me what to update!",
                                            false
                                        )
                                    )
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Reset Chat",
                                    tint = SlateGray,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            IconButton(
                                onClick = { isExpanded = false },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Minimize",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // 2. Quick-Action Suggestion Chips
                    val quickPrompts = listOf(
                        "Audit my current & 10Y wealth",
                        "Add $120/wk Groceries expense",
                        "Simulate -0.50% RBA rate cut",
                        "Compare Offset vs Super boost"
                    )
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(solidAiSubSurface)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(quickPrompts) { qp ->
                            SuggestionChip(
                                onClick = { sendCurrentMessage(qp) },
                                label = { Text(qp, fontSize = 10.sp) }
                            )
                        }
                    }

                    // 3. Chat Messages Area
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(solidAiBg)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(messages) { msg ->
                            val isUser = msg.isFromUser
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                            ) {
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (!isUser) {
                                        Surface(
                                            shape = CircleShape,
                                            color = BrandForestDark,
                                            border = BorderStroke(1.dp, BrandCoralPink),
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.AutoAwesome,
                                                    contentDescription = null,
                                                    tint = BrandCoralPink,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            }
                                        }
                                        Spacer(Modifier.width(6.dp))
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isUser) 16.dp else 4.dp,
                                            bottomEnd = if (isUser) 4.dp else 16.dp
                                        ),
                                        color = if (isUser) BrandCoralPink else solidAiSubSurface,
                                        border = if (!isUser) BorderStroke(1.dp, SlateGray.copy(alpha = 0.25f)) else null,
                                        modifier = Modifier.widthIn(max = 285.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(11.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            if (msg.attachedBitmap != null) {
                                                Image(
                                                    bitmap = msg.attachedBitmap.asImageBitmap(),
                                                    contentDescription = "Attached Image",
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .heightIn(max = 130.dp)
                                                        .clip(RoundedCornerShape(10.dp)),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                            if (!msg.attachedFileName.isNullOrBlank()) {
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = BrandForestDark.copy(alpha = 0.15f)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                    ) {
                                                        Icon(
                                                            Icons.Default.AttachFile,
                                                            contentDescription = null,
                                                            tint = if (isUser) BrandForestDark else BrandSkyBlue,
                                                            modifier = Modifier.size(13.dp)
                                                        )
                                                        Text(
                                                            msg.attachedFileName,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isUser) BrandForestDark else solidAiTextColor
                                                        )
                                                    }
                                                }
                                            }
                                            Text(
                                                text = msg.text,
                                                color = if (isUser) BrandForestDark else solidAiTextColor,
                                                fontSize = 12.sp,
                                                lineHeight = 17.sp
                                            )
                                            if (msg.appliedActions.isNotEmpty()) {
                                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    msg.appliedActions.forEach { act ->
                                                        Surface(
                                                            shape = RoundedCornerShape(8.dp),
                                                            color = BrandMintGreen.copy(alpha = 0.18f),
                                                            border = BorderStroke(1.dp, BrandMintGreen.copy(alpha = 0.6f))
                                                        ) {
                                                            Row(
                                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                            ) {
                                                                Icon(
                                                                    Icons.Default.CheckCircle,
                                                                    contentDescription = null,
                                                                    tint = BrandMintGreen,
                                                                    modifier = Modifier.size(12.dp)
                                                                )
                                                                Text(
                                                                    text = act,
                                                                    fontSize = 10.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = BrandMintGreen
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            Text(
                                                text = msg.timestamp,
                                                fontSize = 9.sp,
                                                color = if (isUser) BrandForestDark.copy(alpha = 0.65f) else SlateGray
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (isLoading) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        border = BorderStroke(1.dp, BrandCoralPink.copy(alpha = 0.35f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(14.dp),
                                                color = BrandCoralPink,
                                                strokeWidth = 2.dp
                                            )
                                            Text(
                                                "EXPNZ AI is analyzing & updating...",
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 4. Staged Attachment Preview Bar (Photo / Camera / File)
                    if (attachedBitmap != null || attachedFileName != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = BrandForestDark,
                            border = BorderStroke(1.dp, BrandMintGreen.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    if (attachedBitmap != null) {
                                        Image(
                                            bitmap = attachedBitmap!!.asImageBitmap(),
                                            contentDescription = "Preview",
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(RoundedCornerShape(6.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            Icons.Default.AttachFile,
                                            contentDescription = null,
                                            tint = BrandSkyBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = when {
                                                attachedBitmap != null && attachedFileName != null -> "Image + $attachedFileName"
                                                attachedBitmap != null -> "Photo / Screenshot Attached"
                                                else -> attachedFileName ?: "File Attached"
                                            },
                                            fontSize = 11.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Ready to send to AI",
                                            fontSize = 9.sp,
                                            color = BrandMintGreen
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        attachedBitmap = null
                                        attachedFileContent = null
                                        attachedFileName = null
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove Attachment",
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 5. Non-Overlapping 2-Row Composer (Toolbar Row + Text Input Row) — Solid Background
                    Surface(
                        color = solidAiSubSurface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Row 1: Distinct, Spaced Action Buttons (Camera, Media, File, Voice) — Zero Overlap!
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Camera Button (with Runtime Permission Check)
                                Surface(
                                    onClick = {
                                        if (ContextCompat.checkSelfPermission(
                                                context,
                                                android.Manifest.permission.CAMERA
                                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                        ) {
                                            try {
                                                cameraCaptureLauncher.launch(null)
                                            } catch (_: Exception) {
                                                Toast.makeText(context, "Camera unavailable", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = BrandWarmAmber.copy(alpha = 0.16f),
                                    border = BorderStroke(1.dp, BrandWarmAmber.copy(alpha = 0.45f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.PhotoCamera,
                                            contentDescription = "Camera",
                                            tint = BrandWarmAmber,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text("Camera", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandWarmAmber)
                                    }
                                }

                                // Media Gallery Button
                                Surface(
                                    onClick = { imagePickerLauncher.launch("image/*") },
                                    shape = RoundedCornerShape(10.dp),
                                    color = BrandMintGreen.copy(alpha = 0.16f),
                                    border = BorderStroke(1.dp, BrandMintGreen.copy(alpha = 0.45f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.AddPhotoAlternate,
                                            contentDescription = "Media Gallery",
                                            tint = BrandMintGreen,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text("Media", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandMintGreen)
                                    }
                                }

                                // File Upload Button
                                Surface(
                                    onClick = {
                                        filePickerLauncher.launch(
                                            arrayOf("text/*", "application/json", "text/csv", "text/comma-separated-values", "*/*")
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = BrandSkyBlue.copy(alpha = 0.16f),
                                    border = BorderStroke(1.dp, BrandSkyBlue.copy(alpha = 0.45f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.AttachFile,
                                            contentDescription = "Upload File",
                                            tint = BrandSkyBlue,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text("File", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandSkyBlue)
                                    }
                                }

                                // Voice Mic Button
                                Surface(
                                    onClick = {
                                        if (ContextCompat.checkSelfPermission(
                                                context,
                                                android.Manifest.permission.RECORD_AUDIO
                                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                        ) {
                                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to your EXPNZ AI Assistant...")
                                            }
                                            try {
                                                speechRecognizerLauncher.launch(intent)
                                            } catch (_: Exception) {
                                                Toast.makeText(context, "Speech recognition not available", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            audioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = BrandCoralPink.copy(alpha = 0.16f),
                                    border = BorderStroke(1.dp, BrandCoralPink.copy(alpha = 0.45f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Mic,
                                            contentDescription = "Voice Input",
                                            tint = BrandCoralPink,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text("Voice", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandCoralPink)
                                    }
                                }
                            }

                            // Row 2: Text Input & Send Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = inputText,
                                    onValueChange = { inputText = it },
                                    placeholder = {
                                        Text(
                                            "Ask AI or say 'Update my income to $1800/wk'...",
                                            fontSize = 11.sp
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    maxLines = 3
                                )

                                Button(
                                    onClick = { sendCurrentMessage() },
                                    enabled = !isLoading && (inputText.isNotBlank() || attachedBitmap != null || attachedFileContent != null),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BrandCoralPink,
                                        contentColor = BrandForestDark
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.height(50.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp)
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Send",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

