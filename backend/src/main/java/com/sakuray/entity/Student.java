package com.sakuray.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String studentName;

    @Column(nullable = false, unique = true)
    private String studentId;

    @Column(nullable = true)
    private String school;

    @Column(nullable = false)
    private String className;

    @Column(nullable = true)
    private Double chinese;

    @Column(nullable = true)
    private Double math;

    @Column(nullable = true)
    private Double english;

    @Column(nullable = true)
    private Double physics;

    @Column(nullable = true)
    private Double chemistry;

    public Student() {}

    public Student(String studentName, String studentId, String school, String className,
                   Double chinese, Double math, Double english, Double physics, Double chemistry) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.school = school;
        this.className = className;
        this.chinese = chinese;
        this.math = math;
        this.english = english;
        this.physics = physics;
        this.chemistry = chemistry;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }   

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Double getChinese() {
        return chinese;
    }

    public void setChinese(Double chinese) {
        this.chinese = chinese;
    }

    public Double getMath() {
        return math;
    }

    public void setMath(Double math) {
        this.math = math;
    }

    public Double getEnglish() {
        return english;
    }

    public void setEnglish(Double english) {
        this.english = english;
    }

    public Double getPhysics() {
        return physics;
    }

    public void setPhysics(Double physics) {
        this.physics = physics;
    }

    public Double getChemistry() {
        return chemistry;
    }

    public void setChemistry(Double chemistry) {
        this.chemistry = chemistry;
    }
}
