package com.example.travelling2.controller;

import com.example.travelling2.entity.Passport;
import com.example.travelling2.service.PassportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/passports")
@RequiredArgsConstructor
public class PassportController {

    private final PassportService service;

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("passports", service.search(search));
        return "passports/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        Passport passport = new Passport();
        passport.setPassportCode(service.generatePassportCode());
        passport.setPassportNumber(service.generatePassportNumber());
        model.addAttribute("passport", passport);
        return "passports/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("passport", service.findById(id));
        return "passports/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("passport") Passport passport, BindingResult result) {
        if (!service.isPassportCodeUnique(passport.getPassportCode(), passport.getId())) {
            result.rejectValue("passportCode", "duplicate", "Этот код уже используется");
        }
        if (result.hasErrors()) return "passports/form";

        service.save(passport);
        return "redirect:/passports";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         @RequestParam String confirmCode,
                         @RequestParam String actualCode,
                         RedirectAttributes ra) {
        if (confirmCode != null && confirmCode.equalsIgnoreCase(actualCode)) {
            service.delete(id);
            ra.addFlashAttribute("message", "Паспорт успешно удален");
        } else {
            ra.addFlashAttribute("error", "Код подтверждения не совпал!");
        }
        return "redirect:/passports";
    }
}