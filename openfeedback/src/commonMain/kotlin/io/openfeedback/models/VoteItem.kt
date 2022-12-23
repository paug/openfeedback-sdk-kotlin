package io.openfeedback.models

class VoteItem(
    val id: String = "",
    val languages: Map<String, String> = emptyMap(),
    val name: String = "",
    val position: Int = 0,
    val type: String = ""
) {
    fun localizedName(language: String): String {
        return languages.getOrElse(language) { name }
    }
}
