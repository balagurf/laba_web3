package com.example.travelling2.controller;

import com.example.travelling2.entity.Traveller;
import com.example.travelling2.service.TravellerService;
import com.example.travelling2.specification.TravellerSpecification;
// Предположим, что CountryCode — это ваш Enum. Если его нет, замените на вашу логику стран.
// import com.example.travelling2.entity.enums.CountryCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

    @GetMapping
    public String list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String passport,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Double minDeposit,
            @RequestParam(required = false) Double maxDeposit,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        // Формируем сортировку
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        // Строим спецификацию на основе параметров фильтрации
        Specification<Traveller> spec = Specification.where(TravellerSpecification.searchGeneral(search))
                .and(TravellerSpecification.byPassport(passport))
                .and(TravellerSpecification.byCountry(country))
                .and(TravellerSpecification.depositRange(minDeposit, maxDeposit));

        model.addAttribute("travellers", service.findAll(spec, sort));

        // Передаем параметры обратно в модель, чтобы сохранить состояние фильтров в UI
        model.addAttribute("search", search);
        model.addAttribute("passport", passport);
        model.addAttribute("country", country);
        model.addAttribute("minDeposit", minDeposit);
        model.addAttribute("maxDeposit", maxDeposit);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

        // model.addAttribute("countries", CountryCode.values()); // Раскомментируйте, если есть Enum стран

        return "travellers/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        Traveller traveller = new Traveller();
        traveller.setTravelCod(generateTravelCode());
        model.addAttribute("traveller", traveller);
        return "travellers/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("traveller", service.findById(id));
        return "travellers/form";
    }

    @GetMapping("/profile/{id}")
    public String profile(@PathVariable Long id, Model model) {
        model.addAttribute("traveller", service.findById(id));
        return "travellers/profile";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Traveller traveller, BindingResult result) {
        // Проверка уникальности кода
        if (!service.isTravelCodUnique(traveller.getTravelCod(), traveller.getId())) {
            result.addError(new FieldError("traveller", "travelCod", "Этот код уже существует"));
        }

        if (result.hasErrors()) {
            return "travellers/form";
        }

        service.save(traveller);
        return "redirect:/travellers";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/travellers";
    }

    // Вспомогательный метод генерации кода
    private String generateTravelCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder("TR-");
        for (int i = 0; i < 18; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
            if ((i + 1) % 4 == 0 && i < 17) {
                sb.append("-");
            }
        }
        return sb.toString();
    }
}