import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm()
    
    android {
       namespace = "git.alektro3000.messenger.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }
    
    sourceSets {

        commonMain.dependencies {

            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.components.resources)
            implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")

            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.9.0-beta05")

            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.8.0")

            implementation("io.coil-kt.coil3:coil-compose:3.5.0")
            implementation("androidx.paging:paging-compose:3.5.0")

            implementation(libs.androidx.datastore.core)
            implementation(libs.androidx.datastore.preferences.core)

            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.paging)
        }

        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.ktx)

            implementation(libs.koin.android)

            implementation("io.ktor:ktor-client-okhttp:3.2.0")
            
            implementation("io.coil-kt.coil3:coil-network-okhttp:3.5.0")

            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.preferences)

        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)

            implementation("io.ktor:ktor-client-cio:3.2.0")

            implementation("io.coil-kt.coil3:coil-network-okhttp:3.5.0")
            implementation(libs.androidx.foundation.layout.desktop)
            implementation(libs.androidx.material3.desktop)
            implementation(libs.androidx.sqlite.bundled.jvm)

            implementation(libs.components.splitpane.desktop)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspJvm", libs.androidx.room.compiler)
}