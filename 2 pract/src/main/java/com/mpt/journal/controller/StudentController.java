package com.mpt.journal.controller;

import com.mpt.journal.entity.Course;
import com.mpt.journal.entity.Student;
import com.mpt.journal.service.CourseService;
import com.mpt.journal.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class StudentController {

    private final StudentService students;
    private final CourseService courses;

    public StudentController(StudentService students, CourseService courses) {
        this.students = students;
        this.courses = courses;
    }

    @GetMapping({"/", "/students"})
    public String listStudents(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(required = false) String q,
                               @RequestParam(required = false) Integer courseId,
                               @RequestParam(required = false) Boolean deleted,
                               @RequestParam(required = false, name = "lastNamePrefix") String lastNamePrefix,
                               Model model) {

        List<Student> base = students.listAll();

        if (StringUtils.hasText(q)) {
            base = students.searchByName(q);
        }

        base = base.stream()
                .filter(s -> courseId == null || Objects.equals(s.getCourseId(), courseId))
                .filter(s -> deleted == null || s.isDeleted() == deleted)
                .filter(s -> lastNamePrefix == null || lastNamePrefix.isBlank() ||
                        (s.getLastName() != null && s.getLastName().toLowerCase(Locale.ROOT)
                                .startsWith(lastNamePrefix.toLowerCase(Locale.ROOT))))
                .collect(Collectors.toList());

        int total = base.size();
        List<Student> pageItems = students.paginate(base, page, size);
        int minSize = 10;
        int appliedSize = Math.max(size, minSize);
        int totalPages = (int) Math.ceil(total / (double) appliedSize);

        model.addAttribute("students", pageItems);
        model.addAttribute("courses", courses.listAll());
        model.addAttribute("page", page);
        model.addAttribute("size", appliedSize);
        model.addAttribute("total", total);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("courseId", courseId);
        model.addAttribute("deleted", deleted);
        model.addAttribute("lastNamePrefix", lastNamePrefix == null ? "" : lastNamePrefix);

        return "studentList";
    }

    @PostMapping("/students/add")
    public String addStudent(@RequestParam String name,
                             @RequestParam String lastName,
                             @RequestParam String firstName,
                             @RequestParam Integer courseId) {
        Student s = new Student(0, name, lastName, firstName, courseId);
        students.create(s);
        return "redirect:/students";
    }

    @PostMapping("/students/update")
    public String updateStudent(@RequestParam int id,
                                @RequestParam String name,
                                @RequestParam String lastName,
                                @RequestParam String firstName,
                                @RequestParam Integer courseId) {
        Student s = students.get(id).orElse(new Student(id, name, lastName, firstName, courseId));
        s.setName(name);
        s.setLastName(lastName);
        s.setFirstName(firstName);
        s.setCourseId(courseId);
        students.update(s);
        return "redirect:/students";
    }

    @PostMapping("/students/delete")
    public String logicalDelete(@RequestParam int id) {
        students.logicalDelete(id);
        return "redirect:/students";
    }

    @PostMapping("/students/delete-physical")
    public String physicalDelete(@RequestParam int id) {
        students.physicalDelete(id);
        return "redirect:/students";
    }

    @PostMapping("/students/delete-batch")
    public String batchDelete(@RequestParam(value = "ids", required = false) List<Integer> ids,
                              @RequestParam(defaultValue = "logical") String type,
                              RedirectAttributes ra) {
        if (ids == null || ids.isEmpty()) {
            ra.addFlashAttribute("error", "Выберите хотя бы одного студента");
            return "redirect:/students";
        }
        if ("physical".equalsIgnoreCase(type)) {
            students.physicalDeleteBatch(ids);
        } else {
            students.logicalDeleteBatch(ids);
        }
        ra.addFlashAttribute("message", "Удалено: " + ids.size());
        return "redirect:/students";
    }
}