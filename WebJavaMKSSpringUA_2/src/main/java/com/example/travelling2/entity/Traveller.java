package com.example.travelling2.entity;

import com.example.travelling2.model.CountryCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.Check;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "travellers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_travel_cod", columnNames = "travel_cod"),
                @UniqueConstraint(name = "uk_phone", columnNames = "phone")
        }
)
@Check(name = "chk_traveller_first_name", constraints = "TRIM(first_name) <> '' AND first_name NOT REGEXP '[0-9]' AND first_name REGEXP '^[A-ZА-ЯІЇЄҐ]'")
@Check(name = "chk_traveller_second_name", constraints = "TRIM(second_name) <> '' AND second_name NOT REGEXP '[0-9]' AND second_name REGEXP '^[A-ZА-ЯІЇЄҐ]'")
@Check(name = "chk_traveller_phone", constraints = "phone REGEXP '^\\\\+[0-9]{11,14}$'")
@Check(name = "chk_traveller_travel_cod", constraints = "travel_cod REGEXP '^TR(-[A-Z0-9]{4}){4}-[A-Z0-9]{2}$'")
@Data
public class Traveller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Имя обязательно")
    @Pattern(
            regexp = "^[A-ZА-ЯІЇЄҐ][a-zA-Zа-яіїєґ]+$",
            message = "Имя должно начинаться с заглавной буквы и не содержать '-' или цифр"
    )
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Pattern(
            regexp = "^[A-ZА-ЯІЇЄҐ][a-zA-Zа-яіїєґ]+(?:['’][a-zA-Zа-яіїєґ]+)?(?:[- ][A-ZА-ЯІЇЄҐ][a-zA-Zа-яіїєґ]+(?:['’][a-zA-Zа-яіїєґ]+)?)*$",
            message = "Фамилия должна начинаться с заглавной буквы; допускаются дефис и апостроф"
    )
    @Column(name = "second_name", nullable = false, length = 150)
    private String secondName;

    @NotBlank(message = "Телефон обязателен")
    @Pattern(
            regexp = "^\\+[0-9]{11,14}$",
            message = "Формат: +380XXXXXXXXX"
    )
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @NotNull(message = "Выберите страну")
    @Enumerated(EnumType.STRING)
    @Column(name = "country_code", nullable = false, length = 10)
    private CountryCode countryCode;

    @NotBlank(message = "Код тура обязателен")
    @Pattern(
            regexp = "^TR(-[A-Z0-9]{4}){4}-[A-Z0-9]{2}$",
            message = "Формат: TR-XXXX-XXXX-XXXX-XXXX-XX"
    )
    @Column(name = "travel_cod", nullable = false, unique = true, length = 30)
    private String travelCod;

    @Column(name = "photo_url", length = 1000)
    private String photoUrl;

    @Column(name = "tour_operator", length = 100)
    private String tourOperator;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate arrivalDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate departureDate;

    @Column(name = "hotel_name", length = 150)
    private String hotelName;

    @Min(value = 0, message = "Депозит не может быть отрицательным")
    @Column(nullable = false)
    private double deposit = 0.00;

    @Column(length = 500)
    private String comments;

    @OneToMany(mappedBy = "traveller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Passport> passports = new ArrayList<>();

    public void addPassport(Passport passport) {
        passports.add(passport);
        passport.setTraveller(this);
    }
}
