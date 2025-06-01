plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

// Function to load properties from local.properties
fun getApiKey(propertyKey: String, project: Project): String {
    val properties = java.util.Properties()
    val localPropertiesFile = project.rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(localPropertiesFile.inputStream())
    }
    return properties.getProperty(propertyKey, "")
}

android {
    namespace = "com.example.dojomovie"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.dojomovie"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Make the API key available as a BuildConfig field
        buildConfigField("String", "MAPS_API_KEY", "\"${getApiKey("MAPS_API_KEY", project)}\"")
        // Make the API key available to the Android Manifest
        manifestPlaceholders["MAPS_API_KEY"] = getApiKey("MAPS_API_KEY", project)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // Material Design 3 - UPDATED VERSION
    implementation("com.google.android.material:material:1.11.0")

    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Google Maps & Location
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.1.0")

    // Network & Data
    implementation("com.android.volley:volley:1.2.1")

    // UI Components
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // Coordinator Layout for Detail Film
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

    // Image Loading
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}