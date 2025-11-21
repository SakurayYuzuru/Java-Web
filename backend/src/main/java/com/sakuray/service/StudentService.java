package com.sakuray.service;

import com.sakuray.entity.Student;
import com.sakuray.dto.student.*;
import java.util.*;
import org.springframework.data.domain.Page;

public interface StudentService {
    String add(AddDTO dto);
    String update(Long id, UpdateDTO dto);
    String delete(Long id);
    Page<Student> getStudentByPage(int page, int size);
    Page<Student> getStudentByName(String name, int page, int size);
}
