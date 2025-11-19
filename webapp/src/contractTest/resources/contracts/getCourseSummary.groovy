package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name("get_course_summary_by_id")
    description("should return a minimal course summary")

    request {
        method GET()
        url("/api/courses/0f13f5ad-0c89-4f4b-905c-8a7b2d893fa0/summary")
        headers {
            accept(applicationJson())
        }
    }

    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body(
                uuid: value(producer(regex(uuid())), consumer("0f13f5ad-0c89-4f4b-905c-8a7b2d893fa0")),
                name: "Spring Cloud Fundamentals!!",
                teacherFullName: "Some Wise Man",
                sectionCount: 5,
                enrolledStudentCount: 42
        )
        bodyMatchers {
            jsonPath('$.sectionCount', byType())
            jsonPath('$.enrolledStudentCount', byType())
        }
    }
}
