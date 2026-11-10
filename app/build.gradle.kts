plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

// Normalize resource file names before the build: lowercase, [a-z0-9._] only,
// and a resource name must start with a letter, so prefix "x" if it starts with a digit.
val resDir = layout.projectDirectory.dir("src/main/res").asFile
if (resDir.exists()) {
    resDir.walkTopDown().filter { it.isFile }.forEach { f ->
        var nb = f.name.lowercase().replace(Regex("[^a-z0-9._]"), "_")
        if (nb.isNotEmpty() && nb[0].isDigit()) nb = "x" + nb
        if (nb != f.name) f.renameTo(f.parentFile.resolve(nb))
    }
}
android {
    namespace = "com.arnoldsroom"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.arnoldsroom"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes { release { isMinifyEnabled = false } }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
}
dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.01.00"))
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.media3:media3-exoplayer:1.5.1")
    implementation("androidx.media3:media3-ui:1.5.1")
    implementation("androidx.media3:media3-common:1.5.1")
}
