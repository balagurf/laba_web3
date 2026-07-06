package com.example.travelling2;
import com.example.travelling2.controller.TravellerController; // ИМПОРТ КОНТРОЛЛЕРА
import com.example.travelling2.entity.Traveller;
import com.example.travelling2.service.TravellerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TravellerController.class)
public class TravellerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TravellerService service;

    @Test
    void testListTravellers() throws Exception {
        when(service.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/travellers"))
                .andExpect(status().isOk())
                .andExpect(view().name("travellers/list"))
                .andExpect(model().attributeExists("travellers"));
    }

    @Test
    void testCreateForm() throws Exception {
        mockMvc.perform(get("/travellers/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("travellers/form"))
                .andExpect(model().attributeExists("traveller"));
    }

    @Test
    void testSaveValidTraveller() throws Exception {
        // Відправляємо POST-запит з валідними даними
        mockMvc.perform(post("/travellers/save")
                        .param("firstName", "Oleg")
                        .param("secondName", "Petrov")
                        .param("travelCod", "TC999")
                        .param("countryCode", "UKR")
                        .param("deposit", "100.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/travellers"));

        verify(service, times(1)).save(any(Traveller.class));
    }

    @Test
    void testSaveInvalidTravellerReturnsForm() throws Exception {
        // Відправляємо дані з помилками (ім'я з цифрами та відсутній код)
        mockMvc.perform(post("/travellers/save")
                        .param("firstName", "Oleg123") // Не проходить валідацію Pattern
                        .param("secondName", "Petrov")) // Немає обов'язкових полів
                .andExpect(status().isOk())
                .andExpect(view().name("travellers/form"))
                .andExpect(model().hasErrors()); // Перевіряємо, що BindingResult зловив помилку

        verify(service, never()).save(any(Traveller.class));
    }

    @Test
    void testProfile() throws Exception {
        Traveller traveller = new Traveller();
        traveller.setId(1L);
        traveller.setFirstName("Oleg");

        when(service.findById(1L)).thenReturn(traveller);

        mockMvc.perform(get("/travellers/profile/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("travellers/profile"))
                .andExpect(model().attributeExists("traveller"))
                .andExpect(model().attributeExists("securityCode"));
    }

    @Test
    void testDeleteWithCorrectCode() throws Exception {
        mockMvc.perform(post("/travellers/delete/1")
                        .param("confirmCode", "1234")
                        .param("actualCode", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/travellers"));

        verify(service, times(1)).delete(1L);
    }

    @Test
    void testDeleteWithIncorrectCode() throws Exception {
        mockMvc.perform(post("/travellers/delete/1")
                        .param("confirmCode", "1234")
                        .param("actualCode", "0000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/travellers/profile/1"))
                .andExpect(flash().attribute("error", true)); // Перевіряємо FlashAttribute

        verify(service, never()).delete(anyLong());
    }
}