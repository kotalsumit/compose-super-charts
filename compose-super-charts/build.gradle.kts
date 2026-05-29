import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("com.vanniktech.maven.publish")
}

group = "io.github.kotalsumit"
version = "1.0.0"

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    
    jvm("desktop")
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ChartLibrary"
            isStatic = true
        }
    }
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

android {
    namespace = "com.composesupercharts"
    compileSdk = 36

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates(
        groupId = "io.github.kotalsumit",
        artifactId = "compose-super-charts",
        version = "1.0.0"
    )

    pom {
        name.set("Compose Super Charts")
        description.set("A Compose Multiplatform charting library for Android, iOS, and desktop.")
        inceptionYear.set("2026")
        url.set("https://github.com/kotalsumit/compose-super-charts")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("kotalsumit")
                name.set("Sumit Kotal")
                url.set("https://github.com/kotalsumit")
            }
        }

        scm {
            url.set("https://github.com/kotalsumit/compose-super-charts")
            connection.set("scm:git:git://github.com/kotalsumit/compose-super-charts.git")
            developerConnection.set("scm:git:ssh://git@github.com/kotalsumit/compose-super-charts.git")
        }
    }
}

tasks.withType<org.gradle.plugins.signing.Sign>().configureEach {
    onlyIf {
        gradle.startParameter.taskNames.none { taskName ->
            taskName.contains("MavenLocal")
        }
    }
}
