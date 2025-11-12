package com.mpt.journal.controller;

import com.mpt.journal.entity.Student;
import com.mpt.journal.repository.CourseRepository;
import com.mpt.journal.repository.TagRepository;
import com.mpt.journal.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class StudentController {

    private final StudentService studentService;
    private final CourseRepository courseRepository;
    private final TagRepository tagRepository;

    public StudentController(StudentService studentService,
                             CourseRepository courseRepository,
                             TagRepository tagRepository) {
        this.studentService = studentService;
        this.courseRepository = courseRepository;
        this.tagRepository = tagRepository;
    }

    @GetMapping({"/", "/students"})
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) Long courseId,
                       @RequestParam(defaultValue = "false") boolean onlyActive, // ← по умолчанию показываем всех
                       @RequestParam(defaultValue = "0") int pageIndex,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "lastName") String sort,
                       @RequestParam(defaultValue = "asc") String dir,
                       Model model) {

        size = Math.max(size, 10);
        Sort.Direction direction = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort srt = Sort.by(direction, sort);

        // ВАЖНО: если onlyActive = true → фильтруем deleted=false,
        // если onlyActive = false → НЕ ФИЛЬТРУЕМ (показываем всех).
        // Для этого передаём showDeleted = null, чтобы сервис не добавлял предикат.
        Boolean showDeletedParam = onlyActive ? Boolean.FALSE : null;

        Page<Student> p = studentService.findPage(q, courseId, showDeletedParam, pageIndex, size, srt);

        model.addAttribute("page", p);
        model.addAttribute("q", q);
        model.addAttribute("courseId", courseId);
        model.addAttribute("onlyActive", onlyActive);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);

        model.addAttribute("courses", courseRepository.findByDeletedFalseOrderByTitleAsc());
        model.addAttribute("student", new Student());
        model.addAttribute("pageIndex", pageIndex);

        return "studentList";
    }

    // ------ CREATE ------
    @PostMapping("/students/add")
    public String add(@Valid @ModelAttribute("student") Student student,
                      BindingResult br,
                      RedirectAttributes ra) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.student", br);
            ra.addFlashAttribute("student", student);
            ra.addFlashAttribute("err", "Проверьте поля формы");
            return "redirect:/students";
        }
        studentService.save(student);
        ra.addFlashAttribute("msg", "Студент сохранён");
        return "redirect:/students";
    }

    // ------ UPDATE ------
    @PostMapping("/students/update")
    public String update(@Valid @ModelAttribute("student") Student student,
                         BindingResult br,
                         RedirectAttributes ra) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("org.springframework.validation.BindingResult.student", br);
            ra.addFlashAttribute("student", student);
            ra.addFlashAttribute("err", "Проверьте поля формы");
            return "redirect:/students";
        }
        studentService.save(student); // save работает и как update при наличии id
        ra.addFlashAttribute("msg", "Студент обновлён");
        return "redirect:/students";
    }

    // ------ DELETE (SOFT/HARD) ------
    @PostMapping("/students/soft-delete")
    public String softDelete(@RequestParam int id, RedirectAttributes ra) {
        studentService.logicalDelete(id);
        ra.addFlashAttribute("msg", "Студент помечен как удалённый");
        return "redirect:/students";
    }

    @PostMapping("/students/hard-delete")
    public String hardDelete(@RequestParam int id, RedirectAttributes ra) {
        studentService.physicalDelete(id);
        ra.addFlashAttribute("msg", "Студент удалён безвозвратно");
        return "redirect:/students";
    }

    // ------ Совместимость со старыми путями ------
    @PostMapping("/students/delete")
    public String legacySoftDelete(@RequestParam int id, RedirectAttributes ra) {
        return softDelete(id, ra);
    }

    @PostMapping("/students/delete-physical")
    public String legacyHardDelete(@RequestParam int id, RedirectAttributes ra) {
        return hardDelete(id, ra);
    }

    // ------ Batch delete ------
    @PostMapping("/students/delete-batch")
    public String deleteBatch(@RequestParam("ids") List<Integer> ids,
                              @RequestParam(defaultValue = "logical") String type,
                              RedirectAttributes ra) {
        if (ids != null && !ids.isEmpty()) {
            if ("physical".equalsIgnoreCase(type)) {
                studentService.physicalDeleteBatch(ids);
            } else {
                studentService.logicalDeleteBatch(ids);
            }
            ra.addFlashAttribute("msg", "Пакетное удаление выполнено (" + type + ")");
        }
        return "redirect:/students";
    }
}
