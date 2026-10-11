package com.example.seckill;

import com.example.seckill.entity.User;
import com.example.seckill.mapper.HealthMapper;
import com.example.seckill.mapper.UserMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserModuleTest {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;
    @Autowired private BCryptPasswordEncoder encoder;
    @Autowired private ApplicationContext context;
    @MockitoBean private UserMapper userMapper;
    @MockitoBean private HealthMapper healthMapper;

    @Test
    void registerStoresBcryptAndReturnsOnlyPublicFields() throws Exception {
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            assertThat(user.getPasswordHash()).isNotEqualTo("secret123").startsWith("$2");
            assertThat(encoder.matches("secret123", user.getPasswordHash())).isTrue();
            user.setId(7L);
            return 1;
        }).when(userMapper).insert(any(User.class));
        String response = mvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"secret123\",\"phone\":\"13800000000\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(7))
                .andExpect(jsonPath("$.data.username").value("alice"))
                .andExpect(jsonPath("$.data.phone").value("13800000000"))
                .andExpect(jsonPath("$.data.createdAt").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
        JsonNode data = json.readTree(response).get("data");
        assertThat(data.size()).isEqualTo(4);
        assertThat(data.has("passwordHash")).isFalse();
        assertThat(data.has("password")).isFalse();
        assertThat(data.has("token")).isFalse();
    }

    @Test
    void phoneMayBeOmittedAndRegistrationBoundaryLengthsAreAccepted() throws Exception {
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            assertThat(user.getPhone()).isNull();
            user.setId(8L);
            return 1;
        }).when(userMapper).insert(any(User.class));
        for (String username : new String[]{"ab", "a".repeat(20)}) {
            for (String password : new String[]{"s".repeat(6), "s".repeat(20)}) {
                mvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON)
                                .content(json.writeValueAsString(Map.of("username", username, "password", password))))
                        .andExpect(status().isOk());
            }
        }
    }

    @Test
    void existingUsernameReturnsConflict() throws Exception {
        when(userMapper.selectByUsername("alice")).thenReturn(existingUser());
        mvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"secret123\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("用户名已存在"));
        verify(userMapper, never()).insert(any());
    }

    @Test
    void concurrentDuplicateAtInsertAlsoReturnsConflict() throws Exception {
        when(userMapper.insert(any(User.class))).thenThrow(new DuplicateKeyException("duplicate username"));
        mvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"secret123\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("用户名已存在"));
    }

    @Test
    void loginMatchesBcryptAndReturnsOnlyPublicFields() throws Exception {
        when(userMapper.selectByUsername("alice")).thenReturn(existingUser());
        mvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(7))
                .andExpect(jsonPath("$.data.username").value("alice"))
                .andExpect(jsonPath("$.data.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.data.token").doesNotExist());
        verify(userMapper, never()).insert(any());
    }

    @Test
    void unknownUserAndWrongPasswordReturnIdenticalResponses() throws Exception {
        when(userMapper.selectByUsername("alice")).thenReturn(existingUser());
        String unknown = loginFailure("unknown", "secret123");
        String wrong = loginFailure("alice", "wrong123");
        assertThat(json.readTree(unknown)).isEqualTo(json.readTree(wrong));
        loginFailure("alice", "x".repeat(100));
    }

    @ParameterizedTest
    @MethodSource("invalidRegisterFields")
    void invalidRegistrationIsRejectedBeforeDatabase(String field, String value) throws Exception {
        Map<String, Object> body = new HashMap<>(Map.of("username", "alice", "password", "secret123"));
        body.put(field, value);
        mvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data").isEmpty());
        verifyNoInteractions(userMapper);
    }

    static Stream<Arguments> invalidRegisterFields() {
        return Stream.of(
                Arguments.of("username", null), Arguments.of("username", ""),
                Arguments.of("username", "  "), Arguments.of("username", "a"),
                Arguments.of("username", "a".repeat(21)), Arguments.of("password", null),
                Arguments.of("password", ""), Arguments.of("password", "      "),
                Arguments.of("password", "12345"), Arguments.of("password", "s".repeat(21)),
                Arguments.of("phone", "1".repeat(21)));
    }

    @ParameterizedTest
    @MethodSource("invalidLoginFields")
    void invalidLoginIsRejectedBeforeDatabase(String field, String value) throws Exception {
        Map<String, Object> body = new HashMap<>(Map.of("username", "alice", "password", "secret123"));
        body.put(field, value);
        mvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
        verifyNoInteractions(userMapper);
    }

    static Stream<Arguments> invalidLoginFields() {
        return Stream.of(Arguments.of("username", null), Arguments.of("username", ""),
                Arguments.of("username", "  "), Arguments.of("password", null),
                Arguments.of("password", ""), Arguments.of("password", "  "));
    }

    @Test
    void invalidBodyUsesUnifiedErrorResponseWithoutLeakingPassword() throws Exception {
        mvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"private-input\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("请求无效"));
        mvc.perform(post("/api/users/register").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(userMapper);
    }

    @Test
    void securityStarterDoesNotInstallDefaultInterception() throws Exception {
        assertThat(context.getBeansOfType(SecurityFilterChain.class)).isEmpty();
        assertThat(context.containsBean("springSecurityFilterChain")).isFalse();
        when(healthMapper.ping()).thenReturn(1);
        mvc.perform(get("/api/health")).andExpect(status().isOk());
        mvc.perform(get("/api/does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    private String loginFailure(String username, String password) throws Exception {
        return mvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of("username", username, "password", password))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"))
                .andReturn().getResponse().getContentAsString();
    }

    private User existingUser() {
        User user = new User();
        user.setId(7L);
        user.setUsername("alice");
        user.setPhone("13800000000");
        user.setCreatedAt(LocalDateTime.of(2026, 10, 9, 12, 0));
        user.setPasswordHash(encoder.encode("secret123"));
        return user;
    }
}
