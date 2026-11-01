package com.example.expnz20

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Build
import android.graphics.ImageDecoder
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.net.URLEncoder

import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneMultiFactorGenerator
import java.util.concurrent.TimeUnit
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

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

val SlateGray = Color(0xFF94A3B8)

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
    val propertyManagementFeePercent: Double = 5.0,
    // Module Toggles
    val showTotalWealth: Boolean = true,
    val showCashFlow: Boolean = true,
    val showProperties: Boolean = true,
    val showSuperAndShares: Boolean = true,
    val showOtherAssets: Boolean = true,
    val showLoans: Boolean = true,
    val showWhatIf: Boolean = true,
    // Custom Colors
    val colorWealthHex: Long = 0xFFFBBF24L, // Amber
    val colorCashFlowHex: Long = 0xFF34D399L, // Mint
    val colorPropertiesHex: Long = 0xFF38BDF8L, // Blue
    val colorSuperSharesHex: Long = 0xFFC084FCL, // Purple
    val colorOtherAssetsHex: Long = 0xFFE38391L, // Coral
    val colorLoansHex: Long = 0xFFF87171L, // Red
    val colorWhatIfHex: Long = 0xFF818CF8L // Indigo
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
    val iconName: String = "Default"
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
                    background = BrandForestDark,
                    surface = BrandForestSurface,
                    onBackground = Color.White,
                    onSurface = Color.White,
                    surfaceVariant = BrandForestVariant,
                    onSurfaceVariant = Color.White
                )
            } else {
                lightColorScheme(
                    primary = BrandForestDark,
                    onPrimary = Color.White,
                    secondary = BrandCoralPink,
                    tertiary = BrandWarmAmber,
                    error = BrandCoralRed,
                    background = Color(0xFFF2F7F5),
                    surface = Color(0xFFFFFFFF),
                    onBackground = BrandForestDark,
                    onSurface = BrandForestDark,
                    surfaceVariant = BrandForestSurface,
                    onSurfaceVariant = Color.White
                )
            }

            CompositionLocalProvider(LocalIsDarkTheme provides isDarkMode) {
                MaterialTheme(colorScheme = colorScheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
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

// ============================================================================
// FINANCIAL CALCULATIONS & AMORTIZATION ENGINE
// ============================================================================
fun calculateFortnightlyRepayment(originalLoan: Double, ratePa: Double, totalFortnights: Int = 780): Double {
    if (originalLoan <= 0.0) return 0.0
    if (ratePa <= 0.0) return originalLoan / totalFortnights
    val r = (ratePa / 100.0) / 26.0
    val factor = (1.0 + r).pow(totalFortnights.toDouble())
    return ((originalLoan * (r * factor)) / (factor - 1.0)).roundToInt().toDouble()
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

    var balance = config.startLoanBalance
    var accrued = 0.0
    val fnPayment = calculateFortnightlyRepayment(
        config.originalLoanAmount,
        config.interestRatePa,
        (config.originalTermYears * 26).coerceAtLeast(26)
    )

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

// ============================================================================
// MAIN APPLICATION COMPOSABLE
// ============================================================================
@OptIn(ExperimentalFoundationApi::class)
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
    val userDocRef = remember(currentUser.uid) {
        db.collection("users").document(currentUser.uid).collection("wealth_data").document("main")
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
            propertyManagementFeePercent = sharedPrefs.getFloat("property_management_fee_pct", 5f).toDouble(),
            showTotalWealth = sharedPrefs.getBoolean("mod_total_wealth", true),
            showCashFlow = sharedPrefs.getBoolean("mod_cash_flow", true),
            showProperties = sharedPrefs.getBoolean("mod_properties", true),
            showSuperAndShares = sharedPrefs.getBoolean("mod_super_shares", true),
            showOtherAssets = sharedPrefs.getBoolean("mod_other_assets", true),
            showLoans = sharedPrefs.getBoolean("mod_loans", true),
            showWhatIf = sharedPrefs.getBoolean("mod_what_if", true),
            colorWealthHex = sharedPrefs.getLong("color_wealth", 0xFFFBBF24L),
            colorCashFlowHex = sharedPrefs.getLong("color_cashflow", 0xFF34D399L),
            colorPropertiesHex = sharedPrefs.getLong("color_properties", 0xFF38BDF8L),
            colorSuperSharesHex = sharedPrefs.getLong("color_supershares", 0xFFC084FCL),
            colorOtherAssetsHex = sharedPrefs.getLong("color_otherassets", 0xFFE38391L),
            colorLoansHex = sharedPrefs.getLong("color_loans", 0xFFF87171L),
            colorWhatIfHex = sharedPrefs.getLong("color_whatif", 0xFF818CF8L)
        )
    }

    var appConfig by remember { mutableStateOf(loadAppConfigFromPrefs()) }
    var showColorPickerDialog by remember { mutableStateOf(false) }
    val geminiMessages = remember {
        mutableStateListOf(
            ChatMessage(
                text = "Hello ${appConfig.person1Name}. I am the EXPNZ AI Assistant. Looking at your EXPNZ Wealth overview, how can I assist you with your goals today?",
                isFromUser = false
            )
        )
    }

    val person1Color = Color(appConfig.person1ColorHex)
    val person2Color = Color(appConfig.person2ColorHex)

    fun updateFirestore(key: String, value: Any) {
        userDocRef.update(key, value).addOnFailureListener {
            userDocRef.set(hashMapOf(key to value), SetOptions.merge())
        }
    }

    fun saveAppConfig(newConfig: UserAppConfig) {
        appConfig = newConfig
        sharedPrefs.edit {
            putBoolean("setup_completed", newConfig.isSetupCompleted)
            putBoolean("is_couple_mode", newConfig.isCoupleMode)
            putString("person1_name", newConfig.person1Name)
            putString("person2_name", newConfig.person2Name)
            putLong("person1_color", newConfig.person1ColorHex)
            putLong("person2_color", newConfig.person2ColorHex)
            putString("property1_name", newConfig.property1Name)
            putString("property2_name", newConfig.property2Name)
            putString("property1_url", newConfig.property1Url)
            putString("property2_url", newConfig.property2Url)
            putBoolean("is_prop1_rented", newConfig.isProperty1Rented)
            putBoolean("is_prop2_rented", newConfig.isProperty2Rented)
            putBoolean("has_second_property", newConfig.hasSecondProperty)
            putFloat("primary_rent_weekly", newConfig.primaryRentWeekly.toFloat())
            putFloat("secondary_rent_weekly", newConfig.secondaryRentWeekly.toFloat())
            putFloat("property_management_fee_pct", newConfig.propertyManagementFeePercent.toFloat())
            putBoolean("mod_total_wealth", newConfig.showTotalWealth)
            putBoolean("mod_cash_flow", newConfig.showCashFlow)
            putBoolean("mod_properties", newConfig.showProperties)
            putBoolean("mod_super_shares", newConfig.showSuperAndShares)
            putBoolean("mod_other_assets", newConfig.showOtherAssets)
            putBoolean("mod_loans", newConfig.showLoans)
            putLong("color_wealth", newConfig.colorWealthHex)
            putLong("color_cashflow", newConfig.colorCashFlowHex)
            putLong("color_properties", newConfig.colorPropertiesHex)
            putLong("color_supershares", newConfig.colorSuperSharesHex)
            putLong("color_otherassets", newConfig.colorOtherAssetsHex)
            putLong("color_loans", newConfig.colorLoansHex)
        }
        val configMap = hashMapOf(
            "isSetupCompleted" to newConfig.isSetupCompleted,
            "isCoupleMode" to newConfig.isCoupleMode,
            "person1Name" to newConfig.person1Name,
            "person2Name" to newConfig.person2Name,
            "person1ColorHex" to newConfig.person1ColorHex,
            "person2ColorHex" to newConfig.person2ColorHex,
            "property1Name" to newConfig.property1Name,
            "property2Name" to newConfig.property2Name,
            "property1Url" to newConfig.property1Url,
            "property2Url" to newConfig.property2Url,
            "isProperty1Rented" to newConfig.isProperty1Rented,
            "isProperty2Rented" to newConfig.isProperty2Rented,
            "hasSecondProperty" to newConfig.hasSecondProperty,
            "primaryRentWeekly" to newConfig.primaryRentWeekly,
            "secondaryRentWeekly" to newConfig.secondaryRentWeekly,
            "propertyManagementFeePercent" to newConfig.propertyManagementFeePercent,
            "showTotalWealth" to newConfig.showTotalWealth,
            "showCashFlow" to newConfig.showCashFlow,
            "showProperties" to newConfig.showProperties,
            "showSuperAndShares" to newConfig.showSuperAndShares,
            "showOtherAssets" to newConfig.showOtherAssets,
            "showLoans" to newConfig.showLoans,
            "colorWealthHex" to newConfig.colorWealthHex,
            "colorCashFlowHex" to newConfig.colorCashFlowHex,
            "colorPropertiesHex" to newConfig.colorPropertiesHex,
            "colorSuperSharesHex" to newConfig.colorSuperSharesHex,
            "colorOtherAssetsHex" to newConfig.colorOtherAssetsHex,
            "colorLoansHex" to newConfig.colorLoansHex
        )
        updateFirestore("app_config", configMap)
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

    fun saveList(key: String, list: List<CashflowItem>) {
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

    // Sync from Firestore
    LaunchedEffect(currentUser.uid) {
        userDocRef.addSnapshotListener { snapshot, e ->
            if (e != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
            val data = snapshot.data ?: return@addSnapshotListener

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
                    propertyManagementFeePercent = (cfgMap["propertyManagementFeePercent"] as? Number)?.toDouble() ?: appConfig.propertyManagementFeePercent,
                    showTotalWealth = cfgMap["showTotalWealth"] as? Boolean ?: appConfig.showTotalWealth,
                    showCashFlow = cfgMap["showCashFlow"] as? Boolean ?: appConfig.showCashFlow,
                    showProperties = cfgMap["showProperties"] as? Boolean ?: appConfig.showProperties,
                    showSuperAndShares = cfgMap["showSuperAndShares"] as? Boolean ?: appConfig.showSuperAndShares,
                    showOtherAssets = cfgMap["showOtherAssets"] as? Boolean ?: appConfig.showOtherAssets,
                    showLoans = cfgMap["showLoans"] as? Boolean ?: appConfig.showLoans,
                    colorWealthHex = (cfgMap["colorWealthHex"] as? Number)?.toLong() ?: appConfig.colorWealthHex,
                    colorCashFlowHex = (cfgMap["colorCashFlowHex"] as? Number)?.toLong() ?: appConfig.colorCashFlowHex,
                    colorPropertiesHex = (cfgMap["colorPropertiesHex"] as? Number)?.toLong() ?: appConfig.colorPropertiesHex,
                    colorSuperSharesHex = (cfgMap["colorSuperSharesHex"] as? Number)?.toLong() ?: appConfig.colorSuperSharesHex,
                    colorOtherAssetsHex = (cfgMap["colorOtherAssetsHex"] as? Number)?.toLong() ?: appConfig.colorOtherAssetsHex,
                    colorLoansHex = (cfgMap["colorLoansHex"] as? Number)?.toLong() ?: appConfig.colorLoansHex
                )
                appConfig = remoteCfg
            }

            person1Income = (data["person1_inc"] as? Number)?.toDouble() ?: person1Income
            person2Income = (data["person2_inc"] as? Number)?.toDouble() ?: person2Income

            @Suppress("UNCHECKED_CAST")
            fun parseList(key: String, current: List<CashflowItem>): List<CashflowItem> {
                val rawList = data[key] as? List<Map<String, Any>> ?: return current
                return rawList.mapNotNull {
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

            prop1EstimatedValue = (data["prop1_market_value"] as? Number)?.toDouble() ?: prop1EstimatedValue
            prop2EstimatedValue = (data["prop2_market_value"] as? Number)?.toDouble() ?: prop2EstimatedValue

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

    // Build Dynamic Active Navigation Sections based on User Toggles
    val activeSections = remember(appConfig) {
        buildList {
            if (appConfig.showTotalWealth) add(AppSection.TOTAL_WEALTH)
            if (appConfig.showCashFlow) add(AppSection.CASH_FLOW)
            if (appConfig.showProperties) add(AppSection.PROPERTIES)
            if (appConfig.showSuperAndShares) add(AppSection.SUPER_SHARES)
            if (appConfig.showOtherAssets) add(AppSection.OTHER_ASSETS)
            val hideLoansDueToRent = appConfig.isProperty1Rented && (!appConfig.hasSecondProperty || appConfig.isProperty2Rented)
            if (appConfig.showLoans && !hideLoansDueToRent) add(AppSection.LOANS)
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

    // Dynamic Mortgage Repayments
    val prop1FortnightlyPmt = if (appConfig.showLoans) {
        calculateFortnightlyRepayment(prop1Loan.originalLoanAmount, prop1Loan.interestRatePa)
    } else 0.0
    val prop1WeeklyMortgage = -(prop1FortnightlyPmt / 2.0)

    val prop2FortnightlyPmt = if (appConfig.showLoans && appConfig.hasSecondProperty) {
        calculateFortnightlyRepayment(prop2Loan.originalLoanAmount, prop2Loan.interestRatePa)
    } else 0.0
    val prop2WeeklyMortgage = -(prop2FortnightlyPmt / 2.0)

    val p1AutoFortnights = getElapsedFortnightsSinceStart(prop1Loan.startDateStr)
    val p1Schedule = generateAmortizationSchedule(prop1Loan, 780)
    val p1CurrentRow = p1Schedule.getOrNull(p1AutoFortnights.coerceIn(0, p1Schedule.size - 1)) ?: p1Schedule.first()
    val prop1LiveBalance = if (appConfig.showLoans) p1CurrentRow.closingBalance else 0.0

    val p2AutoFortnights = getElapsedFortnightsSinceStart(prop2Loan.startDateStr)
    val p2Schedule = generateAmortizationSchedule(prop2Loan, 780)
    val p2CurrentRow = p2Schedule.getOrNull(p2AutoFortnights.coerceIn(0, p2Schedule.size - 1)) ?: p2Schedule.first()
    val prop2LiveBalance = if (appConfig.showLoans && appConfig.hasSecondProperty) p2CurrentRow.closingBalance else 0.0

    val netRent1 = appConfig.primaryRentWeekly * (1.0 - (appConfig.propertyManagementFeePercent / 100.0))
    val prop1HousingCost = if (appConfig.isProperty1Rented) -netRent1 else prop1WeeklyMortgage
    val prop1NetWeekly = prop1HousingCost + prop1Items.sumOf { it.weeklyAmount }

    val netRent2 = appConfig.secondaryRentWeekly * (1.0 - (appConfig.propertyManagementFeePercent / 100.0))
    val prop2HousingCost = if (appConfig.hasSecondProperty && appConfig.isProperty2Rented) -netRent2 else prop2WeeklyMortgage
    val prop2NetWeekly = if (appConfig.hasSecondProperty) (prop2HousingCost + prop2Items.sumOf { it.weeklyAmount }) else 0.0

    val combinedPropertyNet = if (appConfig.showProperties || appConfig.isProperty1Rented) (prop1NetWeekly + prop2NetWeekly) else 0.0
    val splitDivisor = if (appConfig.isCoupleMode) 2.0 else 1.0
    val perPersonPropertyShare = if (combinedPropertyNet < 0) combinedPropertyNet / splitDivisor else 0.0

    // Hoisted What-If Simulation Controls & Milestone State (Shared globally with AI Assistant)
    var whatIfHorizonYears by rememberSaveable { mutableIntStateOf(15) }
    var whatIfLifestyleAdjustmentWeekly by rememberSaveable { mutableFloatStateOf(0f) }
    var whatIfRateShockProp1 by rememberSaveable { mutableFloatStateOf(0f) }
    var whatIfRateShockProp2 by rememberSaveable { mutableFloatStateOf(0f) }
    var whatIfRentalGrowthWeekly by rememberSaveable { mutableFloatStateOf(0f) }

    val whatIfMilestoneList = remember {
        mutableStateListOf(
            LifeMilestoneBlock("m1", "Buy Investment Property", MilestoneCategory.PROPERTY, startYear = 2, durationYears = 15, weeklyCashflowImpact = -180.0, assetImpact = 650000.0, liabilityImpact = 520000.0, isEnabled = false),
            LifeMilestoneBlock("m2", "Sabbatical / Unpaid Leave (6 Mo)", MilestoneCategory.CAREER, startYear = 5, durationYears = 1, weeklyCashflowImpact = -600.0, assetImpact = 0.0, liabilityImpact = 0.0, isEnabled = false),
            LifeMilestoneBlock("m3", "Buy New Family SUV ($45k)", MilestoneCategory.PURCHASE, startYear = 3, durationYears = 1, weeklyCashflowImpact = 0.0, assetImpact = -45000.0, liabilityImpact = 0.0, isEnabled = false),
            LifeMilestoneBlock("m4", "Executive Career Pay Rise", MilestoneCategory.CAREER, startYear = 1, durationYears = 15, weeklyCashflowImpact = 250.0, assetImpact = 0.0, liabilityImpact = 0.0, isEnabled = true),
            LifeMilestoneBlock("m5", "Pay Extra $100/wk to Offset", MilestoneCategory.INVESTMENT, startYear = 1, durationYears = 15, weeklyCashflowImpact = -100.0, assetImpact = 0.0, liabilityImpact = 0.0, isEnabled = true)
        )
    }

    // Transparent Global AI Assistant Context Computation (Unmasked & Available across all tabs & scenarios)
    val prop1Val = prop1EstimatedValue
    val prop2Val = if (appConfig.hasSecondProperty) prop2EstimatedValue else 0.0
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

    val p1ExpensesListStr = person1Items.joinToString { "${it.name}: ${formatCurrency(kotlin.math.abs(it.weeklyAmount), 2)}/wk" }
    val p2ExpensesListStr = if (appConfig.isCoupleMode) person2Items.joinToString { "${it.name}: ${formatCurrency(kotlin.math.abs(it.weeklyAmount), 2)}/wk" } else ""

    // Compute What-If Projections for Global AI Context
    val whatIfTotalRateShockCost = run {
        val bPmt1 = calculateFortnightlyRepayment(prop1Loan.originalLoanAmount, prop1Loan.interestRatePa)
        val sPmt1 = calculateFortnightlyRepayment(prop1Loan.originalLoanAmount, prop1Loan.interestRatePa + whatIfRateShockProp1)
        val j1 = (sPmt1 - bPmt1).coerceAtLeast(0.0) / 2.0

        val bPmt2 = calculateFortnightlyRepayment(prop2Loan.originalLoanAmount, prop2Loan.interestRatePa)
        val sPmt2 = calculateFortnightlyRepayment(prop2Loan.originalLoanAmount, prop2Loan.interestRatePa + whatIfRateShockProp2)
        val j2 = (sPmt2 - bPmt2).coerceAtLeast(0.0) / 2.0

        j1 + (if (appConfig.hasSecondProperty) j2 else 0.0)
    }

    val whatIfBaseSurplusWeekly = run {
        val p1Exp = person1Items.sumOf { it.weeklyAmount }
        val p2Exp = if (appConfig.isCoupleMode) person2Items.sumOf { it.weeklyAmount } else 0.0
        val p1Surp = person1Income + p1Exp
        val p2Surp = if (appConfig.isCoupleMode) (person2Income + p2Exp) else 0.0
        p1Surp + p2Surp - prop1HousingCost - prop2HousingCost
    }

    val whatIfBaseAssets = exactTotalAssets
    val whatIfBaseLiabilities = exactTotalLiabilities
    val whatIfBaseNetWorth = exactNetWorth

    val whatIfPathASeries = buildList {
        var currNW = whatIfBaseNetWorth
        var currAssets = whatIfBaseAssets
        var currLiab = whatIfBaseLiabilities
        add(currNW)
        for (y in 1..whatIfHorizonYears) {
            val annualSurplus = whatIfBaseSurplusWeekly * 52.0
            val assetGrowth = (sharesVal + superVal) * 0.075 + (prop1Val + prop2Val) * 0.04
            val debtPaydown = (currLiab * 0.035).coerceAtMost(currLiab)
            currLiab = (currLiab - debtPaydown).coerceAtLeast(0.0)
            currAssets += annualSurplus + assetGrowth
            currNW = currAssets - currLiab
            add(currNW)
        }
    }

    var whatIfDeficitYear: Int? = null
    val whatIfPathBSeries = buildList {
        var currNW = whatIfBaseNetWorth
        var currAssets = whatIfBaseAssets
        var currLiab = whatIfBaseLiabilities
        var currLiquid = offsetsVal
        add(currNW)

        for (y in 1..whatIfHorizonYears) {
            var activeWeeklyDelta = whatIfLifestyleAdjustmentWeekly.toDouble() - whatIfTotalRateShockCost + whatIfRentalGrowthWeekly.toDouble()
            var activeAssetDelta = 0.0
            var activeLiabDelta = 0.0

            whatIfMilestoneList.filter { it.isEnabled }.forEach { m ->
                if (y >= m.startYear && y < m.startYear + m.durationYears) {
                    activeWeeklyDelta += m.weeklyCashflowImpact
                }
                if (y == m.startYear) {
                    activeAssetDelta += m.assetImpact
                    activeLiabDelta += m.liabilityImpact
                }
            }

            val annualNetSurplus = (whatIfBaseSurplusWeekly + activeWeeklyDelta) * 52.0
            val assetGrowth = (sharesVal + superVal) * 0.075 + (prop1Val + prop2Val) * 0.04
            val debtPaydown = (currLiab * 0.035).coerceAtMost(currLiab)

            currLiquid += annualNetSurplus
            if (currLiquid < 0 && whatIfDeficitYear == null) {
                whatIfDeficitYear = y
            }

            currLiab = (currLiab + activeLiabDelta - debtPaydown).coerceAtLeast(0.0)
            currAssets += annualNetSurplus + assetGrowth + activeAssetDelta
            currNW = currAssets - currLiab
            add(currNW)
        }
    }

    val whatIfEndPathANetWorth = whatIfPathASeries.lastOrNull() ?: whatIfBaseNetWorth
    val whatIfEndPathBNetWorth = whatIfPathBSeries.lastOrNull() ?: whatIfBaseNetWorth
    val whatIfNetDifference = whatIfEndPathBNetWorth - whatIfEndPathANetWorth

    val prop1ItemsStr = prop1Items.joinToString { "${it.name}: ${formatCurrency(kotlin.math.abs(it.weeklyAmount), 2)}/wk" }
    val prop2ItemsStr = if (appConfig.hasSecondProperty) prop2Items.joinToString { "${it.name}: ${formatCurrency(kotlin.math.abs(it.weeklyAmount), 2)}/wk" } else ""

    val detailedFinancialContext = buildString {
        append("EXPLICIT COMPUTED FINANCIAL TOTALS & UNMASKED GLOBAL APP DATA (DO NOT RECALCULATE): ")
        append("Exact Net Worth = ${formatCurrency(exactNetWorth, 2)}. ")
        append("Exact Total Assets = ${formatCurrency(exactTotalAssets, 2)} (Properties: ${formatCurrency(prop1Val + prop2Val, 0)}, Shares: ${formatCurrency(sharesVal, 0)}, Super: ${formatCurrency(superVal, 0)}, Other Assets: ${formatCurrency(otherVal, 0)}, Offset Accounts: ${formatCurrency(offsetsVal, 0)}). ")
        append("Exact Total Liabilities = ${formatCurrency(exactTotalLiabilities, 2)} (Property 1 Mortgage: ${formatCurrency(prop1LiveBalance, 0)}, Property 2 Mortgage: ${formatCurrency(prop2LiveBalance, 0)}). ")

        append("\n\n1. PERSONAL CASH FLOW TAB (UNMASKED): ")
        append("User (${appConfig.person1Name}): Income = ${formatCurrency(person1Income, 2)}/wk, Total Expenses = ${formatCurrency(kotlin.math.abs(p1TotalExpensesContext), 2)}/wk, Net Surplus = ${formatCurrency(p1SurplusContext, 2)}/wk. ")
        if (p1ExpensesListStr.isNotBlank()) append("User Itemized Expenses: $p1ExpensesListStr. ")

        if (appConfig.isCoupleMode) {
            append("Partner (${appConfig.person2Name}): Income = ${formatCurrency(person2Income, 2)}/wk, Total Expenses = ${formatCurrency(kotlin.math.abs(p2TotalExpensesContext), 2)}/wk, Net Surplus = ${formatCurrency(p2SurplusContext, 2)}/wk. ")
            if (p2ExpensesListStr.isNotBlank()) append("Partner Itemized Expenses: $p2ExpensesListStr. ")
            append("COMBINED WEEKLY NET SURPLUS = ${formatCurrency(combinedSurplusContext, 2)}/wk. ")
        }

        append("\n\n2. PROPERTIES TAB (UNMASKED): ")
        append("Property 1 (${appConfig.property1Name}): Market Value = ${formatCurrency(prop1Val, 0)}, Mortgage Balance = ${formatCurrency(prop1LiveBalance, 0)}, Net Equity = ${formatCurrency(prop1Val - prop1LiveBalance, 0)}, Status = ${if (appConfig.isProperty1Rented) "Rented (${formatCurrency(appConfig.primaryRentWeekly, 0)}/wk)" else "Owner Occupied"}. ")
        if (prop1ItemsStr.isNotBlank()) append("Property 1 Expenses: $prop1ItemsStr. ")

        if (appConfig.hasSecondProperty) {
            append("Property 2 (${appConfig.property2Name}): Market Value = ${formatCurrency(prop2Val, 0)}, Mortgage Balance = ${formatCurrency(prop2LiveBalance, 0)}, Net Equity = ${formatCurrency(prop2Val - prop2LiveBalance, 0)}, Status = ${if (appConfig.isProperty2Rented) "Rented (${formatCurrency(appConfig.secondaryRentWeekly, 0)}/wk)" else "Owner Occupied"}. ")
            if (prop2ItemsStr.isNotBlank()) append("Property 2 Expenses: $prop2ItemsStr. ")
        }

        append("\n\n3. LOANS & OFFSET TAB (UNMASKED AMORTIZATION): ")
        append("Property 1 Loan (${prop1Loan.name}): Original Loan = ${formatCurrency(prop1Loan.originalLoanAmount, 0)}, Start Balance = ${formatCurrency(prop1Loan.startLoanBalance, 0)}, Remaining Balance = ${formatCurrency(prop1LiveBalance, 0)}, Interest Rate = ${prop1Loan.interestRatePa}%, Offset Balance = ${formatCurrency(prop1Loan.offsetBalance, 0)}, Net Balance Charging Interest = ${formatCurrency(kotlin.math.max(0.0, prop1LiveBalance - prop1Loan.offsetBalance), 0)}, Fortnightly Repayment = ${formatCurrency(prop1FortnightlyPmt, 0)}, Weekly Equiv = ${formatCurrency(kotlin.math.abs(prop1WeeklyMortgage), 2)}. ")

        if (appConfig.hasSecondProperty) {
            append("Property 2 Loan (${prop2Loan.name}): Original Loan = ${formatCurrency(prop2Loan.originalLoanAmount, 0)}, Start Balance = ${formatCurrency(prop2Loan.startLoanBalance, 0)}, Remaining Balance = ${formatCurrency(prop2LiveBalance, 0)}, Interest Rate = ${prop2Loan.interestRatePa}%, Offset Balance = ${formatCurrency(prop2Loan.offsetBalance, 0)}, Net Balance Charging Interest = ${formatCurrency(kotlin.math.max(0.0, prop2LiveBalance - prop2Loan.offsetBalance), 0)}, Fortnightly Repayment = ${formatCurrency(prop2FortnightlyPmt, 0)}, Weekly Equiv = ${formatCurrency(kotlin.math.abs(prop2WeeklyMortgage), 2)}. ")
        }

        append("\n\n4. SUPER & SHARES TAB (UNMASKED): ")
        if (superAccountsList.isNotEmpty()) {
            append("Superannuation (Total ${formatCurrency(superVal, 0)}): ${superAccountsList.joinToString { "${it.fundName} (${it.owner}): ${formatCurrency(it.balance, 0)}" }}. ")
        }
        if (sharesList.isNotEmpty()) {
            val totalSharesCost = sharesList.sumOf { it.units * it.purchasePrice }
            val totalSharesGain = sharesVal - totalSharesCost
            append("Shares Portfolio (${sharesList.size} holdings, Total Market Value ${formatCurrency(sharesVal, 0)}, Cost Basis ${formatCurrency(totalSharesCost, 0)}, Net Gain/Loss ${formatCurrency(totalSharesGain, 0)}): ${sharesList.joinToString { "${it.code} (${it.owner}, ${it.units} units @ \$${it.currentPrice}, Purchase Price \$${it.purchasePrice}, Market Value ${formatCurrency(it.units * it.currentPrice, 0)}, Gain/Loss ${formatCurrency((it.currentPrice - it.purchasePrice) * it.units, 0)})" }}. ")
        }

        append("\n\n5. OTHER ASSETS TAB (UNMASKED): ")
        if (otherAssetsList.isNotEmpty()) {
            append("Other Assets (Total ${formatCurrency(otherVal, 0)}): ${otherAssetsList.joinToString { "${it.name} (${it.owner}): ${formatCurrency(it.value, 0)}" }}. ")
        }

        append("\n\n6. WHAT-IF SCENARIO SIMULATOR (ACTIVE PROJECTIONS & FUTURE ENTRIES): ")
        append("Active Timeframe = ${whatIfHorizonYears} Years. ")
        append("Extra Weekly Savings/Spending = ${if (whatIfLifestyleAdjustmentWeekly >= 0) "+$" else "-$"}${kotlin.math.abs(whatIfLifestyleAdjustmentWeekly)}/wk. ")
        append("Property 1 Rate Shock = +${whatIfRateShockProp1}%. ")
        if (appConfig.hasSecondProperty) append("Property 2 Rate Shock = +${whatIfRateShockProp2}%. ")
        if (whatIfRentalGrowthWeekly != 0f) append("Rental Growth = +$${whatIfRentalGrowthWeekly}/wk. ")

        val activeMilestones = whatIfMilestoneList.filter { it.isEnabled }
        val inactiveMilestones = whatIfMilestoneList.filter { !it.isEnabled }

        if (activeMilestones.isNotEmpty()) {
            append("Active Life Milestone Entries (${activeMilestones.size}): ${activeMilestones.joinToString { "${it.title} [Category: ${it.category.label}, Start Year: ${it.startYear}, Duration: ${it.durationYears} yrs, Weekly Cashflow Impact: ${if (it.weeklyCashflowImpact >= 0) "+" else ""}\$${it.weeklyCashflowImpact}/wk, Asset Impact: \$${it.assetImpact}, Liability Impact: \$${it.liabilityImpact}]" }}. ")
        } else {
            append("No active milestone entries selected. ")
        }
        if (inactiveMilestones.isNotEmpty()) {
            append("Available Inactive Milestone Options (${inactiveMilestones.size}): ${inactiveMilestones.joinToString { it.title }}. ")
        }

        append("Simulated Projection Results (${whatIfHorizonYears} Years): ")
        append("Path A (Status Quo Baseline Net Worth in ${whatIfHorizonYears} Yrs) = ${formatCurrency(whatIfEndPathANetWorth, 0)}. ")
        append("Path B (Simulated Future Plan Net Worth in ${whatIfHorizonYears} Yrs) = ${formatCurrency(whatIfEndPathBNetWorth, 0)}. ")
        append("Net Difference / Advantage = ${if (whatIfNetDifference >= 0) "+" else ""}${formatCurrency(whatIfNetDifference, 0)}. ")
        append("Cashflow Deficit Year = ${if (whatIfDeficitYear != null) "Year $whatIfDeficitYear (Cash reserves enter deficit)" else "None (Cash reserves positive)"}. ")
    }

    val headerSubtitle = remember(appConfig) {
        if (appConfig.isCoupleMode) {
            "EXPNZ • ${appConfig.person1Name.uppercase()} & ${appConfig.person2Name.uppercase()}"
        } else {
            "EXPNZ • ${appConfig.person1Name.uppercase()}"
        }
    }

    // HEADER WITH BRAND LOGO
    val topBarContent = @Composable {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BrandForestDark.copy(alpha = 0.92f),
            border = BorderStroke(1.dp, BrandCoralPink.copy(alpha = 0.22f)),
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = 10.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
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
                        color = Color.White.copy(alpha = 0.12f),
                        modifier = Modifier.size(40.dp)
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
                NavigationRail(
                    containerColor = BrandForestDark,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Start + WindowInsetsSides.Vertical))
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
                                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                                unselectedTextColor = Color.White.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    // Horizontal Pager allowing left/right swipes between tabs
                    HorizontalPager(
                        state = pagerState,
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
                                onUpdatePropertyValuation = { p1, p2 -> savePropertyValuations(p1, p2) },
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
                                initialHorizonYears = whatIfHorizonYears,
                                onHorizonYearsChange = { whatIfHorizonYears = it },
                                initialLifestyleAdjustmentWeekly = whatIfLifestyleAdjustmentWeekly,
                                onLifestyleAdjustmentWeeklyChange = { whatIfLifestyleAdjustmentWeekly = it },
                                initialRateShockProp1 = whatIfRateShockProp1,
                                onRateShockProp1Change = { whatIfRateShockProp1 = it },
                                initialRateShockProp2 = whatIfRateShockProp2,
                                onRateShockProp2Change = { whatIfRateShockProp2 = it },
                                initialRentalIncomeGrowthWeekly = whatIfRentalGrowthWeekly,
                                onRentalIncomeGrowthWeeklyChange = { whatIfRentalGrowthWeekly = it },
                                milestoneList = whatIfMilestoneList,
                                financialContextSummary = detailedFinancialContext
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
                                onSignOut = onSignOutUser
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
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                // Horizontal Pager for mobile layout
                HorizontalPager(
                    state = pagerState,
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
                            onUpdatePropertyValuation = { p1, p2 -> savePropertyValuations(p1, p2) },
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
                            initialHorizonYears = whatIfHorizonYears,
                            onHorizonYearsChange = { whatIfHorizonYears = it },
                            initialLifestyleAdjustmentWeekly = whatIfLifestyleAdjustmentWeekly,
                            onLifestyleAdjustmentWeeklyChange = { whatIfLifestyleAdjustmentWeekly = it },
                            initialRateShockProp1 = whatIfRateShockProp1,
                            onRateShockProp1Change = { whatIfRateShockProp1 = it },
                            initialRateShockProp2 = whatIfRateShockProp2,
                            onRateShockProp2Change = { whatIfRateShockProp2 = it },
                            initialRentalIncomeGrowthWeekly = whatIfRentalGrowthWeekly,
                            onRentalIncomeGrowthWeeklyChange = { whatIfRentalGrowthWeekly = it },
                            milestoneList = whatIfMilestoneList,
                            financialContextSummary = detailedFinancialContext
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
                            onSignOut = onSignOutUser
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

                // Fixed Footer Navigation Bar overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    NavigationBar(
                        containerColor = BrandForestDark.copy(alpha = 0.95f),
                        tonalElevation = 8.dp,
                        modifier = Modifier.navigationBarsPadding()
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
                                    indicatorColor = section.getColor(appConfig),
                                    unselectedIconColor = Color.White.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }
                }

                // Transparent Floating AI Assistant Bubble sitting above footer in bottom right of every tab
                val prop1Val = prop1EstimatedValue
                val prop2Val = if (appConfig.hasSecondProperty) prop2EstimatedValue else 0.0
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

                val p1ExpensesListStr = person1Items.joinToString { "${it.name}: ${formatCurrency(kotlin.math.abs(it.weeklyAmount), 2)}/wk" }
                val p2ExpensesListStr = if (appConfig.isCoupleMode) person2Items.joinToString { "${it.name}: ${formatCurrency(kotlin.math.abs(it.weeklyAmount), 2)}/wk" } else ""

                // Compute What-If Projections for Global AI Context
                val whatIfTotalRateShockCost = run {
                    val bPmt1 = calculateFortnightlyRepayment(prop1Loan.originalLoanAmount, prop1Loan.interestRatePa)
                    val sPmt1 = calculateFortnightlyRepayment(prop1Loan.originalLoanAmount, prop1Loan.interestRatePa + whatIfRateShockProp1)
                    val j1 = (sPmt1 - bPmt1).coerceAtLeast(0.0) / 2.0

                    val bPmt2 = calculateFortnightlyRepayment(prop2Loan.originalLoanAmount, prop2Loan.interestRatePa)
                    val sPmt2 = calculateFortnightlyRepayment(prop2Loan.originalLoanAmount, prop2Loan.interestRatePa + whatIfRateShockProp2)
                    val j2 = (sPmt2 - bPmt2).coerceAtLeast(0.0) / 2.0

                    j1 + (if (appConfig.hasSecondProperty) j2 else 0.0)
                }

                val whatIfBaseSurplusWeekly = run {
                    val p1Exp = person1Items.sumOf { it.weeklyAmount }
                    val p2Exp = if (appConfig.isCoupleMode) person2Items.sumOf { it.weeklyAmount } else 0.0
                    val p1Surp = person1Income + p1Exp
                    val p2Surp = if (appConfig.isCoupleMode) (person2Income + p2Exp) else 0.0
                    p1Surp + p2Surp - prop1HousingCost - prop2HousingCost
                }

                val whatIfBaseAssets = exactTotalAssets
                val whatIfBaseLiabilities = exactTotalLiabilities
                val whatIfBaseNetWorth = exactNetWorth

                val whatIfPathASeries = buildList {
                    var currNW = whatIfBaseNetWorth
                    var currAssets = whatIfBaseAssets
                    var currLiab = whatIfBaseLiabilities
                    add(currNW)
                    for (y in 1..whatIfHorizonYears) {
                        val annualSurplus = whatIfBaseSurplusWeekly * 52.0
                        val assetGrowth = (sharesVal + superVal) * 0.075 + (prop1Val + prop2Val) * 0.04
                        val debtPaydown = (currLiab * 0.035).coerceAtMost(currLiab)
                        currLiab = (currLiab - debtPaydown).coerceAtLeast(0.0)
                        currAssets += annualSurplus + assetGrowth
                        currNW = currAssets - currLiab
                        add(currNW)
                    }
                }

                var whatIfDeficitYear: Int? = null
                val whatIfPathBSeries = buildList {
                    var currNW = whatIfBaseNetWorth
                    var currAssets = whatIfBaseAssets
                    var currLiab = whatIfBaseLiabilities
                    var currLiquid = offsetsVal
                    add(currNW)

                    for (y in 1..whatIfHorizonYears) {
                        var activeWeeklyDelta = whatIfLifestyleAdjustmentWeekly.toDouble() - whatIfTotalRateShockCost + whatIfRentalGrowthWeekly.toDouble()
                        var activeAssetDelta = 0.0
                        var activeLiabDelta = 0.0

                        whatIfMilestoneList.filter { it.isEnabled }.forEach { m ->
                            if (y >= m.startYear && y < m.startYear + m.durationYears) {
                                activeWeeklyDelta += m.weeklyCashflowImpact
                            }
                            if (y == m.startYear) {
                                activeAssetDelta += m.assetImpact
                                activeLiabDelta += m.liabilityImpact
                            }
                        }

                        val annualNetSurplus = (whatIfBaseSurplusWeekly + activeWeeklyDelta) * 52.0
                        val assetGrowth = (sharesVal + superVal) * 0.075 + (prop1Val + prop2Val) * 0.04
                        val debtPaydown = (currLiab * 0.035).coerceAtMost(currLiab)

                        currLiquid += annualNetSurplus
                        if (currLiquid < 0 && whatIfDeficitYear == null) {
                            whatIfDeficitYear = y
                        }

                        currLiab = (currLiab + activeLiabDelta - debtPaydown).coerceAtLeast(0.0)
                        currAssets += annualNetSurplus + assetGrowth + activeAssetDelta
                        currNW = currAssets - currLiab
                        add(currNW)
                    }
                }

                val whatIfEndPathANetWorth = whatIfPathASeries.lastOrNull() ?: whatIfBaseNetWorth
                val whatIfEndPathBNetWorth = whatIfPathBSeries.lastOrNull() ?: whatIfBaseNetWorth
                val whatIfNetDifference = whatIfEndPathBNetWorth - whatIfEndPathANetWorth

                val prop1ItemsStr = prop1Items.joinToString { "${it.name}: ${formatCurrency(kotlin.math.abs(it.weeklyAmount), 2)}/wk" }
                val prop2ItemsStr = if (appConfig.hasSecondProperty) prop2Items.joinToString { "${it.name}: ${formatCurrency(kotlin.math.abs(it.weeklyAmount), 2)}/wk" } else ""

                val detailedFinancialContext = buildString {
                    append("EXPLICIT COMPUTED FINANCIAL TOTALS & UNMASKED GLOBAL APP DATA (DO NOT RECALCULATE): ")
                    append("Exact Net Worth = ${formatCurrency(exactNetWorth, 2)}. ")
                    append("Exact Total Assets = ${formatCurrency(exactTotalAssets, 2)} (Properties: ${formatCurrency(prop1Val + prop2Val, 0)}, Shares: ${formatCurrency(sharesVal, 0)}, Super: ${formatCurrency(superVal, 0)}, Other Assets: ${formatCurrency(otherVal, 0)}, Offset Accounts: ${formatCurrency(offsetsVal, 0)}). ")
                    append("Exact Total Liabilities = ${formatCurrency(exactTotalLiabilities, 2)} (Property 1 Mortgage: ${formatCurrency(prop1LiveBalance, 0)}, Property 2 Mortgage: ${formatCurrency(prop2LiveBalance, 0)}). ")

                    append("\n\n1. PERSONAL CASH FLOW TAB (UNMASKED): ")
                    append("User (${appConfig.person1Name}): Income = ${formatCurrency(person1Income, 2)}/wk, Total Expenses = ${formatCurrency(kotlin.math.abs(p1TotalExpensesContext), 2)}/wk, Net Surplus = ${formatCurrency(p1SurplusContext, 2)}/wk. ")
                    if (p1ExpensesListStr.isNotBlank()) append("User Itemized Expenses: $p1ExpensesListStr. ")

                    if (appConfig.isCoupleMode) {
                        append("Partner (${appConfig.person2Name}): Income = ${formatCurrency(person2Income, 2)}/wk, Total Expenses = ${formatCurrency(kotlin.math.abs(p2TotalExpensesContext), 2)}/wk, Net Surplus = ${formatCurrency(p2SurplusContext, 2)}/wk. ")
                        if (p2ExpensesListStr.isNotBlank()) append("Partner Itemized Expenses: $p2ExpensesListStr. ")
                        append("COMBINED WEEKLY NET SURPLUS = ${formatCurrency(combinedSurplusContext, 2)}/wk. ")
                    }

                    append("\n\n2. PROPERTIES TAB (UNMASKED): ")
                    append("Property 1 (${appConfig.property1Name}): Market Value = ${formatCurrency(prop1Val, 0)}, Mortgage Balance = ${formatCurrency(prop1LiveBalance, 0)}, Net Equity = ${formatCurrency(prop1Val - prop1LiveBalance, 0)}, Status = ${if (appConfig.isProperty1Rented) "Rented (${formatCurrency(appConfig.primaryRentWeekly, 0)}/wk)" else "Owner Occupied"}. ")
                    if (prop1ItemsStr.isNotBlank()) append("Property 1 Expenses: $prop1ItemsStr. ")

                    if (appConfig.hasSecondProperty) {
                        append("Property 2 (${appConfig.property2Name}): Market Value = ${formatCurrency(prop2Val, 0)}, Mortgage Balance = ${formatCurrency(prop2LiveBalance, 0)}, Net Equity = ${formatCurrency(prop2Val - prop2LiveBalance, 0)}, Status = ${if (appConfig.isProperty2Rented) "Rented (${formatCurrency(appConfig.secondaryRentWeekly, 0)}/wk)" else "Owner Occupied"}. ")
                        if (prop2ItemsStr.isNotBlank()) append("Property 2 Expenses: $prop2ItemsStr. ")
                    }

                    append("\n\n3. LOANS & OFFSET TAB (UNMASKED AMORTIZATION): ")
                    append("Property 1 Loan (${prop1Loan.name}): Original Loan = ${formatCurrency(prop1Loan.originalLoanAmount, 0)}, Start Balance = ${formatCurrency(prop1Loan.startLoanBalance, 0)}, Remaining Balance = ${formatCurrency(prop1LiveBalance, 0)}, Interest Rate = ${prop1Loan.interestRatePa}%, Offset Balance = ${formatCurrency(prop1Loan.offsetBalance, 0)}, Net Balance Charging Interest = ${formatCurrency(kotlin.math.max(0.0, prop1LiveBalance - prop1Loan.offsetBalance), 0)}, Fortnightly Repayment = ${formatCurrency(prop1FortnightlyPmt, 0)}, Weekly Equiv = ${formatCurrency(kotlin.math.abs(prop1WeeklyMortgage), 2)}. ")

                    if (appConfig.hasSecondProperty) {
                        append("Property 2 Loan (${prop2Loan.name}): Original Loan = ${formatCurrency(prop2Loan.originalLoanAmount, 0)}, Start Balance = ${formatCurrency(prop2Loan.startLoanBalance, 0)}, Remaining Balance = ${formatCurrency(prop2LiveBalance, 0)}, Interest Rate = ${prop2Loan.interestRatePa}%, Offset Balance = ${formatCurrency(prop2Loan.offsetBalance, 0)}, Net Balance Charging Interest = ${formatCurrency(kotlin.math.max(0.0, prop2LiveBalance - prop2Loan.offsetBalance), 0)}, Fortnightly Repayment = ${formatCurrency(prop2FortnightlyPmt, 0)}, Weekly Equiv = ${formatCurrency(kotlin.math.abs(prop2WeeklyMortgage), 2)}. ")
                    }

                    append("\n\n4. SUPER & SHARES TAB (UNMASKED): ")
                    if (superAccountsList.isNotEmpty()) {
                        append("Superannuation (Total ${formatCurrency(superVal, 0)}): ${superAccountsList.joinToString { "${it.fundName} (${it.owner}): ${formatCurrency(it.balance, 0)}" }}. ")
                    }
                    if (sharesList.isNotEmpty()) {
                        val totalSharesCost = sharesList.sumOf { it.units * it.purchasePrice }
                        val totalSharesGain = sharesVal - totalSharesCost
                        append("Shares Portfolio (${sharesList.size} holdings, Total Market Value ${formatCurrency(sharesVal, 0)}, Cost Basis ${formatCurrency(totalSharesCost, 0)}, Net Gain/Loss ${formatCurrency(totalSharesGain, 0)}): ${sharesList.joinToString { "${it.code} (${it.owner}, ${it.units} units @ \$${it.currentPrice}, Purchase Price \$${it.purchasePrice}, Market Value ${formatCurrency(it.units * it.currentPrice, 0)}, Gain/Loss ${formatCurrency((it.currentPrice - it.purchasePrice) * it.units, 0)})" }}. ")
                    }

                    append("\n\n5. OTHER ASSETS TAB (UNMASKED): ")
                    if (otherAssetsList.isNotEmpty()) {
                        append("Other Assets (Total ${formatCurrency(otherVal, 0)}): ${otherAssetsList.joinToString { "${it.name} (${it.owner}): ${formatCurrency(it.value, 0)}" }}. ")
                    }

                    append("\n\n6. WHAT-IF SCENARIO SIMULATOR (ACTIVE PROJECTIONS & FUTURE ENTRIES): ")
                    append("Active Timeframe = ${whatIfHorizonYears} Years. ")
                    append("Extra Weekly Savings/Spending Adjustment = ${if (whatIfLifestyleAdjustmentWeekly >= 0) "+$" else "-$"}${kotlin.math.abs(whatIfLifestyleAdjustmentWeekly)}/wk. ")
                    append("Property 1 Rate Shock = +${whatIfRateShockProp1}%. ")
                    if (appConfig.hasSecondProperty) append("Property 2 Rate Shock = +${whatIfRateShockProp2}%. ")
                    if (whatIfRentalGrowthWeekly != 0f) append("Rental Income Growth = +$${whatIfRentalGrowthWeekly}/wk. ")

                    val activeMilestones = whatIfMilestoneList.filter { it.isEnabled }
                    val inactiveMilestones = whatIfMilestoneList.filter { !it.isEnabled }

                    if (activeMilestones.isNotEmpty()) {
                        append("Active Life Milestone Entries (${activeMilestones.size}): ${activeMilestones.joinToString { "${it.title} [Category: ${it.category.label}, Start Year: ${it.startYear}, Duration: ${it.durationYears} yrs, Weekly Cashflow Impact: ${if (it.weeklyCashflowImpact >= 0) "+" else ""}\$${it.weeklyCashflowImpact}/wk, Asset Impact: \$${it.assetImpact}, Liability Impact: \$${it.liabilityImpact}]" }}. ")
                    } else {
                        append("No active milestone entries selected. ")
                    }
                    if (inactiveMilestones.isNotEmpty()) {
                        append("Available Inactive Milestone Options (${inactiveMilestones.size}): ${inactiveMilestones.joinToString { it.title }}. ")
                    }

                    append("Simulated Projection Results (${whatIfHorizonYears} Years): ")
                    append("Path A (Status Quo Baseline Net Worth in ${whatIfHorizonYears} Yrs) = ${formatCurrency(whatIfEndPathANetWorth, 0)}. ")
                    append("Path B (Simulated Future Plan Net Worth in ${whatIfHorizonYears} Yrs) = ${formatCurrency(whatIfEndPathBNetWorth, 0)}. ")
                    append("Net Difference / Advantage = ${if (whatIfNetDifference >= 0) "+" else ""}${formatCurrency(whatIfNetDifference, 0)}. ")
                    append("Cashflow Deficit Year = ${if (whatIfDeficitYear != null) "Year $whatIfDeficitYear (Cash reserves enter deficit)" else "None (Cash reserves remain positive)"}. ")
                }

                FloatingAiAssistantWidget(
                    financialContextSummary = detailedFinancialContext,
                    sharesList = sharesList,
                    personName = appConfig.person1Name,
                    messages = geminiMessages,
                    onAddImportedShares = { newParcels: List<ShareParcel> ->
                        val updated = sharesList + newParcels
                        saveSharesList(updated)
                    },
                    onClearShares = {
                        saveSharesList(emptyList())
                    }
                )
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

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = !isProperty1Rented,
                                onClick = { isProperty1Rented = false },
                                label = { Text("Own") }
                            )
                            FilterChip(
                                selected = isProperty1Rented,
                                onClick = { isProperty1Rented = true },
                                label = { Text("Rent") }
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
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        FilterChip(
                                            selected = !isProperty2Rented,
                                            onClick = { isProperty2Rented = false },
                                            label = { Text("Own") }
                                        )
                                        FilterChip(
                                            selected = isProperty2Rented,
                                            onClick = { isProperty2Rented = true },
                                            label = { Text("Rent") }
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
                                showLoans = showLoans
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
// SETTINGS SCREEN (WITH DYNAMIC MENU TOGGLES & PROFILE CUSTOMIZATION)
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
    onSignOut: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val user = auth.currentUser
    var showEditProfileDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 115.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
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
                        title = "Home Loans & Offset Amortization",
                        subtitle = "Fortnightly mortgage payoff schedule & offset simulator",
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
                        subtitle = "Parallel universe wealth modeling, life milestone stacking & rate shocks",
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
    var hasProp2 by remember { mutableStateOf(config.hasSecondProperty) }
    var prop2Name by remember { mutableStateOf(config.property2Name) }

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
                            hasSecondProperty = hasProp2,
                            property2Name = prop2Name.trim().ifBlank { "Investment Property" }
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
    onAddPerson1: (String, Double, String) -> Unit,
    onEditPerson1: (String, String, Double, String) -> Unit,
    onDeletePerson1: (String) -> Unit,
    onAddPerson2: (String, Double, String) -> Unit,
    onEditPerson2: (String, String, Double, String) -> Unit,
    onDeletePerson2: (String) -> Unit
) {
    var selectedPerson by remember { mutableIntStateOf(0) }
    LaunchedEffect(appConfig.isCoupleMode) {
        if (!appConfig.isCoupleMode) selectedPerson = 0
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 115.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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

        val showHousingCostInCashflow = (appConfig.showProperties || appConfig.isProperty1Rented) && perPersonPropertyShare != 0.0
        if (showHousingCostInCashflow) {
            item {
                val shareLabel = if (appConfig.isProperty1Rented) {
                    if (appConfig.isCoupleMode) "Rent & Housing Share (50% Split)" else "Weekly Rent & Housing Cost"
                } else {
                    if (appConfig.isCoupleMode) "Property Share (50% Net Property Cost)" else "Net Property Housing Cost"
                }
                ExpenseItemCard(
                    item = CashflowItem(
                        id = "locked_prop",
                        name = shareLabel,
                        weeklyAmount = perPersonPropertyShare,
                        isLockedMortgageOrPropertyLink = true,
                        iconName = "Home"
                    ),
                    accentColor = if (appConfig.isProperty1Rented) BrandCoralPink else BrandWarmAmber,
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
    onUpdatePropertyValuation: (Double, Double) -> Unit,
    onUpdateAppConfig: (UserAppConfig) -> Unit
) {
    var selectedProperty by remember { mutableIntStateOf(0) }
    LaunchedEffect(appConfig.hasSecondProperty) {
        if (!appConfig.hasSecondProperty) selectedProperty = 0
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<CashflowItem?>(null) }
    var showValuationDialog by remember { mutableStateOf(false) }
    var showAddressEditDialog by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf("Default") }

    fun maskVal(amt: Double, decimals: Int = 0): String = if (isWealthHidden) "$ ••••••" else formatCurrency(amt, decimals)



    val prop1Net = prop1WeeklyMortgage + prop1Items.sumOf { it.weeklyAmount }
    val prop2Net = prop2WeeklyMortgage + prop2Items.sumOf { it.weeklyAmount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 115.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
            val isRented = if (isProp1) appConfig.isProperty1Rented else appConfig.isProperty2Rented

            if (isRented) {
                var showRentEditDialog by remember { mutableStateOf(false) }
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
                                Text("Rental Summary", fontSize = 12.sp, color = SlateGray)
                            }
                            IconButton(onClick = { showRentEditDialog = true }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Rent", modifier = Modifier.size(16.dp), tint = BrandCoralPink)
                            }
                        }

                        var showMgmtFeeDialog by remember { mutableStateOf(false) }
                        val currentRent = if (selectedProperty == 0) appConfig.primaryRentWeekly else appConfig.secondaryRentWeekly

                        LoanDetailRow("Weekly Gross Rent", maskVal(currentRent, 2), highlightColor = BrandCoralPink)
                        LoanDetailRow("Management Fee (${appConfig.propertyManagementFeePercent}%)", maskVal(currentRent * (appConfig.propertyManagementFeePercent / 100.0), 2), highlightColor = BrandWarmAmber)
                        LoanDetailRow("Net Weekly Rent", maskVal(currentRent * (1.0 - appConfig.propertyManagementFeePercent / 100.0), 2), highlightColor = BrandMintGreen)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { showMgmtFeeDialog = true }) {
                                Text("Edit Mgmt Fee %", fontSize = 11.sp, color = BrandCoralPink)
                            }
                        }

                        if (showMgmtFeeDialog) {
                            EditSingleValueDialog(
                                title = "Property Management Fee %",
                                label = "Management Fee (%)",
                                initialValue = appConfig.propertyManagementFeePercent,
                                onDismiss = { showMgmtFeeDialog = false },
                                onConfirm = { newFee ->
                                    onUpdateAppConfig(appConfig.copy(propertyManagementFeePercent = newFee))
                                    showMgmtFeeDialog = false
                                }
                            )
                        }

                    }
                }

                if (showRentEditDialog) {
                    val currentRent = if (selectedProperty == 0) appConfig.primaryRentWeekly else appConfig.secondaryRentWeekly
                    EditSingleValueDialog(
                        title = "Update Weekly Rent",
                        label = "Weekly Rent ($)",
                        initialValue = currentRent,
                        onDismiss = { showRentEditDialog = false },
                        onConfirm = { newRent ->
                            if (selectedProperty == 0) {
                                onUpdateAppConfig(appConfig.copy(primaryRentWeekly = newRent))
                            } else {
                                onUpdateAppConfig(appConfig.copy(secondaryRentWeekly = newRent))
                            }
                            showRentEditDialog = false
                        }
                    )
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
                            IconButton(onClick = { showValuationDialog = true }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Value", modifier = Modifier.size(16.dp), tint = BrandCoralPink)
                            }
                        }

                        LoanDetailRow("Est. Market Value", maskVal(estVal, 0), highlightColor = BrandMintGreen)
                        if (appConfig.showLoans) {
                            LoanDetailRow("Synced Mortgage Balance", maskVal(liveBal, 0), highlightColor = BrandCoralPink)
                        }
                        LoanDetailRow("Calculated Net Equity", maskVal(netEquity, 0), highlightColor = BrandWarmAmber)

                    }
                }

                if (showValuationDialog) {
                    LivePropertyPriceSyncDialog(
                        propertyName = activePropName,
                        currentValuation = estVal,
                        onDismiss = { showValuationDialog = false },
                        onSyncValuation = { newVal ->
                            if (isProp1) onUpdatePropertyValuation(newVal, prop2MarketValue)
                            else onUpdatePropertyValuation(prop1MarketValue, newVal)
                            showValuationDialog = false
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
            val isRented = if (isProp1) appConfig.isProperty1Rented else appConfig.isProperty2Rented
            if (isRented) {
                var showRentEditDialog by remember { mutableStateOf(false) }
                val currentRent = if (isProp1) appConfig.primaryRentWeekly else appConfig.secondaryRentWeekly
                val netRent = currentRent * (1.0 - (appConfig.propertyManagementFeePercent / 100.0))
                ExpenseItemCard(
                    item = CashflowItem(
                        id = "rent_sync",
                        name = "Net Rent (after ${appConfig.propertyManagementFeePercent}% mgmt fee)",
                        weeklyAmount = -netRent,
                        isLockedMortgageOrPropertyLink = true,
                        iconName = "Home"
                    ),
                    accentColor = BrandCoralPink,
                    isWealthHidden = isWealthHidden,
                    onEdit = { showRentEditDialog = true },
                    onDelete = null
                )
                if (showRentEditDialog) {
                    EditSingleValueDialog(
                        title = "Update Weekly Rent Amount",
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
            } else if (appConfig.showLoans) {
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
    onUpdateProp2: (LoanConfig) -> Unit
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
        contentPadding = PaddingValues(top = 115.dp, bottom = 100.dp),
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
            val fnPmt = calculateFortnightlyRepayment(activeLoan.originalLoanAmount, activeLoan.interestRatePa)
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
// SMART WHAT-IF FINANCIAL SCENARIO SIMULATOR
// ============================================================================

enum class MilestoneCategory(val label: String, val icon: ImageVector, val defaultColor: Color) {
    PROPERTY("Real Estate & Housing", Icons.Default.HomeWork, BrandCoralPink),
    CAREER("Career & Leave", Icons.Default.Work, BrandMintGreen),
    PURCHASE("Major Purchase", Icons.Default.DirectionsCar, BrandWarmAmber),
    INVESTMENT("Wealth & Investing", Icons.AutoMirrored.Filled.TrendingUp, BrandSkyBlue)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatIfScenarioScreen(
    appConfig: UserAppConfig,
    person1Income: Double,
    person2Income: Double,
    person1Items: List<CashflowItem>,
    person2Items: List<CashflowItem>,
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
    initialHorizonYears: Int = 15,
    onHorizonYearsChange: (Int) -> Unit = {},
    initialLifestyleAdjustmentWeekly: Float = 0f,
    onLifestyleAdjustmentWeeklyChange: (Float) -> Unit = {},
    initialRateShockProp1: Float = 0f,
    onRateShockProp1Change: (Float) -> Unit = {},
    initialRateShockProp2: Float = 0f,
    onRateShockProp2Change: (Float) -> Unit = {},
    initialRentalIncomeGrowthWeekly: Float = 0f,
    onRentalIncomeGrowthWeeklyChange: (Float) -> Unit = {},
    milestoneList: SnapshotStateList<LifeMilestoneBlock> = remember { mutableStateListOf() },
    financialContextSummary: String = ""
) {
    fun maskVal(valAmt: Double, decimals: Int = 0): String {
        return if (isWealthHidden) "$ ••••••" else formatCurrency(valAmt, decimals)
    }

    // Local Simulation Controls
    var horizonYears by remember { mutableIntStateOf(initialHorizonYears) }
    var lifestyleAdjustmentWeekly by remember { mutableFloatStateOf(initialLifestyleAdjustmentWeekly) }
    var rateShockProp1 by remember { mutableFloatStateOf(initialRateShockProp1) }
    var rateShockProp2 by remember { mutableFloatStateOf(initialRateShockProp2) }
    var rentalIncomeGrowthWeekly by remember { mutableFloatStateOf(initialRentalIncomeGrowthWeekly) }

    LaunchedEffect(horizonYears, lifestyleAdjustmentWeekly, rateShockProp1, rateShockProp2, rentalIncomeGrowthWeekly) {
        onHorizonYearsChange(horizonYears)
        onLifestyleAdjustmentWeeklyChange(lifestyleAdjustmentWeekly)
        onRateShockProp1Change(rateShockProp1)
        onRateShockProp2Change(rateShockProp2)
        onRentalIncomeGrowthWeeklyChange(rentalIncomeGrowthWeekly)
    }

    // Baseline Financial Calculations
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
    val baseNetWorth = totalAssets - totalLiabilities

    val p1Expenses = person1Items.sumOf { it.weeklyAmount }
    val p2Expenses = if (appConfig.isCoupleMode) person2Items.sumOf { it.weeklyAmount } else 0.0
    val netWeeklyRent1 = appConfig.primaryRentWeekly * (1.0 - (appConfig.propertyManagementFeePercent / 100.0))
    val prop1Housing = if (appConfig.isProperty1Rented) -netWeeklyRent1 else prop1WeeklyMortgage
    val prop2Housing = if (appConfig.hasSecondProperty && appConfig.isProperty2Rented) 0.0 else prop2WeeklyMortgage

    val p1Surplus = person1Income + p1Expenses
    val p2Surplus = if (appConfig.isCoupleMode) (person2Income + p2Expenses) else 0.0
    val baseSurplusWeekly = p1Surplus + p2Surplus - prop1Housing - prop2Housing

    // Simulation Controls
    var showGuide by remember { mutableStateOf(true) }
    var showAddMilestoneDialog by remember { mutableStateOf(false) }
    var milestoneToEdit by remember { mutableStateOf<LifeMilestoneBlock?>(null) }
    var whatIfChatInput by remember { mutableStateOf("") }
    val whatIfChatHistory = remember { mutableStateListOf<ChatMessage>() }
    var isWhatIfAiLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Rate Shock Math (Per Loan)
    val baseRate1 = prop1Loan.interestRatePa
    val shockedRate1 = baseRate1 + rateShockProp1
    val basePmt1 = calculateFortnightlyRepayment(prop1Loan.originalLoanAmount, baseRate1)
    val shockedPmt1 = calculateFortnightlyRepayment(prop1Loan.originalLoanAmount, shockedRate1)
    val jump1 = (shockedPmt1 - basePmt1).coerceAtLeast(0.0) / 2.0

    val baseRate2 = prop2Loan.interestRatePa
    val shockedRate2 = baseRate2 + rateShockProp2
    val basePmt2 = calculateFortnightlyRepayment(prop2Loan.originalLoanAmount, baseRate2)
    val shockedPmt2 = calculateFortnightlyRepayment(prop2Loan.originalLoanAmount, shockedRate2)
    val jump2 = (shockedPmt2 - basePmt2).coerceAtLeast(0.0) / 2.0

    val totalWeeklyRateShockCost = jump1 + (if (appConfig.hasSecondProperty) jump2 else 0.0)

    // Multi-Year Simulation Projections
    val pathASeries = remember(baseNetWorth, baseSurplusWeekly, horizonYears, totalSharesVal, totalSuperVal, totalPropertyVal, totalLiabilities) {
        val series = mutableListOf<Double>()
        var currNW = baseNetWorth
        var currAssets = totalAssets
        var currLiab = totalLiabilities
        series.add(currNW)
        for (y in 1..horizonYears) {
            val annualSurplus = baseSurplusWeekly * 52.0
            val assetGrowth = (totalSharesVal + totalSuperVal) * 0.075 + (totalPropertyVal * 0.04)
            val debtPaydown = (currLiab * 0.035).coerceAtMost(currLiab)
            currLiab = (currLiab - debtPaydown).coerceAtLeast(0.0)
            currAssets += annualSurplus + assetGrowth
            currNW = currAssets - currLiab
            series.add(currNW)
        }
        series
    }

    val (pathBSeries, deficitYear) = remember(
        baseNetWorth, baseSurplusWeekly, horizonYears, lifestyleAdjustmentWeekly, totalWeeklyRateShockCost, rentalIncomeGrowthWeekly,
        milestoneList.map { Pair(it.id, it.isEnabled) }
    ) {
        val series = mutableListOf<Double>()
        var currNW = baseNetWorth
        var currAssets = totalAssets
        var currLiab = totalLiabilities
        var currLiquid = totalOffsets
        var foundDeficitYear: Int? = null

        series.add(currNW)

        for (y in 1..horizonYears) {
            var activeWeeklyCashflowDelta = lifestyleAdjustmentWeekly.toDouble() - totalWeeklyRateShockCost + rentalIncomeGrowthWeekly.toDouble()
            var activeAssetDelta = 0.0
            var activeLiabDelta = 0.0

            milestoneList.filter { it.isEnabled }.forEach { m ->
                if (y >= m.startYear && y < m.startYear + m.durationYears) {
                    activeWeeklyCashflowDelta += m.weeklyCashflowImpact
                }
                if (y == m.startYear) {
                    activeAssetDelta += m.assetImpact
                    activeLiabDelta += m.liabilityImpact
                }
            }

            val annualNetSurplus = (baseSurplusWeekly + activeWeeklyCashflowDelta) * 52.0
            val assetGrowth = (totalSharesVal + totalSuperVal) * 0.075 + (totalPropertyVal * 0.04)
            val debtPaydown = (currLiab * 0.035).coerceAtMost(currLiab)

            currLiquid += annualNetSurplus
            if (currLiquid < 0 && foundDeficitYear == null) {
                foundDeficitYear = y
            }

            currLiab = (currLiab + activeLiabDelta - debtPaydown).coerceAtLeast(0.0)
            currAssets += annualNetSurplus + assetGrowth + activeAssetDelta
            currNW = currAssets - currLiab
            series.add(currNW)
        }

        Pair(series, foundDeficitYear)
    }

    val endPathANetWorth = pathASeries.lastOrNull() ?: baseNetWorth
    val endPathBNetWorth = pathBSeries.lastOrNull() ?: baseNetWorth
    val netWorthDifference = endPathBNetWorth - endPathANetWorth

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 115.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // SCENARIO PRESET TILES
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Preset Scenario Explorer", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Card(
                            onClick = {
                                lifestyleAdjustmentWeekly = 150f
                                rateShockProp1 = 0f
                                rateShockProp2 = 0f
                                rentalIncomeGrowthWeekly = 0f
                                milestoneList.clear()
                            },
                            modifier = Modifier.size(110.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BrandMintGreen)
                        ) {
                            Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = BrandForestDark, modifier = Modifier.size(28.dp))
                                Text("Aggressive Saver", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandForestDark, lineHeight = 14.sp)
                            }
                        }
                    }
                    item {
                        Card(
                            onClick = {
                                lifestyleAdjustmentWeekly = 0f
                                rateShockProp1 = 0f
                                rateShockProp2 = 0f
                                rentalIncomeGrowthWeekly = 0f
                                milestoneList.clear()
                                milestoneList.add(
                                    LifeMilestoneBlock("m_prop", "Investment Property", MilestoneCategory.PROPERTY, startYear = 2, durationYears = 15, weeklyCashflowImpact = -180.0, assetImpact = 650000.0, liabilityImpact = 520000.0, isEnabled = true)
                                )
                            },
                            modifier = Modifier.size(110.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BrandCoralPink)
                        ) {
                            Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                Icon(Icons.Default.HomeWork, contentDescription = null, tint = BrandForestDark, modifier = Modifier.size(28.dp))
                                Text("Investment Property", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandForestDark, lineHeight = 14.sp)
                            }
                        }
                    }
                    item {
                        Card(
                            onClick = {
                                lifestyleAdjustmentWeekly = 0f
                                rateShockProp1 = 2f
                                rateShockProp2 = 2f
                                rentalIncomeGrowthWeekly = 0f
                                milestoneList.clear()
                            },
                            modifier = Modifier.size(110.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = BrandWarmAmber)
                        ) {
                            Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = BrandForestDark, modifier = Modifier.size(28.dp))
                                Text("Rate Hike (+2%)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandForestDark, lineHeight = 14.sp)
                            }
                        }
                    }
                    item {
                        Card(
                            onClick = {
                                lifestyleAdjustmentWeekly = 0f
                                rateShockProp1 = 0f
                                rateShockProp2 = 0f
                                rentalIncomeGrowthWeekly = 0f
                                milestoneList.clear()
                            },
                            modifier = Modifier.size(110.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = SlateGray, modifier = Modifier.size(28.dp))
                                Text("Reset All", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, lineHeight = 14.sp)
                            }
                        }
                    }
                }
            }
        }

        // WELCOME GUIDE OR HELP BUTTON
        item {
            if (showGuide) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandForestDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = BrandCoralPink, modifier = Modifier.size(20.dp))
                                Text("HOW THE WHAT-IF SIMULATOR WORKS", color = BrandCoralPink, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            TextButton(onClick = { showGuide = false }) {
                                Text("Got it, hide", fontSize = 11.sp, color = BrandMintGreen)
                            }
                        }
                        Text(
                            text = "This simulator models your future Net Worth over time by comparing your current Status Quo (Path A) against your New Plan (Path B).\n\n" +
                                    "• Use the Timeframe & Mortgage Interest Levers to test economic changes.\n" +
                                    "• Add Life Milestones to model property purchases, career changes, or major expenses. Each milestone automatically deducts/adds to your weekly surplus and updates your compounding wealth timeline starting from your chosen future year.",
                            color = Color.White,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { showGuide = true }) {
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Show Simulator Guide", fontSize = 12.sp)
                    }
                }
            }
        }

        // AI COACH INSIGHT BANNER
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (deficitYear != null) BrandCoralPink.copy(alpha = 0.15f)
                    else if (netWorthDifference >= 0) BrandMintGreen.copy(alpha = 0.15f)
                    else BrandWarmAmber.copy(alpha = 0.15f)
                ),
                border = BorderStroke(
                    1.5.dp,
                    if (deficitYear != null) BrandCoralPink
                    else if (netWorthDifference >= 0) BrandMintGreen
                    else BrandWarmAmber
                )
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                if (deficitYear != null) Icons.Default.Warning
                                else if (netWorthDifference >= 0) Icons.Default.CheckCircle
                                else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (deficitYear != null) BrandCoralPink
                                else if (netWorthDifference >= 0) BrandMintGreen
                                else BrandWarmAmber,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                "AI FINANCIAL COACH ADVICE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (deficitYear != null) BrandCoralPink
                                else if (netWorthDifference >= 0) BrandMintGreen
                                else BrandWarmAmber,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Text(
                        text = when {
                            deficitYear != null -> "🚨 Cash Flow Alert: In Year $deficitYear, active life events cause your cash reserves to enter a deficit. Consider spacing out major expenses."
                            netWorthDifference > 100000 -> "💡 Great News! Your selected plan leaves you +${maskVal(netWorthDifference, 0)} richer by Year $horizonYears compared to your current status quo!"
                            lifestyleAdjustmentWeekly > 50 -> "📈 Excellent savings habit! Stashing an extra $${lifestyleAdjustmentWeekly.toInt()}/wk significantly boosts your long-term compounding."
                            else -> "✨ Tip: Toggle life milestone events below or adjust your weekly savings slider to test different financial futures!"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // CONVERSATIONAL WHAT-IF AI CHAT COMPANION CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrandCoralPink, modifier = Modifier.size(20.dp))
                        Text("Conversational What-If AI Companion", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Text("Ask complex financial questions in plain English (e.g. 'What happens if I buy a $40k car next year while increasing my extra mortgage repayments by $100 a week?'):", fontSize = 12.sp, color = SlateGray)

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
                            placeholder = { Text("Ask Gemini a What-If question...", fontSize = 12.sp) },
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
                                            else list.joinToString { "'${it.title}' (Start Yr ${it.startYear}, Duration ${it.durationYears}Y, Assets Impact: ${formatCurrency(it.assetImpact, 0)}, Liability Impact: ${formatCurrency(it.liabilityImpact, 0)}, Weekly Cashflow Impact: ${formatCurrency(it.weeklyCashflowImpact, 2)}/wk)" }
                                        }

                                        val whatIfContextSummary = buildString {
                                            append("EXPLICIT COMPUTED WHAT-IF SIMULATION DATA (DO NOT RECALCULATE): ")
                                            append("Time Horizon = ${horizonYears} Years. ")
                                            append("Baseline Net Worth = ${formatCurrency(baseNetWorth, 2)}. ")
                                            append("Baseline Weekly Surplus = ${formatCurrency(baseSurplusWeekly, 2)}/wk. ")
                                            append("Baseline Projected Net Worth in ${horizonYears}Y (Path A) = ${formatCurrency(pathAEndNW, 2)}. ")
                                            append("Simulated Projected Net Worth in ${horizonYears}Y (Path B) = ${formatCurrency(pathBEndNW, 2)}. ")
                                            append("Net Wealth Impact (Path B vs Path A) = ${if (nwImpact >= 0) "+" else ""}${formatCurrency(nwImpact, 2)}. ")
                                            if (lifestyleAdjustmentWeekly != 0f) {
                                                append("Lifestyle Spending Adjustment = ${formatCurrency(lifestyleAdjustmentWeekly.toDouble(), 2)}/wk. ")
                                            }
                                            if (totalWeeklyRateShockCost > 0) {
                                                append("Mortgage Interest Rate Shock Cost = -${formatCurrency(totalWeeklyRateShockCost, 2)}/wk. ")
                                            }
                                            if (rentalIncomeGrowthWeekly != 0f) {
                                                append("Rental Income Growth Adjustment = +${formatCurrency(rentalIncomeGrowthWeekly.toDouble(), 2)}/wk. ")
                                            }
                                            if (deficitYear != null) {
                                                append("CRITICAL WARNING: Cashflow Deficit projected in Year $deficitYear! ")
                                            }
                                            append("Active Life Milestones in Scenario: $activeMilestonesStr. ")
                                            append("Total Assets = ${formatCurrency(totalAssets, 0)} (Properties: ${formatCurrency(totalPropertyVal, 0)}, Shares: ${formatCurrency(totalSharesVal, 0)}, Super: ${formatCurrency(totalSuperVal, 0)}, Offsets: ${formatCurrency(totalOffsets, 0)}). ")
                                            append("Total Liabilities = ${formatCurrency(totalLiabilities, 0)}.")
                                        }

                                        val reply = AiAssistantManager.askGeminiAssistant(q, financialContextSummary.ifBlank { whatIfContextSummary })
                                        whatIfChatHistory.add(ChatMessage(reply, false))
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

        // SIMULATOR CONTROLS (TIME HORIZON & SLIDERS)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("1. Projection Timeframe", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("How far into the future to simulate", fontSize = 11.sp, color = SlateGray)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 10, 15, 20, 30).forEach { yrs ->
                            FilterChip(
                                selected = horizonYears == yrs,
                                onClick = { onHorizonYearsChange(yrs) },
                                label = { Text("${yrs}Y", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }

                    HorizontalDivider()

                    // Weekly Savings / Spending Slider
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("2. Extra Weekly Savings / Spending", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                if (lifestyleAdjustmentWeekly == 0f) "$0 / wk"
                                else if (lifestyleAdjustmentWeekly > 0) "+$${lifestyleAdjustmentWeekly.toInt()} / wk saved"
                                else "-$${abs(lifestyleAdjustmentWeekly.toInt())} / wk spent",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (lifestyleAdjustmentWeekly > 0) BrandMintGreen else BrandCoralPink
                            )
                        }
                        Text("Slide right to test saving extra money each week, or left to test spending more.", fontSize = 11.sp, color = SlateGray)
                        Slider(
                            value = lifestyleAdjustmentWeekly,
                            onValueChange = { onLifestyleAdjustmentWeeklyChange(it) },
                            valueRange = -200f..300f,
                            steps = 10,
                            colors = SliderDefaults.colors(
                                thumbColor = BrandCoralPink,
                                activeTrackColor = BrandCoralPink
                            )
                        )
                    }

                    HorizontalDivider()

                    // Interest Rate Hike Sliders (Per Loan)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("3. Mortgage Interest Rate Levers", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Test how interest rate changes on your home loans impact your wealth over time.", fontSize = 11.sp, color = SlateGray)

                        // Loan 1
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Loan 1: ${prop1Loan.name}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    if (rateShockProp1 == 0f) "Base (${String.format(Locale.getDefault(), "%.2f", baseRate1)}%)"
                                    else "+${String.format(Locale.getDefault(), "%.1f", rateShockProp1)}% (${String.format(Locale.getDefault(), "%.2f", shockedRate1)}%)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (rateShockProp1 > 0) BrandCoralPink else BrandMintGreen
                                )
                            }
                            Slider(
                                value = rateShockProp1,
                                onValueChange = { onRateShockProp1Change(it) },
                                valueRange = 0f..4f,
                                steps = 8,
                                colors = SliderDefaults.colors(thumbColor = BrandMintGreen, activeTrackColor = BrandMintGreen)
                            )
                        }

                        // Loan 2 (if present)
                        if (appConfig.hasSecondProperty) {
                            Spacer(Modifier.height(4.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Loan 2: ${prop2Loan.name}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        if (rateShockProp2 == 0f) "Base (${String.format(Locale.getDefault(), "%.2f", baseRate2)}%)"
                                        else "+${String.format(Locale.getDefault(), "%.1f", rateShockProp2)}% (${String.format(Locale.getDefault(), "%.2f", shockedRate2)}%)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (rateShockProp2 > 0) BrandCoralPink else BrandMintGreen
                                    )
                                }
                                Slider(
                                    value = rateShockProp2,
                                    onValueChange = { onRateShockProp2Change(it) },
                                    valueRange = 0f..4f,
                                    steps = 8,
                                    colors = SliderDefaults.colors(thumbColor = BrandMintGreen, activeTrackColor = BrandMintGreen)
                                )
                            }
                        }
                    }
                }
            }
        }

        // SIDE-BY-SIDE NET WORTH COMPARISON CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Your Projected Net Worth in $horizonYears Years", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Path A
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("CURRENT PATH", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = SlateGray)
                                Text(maskVal(endPathANetWorth, 0), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("Status Quo Trajectory", fontSize = 11.sp, color = SlateGray)
                            }
                        }

                        // Path B
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = BrandCoralPink.copy(alpha = 0.15f)),
                            border = BorderStroke(1.dp, BrandCoralPink)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("YOUR NEW PLAN", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = BrandCoralPink)
                                Text(maskVal(endPathBNetWorth, 0), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = BrandCoralPink)
                                Text("With Sliders & Milestones", fontSize = 11.sp, color = BrandCoralPink)
                            }
                        }
                    }

                    // Difference pill
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = if (netWorthDifference >= 0) BrandMintGreen.copy(alpha = 0.15f) else BrandCoralPink.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Net Advantage in $horizonYears Years:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                "${if (netWorthDifference >= 0) "+" else ""}${maskVal(netWorthDifference, 0)}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = if (netWorthDifference >= 0) BrandMintGreen else BrandCoralPink
                            )
                        }
                    }
                }
            }
        }

        // LIFE MILESTONES STACKER CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
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
                            Text("Life Milestone Events", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Toggle, edit or add events in your plan", fontSize = 12.sp, color = SlateGray)
                        }
                        Button(
                            onClick = { showAddMilestoneDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Add Milestone", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (milestoneList.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.EventNote, contentDescription = null, tint = SlateGray, modifier = Modifier.size(32.dp))
                                Text("No milestones added yet", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("Tap 'Add Milestone' above to model custom life events, property purchases, or career shifts.", fontSize = 12.sp, color = SlateGray, textAlign = TextAlign.Center)
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
                                    modifier = Modifier.padding(14.dp),
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
                                        Column {
                                            Text(milestone.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text(
                                                "Starts Year ${milestone.startYear} • ${if (milestone.weeklyCashflowImpact != 0.0) "${if (milestone.weeklyCashflowImpact > 0) "+" else ""}$${milestone.weeklyCashflowImpact.toInt()}/wk" else "Capital Outlay"}",
                                                fontSize = 11.sp,
                                                color = SlateGray
                                            )
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
    }

    if (showAddMilestoneDialog) {
        AddCustomMilestoneDialog(
            onDismiss = { showAddMilestoneDialog = false },
            onConfirm = { title, cat, startY, weeklyCash, assetAmt, liabAmt ->
                milestoneList.add(
                    LifeMilestoneBlock(
                        id = "m_${System.currentTimeMillis()}",
                        title = title,
                        category = cat,
                        startYear = startY,
                        durationYears = 10,
                        weeklyCashflowImpact = weeklyCash,
                        assetImpact = assetAmt,
                        liabilityImpact = liabAmt,
                        isEnabled = true
                    )
                )
                showAddMilestoneDialog = false
            }
        )
    }

    if (milestoneToEdit != null) {
        EditCustomMilestoneDialog(
            milestone = milestoneToEdit!!,
            onDismiss = { milestoneToEdit = null },
            onConfirm = { title, cat, startY, weeklyCash, assetAmt, liabAmt ->
                val idx = milestoneList.indexOfFirst { it.id == milestoneToEdit!!.id }
                if (idx >= 0) {
                    milestoneList[idx] = milestoneToEdit!!.copy(
                        title = title,
                        category = cat,
                        startYear = startY,
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
fun AddCustomMilestoneDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, MilestoneCategory, Int, Double, Double, Double) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(MilestoneCategory.PROPERTY) }
    var startYearStr by remember { mutableStateOf("3") }
    var weeklyCashflowStr by remember { mutableStateOf("-150") }
    var assetImpactStr by remember { mutableStateOf("0") }
    var liabilityImpactStr by remember { mutableStateOf("0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Life Milestone", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Milestone Name (e.g. Sabbatical, Buy Boat)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Category:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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

                OutlinedTextField(
                    value = startYearStr,
                    onValueChange = { startYearStr = it },
                    label = { Text("Start Year on Timeline (e.g. 3)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = weeklyCashflowStr,
                    onValueChange = { weeklyCashflowStr = it },
                    label = { Text("Weekly Cashflow Impact ($) (+ or -)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val sy = startYearStr.toIntOrNull() ?: 1
                        val wc = weeklyCashflowStr.toDoubleOrNull() ?: 0.0
                        val ai = assetImpactStr.toDoubleOrNull() ?: 0.0
                        val li = liabilityImpactStr.toDoubleOrNull() ?: 0.0
                        onConfirm(title.trim(), selectedCategory, sy, wc, ai, li)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark)
            ) {
                Text("Add Milestone", fontWeight = FontWeight.ExtraBold)
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
    onConfirm: (String, MilestoneCategory, Int, Double, Double, Double) -> Unit
) {
    var title by remember { mutableStateOf(milestone.title) }
    var selectedCategory by remember { mutableStateOf(milestone.category) }
    var startYearStr by remember { mutableStateOf(milestone.startYear.toString()) }
    var weeklyCashflowStr by remember { mutableStateOf(milestone.weeklyCashflowImpact.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Life Milestone", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Milestone Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Category:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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

                OutlinedTextField(
                    value = startYearStr,
                    onValueChange = { startYearStr = it },
                    label = { Text("Start Year on Timeline") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = weeklyCashflowStr,
                    onValueChange = { weeklyCashflowStr = it },
                    label = { Text("Weekly Cashflow Impact ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val sy = startYearStr.toIntOrNull() ?: milestone.startYear
                        val wc = weeklyCashflowStr.toDoubleOrNull() ?: milestone.weeklyCashflowImpact
                        onConfirm(title.trim(), selectedCategory, sy, wc, milestone.assetImpact, milestone.liabilityImpact)
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 115.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            TextButton(onClick = onNavigateToSettings) {
                                Text("Customize Menus", fontSize = 12.sp, color = BrandCoralPink, fontWeight = FontWeight.Bold)
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 115.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 115.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    if (item.isLockedMortgageOrPropertyLink) {
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
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Weekly", "Fortnight", "Monthly", "Quarterly", "Yearly").forEachIndexed { index, label ->
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
fun EditPropertyValuationDialog(
    propertyName: String,
    currentValuation: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var valStr by remember { mutableStateOf(if (currentValuation == 0.0) "" else currentValuation.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update $propertyName Valuation", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Enter the estimated market value for $propertyName:", fontSize = 12.sp, color = SlateGray)
                OutlinedTextField(
                    value = valStr,
                    onValueChange = { valStr = it },
                    label = { Text("Market Value ($)") },
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
                    if (v >= 0) onConfirm(v)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark)
            ) {
                Text("Update Valuation", fontWeight = FontWeight.ExtraBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun LivePropertyPriceSyncDialog(
    propertyName: String,
    currentValuation: Double,
    onDismiss: () -> Unit,
    onSyncValuation: (Double) -> Unit
) {
    val cleanAddress = propertyName.ifBlank { "Australia" }
    val encodedAddress = remember(cleanAddress) { URLEncoder.encode("$cleanAddress Australia", "UTF-8") }
    
    // Direct realestate.com.au URLs: Login page first to establish session, and target property search page
    val reaLoginUrl = "https://www.realestate.com.au/login"
    val reaPropertySearchUrl = "https://www.realestate.com.au/property/in-$encodedAddress"

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Property Search, 1 = Login Portal
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var detectedPrices by remember { mutableStateOf<List<Double>>(emptyList()) }
    var manualSyncInput by remember { mutableStateOf(currentValuation.toInt().toString()) }
    var isScanningPage by remember { mutableStateOf(false) }

    fun extractPricesFromText(rawText: String): List<Double> {
        val found = mutableSetOf<Double>()
        val millionRegex = Regex("""\$\s*(\d+(?:\.\d+)?)\s*[mM]\b""")
        millionRegex.findAll(rawText).forEach { m ->
            val num = m.groupValues[1].toDoubleOrNull()
            if (num != null && num in 0.2..25.0) {
                found.add((num * 1_000_000.0).roundToInt().toDouble())
            }
        }
        val kRegex = Regex("""\$\s*(\d{3,4}(?:\.\d+)?)\s*[kK]\b""")
        kRegex.findAll(rawText).forEach { m ->
            val num = m.groupValues[1].toDoubleOrNull()
            if (num != null && num in 150.0..9999.0) {
                found.add((num * 1_000.0).roundToInt().toDouble())
            }
        }
        val fullCurrencyRegex = Regex("""\$\s*(\d{1,3}(?:,\d{3})+)\b""")
        fullCurrencyRegex.findAll(rawText).forEach { m ->
            val clean = m.groupValues[1].replace(",", "").toDoubleOrNull()
            if (clean != null && clean in 150_000.0..25_000_000.0) {
                found.add(clean)
            }
        }
        return found.toList().sorted()
    }

    fun scanCurrentWebViewPage() {
        val wv = webViewRef ?: return
        isScanningPage = true
        wv.evaluateJavascript("(function() { return document.body.innerText; })();") { result ->
            isScanningPage = false
            if (!result.isNullOrBlank() && result != "null") {
                val extracted = extractPricesFromText(result)
                detectedPrices = extracted
                if (extracted.isNotEmpty()) {
                    val medianPrice = extracted[extracted.size / 2]
                    manualSyncInput = medianPrice.toInt().toString()
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header displaying the property address
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BrandForestDark)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "REALESTATE.COM.AU • ADDRESS SYNC",
                            color = BrandCoralPink,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            propertyName,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // Instruction & Login Status Banner
                Surface(
                    color = BrandMintGreen.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "💡 Step 1: Tap 'Log In' if needed. Step 2: Switch to 'Property Address' to view & scan valuation data for $propertyName.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                // Navigation Tabs (Switch between Property Address Search and REA Login)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            webViewRef?.loadUrl(reaPropertySearchUrl)
                        },
                        label = { Text("1. Property Address", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            webViewRef?.loadUrl(reaLoginUrl)
                        },
                        label = { Text("2. Log In to REA", fontSize = 11.sp) }
                    )
                }

                // Detected Valuations Bar
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                if (detectedPrices.isEmpty()) "Tap Scan once your address details load below:" else "Detected Valuations (Tap to select):",
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                            TextButton(
                                onClick = { scanCurrentWebViewPage() },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.FindInPage, contentDescription = null, tint = BrandMintGreen, modifier = Modifier.size(15.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(if (isScanningPage) "Scanning..." else "Scan Page", color = BrandMintGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (detectedPrices.isNotEmpty()) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(detectedPrices) { price ->
                                    SuggestionChip(
                                        onClick = { manualSyncInput = price.toInt().toString() },
                                        label = {
                                            Text(
                                                formatCurrency(price, 0),
                                                fontWeight = FontWeight.ExtraBold,
                                                color = BrandMintGreen
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))

                // Embedded WebView with Persistent Session Cookies
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, SlateGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                settings.userAgentString =
                                    "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"

                                val cookieManager = android.webkit.CookieManager.getInstance()
                                cookieManager.setAcceptCookie(true)
                                cookieManager.setAcceptThirdPartyCookies(this, true)

                                webViewClient = object : WebViewClient() {
                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        super.onPageFinished(view, url)
                                        android.webkit.CookieManager.getInstance().flush()
                                        scanCurrentWebViewPage()
                                    }
                                }
                                // Default load the saved property address search
                                loadUrl(reaPropertySearchUrl)
                                webViewRef = this
                            }
                        }
                    )
                }

                // Bottom Save / Sync Bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = manualSyncInput,
                        onValueChange = { manualSyncInput = it },
                        label = { Text("Valuation to Save ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Button(
                        onClick = {
                            val finalPrice = manualSyncInput.toDoubleOrNull()
                            if (finalPrice != null && finalPrice > 0) {
                                onSyncValuation(finalPrice)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandMintGreen,
                            contentColor = BrandForestDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Save to Property", fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
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
    sharesList: List<ShareParcel>,
    personName: String,
    messages: MutableList<ChatMessage>,
    onAddImportedShares: (List<ShareParcel>) -> Unit,
    onClearShares: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var attachedBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isSpeechEnabled by remember { mutableStateOf(true) }

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
                messages.add(ChatMessage("📷 Attached screenshot. Tap 'Upload Shares Screenshot' below or ask AI about it!", false))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val speechRecognizerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                inputText = spokenText
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
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
            } catch (e: Exception) {
                Toast.makeText(context, "Speech recognition not available", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Microphone permission is required for voice conversation", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 110.dp, end = 20.dp, start = 16.dp, top = 16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        if (!isExpanded) {
            // Movable, transparent floating chat head (Facebook Messenger style)
            Surface(
                onClick = { isExpanded = true },
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.35f),
                border = BorderStroke(1.5.dp, BrandCoralPink.copy(alpha = 0.6f)),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .size(56.dp)
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
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
                        contentDescription = "Open AI Assistant",
                        tint = BrandCoralPink,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        } else {
            // Expanded Chat Panel
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 16.dp,
                shadowElevation = 16.dp,
                border = BorderStroke(1.dp, BrandCoralPink.copy(alpha = 0.3f)),
                modifier = Modifier
                    .width(360.dp)
                    .height(520.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    // Header with Voice Output Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BrandForestDark, RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = CircleShape,
                                color = BrandCoralPink,
                                modifier = Modifier.size(30.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrandForestDark, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text("EXPNZ AI ASSISTANT", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = { isSpeechEnabled = !isSpeechEnabled }, modifier = Modifier.size(28.dp)) {
                                Icon(
                                    if (isSpeechEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                                    contentDescription = "Toggle Voice Output",
                                    tint = if (isSpeechEnabled) BrandMintGreen else SlateGray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(onClick = { isExpanded = false }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Minimize", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    // Chat Messages List
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(messages) { msg ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (msg.isFromUser) Arrangement.End else Arrangement.Start
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (msg.isFromUser) BrandCoralPink else BrandForestDark,
                                    modifier = Modifier.widthIn(max = 260.dp)
                                ) {
                                    Text(
                                        text = msg.text,
                                        color = if (msg.isFromUser) BrandForestDark else Color.White,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                        if (isLoading) {
                            item {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                                    Surface(shape = RoundedCornerShape(12.dp), color = BrandForestDark) {
                                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = BrandCoralPink, strokeWidth = 2.dp)
                                            Text("AI analyzing & writing data...", color = Color.White, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Attached Preview Bar
                    if (attachedBitmap != null) {
                        Spacer(Modifier.height(4.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = BrandForestDark.copy(alpha = 0.9f)
                        ) {
                            Row(
                                modifier = Modifier.padding(6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Screenshot ready to write to portfolio", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                IconButton(onClick = { attachedBitmap = null }, modifier = Modifier.size(20.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    // Input Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IconButton(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier
                                .size(38.dp)
                                .background(BrandMintGreen.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = BrandMintGreen, modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = {
                                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to your EXPNZ AI Assistant...")
                                    }
                                    try {
                                        speechRecognizerLauncher.launch(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Speech recognition not available", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .background(BrandCoralPink.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = BrandCoralPink, modifier = Modifier.size(18.dp))
                        }

                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Ask AI or speak...", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 2
                        )

                        Button(
                            onClick = {
                                if ((inputText.isNotBlank() || attachedBitmap != null) && !isLoading) {
                                    val question = inputText.trim().ifBlank { "Analyze this attached portfolio screenshot and add shares to my portfolio." }
                                    inputText = ""
                                    messages.add(ChatMessage(question, true))
                                    val bitmapToSend = attachedBitmap
                                    attachedBitmap = null
                                    isLoading = true
                                    coroutineScope.launch {
                                        var reply = ""
                                        if (bitmapToSend != null) {
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
                                                reply = "Successfully extracted ${importedItems.size} shares (${importedItems.joinToString { it.ticker }}) from your screenshot and wrote them directly into your Shares tab!"
                                            } else {
                                                reply = AiAssistantManager.askGeminiAssistant(question, financialContextSummary, bitmapToSend)
                                            }
                                        } else {
                                            reply = AiAssistantManager.askGeminiAssistant(question, financialContextSummary, null)
                                        }
                                        messages.add(ChatMessage(reply, false))
                                        if (isSpeechEnabled) {
                                            tts?.speak(reply, TextToSpeech.QUEUE_FLUSH, null, null)
                                        }
                                        isLoading = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandCoralPink, contentColor = BrandForestDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(46.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
