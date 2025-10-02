plugins {
    id("java")
}

group = "ukma.springboot.nextskill"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    //Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")

    runtimeOnly("org.postgresql:postgresql")

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.modulith:spring-modulith-starter-core:1.2.1")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test:1.2.1")
    testImplementation("org.springframework.modulith:spring-modulith-docs:1.2.1")
}

tasks.bootJar {
    archiveFileName.set("NextSkill-webapp.jar")
}

tasks.test {
    useJUnitPlatform()
}