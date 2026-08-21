package com.example.jpa.repository;

import com.example.jpa.entity.Registration;
import com.example.jpa.entity.Student;
import com.example.jpa.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByStudent(Student student);
    List<Registration> findByCourse(Course course);
}
