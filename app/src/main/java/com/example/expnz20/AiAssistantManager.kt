package com.example.expnz20

import android.graphics.Bitmap
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

data class ImportedShareItem(
    val ticker: String,
    val quantity: Double,
    val averagePrice: Double,
)

data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
)

data class SubscriptionLeak(
    val merchant: String,
    val monthlyAmount: Double,
    val annualImpact: Double,
    val category: String,
)

fun cleanMarkdownText(raw: String): String {
    return raw
        .replace(Regex("""[#*`_~-]"""), "")
        .replace(Regex("""\n{3,}"""), "\n\n")
        .trim()
}

object AiAssistantManager {

    // Chat with Gemini Financial Assistant using Firebase AI SDK
    suspend fun askGeminiAssistant(prompt: String, contextSummary: String = "", bitmap: Bitmap? = null): String = withContext(Dispatchers.IO) {
        try {
            val aiModel = Firebase.ai.generativeModel("gemini-3.5-flash-lite")
            val fullPrompt = buildString {
                append("You are EXPNZ AI Assistant, an expert financial AI companion embedded inside the EXPNZ personal wealth and finance mobile app.\n\n")
                if (contextSummary.isNotBlank()) {
                    append("REAL-TIME UNMASKED PRE-COMPUTED APP FINANCIAL DATA (ALL TABS & FUTURE PROJECTIONS):\n")
                    append("$contextSummary\n\n")
                }
                append("STRICT ACCURACY & REASONING GUIDELINES:\n")
                append("1. You have complete, unmasked, cross-tab visibility into all parts of the user's financial profile—including Net Worth, itemized Cash Flow entries, Property equity & expenses, Loan amortization & offset savings, Super & Shares holdings, Other Assets, and active What-If scenario future projections.\n")
                append("2. ALL numbers provided in the financial data summary above are exact, unmasked actual figures (even if the user toggled privacy mode on screen). ALWAYS use these exact figures. NEVER guess, invent, or hallucinate numbers.\n")
                append("3. Use logical mathematical reasoning to cross-reference data between tabs (e.g. comparing weekly surplus vs mortgage repayments, or analyzing how a What-If milestone impacts future net worth).\n")
                append("4. When answering questions in conversational What-If mode, directly reference Path A (Baseline Status Quo) vs Path B (Simulated Future Plan) net worth, net advantage, and cashflow deficit years provided in the summary.\n")
                append("5. Do NOT use markdown symbols like hashtags (#), asterisks (*), or backticks (`) for formatting. Keep responses clean, conversational, direct, and plain text.\n\n")
                append("User question: $prompt")
            }
            val response = if (bitmap != null) {
                aiModel.generateContent(
                    content {
                        image(bitmap)
                        text(fullPrompt)
                    }
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
                        category = obj.optString("category", "Subscription")
                    )
                )
            }
            return@withContext results
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }

    // Parse Pearler Portfolio Screenshots using Firebase AI
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
                No markdown formatting, no backticks, just the raw JSON array.
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
                results.add(
                    ImportedShareItem(
                        ticker = obj.optString("ticker", "UNKNOWN"),
                        quantity = obj.optDouble("quantity", 0.0),
                        averagePrice = obj.optDouble("averagePrice", 0.0)
                    )
                )
            }
            return@withContext results
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }
}
