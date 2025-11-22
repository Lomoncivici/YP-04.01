package com.mpt.journal.controller;

import com.mpt.journal.entity.Route;
import com.mpt.journal.repository.AirportRepository;
import com.mpt.journal.repository.RouteRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/routes")
public class AdminRouteController {

    private final RouteRepository routeRepository;
    private final AirportRepository airportRepository;

    public AdminRouteController(RouteRepository routeRepository,
                                AirportRepository airportRepository) {
        this.routeRepository = routeRepository;
        this.airportRepository = airportRepository;
    }

    @GetMapping
    public String list(@RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "search", required = false) String search,
                        Model model) {

        Pageable pageable = PageRequest.of(page, 10);
        Page<Route> routePage = routeRepository.search(search, pageable);

        model.addAttribute("routePage", routePage);
        model.addAttribute("search", search);

        return "admin/routes/list";
    }


    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("route", new Route());
        model.addAttribute("airports", airportRepository.findAll());
        model.addAttribute("title", "Создание маршрута");
        return "admin/routes/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Маршрут не найден, id=" + id));
        model.addAttribute("route", route);
        model.addAttribute("airports", airportRepository.findAll());
        model.addAttribute("title", "Редактирование маршрута");
        return "admin/routes/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("route") Route route,
                       BindingResult bindingResult,
                       Model model) {

        if (route.getDepartureAirport() != null && route.getArrivalAirport() != null &&
                route.getDepartureAirport().getId() != null &&
                route.getDepartureAirport().getId().equals(route.getArrivalAirport().getId())) {
            bindingResult.reject("airports.same", "Аэропорт вылета и прилёта не могут совпадать");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("airports", airportRepository.findAll());
            model.addAttribute("title", route.getId() == null ? "Создание маршрута" : "Редактирование маршрута");
            return "admin/routes/form";
        }

        routeRepository.save(route);
        return "redirect:/admin/routes";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        routeRepository.deleteById(id);
        return "redirect:/admin/routes";
    }
}
