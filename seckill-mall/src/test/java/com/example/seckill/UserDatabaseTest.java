package com.example.seckill;

import com.example.seckill.entity.User;
import com.example.seckill.mapper.UserMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@EnabledIfEnvironmentVariable(named = "SECKILL_DB_TEST", matches = "true")
class UserDatabaseTest {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;
    @Autowired private UserMapper userMapper;
    @Autowired private BCryptPasswordEncoder encoder;

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "13800000000")
    void registerAndLoginUsingRealMysql(String phone) throws Exception {
        String username = "u" + UUID.randomUUID().toString().replace("-", "").substring(0, 15);
        Map<String, Object> registration = new HashMap<>(Map.of("username", username, "password", "secret123"));
        registration.put("phone", phone);
        String registered = mvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(registration)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        User stored = userMapper.selectByUsername(username);
        assertThat(stored.getId()).isPositive();
        assertThat(stored.getPhone()).isEqualTo(phone);
        assertThat(stored.getCreatedAt()).isNotNull();
        assertThat(stored.getPasswordHash()).isNotEqualTo("secret123");
        assertThat(encoder.matches("secret123", stored.getPasswordHash())).isTrue();
        assertThat(userMapper.selectById(stored.getId()).getUsername()).isEqualTo(username);
        JsonNode publicData = json.readTree(registered).get("data");
        assertThat(publicData.size()).isEqualTo(4);
        assertThat(publicData.get("id").asLong()).isEqualTo(stored.getId());
        assertThat(publicData.get("createdAt").asText()).isEqualTo(
                json.readTree(json.writeValueAsString(stored)).get("createdAt").asText());

        String loggedIn = mvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("username", username, "password", "secret123"))))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(json.readTree(loggedIn)).isEqualTo(json.readTree(registered));
        mvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(registration)))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.message").value("用户名已存在"));
        mvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("username", username, "password", "wrong123"))))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }
}
