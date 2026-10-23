package com.personal.freshkeep.scanner

import com.personal.freshkeep.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object BarcodeNestClient {
    private val apiKey = BuildConfig.BARCODENEST_API_KEY

    // Returns a Pair containing: (Extracted Product Title, Complete Raw JSON Response String)
    suspend fun fetchProductData(barcode: String): Pair<String?, String?> = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://barcodenest.com")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.setRequestProperty("X-API-Key", apiKey)
            
            if (connection.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val rawResponse = reader.use { it.readText() }
                
                val json = JSONObject(rawResponse)
                val title = json.optJSONObject("product")?.optString("title") 
                    ?: json.optString("name", null)
                
                Pair(title, rawResponse)
            } else Pair(null, null)
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(null, null)
        }
    }
}
