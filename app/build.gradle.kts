import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.egoriku.application.configureProductFlavors
import com.egoriku.application.provideVersionCode
import com.egoriku.application.provideVersionName
import com.egoriku.dependencies.Libs
import com.egoriku.dependencies.Modules.DynamicFeatures
import com.egoriku.dependencies.Modules.Features
import com.egoriku.dependencies.Modules.Libraries
import com.egoriku.dependencies.versions.ProjectVersion
import com.egoriku.ext.*
import org.jetbrains.kotlin.konan.file.File
import org.jetbrains.kotlin.konan.properties.Properties
import org.jetbrains.kotlin.konan.properties.loadProperties
import org.jetbrains.kotlin.konan.properties.saveToFile

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("io.fabric")
}

android {
    buildToolsVersion(ProjectVersion.buildToolsVersion)
    compileSdkVersion(ProjectVersion.compileSdkVersion)

    defaultConfig {
        applicationId = "com.egoriku.ladyhappy"
        minSdkVersion(ProjectVersion.minSdkVersion)
        versionCode = provideVersionCode()
        versionName = provideVersionName()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resConfigs("en", "ru")
    }

    dynamicFeatures = mutableSetOf(DynamicFeatures.postCreator)

    signingConfigs {
        release {
            storeFile = file("lady_happy_key_store.jks")
            storePassword = System.getenv("KEY_STORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isDebuggable = false
            multiDexEnabled = false
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles("proguard-rules.pro", getDefaultProguardFile("proguard-android-optimize.txt"))
            extra["enableCrashlytics"] = true
            extra["alwaysUpdateBuildId"] = true
        }

        debug {
            multiDexEnabled = true
            extra["enableCrashlytics"] = false
            extra["alwaysUpdateBuildId"] = false
        }
    }

    configureBuildFlavors(
            onLocalBuild = {
                configureProductFlavors()
            },
            onRemoteBuild = {
                println("It's app center build.")
            }
    )

    gradle.taskGraph.whenReady {
        if (hasTask(":app:assembleAllFeaturesDebug")
                || hasTask(":app:assembleAllFeaturesRelease")
                || hasTask(":app:assembleJustLandingDebug")
                || hasTask(":app:assembleJustPhotoReportDebug")
                || hasTask(":app:assembleJustCatalogDebug")
                || hasTask(":app:assembleJustSettingsDebug")
        ) {
            autoIncrementBuildVersionNumber()
        }
    }

    applicationVariants.all {
        outputs.all {
            if (this is BaseVariantOutputImpl) {
                outputFileName = "${"Lady Happy"}.${name}_${versionName}.apk"
            }
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

withProjects(
        Features.catalog,
        Features.landing,
        Features.launchScreen,
        Features.login,
        Features.mainScreen,
        Features.photoReport,
        Features.settings,

        Libraries.arch,
        Libraries.auth,
        Libraries.core,
        Libraries.extensions,
        Libraries.navigation,
        Libraries.network,
        Libraries.rendering
)

withLibraries(
        Libs.appcompat,
        Libs.coroutinesAndroid,
        Libs.firebaseAuth,
        Libs.firebaseCore,
        Libs.firebaseFirestoreKtx,
        Libs.koinAndroid,
        Libs.koinCore,
        Libs.kotlin,
        Libs.material,
        Libs.playCore
)

withKapt(Libs.dagger andKapt Libs.daggerCompiler)

dependencies {
    implementation(Libs.crashlytics) {
        isTransitive = true
    }

    debugImplementation(Libs.beagle)
    debugImplementation(Libs.leakCanary)

    testImplementation(Libs.junit)
}

apply {
    plugin("com.google.gms.google-services")
}

fun autoIncrementBuildVersionNumber() {
    val properties: Properties = loadProperties("$rootDir/app/version.properties")

    val newVersion = properties.propertyInt("BUILD_VERSION").inc()

    if (newVersion == 1000) {
        val subVersion = properties.propertyInt("SUB_VERSION").inc()

        properties["SUB_VERSION"] = subVersion.toString()
        properties["BUILD_VERSION"] = 0.toString()
    } else {
        properties["BUILD_VERSION"] = newVersion.toString()
    }

    properties.saveToFile(File("$rootDir/app/version.properties"))
}