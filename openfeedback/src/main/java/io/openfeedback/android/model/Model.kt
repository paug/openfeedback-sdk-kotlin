package io.openfeedback.android.model

class Project(
        val chipColors: List<String> = emptyList(),
        val voteItems: List<VoteItem> = emptyList()
)

class VoteItem(
        val id: String = "",
        val languages: Map<String, String> = emptyMap(),
        val name: String = "",
        val position: Int = 0,
        val type: String = ""
) {
    fun localizedName(language: String): String {
        return languages.getOrElse(language, {name})
    }
}

enum class VoteStatus(val value: String) {
    Active("active"),
    Deleted("deleted")
}