package com.mpt.journal.controller;

import com.mpt.journal.entity.Airport;
import com.mpt.journal.repository.AirportRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/airports")
public class AdminAirportController {

    private final AirportRepository airportRepository;

    public AdminAirportController(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    @GetMapping
    public String list(@RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "search", required = false) String search,
                       Model model) {

        int size = 10;

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<Airport> airportPage = airportRepository.search(search, pageable);

        model.addAttribute("airportPage", airportPage);
        model.addAttribute("search", search);

        return "admin/airports/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("airport", new Airport());
        model.addAttribute("title", "Создание аэропорта");
        return "admin/airports/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Аэропорт не найден, id = " + id));
        model.addAttribute("airport", airport);
        model.addAttribute("title", "Редактирование аэропорта");
        return "admin/airports/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("airport") Airport airport,
                       BindingResult bindingResult,
                       Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("title",
                    airport.getId() == null ? "Создание аэропорта" : "Редактирование аэропорта");
            return "admin/airports/form";
        }

        airportRepository.save(airport);
        return "redirect:/admin/airports";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        airportRepository.deleteById(id);
        return "redirect:/admin/airports";
    }
}
