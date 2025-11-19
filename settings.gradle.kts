rootProject.name = "NextSkillK8S"

include("webapp")
include("email-service")
include("common")
include("admin-server")

pluginManagement {
    plugins {
        id("io.spring.dependency-management") version "1.1.7"
        id("org.springframework.cloud.contract") version "4.1.4"
    }
}
