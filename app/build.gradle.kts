plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlinx-serialization")
}
android {
    compileSdkVersion(28)

    defaultConfig {
        multiDexEnabled = true
        applicationId = "ru.tradernet"
        minSdkVersion(21)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "0.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {

    }
    lintOptions {
        disable("RestrictedApi")
    }
    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            val API_URL = "https://tradernet.ru/api"
            buildConfigField("String", "API_URL", "\"$API_URL\"")
            isMinifyEnabled = false
        }
        getByName("release") {
            val API_URL = "https://tradernet.ru/api"
            buildConfigField("String", "API_URL", "\"$API_URL\"")

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
    packagingOptions {
        exclude("META-INF/main.kotlin_module")
        exclude("META-INF/kotlinx-io.kotlin_module")
        exclude("META-INF/atomicfu.kotlin_module")
        exclude("META-INF/kotlinx-coroutines-io.kotlin_module")
        exclude("META-INF/kotlinx-coroutines-core.kotlin_module")
        exclude("META-INF/ktor-http.kotlin_module")
        exclude("META-INF/ktor-utils.kotlin_module")
        exclude("META-INF/ktor-client-core.kotlin_module")
    }
}
dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.appcompat", "appcompat", "1.0.2")
    implementation("androidx.core", "core-ktx", "1.1.0-beta01")
    implementation("androidx.lifecycle", "lifecycle-extensions", "2.1.0-beta01")
    implementation("androidx.lifecycle", "lifecycle-viewmodel-ktx", "2.1.0-beta01")
    implementation("androidx.recyclerview", "recyclerview", "1.0.0")
    implementation("androidx.constraintlayout", "constraintlayout", "1.1.3")
    implementation("androidx.navigation", "navigation-fragment-ktx", "2.1.0-alpha04")
    implementation("androidx.navigation", "navigation-fragment", "2.1.0-alpha04")
    implementation("androidx.legacy", "legacy-support-v4", "1.0.0")
    implementation("org.jetbrains.kotlin", "kotlin-stdlib-jdk7", "1.3.31")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.1.1")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-android", "1.1.1")
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-runtime", "0.10.0")
    implementation("com.google.android.material", "material", "1.0.0")
    implementation("com.google.android", "flexbox", "1.1.0")
    implementation("io.ktor", "ktor-client-android", "1.2.1")
    implementation("com.github.nkzawa", "socket.io-client", "0.6.0")
    implementation("com.android.support", "multidex", "1.0.0")
    implementation("org.kodein.di", "kodein-di-erased-jvm", "6.1.0")
    implementation("com.orhanobut", "logger", "2.2.0")
    implementation("com.caverock", "androidsvg-aar", "1.3")
    implementation("com.balysv", "material-ripple", "1.0.2")
    implementation("com.squareup.picasso", "picasso", "2.71828")
    implementation("com.nabinbhandari.android", "permissions", "3.8")
    implementation("com.github.medyo", "fancybuttons", "1.9.1")

    testImplementation("junit", "junit", "4.12")
    testImplementation("org.jetbrains.kotlin", "kotlin-test-junit", "1.3.31")
    testImplementation("org.jetbrains.kotlin", "kotlin-test", "1.3.31")
    androidTestImplementation("androidx.test", "runner", "1.1.1")
    androidTestImplementation("androidx.test.espresso", "espresso-core", "3.1.1")
}