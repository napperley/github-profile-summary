plugins {
    kotlin(module = "jvm") version "1.2.10"
    application
}

application {
    mainClassName = "server.MainKt"
}

dependencies {
    val javalinVer = "1.2.0"
    val jettyWebsocketVer = "9.4.7.v20170914"
    val jacksonKotlinVer = "2.9.2"
    val velocityVer = "1.7"
    val apacheCommonsVer = "3.7"
    val slf4jVer = "1.7.25"
    val egitVer = "2.1.5"
    val kotlinTestVer = "1.2.10"

    compile(kotlin(module = "stdlib-jre8", version = "1.2.10"))
    compile("io.javalin:javalin:$javalinVer")
    compile("org.eclipse.jetty.websocket:websocket-server:$jettyWebsocketVer")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonKotlinVer")
    compile("org.apache.velocity:velocity:$velocityVer")
    compile("org.apache.commons:commons-lang3:$apacheCommonsVer")
    compile("org.slf4j:slf4j-simple:$slf4jVer")
    compile("org.eclipse.mylyn.github:org.eclipse.egit.github.core:$egitVer")
    testCompile("org.jetbrains.kotlin:kotlin-test:$kotlinTestVer")
}