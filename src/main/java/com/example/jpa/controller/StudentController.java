package com.example.jpa.controller;

import com.example.jpa.entity.Student;
import com.example.jpa.entity.Course;
import com.example.jpa.entity.Registration;
import com.example.jpa.repository.StudentRepository;
import com.example.jpa.repository.CourseRepository;
import com.example.jpa.repository.RegistrationRepository;
import com.example.jpa.specification.StudentSpecification;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentRepository students;
    private final CourseRepository courses;
    private final RegistrationRepository registrations;

    public StudentController(StudentRepository students, CourseRepository courses, RegistrationRepository registrations) {
        this.students = students;
        this.courses = courses;
        this.registrations = registrations;
    }

    @PostMapping
    public Student create(@RequestBody Student student) {
        return students.save(student);
    }

    @GetMapping
    public List<Student> all() {
        return students.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> get(@PathVariable Long id) {
        return students.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> update(@PathVariable Long id, @RequestBody Student input) {
        return students.findById(id).map(s -> {
            s.setName(input.getName());
            s.setEmail(input.getEmail());
            s.setAge(input.getAge());
            return ResponseEntity.ok(students.save(s));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!students.existsById(id)) return ResponseEntity.notFound().build();
        students.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public Page<Student> search(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Integer ageFrom,
        @RequestParam(required = false) Integer ageTo,
        @RequestParam(required = false) String email,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id,asc") String sort) {

        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc")
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));

        Specification<Student> spec = Specification.allOf(
            StudentSpecification.nameContains(name),
            StudentSpecification.ageFrom(ageFrom),
            StudentSpecification.ageTo(ageTo),
            StudentSpecification.emailEndsWith(email)
        );
        return students.findAll(spec, pageable);
    }

    @GetMapping("/{studentId}/courses")
    public List<Course> getCourses(@PathVariable Long studentId) {
        Student student = students.findById(studentId).orElseThrow();
        return registrations.findByStudent(student).stream()
            .map(Registration::getCourse).collect(Collectors.toList());
    }

    @PostMapping("/{studentId}/courses")
    public List<Registration> registerCourses(@PathVariable Long studentId, @RequestBody List<Long> courseIds) {
        Student student = students.findById(studentId).orElseThrow();
        List<Registration> result = new ArrayList<>();
        for (Long courseId : courseIds) {
            Course course = courses.findById(courseId).orElseThrow();
            Registration r = new Registration();
            r.setStudent(student);
            r.setCourse(course);
            r.setRegistrationDate(LocalDate.now());
            result.add(registrations.save(r));
        }
        return result;
    }

    @PostMapping("/{studentId}/courses/{courseId}")
    public ResponseEntity<Void> enroll(@PathVariable Long studentId, @PathVariable Long courseId) {
        Student s = students.findById(studentId).orElseThrow();
        Course c = courses.findById(courseId).orElseThrow();
        if (s.getCourses().stream().noneMatch(x -> x.getId().equals(courseId))) {
            s.getCourses().add(c);
            students.save(s);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{studentId}/courses/{courseId}")
    public ResponseEntity<Void> unenroll(@PathVariable Long studentId, @PathVariable Long courseId) {
        Student s = students.findById(studentId).orElseThrow();
        s.getCourses().removeIf(c -> c.getId().equals(courseId));
        students.save(s);
        return ResponseEntity.noContent().build();
    }
}
