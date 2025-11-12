plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
	implementation("org.springframework.cloud:spring-cloud-config-server")
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    implementation("org.springframework.boot:spring-boot-starter-actuator:3.5.7")
    implementation("io.micrometer:micrometer-registry-prometheus:1.16.0")
    implementation("de.codecentric:spring-boot-admin-starter-client:3.5.6")
}

tasks.bootJar {
    archiveFileName.set("NextSkill-config-service.jar")
}
