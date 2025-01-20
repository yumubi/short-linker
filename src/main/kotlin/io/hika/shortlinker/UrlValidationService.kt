package io.hika.shortlinker

class UrlValidationService {

    fun isValidUrl(url: String): Boolean {
        return url.matches(Regex("^(http|https)://.*"))
    }
}
