package com.example.travelling2.controller;

import com.example.travelling2.entity.Passport;
import com.example.travelling2.entity.Traveller;
import com.example.travelling2.service.PassportService;
import com.example.travelling2.service.TravellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/travellers")
@RequiredArgsConstructor
public class TravellerController {

    private final TravellerService service;
    private final PassportService passportService;

    @GetMapping
    public String list(Model model) {
        List<Traveller> travellers = service.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("travellers", travellers);
        return "travellers/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        Traveller traveller = new Traveller();
        traveller.setTravelCod(generateTravelCode());
        model.addAttribute("traveller", traveller);
        model.addAttribute("passports", passportService.findAll());
        return "travellers/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("traveller", service.findById(id));
        model.addAttribute("passports", passportService.findAll());
        return "travellers/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Traveller traveller, BindingResult result) {
        if (!service.isTravelCodUnique(traveller.getTravelCod(), traveller.getId())) {
            result.addError(new FieldError("traveller", "travelCod", "Travel code already exists"));
        }
        if (result.hasErrors()) return "travellers/form";
        service.save(traveller);
        return "redirect:/travellers";
    }

    @GetMapping("/profile/{id}")
    public String profile(@PathVariable Long id, Model model) {
        Traveller traveller = service.findById(id);
        String securityCode = String.format("%04d", new Random().nextInt(10000));
        model.addAttribute("traveller", traveller);
        model.addAttribute("securityCode", securityCode);
        return "travellers/profile";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/travellers";
    }

    private String generateTravelCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder("TR-");
        for (int i = 0; i < 18; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
            if ((i + 1) % 4 == 0 && i < 17) sb.append("-");
        }
        return sb.toString();
    }
}