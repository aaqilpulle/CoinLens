package com.example.expnz20

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Build
import android.graphics.BitmapFactory
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
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.graphics.TransformOrigin
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
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
val BrandForestDark = Color(0xFF04140E)      // Deep dark emerald forest app background & contrast color
val BrandForestSurface = Color(0xFF071B14)   // Rich deeper dark forest card surface
val BrandForestVariant = Color(0xFF0C241C)   // Highlighted dark forest container
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
    val baseBg = if (isDarkMode) BrandForestDark else LightModeAppBackground

    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .background(baseBg)
            .drawWithCache {
                val w = size.width
                val h = size.height
                val maxDim = max(w, h)

                val c1 = Offset(w * 0.20f, h * 0.16f)
                val brush1 = Brush.radialGradient(
                    colors = listOf(
                        BrandCoralPink.copy(alpha = if (isDarkMode) 0.12f else 0.045f),
                        BrandCoralPink.copy(alpha = if (isDarkMode) 0.03f else 0.01f),
                        Color.Transparent
                    ),
                    center = c1,
                    radius = maxDim * 0.50f
                )

                val c2 = Offset(w * 0.82f, h * 0.44f)
                val brush2 = Brush.radialGradient(
                    colors = listOf(
                        BrandMintGreen.copy(alpha = if (isDarkMode) 0.11f else 0.035f),
                        BrandSkyBlue.copy(alpha = if (isDarkMode) 0.03f else 0.01f),
                        Color.Transparent
                    ),
                    center = c2,
                    radius = maxDim * 0.48f
                )

                val c3 = Offset(w * 0.26f, h * 0.82f)
                val brush3 = Brush.radialGradient(
                    colors = listOf(
                        BrandSkyBlue.copy(alpha = if (isDarkMode) 0.09f else 0.04f),
                        BrandPurple.copy(alpha = if (isDarkMode) 0.04f else 0.015f),
                        Color.Transparent
                    ),
                    center = c3,
                    radius = maxDim * 0.50f
                )

                val c4 = Offset(w * 0.76f, h * 0.86f)
                val brush4 = Brush.radialGradient(
                    colors = listOf(
                        BrandWarmAmber.copy(alpha = if (isDarkMode) 0.08f else 0.035f),
                        Color.Transparent
                    ),
                    center = c4,
                    radius = maxDim * 0.38f
                )

                onDrawBehind {
                    drawCircle(brush = brush1, radius = maxDim * 0.50f, center = c1)
                    drawCircle(brush = brush2, radius = maxDim * 0.48f, center = c2)
                    drawCircle(brush = brush3, radius = maxDim * 0.50f, center = c3)
                    drawCircle(brush = brush4, radius = maxDim * 0.38f, center = c4)
                }
            }
    )
}

enum class LiquidGlassRole {
    ORB,
    HEADER,
    FOOTER,
    STATIC
}

@Immutable
data class OrbBoundarySpan(
    val leftPx: Float,
    val rightPx: Float,
    val intensity: Float
)

@Stable
class LiquidScrollBoundaryState {
    var hostViewIdentity by mutableIntStateOf(0)

    var headerTopPx by mutableFloatStateOf(0f)
    var headerBottomPx by mutableFloatStateOf(0f)
    var headerLeftPx by mutableFloatStateOf(0f)
    var headerRightPx by mutableFloatStateOf(0f)
    var headerWidthPx by mutableFloatStateOf(0f)

    var footerTopPx by mutableFloatStateOf(Float.MAX_VALUE)
    var footerBottomPx by mutableFloatStateOf(Float.MAX_VALUE)
    var footerLeftPx by mutableFloatStateOf(0f)
    var footerRightPx by mutableFloatStateOf(0f)
    var footerWidthPx by mutableFloatStateOf(0f)

    val headerContacts = mutableStateMapOf<Int, OrbBoundarySpan>()
    val footerContacts = mutableStateMapOf<Int, OrbBoundarySpan>()

    fun updateContact(
        id: Int,
        topIntensity: Float,
        bottomIntensity: Float,
        leftPx: Float,
        rightPx: Float
    ) {
        if (topIntensity > 0.015f) {
            val prev = headerContacts[id]
            if (prev == null ||
                abs(prev.intensity - topIntensity) > 0.02f ||
                abs(prev.leftPx - leftPx) > 3f ||
                abs(prev.rightPx - rightPx) > 3f
            ) {
                headerContacts[id] = OrbBoundarySpan(leftPx, rightPx, topIntensity)
            }
        } else if (headerContacts.containsKey(id)) {
            headerContacts.remove(id)
        }
        if (bottomIntensity > 0.015f) {
            val prev = footerContacts[id]
            if (prev == null ||
                abs(prev.intensity - bottomIntensity) > 0.02f ||
                abs(prev.leftPx - leftPx) > 3f ||
                abs(prev.rightPx - rightPx) > 3f
            ) {
                footerContacts[id] = OrbBoundarySpan(leftPx, rightPx, bottomIntensity)
            }
        } else if (footerContacts.containsKey(id)) {
            footerContacts.remove(id)
        }
    }

    fun removeContact(id: Int) {
        headerContacts.remove(id)
        footerContacts.remove(id)
    }
}

private object LiquidOrbIdGenerator {
    private val counter = AtomicInteger(1)
    fun nextId(): Int = counter.getAndIncrement()
}

@Stable
private class OrbLiquidContactState {
    // Smooth surface-tension impulse [-1..1]: positive = kiss/pull toward bar at contact
    var topPull by mutableFloatStateOf(0f)
    var bottomPull by mutableFloatStateOf(0f)

    // Pre-contact / trailing capillary meniscus gap [0..bridgeRangePx] & progress [0..1]
    var topBridgeStrength by mutableFloatStateOf(0f)
    var topBridgeGapPx by mutableFloatStateOf(0f)
    var topBridgeFromBottomEdge by mutableStateOf(false)

    var bottomBridgeStrength by mutableFloatStateOf(0f)
    var bottomBridgeGapPx by mutableFloatStateOf(0f)
    var bottomBridgeFromTopEdge by mutableStateOf(false)

    // Local X span intersecting the bar [0..width] & corner fillet visibility while passing under
    var headerLocalLeftPx by mutableFloatStateOf(0f)
    var headerLocalRightPx by mutableFloatStateOf(0f)
    var headerSeamYPx by mutableFloatStateOf(0f)
    var headerSeamStrength by mutableFloatStateOf(0f)
    var headerCanFilletLeft by mutableStateOf(false)
    var headerCanFilletRight by mutableStateOf(false)

    var footerLocalLeftPx by mutableFloatStateOf(0f)
    var footerLocalRightPx by mutableFloatStateOf(0f)
    var footerSeamYPx by mutableFloatStateOf(0f)
    var footerSeamStrength by mutableFloatStateOf(0f)
    var footerCanFilletLeft by mutableStateOf(false)
    var footerCanFilletRight by mutableStateOf(false)
}

private fun smoothstep01(t: Float): Float {
    val c = t.coerceIn(0f, 1f)
    return c * c * (3f - 2f * c)
}

@Stable
class OrbRevealState {
    var activeOrbKey by mutableStateOf<Any?>(null)
    var activeActionTrayBounds by mutableStateOf<Rect?>(null)

    fun dismiss() {
        activeOrbKey = null
        activeActionTrayBounds = null
    }
}

val LocalOrbRevealState = staticCompositionLocalOf { OrbRevealState() }
val LocalLiquidScrollBoundary = staticCompositionLocalOf { LiquidScrollBoundaryState() }
val LocalInsideOrbCard = staticCompositionLocalOf { false }
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

@Composable
fun Modifier.liquidGlass(
    shape: Shape = RoundedCornerShape(22.dp),
    tintColor: Color = BrandForestSurface,
    intensity: Float = 1f,
    accentRimColor: Color = BrandCoralPink,
    role: LiquidGlassRole = LiquidGlassRole.ORB
): Modifier {
    val boundary = LocalLiquidScrollBoundary.current
    val currentViewId = System.identityHashCode(LocalView.current)
    val isDark = LocalIsDarkTheme.current
    val density = LocalDensity.current
    val shouldClip = role == LiquidGlassRole.HEADER || role == LiquidGlassRole.FOOTER

    val orbId = remember(role) { if (role == LiquidGlassRole.ORB) LiquidOrbIdGenerator.nextId() else 0 }
    val orbLocalState = remember(role) { if (role == LiquidGlassRole.ORB) OrbLiquidContactState() else null }

    if (role == LiquidGlassRole.ORB) {
        DisposableEffect(boundary, orbId) {
            onDispose { boundary.removeContact(orbId) }
        }
    }

    var trackedModifier: Modifier = if (shouldClip) this.clip(shape) else this

    when (role) {
        LiquidGlassRole.HEADER -> {
            trackedModifier = trackedModifier.onGloballyPositioned { coords ->
                if (!coords.isAttached) return@onGloballyPositioned
                val b = coords.boundsInRoot()
                boundary.headerTopPx = b.top
                boundary.headerBottomPx = b.bottom
                boundary.headerLeftPx = b.left
                boundary.headerRightPx = b.right
                boundary.headerWidthPx = b.width
            }
        }
        LiquidGlassRole.FOOTER -> {
            trackedModifier = trackedModifier.onGloballyPositioned { coords ->
                if (!coords.isAttached) return@onGloballyPositioned
                val b = coords.boundsInRoot()
                boundary.footerTopPx = b.top
                boundary.footerBottomPx = b.bottom
                boundary.footerLeftPx = b.left
                boundary.footerRightPx = b.right
                boundary.footerWidthPx = b.width
            }
        }
        LiquidGlassRole.ORB -> {
            val localState = orbLocalState!!
            val contactZonePx = with(density) { 6.5.dp.toPx() }
            val bridgeRangePx = with(density) { 5.5.dp.toPx() }
            val filletMarginPx = with(density) { 8.dp.toPx() }
            val seamFadePx = with(density) { 7.dp.toPx() }
            val maxPullShiftPx = with(density) { 1.15.dp.toPx() }

            trackedModifier = trackedModifier
                .onGloballyPositioned { coords ->
                    if (!coords.isAttached || boundary.hostViewIdentity == 0 || currentViewId != boundary.hostViewIdentity) {
                        localState.topPull = 0f
                        localState.bottomPull = 0f
                        localState.topBridgeStrength = 0f
                        localState.bottomBridgeStrength = 0f
                        localState.headerSeamStrength = 0f
                        localState.footerSeamStrength = 0f
                        boundary.removeContact(orbId)
                        return@onGloballyPositioned
                    }
                    val b = coords.boundsInRoot()
                    val headerBottom = boundary.headerBottomPx
                    val overlapHeaderLeft = max(b.left, boundary.headerLeftPx)
                    val overlapHeaderRight = min(b.right, boundary.headerRightPx)
                    val overlapsHeaderX = headerBottom > 0f && overlapHeaderRight - overlapHeaderLeft > 24f

                    if (overlapsHeaderX) {
                        val gapTop = b.top - headerBottom
                        val gapBottom = b.bottom - headerBottom
                        val kissTop = smoothstep01(1f - abs(gapTop) / contactZonePx)
                        val pinchTop = smoothstep01(1f - abs(gapBottom) / contactZonePx) * 0.75f
                        localState.topPull = max(kissTop, pinchTop)

                        localState.headerLocalLeftPx = (overlapHeaderLeft - b.left).coerceIn(0f, b.width)
                        localState.headerLocalRightPx = (overlapHeaderRight - b.left).coerceIn(0f, b.width)

                        when {
                            gapTop > 0f && gapTop < bridgeRangePx -> {
                                localState.topBridgeStrength = smoothstep01(1f - gapTop / bridgeRangePx)
                                localState.topBridgeGapPx = gapTop
                                localState.topBridgeFromBottomEdge = false
                            }
                            gapBottom > 0f && gapBottom < bridgeRangePx -> {
                                localState.topBridgeStrength = smoothstep01(1f - gapBottom / bridgeRangePx) * 0.85f
                                localState.topBridgeGapPx = gapBottom
                                localState.topBridgeFromBottomEdge = true
                            }
                            else -> {
                                localState.topBridgeStrength = 0f
                                localState.topBridgeGapPx = 0f
                            }
                        }

                        if (b.top < headerBottom && b.bottom > headerBottom) {
                            val seamY = headerBottom - b.top
                            val edgeDist = min(seamY, b.height - seamY)
                            localState.headerSeamYPx = seamY
                            localState.headerSeamStrength = smoothstep01(edgeDist / seamFadePx)
                            localState.headerCanFilletLeft = b.left > boundary.headerLeftPx + filletMarginPx
                            localState.headerCanFilletRight = b.right < boundary.headerRightPx - filletMarginPx
                        } else {
                            localState.headerSeamStrength = 0f
                        }
                    } else {
                        localState.topPull = 0f
                        localState.topBridgeStrength = 0f
                        localState.headerSeamStrength = 0f
                    }

                    val footerTop = boundary.footerTopPx
                    val overlapFooterLeft = max(b.left, boundary.footerLeftPx)
                    val overlapFooterRight = min(b.right, boundary.footerRightPx)
                    val overlapsFooterX = footerTop < Float.MAX_VALUE * 0.5f && overlapFooterRight - overlapFooterLeft > 24f

                    if (overlapsFooterX) {
                        val gapBottom = footerTop - b.bottom
                        val gapTop = footerTop - b.top
                        val kissBottom = smoothstep01(1f - abs(gapBottom) / contactZonePx)
                        val pinchBottom = smoothstep01(1f - abs(gapTop) / contactZonePx) * 0.75f
                        localState.bottomPull = max(kissBottom, pinchBottom)

                        localState.footerLocalLeftPx = (overlapFooterLeft - b.left).coerceIn(0f, b.width)
                        localState.footerLocalRightPx = (overlapFooterRight - b.left).coerceIn(0f, b.width)

                        when {
                            gapBottom > 0f && gapBottom < bridgeRangePx -> {
                                localState.bottomBridgeStrength = smoothstep01(1f - gapBottom / bridgeRangePx)
                                localState.bottomBridgeGapPx = gapBottom
                                localState.bottomBridgeFromTopEdge = false
                            }
                            gapTop > 0f && gapTop < bridgeRangePx -> {
                                localState.bottomBridgeStrength = smoothstep01(1f - gapTop / bridgeRangePx) * 0.85f
                                localState.bottomBridgeGapPx = gapTop
                                localState.bottomBridgeFromTopEdge = true
                            }
                            else -> {
                                localState.bottomBridgeStrength = 0f
                                localState.bottomBridgeGapPx = 0f
                            }
                        }

                        if (b.top < footerTop && b.bottom > footerTop) {
                            val seamY = footerTop - b.top
                            val edgeDist = min(seamY, b.height - seamY)
                            localState.footerSeamYPx = seamY
                            localState.footerSeamStrength = smoothstep01(edgeDist / seamFadePx)
                            localState.footerCanFilletLeft = b.left > boundary.footerLeftPx + filletMarginPx
                            localState.footerCanFilletRight = b.right < boundary.footerRightPx - filletMarginPx
                        } else {
                            localState.footerSeamStrength = 0f
                        }
                    } else {
                        localState.bottomPull = 0f
                        localState.bottomBridgeStrength = 0f
                        localState.footerSeamStrength = 0f
                    }

                    val headerContactStrength = if (overlapsHeaderX) {
                        max(localState.topPull, localState.headerSeamStrength * 0.85f)
                    } else 0f
                    val footerContactStrength = if (overlapsFooterX) {
                        max(localState.bottomPull, localState.footerSeamStrength * 0.85f)
                    } else 0f

                    val contactLeft = if (overlapsHeaderX) overlapHeaderLeft else overlapFooterLeft
                    val contactRight = if (overlapsHeaderX) overlapHeaderRight else overlapFooterRight
                    boundary.updateContact(
                        id = orbId,
                        topIntensity = headerContactStrength,
                        bottomIntensity = footerContactStrength,
                        leftPx = contactLeft,
                        rightPx = contactRight
                    )
                }
                .graphicsLayer {
                    val pullTop = localState.topPull
                    val pullBottom = localState.bottomPull
                    val pull = max(pullTop, pullBottom)
                    if (pull > 0.005f) {
                        scaleY = 1f + 0.0055f * pull
                        scaleX = 1f - 0.0025f * pull
                        translationY = (pullBottom - pullTop) * maxPullShiftPx
                        transformOrigin = if (pullTop >= pullBottom) {
                            TransformOrigin(0.5f, 0f)
                        } else {
                            TransformOrigin(0.5f, 1f)
                        }
                    }
                }
        }
        LiquidGlassRole.STATIC -> Unit
    }

    return trackedModifier.drawWithCache {
        val outline = shape.createOutline(size, layoutDirection, this)
        val safeIntensity = intensity.coerceIn(0.65f, 1.5f)
        val strokePx = 1.25.dp.toPx()
        val solidTint = tintColor.copy(alpha = 1f)

        val frostBrush = Brush.verticalGradient(
            colors = listOf(
                solidTint.copy(alpha = (0.88f * safeIntensity).coerceIn(0.80f, 0.96f)),
                solidTint.copy(alpha = (0.82f * safeIntensity).coerceIn(0.75f, 0.93f))
            ),
            startY = 0f,
            endY = size.height
        )

        val meniscusHighlight = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = (0.12f * safeIntensity).coerceIn(0f, 0.20f)),
                Color.Transparent
            ),
            startY = 0f,
            endY = (size.height * 0.30f).coerceAtLeast(1f)
        )

        val rimBrush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = (0.65f * safeIntensity).coerceIn(0f, 0.90f)),
                Color.White.copy(alpha = (0.18f * safeIntensity).coerceIn(0f, 0.40f)),
                accentRimColor.copy(alpha = (0.55f * safeIntensity).coerceIn(0f, 0.85f)),
                Color.White.copy(alpha = (0.35f * safeIntensity).coerceIn(0f, 0.70f))
            ),
            start = Offset.Zero,
            end = Offset(size.width, size.height)
        )

        val bridgeStrokePx = 1.0.dp.toPx()
        val cornerInsetMax = 14.dp.toPx()
        val waistPullPx = 4.dp.toPx()
        val filletMaxW = 5.5.dp.toPx()
        val filletMaxH = 5.5.dp.toPx()
        val barRimMarginPx = 14.dp.toPx()
        val barSoftStrokePx = 2.0.dp.toPx()

        onDrawWithContent {
            drawOutline(outline = outline, brush = frostBrush)
            drawOutline(outline = outline, brush = meniscusHighlight)

            if (role == LiquidGlassRole.ORB && orbLocalState != null && size.width > 24f) {
                val bridgeFillBase = if (isDark) {
                    solidTint.copy(alpha = (0.86f * safeIntensity).coerceIn(0.78f, 0.94f))
                } else {
                    solidTint.copy(alpha = 0.84f)
                }
                val bridgeRimBase = if (isDark) {
                    Color.White.copy(alpha = 0.26f)
                } else {
                    Color(0xFF94A3B8).copy(alpha = 0.28f)
                }

                // 1) Full-width capillary meniscus bridge to header (upward)
                val topStr = orbLocalState.topBridgeStrength
                val topGap = orbLocalState.topBridgeGapPx
                if (topStr > 0.02f && topGap > 0.5f) {
                    val left = orbLocalState.headerLocalLeftPx
                    val right = orbLocalState.headerLocalRightPx
                    val spanW = right - left
                    if (spanW > 24f) {
                        val inset = min(cornerInsetMax, spanW * 0.14f)
                        val baseLeft = left + inset
                        val baseRight = right - inset
                        val barSpread = 2.5.dp.toPx() * (1f - topStr)
                        val topLeft = baseLeft + barSpread
                        val topRight = baseRight - barSpread
                        val yBase = if (orbLocalState.topBridgeFromBottomEdge) size.height - 1f else 1f
                        val yBar = if (orbLocalState.topBridgeFromBottomEdge) size.height - topGap else -topGap
                        val spanY = yBase - yBar
                        val waist = waistPullPx * (0.4f + 0.6f * (1f - topStr))

                        val fillPath = Path().apply {
                            moveTo(baseLeft, yBase)
                            cubicTo(
                                baseLeft + waist, yBase - spanY * 0.28f,
                                topLeft + waist * 0.8f, yBar + spanY * 0.28f,
                                topLeft, yBar
                            )
                            lineTo(topRight, yBar)
                            cubicTo(
                                topRight - waist * 0.8f, yBar + spanY * 0.28f,
                                baseRight - waist, yBase - spanY * 0.28f,
                                baseRight, yBase
                            )
                            close()
                        }
                        drawPath(path = fillPath, color = bridgeFillBase.copy(alpha = bridgeFillBase.alpha * topStr))

                        val sideRimPath = Path().apply {
                            moveTo(baseLeft, yBase)
                            cubicTo(
                                baseLeft + waist, yBase - spanY * 0.28f,
                                topLeft + waist * 0.8f, yBar + spanY * 0.28f,
                                topLeft, yBar
                            )
                            moveTo(topRight, yBar)
                            cubicTo(
                                topRight - waist * 0.8f, yBar + spanY * 0.28f,
                                baseRight - waist, yBase - spanY * 0.28f,
                                baseRight, yBase
                            )
                        }
                        drawPath(
                            path = sideRimPath,
                            color = bridgeRimBase.copy(alpha = bridgeRimBase.alpha * topStr),
                            style = Stroke(width = bridgeStrokePx, cap = StrokeCap.Round)
                        )
                    }
                }

                // 2) Full-width capillary meniscus bridge to footer (downward)
                val botStr = orbLocalState.bottomBridgeStrength
                val botGap = orbLocalState.bottomBridgeGapPx
                if (botStr > 0.02f && botGap > 0.5f) {
                    val left = orbLocalState.footerLocalLeftPx
                    val right = orbLocalState.footerLocalRightPx
                    val spanW = right - left
                    if (spanW > 24f) {
                        val inset = min(cornerInsetMax, spanW * 0.14f)
                        val baseLeft = left + inset
                        val baseRight = right - inset
                        val barSpread = 2.5.dp.toPx() * (1f - botStr)
                        val botLeft = baseLeft + barSpread
                        val botRight = baseRight - barSpread
                        val yBase = if (orbLocalState.bottomBridgeFromTopEdge) 1f else size.height - 1f
                        val yBar = if (orbLocalState.bottomBridgeFromTopEdge) botGap else size.height + botGap
                        val spanY = yBar - yBase
                        val waist = waistPullPx * (0.4f + 0.6f * (1f - botStr))

                        val fillPath = Path().apply {
                            moveTo(baseLeft, yBase)
                            cubicTo(
                                baseLeft + waist, yBase + spanY * 0.28f,
                                botLeft + waist * 0.8f, yBar - spanY * 0.28f,
                                botLeft, yBar
                            )
                            lineTo(botRight, yBar)
                            cubicTo(
                                botRight - waist * 0.8f, yBar - spanY * 0.28f,
                                baseRight - waist, yBase + spanY * 0.28f,
                                baseRight, yBase
                            )
                            close()
                        }
                        drawPath(path = fillPath, color = bridgeFillBase.copy(alpha = bridgeFillBase.alpha * botStr))

                        val sideRimPath = Path().apply {
                            moveTo(baseLeft, yBase)
                            cubicTo(
                                baseLeft + waist, yBase + spanY * 0.28f,
                                botLeft + waist * 0.8f, yBar - spanY * 0.28f,
                                botLeft, yBar
                            )
                            moveTo(botRight, yBar)
                            cubicTo(
                                botRight - waist * 0.8f, yBar - spanY * 0.28f,
                                baseRight - waist, yBase + spanY * 0.28f,
                                baseRight, yBase
                            )
                        }
                        drawPath(
                            path = sideRimPath,
                            color = bridgeRimBase.copy(alpha = bridgeRimBase.alpha * botStr),
                            style = Stroke(width = bridgeStrokePx, cap = StrokeCap.Round)
                        )
                    }
                }

                // 3) Concave corner wetting fillets where orb edges meet header/footer seam
                val hSeam = orbLocalState.headerSeamStrength
                if (hSeam > 0.02f) {
                    val sy = orbLocalState.headerSeamYPx
                    val fw = filletMaxW * hSeam
                    val fh = filletMaxH * hSeam
                    if (orbLocalState.headerCanFilletLeft) {
                        val fPath = Path().apply {
                            moveTo(0.5f, sy - 0.5f)
                            lineTo(-fw, sy - 0.5f)
                            cubicTo(-fw * 0.2f, sy, 0.5f, sy + fh * 0.35f, 0.5f, sy + fh)
                            close()
                        }
                        drawPath(fPath, color = bridgeFillBase.copy(alpha = bridgeFillBase.alpha * hSeam))
                    }
                    if (orbLocalState.headerCanFilletRight) {
                        val fPath = Path().apply {
                            moveTo(size.width - 0.5f, sy - 0.5f)
                            lineTo(size.width + fw, sy - 0.5f)
                            cubicTo(size.width + fw * 0.2f, sy, size.width - 0.5f, sy + fh * 0.35f, size.width - 0.5f, sy + fh)
                            close()
                        }
                        drawPath(fPath, color = bridgeFillBase.copy(alpha = bridgeFillBase.alpha * hSeam))
                    }
                }

                val fSeam = orbLocalState.footerSeamStrength
                if (fSeam > 0.02f) {
                    val sy = orbLocalState.footerSeamYPx
                    val fw = filletMaxW * fSeam
                    val fh = filletMaxH * fSeam
                    if (orbLocalState.footerCanFilletLeft) {
                        val fPath = Path().apply {
                            moveTo(0.5f, sy + 0.5f)
                            lineTo(-fw, sy + 0.5f)
                            cubicTo(-fw * 0.2f, sy, 0.5f, sy - fh * 0.35f, 0.5f, sy - fh)
                            close()
                        }
                        drawPath(fPath, color = bridgeFillBase.copy(alpha = bridgeFillBase.alpha * fSeam))
                    }
                    if (orbLocalState.footerCanFilletRight) {
                        val fPath = Path().apply {
                            moveTo(size.width - 0.5f, sy + 0.5f)
                            lineTo(size.width + fw, sy + 0.5f)
                            cubicTo(size.width + fw * 0.2f, sy, size.width - 0.5f, sy - fh * 0.35f, size.width - 0.5f, sy - fh)
                            close()
                        }
                        drawPath(fPath, color = bridgeFillBase.copy(alpha = bridgeFillBase.alpha * fSeam))
                    }
                }
            }

            drawContent()
            drawOutline(outline = outline, brush = rimBrush, style = Stroke(width = strokePx))

            // Subtle rim softening along exact contact span when orbs touch header/footer (NO glow)
            when (role) {
                LiquidGlassRole.HEADER -> {
                    if (size.width > 40f && boundary.headerContacts.isNotEmpty()) {
                        val edgeY = size.height - 0.8f
                        boundary.headerContacts.values.forEach { span ->
                            if (span.intensity > 0.02f) {
                                val segL = (span.leftPx - boundary.headerLeftPx).coerceIn(barRimMarginPx, size.width - barRimMarginPx)
                                val segR = (span.rightPx - boundary.headerLeftPx).coerceIn(segL, size.width - barRimMarginPx)
                                if (segR - segL > 12f) {
                                    drawLine(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                solidTint.copy(alpha = 0.55f * span.intensity),
                                                solidTint.copy(alpha = 0.55f * span.intensity),
                                                Color.Transparent
                                            ),
                                            startX = segL,
                                            endX = segR
                                        ),
                                        start = Offset(segL, edgeY),
                                        end = Offset(segR, edgeY),
                                        strokeWidth = barSoftStrokePx,
                                        cap = StrokeCap.Round
                                    )
                                }
                            }
                        }
                    }
                }
                LiquidGlassRole.FOOTER -> {
                    if (size.width > 40f && boundary.footerContacts.isNotEmpty()) {
                        val edgeY = 0.8f
                        boundary.footerContacts.values.forEach { span ->
                            if (span.intensity > 0.02f) {
                                val segL = (span.leftPx - boundary.footerLeftPx).coerceIn(barRimMarginPx, size.width - barRimMarginPx)
                                val segR = (span.rightPx - boundary.footerLeftPx).coerceIn(segL, size.width - barRimMarginPx)
                                if (segR - segL > 12f) {
                                    drawLine(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                solidTint.copy(alpha = 0.55f * span.intensity),
                                                solidTint.copy(alpha = 0.55f * span.intensity),
                                                Color.Transparent
                                            ),
                                            startX = segL,
                                            endX = segR
                                        ),
                                        start = Offset(segL, edgeY),
                                        end = Offset(segR, edgeY),
                                        strokeWidth = barSoftStrokePx,
                                        cap = StrokeCap.Round
                                    )
                                }
                            }
                        }
                    }
                }
                LiquidGlassRole.ORB, LiquidGlassRole.STATIC -> Unit
            }
        }
    }
}

@Composable
fun rememberHeaderAwareTopPadding(orbSpacing: Dp): Dp {
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    return statusBarTop + 60.dp + orbSpacing
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
    val defaultTint = if (isDark) Color(0xFF09221D) else Color(0xFFFFFFFF)
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
    } ?: if (isDark) BrandCoralPink else Color(0xFFCBD5E1)

    val zeroElevation = if (elevation == CardDefaults.cardElevation()) {
        CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
    } else {
        CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
    }

    val resolvedContentColor = adaptiveCardContentColor(
        isDark = isDark,
        background = glassBaseTint,
        preferred = if (colors.contentColor != Color.Unspecified) colors.contentColor else if (isDark) Color.White else LightModeInk
    )

    val isNestedOrb = LocalInsideOrbCard.current
    androidx.compose.material3.Card(
        modifier = modifier
            .liquidGlass(
                shape = shape,
                tintColor = glassBaseTint,
                intensity = if (isDark) 1.05f else 1.15f,
                accentRimColor = rimAccent,
                role = if (isNestedOrb) LiquidGlassRole.STATIC else LiquidGlassRole.ORB
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = resolvedContentColor
        ),
        elevation = zeroElevation,
        border = border,
        content = {
            if (isNestedOrb) {
                content()
            } else {
                CompositionLocalProvider(LocalInsideOrbCard provides true) {
                    content()
                }
            }
        }
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
    val defaultTint = if (isDark) Color(0xFF081E19) else Color(0xFFFCFCFD)
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
    } ?: if (isDark) BrandCoralPink else Color(0xFFCBD5E1)

    val zeroElevation = if (elevation == CardDefaults.cardElevation()) {
        CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
    } else {
        CardDefaults.cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
    }

    val resolvedContentColor = adaptiveCardContentColor(
        isDark = isDark,
        background = glassBaseTint,
        preferred = if (colors.contentColor != Color.Unspecified) colors.contentColor else if (isDark) Color.White else LightModeInk
    )

    // Clickable cards/orbs have higher intensity (1.22f) so they look more solid and readable
    val isNestedOrb = LocalInsideOrbCard.current
    androidx.compose.material3.Card(
        onClick = onClick,
        modifier = modifier
            .liquidGlass(
                shape = shape,
                tintColor = glassBaseTint,
                intensity = 1.22f,
                accentRimColor = rimAccent,
                role = if (isNestedOrb) LiquidGlassRole.STATIC else LiquidGlassRole.ORB
            ),
        enabled = enabled,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = resolvedContentColor
        ),
        elevation = zeroElevation,
        border = border,
        interactionSource = interactionSource,
        content = {
            if (isNestedOrb) {
                content()
            } else {
                CompositionLocalProvider(LocalInsideOrbCard provides true) {
                    content()
                }
            }
        }
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
    val isDarkDialog = baseGlass.luminance() < 0.35f
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
        titleContentColor = if (titleContentColor != Color.Unspecified) titleContentColor else if (isDarkDialog) Color.White else LightModeInk,
        textContentColor = if (textContentColor != Color.Unspecified) textContentColor else if (isDarkDialog) Color.White.copy(alpha = 0.95f) else LightModeInk.copy(alpha = 0.95f),
        tonalElevation = zeroTonalElevation,
        properties = properties
    )
}

// ============================================================================
// CUSTOM VECTOR APP LOGO COMPOSABLE (MATCHES COINLENS CORAL 'C' + EMERALD 'L' & CHART LOGO)
// ============================================================================
@Composable
fun AppLogoIcon(
    modifier: Modifier = Modifier,
    size: Dp = 38.dp,
    backgroundColor: Color = Color.Transparent,
    foregroundColor: Color = BrandCoralPink,
    showBorder: Boolean = false
) {
    val shape = RoundedCornerShape(size * 0.26f)
    val boxModifier = if (showBorder) {
        modifier
            .size(size)
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        foregroundColor.copy(alpha = 0.45f),
                        BrandMintGreen.copy(alpha = 0.45f)
                    )
                ),
                shape = shape
            )
    } else {
        modifier
            .size(size)
            .background(backgroundColor)
    }
    val canvasPadding = if (showBorder) size * 0.12f else size * 0.04f
    Box(
        modifier = boxModifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(canvasPadding)) {
            val w = this.size.width
            val h = this.size.height
            val strokeW = w * 0.115f

            val coralBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFF5A3B0),
                    foregroundColor,
                    Color(0xFFB84A5A)
                ),
                start = Offset(0f, 0f),
                end = Offset(w * 0.8f, h)
            )
            val coralHighlightBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFDCE2).copy(alpha = 0.80f),
                    foregroundColor.copy(alpha = 0.25f),
                    Color(0xFFFFC4CE).copy(alpha = 0.65f)
                ),
                start = Offset(0f, 0f),
                end = Offset(w * 0.7f, h)
            )
            val emeraldBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF7CF5CC),
                    BrandMintGreen,
                    Color(0xFF1E7A5C)
                ),
                start = Offset(w * 0.35f, h * 0.15f),
                end = Offset(w * 0.90f, h * 0.75f)
            )
            val emeraldHighlight = Color(0xFFD8FFF0).copy(alpha = 0.75f)

            // 1. Coral-Rose 3D Glass 'C' Arc (Left & Outer)
            val cTopLeft = Offset(w * 0.06f, h * 0.08f)
            val cSize = Size(w * 0.76f, h * 0.84f)
            // Soft outer coral glow
            drawArc(
                color = foregroundColor.copy(alpha = 0.18f),
                startAngle = 42f,
                sweepAngle = 266f,
                useCenter = false,
                topLeft = cTopLeft,
                size = cSize,
                style = Stroke(width = strokeW * 1.65f, cap = StrokeCap.Round)
            )
            // Main tubular 'C' body
            drawArc(
                brush = coralBrush,
                startAngle = 42f,
                sweepAngle = 266f,
                useCenter = false,
                topLeft = cTopLeft,
                size = cSize,
                style = Stroke(width = strokeW * 1.18f, cap = StrokeCap.Round)
            )
            // Inner glass core refraction on 'C'
            drawArc(
                brush = coralHighlightBrush,
                startAngle = 46f,
                sweepAngle = 258f,
                useCenter = false,
                topLeft = cTopLeft,
                size = cSize,
                style = Stroke(width = strokeW * 0.48f, cap = StrokeCap.Round)
            )
            // Bright specular glass highlight along upper-left curve of 'C'
            drawArc(
                color = Color.White.copy(alpha = 0.65f),
                startAngle = 150f,
                sweepAngle = 95f,
                useCenter = false,
                topLeft = Offset(cTopLeft.x + strokeW * 0.12f, cTopLeft.y + strokeW * 0.12f),
                size = Size(cSize.width - strokeW * 0.24f, cSize.height - strokeW * 0.24f),
                style = Stroke(width = strokeW * 0.18f, cap = StrokeCap.Round)
            )

            // 2. Emerald-Mint 3D Glass 'L' Frame (Nested inside right side of 'C')
            val lPath = Path().apply {
                moveTo(w * 0.58f, h * 0.45f)
                lineTo(w * 0.40f, h * 0.68f)
                lineTo(w * 0.82f, h * 0.68f)
            }
            drawPath(
                path = lPath,
                color = BrandMintGreen.copy(alpha = 0.20f),
                style = Stroke(width = strokeW * 1.05f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            drawPath(
                path = lPath,
                brush = emeraldBrush,
                style = Stroke(width = strokeW * 0.72f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            drawPath(
                path = lPath,
                color = emeraldHighlight,
                style = Stroke(width = strokeW * 0.18f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // 3. Three Rising Emerald Glass Chart Bars inside the 'L'
            val barCorner = CornerRadius(w * 0.028f, w * 0.028f)
            // Small bar
            drawRoundRect(
                brush = emeraldBrush,
                topLeft = Offset(w * 0.54f, h * 0.53f),
                size = Size(w * 0.062f, h * 0.09f),
                cornerRadius = barCorner
            )
            // Medium bar
            drawRoundRect(
                brush = emeraldBrush,
                topLeft = Offset(w * 0.63f, h * 0.45f),
                size = Size(w * 0.068f, h * 0.17f),
                cornerRadius = barCorner
            )
            // Tall glossy bar
            drawRoundRect(
                brush = emeraldBrush,
                topLeft = Offset(w * 0.725f, h * 0.34f),
                size = Size(w * 0.095f, h * 0.28f),
                cornerRadius = CornerRadius(w * 0.04f, w * 0.04f)
            )
            drawRoundRect(
                color = emeraldHighlight,
                topLeft = Offset(w * 0.74f, h * 0.355f),
                size = Size(w * 0.065f, h * 0.25f),
                cornerRadius = CornerRadius(w * 0.03f, w * 0.03f),
                style = Stroke(width = strokeW * 0.13f)
            )

            // 4. Rising Zig-Zag Emerald Trend Arrow above the 'L'
            val arrowShaftPath = Path().apply {
                moveTo(w * 0.52f, h * 0.38f)
                lineTo(w * 0.60f, h * 0.29f)
                lineTo(w * 0.66f, h * 0.34f)
                lineTo(w * 0.86f, h * 0.15f)
            }
            drawPath(
                path = arrowShaftPath,
                color = BrandMintGreen.copy(alpha = 0.22f),
                style = Stroke(width = strokeW * 1.15f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            drawPath(
                path = arrowShaftPath,
                brush = emeraldBrush,
                style = Stroke(width = strokeW * 0.76f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            drawPath(
                path = arrowShaftPath,
                color = emeraldHighlight,
                style = Stroke(width = strokeW * 0.20f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Arrowhead at top-right
            val arrowHeadPath = Path().apply {
                moveTo(w * 0.90f, h * 0.11f)
                lineTo(w * 0.73f, h * 0.14f)
                lineTo(w * 0.86f, h * 0.27f)
                close()
            }
            drawPath(path = arrowHeadPath, brush = emeraldBrush)
            drawPath(
                path = arrowHeadPath,
                color = emeraldHighlight,
                style = Stroke(width = strokeW * 0.14f, join = StrokeJoin.Round)
            )
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
    val colorPropertiesHex: Long = 0xFFE38391L, // Pink
    val colorSuperSharesHex: Long = 0xFF38BDF8L, // Blue
    val colorOtherAssetsHex: Long = 0xFFC084FCL, // Purple
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
    TOTAL_WEALTH("Summary", "Summary", Icons.Default.PieChart),
    CASH_FLOW("Personal Cash Flow", "Cash Flow", Icons.Default.AccountBalanceWallet),
    PROPERTIES("Properties", "Properties", Icons.Default.HomeWork),
    SUPER_SHARES("Super & Shares", "Super/Shares", Icons.AutoMirrored.Filled.TrendingUp),
    OTHER_ASSETS("Other Assets", "Assets", Icons.Default.DirectionsCar),
    LOANS("Home Loans & Offset", "Loans", Icons.Default.QueryStats),
    WHAT_IF("Scenario Simulator", "What-If", Icons.Default.Science),
    SETTINGS("Settings", "Settings", Icons.Default.Settings);

    fun getColor(): Color {
        return when (this) {
            TOTAL_WEALTH -> BrandWarmAmber
            CASH_FLOW -> BrandMintGreen
            PROPERTIES -> BrandCoralPink
            SUPER_SHARES -> BrandSkyBlue
            OTHER_ASSETS -> BrandPurple
            LOANS -> BrandCoralRed
            WHAT_IF -> Color(0xFF818CF8)
            SETTINGS -> SlateGray
        }
    }
}

enum class BillingCycle(
    val label: String,
    val cycleDays: Long,
    val cycleWeeks: Double
) {
    WEEKLY("Weekly", 7L, 1.0),
    FORTNIGHTLY("Fortnight", 14L, 2.0),
    MONTHLY("Monthly", 30L, 52.0 / 12.0),
    QUARTERLY("Quarterly", 91L, 13.0),
    SIX_MONTHLY("6-Monthly", 182L, 26.0),
    ANNUAL("Annual", 365L, 52.0);

    companion object {
        fun fromName(raw: String?): BillingCycle {
            if (raw.isNullOrBlank()) return WEEKLY
            val clean = raw.trim()
            return entries.firstOrNull {
                it.name.equals(clean, ignoreCase = true) ||
                    it.label.equals(clean, ignoreCase = true) ||
                    (it == ANNUAL && clean.equals("Yearly", ignoreCase = true)) ||
                    (it == FORTNIGHTLY && clean.equals("Fortnightly", ignoreCase = true))
            } ?: WEEKLY
        }
    }
}

private val CachedSystemZoneId: ZoneId = ZoneId.systemDefault()

@Immutable
data class CashflowItem(
    val id: String,
    val name: String,
    val weeklyAmount: Double,
    val isLockedMortgageOrPropertyLink: Boolean = false,
    val iconName: String = "Default",
    val customSubtitle: String? = null,
    val billingCycle: BillingCycle = BillingCycle.WEEKLY,
    val nextDueDateEpochMillis: Long? = null,
    val linkedAssetId: String? = null
) {
    val cycleDays: Long = billingCycle.cycleDays.coerceAtLeast(1L)

    val cycleWeeks: Double = billingCycle.cycleWeeks

    val isBatteryEnabled: Boolean = billingCycle != BillingCycle.WEEKLY &&
        nextDueDateEpochMillis != null &&
        nextDueDateEpochMillis > 0L &&
        weeklyAmount < 0.0 &&
        !isLockedMortgageOrPropertyLink

    val cycleTargetAmount: Double = abs(weeklyAmount) * cycleWeeks

    @Transient
    private val rawInitialDueDate: LocalDate? = if (nextDueDateEpochMillis != null && nextDueDateEpochMillis > 0L) {
        try {
            Instant.ofEpochMilli(nextDueDateEpochMillis).atZone(CachedSystemZoneId).toLocalDate()
        } catch (_: Exception) {
            null
        }
    } else {
        null
    }

    @Transient
    private var cachedTodayEpochDay: Long = Long.MIN_VALUE

    @Transient
    private var cachedResolvedDueDate: LocalDate? = null

    @Transient
    private var cachedDaysUntilDue: Long = 0L

    private fun ensureCycleDateCache() {
        val initialDue = rawInitialDueDate ?: return
        val todayEpochDay = System.currentTimeMillis() / 86_400_000L
        if (cachedTodayEpochDay == todayEpochDay && cachedResolvedDueDate != null) return
        val today = LocalDate.now(CachedSystemZoneId)
        var dueDate = initialDue
        if (billingCycle != BillingCycle.WEEKLY && dueDate.isBefore(today)) {
            val stepDays = cycleDays
            val overdueDays = ChronoUnit.DAYS.between(dueDate, today)
            val cyclesToAdvance = (overdueDays / stepDays) + 1L
            dueDate = dueDate.plusDays(cyclesToAdvance * stepDays)
            while (dueDate.isBefore(today)) {
                dueDate = dueDate.plusDays(stepDays)
            }
        }
        cachedTodayEpochDay = todayEpochDay
        cachedResolvedDueDate = dueDate
        cachedDaysUntilDue = ChronoUnit.DAYS.between(today, dueDate).coerceAtLeast(0L)
    }

    val nextDueDate: LocalDate?
        get() {
            if (rawInitialDueDate == null) return null
            ensureCycleDateCache()
            return cachedResolvedDueDate
        }

    val daysUntilDue: Long
        get() {
            if (rawInitialDueDate == null) return 0L
            ensureCycleDateCache()
            return cachedDaysUntilDue
        }

    val chargeProgress: Float
        get() = if (!isBatteryEnabled) {
            0f
        } else {
            ((cycleDays - daysUntilDue).toFloat() / cycleDays.toFloat()).coerceIn(0f, 1f)
        }

    val accruedAmount: Double
        get() = if (!isBatteryEnabled) 0.0 else cycleTargetAmount * chargeProgress

    val weeksUntilDue: Long
        get() = (daysUntilDue / 7L).coerceAtLeast(0L)

    val isReadyToPay: Boolean
        get() = isBatteryEnabled && (daysUntilDue in 0L..7L || chargeProgress >= 0.95f)
}

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
    val currentPrice: Double,
    val purchaseDate: String = "",
    val market: String = "ASX",
    val usdPurchasePrice: Double = 0.0,
    val usdCurrentPrice: Double = 0.0,
    val fxRateAtSync: Double = 0.0
)

data class GroupedShareHolding(
    val code: String,
    val owners: List<String>,
    val parcels: List<ShareParcel>
) {
    val market: String = parcels.firstOrNull()?.market?.uppercase()?.takeIf { it == "US" } ?: "ASX"
    val isUsMarket: Boolean get() = market == "US"
    val holdingKey: String get() = "$market:${code.uppercase()}"
    val totalUnits: Double = parcels.sumOf { it.units }
    val currentPrice: Double = parcels.firstOrNull { it.currentPrice > 0 }?.currentPrice
        ?: parcels.firstOrNull()?.currentPrice ?: 0.0
    val totalCostBasis: Double = parcels.sumOf { it.units * it.purchasePrice }
    val totalMarketValue: Double = totalUnits * currentPrice
    val averagePurchasePrice: Double = if (totalUnits > 0) totalCostBasis / totalUnits else 0.0
    val totalGainLoss: Double = totalMarketValue - totalCostBasis
    val gainLossPct: Double = if (totalCostBasis > 0) (totalGainLoss / totalCostBasis) * 100 else 0.0

    val fxRate: Double = parcels.firstOrNull { it.fxRateAtSync > 0 }?.fxRateAtSync ?: 0.0
    val usdCurrentPrice: Double = parcels.firstOrNull { it.usdCurrentPrice > 0 }?.usdCurrentPrice
        ?: if (isUsMarket && fxRate > 0 && currentPrice > 0) currentPrice / fxRate else 0.0
    val usdTotalCostBasis: Double = parcels.sumOf { p ->
        val pUsd = when {
            p.usdPurchasePrice > 0 -> p.usdPurchasePrice
            p.fxRateAtSync > 0 && p.purchasePrice > 0 -> p.purchasePrice / p.fxRateAtSync
            fxRate > 0 && p.purchasePrice > 0 -> p.purchasePrice / fxRate
            else -> 0.0
        }
        p.units * pUsd
    }
    val usdTotalMarketValue: Double = totalUnits * usdCurrentPrice
    val usdAveragePurchasePrice: Double = if (totalUnits > 0) usdTotalCostBasis / totalUnits else 0.0
    val usdTotalGainLoss: Double = usdTotalMarketValue - usdTotalCostBasis
    val usdGainLossPct: Double = if (usdTotalCostBasis > 0) (usdTotalGainLoss / usdTotalCostBasis) * 100 else 0.0
}

@Composable
@ReadOnlyComposable
private fun currentLocale(): Locale {
    val locales = LocalConfiguration.current.locales
    return if (locales.isEmpty) Locale.ENGLISH else locales[0]
}

fun appDateDisplayFormatter(): DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())

fun parseFlexibleDate(dateStr: String): LocalDate? {
    val trimmed = dateStr.trim()
    if (trimmed.isBlank()) return null
    val patterns = listOf(
        "d MMM yyyy",
        "d MMMM yyyy",
        "dd/MM/yyyy",
        "d/M/yyyy",
        "dd-MM-yyyy",
        "d-M-yyyy",
        "yyyy-MM-dd"
    )
    for (pat in patterns) {
        try {
            return LocalDate.parse(trimmed, DateTimeFormatter.ofPattern(pat, Locale.getDefault()))
        } catch (_: Exception) {}
    }
    return try {
        LocalDate.parse(trimmed)
    } catch (_: Exception) {
        null
    }
}

fun formatDisplayDate(dateStr: String): String {
    val trimmed = dateStr.trim()
    if (trimmed.isBlank()) return ""
    val parsed = parseFlexibleDate(trimmed)
    return parsed?.format(appDateDisplayFormatter()) ?: trimmed
}

fun formatParcelDate(parcel: ShareParcel): String {
    if (parcel.purchaseDate.isNotBlank()) {
        try {
            val ld = parseFlexibleDate(parcel.purchaseDate)
            if (ld != null) return ld.format(appDateDisplayFormatter())
            return parcel.purchaseDate
        } catch (_: Exception) {
            return parcel.purchaseDate
        }
    }
    val match = Regex("""\d{10,13}""").find(parcel.id)
    if (match != null) {
        try {
            val epoch = match.value.toLong()
            val date = Instant.ofEpochMilli(epoch).atZone(ZoneId.systemDefault()).toLocalDate()
            return date.format(appDateDisplayFormatter())
        } catch (_: Exception) {}
    }
    return "Initial Purchase"
}

fun formatShareUnits(units: Double): String {
    return if (units % 1.0 == 0.0) units.toLong().toString() else String.format(Locale.getDefault(), "%.3f", units).trimEnd('0').trimEnd('.')
}

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
        
        // Fire off background AI warmup to eliminate the 1-2 minute Play Integrity cold start delay
        kotlin.concurrent.thread {
            kotlinx.coroutines.runBlocking {
                AiAssistantManager.warmUp()
            }
        }
        
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
                    primary = LightModeInk,
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFFE2E8F0),
                    onPrimaryContainer = LightModeInk,
                    secondary = BrandCoralPink,
                    onSecondary = LightModeInk,
                    secondaryContainer = LightModeInk,
                    onSecondaryContainer = Color.White,
                    tertiary = BrandWarmAmber,
                    error = BrandCoralRed,
                    background = Color.Transparent,
                    surface = Color.White.copy(alpha = 0.96f),
                    onBackground = LightModeInk,
                    onSurface = LightModeInk,
                    surfaceVariant = Color(0xFFE2E8F0),
                    onSurfaceVariant = LightModeInkSecondary,
                    outline = Color(0xFF64748B),
                    outlineVariant = Color(0xFFCBD5E1)
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
                                        onDeleteAccountUser = {
                                            val targetUser = auth.currentUser ?: currentUser
                                            val userUid = targetUser?.uid
                                            val userPrefsKey = if (userUid != null) "expnz_user_$userUid" else null

                                            fun finalizeLocalCleanup() {
                                                if (userPrefsKey != null) {
                                                    try {
                                                        applicationContext.getSharedPreferences(userPrefsKey, Context.MODE_PRIVATE)
                                                            .edit().clear().apply()
                                                    } catch (_: Exception) {}
                                                }
                                                auth.signOut()
                                                currentUser = null
                                                Toast.makeText(applicationContext, "Account deleted successfully", Toast.LENGTH_LONG).show()
                                            }

                                            if (userUid != null) {
                                                Firebase.firestore.collection("users").document(userUid)
                                                    .collection("wealth_data").document("main")
                                                    .delete()
                                                    .addOnCompleteListener {
                                                        Firebase.firestore.collection("users").document(userUid)
                                                            .delete()
                                                            .addOnCompleteListener {
                                                                targetUser?.delete()
                                                                    ?.addOnCompleteListener { delTask ->
                                                                        if (delTask.isSuccessful) {
                                                                            finalizeLocalCleanup()
                                                                        } else {
                                                                            val err = delTask.exception?.localizedMessage ?: "Please sign out and sign back in to delete your account."
                                                                            Toast.makeText(applicationContext, "Account deletion requires recent login: $err", Toast.LENGTH_LONG).show()
                                                                            finalizeLocalCleanup()
                                                                        }
                                                                    } ?: finalizeLocalCleanup()
                                                            }
                                                    }
                                            } else {
                                                finalizeLocalCleanup()
                                            }
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
    var currentDate = parseFlexibleDate(config.startDateStr) ?: LocalDate.now()

    var balance = config.startLoanBalance.coerceAtLeast(0.0)
    var accrued = 0.0
    val fnPayment = calculateEffectiveFortnightlyRepayment(config)

    rows.add(
        AmortizationRow(
            fortnightNumber = 0,
            paymentDate = currentDate.format(appDateDisplayFormatter()),
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
                paymentDate = currentDate.format(appDateDisplayFormatter()),
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
        val start = parseFlexibleDate(startDateStr) ?: return 0
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
    incomeGrowthWeekly: Float = 0f,
    rateShockProp1: Float,
    rateShockProp2: Float,
    rentalIncomeGrowthWeekly: Float,
    superBoostWeekly: Float,
    customLevers: List<CustomWhatIfLever>,
    milestones: List<LifeMilestoneBlock>,
    prop1LiveBalance: Double = -1.0,
    prop2LiveBalance: Double = -1.0,
    isProperty2Rented: Boolean = false
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

    val p1Bal0 = if (!showLoans) 0.0
    else if (prop1LiveBalance >= 0.0) prop1LiveBalance
    else prop1Loan.startLoanBalance.coerceAtLeast(0.0)

    val p2Bal0 = if (!showLoans || !hasSecondProperty || isProperty2Rented) 0.0
    else if (prop2LiveBalance >= 0.0) prop2LiveBalance
    else prop2Loan.startLoanBalance.coerceAtLeast(0.0)

    val extraBaseLiab0 = (baseLiabilitiesVal - (p1Bal0 + p2Bal0)).coerceAtLeast(0.0)

    val hasActiveProp1Loan = showLoans && p1Bal0 > 0.0
    val hasActiveProp2Loan = showLoans && hasSecondProperty && !isProperty2Rented && p2Bal0 > 0.0

    val baseRate1 = prop1Loan.interestRatePa.coerceAtLeast(0.0)
    val shockedRate1 = (prop1Loan.interestRatePa + effectiveShock1).coerceAtLeast(0.5)
    val baseFnPmt1 = if (hasActiveProp1Loan) calculateEffectiveFortnightlyRepayment(prop1Loan) else 0.0
    val shockedFnPmt1 = if (hasActiveProp1Loan) calculateEffectiveFortnightlyRepayment(prop1Loan, shockedRate1) else 0.0
    val weeklyRateDelta1 = (shockedFnPmt1 - baseFnPmt1) / 2.0

    val baseRate2 = prop2Loan.interestRatePa.coerceAtLeast(0.0)
    val shockedRate2 = (prop2Loan.interestRatePa + effectiveShock2).coerceAtLeast(0.5)
    val baseFnPmt2 = if (hasActiveProp2Loan) calculateEffectiveFortnightlyRepayment(prop2Loan) else 0.0
    val shockedFnPmt2 = if (hasActiveProp2Loan) calculateEffectiveFortnightlyRepayment(prop2Loan, shockedRate2) else 0.0
    val weeklyRateDelta2 = when {
        hasActiveProp2Loan -> (shockedFnPmt2 - baseFnPmt2) / 2.0
        rateShockProp2 != 0f -> {
            // Allow Property 2 Rate Lever to model a hypothetical $500k investment loan if 2nd property is not yet enabled
            val fallbackLoan = if (prop2Loan.startLoanBalance > 0.0 || prop2Loan.originalLoanAmount > 0.0) {
                prop2Loan
            } else {
                LoanConfig("Property 2", LocalDate.now().toString(), 500000.0, 30, 500000.0, 30.0, 6.25, 0.0)
            }
            val fbBase = calculateEffectiveFortnightlyRepayment(fallbackLoan)
            val fbShockedRate = (fallbackLoan.interestRatePa + effectiveShock2).coerceAtLeast(0.5)
            val fbShocked = calculateEffectiveFortnightlyRepayment(fallbackLoan, fbShockedRate)
            (fbShocked - fbBase) / 2.0
        }
        else -> 0.0
    }

    val totalWeeklyRateCost = weeklyRateDelta1 + weeklyRateDelta2
    val hypotheticalRateCostWeekly = if (!hasActiveProp2Loan) weeklyRateDelta2 else 0.0

    // Separate fixed nominal mortgage P&I repayments from inflating pre-loan surplus
    val p1AnnualPmtBase = baseFnPmt1 * 26.0
    val p1AnnualPmtShocked = shockedFnPmt1 * 26.0
    val p2AnnualPmtBase = baseFnPmt2 * 26.0
    val p2AnnualPmtShocked = shockedFnPmt2 * 26.0
    val basePreLoanSurplusWeekly = baseSurplusWeekly + ((p1AnnualPmtBase + p2AnnualPmtBase) / 52.0)

    val baseAssets = basePropertyVal + baseSharesAndSuperVal + baseOtherAssetsVal + baseOffsetsVal
    val baseNetWorth = baseAssets - baseLiabilitiesVal
    val safeHorizon = horizonYears.coerceIn(1, 40)

    // Path A: Status Quo Baseline Compounding Trajectory
    val pathASeries = mutableListOf<Double>()
    var aProp = basePropertyVal
    var aSharesSuper = baseSharesAndSuperVal
    var aLiquid = baseOffsetsVal
    var aP1Bal = p1Bal0
    var aP2Bal = p2Bal0
    var aExtraLiab = extraBaseLiab0
    pathASeries.add(baseNetWorth)

    for (year in 1..safeHorizon) {
        aProp *= 1.04
        aSharesSuper *= 1.075

        var nonLoanAnnualA = basePreLoanSurplusWeekly * 52.0
        if (year > 1) {
            nonLoanAnnualA *= 1.03.pow((year - 1).toDouble())
        }

        // Offset cash reduces mortgage interest charged first; excess positive cash earns 4.5% HISA
        var remainingOffsetA = aLiquid.coerceAtLeast(0.0)
        var paid1A = 0.0
        if (aP1Bal > 0.0) {
            val offset1A = min(aP1Bal, remainingOffsetA)
            remainingOffsetA -= offset1A
            val interest1A = (aP1Bal - offset1A) * (baseRate1 / 100.0)
            paid1A = min(p1AnnualPmtBase, aP1Bal + interest1A)
            aP1Bal = (aP1Bal + interest1A - paid1A).coerceAtLeast(0.0)
        }
        var paid2A = 0.0
        if (aP2Bal > 0.0) {
            val offset2A = min(aP2Bal, remainingOffsetA)
            remainingOffsetA -= offset2A
            val interest2A = (aP2Bal - offset2A) * (baseRate2 / 100.0)
            paid2A = min(p2AnnualPmtBase, aP2Bal + interest2A)
            aP2Bal = (aP2Bal + interest2A - paid2A).coerceAtLeast(0.0)
        }

        val hisaInterestA = remainingOffsetA * 0.045
        aLiquid += nonLoanAnnualA - paid1A - paid2A + hisaInterestA

        val extraPaydownA = (aExtraLiab * 0.035).coerceAtMost(aExtraLiab)
        aExtraLiab = (aExtraLiab - extraPaydownA).coerceAtLeast(0.0)

        val totalLiabA = aP1Bal + aP2Bal + aExtraLiab
        val nwA = (aProp + aSharesSuper + baseOtherAssetsVal + aLiquid) - totalLiabA
        pathASeries.add(nwA)
    }

    // Path B: Simulated What-If Plan Compounding Trajectory
    val pathBSeries = mutableListOf<Double>()
    var bProp = basePropertyVal
    var bSharesSuper = baseSharesAndSuperVal
    var bOther = baseOtherAssetsVal
    var bLiquid = baseOffsetsVal + customLumpSum
    var bP1Bal = p1Bal0
    var bP2Bal = p2Bal0
    var bExtraLiab = extraBaseLiab0
    var bExtraSuper = 0.0
    var foundDeficitYear: Int? = null

    pathBSeries.add(baseNetWorth + customLumpSum)

    val activeMilestones = milestones.filter { it.isEnabled }
    val effRate1 = if (hasActiveProp1Loan) shockedRate1 else baseRate1
    val effRate2 = if (hasActiveProp2Loan) shockedRate2 else baseRate2

    for (year in 1..safeHorizon) {
        var activeWeeklyDelta =
            lifestyleAdjustmentWeekly.toDouble() +
                incomeGrowthWeekly.toDouble() +
                rentalIncomeGrowthWeekly.toDouble() +
                customWeeklyCashflow -
                hypotheticalRateCostWeekly -
                (superBoostWeekly.toDouble() * 0.68)
        var propAssetAdd = 0.0
        var investAssetAdd = 0.0
        var otherAssetAdd = 0.0
        var liabAdd = 0.0
        var milestoneCashOutflow = 0.0

        activeMilestones.forEach { m ->
            if (year >= m.startYear && year < m.startYear + m.durationYears) {
                activeWeeklyDelta += m.weeklyCashflowImpact
            }
            if (year == m.startYear) {
                if (m.assetImpact < 0.0) {
                    // Cash purchase / lifestyle spend comes from liquid cash rather than creating negative asset value
                    milestoneCashOutflow += -m.assetImpact
                } else {
                    when (m.category) {
                        MilestoneCategory.PROPERTY -> propAssetAdd += m.assetImpact
                        MilestoneCategory.INVESTMENT -> investAssetAdd += m.assetImpact
                        else -> otherAssetAdd += m.assetImpact
                    }
                    // Property purchases with a loan require a cash deposit (asset - loan) from liquid reserves
                    if (m.category == MilestoneCategory.PROPERTY && m.assetImpact > 0.0 && m.liabilityImpact > 0.0) {
                        milestoneCashOutflow += (m.assetImpact - m.liabilityImpact).coerceAtLeast(0.0)
                    }
                }
                liabAdd += m.liabilityImpact
            }
        }

        // Deduct upfront milestone deposits / cash purchases from liquid cash before offset/HISA interest
        bLiquid -= milestoneCashOutflow

        // Base pre-loan surplus + non-loan weekly deltas, inflating at 3% p.a.
        var nonLoanAnnualB = (basePreLoanSurplusWeekly + activeWeeklyDelta) * 52.0
        if (year > 1) {
            nonLoanAnnualB *= 1.03.pow((year - 1).toDouble())
        }

        // Concessional super salary sacrifice: 15% super contributions tax, compounding at 7.5% p.a.
        val annualNetSuperBoost = superBoostWeekly.toDouble() * 0.85 * 52.0

        bProp = (bProp + propAssetAdd) * 1.04
        bSharesSuper = (bSharesSuper + investAssetAdd + customAnnualAssetBoost) * 1.075
        bExtraSuper = (bExtraSuper + annualNetSuperBoost) * 1.075
        bOther += otherAssetAdd

        var remainingOffsetB = bLiquid.coerceAtLeast(0.0)
        var paid1B = 0.0
        if (bP1Bal > 0.0) {
            val offset1B = min(bP1Bal, remainingOffsetB)
            remainingOffsetB -= offset1B
            val interest1B = (bP1Bal - offset1B) * (effRate1 / 100.0)
            paid1B = min(p1AnnualPmtShocked, bP1Bal + interest1B)
            bP1Bal = (bP1Bal + interest1B - paid1B).coerceAtLeast(0.0)
        }
        var paid2B = 0.0
        if (bP2Bal > 0.0) {
            val offset2B = min(bP2Bal, remainingOffsetB)
            remainingOffsetB -= offset2B
            val interest2B = (bP2Bal - offset2B) * (effRate2 / 100.0)
            paid2B = min(p2AnnualPmtShocked, bP2Bal + interest2B)
            bP2Bal = (bP2Bal + interest2B - paid2B).coerceAtLeast(0.0)
        }

        val hisaInterestB = remainingOffsetB * 0.045
        bLiquid += nonLoanAnnualB - paid1B - paid2B + hisaInterestB

        if (bLiquid < 0.0 && foundDeficitYear == null) {
            foundDeficitYear = year
        }

        val combinedExtraLiab = (bExtraLiab + liabAdd).coerceAtLeast(0.0)
        val extraPaydownB = (combinedExtraLiab * 0.035).coerceAtMost(combinedExtraLiab)
        bExtraLiab = (combinedExtraLiab - extraPaydownB).coerceAtLeast(0.0)

        val totalLiabB = bP1Bal + bP2Bal + bExtraLiab
        val nwB = (bProp + bSharesSuper + bExtraSuper + bOther + bLiquid) - totalLiabB
        pathBSeries.add(nwB)
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
    onDeleteAccountUser: () -> Unit = {},
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
            colorPropertiesHex = sharedPrefs.getLong("color_properties", 0xFFE38391L),
            colorSuperSharesHex = sharedPrefs.getLong("color_supershares", 0xFF38BDF8L),
            colorOtherAssetsHex = sharedPrefs.getLong("color_otherassets", 0xFFC084FCL),
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

    val person1Color = Color(appConfig.person1ColorHex)
    val person2Color = Color(appConfig.person2ColorHex)

    fun updateFirestore(key: String, value: Any) {
        userDocRef.update(key, value).addOnFailureListener {
            userDocRef.set(hashMapOf(key to value), SetOptions.merge())
        }
    }

    fun loadLearnedFacts(): List<LearnedUserFact> {
        val raw = sharedPrefs.getString("ai_learned_facts", null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            val list = mutableListOf<LearnedUserFact>()
            for (i in 0 until arr.length()) {
                list.add(LearnedUserFact.fromJson(arr.getJSONObject(i)))
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun persistLearnedFactsToPrefs(facts: List<LearnedUserFact>) {
        try {
            val arr = JSONArray()
            facts.forEach { arr.put(it.toJson()) }
            sharedPrefs.edit { putString("ai_learned_facts", arr.toString()) }
        } catch (_: Exception) {}
    }

    fun loadConversationDigest(): String {
        return sharedPrefs.getString("ai_conversation_digest", "") ?: ""
    }

    var learnedFacts by remember(activeHouseholdUid) {
        mutableStateOf(loadLearnedFacts())
    }
    var conversationDigest by remember(activeHouseholdUid) {
        mutableStateOf(loadConversationDigest())
    }

    fun saveLearnedFacts(facts: List<LearnedUserFact>) {
        persistLearnedFactsToPrefs(facts)
        val serialized = facts.map { f ->
            hashMapOf<String, Any>(
                "id" to f.id,
                "category" to f.category,
                "fact" to f.fact,
                "timestamp" to f.timestamp
            )
        }
        updateFirestore("ai_learned_facts", serialized)
    }

    fun saveConversationDigest(digest: String) {
        conversationDigest = digest
        sharedPrefs.edit { putString("ai_conversation_digest", digest) }
        updateFirestore("ai_conversation_digest", digest)
    }

    val geminiMessages = remember(activeHouseholdUid) {
        // Ephemeral in-session chat: starts fresh when app opens
        mutableStateListOf<ChatMessage>()
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
                        iconName = o.optString("iconName", "Default"),
                        customSubtitle = o.optString("customSubtitle", "").takeIf { it.isNotBlank() },
                        billingCycle = BillingCycle.fromName(o.optString("billingCycle", "WEEKLY")),
                        nextDueDateEpochMillis = if (o.has("nextDueDateEpochMillis") && !o.isNull("nextDueDateEpochMillis")) {
                            o.optLong("nextDueDateEpochMillis", 0L).takeIf { it > 0L }
                        } else null,
                        linkedAssetId = o.optString("linkedAssetId", "").takeIf { it.isNotBlank() }
                    )
                )
            }
            if (list.isEmpty()) defaults else list
        } catch (_: Exception) {
            defaults
        }
    }

    fun loadSharesFromPrefs(defaults: List<ShareParcel>): List<ShareParcel> {
        val raw = sharedPrefs.getString("local_shares_portfolio", null) ?: return defaults
        return try {
            val arr = JSONArray(raw)
            val list = mutableListOf<ShareParcel>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                list.add(
                    ShareParcel(
                        id = o.optString("id", "sp_$i"),
                        code = o.getString("code"),
                        owner = o.optString("owner", appConfig.person1Name),
                        units = o.getDouble("units"),
                        purchasePrice = o.getDouble("purchasePrice"),
                        currentPrice = o.optDouble("currentPrice", o.getDouble("purchasePrice")),
                        purchaseDate = o.optString("purchaseDate", ""),
                        market = o.optString("market", "ASX").uppercase().takeIf { it == "US" } ?: "ASX",
                        usdPurchasePrice = o.optDouble("usdPurchasePrice", 0.0),
                        usdCurrentPrice = o.optDouble("usdCurrentPrice", 0.0),
                        fxRateAtSync = o.optDouble("fxRateAtSync", 0.0)
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
            obj.put("billingCycle", it.billingCycle.name)
            if (it.nextDueDateEpochMillis != null && it.nextDueDateEpochMillis > 0L) {
                obj.put("nextDueDateEpochMillis", it.nextDueDateEpochMillis)
            }
            if (!it.linkedAssetId.isNullOrBlank()) {
                obj.put("linkedAssetId", it.linkedAssetId)
            }
            if (!it.customSubtitle.isNullOrBlank()) {
                obj.put("customSubtitle", it.customSubtitle)
            }
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
                "iconName" to it.iconName,
                "billingCycle" to it.billingCycle.name,
                "nextDueDateEpochMillis" to (it.nextDueDateEpochMillis ?: 0L),
                "linkedAssetId" to (it.linkedAssetId ?: "")
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
    var person1Savings by remember { mutableDoubleStateOf(sharedPrefs.getFloat("person1_savings", 0f).toDouble()) }
    var person2Savings by remember { mutableDoubleStateOf(sharedPrefs.getFloat("person2_savings", 0f).toDouble()) }

    var person1Items by remember { mutableStateOf(loadListFromPrefs("person1_items", defaultPerson1Items)) }
    var person2Items by remember { mutableStateOf(loadListFromPrefs("person2_items", defaultPerson2Items)) }
    var prop1Items by remember { mutableStateOf(loadListFromPrefs("prop1_items", defaultProp1Items)) }
    var prop2Items by remember { mutableStateOf(loadListFromPrefs("prop2_items", defaultProp2Items)) }
    var sharesList by remember { mutableStateOf(loadSharesFromPrefs(defaultShares)) }
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
        try {
            val arr = JSONArray()
            list.forEach {
                val obj = JSONObject()
                obj.put("id", it.id)
                obj.put("code", it.code)
                obj.put("owner", it.owner)
                obj.put("units", it.units)
                obj.put("purchasePrice", it.purchasePrice)
                obj.put("currentPrice", it.currentPrice)
                obj.put("purchaseDate", it.purchaseDate)
                obj.put("market", it.market)
                obj.put("usdPurchasePrice", it.usdPurchasePrice)
                obj.put("usdCurrentPrice", it.usdCurrentPrice)
                obj.put("fxRateAtSync", it.fxRateAtSync)
                arr.put(obj)
            }
            sharedPrefs.edit { putString("local_shares_portfolio", arr.toString()) }
        } catch (_: Exception) {}

        val serializedList = list.map {
            hashMapOf(
                "id" to it.id,
                "code" to it.code,
                "owner" to it.owner,
                "units" to it.units,
                "purchasePrice" to it.purchasePrice,
                "currentPrice" to it.currentPrice,
                "purchaseDate" to it.purchaseDate,
                "market" to it.market,
                "usdPurchasePrice" to it.usdPurchasePrice,
                "usdCurrentPrice" to it.usdCurrentPrice,
                "fxRateAtSync" to it.fxRateAtSync
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
            if (snapshot.metadata.hasPendingWrites()) return@addSnapshotListener
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
            (data["person1_savings"] as? Number)?.toDouble()?.let {
                person1Savings = it
                sharedPrefs.edit { putFloat("person1_savings", it.toFloat()) }
            }
            (data["person2_savings"] as? Number)?.toDouble()?.let {
                person2Savings = it
                sharedPrefs.edit { putFloat("person2_savings", it.toFloat()) }
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
                            iconName = it["iconName"] as? String ?: "Default",
                            billingCycle = BillingCycle.fromName(it["billingCycle"] as? String),
                            nextDueDateEpochMillis = (it["nextDueDateEpochMillis"] as? Number)?.toLong()?.takeIf { ms -> ms > 0L },
                            linkedAssetId = (it["linkedAssetId"] as? String)?.takeIf { s -> s.isNotBlank() }
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
                val parsedShares = sharesRaw.mapNotNull {
                    try {
                        ShareParcel(
                            id = it["id"] as? String ?: return@mapNotNull null,
                            code = it["code"] as? String ?: return@mapNotNull null,
                            owner = it["owner"] as? String ?: appConfig.person1Name,
                            units = (it["units"] as? Number)?.toDouble() ?: 0.0,
                            purchasePrice = (it["purchasePrice"] as? Number)?.toDouble() ?: 0.0,
                            currentPrice = (it["currentPrice"] as? Number)?.toDouble() ?: 0.0,
                            purchaseDate = it["purchaseDate"] as? String ?: "",
                            market = (it["market"] as? String)?.uppercase()?.takeIf { m -> m == "US" } ?: "ASX",
                            usdPurchasePrice = (it["usdPurchasePrice"] as? Number)?.toDouble() ?: 0.0,
                            usdCurrentPrice = (it["usdCurrentPrice"] as? Number)?.toDouble() ?: 0.0,
                            fxRateAtSync = (it["fxRateAtSync"] as? Number)?.toDouble() ?: 0.0
                        )
                    } catch (_: Exception) { null }
                }
                sharesList = parsedShares
                try {
                    val arr = JSONArray()
                    parsedShares.forEach {
                        val obj = JSONObject()
                        obj.put("id", it.id)
                        obj.put("code", it.code)
                        obj.put("owner", it.owner)
                        obj.put("units", it.units)
                        obj.put("purchasePrice", it.purchasePrice)
                        obj.put("currentPrice", it.currentPrice)
                        obj.put("purchaseDate", it.purchaseDate)
                        obj.put("market", it.market)
                        obj.put("usdPurchasePrice", it.usdPurchasePrice)
                        obj.put("usdCurrentPrice", it.usdCurrentPrice)
                        obj.put("fxRateAtSync", it.fxRateAtSync)
                        arr.put(obj)
                    }
                    sharedPrefs.edit { putString("local_shares_portfolio", arr.toString()) }
                } catch (_: Exception) {}
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

            @Suppress("UNCHECKED_CAST")
            val remoteFacts = data["ai_learned_facts"] as? List<Map<String, Any>>
            if (remoteFacts != null) {
                val parsed = remoteFacts.mapNotNull { m ->
                    val factText = m["fact"] as? String ?: return@mapNotNull null
                    LearnedUserFact(
                        id = m["id"] as? String ?: java.util.UUID.randomUUID().toString(),
                        category = m["category"] as? String ?: "General",
                        fact = factText,
                        timestamp = (m["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
                    )
                }
                if (parsed.isNotEmpty()) {
                    learnedFacts = parsed
                    persistLearnedFactsToPrefs(parsed)
                }
            }

            val remoteDigest = data["ai_conversation_digest"] as? String
            if (!remoteDigest.isNullOrBlank() && remoteDigest != conversationDigest) {
                conversationDigest = remoteDigest
                sharedPrefs.edit { putString("ai_conversation_digest", remoteDigest) }
            }
        }
    }

    if (isBiometricEnabled && !isBiometricAuthenticated) {
        BiometricLockScreen(
            onUnlockClick = {
                val fragmentActivity = context as? FragmentActivity
                if (fragmentActivity != null) {
                    showBiometricPrompt(
                        activity = fragmentActivity,
                        title = "Unlock COINLENS",
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
                    title = "Unlock COINLENS",
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

    // Smooth tab navigation helper: optimizes multi-tab jumps by skipping intermediate rendering
    val navigateToTab: (Int) -> Unit = remember(pagerState, activeSections.size, coroutineScope) {
        { targetIndex ->
            if (targetIndex in activeSections.indices && targetIndex != pagerState.currentPage) {
                coroutineScope.launch {
                    val currentIndex = pagerState.currentPage
                    if (abs(currentIndex - targetIndex) > 1) {
                        val intermediate = if (targetIndex > currentIndex) targetIndex - 1 else targetIndex + 1
                        pagerState.scrollToPage(intermediate)
                    }
                    pagerState.animateScrollToPage(
                        page = targetIndex,
                        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
                    )
                }
            }
        }
    }

    // Keep currentSection in sync when user swipes horizontally
    val currentSection by remember(pagerState, activeSections) {
        derivedStateOf {
            activeSections.getOrElse(pagerState.currentPage) { activeSections.first() }
        }
    }

    // Dynamic Mortgage Repayments (consistent across all tabs using loan's originalTermYears & showLoans toggle)
    val prop1FortnightlyPmt = remember(prop1Loan) { calculateEffectiveFortnightlyRepayment(prop1Loan) }
    val prop1WeeklyMortgage = remember(appConfig.showLoans, appConfig.isProperty1Rented, prop1FortnightlyPmt) {
        if (appConfig.showLoans && !appConfig.isProperty1Rented) -(prop1FortnightlyPmt / 2.0) else 0.0
    }

    val prop2FortnightlyPmt = remember(prop2Loan, appConfig.hasSecondProperty) {
        if (appConfig.hasSecondProperty) calculateEffectiveFortnightlyRepayment(prop2Loan) else 0.0
    }
    val prop2WeeklyMortgage = remember(appConfig.showLoans, appConfig.hasSecondProperty, appConfig.isProperty2Rented, prop2FortnightlyPmt) {
        if (appConfig.showLoans && appConfig.hasSecondProperty && !appConfig.isProperty2Rented) -(prop2FortnightlyPmt / 2.0) else 0.0
    }

    val p1Schedule = remember(prop1Loan) { generateAmortizationSchedule(prop1Loan, 780) }
    val prop1LiveBalance = remember(appConfig.showLoans, appConfig.isProperty1Rented, prop1Loan.startDateStr, p1Schedule) {
        val p1AutoFortnights = getElapsedFortnightsSinceStart(prop1Loan.startDateStr)
        val p1CurrentRow = p1Schedule.getOrNull(p1AutoFortnights.coerceIn(0, p1Schedule.size - 1)) ?: p1Schedule.first()
        if (appConfig.showLoans && !appConfig.isProperty1Rented) p1CurrentRow.closingBalance else 0.0
    }

    val p2Schedule = remember(prop2Loan) { generateAmortizationSchedule(prop2Loan, 780) }
    val prop2LiveBalance = remember(appConfig.showLoans, appConfig.hasSecondProperty, appConfig.isProperty2Rented, prop2Loan.startDateStr, p2Schedule) {
        val p2AutoFortnights = getElapsedFortnightsSinceStart(prop2Loan.startDateStr)
        val p2CurrentRow = p2Schedule.getOrNull(p2AutoFortnights.coerceIn(0, p2Schedule.size - 1)) ?: p2Schedule.first()
        if (appConfig.showLoans && appConfig.hasSecondProperty && !appConfig.isProperty2Rented) p2CurrentRow.closingBalance else 0.0
    }

    val prop1HousingCost = remember(appConfig.isProperty1Rented, appConfig.primaryRentWeekly, prop1WeeklyMortgage) {
        if (appConfig.isProperty1Rented) -appConfig.primaryRentWeekly else prop1WeeklyMortgage
    }
    val prop1NetWeekly = remember(prop1HousingCost, prop1Items) {
        prop1HousingCost + prop1Items.sumOf { it.weeklyAmount }
    }

    val prop2RentIncome = remember(appConfig.hasSecondProperty, appConfig.secondaryRentWeekly) {
        if (appConfig.hasSecondProperty) appConfig.secondaryRentWeekly else 0.0
    }
    val prop2Mortgage = remember(appConfig.hasSecondProperty, prop2WeeklyMortgage) {
        if (appConfig.hasSecondProperty) prop2WeeklyMortgage else 0.0
    }
    val prop2Expenses = remember(appConfig.hasSecondProperty, prop2Items) {
        if (appConfig.hasSecondProperty) prop2Items.sumOf { it.weeklyAmount } else 0.0
    }
    val prop2NetWeekly = remember(prop2RentIncome, prop2Mortgage, prop2Expenses) {
        prop2RentIncome + prop2Mortgage + prop2Expenses
    }

    val combinedPropertyNet = remember(prop1NetWeekly, prop2NetWeekly) {
        prop1NetWeekly + prop2NetWeekly
    }
    val splitDivisor = remember(appConfig.isCoupleMode) {
        if (appConfig.isCoupleMode) 2.0 else 1.0
    }
    val perPersonPropertyShare = remember(combinedPropertyNet, splitDivisor) {
        combinedPropertyNet / splitDivisor
    }

    // Hoisted What-If Simulation Controls, Custom Levers & Milestone State (Empty by default so user adds what they like)
    var whatIfHorizonYears by rememberSaveable { mutableIntStateOf(15) }
    var whatIfLifestyleAdjustmentWeekly by rememberSaveable { mutableFloatStateOf(0f) }
    var whatIfIncomeGrowthP1Weekly by rememberSaveable { mutableFloatStateOf(0f) }
    var whatIfIncomeGrowthP2Weekly by rememberSaveable { mutableFloatStateOf(0f) }
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
    val prop1Val = remember(appConfig.showProperties, appConfig.isProperty1Rented, prop1EstimatedValue) {
        if (appConfig.showProperties && !appConfig.isProperty1Rented) prop1EstimatedValue else 0.0
    }
    val prop2Val = remember(appConfig.showProperties, appConfig.hasSecondProperty, appConfig.isProperty2Rented, prop2EstimatedValue) {
        if (appConfig.showProperties && appConfig.hasSecondProperty && !appConfig.isProperty2Rented) prop2EstimatedValue else 0.0
    }
    val superVal = remember(appConfig.showSuperAndShares, superAccountsList) {
        if (appConfig.showSuperAndShares) superAccountsList.sumOf { it.balance } else 0.0
    }
    val sharesVal = remember(appConfig.showSuperAndShares, sharesList) {
        if (appConfig.showSuperAndShares) sharesList.sumOf { it.units * it.currentPrice } else 0.0
    }
    val otherVal = remember(appConfig.showOtherAssets, otherAssetsList) {
        if (appConfig.showOtherAssets) otherAssetsList.sumOf { it.value } else 0.0
    }
    val offsetsVal = remember(appConfig.showLoans, appConfig.isProperty1Rented, appConfig.hasSecondProperty, appConfig.isProperty2Rented, prop1Loan.offsetBalance, prop2Loan.offsetBalance) {
        if (appConfig.showLoans && !appConfig.isProperty1Rented) prop1Loan.offsetBalance + (if (appConfig.hasSecondProperty && !appConfig.isProperty2Rented) prop2Loan.offsetBalance else 0.0) else 0.0
    }
    val cashSavingsVal = remember(appConfig.isProperty1Rented, appConfig.isCoupleMode, person1Savings, person2Savings) {
        if (appConfig.isProperty1Rented) person1Savings + (if (appConfig.isCoupleMode) person2Savings else 0.0) else 0.0
    }

    val exactTotalAssets = remember(prop1Val, prop2Val, superVal, sharesVal, otherVal, offsetsVal, cashSavingsVal) {
        prop1Val + prop2Val + superVal + sharesVal + otherVal + offsetsVal + cashSavingsVal
    }
    val exactTotalLiabilities = remember(prop1LiveBalance, prop2LiveBalance) {
        prop1LiveBalance + prop2LiveBalance
    }
    val exactNetWorth = remember(exactTotalAssets, exactTotalLiabilities) {
        exactTotalAssets - exactTotalLiabilities
    }

    val p1TotalExpensesContext = remember(perPersonPropertyShare, person1Items) {
        perPersonPropertyShare + person1Items.sumOf { it.weeklyAmount }
    }
    val p1SurplusContext = remember(person1Income, p1TotalExpensesContext) {
        person1Income + p1TotalExpensesContext
    }
    val p2TotalExpensesContext = remember(appConfig.isCoupleMode, perPersonPropertyShare, person2Items) {
        if (appConfig.isCoupleMode) (perPersonPropertyShare + person2Items.sumOf { it.weeklyAmount }) else 0.0
    }
    val p2SurplusContext = remember(appConfig.isCoupleMode, person2Income, p2TotalExpensesContext) {
        if (appConfig.isCoupleMode) (person2Income + p2TotalExpensesContext) else 0.0
    }
    val combinedSurplusContext = remember(p1SurplusContext, p2SurplusContext) {
        p1SurplusContext + p2SurplusContext
    }

    val p1ExpensesListStr = remember(person1Items) {
        person1Items.joinToString { "${it.name}: ${formatCurrency(abs(it.weeklyAmount), 2)}/wk" }
    }
    val p2ExpensesListStr = remember(appConfig.isCoupleMode, person2Items) {
        if (appConfig.isCoupleMode) person2Items.joinToString { "${it.name}: ${formatCurrency(abs(it.weeklyAmount), 2)}/wk" } else ""
    }

    val whatIfBaseSurplusWeekly = remember(person1Items, person2Items, person1Income, person2Income, appConfig.isCoupleMode, combinedPropertyNet) {
        val p1Exp = person1Items.sumOf { it.weeklyAmount }
        val p2Exp = if (appConfig.isCoupleMode) person2Items.sumOf { it.weeklyAmount } else 0.0
        val p1Surp = person1Income + p1Exp
        val p2Surp = if (appConfig.isCoupleMode) (person2Income + p2Exp) else 0.0
        p1Surp + p2Surp + combinedPropertyNet
    }

    val totalWhatIfIncomeGrowthWeekly = remember(whatIfIncomeGrowthP1Weekly, whatIfIncomeGrowthP2Weekly, appConfig.isCoupleMode) {
        whatIfIncomeGrowthP1Weekly + (if (appConfig.isCoupleMode) whatIfIncomeGrowthP2Weekly else 0f)
    }

    // Unified Multi-Year What-If Projection (up to 30 years so AI can answer 1Y, 5Y, 10Y, 15Y, 20Y, 30Y questions)
    val maxProjectionHorizon = max(whatIfHorizonYears, 30)
    val fullWhatIfProj = remember(
        maxProjectionHorizon,
        prop1Val, prop2Val, sharesVal, superVal, otherVal, offsetsVal, cashSavingsVal,
        exactTotalLiabilities, whatIfBaseSurplusWeekly, prop1Loan, prop2Loan, prop1LiveBalance, prop2LiveBalance,
        appConfig.hasSecondProperty, appConfig.showLoans, appConfig.isProperty1Rented, appConfig.isProperty2Rented,
        whatIfLifestyleAdjustmentWeekly, totalWhatIfIncomeGrowthWeekly,
        whatIfRateShockProp1, whatIfRateShockProp2, whatIfRentalGrowthWeekly,
        whatIfSuperBoostWeekly, whatIfCustomLevers.toList(), whatIfMilestoneList.toList()
    ) {
        computeWhatIfProjection(
            horizonYears = maxProjectionHorizon,
            basePropertyVal = prop1Val + prop2Val,
            baseSharesAndSuperVal = sharesVal + superVal,
            baseOtherAssetsVal = otherVal,
            baseOffsetsVal = offsetsVal + cashSavingsVal,
            baseLiabilitiesVal = exactTotalLiabilities,
            baseSurplusWeekly = whatIfBaseSurplusWeekly,
            prop1Loan = prop1Loan,
            prop2Loan = prop2Loan,
            hasSecondProperty = appConfig.hasSecondProperty,
            showLoans = appConfig.showLoans && !appConfig.isProperty1Rented,
            lifestyleAdjustmentWeekly = whatIfLifestyleAdjustmentWeekly,
            incomeGrowthWeekly = totalWhatIfIncomeGrowthWeekly,
            rateShockProp1 = if (appConfig.isProperty1Rented) 0f else whatIfRateShockProp1,
            rateShockProp2 = if (appConfig.isProperty1Rented) 0f else whatIfRateShockProp2,
            rentalIncomeGrowthWeekly = if (appConfig.isProperty1Rented) -whatIfRentalGrowthWeekly else whatIfRentalGrowthWeekly,
            superBoostWeekly = whatIfSuperBoostWeekly,
            customLevers = whatIfCustomLevers,
            milestones = whatIfMilestoneList,
            prop1LiveBalance = prop1LiveBalance,
            prop2LiveBalance = prop2LiveBalance,
            isProperty2Rented = appConfig.isProperty2Rented
        )
    }

    val whatIfEndPathANetWorth = fullWhatIfProj.pathASeries.getOrElse(whatIfHorizonYears) { exactNetWorth }
    val whatIfEndPathBNetWorth = fullWhatIfProj.pathBSeries.getOrElse(whatIfHorizonYears) { exactNetWorth }
    val whatIfNetDifference = whatIfEndPathBNetWorth - whatIfEndPathANetWorth
    val whatIfDeficitYear = fullWhatIfProj.deficitYear?.takeIf { it <= whatIfHorizonYears }

    val prop1ItemsStr = remember(prop1Items) {
        prop1Items.joinToString { "${it.name}: ${formatCurrency(abs(it.weeklyAmount), 2)}/wk" }
    }
    val prop2ItemsStr = remember(appConfig.hasSecondProperty, prop2Items) {
        if (appConfig.hasSecondProperty) prop2Items.joinToString { "${it.name}: ${formatCurrency(abs(it.weeklyAmount), 2)}/wk" } else ""
    }

    val detailedFinancialContext = remember(
        activeLoggedInPersonName, isLoggedInAsPartner, currentUser.email, appConfig,
        exactNetWorth, exactTotalAssets, exactTotalLiabilities,
        prop1Val, prop2Val, sharesVal, superVal, otherVal, offsetsVal, cashSavingsVal,
        prop1LiveBalance, prop2LiveBalance,
        person1Income, person2Income, person1Savings, person2Savings,
        p1TotalExpensesContext, p2TotalExpensesContext, p1SurplusContext, p2SurplusContext, combinedSurplusContext,
        p1ExpensesListStr, p2ExpensesListStr, prop1ItemsStr, prop2ItemsStr,
        prop1Loan, prop2Loan, superAccountsList, sharesList, otherAssetsList,
        whatIfHorizonYears, whatIfLifestyleAdjustmentWeekly, whatIfIncomeGrowthP1Weekly, whatIfIncomeGrowthP2Weekly,
        whatIfRateShockProp1, whatIfRateShockProp2, whatIfSuperBoostWeekly, whatIfRentalGrowthWeekly,
        whatIfCustomLevers.toList(), whatIfMilestoneList.toList(),
        whatIfEndPathANetWorth, whatIfEndPathBNetWorth, whatIfNetDifference, whatIfDeficitYear, fullWhatIfProj
    ) {
        buildString {
            append("CURRENTLY LOGGED-IN USER SPEAKING TO YOU: $activeLoggedInPersonName (${if (isLoggedInAsPartner) "Partner / Person 2, Email: ${currentUser.email ?: appConfig.partnerEmail}" else "Primary Account Holder / Person 1, Email: ${currentUser.email ?: appConfig.primaryOwnerEmail}"}). ")
            append("IMPORTANT RULE: Always address the user directly by their own name '$activeLoggedInPersonName' (NEVER call them '${if (isLoggedInAsPartner) appConfig.person1Name else appConfig.person2Name}'). When they say 'my income', 'my expenses', 'my super', or 'my shares', they mean $activeLoggedInPersonName's (${if (isLoggedInAsPartner) "Person 2" else "Person 1"}) items. ")
            append("EXPLICIT COMPUTED FINANCIAL TOTALS & UNMASKED GLOBAL APP DATA (CURRENT & FUTURE): ")
            append("Exact Net Worth Today = ${formatCurrency(exactNetWorth, 2)}. ")
            append("Exact Total Assets Today = ${formatCurrency(exactTotalAssets, 2)} (Properties: ${formatCurrency(prop1Val + prop2Val, 0)}, Shares: ${formatCurrency(sharesVal, 0)}, Super: ${formatCurrency(superVal, 0)}, Other Assets: ${formatCurrency(otherVal, 0)}, Offset Accounts: ${formatCurrency(offsetsVal, 0)}${if (appConfig.isProperty1Rented) ", Cash Savings: ${formatCurrency(cashSavingsVal, 0)}" else ""}). ")
            append("Exact Total Liabilities Today = ${formatCurrency(exactTotalLiabilities, 2)} (Property 1 Mortgage: ${formatCurrency(prop1LiveBalance, 0)}, Property 2 Mortgage: ${formatCurrency(prop2LiveBalance, 0)}). ")

            append("\n\n1. PERSONAL CASH FLOW TAB (UNMASKED): ")
            append("Person 1 (${appConfig.person1Name}): Income = ${formatCurrency(person1Income, 2)}/wk${if (appConfig.isProperty1Rented) ", Cash Savings = ${formatCurrency(person1Savings, 0)}" else ""}, Total Expenses (incl. property share) = ${formatCurrency(abs(p1TotalExpensesContext), 2)}/wk, Net Surplus = ${formatCurrency(p1SurplusContext, 2)}/wk. ")
            if (p1ExpensesListStr.isNotBlank()) append("Person 1 Itemized Expenses: $p1ExpensesListStr. ")

            if (appConfig.isCoupleMode) {
                append("Person 2 (${appConfig.person2Name}): Income = ${formatCurrency(person2Income, 2)}/wk${if (appConfig.isProperty1Rented) ", Cash Savings = ${formatCurrency(person2Savings, 0)}" else ""}, Total Expenses (incl. property share) = ${formatCurrency(abs(p2TotalExpensesContext), 2)}/wk, Net Surplus = ${formatCurrency(p2SurplusContext, 2)}/wk. ")
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
                append("Shares Portfolio (${sharesList.size} holdings, Total Market Value ${formatCurrency(sharesVal, 0)}, Cost Basis ${formatCurrency(totalSharesCost, 0)}, Net Gain/Loss ${formatCurrency(totalSharesGain, 0)}): ${sharesList.joinToString { "${it.code} [${it.market}] (${it.owner}, ${it.units} units @ ${formatCurrency(it.currentPrice, 2)}${if (it.market == "US" && it.usdCurrentPrice > 0) " / ${formatUsdCurrency(it.usdCurrentPrice, 2)}" else ""}, Purchase Price ${formatCurrency(it.purchasePrice, 2)}, Market Value ${formatCurrency(it.units * it.currentPrice, 0)}, Gain/Loss ${formatCurrency((it.currentPrice - it.purchasePrice) * it.units, 0)})" }}. ")
            }

            append("\n\n5. OTHER ASSETS TAB (UNMASKED): ")
            if (otherAssetsList.isNotEmpty()) {
                append("Other Assets (Total ${formatCurrency(otherVal, 0)}): ${otherAssetsList.joinToString { "${it.name} (${it.owner}): ${formatCurrency(it.value, 0)}" }}. ")
            }

            append("\n\n6. WHAT-IF SCENARIO SIMULATOR (ACTIVE LEVERS, CUSTOM LEVERS, MILESTONES & FUTURE PROJECTIONS): ")
            append("Active Timeframe = $whatIfHorizonYears Years. ")
            append("Extra Weekly Savings/Spending = ${if (whatIfLifestyleAdjustmentWeekly >= 0) "+$" else "-$"}${abs(whatIfLifestyleAdjustmentWeekly)}/wk. ")
            if (whatIfIncomeGrowthP1Weekly != 0f) {
                append("${appConfig.person1Name} Weekly Income Growth = ${if (whatIfIncomeGrowthP1Weekly >= 0) "+$" else "-$"}${abs(whatIfIncomeGrowthP1Weekly)}/wk. ")
            }
            if (appConfig.isCoupleMode && whatIfIncomeGrowthP2Weekly != 0f) {
                append("${appConfig.person2Name} Weekly Income Growth = ${if (whatIfIncomeGrowthP2Weekly >= 0) "+$" else "-$"}${abs(whatIfIncomeGrowthP2Weekly)}/wk. ")
            }
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
    }

    // Live AI Read/Write Action Executor — allows the AI to edit any mutable data in the app when asked
    fun executeAiActions(rawResponse: String): Pair<String, List<String>> {
        val actionRegex = Regex("""\[{1,2}\s*ACTION\s*:\s*([^]]+)]{1,2}""", RegexOption.IGNORE_CASE)
        val appliedSummaries = mutableListOf<String>()

        fun parseCleanDouble(raw: String?): Double? {
            if (raw.isNullOrBlank()) return null
            var s = raw.trim().replace("$", "").replace(",", "").replace("%", "").lowercase()
            var factor = 1.0
            when {
                s.endsWith("/wk") || s.endsWith("/week") || s.endsWith("pw") -> {
                    s = s.substringBefore("/").substringBefore("pw").trim()
                }
                s.endsWith("/fortnight") || s.endsWith("/fn") || s.endsWith("pf") -> {
                    s = s.substringBefore("/").substringBefore("pf").trim()
                    factor = 1.0 / 2.0
                }
                s.endsWith("/month") || s.endsWith("/mo") || s.endsWith("/m") -> {
                    s = s.substringBefore("/").substringBefore("m").substringBefore("o").trim()
                    factor = 12.0 / 52.0
                }
                s.endsWith("/yr") || s.endsWith("/year") || s.endsWith("/pa") || s.endsWith("pa") -> {
                    s = s.substringBefore("/").substringBefore("pa").trim()
                    factor = 1.0 / 52.0
                }
                s.endsWith("k") -> {
                    s = s.removeSuffix("k").trim()
                    factor = 1000.0
                }
                s.endsWith("m") -> {
                    s = s.removeSuffix("m").trim()
                    factor = 1000000.0
                }
            }
            val d = s.toDoubleOrNull() ?: return null
            return d * factor
        }

        fun parseCleanRawDouble(raw: String?): Double? {
            if (raw.isNullOrBlank()) return null
            var s = raw.trim().replace("$", "").replace(",", "").replace("%", "").lowercase()
            var multiplier = 1.0
            if (s.endsWith("k")) {
                s = s.removeSuffix("k").trim()
                multiplier = 1000.0
            } else if (s.endsWith("m")) {
                s = s.removeSuffix("m").trim()
                multiplier = 1000000.0
            }
            val d = s.toDoubleOrNull() ?: return null
            return d * multiplier
        }

        fun parseCleanRawFloat(raw: String?): Float? {
            return parseCleanRawDouble(raw)?.toFloat()
        }

        fun parseCleanFloat(raw: String?): Float? {
            return parseCleanDouble(raw)?.toFloat()
        }

        fun resolvePersonIsTwo(rawTarget: String?): Boolean {
            if (!appConfig.isCoupleMode) return false
            val t = rawTarget?.trim()?.lowercase() ?: return isLoggedInAsPartner
            return when {
                t == "2" || t == "person2" || t == "p2" || t == "partner" -> true
                t == "1" || t == "person1" || t == "p1" || t == "primary" -> false
                t == "me" || t == "myself" -> isLoggedInAsPartner
                appConfig.person2Name.isNotBlank() && t.contains(appConfig.person2Name.trim(), ignoreCase = true) -> true
                appConfig.person1Name.isNotBlank() && t.contains(appConfig.person1Name.trim(), ignoreCase = true) -> false
                else -> isLoggedInAsPartner
            }
        }

        fun resolveOwnerName(rawOwner: String?): String {
            val o = rawOwner?.trim()
            if (o.isNullOrBlank() || o.equals("Me", ignoreCase = true) || o.equals("Myself", ignoreCase = true)) {
                return activeLoggedInPersonName
            }
            if (o == "1" || o.equals("person1", ignoreCase = true)) return appConfig.person1Name
            if (o == "2" || o.equals("person2", ignoreCase = true)) return if (appConfig.isCoupleMode) appConfig.person2Name else appConfig.person1Name
            return o
        }

        actionRegex.findAll(rawResponse).forEach { match ->
            val payload = match.groupValues[1].trim()
            val parts = payload.split(Regex("""[|\n]""")).map { it.trim() }.filter { it.isNotEmpty() }
            if (parts.isEmpty()) return@forEach
            val rawActionType = parts.first().substringBefore("=").substringBefore(":").trim().uppercase()
            val actionType = rawActionType.removePrefix("ACTION:")
            val params = mutableMapOf<String, String>()
            parts.forEachIndexed { idx, part ->
                if (idx == 0 && !part.contains("=") && !part.contains(":")) {
                    return@forEachIndexed
                }
                val separatorIdx = part.indexOf('=').takeIf { it > 0 } ?: part.indexOf(':').takeIf { it > 0 } ?: -1
                if (separatorIdx > 0) {
                    val k = part.substring(0, separatorIdx).trim().lowercase()
                    val v = part.substring(separatorIdx + 1).trim()
                    params[k] = v
                }
            }

            when (actionType) {
                "SET_INCOME", "UPDATE_INCOME" -> {
                    val isP2 = resolvePersonIsTwo(params["person"] ?: params["target"])
                    val amount = parseCleanDouble(params["amount"] ?: params["weeklyamount"] ?: params["income"] ?: params["value"])
                    if (amount != null && amount >= 0.0) {
                        if (isP2) {
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
                "SET_SAVINGS", "UPDATE_SAVINGS", "SET_SAVINGS_GOAL", "UPDATE_SAVINGS_GOAL" -> {
                    val isP2 = resolvePersonIsTwo(params["person"] ?: params["target"])
                    val amount = parseCleanRawDouble(params["amount"] ?: params["weeklyamount"] ?: params["savings"] ?: params["value"] ?: params["target"])
                    if (amount != null && amount >= 0.0) {
                        if (isP2) {
                            person2Savings = amount
                            sharedPrefs.edit { putFloat("person2_savings", amount.toFloat()) }
                            updateFirestore("person2_savings", amount)
                            appliedSummaries.add("Updated ${appConfig.person2Name} savings target to ${formatCurrency(amount, 0)}")
                        } else {
                            person1Savings = amount
                            sharedPrefs.edit { putFloat("person1_savings", amount.toFloat()) }
                            updateFirestore("person1_savings", amount)
                            appliedSummaries.add("Updated ${appConfig.person1Name} savings target to ${formatCurrency(amount, 0)}")
                        }
                    }
                }
                "ADD_CASHFLOW", "UPDATE_CASHFLOW", "ADD_EXPENSE", "UPDATE_EXPENSE", "ADD_BILL", "UPDATE_BILL", "ADD_LIVING_EXPENSE", "ADD_INCOME_ITEM" -> {
                    val targetRaw = (params["target"] ?: params["person"] ?: params["property"] ?: "").trim().lowercase()
                    val name = (params["name"] ?: params["title"] ?: params["item"])?.takeIf { it.isNotBlank() } ?: "Expense"
                    val rawAmt = parseCleanDouble(params["weeklyamount"] ?: params["amount"] ?: params["value"] ?: params["cost"])
                    val iconName = params["icon"] ?: params["iconname"] ?: "Receipt"
                    if (rawAmt != null) {
                        val signedAmt = if (actionType == "ADD_INCOME_ITEM") abs(rawAmt) else if (rawAmt > 0) -rawAmt else rawAmt
                        when {
                            targetRaw == "prop2" || targetRaw == "property2" || params["property"] == "2" -> {
                                val existingIdx = prop2Items.indexOfFirst { it.name.equals(name, ignoreCase = true) }
                                val updated = if (existingIdx >= 0) {
                                    prop2Items.toMutableList().apply {
                                        set(existingIdx, this[existingIdx].copy(name = name, weeklyAmount = signedAmt, iconName = iconName))
                                    }
                                } else {
                                    prop2Items + CashflowItem("pr2_${System.currentTimeMillis()}", name, signedAmt, iconName = iconName)
                                }
                                prop2Items = updated
                                saveList("prop2_items", updated)
                                appliedSummaries.add("Saved $name (${formatCurrency(abs(signedAmt), 2)}/wk) on ${appConfig.property2Name}")
                            }
                            targetRaw == "prop1" || targetRaw == "property1" || params["property"] == "1" -> {
                                val existingIdx = prop1Items.indexOfFirst { it.name.equals(name, ignoreCase = true) }
                                val updated = if (existingIdx >= 0) {
                                    prop1Items.toMutableList().apply {
                                        set(existingIdx, this[existingIdx].copy(name = name, weeklyAmount = signedAmt, iconName = iconName))
                                    }
                                } else {
                                    prop1Items + CashflowItem("pr1_${System.currentTimeMillis()}", name, signedAmt, iconName = iconName)
                                }
                                prop1Items = updated
                                saveList("prop1_items", updated)
                                appliedSummaries.add("Saved $name (${formatCurrency(abs(signedAmt), 2)}/wk) on ${appConfig.property1Name}")
                            }
                            resolvePersonIsTwo(targetRaw.ifBlank { null }) -> {
                                val existingIdx = person2Items.indexOfFirst { it.name.equals(name, ignoreCase = true) }
                                val updated = if (existingIdx >= 0) {
                                    person2Items.toMutableList().apply {
                                        set(existingIdx, this[existingIdx].copy(name = name, weeklyAmount = signedAmt, iconName = iconName))
                                    }
                                } else {
                                    person2Items + CashflowItem("p2_${System.currentTimeMillis()}", name, signedAmt, iconName = iconName)
                                }
                                person2Items = updated
                                saveList("person2_items", updated)
                                appliedSummaries.add("Saved $name (${formatCurrency(abs(signedAmt), 2)}/wk) for ${appConfig.person2Name}")
                            }
                            else -> {
                                val existingIdx = person1Items.indexOfFirst { it.name.equals(name, ignoreCase = true) }
                                val updated = if (existingIdx >= 0) {
                                    person1Items.toMutableList().apply {
                                        set(existingIdx, this[existingIdx].copy(name = name, weeklyAmount = signedAmt, iconName = iconName))
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
                }
                "DELETE_CASHFLOW", "DELETE_EXPENSE", "REMOVE_EXPENSE", "DELETE_BILL", "REMOVE_BILL" -> {
                    val targetRaw = (params["target"] ?: params["person"] ?: params["property"] ?: "").trim().lowercase()
                    val name = (params["name"] ?: params["title"] ?: params["item"])?.takeIf { it.isNotBlank() }
                    if (name != null) {
                        when (targetRaw) {
                            "prop2", "property2" -> {
                                prop2Items = prop2Items.filterNot { it.name.contains(name, ignoreCase = true) }
                                saveList("prop2_items", prop2Items)
                            }
                            "prop1", "property1" -> {
                                prop1Items = prop1Items.filterNot { it.name.contains(name, ignoreCase = true) }
                                saveList("prop1_items", prop1Items)
                            }
                            "person2", "2" -> {
                                person2Items = person2Items.filterNot { it.name.contains(name, ignoreCase = true) }
                                saveList("person2_items", person2Items)
                            }
                            "person1", "1" -> {
                                person1Items = person1Items.filterNot { it.name.contains(name, ignoreCase = true) }
                                saveList("person1_items", person1Items)
                            }
                            else -> {
                                val p1Filtered = person1Items.filterNot { it.name.contains(name, ignoreCase = true) }
                                if (p1Filtered.size != person1Items.size) {
                                    person1Items = p1Filtered
                                    saveList("person1_items", p1Filtered)
                                }
                                val p2Filtered = person2Items.filterNot { it.name.contains(name, ignoreCase = true) }
                                if (p2Filtered.size != person2Items.size) {
                                    person2Items = p2Filtered
                                    saveList("person2_items", p2Filtered)
                                }
                                val pr1Filtered = prop1Items.filterNot { it.name.contains(name, ignoreCase = true) }
                                if (pr1Filtered.size != prop1Items.size) {
                                    prop1Items = pr1Filtered
                                    saveList("prop1_items", pr1Filtered)
                                }
                                val pr2Filtered = prop2Items.filterNot { it.name.contains(name, ignoreCase = true) }
                                if (pr2Filtered.size != prop2Items.size) {
                                    prop2Items = pr2Filtered
                                    saveList("prop2_items", pr2Filtered)
                                }
                            }
                        }
                        appliedSummaries.add("Removed '$name'")
                    }
                }
                "UPDATE_PROPERTY", "SET_PROPERTY_VALUE", "SET_PROPERTY_RENT", "SET_RENT", "SET_PROPERTY_NAME", "ENABLE_SECOND_PROPERTY" -> {
                    if (actionType == "ENABLE_SECOND_PROPERTY") {
                        val enabled = (params["enabled"] ?: params["value"])?.toBooleanStrictOrNull() ?: true
                        val cfg = appConfig.copy(hasSecondProperty = enabled)
                        saveAppConfig(cfg)
                        appliedSummaries.add(if (enabled) "Enabled second property" else "Disabled second property")
                    } else {
                        val propNum = params["property"]?.toIntOrNull() ?: 1
                        val marketVal = parseCleanRawDouble(params["marketvalue"] ?: params["value"] ?: params["valuation"])
                        val rentVal = parseCleanDouble(params["rentweekly"] ?: params["weeklyrent"] ?: params["rent"])
                        val newName = (params["name"] ?: params["propertyname"])?.takeIf { it.isNotBlank() }
                        val isRentedFlag = (params["isrented"] ?: params["rented"])?.toBooleanStrictOrNull()

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
                }
                "ADD_PROPERTY_ITEM", "UPDATE_PROPERTY_ITEM", "ADD_PROPERTY_EXPENSE", "ADD_PROPERTY_BILL" -> {
                    val propNum = params["property"]?.toIntOrNull() ?: 1
                    val name = (params["name"] ?: params["title"])?.takeIf { it.isNotBlank() } ?: "Property Expense"
                    val rawAmt = parseCleanDouble(params["weeklyamount"] ?: params["amount"] ?: params["value"])
                    if (rawAmt != null) {
                        val signedAmt = if (rawAmt > 0) -rawAmt else rawAmt
                        if (propNum == 2) {
                            val existingIdx = prop2Items.indexOfFirst { it.name.equals(name, ignoreCase = true) }
                            val updated = if (existingIdx >= 0) {
                                prop2Items.toMutableList().apply { set(existingIdx, this[existingIdx].copy(name = name, weeklyAmount = signedAmt)) }
                            } else {
                                prop2Items + CashflowItem("pr2_${System.currentTimeMillis()}", name, signedAmt)
                            }
                            prop2Items = updated
                            saveList("prop2_items", updated)
                            appliedSummaries.add("Saved $name (${formatCurrency(abs(signedAmt), 2)}/wk) on ${appConfig.property2Name}")
                        } else {
                            val existingIdx = prop1Items.indexOfFirst { it.name.equals(name, ignoreCase = true) }
                            val updated = if (existingIdx >= 0) {
                                prop1Items.toMutableList().apply { set(existingIdx, this[existingIdx].copy(name = name, weeklyAmount = signedAmt)) }
                            } else {
                                prop1Items + CashflowItem("pr1_${System.currentTimeMillis()}", name, signedAmt)
                            }
                            prop1Items = updated
                            saveList("prop1_items", updated)
                            appliedSummaries.add("Saved $name (${formatCurrency(abs(signedAmt), 2)}/wk) on ${appConfig.property1Name}")
                        }
                    }
                }
                "DELETE_PROPERTY_ITEM", "DELETE_PROPERTY_BILL", "REMOVE_PROPERTY_ITEM", "REMOVE_PROPERTY_BILL" -> {
                    val propNum = params["property"]?.toIntOrNull() ?: 1
                    val name = (params["name"] ?: params["title"])?.takeIf { it.isNotBlank() }
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
                "UPDATE_LOAN", "SET_LOAN", "UPDATE_OFFSET", "SET_OFFSET" -> {
                    val propNum = params["property"]?.toIntOrNull() ?: 1
                    val bal = parseCleanRawDouble(params["startloanbalance"] ?: params["balance"] ?: params["loanbalance"])
                    val rate = (params["interestratepa"] ?: params["rate"] ?: params["interestrate"])
                        ?.replace("%", "")?.toDoubleOrNull()
                    val offset = parseCleanRawDouble(params["offsetbalance"] ?: params["offset"] ?: params["amount"])
                    val orig = parseCleanRawDouble(params["originalloanamount"] ?: params["originalloan"])
                    val term = (params["termyears"] ?: params["originaltermyears"])?.toIntOrNull()
                    val startDate = (params["startdatestr"] ?: params["startdate"])?.takeIf { it.isNotBlank() }
                    if (propNum == 2) {
                        val updated = prop2Loan.copy(
                            startLoanBalance = bal ?: prop2Loan.startLoanBalance,
                            originalLoanAmount = orig ?: max(prop2Loan.originalLoanAmount, bal ?: prop2Loan.startLoanBalance),
                            interestRatePa = rate ?: prop2Loan.interestRatePa,
                            offsetBalance = offset ?: prop2Loan.offsetBalance,
                            originalTermYears = term ?: prop2Loan.originalTermYears,
                            startDateStr = startDate ?: prop2Loan.startDateStr
                        )
                        saveProp2Loan(updated)
                        appliedSummaries.add("Updated ${prop2Loan.name} Loan parameters")
                    } else {
                        val updated = prop1Loan.copy(
                            startLoanBalance = bal ?: prop1Loan.startLoanBalance,
                            originalLoanAmount = orig ?: max(prop1Loan.originalLoanAmount, bal ?: prop1Loan.startLoanBalance),
                            interestRatePa = rate ?: prop1Loan.interestRatePa,
                            offsetBalance = offset ?: prop1Loan.offsetBalance,
                            originalTermYears = term ?: prop1Loan.originalTermYears,
                            startDateStr = startDate ?: prop1Loan.startDateStr
                        )
                        saveProp1Loan(updated)
                        appliedSummaries.add("Updated ${prop1Loan.name} Loan parameters")
                    }
                }
                "ADD_SHARE", "UPDATE_SHARE", "SET_SHARE" -> {
                    val code = (params["code"] ?: params["ticker"] ?: params["symbol"])?.trim()?.uppercase()?.takeIf { it.isNotBlank() }
                    val owner = resolveOwnerName(params["owner"])
                    val units = (params["units"] ?: params["quantity"])?.replace(",", "")?.toDoubleOrNull()
                    val pPrice = parseCleanRawDouble(params["purchaseprice"] ?: params["buyprice"] ?: params["avgprice"])
                    val cPrice = parseCleanRawDouble(params["currentprice"] ?: params["price"])
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
                "DELETE_SHARE", "REMOVE_SHARE" -> {
                    val code = (params["code"] ?: params["ticker"] ?: params["symbol"])?.trim()
                    if (!code.isNullOrBlank()) {
                        val updated = sharesList.filterNot { it.code.equals(code, ignoreCase = true) }
                        saveSharesList(updated)
                        appliedSummaries.add("Removed share holding $code")
                    }
                }
                "ADD_SUPER", "UPDATE_SUPER", "SET_SUPER" -> {
                    val fund = (params["fundname"] ?: params["fund"] ?: params["name"])?.takeIf { it.isNotBlank() } ?: "Super Fund"
                    val owner = resolveOwnerName(params["owner"])
                    val bal = parseCleanRawDouble(params["balance"] ?: params["amount"] ?: params["value"])
                    if (bal != null) {
                        val existingIdx = superAccountsList.indexOfFirst {
                            it.fundName.equals(fund, ignoreCase = true) || (actionType != "ADD_SUPER" && it.owner.equals(owner, ignoreCase = true))
                        }
                        val updated = if (existingIdx >= 0) {
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
                "DELETE_SUPER", "REMOVE_SUPER" -> {
                    val fund = (params["fundname"] ?: params["fund"] ?: params["name"])?.takeIf { it.isNotBlank() }
                    if (fund != null) {
                        val updated = superAccountsList.filterNot { it.fundName.contains(fund, ignoreCase = true) }
                        saveSuperAccounts(updated)
                        appliedSummaries.add("Deleted Super account $fund")
                    }
                }
                "ADD_OTHER_ASSET", "UPDATE_OTHER_ASSET", "SET_OTHER_ASSET", "ADD_ASSET", "UPDATE_ASSET", "SET_ASSET" -> {
                    val name = (params["name"] ?: params["title"] ?: params["asset"])?.takeIf { it.isNotBlank() } ?: "Asset"
                    val owner = resolveOwnerName(params["owner"])
                    val valAmt = parseCleanRawDouble(params["value"] ?: params["amount"] ?: params["balance"])
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
                "DELETE_OTHER_ASSET", "REMOVE_OTHER_ASSET", "DELETE_ASSET", "REMOVE_ASSET" -> {
                    val name = (params["name"] ?: params["title"] ?: params["asset"])?.takeIf { it.isNotBlank() }
                    if (name != null) {
                        val updated = otherAssetsList.filterNot { it.name.contains(name, ignoreCase = true) }
                        saveOtherAssets(updated)
                        appliedSummaries.add("Removed asset $name")
                    }
                }
                "SET_WHAT_IF_LEVERS", "UPDATE_WHAT_IF", "UPDATE_LEVERS", "SET_LEVERS" -> {
                    (params["horizonyears"] ?: params["horizon"] ?: params["years"])?.toIntOrNull()?.let {
                        whatIfHorizonYears = it.coerceIn(1, 40)
                    }
                    parseCleanFloat(params["extrasavingsweekly"] ?: params["savingsweekly"] ?: params["lifestyleadjustmentweekly"] ?: params["extrasavings"])?.let {
                        whatIfLifestyleAdjustmentWeekly = it.coerceIn(-2000f, 5000f)
                    }
                    parseCleanFloat(params["incomegrowth1weekly"] ?: params["incomegrowthweekly"] ?: params["incomegrowthp1"])?.let {
                        whatIfIncomeGrowthP1Weekly = it.coerceIn(-2000f, 5000f)
                    }
                    parseCleanFloat(params["incomegrowth2weekly"] ?: params["incomegrowthp2"])?.let {
                        whatIfIncomeGrowthP2Weekly = it.coerceIn(-2000f, 5000f)
                    }
                    (params["rateshockprop1"] ?: params["ratechangeprop1"] ?: params["rateshock"] ?: params["ratechange1"])
                        ?.replace("%", "")?.toFloatOrNull()?.let {
                            whatIfRateShockProp1 = it.coerceIn(-5f, 8f)
                        }
                    (params["rateshockprop2"] ?: params["ratechangeprop2"] ?: params["ratechange2"])
                        ?.replace("%", "")?.toFloatOrNull()?.let {
                            whatIfRateShockProp2 = it.coerceIn(-5f, 8f)
                        }
                    parseCleanFloat(params["superboostweekly"] ?: params["superboost"] ?: params["extrasuper"])?.let {
                        whatIfSuperBoostWeekly = it.coerceIn(0f, 3000f)
                    }
                    parseCleanFloat(params["rentalgrowthweekly"] ?: params["rentchangeweekly"] ?: params["rentgrowth"])?.let {
                        whatIfRentalGrowthWeekly = it.coerceIn(-2000f, 5000f)
                    }
                    appliedSummaries.add("Updated What-If Simulator levers")
                }
                "RESET_LEVERS", "RESET_WHAT_IF_LEVERS" -> {
                    whatIfHorizonYears = 10
                    whatIfLifestyleAdjustmentWeekly = 0f
                    whatIfIncomeGrowthP1Weekly = 0f
                    whatIfIncomeGrowthP2Weekly = 0f
                    whatIfRateShockProp1 = 0f
                    whatIfRateShockProp2 = 0f
                    whatIfSuperBoostWeekly = 0f
                    whatIfRentalGrowthWeekly = 0f
                    appliedSummaries.add("Reset What-If Simulator levers to default")
                }
                "ADD_CUSTOM_LEVER", "UPDATE_CUSTOM_LEVER" -> {
                    val name = (params["title"] ?: params["name"])?.takeIf { it.isNotBlank() } ?: "Custom Lever"
                    val value = parseCleanRawFloat(params["value"] ?: params["amount"]) ?: 0f
                    val minV = parseCleanRawFloat(params["min"]) ?: -500f
                    val maxV = parseCleanRawFloat(params["max"]) ?: 500f
                    val stepV = parseCleanRawFloat(params["step"]) ?: 25f
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
                "DELETE_CUSTOM_LEVER", "REMOVE_CUSTOM_LEVER" -> {
                    val name = (params["title"] ?: params["name"])?.takeIf { it.isNotBlank() }
                    if (name != null) {
                        whatIfCustomLevers.removeAll { it.title.contains(name, ignoreCase = true) }
                        appliedSummaries.add("Removed custom lever '$name'")
                    }
                }
                "ADD_MILESTONE", "ADD_LIFE_MILESTONE", "UPDATE_MILESTONE" -> {
                    val title = (params["title"] ?: params["name"])?.takeIf { it.isNotBlank() } ?: "Custom Milestone"
                    val catStr = params["category"]?.uppercase() ?: "PURCHASE"
                    val cat = MilestoneCategory.entries.find { it.name == catStr } ?: MilestoneCategory.PURCHASE
                    val startY = (params["startyear"]?.toIntOrNull() ?: 2).coerceIn(1, 30)
                    val durY = (params["durationyears"]?.toIntOrNull() ?: 1).coerceIn(1, 30)
                    val wk = parseCleanDouble(params["weeklycashflow"]) ?: 0.0
                    val ast = parseCleanRawDouble(params["assetimpact"]) ?: 0.0
                    val liab = parseCleanRawDouble(params["liabilityimpact"]) ?: 0.0
                    val existingIdx = whatIfMilestoneList.indexOfFirst { it.title.equals(title, ignoreCase = true) }
                    if (existingIdx >= 0) {
                        whatIfMilestoneList[existingIdx] = whatIfMilestoneList[existingIdx].copy(
                            category = cat,
                            startYear = startY,
                            durationYears = durY,
                            weeklyCashflowImpact = wk,
                            assetImpact = ast,
                            liabilityImpact = liab,
                            isEnabled = true
                        )
                        appliedSummaries.add("Updated milestone '$title' (Yr $startY)")
                    } else {
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
                }
                "DELETE_MILESTONE", "REMOVE_MILESTONE" -> {
                    val title = (params["title"] ?: params["name"])?.takeIf { it.isNotBlank() }
                    if (title != null) {
                        whatIfMilestoneList.removeAll { it.title.contains(title, ignoreCase = true) }
                        appliedSummaries.add("Removed milestone '$title'")
                    }
                }
                "CLEAR_MILESTONES" -> {
                    whatIfMilestoneList.clear()
                    appliedSummaries.add("Cleared all What-If milestones")
                }
                "UPDATE_PROFILE", "SET_PROFILE" -> {
                    var cfg = appConfig
                    params["person1name"]?.takeIf { it.isNotBlank() }?.let { cfg = cfg.copy(person1Name = it) }
                    params["person2name"]?.takeIf { it.isNotBlank() }?.let { cfg = cfg.copy(person2Name = it) }
                    params["iscouplemode"]?.toBooleanStrictOrNull()?.let { cfg = cfg.copy(isCoupleMode = it) }
                    params["hassecondproperty"]?.toBooleanStrictOrNull()?.let { cfg = cfg.copy(hasSecondProperty = it) }
                    saveAppConfig(cfg)
                    appliedSummaries.add("Updated profile settings")
                }
                "REMEMBER_FACT", "LEARN_FACT", "ADD_MEMORY", "SET_MEMORY" -> {
                    val fact = params["fact"] ?: params["text"] ?: params["value"] ?: params["name"] ?: ""
                    val category = (params["category"] ?: params["type"] ?: "General").replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                    }
                    if (fact.isNotBlank()) {
                        val newFact = LearnedUserFact(
                            category = category,
                            fact = fact
                        )
                        val updated = (learnedFacts.filterNot { it.fact.equals(fact, ignoreCase = true) } + newFact)
                        learnedFacts = updated
                        saveLearnedFacts(updated)
                        appliedSummaries.add("Remembered: $fact ($category)")
                    }
                }
                "FORGET_FACT", "DELETE_MEMORY", "REMOVE_MEMORY", "FORGET_MEMORY" -> {
                    val query = params["query"] ?: params["fact"] ?: params["keyword"] ?: ""
                    if (query.isNotBlank()) {
                        val updated = learnedFacts.filterNot { it.fact.contains(query, ignoreCase = true) || it.id == query }
                        learnedFacts = updated
                        saveLearnedFacts(updated)
                        appliedSummaries.add("Removed memory matching '$query'")
                    }
                }
            }
        }

        val cleanedText = rawResponse.replace(actionRegex, "").trim()
        return Pair(cleanedText, appliedSummaries)
    }

    val headerSubtitle = remember(appConfig) {
        val suffix = if (appConfig.isCoupleMode) {
            " • ${appConfig.person1Name.uppercase()} & ${appConfig.person2Name.uppercase()}"
        } else {
            " • ${appConfig.person1Name.uppercase()}"
        }
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) { append("COIN") }
            withStyle(SpanStyle(fontWeight = FontWeight.Normal)) { append("LENS") }
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) { append(suffix) }
        }
    }

    // HEADER WITH BRAND LOGO — FLOATING APPLE LIQUID GLASS CAPSULE (SLIMMER)
    val topBarContent = @Composable {
        val headerGlassShape = RoundedCornerShape(18.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .liquidGlass(
                        shape = headerGlassShape,
                        tintColor = BrandForestDark,
                        intensity = 1.20f,
                        accentRimColor = currentSection.getColor(),
                        role = LiquidGlassRole.HEADER
                    ),
                shape = headerGlassShape,
                color = Color.Transparent,
                contentColor = Color.White,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AppLogoIcon(size = 30.dp, backgroundColor = Color.Transparent, showBorder = false)
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = headerSubtitle,
                                color = BrandCoralPink,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (currentSection == AppSection.PROPERTIES) propertiesSectionTitle else currentSection.title,
                                color = Color.White,
                                fontSize = 16.sp,
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
                            contentColor = Color.White,
                            modifier = Modifier
                                .size(28.dp)
                                .liquidGlass(
                                    shape = CircleShape,
                                    tintColor = BrandForestSurface,
                                    intensity = 1.2f,
                                    accentRimColor = if (isWealthHidden) BrandCoralPink else Color.White,
                                    role = LiquidGlassRole.STATIC
                                )
                        ) {
                            IconButton(
                                onClick = { isWealthHidden = !isWealthHidden },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    if (isWealthHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Privacy Mask",
                                    tint = if (isWealthHidden) BrandCoralPink else Color.White,
                                    modifier = Modifier.size(15.dp)
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
    val orbRevealState = remember { OrbRevealState() }
    var isHorizontalChildTouched by remember { mutableStateOf(false) }

    val selectedTabIndex by remember(pagerState) {
        derivedStateOf { pagerState.targetPage }
    }

    val navigateToSettingsTab: () -> Unit = remember(activeSections, navigateToTab) {
        {
            val settingsIdx = activeSections.indexOf(AppSection.SETTINGS)
            navigateToTab(settingsIdx)
        }
    }
    val navigateToWhatIfTab: () -> Unit = remember(activeSections, navigateToTab) {
        {
            val whatIfIdx = activeSections.indexOf(AppSection.WHAT_IF)
            navigateToTab(whatIfIdx)
        }
    }
    val onUpdateIncomeCallback: (Boolean, Double) -> Unit = remember(sharedPrefs) {
        { isPerson1, newInc ->
            if (isPerson1) {
                person1Income = newInc
                sharedPrefs.edit { putFloat("person1_inc", newInc.toFloat()) }
                updateFirestore("person1_inc", newInc)
            } else {
                person2Income = newInc
                sharedPrefs.edit { putFloat("person2_inc", newInc.toFloat()) }
                updateFirestore("person2_inc", newInc)
            }
        }
    }
    val onUpdateSavingsCallback: (Boolean, Double) -> Unit = remember(sharedPrefs) {
        { isPerson1, newSavings ->
            if (isPerson1) {
                person1Savings = newSavings
                sharedPrefs.edit { putFloat("person1_savings", newSavings.toFloat()) }
                updateFirestore("person1_savings", newSavings)
            } else {
                person2Savings = newSavings
                sharedPrefs.edit { putFloat("person2_savings", newSavings.toFloat()) }
                updateFirestore("person2_savings", newSavings)
            }
        }
    }
    val onAddPerson1Callback: (String, Double, String, BillingCycle, Long?, String?) -> Unit = remember {
        { name, amt, iconName, cycle, dueMillis, assetId ->
            val updated = person1Items + CashflowItem(
                id = "p1_${System.currentTimeMillis()}",
                name = name,
                weeklyAmount = amt,
                iconName = iconName,
                billingCycle = cycle,
                nextDueDateEpochMillis = dueMillis,
                linkedAssetId = assetId
            )
            person1Items = updated
            saveList("person1_items", updated)
        }
    }
    val onEditPerson1Callback: (String, String, Double, String, BillingCycle, Long?, String?) -> Unit = remember {
        { id, name, amt, iconName, cycle, dueMillis, assetId ->
            val updated = person1Items.map {
                if (it.id == id) {
                    it.copy(
                        name = name,
                        weeklyAmount = amt,
                        iconName = iconName,
                        billingCycle = cycle,
                        nextDueDateEpochMillis = dueMillis,
                        linkedAssetId = assetId
                    )
                } else it
            }
            person1Items = updated
            saveList("person1_items", updated)
        }
    }
    val onDeletePerson1Callback: (String) -> Unit = remember {
        { id ->
            val updated = person1Items.filterNot { it.id == id }
            person1Items = updated
            saveList("person1_items", updated)
        }
    }
    val onAddPerson2Callback: (String, Double, String, BillingCycle, Long?, String?) -> Unit = remember {
        { name, amt, iconName, cycle, dueMillis, assetId ->
            val updated = person2Items + CashflowItem(
                id = "p2_${System.currentTimeMillis()}",
                name = name,
                weeklyAmount = amt,
                iconName = iconName,
                billingCycle = cycle,
                nextDueDateEpochMillis = dueMillis,
                linkedAssetId = assetId
            )
            person2Items = updated
            saveList("person2_items", updated)
        }
    }
    val onEditPerson2Callback: (String, String, Double, String, BillingCycle, Long?, String?) -> Unit = remember {
        { id, name, amt, iconName, cycle, dueMillis, assetId ->
            val updated = person2Items.map {
                if (it.id == id) {
                    it.copy(
                        name = name,
                        weeklyAmount = amt,
                        iconName = iconName,
                        billingCycle = cycle,
                        nextDueDateEpochMillis = dueMillis,
                        linkedAssetId = assetId
                    )
                } else it
            }
            person2Items = updated
            saveList("person2_items", updated)
        }
    }
    val onDeletePerson2Callback: (String) -> Unit = remember {
        { id ->
            val updated = person2Items.filterNot { it.id == id }
            person2Items = updated
            saveList("person2_items", updated)
        }
    }
    val onAddProp1Callback: (String, Double, String, BillingCycle, Long?, String?) -> Unit = remember {
        { name, amt, iconName, cycle, dueMillis, assetId ->
            val updated = prop1Items + CashflowItem(
                id = "pr1_${System.currentTimeMillis()}",
                name = name,
                weeklyAmount = amt,
                iconName = iconName,
                billingCycle = cycle,
                nextDueDateEpochMillis = dueMillis,
                linkedAssetId = assetId
            )
            prop1Items = updated
            saveList("prop1_items", updated)
        }
    }
    val onEditProp1Callback: (String, String, Double, String, BillingCycle, Long?, String?) -> Unit = remember {
        { id, name, amt, iconName, cycle, dueMillis, assetId ->
            val updated = prop1Items.map {
                if (it.id == id) {
                    it.copy(
                        name = name,
                        weeklyAmount = amt,
                        iconName = iconName,
                        billingCycle = cycle,
                        nextDueDateEpochMillis = dueMillis,
                        linkedAssetId = assetId
                    )
                } else it
            }
            prop1Items = updated
            saveList("prop1_items", updated)
        }
    }
    val onDeleteProp1Callback: (String) -> Unit = remember {
        { id ->
            val updated = prop1Items.filterNot { it.id == id }
            prop1Items = updated
            saveList("prop1_items", updated)
        }
    }
    val onAddProp2Callback: (String, Double, String, BillingCycle, Long?, String?) -> Unit = remember {
        { name, amt, iconName, cycle, dueMillis, assetId ->
            val updated = prop2Items + CashflowItem(
                id = "pr2_${System.currentTimeMillis()}",
                name = name,
                weeklyAmount = amt,
                iconName = iconName,
                billingCycle = cycle,
                nextDueDateEpochMillis = dueMillis,
                linkedAssetId = assetId
            )
            prop2Items = updated
            saveList("prop2_items", updated)
        }
    }
    val onEditProp2Callback: (String, String, Double, String, BillingCycle, Long?, String?) -> Unit = remember {
        { id, name, amt, iconName, cycle, dueMillis, assetId ->
            val updated = prop2Items.map {
                if (it.id == id) {
                    it.copy(
                        name = name,
                        weeklyAmount = amt,
                        iconName = iconName,
                        billingCycle = cycle,
                        nextDueDateEpochMillis = dueMillis,
                        linkedAssetId = assetId
                    )
                } else it
            }
            prop2Items = updated
            saveList("prop2_items", updated)
        }
    }
    val onDeleteProp2Callback: (String) -> Unit = remember {
        { id ->
            val updated = prop2Items.filterNot { it.id == id }
            prop2Items = updated
            saveList("prop2_items", updated)
        }
    }
    val onAddShareCallback: (String, String, Double, Double, Double, String, String, Double, Double, Double) -> Unit = remember {
        { code, owner, units, pPrice, cPrice, purchaseDate, market, usdPPrice, usdCPrice, fxRate ->
            val upperCode = code.trim().uppercase()
            val cleanMarket = market.trim().uppercase().takeIf { it == "US" } ?: "ASX"
            val newParcel = ShareParcel(
                id = "sp_${System.currentTimeMillis()}",
                code = upperCode,
                owner = owner,
                units = units,
                purchasePrice = pPrice,
                currentPrice = cPrice,
                purchaseDate = purchaseDate.ifBlank { LocalDate.now().toString() },
                market = cleanMarket,
                usdPurchasePrice = usdPPrice,
                usdCurrentPrice = usdCPrice,
                fxRateAtSync = fxRate
            )
            val updated = sharesList.map {
                if (it.code.equals(upperCode, ignoreCase = true) && it.market.equals(cleanMarket, ignoreCase = true) && cPrice > 0) {
                    it.copy(
                        currentPrice = cPrice,
                        usdCurrentPrice = if (usdCPrice > 0) usdCPrice else it.usdCurrentPrice,
                        fxRateAtSync = if (fxRate > 0) fxRate else it.fxRateAtSync
                    )
                } else it
            } + newParcel
            saveSharesList(updated)
        }
    }
    val onEditShareCallback: (String, String, String, Double, Double, Double, String, String, Double, Double, Double) -> Unit = remember {
        { id, code, owner, units, pPrice, cPrice, purchaseDate, market, usdPPrice, usdCPrice, fxRate ->
            val upperCode = code.trim().uppercase()
            val cleanMarket = market.trim().uppercase().takeIf { it == "US" } ?: "ASX"
            val updated = sharesList.map {
                if (it.id == id) {
                    it.copy(
                        code = upperCode,
                        owner = owner,
                        units = units,
                        purchasePrice = pPrice,
                        currentPrice = cPrice,
                        purchaseDate = purchaseDate,
                        market = cleanMarket,
                        usdPurchasePrice = usdPPrice,
                        usdCurrentPrice = usdCPrice,
                        fxRateAtSync = fxRate
                    )
                } else if (it.code.equals(upperCode, ignoreCase = true) && it.market.equals(cleanMarket, ignoreCase = true) && cPrice > 0) {
                    it.copy(
                        currentPrice = cPrice,
                        usdCurrentPrice = if (usdCPrice > 0) usdCPrice else it.usdCurrentPrice,
                        fxRateAtSync = if (fxRate > 0) fxRate else it.fxRateAtSync
                    )
                } else {
                    it
                }
            }
            saveSharesList(updated)
        }
    }
    val onDeleteShareCallback: (String) -> Unit = remember {
        { id ->
            val updated = sharesList.filterNot { it.id == id }
            saveSharesList(updated)
        }
    }
    val onAddSuperAccountCallback: (String, String, Double) -> Unit = remember {
        { fundName, owner, bal ->
            val updated = superAccountsList + SuperAccount("sup_${System.currentTimeMillis()}", fundName, owner, bal)
            saveSuperAccounts(updated)
        }
    }
    val onEditSuperAccountCallback: (String, String, String, Double) -> Unit = remember {
        { id, fundName, owner, bal ->
            val updated = superAccountsList.map { if (it.id == id) it.copy(fundName = fundName, owner = owner, balance = bal) else it }
            saveSuperAccounts(updated)
        }
    }
    val onDeleteSuperAccountCallback: (String) -> Unit = remember {
        { id ->
            val updated = superAccountsList.filterNot { it.id == id }
            saveSuperAccounts(updated)
        }
    }
    val onAddOtherAssetCallback: (String, String, Double) -> Unit = remember {
        { name, owner, valAmt ->
            val updated = otherAssetsList + OtherAsset("oa_${System.currentTimeMillis()}", name, owner, valAmt)
            saveOtherAssets(updated)
        }
    }
    val onEditOtherAssetCallback: (String, String, String, Double) -> Unit = remember {
        { id, name, owner, valAmt ->
            val updated = otherAssetsList.map { if (it.id == id) it.copy(name = name, owner = owner, value = valAmt) else it }
            saveOtherAssets(updated)
        }
    }
    val onDeleteOtherAssetCallback: (String) -> Unit = remember {
        { id ->
            val updated = otherAssetsList.filterNot { it.id == id }
            saveOtherAssets(updated)
        }
    }

    var rootCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val hostViewId = System.identityHashCode(LocalView.current)
    LaunchedEffect(hostViewId) {
        liquidBoundaryState.hostViewIdentity = hostViewId
    }

    CompositionLocalProvider(
        LocalOrbRevealState provides orbRevealState,
        LocalLiquidScrollBoundary provides liquidBoundaryState,
        LocalOnHorizontalChildTouchChange provides { isHorizontalChildTouched = it }
    ) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { rootCoords = it }
            .pointerInput(orbRevealState) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                    if (orbRevealState.activeOrbKey != null) {
                        val rootPos = rootCoords?.localToRoot(down.position) ?: down.position
                        val trayBounds = orbRevealState.activeActionTrayBounds
                        val isInsideActionTray = trayBounds?.contains(rootPos) == true
                        if (!isInsideActionTray) {
                            orbRevealState.dismiss()
                        }
                    }
                }
            }
    ) {
        if (isExpanded) {
            Row(modifier = Modifier.fillMaxSize()) {
                val railShape = RoundedCornerShape(28.dp)
                NavigationRail(
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Start + WindowInsetsSides.Vertical))
                        .padding(start = 10.dp, top = 10.dp, bottom = 10.dp)
                        .liquidGlass(
                            shape = railShape,
                            tintColor = BrandForestDark,
                            intensity = 1.15f,
                            accentRimColor = currentSection.getColor(),
                            role = LiquidGlassRole.STATIC
                        )
                ) {
                    Spacer(Modifier.height(16.dp))
                    activeSections.forEachIndexed { index, section ->
                        NavigationRailItem(
                            selected = selectedTabIndex == index,
                            onClick = { navigateToTab(index) },
                            icon = { Icon(section.icon, contentDescription = section.title) },
                            label = { Text(if (section == AppSection.PROPERTIES) propertiesNavLabel else section.shortLabel, fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = BrandForestDark,
                                selectedTextColor = section.getColor(),
                                indicatorColor = section.getColor(),
                                unselectedIconColor = Color.White.copy(alpha = 0.75f),
                                unselectedTextColor = Color.White.copy(alpha = 0.75f)
                            )
                        )
                    }
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    HorizontalPager(
                        state = pagerState,
                        key = { page -> activeSections.getOrNull(page)?.name ?: page },
                        beyondViewportPageCount = 0,
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
                                person1Savings = person1Savings,
                                person2Savings = person2Savings,
                                isWealthHidden = isWealthHidden,
                                onNavigateToSettings = {
                                    val settingsIdx = activeSections.indexOf(AppSection.SETTINGS)
                                    navigateToTab(settingsIdx)
                                },
                                onNavigateToWhatIf = {
                                    val whatIfIdx = activeSections.indexOf(AppSection.WHAT_IF)
                                    navigateToTab(whatIfIdx)
                                },
                                prop1Items = prop1Items,
                                prop2Items = prop2Items,
                                onAddPerson1 = onAddPerson1Callback,
                                onEditPerson1 = onEditPerson1Callback,
                                onDeletePerson1 = onDeletePerson1Callback,
                                onAddPerson2 = onAddPerson2Callback,
                                onEditPerson2 = onEditPerson2Callback,
                                onDeletePerson2 = onDeletePerson2Callback,
                                onAddProp1 = onAddProp1Callback,
                                onEditProp1 = onEditProp1Callback,
                                onDeleteProp1 = onDeleteProp1Callback,
                                onAddProp2 = onAddProp2Callback,
                                onEditProp2 = onEditProp2Callback,
                                onDeleteProp2 = onDeleteProp2Callback
                            )
                            AppSection.CASH_FLOW -> MoneyPlanScreen(
                                appConfig = appConfig,
                                person1Color = person1Color,
                                person2Color = person2Color,
                                person1Income = person1Income,
                                person2Income = person2Income,
                                person1Savings = person1Savings,
                                person2Savings = person2Savings,
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
                                onUpdateSavings = { isPerson1, newSavings ->
                                    if (isPerson1) {
                                        person1Savings = newSavings
                                        sharedPrefs.edit { putFloat("person1_savings", newSavings.toFloat()) }
                                        updateFirestore("person1_savings", newSavings)
                                    } else {
                                        person2Savings = newSavings
                                        sharedPrefs.edit { putFloat("person2_savings", newSavings.toFloat()) }
                                        updateFirestore("person2_savings", newSavings)
                                    }
                                },
                                perPersonPropertyShare = perPersonPropertyShare,
                                person1Items = person1Items,
                                person2Items = person2Items,
                                onAddPerson1 = { name, amt, iconName, cycle, dueMillis, assetId ->
                                    val updated = person1Items + CashflowItem(
                                        id = "p1_${System.currentTimeMillis()}",
                                        name = name,
                                        weeklyAmount = amt,
                                        iconName = iconName,
                                        billingCycle = cycle,
                                        nextDueDateEpochMillis = dueMillis,
                                        linkedAssetId = assetId
                                    )
                                    person1Items = updated
                                    saveList("person1_items", updated)
                                },
                                onEditPerson1 = { id, name, amt, iconName, cycle, dueMillis, assetId ->
                                    val updated = person1Items.map {
                                        if (it.id == id) {
                                            it.copy(
                                                name = name,
                                                weeklyAmount = amt,
                                                iconName = iconName,
                                                billingCycle = cycle,
                                                nextDueDateEpochMillis = dueMillis,
                                                linkedAssetId = assetId
                                            )
                                        } else it
                                    }
                                    person1Items = updated
                                    saveList("person1_items", updated)
                                },
                                onDeletePerson1 = { id ->
                                    val updated = person1Items.filterNot { it.id == id }
                                    person1Items = updated
                                    saveList("person1_items", updated)
                                },
                                onAddPerson2 = { name, amt, iconName, cycle, dueMillis, assetId ->
                                    val updated = person2Items + CashflowItem(
                                        id = "p2_${System.currentTimeMillis()}",
                                        name = name,
                                        weeklyAmount = amt,
                                        iconName = iconName,
                                        billingCycle = cycle,
                                        nextDueDateEpochMillis = dueMillis,
                                        linkedAssetId = assetId
                                    )
                                    person2Items = updated
                                    saveList("person2_items", updated)
                                },
                                onEditPerson2 = { id, name, amt, iconName, cycle, dueMillis, assetId ->
                                    val updated = person2Items.map {
                                        if (it.id == id) {
                                            it.copy(
                                                name = name,
                                                weeklyAmount = amt,
                                                iconName = iconName,
                                                billingCycle = cycle,
                                                nextDueDateEpochMillis = dueMillis,
                                                linkedAssetId = assetId
                                            )
                                        } else it
                                    }
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
                                onAddProp1 = { name, amt, iconName, cycle, dueMillis, assetId ->
                                    val updated = prop1Items + CashflowItem(
                                        id = "pr1_${System.currentTimeMillis()}",
                                        name = name,
                                        weeklyAmount = amt,
                                        iconName = iconName,
                                        billingCycle = cycle,
                                        nextDueDateEpochMillis = dueMillis,
                                        linkedAssetId = assetId
                                    )
                                    prop1Items = updated
                                    saveList("prop1_items", updated)
                                },
                                onEditProp1 = { id, name, amt, iconName, cycle, dueMillis, assetId ->
                                    val updated = prop1Items.map {
                                        if (it.id == id) {
                                            it.copy(
                                                name = name,
                                                weeklyAmount = amt,
                                                iconName = iconName,
                                                billingCycle = cycle,
                                                nextDueDateEpochMillis = dueMillis,
                                                linkedAssetId = assetId
                                            )
                                        } else it
                                    }
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
                                onAddProp2 = { name, amt, iconName, cycle, dueMillis, assetId ->
                                    val updated = prop2Items + CashflowItem(
                                        id = "pr2_${System.currentTimeMillis()}",
                                        name = name,
                                        weeklyAmount = amt,
                                        iconName = iconName,
                                        billingCycle = cycle,
                                        nextDueDateEpochMillis = dueMillis,
                                        linkedAssetId = assetId
                                    )
                                    prop2Items = updated
                                    saveList("prop2_items", updated)
                                },
                                onEditProp2 = { id, name, amt, iconName, cycle, dueMillis, assetId ->
                                    val updated = prop2Items.map {
                                        if (it.id == id) {
                                            it.copy(
                                                name = name,
                                                weeklyAmount = amt,
                                                iconName = iconName,
                                                billingCycle = cycle,
                                                nextDueDateEpochMillis = dueMillis,
                                                linkedAssetId = assetId
                                            )
                                        } else it
                                    }
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
                                onUpdatePropertyValuations = { p1Val, p2Val -> savePropertyValuations(p1Val, p2Val) },
                                onUpdateAppConfig = { saveAppConfig(it) }
                            )
                            AppSection.SUPER_SHARES -> SuperAndSharesScreen(
                                appConfig = appConfig,
                                person1Color = person1Color,
                                person2Color = person2Color,
                                sharesList = sharesList,
                                superAccountsList = superAccountsList,
                                isWealthHidden = isWealthHidden,
                                onAddShare = onAddShareCallback,
                                onEditShare = onEditShareCallback,
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
                                person1Savings = person1Savings,
                                person2Savings = person2Savings,
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
                                incomeGrowthPerson1Weekly = whatIfIncomeGrowthP1Weekly,
                                onIncomeGrowthPerson1WeeklyChange = { whatIfIncomeGrowthP1Weekly = it },
                                incomeGrowthPerson2Weekly = whatIfIncomeGrowthP2Weekly,
                                onIncomeGrowthPerson2WeeklyChange = { whatIfIncomeGrowthP2Weekly = it },
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
                                onExecuteAiActions = ::executeAiActions,
                                learnedFacts = learnedFacts,
                                conversationDigest = conversationDigest
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
                                onDeleteAccount = onDeleteAccountUser,
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
                    // Fixed Top Bar overlay (pure floating liquid glass capsule)
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
                            messages = geminiMessages,
                            learnedFacts = learnedFacts,
                            conversationDigest = conversationDigest,
                            onExecuteAiActions = ::executeAiActions,
                            onUpdateDigest = {
                                conversationDigest = it
                                saveConversationDigest(it)
                            }
                        )
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    key = { page -> activeSections.getOrNull(page)?.name ?: page },
                    beyondViewportPageCount = 0,
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
                            person1Savings = person1Savings,
                            person2Savings = person2Savings,
                            isWealthHidden = isWealthHidden,
                            onNavigateToSettings = navigateToSettingsTab,
                            onNavigateToWhatIf = navigateToWhatIfTab,
                            prop1Items = prop1Items,
                            prop2Items = prop2Items,
                            onAddPerson1 = onAddPerson1Callback,
                            onEditPerson1 = onEditPerson1Callback,
                            onDeletePerson1 = onDeletePerson1Callback,
                            onAddPerson2 = onAddPerson2Callback,
                            onEditPerson2 = onEditPerson2Callback,
                            onDeletePerson2 = onDeletePerson2Callback,
                            onAddProp1 = onAddProp1Callback,
                            onEditProp1 = onEditProp1Callback,
                            onDeleteProp1 = onDeleteProp1Callback,
                            onAddProp2 = onAddProp2Callback,
                            onEditProp2 = onEditProp2Callback,
                            onDeleteProp2 = onDeleteProp2Callback
                        )
                        AppSection.CASH_FLOW -> MoneyPlanScreen(
                            appConfig = appConfig,
                            person1Color = person1Color,
                            person2Color = person2Color,
                            person1Income = person1Income,
                            person2Income = person2Income,
                            person1Savings = person1Savings,
                            person2Savings = person2Savings,
                            isWealthHidden = isWealthHidden,
                            isPartnerLoggedIn = isLoggedInAsPartner,
                            onUpdateIncome = onUpdateIncomeCallback,
                            onUpdateSavings = onUpdateSavingsCallback,
                            perPersonPropertyShare = perPersonPropertyShare,
                            person1Items = person1Items,
                            person2Items = person2Items,
                            onAddPerson1 = onAddPerson1Callback,
                            onEditPerson1 = onEditPerson1Callback,
                            onDeletePerson1 = onDeletePerson1Callback,
                            onAddPerson2 = onAddPerson2Callback,
                            onEditPerson2 = onEditPerson2Callback,
                            onDeletePerson2 = onDeletePerson2Callback
                        )
                        AppSection.PROPERTIES -> PropertiesScreen(
                            appConfig = appConfig,
                            prop1WeeklyMortgage = prop1WeeklyMortgage,
                            prop1Items = prop1Items,
                            isWealthHidden = isWealthHidden,
                            onAddProp1 = onAddProp1Callback,
                            onEditProp1 = onEditProp1Callback,
                            onDeleteProp1 = onDeleteProp1Callback,
                            prop2WeeklyMortgage = prop2WeeklyMortgage,
                            prop2Items = prop2Items,
                            onAddProp2 = onAddProp2Callback,
                            onEditProp2 = onEditProp2Callback,
                            onDeleteProp2 = onDeleteProp2Callback,
                            prop1MarketValue = prop1EstimatedValue,
                            prop2MarketValue = prop2EstimatedValue,
                            prop1LoanBalance = prop1LiveBalance,
                            prop2LoanBalance = prop2LiveBalance,
                            onUpdatePropertyValuations = { p1Val, p2Val -> savePropertyValuations(p1Val, p2Val) },
                            onUpdateAppConfig = { saveAppConfig(it) }
                        )
                        AppSection.SUPER_SHARES -> SuperAndSharesScreen(
                            appConfig = appConfig,
                            person1Color = person1Color,
                            person2Color = person2Color,
                            sharesList = sharesList,
                            superAccountsList = superAccountsList,
                            isWealthHidden = isWealthHidden,
                            onAddShare = onAddShareCallback,
                            onEditShare = onEditShareCallback,
                            onDeleteShare = onDeleteShareCallback,
                            onUpdateSharesList = { saveSharesList(it) },
                            onAddSuperAccount = onAddSuperAccountCallback,
                            onEditSuperAccount = onEditSuperAccountCallback,
                            onDeleteSuperAccount = onDeleteSuperAccountCallback
                        )
                        AppSection.OTHER_ASSETS -> OtherAssetsScreen(
                            appConfig = appConfig,
                            person1Color = person1Color,
                            person2Color = person2Color,
                            otherAssetsList = otherAssetsList,
                            isWealthHidden = isWealthHidden,
                            onAddOtherAsset = onAddOtherAssetCallback,
                            onEditOtherAsset = onEditOtherAssetCallback,
                            onDeleteOtherAsset = onDeleteOtherAssetCallback
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
                            person1Savings = person1Savings,
                            person2Savings = person2Savings,
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
                            incomeGrowthPerson1Weekly = whatIfIncomeGrowthP1Weekly,
                            onIncomeGrowthPerson1WeeklyChange = { whatIfIncomeGrowthP1Weekly = it },
                            incomeGrowthPerson2Weekly = whatIfIncomeGrowthP2Weekly,
                            onIncomeGrowthPerson2WeeklyChange = { whatIfIncomeGrowthP2Weekly = it },
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
                            onExecuteAiActions = ::executeAiActions,
                            learnedFacts = learnedFacts,
                            conversationDigest = conversationDigest
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

                // Fixed Top Bar overlay (pure floating liquid glass capsule)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                ) {
                    topBarContent()
                }

                // Floating Apple Liquid Glass Pill Dock Footer (Slimmer)
                val dockShape = RoundedCornerShape(18.dp)
                val tabIndicatorShape = RoundedCornerShape(14.dp)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .liquidGlass(
                                shape = dockShape,
                                tintColor = BrandForestDark,
                                intensity = 1.20f,
                                accentRimColor = currentSection.getColor(),
                                role = LiquidGlassRole.FOOTER
                            ),
                        shape = dockShape,
                        color = Color.Transparent,
                        contentColor = Color.White,
                        tonalElevation = 0.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            activeSections.forEachIndexed { index, section ->
                                val isSelected = selectedTabIndex == index
                                val animatedBgColor by animateColorAsState(
                                    targetValue = if (isSelected) section.getColor().copy(alpha = 0.92f) else Color.Transparent,
                                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                                    label = "tab_bg_$index"
                                )
                                val animatedIconColor by animateColorAsState(
                                    targetValue = if (isSelected) BrandForestDark else Color.White.copy(alpha = 0.78f),
                                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                                    label = "tab_icon_$index"
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            navigateToTab(index)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(width = 46.dp, height = 36.dp)
                                            .background(
                                                color = animatedBgColor,
                                                shape = tabIndicatorShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            section.icon,
                                            contentDescription = section.title,
                                            tint = animatedIconColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (appConfig.useExpnzAi) {
                    FloatingAiAssistantWidget(
                        financialContextSummary = detailedFinancialContext,
                        messages = geminiMessages,
                        learnedFacts = learnedFacts,
                        conversationDigest = conversationDigest,
                        onExecuteAiActions = ::executeAiActions,
                        onUpdateDigest = {
                            conversationDigest = it
                            saveConversationDigest(it)
                        }
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
                            title = "Summary Dashboard (Front Screen)",
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
                            title = "CoinBot (Gemini)",
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
                                leadingIcon = {
                                    Box(modifier = Modifier.padding(start = 6.dp), contentAlignment = Alignment.Center) {
                                        AsxShareLogoBadge(ticker = share.code, size = 18.dp, market = share.market)
                                    }
                                },
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
                                hasSecondProperty = !isProperty1Rented && hasSecondProperty,
                                showTotalWealth = finalShowWealth,
                                showCashFlow = showCashFlow,
                                showProperties = showProperties,
                                showSuperAndShares = showSuperAndShares,
                                showOtherAssets = showOtherAssets,
                                showLoans = !isProperty1Rented && showLoans,
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
                                if (isProperty1Rented) 0.0 else (prop1ValueStr.toDoubleOrNull() ?: 750000.0),
                                if (isProperty1Rented) 0.0 else (prop1LoanBalStr.toDoubleOrNull() ?: 465000.0),
                                prop1RateStr.toDoubleOrNull() ?: 6.29,
                                if (isProperty1Rented) 0.0 else (prop1OffsetStr.toDoubleOrNull() ?: 45000.0),
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
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) { append("COIN") }
                        withStyle(SpanStyle(fontWeight = FontWeight.Normal)) { append("LENS") }
                        withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) { append(" • PERSONAL WEALTH") }
                    },
                    color = BrandCoralPink,
                    fontSize = 14.sp,
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
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) { append("COIN") }
                        withStyle(SpanStyle(fontWeight = FontWeight.Normal)) { append("LENS") }
                    },
                    color = BrandCoralPink,
                    fontSize = 14.sp,
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
    val locale = currentLocale()
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
        contentPadding = PaddingValues(top = headerTopPad, bottom = 140.dp),
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
                                Text("${String.format(locale, "%.2f", prop1Loan.interestRatePa)}% p.a.", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BrandCoralPink)
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
                        title = "Summary Dashboard",
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
                        accent = Color(0xFF818CF8),
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

                    HorizontalDivider()

                    val subAuditPrefs = remember { context.getSharedPreferences("coinlens_sub_audit_v1", Context.MODE_PRIVATE) }
                    var auditedSubCount by remember {
                        mutableIntStateOf(
                            (subAuditPrefs.getStringSet("kept_merchants", emptySet())?.size ?: 0) +
                                (subAuditPrefs.getStringSet("cancel_merchants", emptySet())?.size ?: 0) +
                                (subAuditPrefs.getStringSet("negotiate_merchants", emptySet())?.size ?: 0)
                        )
                    }
                    var reclaimedSubTotal by remember { mutableDoubleStateOf(subAuditPrefs.getFloat("reclaimed_annual_total", 0f).toDouble()) }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Subscription Audit Whitelist", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(
                                when {
                                    auditedSubCount > 0 && reclaimedSubTotal > 0 -> "$auditedSubCount audited • ${formatCurrency(reclaimedSubTotal, 0)}/yr reclaimed"
                                    auditedSubCount > 0 -> "$auditedSubCount recurring subscription(s) audited"
                                    reclaimedSubTotal > 0 -> "${formatCurrency(reclaimedSubTotal, 0)}/yr reclaimed savings"
                                    else -> "No subscriptions audited yet"
                                },
                                fontSize = 12.sp,
                                color = SlateGray
                            )
                        }
                        if (auditedSubCount > 0 || reclaimedSubTotal > 0) {
                            TextButton(onClick = {
                                subAuditPrefs.edit {
                                    remove("kept_merchants")
                                    remove("cancel_merchants")
                                    remove("negotiate_merchants")
                                    remove("reclaimed_annual_total")
                                }
                                auditedSubCount = 0
                                reclaimedSubTotal = 0.0
                            }) {
                                Text("Reset Audit", color = BrandCoralPink, fontWeight = FontWeight.Bold)
                            }
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
                        buildAnnotatedString {
                            append("Enter your partner's name and email below. When your partner creates their own ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("COIN") }
                            append("LENS account (or signs in) using this email, they will automatically connect to your shared household with full real-time read & write access.")
                        },
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
                            append("Hi $cleanName! I've invited you to share our household wealth & budget tracker on COINLENS.\n\n")
                            append("1. Open COINLENS and Sign Up (or Sign In) with your email: $cleanEmail\n")
                            append("2. Your account will automatically sync with our shared household data (or use Invite Code: $householdInviteCode in Settings).")
                        }
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Join our shared COINLENS Household")
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
    person1Savings: Double = 0.0,
    person2Savings: Double = 0.0,
    onUpdateIncome: (Boolean, Double) -> Unit,
    onUpdateSavings: (Boolean, Double) -> Unit = { _, _ -> },
    perPersonPropertyShare: Double,
    person1Items: List<CashflowItem>,
    person2Items: List<CashflowItem>,
    isWealthHidden: Boolean = false,
    isPartnerLoggedIn: Boolean = false,
    onAddPerson1: (String, Double, String, BillingCycle, Long?, String?) -> Unit,
    onEditPerson1: (String, String, Double, String, BillingCycle, Long?, String?) -> Unit,
    onDeletePerson1: (String) -> Unit,
    onAddPerson2: (String, Double, String, BillingCycle, Long?, String?) -> Unit,
    onEditPerson2: (String, String, Double, String, BillingCycle, Long?, String?) -> Unit,
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
    var itemToDelete by remember { mutableStateOf<CashflowItem?>(null) }
    var showIncomeDialog by remember { mutableStateOf(false) }
    var showSavingsDialog by remember { mutableStateOf(false) }
    var editingSavingsPerson by remember { mutableIntStateOf(0) }
    var sortBy by remember { mutableStateOf("Highest $") }

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

    val rawActiveList = if (selectedPerson == 0) person1Items else person2Items
    val activeList = remember(rawActiveList, sortBy) {
        when (sortBy) {
            "Highest $" -> rawActiveList.sortedByDescending { abs(it.weeklyAmount) }
            "Lowest $" -> rawActiveList.sortedBy { abs(it.weeklyAmount) }
            "A-Z" -> rawActiveList.sortedBy { it.name.lowercase() }
            else -> rawActiveList
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = headerTopPad, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(orbSpacing)
    ) {
        item {
            val isDarkCard = LocalIsDarkTheme.current
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkCard) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                ),
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
                                color = Color.White,
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
                            color = if (isDarkCard) Color.White.copy(alpha = 0.1f) else Color(0xFFF1F5F9),
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

        if (appConfig.isProperty1Rented) {
            item {
                val activeName = if (selectedPerson == 0) appConfig.person1Name else appConfig.person2Name
                val activeSavings = if (selectedPerson == 0) person1Savings else person2Savings

                SavingsOrb(
                    personName = activeName,
                    savingsAmount = activeSavings,
                    accentColor = BrandMintGreen,
                    isWealthHidden = isWealthHidden,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        editingSavingsPerson = selectedPerson
                        showSavingsDialog = true
                    }
                )
            }
        }

        item {
            val activeName = if (selectedPerson == 0) appConfig.person1Name else appConfig.person2Name
            val activeColor = if (selectedPerson == 0) person1Color else person2Color
            val activeSavings = if (selectedPerson == 0) person1Savings else person2Savings

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (appConfig.isProperty1Rented) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { showIncomeDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = BrandMintGreen)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit Income", color = BrandMintGreen, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        OutlinedButton(
                            onClick = {
                                editingSavingsPerson = selectedPerson
                                showSavingsDialog = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                        ) {
                            Icon(Icons.Default.Savings, contentDescription = null, modifier = Modifier.size(16.dp), tint = BrandMintGreen)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (activeSavings > 0) "Edit Savings" else "Add Savings",
                                color = BrandMintGreen,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
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
                } else {
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
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { showIncomeDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = BrandMintGreen)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit Income", color = BrandMintGreen, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
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
                    listOf("Highest $", "Lowest $", "A-Z", "Default").forEach { option ->
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

        val activeAccent = if (selectedPerson == 0) person1Color else person2Color

        items(activeList, key = { it.id }, contentType = { "expense_card" }) { item ->
            ExpenseItemCard(
                item = item,
                accentColor = activeAccent,
                isWealthHidden = isWealthHidden,
                onEdit = { itemToEdit = item },
                onDelete = {
                    itemToDelete = item
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
            onConfirm = { name, amount, iconName, billingCycle, nextDueDateMillis, linkedAssetId ->
                if (isEdit) {
                    if (selectedPerson == 0) onEditPerson1(itemToEdit!!.id, name, amount, iconName, billingCycle, nextDueDateMillis, linkedAssetId)
                    else onEditPerson2(itemToEdit!!.id, name, amount, iconName, billingCycle, nextDueDateMillis, linkedAssetId)
                } else {
                    if (selectedPerson == 0) onAddPerson1(name, amount, iconName, billingCycle, nextDueDateMillis, linkedAssetId)
                    else onAddPerson2(name, amount, iconName, billingCycle, nextDueDateMillis, linkedAssetId)
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

    if (showSavingsDialog) {
        val isPerson1 = editingSavingsPerson == 0
        val targetName = if (isPerson1) appConfig.person1Name else appConfig.person2Name
        val currentSavings = if (isPerson1) person1Savings else person2Savings
        EditSingleValueDialog(
            title = "Update $targetName's Cash Savings",
            label = "Cash Savings Balance ($)",
            initialValue = currentSavings,
            onDismiss = { showSavingsDialog = false },
            onConfirm = { newVal ->
                onUpdateSavings(isPerson1, newVal)
                showSavingsDialog = false
            }
        )
    }

    itemToDelete?.let { item ->
        val isExpense = item.weeklyAmount < 0
        val typeLabel = if (isExpense) "Expense" else "Income"
        ConfirmDeleteDialog(
            title = "Delete $typeLabel?",
            itemName = item.name,
            itemType = typeLabel.lowercase(),
            onDismiss = { itemToDelete = null },
            onConfirm = {
                if (selectedPerson == 0) onDeletePerson1(item.id) else onDeletePerson2(item.id)
                itemToDelete = null
            }
        )
    }
}

@Composable
fun SavingsOrb(
    personName: String,
    savingsAmount: Double,
    isWealthHidden: Boolean,
    modifier: Modifier = Modifier,
    accentColor: Color = BrandMintGreen,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = accentColor.copy(alpha = 0.12f)),
        border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(accentColor.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Savings,
                        contentDescription = "Savings",
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Column {
                    Text(
                        text = "$personName's Cash Savings",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (savingsAmount > 0) "Cash Asset • Tap to edit" else "+ Tap to add cash savings",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = SlateGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isWealthHidden) "$ ••••••" else formatCurrency(savingsAmount, 0),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
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
    val isDark = LocalIsDarkTheme.current
    val isPositive = surplus >= 0
    val arrowIcon = if (isPositive) Icons.Default.NorthEast else Icons.Default.SouthEast

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = if (isSelected) accent else if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.94f),
        border = BorderStroke(1.5.dp, if (isSelected) (if (isDark) Color.White else BrandForestDark) else accent.copy(alpha = 0.6f)),
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
    onAddProp1: (String, Double, String, BillingCycle, Long?, String?) -> Unit,
    onEditProp1: (String, String, Double, String, BillingCycle, Long?, String?) -> Unit,
    onDeleteProp1: (String) -> Unit,
    prop2WeeklyMortgage: Double,
    prop2Items: List<CashflowItem>,
    onAddProp2: (String, Double, String, BillingCycle, Long?, String?) -> Unit,
    onEditProp2: (String, String, Double, String, BillingCycle, Long?, String?) -> Unit,
    onDeleteProp2: (String) -> Unit,
    prop1MarketValue: Double,
    prop2MarketValue: Double,
    prop1LoanBalance: Double,
    prop2LoanBalance: Double,
    isWealthHidden: Boolean = false,
    onUpdatePropertyValuations: (Double, Double) -> Unit = { _, _ -> },
    onUpdateAppConfig: (UserAppConfig) -> Unit
) {
    var selectedProperty by remember { mutableIntStateOf(0) }
    LaunchedEffect(appConfig.hasSecondProperty) {
        if (!appConfig.hasSecondProperty) selectedProperty = 0
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<CashflowItem?>(null) }
    var itemToDelete by remember { mutableStateOf<CashflowItem?>(null) }
    var showAddressEditDialog by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf("Highest $") }

    fun maskVal(amt: Double, decimals: Int = 0): String = if (isWealthHidden) "$ ••••••" else formatCurrency(amt, decimals)



    val prop1HousingCost = if (appConfig.isProperty1Rented) -appConfig.primaryRentWeekly else prop1WeeklyMortgage
    val prop1Net = prop1HousingCost + prop1Items.sumOf { it.weeklyAmount }

    val prop2RentIncome = if (appConfig.hasSecondProperty) appConfig.secondaryRentWeekly else 0.0
    val prop2Mortgage = if (appConfig.hasSecondProperty) prop2WeeklyMortgage else 0.0
    val prop2Expenses = if (appConfig.hasSecondProperty) prop2Items.sumOf { it.weeklyAmount } else 0.0
    val prop2Net = prop2RentIncome + prop2Mortgage + prop2Expenses

    val orbSpacing = 14.dp
    val headerTopPad = rememberHeaderAwareTopPadding(orbSpacing)

    val rawActiveList = if (selectedProperty == 0) prop1Items else prop2Items
    val activeList = remember(rawActiveList, sortBy) {
        when (sortBy) {
            "Highest $" -> rawActiveList.sortedByDescending { abs(it.weeklyAmount) }
            "Lowest $" -> rawActiveList.sortedBy { abs(it.weeklyAmount) }
            "A-Z" -> rawActiveList.sortedBy { it.name.lowercase() }
            else -> rawActiveList
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = headerTopPad, bottom = 140.dp),
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
                        accent = BrandCoralPink,
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
            var showValueEditDialog by remember { mutableStateOf(false) }

            if (isProp1) {
                val isRented = appConfig.isProperty1Rented
                if (isRented) {
                    SwipeToRevealBox(
                        onEdit = { showRentEditDialog = true }
                    ) {
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
                                }

                                val currentRent = appConfig.primaryRentWeekly
                                LoanDetailRow("Weekly Gross Rent", maskVal(currentRent, 2), highlightColor = BrandMintGreen)

                                if (!appConfig.isProperty1Rented) {
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
                } else {
                    SwipeToRevealBox(
                        onEdit = { showValueEditDialog = true }
                    ) {
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
                }
            } else {
                // Property 2: Investment Property — Show BOTH Valuation & Equity AND Rental Summary together in one unified view!
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    SwipeToRevealBox(
                        onEdit = { showValueEditDialog = true }
                    ) {
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

                    SwipeToRevealBox(
                        onEdit = { showRentEditDialog = true }
                    ) {
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
                                }

                                val currentRent = appConfig.secondaryRentWeekly
                                LoanDetailRow("Weekly Gross Rent", maskVal(currentRent, 2), highlightColor = BrandMintGreen)

                                if (!appConfig.isProperty1Rented) {
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
                }
            }

            if (showValueEditDialog) {
                EditSingleValueDialog(
                    title = "Update $activePropName Value",
                    label = "Estimated Market Value ($)",
                    initialValue = estVal,
                    onDismiss = { showValueEditDialog = false },
                    onConfirm = { newVal ->
                        if (isProp1) {
                            onUpdatePropertyValuations(newVal, prop2MarketValue)
                        } else {
                            onUpdatePropertyValuations(prop1MarketValue, newVal)
                        }
                        showValueEditDialog = false
                    }
                )
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
                            onAddProp1(name, amount, "Utility", BillingCycle.WEEKLY, null, null)
                        } else {
                            onAddProp2(name, amount, "Utility", BillingCycle.WEEKLY, null, null)
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
            val btnColor = BrandCoralPink
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
                    listOf("Highest $", "Lowest $", "A-Z", "Default").forEach { option ->
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
                if (appConfig.showLoans && !(isProp1 && appConfig.isProperty1Rented) && !(!isProp1 && appConfig.isProperty2Rented)) {
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

        val activeAccent = BrandCoralPink

        items(activeList, key = { it.id }, contentType = { "property_expense_card" }) { item ->
            ExpenseItemCard(
                item = item,
                accentColor = if (item.weeklyAmount >= 0) BrandMintGreen else activeAccent,
                isWealthHidden = isWealthHidden,
                onEdit = { itemToEdit = item },
                onDelete = {
                    itemToDelete = item
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
            onConfirm = { name, amount, iconName, billingCycle, nextDueDateMillis, linkedAssetId ->
                if (isEdit) {
                    if (selectedProperty == 0) onEditProp1(itemToEdit!!.id, name, amount, iconName, billingCycle, nextDueDateMillis, linkedAssetId)
                    else onEditProp2(itemToEdit!!.id, name, amount, iconName, billingCycle, nextDueDateMillis, linkedAssetId)
                } else {
                    if (selectedProperty == 0) onAddProp1(name, amount, iconName, billingCycle, nextDueDateMillis, linkedAssetId)
                    else onAddProp2(name, amount, iconName, billingCycle, nextDueDateMillis, linkedAssetId)
                }
                showAddDialog = false
                itemToEdit = null
            }
        )
    }

    itemToDelete?.let { item ->
        val isExpense = item.weeklyAmount < 0
        val typeLabel = if (isExpense) "Expense" else "Income"
        ConfirmDeleteDialog(
            title = "Delete $typeLabel?",
            itemName = item.name,
            itemType = typeLabel.lowercase(),
            onDismiss = { itemToDelete = null },
            onConfirm = {
                if (selectedProperty == 0) onDeleteProp1(item.id) else onDeleteProp2(item.id)
                itemToDelete = null
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
    val locale = currentLocale()
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
        contentPadding = PaddingValues(top = topPadding, bottom = 140.dp),
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
                            containerColor = if (selectedLoanIndex == 1) BrandCoralPink else MaterialTheme.colorScheme.surface,
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
            val isDarkLoanHero = LocalIsDarkTheme.current
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkLoanHero) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                ),
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
                            Text("${String.format(locale, "%.2f", activeLoan.interestRatePa)}%", color = BrandForestDark, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = if (isDarkLoanHero) Color.White.copy(alpha = 0.12f) else Color(0xFFCBD5E1))
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
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = if (isDarkLoanHero) Color.White.copy(alpha = 0.15f) else Color(0xFFE2E8F0)
                                )
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
                    LoanDetailRow("Start Balance (${formatDisplayDate(activeLoan.startDateStr)})", maskVal(activeLoan.startLoanBalance, 0))
                    LoanDetailRow("Current Remaining Balance", maskVal(liveBalance, 0), highlightColor = BrandCoralPink)
                    LoanDetailRow("Offset Account Balance", maskVal(activeLoan.offsetBalance, 0), highlightColor = BrandMintGreen)
                    LoanDetailRow("Net Balance Charging Interest", maskVal(netBalanceChargingInterest, 0))
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    LoanDetailRow("Standard Interest Rate", "${String.format(locale, "%.2f", activeLoan.interestRatePa)}% p.a.")
                    LoanDetailRow("Effective Rate With Offset", "${String.format(locale, "%.2f", effectiveRate)}% p.a.", highlightColor = BrandMintGreen)
                    LoanDetailRow("Calculated Fortnightly Repayment", maskVal(fnPmt, 0))
                    LoanDetailRow("Calculated Weekly Repayment", maskVal(wkPmt, 2))
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    LoanDetailRow("Interest Saved (Per Fortnight)", maskVal(fortnightlyOffsetSavings, 2), highlightColor = BrandMintGreen)
                    LoanDetailRow("Interest Saved (Per Month)", maskVal(monthlyOffsetSavings, 2), highlightColor = BrandMintGreen)
                    LoanDetailRow("Interest Saved (Per Year)", maskVal(annualOffsetSavings, 0), highlightColor = BrandMintGreen)
                    LoanDetailRow("Estimated Payoff Time Left", "${String.format(locale, "%.1f", actualPayoffYearsLeft)} Years")
                    LoanDetailRow("Years Saved By Offset & Advance", "${String.format(locale, "%.2f", yearsSaved)} Years Saved", highlightColor = BrandCoralPink)
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
                colors = CardDefaults.cardColors(
                    containerColor = if (LocalIsDarkTheme.current) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                )
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
    person1Savings: Double = 0.0,
    person2Savings: Double = 0.0,
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
    incomeGrowthPerson1Weekly: Float = 0f,
    onIncomeGrowthPerson1WeeklyChange: (Float) -> Unit = {},
    incomeGrowthPerson2Weekly: Float = 0f,
    onIncomeGrowthPerson2WeeklyChange: (Float) -> Unit = {},
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
    onExecuteAiActions: (String) -> Pair<String, List<String>> = { it to emptyList() },
    learnedFacts: List<LearnedUserFact> = emptyList(),
    conversationDigest: String = ""
) {
    fun maskVal(valAmt: Double, decimals: Int = 0): String {
        return if (isWealthHidden) "$ ••••••" else formatCurrency(valAmt, decimals)
    }

    var activeScenarioId by rememberSaveable { mutableStateOf("status_quo") }

    val locale = currentLocale()
    val isTenantMode = appConfig.isProperty1Rented

    // Baseline Financial Calculations (matching Total Wealth & Cash Flow tabs)
    val totalPropertyVal = if (appConfig.showProperties && !isTenantMode) {
        prop1MarketValue + (if (appConfig.hasSecondProperty && !appConfig.isProperty2Rented) prop2MarketValue else 0.0)
    } else 0.0
    val totalSuperVal = if (appConfig.showSuperAndShares) superAccountsList.sumOf { it.balance } else 0.0
    val totalSharesVal = if (appConfig.showSuperAndShares) sharesList.sumOf { it.units * it.currentPrice } else 0.0
    val totalOtherAssetsVal = if (appConfig.showOtherAssets) otherAssetsList.sumOf { it.value } else 0.0
    val totalOffsets = if (appConfig.showLoans && !isTenantMode) {
        prop1Loan.offsetBalance + (if (appConfig.hasSecondProperty && !appConfig.isProperty2Rented) prop2Loan.offsetBalance else 0.0)
    } else 0.0
    val totalCashSavings = if (isTenantMode) {
        person1Savings + (if (appConfig.isCoupleMode) person2Savings else 0.0)
    } else 0.0

    val totalAssets = totalPropertyVal + totalSuperVal + totalSharesVal + totalOtherAssetsVal + totalOffsets + totalCashSavings

    val p1AutoFortnights = getElapsedFortnightsSinceStart(prop1Loan.startDateStr)
    val p1Schedule = remember(prop1Loan) { generateAmortizationSchedule(prop1Loan, 780) }
    val p1CurrentRow = p1Schedule.getOrNull(p1AutoFortnights.coerceIn(0, p1Schedule.size - 1)) ?: p1Schedule.first()
    val p1LiveBalance = if (appConfig.showLoans && !isTenantMode) p1CurrentRow.closingBalance else 0.0

    val p2AutoFortnights = getElapsedFortnightsSinceStart(prop2Loan.startDateStr)
    val p2Schedule = remember(prop2Loan) { generateAmortizationSchedule(prop2Loan, 780) }
    val p2CurrentRow = p2Schedule.getOrNull(p2AutoFortnights.coerceIn(0, p2Schedule.size - 1)) ?: p2Schedule.first()
    val p2LiveBalance = if (appConfig.showLoans && appConfig.hasSecondProperty && !isTenantMode && !appConfig.isProperty2Rented) p2CurrentRow.closingBalance else 0.0

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
    var milestoneToDelete by remember { mutableStateOf<LifeMilestoneBlock?>(null) }
    var showAddCustomLeverDialog by remember { mutableStateOf(false) }
    var customLeverToEdit by remember { mutableStateOf<CustomWhatIfLever?>(null) }
    var customLeverToDelete by remember { mutableStateOf<CustomWhatIfLever?>(null) }
    var whatIfChatInput by remember { mutableStateOf("") }
    val whatIfChatHistory = remember { mutableStateListOf<ChatMessage>() }
    var isWhatIfAiLoading by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val milestoneFingerprint = milestoneList.map {
        "${it.id}_${it.title}_${it.startYear}_${it.durationYears}_${it.weeklyCashflowImpact}_${it.assetImpact}_${it.liabilityImpact}_${it.isEnabled}"
    }
    val customLeversFingerprint = customLevers.map {
        "${it.id}_${it.title}_${it.impactType}_${it.minValue}_${it.maxValue}_${it.stepSize}_${it.currentValue}_${it.isEnabled}"
    }

    val effectiveIncomeGrowthWeekly = incomeGrowthPerson1Weekly + (if (appConfig.isCoupleMode) incomeGrowthPerson2Weekly else 0f)
    val effectiveRentalCashflowDelta = if (isTenantMode) -rentalIncomeGrowthWeekly else rentalIncomeGrowthWeekly

    val proj = remember(
        baseNetWorth, baseSurplusWeekly, horizonYears,
        totalSharesVal, totalSuperVal, totalPropertyVal, totalOtherAssetsVal, totalOffsets, totalCashSavings, totalLiabilities,
        appConfig.showLoans, appConfig.hasSecondProperty, isTenantMode, appConfig.isProperty2Rented, prop1Loan, prop2Loan, p1LiveBalance, p2LiveBalance,
        lifestyleAdjustmentWeekly, effectiveIncomeGrowthWeekly, rateShockProp1, rateShockProp2, effectiveRentalCashflowDelta, superBoostWeekly,
        milestoneFingerprint, customLeversFingerprint
    ) {
        computeWhatIfProjection(
            horizonYears = horizonYears,
            basePropertyVal = totalPropertyVal,
            baseSharesAndSuperVal = totalSharesVal + totalSuperVal,
            baseOtherAssetsVal = totalOtherAssetsVal,
            baseOffsetsVal = totalOffsets + totalCashSavings,
            baseLiabilitiesVal = totalLiabilities,
            baseSurplusWeekly = baseSurplusWeekly,
            prop1Loan = prop1Loan,
            prop2Loan = prop2Loan,
            hasSecondProperty = appConfig.hasSecondProperty,
            showLoans = appConfig.showLoans && !isTenantMode,
            lifestyleAdjustmentWeekly = lifestyleAdjustmentWeekly,
            incomeGrowthWeekly = effectiveIncomeGrowthWeekly,
            rateShockProp1 = if (isTenantMode) 0f else rateShockProp1,
            rateShockProp2 = if (isTenantMode) 0f else rateShockProp2,
            rentalIncomeGrowthWeekly = effectiveRentalCashflowDelta,
            superBoostWeekly = superBoostWeekly,
            customLevers = customLevers.toList(),
            milestones = milestoneList.toList(),
            prop1LiveBalance = p1LiveBalance,
            prop2LiveBalance = p2LiveBalance,
            isProperty2Rented = appConfig.isProperty2Rented
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

    val year1MilestoneWeekly = milestoneList.filter { it.isEnabled && 1 >= it.startYear && 1 < it.startYear + it.durationYears }.sumOf { it.weeklyCashflowImpact }
    val weeklyCashflowDeltaNow =
        lifestyleAdjustmentWeekly.toDouble() + effectiveIncomeGrowthWeekly.toDouble() - totalWeeklyRateShockCost + effectiveRentalCashflowDelta.toDouble() - (superBoostWeekly.toDouble() * 0.68) + proj.customWeeklyCashflowTotal + year1MilestoneWeekly
    val simulatedWeeklySurplusNow = baseSurplusWeekly + weeklyCashflowDeltaNow

    val hasAnyChanges = lifestyleAdjustmentWeekly != 0f ||
            incomeGrowthPerson1Weekly != 0f ||
            (appConfig.isCoupleMode && incomeGrowthPerson2Weekly != 0f) ||
            (!isTenantMode && rateShockProp1 != 0f) ||
            (!isTenantMode && rateShockProp2 != 0f) ||
            rentalIncomeGrowthWeekly != 0f ||
            superBoostWeekly != 0f ||
            customLevers.any { it.isEnabled && abs(it.currentValue) > 0.001f } ||
            milestoneList.any { it.isEnabled }

    fun resetAllToStatusQuo() {
        activeScenarioId = "status_quo"
        onLifestyleAdjustmentWeeklyChange(0f)
        onIncomeGrowthPerson1WeeklyChange(0f)
        onIncomeGrowthPerson2WeeklyChange(0f)
        onRateShockProp1Change(0f)
        onRateShockProp2Change(0f)
        onRentalIncomeGrowthWeeklyChange(0f)
        onSuperBoostWeeklyChange(0f)
        customLevers.forEachIndexed { idx, cl ->
            customLevers[idx] = cl.copy(currentValue = 0f)
        }
        milestoneList.clear()
    }

    // Australian Square Tiles Scenario Explorer (Tailored for Aussie Renters vs Mortgage Holders)
    val scenarioTiles = if (isTenantMode) {
        listOf(
            AussieScenarioTile(
                id = "status_quo",
                title = "Status Quo",
                badge = "Baseline",
                icon = Icons.Default.Refresh,
                color = SlateGray,
                onApply = { resetAllToStatusQuo() }
            ),
            AussieScenarioTile(
                id = "rent_hike",
                title = "Rent Hike",
                badge = "+$60/wk Renewal",
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                color = BrandCoralPink,
                onApply = {
                    activeScenarioId = "rent_hike"
                    onLifestyleAdjustmentWeeklyChange(0f)
                    onIncomeGrowthPerson1WeeklyChange(0f)
                    onIncomeGrowthPerson2WeeklyChange(0f)
                    onRateShockProp1Change(0f)
                    onRateShockProp2Change(0f)
                    onRentalIncomeGrowthWeeklyChange(60f)
                    onSuperBoostWeeklyChange(0f)
                    milestoneList.clear()
                }
            ),
            AussieScenarioTile(
                id = "cheaper_rental",
                title = "Cheaper Rental",
                badge = "Save $100/wk",
                icon = Icons.AutoMirrored.Filled.TrendingDown,
                color = BrandMintGreen,
                onApply = {
                    activeScenarioId = "cheaper_rental"
                    onLifestyleAdjustmentWeeklyChange(0f)
                    onIncomeGrowthPerson1WeeklyChange(0f)
                    onIncomeGrowthPerson2WeeklyChange(0f)
                    onRateShockProp1Change(0f)
                    onRateShockProp2Change(0f)
                    onRentalIncomeGrowthWeeklyChange(-100f)
                    onSuperBoostWeeklyChange(0f)
                    milestoneList.clear()
                }
            ),
            AussieScenarioTile(
                id = "house_deposit",
                title = "Deposit Sprint",
                badge = "+$250/wk Saved",
                icon = Icons.Default.Savings,
                color = BrandSkyBlue,
                onApply = {
                    activeScenarioId = "house_deposit"
                    onLifestyleAdjustmentWeeklyChange(250f)
                    onIncomeGrowthPerson1WeeklyChange(0f)
                    onIncomeGrowthPerson2WeeklyChange(0f)
                    onRateShockProp1Change(0f)
                    onRateShockProp2Change(0f)
                    onRentalIncomeGrowthWeeklyChange(0f)
                    onSuperBoostWeeklyChange(0f)
                    milestoneList.clear()
                }
            ),
            AussieScenarioTile(
                id = "buy_first_home",
                title = "Buy First Home",
                badge = "$650k in Yr 3",
                icon = Icons.Default.Home,
                color = BrandWarmAmber,
                onApply = {
                    activeScenarioId = "buy_first_home"
                    onLifestyleAdjustmentWeeklyChange(0f)
                    onIncomeGrowthPerson1WeeklyChange(0f)
                    onIncomeGrowthPerson2WeeklyChange(0f)
                    onRateShockProp1Change(0f)
                    onRateShockProp2Change(0f)
                    onRentalIncomeGrowthWeeklyChange(0f)
                    onSuperBoostWeeklyChange(0f)
                    milestoneList.clear()
                    milestoneList.add(
                        LifeMilestoneBlock(
                            id = "m_au_first_home",
                            title = "Buy First Home ($650k)",
                            category = MilestoneCategory.PROPERTY,
                            startYear = 3,
                            durationYears = 25,
                            weeklyCashflowImpact = -200.0,
                            assetImpact = 650000.0,
                            liabilityImpact = 520000.0,
                            isEnabled = true
                        )
                    )
                }
            ),
            AussieScenarioTile(
                id = "super_boost",
                title = "Super / FHSS",
                badge = "+$150/wk Sacr.",
                icon = Icons.Default.AccountBalance,
                color = BrandPurple,
                onApply = {
                    activeScenarioId = "super_boost"
                    onLifestyleAdjustmentWeeklyChange(0f)
                    onIncomeGrowthPerson1WeeklyChange(0f)
                    onIncomeGrowthPerson2WeeklyChange(0f)
                    onRateShockProp1Change(0f)
                    onRateShockProp2Change(0f)
                    onRentalIncomeGrowthWeeklyChange(0f)
                    onSuperBoostWeeklyChange(150f)
                    milestoneList.clear()
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
                    onIncomeGrowthPerson1WeeklyChange(0f)
                    onIncomeGrowthPerson2WeeklyChange(0f)
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
                    onIncomeGrowthPerson1WeeklyChange(0f)
                    onIncomeGrowthPerson2WeeklyChange(0f)
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
    } else {
        listOf(
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
                    onIncomeGrowthPerson1WeeklyChange(0f)
                    onIncomeGrowthPerson2WeeklyChange(0f)
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
                    onIncomeGrowthPerson1WeeklyChange(0f)
                    onIncomeGrowthPerson2WeeklyChange(0f)
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
                    onIncomeGrowthPerson1WeeklyChange(0f)
                    onIncomeGrowthPerson2WeeklyChange(0f)
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
                    onIncomeGrowthPerson1WeeklyChange(0f)
                    onIncomeGrowthPerson2WeeklyChange(0f)
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
                    onIncomeGrowthPerson1WeeklyChange(0f)
                    onIncomeGrowthPerson2WeeklyChange(0f)
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
                    onIncomeGrowthPerson1WeeklyChange(0f)
                    onIncomeGrowthPerson2WeeklyChange(0f)
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
                    onIncomeGrowthPerson1WeeklyChange(0f)
                    onIncomeGrowthPerson2WeeklyChange(0f)
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
    }

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
    val whatIfListState = rememberLazyListState()

    // Floating Live Projection pop-up when Projected Net Worth chart is scrolled out of view
    val isProjectionCardOutOfView by remember {
        derivedStateOf {
            val visibleItems = whatIfListState.layoutInfo.visibleItemsInfo
            val item1 = visibleItems.firstOrNull { it.index == 1 }
            when {
                // Not in visible items at all
                item1 == null -> whatIfListState.firstVisibleItemIndex > 1
                // Partially scrolled off top: check if more than 60% scrolled off or header obscures it
                else -> {
                    val visibleHeight = item1.size - maxOf(0, -item1.offset)
                    visibleHeight < item1.size * 0.4f
                }
            }
        }
    }
    var isPopupDismissedManually by remember { mutableStateOf(false) }
    var isPopupExpanded by remember { mutableStateOf(true) }

    // Reset manual dismissal when scrolling back to the top so it pops up again when re-scrolling to levers
    LaunchedEffect(isProjectionCardOutOfView) {
        if (!isProjectionCardOutOfView) {
            isPopupDismissedManually = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = whatIfListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = headerTopPad, bottom = 150.dp),
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Scenario Explorer", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            IconButton(onClick = { showInfoDialog = true }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "Help", tint = BrandSkyBlue, modifier = Modifier.size(18.dp))
                            }
                        }
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
                    // Header + Timeframe Selector Pills (Responsive for wide Folds and narrow Flips)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Projected Net Worth", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            Text("In $horizonYears Years", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = BrandCoralPink)
                        }
                        Text("Baseline Today: ${maskVal(baseNetWorth, 0)}", fontSize = 11.sp, color = SlateGray)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(5, 10, 15, 20, 30).forEach { yrs ->
                                val selected = horizonYears == yrs
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onHorizonYearsChange(yrs) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selected) BrandCoralPink else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "${yrs}Y",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        softWrap = false,
                                        color = if (selected) BrandForestDark else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(vertical = 6.dp)
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
                            colors = CardDefaults.cardColors(
                                containerColor = if (LocalIsDarkTheme.current) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("STATUS QUO", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = SlateGray, maxLines = 1, softWrap = false)
                                Text(
                                    maskVal(endPathANetWorth, 0),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    "Surplus: ${maskVal(baseSurplusWeekly, 0)}/wk",
                                    fontSize = 11.sp,
                                    color = SlateGray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
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
                                Text("WHAT-IF PLAN", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = planHeaderColor, maxLines = 1, softWrap = false)
                                Text(
                                    maskVal(endPathBNetWorth, 0),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = planValueColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    "Surplus: ${maskVal(simulatedWeeklySurplusNow, 0)}/wk",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = planSubColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Visual Dual-Line Wealth Trajectory Chart
                    WhatIfTrajectoryChart(
                        pathASeries = pathASeries,
                        pathBSeries = pathBSeries,
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
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text(
                                    if (abs(netWorthDifference) < 1.0) "No changes active yet"
                                    else if (netWorthDifference > 0) "Net Wealth Gain in $horizonYears Yrs"
                                    else "Net Wealth Cost in $horizonYears Yrs",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    if (abs(weeklyCashflowDeltaNow) < 0.5) "Weekly cashflow unchanged"
                                    else "Weekly cashflow: ${if (weeklyCashflowDeltaNow > 0) "+" else ""}${maskVal(weeklyCashflowDeltaNow, 0)}/wk",
                                    fontSize = 11.sp,
                                    color = SlateGray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = if (abs(netWorthDifference) < 1.0) "$0"
                                else "${if (netWorthDifference > 0) "+" else ""}${maskVal(netWorthDifference, 0)}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp,
                                color = diffColor,
                                maxLines = 1,
                                softWrap = false
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

                    var leverIdx = 1

                    // LEVER 1: EXTRA WEEKLY SAVINGS / OFFSET
                    WhatIfInteractiveLeverCard(
                        title = if (isTenantMode) "${leverIdx++}. Extra Weekly Savings / Investments" else "${leverIdx++}. Extra Weekly Offset / Savings",
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

                    // INCOME GROWTH LEVER (MEMBER 1 — ALWAYS SHOWN FOR MORTGAGE HOLDER & TENANT)
                    val simIncomeP1 = (person1Income + incomeGrowthPerson1Weekly).coerceAtLeast(0.0)
                    WhatIfInteractiveLeverCard(
                        title = if (appConfig.isCoupleMode) {
                            "${leverIdx++}. ${appConfig.person1Name} Weekly Income Growth"
                        } else {
                            "${leverIdx++}. Weekly Income Growth (${appConfig.person1Name})"
                        },
                        subtitle = when {
                            abs(incomeGrowthPerson1Weekly) < 0.5f -> "Current Income: ${maskVal(person1Income, 0)}/wk (Slide right to simulate a pay rise or income growth)"
                            incomeGrowthPerson1Weekly > 0f -> "+${maskVal(incomeGrowthPerson1Weekly.toDouble(), 0)}/wk (+${maskVal(incomeGrowthPerson1Weekly * 52.0, 0)}/yr • New Income: ${maskVal(simIncomeP1, 0)}/wk)"
                            else -> "-${maskVal(abs(incomeGrowthPerson1Weekly.toDouble()), 0)}/wk (-${maskVal(abs(incomeGrowthPerson1Weekly) * 52.0, 0)}/yr • New Income: ${maskVal(simIncomeP1, 0)}/wk)"
                        },
                        value = incomeGrowthPerson1Weekly,
                        valueRange = -500f..1500f,
                        stepSize = 25f,
                        isPercentage = false,
                        unitSuffix = "/wk",
                        accentColor = if (incomeGrowthPerson1Weekly >= 0f) BrandMintGreen else BrandCoralPink,
                        presets = listOf(0f to "$0", 50f to "+$50", 100f to "+$100", 250f to "+$250", 500f to "+$500"),
                        onValueChange = { newVal ->
                            activeScenarioId = "custom"
                            onIncomeGrowthPerson1WeeklyChange(newVal)
                        }
                    )

                    // INCOME GROWTH LEVER (MEMBER 2 — SHOWN IN COUPLE MODE FOR MORTGAGE HOLDER & TENANT)
                    if (appConfig.isCoupleMode) {
                        HorizontalDivider()

                        val simIncomeP2 = (person2Income + incomeGrowthPerson2Weekly).coerceAtLeast(0.0)
                        WhatIfInteractiveLeverCard(
                            title = "${leverIdx++}. ${appConfig.person2Name} Weekly Income Growth",
                            subtitle = when {
                                abs(incomeGrowthPerson2Weekly) < 0.5f -> "Current Income: ${maskVal(person2Income, 0)}/wk (Slide right to simulate a pay rise or income growth)"
                                incomeGrowthPerson2Weekly > 0f -> "+${maskVal(incomeGrowthPerson2Weekly.toDouble(), 0)}/wk (+${maskVal(incomeGrowthPerson2Weekly * 52.0, 0)}/yr • New Income: ${maskVal(simIncomeP2, 0)}/wk)"
                                else -> "-${maskVal(abs(incomeGrowthPerson2Weekly.toDouble()), 0)}/wk (-${maskVal(abs(incomeGrowthPerson2Weekly) * 52.0, 0)}/yr • New Income: ${maskVal(simIncomeP2, 0)}/wk)"
                            },
                            value = incomeGrowthPerson2Weekly,
                            valueRange = -500f..1500f,
                            stepSize = 25f,
                            isPercentage = false,
                            unitSuffix = "/wk",
                            accentColor = if (incomeGrowthPerson2Weekly >= 0f) BrandMintGreen else BrandCoralPink,
                            presets = listOf(0f to "$0", 50f to "+$50", 100f to "+$100", 250f to "+$250", 500f to "+$500"),
                            onValueChange = { newVal ->
                                activeScenarioId = "custom"
                                onIncomeGrowthPerson2WeeklyChange(newVal)
                            }
                        )
                    }

                    HorizontalDivider()

                    if (isTenantMode) {
                        // TENANT LEVER: SIMULATE WEEKLY RENT INCREASE / CHANGE (EXPENSE)
                        val simulatedWeeklyRent = (appConfig.primaryRentWeekly + rentalIncomeGrowthWeekly).coerceAtLeast(0.0)
                        WhatIfInteractiveLeverCard(
                            title = "${leverIdx++}. Weekly Rent Increase (${appConfig.property1Name})",
                            subtitle = when {
                                abs(rentalIncomeGrowthWeekly) < 0.5f -> "Current Rent: ${maskVal(appConfig.primaryRentWeekly, 0)}/wk (Slide right to simulate a rent increase)"
                                rentalIncomeGrowthWeekly > 0f -> "Costs -${maskVal(rentalIncomeGrowthWeekly.toDouble(), 0)}/wk extra (-${maskVal(rentalIncomeGrowthWeekly * 52.0, 0)}/yr • New Rent: ${maskVal(simulatedWeeklyRent, 0)}/wk)"
                                else -> "Saves +${maskVal(abs(rentalIncomeGrowthWeekly.toDouble()), 0)}/wk (+${maskVal(abs(rentalIncomeGrowthWeekly) * 52.0, 0)}/yr • New Rent: ${maskVal(simulatedWeeklyRent, 0)}/wk)"
                            },
                            value = rentalIncomeGrowthWeekly,
                            valueRange = -200f..400f,
                            stepSize = 10f,
                            isPercentage = false,
                            unitSuffix = "/wk",
                            accentColor = if (rentalIncomeGrowthWeekly <= 0f) BrandMintGreen else BrandCoralPink,
                            presets = listOf(-100f to "-$100", -50f to "-$50", 0f to "$0", 50f to "+$50", 100f to "+$100"),
                            onValueChange = { newVal ->
                                activeScenarioId = "custom"
                                onRentalIncomeGrowthWeeklyChange(newVal)
                            }
                        )

                        HorizontalDivider()

                        // TENANT LEVER: SUPERANNUATION SALARY SACRIFICE BOOST
                        WhatIfInteractiveLeverCard(
                            title = "$leverIdx. Extra Super Salary Sacrifice",
                            subtitle = if (superBoostWeekly == 0f) "After 15% super tax (~32% take-home offset), compounding at 7.5% p.a."
                            else "+${maskVal(superBoostWeekly * 0.85 * 52.0, 0)}/yr into Super (-${maskVal(superBoostWeekly * 0.68, 0)}/wk take-home)",
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
                    } else {
                        // MORTGAGE HOLDER LEVER: PROPERTY 1 INTEREST RATE CHANGE
                        WhatIfInteractiveLeverCard(
                            title = "${leverIdx++}. ${appConfig.property1Name} Interest Rate Δ",
                            subtitle = when {
                                abs(weeklyMortgageDelta1) < 0.5 -> "${prop1Loan.name}: ${String.format(locale, "%.2f", baseRate1)}% p.a."
                                weeklyMortgageDelta1 < 0 -> "Saves +${maskVal(abs(weeklyMortgageDelta1), 0)}/wk (${String.format(locale, "%.2f", shockedRate1)}% p.a.)"
                                else -> "Costs -${maskVal(weeklyMortgageDelta1, 0)}/wk extra (${String.format(locale, "%.2f", shockedRate1)}% p.a.)"
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

                        // MORTGAGE HOLDER LEVER: PROPERTY 2 INTEREST RATE CHANGE (DEDICATED LEVER PER PROPERTY)
                        WhatIfInteractiveLeverCard(
                            title = "${leverIdx++}. ${if (appConfig.hasSecondProperty) appConfig.property2Name else "Property 2"} Interest Rate Δ",
                            subtitle = if (!appConfig.hasSecondProperty || appConfig.isProperty2Rented) {
                                "Test hypothetical $500k 2nd property loan rate changes (${String.format(locale, "%.2f", shockedRate2)}% p.a.)"
                            } else {
                                when {
                                    abs(weeklyMortgageDelta2) < 0.5 -> "${prop2Loan.name}: ${String.format(locale, "%.2f", baseRate2)}% p.a."
                                    weeklyMortgageDelta2 < 0 -> "Saves +${maskVal(abs(weeklyMortgageDelta2), 0)}/wk (${String.format(locale, "%.2f", shockedRate2)}% p.a.)"
                                    else -> "Costs -${maskVal(weeklyMortgageDelta2, 0)}/wk extra (${String.format(locale, "%.2f", shockedRate2)}% p.a.)"
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

                        // MORTGAGE HOLDER LEVER: SUPERANNUATION SALARY SACRIFICE BOOST
                        WhatIfInteractiveLeverCard(
                            title = "${leverIdx++}. Extra Super Salary Sacrifice",
                            subtitle = if (superBoostWeekly == 0f) "After 15% super tax (~32% take-home offset), compounding at 7.5% p.a."
                            else "+${maskVal(superBoostWeekly * 0.85 * 52.0, 0)}/yr into Super (-${maskVal(superBoostWeekly * 0.68, 0)}/wk take-home)",
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

                        // MORTGAGE HOLDER LEVER: RENTAL INCOME / PROPERTY CASHFLOW CHANGE
                        WhatIfInteractiveLeverCard(
                            title = "$leverIdx. Weekly Rent / Property Cashflow",
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
                    }

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
                                "${if (v > 0f) "+" else ""}${String.format(locale, "%.2f", v)}%"
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

                            SwipeToRevealBox(
                                onEdit = {
                                    customLeverToEdit = cLever
                                    showAddCustomLeverDialog = true
                                },
                                onDelete = { customLeverToDelete = cLever }
                            ) {
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
                                            Switch(
                                                checked = cLever.isEnabled,
                                                onCheckedChange = { enabled ->
                                                    customLevers[idx] = cLever.copy(isEnabled = enabled)
                                                }
                                            )
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
                        Spacer(Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = {
                                prefillTemplate = null
                                showAddMilestoneDialog = true
                            },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = BrandCoralPink, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Custom Event", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandCoralPink)
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
                            SwipeToRevealBox(
                                onEdit = { milestoneToEdit = milestone },
                                onDelete = { milestoneToDelete = milestone }
                            ) {
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
                            Text("Ask CoinBot About This Scenario", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                            Text("Get tailored Australian guidance on your numbers", fontSize = 11.sp, color = SlateGray)
                        }
                    }

                    val samplePrompts = if (isTenantMode) {
                        listOf(
                            "How does a $60/wk rent increase impact us?",
                            "When can we afford a $650k first home?",
                            "ASX ETF investing vs Super FHSS boost?"
                        )
                    } else {
                        listOf(
                            "Offset vs Super salary sacrifice for us?",
                            "Can we afford a $750k investment property?",
                            "Impact of two 0.25% RBA rate cuts?"
                        )
                    }
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
                                    val historySnapshot = whatIfChatHistory.toList()
                                    whatIfChatHistory.add(ChatMessage(q, true))
                                    isWhatIfAiLoading = true
                                    coroutineScope.launch {
                                        val lastItemIdx = (whatIfListState.layoutInfo.totalItemsCount - 1).coerceAtLeast(0)
                                        whatIfListState.animateScrollToItem(lastItemIdx)

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
                                            append("${appConfig.person1Name} Weekly Income Growth = ${formatCurrency(incomeGrowthPerson1Weekly.toDouble(), 2)}/wk. ")
                                            if (appConfig.isCoupleMode) {
                                                append("${appConfig.person2Name} Weekly Income Growth = ${formatCurrency(incomeGrowthPerson2Weekly.toDouble(), 2)}/wk. ")
                                            }
                                            append("Property 1 Rate Change = ${rateShockProp1}% (Prop 2 Rate Change = ${rateShockProp2}%, Total weekly repayment delta: ${formatCurrency(totalWeeklyRateShockCost, 2)}/wk). ")
                                            append("Extra Super Salary Sacrifice = ${formatCurrency(superBoostWeekly.toDouble(), 2)}/wk. ")
                                            append("Active Milestones: $activeMilestonesStr. ")
                                        }

                                        var streamMessageAdded = false
                                        var botMessageIndex = -1
                                        var lastStreamUiUpdateMs = 0L

                                        val rawReply = AiAssistantManager.askGeminiAssistant(
                                            prompt = q,
                                            contextSummary = if (financialContextSummary.isNotBlank()) "$financialContextSummary\n$whatIfContextSummary" else whatIfContextSummary,
                                            conversationHistory = historySnapshot,
                                            learnedFacts = learnedFacts,
                                            longTermConversationDigest = conversationDigest,
                                            onPartialText = { partialSoFar ->
                                                val now = System.currentTimeMillis()
                                                if (!streamMessageAdded || now - lastStreamUiUpdateMs >= 40L) {
                                                    lastStreamUiUpdateMs = now
                                                    withContext(Dispatchers.Main) {
                                                        val streamClean = cleanMarkdownText(
                                                            partialSoFar
                                                                .substringBefore("[[ACTION:")
                                                                .substringBefore("[ACTION:")
                                                        )
                                                        if (streamClean.isNotBlank()) {
                                                            if (!streamMessageAdded) {
                                                                streamMessageAdded = true
                                                                isWhatIfAiLoading = false
                                                                whatIfChatHistory.add(ChatMessage(text = streamClean, isFromUser = false))
                                                                botMessageIndex = whatIfChatHistory.lastIndex
                                                            } else if (botMessageIndex in whatIfChatHistory.indices) {
                                                                whatIfChatHistory[botMessageIndex] = whatIfChatHistory[botMessageIndex].copy(text = streamClean)
                                                            }
                                                            val updatedLastIdx = (whatIfListState.layoutInfo.totalItemsCount - 1).coerceAtLeast(0)
                                                            whatIfListState.scrollToItem(updatedLastIdx)
                                                        }
                                                    }
                                                }
                                            }
                                        )
                                        val (cleanReply, appliedList) = onExecuteAiActions(rawReply)
                                        if (streamMessageAdded && botMessageIndex in whatIfChatHistory.indices) {
                                            whatIfChatHistory[botMessageIndex] = ChatMessage(text = cleanReply, isFromUser = false, appliedActions = appliedList)
                                        } else {
                                            whatIfChatHistory.add(ChatMessage(text = cleanReply, isFromUser = false, appliedActions = appliedList))
                                        }
                                        isWhatIfAiLoading = false
                                        val updatedLastIdx = (whatIfListState.layoutInfo.totalItemsCount - 1).coerceAtLeast(0)
                                        whatIfListState.scrollToItem(updatedLastIdx)
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

        // ====================================================================
        // FLOATING LIVE WHAT-IF PROJECTION & CHART POP-UP
        // Shows up dynamically when the hero chart is scrolled out of view!
        // ====================================================================
        val isPopupVisible = isProjectionCardOutOfView && !isPopupDismissedManually
        val isPositiveDiff = netWorthDifference >= 0
        val planAccent = if (!hasAnyChanges) BrandSkyBlue else if (isPositiveDiff) BrandMintGreen else BrandCoralPink
        val diffColor = if (abs(netWorthDifference) < 1.0) SlateGray else if (netWorthDifference > 0) BrandMintGreen else BrandCoralPink

        AnimatedVisibility(
            visible = isPopupVisible,
            enter = slideInVertically(initialOffsetY = { -it / 2 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it / 2 }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(top = headerTopPad)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        coroutineScope.launch {
                            whatIfListState.animateScrollToItem(1)
                        }
                    },
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                border = BorderStroke(1.5.dp, planAccent.copy(alpha = 0.85f)),
                shadowElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Header Bar: Live Badge, Horizon, Collapse/Expand, Scroll-to & Close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = BrandMintGreen.copy(alpha = 0.25f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(BrandMintGreen, CircleShape)
                                        .padding(2.dp)
                                )
                            }
                            Text(
                                "Live What-If View",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = planAccent
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    "${horizonYears}Y",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrandCoralPink,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "Tap to view chart",
                                fontSize = 10.sp,
                                color = SlateGray,
                                modifier = Modifier.padding(end = 2.dp)
                            )
                            IconButton(
                                onClick = { isPopupExpanded = !isPopupExpanded },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    if (isPopupExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (isPopupExpanded) "Collapse" else "Expand",
                                    tint = SlateGray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = { isPopupDismissedManually = true },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Dismiss Pop-up",
                                    tint = SlateGray,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }

                    // Compact Live Numbers Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Status Quo
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    "STATUS QUO (${horizonYears}Y)",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateGray,
                                    maxLines = 1,
                                    softWrap = false
                                )
                                Text(
                                    maskVal(endPathANetWorth, 0),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    "${maskVal(baseSurplusWeekly, 0)}/wk",
                                    fontSize = 10.sp,
                                    color = SlateGray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // What-If Plan
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = planAccent.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, planAccent.copy(alpha = 0.6f))
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    "WHAT-IF PLAN",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = planAccent,
                                    maxLines = 1,
                                    softWrap = false
                                )
                                Text(
                                    maskVal(endPathBNetWorth, 0),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    "${maskVal(simulatedWeeklySurplusNow, 0)}/wk",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = planAccent,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Net Impact Pill
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = diffColor.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, diffColor.copy(alpha = 0.45f))
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    "NET DIFFERENCE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateGray,
                                    maxLines = 1,
                                    softWrap = false
                                )
                                Text(
                                    text = if (abs(netWorthDifference) < 1.0) "$0"
                                    else "${if (netWorthDifference > 0) "+" else ""}${maskVal(netWorthDifference, 0)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = diffColor,
                                    maxLines = 1,
                                    softWrap = false
                                )
                                Text(
                                    text = if (abs(weeklyCashflowDeltaNow) < 0.5) "0/wk"
                                    else "${if (weeklyCashflowDeltaNow > 0) "+" else ""}${maskVal(weeklyCashflowDeltaNow, 0)}/wk",
                                    fontSize = 10.sp,
                                    color = diffColor,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }

                    // Live Interactive Mini Wealth Trajectory Chart (Collapsible)
                    AnimatedVisibility(
                        visible = isPopupExpanded,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        WhatIfTrajectoryChart(
                            pathASeries = pathASeries,
                            pathBSeries = pathBSeries,
                            isPositiveDiff = netWorthDifference >= 0
                        )
                    }
                }
            }
        }
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("How Projections Are Calculated", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• Property Growth: Assumes 4% average annual capital growth.")
                    Text("• Shares & Super: Assumes 7.5% annual average market return.")
                    Text("• Offset & Cash Savings: In Home Owner mode, liquid cash sits in your mortgage offset account—reducing loan interest at your loan rate and paying down principal faster. Any cash above loan balances (and all cash in Tenant mode) earns ~4.5% p.a. HISA.")
                    Text("• Super Salary Sacrifice: Deducts estimated ~32% marginal take-home impact from cashflow and credits 85% (after 15% super tax) into Super compounding at 7.5% p.a.")
                    Text("• Wage & Rental Inflation: Compounds non-mortgage cashflow by ~3% p.a., while P&I mortgage repayments stay fixed in nominal dollars until paid off.")
                    Text("• Status Quo vs What-If: Status Quo projects your current baseline with identical loan & offset mechanics, while What-If layers in your levers, rate shocks, and life milestones (including cash deposits & purchases).")
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Got It", fontWeight = FontWeight.Bold, color = BrandCoralPink)
                }
            }
        )
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

    customLeverToDelete?.let { lever ->
        ConfirmDeleteDialog(
            title = "Delete Custom Lever?",
            itemName = lever.title,
            itemType = "custom lever",
            onDismiss = { customLeverToDelete = null },
            onConfirm = {
                customLevers.removeAll { it.id == lever.id }
                customLeverToDelete = null
            }
        )
    }

    milestoneToDelete?.let { milestone ->
        ConfirmDeleteDialog(
            title = "Delete Milestone?",
            itemName = milestone.title,
            itemType = "milestone",
            onDismiss = { milestoneToDelete = null },
            onConfirm = {
                milestoneList.removeAll { it.id == milestone.id }
                milestoneToDelete = null
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
    defaultValue: Float = 0f.coerceIn(valueRange.start, valueRange.endInclusive),
    onReset: (() -> Unit)? = null,
    onValueChange: (Float) -> Unit
) {
    val locale = currentLocale()
    var showNumberEntry by remember { mutableStateOf(false) }
    var typedValueText by remember(value, showNumberEntry, locale) {
        mutableStateOf(
            if (isPercentage) String.format(locale, "%.2f", value)
            else value.roundToInt().toString()
        )
    }

    val isModified = abs(value - defaultValue) > (if (isPercentage) 0.005f else 0.5f)

    fun applyTypedNumber() {
        val parsed = typedValueText.replace("$", "").replace("%", "").replace("+", "").trim().toFloatOrNull()
        if (parsed != null) {
            onValueChange(parsed.coerceIn(valueRange.start, valueRange.endInclusive))
        }
        showNumberEntry = false
    }

    fun resetLever() {
        onReset?.invoke() ?: onValueChange(defaultValue)
        typedValueText = if (isPercentage) String.format(locale, "%.2f", defaultValue)
        else defaultValue.roundToInt().toString()
        showNumberEntry = false
    }

    val formattedBadge = if (isPercentage) {
        when {
            abs(value) < 0.005f -> "0.00%"
            value > 0f -> "+${String.format(locale, "%.2f", value)}%"
            else -> "${String.format(locale, "%.2f", value)}%"
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    onClick = {
                        if (isModified) {
                            resetLever()
                        }
                    },
                    enabled = isModified,
                    shape = RoundedCornerShape(10.dp),
                    color = if (isModified) BrandCoralPink.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    border = BorderStroke(1.dp, if (isModified) BrandCoralPink.copy(alpha = 0.5f) else SlateGray.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Reset lever to baseline",
                            tint = if (isModified) BrandCoralPink else SlateGray.copy(alpha = 0.35f),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Reset",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isModified) BrandCoralPink else SlateGray.copy(alpha = 0.35f)
                        )
                    }
                }

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
                Text(
                    text = "Wealth Trajectory",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateGray,
                    maxLines = 1,
                    softWrap = false
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SlateGray))
                        Text("Status Quo", fontSize = 10.sp, color = SlateGray, maxLines = 1, softWrap = false)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(planColor))
                        Text("What-If Plan", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = planColor, maxLines = 1, softWrap = false)
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
    person1Savings: Double = 0.0,
    person2Savings: Double = 0.0,
    isWealthHidden: Boolean,
    onNavigateToSettings: () -> Unit,
    onNavigateToWhatIf: () -> Unit = {},
    prop1Items: List<CashflowItem> = emptyList(),
    prop2Items: List<CashflowItem> = emptyList(),
    onAddPerson1: (String, Double, String, BillingCycle, Long?, String?) -> Unit = { _, _, _, _, _, _ -> },
    onEditPerson1: (String, String, Double, String, BillingCycle, Long?, String?) -> Unit = { _, _, _, _, _, _, _ -> },
    onDeletePerson1: (String) -> Unit = {},
    onAddPerson2: (String, Double, String, BillingCycle, Long?, String?) -> Unit = { _, _, _, _, _, _ -> },
    onEditPerson2: (String, String, Double, String, BillingCycle, Long?, String?) -> Unit = { _, _, _, _, _, _, _ -> },
    onDeletePerson2: (String) -> Unit = {},
    onAddProp1: (String, Double, String, BillingCycle, Long?, String?) -> Unit = { _, _, _, _, _, _ -> },
    onEditProp1: (String, String, Double, String, BillingCycle, Long?, String?) -> Unit = { _, _, _, _, _, _, _ -> },
    onDeleteProp1: (String) -> Unit = {},
    onAddProp2: (String, Double, String, BillingCycle, Long?, String?) -> Unit = { _, _, _, _, _, _ -> },
    onEditProp2: (String, String, Double, String, BillingCycle, Long?, String?) -> Unit = { _, _, _, _, _, _, _ -> },
    onDeleteProp2: (String) -> Unit = {}
) {
    val locale = currentLocale()
    fun maskVal(valAmt: Double, decimals: Int = 0): String {
        return if (isWealthHidden) "$ ••••••" else formatCurrency(valAmt, decimals)
    }

    val context = LocalContext.current
    val subAuditPrefs = remember { context.getSharedPreferences("coinlens_sub_audit_v1", Context.MODE_PRIVATE) }
    var keptSubscriptions by remember {
        mutableStateOf(subAuditPrefs.getStringSet("kept_merchants", emptySet())?.toSet() ?: emptySet())
    }
    var cancelTargetSubscriptions by remember {
        mutableStateOf(subAuditPrefs.getStringSet("cancel_merchants", emptySet())?.toSet() ?: emptySet())
    }
    var negotiateTargetSubscriptions by remember {
        mutableStateOf(subAuditPrefs.getStringSet("negotiate_merchants", emptySet())?.toSet() ?: emptySet())
    }
    var reclaimedAnnualTotal by remember {
        mutableDoubleStateOf(subAuditPrefs.getFloat("reclaimed_annual_total", 0f).toDouble())
    }
    var subscriptionLeaks by remember { mutableStateOf<List<SubscriptionLeak>>(emptyList()) }
    var isScanningLeaks by remember { mutableStateOf(false) }
    var showSubCommandCenterDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val person1Color = Color(appConfig.person1ColorHex)
    val person2Color = Color(appConfig.person2ColorHex)

    val allRecurringEntries = remember(person1Items, person2Items, prop1Items, prop2Items, appConfig) {
        buildList {
            person1Items.filter { it.weeklyAmount < 0 }.forEach { item ->
                val (isSub, catTag) = classifyRecurringCategory(item.name, item.iconName)
                add(
                    TrackedRecurringEntry(
                        item = item,
                        sourceKey = "person1",
                        ownerLabel = appConfig.person1Name,
                        ownerColor = person1Color,
                        isDetectedSubscription = isSub,
                        categoryTag = catTag
                    )
                )
            }
            if (appConfig.isCoupleMode) {
                person2Items.filter { it.weeklyAmount < 0 }.forEach { item ->
                    val (isSub, catTag) = classifyRecurringCategory(item.name, item.iconName)
                    add(
                        TrackedRecurringEntry(
                            item = item,
                            sourceKey = "person2",
                            ownerLabel = appConfig.person2Name,
                            ownerColor = person2Color,
                            isDetectedSubscription = isSub,
                            categoryTag = catTag
                        )
                    )
                }
            }
            if (appConfig.showProperties) {
                prop1Items.filter { it.weeklyAmount < 0 }.forEach { item ->
                    val (isSub, catTag) = classifyRecurringCategory(item.name, item.iconName)
                    add(
                        TrackedRecurringEntry(
                            item = item,
                            sourceKey = "prop1",
                            ownerLabel = appConfig.property1Name,
                            ownerColor = BrandCoralPink,
                            isDetectedSubscription = isSub,
                            categoryTag = catTag
                        )
                    )
                }
                if (appConfig.hasSecondProperty) {
                    prop2Items.filter { it.weeklyAmount < 0 }.forEach { item ->
                        val (isSub, catTag) = classifyRecurringCategory(item.name, item.iconName)
                        add(
                            TrackedRecurringEntry(
                                item = item,
                                sourceKey = "prop2",
                                ownerLabel = appConfig.property2Name,
                                ownerColor = BrandWarmAmber,
                                isDetectedSubscription = isSub,
                                categoryTag = catTag
                            )
                        )
                    }
                }
            }
        }
    }

    fun setSubscriptionAuditStatus(merchant: String, status: String) {
        val clean = merchant.trim().lowercase()
        val nextKept = (keptSubscriptions - clean).let { if (status == "KEPT") it + clean else it }
        val nextCancel = (cancelTargetSubscriptions - clean).let { if (status == "CANCEL") it + clean else it }
        val nextNegotiate = (negotiateTargetSubscriptions - clean).let { if (status == "NEGOTIATE") it + clean else it }
        keptSubscriptions = nextKept
        cancelTargetSubscriptions = nextCancel
        negotiateTargetSubscriptions = nextNegotiate
        subAuditPrefs.edit {
            putStringSet("kept_merchants", nextKept)
            putStringSet("cancel_merchants", nextCancel)
            putStringSet("negotiate_merchants", nextNegotiate)
        }
        if (status != "NONE") {
            subscriptionLeaks = subscriptionLeaks.filter { it.merchant.trim().lowercase() != clean }
        }
    }

    fun resetAllAuditStatuses() {
        keptSubscriptions = emptySet()
        cancelTargetSubscriptions = emptySet()
        negotiateTargetSubscriptions = emptySet()
        subAuditPrefs.edit {
            remove("kept_merchants")
            remove("cancel_merchants")
            remove("negotiate_merchants")
        }
    }

    fun markAllSubscriptionsKept(merchants: List<String> = allRecurringEntries.map { it.item.name }) {
        val cleanTargets = (merchants + subscriptionLeaks.map { it.merchant })
            .map { it.trim().lowercase() }
            .filter { it.isNotBlank() }
            .toSet()
        val nextKept = keptSubscriptions + cleanTargets
        val nextCancel = cancelTargetSubscriptions - cleanTargets
        val nextNegotiate = negotiateTargetSubscriptions - cleanTargets
        keptSubscriptions = nextKept
        cancelTargetSubscriptions = nextCancel
        negotiateTargetSubscriptions = nextNegotiate
        subAuditPrefs.edit {
            putStringSet("kept_merchants", nextKept)
            putStringSet("cancel_merchants", nextCancel)
            putStringSet("negotiate_merchants", nextNegotiate)
        }
        subscriptionLeaks = subscriptionLeaks.filter { it.merchant.trim().lowercase() !in cleanTargets }
    }

    fun runAiLeakScan() {
        if (isScanningLeaks) return
        isScanningLeaks = true
        coroutineScope.launch {
            val allExpensesStr = allRecurringEntries.joinToString("; ") {
                "${it.ownerLabel}: ${it.item.name} ($${String.format(Locale.US, "%.2f", abs(it.item.weeklyAmount))}/wk, ${it.item.billingCycle.label})"
            }
            val detected = AiAssistantManager.detectSubscriptionLeaks(allExpensesStr)
            val currentKept = subAuditPrefs.getStringSet("kept_merchants", emptySet())?.toSet() ?: emptySet()
            val currentCancel = subAuditPrefs.getStringSet("cancel_merchants", emptySet())?.toSet() ?: emptySet()
            subscriptionLeaks = detected.filter {
                val key = it.merchant.trim().lowercase()
                key !in currentKept && key !in currentCancel
            }
            isScanningLeaks = false
        }
    }

    fun handleAddSubscriptionItem(
        sourceKey: String,
        name: String,
        weeklyAmt: Double,
        iconName: String,
        cycle: BillingCycle,
        dueMillis: Long?
    ) {
        val signedAmt = -abs(weeklyAmt)
        when (sourceKey) {
            "person2" -> onAddPerson2(name, signedAmt, iconName, cycle, dueMillis, null)
            "prop1" -> onAddProp1(name, signedAmt, iconName, cycle, dueMillis, null)
            "prop2" -> onAddProp2(name, signedAmt, iconName, cycle, dueMillis, null)
            else -> onAddPerson1(name, signedAmt, iconName, cycle, dueMillis, null)
        }
    }

    fun handleEditRecurringEntry(
        entry: TrackedRecurringEntry,
        name: String,
        weeklyAmt: Double,
        iconName: String,
        cycle: BillingCycle,
        dueMillis: Long?
    ) {
        val signedAmt = -abs(weeklyAmt)
        val assetId = entry.item.linkedAssetId
        when (entry.sourceKey) {
            "person2" -> onEditPerson2(entry.item.id, name, signedAmt, iconName, cycle, dueMillis, assetId)
            "prop1" -> onEditProp1(entry.item.id, name, signedAmt, iconName, cycle, dueMillis, assetId)
            "prop2" -> onEditProp2(entry.item.id, name, signedAmt, iconName, cycle, dueMillis, assetId)
            else -> onEditPerson1(entry.item.id, name, signedAmt, iconName, cycle, dueMillis, assetId)
        }
    }

    fun handleDeleteRecurringEntry(entry: TrackedRecurringEntry) {
        when (entry.sourceKey) {
            "person2" -> onDeletePerson2(entry.item.id)
            "prop1" -> onDeleteProp1(entry.item.id)
            "prop2" -> onDeleteProp2(entry.item.id)
            else -> onDeletePerson1(entry.item.id)
        }
    }

    fun handleReclaimCancelledEntry(entry: TrackedRecurringEntry) {
        val annualSaved = abs(entry.item.weeklyAmount) * 52.0
        val newTotal = reclaimedAnnualTotal + annualSaved
        reclaimedAnnualTotal = newTotal
        val clean = entry.item.name.trim().lowercase()
        val nextCancel = cancelTargetSubscriptions - clean
        val nextNegotiate = negotiateTargetSubscriptions - clean
        cancelTargetSubscriptions = nextCancel
        negotiateTargetSubscriptions = nextNegotiate
        subAuditPrefs.edit {
            putFloat("reclaimed_annual_total", newTotal.toFloat())
            putStringSet("cancel_merchants", nextCancel)
            putStringSet("negotiate_merchants", nextNegotiate)
        }
        subscriptionLeaks = subscriptionLeaks.filter { it.merchant.trim().lowercase() != clean }
        handleDeleteRecurringEntry(entry)
    }

    val totalPropertyVal = if (appConfig.showProperties && !appConfig.isProperty1Rented) {
        prop1MarketValue + (if (appConfig.hasSecondProperty && !appConfig.isProperty2Rented) prop2MarketValue else 0.0)
    } else 0.0

    val totalSuperVal = if (appConfig.showSuperAndShares) superAccountsList.sumOf { it.balance } else 0.0
    val totalSharesVal = if (appConfig.showSuperAndShares) sharesList.sumOf { it.units * it.currentPrice } else 0.0
    val totalOtherAssetsVal = if (appConfig.showOtherAssets) otherAssetsList.sumOf { it.value } else 0.0
    val totalOffsets = if (appConfig.showLoans && !appConfig.isProperty1Rented) {
        prop1Loan.offsetBalance + (if (appConfig.hasSecondProperty && !appConfig.isProperty2Rented) prop2Loan.offsetBalance else 0.0)
    } else 0.0
    val totalCashSavings = if (appConfig.isProperty1Rented) {
        person1Savings + (if (appConfig.isCoupleMode) person2Savings else 0.0)
    } else 0.0

    val totalAssets = totalPropertyVal + totalSuperVal + totalSharesVal + totalOtherAssetsVal + totalOffsets + totalCashSavings

    val p1AutoFortnights = remember(prop1Loan.startDateStr) { getElapsedFortnightsSinceStart(prop1Loan.startDateStr) }
    val p1Schedule = remember(prop1Loan) { generateAmortizationSchedule(prop1Loan, 780) }
    val p1CurrentRow = p1Schedule.getOrNull(p1AutoFortnights.coerceIn(0, p1Schedule.size - 1)) ?: p1Schedule.first()
    val p1LiveBalance = if (appConfig.showLoans && !appConfig.isProperty1Rented) p1CurrentRow.closingBalance else 0.0

    val p2AutoFortnights = remember(prop2Loan.startDateStr) { getElapsedFortnightsSinceStart(prop2Loan.startDateStr) }
    val p2Schedule = remember(prop2Loan, appConfig.hasSecondProperty) {
        if (appConfig.hasSecondProperty) generateAmortizationSchedule(prop2Loan, 780) else emptyList()
    }
    val p2CurrentRow = p2Schedule.getOrNull(p2AutoFortnights.coerceIn(0, max(0, p2Schedule.size - 1)))
    val p2LiveBalance = if (appConfig.showLoans && appConfig.hasSecondProperty && !appConfig.isProperty1Rented && !appConfig.isProperty2Rented) {
        p2CurrentRow?.closingBalance ?: 0.0
    } else 0.0

    val totalLiabilities = p1LiveBalance + p2LiveBalance
    val netWorth = totalAssets - totalLiabilities

    val dynamicChartPairs = buildList {
        if (appConfig.showProperties && totalPropertyVal > 0) add(Triple("Properties", totalPropertyVal, BrandCoralPink))
        if (appConfig.showSuperAndShares && totalSuperVal > 0) add(Triple("Superannuation", totalSuperVal, BrandWarmAmber))
        if (appConfig.showSuperAndShares && totalSharesVal > 0) add(Triple("Shares Portfolio", totalSharesVal, BrandSkyBlue))
        if (appConfig.showOtherAssets && totalOtherAssetsVal > 0) add(Triple("Other Assets", totalOtherAssetsVal, BrandPurple))
        if (appConfig.showLoans && totalOffsets > 0) add(Triple("Offset / Cash", totalOffsets, BrandMintGreen))
        if (appConfig.isProperty1Rented && totalCashSavings > 0) add(Triple("Cash (Combined)", totalCashSavings, BrandMintGreen))
    }

    val orbSpacing = 16.dp
    val headerTopPad = rememberHeaderAwareTopPadding(orbSpacing)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = headerTopPad, bottom = 140.dp),
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
                    Text(
                        if (appConfig.isCoupleMode) "COMBINED NET WEALTH" else "TOTAL NET WEALTH",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 1.2.sp
                    )
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
                                    if (isWealthHidden) "•••••• (${String.format(locale, "%.1f", pct)}%)"
                                    else "${formatCurrency(triple.second, 0)} (${String.format(locale, "%.1f", pct)}%)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // INTERACTIVE SUBSCRIPTION & LEAK AUDIT ORB
        item {
            SubscriptionAndLeakAuditOrbCard(
                allRecurringEntries = allRecurringEntries,
                subscriptionLeaks = subscriptionLeaks,
                keptSubscriptions = keptSubscriptions,
                cancelTargetSubscriptions = cancelTargetSubscriptions,
                reclaimedAnnualTotal = reclaimedAnnualTotal,
                isScanningLeaks = isScanningLeaks,
                isWealthHidden = isWealthHidden,
                onOpenCommandCenter = { showSubCommandCenterDialog = true },
                onRunAiLeakScan = { runAiLeakScan() },
                onMarkSubscriptionKept = { setSubscriptionAuditStatus(it, "KEPT") },
                onFlagSubscriptionCancel = { setSubscriptionAuditStatus(it, "CANCEL") }
            )
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
                    if (appConfig.showProperties && !appConfig.isProperty1Rented) {
                        LoanDetailRow("${appConfig.property1Name} (Est. Market)", maskVal(prop1MarketValue, 0))
                        if (appConfig.hasSecondProperty && !appConfig.isProperty2Rented) {
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
                    if (appConfig.showLoans && !appConfig.isProperty1Rented) {
                        LoanDetailRow("${appConfig.property1Name} Offset", maskVal(prop1Loan.offsetBalance, 0))
                        if (appConfig.hasSecondProperty && !appConfig.isProperty2Rented) {
                            LoanDetailRow("${appConfig.property2Name} Offset", maskVal(prop2Loan.offsetBalance, 0))
                        }
                    }
                    if (appConfig.isProperty1Rented && totalCashSavings > 0) {
                        LoanDetailRow("Combined Cash Savings", maskVal(totalCashSavings, 0))
                    }
                }
            }
        }

        if (appConfig.showLoans && !appConfig.isProperty1Rented) {
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
                        if (appConfig.hasSecondProperty && !appConfig.isProperty2Rented) {
                            LoanDetailRow("${appConfig.property2Name} Loan Balance", maskVal(p2LiveBalance, 0), highlightColor = BrandCoralPink)
                        }
                    }
                }
            }
        }
    }

    if (showSubCommandCenterDialog) {
        SubscriptionAndLeakAuditCommandCenterDialog(
            appConfig = appConfig,
            allRecurringEntries = allRecurringEntries,
            subscriptionLeaks = subscriptionLeaks,
            keptSubscriptions = keptSubscriptions,
            cancelTargetSubscriptions = cancelTargetSubscriptions,
            reclaimedAnnualTotal = reclaimedAnnualTotal,
            isScanningLeaks = isScanningLeaks,
            isWealthHidden = isWealthHidden,
            onRunAiScan = { runAiLeakScan() },
            onSetSubscriptionStatus = { merchant, status -> setSubscriptionAuditStatus(merchant, status) },
            onKeepAll = { merchants -> markAllSubscriptionsKept(merchants) },
            onResetAuditStatuses = { resetAllAuditStatuses() },
            onAddSubscriptionItem = { sourceKey, name, weeklyAmt, iconName, cycle, dueMillis ->
                handleAddSubscriptionItem(sourceKey, name, weeklyAmt, iconName, cycle, dueMillis)
            },
            onEditRecurringEntry = { entry, name, weeklyAmt, iconName, cycle, dueMillis ->
                handleEditRecurringEntry(entry, name, weeklyAmt, iconName, cycle, dueMillis)
            },
            onDeleteRecurringEntry = { entry -> handleDeleteRecurringEntry(entry) },
            onReclaimCancelledEntry = { entry -> handleReclaimCancelledEntry(entry) },
            onDismiss = { showSubCommandCenterDialog = false }
        )
    }
}

data class TimelinePoint(
    val label: String,
    val value: Double,
    val dateText: String,
    val xFraction: Float = 0f,
    val costBasisAtDate: Double = 0.0,
    val addedTickers: List<String> = emptyList()
)

fun resolveParcelPurchaseDate(parcel: ShareParcel, fallbackDate: LocalDate = LocalDate.now()): LocalDate {
    if (parcel.purchaseDate.isNotBlank()) {
        parseFlexibleDate(parcel.purchaseDate)?.let { return it }
    }
    val match = Regex("""\d{10,13}""").find(parcel.id)
    if (match != null) {
        try {
            val raw = match.value.toLong()
            val millis = if (match.value.length <= 10) raw * 1000L else raw
            return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
        } catch (_: Exception) {}
    }
    return fallbackDate
}

private data class ParcelTimelineEntry(
    val code: String,
    val buyDate: LocalDate,
    val buyCost: Double,
    val nowValue: Double
)

fun buildPurchaseAwareTimelinePoints(
    shares: List<ShareParcel>,
    timeframe: String
): List<TimelinePoint> {
    if (shares.isEmpty()) {
        return listOf(
            TimelinePoint(label = "Start", value = 0.0, dateText = "No holdings", xFraction = 0f),
            TimelinePoint(label = "End", value = 0.0, dateText = "No holdings", xFraction = 1f)
        )
    }

    val now = LocalDate.now()
    val dtf = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())
    val entries = shares.map { parcel ->
        val buyDate = resolveParcelPurchaseDate(parcel, now).coerceAtMost(now)
        val unitBuyPrice = if (parcel.purchasePrice > 0.0) parcel.purchasePrice else parcel.currentPrice
        ParcelTimelineEntry(
            code = parcel.code.trim().uppercase(),
            buyDate = buyDate,
            buyCost = (parcel.units * unitBuyPrice).coerceAtLeast(0.0),
            nowValue = (parcel.units * parcel.currentPrice).coerceAtLeast(0.0)
        )
    }

    val earliestBuy = entries.minOf { it.buyDate }
    val windowStart = when (timeframe) {
        "1M" -> now.minusMonths(1)
        "3M" -> now.minusMonths(3)
        "6M" -> now.minusMonths(6)
        "1Y" -> now.minusYears(1)
        else -> if (earliestBuy.isBefore(now)) earliestBuy.minusDays(1) else now.minusDays(1)
    }.let { candidate ->
        if (candidate.isBefore(now)) candidate else now.minusDays(1)
    }

    val totalDays = ChronoUnit.DAYS.between(windowStart, now).coerceAtLeast(1L)
    val gridSteps = 24
    val candidateDates = sortedSetOf<LocalDate>()
    candidateDates.add(windowStart)
    candidateDates.add(now)
    for (i in 1 until gridSteps) {
        val offset = ((totalDays.toDouble() * i.toDouble()) / gridSteps.toDouble()).roundToInt().toLong().coerceIn(0L, totalDays)
        candidateDates.add(windowStart.plusDays(offset))
    }

    val buysByDate = entries.groupBy { it.buyDate }
    buysByDate.keys.forEach { buyDate ->
        if (!buyDate.isBefore(windowStart) && !buyDate.isAfter(now)) {
            candidateDates.add(buyDate)
        }
    }

    fun evaluateAt(date: LocalDate, includeSameDayBuys: Boolean): Pair<Double, Double> {
        var valSum = 0.0
        var costSum = 0.0
        for (e in entries) {
            val isOwned = if (includeSameDayBuys) {
                !e.buyDate.isAfter(date)
            } else {
                e.buyDate.isBefore(date)
            }
            if (!isOwned) continue

            costSum += e.buyCost
            val spanDays = ChronoUnit.DAYS.between(e.buyDate, now)
            val parcelVal = when {
                spanDays <= 0L || date == now -> e.nowValue
                date == e.buyDate -> e.buyCost
                else -> {
                    val elapsed = ChronoUnit.DAYS.between(e.buyDate, date).coerceIn(0L, spanDays)
                    val progress = elapsed.toDouble() / spanDays.toDouble()
                    (e.buyCost + (e.nowValue - e.buyCost) * progress).coerceAtLeast(0.0)
                }
            }
            valSum += parcelVal
        }
        return valSum to costSum
    }

    val points = mutableListOf<TimelinePoint>()
    candidateDates.forEach { d ->
        val xFrac = (ChronoUnit.DAYS.between(windowStart, d).toFloat() / totalDays.toFloat()).coerceIn(0f, 1f)
        val dateLabel = d.format(dtf)
        val dayBuys = buysByDate[d].orEmpty()

        if (dayBuys.isNotEmpty() && d.isAfter(windowStart)) {
            val (preVal, preCost) = evaluateAt(d, includeSameDayBuys = false)
            points.add(
                TimelinePoint(
                    label = "",
                    value = preVal,
                    dateText = dateLabel,
                    xFraction = xFrac,
                    costBasisAtDate = preCost
                )
            )
            val (postVal, postCost) = evaluateAt(d, includeSameDayBuys = true)
            val tickerSummary = dayBuys
                .groupBy { it.code }
                .map { (ticker, list) -> if (list.size > 1) "+$ticker ×${list.size}" else "+$ticker" }
            points.add(
                TimelinePoint(
                    label = if (d == now) "Today" else "",
                    value = postVal,
                    dateText = dateLabel,
                    xFraction = xFrac,
                    costBasisAtDate = postCost,
                    addedTickers = tickerSummary
                )
            )
        } else {
            val (v, c) = evaluateAt(d, includeSameDayBuys = true)
            val startBuys = if (d == windowStart && dayBuys.isNotEmpty()) {
                dayBuys.groupBy { it.code }.map { (ticker, list) -> if (list.size > 1) "+$ticker ×${list.size}" else "+$ticker" }
            } else {
                emptyList()
            }
            points.add(
                TimelinePoint(
                    label = when (d) {
                        windowStart -> "Start"
                        now -> "Today"
                        else -> ""
                    },
                    value = v,
                    dateText = dateLabel,
                    xFraction = xFrac,
                    costBasisAtDate = c,
                    addedTickers = startBuys
                )
            )
        }
    }

    return points
}

@Composable
fun SharesHoldingTimelineCard(
    ownerFilter: String,
    totalValue: Double,
    costBasis: Double,
    shares: List<ShareParcel>,
    isWealthHidden: Boolean
) {
    val locale = currentLocale()
    var selectedTimeframe by remember { mutableStateOf("ALL") }
    val timeframes = listOf("1M", "3M", "6M", "1Y", "ALL")
    var scrubbedIndex by remember { mutableStateOf<Int?>(null) }

    val hasShares = shares.isNotEmpty()

    val points = remember(selectedTimeframe, totalValue, costBasis, shares) {
        buildPurchaseAwareTimelinePoints(shares = shares, timeframe = selectedTimeframe)
    }

    val activeIndex = (scrubbedIndex ?: (points.size - 1)).coerceIn(0, points.lastIndex)
    val activePoint = points[activeIndex]
    val displayValue = if (isWealthHidden) "$ ••••••" else formatCurrency(activePoint.value, 2)

    val periodStartVal = points.first().value
    val activeCostBasis = if (hasShares) activePoint.costBasisAtDate else 0.0
    val periodValueGrowth = activePoint.value - periodStartVal
    val periodGrowthPct = if (periodStartVal > 0.0) (periodValueGrowth / periodStartVal) * 100.0 else 0.0
    val unrealisedGain = activePoint.value - activeCostBasis
    val unrealisedPct = if (activeCostBasis > 0.0) (unrealisedGain / activeCostBasis) * 100.0 else 0.0

    val usePeriodGrowth = periodStartVal > 0.0
    val headlineDelta = if (usePeriodGrowth) periodValueGrowth else unrealisedGain
    val headlinePct = if (usePeriodGrowth) periodGrowthPct else unrealisedPct
    val isPositive = headlineDelta >= 0.0
    val trendColor = if (isPositive) BrandMintGreen else BrandCoralPink

    val buyMarkersCount = remember(points) { points.count { it.addedTickers.isNotEmpty() } }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: Title & Timeframe Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$ownerFilter Shares Value Trajectory".uppercase(),
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateGray,
                    letterSpacing = 1.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    timeframes.forEach { tf ->
                        val isTfSelected = selectedTimeframe == tf
                        Surface(
                            onClick = {
                                selectedTimeframe = tf
                                scrubbedIndex = null
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isTfSelected) trendColor.copy(alpha = 0.22f) else Color.Transparent,
                            border = if (isTfSelected) BorderStroke(1.dp, trendColor.copy(alpha = 0.5f)) else null
                        ) {
                            Text(
                                text = tf,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                fontSize = 11.sp,
                                fontWeight = if (isTfSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isTfSelected) trendColor else SlateGray,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }

            // Value & Gain/Loss Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = displayValue,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            if (isPositive) Icons.Default.NorthEast else Icons.Default.SouthEast,
                            contentDescription = null,
                            tint = trendColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (isWealthHidden) {
                                "$ •••••• (••••%)"
                            } else if (activeCostBasis <= 0.0 && activePoint.value <= 0.0) {
                                "Not yet invested"
                            } else {
                                "${if (headlineDelta >= 0) "+" else ""}${formatCurrency(headlineDelta, 2)} (${String.format(locale, "%.2f", headlinePct)}%)"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeCostBasis <= 0.0 && activePoint.value <= 0.0) SlateGray else trendColor
                        )
                        Text(
                            text = if (scrubbedIndex != null) "• ${activePoint.dateText}" else "• $selectedTimeframe",
                            fontSize = 11.sp,
                            color = SlateGray
                        )
                    }
                }

                if (hasShares && !isWealthHidden) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            if (scrubbedIndex != null) "Invested Then" else "Cost Basis",
                            fontSize = 10.sp,
                            color = SlateGray
                        )
                        Text(
                            formatCurrency(activeCostBasis, 0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateGray
                        )
                    }
                }
            }

            if (activePoint.addedTickers.isNotEmpty() && !isWealthHidden) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BrandSkyBlue.copy(alpha = 0.14f),
                    border = BorderStroke(0.8.dp, BrandSkyBlue.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = "Buy on ${activePoint.dateText}: ${activePoint.addedTickers.joinToString(", ")}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandSkyBlue
                    )
                }
            }

            // Interactive Canvas Timeline Curve
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .lockPagerOnTouch()
                        .pointerInput(points) {
                            fun findClosestIndex(xPx: Float): Int {
                                val targetFrac = (xPx / size.width.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)
                                var bestIdx = points.lastIndex
                                var bestDist = Float.MAX_VALUE
                                points.forEachIndexed { idx, pt ->
                                    val dist = abs(pt.xFraction - targetFrac)
                                    if (dist <= bestDist) {
                                        bestDist = dist
                                        bestIdx = idx
                                    }
                                }
                                return bestIdx
                            }

                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                val x = down.position.x.coerceIn(0f, size.width.toFloat())
                                scrubbedIndex = findClosestIndex(x)

                                while (true) {
                                    val event = awaitPointerEvent(PointerEventPass.Main)
                                    val change = event.changes.firstOrNull() ?: break
                                    if (!change.pressed) break
                                    val currX = change.position.x.coerceIn(0f, size.width.toFloat())
                                    scrubbedIndex = findClosestIndex(currX)
                                    change.consume()
                                }
                                scrubbedIndex = null
                            }
                        }
                ) {
                    if (points.isEmpty()) return@Canvas
                    val w = size.width
                    val h = size.height
                    val padTop = 14.dp.toPx()
                    val padBottom = 14.dp.toPx()
                    val usableH = (h - padTop - padBottom).coerceAtLeast(10f)

                    val allValues = points.flatMap { listOf(it.value, it.costBasisAtDate) }
                    val rawMin = allValues.minOrNull() ?: 0.0
                    val rawMax = allValues.maxOrNull() ?: 1.0
                    val range = max(1.0, rawMax - rawMin)
                    val minVal = (rawMin - range * 0.05).coerceAtLeast(0.0)
                    val maxVal = rawMax + range * 0.06
                    val spread = max(1.0, maxVal - minVal)

                    fun pointFor(idx: Int): Offset {
                        val pt = points[idx]
                        val x = pt.xFraction * w
                        val normY = ((pt.value - minVal) / spread).toFloat().coerceIn(0f, 1f)
                        val y = padTop + usableH * (1f - normY)
                        return Offset(x, y)
                    }

                    fun costPointFor(idx: Int): Offset {
                        val pt = points[idx]
                        val x = pt.xFraction * w
                        val normY = ((pt.costBasisAtDate - minVal) / spread).toFloat().coerceIn(0f, 1f)
                        val y = padTop + usableH * (1f - normY)
                        return Offset(x, y)
                    }

                    // Draw cumulative Cost Basis step-line (shows cash added over time)
                    if (hasShares) {
                        val costPath = Path()
                        val costStart = costPointFor(0)
                        costPath.moveTo(costStart.x, costStart.y)
                        for (i in 1 until points.size) {
                            val cp = costPointFor(i)
                            costPath.lineTo(cp.x, cp.y)
                        }
                        drawPath(
                            path = costPath,
                            color = SlateGray.copy(alpha = 0.42f),
                            style = Stroke(
                                width = 1.3.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                            )
                        )
                    }

                    // Build step-aware growth Path
                    val path = Path()
                    val startPt = pointFor(0)
                    path.moveTo(startPt.x, startPt.y)
                    for (i in 1 until points.size) {
                        val p1 = pointFor(i)
                        path.lineTo(p1.x, p1.y)
                    }

                    // Shaded gradient area under curve
                    val fillPath = Path().apply {
                        addPath(path)
                        lineTo(w, h)
                        lineTo(0f, h)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                trendColor.copy(alpha = 0.28f),
                                trendColor.copy(alpha = 0.03f),
                                Color.Transparent
                            )
                        )
                    )

                    // Draw glowing stroke
                    drawPath(
                        path = path,
                        color = trendColor,
                        style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )

                    // Draw purchase event markers on the curve (new shares & repeat buys)
                    points.forEachIndexed { idx, pt ->
                        if (pt.addedTickers.isNotEmpty() && idx != activeIndex) {
                            val buyPt = pointFor(idx)
                            drawCircle(color = BrandSkyBlue.copy(alpha = 0.25f), radius = 5.5.dp.toPx(), center = buyPt)
                            drawCircle(color = BrandSkyBlue, radius = 3.dp.toPx(), center = buyPt)
                        }
                    }

                    // Active marker / scrub dot
                    val activePt = pointFor(activeIndex)

                    if (scrubbedIndex != null) {
                        drawLine(
                            color = SlateGray.copy(alpha = 0.40f),
                            start = Offset(activePt.x, 0f),
                            end = Offset(activePt.x, h),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    drawCircle(color = trendColor.copy(alpha = 0.25f), radius = 8.dp.toPx(), center = activePt)
                    drawCircle(color = trendColor, radius = 4.5.dp.toPx(), center = activePt)
                    drawCircle(color = Color.White, radius = 2.dp.toPx(), center = activePt)
                }
            }

            // Bottom Timeline Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (hasShares) points.first().dateText else "No holdings",
                    fontSize = 10.sp,
                    color = SlateGray
                )
                Text(
                    text = if (buyMarkersCount > 0) "$buyMarkersCount buy event(s) in range • Drag to inspect" else "Dashed line = cost basis • Drag to inspect",
                    fontSize = 9.sp,
                    color = SlateGray.copy(alpha = 0.75f)
                )
                Text(
                    text = "Today",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateGray
                )
            }
        }
    }
}

// ============================================================================
// SINGLE SHARE TIMELINE GRAPH CARD
// ============================================================================
@Composable
fun SingleShareTimelineCard(
    holding: GroupedShareHolding,
    isWealthHidden: Boolean
) {
    val locale = currentLocale()
    var selectedTimeframe by remember { mutableStateOf("6M") }
    val timeframes = listOf("1M", "3M", "6M", "1Y", "ALL")
    var scrubbedIndex by remember { mutableStateOf<Int?>(null) }

    val hasHolding = holding.parcels.isNotEmpty()
    val points = remember(selectedTimeframe, holding) {
        buildPurchaseAwareTimelinePoints(shares = holding.parcels, timeframe = selectedTimeframe)
    }

    val activeIndex = (scrubbedIndex ?: (points.size - 1)).coerceIn(0, points.lastIndex)
    val activePoint = points[activeIndex]
    val displayValue = if (isWealthHidden) "$ ••••••" else formatCurrency(activePoint.value, 2)
    val periodStartVal = points.first().value
    val activeCostBasis = if (hasHolding) activePoint.costBasisAtDate else 0.0
    val periodValueGrowth = activePoint.value - periodStartVal
    val periodGrowthPct = if (periodStartVal > 0.0) (periodValueGrowth / periodStartVal) * 100.0 else 0.0
    val unrealisedGain = activePoint.value - activeCostBasis
    val unrealisedPct = if (activeCostBasis > 0.0) (unrealisedGain / activeCostBasis) * 100.0 else 0.0

    val usePeriodGrowth = periodStartVal > 0.0
    val headlineDelta = if (usePeriodGrowth) periodValueGrowth else unrealisedGain
    val headlinePct = if (usePeriodGrowth) periodGrowthPct else unrealisedPct
    val isPositive = headlineDelta >= 0.0
    val trendColor = if (isPositive) BrandMintGreen else BrandCoralPink

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${holding.code.uppercase()} VALUE TRAJECTORY",
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateGray,
                    letterSpacing = 1.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    timeframes.forEach { tf ->
                        val isTfSelected = selectedTimeframe == tf
                        Surface(
                            onClick = {
                                selectedTimeframe = tf
                                scrubbedIndex = null
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isTfSelected) trendColor.copy(alpha = 0.22f) else Color.Transparent,
                            border = if (isTfSelected) BorderStroke(1.dp, trendColor.copy(alpha = 0.5f)) else null
                        ) {
                            Text(
                                text = tf,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                fontSize = 11.sp,
                                fontWeight = if (isTfSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isTfSelected) trendColor else SlateGray,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = displayValue,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            if (isPositive) Icons.Default.NorthEast else Icons.Default.SouthEast,
                            contentDescription = null,
                            tint = trendColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = if (isWealthHidden) {
                                "$ •••••• (••••%)"
                            } else if (activeCostBasis <= 0.0 && activePoint.value <= 0.0) {
                                "Not yet owned"
                            } else {
                                "${if (headlineDelta >= 0) "+" else ""}${formatCurrency(headlineDelta, 2)} (${String.format(locale, "%.2f", headlinePct)}%)"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeCostBasis <= 0.0 && activePoint.value <= 0.0) SlateGray else trendColor
                        )
                        Text(
                            text = if (scrubbedIndex != null) "• ${activePoint.dateText}" else "• $selectedTimeframe",
                            fontSize = 11.sp,
                            color = SlateGray
                        )
                    }
                }

                if (hasHolding && !isWealthHidden) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            if (scrubbedIndex != null) "Invested Then" else "Cost Basis",
                            fontSize = 10.sp,
                            color = SlateGray
                        )
                        Text(formatCurrency(activeCostBasis, 0), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SlateGray)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .lockPagerOnTouch()
                        .pointerInput(points) {
                            fun findClosestIndex(xPx: Float): Int {
                                val targetFrac = (xPx / size.width.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)
                                var bestIdx = points.lastIndex
                                var bestDist = Float.MAX_VALUE
                                points.forEachIndexed { idx, pt ->
                                    val dist = abs(pt.xFraction - targetFrac)
                                    if (dist <= bestDist) {
                                        bestDist = dist
                                        bestIdx = idx
                                    }
                                }
                                return bestIdx
                            }

                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                val x = down.position.x.coerceIn(0f, size.width.toFloat())
                                scrubbedIndex = findClosestIndex(x)

                                while (true) {
                                    val event = awaitPointerEvent(PointerEventPass.Main)
                                    val change = event.changes.firstOrNull() ?: break
                                    if (!change.pressed) break
                                    val currX = change.position.x.coerceIn(0f, size.width.toFloat())
                                    scrubbedIndex = findClosestIndex(currX)
                                    change.consume()
                                }
                                scrubbedIndex = null
                            }
                        }
                ) {
                    if (points.isEmpty()) return@Canvas
                    val w = size.width
                    val h = size.height
                    val padTop = 14.dp.toPx()
                    val padBottom = 14.dp.toPx()
                    val usableH = (h - padTop - padBottom).coerceAtLeast(10f)

                    val allValues = points.flatMap { listOf(it.value, it.costBasisAtDate) }
                    val rawMin = allValues.minOrNull() ?: 0.0
                    val rawMax = allValues.maxOrNull() ?: 1.0
                    val range = max(1.0, rawMax - rawMin)
                    val minVal = (rawMin - range * 0.05).coerceAtLeast(0.0)
                    val maxVal = rawMax + range * 0.06
                    val spread = max(1.0, maxVal - minVal)

                    fun pointFor(idx: Int): Offset {
                        val pt = points[idx]
                        val x = pt.xFraction * w
                        val normY = ((pt.value - minVal) / spread).toFloat().coerceIn(0f, 1f)
                        val y = padTop + usableH * (1f - normY)
                        return Offset(x, y)
                    }

                    fun costPointFor(idx: Int): Offset {
                        val pt = points[idx]
                        val x = pt.xFraction * w
                        val normY = ((pt.costBasisAtDate - minVal) / spread).toFloat().coerceIn(0f, 1f)
                        val y = padTop + usableH * (1f - normY)
                        return Offset(x, y)
                    }

                    if (hasHolding) {
                        val costPath = Path()
                        val costStart = costPointFor(0)
                        costPath.moveTo(costStart.x, costStart.y)
                        for (i in 1 until points.size) {
                            val cp = costPointFor(i)
                            costPath.lineTo(cp.x, cp.y)
                        }
                        drawPath(
                            path = costPath,
                            color = SlateGray.copy(alpha = 0.42f),
                            style = Stroke(
                                width = 1.3.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                            )
                        )
                    }

                    val path = Path()
                    val startPt = pointFor(0)
                    path.moveTo(startPt.x, startPt.y)
                    for (i in 1 until points.size) {
                        val p1 = pointFor(i)
                        path.lineTo(p1.x, p1.y)
                    }

                    val fillPath = Path().apply {
                        addPath(path)
                        lineTo(w, h)
                        lineTo(0f, h)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                trendColor.copy(alpha = 0.28f),
                                trendColor.copy(alpha = 0.03f),
                                Color.Transparent
                            )
                        )
                    )

                    drawPath(
                        path = path,
                        color = trendColor,
                        style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )

                    points.forEachIndexed { idx, pt ->
                        if (pt.addedTickers.isNotEmpty() && idx != activeIndex) {
                            val buyPt = pointFor(idx)
                            drawCircle(color = BrandSkyBlue.copy(alpha = 0.25f), radius = 5.5.dp.toPx(), center = buyPt)
                            drawCircle(color = BrandSkyBlue, radius = 3.dp.toPx(), center = buyPt)
                        }
                    }

                    val activePt = pointFor(activeIndex)

                    if (scrubbedIndex != null) {
                        drawLine(
                            color = SlateGray.copy(alpha = 0.40f),
                            start = Offset(activePt.x, 0f),
                            end = Offset(activePt.x, h),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    drawCircle(color = trendColor.copy(alpha = 0.25f), radius = 8.dp.toPx(), center = activePt)
                    drawCircle(color = trendColor, radius = 4.5.dp.toPx(), center = activePt)
                    drawCircle(color = Color.White, radius = 2.dp.toPx(), center = activePt)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (hasHolding) points.first().dateText else "No holdings",
                    fontSize = 10.sp,
                    color = SlateGray
                )
                Text(
                    text = "Tap & drag across graph to inspect",
                    fontSize = 9.sp,
                    color = SlateGray.copy(alpha = 0.7f)
                )
                Text(
                    text = "Today",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateGray
                )
            }
        }
    }
}

// ============================================================================
// SHARE DETAIL VIEW: TIMELINE GRAPH + ALL PURCHASE TRANSACTIONS
// ============================================================================
@Composable
fun ShareDetailView(
    holding: GroupedShareHolding,
    appConfig: UserAppConfig,
    person1Color: Color,
    person2Color: Color,
    isWealthHidden: Boolean,
    onBack: () -> Unit,
    onAddPurchase: () -> Unit,
    onEditParcel: (ShareParcel) -> Unit,
    onDeleteParcel: (ShareParcel) -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val locale = currentLocale()
    val orbSpacing = 14.dp
    val headerTopPad = rememberHeaderAwareTopPadding(orbSpacing)
    val sharesAccent = BrandSkyBlue
    val sharesTextAccent = if (isDark) BrandSkyBlue else Color(0xFF0284C7)
    val gainTextColor = if (isDark) BrandMintGreen else Color(0xFF059669)
    val lossTextColor = if (isDark) BrandCoralPink else Color(0xFFDC2626)
    fun maskVal(amt: Double, decimals: Int = 2): String = if (isWealthHidden) "$ ••••••" else formatCurrency(amt, decimals)
    fun maskUsd(amt: Double, decimals: Int = 2, showPlus: Boolean = false): String =
        if (isWealthHidden) "US$ ••••••" else formatUsdCurrency(amt, decimals, showPlus)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = headerTopPad, bottom = 140.dp),
        verticalArrangement = Arrangement.spacedBy(orbSpacing)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Shares",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    AsxShareLogoBadge(ticker = holding.code, size = 38.dp, market = holding.market)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                holding.code.uppercase(),
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (holding.isUsMarket) BrandPurple.copy(alpha = 0.18f) else sharesAccent.copy(alpha = 0.14f),
                                border = BorderStroke(
                                    0.8.dp,
                                    (if (holding.isUsMarket) BrandPurple else sharesAccent).copy(alpha = 0.45f)
                                )
                            ) {
                                Text(
                                    text = if (holding.isUsMarket) "US" else "ASX",
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp),
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (holding.isUsMarket) (if (isDark) BrandPurple else Color(0xFF7C3AED)) else sharesTextAccent
                                )
                            }
                            holding.owners.forEach { ownerName ->
                                val isP1 = ownerName.equals(appConfig.person1Name, ignoreCase = true)
                                val col = if (isP1) person1Color else person2Color
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = col.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, col.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        ownerName,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = col
                                    )
                                }
                            }
                        }
                        Text(
                            "${formatShareUnits(holding.totalUnits)} total units • ${holding.parcels.size} ${if (holding.parcels.size == 1) "purchase" else "purchases"}",
                            fontSize = 12.sp,
                            color = SlateGray
                        )
                    }
                }

                Button(
                    onClick = onAddPurchase,
                    colors = ButtonDefaults.buttonColors(containerColor = sharesAccent, contentColor = BrandForestDark),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add Buy", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                if (holding.isUsMarket) "Total Holding Value (AUD)" else "Total Holding Value",
                                fontSize = 12.sp,
                                color = SlateGray,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                maskVal(holding.totalMarketValue, 2),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (holding.isUsMarket && holding.usdTotalMarketValue > 0 && !isWealthHidden) {
                                Text(
                                    "≈ ${formatUsdCurrency(holding.usdTotalMarketValue, 2)} USD",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SlateGray
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = sharesAccent.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, sharesAccent.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(BrandMintGreen))
                                    Text(
                                        if (isWealthHidden) "$ •••••• live" else "${formatCurrency(holding.currentPrice, 2)} AUD live",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = sharesTextAccent
                                    )
                                }
                            }
                            if (holding.isUsMarket && holding.usdCurrentPrice > 0 && !isWealthHidden) {
                                Text(
                                    "${formatUsdCurrency(holding.usdCurrentPrice, 2)} USD / share",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SlateGray
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val isGain = holding.totalGainLoss >= 0
                        val gainColor = if (isGain) gainTextColor else lossTextColor
                        Icon(
                            if (isGain) Icons.Default.NorthEast else Icons.Default.SouthEast,
                            contentDescription = null,
                            tint = gainColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            if (isWealthHidden) "$ •••••• (••••%)"
                            else "${if (isGain) "+" else ""}${formatCurrency(holding.totalGainLoss, 2)} (${String.format(locale, "%.2f", holding.gainLossPct)}%)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isWealthHidden) MaterialTheme.colorScheme.onSurface else gainColor
                        )
                        Text(
                            if (holding.isUsMarket) "total AUD return" else "total return",
                            fontSize = 12.sp,
                            color = SlateGray
                        )
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Units", fontSize = 11.sp, color = SlateGray)
                            Text(formatShareUnits(holding.totalUnits), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Avg Purchase Price", fontSize = 11.sp, color = SlateGray)
                            Text(maskVal(holding.averagePurchasePrice, 2), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = sharesTextAccent)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Cost Basis", fontSize = 11.sp, color = SlateGray)
                            Text(maskVal(holding.totalCostBasis, 2), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (holding.isUsMarket) {
            item {
                val fx = holding.fxRate
                val hasUsdData = holding.usdCurrentPrice > 0 || holding.usdTotalCostBasis > 0
                val fxAccent = if (isDark) BrandPurple else Color(0xFF7C3AED)
                val fxImpactPct = holding.gainLossPct - holding.usdGainLossPct
                val fxIsTailwind = fxImpactPct >= 0

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, BrandPurple.copy(alpha = 0.28f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "US MARKET & FX INSIGHT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = fxAccent,
                                letterSpacing = 0.9.sp
                            )
                            if (fx > 0) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = BrandPurple.copy(alpha = 0.14f),
                                    border = BorderStroke(0.8.dp, BrandPurple.copy(alpha = 0.35f))
                                ) {
                                    Text(
                                        text = "1 USD = A$${String.format(locale, "%.4f", fx)}",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = fxAccent
                                    )
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("US SHARE PRICE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SlateGray, letterSpacing = 0.5.sp)
                                    Text(
                                        if (hasUsdData) maskUsd(holding.usdCurrentPrice, 2) else "Tap Sync",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        if (fx > 0) "1 AUD = US$${String.format(locale, "%.4f", 1.0 / fx)}" else "USD native quote",
                                        fontSize = 10.sp,
                                        color = SlateGray
                                    )
                                }
                                Icon(
                                    Icons.AutoMirrored.Filled.CompareArrows,
                                    contentDescription = null,
                                    tint = fxAccent,
                                    modifier = Modifier.padding(horizontal = 6.dp).size(18.dp)
                                )
                                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                    Text("AUD EQUIVALENT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = sharesTextAccent, letterSpacing = 0.5.sp)
                                    Text(
                                        maskVal(holding.currentPrice, 2),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = sharesTextAccent
                                    )
                                    Text(
                                        "Used in portfolio totals",
                                        fontSize = 10.sp,
                                        color = SlateGray
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("USD Market Value", fontSize = 10.sp, color = SlateGray)
                                Text(maskUsd(holding.usdTotalMarketValue, 2), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("USD Cost Basis", fontSize = 10.sp, color = SlateGray)
                                Text(maskUsd(holding.usdTotalCostBasis, 2), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                Text("USD Avg Buy", fontSize = 10.sp, color = SlateGray)
                                Text(maskUsd(holding.usdAveragePurchasePrice, 2), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (holding.usdTotalCostBasis > 0 && !isWealthHidden) {
                            HorizontalDivider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Stock Return (USD)", fontSize = 10.sp, color = SlateGray)
                                    val usdGainCol = if (holding.usdTotalGainLoss >= 0) gainTextColor else lossTextColor
                                    Text(
                                        "${formatUsdCurrency(holding.usdTotalGainLoss, 2, showPlus = true)} (${String.format(locale, "%.2f", holding.usdGainLossPct)}%)",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = usdGainCol
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("AUD Currency Effect", fontSize = 10.sp, color = SlateGray)
                                    val fxCol = if (fxIsTailwind) gainTextColor else lossTextColor
                                    Text(
                                        "${if (fxIsTailwind) "+" else ""}${String.format(locale, "%.2f", fxImpactPct)}% ${if (fxIsTailwind) "FX tailwind" else "FX headwind"}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = fxCol
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            SingleShareTimelineCard(
                holding = holding,
                isWealthHidden = isWealthHidden
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "PURCHASE TRANSACTIONS (${holding.parcels.size})".uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateGray,
                    letterSpacing = 1.sp
                )
                Text(
                    "Avg: ${maskVal(holding.averagePurchasePrice, 2)}",
                    fontSize = 11.sp,
                    color = SlateGray,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        items(holding.parcels, key = { it.id }) { parcel ->
            val parcelCost = parcel.units * parcel.purchasePrice
            val parcelVal = parcel.units * parcel.currentPrice
            val parcelGain = parcelVal - parcelCost
            val parcelGainPct = if (parcelCost > 0) (parcelGain / parcelCost) * 100 else 0.0
            val isP1 = parcel.owner.equals(appConfig.person1Name, ignoreCase = true)
            val ownerBadgeColor = if (isP1) person1Color else person2Color
            val isGain = parcelGain >= 0
            val gainColor = if (isGain) gainTextColor else lossTextColor
            val isUsParcel = parcel.market.equals("US", ignoreCase = true)
            val parcelUsdBuy = when {
                parcel.usdPurchasePrice > 0 -> parcel.usdPurchasePrice
                parcel.fxRateAtSync > 0 && parcel.purchasePrice > 0 -> parcel.purchasePrice / parcel.fxRateAtSync
                holding.fxRate > 0 && parcel.purchasePrice > 0 -> parcel.purchasePrice / holding.fxRate
                else -> 0.0
            }
            val parcelUsdCurr = when {
                parcel.usdCurrentPrice > 0 -> parcel.usdCurrentPrice
                holding.usdCurrentPrice > 0 -> holding.usdCurrentPrice
                else -> 0.0
            }

            SwipeToRevealBox(
                onEdit = { onEditParcel(parcel) },
                onDelete = { onDeleteParcel(parcel) }
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(11.dp), tint = SlateGray)
                                        Text(
                                            formatParcelDate(parcel),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = ownerBadgeColor.copy(alpha = 0.18f),
                                    border = BorderStroke(1.dp, ownerBadgeColor.copy(alpha = 0.45f))
                                ) {
                                    Text(
                                        parcel.owner,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ownerBadgeColor
                                    )
                                }
                            }
                        }

                        // Dedicated Pricing & Valuation Capsule for Transaction
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("BOUGHT AT", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = SlateGray, letterSpacing = 0.5.sp)
                                    Text(
                                        maskVal(parcel.purchasePrice, 2),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isUsParcel && parcelUsdBuy > 0 && !isWealthHidden) {
                                        Text(
                                            formatUsdCurrency(parcelUsdBuy, 2),
                                            fontSize = 9.5.sp,
                                            color = SlateGray
                                        )
                                    }
                                }
                                Box(modifier = Modifier.width(1.dp).height(22.dp).background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)))
                                Column {
                                    Text("UNITS", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = SlateGray, letterSpacing = 0.5.sp)
                                    Text(
                                        formatShareUnits(parcel.units),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Box(modifier = Modifier.width(1.dp).height(22.dp).background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)))
                                Column {
                                    Text("COST", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = SlateGray, letterSpacing = 0.5.sp)
                                    Text(
                                        maskVal(parcelCost, 2),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isUsParcel && parcelUsdBuy > 0 && !isWealthHidden) {
                                        Text(
                                            formatUsdCurrency(parcel.units * parcelUsdBuy, 2),
                                            fontSize = 9.5.sp,
                                            color = SlateGray
                                        )
                                    }
                                }
                                Box(modifier = Modifier.width(1.dp).height(22.dp).background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)))
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("CURRENT VALUE", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = sharesTextAccent, letterSpacing = 0.5.sp)
                                    Text(
                                        maskVal(parcelVal, 2),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = sharesTextAccent
                                    )
                                    if (isUsParcel && parcelUsdCurr > 0 && !isWealthHidden) {
                                        Text(
                                            formatUsdCurrency(parcel.units * parcelUsdCurr, 2),
                                            fontSize = 9.5.sp,
                                            color = SlateGray
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (parcel.purchasePrice > 0 && parcel.currentPrice > 0) {
                                    val diff = parcel.currentPrice - parcel.purchasePrice
                                    val diffPct = (diff / parcel.purchasePrice) * 100
                                    "Per share: ${if (diff >= 0) "+" else ""}${formatCurrency(diff, 2)} (${String.format(locale, "%.1f", diffPct)}%)"
                                } else "",
                                fontSize = 10.5.sp,
                                color = SlateGray,
                                fontWeight = FontWeight.Medium
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = gainColor.copy(alpha = 0.14f),
                                border = BorderStroke(0.8.dp, gainColor.copy(alpha = 0.35f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        if (isGain) Icons.Default.NorthEast else Icons.Default.SouthEast,
                                        contentDescription = null,
                                        tint = gainColor,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        if (isWealthHidden) "$ ••••••"
                                        else "${if (isGain) "+" else ""}${formatCurrency(parcelGain, 2)} (${String.format(locale, "%.1f", parcelGainPct)}%)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isWealthHidden) MaterialTheme.colorScheme.onSurface else gainColor
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
    onAddShare: (String, String, Double, Double, Double, String, String, Double, Double, Double) -> Unit,
    onEditShare: (String, String, String, Double, Double, Double, String, String, Double, Double, Double) -> Unit,
    onDeleteShare: (String) -> Unit,
    onUpdateSharesList: (List<ShareParcel>) -> Unit,
    onAddSuperAccount: (String, String, Double) -> Unit,
    onEditSuperAccount: (String, String, String, Double) -> Unit,
    onDeleteSuperAccount: (String) -> Unit
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkTheme.current
    val locale = currentLocale()
    var selectedCategory by remember { mutableIntStateOf(0) }
    var ownerFilter by remember { mutableStateOf("All") }
    var selectedHoldingKey by remember { mutableStateOf<String?>(null) }
    var showAddShareDialog by remember { mutableStateOf(false) }
    var addPurchaseForHolding by remember { mutableStateOf<GroupedShareHolding?>(null) }
    var holdingForParcelPicker by remember { mutableStateOf<GroupedShareHolding?>(null) }
    var holdingForDeleteConfirm by remember { mutableStateOf<GroupedShareHolding?>(null) }
    var shareToEdit by remember { mutableStateOf<ShareParcel?>(null) }
    var shareToDelete by remember { mutableStateOf<ShareParcel?>(null) }
    var showAddSuperDialog by remember { mutableStateOf(false) }
    var superToEdit by remember { mutableStateOf<SuperAccount?>(null) }
    var superToDelete by remember { mutableStateOf<SuperAccount?>(null) }
    var isSyncing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val prefs = context.getSharedPreferences("expnz_public_wealth_v2", Context.MODE_PRIVATE)
    var lastSyncTime by remember { mutableLongStateOf(prefs.getLong("last_asx_sync", 0L)) }
    val latestSharesList by rememberUpdatedState(sharesList)

    suspend fun syncAllSharePrices(showToast: Boolean) {
        if (isSyncing) return
        isSyncing = true
        val snapshot = latestSharesList
        val hasUsShares = snapshot.any { it.market.equals("US", ignoreCase = true) }
        val liveFxRate = if (hasUsShares) fetchUsdToAudRate(context) else null
        val quoteCache = mutableMapOf<String, LiveShareQuote?>()
        val failedTickers = mutableListOf<String>()
        var updatedCount = 0
        val updated = snapshot.map { parcel ->
            val mkt = parcel.market.trim().uppercase().takeIf { it == "US" } ?: "ASX"
            val key = "$mkt:${parcel.code.trim().uppercase()}"
            val quote = quoteCache.getOrPut(key) {
                fetchShareLiveQuote(
                    ticker = parcel.code,
                    market = mkt,
                    usdToAudRateOverride = liveFxRate,
                    context = context
                )
            }
            if (quote != null && quote.audPrice > 0.0) {
                updatedCount++
                parcel.copy(
                    currentPrice = quote.audPrice,
                    usdCurrentPrice = quote.usdPrice ?: parcel.usdCurrentPrice,
                    fxRateAtSync = quote.usdToAudRate ?: parcel.fxRateAtSync
                )
            } else {
                if (!failedTickers.contains(parcel.code.uppercase())) {
                    failedTickers.add(parcel.code.uppercase())
                }
                parcel
            }
        }
        onUpdateSharesList(updated)
        val newSyncTime = System.currentTimeMillis()
        prefs.edit { putLong("last_asx_sync", newSyncTime) }
        lastSyncTime = newSyncTime
        isSyncing = false
        if (showToast) {
            val msg = when {
                failedTickers.isEmpty() && updatedCount > 0 ->
                    if (hasUsShares) "Updated ASX & US live prices (AUD)" else "Updated ASX live prices"
                updatedCount > 0 ->
                    "Updated prices (couldn't fetch: ${failedTickers.joinToString(", ")})"
                else ->
                    "Could not fetch live prices. Check connection."
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(selectedCategory) {
        if (selectedCategory == 0 && latestSharesList.isNotEmpty()) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastSyncTime > 15 * 60 * 1000) { // 15 mins
                syncAllSharePrices(showToast = false)
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

    // Always automatically reorganised from most valuable to least valuable
    val groupedShares = remember(filteredShares) {
        filteredShares
            .groupBy {
                val mkt = it.market.trim().uppercase().takeIf { m -> m == "US" } ?: "ASX"
                "$mkt:${it.code.trim().uppercase()}"
            }
            .map { (_, parcels) ->
                GroupedShareHolding(
                    code = parcels.first().code.trim().uppercase(),
                    owners = parcels.map { it.owner }.distinct(),
                    parcels = parcels.sortedByDescending { it.purchaseDate }
                )
            }
            .sortedByDescending { it.totalMarketValue }
    }

    val totalSharesVal = filteredShares.sumOf { it.units * it.currentPrice }
    val totalCostBasis = filteredShares.sumOf { it.units * it.purchasePrice }
    val totalSharesGainLoss = totalSharesVal - totalCostBasis
    val totalReturnPct = if (totalCostBasis > 0) (totalSharesGainLoss / totalCostBasis) * 100 else 0.0

    val totalSuperVal = filteredSuper.sumOf { it.balance }
    val sharesAccent = BrandSkyBlue
    val sharesTextAccent = if (isDark) BrandSkyBlue else Color(0xFF0284C7)
    val gainTextColor = if (isDark) BrandMintGreen else Color(0xFF059669)
    val lossTextColor = if (isDark) BrandCoralPink else Color(0xFFDC2626)

    val orbSpacing = 14.dp
    val headerTopPad = rememberHeaderAwareTopPadding(orbSpacing)

    val activeHolding = selectedHoldingKey?.let { key ->
        groupedShares.find { it.holdingKey == key }
    }

    if (selectedCategory == 0 && activeHolding != null) {
        BackHandler {
            selectedHoldingKey = null
        }
        ShareDetailView(
            holding = activeHolding,
            appConfig = appConfig,
            person1Color = person1Color,
            person2Color = person2Color,
            isWealthHidden = isWealthHidden,
            onBack = { selectedHoldingKey = null },
            onAddPurchase = { addPurchaseForHolding = activeHolding },
            onEditParcel = { shareToEdit = it },
            onDeleteParcel = { shareToDelete = it }
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = headerTopPad, bottom = 140.dp),
            verticalArrangement = Arrangement.spacedBy(orbSpacing)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CategorySelectCard(
                        title = "Shares Portfolio",
                        subtitle = "ASX & US (AUD)",
                        totalValue = totalSharesVal,
                        accent = sharesAccent,
                        isSelected = selectedCategory == 0,
                        isWealthHidden = isWealthHidden,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedCategory = 0
                            selectedHoldingKey = null
                        }
                    )
                    CategorySelectCard(
                        title = "Superannuation",
                        subtitle = "Retirement Funds",
                        totalValue = totalSuperVal,
                        accent = BrandWarmAmber,
                        isSelected = selectedCategory == 1,
                        isWealthHidden = isWealthHidden,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedCategory = 1
                            selectedHoldingKey = null
                        }
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
                                val sdf = java.text.SimpleDateFormat("dd MMM HH:mm", locale)
                                sdf.format(java.util.Date(lastSyncTime))
                            }
                            Text("Synced: $lastSyncString", fontSize = 10.sp, color = SlateGray, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { showAddShareDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = sharesAccent, contentColor = BrandForestDark),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Add Share", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }

                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        syncAllSharePrices(showToast = true)
                                    }
                                },
                                enabled = !isSyncing,
                                modifier = Modifier.height(32.dp),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                border = BorderStroke(1.dp, sharesAccent.copy(alpha = 0.55f))
                            ) {
                                if (isSyncing) {
                                    CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 1.5.dp, color = sharesAccent)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Syncing", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = sharesTextAccent, maxLines = 1)
                                } else {
                                    Icon(Icons.Default.Refresh, contentDescription = "Sync", modifier = Modifier.size(13.dp), tint = sharesTextAccent)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Sync", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = sharesTextAccent, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }

                item {
                    SharesHoldingTimelineCard(
                        ownerFilter = ownerFilter,
                        totalValue = totalSharesVal,
                        costBasis = totalCostBasis,
                        shares = filteredShares,
                        isWealthHidden = isWealthHidden
                    )
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    "COST BASIS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateGray,
                                    letterSpacing = 0.6.sp
                                )
                                Text(
                                    maskVal(totalCostBasis, 2),
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(18.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    "ALL-TIME RETURN",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateGray,
                                    letterSpacing = 0.6.sp
                                )
                                val isGain = totalSharesGainLoss >= 0
                                val gainColor = if (isGain) BrandMintGreen else BrandCoralPink
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        if (isGain) Icons.Default.NorthEast else Icons.Default.SouthEast,
                                        contentDescription = null,
                                        tint = gainColor,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        if (isWealthHidden) "$ ••••••"
                                        else "${if (isGain) "+" else ""}${formatCurrency(totalSharesGainLoss, 2)} (${String.format(locale, "%.1f", totalReturnPct)}%)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isWealthHidden) MaterialTheme.colorScheme.onSurface else gainColor
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "PORTFOLIO HOLDINGS (${groupedShares.size})".uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateGray,
                            letterSpacing = 1.sp
                        )
                        Text(
                            "Sorted by value",
                            fontSize = 11.sp,
                            color = SlateGray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (groupedShares.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp),
                                    tint = sharesAccent.copy(alpha = 0.7f)
                                )
                                Text(
                                    "No shares tracked yet",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "Tap '+ Add Share' above to log your first ASX or US share purchase.",
                                    fontSize = 12.sp,
                                    color = SlateGray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(groupedShares, key = { it.holdingKey }) { holding ->
                        val isGain = holding.totalGainLoss >= 0
                        val gainColor = if (isGain) gainTextColor else lossTextColor
                        val marketBadgeColor = if (holding.isUsMarket) (if (isDark) BrandPurple else Color(0xFF7C3AED)) else sharesTextAccent

                        SwipeToRevealBox(
                            onEdit = {
                                if (holding.parcels.size == 1) {
                                    shareToEdit = holding.parcels.first()
                                } else {
                                    holdingForParcelPicker = holding
                                }
                            },
                            onDelete = {
                                if (holding.parcels.size == 1) {
                                    shareToDelete = holding.parcels.first()
                                } else {
                                    holdingForDeleteConfirm = holding
                                }
                            }
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedHoldingKey = holding.holdingKey },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // TOP TIER: Share Logo, Ticker, Market & Owner Chips, Total AUD Value & Gain/Loss Pill
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                                        ) {
                                            AsxShareLogoBadge(ticker = holding.code, size = 38.dp, market = holding.market)
                                            Spacer(Modifier.width(10.dp))
                                            Column {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                                ) {
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = sharesAccent.copy(alpha = 0.16f)
                                                    ) {
                                                        Text(
                                                            holding.code.uppercase(),
                                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                                            fontWeight = FontWeight.Black,
                                                            fontSize = 14.sp,
                                                            color = sharesTextAccent
                                                        )
                                                    }
                                                    if (holding.isUsMarket) {
                                                        Surface(
                                                            shape = RoundedCornerShape(5.dp),
                                                            color = BrandPurple.copy(alpha = 0.16f),
                                                            border = BorderStroke(0.7.dp, BrandPurple.copy(alpha = 0.4f))
                                                        ) {
                                                            Text(
                                                                "US",
                                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp),
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.ExtraBold,
                                                                color = marketBadgeColor
                                                            )
                                                        }
                                                    }
                                                    holding.owners.forEach { ownerName ->
                                                        val isP1 = ownerName.equals(appConfig.person1Name, ignoreCase = true)
                                                        val col = if (isP1) person1Color else person2Color
                                                        Surface(
                                                            shape = RoundedCornerShape(10.dp),
                                                            color = col.copy(alpha = 0.18f),
                                                            border = BorderStroke(1.dp, col.copy(alpha = 0.45f))
                                                        ) {
                                                            Text(
                                                                ownerName,
                                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = col
                                                            )
                                                        }
                                                    }
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                                                    ) {
                                                        Text(
                                                            "${holding.parcels.size} ${if (holding.parcels.size == 1) "buy" else "buys"}",
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                            fontSize = 10.sp,
                                                            color = SlateGray,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    }
                                                }
                                                Spacer(Modifier.height(3.dp))
                                                Text(
                                                    "${formatShareUnits(holding.totalUnits)} units held",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = SlateGray
                                                )
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                maskVal(holding.totalMarketValue, 2),
                                                fontWeight = FontWeight.Black,
                                                fontSize = 17.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(Modifier.height(2.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = gainColor.copy(alpha = 0.14f),
                                                border = BorderStroke(0.8.dp, gainColor.copy(alpha = 0.35f))
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                                ) {
                                                    Icon(
                                                        if (isGain) Icons.Default.NorthEast else Icons.Default.SouthEast,
                                                        contentDescription = if (isGain) "Gain" else "Loss",
                                                        tint = gainColor,
                                                        modifier = Modifier.size(11.dp)
                                                    )
                                                    Text(
                                                        if (isWealthHidden) "$ ••••••"
                                                        else "${if (isGain) "+" else ""}${formatCurrency(holding.totalGainLoss, 2)} (${String.format(locale, "%.1f", holding.gainLossPct)}%)",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isWealthHidden) MaterialTheme.colorScheme.onSurface else gainColor
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // MIDDLE TIER: DEDICATED CLEAN DUAL-METRIC PRICING CAPSULE (AUD + TINY USD FOR US)
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Average Buy Price Block (AUD)
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(6.dp)
                                                            .clip(CircleShape)
                                                            .background(SlateGray.copy(alpha = 0.8f))
                                                    )
                                                    Text(
                                                        if (holding.isUsMarket) "AVG BUY (AUD)" else "AVG BUY PRICE",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = SlateGray,
                                                        letterSpacing = 0.6.sp
                                                    )
                                                }
                                                Spacer(Modifier.height(2.dp))
                                                Text(
                                                    text = maskVal(holding.averagePurchasePrice, 2),
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                if (holding.isUsMarket && holding.usdAveragePurchasePrice > 0 && !isWealthHidden) {
                                                    Text(
                                                        text = formatUsdCurrency(holding.usdAveragePurchasePrice, 2),
                                                        fontSize = 9.sp,
                                                        color = SlateGray,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }

                                            // Vertical Divider
                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp)
                                                    .width(1.dp)
                                                    .height(if (holding.isUsMarket) 34.dp else 26.dp)
                                                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
                                            )

                                            // Current Market Price Block (AUD + tiny USD share price for US shares)
                                            Column(
                                                modifier = Modifier.weight(1.1f),
                                                horizontalAlignment = Alignment.End
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(6.dp)
                                                            .clip(CircleShape)
                                                            .background(BrandMintGreen)
                                                    )
                                                    Text(
                                                        if (holding.isUsMarket) "CURRENT PRICE (AUD)" else "CURRENT ASX PRICE",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = sharesTextAccent,
                                                        letterSpacing = 0.6.sp
                                                    )
                                                }
                                                Spacer(Modifier.height(2.dp))
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                                ) {
                                                    Text(
                                                        text = if (isWealthHidden) "$ ••••••" else formatCurrency(holding.currentPrice, 2),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = sharesTextAccent
                                                    )
                                                    if (!isWealthHidden && holding.currentPrice > 0 && holding.averagePurchasePrice > 0) {
                                                        val priceDiffPct = ((holding.currentPrice - holding.averagePurchasePrice) / holding.averagePurchasePrice) * 100
                                                        val isDiffPos = priceDiffPct >= 0
                                                        Surface(
                                                            shape = RoundedCornerShape(4.dp),
                                                            color = (if (isDiffPos) gainTextColor else lossTextColor).copy(alpha = 0.16f)
                                                        ) {
                                                            Text(
                                                                text = "${if (isDiffPos) "+" else ""}${String.format(locale, "%.1f", priceDiffPct)}%",
                                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                                                fontSize = 9.5.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = if (isDiffPos) gainTextColor else lossTextColor
                                                            )
                                                        }
                                                    }
                                                }
                                                if (holding.isUsMarket && holding.usdCurrentPrice > 0 && !isWealthHidden) {
                                                    Text(
                                                        text = "${formatUsdCurrency(holding.usdCurrentPrice, 2)} USD",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = SlateGray
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // BOTTOM TIER: Total Cost Basis & Clickable Graph Affordance
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Cost Basis: ${maskVal(holding.totalCostBasis, 2)}",
                                            fontSize = 11.sp,
                                            color = SlateGray,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(
                                                "View Graph & History",
                                                fontSize = 11.sp,
                                                color = sharesTextAccent,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Icon(
                                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                                contentDescription = null,
                                                tint = sharesTextAccent,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
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

                    SwipeToRevealBox(
                        onEdit = { superToEdit = superAcc },
                        onDelete = { superToDelete = superAcc }
                    ) {
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

                                Text(maskVal(superAcc.balance, 0), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
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
            initialTicker = null,
            onDismiss = { showAddShareDialog = false },
            onConfirm = { code, owner, units, pPrice, cPrice, date, market, usdPPrice, usdCPrice, fxRate ->
                onAddShare(code, owner, units, pPrice, cPrice, date, market, usdPPrice, usdCPrice, fxRate)
                showAddShareDialog = false
            }
        )
    }

    addPurchaseForHolding?.let { holding ->
        AddEditShareParcelDialog(
            parcel = null,
            ownerOptions = ownerChoices,
            initialTicker = holding.code,
            initialCurrentPrice = holding.currentPrice,
            initialMarket = holding.market,
            initialUsdCurrentPrice = holding.usdCurrentPrice.takeIf { it > 0 },
            initialFxRate = holding.fxRate.takeIf { it > 0 },
            onDismiss = { addPurchaseForHolding = null },
            onConfirm = { code, owner, units, pPrice, cPrice, date, market, usdPPrice, usdCPrice, fxRate ->
                onAddShare(code, owner, units, pPrice, cPrice, date, market, usdPPrice, usdCPrice, fxRate)
                addPurchaseForHolding = null
            }
        )
    }

    shareToEdit?.let { parcel ->
        AddEditShareParcelDialog(
            parcel = parcel,
            ownerOptions = ownerChoices,
            initialTicker = parcel.code,
            initialCurrentPrice = parcel.currentPrice,
            initialMarket = parcel.market,
            onDismiss = { shareToEdit = null },
            onConfirm = { code, owner, units, pPrice, cPrice, date, market, usdPPrice, usdCPrice, fxRate ->
                onEditShare(parcel.id, code, owner, units, pPrice, cPrice, date, market, usdPPrice, usdCPrice, fxRate)
                shareToEdit = null
            }
        )
    }

    holdingForParcelPicker?.let { pickedHolding ->
        val liveHolding = groupedShares.find { it.holdingKey == pickedHolding.holdingKey } ?: pickedHolding
        AlertDialog(
            onDismissRequest = { holdingForParcelPicker = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AsxShareLogoBadge(ticker = liveHolding.code, size = 30.dp, market = liveHolding.market)
                    Column {
                        Text(
                            "Edit Individual Buys • ${liveHolding.code}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            "${liveHolding.parcels.size} buys • ${formatShareUnits(liveHolding.totalUnits)} units (${maskVal(liveHolding.totalMarketValue, 2)})",
                            fontSize = 11.sp,
                            color = SlateGray
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    liveHolding.parcels.forEach { parcel ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "${formatParcelDate(parcel)} • ${parcel.owner}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        "${formatShareUnits(parcel.units)} units @ ${maskVal(parcel.purchasePrice, 2)}" +
                                            if (parcel.market.equals("US", ignoreCase = true) && parcel.usdPurchasePrice > 0 && !isWealthHidden) {
                                                " (${formatUsdCurrency(parcel.usdPurchasePrice, 2)})"
                                            } else "",
                                        fontSize = 11.sp,
                                        color = SlateGray
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            holdingForParcelPicker = null
                                            shareToEdit = parcel
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit Buy",
                                            tint = BrandWarmAmber,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            holdingForParcelPicker = null
                                            shareToDelete = parcel
                                        },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.DeleteOutline,
                                            contentDescription = "Delete Buy",
                                            tint = BrandCoralRed,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        holdingForParcelPicker = null
                        addPurchaseForHolding = liveHolding
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = sharesAccent, contentColor = BrandForestDark)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Add Buy", fontWeight = FontWeight.ExtraBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { holdingForParcelPicker = null }) {
                    Text("Close")
                }
            }
        )
    }

    holdingForDeleteConfirm?.let { delHolding ->
        AlertDialog(
            onDismissRequest = { holdingForDeleteConfirm = null },
            title = { Text("Delete ${delHolding.code}?", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "${delHolding.code} has ${delHolding.parcels.size} individual buys. Delete all ${delHolding.parcels.size} buys or choose a specific buy?",
                        fontSize = 13.sp,
                        color = SlateGray
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 260.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        delHolding.parcels.forEach { parcel ->
                            Surface(
                                onClick = {
                                    holdingForDeleteConfirm = null
                                    shareToDelete = parcel
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(0.8.dp, BrandCoralRed.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "${formatParcelDate(parcel)} • ${parcel.owner}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "${formatShareUnits(parcel.units)} units @ ${maskVal(parcel.purchasePrice, 2)}",
                                            fontSize = 11.sp,
                                            color = SlateGray
                                        )
                                    }
                                    Icon(
                                        Icons.Default.DeleteOutline,
                                        contentDescription = "Delete Buy",
                                        tint = BrandCoralRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        delHolding.parcels.forEach { onDeleteShare(it.id) }
                        if (selectedHoldingKey == delHolding.holdingKey) {
                            selectedHoldingKey = null
                        }
                        holdingForDeleteConfirm = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCoralRed, contentColor = Color.White)
                ) {
                    Text("Delete All (${delHolding.parcels.size})", fontWeight = FontWeight.ExtraBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { holdingForDeleteConfirm = null }) {
                    Text("Cancel")
                }
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

    shareToDelete?.let { parcel ->
        ConfirmDeleteDialog(
            title = "Delete Purchase Transaction?",
            itemName = parcel.code.uppercase(),
            itemType = "purchase transaction",
            message = "Are you sure you want to delete ${formatShareUnits(parcel.units)} units of ${parcel.code.uppercase()} bought on ${formatParcelDate(parcel)}? This action cannot be undone.",
            onDismiss = { shareToDelete = null },
            onConfirm = {
                onDeleteShare(parcel.id)
                shareToDelete = null
            }
        )
    }

    superToDelete?.let { superAcc ->
        ConfirmDeleteDialog(
            title = "Delete Super Account?",
            itemName = superAcc.fundName,
            itemType = "super account",
            onDismiss = { superToDelete = null },
            onConfirm = {
                onDeleteSuperAccount(superAcc.id)
                superToDelete = null
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
    var assetToDelete by remember { mutableStateOf<OtherAsset?>(null) }

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
        contentPadding = PaddingValues(top = headerTopPad, bottom = 140.dp),
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

            SwipeToRevealBox(
                onEdit = { assetToEdit = asset },
                onDelete = { assetToDelete = asset }
            ) {
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

                        Text(maskVal(asset.value, 0), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
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

    assetToDelete?.let { asset ->
        ConfirmDeleteDialog(
            title = "Delete Asset?",
            itemName = asset.name,
            itemType = "asset",
            onDismiss = { assetToDelete = null },
            onConfirm = {
                onDeleteOtherAsset(asset.id)
                assetToDelete = null
            }
        )
    }
}

// ============================================================================
// SHARED COMPONENTS & DIALOGS (WITH COLOR-BLIND IN/OUT ARROWS)
// ============================================================================
@Composable
fun SwipeToRevealBox(
    modifier: Modifier = Modifier,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val actionCount = (if (onEdit != null) 1 else 0) + (if (onDelete != null) 1 else 0)
    if (actionCount == 0) {
        Box(modifier = modifier) {
            content()
        }
        return
    }

    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val revealState = LocalOrbRevealState.current
    val orbKey = remember { Any() }
    val isRevealed = revealState.activeOrbKey == orbKey

    DisposableEffect(orbKey) {
        onDispose {
            if (revealState.activeOrbKey == orbKey) {
                revealState.dismiss()
            }
        }
    }

    val revealWidthDp = (actionCount * 68 + (actionCount - 1) * 6 + 10).dp
    val maxRevealPx = with(density) { revealWidthDp.toPx() }

    val animatedOffsetX by animateFloatAsState(
        targetValue = if (isRevealed) -maxRevealPx else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "long_press_reveal_offset"
    )

    Box(modifier = modifier.fillMaxWidth()) {
        // Revealed background action pills (Edit / Delete)
        if (animatedOffsetX < -2f) {
            Row(
                modifier = Modifier
                    .matchParentSize()
                    .padding(start = 16.dp, end = 2.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(vertical = 2.dp)
                        .onGloballyPositioned { coords ->
                            if (isRevealed && coords.isAttached) {
                                revealState.activeActionTrayBounds = coords.boundsInRoot()
                            }
                        }
                ) {
                    if (onEdit != null) {
                        Surface(
                            onClick = {
                                revealState.dismiss()
                                onEdit()
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = BrandWarmAmber.copy(alpha = 0.22f),
                            border = BorderStroke(1.dp, BrandWarmAmber.copy(alpha = 0.65f)),
                            modifier = Modifier
                                .width(66.dp)
                                .fillMaxHeight()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = BrandWarmAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "Edit",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrandWarmAmber
                                )
                            }
                        }
                    }

                    if (onDelete != null) {
                        Surface(
                            onClick = {
                                revealState.dismiss()
                                onDelete()
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = BrandCoralRed.copy(alpha = 0.22f),
                            border = BorderStroke(1.dp, BrandCoralRed.copy(alpha = 0.70f)),
                            modifier = Modifier
                                .width(66.dp)
                                .fillMaxHeight()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.DeleteOutline,
                                    contentDescription = "Delete",
                                    tint = BrandCoralRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "Delete",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrandCoralRed
                                )
                            }
                        }
                    }
                }
            }
        }

        // Foreground card: press & hold (long-press) to reveal Edit/Delete without blocking horizontal/vertical scrolling
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                .pointerInput(orbKey, revealState) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                        val touchSlop = viewConfiguration.touchSlop
                        val holdTimeoutMs = 380L
                        var movedPastSlop = false
                        var releasedEarly = false

                        val heldUntilTimeout = withTimeoutOrNull(holdTimeoutMs) {
                            while (true) {
                                val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                                val change = event.changes.firstOrNull { it.id == down.id }
                                    ?: event.changes.firstOrNull()
                                if (change == null || !change.pressed) {
                                    releasedEarly = true
                                    break
                                }
                                if ((change.position - down.position).getDistance() > touchSlop) {
                                    movedPastSlop = true
                                    break
                                }
                            }
                        } == null

                        if (heldUntilTimeout && !movedPastSlop && !releasedEarly) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (revealState.activeOrbKey == orbKey) {
                                revealState.dismiss()
                            } else {
                                revealState.activeOrbKey = orbKey
                            }
                            do {
                                val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                                event.changes.forEach { it.consume() }
                            } while (event.changes.any { it.pressed })
                        }
                    }
                }
        ) {
            content()

            // Tap shifted card to close reveal tray
            if (isRevealed) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            revealState.dismiss()
                        }
                )
            }
        }
    }
}

@Composable
fun ConfirmDeleteDialog(
    title: String,
    message: String? = null,
    itemName: String? = null,
    itemType: String = "item",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = BrandCoralRed,
                    modifier = Modifier.size(24.dp)
                )
                Text(title, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Text(
                text = message ?: if (!itemName.isNullOrBlank()) {
                    "Are you sure you want to delete \"$itemName\"? This action cannot be undone."
                } else {
                    "Are you sure you want to delete this $itemType? This action cannot be undone."
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandCoralRed,
                    contentColor = BrandForestDark
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Delete", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ExpenseItemCard(
    item: CashflowItem,
    accentColor: Color,
    isWealthHidden: Boolean = false,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)?
) {
    val batteryEnabled = item.isBatteryEnabled
    val readyToPay = item.isReadyToPay
    val progress = if (batteryEnabled) {
        val animatedProgress by animateFloatAsState(
            targetValue = item.chargeProgress,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            label = "battery_charge_progress"
        )
        animatedProgress
    } else {
        0f
    }

    val readyBorderGlowAlpha = if (readyToPay) {
        val infiniteTransition = rememberInfiniteTransition(label = "ready_glow_transition")
        val animatedAlpha by infiniteTransition.animateFloat(
            initialValue = 0.45f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "ready_glow_alpha"
        )
        animatedAlpha
    } else {
        0f
    }

    val batteryAccent = if (readyToPay) BrandMintGreen else accentColor

    SwipeToRevealBox(
        onEdit = onEdit,
        onDelete = onDelete
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            border = when {
                readyToPay -> BorderStroke(1.8.dp, batteryAccent.copy(alpha = readyBorderGlowAlpha))
                batteryEnabled -> BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
                else -> null
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = if (batteryEnabled) 10.dp else 12.dp),
                verticalArrangement = Arrangement.spacedBy(if (batteryEnabled) 6.dp else 10.dp)
            ) {
                // Top Row: Icon + Name on left, Weekly Amount on right (swipe left for Edit/Delete)
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(iconVector, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
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

                    CashflowDirectionAmount(
                        amount = item.weeklyAmount,
                        suffix = "/wk",
                        decimals = 2,
                        isWealthHidden = isWealthHidden,
                        fontSize = 14
                    )
                }

                // Bottom Section: Sleek, ultra-compact Battery strip (minimal height, no top-row shift)
                if (batteryEnabled) {
                    val pctInt = (item.chargeProgress * 100).toInt()
                    val bankedStr = if (isWealthHidden) {
                        "$••• / $••• banked"
                    } else {
                        "${formatCurrency(item.accruedAmount, 0)} / ${formatCurrency(item.cycleTargetAmount, 0)} banked"
                    }
                    val dueInText = when {
                        item.daysUntilDue <= 0L -> "Due today"
                        item.daysUntilDue == 1L -> "Due in 1d"
                        item.daysUntilDue < 14L -> "Due in ${item.daysUntilDue}d"
                        else -> "Due in ${item.weeksUntilDue}w"
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(3.5.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = batteryAccent.copy(alpha = 0.18f),
                                    border = BorderStroke(0.6.dp, batteryAccent.copy(alpha = 0.45f))
                                ) {
                                    Text(
                                        text = item.billingCycle.label.uppercase(),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = batteryAccent,
                                        letterSpacing = 0.4.sp
                                    )
                                }
                                Text(
                                    text = bankedStr,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                                )
                            }

                            Text(
                                text = if (readyToPay) "⚡ Ready • $dueInText" else "$pctInt% • $dueInText",
                                fontSize = 10.5.sp,
                                fontWeight = if (readyToPay) FontWeight.ExtraBold else FontWeight.SemiBold,
                                color = if (readyToPay) BrandMintGreen else batteryAccent.copy(alpha = 0.92f)
                            )
                        }

                        // Sleek Thin Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.5.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.Black.copy(alpha = 0.32f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(progress.coerceIn(0.03f, 1f))
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                batteryAccent.copy(alpha = 0.85f),
                                                lerp(batteryAccent, Color.White, 0.22f)
                                            )
                                        )
                                    )
                            )
                        }
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
    onConfirm: (String, Double, String, BillingCycle, Long?, String?) -> Unit
) {
    fun formatCleanAmount(amt: Double): String {
        if (amt <= 0.0) return ""
        return if (abs(amt - amt.roundToInt()) < 0.01) {
            amt.roundToInt().toString()
        } else {
            String.format(Locale.US, "%.2f", amt).trimEnd('0').trimEnd('.')
        }
    }

    val locale = currentLocale()
    val zoneId = remember { ZoneId.systemDefault() }
    val dateFormatter = remember(locale) { DateTimeFormatter.ofPattern("d MMM yyyy", locale) }

    var name by remember(initialItem) { mutableStateOf(initialItem?.name ?: "") }
    var selectedCycle by remember(initialItem) {
        mutableStateOf(initialItem?.billingCycle ?: BillingCycle.WEEKLY)
    }
    var amountStr by remember(initialItem) {
        val initAmt = if (initialItem != null) {
            if (initialItem.billingCycle != BillingCycle.WEEKLY) {
                initialItem.cycleTargetAmount
            } else {
                abs(initialItem.weeklyAmount)
            }
        } else 0.0
        mutableStateOf(formatCleanAmount(initAmt))
    }
    var isExpense by remember(initialItem) {
        mutableStateOf(initialItem?.let { it.weeklyAmount < 0 } ?: defaultIsExpense)
    }
    var nextDueDateMillis by remember(initialItem) {
        mutableStateOf(initialItem?.nextDueDateEpochMillis)
    }
    var dueInWeeksInput by remember(initialItem) {
        val initWeeks = if (initialItem != null && initialItem.isBatteryEnabled) {
            initialItem.weeksUntilDue.toString()
        } else {
            ""
        }
        mutableStateOf(initWeeks)
    }
    var showDatePickerModal by remember { mutableStateOf(false) }

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
                    label = { Text("Description (e.g. Strata, Rego, Council Rates)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = {
                        Text(
                            if (selectedCycle == BillingCycle.WEEKLY) "Weekly Amount ($)"
                            else "${selectedCycle.label} Bill Target ($)"
                        )
                    },
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
                Text(
                    text = "Billing Cycle (auto-converts to $/wk):",
                    fontSize = 12.sp,
                    color = SlateGray
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(BillingCycle.entries) { cycle ->
                        FilterChip(
                            selected = selectedCycle == cycle,
                            onClick = {
                                if (selectedCycle != cycle) {
                                    val currentTyped = abs(amountStr.toDoubleOrNull() ?: 0.0)
                                    if (initialItem != null && currentTyped > 0.0) {
                                        val currentWeekly = currentTyped / selectedCycle.cycleWeeks
                                        val newTarget = currentWeekly * cycle.cycleWeeks
                                        amountStr = formatCleanAmount(newTarget)
                                    }
                                    selectedCycle = cycle
                                    if (cycle != BillingCycle.WEEKLY && isExpense && (nextDueDateMillis == null || nextDueDateMillis!! <= 0L)) {
                                        val defaultDue = LocalDate.now(zoneId).plusDays(cycle.cycleDays)
                                        nextDueDateMillis = defaultDue.atStartOfDay(zoneId).toInstant().toEpochMilli()
                                        dueInWeeksInput = (cycle.cycleDays / 7L).toString()
                                    }
                                }
                            },
                            label = { Text(cycle.label, fontSize = 11.sp) }
                        )
                    }
                }

                AnimatedVisibility(visible = isExpense && selectedCycle != BillingCycle.WEEKLY) {
                    val effectiveDueMillis = nextDueDateMillis ?: remember(selectedCycle) {
                        LocalDate.now(zoneId).plusDays(selectedCycle.cycleDays)
                            .atStartOfDay(zoneId).toInstant().toEpochMilli()
                    }
                    val dueLocalDate = remember(effectiveDueMillis) {
                        try {
                            Instant.ofEpochMilli(effectiveDueMillis).atZone(zoneId).toLocalDate()
                        } catch (_: Exception) {
                            LocalDate.now(zoneId).plusDays(selectedCycle.cycleDays)
                        }
                    }
                    val daysRemaining = remember(dueLocalDate) {
                        ChronoUnit.DAYS.between(LocalDate.now(zoneId), dueLocalDate).coerceAtLeast(0L)
                    }
                    val weeksRemaining = (daysRemaining / 7L).toInt()
                    val rawAmt = abs(amountStr.toDoubleOrNull() ?: 0.0)
                    val previewWeekly = if (selectedCycle.cycleWeeks > 0.0) rawAmt / selectedCycle.cycleWeeks else rawAmt
                    val previewProgress = remember(daysRemaining, selectedCycle) {
                        ((selectedCycle.cycleDays - daysRemaining).toFloat() / selectedCycle.cycleDays.toFloat()).coerceIn(0f, 1f)
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BrandMintGreen.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, BrandMintGreen.copy(alpha = 0.40f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Due in ${if (daysRemaining < 14L) "${daysRemaining}d" else "${weeksRemaining}w"} • ${(previewProgress * 100).toInt()}% banked",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrandMintGreen
                                )
                                if (rawAmt > 0.0) {
                                    Text(
                                        text = "${formatCurrency(previewWeekly, 2)}/wk",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandWarmAmber
                                    )
                                }
                            }

                            // Quick "Due in ..." weeks input + Calendar picker side-by-side
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = dueInWeeksInput,
                                    onValueChange = { input ->
                                        val filtered = input.filter { it.isDigit() }.take(3)
                                        dueInWeeksInput = filtered
                                        val wks = filtered.toLongOrNull()
                                        if (wks != null) {
                                            val newDate = LocalDate.now(zoneId).plusDays(wks * 7L)
                                            nextDueDateMillis = newDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
                                        }
                                    },
                                    label = { Text("Due in (wks)", fontSize = 11.sp) },
                                    placeholder = { Text("${weeksRemaining}w") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.weight(0.45f)
                                )

                                OutlinedButton(
                                    onClick = { showDatePickerModal = true },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(0.55f),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp)
                                ) {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = "Select Next Due Date",
                                        tint = BrandCoralPink,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = dueLocalDate.format(dateFormatter),
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Quick-select "Due in ..." preset chips
                            val maxPresetWeeks = (selectedCycle.cycleDays / 7L).toInt().coerceAtLeast(4)
                            val presetWeeks = remember(selectedCycle) {
                                listOf(1, 2, 4, 6, 8, 12, 26, 52)
                                    .filter { it <= maxPresetWeeks }
                                    .ifEmpty { listOf(1, 2, 4) }
                            }
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(presetWeeks) { wks ->
                                    val isSelected = dueInWeeksInput == wks.toString() || (dueInWeeksInput.isEmpty() && weeksRemaining == wks)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            dueInWeeksInput = wks.toString()
                                            val newDate = LocalDate.now(zoneId).plusDays(wks * 7L)
                                            nextDueDateMillis = newDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
                                        },
                                        label = { Text("Due in ${wks}w", fontSize = 10.5.sp) }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
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
                        val weeklyVal = if (selectedCycle.cycleWeeks > 0.0) {
                            raw / selectedCycle.cycleWeeks
                        } else {
                            raw
                        }
                        val finalCycle = if (isExpense) selectedCycle else BillingCycle.WEEKLY
                        val finalDueMillis = if (isExpense && finalCycle != BillingCycle.WEEKLY) {
                            nextDueDateMillis ?: LocalDate.now(zoneId)
                                .plusDays(finalCycle.cycleDays)
                                .atStartOfDay(zoneId)
                                .toInstant()
                                .toEpochMilli()
                        } else {
                            null
                        }
                        onConfirm(
                            name.trim(),
                            if (isExpense) -weeklyVal else weeklyVal,
                            selectedIcon,
                            finalCycle,
                            finalDueMillis,
                            null
                        )
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

    if (showDatePickerModal) {
        val initialMillis = nextDueDateMillis ?: LocalDate.now(zoneId)
            .plusDays(selectedCycle.cycleDays)
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePickerModal = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { pickedUtcMillis ->
                            val pickedLocalDate = Instant.ofEpochMilli(pickedUtcMillis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                            nextDueDateMillis = pickedLocalDate
                                .atStartOfDay(zoneId)
                                .toInstant()
                                .toEpochMilli()
                            val pickedDays = ChronoUnit.DAYS.between(LocalDate.now(zoneId), pickedLocalDate).coerceAtLeast(0L)
                            dueInWeeksInput = (pickedDays / 7L).toString()
                        }
                        showDatePickerModal = false
                    }
                ) {
                    Text("Confirm", fontWeight = FontWeight.Bold, color = BrandCoralPink)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerModal = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun EditSingleValueDialog(
    title: String,
    label: String,
    initialValue: Double,
    confirmColor: Color = BrandCoralPink,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var textVal by remember {
        mutableStateOf(
            if (initialValue == 0.0) ""
            else if (initialValue % 1.0 == 0.0) initialValue.toLong().toString()
            else initialValue.toString()
        )
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = textVal,
                onValueChange = { textVal = it },
                label = { Text(label) },
                placeholder = { Text("0") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val v = textVal.toDoubleOrNull() ?: 0.0
                    if (v >= 0) onConfirm(v)
                },
                colors = ButtonDefaults.buttonColors(containerColor = confirmColor, contentColor = BrandForestDark)
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
    val locale = currentLocale()
    var nameStr by remember { mutableStateOf(loan.name) }
    var startDateStr by remember { mutableStateOf(loan.startDateStr) }
    var rateStr by remember { mutableStateOf(loan.interestRatePa.toString()) }
    var startBalStr by remember { mutableStateOf(loan.startLoanBalance.toInt().toString()) }
    var offsetStr by remember { mutableStateOf(loan.offsetBalance.toInt().toString()) }
    var origLoanStr by remember { mutableStateOf(loan.originalLoanAmount.toInt().toString()) }
    var showStartDatePicker by remember { mutableStateOf(false) }

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = startDateStr,
                        onValueChange = { startDateStr = it },
                        label = { Text("Schedule Start Date (DD/MM/YYYY)") },
                        placeholder = { Text("DD/MM/YYYY") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedButton(
                        onClick = { showStartDatePicker = true },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick Date", modifier = Modifier.size(16.dp), tint = BrandCoralPink)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newRate = rateStr.toDoubleOrNull() ?: loan.interestRatePa
                    val newOffset = offsetStr.toDoubleOrNull() ?: loan.offsetBalance
                    val newStartBal = startBalStr.toDoubleOrNull() ?: loan.startLoanBalance
                    val newOrig = origLoanStr.toDoubleOrNull() ?: loan.originalLoanAmount
                    val finalStartStr = startDateStr.trim().ifBlank { loan.startDateStr }
                    onSave(
                        loan.copy(
                            name = nameStr.trim().ifBlank { loan.name },
                            startDateStr = finalStartStr,
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

    if (showStartDatePicker) {
        val initialDate = parseFlexibleDate(startDateStr) ?: LocalDate.now()
        val initialMillis = initialDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { pickedUtcMillis ->
                            val pickedLocalDate = Instant.ofEpochMilli(pickedUtcMillis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                            startDateStr = pickedLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", locale))
                        }
                        showStartDatePicker = false
                    }
                ) {
                    Text("Confirm", fontWeight = FontWeight.Bold, color = BrandCoralPink)
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
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
    initialTicker: String? = null,
    initialCurrentPrice: Double? = null,
    initialMarket: String? = null,
    initialUsdCurrentPrice: Double? = null,
    initialFxRate: Double? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, Double, Double, String, String, Double, Double, Double) -> Unit
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkTheme.current
    val locale = currentLocale()
    val prefs = remember { context.getSharedPreferences("expnz_public_wealth_v2", Context.MODE_PRIVATE) }
    val initialMkt = (parcel?.market ?: initialMarket ?: "ASX").uppercase().takeIf { it == "US" } ?: "ASX"
    var market by remember { mutableStateOf(initialMkt) }
    var enterUsInAud by remember {
        mutableStateOf(
            parcel != null && initialMkt == "US" && parcel.usdPurchasePrice <= 0.0 && parcel.purchasePrice > 0.0
        )
    }
    var code by remember { mutableStateOf(parcel?.code ?: initialTicker ?: if (initialMkt == "US") "AAPL" else "VAS") }
    var owner by remember { mutableStateOf(parcel?.owner ?: ownerOptions.firstOrNull() ?: "Owner") }
    var unitsStr by remember { mutableStateOf(parcel?.units?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "") }

    val savedFx = prefs.getFloat("last_usd_aud_rate", 0f).toDouble()
    val initFx = when {
        parcel != null && parcel.fxRateAtSync > 0 -> parcel.fxRateAtSync
        parcel != null && parcel.usdPurchasePrice > 0 && parcel.purchasePrice > 0 -> parcel.purchasePrice / parcel.usdPurchasePrice
        initialFxRate != null && initialFxRate > 0 -> initialFxRate
        savedFx > 0 -> savedFx
        else -> 1.54
    }
    var bgFxRate by remember { mutableDoubleStateOf(initFx) }

    val initPPrice = when {
        parcel == null -> ""
        initialMkt == "US" && !enterUsInAud && parcel.usdPurchasePrice > 0 -> String.format(locale, "%.2f", parcel.usdPurchasePrice)
        initialMkt == "US" && !enterUsInAud && parcel.purchasePrice > 0 -> String.format(locale, "%.2f", parcel.purchasePrice / initFx)
        parcel.purchasePrice > 0 -> String.format(locale, "%.2f", parcel.purchasePrice)
        else -> ""
    }
    val initCPrice = when {
        initialMkt == "US" && !enterUsInAud && parcel != null && parcel.usdCurrentPrice > 0 -> String.format(locale, "%.2f", parcel.usdCurrentPrice)
        initialMkt == "US" && !enterUsInAud && initialUsdCurrentPrice != null && initialUsdCurrentPrice > 0 -> String.format(locale, "%.2f", initialUsdCurrentPrice)
        initialMkt == "US" && !enterUsInAud && parcel != null && parcel.currentPrice > 0 -> String.format(locale, "%.2f", parcel.currentPrice / initFx)
        parcel?.currentPrice != null && parcel.currentPrice > 0 -> String.format(locale, "%.2f", parcel.currentPrice)
        initialCurrentPrice != null && initialCurrentPrice > 0 -> String.format(locale, "%.2f", initialCurrentPrice)
        else -> ""
    }

    var pPriceStr by remember { mutableStateOf(initPPrice) }
    var cPriceStr by remember { mutableStateOf(initCPrice) }
    var purchaseDateStr by remember {
        mutableStateOf(
            parcel?.purchaseDate?.ifBlank { null }?.let { formatDisplayDate(it) }
                ?: LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy", locale))
        )
    }
    var showPurchaseDatePicker by remember { mutableStateOf(false) }
    var isFetchingPrice by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var fetchStatusMsg by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(market) {
        if (market == "US") {
            val liveFx = fetchUsdToAudRate(context)
            if (liveFx != null && liveFx > 0.0) {
                bgFxRate = liveFx
            }
        }
    }

    fun fetchPriceForTicker(ticker: String, targetMarket: String, inAud: Boolean) {
        if (ticker.isBlank()) return
        isFetchingPrice = true
        fetchStatusMsg = null
        scope.launch {
            val quote = fetchShareLiveQuote(
                ticker = ticker,
                market = targetMarket,
                usdToAudRateOverride = null,
                context = context
            )
            if (quote != null && quote.audPrice > 0.0) {
                if (targetMarket == "US") {
                    val usdVal = quote.usdPrice ?: quote.nativePrice
                    val fxVal = quote.usdToAudRate ?: bgFxRate
                    bgFxRate = fxVal
                    val displayVal = if (inAud) quote.audPrice else usdVal
                    cPriceStr = String.format(locale, "%.2f", displayVal)
                    if (pPriceStr.isBlank()) {
                        pPriceStr = cPriceStr
                    }
                    fetchStatusMsg = if (inAud) {
                        "Live ${ticker.uppercase()}: A$${String.format(locale, "%.2f", quote.audPrice)}"
                    } else {
                        "Live ${ticker.uppercase()}: ${formatUsdCurrency(usdVal, 2)}"
                    }
                } else {
                    cPriceStr = String.format(locale, "%.2f", quote.audPrice)
                    if (pPriceStr.isBlank()) {
                        pPriceStr = cPriceStr
                    }
                    fetchStatusMsg = "Live ${ticker.uppercase()}.AX: A$${String.format(locale, "%.2f", quote.audPrice)}"
                }
            } else {
                fetchStatusMsg = "Could not fetch live quote for ${ticker.uppercase()}"
            }
            isFetchingPrice = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (parcel == null) "Add Share Purchase" else "Edit Purchase Transaction", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = market == "ASX",
                        onClick = {
                            if (market != "ASX") {
                                market = "ASX"
                                if (parcel == null && initialTicker == null && code == "AAPL") code = "VAS"
                                fetchStatusMsg = null
                            }
                        },
                        label = { Text("ASX", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = market == "US",
                        onClick = {
                            if (market != "US") {
                                market = "US"
                                if (parcel == null && initialTicker == null && code == "VAS") code = "AAPL"
                                fetchStatusMsg = null
                            }
                        },
                        label = { Text("US Shares", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (market == "US") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = !enterUsInAud,
                            onClick = {
                                if (enterUsInAud) {
                                    val fx = bgFxRate.coerceAtLeast(0.01)
                                    pPriceStr.toDoubleOrNull()?.let { pPriceStr = String.format(locale, "%.2f", it / fx) }
                                    cPriceStr.toDoubleOrNull()?.let { cPriceStr = String.format(locale, "%.2f", it / fx) }
                                    enterUsInAud = false
                                    fetchStatusMsg = null
                                }
                            },
                            label = { Text("Enter in US$", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = enterUsInAud,
                            onClick = {
                                if (!enterUsInAud) {
                                    val fx = bgFxRate.coerceAtLeast(0.01)
                                    pPriceStr.toDoubleOrNull()?.let { pPriceStr = String.format(locale, "%.2f", it * fx) }
                                    cPriceStr.toDoubleOrNull()?.let { cPriceStr = String.format(locale, "%.2f", it * fx) }
                                    enterUsInAud = true
                                    fetchStatusMsg = null
                                }
                            },
                            label = { Text("Enter in A$", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it.uppercase() },
                        label = {
                            Text(
                                if (market == "US") "US Ticker (e.g. AAPL, NVDA, VOO)"
                                else "ASX Ticker (e.g. VAS, IVV, BHP)"
                            )
                        },
                        leadingIcon = {
                            Box(modifier = Modifier.padding(start = 6.dp), contentAlignment = Alignment.Center) {
                                AsxShareLogoBadge(ticker = code, size = 18.dp, market = market)
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedButton(
                        onClick = { fetchPriceForTicker(code, market, enterUsInAud) },
                        enabled = !isFetchingPrice && code.isNotBlank(),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isFetchingPrice) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Fetch Live", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isDark) BrandSkyBlue else Color(0xFF0284C7))
                        }
                    }
                }

                if (fetchStatusMsg != null) {
                    Text(
                        text = fetchStatusMsg!!,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (fetchStatusMsg!!.startsWith("Live")) (if (isDark) BrandMintGreen else Color(0xFF059669)) else BrandCoralRed
                    )
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

                val inputCurrencyLabel = if (market == "US" && !enterUsInAud) "US$" else "A$"
                OutlinedTextField(
                    value = pPriceStr,
                    onValueChange = { pPriceStr = it },
                    label = { Text("Purchase Price per Share ($inputCurrencyLabel)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = cPriceStr,
                    onValueChange = { cPriceStr = it },
                    label = { Text("Current Price per Share ($inputCurrencyLabel)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = purchaseDateStr,
                        onValueChange = { purchaseDateStr = it },
                        label = { Text("Purchase Date (DD/MM/YYYY)") },
                        placeholder = { Text("DD/MM/YYYY") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedButton(
                        onClick = { showPurchaseDatePicker = true },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick Date", modifier = Modifier.size(16.dp), tint = BrandPurple)
                    }
                    OutlinedButton(
                        onClick = {
                            purchaseDateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy", locale))
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Today", fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isSaving,
                onClick = {
                    val u = unitsStr.toDoubleOrNull() ?: 0.0
                    val rawBuy = pPriceStr.toDoubleOrNull() ?: 0.0
                    val rawCurr = cPriceStr.toDoubleOrNull() ?: rawBuy
                    val cleanCode = if (market == "US") code.trim().uppercase().removeSuffix(".US").removeSuffix(".AX") else code.trim().uppercase().removeSuffix(".AX")
                    if (cleanCode.isNotBlank() && u > 0) {
                        if (market == "US") {
                            isSaving = true
                            scope.launch {
                                val liveFx = fetchUsdToAudRate(context) ?: bgFxRate
                                val fx = liveFx.coerceAtLeast(0.01)
                                bgFxRate = fx
                                val shouldFetchLivePrice = rawCurr <= 0.0 || parcel == null && cPriceStr.isBlank()
                                val liveQuote = if (shouldFetchLivePrice) {
                                    fetchShareLiveQuote(cleanCode, "US", usdToAudRateOverride = fx, context = context)
                                } else null

                                val audBuy = if (enterUsInAud) rawBuy else rawBuy * fx
                                val usdBuy = if (enterUsInAud) rawBuy / fx else rawBuy
                                val audCurr = when {
                                    liveQuote != null && liveQuote.audPrice > 0.0 && rawCurr <= 0.0 -> liveQuote.audPrice
                                    enterUsInAud -> rawCurr
                                    else -> rawCurr * fx
                                }
                                val usdCurr = when {
                                    liveQuote?.usdPrice != null && liveQuote.usdPrice > 0.0 && rawCurr <= 0.0 -> liveQuote.usdPrice
                                    enterUsInAud -> audCurr / fx
                                    else -> rawCurr
                                }
                                isSaving = false
                                onConfirm(cleanCode, owner, u, audBuy, audCurr, purchaseDateStr.trim(), market, usdBuy, usdCurr, fx)
                            }
                        } else {
                            onConfirm(cleanCode, owner, u, rawBuy, rawCurr, purchaseDateStr.trim(), market, 0.0, 0.0, 0.0)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandPurple, contentColor = BrandForestDark)
            ) {
                Text("Save Transaction", fontWeight = FontWeight.ExtraBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )

    if (showPurchaseDatePicker) {
        val initialDate = parseFlexibleDate(purchaseDateStr) ?: LocalDate.now()
        val initialMillis = initialDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showPurchaseDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { pickedUtcMillis ->
                            val pickedLocalDate = Instant.ofEpochMilli(pickedUtcMillis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                            purchaseDateStr = pickedLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", locale))
                        }
                        showPurchaseDatePicker = false
                    }
                ) {
                    Text("Confirm", fontWeight = FontWeight.Bold, color = BrandPurple)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPurchaseDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
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

fun formatUsdCurrency(value: Double, decimals: Int = 2, showPlus: Boolean = false): String {
    val sign = when {
        value < 0 -> "-"
        showPlus && value > 0 -> "+"
        else -> ""
    }
    val absVal = abs(value)
    val formatted = if (decimals == 0) {
        String.format(Locale.getDefault(), "%,.0f", absVal)
    } else {
        String.format(Locale.getDefault(), "%,.${decimals}f", absVal)
    }
    return "${sign}US$$formatted"
}

// ============================================================================
// ASX & US LIVE PRICE + USD/AUD FX LOOKUP ENGINE
// ============================================================================
data class LiveShareQuote(
    val audPrice: Double,
    val nativePrice: Double,
    val usdPrice: Double?,
    val usdToAudRate: Double?,
    val market: String
)

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

private const val BROWSER_USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"

private fun fetchYahooChartMetaPrice(symbol: String): Pair<Double, String?>? {
    val hosts = listOf("query1.finance.yahoo.com", "query2.finance.yahoo.com")
    val encodedSymbol = java.net.URLEncoder.encode(symbol, "UTF-8")
    for (host in hosts) {
        try {
            val url = URL("https://$host/v8/finance/chart/$encodedSymbol?interval=1d&range=5d")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 6000
                readTimeout = 6000
                setRequestProperty("User-Agent", BROWSER_USER_AGENT)
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Accept-Language", "en-US,en;q=0.9")
            }
            if (conn.responseCode == 200) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(responseText)
                val result = json.optJSONObject("chart")?.optJSONArray("result")
                if (result != null && result.length() > 0) {
                    val meta = result.getJSONObject(0).optJSONObject("meta")
                    val regPrice = meta?.optDouble("regularMarketPrice", Double.NaN)
                        ?.takeIf { !it.isNaN() && it > 0.0 }
                        ?: meta?.optDouble("chartPreviousClose", Double.NaN)
                            ?.takeIf { !it.isNaN() && it > 0.0 }
                    val currency = meta?.optString("currency", "")?.uppercase()?.takeIf { it.isNotBlank() }
                    if (regPrice != null) {
                        return regPrice to currency
                    }
                }
            }
        } catch (_: Exception) {}
    }
    return null
}

private fun fetchNasdaqLivePriceUsd(cleanCode: String): Double? {
    val assetClasses = listOf("stocks", "etf")
    for (assetClass in assetClasses) {
        try {
            val encoded = java.net.URLEncoder.encode(cleanCode, "UTF-8")
            val url = URL("https://api.nasdaq.com/api/quote/$encoded/info?assetclass=$assetClass")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 6000
                readTimeout = 6000
                setRequestProperty("User-Agent", BROWSER_USER_AGENT)
                setRequestProperty("Accept", "application/json, text/plain, */*")
                setRequestProperty("Origin", "https://www.nasdaq.com")
                setRequestProperty("Referer", "https://www.nasdaq.com/")
            }
            if (conn.responseCode == 200) {
                val text = conn.inputStream.bufferedReader().use { it.readText() }
                val data = JSONObject(text).optJSONObject("data")
                val priceStr = data?.optJSONObject("primaryData")?.optString("lastSalePrice", "")
                    .orEmpty()
                    .replace("$", "")
                    .replace(",", "")
                    .trim()
                val parsed = priceStr.toDoubleOrNull()
                if (parsed != null && parsed > 0.0) {
                    return parsed
                }
            }
        } catch (_: Exception) {}
    }
    return null
}

suspend fun fetchUsdToAudRate(context: Context? = null): Double? = withContext(Dispatchers.IO) {
    val prefs = context?.getSharedPreferences("expnz_public_wealth_v2", Context.MODE_PRIVATE)
    var rate: Double? = null

    val usdAud = fetchYahooChartMetaPrice("USDAUD=X")?.first
    if (usdAud != null && usdAud in 0.5..4.0) {
        rate = usdAud
    }

    if (rate == null) {
        val audUsd = fetchYahooChartMetaPrice("AUDUSD=X")?.first
        if (audUsd != null && audUsd in 0.25..2.0) {
            rate = 1.0 / audUsd
        }
    }

    if (rate == null) {
        try {
            val url = URL("https://open.er-api.com/v6/latest/USD")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 5000
                readTimeout = 5000
                setRequestProperty("User-Agent", BROWSER_USER_AGENT)
            }
            if (conn.responseCode == 200) {
                val text = conn.inputStream.bufferedReader().use { it.readText() }
                val aud = JSONObject(text).optJSONObject("rates")?.optDouble("AUD", Double.NaN)
                if (aud != null && !aud.isNaN() && aud in 0.5..4.0) {
                    rate = aud
                }
            }
        } catch (_: Exception) {}
    }

    if (rate != null && rate > 0.0) {
        prefs?.edit { putFloat("last_usd_aud_rate", rate.toFloat()) }
        return@withContext rate
    }

    val cached = prefs?.getFloat("last_usd_aud_rate", 0f)?.toDouble() ?: 0.0
    return@withContext cached.takeIf { it in 0.5..4.0 }
}

suspend fun fetchShareLiveQuote(
    ticker: String,
    market: String = "ASX",
    usdToAudRateOverride: Double? = null,
    context: Context? = null
): LiveShareQuote? = withContext(Dispatchers.IO) {
    val cleanMarket = market.trim().uppercase().takeIf { it == "US" } ?: "ASX"
    if (cleanMarket == "US") {
        val cleanCode = ticker.trim().uppercase().removeSuffix(".US").removeSuffix(".AX")
        if (cleanCode.isBlank()) return@withContext null

        val candidateSymbols = listOfNotNull(
            cleanCode,
            cleanCode.replace('.', '-').takeIf { it != cleanCode }
        )
        var quotePair: Pair<Double, String?>? = null
        for (sym in candidateSymbols) {
            quotePair = fetchYahooChartMetaPrice(sym)
            if (quotePair != null) break
        }
        if (quotePair == null) {
            val nasdaqPrice = fetchNasdaqLivePriceUsd(cleanCode)
                ?: cleanCode.replace('-', '.').takeIf { it != cleanCode }?.let { fetchNasdaqLivePriceUsd(it) }
            if (nasdaqPrice != null && nasdaqPrice > 0.0) {
                quotePair = nasdaqPrice to "USD"
            }
        }
        val (nativePrice, currency) = quotePair ?: return@withContext null
        val fx = usdToAudRateOverride ?: fetchUsdToAudRate(context) ?: 1.54
        return@withContext if (currency == "AUD") {
            LiveShareQuote(
                audPrice = nativePrice,
                nativePrice = nativePrice,
                usdPrice = nativePrice / fx,
                usdToAudRate = fx,
                market = "US"
            )
        } else {
            LiveShareQuote(
                audPrice = nativePrice * fx,
                nativePrice = nativePrice,
                usdPrice = nativePrice,
                usdToAudRate = fx,
                market = "US"
            )
        }
    }

    val cleanCode = ticker.trim().uppercase().replace(".AX", "")
    if (cleanCode.isBlank()) return@withContext null

    try {
        val url = URL("https://www.asx.com.au/asx/1/share/$cleanCode")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 6000
            readTimeout = 6000
            setRequestProperty("User-Agent", BROWSER_USER_AGENT)
            setRequestProperty("Accept", "application/json")
        }
        if (conn.responseCode == 200) {
            val responseText = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(responseText)
            val lastPrice = json.optDouble("last_price", Double.NaN)
            if (!lastPrice.isNaN() && lastPrice > 0.0) {
                return@withContext LiveShareQuote(
                    audPrice = lastPrice,
                    nativePrice = lastPrice,
                    usdPrice = null,
                    usdToAudRate = null,
                    market = "ASX"
                )
            }
        }
    } catch (_: Exception) {}

    val yahooAsx = fetchYahooChartMetaPrice("$cleanCode.AX")?.first
    if (yahooAsx != null && yahooAsx > 0.0) {
        return@withContext LiveShareQuote(
            audPrice = yahooAsx,
            nativePrice = yahooAsx,
            usdPrice = null,
            usdToAudRate = null,
            market = "ASX"
        )
    }

    return@withContext null
}

suspend fun fetchAsxLivePrice(ticker: String): Double? = withContext(Dispatchers.IO) {
    val cleanCode = ticker.trim().uppercase().replace(".AX", "")
    fetchShareLiveQuote(cleanCode, "ASX")?.audPrice ?: sampleAsxLivePrices[cleanCode]
}

// ============================================================================
// ASX SHARE LOGO AUTO-IMPORT & CACHING ENGINE
// ============================================================================
private val shareLogoMemoryCache = java.util.concurrent.ConcurrentHashMap<String, android.graphics.Bitmap>()

private val asxKnownCompanyDomains = mapOf(
    "VAS" to "vanguard.com.au",
    "VGS" to "vanguard.com.au",
    "VTS" to "vanguard.com.au",
    "VEU" to "vanguard.com.au",
    "VDHG" to "vanguard.com.au",
    "VHY" to "vanguard.com.au",
    "VAE" to "vanguard.com.au",
    "VAP" to "vanguard.com.au",
    "VAF" to "vanguard.com.au",
    "VGAD" to "vanguard.com.au",
    "VETH" to "vanguard.com.au",
    "VSO" to "vanguard.com.au",
    "VLC" to "vanguard.com.au",
    "VBND" to "vanguard.com.au",
    "IVV" to "blackrock.com",
    "IOZ" to "blackrock.com",
    "IOO" to "blackrock.com",
    "IAA" to "blackrock.com",
    "IEM" to "blackrock.com",
    "IHVV" to "blackrock.com",
    "IJR" to "blackrock.com",
    "IJH" to "blackrock.com",
    "NDQ" to "betashares.com.au",
    "A200" to "betashares.com.au",
    "DHHF" to "betashares.com.au",
    "BGBL" to "betashares.com.au",
    "HGBL" to "betashares.com.au",
    "HNDQ" to "betashares.com.au",
    "QLTY" to "betashares.com.au",
    "ETHI" to "betashares.com.au",
    "FAIR" to "betashares.com.au",
    "HACK" to "betashares.com.au",
    "ATEC" to "betashares.com.au",
    "AAA" to "betashares.com.au",
    "GEAR" to "betashares.com.au",
    "GGUS" to "betashares.com.au",
    "GHHF" to "betashares.com.au",
    "STW" to "ssga.com",
    "SLF" to "ssga.com",
    "SFY" to "ssga.com",
    "QUAL" to "vaneck.com.au",
    "MOAT" to "vaneck.com.au",
    "MVW" to "vaneck.com.au",
    "GOLD" to "globalxetfs.com.au",
    "FANG" to "globalxetfs.com.au",
    "SEMI" to "globalxetfs.com.au",
    "CBA" to "commbank.com.au",
    "BHP" to "bhp.com",
    "CSL" to "csl.com",
    "NAB" to "nab.com.au",
    "WBC" to "westpac.com.au",
    "ANZ" to "anz.com.au",
    "MQG" to "macquarie.com",
    "WES" to "wesfarmers.com.au",
    "WOW" to "woolworthsgroup.com.au",
    "COL" to "colesgroup.com.au",
    "TLS" to "telstra.com.au",
    "WDS" to "woodside.com",
    "RIO" to "riotinto.com",
    "FMG" to "fortescue.com",
    "GMG" to "goodman.com",
    "TCL" to "transurban.com",
    "WTC" to "wisetechglobal.com",
    "XRO" to "xero.com",
    "QBE" to "qbe.com",
    "SUN" to "suncorp.com.au",
    "IAG" to "iag.com.au",
    "MPL" to "medibank.com.au",
    "REA" to "rea-group.com",
    "CAR" to "carsales.com.au",
    "SEK" to "seek.com.au",
    "JBH" to "jbhifi.com.au",
    "HVN" to "harveynorman.com.au",
    "ALL" to "aristocrat.com",
    "QAN" to "qantas.com",
    "STO" to "santos.com",
    "ORG" to "originenergy.com.au",
    "AGL" to "agl.com.au",
    "APA" to "apa.com.au",
    "MIN" to "mineralresources.com.au",
    "PLS" to "pilbaraminerals.com.au",
    "S32" to "south32.net",
    "NST" to "nsrltd.com",
    "EVN" to "evolutionmining.com.au",
    "RMD" to "resmed.com",
    "COH" to "cochlear.com",
    "FPH" to "fphcare.com",
    "SHL" to "sonichealthcare.com",
    "RHC" to "ramsayhealth.com",
    "PME" to "promedicus.com.au",
    "CPU" to "computershare.com",
    "ASX" to "asx.com.au",
    "BXB" to "brambles.com",
    "AMC" to "amcor.com",
    "JHX" to "jameshardie.com.au",
    "BSL" to "bluescope.com",
    "SOL" to "whsp.com.au",
    "SGP" to "stockland.com.au",
    "MGR" to "mirvac.com",
    "VCX" to "vicinity.com.au",
    "SCG" to "scentregroup.com",
    "DXS" to "dexus.com",
    "GPT" to "gpt.com.au",
    "CHC" to "charterhall.com.au",
    "TWE" to "tweglobal.com",
    "EDV" to "endeavourgroup.com.au",
    "TLC" to "thelotterycorporation.com",
    "NXT" to "nextdc.com",
    "TNE" to "technologyonecorp.com",
    "360" to "life360.com",
    "ZIP" to "zip.co",
    "XYZ" to "block.xyz",
    "SQ2" to "block.xyz",
    "HUB" to "hub24.com.au",
    "NWL" to "netwealth.com.au",
    "AMP" to "amp.com.au",
    "BEN" to "bendigoadelaide.com.au",
    "BOQ" to "boq.com.au",
    "HLI" to "helia.com.au",
    "ALQ" to "alsglobal.com",
    "WOR" to "worley.com",
    "QUB" to "qube.com.au",
    "AZJ" to "aurizon.com.au",
    "ALD" to "ampol.com.au",
    "VEA" to "vivaenergy.com.au",
    "LYC" to "lynasrareearths.com",
    "IGO" to "igo.com.au",
    "ILU" to "iluka.com",
    "WHC" to "whitehavencoal.com.au",
    "NHC" to "newhopegroup.com.au",
    "YAL" to "yancoal.com.au",
    "PDN" to "paladinenergy.com.au",
    "BOE" to "bossenergy.com",
    "DMP" to "dominos.com.au",
    "LOV" to "lovisa.com.au",
    "PMV" to "premierinvestments.com.au",
    "SUL" to "superretailgroup.com.au"
)

private fun downloadLogoBitmapWithUserAgent(urlStr: String, userAgent: String): Pair<Int, android.graphics.Bitmap?> {
    return try {
        var currentUrl = urlStr
        var conn: HttpURLConnection? = null
        for (i in 0..3) {
            conn = (URL(currentUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 5000
                readTimeout = 5000
                instanceFollowRedirects = true
                setRequestProperty("User-Agent", userAgent)
                setRequestProperty("Accept", "image/png,image/jpeg,image/webp,image/*,*/*")
            }
            val code = conn.responseCode
            if (code in 300..399) {
                val loc = conn.getHeaderField("Location") ?: break
                currentUrl = URL(URL(currentUrl), loc).toString()
                conn.disconnect()
            } else {
                break
            }
        }
        val finalConn = conn ?: return 0 to null
        val code = finalConn.responseCode
        if (code in 200..299) {
            val contentType = finalConn.contentType.orEmpty().lowercase()
            if (contentType.contains("svg") || contentType.contains("html") || contentType.contains("json")) {
                finalConn.disconnect()
                return code to null
            }
            val bytes = finalConn.inputStream.use { it.readBytes() }
            finalConn.disconnect()
            if (bytes.isNotEmpty()) {
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                if (bmp != null && bmp.width >= 16 && bmp.height >= 16) code to bmp else code to null
            } else code to null
        } else {
            finalConn.disconnect()
            code to null
        }
    } catch (_: Exception) {
        0 to null
    }
}

private fun downloadLogoBitmap(urlStr: String): android.graphics.Bitmap? {
    // MarketIndex & Cloudflare-fronted CDNs allow "Mozilla/5.0 (Android)" while rejecting desktop Chrome TLS/UA mismatches.
    val (code, bmp) = downloadLogoBitmapWithUserAgent(urlStr, "Mozilla/5.0 (Android)")
    if (bmp != null) return bmp
    if (code == 403 || code == 401 || code == 0) {
        val (_, retryBmp) = downloadLogoBitmapWithUserAgent(urlStr, BROWSER_USER_AGENT)
        if (retryBmp != null) return retryBmp
    }
    return null
}

private fun resolveDynamicDomainForAsx(cleanCode: String): String? {
    asxKnownCompanyDomains[cleanCode]?.let { return it }

    // 1. Yahoo Finance search for "<TICKER>.AX" (crumb-free) -> Clearbit domain autocomplete
    try {
        val sym = java.net.URLEncoder.encode("$cleanCode.AX", "UTF-8")
        val url = URL("https://query2.finance.yahoo.com/v1/finance/search?q=$sym&quotesCount=3")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 4000
            readTimeout = 4000
            setRequestProperty("User-Agent", BROWSER_USER_AGENT)
            setRequestProperty("Accept", "application/json")
        }
        if (conn.responseCode == 200) {
            val text = conn.inputStream.bufferedReader().use { it.readText() }
            val quotes = JSONObject(text).optJSONArray("quotes")
            if (quotes != null && quotes.length() > 0) {
                val first = quotes.getJSONObject(0)
                val companyName = first.optString("longname", "").ifBlank { first.optString("shortname", "") }
                if (companyName.isNotBlank()) {
                    val cleanedQuery = companyName
                        .replace(Regex("(?i)\\b(LIMITED|LTD\\.?|GROUP|HOLDINGS|CORP\\.?|CORPORATION|PLC|TRUST|ETF|FUND)\\b"), "")
                        .replace(",", "")
                        .trim()
                    if (cleanedQuery.isNotBlank()) {
                        val encoded = java.net.URLEncoder.encode(cleanedQuery, "UTF-8")
                        val cbUrl = URL("https://autocomplete.clearbit.com/v1/companies/suggest?query=$encoded")
                        val cbConn = (cbUrl.openConnection() as HttpURLConnection).apply {
                            requestMethod = "GET"
                            connectTimeout = 4000
                            readTimeout = 4000
                            setRequestProperty("User-Agent", BROWSER_USER_AGENT)
                        }
                        if (cbConn.responseCode == 200) {
                            val cbArr = JSONArray(cbConn.inputStream.bufferedReader().use { it.readText() })
                            if (cbArr.length() > 0) {
                                val domain = cbArr.getJSONObject(0).optString("domain", "")
                                if (domain.isNotBlank()) return domain
                            }
                        }
                    }
                }
            }
        }
    } catch (_: Exception) {}

    // 2. Fallback: ASX company endpoint + Clearbit domain autocomplete
    try {
        val asxUrl = URL("https://www.asx.com.au/asx/1/share/$cleanCode")
        val conn = (asxUrl.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 4000
            readTimeout = 4000
            setRequestProperty("User-Agent", "Mozilla/5.0 (Android)")
            setRequestProperty("Accept", "application/json")
        }
        if (conn.responseCode == 200) {
            val text = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(text)
            val descName = json.optString("desc_full", "").ifBlank { json.optString("company_name", "") }
            if (descName.isNotBlank()) {
                val cleanedQuery = descName
                    .replace(Regex("(?i)\\b(LIMITED|LTD|GROUP|HOLDINGS|CORP|CORPORATION|PLC|TRUST|ETF|FUND)\\b"), "")
                    .trim()
                if (cleanedQuery.isNotBlank()) {
                    val encoded = java.net.URLEncoder.encode(cleanedQuery, "UTF-8")
                    val cbUrl = URL("https://autocomplete.clearbit.com/v1/companies/suggest?query=$encoded")
                    val cbConn = (cbUrl.openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"
                        connectTimeout = 4000
                        readTimeout = 4000
                        setRequestProperty("User-Agent", "Mozilla/5.0 (Android)")
                    }
                    if (cbConn.responseCode == 200) {
                        val cbArr = JSONArray(cbConn.inputStream.bufferedReader().use { it.readText() })
                        if (cbArr.length() > 0) {
                            val firstObj = cbArr.getJSONObject(0)
                            val domain = firstObj.optString("domain", "")
                            if (domain.isNotBlank()) return domain
                        }
                    }
                }
            }
        }
    } catch (_: Exception) {}

    return null
}

private val usKnownCompanyDomains = mapOf(
    "AAPL" to "apple.com",
    "MSFT" to "microsoft.com",
    "NVDA" to "nvidia.com",
    "GOOGL" to "abc.xyz",
    "GOOG" to "abc.xyz",
    "AMZN" to "amazon.com",
    "META" to "meta.com",
    "TSLA" to "tesla.com",
    "NFLX" to "netflix.com",
    "AMD" to "amd.com",
    "INTC" to "intel.com",
    "AVGO" to "broadcom.com",
    "ORCL" to "oracle.com",
    "CRM" to "salesforce.com",
    "ADBE" to "adobe.com",
    "UBER" to "uber.com",
    "SHOP" to "shopify.com",
    "PLTR" to "palantir.com",
    "COIN" to "coinbase.com",
    "HOOD" to "robinhood.com",
    "V" to "visa.com",
    "MA" to "mastercard.com",
    "JPM" to "jpmorganchase.com",
    "BRK-B" to "berkshirehathaway.com",
    "BRK.B" to "berkshirehathaway.com",
    "COST" to "costco.com",
    "WMT" to "walmart.com",
    "DIS" to "thewaltdisneycompany.com",
    "KO" to "coca-colacompany.com",
    "PEP" to "pepsico.com",
    "NKE" to "nike.com",
    "SBUX" to "starbucks.com",
    "VOO" to "vanguard.com",
    "VTI" to "vanguard.com",
    "VUG" to "vanguard.com",
    "VGT" to "vanguard.com",
    "VT" to "vanguard.com",
    "VXUS" to "vanguard.com",
    "SCHD" to "schwab.com",
    "SPY" to "ssga.com",
    "QQQ" to "invesco.com",
    "IVV" to "blackrock.com",
    "TSM" to "tsmc.com",
    "ARM" to "arm.com",
    "SMCI" to "supermicro.com",
    "SNOW" to "snowflake.com",
    "NET" to "cloudflare.com",
    "SPOT" to "spotify.com",
    "RBLX" to "roblox.com",
    "SOFI" to "sofi.com",
    "PYPL" to "paypal.com",
    "XYZ" to "block.xyz",
    "SQ" to "block.xyz"
)

private fun resolveDynamicDomainForUs(cleanCode: String): String? {
    usKnownCompanyDomains[cleanCode]?.let { return it }

    // 1. Yahoo Finance search (crumb-free) -> Clearbit autocomplete
    try {
        val sym = java.net.URLEncoder.encode(cleanCode.replace('.', '-'), "UTF-8")
        val url = URL("https://query2.finance.yahoo.com/v1/finance/search?q=$sym&quotesCount=3")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 4000
            readTimeout = 4000
            setRequestProperty("User-Agent", BROWSER_USER_AGENT)
            setRequestProperty("Accept", "application/json")
        }
        if (conn.responseCode == 200) {
            val text = conn.inputStream.bufferedReader().use { it.readText() }
            val quotes = JSONObject(text).optJSONArray("quotes")
            if (quotes != null && quotes.length() > 0) {
                val first = quotes.getJSONObject(0)
                val companyName = first.optString("longname", "").ifBlank { first.optString("shortname", "") }
                if (companyName.isNotBlank()) {
                    val cleanedQuery = companyName
                        .replace(Regex("(?i)\\b(INC\\.?|CORP\\.?|CORPORATION|LIMITED|LTD\\.?|PLC|HOLDINGS|GROUP|CO\\.?|ETF|TRUST)\\b"), "")
                        .replace(",", "")
                        .trim()
                    if (cleanedQuery.isNotBlank()) {
                        val encoded = java.net.URLEncoder.encode(cleanedQuery, "UTF-8")
                        val cbUrl = URL("https://autocomplete.clearbit.com/v1/companies/suggest?query=$encoded")
                        val cbConn = (cbUrl.openConnection() as HttpURLConnection).apply {
                            requestMethod = "GET"
                            connectTimeout = 4000
                            readTimeout = 4000
                            setRequestProperty("User-Agent", BROWSER_USER_AGENT)
                        }
                        if (cbConn.responseCode == 200) {
                            val cbArr = JSONArray(cbConn.inputStream.bufferedReader().use { it.readText() })
                            if (cbArr.length() > 0) {
                                val domain = cbArr.getJSONObject(0).optString("domain", "")
                                if (domain.isNotBlank()) return domain
                            }
                        }
                    }
                }
            }
        }
    } catch (_: Exception) {}

    return null
}

suspend fun fetchAsxShareLogoBitmap(
    context: Context,
    ticker: String,
    market: String = "ASX"
): android.graphics.Bitmap? = withContext(Dispatchers.IO) {
    val cleanMarket = market.trim().uppercase().takeIf { it == "US" } ?: "ASX"
    val cleanCode = if (cleanMarket == "US") {
        ticker.trim().uppercase().removeSuffix(".US").removeSuffix(".AX")
    } else {
        ticker.trim().uppercase().replace(".AX", "")
    }
    if (cleanCode.isBlank() || cleanCode.length > 10) return@withContext null

    val cacheKey = "${cleanMarket}_$cleanCode"
    shareLogoMemoryCache[cacheKey]?.let { return@withContext it }

    val cacheDir = java.io.File(context.cacheDir, "share_logos_v3").apply { if (!exists()) mkdirs() }
    val diskFile = java.io.File(cacheDir, "${cacheKey}.png")
    if (diskFile.exists() && diskFile.length() > 0) {
        val diskBmp = runCatching { BitmapFactory.decodeFile(diskFile.absolutePath) }.getOrNull()
        if (diskBmp != null && diskBmp.width >= 16 && diskBmp.height >= 16) {
            shareLogoMemoryCache[cacheKey] = diskBmp
            return@withContext diskBmp
        }
    }

    val candidateUrls = if (cleanMarket == "US") {
        val list = mutableListOf(
            "https://financialmodelingprep.com/image-stock/$cleanCode.png",
            "https://financialmodelingprep.com/image-stock/${cleanCode.replace('.', '-')}.png"
        )
        val domain = resolveDynamicDomainForUs(cleanCode)
        if (!domain.isNullOrBlank()) {
            list.add("https://t2.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=https://$domain&size=128")
            list.add("https://www.google.com/s2/favicons?domain=$domain&sz=128")
        }
        list.add("https://assets.parqet.com/logos/symbol/$cleanCode?format=png")
        list
    } else {
        val list = mutableListOf(
            "https://files.marketindex.com.au/xasx/96x96-png/${cleanCode.lowercase()}.png"
        )
        val domain = resolveDynamicDomainForAsx(cleanCode)
        if (!domain.isNullOrBlank()) {
            list.add("https://t2.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=https://$domain&size=128")
            list.add("https://www.google.com/s2/favicons?domain=$domain&sz=128")
        }
        list
    }

    for (urlStr in candidateUrls.distinct()) {
        val bmp = downloadLogoBitmap(urlStr)
        if (bmp != null) {
            shareLogoMemoryCache[cacheKey] = bmp
            runCatching {
                java.io.FileOutputStream(diskFile).use { out ->
                    bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
                }
            }
            return@withContext bmp
        }
    }

    return@withContext null
}

@Composable
fun AsxShareLogoBadge(
    ticker: String,
    size: Dp = 35.dp,
    market: String = "ASX"
) {
    val context = LocalContext.current
    val cleanMarket = remember(market) { market.trim().uppercase().takeIf { it == "US" } ?: "ASX" }
    val cleanCode = remember(ticker, cleanMarket) {
        if (cleanMarket == "US") ticker.trim().uppercase().removeSuffix(".US").removeSuffix(".AX")
        else ticker.trim().uppercase().replace(".AX", "")
    }
    val cacheKey = "${cleanMarket}_$cleanCode"
    var logoBitmap by remember(cacheKey) { mutableStateOf(shareLogoMemoryCache[cacheKey]) }

    LaunchedEffect(cacheKey) {
        if (cleanCode.isNotBlank() && logoBitmap == null) {
            logoBitmap = fetchAsxShareLogoBitmap(context, cleanCode, cleanMarket)
        }
    }

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                if (logoBitmap != null) Color.White else BrandCoralPink.copy(alpha = 0.25f)
            ),
        contentAlignment = Alignment.Center
    ) {
        val bmp = logoBitmap
        if (bmp != null) {
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = "$cleanCode logo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = cleanCode.take(1).ifBlank { "•" },
                fontSize = (size.value * 0.58f).sp,
                fontWeight = FontWeight.ExtraBold,
                color = BrandCoralPink,
                lineHeight = (size.value * 0.65f).sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FloatingAiAssistantWidget(
    financialContextSummary: String,
    messages: MutableList<ChatMessage>,
    learnedFacts: List<LearnedUserFact> = emptyList(),
    conversationDigest: String = "",
    onExecuteAiActions: (String) -> Pair<String, List<String>> = { it to emptyList() },
    onUpdateDigest: (String) -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
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
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to CoinBot...")
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

    val currentFinancialContext by rememberUpdatedState(financialContextSummary)
    val currentOnExecuteAiActions by rememberUpdatedState(onExecuteAiActions)
    val chatListState = rememberLazyListState()
    val isKeyboardOpen = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    // Scroll to bottom cleanly when the panel opens or keyboard state changes without fighting stream updates
    LaunchedEffect(isExpanded, isKeyboardOpen) {
        if (isExpanded && messages.isNotEmpty()) {
            val totalRendered = chatListState.layoutInfo.totalItemsCount
            val targetIdx = (if (totalRendered > 0) totalRendered - 1 else messages.lastIndex).coerceAtLeast(0)
            runCatching { chatListState.scrollToItem(targetIdx) }
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

            val historySnapshot = messages.toList()

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
                runCatching { chatListState.scrollToItem(messages.lastIndex.coerceAtLeast(0)) }

                var streamMessageAdded = false
                var botMessageIndex = -1
                var lastStreamUiUpdateMs = 0L

                val onStreamUpdate: suspend (String) -> Unit = { partialSoFar ->
                    val now = System.currentTimeMillis()
                    if (!streamMessageAdded || now - lastStreamUiUpdateMs >= 40L) {
                        lastStreamUiUpdateMs = now
                        withContext(Dispatchers.Main) {
                            val streamClean = cleanMarkdownText(
                                partialSoFar
                                    .substringBefore("[[ACTION:")
                                    .substringBefore("[ACTION:")
                            )
                            if (streamClean.isNotBlank()) {
                                if (!streamMessageAdded) {
                                    streamMessageAdded = true
                                    isLoading = false
                                    messages.add(
                                        ChatMessage(
                                            text = streamClean,
                                            isFromUser = false
                                        )
                                    )
                                    botMessageIndex = messages.lastIndex
                                } else if (botMessageIndex in messages.indices) {
                                    messages[botMessageIndex] = messages[botMessageIndex].copy(text = streamClean)
                                }
                                if (chatListState.layoutInfo.totalItemsCount > 0) {
                                    chatListState.scrollToItem(messages.lastIndex)
                                }
                            }
                        }
                    }
                }

                try {
                    val rawAi = AiAssistantManager.askGeminiAssistant(
                        prompt = question,
                        contextSummary = currentFinancialContext,
                        conversationHistory = historySnapshot,
                        bitmap = bitmapToSend,
                        fileContent = fileContentToSend,
                        fileName = fileNameToSend,
                        learnedFacts = learnedFacts,
                        longTermConversationDigest = conversationDigest,
                        onPartialText = onStreamUpdate
                    )
                    val (finalReply, appliedActions) = currentOnExecuteAiActions(rawAi)

                    if (streamMessageAdded && botMessageIndex in messages.indices) {
                        messages[botMessageIndex] = ChatMessage(
                            text = finalReply,
                            isFromUser = false,
                            appliedActions = appliedActions
                        )
                    } else {
                        messages.add(
                            ChatMessage(
                                text = finalReply,
                                isFromUser = false,
                                appliedActions = appliedActions
                            )
                        )
                    }
                    if (chatListState.layoutInfo.totalItemsCount > 0) {
                        chatListState.scrollToItem(messages.lastIndex)
                    }
                    if (isSpeechEnabled) {
                        tts?.speak(finalReply, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    val errMsg = "Sorry, something went wrong: ${e.localizedMessage ?: "Unknown error"}"
                    if (streamMessageAdded && botMessageIndex in messages.indices) {
                        messages[botMessageIndex] = ChatMessage(
                            text = errMsg,
                            isFromUser = false
                        )
                    } else {
                        messages.add(
                            ChatMessage(
                                text = errMsg,
                                isFromUser = false
                            )
                        )
                    }
                } finally {
                    isLoading = false
                }
            }
        }
    }

    fun closeAiWindow() {
        focusManager.clearFocus()
        keyboardController?.hide()
        isExpanded = false
    }

    BackHandler(enabled = isExpanded) {
        closeAiWindow()
    }

    var isManualCollapsed by remember { mutableStateOf(false) }
    // Keep top near the header (1.0f of the header-safe container) both when keyboard is up and down,
    // so when the keyboard is down the top stays fixed near the header and the bottom extends down.
    val targetHeightFraction = if (isManualCollapsed) 0.50f else 1.0f
    val animatedHeightFraction by animateFloatAsState(
        targetValue = targetHeightFraction,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "ai_window_height"
    )

    val expandAnimatable = remember { Animatable(0f) }

    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            expandAnimatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 200,
                    easing = FastOutSlowInEasing
                )
            )
        } else {
            expandAnimatable.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 150,
                    easing = FastOutLinearInEasing
                )
            )
        }
    }

    val aiPanelShape = remember { RoundedCornerShape(24.dp) }
    val density = LocalDensity.current

    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val headerSafeTop = statusBarTop + 64.dp

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Draggable Floating 3D Liquid Glass Orb Chatbot Button (only rendered when window is fully closed)
        if (!isExpanded && expandAnimatable.value == 0f) {
            Surface(
                onClick = {
                    isManualCollapsed = false
                    isExpanded = true
                },
                shape = CircleShape,
                color = BrandForestDark.copy(alpha = 0.90f),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(bottom = 72.dp, end = 12.dp)
                    .size(52.dp)
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
                        contentDescription = "Open CoinBot",
                        tint = BrandCoralPink,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        // 2. Expanding AI Chatbot Window (top anchored near header, bottom extends down when keyboard is down)
        if (isExpanded || expandAnimatable.value > 0.001f) {
            val solidAiBg = Color(0xFF03120C)        // Much darker deep forest green background
            val solidAiSubSurface = Color(0xFF082218) // Rich dark emerald subsurface for bot bubbles & composer
            val solidAiHeaderBg = Color(0xFF020A07)   // Ultra dark forest green header
            val solidAiTextColor = Color.White

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .navigationBarsPadding()
                    .padding(
                        bottom = if (isKeyboardOpen) 4.dp else 12.dp,
                        end = 12.dp,
                        start = 12.dp,
                        top = headerSafeTop
                    )
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .widthIn(min = 320.dp, max = 420.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(animatedHeightFraction)
                        .graphicsLayer {
                            val p = expandAnimatable.value.coerceIn(0f, 1f)
                            val scale = 0.94f + (0.06f * p)
                            scaleX = scale
                            scaleY = scale
                            translationY = (1f - p) * with(density) { 18.dp.toPx() }
                            alpha = p
                            clip = true
                            shape = aiPanelShape
                        }
                        .background(solidAiBg, aiPanelShape)
                        .border(1.5.dp, BrandCoralPink.copy(alpha = 0.55f), aiPanelShape)
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                awaitFirstDown(requireUnconsumed = false)
                            }
                        }
                ) {
                    CompositionLocalProvider(LocalContentColor provides Color.White) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(solidAiBg)
                        ) {
                        // 1. Professional Chatbot Header (Ultra Dark Green)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(solidAiHeaderBg)
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
                                Text(
                                    "CoinBot",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                IconButton(
                                    onClick = { isManualCollapsed = !isManualCollapsed },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        if (targetHeightFraction > 0.65f) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                                        contentDescription = if (targetHeightFraction > 0.65f) "Half Screen" else "Expand to Header",
                                        tint = SlateGray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
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
                                        val currentMsgs = messages.toList()
                                        coroutineScope.launch(Dispatchers.IO) {
                                            if (currentMsgs.size > 2) {
                                                val newDigest = AiAssistantManager.synthesizeConversationDigest(
                                                    olderMessages = currentMsgs,
                                                    currentDigest = conversationDigest
                                                )
                                                if (newDigest.isNotBlank()) {
                                                    withContext(Dispatchers.Main) {
                                                        onUpdateDigest(newDigest)
                                                    }
                                                }
                                            }
                                        }
                                        messages.clear()
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
                                    onClick = { closeAiWindow() },
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
                                label = { Text(qp, fontSize = 10.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = solidAiBg,
                                    labelColor = Color.White.copy(alpha = 0.92f)
                                ),
                                border = BorderStroke(1.dp, BrandMintGreen.copy(alpha = 0.35f))
                            )
                        }
                    }

                    // 3. Chat Messages Area
                    LazyColumn(
                        state = chatListState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(solidAiBg)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (messages.isEmpty() && !isLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillParentMaxSize()
                                        .padding(horizontal = 24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = BrandForestDark,
                                            border = BorderStroke(1.dp, BrandCoralPink.copy(alpha = 0.5f)),
                                            modifier = Modifier.size(44.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.AutoAwesome,
                                                    contentDescription = null,
                                                    tint = BrandCoralPink,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "CoinBot is ready",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White.copy(alpha = 0.75f)
                                        )
                                        Text(
                                            text = "Ask a question, run a scenario, or share a document/screenshot.",
                                            fontSize = 11.sp,
                                            color = SlateGray.copy(alpha = 0.8f),
                                            lineHeight = 15.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
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
                                            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 4.dp),
                                            verticalArrangement = Arrangement.spacedBy(3.dp)
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
                                                lineHeight = 16.sp
                                            )
                                            if (msg.appliedActions.isNotEmpty()) {
                                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                                    msg.appliedActions.forEach { act ->
                                                        Surface(
                                                            shape = RoundedCornerShape(8.dp),
                                                            color = BrandMintGreen.copy(alpha = 0.18f),
                                                            border = BorderStroke(1.dp, BrandMintGreen.copy(alpha = 0.6f))
                                                        ) {
                                                            Row(
                                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
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
                                                lineHeight = 10.sp,
                                                color = if (isUser) BrandForestDark.copy(alpha = 0.70f) else SlateGray.copy(alpha = 0.85f),
                                                modifier = Modifier
                                                    .align(if (isUser) Alignment.End else Alignment.Start)
                                                    .padding(top = 1.dp, bottom = 2.dp)
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
                                        color = solidAiSubSurface,
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
                                                "CoinBot is analyzing & updating...",
                                                color = Color.White,
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

                    // 5. Streamlined Unified Composer (Clean Text Area + Minimalist Action Icons)
                    Surface(
                        color = solidAiSubSurface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(18.dp),
                            color = solidAiBg,
                            border = BorderStroke(1.dp, BrandMintGreen.copy(alpha = 0.28f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                // Clean Multi-line Text Input Field
                                OutlinedTextField(
                                    value = inputText,
                                    onValueChange = { inputText = it },
                                    placeholder = {
                                        Text(
                                            "Message CoinBot or say 'Set my income to $1800/wk'...",
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.50f)
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 4,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        cursorColor = BrandCoralPink
                                    )
                                )

                                // Sleek, Minimalist Action Row (Camera, Media, File | Voice, Send)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Media & Document Attachments: Camera, Gallery, File
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Camera Button
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
                                            shape = CircleShape,
                                            color = solidAiSubSurface,
                                            border = BorderStroke(1.dp, BrandWarmAmber.copy(alpha = 0.35f)),
                                            modifier = Modifier.size(34.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.PhotoCamera,
                                                    contentDescription = "Take Photo",
                                                    tint = BrandWarmAmber,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        // Media Gallery Picker Button
                                        Surface(
                                            onClick = { imagePickerLauncher.launch("image/*") },
                                            shape = CircleShape,
                                            color = solidAiSubSurface,
                                            border = BorderStroke(1.dp, BrandMintGreen.copy(alpha = 0.35f)),
                                            modifier = Modifier.size(34.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.AddPhotoAlternate,
                                                    contentDescription = "Choose from Gallery",
                                                    tint = BrandMintGreen,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        // File / Document Upload Button
                                        Surface(
                                            onClick = {
                                                filePickerLauncher.launch(
                                                    arrayOf("text/*", "application/json", "text/csv", "text/comma-separated-values", "*/*")
                                                )
                                            },
                                            shape = CircleShape,
                                            color = solidAiSubSurface,
                                            border = BorderStroke(1.dp, BrandSkyBlue.copy(alpha = 0.35f)),
                                            modifier = Modifier.size(34.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.AttachFile,
                                                    contentDescription = "Attach Document",
                                                    tint = BrandSkyBlue,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    // Voice Dictation & Send
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Voice Dictation Mic Button
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
                                                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to CoinBot...")
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
                                            shape = CircleShape,
                                            color = solidAiSubSurface,
                                            border = BorderStroke(1.dp, BrandCoralPink.copy(alpha = 0.35f)),
                                            modifier = Modifier.size(34.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.Mic,
                                                    contentDescription = "Voice Input",
                                                    tint = BrandCoralPink,
                                                    modifier = Modifier.size(17.dp)
                                                )
                                            }
                                        }

                                        // Send Button (Glows Coral Pink when ready to send)
                                        val isSendEnabled = !isLoading && (inputText.isNotBlank() || attachedBitmap != null || attachedFileContent != null)
                                        Surface(
                                            onClick = { sendCurrentMessage() },
                                            enabled = isSendEnabled,
                                            shape = CircleShape,
                                            color = if (isSendEnabled) BrandCoralPink else solidAiSubSurface,
                                            border = if (isSendEnabled) null else BorderStroke(1.dp, SlateGray.copy(alpha = 0.3f)),
                                            modifier = Modifier.size(34.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.AutoMirrored.Filled.Send,
                                                    contentDescription = "Send",
                                                    tint = if (isSendEnabled) BrandForestDark else SlateGray.copy(alpha = 0.6f),
                                                    modifier = Modifier.size(16.dp)
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
            }
        }
        }
    }
}

// ============================================================================
// AI CONTINUOUS LEARNING & MEMORY BANK DIALOG
// ============================================================================
@Composable
fun AiMemoryDialog(
    learnedFacts: List<LearnedUserFact>,
    conversationDigest: String,
    onDismiss: () -> Unit,
    onAddFact: (LearnedUserFact) -> Unit,
    onRemoveFact: (String) -> Unit,
    onClearAll: () -> Unit,
    onClearDigest: () -> Unit = {}
) {
    var newFactText by remember { mutableStateOf("") }
    var showClearConfirmation by remember { mutableStateOf(false) }
    var filterCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Goals", "Career", "Preferences", "Household", "Assets", "General")

    val filteredFacts = remember(learnedFacts, filterCategory) {
        if (filterCategory == "All") learnedFacts
        else learnedFacts.filter { it.category.equals(filterCategory, ignoreCase = true) }
    }

    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            title = { Text("Clear All Memory?", fontWeight = FontWeight.Bold, color = BrandCoralPink) },
            text = {
                Text(
                    "This will delete all learned user facts and conversation digests permanently. CoinBot will forget previous personal context.",
                    color = SlateGray,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAll()
                        onClearDigest()
                        showClearConfirmation = false
                    }
                ) {
                    Text("Delete Everything", color = BrandCoralPink, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmation = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF082218)
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = BrandCoralPink,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = BrandForestDark,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
                Column {
                    Text("CoinBot Memory Bank", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    Text(
                        "${learnedFacts.size} facts remembered across sessions",
                        fontSize = 11.sp,
                        color = BrandMintGreen
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "CoinBot continuously learns about your goals, habits, assets, family and preferences across all conversations.",
                    fontSize = 12.sp,
                    color = SlateGray
                )

                // Filter Category Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = filterCategory == cat,
                            onClick = { filterCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandCoralPink,
                                selectedLabelColor = BrandForestDark,
                                containerColor = Color(0xFF03120C),
                                labelColor = Color.White.copy(alpha = 0.8f)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = filterCategory == cat,
                                borderColor = if (filterCategory == cat) BrandCoralPink else SlateGray.copy(alpha = 0.3f)
                            )
                        )
                    }
                }

                // Add New Fact Manual Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF03120C), RoundedCornerShape(10.dp))
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = newFactText,
                        onValueChange = { newFactText = it },
                        placeholder = { Text("Teach CoinBot a fact...", fontSize = 12.sp, color = SlateGray) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, color = Color.White),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandCoralPink,
                            unfocusedBorderColor = SlateGray.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Button(
                        onClick = {
                            if (newFactText.isNotBlank()) {
                                onAddFact(
                                    LearnedUserFact(
                                        id = "fact_${System.currentTimeMillis()}",
                                        category = if (filterCategory != "All") filterCategory else "Goals",
                                        fact = newFactText.trim()
                                    )
                                )
                                newFactText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("Add", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Learned Facts List
                if (filteredFacts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .padding(vertical = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (learnedFacts.isEmpty()) "No memories yet. Chat with CoinBot or add one above!"
                            else "No memories in this category.",
                            fontSize = 12.sp,
                            color = SlateGray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredFacts, key = { it.id }) { factItem ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF03120C),
                                border = BorderStroke(1.dp, BrandMintGreen.copy(alpha = 0.2f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            factItem.category.uppercase(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandCoralPink
                                        )
                                        Text(
                                            factItem.fact,
                                            fontSize = 12.sp,
                                            color = Color.White.copy(alpha = 0.95f)
                                        )
                                    }
                                    IconButton(
                                        onClick = { onRemoveFact(factItem.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Forget",
                                            tint = SlateGray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Long-term conversation digest
                if (conversationDigest.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF03120C),
                        border = BorderStroke(1.dp, SlateGray.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                "CROSS-SESSION CONVERSATION DIGEST",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandMintGreen
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                conversationDigest,
                                fontSize = 11.sp,
                                color = SlateGray,
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = BrandMintGreen, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            if (learnedFacts.isNotEmpty() || conversationDigest.isNotBlank()) {
                TextButton(onClick = { showClearConfirmation = true }) {
                    Text("Clear All", color = BrandCoralPink)
                }
            }
        },
        containerColor = Color(0xFF082218)
    )
}

