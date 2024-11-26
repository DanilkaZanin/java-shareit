package ru.practicum.shareit.error;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ErrorHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHandleNotFoundException() throws Exception {
        mockMvc.perform(get("/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found Error"));
    }

    @Test
    void testHandleInternalServerError() throws Exception {
        mockMvc.perform(get("/internal-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Произошла непредвиденная ошибка.Unexpected Error"));
    }

    @Test
    void testHandleForbiddenException() throws Exception {
        mockMvc.perform(get("/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Unauthorized Access"));
    }
}