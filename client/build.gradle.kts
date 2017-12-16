import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

buildscript {
    var kotlinVer: String by extra

    kotlinVer = "1.2.10"

    repositories {
        jcenter()
        mavenCentral()
    }

    dependencies {
        classpath(kotlin(module = "gradle-plugin", version = kotlinVer))
    }
}

apply {
    plugin("kotlin2js")
}

val kotlinVer: String by extra

repositories {
    maven { url = uri("libs") }
}

dependencies {
    val websceneClientVer = "0.1-SNAPSHOT"

    "compile"(kotlin(module = "stdlib-js", version = kotlinVer))
    "compile"("org.webscene:webscene-client:$websceneClientVer")
}

val compileKotlin2Js by tasks.getting(Kotlin2JsCompile::class) {
    val fileName = "client.js"
    val dirPath = "${projectDir.absolutePath}/web"

    kotlinOptions.outputFile = "$dirPath/$fileName"
    kotlinOptions.sourceMap = true
    doFirst { File(dirPath).deleteRecursively() }
}
val build by tasks
val assembleWeb by tasks.creating(Copy::class) {
    dependsOn("classes")
    configurations["compile"].forEach { file ->
        from(zipTree(file.absolutePath)) {
            includeEmptyDirs = false
            include { fileTreeElement ->
                val path = fileTreeElement.path

                path.endsWith(".js") && path.startsWith("META-INF/resources/") || !path.startsWith("META_INF/")
            }
        }
    }
    from(compileKotlin2Js.destinationDir)
    into("${projectDir.absolutePath}/web")
}

task<Copy>("deployClientToServer") {
    dependsOn(compileKotlin2Js, assembleWeb)
    from("${projectDir.absolutePath}/web")
    into("${projectDir.parent}/server/src/main/resources/static")
}