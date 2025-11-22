package com.mpt.journal.controller;

import com.mpt.journal.entity.Aircraft;
import com.mpt.journal.entity.Flight;
import com.mpt.journal.entity.Route;
import com.mpt.journal.repository.AircraftRepository;
import com.mpt.journal.repository.FlightRepository;
import com.mpt.journal.repository.RouteRepository;
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
@RequestMapping("/admin/flights")
public class AdminFlightController {

    private final FlightRepository flightRepository;
    private final RouteRepository routeRepository;
    private final AircraftRepository aircraftRepository;

    public AdminFlightController(FlightRepository flightRepository,
                                 RouteRepository routeRepository,
                                 AircraftRepository aircraftRepository) {
        this.flightRepository = flightRepository;
        this.routeRepository = routeRepository;
        this.aircraftRepository = aircraftRepository;
    }

    @GetMapping
    public String list(@RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "search", required = false) String search,
                       Model model) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by("departureTime").ascending());
        Page<Flight> flightPage = flightRepository.search(search, pageable);

        model.addAttribute("flightPage", flightPage);
        model.addAttribute("search", search);

        return "admin/flights/list";
    }


    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("flight", new Flight());
        model.addAttribute("routes", routeRepository.findAll());
        model.addAttribute("aircrafts", aircraftRepository.findAll());
        model.addAttribute("title", "Создание рейса");
        return "admin/flights/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Рейс не найден, id=" + id));

        model.addAttribute("flight", flight);
        model.addAttribute("routes", routeRepository.findAll());
        model.addAttribute("aircrafts", aircraftRepository.findAll());
        model.addAttribute("title", "Редактирование рейса");
        return "admin/flights/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("flight") Flight flight,
                       BindingResult bindingResult,
                       Model model) {

        if (flight.getDepartureTime() != null && flight.getArrivalTime() != null &&
                !flight.getArrivalTime().isAfter(flight.getDepartureTime())) {
            bindingResult.reject("time.order", "Время прилёта должно быть позже времени вылета");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("routes", routeRepository.findAll());
            model.addAttribute("aircrafts", aircraftRepository.findAll());
            model.addAttribute("title", flight.getId() == null ? "Создание рейса" : "Редактирование рейса");
            return "admin/flights/form";
        }

        flightRepository.save(flight);
        return "redirect:/admin/flights";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        flightRepository.deleteById(id);
        return "redirect:/admin/flights";
    }
}
