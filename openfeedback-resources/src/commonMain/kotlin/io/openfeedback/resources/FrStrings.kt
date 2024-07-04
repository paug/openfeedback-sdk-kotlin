package io.openfeedback.resources

import cafe.adriel.lyricist.LyricistStrings

@LyricistStrings(languageTag = "fr")
val FrStrings = Strings(
    comments = CommentStrings(
        titleSection = "Commentaires",
        titleInput = "Votre commentaire",
        actionSend = "Soumettre votre commentaire",
        nbVotes = { nbVotes: Int -> "$nbVotes votes" }
    ),
    poweredBy = "Proposé par",
    fromYou = ", de vous"
)
