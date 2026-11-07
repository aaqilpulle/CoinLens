package com.example.expnz20

import android.graphics.Bitmap
import android.util.Log
import androidx.core.graphics.scale
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.GenerationConfig
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class LearnedUserFact(
    val id: String = UUID.randomUUID().toString(),
    val category: String = "General", // "Goals", "History", "Preferences", "Personal", "Financial", "Milestones"
    val fact: String,
    val timestamp: Long = System.currentTimeMillis(),
) {
    fun toJson(): JSONObject {
        val o = JSONObject()
        o.put("id", id)
        o.put("category", category)
        o.put("fact", fact)
        o.put("timestamp", timestamp)
        return o
    }

    companion object {
        fun fromJson(o: JSONObject): LearnedUserFact {
            return LearnedUserFact(
                id = o.optString("id", UUID.randomUUID().toString()),
                category = o.optString("category", "General"),
                fact = o.optString("fact", ""),
                timestamp = o.optLong("timestamp", System.currentTimeMillis()),
            )
        }
    }
}

data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val timestamp: String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
    val attachedBitmap: Bitmap? = null,
    val attachedFileName: String? = null,
    val appliedActions: List<String> = emptyList(),
) {
    fun toJson(): JSONObject {
        val o = JSONObject()
        o.put("text", text)
        o.put("isFromUser", isFromUser)
        o.put("timestamp", timestamp)
        if (!attachedFileName.isNullOrBlank()) {
            o.put("attachedFileName", attachedFileName)
        }
        if (appliedActions.isNotEmpty()) {
            val arr = JSONArray()
            appliedActions.forEach { arr.put(it) }
            o.put("appliedActions", arr)
        }
        return o
    }

    companion object {
        fun fromJson(o: JSONObject): ChatMessage {
            val arr = o.optJSONArray("appliedActions")
            val actions = mutableListOf<String>()
            if (arr != null) {
                for (i in 0 until arr.length()) {
                    actions.add(arr.optString(i))
                }
            }
            return ChatMessage(
                text = o.optString("text", ""),
                isFromUser = o.optBoolean("isFromUser", false),
                timestamp = o.optString("timestamp", SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())),
                attachedFileName = if (o.has("attachedFileName")) o.optString("attachedFileName").takeIf { it.isNotBlank() } else null,
                appliedActions = actions,
            )
        }
    }
}

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

    private const val TAG = "AiAssistantManager"

    // Supported active modern Gemini models in priority order
    private val preferredModelCandidates = listOf(
        "gemini-3.5-flash",
        "gemini-3.5-flash-lite",
        "gemini-3.8-flash",
    )

    @Volatile
    private var activeWorkingModel: String = "gemini-3.5-flash"

    // Cooldown map to avoid retrying models that exhausted their free tier quota (e.g. 429) or do not exist (404)
    private val modelCooldownUntil = ConcurrentHashMap<String, Long>()

    // Background warm-up to preemptively trigger Firebase App Check and TLS handshakes,
    // avoiding the 1-2 minute cold start delay on the user's first actual chat message.
    suspend fun warmUp() = withContext(Dispatchers.IO) {
        try {
            val aiModel = Firebase.ai.generativeModel(
                modelName = activeWorkingModel,
                generationConfig = createStandardChatConfig(),
            )
            // countTokens is fast, cheap, and forces the network initialization
            aiModel.countTokens("warm-up")
            Log.i(TAG, "AI Assistant background warm-up complete.")
        } catch (e: Exception) {
            Log.w(TAG, "AI Assistant warm-up failed: ${e.message}")
        }
    }

    private fun isModelCoolingDown(modelName: String): Boolean {
        val until = modelCooldownUntil[modelName] ?: return false
        if (System.currentTimeMillis() > until) {
            modelCooldownUntil.remove(modelName)
            return false
        }
        return true
    }

    private fun markModelUnavailable(modelName: String, isPermanent: Boolean = false) {
        val duration = if (isPermanent) Long.MAX_VALUE else (System.currentTimeMillis() + (6 * 60 * 60 * 1000L))
        modelCooldownUntil[modelName] = duration
        Log.w(TAG, "Model $modelName marked unavailable (permanent=$isPermanent) to preserve responsiveness")
    }

    private fun markModelQuotaExceeded(modelName: String) = markModelUnavailable(modelName, isPermanent = false)

    private fun isQuotaOrNotFoundError(e: Exception): Boolean {
        val msg = e.message?.lowercase() ?: ""
        return msg.contains("quota") || msg.contains("429") || msg.contains("resource_exhausted") ||
                msg.contains("404") || msg.contains("not found") || msg.contains("retired") ||
                msg.contains("does not exist") || msg.contains("unregistered") || msg.contains("unsupported")
    }

    private fun getModelCandidates(): List<String> {
        val available = preferredModelCandidates.filter { !isModelCoolingDown(it) }
        val pool = available.ifEmpty { listOf(activeWorkingModel) }
        val active = activeWorkingModel
        return if (!isModelCoolingDown(active) && pool.contains(active)) {
            listOf(active) + pool.filter { it != active }
        } else {
            pool
        }
    }

    private fun optimizeBitmapForAi(original: Bitmap, maxDimension: Int = 768): Bitmap {
        val width = original.width
        val height = original.height
        if ((width <= maxDimension) && (height <= maxDimension)) return original

        val ratio = width.toFloat() / height.toFloat()
        val targetWidth: Int
        val targetHeight: Int
        if (width > height) {
            targetWidth = maxDimension
            targetHeight = (maxDimension / ratio).toInt().coerceAtLeast(1)
        } else {
            targetHeight = maxDimension
            targetWidth = (maxDimension * ratio).toInt().coerceAtLeast(1)
        }
        return original.scale(targetWidth, targetHeight)
    }

    private fun createFastChatConfig(): GenerationConfig {
        return generationConfig {
            temperature = 0.2f
            maxOutputTokens = 700
        }
    }

    private fun createStandardChatConfig(): GenerationConfig {
        return generationConfig {
            temperature = 0.2f
            maxOutputTokens = 700
        }
    }

    private fun createFastJsonConfig(): GenerationConfig {
        return generationConfig {
            temperature = 0.1f
            maxOutputTokens = 600
            responseMimeType = "application/json"
        }
    }

    private fun formatHistoryMessageForContext(msg: ChatMessage): String {
        return buildString {
            if (!msg.attachedFileName.isNullOrBlank()) {
                append("[Attached File: ${msg.attachedFileName}] ")
            }
            if (msg.attachedBitmap != null) {
                append("[Attached Image/Screenshot] ")
            }
            append(msg.text.trim())
            if (msg.appliedActions.isNotEmpty()) {
                append("\n[Applied App Actions: ${msg.appliedActions.joinToString("; ")}]")
            }
        }
    }

    private fun buildAlternatingHistoryContents(history: List<ChatMessage>): List<Content> {
        val recent = history
            .filter { (text, _, _, attachedBitmap, attachedFileName) ->
                (text.isNotBlank()) || (attachedBitmap != null) || (!attachedFileName.isNullOrBlank())
            }
            .takeLast(6) // 6 recent turns keeps context tight and minimizes Time-To-First-Token

        val firstUserIdx = recent.indexOfFirst { (_, isFromUser) -> isFromUser }
        if (firstUserIdx < 0) return emptyList()
        val trimmed = recent.subList(firstUserIdx, recent.size)

        val coalesced = mutableListOf<Pair<String, String>>()
        for (msg in trimmed) {
            val role = if (msg.isFromUser) "user" else "model"
            val formatted = formatHistoryMessageForContext(msg)
            if (coalesced.isNotEmpty() && (coalesced.last().first == role)) {
                val (_, prevText) = coalesced.removeAt(coalesced.lastIndex)
                coalesced.add(role to "$prevText\n\n$formatted")
            } else {
                coalesced.add(role to formatted)
            }
        }

        if (coalesced.isNotEmpty() && (coalesced.last().first == "user")) {
            coalesced.add("model" to "Understood. Please continue.")
        }

        return coalesced.map { (role, textBody) ->
            content(role = role) {
                text(textBody)
            }
        }
    }

    // Chat with Gemini Financial Assistant (supports real-time streaming to UI + automated lifelong memory)
    suspend fun askGeminiAssistant(
        prompt: String,
        contextSummary: String = "",
        conversationHistory: List<ChatMessage> = emptyList(),
        learnedFacts: List<LearnedUserFact> = emptyList(),
        userProfileSummary: String = "",
        longTermConversationDigest: String = "",
        bitmap: Bitmap? = null,
        fileContent: String? = null,
        fileName: String? = null,
        onPartialText: (suspend (String) -> Unit)? = null,
    ): String = withContext(Dispatchers.IO) {
        try {
            val systemInstructionText = buildString {
                append("You are CoinBot, an elite Australian personal wealth strategist and financial co-pilot embedded inside the COINLENS app.\n\n")
                append("CRITICAL: REAL-TIME WRITE ACCESS TO DASHBOARD TABS:\n")
                append("Whenever the user asks you to add, edit, change, set, or delete ANY item or number (or confirms a change), you MUST append the exact [[ACTION:ACTION_TYPE|...]] tag at the VERY END of your reply.\n\n")
                append("Supported ACTION_TYPEs:\n")
                append("- Personal Income: [[ACTION:SET_INCOME|person=1|amount=1500]] (person=1 or 2, weekly $)\n")
                append("- Personal Savings Goal: [[ACTION:SET_SAVINGS|person=1|amount=300]]\n")
                append("- Personal / Household Expenses & Income: [[ACTION:ADD_CASHFLOW|target=person1|name=Groceries|weeklyAmount=-180|icon=Food]] (target=person1, person2, prop1, or prop2; negative for expense; icon=Receipt, Home, Food, Car, Shopping, Health, Utility, Entertainment)\n")
                append("- Update Cashflow: [[ACTION:UPDATE_CASHFLOW|target=person1|name=Groceries|weeklyAmount=-200]]\n")
                append("- Delete Cashflow: [[ACTION:DELETE_CASHFLOW|target=person1|name=Groceries]]\n")
                append("- Property Value & Rent: [[ACTION:SET_PROPERTY_VALUE|property=1|value=850000]], [[ACTION:SET_PROPERTY_RENT|property=1|weeklyRent=650|isRented=true]], [[ACTION:SET_PROPERTY_NAME|property=1|name=12 Smith St]]\n")
                append("- Property Bills: [[ACTION:ADD_PROPERTY_ITEM|property=1|name=Council Rates|weeklyAmount=-45]], [[ACTION:DELETE_PROPERTY_ITEM|property=1|name=Council Rates]]\n")
                append("- Mortgage & Offset: [[ACTION:UPDATE_LOAN|property=1|startLoanBalance=450000|originalLoanAmount=500000|interestRatePa=6.15|offsetBalance=55000]]\n")
                append("- Shares / ETFs / Stock Portfolio Screenshots: [[ACTION:ADD_SHARE|code=VAS|owner=Me|units=50|purchasePrice=95.0|currentPrice=108.57]], [[ACTION:UPDATE_SHARE|code=VAS|units=60|purchasePrice=96.0|currentPrice=108.57]], [[ACTION:DELETE_SHARE|code=VAS]]. If the user provides a portfolio screenshot, brokerage statement, or trade confirmation image, inspect every holding shown and emit [[ACTION:ADD_SHARE|code=...|owner=Me|units=...|purchasePrice=...|currentPrice=...]] for each share holding.\n")
                append("- Superannuation: [[ACTION:ADD_SUPER|fundName=AustralianSuper|owner=Me|balance=125000]], [[ACTION:UPDATE_SUPER|fundName=AustralianSuper|balance=130000]], [[ACTION:DELETE_SUPER|fundName=AustralianSuper]]\n")
                append("- Other Assets: [[ACTION:ADD_OTHER_ASSET|name=Car|owner=Me|value=25000]], [[ACTION:UPDATE_OTHER_ASSET|name=Car|value=28000]], [[ACTION:DELETE_OTHER_ASSET|name=Car]]\n")
                append("- Levers: [[ACTION:SET_WHAT_IF_LEVERS|horizonYears=15|extraSavingsWeekly=200|incomeGrowth1Weekly=100|incomeGrowth2Weekly=0|rateShockProp1=-0.5|rateShockProp2=-0.5|superBoostWeekly=100|rentalGrowthWeekly=50]], [[ACTION:RESET_LEVERS]]\n")
                append("- Milestones: [[ACTION:ADD_MILESTONE|title=Buy Investment Property|category=PROPERTY|startYear=2|durationYears=25|weeklyCashflow=-150|assetImpact=750000|liabilityImpact=600000]], [[ACTION:DELETE_MILESTONE|title=Buy Investment Property]]\n")
                append("- Profile: [[ACTION:UPDATE_PROFILE|person1Name=Alex|person2Name=Sam|isCoupleMode=true]]\n")
                append("- Remember Fact: [[ACTION:REMEMBER_FACT|category=Goals|fact=Targeting $100k ETF portfolio by 2027]] (categories: Goals, History, Preferences, Personal, Financial, Milestones)\n")
                append("- Forget Fact: [[ACTION:FORGET_FACT|query=Targeting $100k]]\n\n")
                append("RULES: 1 yr = 52 wks. Monthly = weekly * 52 / 12. Be concise, direct, sharp, and warm. No markdown asterisks or hashes.\n\n")

                if (learnedFacts.isNotEmpty() || userProfileSummary.isNotBlank() || longTermConversationDigest.isNotBlank()) {
                    append("PERMANENT USER MEMORY:\n")
                    if (learnedFacts.isNotEmpty()) {
                        val grouped = learnedFacts.takeLast(15).groupBy { (_, category) -> category }
                        for ((cat, facts) in grouped) {
                            append("[$cat]: ${facts.joinToString("; ") { (_, _, fact) -> fact }}\n")
                        }
                    }
                    if (userProfileSummary.isNotBlank()) append("USER SUMMARY: $userProfileSummary\n")
                    if (longTermConversationDigest.isNotBlank()) append("DIGEST: $longTermConversationDigest\n")
                    append("\n")
                }

                append("Whenever the user shares a new personal or financial fact/goal, emit [[ACTION:REMEMBER_FACT|category=...|fact=...]] at the very end.\n\n")

                if (contextSummary.isNotBlank()) {
                    append("REAL-TIME APP FINANCIAL DATA:\n$contextSummary\n")
                }
            }

            val historyContents = buildAlternatingHistoryContents(conversationHistory)

            val userTurnPrompt = buildString {
                if (historyContents.isEmpty() && conversationHistory.isNotEmpty()) {
                    val relevantTurns = conversationHistory
                        .filter { (text, _, _, attachedBitmap, attachedFileName) ->
                            (text.isNotBlank()) || (attachedBitmap != null) || (!attachedFileName.isNullOrBlank())
                        }
                        .takeLast(4)
                    if (relevantTurns.isNotEmpty()) {
                        append("RECENT CONVERSATION CONTEXT:\n")
                        relevantTurns.forEach { msg ->
                            val speaker = if (msg.isFromUser) "USER" else "COINBOT"
                            append("[$speaker]: ${formatHistoryMessageForContext(msg)}\n")
                        }
                        append("\n")
                    }
                }
                if (!fileName.isNullOrBlank() && !fileContent.isNullOrBlank()) {
                    append("ATTACHED FILE ($fileName):\n${fileContent.take(10000)}\n\n")
                }
                append("CURRENT USER MESSAGE:\n$prompt\n\n")
                append("[DIRECTIVE: Keep response concise and fast. If confirming or applying changes/facts, emit the exact [[ACTION:ACTION_TYPE|...]] tag at the very end.]")
            }

            val optimizedBitmap = bitmap?.let { optimizeBitmapForAi(it) }
            val currentTurnContent = content(role = "user") {
                optimizedBitmap?.let { image(it) }
                text(userTurnPrompt)
            }
            val inputList = if (historyContents.isNotEmpty()) historyContents + currentTurnContent else listOf(currentTurnContent)
            val systemInstructionContent = content { text(systemInstructionText) }

            var lastError: Exception? = null
            for (modelName in getModelCandidates()) {
                try {
                    val aiModel = Firebase.ai.generativeModel(
                        modelName = modelName,
                        generationConfig = createStandardChatConfig(),
                        systemInstruction = systemInstructionContent,
                    )
                    val buffer = StringBuilder()
                    if (onPartialText != null) {
                        aiModel.generateContentStream(inputList).collect { chunk ->
                            val text = chunk.text
                            if (!text.isNullOrEmpty()) {
                                buffer.append(text)
                                onPartialText.invoke(buffer.toString())
                            }
                        }
                    } else {
                        val response = aiModel.generateContent(inputList)
                        buffer.append(response.text ?: "")
                    }
                    val rawText = buffer.toString()
                    if (rawText.isNotBlank()) {
                        activeWorkingModel = modelName
                        return@withContext cleanMarkdownText(rawText)
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Model $modelName standard chat config failed: ${e.message}")
                    lastError = e
                    val msg = e.message?.lowercase() ?: ""
                    val isPermanent = msg.contains("404") || msg.contains("not found") || msg.contains("retired")
                    markModelUnavailable(modelName, isPermanent = isPermanent)
                    continue
                }
            }

            return@withContext "Error communicating with Gemini AI: ${lastError?.localizedMessage ?: "No response from model"}"
        } catch (e: Exception) {
            Log.e(TAG, "Error in askGeminiAssistant: ${e.message}", e)
            return@withContext "Error communicating with Gemini AI: ${e.localizedMessage ?: "Unknown error"}"
        }
    }

    // Synthesize dense long-term conversation digest across older conversation turns
    suspend fun synthesizeConversationDigest(
        olderMessages: List<ChatMessage>,
        currentDigest: String,
    ): String = withContext(Dispatchers.IO) {
        if (olderMessages.isEmpty()) return@withContext currentDigest
        val transcript = olderMessages.takeLast(10).joinToString("\n") { (text, isFromUser) ->
            val role = if (isFromUser) "User" else "CoinBot"
            "$role: ${text.take(180)}"
        }
        val prompt = """
            You are the memory engine for CoinBot, an Australian wealth advisor.
            Existing long-term conversation memory digest:
            "$currentDigest"

            New historical conversation turns to assimilate:
            "$transcript"

            Synthesize an updated, compact chronological digest of all previous conversations.
            Capture:
            - Key financial decisions made or agreed upon
            - Questions asked and advice provided
            - Financial milestones explored
            - Important life events or context discussed
            Keep it dense, bulleted, clear, and high signal. Maximum 350 words.
        """.trimIndent()

        val candidates = getModelCandidates()
        for (modelName in candidates) {
            try {
                val aiModel = Firebase.ai.generativeModel(
                    modelName = modelName,
                    generationConfig = createFastChatConfig(),
                )
                val resp = aiModel.generateContent(prompt)
                val text = resp.text
                if (!text.isNullOrBlank()) {
                    activeWorkingModel = modelName
                    return@withContext cleanMarkdownText(text)
                }
            } catch (e: Exception) {
                Log.w(TAG, "synthesizeConversationDigest failed on $modelName: ${e.message}")
                if (isQuotaOrNotFoundError(e)) {
                    markModelQuotaExceeded(modelName)
                }
            }
        }
        return@withContext currentDigest
    }

    // Intelligent Subscription & Money Leak Detector
    suspend fun detectSubscriptionLeaks(expensesContext: String): List<SubscriptionLeak> = withContext(Dispatchers.IO) {
        if (expensesContext.isBlank()) return@withContext emptyList()
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

        val candidates = getModelCandidates()
        for (modelName in candidates) {
            try {
                val aiModel = Firebase.ai.generativeModel(
                    modelName = modelName,
                    generationConfig = createFastJsonConfig(),
                )
                val response = aiModel.generateContent(prompt)
                val rawText = response.text ?: continue
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
                activeWorkingModel = modelName
                return@withContext results
            } catch (e: Exception) {
                Log.w(TAG, "detectSubscriptionLeaks failed on $modelName: ${e.message}")
                if (isQuotaOrNotFoundError(e)) {
                    markModelQuotaExceeded(modelName)
                }
            }
        }
        return@withContext emptyList()
    }
}
