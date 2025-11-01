import com.android.build.gradle.LibraryExtension

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

val newBuildDir: Directory = rootProject.layout.buildDirectory.dir("../../build").get()
rootProject.layout.buildDirectory.value(newBuildDir)

subprojects {
    val newSubprojectBuildDir: Directory = newBuildDir.dir(project.name)
    project.layout.buildDirectory.value(newSubprojectBuildDir)
}
subprojects {
    project.evaluationDependsOn(":app")
}

// Ensure library modules coming from plugins (e.g., packages in pub cache)
// have a namespace set to avoid AGP errors like "Namespace not specified".
subprojects {
    // If the project is already evaluated, run immediately, otherwise schedule afterEvaluate.
    val applyNamespaceAction = {
        try {
            val androidExt = extensions.findByName("android")
            if (androidExt is LibraryExtension) {
                val currentNamespace = androidExt.namespace
                if (currentNamespace == null || currentNamespace.isBlank()) {
                    androidExt.namespace = "com.example.${project.name.replace('-', '_')}"
                }
            }
        } catch (e: Exception) {
            // ignore projects that don't expose android extension or on older AGP
        }
    }

    if (project.state.executed) {
        applyNamespaceAction()
    } else {
        afterEvaluate { applyNamespaceAction() }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}