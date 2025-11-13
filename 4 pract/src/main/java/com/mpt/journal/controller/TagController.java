package com.mpt.journal.controller;

import com.mpt.journal.entity.Tag;
import com.mpt.journal.repository.TagRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tags")
public class TagController {
    private final TagRepository repo;

    public TagController(TagRepository repo) { this.repo = repo; }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int pageIndex,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "name") String sort,
                       @RequestParam(defaultValue = "asc") String dir,
                       Model model) {
        size = Math.max(10, size);
        Sort.Direction d = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(d, sort));
        Page<Tag> page = repo.findAll(pageable);
        model.addAttribute("page", page);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("tag", new Tag());
        return "tagList";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("tag") Tag t) {
        repo.save(t);
        return "redirect:/tags";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        repo.deleteById(id);
        return "redirect:/tags";
    }
}
