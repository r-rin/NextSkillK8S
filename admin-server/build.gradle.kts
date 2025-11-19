plugins {
    java
}

dependencies {
    implementation(project(":common"))
    implementation("de.codecentric:spring-boot-admin-starter-server")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner")
    testImplementation(project(mapOf("path" to ":webapp", "configuration" to "stubs")))
}

tasks.bootJar {
    archiveFileName.set("NextSkill-springboot-admin-service.jar")
}

tasks.test {
    useJUnitPlatform()
}