plugins {
    id("java")
}

group = "ukma.springboot.nextskill"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-activemq")
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation("org.springframework.boot:spring-boot-starter-actuator:3.5.7")
    implementation("de.codecentric:spring-boot-admin-starter-client:3.5.6")
}

tasks.bootJar {
    archiveFileName.set("NextSkill-email-service.jar")
}

tasks.test {
    useJUnitPlatform()
}
