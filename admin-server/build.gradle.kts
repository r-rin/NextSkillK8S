plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation("de.codecentric:spring-boot-admin-starter-server")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
}

tasks.bootJar {
    archiveFileName.set("NextSkill-springboot-admin-service.jar")
}
