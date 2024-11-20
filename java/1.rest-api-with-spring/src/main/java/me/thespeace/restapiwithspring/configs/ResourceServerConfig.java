package me.thespeace.restapiwithspring.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

/**
 * <h1>Token 인증 정보 유효성 확인 및 resource 서버 접근 제한</h1>
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    /**
     * <h2>resources id 설정</h2>
     * @param resources configurer for the resource server
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("event");
    }

    /**
     * <h2>접근 제한 설정</h2>
     * @param http the current http filter configuration
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .anonymous()
                .and()
            .authorizeRequests()
                .mvcMatchers(HttpMethod.GET, "/api/**")
                    .permitAll()
                .anyRequest()
                    .authenticated()
                .and()
            .exceptionHandling()
                .accessDeniedHandler(new OAuth2AccessDeniedHandler());
    }
}
