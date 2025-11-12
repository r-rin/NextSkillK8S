plugins {
    java
}

dependencies {
	implementation("org.springframework.cloud:spring-cloud-config-server")
}

tasks.bootJar {
    archiveFileName.set("NextSkill-springboot-config-service.jar")
}
