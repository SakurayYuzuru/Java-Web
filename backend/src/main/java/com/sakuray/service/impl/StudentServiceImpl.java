package com.sakuray.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.*;
import com.sakuray.dto.student.AddDTO;
import com.sakuray.dto.student.UpdateDTO;
import com.sakuray.entity.Student;
import com.sakuray.repo.StudentRepo;
import com.sakuray.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentRepo repo;

    @Override
    public String add(AddDTO dto) {
        Student student = repo.findByStudentName(dto.getStudentName());
        if (student != null) {
            return "学生已存在";
        }

        Student newStudent = new Student(dto.getStudentName(), dto.getStudentId(), dto.getSchool(), dto.getClassName(),
                dto.getChinese(), dto.getMath(), dto.getEnglish(), dto.getPhysics(), dto.getChemistry());
        repo.save(newStudent);

        return "添加成功";
    }

    @Override
    public String update(Long id, UpdateDTO dto) {
        Student student = repo.findById(id).orElse(null);
        if (student == null) {
            return "学生不存在";
        }

        student.setStudentName(dto.getStudentName());
        student.setStudentId(dto.getStudentId());
        student.setSchool(dto.getSchool());
        student.setClassName(dto.getClassName());
        student.setChinese(dto.getChinese());
        student.setMath(dto.getMath());
        student.setEnglish(dto.getEnglish());
        student.setPhysics(dto.getPhysics());
        student.setChemistry(dto.getChemistry());

        repo.save(student);

        return "更新成功";
    }

    @Override
    public String delete(Long id) {
        Student student = repo.findById(id).orElse(null);
        if (student == null) {
            return "学生不存在";
        }

        repo.delete(student);

        return "删除成功";
    }

    @Override
    public Page<Student> getStudentByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findAll(pageable);
    }
    
    @Override
    public Page<Student> getStudentByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findByStudentNameContaining(name, pageable);
    }
}
