package com.gritlab.lets_play;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deleteProduct_Unauthenticated_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/products/some-id"))
                .andExpect(status().isForbidden()); // Correctly expects 403
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = {"USER"}) // Logged in as a regular USER
    void deleteProductByAdmin_AsUser_ReturnsForbidden() throws Exception {
        // Try to access an admin-only endpoint as a regular user
        mockMvc.perform(delete("/api/products/admin/some-id"))
                .andExpect(status().isForbidden()); // Expect a 403 error
    }

    @Test
    @WithMockUser(username = "admin@example.com", authorities = {"ADMIN"}) // Logged in as an ADMIN
    void deleteProductByAdmin_AsAdmin_ReturnsNoContent() throws Exception {
        // Access an admin-only endpoint as an admin
        // This will fail if the product ID doesn't exist, but it tests the security layer
        // We expect a 204 No Content or a 404 Not Found, but NOT a 403 Forbidden
        mockMvc.perform(delete("/api/products/admin/non-existent-id"))
                .andExpect(result -> assertTrue(
                        result.getResponse().getStatus() == 204 || result.getResponse().getStatus() == 404
                ));
    }
}