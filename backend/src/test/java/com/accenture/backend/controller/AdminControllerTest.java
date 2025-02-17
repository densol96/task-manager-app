package com.accenture.backend.controller;

import com.accenture.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminController adminController;

    private static final String BASE_URL = "/api/v1/admin-dashboard";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
        Mockito.doNothing().when(userService).changeRole(Mockito.anyString(), Mockito.any());
    }

    @Test
    void testAddModerator_Success() throws Exception {
        String email = "test@example.com";

        mockMvc.perform(put(BASE_URL + "/add-moderator")
                        .param("email", email))
                .andExpect(status().isOk());
    }

    @Test
    void testDisableUser_Success() throws Exception {
        String email = "test@example.com";

        mockMvc.perform(put(BASE_URL + "/disable-user")
                        .param("email", email))
                .andExpect(status().isOk());
    }

    @Test
    void testAddModerator_InvalidEmail() throws Exception {
        mockMvc.perform(put(BASE_URL + "/add-moderator")
                        .param("email", "invalid-email"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDisableUser_InvalidEmail() throws Exception {
        mockMvc.perform(put(BASE_URL + "/disable-user")
                        .param("email", "invalid-email"))
                .andExpect(status().isBadRequest());
    }
}


