package me.thespeace.restapiwithspring.common;

import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

/**
 * <h1>RestDocsMockMvc 커스터마이징</h1>
 * <p>RestDocsMockMvcConfigurationCustomizer 구현한 빈 등록</p>
 *
 * @see <a href="https://docs.spring.io/spring-restdocs/docs/current/reference/htmlsingle/#customizing-requests-and-responses">Preprocessors docs</a>
 */
@TestConfiguration //테스트에서만 사용하는 설정
public class RestDocsConfiguration {

    @Bean
    public RestDocsMockMvcConfigurationCustomizer restDocsMockMvcConfigurationCustomizer() {
        return configurer -> configurer.operationPreprocessors()
                .withRequestDefaults(prettyPrint())
                .withResponseDefaults(prettyPrint());
    }
}
