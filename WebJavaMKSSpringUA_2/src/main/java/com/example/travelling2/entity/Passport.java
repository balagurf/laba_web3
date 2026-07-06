package com.example.travelling2.entity;

import com.example.travelling2.model.CountryCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "passports",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_passport_code", columnNames = "passport_code"),
                @UniqueConstraint(name = "uk_passport_number", columnNames = "passport_number"),
                @UniqueConstraint(name = "uk_tax_code", columnNames = "tax_code")
        }
)
/*
  MySQL-compatible checks:
  - TRIM(...) <> '' ensures non-empty after trimming
  - NOT REGEXP '[0-9]' forbids digits
  - REGEXP '^...' ensures starts with uppercase letter or matches specific format
*/
@Check(constraints = "TRIM(first_name) <> '' AND first_name NOT REGEXP '[0-9]' AND first_name REGEXP '^[A-ZА-ЯІЇЄҐ]'")
@Check(constraints = "TRIM(last_name) <> '' AND last_name NOT REGEXP '[0-9]' AND last_name REGEXP '^[A-ZА-ЯІЇЄҐ]'")
@Check(constraints = "phone REGEXP '^\\\\+[0-9]{11,14}$'")
@Check(constraints = "passport_code REGEXP '^[A-Z0-9]{3,20}$'")
@Check(constraints = "passport_number REGEXP '^[A-Z]{2}[0-9]{7}$'")
@Check(constraints = "tax_code REGEXP '^[A-Z0-9]{5,30}$'")
@Check(constraints = "email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,6}$'")
@Check(constraints = "balance >= 0")
@Check(constraints = "children_count >= 0")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "traveller")
public class Passport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // связь с Traveller
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "traveller_id")
    private Traveller traveller;

    // PASSPORT CODE (без '-')
    @NotBlank(message = "Введите код паспорта")
    @Pattern(regexp = "^[A-Z0-9]{3,20}$", message = "Код паспорта: только A-Z и цифры, 3-20 символов")
    @Column(name = "passport_code", nullable = false, length = 20)
    private String passportCode;

    // PASSPORT NUMBER
    @NotBlank(message = "Введите номер паспорта")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{7}$", message = "Номер паспорта: 2 буквы + 7 цифр")
    @Column(name = "passport_number", nullable = false, length = 20)
    private String passportNumber;

    // TAX CODE (без '-')
    @NotBlank(message = "Введите налоговый код")
    @Pattern(regexp = "^[A-Z0-9]{5,30}$", message = "Налоговый код: A-Z и цифры, 5-30 символов")
    @Column(name = "tax_code", nullable = false, length = 30)
    private String taxCode;

    // Регулярка для имён/фамилий/отчества (Java-валидация)
    // Поддерживает дефис, пробел и апостроф внутри частей; каждая часть начинается с заглавной буквы.
    private static final String NAME_PART = "[A-ZА-ЯІЇЄҐ][A-Za-zA-Zа-яіїєґ]*(?:['’][A-Za-zA-Zа-яіїєґ]+)?";
    private static final String FULL_NAME_PATTERN = "^" + NAME_PART + "(?:[- ]" + NAME_PART + ")*$";

    // FIRST NAME
    @NotBlank(message = "Введите имя")
    @Pattern(regexp = FULL_NAME_PATTERN, message = "Имя: каждая часть с заглавной, допускаются дефис и апостроф")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    // LAST NAME
    @NotBlank(message = "Введите фамилию")
    @Pattern(regexp = FULL_NAME_PATTERN, message = "Фамилия: каждая часть с заглавной, допускаются дефис и апостроф")
    @Column(name = "last_name", nullable = false, length = 150)
    private String lastName;

    // PATRONYMIC (опционально)
    @Pattern(regexp = FULL_NAME_PATTERN, message = "Отчество: каждая часть с заглавной, допускаются дефис и апостроф")
    @Column(name = "patronymic", length = 100)
    private String patronymic;

    // PHONE
    @NotBlank(message = "Введите телефон")
    @Pattern(regexp = "^\\+[0-9]{11,14}$", message = "Формат телефона: +380XXXXXXXXX")
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    // EMAIL
    @NotBlank(message = "Введите email")
    @Email(message = "Некорректный email")
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    // COUNTRY
    @NotNull(message = "Выберите страну")
    @Enumerated(EnumType.STRING)
    @Column(name = "country_code", nullable = false, length = 10)
    private CountryCode countryCode;

    // DATES
    @NotNull(message = "Выберите дату прилёта")
    @Column(name = "arrival_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate arrivalDate;

    @NotNull(message = "Выберите дату вылета")
    @Column(name = "departure_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate departureDate;

    @NotNull(message = "Выберите дату окончания")
    @Column(name = "expiry_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    // INSURANCE
    @Column(name = "insurance_type", length = 100)
    private String insuranceType;

    // CHILDREN
    @Column(name = "children_count", nullable = false)
    @ColumnDefault("0")
    private int childrenCount = 0;

    @Column(name = "children_info", columnDefinition = "TEXT")
    private String childrenInfo;

    // BALANCE
    @NotNull(message = "Баланс не может быть пустым")
    @Column(name = "balance", nullable = false, precision = 12, scale = 2, columnDefinition = "DECIMAL(12,2) DEFAULT 0.00")
    @ColumnDefault("0.00")
    private BigDecimal balance = BigDecimal.ZERO;
}
