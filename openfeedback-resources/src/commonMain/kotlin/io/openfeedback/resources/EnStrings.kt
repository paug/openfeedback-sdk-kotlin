package io.openfeedback.resources

import cafe.adriel.lyricist.LyricistStrings

@LyricistStrings(languageTag = "en", default = true)
internal val EnStrings = Strings(
    notReady = NotReadyStrings(
        title = "React online!",
        description = "A little more patience, and you'll be able to share your feedback when the session starts."
    ),
    comments = CommentStrings(
        titleSection = "Comments",
        titleInput = "Your comment",
        actionSend = "Submit comment",
        nbVotes = { nbVotes: Int -> "$nbVotes votes" }
    ),
    poweredBy = "Powered by",
    fromYou = ", from you"
)
