package com.sakuray.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sakuray.dto.page.PageRequestDTO;
import com.sakuray.dto.student.AddDTO;
import com.sakuray.dto.student.DeleteDTO;
import com.sakuray.dto.student.UpdateDTO;
import com.sakuray.dto.student.SearchDTO;
import com.sakuray.entity.Student;
import com.sakuray.service.StudentService;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    @Autowired
    private StudentService service;

    @PostMapping("/add")
    public String addStudent(@RequestBody AddDTO dto) {
        return service.add(dto);
    }

    @PostMapping("/update")
    public String updateStudent(@RequestBody UpdateDTO dto) {
        return service.update(dto.getId(), dto);
    }

    @PostMapping("/delete")
    public String deleteStudent(@RequestBody DeleteDTO dto) {
        return service.delete(dto.getId());
    }

    @PostMapping("/page")
    public Page<Student> getStudentByPage(@RequestBody PageRequestDTO dto) {
        return service.getStudentByPage(dto.getPage(), dto.getSize());
    }

    @PostMapping("/search")
    public Page<Student> getStudentByName(@RequestBody SearchDTO dto) {
        return service.getStudentByName(dto.getName(), dto.getPage(), dto.getSize());
    }
}
