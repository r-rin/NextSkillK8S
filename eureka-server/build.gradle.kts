plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}


dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
}

tasks.bootJar {
    archiveFileName.set("NextSkill-eureka-service.jar")
}
