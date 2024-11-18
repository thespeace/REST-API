package me.thespeace.restapiwithspring.configs;

import me.thespeace.restapiwithspring.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * <h2>OAuth 토큰 저장소</h2>
     * <p>InMemoryTokenStore 사용</p>
     */
    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    /**
     * <h2>AuthenticationManager를 Bean으로 노출</h2>
     * <p>AuthorizationServer와 ResourceServer가 참조할 수 있도록.</p>
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * <h2>AuthenticationManager 설정</h2>
     * <p>AuthenticationManagerBuilder 재정의</p>
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService)
                .passwordEncoder(passwordEncoder);
    }

    /**
     * <h2>Security 필터 설정</h2>
     * <p>ignore은 HttpSecurity 보다는 WebSecurity가 서버에 부담을 덜 주기때문에 WebSecurity로 구현</p>
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/docs/index.html");
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()); //정적 리소스
    }

    /**
     * <h2>Spring Security 설정</h2>
     * @param http the {@link HttpSecurity} to modify
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .anonymous()
                .and()
            .formLogin()
                .and()
            .authorizeRequests()
                .mvcMatchers(HttpMethod.GET,"/api/**").authenticated()
                .anyRequest().authenticated();
    }
}
