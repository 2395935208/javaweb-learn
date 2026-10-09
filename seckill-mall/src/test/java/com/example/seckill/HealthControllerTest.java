package com.example.seckill;

import com.example.seckill.controller.HealthController;
import com.example.seckill.dto.HealthResponse;
import com.example.seckill.service.HealthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = HealthController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
class HealthControllerTest {
    @Autowired private MockMvc mvc;
    @MockitoBean private HealthService healthService;

    @Test
    void returnsUnifiedSuccessResponse() throws Exception {
        when(healthService.check()).thenReturn(new HealthResponse("seckill-mall", "UP"));
        mvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.database").value("UP"));
    }

    @Test
    void hidesInternalExceptionDetails() throws Exception {
        when(healthService.check()).thenThrow(new IllegalStateException("private database detail"));
        mvc.perform(get("/api/health"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("服务器内部错误"));
    }

    @Test
    void missingResourceUsesUnifiedErrorResponse() throws Exception {
        mvc.perform(get("/api/does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }
}
