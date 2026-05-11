package com.example.travelling2.entity;

import com.example.travelling2.model.CountryCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "passports")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Passport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Связь Detail -> Master
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "traveller_id", nullable = false)
    private Traveller traveller;

    @Column(name = "passport_code", nullable = false, unique = true, length = 30)
    private String passportCode;

    @NotBlank(message = "Введите номер паспорта")
    @Column(name = "passport_number", nullable = false, length = 30)
    private String passportNumber;

    @NotBlank(message = "Введите налоговый код")
    @Column(name = "tax_code", nullable = false, length = 30)
    private String taxCode;

    @NotBlank(message = "Введите имя")
    @Pattern(regexp = "^[^0-9]+$", message = "Имя не должно содержать цифры")
    private String firstName;

    @NotBlank(message = "Введите фамилию")
    @Pattern(regexp = "^[^0-9]+$", message = "Фамилия не должна содержать цифры")
    private String lastName;

    @Pattern(regexp = "^[^0-9]*$", message = "Отчество не должно содержать цифры")
    private String patronymic;

    @NotBlank(message = "Введите телефон")
    private String phone;

    @Email(message = "Некорректный email")
    @NotBlank(message = "Введите email")
    private String email;

    @Enumerated(EnumType.STRING)
    private CountryCode countryCode;

    @NotNull(message = "Выберите дату прилета")
    private LocalDate arrivalDate;

    @NotNull(message = "Выберите дату вылета")
    private LocalDate departureDate;

    @NotNull(message = "Выберите дату окончания паспорта")
    private LocalDate expiryDate;

    private String insuranceType;

    @Builder.Default
    private Integer childrenCount = 0;

    @Column(columnDefinition = "TEXT")
    private String childrenInfo;

    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;
}