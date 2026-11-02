package com.example.expnz20

import android.graphics.Bitmap
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ImportedShareItem(
    val ticker: String,
    val quantity: Double,
    val averagePrice: Double,
)

data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val timestamp: String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
    val attachedBitmap: Bitmap? = null,
    val attachedFileName: String? = null,
    val appliedActions: List<String> = emptyList(),
)

data class SubscriptionLeak(
    val merchant: String,
    val monthlyAmount: Double,
    val annualImpact: Double,
    val category: String,
)

fun cleanMarkdownText(raw: String): String {
    return raw
        .replace(Regex("""[#*`~]"""), "")
        .replace(Regex("""\n{3,}"""), "\n\n")
        .trim()
}

object AiAssistantManager {

    // Chat with Gemini Financial Assistant using Firebase AI SDK (Supports Text, Camera/Media Bitmaps, and Uploaded Files + Full Read/Write Actions)
    suspend fun askGeminiAssistant(
        prompt: String,
        contextSummary: String = "",
        bitmap: Bitmap? = null,
        fileContent: String? = null,
        fileName: String? = null,
    ): String = withContext(Dispatchers.IO) {
        try {
            val aiModel = Firebase.ai.generativeModel("gemini-3.5-flash-lite")
            val fullPrompt = buildString {
                append("You are EXPNZ AI Assistant, a professional Australian financial co-pilot embedded inside the EXPNZ personal wealth and finance app.\n\n")
                if (contextSummary.isNotBlank()) {
                    append("REAL-TIME UNMASKED APP FINANCIAL DATA (ALL CURRENT TABS & FUTURE PROJECTIONS):\n")
                    append("$contextSummary\n\n")
                }
                if (!fileName.isNullOrBlank() && !fileContent.isNullOrBlank()) {
                    append("ATTACHED FILE UPLOADED BY USER ($fileName):\n")
                    append("${fileContent.take(25000)}\n\n")
                }
                append("FULL READ & WRITE CAPABILITIES INSTRUCTIONS:\n")
                append("1. You have complete read access to all current and future projected data across every tab in the app.\n")
                append("2. You also have FULL WRITE ACCESS to create, edit, update, or delete any editable data in the app whenever the user asks you to make a change, log an item, update a number, adjust a What-If lever, or import data from a photo/receipt/screenshot/file.\n")
                append("3. Whenever the user asks you to add, update, change, set, or delete any data, emit one or more action tags at the END of your response using this exact single-line format:\n")
                append("   [[ACTION:ACTION_TYPE|key1=value1|key2=value2]]\n")
                append("   Supported ACTION_TYPEs and keys:\n")
                append("   - [[ACTION:SET_INCOME|person=1|amount=1500.0]] (person=1 or 2, weekly take-home income in $)\n")
                append("   - [[ACTION:ADD_CASHFLOW|target=person1|name=Groceries|weeklyAmount=-180.0|icon=Food]] (target=person1, person2, prop1, or prop2; negative weeklyAmount for expense, positive for income; icon=Default, Home, Food, Car, Shopping, Health, Utility, Entertainment)\n")
                append("   - [[ACTION:UPDATE_CASHFLOW|target=person1|name=Groceries|weeklyAmount=-200.0]]\n")
                append("   - [[ACTION:DELETE_CASHFLOW|target=person1|name=Groceries]]\n")
                append("   - [[ACTION:SET_PROPERTY_VALUE|property=1|value=850000]] (property=1 or 2)\n")
                append("   - [[ACTION:SET_PROPERTY_RENT|property=1|weeklyRent=650|isRented=true]] (property=1 or 2)\n")
                append("   - [[ACTION:SET_PROPERTY_NAME|property=1|name=12 Smith St]]\n")
                append("   - [[ACTION:ENABLE_SECOND_PROPERTY|enabled=true]]\n")
                append("   - [[ACTION:UPDATE_LOAN|property=1|startLoanBalance=450000|originalLoanAmount=500000|interestRatePa=6.15|offsetBalance=55000|startDateStr=2024-01-01]] (include only the keys to change for property=1 or 2)\n")
                append("   - [[ACTION:ADD_SHARE|code=VAS|owner=Me|units=50|purchasePrice=95.0|currentPrice=108.57]]\n")
                append("   - [[ACTION:UPDATE_SHARE|code=VAS|units=60|purchasePrice=96.0|currentPrice=108.57]]\n")
                append("   - [[ACTION:DELETE_SHARE|code=VAS]]\n")
                append("   - [[ACTION:ADD_SUPER|fundName=AustralianSuper|owner=Me|balance=125000]]\n")
                append("   - [[ACTION:UPDATE_SUPER|fundName=AustralianSuper|balance=130000]]\n")
                append("   - [[ACTION:DELETE_SUPER|fundName=AustralianSuper]]\n")
                append("   - [[ACTION:ADD_OTHER_ASSET|name=Emergency Fund|owner=Me|value=25000]]\n")
                append("   - [[ACTION:UPDATE_OTHER_ASSET|name=Emergency Fund|value=30000]]\n")
                append("   - [[ACTION:DELETE_OTHER_ASSET|name=Emergency Fund]]\n")
                append("   - [[ACTION:SET_WHAT_IF_LEVERS|horizonYears=15|extraSavingsWeekly=200|rateShockProp1=-0.5|rateShockProp2=-0.5|superBoostWeekly=100|rentalGrowthWeekly=50]] (include any levers to update)\n")
                append("   - [[ACTION:ADD_CUSTOM_LEVER|title=Side Hustle|impactType=WEEKLY_CASHFLOW|value=150|min=-200|max=1000|step=25]] (impactType: WEEKLY_CASHFLOW, ANNUAL_ASSET_CHANGE, LUMP_SUM_OFFSET, or INTEREST_RATE_DELTA)\n")
                append("   - [[ACTION:UPDATE_CUSTOM_LEVER|title=Side Hustle|value=250]]\n")
                append("   - [[ACTION:DELETE_CUSTOM_LEVER|title=Side Hustle]]\n")
                append("   - [[ACTION:ADD_MILESTONE|title=Buy Investment Property|category=PROPERTY|startYear=2|durationYears=25|weeklyCashflow=-150|assetImpact=750000|liabilityImpact=600000]] (category: PROPERTY, CAREER, PURCHASE, INVESTMENT)\n")
                append("   - [[ACTION:DELETE_MILESTONE|title=Buy Investment Property]]\n")
                append("   - [[ACTION:CLEAR_MILESTONES]]\n")
                append("   - [[ACTION:UPDATE_PROFILE|person1Name=Alex|person2Name=Sam|isCoupleMode=true]]\n")
                append("4. Always use exact numbers from the context summary. Keep your explanation clear, concise, and helpful. Do not use markdown hashtags (#) or asterisks (*).\n\n")
                append("User message: $prompt")
            }
            val response = if (bitmap != null) {
                aiModel.generateContent(
                    content {
                        image(bitmap)
                        text(fullPrompt)
                    },
                )
            } else {
                aiModel.generateContent(fullPrompt)
            }
            val rawText = response.text ?: "I'm sorry, I couldn't generate a response at this moment."
            return@withContext cleanMarkdownText(rawText)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext "Error communicating with Gemini AI: ${e.localizedMessage ?: "Unknown error"}"
        }
    }

    // Intelligent Subscription & Money Leak Detector
    suspend fun detectSubscriptionLeaks(expensesContext: String): List<SubscriptionLeak> = withContext(Dispatchers.IO) {
        if (expensesContext.isBlank()) return@withContext emptyList()
        try {
            val aiModel = Firebase.ai.generativeModel("gemini-3.5-flash-lite")
            val prompt = """
                Analyze this list of past expenses and bank statements: "$expensesContext".
                Scan for recurring subscriptions, hidden memberships, streaming services, or duplicate charges.
                Return ONLY a valid JSON array of objects with these exact keys:
                - "merchant": string (e.g. "Netflix", "Gym Membership")
                - "monthlyAmount": number (estimated monthly cost)
                - "annualImpact": number (monthlyAmount * 12)
                - "category": string (e.g. "Subscription", "Entertainment", "Fitness")
                No markdown formatting, no backticks, just the raw JSON array. If none found, return [].
            """.trimIndent()

            val response = aiModel.generateContent(prompt)
            val rawText = response.text ?: return@withContext emptyList()
            val cleanJson = rawText.replace("```json", "").replace("```", "").trim()
            val jsonArray = JSONArray(cleanJson)
            val results = mutableListOf<SubscriptionLeak>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val monthly = obj.optDouble("monthlyAmount", 0.0)
                results.add(
                    SubscriptionLeak(
                        merchant = obj.optString("merchant", "Unknown"),
                        monthlyAmount = monthly,
                        annualImpact = obj.optDouble("annualImpact", monthly * 12.0),
                        category = obj.optString("category", "Subscription"),
                    ),
                )
            }
            return@withContext results
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }

    // Parse Portfolio Screenshots using Firebase AI
    suspend fun parsePearlerScreenshotWithAi(bitmap: Bitmap): List<ImportedShareItem> = withContext(Dispatchers.IO) {
        try {
            val aiModel = Firebase.ai.generativeModel("gemini-3.5-flash-lite")

            val prompt = """
                Analyze this screenshot of an investment/shares portfolio containing multiple shares.
                Extract every individual stock holding visible in the image.
                Return ONLY a valid JSON array of objects with these exact keys:
                - "ticker": string (e.g., "VAS", "VDHG", "AAPL")
                - "quantity": number (total units or shares held)
                - "averagePrice": number (price shown, or 0.0 if not available)
                No markdown formatting, no backticks, just the raw JSON array. If this is not a shares portfolio screenshot, return [].
            """.trimIndent()

            val inputContent = content {
                image(bitmap)
                text(prompt)
            }

            val response = aiModel.generateContent(inputContent)
            val rawText = response.text ?: return@withContext emptyList()

            val cleanJsonStr = rawText.replace("```json", "").replace("```", "").trim()
            val jsonArray = JSONArray(cleanJsonStr)
            val results = mutableListOf<ImportedShareItem>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val ticker = obj.optString("ticker", "").trim()
                val qty = obj.optDouble("quantity", 0.0)
                if ((ticker.isNotBlank()) && (qty > 0.0)) {
                    results.add(
                        ImportedShareItem(
                            ticker = ticker,
                            quantity = qty,
                            averagePrice = obj.optDouble("averagePrice", 0.0),
                        ),
                    )
                }
            }
            return@withContext results
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }
}
