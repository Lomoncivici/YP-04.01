package com.mpt.journal.controller;

import com.mpt.journal.entity.Course;
import com.mpt.journal.service.CourseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courses;

    public CourseController(CourseService courses) {
        this.courses = courses;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("courses", courses.listAll());
        return "courseList";
    }

    @PostMapping("/add")
    public String add(@RequestParam String title,
                      @RequestParam int year,
                      @RequestParam String teacher) {
        courses.create(new Course(0, title, year, teacher));
        return "redirect:/courses";
    }

    @PostMapping("/update")
    public String update(@RequestParam int id,
                         @RequestParam String title,
                         @RequestParam int year,
                         @RequestParam String teacher) {
        courses.update(new Course(id, title, year, teacher));
        return "redirect:/courses";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam int id) {
        courses.delete(id);
        return "redirect:/courses";
    }
}