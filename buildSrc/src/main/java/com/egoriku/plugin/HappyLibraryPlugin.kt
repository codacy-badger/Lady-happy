package com.egoriku.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryPlugin
import com.egoriku.dependencies.Libs
import com.egoriku.dependencies.versions.ProjectVersion
import com.egoriku.ext.implementation
import com.egoriku.ext.release
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

open class HappyLibraryPlugin : Plugin<Project> {
   
    override fun apply(project: Project) {
        with(project) {
            plugins.all {
                when (this) {
                    is JavaPlugin -> println("This is JavaPlugin")
                    is LibraryPlugin -> {
                        addCommonPlugins()
                        addAndroidLibrarySection()
                        addCommonDependencies()
                    }
                    is AppPlugin -> {
                        addCommonPlugins()
                        addCommonDependencies()
                    }
                    is KotlinBasePluginWrapper -> {
                        tasks.withType(KotlinCompile::class.java).configureEach {
                            kotlinOptions {
                                jvmTarget = "1.8"
                            }
                        }
                    }
                }
            }
        }
    }

    private fun Project.addCommonPlugins() {
        plugins.apply("kotlin-android")
    }
}

fun Project.addAndroidLibrarySection() = extensions.getByType<BaseExtension>().run {
    defaultConfig {
        minSdkVersion(ProjectVersion.minSdkVersion)
        compileSdkVersion(ProjectVersion.compileSdkVersion)
        versionCode = 1
        versionName = "1.0"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

fun Project.addCommonDependencies() = dependencies {
    implementation(Libs.kotlin)
}