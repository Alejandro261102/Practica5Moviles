// build.gradle.kts (Project: Practica5Moviles)
plugins {
    // Usamos versiones ESTABLES y COMPATIBLES entre sí
    id("com.android.application") version "8.13.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    // KSP 1.9.0-1.0.13 es la pareja exacta de Kotlin 1.9.0
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
}