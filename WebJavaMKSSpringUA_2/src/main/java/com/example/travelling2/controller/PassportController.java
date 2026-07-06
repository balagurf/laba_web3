package com.example.travelling2.controller;

import com.example.travelling2.entity.Passport;
import com.example.travelling2.service.PassportService;
import com.example.travelling2.service.TravellerService;
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

    private final PassportService passportService;
    private final TravellerService travellerService;

    @GetMapping
    public String list(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("passports", passportService.search(search));
        return "passports/list";
    }

    @GetMapping("/create")
    public String create(@RequestParam(required = false) Long travellerId, Model model) {
        Passport passport = new Passport();

        // Генерация кодов через сервис
        passport.setPassportCode(passportService.generatePassportCode());
        passport.setPassportNumber(passportService.generatePassportNumber());

        // Если создаем паспорт из профиля путешественника - подставляем его
        if (travellerId != null) {
            passport.setTraveller(travellerService.findById(travellerId));
        }

        model.addAttribute("passport", passport);
        model.addAttribute("travellers", travellerService.findAll());

        return "passports/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("passport", passportService.findById(id));
        model.addAttribute("travellers", travellerService.findAll());
        return "passports/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("passport") Passport passport,
                       BindingResult result,
                       Model model) {

        if (!passportService.isPassportCodeUnique(passport.getPassportCode(), passport.getId())) {
            result.rejectValue("passportCode", "duplicate", "Этот системный код уже используется");
        }

        if (result.hasErrors()) {
            model.addAttribute("travellers", travellerService.findAll());
            return "passports/form";
        }

        passportService.save(passport);
        return "redirect:/passports";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        passportService.delete(id);
        ra.addFlashAttribute("message", "Паспорт успешно удален");
        return "redirect:/passports";
    }
}