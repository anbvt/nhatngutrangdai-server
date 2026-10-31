package jp.co.nhatngutrangdai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class PaginationConfiguration {

    @Bean
    PageableHandlerMethodArgumentResolverCustomizer pageableResolverCustomizer() {
        return resolver -> {
            resolver.setFallbackPageable(PageRequest.of(0, 20));
            resolver.setMaxPageSize(100);
            resolver.setOneIndexedParameters(false);
        };
    }
}
