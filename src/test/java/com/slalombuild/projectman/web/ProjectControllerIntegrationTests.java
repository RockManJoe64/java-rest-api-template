/* (C) 2023 */
package com.slalombuild.projectman.web;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.slalombuild.projectman.ProjectmanApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = ProjectmanApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProjectControllerIntegrationTests {
  @Autowired private MockMvc mockMvc;

  @Test
  public void givenProjects_whenGetProjects_ThenStatus200() throws Exception {
    // TODO why is application-test not loading?
    mockMvc
        .perform(get("/project").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].name", is("Telecom Customer Enhancement")));
  }
}
