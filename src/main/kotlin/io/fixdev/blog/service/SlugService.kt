package io.fixdev.blog.service

import org.springframework.stereotype.Service

@Service
class SlugService {
    private val translitMap = mapOf(
        'а' to "a", 'б' to "b", 'в' to "v", 'г' to "g", 'д' to "d",
        'е' to "e", 'ё' to "yo", 'ж' to "zh", 'з' to "z", 'и' to "i",
        'й' to "y", 'к' to "k", 'л' to "l", 'м' to "m", 'н' to "n",
        'о' to "o", 'п' to "p", 'р' to "r", 'с' to "s", 'т' to "t",
        'у' to "u", 'ф' to "f", 'х' to "kh", 'ц' to "ts", 'ч' to "ch",
        'ш' to "sh", 'щ' to "shch", 'ъ' to "", 'ы' to "y", 'ь' to "",
        'э' to "e", 'ю' to "yu", 'я' to "ya"
    )

    fun generate(title: String): String {
        return title.lowercase()
            .map { translitMap[it] ?: it.toString() }
            .joinToString("")
            .replace(Regex("[^a-z0-9\\s-]"), "")
            .trim()
            .replace(Regex("\\s+"), "-")
            .replace(Regex("-+"), "-")
    }
}
