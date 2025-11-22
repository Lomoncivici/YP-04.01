package com.mpt.journal.controller;

import com.mpt.journal.entity.Aircraft;
import com.mpt.journal.repository.AircraftRepository;
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
@RequestMapping("/admin/aircrafts")
public class AdminAircraftController {

    private final AircraftRepository aircraftRepository;

    public AdminAircraftController(AircraftRepository aircraftRepository) {
        this.aircraftRepository = aircraftRepository;
    }

    @GetMapping
    public String list(@RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "search", required = false) String search,
                       Model model) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("model"));

        Page<Aircraft> aircraftPage;
        if (search != null && !search.isBlank()) {
            aircraftPage = aircraftRepository.findByModelContainingIgnoreCase(search, pageable);
        } else {
            aircraftPage = aircraftRepository.findAll(pageable);
        }

        model.addAttribute("aircraftPage", aircraftPage);
        model.addAttribute("search", search);

        return "admin/aircrafts/list";
    }


    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("aircraft", new Aircraft());
        model.addAttribute("title", "Создание самолёта");
        return "admin/aircrafts/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Самолёт не найден, id=" + id));
        model.addAttribute("aircraft", aircraft);
        model.addAttribute("title", "Редактирование самолёта");
        return "admin/aircrafts/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("aircraft") Aircraft aircraft,
                       BindingResult bindingResult,
                       Model model) {

        if (aircraft.getEconomySeats() + aircraft.getBusinessSeats() > aircraft.getTotalSeats()) {
            bindingResult.reject("seats.sum", "Сумма мест эконом и бизнес-класса не может быть больше общего количества мест");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("title", aircraft.getId() == null ? "Создание самолёта" : "Редактирование самолёта");
            return "admin/aircrafts/form";
        }

        aircraftRepository.save(aircraft);
        return "redirect:/admin/aircrafts";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        aircraftRepository.deleteById(id);
        return "redirect:/admin/aircrafts";
    }
}
