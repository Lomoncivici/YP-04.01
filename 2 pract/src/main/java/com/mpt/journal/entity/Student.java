package com.mpt.journal.entity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Student {
    private int id;
    private String name;
    private String lastName;
    private String firstName;
    private Integer courseId;
    private boolean deleted;
    private LocalDateTime deletedAt;

    public Student(int id, String name, String lastName, String firstName, Integer courseId) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.firstName = firstName;
        this.courseId = courseId;
        this.deleted = false;
        this.deletedAt = null;
    }

    public Student() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student student)) return false;
        return id == student.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}