package io.github.betterclient.bangify

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@SpringBootApplication
class BangifyApplication

const val DEFAULT_BANG = "g" //google by default
lateinit var defBang: BangK

fun main(args: Array<String>) {
	BangExtractor.extract()
	defBang = bangs.find { it.bangSyntax == DEFAULT_BANG }!!

	runApplication<BangifyApplication>(*args)
}

@RestController
class BaseController {
	@GetMapping("/")
	fun index(@RequestParam("q", required = true) query: String): RedirectView {
		val regex = Regex("!(\\S+)", RegexOption.IGNORE_CASE)
		val match = regex.find(query)
		val bangCandidate = match?.groupValues[0]?.lowercase()?.substring(1)
		val bang = bangs.find {
			it.bangSyntax == bangCandidate
		} ?: defBang

		val cleanQuery = query.replace(regex, "").trim()

		println("Redirecting to ${bang.baseURL} for query -> \"$cleanQuery\"")
		if (cleanQuery == "") return RedirectView("https://${bang.baseURL}")

		return RedirectView(bang.url.replace(
			"{{{s}}}",
			URLEncoder.encode(cleanQuery, StandardCharsets.UTF_8),
		))
	}
}