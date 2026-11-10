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
        // The backup video engine (libVLC) ships native code;
        // arm64 covers the Fold, the Razr, and nearly every
        // modern phone, and keeps the app from ballooning.
        ndk { abiFilters.add("arm64-v8a") }
    }
    buildTypes { release { isMinifyEnabled = false } }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
}
// Fetch the Baloo 2 cartoon font at build time (kept out of the repo: no binary upload needed).
val balooFile = layout.projectDirectory.file("src/main/res/font/baloo2.ttf").asFile
val fetchBalooFont = tasks.register<Exec>("fetchBalooFont") {
    onlyIf { !balooFile.exists() || balooFile.length() < 10_000 }
    doFirst { balooFile.parentFile.mkdirs() }
    commandLine(
        "curl", "-g", "-fL", "--retry", "3", "-o", balooFile.absolutePath,
        "https://cdn.jsdelivr.net/gh/google/fonts@main/ofl/baloo2/Baloo2%5Bwght%5D.ttf"
    )
}
val balooBoldFile = layout.projectDirectory.file("src/main/res/font/baloo2_bold.ttf").asFile
val fetchBalooBoldFont = tasks.register<Exec>("fetchBalooBoldFont") {
    onlyIf { !balooBoldFile.exists() || balooBoldFile.length() < 10_000 }
    doFirst { balooBoldFile.parentFile.mkdirs() }
    commandLine(
        "curl", "-g", "-fL", "--retry", "3", "-o", balooBoldFile.absolutePath,
        "https://fonts.gstatic.com/s/baloo2/v23/wXK0E3kTposypRydzVT08TS3JnAmtdj9yqpv.ttf"
    )
}
val caveatFile = layout.projectDirectory.file("src/main/res/font/caveat.ttf").asFile
val fetchCaveatFont = tasks.register<Exec>("fetchCaveatFont") {
    onlyIf { !caveatFile.exists() || caveatFile.length() < 10_000 }
    doFirst { caveatFile.parentFile.mkdirs() }
    commandLine(
        "curl", "-g", "-fL", "--retry", "3", "-o", caveatFile.absolutePath,
        "https://cdn.jsdelivr.net/gh/google/fonts@main/ofl/caveat/Caveat%5Bwght%5D.ttf"
    )
}
tasks.named("preBuild") { dependsOn(fetchBalooFont); dependsOn(fetchBalooBoldFont); dependsOn(fetchCaveatFont) }

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
    implementation("androidx.media:media:1.7.0")
    implementation("androidx.media3:media3-exoplayer:1.5.1")
    implementation("androidx.media3:media3-ui:1.5.1")
    implementation("androidx.media3:media3-common:1.5.1")
    implementation("org.videolan.android:libvlc-all:3.6.0")
}
