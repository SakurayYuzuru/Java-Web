package com.sakuray.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sakuray.entity.Student;

@Repository
public interface StudentRepo extends JpaRepository<Student, Long> {
    Page<Student> findByStudentNameContaining(String name, Pageable pageable);
    Student findByStudentName(String name);
    Page<Student> findAll(Pageable pageable);
}
