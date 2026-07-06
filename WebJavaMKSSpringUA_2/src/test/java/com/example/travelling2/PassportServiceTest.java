package com.example.travelling2;
import org.junit.jupiter.api.Test;
import com.example.travelling2.service.PassportService; // Для самого сервиса
import com.example.travelling2.entity.Passport;         // Для сущности Passport
import com.example.travelling2.entity.Passport;
import com.example.travelling2.repository.PassportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PassportServiceTest {

    @Mock
    private PassportRepository repository;

    @InjectMocks
    private PassportService service;

    private Passport passport;

    @BeforeEach
    void setUp() {
        passport = new Passport();
        passport.setId(1L);
        passport.setFirstName("Ivan");
        passport.setLastName("Ivanov");
        passport.setPassportNumber("AB123456");
    }

    @Test
    void testFindAll() {
        // Arrange
        when(repository.findAll()).thenReturn(Arrays.asList(passport, new Passport()));

        // Act
        List<Passport> result = service.findAll();

        // Assert
        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(passport));

        // Act
        Passport result = service.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Ivan", result.getFirstName());
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        when(repository.findById(2L)).thenReturn(Optional.empty());

        // Act
        Passport result = service.findById(2L);

        // Assert
        assertNull(result);
    }

    @Test
    void testSave() {
        // Act
        service.save(passport);

        // Assert
        verify(repository, times(1)).save(passport);
    }

    @Test
    void testDelete() {
        // Act
        service.delete(1L);

        // Assert
        verify(repository, times(1)).deleteById(1L);
    }
}