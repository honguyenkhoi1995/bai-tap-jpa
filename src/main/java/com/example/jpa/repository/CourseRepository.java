package com.example.jpa.repository;

import com.example.jpa.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c WHERE c.duration > :hours")
    List<Course> findByDurationGreaterThan(int hours);

    @Query("SELECT COUNT(c) FROM Course c")
    long countCourses();
}
