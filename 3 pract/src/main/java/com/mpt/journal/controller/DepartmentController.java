package com.mpt.journal.controller;

import com.mpt.journal.entity.Department;
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
@RequestMapping("/departments")
public class DepartmentController {
    private final DepartmentRepository repo;

    public DepartmentController(DepartmentRepository repo) { this.repo = repo; }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int pageIndex,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "false") boolean onlyActive, // ⬅️ показываем ВСЕ по умолчанию
                       @RequestParam(defaultValue = "name") String sort,
                       @RequestParam(defaultValue = "asc") String dir,
                       Model model) {
        size = Math.max(10, size);
        Sort.Direction d = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(d, sort));
        Page<Department> page = onlyActive ? repo.findByDeletedFalse(pageable)
                : repo.findAll(pageable);

        model.addAttribute("page", page);
        model.addAttribute("onlyActive", onlyActive);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("dept", new Department());
        return "departmentList";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("dept") Department d) {
        repo.save(d);
        return "redirect:/departments";
    }

    @PostMapping("/soft-delete")
    public String softDelete(@RequestParam Long id) {
        repo.findById(id).ifPresent(x -> { x.setDeleted(true); repo.save(x); });
        // ⬇️ чтобы запись осталась видимой и перечёркнутой
        return "redirect:/departments?onlyActive=false";
    }

    @PostMapping("/hard-delete")
    public String hardDelete(@RequestParam Long id) {
        repo.deleteById(id);
        return "redirect:/departments";
    }

    @PostMapping("/delete")
    public String legacyDelete(@RequestParam Long id, @RequestParam(defaultValue="logical") String type) {
        return "physical".equalsIgnoreCase(type) ? hardDelete(id) : softDelete(id);
    }
}
