package com.example.travelling2.entity;

import com.example.travelling2.model.CountryCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "travellers", uniqueConstraints = {@UniqueConstraint(name = "uk_traveller_travel_cod", columnNames = "travel_cod")})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Traveller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Имя обязательно")
    @Pattern(regexp = "^[^0-9]+$", message = "Имя не должно содержать цифры")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Pattern(regexp = "^[^0-9]+$", message = "Фамилия не должна содержать цифры")
    @Column(name = "second_name", nullable = false)
    private String secondName;

    @Column(name = "phone")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "country_code")
    private CountryCode countryCode = CountryCode.MDA;

    @Column(name = "travel_cod", unique = true)
    private String travelCod;

    @Column(name = "photo_url", length = 1000)
    private String photoUrl;

    @Column(name = "travel_history", columnDefinition = "TEXT")
    private String travelHistory;

    @Column(name = "tour_operator")
    private String tourOperator;

    @Column(name = "arrival_date")
    private LocalDate arrivalDate;

    @Column(name = "departure_date")
    private LocalDate departureDate;

    @Column(name = "hotel_name")
    private String hotelName;

    @Column(name = "insurance_type")
    private String insuranceType;

    @Column(name = "deposit")
    private double deposit;

    @Column(name = "comments", length = 500)
    private String comments;

    // Связь Master -> Detail
    @OneToMany(mappedBy = "traveller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Passport> passports = new ArrayList<>();
}