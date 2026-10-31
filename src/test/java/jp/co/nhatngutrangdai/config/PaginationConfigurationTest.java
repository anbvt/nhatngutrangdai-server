package jp.co.nhatngutrangdai.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class PaginationConfigurationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        var resolver = new PageableHandlerMethodArgumentResolver();
        new PaginationConfiguration().pageableResolverCustomizer().customize(resolver);
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setCustomArgumentResolvers(resolver)
                .build();
    }

    @Test
    void usesZeroBasedPageAndDefaultSize() throws Exception {
        mockMvc.perform(get("/page"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    void capsRequestedPageSize() throws Exception {
        mockMvc.perform(get("/page?page=2&size=500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.size").value(100));
    }

    @RestController
    static class TestController {

        @GetMapping("/page")
        PaginationView page(Pageable pageable) {
            return new PaginationView(pageable.getPageNumber(), pageable.getPageSize());
        }
    }

    record PaginationView(int page, int size) {
    }
}
