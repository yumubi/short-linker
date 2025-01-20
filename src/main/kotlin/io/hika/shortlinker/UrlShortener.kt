package io.hika.shortlinker

class UrlShortener {
    companion object {
        private const val ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private const val BASE = ALPHABET.length

        fun encode(num: Long): String {
            var n = num
            val sb = StringBuilder("/")

            while (n >= BASE) {
                val remainder = (n % BASE).toInt()
                sb.append(ALPHABET[remainder])
                n /= BASE
            }

            sb.append(ALPHABET[n.toInt()])
            return sb.toString()
        }
    }
}
