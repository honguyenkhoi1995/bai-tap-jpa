Bai tap JPA

Run:
mvn spring-boot:run

CRUD:
POST /api/students
GET /api/students
GET /api/students/{id}
PUT /api/students/{id}
DELETE /api/students/{id}

JPQL:
GET /api/courses?durationGreaterThan=10
GET /api/students/search?name=John
GET /api/courses/count

Registration:
POST /api/students/{studentId}/courses
GET /api/students/{studentId}/courses
GET /api/courses/{courseId}/students

Many-to-Many:
POST /api/students/{studentId}/courses/{courseId}
DELETE /api/students/{studentId}/courses/{courseId}

Specification:
GET /api/students/search?name=John&ageFrom=18&ageTo=30&email=@gmail.com&page=0&size=10&sort=name,asc

Course pagination:
GET /api/courses?page=0&size=5&sort=duration,desc
