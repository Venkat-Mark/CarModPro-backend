package com.carmod.app.controller;

import com.carmod.app.config.SecurityConfig;
import com.carmod.app.service.CustomUserDetailsService;
import com.carmod.app.service.ModificationServiceManagement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HomeController.class)
@Import(SecurityConfig.class)
class SecurityAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModificationServiceManagement serviceManagement;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("Public home page should be accessible without authentication")
    void publicHomePage_AccessibleAnonymously() throws Exception {
        when(serviceManagement.getActiveCategories()).thenReturn(Collections.emptyList());
        when(serviceManagement.getAvailableServices(any(), any())).thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Public services catalog should be accessible without authentication")
    void publicServicesPage_AccessibleAnonymously() throws Exception {
        when(serviceManagement.getActiveCategories()).thenReturn(Collections.emptyList());
        when(serviceManagement.getAvailableServices(any(), any())).thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/services"))
                .andExpect(status().isOk());
    }
}
