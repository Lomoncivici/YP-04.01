package com.mpt.journal.entity;

import java.util.Objects;

public class Course {
    private int id;
    private String title;
    private int year;
    private String teacher;

    public Course() {}

    public Course(int id, String title, int year, String teacher) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.teacher = teacher;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getTeacher() { return teacher; }
    public void setTeacher(String teacher) { this.teacher = teacher; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course course = (Course) o;
        return id == course.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}