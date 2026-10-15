import org.gradle.api.Project

inline fun <reified T: Any> Project.extensionOrNull(): T? {
    return extensions.findByType(T::class.java)
}

inline fun <reified T: Any> Project.extension(): T {
    return extensionOrNull<T>() ?: error("No extension of type '${T::class.java.name}")
}