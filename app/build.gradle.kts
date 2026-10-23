plugins {
    id("com.android.application")
}

val releaseSigning = listOf(
    "MUSE3_KEYSTORE",
    "MUSE3_KEYSTORE_PASS",
    "MUSE3_KEY_ALIAS",
    "MUSE3_KEY_PASS"
).associateWith { name ->
    System.getenv(name)
}

val hasReleaseSigning = releaseSigning.values.all {
    !it.isNullOrBlank()
}

android {
    namespace = "com.fedos.musenumberthree"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fedos.musenumberthree"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("museRelease") {
                storeFile = rootProject.file(
                    requireNotNull(releaseSigning["MUSE3_KEYSTORE"])
                )
                storePassword = releaseSigning["MUSE3_KEYSTORE_PASS"]
                keyAlias = releaseSigning["MUSE3_KEY_ALIAS"]
                keyPassword = releaseSigning["MUSE3_KEY_PASS"]
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("museRelease")
            }
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.webkit:webkit:1.17.1")
    implementation("com.google.android.material:material:1.12.0")
}
