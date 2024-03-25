import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension


inline fun <reified T> Project.extensionOrNull(): T? {
    return extensions.findByType(T::class.java)
}

inline fun <reified T> Project.extension(): T {
    return extensionOrNull<T>() ?: error("No extension of type '${T::class.java.name}")
}

inline fun <reified T> Project.extension(block: T.() -> Unit) {
    extension<T>().apply(block)
}


val KotlinMultiplatformExtension.compose: ComposePlugin.Dependencies
    get() {
        return (this as ExtensionAware).extensions.getByName("compose") as ComposePlugin.Dependencies
    }
