plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.victor.lib.aar"
    compileSdk = 34

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("alipaySdk") {
                groupId = "com.victor.lib.aar"
                artifactId = "alipay-sdk"
                version = "15.7.4"

                artifact("$projectDir/libs/alipaySdk-15.7.4-20200228192259.aar")
            }

            create<MavenPublication>("qmfPpplugin") {
                groupId = "com.victor.lib.aar"
                artifactId = "qmf-ppplugin-android"
                version = "3.1.8"

                artifact("$projectDir/libs/qmf-ppplugin-android-3.1.8.aar")
            }
        }

        repositories {
            maven {
                // 发布到 Maven Local
                url = uri("${rootProject.projectDir}/repo")
            }
        }
    }
}