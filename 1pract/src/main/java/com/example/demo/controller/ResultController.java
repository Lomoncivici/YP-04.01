package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DecimalFormat;

@Controller
public class ResultController {

    @GetMapping("/result")
    public String result(@RequestParam("type") String type,
                         @RequestParam("value") double value,
                         @RequestParam(value = "from", required = false) String from,
                         @RequestParam(value = "to", required = false) String to,
                         @RequestParam(value = "amount", required = false) Double amount,
                         Model model) {

        String title;
        String message;

        DecimalFormat df = new DecimalFormat("#.####");

        if ("calc".equals(type)) {
            title = "Результат вычисления";
            message = "Ответ: " + df.format(value);
        } else if ("conv".equals(type) && from != null && to != null && amount != null) {
            title = "Результат конвертации";
            message = String.format(
                    "Ответ: %s %s = %s %s",
                    df.format(amount), from,
                    df.format(value), to
            );
        } else {
            title = "Результат";
            message = "Неизвестный тип результата";
        }

        model.addAttribute("title", title);
        model.addAttribute("message", message);
        return "result";
    }
}