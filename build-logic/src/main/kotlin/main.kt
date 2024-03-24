import com.android.build.api.dsl.CommonExtension
import io.openfeedback.extensions.configureKotlinAndroid
import io.openfeedback.extensions.configureKotlinCompiler
import org.gradle.api.Project

fun Project.configureAndroid() {
    extensions.getByName("android").apply {
        this as CommonExtension<*,*,*,*,*>
        configureKotlinAndroid()
    }
}

fun Project.configureKotlin() {
    tasks.configureKotlinCompiler()
}