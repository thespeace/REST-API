package me.thespeace.restapiwithspring.configs;

import me.thespeace.restapiwithspring.accounts.Account;
import me.thespeace.restapiwithspring.accounts.AccountRole;
import me.thespeace.restapiwithspring.accounts.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AppConfig {


    /**
     * <h2>DTO -> Domain</h2>
     * <p>간편하게 객체로 값 복사를 위해 사용</p>
     * <p>조금 더 성능상 이점을 노린다면 직접 builder 사용</p>
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {

            @Autowired
            AccountService accountService;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                Account thespeace = Account.builder()
                        .email("thespeace1@gmail.com")
                        .password("pass")
                        .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                        .build();
                accountService.saveAccount(thespeace);
            }
        };
    }
}
