package k2.rizzerve.controller;

import com.jayway.jsonpath.JsonPath;
import k2.rizzerve.model.Admin;
import k2.rizzerve.repository.AdminRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        properties = "app.jwt.expiration-ms=3600000"
)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private AdminRepository repo;

    private static String oldToken;
    private static String newToken;

    @Test @Order(1)
    void registerMustCreateAdmin() throws Exception {
        var json = """
      {"name":"Test","username":"test","password":"pass"}
      """;
        mvc.perform(post("/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("test"));

        Admin a = repo.findByUsername("test").orElseThrow();
        assertThat(a.getName()).isEqualTo("Test");
    }

    @Test @Order(2)
    void loginMustReturnJwt() throws Exception {
        var resp = mvc.perform(post("/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"test\",\"password\":\"pass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        oldToken = JsonPath.read(resp, "$.token");
        assertThat(oldToken).isNotBlank();
    }

    @Test @Order(3)
    void getProfileWithoutTokenShouldBeForbidden() throws Exception {
        mvc.perform(get("/admin/profile"))
                .andExpect(status().isForbidden());
    }

    @Test @Order(4)
    void getProfileWithTokenShouldReturnDto() throws Exception {
        mvc.perform(get("/admin/profile")
                        .header("Authorization", "Bearer " + oldToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test"));
    }

    @Test @Order(5)
    void updateProfileShouldReturnNewTokenAndBothStillValid() throws Exception {
        var updateJson = """
      {"name":"NewName","username":"newuser"}
      """;

        var resp = mvc.perform(put("/admin/profile")
                        .header("Authorization", "Bearer " + oldToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.admin.username").value("newuser"))
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        newToken = JsonPath.read(resp, "$.token");

        mvc.perform(get("/admin/profile")
                        .header("Authorization", "Bearer " + oldToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newuser"));

        mvc.perform(get("/admin/profile")
                        .header("Authorization", "Bearer " + newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test @Order(6)
    void deleteProfileWithNewTokenShouldReturnNoContent() throws Exception {
        mvc.perform(delete("/admin/profile")
                        .header("Authorization", "Bearer " + newToken))
                .andExpect(status().isNoContent());

        assertThat(repo.findByUsername("newuser")).isEmpty();
    }
}
