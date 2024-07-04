package io.openfeedback.resources

import cafe.adriel.lyricist.LyricistStrings

@LyricistStrings(languageTag = "en", default = true)
val EnStrings = Strings(
    comments = CommentStrings(
        titleSection = "Comments",
        titleInput = "Your comment",
        actionSend = "Submit comment",
        nbVotes = { nbVotes: Int -> "$nbVotes votes" }
    ),
    poweredBy = "Powered by",
    fromYou = ", from you"
)
