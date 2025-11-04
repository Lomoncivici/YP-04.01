package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class ConverterController {

    // Курс «сколько USD в 1 единице валюты»
    // Примеры, можно заменить на свои/динамические
    private static final Map<String, Double> USD_RATES = new LinkedHashMap<>();
    static {
        USD_RATES.put("USD", 1.0);
        USD_RATES.put("EUR", 1.07);
        USD_RATES.put("RUB", 0.0105);
        USD_RATES.put("KZT", 0.0021);
    }

    @GetMapping("/converter")
    public String converter(Model model) {
        model.addAttribute("currencies", USD_RATES.keySet());
        return "converter";
    }


    @PostMapping("/converter")
    public String convert(@RequestParam("amount") double amount,
                          @RequestParam("from") String from,
                          @RequestParam("to") String to,
                          RedirectAttributes ra) {
        if (!USD_RATES.containsKey(from) || !USD_RATES.containsKey(to)) {
            ra.addFlashAttribute("error", "Выбрана неизвестная валюта");
            return "redirect:/converter";
        }
        if (amount < 0) {
            ra.addFlashAttribute("error", "Сумма не может быть отрицательной");
            return "redirect:/converter";
        }
        double usd = amount * USD_RATES.get(from);
        double result = usd / USD_RATES.get(to);
        return "redirect:/result?type=conv&value=" + result + "&from=" + from + "&to=" + to + "&amount=" + amount;
    }
}