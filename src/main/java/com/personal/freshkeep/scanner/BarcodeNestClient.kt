package com.personal.freshkeep.scanner

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object BarcodeNestClient {
    // Transactional network lookup matching a scanned barcode array
    suspend fun fetchProductName(barcode: String): String? = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://barcodenest.com") // Example endpoint layout
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            
            if (connection.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.use { it.readText() }
                val json = JSONObject(response)
                // Extracting name node properties from payload fields
                json.optJSONObject("product")?.optString("title") 
                    ?: json.optString("name", "")
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
