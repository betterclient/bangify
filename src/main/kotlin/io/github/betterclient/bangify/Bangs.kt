package io.github.betterclient.bangify

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URI

//tool to extract bangs from https://duckduckgo.com/bang.js
//serializable bangs
@Serializable
data class BangS(val c: String?, val d: String?, val r: Int?, val s: String?, val sc: String?, val t: String?, val u: String?)

//kotlin side bangs (only includes what the code uses)
@Serializable
data class BangK(val category: String, val bangSyntax: String, val url: String, val baseURL: String)

object BangExtractor {
    @OptIn(ExperimentalSerializationApi::class)
    val json: Json = Json { explicitNulls = false }

    fun extract() {
        val f = (URI("https://duckduckgo.com/bang.js").toURL().openConnection() as HttpURLConnection).let {
            //?? why is this required ??
            it.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/136.0.0.0 Safari/537.36"
            )
            return@let it.inputStream.use { iss ->
                String(iss.readAllBytes())
            }
        }

        val bangs0: MutableList<BangS> = json.decodeFromString(f)
        val bangs1 = mutableListOf<BangK>()
        for (bang in bangs0) {
            bangs1.add(BangK(bang.c ?: bang.sc ?: "N/A", bang.t!!, bang.u!!, bang.d!!))
        }

        bangs.addAll(bangs1)
    }
}

val bangs = mutableListOf<BangK>()