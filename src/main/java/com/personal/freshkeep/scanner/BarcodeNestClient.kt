package com.personal.freshkeep.scanner

import com.personal.freshkeep.BuildConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class BarcodeLookupResult(
    val productName: String?,
    val rawJson: String?,
    val message: String
)

object BarcodeNestClient {
    private val apiKey = BuildConfig.BARCODENEST_API_KEY

    suspend fun fetchProductData(barcode: String): BarcodeLookupResult = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            if (apiKey.isBlank()) {
                return@withContext BarcodeLookupResult(null, null, "BarcodeNest API key is not configured.")
            }

            val url = URL("https://api.barcodenest.com/v1/products/${barcode.trim()}")
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.setRequestProperty("X-API-Key", apiKey)

            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                return@withContext BarcodeLookupResult(null, null, "BarcodeNest returned HTTP $responseCode.")
            }

            val rawResponse = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(rawResponse)
            val productName = json.optJSONObject("product")?.optString("name")
                ?.takeIf { it.isNotBlank() && it != "null" }
                ?: json.optString("name").takeIf { it.isNotBlank() && it != "null" }
            val found = json.optBoolean("found", productName != null)

            val message = when {
                !found -> "Request succeeded, but no product was found for this barcode."
                productName == null -> "Lookup succeeded, but the response did not include a product name."
                else -> "Product found. Name autofilled."
            }
            BarcodeLookupResult(productName, rawResponse, message)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            BarcodeLookupResult(null, null, "BarcodeNest lookup failed: ${e.localizedMessage ?: "unknown error"}")
        } finally {
            connection?.disconnect()
        }
    }
}
