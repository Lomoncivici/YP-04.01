package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CalculatorController {

    @GetMapping("/calculator")
    public String calculator() {
        return "calculator";
    }

    @PostMapping("/calculator")
    public String calculate(@RequestParam("a") double a,
                            @RequestParam("b") double b,
                            @RequestParam("op") String op,
                            RedirectAttributes ra) {

        double result;

        switch (op) {
            case "add" -> result = a + b;
            case "sub" -> result = a - b;
            case "mul" -> result = a * b;
            case "div" -> {
                if (b == 0) {
                    ra.addFlashAttribute("error", "Деление на ноль невозможно");
                    return "redirect:/calculator";
                }
                result = a / b;
            }
            default -> {
                ra.addFlashAttribute("error", "Неизвестная операция");
                return "redirect:/calculator";
            }
        }

        return "redirect:/result?type=calc&value=" + result;
    }
}