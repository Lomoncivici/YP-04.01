package com.mpt.journal.controller;

import com.mpt.journal.entity.Course;
import com.mpt.journal.repository.CourseRepository;
import com.mpt.journal.repository.DepartmentRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseRepository courses;
    private final DepartmentRepository departments;

    public CourseController(CourseRepository courses, DepartmentRepository departments) {
        this.courses = courses;
        this.departments = departments;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int pageIndex,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "false") boolean onlyActive,
                       @RequestParam(defaultValue = "title") String sort,
                       @RequestParam(defaultValue = "asc") String dir,
                       Model model) {
        size = Math.max(10, size);
        Sort.Direction d = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(d, sort));
        Page<Course> page = onlyActive ? courses.findByDeletedFalse(pageable)
                : courses.findAll(pageable);

        model.addAttribute("page", page);
        model.addAttribute("onlyActive", onlyActive);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("departments", departments.findByDeletedFalseOrderByNameAsc());
        model.addAttribute("course", new Course());
        return "courseList";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("course") Course c) {
        courses.save(c);
        return "redirect:/courses";
    }

    @PostMapping("/soft-delete")
    public String softDelete(@RequestParam Long id) {
        courses.findById(id).ifPresent(x -> { x.setDeleted(true); courses.save(x); });
        return "redirect:/courses?onlyActive=false";
    }

    @PostMapping("/hard-delete")
    public String hardDelete(@RequestParam Long id) {
        courses.deleteById(id);
        return "redirect:/courses";
    }

    @PostMapping("/delete")
    public String legacyDelete(@RequestParam Long id, @RequestParam(defaultValue = "logical") String type) {
        return "physical".equalsIgnoreCase(type) ? hardDelete(id) : softDelete(id);
    }
}
