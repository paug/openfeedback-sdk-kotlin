object EnvVarKeys {
    object Nexus {
        const val username = "SONATYPE_NEXUS_USERNAME"
        const val password = "SONATYPE_NEXUS_PASSWORD"
        const val profileId = "IO_OPENFEEDBACK_PROFILE_ID"
    }

    object GPG {
        const val privateKey = "OPENFEEDBACK_GPG_PRIVATE_KEY"
        const val password = "OPENFEEDBACK_GPG_PRIVATE_KEY_PASSWORD"
    }

    object GitHub {
        const val event = "GITHUB_EVENT_NAME"
        const val ref = "GITHUB_REF"
    }
}