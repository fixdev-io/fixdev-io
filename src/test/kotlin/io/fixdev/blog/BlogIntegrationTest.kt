package io.fixdev.blog

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BlogIntegrationTest @Autowired constructor(
    private val mockMvc: MockMvc
) {
    @MockitoBean
    lateinit var clientRegistrationRepository: ClientRegistrationRepository

    @Test
    fun `app starts and home page loads`() {
        mockMvc.perform(get("/")).andExpect(status().isOk)
    }

    @Test
    fun `blog page loads`() {
        mockMvc.perform(get("/blog")).andExpect(status().isOk)
    }
}
