package io.openfeedback.resources

import cafe.adriel.lyricist.LyricistStrings

@LyricistStrings(languageTag = "fr")
internal val FrStrings = Strings(
    notReady = NotReadyStrings(
        title = "Réagissez en live !",
        description = "Encore un peu de patience, vous pourrez partagez vos feedbacks lorsque la session démarrera."
    ),
    comments = CommentStrings(
        titleSection = "Commentaires",
        titleInput = "Votre commentaire",
        actionSend = "Soumettre votre commentaire",
        nbVotes = { nbVotes: Int -> "$nbVotes votes" },
        hidden = "Masqué par l'organisateur. Contactez-le pour plus d'informations."
    ),
    poweredBy = "Proposé par",
    fromYou = ", de vous"
)
