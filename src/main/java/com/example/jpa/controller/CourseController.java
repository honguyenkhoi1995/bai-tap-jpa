package com.example.jpa.controller;

import com.example.jpa.entity.Course;
import com.example.jpa.entity.Student;
import com.example.jpa.entity.Registration;
import com.example.jpa.repository.CourseRepository;
import com.example.jpa.repository.RegistrationRepository;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseRepository courses;
    private final RegistrationRepository registrations;

    public CourseController(CourseRepository courses, RegistrationRepository registrations) {
        this.courses = courses;
        this.registrations = registrations;
    }

    @PostMapping
    public Course create(@RequestBody Course course) {
        return courses.save(course);
    }

    @GetMapping
    public Page<Course> all(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id,asc") String sort,
        @RequestParam(required = false) Integer durationGreaterThan) {

        String[] p = sort.split(",");
        Sort.Direction d = p.length > 1 && p[1].equalsIgnoreCase("desc")
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(d, p[0]));

        if (durationGreaterThan != null) {
            List<Course> result = courses.findByDurationGreaterThan(durationGreaterThan);
            int start = Math.min((int) pageable.getOffset(), result.size());
            int end = Math.min(start + pageable.getPageSize(), result.size());
            return new PageImpl<>(result.subList(start, end), pageable, result.size());
        }
        return courses.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Course get(@PathVariable Long id) {
        return courses.findById(id).orElseThrow();
    }

    @GetMapping("/count")
    public long count() {
        return courses.countCourses();
    }

    @GetMapping("/{courseId}/students")
    public List<Student> getStudents(@PathVariable Long courseId) {
        Course course = courses.findById(courseId).orElseThrow();
        return registrations.findByCourse(course).stream()
            .map(Registration::getStudent).collect(Collectors.toList());
    }
}
