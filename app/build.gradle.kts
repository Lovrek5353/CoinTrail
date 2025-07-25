    plugins {
        alias(libs.plugins.android.application)
        alias(libs.plugins.kotlin.android)
        alias(libs.plugins.kotlin.compose)
        id("com.google.gms.google-services")
        id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23" // Match your Kotlin version
    }

    android {
        namespace = "com.example.cointrail"
        compileSdk = 35

        defaultConfig {
            applicationId = "com.example.cointrail"
            minSdk = 35
            targetSdk = 35
            versionCode = 1
            versionName = "1.0"

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        buildFeatures {
            compose = true
        }
    }

    dependencies {

        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.ui)
        implementation(libs.androidx.ui.graphics)
        implementation(libs.androidx.ui.tooling.preview)
        implementation(libs.androidx.material3)
        implementation(libs.firebase.firestore.ktx)
        implementation(libs.firebase.auth.ktx)
        implementation(libs.androidx.navigation.compose)
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(platform(libs.androidx.compose.bom))
        androidTestImplementation(libs.androidx.ui.test.junit4)
        debugImplementation(libs.androidx.ui.tooling)
        debugImplementation(libs.androidx.ui.test.manifest)


        // Import the Firebase BoM
        implementation(platform(libs.firebase.bom))


        // TODO: Add the dependencies for Firebase products you want to use
        // When using the BoM, don't specify versions in Firebase dependencies
        //implementation(libs.firebase.analytics)

        implementation(libs.koin.androidx.compose)
        implementation(libs.koin.core)
        implementation(libs.koin.android)

        implementation(platform(libs.firebase.bom.v33150))
        implementation(libs.firebase.auth)

        //implementation (libs.androidx.material.icons.extended)

        // Ktor client core and Android engine
        implementation(platform(libs.ktor.bom))
        implementation(libs.ktor.client.core)
        implementation(libs.ktor.client.android)
        implementation(libs.ktor.client.content.negotiation)
        implementation(libs.ktor.serialization.kotlinx.json)
        implementation(libs.ktor.client.logging)

        // Kotlinx serialization
        implementation(libs.kotlinx.serialization.json)

        implementation(libs.slf4j.android)


        // Add the dependencies for any other desired Firebase products
        // https://firebase.google.com/docs/android/setup#available-libraries
    }